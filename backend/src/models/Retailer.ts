import mongoose, { Document, Schema } from 'mongoose';

export type VerificationStatus = 'PENDING' | 'VERIFIED' | 'REJECTED';

export interface IRetailer extends Document {
  ownerName: string;
  businessName: string;
  phone: string;
  email?: string;
  category?: string;
  verificationStatus: VerificationStatus;
  otp?: string;
  otpExpiresAt?: Date;
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
    otp: { type: String, select: false, default: undefined },
    otpExpiresAt: { type: Date, select: false, default: undefined },
  },
  { timestamps: true }
);

export const Retailer = mongoose.model<IRetailer>('Retailer', retailerSchema);
