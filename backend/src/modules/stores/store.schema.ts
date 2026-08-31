import { z } from 'zod';

export const nearbyStoreSchema = z.object({
  latitude: z.coerce.number().min(-90).max(90),
  longitude: z.coerce.number().min(-180).max(180),
  radius: z.coerce.number().min(0.1).max(50).default(5),
  category: z.string().optional(),
});
