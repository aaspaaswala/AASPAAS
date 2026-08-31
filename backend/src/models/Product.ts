import mongoose, { Document, Schema, Types } from 'mongoose';

export interface IProduct extends Document {
  name: string;
  brand?: string;
  description?: string;
  categoryId?: Types.ObjectId;
  images: string[];
  /**
   * Flexible key-value attributes for the product definition level.
   * Examples:
   *   Clothing:     { material: "wool", gender: "unisex" }
   *   Electronics:  { type: "smartphone", connectivity: "5G" }
   * Variant-level attributes (size, color, storage, etc.) live on ProductVariant.
   */
  attributes: Map<string, string>;
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
}

const productSchema = new Schema<IProduct>(
  {
    name: { type: String, required: true, trim: true },
    brand: { type: String, trim: true, default: undefined },
    description: { type: String, default: undefined },
    categoryId: { type: Schema.Types.ObjectId, ref: 'Category', default: undefined },
    images: [{ type: String }],
    attributes: { type: Map, of: String, default: () => new Map() },
    isActive: { type: Boolean, default: true },
  },
  { timestamps: true }
);

// Text index for search (Step 2.10)
productSchema.index({ name: 'text', brand: 'text' });
productSchema.index({ categoryId: 1 }, { sparse: true });
productSchema.index({ brand: 1 }, { sparse: true });

export const Product = mongoose.model<IProduct>('Product', productSchema);
