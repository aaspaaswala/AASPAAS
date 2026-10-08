import { z } from 'zod';

export const customerRegisterSchema = z.object({
  phone: z.string().min(10).max(15).regex(/^\+?[0-9]+$/, 'Invalid phone number'),
  name: z.string().trim().min(1).max(100).optional(),
  email: z.string().trim().email().optional().or(z.literal('')),
  dob: z.string().regex(/^\d{4}-\d{2}-\d{2}$/).optional().or(z.literal('')),
});

export const customerLoginSchema = z.object({
  phone: z.string().min(10).max(15).regex(/^\+?[0-9]+$/, 'Invalid phone number'),
  dob: z.string().regex(/^\d{4}-\d{2}-\d{2}$/).optional().or(z.literal('')),
});

export const phoneOtpRequestSchema = z.object({
  phone: z.string().trim().min(10).max(16).regex(/^\+?[0-9]+$/, 'Invalid phone number'),
});

export const phoneOtpVerifySchema = phoneOtpRequestSchema.extend({
  otp: z.string().regex(/^\d{6}$/, 'OTP must be 6 digits'),
  name: z.string().trim().min(1).max(100).optional(),
  email: z.string().trim().email().optional(),
  dob: z.string().regex(/^\d{4}-\d{2}-\d{2}$/).optional(),
});

const emailSchema = z.string().trim().email().transform((value) => value.toLowerCase());

export const emailOtpRequestSchema = z.object({
  email: emailSchema,
  name: z.string().trim().min(1).max(100).optional(),
  dob: z.string().regex(/^\d{4}-\d{2}-\d{2}$/).optional().or(z.literal('')),
});

export const emailOtpVerifySchema = emailOtpRequestSchema.extend({
  otp: z.string().regex(/^\d{6}$/, 'OTP must be 6 digits'),
});

export const socialAuthSchema = z.object({
  provider: z.enum(['google', 'facebook']),
  idToken: z.string().min(1),
  name: z.string().trim().min(1).max(100).optional(),
});

export const refreshSchema = z.object({
  refreshToken: z.string().min(1),
});
