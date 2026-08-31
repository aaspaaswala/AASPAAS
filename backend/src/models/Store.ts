import mongoose, { Document, Schema, Types } from 'mongoose';

export interface IStore extends Document {
  retailerId: Types.ObjectId;
  name: string;
  description?: string;
  address: string;
  location: { type: 'Point'; coordinates: [number, number] };
  phone?: string;
  openingHours: string;
  images: string[];
  categories: string[];
  rating?: number;
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
}

const storeSchema = new Schema<IStore>(
  {
    retailerId: { type: Schema.Types.ObjectId, ref: 'Retailer', required: true },
    name: { type: String, required: true, trim: true },
    description: { type: String, default: undefined },
    address: { type: String, required: true },
    location: {
      type: { type: String, enum: ['Point'], required: true, default: 'Point' },
      coordinates: { type: [Number], required: true }, // [lng, lat]
    },
    phone: { type: String, default: undefined },
    openingHours: { type: String, default: '9:00 AM – 9:00 PM' },
    images: [{ type: String }],
    categories: [{ type: String }],
    rating: { type: Number, default: undefined },
    isActive: { type: Boolean, default: true },
  },
  { timestamps: true }
);

storeSchema.index({ location: '2dsphere' });
storeSchema.index({ retailerId: 1 });

export const Store = mongoose.model<IStore>('Store', storeSchema);
