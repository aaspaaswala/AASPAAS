import { env } from '../../config/env';
import { logger } from '../../utils/logger';
import { OTP_EXPIRY_MINUTES } from '../../config/constants';
import { randomInt } from 'crypto';

export interface OtpResult {
  otp: string;
  expiresAt: Date;
}

export interface OtpProvider {
  generate(): OtpResult;
  send(phone: string, otp: string): Promise<void>;
}

class DevelopmentOtpProvider implements OtpProvider {
  generate(): OtpResult {
    return {
      otp: env.otp.mockOtp ?? randomInt(100000, 1000000).toString(),
      expiresAt: new Date(Date.now() + OTP_EXPIRY_MINUTES * 60 * 1000),
    };
  }

  async send(phone: string, otp: string): Promise<void> {
    if (env.nodeEnv !== 'development' && env.nodeEnv !== 'test') {
      throw new Error('Mock SMS delivery is disabled outside development and test environments');
    }
    logger.debug(`[DEV OTP] phone=${phone} otp=${otp}`);
  }
}

class ProductionOtpProvider implements OtpProvider {
  generate(): OtpResult {
    const otp = randomInt(100000, 1000000).toString();
    return { otp, expiresAt: new Date(Date.now() + OTP_EXPIRY_MINUTES * 60 * 1000) };
  }

  async send(phone: string, otp: string): Promise<void> {
    const { url, token, senderId } = env.otp.sms;
    if (!url || !token || !senderId) throw new Error('SMS provider is not configured');

    const response = await fetch(url, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({ from: senderId, to: phone, message: `Your Aas Paas Wala code is ${otp}. It expires in 10 minutes.` }),
      signal: AbortSignal.timeout(10000),
    });
    if (!response.ok) {
      logger.error('SMS provider request failed', { status: response.status });
      throw new Error('SMS delivery failed');
    }
  }
}

export const otpProvider: OtpProvider = env.otp.useMock
  ? new DevelopmentOtpProvider()
  : new ProductionOtpProvider();
