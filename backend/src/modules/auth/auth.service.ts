import { User } from '../../models/User';
import { Retailer } from '../../models/Retailer';
import { signAccessToken, signRefreshToken, verifyRefreshToken } from './jwt';
import { ROLES } from '../../config/constants';
import { UnauthorizedError, NotFoundError, ValidationError, TooManyRequestsError } from '../../utils/errors';
import { logger } from '../../utils/logger';
import { generateEmailOtp, sendEmailOtp } from './email-otp.provider';
import { otpProvider } from './otp.provider';
import { SocialProvider, verifySocialToken } from './social.provider';
import { hashOtp, isOtpMatch, OTP_MAX_ATTEMPTS, OTP_RESEND_COOLDOWN_MS } from './otp.utils';

// ── Customer ──────────────────────────────────────────────────────────────────

export async function requestCustomerPhoneOtp(phone: string): Promise<void> {
  const existing = await User.findOne({ phone }).select('+otpRequestedAt');
  if (existing?.otpRequestedAt && Date.now() - existing.otpRequestedAt.getTime() < OTP_RESEND_COOLDOWN_MS) {
    throw new TooManyRequestsError('Please wait before requesting another code.');
  }
  const { otp, expiresAt } = otpProvider.generate();
  const user = await User.findOneAndUpdate(
    { phone },
    {
      $setOnInsert: { phone, name: '' },
      $set: { otpHash: hashOtp('phone', phone, otp), otpExpiresAt: expiresAt, otpChannel: 'phone', otpAttempts: 0, otpRequestedAt: new Date() },
    },
    { new: true, upsert: true, setDefaultsOnInsert: true }
  ).select('+otpHash');
  try {
    await otpProvider.send(phone, otp);
  } catch (error) {
    await User.updateOne({ _id: user._id, otpHash: user.otpHash }, { $unset: { otpHash: 1, otpExpiresAt: 1, otpChannel: 1 } });
    throw error;
  }
}

export async function verifyCustomerPhoneOtp(
  phone: string,
  otp: string,
  name?: string,
  email?: string,
  dob?: string
): Promise<{ accessToken: string; refreshToken: string; user: object; isNew: boolean }> {
  let user = await User.findOne({ phone }).select('+otpHash +otpExpiresAt +otpAttempts +otpChannel');
  if (!user) throw new UnauthorizedError('Invalid or expired OTP');
  const isNew = user.name === '';
  const normalizedDob = dob ? new Date(dob) : undefined;
  if (dob && Number.isNaN(normalizedDob?.getTime())) throw new ValidationError('Invalid date of birth');
  if (!user.otpHash || user.otpChannel !== 'phone' || !user.otpExpiresAt || user.otpExpiresAt.getTime() <= Date.now() ||
      (user.otpAttempts ?? 0) >= OTP_MAX_ATTEMPTS) throw new UnauthorizedError('Invalid or expired OTP');
  if (!isOtpMatch('phone', phone, otp, user.otpHash)) {
    const attempts = (user.otpAttempts ?? 0) + 1;
    await User.updateOne(
      { _id: user._id, otpHash: user.otpHash, otpChannel: 'phone' },
      { $inc: { otpAttempts: 1 }, ...(attempts >= OTP_MAX_ATTEMPTS ? { $unset: { otpHash: 1, otpExpiresAt: 1, otpChannel: 1 } } : {}) }
    );
    throw new UnauthorizedError('Invalid or expired OTP');
  }
  const consumedUser = await User.findOneAndUpdate(
    { _id: user._id, otpHash: user.otpHash, otpChannel: 'phone', otpExpiresAt: { $gt: new Date() }, otpAttempts: { $lt: OTP_MAX_ATTEMPTS } },
    { $unset: { otpHash: 1, otpExpiresAt: 1, otpChannel: 1, otpRequestedAt: 1 }, $set: { otpAttempts: 0 } },
    { new: true }
  );
  if (!consumedUser) throw new UnauthorizedError('Invalid or expired OTP');
  user = consumedUser;
  if (user.isBlocked) throw new UnauthorizedError('This account has been suspended');
  if (name) user.name = name;
  if (email) user.email = email;
  if (normalizedDob) user.dob = normalizedDob;
  await user.save();
  return customerTokens(user, isNew);
}

