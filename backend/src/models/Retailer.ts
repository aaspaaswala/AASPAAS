import mongoose, { Document, Schema } from 'mongoose';

export type VerificationStatus = 'PENDING' | 'VERIFIED' | 'REJECTED';

export interface IRetailer extends Document {
  ownerName: string;
  businessName: string;
  phone: string;
  email?: string;
  category?: string;
  verificationStatus: VerificationStatus;
  isBlocked: boolean;
  blockedAt?: Date;
  blockReason?: string;
  otpHash?: string;
  otpExpiresAt?: Date;
  otpChannel?: 'email' | 'phone';
  otpAttempts: number;
  otpRequestedAt?: Date;
  createdAt: Date;
  updatedAt: Date;
}

const retailerSchema = new Schema<IRetailer>(
  {
    ownerName: { type: String, required: true, trim: true },
    businessName: { type: String, required: true, trim: true },
    phone: { type: String, required: true, unique: true, trim: true },
    email: { type: String, trim: true, lowercase: true, default: undefined },
    category: { type: String, trim: true, default: undefined },
    verificationStatus: {
      type: String,
      enum: ['PENDING', 'VERIFIED', 'REJECTED'],
      default: 'PENDING',
    },
    isBlocked: { type: Boolean, default: false },
    blockedAt: { type: Date, default: undefined },
    blockReason: { type: String, trim: true, maxlength: 500, default: undefined },
    otpHash: { type: String, select: false, default: undefined },
    otpExpiresAt: { type: Date, select: false, default: undefined },
    otpChannel: { type: String, enum: ['email', 'phone'], select: false, default: undefined },
    otpAttempts: { type: Number, default: 0, select: false },
    otpRequestedAt: { type: Date, select: false, default: undefined },
  },
  { timestamps: true }
);

export const Retailer = mongoose.model<IRetailer>('Retailer', retailerSchema);
