import mongoose, { Document, Schema } from 'mongoose';

export interface IUser extends Document {
  name: string;
  phone: string;
  email?: string;
  profileImage?: string;
  location?: { type: 'Point'; coordinates: [number, number] };
  otp?: string;
  otpExpiresAt?: Date;
  subscription: { plan: 'FREE' | 'PLUS' | 'PRO'; expiresAt?: Date };
  createdAt: Date;
  updatedAt: Date;
}

const userSchema = new Schema<IUser>(
  {
    name: { type: String, default: '' },
    phone: { type: String, required: true, unique: true, trim: true },
    email: { type: String, trim: true, lowercase: true, default: undefined },
    profileImage: { type: String, default: undefined },
    location: {
      type: { type: String, enum: ['Point'], default: 'Point' },
      coordinates: { type: [Number], default: undefined },
    },
    otp: { type: String, select: false, default: undefined },
    otpExpiresAt: { type: Date, select: false, default: undefined },
    subscription: {
      plan: { type: String, enum: ['FREE', 'PLUS', 'PRO'], default: 'FREE' },
      expiresAt: { type: Date, default: undefined },
    },
  },
  { timestamps: true }
);

userSchema.index({ email: 1 }, { sparse: true });
userSchema.index({ location: '2dsphere' }, { sparse: true });

export const User = mongoose.model<IUser>('User', userSchema);
