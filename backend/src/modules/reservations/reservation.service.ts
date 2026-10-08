import { Reservation } from './reservation.model';
import { Inventory } from '../../models/Inventory';
import { ProductVariant } from '../../models/ProductVariant';
import { Product } from '../../models/Product';
import { Store } from '../../models/Store';
import { User } from '../../models/User';
import {
  NotFoundError,
  ConflictError,
  InsufficientStockError,
  ForbiddenError,
} from '../../utils/errors';
import { ROLES, RESERVATION_STATUS } from '../../config/constants';
import { AuthPayload } from '../../types';
import { env } from '../../config/env';
import { logger } from '../../utils/logger';

function generateReservationCode(): string {
  const ts = Date.now().toString(36).toUpperCase();
  const rand = Math.random().toString(36).substring(2, 6).toUpperCase();
  return `RES-${ts}-${rand}`;
}

function buildVariantDescription(attributes: Map<string, string> | undefined): string {
  if (!attributes || attributes.size === 0) return '';
  return Array.from(attributes.entries())
    .map(([k, v]) => `${k}: ${v}`)
    .join(', ');
}

export async function createReservation(
  auth: AuthPayload,
  input: { variantId: string; quantity: number }
): Promise<object> {
  if (auth.role !== ROLES.CUSTOMER) {
    throw new ForbiddenError('Only customers can create reservations');
  }

  const variant = await ProductVariant.findById(input.variantId);
  if (!variant) throw new NotFoundError('Product variant');

  const product = await Product.findById(variant.productId);
  if (!product) throw new NotFoundError('Product');

  const inventories = await Inventory.find({
    productVariantId: variant._id,
    availableStock: { $gte: input.quantity },
  });

  if (inventories.length === 0) {
    throw new InsufficientStockError();
  }

  const inventory = inventories[0];
  const store = await Store.findById(inventory.storeId);
  if (!store || !store.isActive) {
    throw new NotFoundError('Store');
  }

  const customer = await User.findById(auth.id);
  if (!customer) throw new NotFoundError('User');

  const durationHours = env.reservation.freeHours;
  const now = new Date();
  const expiresAt = new Date(now.getTime() + durationHours * 60 * 60 * 1000);

  const updatedInventory = await Inventory.findOneAndUpdate(
    { _id: inventory._id, availableStock: { $gte: input.quantity } },
    {
      $inc: { reservedStock: input.quantity, availableStock: -input.quantity },
    },
    { new: true }
  );

  if (!updatedInventory) {
    throw new ConflictError('Failed to reserve stock. Please try again.');
  }

  let reservation: InstanceType<typeof Reservation>;
  try {
    reservation = await Reservation.create({
      customerId: auth.id,
      storeId: store._id,
      productId: product._id,
      variantId: variant._id,
      inventoryId: inventory._id,
      customerName: customer.name || 'Customer',
      customerMobile: customer.phone || '',
      productName: product.name,
      productImage: product.images?.[0],
      variantSku: variant.sku,
      variantDescription: buildVariantDescription(variant.attributes),
      storeName: store.name,
      storeAddress: store.address,
      storeLocation: store.location,
      storePhone: store.phone,
      price: inventory.price,
      quantity: input.quantity,
      reservationCode: generateReservationCode(),
      durationHours,
      expiresAt,
    });
  } catch (error) {
    await Inventory.findOneAndUpdate(
      { _id: inventory._id, reservedStock: { $gte: input.quantity } },
      { $inc: { reservedStock: -input.quantity, availableStock: input.quantity } }
    );
    throw error;
  }

  logger.info('Reservation created', {
    reservationId: reservation._id,
    customerId: auth.id,
    storeId: store._id,
    variantId: variant._id,
    quantity: input.quantity,
  });

  return sanitizeReservation(reservation);
}

