import dotenv from 'dotenv';
import { z } from 'zod';

dotenv.config();

const envSchema = z.object({
  PORT: z.string().default('3000'),
  NODE_ENV: z.enum(['development', 'production', 'test']).default('development'),
  MONGODB_URI: z.string().min(1),
  USE_MEMORY_MONGO: z.string().default('false'),
  JWT_ACCESS_SECRET: z.string().min(16),
  JWT_REFRESH_SECRET: z.string().min(16),
  JWT_ACCESS_EXPIRES_IN: z.string().default('15m'),
  JWT_REFRESH_EXPIRES_IN: z.string().default('30d'),
  CORS_ORIGINS: z.string().default('*'),
  USE_MOCK_OTP: z.string().optional(),
  MOCK_OTP: z.string().optional(),
  USE_MOCK_EMAIL_OTP: z.string().optional(),
  EMAIL_OTP_FROM: z.string().email().optional(),
  SMTP_HOST: z.string().optional(),
  SMTP_PORT: z.string().default('587'),
  SMTP_SECURE: z.string().default('false'),
  SMTP_USER: z.string().optional(),
  SMTP_PASSWORD: z.string().optional(),
  SMS_PROVIDER_URL: z.string().url().optional(),
  SMS_PROVIDER_TOKEN: z.string().optional(),
  SMS_SENDER_ID: z.string().optional(),
  GOOGLE_CLIENT_ID: z.string().optional(),
  FACEBOOK_APP_ID: z.string().optional(),
  FACEBOOK_APP_SECRET: z.string().optional(),
  FREE_RESERVATION_HOURS: z.string().default('6'),
  FIREBASE_PROJECT_ID: z.string().optional(),
  GOOGLE_MAPS_API_KEY: z.string().optional(),
});

const parsed = envSchema.safeParse(process.env);

if (!parsed.success) {
  console.error('❌  Invalid environment variables:\n', parsed.error.flatten().fieldErrors);
  process.exit(1);
}

const e = parsed.data;
const useMockOtp = e.USE_MOCK_OTP === undefined ? e.NODE_ENV !== 'production' : e.USE_MOCK_OTP === 'true';
const useMockEmailOtp = e.USE_MOCK_EMAIL_OTP === undefined ? e.NODE_ENV !== 'production' : e.USE_MOCK_EMAIL_OTP === 'true';

if (e.NODE_ENV === 'production') {
  const missing: string[] = [];
  if (e.JWT_ACCESS_SECRET.length < 32) missing.push('JWT_ACCESS_SECRET (at least 32 characters)');
  if (e.JWT_REFRESH_SECRET.length < 32) missing.push('JWT_REFRESH_SECRET (at least 32 characters)');
  if (useMockOtp) missing.push('USE_MOCK_OTP=false');
  if (useMockEmailOtp) missing.push('USE_MOCK_EMAIL_OTP=false');
  if (!e.SMS_PROVIDER_URL || !e.SMS_PROVIDER_TOKEN || !e.SMS_SENDER_ID) {
    missing.push('SMS_PROVIDER_URL, SMS_PROVIDER_TOKEN, SMS_SENDER_ID');
  }
  if (!e.SMTP_HOST || !e.SMTP_USER || !e.SMTP_PASSWORD || !e.EMAIL_OTP_FROM) {
    missing.push('SMTP_HOST, SMTP_USER, SMTP_PASSWORD, EMAIL_OTP_FROM');
  }
  if (missing.length) {
    console.error('❌  Production authentication configuration is incomplete:', missing);
    process.exit(1);
  }
}

export const env = {
  port: parseInt(e.PORT, 10),
  nodeEnv: e.NODE_ENV,
  mongoUri: e.MONGODB_URI,
  useMemoryMongo: e.USE_MEMORY_MONGO === 'true',
  jwt: {
    accessSecret: e.JWT_ACCESS_SECRET,
    refreshSecret: e.JWT_REFRESH_SECRET,
    accessExpiresIn: e.JWT_ACCESS_EXPIRES_IN,
    refreshExpiresIn: e.JWT_REFRESH_EXPIRES_IN,
  },
  corsOrigins: e.CORS_ORIGINS.split(',').map((o) => o.trim()),
  otp: {
    useMock: useMockOtp,
    mockOtp: e.MOCK_OTP || (e.NODE_ENV === 'test' ? '123456' : undefined),
    sms: {
      url: e.SMS_PROVIDER_URL,
      token: e.SMS_PROVIDER_TOKEN,
      senderId: e.SMS_SENDER_ID,
    },
  },
  emailOtp: {
    useMock: useMockEmailOtp,
    from: e.EMAIL_OTP_FROM,
    smtp: {
      host: e.SMTP_HOST,
      port: parseInt(e.SMTP_PORT, 10),
      secure: e.SMTP_SECURE === 'true',
      user: e.SMTP_USER,
      password: e.SMTP_PASSWORD,
    },
  },
  social: {
    googleClientId: e.GOOGLE_CLIENT_ID,
    facebookAppId: e.FACEBOOK_APP_ID,
    facebookAppSecret: e.FACEBOOK_APP_SECRET,
  },
  reservation: {
    freeHours: parseInt(e.FREE_RESERVATION_HOURS, 10),
  },
  firebase: {
    projectId: e.FIREBASE_PROJECT_ID,
  },
} as const;
