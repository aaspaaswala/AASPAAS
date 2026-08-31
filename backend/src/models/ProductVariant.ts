import mongoose, { Document, Schema, Types } from 'mongoose';

export interface IProductVariant extends Document {
  productId: Types.ObjectId;
  /**
   * SKU is unique per product (compound index: productId + sku).
   * Not globally unique — two retailers can independently use "SWT-BLK-M"
   * for their own products without collision.
   */
  sku: string;
  /**
   * Variant-level distinguishing attributes.
   * Examples:
   *   Clothing:     { size: "M", color: "Black" }
   *   Shoes:        { size: "42", color: "White", gender: "men" }
   *   Electronics:  { storage: "128GB", color: "Midnight", ram: "8GB" }
   *   Furniture:    { color: "Walnut", dimensions: "120x60x75cm" }
   */
  attributes: Map<string, string>;
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
}

const productVariantSchema = new Schema<IProductVariant>(
  {
    productId: { type: Schema.Types.ObjectId, ref: 'Product', required: true },
    sku: { type: String, required: true, trim: true, uppercase: true },
    attributes: { type: Map, of: String, default: () => new Map() },
    isActive: { type: Boolean, default: true },
  },
  { timestamps: true }
);

// SKU unique within a product — two different products can share the same SKU string
productVariantSchema.index({ productId: 1, sku: 1 }, { unique: true });
productVariantSchema.index({ productId: 1 });

export const ProductVariant = mongoose.model<IProductVariant>(
  'ProductVariant',
  productVariantSchema
);