export async function getMyReservations(auth: AuthPayload): Promise<object[]> {
  if (auth.role !== ROLES.CUSTOMER) {
    throw new ForbiddenError('Only customers can view reservations');
  }

  const reservations = await Reservation.find({ customerId: auth.id })
    .sort({ createdAt: -1 })
    .lean();

  return reservations.map(sanitizeReservation);
}

export async function getReservationById(
  auth: AuthPayload,
  reservationId: string
): Promise<object> {
  const reservation = await Reservation.findById(reservationId).lean();
  if (!reservation) throw new NotFoundError('Reservation');

  if (auth.role === ROLES.CUSTOMER && reservation.customerId.toString() !== auth.id) {
    throw new ForbiddenError('Not your reservation');
  }

  return sanitizeReservation(reservation);
}

export async function cancelMyReservation(
  auth: AuthPayload,
  reservationId: string
): Promise<void> {
  if (auth.role !== ROLES.CUSTOMER) {
    throw new ForbiddenError('Only customers can cancel reservations');
  }

  const reservation = await Reservation.findById(reservationId);
  if (!reservation) throw new NotFoundError('Reservation');

  if (reservation.customerId.toString() !== auth.id) {
    throw new ForbiddenError('Not your reservation');
  }

  if (!['PENDING', 'CONFIRMED', 'READY'].includes(reservation.status)) {
    throw new ConflictError('Cannot cancel reservation in current status');
  }

  const cancelled = await Reservation.findOneAndUpdate(
    {
      _id: reservation._id,
      status: { $in: [RESERVATION_STATUS.PENDING, RESERVATION_STATUS.CONFIRMED, RESERVATION_STATUS.READY] },
    },
    { $set: { status: RESERVATION_STATUS.CANCELLED, cancelledAt: new Date() } },
    { new: true }
  );

  if (!cancelled) {
    throw new ConflictError('Reservation was already updated');
  }

  await Inventory.findByIdAndUpdate(reservation.inventoryId, {
    $inc: { reservedStock: -reservation.quantity, availableStock: reservation.quantity },
  });

  logger.info('Reservation cancelled', {
    reservationId: reservation._id,
    customerId: auth.id,
    quantity: reservation.quantity,
  });
}

export async function expireOldReservations(): Promise<number> {
  const now = new Date();
  const candidates = await Reservation.find({
    status: { $in: [RESERVATION_STATUS.PENDING, RESERVATION_STATUS.CONFIRMED, RESERVATION_STATUS.READY] },
    expiresAt: { $lte: now },
  }).lean();

  if (candidates.length === 0) return 0;

  const inventoryUpdates = new Map<string, number>();
  let expiredCount = 0;

  for (const r of candidates) {
    const expired = await Reservation.findOneAndUpdate(
      {
        _id: r._id,
        status: { $in: [RESERVATION_STATUS.PENDING, RESERVATION_STATUS.CONFIRMED, RESERVATION_STATUS.READY] },
        expiresAt: { $lte: now },
      },
      { $set: { status: RESERVATION_STATUS.EXPIRED } },
      { new: true }
    ).lean();

    if (!expired) continue;

    inventoryUpdates.set(r.inventoryId.toString(), (inventoryUpdates.get(r.inventoryId.toString()) || 0) + r.quantity);
    expiredCount += 1;
  }

  if (expiredCount === 0) return 0;

  for (const [inventoryId, totalQty] of inventoryUpdates) {
    await Inventory.findByIdAndUpdate(inventoryId, {
      $inc: { reservedStock: -totalQty, availableStock: totalQty },
    });
  }

  logger.info('Reservations expired', { count: expiredCount });
  return expiredCount;
}

function sanitizeReservation(reservation: any): object {
  const obj = reservation.toObject ? reservation.toObject() : reservation;
  delete obj.__v;
  delete obj.customerId;
  delete obj.storeId;
  delete obj.productId;
  delete obj.variantId;
  delete obj.inventoryId;
  return obj;
}
