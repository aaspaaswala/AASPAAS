import { env } from '../../config/env';
import { logger } from '../../utils/logger';
import { OTP_EXPIRY_MINUTES } from '../../config/constants';

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
      otp: env.otp.mockOtp,
      expiresAt: new Date(Date.now() + OTP_EXPIRY_MINUTES * 60 * 1000),
    };
  }

  async send(phone: string, otp: string): Promise<void> {
    logger.debug(`[DEV OTP] phone=${phone}  otp=${otp}`);
  }
}

class ProductionOtpProvider implements OtpProvider {
  generate(): OtpResult {
    const otp = Math.floor(100000 + Math.random() * 900000).toString();
    return { otp, expiresAt: new Date(Date.now() + OTP_EXPIRY_MINUTES * 60 * 1000) };
  }

  async send(_phone: string, _otp: string): Promise<void> {
    // TODO: integrate SMS provider (MSG91, Twilio, etc.)
    throw new Error('SMS provider not configured. Set USE_MOCK_OTP=true for development.');
  }
}

export const otpProvider: OtpProvider = env.otp.useMock
  ? new DevelopmentOtpProvider()
  : new ProductionOtpProvider();
