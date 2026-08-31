import { Types } from 'mongoose';
import { Store } from '../../models/Store';
import { Retailer } from '../../models/Retailer';
import { Product, IProduct } from '../../models/Product';
import { ProductVariant } from '../../models/ProductVariant';
import { Inventory } from '../../models/Inventory';
import { Reservation } from '../../modules/reservations/reservation.model';
import { Category } from './category.model';
import {
  NotFoundError,
  ConflictError,
  ForbiddenError,
} from '../../utils/errors';
import { ROLES, RESERVATION_STATUS, LOW_STOCK_THRESHOLD } from '../../config/constants';
import { env } from '../../config/env';
import { logger } from '../../utils/logger';

function buildVariantDescription(attributes: Map<string, string> | undefined): string {
  if (!attributes || attributes.size === 0) return '';
  return Array.from(attributes.entries())
    .map(([k, v]) => `${k}: ${v}`)
    .join(', ');
}

function getAttr(attributes: any, key: string): string | null {
  if (!attributes) return null;
  if (typeof attributes.get === 'function') return attributes.get(key) ?? null;
  return attributes[key] ?? null;
}

export async function getMyStore(retailerId: string): Promise<object> {
  const store = await Store.findOne({ retailerId: new Types.ObjectId(retailerId) }).lean();
  if (!store) throw new NotFoundError('Store');
  return transformStore(store);
}

export async function updateMyStore(retailerId: string, input: Record<string, unknown>): Promise<object> {
  const store = await Store.findOne({ retailerId: new Types.ObjectId(retailerId) });
  if (!store) throw new NotFoundError('Store');

  if (input.name !== undefined) store.name = input.name as string;
  if (input.address !== undefined) store.address = input.address as string;
  if (input.latitude !== undefined || input.longitude !== undefined) {
    const lat = (input.latitude as number) ?? store.location.coordinates[1];
    const lng = (input.longitude as number) ?? store.location.coordinates[0];
    store.location = { type: 'Point', coordinates: [lng, lat] };
  }
  if (input.phone !== undefined) store.phone = input.phone as string | undefined;
  if (input.openingHours !== undefined) store.openingHours = input.openingHours as string;
  if (input.categories !== undefined) store.categories = input.categories as string[];
  if (input.imageUrl !== undefined) {
    store.images = input.imageUrl ? [input.imageUrl as string] : [];
  }

  await store.save();
  logger.info('Store updated', { storeId: store._id, retailerId });
  return transformStore(store.toObject());
}

export async function getMyProducts(retailerId: string): Promise<object[]> {
  const store = await Store.findOne({ retailerId: new Types.ObjectId(retailerId) }).select('_id');
  if (!store) throw new NotFoundError('Store');

  const inventories = await Inventory.find({ storeId: store._id })
    .populate('productVariantId')
    .lean();

  if (inventories.length === 0) return [];

  const variantIds = inventories.map((i) => (i.productVariantId as any)._id ?? i.productVariantId);
  const variants = await ProductVariant.find({ _id: { $in: variantIds } }).lean();
  const variantMap = new Map(variants.map((v) => [v._id.toString(), v]));

  const productIds = [...new Set(variants.map((v) => v.productId.toString()))];
  const products = await Product.find({ _id: { $in: productIds } }).lean();
  const productMap = new Map(products.map((p) => [p._id.toString(), p]));

  const inventoryMap = new Map<string, any>();
  for (const inv of inventories) {
    const vid = (inv.productVariantId as any)._id?.toString() ?? inv.productVariantId.toString();
    inventoryMap.set(vid, inv);
  }

  const result: any[] = [];
  for (const product of products) {
    const productVariants = variants.filter((v) => v.productId.toString() === product._id.toString());
    const variantDtos = productVariants.map((v) => {
      const inv = inventoryMap.get(v._id.toString());
      const stockStatus = inv
        ? inv.availableStock === 0
          ? 'OUT_OF_STOCK'
          : inv.availableStock <= inv.lowStockThreshold
            ? 'LOW_STOCK'
            : 'AVAILABLE'
        : 'OUT_OF_STOCK';
      return {
        _id: v._id,
        size: getAttr(v.attributes, 'size'),
        color: getAttr(v.attributes, 'color'),
        price: inv ? inv.price / 100 : 0,
        inventory: inv
          ? {
              _id: inv._id,
              totalStock: inv.totalStock,
              availableStock: inv.availableStock,
              reservedStock: inv.reservedStock,
              status: stockStatus,
            }
          : null,
      };
    });

    const category = product.categoryId
      ? await Category.findById(product.categoryId).select('name').lean()
      : null;

    result.push({
      _id: product._id,
      name: product.name,
      brand: product.brand,
      description: product.description,
      imageUrls: product.images,
      category: category?.name ?? '',
      variants: variantDtos,
    });
  }

  return result;
}