export async function requestCustomerEmailOtp(email: string): Promise<void> {
  const { otp, expiresAt } = generateEmailOtp();
  const existing = await User.findOne({ email }).select('+otpRequestedAt');
  if (existing?.otpRequestedAt && Date.now() - existing.otpRequestedAt.getTime() < OTP_RESEND_COOLDOWN_MS) {
    throw new TooManyRequestsError('Please wait before requesting another code.');
  }
  const user = await User.findOneAndUpdate(
    { email },
    {
      $setOnInsert: { email, name: '' },
      $set: {
        otpHash: hashOtp('email', email, otp),
        otpExpiresAt: expiresAt,
        otpChannel: 'email',
        otpAttempts: 0,
        otpRequestedAt: new Date(),
      },
    },
    { new: true, upsert: true, setDefaultsOnInsert: true }
  ).select('+otpHash +otpExpiresAt');
  try {
    await sendEmailOtp(email, otp);
  } catch (error) {
    await User.updateOne({ _id: user._id, otpHash: user.otpHash }, { $unset: { otpHash: 1, otpExpiresAt: 1, otpChannel: 1 } });
    throw error;
  }
}

export async function verifyCustomerEmailOtp(
  email: string,
  otp: string,
  name?: string,
  dob?: string
): Promise<{ accessToken: string; refreshToken: string; user: object; isNew: boolean }> {
  let user = await User.findOne({ email }).select('+otpHash +otpExpiresAt +otpAttempts +otpChannel');
  let isNew = false;
  const normalizedDob = dob ? new Date(dob) : undefined;
  if (dob && Number.isNaN(normalizedDob?.getTime())) throw new ValidationError('Invalid date of birth');

  if (!user) throw new UnauthorizedError('Invalid or expired OTP');
  if (!user.otpHash || user.otpChannel !== 'email' || !user.otpExpiresAt || user.otpExpiresAt.getTime() <= Date.now() ||
      (user.otpAttempts ?? 0) >= OTP_MAX_ATTEMPTS) {
    throw new UnauthorizedError('Invalid or expired OTP');
  }
  if (!isOtpMatch('email', email, otp, user.otpHash)) {
    const attempts = (user.otpAttempts ?? 0) + 1;
    await User.updateOne(
      { _id: user._id, otpHash: user.otpHash, otpChannel: 'email' },
      {
        $inc: { otpAttempts: 1 },
        ...(attempts >= OTP_MAX_ATTEMPTS ? { $unset: { otpHash: 1, otpExpiresAt: 1, otpChannel: 1 } } : {}),
      }
    );
    throw new UnauthorizedError('Invalid or expired OTP');
  }
  const consumedUser = await User.findOneAndUpdate(
    { _id: user._id, otpHash: user.otpHash, otpChannel: 'email', otpExpiresAt: { $gt: new Date() }, otpAttempts: { $lt: OTP_MAX_ATTEMPTS } },
    { $unset: { otpHash: 1, otpExpiresAt: 1, otpChannel: 1, otpRequestedAt: 1 }, $set: { otpAttempts: 0 } },
    { new: true }
  );
  if (!consumedUser) throw new UnauthorizedError('Invalid or expired OTP');
  user = consumedUser;
  isNew = !user.createdAt || user.name === '';
  if (user.isBlocked) throw new UnauthorizedError('This account has been suspended');
  if (name) user.name = name;
  if (normalizedDob) user.dob = normalizedDob;
  await user.save();

  return customerTokens(user, isNew);
}

export async function socialCustomerAuth(
  provider: SocialProvider,
  idToken: string,
  name?: string
): Promise<{ accessToken: string; refreshToken: string; user: object; isNew: boolean }> {
  const profile = await verifySocialToken(provider, idToken);
  let user = await User.findOne({ email: profile.email });
  let isNew = false;
  if (!user) {
    user = await User.create({
      email: profile.email,
      name: name || profile.name || '',
      profileImage: profile.profileImage,
    });
    isNew = true;
  } else {
    if (user.isBlocked) throw new UnauthorizedError('This account has been suspended');
    if (name || profile.name) user.name = name || profile.name || user.name;
    if (profile.profileImage) user.profileImage = profile.profileImage;
    await user.save();
  }
  return customerTokens(user, isNew);
}

// ── Retailer ──────────────────────────────────────────────────────────────────

export async function loginRetailer(
  phone: string
): Promise<{ accessToken: string; refreshToken: string; retailer: object }> {
  const retailer = await Retailer.findOne({ phone });
  if (!retailer) throw new NotFoundError('Retailer account not found. Please register first.');
  ensureRetailerNotBlocked(retailer);

  const accessToken = signAccessToken(retailer.id, ROLES.RETAILER);
  const refreshToken = signRefreshToken(retailer.id, ROLES.RETAILER);
  logger.info('Retailer logged in', { phone });
  return { accessToken, refreshToken, retailer: sanitizeRetailer(retailer) };
}

