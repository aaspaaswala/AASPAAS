import { User } from '../../models/User';
import { Retailer } from '../../models/Retailer';
import { otpProvider } from './otp.provider';
import { signAccessToken, signRefreshToken, verifyRefreshToken } from './jwt';
import { ROLES } from '../../config/constants';
import {
  UnauthorizedError,
  NotFoundError,
  ValidationError,
} from '../../utils/errors';
import { logger } from '../../utils/logger';

// ── Customer OTP ──────────────────────────────────────────────────────────────

export async function sendCustomerOtp(phone: string): Promise<void> {
  const { otp, expiresAt } = otpProvider.generate();
  await User.findOneAndUpdate(
    { phone },
    { phone, otp, otpExpiresAt: expiresAt },
    { upsert: true, new: true, setDefaultsOnInsert: true }
  );
  await otpProvider.send(phone, otp);
  logger.info('Customer OTP sent', { phone });
}

export async function verifyCustomerOtp(
  phone: string,
  otp: string
): Promise<{ accessToken: string; refreshToken: string; user: object }> {
  const user = await User.findOne({ phone }).select('+otp +otpExpiresAt');
  if (!user) throw new NotFoundError('User');
  if (!user.otp || user.otp !== otp) throw new UnauthorizedError('Invalid OTP');
  if (!user.otpExpiresAt || user.otpExpiresAt < new Date())
    throw new UnauthorizedError('OTP expired');

  user.otp = undefined;
  user.otpExpiresAt = undefined;
  await user.save();

  const accessToken = signAccessToken(user.id, ROLES.CUSTOMER);
  const refreshToken = signRefreshToken(user.id, ROLES.CUSTOMER);
  logger.info('Customer authenticated', { userId: user.id });
  return { accessToken, refreshToken, user: sanitizeUser(user) };
}

// ── Retailer OTP ──────────────────────────────────────────────────────────────

export async function sendRetailerOtp(phone: string): Promise<void> {
  const retailer = await Retailer.findOne({ phone });
  if (!retailer) throw new NotFoundError('Retailer account');
  const { otp, expiresAt } = otpProvider.generate();
  retailer.otp = otp;
  retailer.otpExpiresAt = expiresAt;
  await retailer.save();
  await otpProvider.send(phone, otp);
  logger.info('Retailer OTP sent', { phone });
}

export async function verifyRetailerOtp(
  phone: string,
  otp: string
): Promise<{ accessToken: string; refreshToken: string; retailer: object }> {
  const retailer = await Retailer.findOne({ phone }).select('+otp +otpExpiresAt');
  if (!retailer) throw new NotFoundError('Retailer');
  if (!retailer.otp || retailer.otp !== otp) throw new UnauthorizedError('Invalid OTP');
  if (!retailer.otpExpiresAt || retailer.otpExpiresAt < new Date())
    throw new UnauthorizedError('OTP expired');

  retailer.otp = undefined;
  retailer.otpExpiresAt = undefined;
  await retailer.save();

  const accessToken = signAccessToken(retailer.id, ROLES.RETAILER);
  const refreshToken = signRefreshToken(retailer.id, ROLES.RETAILER);
  logger.info('Retailer authenticated', { retailerId: retailer.id });
  return { accessToken, refreshToken, retailer: sanitizeRetailer(retailer) };
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

  // Verify entity still exists
  if (payload.role === ROLES.CUSTOMER) {
    const user = await User.findById(payload.id);
    if (!user) throw new UnauthorizedError('User not found');
  } else if (payload.role === ROLES.RETAILER) {
    const retailer = await Retailer.findById(payload.id);
    if (!retailer) throw new UnauthorizedError('Retailer not found');
  }

  return {
    accessToken: signAccessToken(payload.id, payload.role),
    refreshToken: signRefreshToken(payload.id, payload.role),
  };
}

// ── Me ────────────────────────────────────────────────────────────────────────

export async function getMe(
  id: string,
  role: string
): Promise<object> {
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
  delete obj.otp;
  delete obj.otpExpiresAt;
  return obj;
}

function sanitizeRetailer(retailer: InstanceType<typeof Retailer>) {
  const obj = retailer.toObject();
  delete obj.otp;
  delete obj.otpExpiresAt;
  return obj;
}