export async function createProduct(retailerId: string, input: Record<string, unknown>): Promise<object> {
  const store = await Store.findOne({ retailerId: new Types.ObjectId(retailerId) });
  if (!store) throw new NotFoundError('Store');

  let categoryId: Types.ObjectId | undefined;
  if (input.category) {
    const category = await Category.findOneAndUpdate(
      { name: input.category as string },
      { name: input.category as string, isActive: true },
      { upsert: true, new: true, setDefaultsOnInsert: true }
    );
    categoryId = category._id;
  }

  const product = await Product.create({
    name: input.name as string,
    brand: input.brand as string | undefined,
    description: input.description as string | undefined,
    categoryId,
    images: [],
    attributes: new Map(),
    isActive: true,
  });

  const variants = input.variants as any[];
  const createdVariants: any[] = [];

  for (const v of variants) {
    const attrs: [string, string][] = [];
    if (v.size) attrs.push(['size', v.size]);
    if (v.color) attrs.push(['color', v.color]);
    const variant = await ProductVariant.create({
      productId: product._id,
      sku: `SKU-${product._id.toString().slice(-6)}-${Date.now().toString(36)}`,
      attributes: new Map<string, string>(attrs),
      isActive: true,
    });
    createdVariants.push(variant);

    await Inventory.create({
      storeId: store._id,
      productVariantId: variant._id,
      price: Math.round((v.price as number) * 100),
      totalStock: v.stock as number,
      availableStock: v.stock as number,
      reservedStock: 0,
      soldStock: 0,
      lowStockThreshold: LOW_STOCK_THRESHOLD,
    });
  }

  logger.info('Product created', { productId: product._id, retailerId, variantCount: createdVariants.length });

  const productDto = await buildProductDto(product._id, createdVariants);
  return productDto;
}

