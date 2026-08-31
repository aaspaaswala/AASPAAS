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
  USE_MOCK_OTP: z.string().default('true'),
  MOCK_OTP: z.string().default('123456'),
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
    useMock: e.USE_MOCK_OTP === 'true',
    mockOtp: e.MOCK_OTP,
  },
  reservation: {
    freeHours: parseInt(e.FREE_RESERVATION_HOURS, 10),
  },
  firebase: {
    projectId: e.FIREBASE_PROJECT_ID,
  },
} as const;
