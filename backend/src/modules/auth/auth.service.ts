import { User } from '../../models/User';
import { Retailer } from '../../models/Retailer';
import { signAccessToken, signRefreshToken, verifyRefreshToken } from './jwt';
import { ROLES } from '../../config/constants';
import { UnauthorizedError, NotFoundError, ValidationError } from '../../utils/errors';
import { logger } from '../../utils/logger';

// ── Customer ──────────────────────────────────────────────────────────────────

export async function registerOrLoginCustomer(
  phone: string,
  name?: string
): Promise<{ accessToken: string; refreshToken: string; user: object; isNew: boolean }> {
  let isNew = false;
  let user = await User.findOne({ phone });

  if (!user) {
    user = await User.create({ phone, name: name || '' });
    isNew = true;
    logger.info('Customer registered', { phone });
  } else {
    if (name) { user.name = name; await user.save(); }
    logger.info('Customer logged in', { phone });
  }

  const accessToken = signAccessToken(user.id, ROLES.CUSTOMER);
  const refreshToken = signRefreshToken(user.id, ROLES.CUSTOMER);
  return { accessToken, refreshToken, user: sanitizeUser(user), isNew };
}

// ── Retailer ──────────────────────────────────────────────────────────────────

export async function loginRetailer(
  phone: string
): Promise<{ accessToken: string; refreshToken: string; retailer: object }> {
  const retailer = await Retailer.findOne({ phone });
  if (!retailer) throw new NotFoundError('Retailer account not found. Please register first.');

  const accessToken = signAccessToken(retailer.id, ROLES.RETAILER);
  const refreshToken = signRefreshToken(retailer.id, ROLES.RETAILER);
  logger.info('Retailer logged in', { phone });
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
