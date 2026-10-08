import mongoose, { Document, Schema, Types } from 'mongoose';

export interface IReservation extends Document {
  customerId: Types.ObjectId;
  storeId: Types.ObjectId;
  productId: Types.ObjectId;
  variantId: Types.ObjectId;
  inventoryId: Types.ObjectId;
  customerName: string;
  customerMobile: string;
  productName: string;
  productImage?: string;
  variantSku: string;
  variantDescription: string;
  storeName: string;
  storeAddress: string;
  storeLocation: { type: 'Point'; coordinates: [number, number] };
  storePhone?: string;
  price: number;
  quantity: number;
  status: 'PENDING' | 'CONFIRMED' | 'READY' | 'COMPLETED' | 'CANCELLED' | 'EXPIRED' | 'REJECTED' | 'NO_SHOW';
  rejectionReason?: string;
  rejectedAt?: Date;
  noShowAt?: Date;
  reservationCode: string;
  durationHours: number;
  expiresAt: Date;
  completedAt?: Date;
  cancelledAt?: Date;
  createdAt: Date;
  updatedAt: Date;
}

const reservationSchema = new Schema<IReservation>(
  {
    customerId: { type: Schema.Types.ObjectId, ref: 'User', required: true },
    storeId: { type: Schema.Types.ObjectId, ref: 'Store', required: true },
    productId: { type: Schema.Types.ObjectId, ref: 'Product', required: true },
    variantId: { type: Schema.Types.ObjectId, ref: 'ProductVariant', required: true },
    inventoryId: { type: Schema.Types.ObjectId, ref: 'Inventory', required: true },
    customerName: { type: String, required: true, trim: true },
    customerMobile: { type: String, required: true, trim: true },
    productName: { type: String, required: true, trim: true },
    productImage: { type: String, default: undefined },
    variantSku: { type: String, required: true, trim: true },
    variantDescription: { type: String, required: true, trim: true },
    storeName: { type: String, required: true, trim: true },
    storeAddress: { type: String, required: true, trim: true },
    storeLocation: {
      type: { type: String, enum: ['Point'], default: 'Point' },
      coordinates: { type: [Number], required: true },
    },
    storePhone: { type: String, default: undefined },
    price: { type: Number, required: true, min: 0 },
    quantity: { type: Number, required: true, min: 1, default: 1 },
    status: {
      type: String,
      enum: ['PENDING', 'CONFIRMED', 'READY', 'COMPLETED', 'CANCELLED', 'EXPIRED', 'REJECTED', 'NO_SHOW'],
      default: 'PENDING',
    },
    rejectionReason: { type: String, trim: true, maxlength: 500, default: undefined },
    rejectedAt: { type: Date, default: undefined },
    noShowAt: { type: Date, default: undefined },
    reservationCode: { type: String, required: true, unique: true },
    durationHours: { type: Number, required: true, min: 1 },
    expiresAt: { type: Date, required: true },
    completedAt: { type: Date, default: undefined },
    cancelledAt: { type: Date, default: undefined },
  },
  { timestamps: true }
);

reservationSchema.index({ customerId: 1, createdAt: -1 });
reservationSchema.index({ storeId: 1, status: 1 });
reservationSchema.index({ status: 1, expiresAt: 1 });
reservationSchema.index({ expiresAt: 1 });

export const Reservation = mongoose.model<IReservation>('Reservation', reservationSchema);
