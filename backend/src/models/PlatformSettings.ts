import mongoose, { Document, Schema } from 'mongoose';

export interface IPlatformSettings extends Document {
  key: 'global';
  general: {
    siteName: string;
    supportEmail: string;
    supportPhone: string;
    timezone: string;
  };
  notifications: {
    newStore: boolean;
    newReservation: boolean;
    expiredReservation: boolean;
    newCustomer: boolean;
  };
  security: {
    twoFactor: boolean;
    sessionTimeout: number;
    loginAlerts: boolean;
  };
  appearance: { brandColor: string };
  updatedAt: Date;
}

const platformSettingsSchema = new Schema<IPlatformSettings>(
  {
    key: { type: String, enum: ['global'], unique: true, required: true, default: 'global' },
    general: {
      siteName: { type: String, required: true, default: 'AasPaas Wala' },
      supportEmail: { type: String, required: true, default: 'support@aaspaas.in' },
      supportPhone: { type: String, required: true, default: '' },
      timezone: { type: String, required: true, default: 'Asia/Kolkata' },
    },
    notifications: {
      newStore: { type: Boolean, default: true },
      newReservation: { type: Boolean, default: true },
      expiredReservation: { type: Boolean, default: false },
      newCustomer: { type: Boolean, default: true },
    },
    security: {
      twoFactor: { type: Boolean, default: false },
      sessionTimeout: { type: Number, min: 1, max: 48, default: 24 },
      loginAlerts: { type: Boolean, default: true },
    },
    appearance: {
      brandColor: { type: String, match: /^#[0-9a-fA-F]{6}$/, default: '#f97316' },
    },
  },
  { timestamps: true }
);

export const PlatformSettings = mongoose.model<IPlatformSettings>('PlatformSettings', platformSettingsSchema);
