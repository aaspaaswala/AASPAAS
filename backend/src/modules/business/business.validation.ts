import { z } from 'zod';

export const updateStoreSchema = z.object({
  name: z.string().min(1).max(100).optional(),
  address: z.string().min(1).max(255).optional(),
  latitude: z.coerce.number().min(-90).max(90).optional(),
  longitude: z.coerce.number().min(-180).max(180).optional(),
  phone: z.string().max(20).optional(),
  openingHours: z.string().max(100).optional(),
  categories: z.array(z.string()).optional(),
  imageUrl: z.string().url().optional(),
});

export const createProductSchema = z.object({
  name: z.string().min(1).max(100),
  brand: z.string().max(50).optional(),
  description: z.string().max(500).optional(),
  category: z.string().min(1).max(50),
  variants: z.array(
    z.object({
      size: z.string().max(20).optional(),
      color: z.string().max(20).optional(),
      price: z.coerce.number().min(0),
      stock: z.coerce.number().int().min(0),
    })
  ).min(1),
});

export const updateInventorySchema = z.object({
  stock: z.coerce.number().int().min(0),
});

export type UpdateStoreInput = z.infer<typeof updateStoreSchema>;
export type CreateProductInput = z.infer<typeof createProductSchema>;
export type UpdateInventoryInput = z.infer<typeof updateInventorySchema>;
