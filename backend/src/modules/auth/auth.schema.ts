import { z } from 'zod';

export const customerRegisterSchema = z.object({
  phone: z.string().min(10).max(15).regex(/^\+?[0-9]+$/, 'Invalid phone number'),
  name: z.string().min(1).max(100).optional(),
});

export const customerLoginSchema = z.object({
  phone: z.string().min(10).max(15).regex(/^\+?[0-9]+$/, 'Invalid phone number'),
});

export const refreshSchema = z.object({
  refreshToken: z.string().min(1),
});
