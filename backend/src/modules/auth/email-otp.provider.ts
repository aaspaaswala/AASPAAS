import { env } from '../../config/env';
import { OTP_EXPIRY_MINUTES } from '../../config/constants';
import { logger } from '../../utils/logger';
import nodemailer from 'nodemailer';
import { randomInt } from 'crypto';

export function generateEmailOtp(): { otp: string; expiresAt: Date } {
  const otp = env.emailOtp.useMock
    ? env.otp.mockOtp ?? randomInt(100000, 1000000).toString()
    : randomInt(100000, 1000000).toString();

  return {
    otp,
    expiresAt: new Date(Date.now() + OTP_EXPIRY_MINUTES * 60 * 1000),
  };
}

export async function sendEmailOtp(email: string, otp: string): Promise<void> {
  if (env.emailOtp.useMock) {
    if (env.nodeEnv !== 'development' && env.nodeEnv !== 'test') {
      throw new Error('Mock email OTP delivery is disabled outside development and test environments');
    }
    logger.debug(`[DEV EMAIL OTP] email=${email} otp=${otp}`);
    return;
  }

  const { host, port, secure, user, password } = env.emailOtp.smtp;
  if (!host || !user || !password || !env.emailOtp.from) {
    throw new Error('SMTP email provider is not configured');
  }

  const transporter = nodemailer.createTransport({
    host,
    port,
    secure,
    auth: { user, pass: password },
  });

  await transporter.sendMail({
    from: env.emailOtp.from,
    to: email,
    subject: 'Your Aas Paas Wala verification code',
    text: `Your verification code is ${otp}. It expires in 10 minutes.`,
    html: `<p>Your Aas Paas Wala verification code is <strong>${otp}</strong>.</p><p>This code expires in 10 minutes.</p>`,
  });
}