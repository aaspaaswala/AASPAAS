import { z } from 'zod';

export const createReservationSchema = z.object({
  variantId: z.string().min(1, 'variantId is required'),
  quantity: z.coerce.number().int().min(1).max(10).default(1),
});

export const cancelReservationSchema = z.object({});

export type CreateReservationInput = z.infer<typeof createReservationSchema>;
