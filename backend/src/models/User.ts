import mongoose, { Document, Schema } from 'mongoose';

export interface IUser extends Document {
  name: string;
  phone?: string;
  email?: string;
  dob?: Date;
  profileImage?: string;
  location?: { type: 'Point'; coordinates: [number, number] };
  isBlocked: boolean;
  blockedAt?: Date;
  blockReason?: string;
  otpHash?: string;
  otpExpiresAt?: Date;
  otpChannel?: 'email' | 'phone';
  otpAttempts: number;
  otpRequestedAt?: Date;
  subscription: { plan: 'FREE' | 'PLUS' | 'PRO'; expiresAt?: Date };
  createdAt: Date;
  updatedAt: Date;
}

const userSchema = new Schema<IUser>(
  {
    name: { type: String, default: '' },
    phone: { type: String, unique: true, sparse: true, trim: true },
    email: { type: String, trim: true, lowercase: true, unique: true, sparse: true, default: undefined },
    dob: { type: Date, default: undefined },
    profileImage: { type: String, default: undefined },
    location: {
      type: { type: String, enum: ['Point'], default: undefined },
      coordinates: { type: [Number], default: undefined },
    },
    isBlocked: { type: Boolean, default: false },
    blockedAt: { type: Date, default: undefined },
    blockReason: { type: String, trim: true, maxlength: 500, default: undefined },
    otpHash: { type: String, select: false, default: undefined },
    otpExpiresAt: { type: Date, select: false, default: undefined },
    otpChannel: { type: String, enum: ['email', 'phone'], select: false, default: undefined },
    otpAttempts: { type: Number, default: 0, select: false },
    otpRequestedAt: { type: Date, select: false, default: undefined },
    subscription: {
      plan: { type: String, enum: ['FREE', 'PLUS', 'PRO'], default: 'FREE' },
      expiresAt: { type: Date, default: undefined },
    },
  },
  { timestamps: true }
);

userSchema.index({ location: '2dsphere' }, { sparse: true });

export const User = mongoose.model<IUser>('User', userSchema);
