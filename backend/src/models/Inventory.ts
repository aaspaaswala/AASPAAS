import mongoose, { Document, Schema, Types } from 'mongoose';

/**
 * INVENTORY ACCOUNTING MODEL
 * ─────────────────────────────────────────────────────────────────────────────
 * totalStock     = current physical units in the store (available + reserved).
 *                  Decreases when a sale is completed or stock is removed.
 *                  Increases when stock is restocked.
 *
 * availableStock = units a customer can reserve right now.
 * reservedStock  = units held by active/confirmed reservations.
 *
 * INVARIANT (enforced by service layer):
 *   availableStock + reservedStock === totalStock
 *
 * soldStock      = cumulative historical sales counter.
 *                  NOT part of the current balance equation.
 *                  Used for analytics and reporting only.
 *
 * PRICE
 * ─────────────────────────────────────────────────────────────────────────────
 * Stored as integer paise (smallest INR unit).
 *   ₹1499  →  149900 paise
 * API layer divides by 100 for display. Avoids all floating-point issues.
 * ─────────────────────────────────────────────────────────────────────────────
 */

export interface IInventory extends Document {
  storeId: Types.ObjectId;
  productVariantId: Types.ObjectId;
  /** Price in paise (integer). ₹1499 = 149900 */
  price: number;
  totalStock: number;
  availableStock: number;
  reservedStock: number;
  /** Cumulative sales counter — analytics only, not part of current balance */
  soldStock: number;
  lowStockThreshold: number;
  createdAt: Date;
  updatedAt: Date;
}

const inventorySchema = new Schema<IInventory>(
  {
    storeId: { type: Schema.Types.ObjectId, ref: 'Store', required: true },
    productVariantId: { type: Schema.Types.ObjectId, ref: 'ProductVariant', required: true },
    price: {
      type: Number,
      required: true,
      min: [0, 'Price cannot be negative'],
      validate: {
        validator: Number.isInteger,
        message: 'Price must be an integer (paise)',
      },
    },
    totalStock: { type: Number, required: true, min: [0, 'totalStock cannot be negative'] },
    availableStock: { type: Number, required: true, min: [0, 'availableStock cannot be negative'] },
    reservedStock: { type: Number, default: 0, min: [0, 'reservedStock cannot be negative'] },
    soldStock: { type: Number, default: 0, min: [0, 'soldStock cannot be negative'] },
    lowStockThreshold: { type: Number, default: 3, min: 0 },
  },
  { timestamps: true }
);

// Core constraint: one inventory record per store+variant combination
inventorySchema.index({ storeId: 1, productVariantId: 1 }, { unique: true });
inventorySchema.index({ storeId: 1 });
inventorySchema.index({ productVariantId: 1 });

// Pre-save guard: availableStock + reservedStock must equal totalStock
inventorySchema.pre('save', function (next) {
  if (this.availableStock + this.reservedStock !== this.totalStock) {
    return next(
      new Error(
        `Inventory invariant violated: availableStock(${this.availableStock}) + reservedStock(${this.reservedStock}) !== totalStock(${this.totalStock})`
      )
    );
  }
  next();
});

export const Inventory = mongoose.model<IInventory>('Inventory', inventorySchema);