export async function updateProduct(
  retailerId: string,
  productId: string,
  input: Record<string, unknown>
): Promise<object> {
  const store = await Store.findOne({ retailerId: new Types.ObjectId(retailerId) });
  if (!store) throw new NotFoundError('Store');

  const product = await Product.findById(productId);
  if (!product) throw new NotFoundError('Product');

  let categoryId = product.categoryId;
  if (input.category) {
    const category = await Category.findOneAndUpdate(
      { name: input.category as string },
      { name: input.category as string, isActive: true },
      { upsert: true, new: true, setDefaultsOnInsert: true }
    );
    categoryId = category._id;
  }

  product.name = input.name as string;
  product.brand = (input.brand as string | undefined) ?? product.brand;
  product.description = (input.description as string | undefined) ?? product.description;
  product.categoryId = categoryId;
  await product.save();

  const existingVariants = await ProductVariant.find({ productId: product._id });
  const existingIds = new Set(existingVariants.map((v) => v._id.toString()));

  const variants = input.variants as any[];
  const newVariantIds: string[] = [];

  for (const v of variants) {
    if (v._id && existingIds.has(v._id)) {
      const variant = await ProductVariant.findById(v._id);
      if (variant) {
        const attrs = new Map<string, string>();
        if (v.size) attrs.set('size', v.size);
        if (v.color) attrs.set('color', v.color);
        variant.attributes = attrs;
        await variant.save();
        newVariantIds.push(variant._id.toString());

        const inventory = await Inventory.findOne({
          storeId: store._id,
          productVariantId: variant._id,
        });
        if (inventory) {
          const newPrice = Math.round((v.price as number) * 100);
          if (inventory.availableStock === inventory.totalStock) {
            inventory.price = newPrice;
            inventory.totalStock = v.stock as number;
            inventory.availableStock = v.stock as number;
          } else {
            const diff = v.stock as number - inventory.totalStock;
            inventory.price = newPrice;
            inventory.totalStock = v.stock as number;
            inventory.availableStock = Math.max(0, inventory.availableStock + diff);
          }
          await inventory.save();
        }
      }
    } else {
      const attrs: [string, string][] = [];
      if (v.size) attrs.push(['size', v.size]);
      if (v.color) attrs.push(['color', v.color]);
      const variant = await ProductVariant.create({
        productId: product._id,
        sku: `SKU-${product._id.toString().slice(-6)}-${Date.now().toString(36)}`,
        attributes: new Map<string, string>(attrs),
        isActive: true,
      });
      newVariantIds.push(variant._id.toString());

      await Inventory.create({
        storeId: store._id,
        productVariantId: variant._id,
        price: Math.round((v.price as number) * 100),
        totalStock: v.stock as number,
        availableStock: v.stock as number,
        reservedStock: 0,
        soldStock: 0,
        lowStockThreshold: LOW_STOCK_THRESHOLD,
      });
    }
  }

  for (const existing of existingVariants) {
    if (!newVariantIds.includes(existing._id.toString())) {
      const inv = await Inventory.findOne({
        storeId: store._id,
        productVariantId: existing._id,
      });
      if (inv && inv.availableStock === inv.totalStock && inv.reservedStock === 0) {
        await Inventory.deleteOne({ _id: inv._id });
        await ProductVariant.deleteOne({ _id: existing._id });
      }
    }
  }

  logger.info('Product updated', { productId: product._id, retailerId });
  return buildProductDto(product._id);
}

export async function deleteProduct(retailerId: string, productId: string): Promise<void> {
  const store = await Store.findOne({ retailerId: new Types.ObjectId(retailerId) });
  if (!store) throw new NotFoundError('Store');

  const product = await Product.findById(productId);
  if (!product) throw new NotFoundError('Product');

  const variants = await ProductVariant.find({ productId });
  const variantIds = variants.map((v) => v._id);
  const inventories = await Inventory.find({ storeId: store._id, productVariantId: { $in: variantIds } });

  for (const inv of inventories) {
    if (inv.reservedStock > 0) {
      throw new ConflictError('Cannot delete product with active reservations');
    }
  }

  await Inventory.deleteMany({ storeId: store._id, productVariantId: { $in: variantIds } });
  await ProductVariant.deleteMany({ productId });
  await Product.deleteOne({ _id: productId });

  logger.info('Product deleted', { productId, retailerId });
}

export async function updateInventory(
  retailerId: string,
  inventoryId: string,
  input: Record<string, unknown>
): Promise<object> {
  const store = await Store.findOne({ retailerId: new Types.ObjectId(retailerId) });
  if (!store) throw new NotFoundError('Store');

  const inventory = await Inventory.findOne({ _id: inventoryId, storeId: store._id });
  if (!inventory) throw new NotFoundError('Inventory');

  const newTotal = input.stock as number;
  if (newTotal < inventory.reservedStock) {
    throw new ConflictError('Total stock cannot be less than reserved stock');
  }

  inventory.totalStock = newTotal;
  inventory.availableStock = newTotal - inventory.reservedStock;
  await inventory.save();

  logger.info('Inventory updated', { inventoryId, retailerId, newTotal });
  return transformInventory(inventory);
}

export async function getReservations(retailerId: string): Promise<object[]> {
  const store = await Store.findOne({ retailerId: new Types.ObjectId(retailerId) }).select('_id');
  if (!store) throw new NotFoundError('Store');

  const reservations = await Reservation.find({ storeId: store._id })
    .sort({ createdAt: -1 })
    .lean();

  return reservations.map(buildBusinessReservationDto);
}

