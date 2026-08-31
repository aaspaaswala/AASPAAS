import { z } from 'zod';

export const sendOtpSchema = z.object({
  phone: z.string().min(10).max(15).regex(/^\+?[0-9]+$/, 'Invalid phone number'),
  role: z.enum(['CUSTOMER', 'RETAILER']),
});

export const verifyOtpSchema = z.object({
  phone: z.string().min(10).max(15),
  otp: z.string().length(6),
  role: z.enum(['CUSTOMER', 'RETAILER']),
});

export const refreshSchema = z.object({
  refreshToken: z.string().min(1),
});