export async function requestRetailerEmailOtp(email: string): Promise<void> {
  const retailer = await Retailer.findOne({ email }).select('+otpRequestedAt');
  if (!retailer || retailer.isBlocked) return;
  if (retailer.otpRequestedAt && Date.now() - retailer.otpRequestedAt.getTime() < OTP_RESEND_COOLDOWN_MS) {
    throw new TooManyRequestsError('Please wait before requesting another code.');
  }
  const { otp, expiresAt } = generateEmailOtp();
  retailer.otpHash = hashOtp('email', email, otp);
  retailer.otpExpiresAt = expiresAt;
  retailer.otpChannel = 'email';
  retailer.otpAttempts = 0;
  retailer.otpRequestedAt = new Date();
  await retailer.save();
  try {
    await sendEmailOtp(email, otp);
  } catch (error) {
    await Retailer.updateOne({ _id: retailer._id, otpHash: retailer.otpHash }, { $unset: { otpHash: 1, otpExpiresAt: 1, otpChannel: 1 } });
    throw error;
  }
}

export async function requestRetailerPhoneOtp(phone: string): Promise<void> {
  const retailer = await Retailer.findOne({ phone }).select('+otpRequestedAt');
  if (!retailer || retailer.isBlocked) return;
  if (retailer.otpRequestedAt && Date.now() - retailer.otpRequestedAt.getTime() < OTP_RESEND_COOLDOWN_MS) {
    throw new TooManyRequestsError('Please wait before requesting another code.');
  }
  const { otp, expiresAt } = otpProvider.generate();
  retailer.otpHash = hashOtp('phone', phone, otp);
  retailer.otpExpiresAt = expiresAt;
  retailer.otpChannel = 'phone';
  retailer.otpAttempts = 0;
  retailer.otpRequestedAt = new Date();
  await retailer.save();
  try {
    await otpProvider.send(phone, otp);
  } catch (error) {
    await Retailer.updateOne({ _id: retailer._id, otpHash: retailer.otpHash }, { $unset: { otpHash: 1, otpExpiresAt: 1, otpChannel: 1 } });
    throw error;
  }
}

export async function verifyRetailerPhoneOtp(
  phone: string,
  otp: string
): Promise<{ accessToken: string; refreshToken: string; retailer: object }> {
  let retailer = await Retailer.findOne({ phone }).select('+otpHash +otpExpiresAt +otpAttempts +otpChannel');
  if (retailer) ensureRetailerNotBlocked(retailer);
  if (!retailer || !retailer.otpHash || retailer.otpChannel !== 'phone' || !retailer.otpExpiresAt ||
      retailer.otpExpiresAt.getTime() <= Date.now() || (retailer.otpAttempts ?? 0) >= OTP_MAX_ATTEMPTS) {
    throw new UnauthorizedError('Invalid or expired OTP');
  }
  if (!isOtpMatch('phone', phone, otp, retailer.otpHash)) {
    const attempts = (retailer.otpAttempts ?? 0) + 1;
    await Retailer.updateOne(
      { _id: retailer._id, otpHash: retailer.otpHash, otpChannel: 'phone' },
      { $inc: { otpAttempts: 1 }, ...(attempts >= OTP_MAX_ATTEMPTS ? { $unset: { otpHash: 1, otpExpiresAt: 1, otpChannel: 1 } } : {}) }
    );
    throw new UnauthorizedError('Invalid or expired OTP');
  }
  const consumedRetailer = await Retailer.findOneAndUpdate(
    { _id: retailer._id, otpHash: retailer.otpHash, otpChannel: 'phone', otpExpiresAt: { $gt: new Date() }, otpAttempts: { $lt: OTP_MAX_ATTEMPTS } },
    { $unset: { otpHash: 1, otpExpiresAt: 1, otpChannel: 1, otpRequestedAt: 1 }, $set: { otpAttempts: 0 } },
    { new: true }
  );
  if (!consumedRetailer) throw new UnauthorizedError('Invalid or expired OTP');
  retailer = consumedRetailer;
  await retailer.save();
  return retailerTokens(retailer);
}