export async function getReservationById(retailerId: string, reservationId: string): Promise<object> {
  const store = await Store.findOne({ retailerId: new Types.ObjectId(retailerId) }).select('_id');
  if (!store) throw new NotFoundError('Store');

  const reservation = await Reservation.findOne({ _id: reservationId, storeId: store._id }).lean();
  if (!reservation) throw new NotFoundError('Reservation');

  return buildBusinessReservationDto(reservation);
}

export async function confirmReservation(retailerId: string, reservationId: string): Promise<object> {
  const store = await Store.findOne({ retailerId: new Types.ObjectId(retailerId) }).select('_id');
  if (!store) throw new NotFoundError('Store');

  const reservation = await Reservation.findOne({ _id: reservationId, storeId: store._id });
  if (!reservation) throw new NotFoundError('Reservation');

  if (reservation.status !== RESERVATION_STATUS.PENDING) {
    throw new ConflictError('Can only confirm PENDING reservations');
  }

  reservation.status = RESERVATION_STATUS.CONFIRMED;
  await reservation.save();

  logger.info('Reservation confirmed', { reservationId, retailerId });
  return buildBusinessReservationDto(reservation);
}

export async function completeReservation(retailerId: string, reservationId: string): Promise<object> {
  const store = await Store.findOne({ retailerId: new Types.ObjectId(retailerId) }).select('_id');
  if (!store) throw new NotFoundError('Store');

  const reservation = await Reservation.findOne({ _id: reservationId, storeId: store._id });
  if (!reservation) throw new NotFoundError('Reservation');

  if (![RESERVATION_STATUS.CONFIRMED, RESERVATION_STATUS.ACTIVE].includes(reservation.status as any)) {
    throw new ConflictError('Can only complete CONFIRMED or ACTIVE reservations');
  }

  reservation.status = RESERVATION_STATUS.COMPLETED;
  reservation.completedAt = new Date();
  await reservation.save();

  await Inventory.findByIdAndUpdate(reservation.inventoryId, {
    $inc: { reservedStock: -reservation.quantity, soldStock: reservation.quantity },
  });

  logger.info('Reservation completed', { reservationId, retailerId });
  return buildBusinessReservationDto(reservation);
}

export async function cancelReservation(retailerId: string, reservationId: string): Promise<object> {
  const store = await Store.findOne({ retailerId: new Types.ObjectId(retailerId) }).select('_id');
  if (!store) throw new NotFoundError('Store');

  const reservation = await Reservation.findOne({ _id: reservationId, storeId: store._id });
  if (!reservation) throw new NotFoundError('Reservation');

  if (![RESERVATION_STATUS.PENDING, RESERVATION_STATUS.CONFIRMED].includes(reservation.status as any)) {
    throw new ConflictError('Can only cancel PENDING or CONFIRMED reservations');
  }

  reservation.status = RESERVATION_STATUS.CANCELLED;
  reservation.cancelledAt = new Date();
  await reservation.save();

  await Inventory.findByIdAndUpdate(reservation.inventoryId, {
    $inc: { reservedStock: -reservation.quantity, availableStock: reservation.quantity },
  });

  logger.info('Reservation cancelled by business', { reservationId, retailerId });
  return buildBusinessReservationDto(reservation);
}

