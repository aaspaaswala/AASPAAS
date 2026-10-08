import { z } from 'zod';

export const inquirySchema = z.object({
  type: z.string().trim().min(1).max(50),
  name: z.string().trim().min(1).max(100),
  business: z.string().trim().max(120).optional(),
  city: z.string().trim().max(100).optional(),
  phone: z.string().trim().max(20).optional(),
  email: z.string().trim().email().max(254),
  subject: z.string().trim().max(120).optional(),
  message: z.string().trim().max(5000).optional(),
});

export const platformSettingsSchema = z.object({
  general: z.object({
    siteName: z.string().trim().min(1).max(100),
    supportEmail: z.string().trim().email(),
    supportPhone: z.string().trim().max(20),
    timezone: z.string().trim().min(1).max(100),
  }),
  notifications: z.object({
    newStore: z.boolean(),
    newReservation: z.boolean(),
    expiredReservation: z.boolean(),
    newCustomer: z.boolean(),
  }),
  security: z.object({
    twoFactor: z.boolean(),
    sessionTimeout: z.number().int().min(1).max(48),
    loginAlerts: z.boolean(),
  }),
  appearance: z.object({ brandColor: z.string().regex(/^#[0-9a-fA-F]{6}$/) }),
});

export const inquiryStatusSchema = z.object({ status: z.enum(['NEW', 'READ', 'CLOSED']) });

export const adminStoreProfileSchema = z.object({
  retailer: z.object({
    ownerName: z.string().trim().min(1).max(120).optional(),
    businessName: z.string().trim().min(1).max(120).optional(),
    phone: z.string().trim().min(5).max(20).optional(),
    email: z.union([z.string().trim().email().max(254), z.literal('')]).optional(),
    category: z.string().trim().max(50).optional(),
    verificationStatus: z.enum(['PENDING', 'VERIFIED', 'REJECTED']).optional(),
  }).optional(),
  store: z.object({
    name: z.string().trim().min(1).max(100).optional(),
    description: z.string().trim().max(1000).optional(),
    address: z.string().trim().min(1).max(255).optional(),
    phone: z.string().trim().max(20).optional(),
    openingHours: z.string().trim().max(100).optional(),
    categories: z.array(z.string().trim().min(1).max(50)).optional(),
    imageUrl: z.union([z.string().url(), z.literal('')]).optional(),
    latitude: z.coerce.number().min(-90).max(90).optional(),
    longitude: z.coerce.number().min(-180).max(180).optional(),
  }).optional(),
});

export const retailerBlockSchema = z.object({
  isBlocked: z.boolean(),
  blockReason: z.string().trim().max(500).optional(),
});

const productVariantSchema = z.object({
  _id: z.string().optional(),
  size: z.string().trim().max(30).optional(),
  color: z.string().trim().max(30).optional(),
  price: z.coerce.number().min(0),
  stock: z.coerce.number().int().min(0),
});

export const adminProductSchema = z.object({
  name: z.string().trim().min(1).max(100),
  brand: z.string().trim().max(50).optional(),
  description: z.string().trim().max(500).optional(),
  category: z.string().trim().min(1).max(50),
  variants: z.array(productVariantSchema).min(1),
});

export const adminInventorySchema = z.object({
  stock: z.coerce.number().int().min(0).optional(),
  price: z.coerce.number().min(0).optional(),
}).refine((data) => data.stock !== undefined || data.price !== undefined, {
  message: 'Provide stock or price to update',
});