export async function verifyRetailerEmailOtp(
  email: string,
  otp: string
): Promise<{ accessToken: string; refreshToken: string; retailer: object }> {
  let retailer = await Retailer.findOne({ email }).select('+otpHash +otpExpiresAt +otpAttempts +otpChannel');
  if (!retailer) throw new NotFoundError('Retailer account not found. Please register first.');
  ensureRetailerNotBlocked(retailer);
  if (!retailer.otpHash || retailer.otpChannel !== 'email' || !retailer.otpExpiresAt || retailer.otpExpiresAt.getTime() <= Date.now() ||
      (retailer.otpAttempts ?? 0) >= OTP_MAX_ATTEMPTS) {
    throw new UnauthorizedError('Invalid or expired OTP');
  }
  if (!isOtpMatch('email', email, otp, retailer.otpHash)) {
    const attempts = (retailer.otpAttempts ?? 0) + 1;
    await Retailer.updateOne(
      { _id: retailer._id, otpHash: retailer.otpHash, otpChannel: 'email' },
      {
        $inc: { otpAttempts: 1 },
        ...(attempts >= OTP_MAX_ATTEMPTS ? { $unset: { otpHash: 1, otpExpiresAt: 1, otpChannel: 1 } } : {}),
      }
    );
    throw new UnauthorizedError('Invalid or expired OTP');
  }
  const consumedRetailer = await Retailer.findOneAndUpdate(
    { _id: retailer._id, otpHash: retailer.otpHash, otpChannel: 'email', otpExpiresAt: { $gt: new Date() }, otpAttempts: { $lt: OTP_MAX_ATTEMPTS } },
    { $unset: { otpHash: 1, otpExpiresAt: 1, otpChannel: 1, otpRequestedAt: 1 }, $set: { otpAttempts: 0 } },
    { new: true }
  );
  if (!consumedRetailer) throw new UnauthorizedError('Invalid or expired OTP');
  retailer = consumedRetailer;
  await retailer.save();
  return retailerTokens(retailer);
}

export async function socialRetailerAuth(
  provider: SocialProvider,
  idToken: string
): Promise<{ accessToken: string; refreshToken: string; retailer: object }> {
  const profile = await verifySocialToken(provider, idToken);
  const retailer = await Retailer.findOne({ email: profile.email });
  if (!retailer) throw new NotFoundError('Retailer account not found. Please register first.');
  ensureRetailerNotBlocked(retailer);
  return retailerTokens(retailer);
}

// ── Refresh ───────────────────────────────────────────────────────────────────

export async function refreshTokens(
  token: string
): Promise<{ accessToken: string; refreshToken: string }> {
  let payload;
  try {
    payload = verifyRefreshToken(token);
  } catch {
    throw new UnauthorizedError('Invalid refresh token');
  }

  if (payload.role === ROLES.CUSTOMER) {
    const user = await User.findById(payload.id);
    if (!user) throw new UnauthorizedError('User not found');
    if (user.isBlocked) throw new UnauthorizedError('This account has been suspended');
  } else if (payload.role === ROLES.RETAILER) {
    const retailer = await Retailer.findById(payload.id);
    if (!retailer) throw new UnauthorizedError('Retailer not found');
    ensureRetailerNotBlocked(retailer);
  }

  return {
    accessToken: signAccessToken(payload.id, payload.role),
    refreshToken: signRefreshToken(payload.id, payload.role),
  };
}

function ensureRetailerNotBlocked(retailer: { isBlocked?: boolean }): void {
  if (retailer.isBlocked) throw new UnauthorizedError('Business account is blocked');
}

function customerTokens(user: InstanceType<typeof User>, isNew: boolean) {
  return {
    accessToken: signAccessToken(user.id, ROLES.CUSTOMER),
    refreshToken: signRefreshToken(user.id, ROLES.CUSTOMER),
    user: sanitizeUser(user),
    isNew,
  };
}

function retailerTokens(retailer: InstanceType<typeof Retailer>) {
  return {
    accessToken: signAccessToken(retailer.id, ROLES.RETAILER),
    refreshToken: signRefreshToken(retailer.id, ROLES.RETAILER),
    retailer: sanitizeRetailer(retailer),
  };
}

// ── Me ────────────────────────────────────────────────────────────────────────

export async function getMe(id: string, role: string): Promise<object> {
  if (role === ROLES.CUSTOMER) {
    const user = await User.findById(id);
    if (!user) throw new NotFoundError('User');
    return sanitizeUser(user);
  }
  if (role === ROLES.RETAILER) {
    const retailer = await Retailer.findById(id);
    if (!retailer) throw new NotFoundError('Retailer');
    return sanitizeRetailer(retailer);
  }
  throw new ValidationError('Unknown role');
}

// ── Helpers ───────────────────────────────────────────────────────────────────

function sanitizeUser(user: InstanceType<typeof User>) {
  const obj = user.toObject();
  delete (obj as any).otpHash;
  delete (obj as any).otpExpiresAt;
  delete (obj as any).otpAttempts;
  delete (obj as any).otpRequestedAt;
  return obj;
}

function sanitizeRetailer(retailer: InstanceType<typeof Retailer>) {
  const obj = retailer.toObject();
  delete (obj as any).otpHash;
  delete (obj as any).otpExpiresAt;
  delete (obj as any).otpAttempts;
  delete (obj as any).otpRequestedAt;
  return obj;
}