export async function getDashboardStats(retailerId: string): Promise<object> {
  const store = await Store.findOne({ retailerId: new Types.ObjectId(retailerId) }).select('_id');
  if (!store) throw new NotFoundError('Store');

  const productIds = await Inventory.distinct('productVariantId', { storeId: store._id });
  const variants = await ProductVariant.find({ _id: { $in: productIds } });
  const uniqueProductIds = [...new Set(variants.map((v) => v.productId.toString()))];

  const totalProducts = uniqueProductIds.length;

  const inventories = await Inventory.find({ storeId: store._id });
  const availableProducts = new Set(
    inventories.filter((i) => i.availableStock > 0).map((i) => i.productVariantId.toString())
  );
  const availableProductCount = variants
    .filter((v) => availableProducts.has(v._id.toString()))
    .map((v) => v.productId.toString());
  const availableProductsSet = new Set(availableProductCount);

  const lowStockProducts = new Set(
    inventories
      .filter((i) => i.availableStock > 0 && i.availableStock <= i.lowStockThreshold)
      .map((i) => i.productVariantId.toString())
  );
  const lowStockProductCount = variants
    .filter((v) => lowStockProducts.has(v._id.toString()))
    .map((v) => v.productId.toString());
  const lowStockProductsSet = new Set(lowStockProductCount);

  const todayStart = new Date();
  todayStart.setHours(0, 0, 0, 0);

  const [activeRes, todayRes] = await Promise.all([
    Reservation.countDocuments({
      storeId: store._id,
      status: { $in: [RESERVATION_STATUS.PENDING, RESERVATION_STATUS.CONFIRMED, RESERVATION_STATUS.ACTIVE] },
    }),
    Reservation.countDocuments({
      storeId: store._id,
      createdAt: { $gte: todayStart },
    }),
  ]);

  return {
    totalProducts,
    availableProducts: availableProductsSet.size,
    lowStockProducts: lowStockProductsSet.size,
    activeReservations: activeRes,
    todayReservations: todayRes,
  };
}

async function buildProductDto(productId: Types.ObjectId, variants?: any[]): Promise<object> {
  const product = await Product.findById(productId).lean();
  if (!product) throw new NotFoundError('Product');

  const productVariants = variants || (await ProductVariant.find({ productId }).lean());
  const variantIds = productVariants.map((v) => v._id);

  const inventories = await Inventory.find({ productVariantId: { $in: variantIds } }).lean();
  const inventoryMap = new Map(inventories.map((i) => [i.productVariantId.toString(), i]));

  const category = product.categoryId
    ? await Category.findById(product.categoryId).select('name').lean()
    : null;

  const variantDtos = productVariants.map((v) => {
    const inv = inventoryMap.get(v._id.toString());
    const stockStatus = inv
      ? inv.availableStock === 0
        ? 'OUT_OF_STOCK'
        : inv.availableStock <= inv.lowStockThreshold
          ? 'LOW_STOCK'
          : 'AVAILABLE'
      : 'OUT_OF_STOCK';
    return {
      _id: v._id,
      size: getAttr(v.attributes, 'size'),
      color: getAttr(v.attributes, 'color'),
      price: inv ? inv.price / 100 : 0,
      inventory: inv
        ? {
            _id: inv._id,
            totalStock: inv.totalStock,
            availableStock: inv.availableStock,
            reservedStock: inv.reservedStock,
            status: stockStatus,
          }
        : null,
    };
  });

  return {
    _id: product._id,
    name: product.name,
    brand: product.brand,
    description: product.description,
    imageUrls: product.images,
    category: category?.name ?? '',
    variants: variantDtos,
  };
}

function buildBusinessReservationDto(reservation: any): object {
  const variantDescription = reservation.variantDescription || '';
  return {
    _id: reservation._id,
    customerName: reservation.customerName,
    customerMobile: reservation.customerMobile,
    productName: reservation.productName,
    variantDescription,
    price: reservation.price / 100,
    status: reservation.status,
    createdAt: reservation.createdAt,
    expiresAt: reservation.expiresAt,
    completedAt: reservation.completedAt,
    cancelledAt: reservation.cancelledAt,
  };
}

function transformStore(store: any): object {
  const [lng, lat] = store.location?.coordinates || [0, 0];
  return {
    _id: store._id,
    name: store.name,
    address: store.address,
    latitude: lat,
    longitude: lng,
    phone: store.phone,
    openingHours: store.openingHours,
    categories: store.categories || [],
    imageUrl: store.images?.[0] || null,
    verificationStatus: store.isActive ? 'VERIFIED' : 'REJECTED',
  };
}

function transformInventory(inventory: any): object {
  const stockStatus =
    inventory.availableStock === 0
      ? 'OUT_OF_STOCK'
      : inventory.availableStock <= inventory.lowStockThreshold
        ? 'LOW_STOCK'
        : 'AVAILABLE';

  return {
    _id: inventory._id,
    totalStock: inventory.totalStock,
    availableStock: inventory.availableStock,
    reservedStock: inventory.reservedStock,
    status: stockStatus,
  };
}
