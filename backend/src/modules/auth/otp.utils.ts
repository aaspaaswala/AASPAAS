import { createHmac, timingSafeEqual } from 'crypto';
import { env } from '../../config/env';

export const OTP_MAX_ATTEMPTS = 5;
export const OTP_RESEND_COOLDOWN_MS = 60_000;

export function hashOtp(channel: 'email' | 'phone', destination: string, otp: string): string {
  return createHmac('sha256', env.jwt.refreshSecret)
    .update(`${channel}:${destination.toLowerCase()}:${otp}`)
    .digest('hex');
}

export function isOtpMatch(
  channel: 'email' | 'phone',
  destination: string,
  otp: string,
  expectedHash: string
): boolean {
  const actual = Buffer.from(hashOtp(channel, destination, otp), 'hex');
  const expected = Buffer.from(expectedHash, 'hex');
  return actual.length === expected.length && timingSafeEqual(actual, expected);
}
