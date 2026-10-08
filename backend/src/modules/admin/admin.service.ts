import { Types } from 'mongoose';
import { User } from '../../models/User';
import { Retailer } from '../../models/Retailer';
import { Store } from '../../models/Store';
import { Product } from '../../models/Product';
import { ProductVariant } from '../../models/ProductVariant';
import { Reservation } from '../reservations/reservation.model';
import { Category } from '../business/category.model';
import { Inventory } from '../../models/Inventory';
import { Inquiry } from '../../models/Inquiry';
import { PlatformSettings } from '../../models/PlatformSettings';
import { ConflictError, NotFoundError } from '../../utils/errors';
import * as businessService from '../business/business.service';

// ── Dashboard Stats ───────────────────────────────────────────────────────────

export async function getAdminStats(): Promise<object> {
  const now = new Date();
  const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
  const startOfLastMonth = new Date(now.getFullYear(), now.getMonth() - 1, 1);
  const endOfLastMonth = new Date(now.getFullYear(), now.getMonth(), 0);

  const [
    totalCustomers, totalStores, totalReservations, totalProducts,
    newCustomersThisMonth, newCustomersLastMonth,
    newStoresThisMonth, newStoresLastMonth,
    reservationsThisMonth, reservationsLastMonth,
    activeReservations,
  ] = await Promise.all([
    User.countDocuments(),
    Store.countDocuments(),
    Reservation.countDocuments(),
    Product.countDocuments({ isActive: true }),
    User.countDocuments({ createdAt: { $gte: startOfMonth } }),
    User.countDocuments({ createdAt: { $gte: startOfLastMonth, $lte: endOfLastMonth } }),
    Store.countDocuments({ createdAt: { $gte: startOfMonth } }),
    Store.countDocuments({ createdAt: { $gte: startOfLastMonth, $lte: endOfLastMonth } }),
    Reservation.countDocuments({ createdAt: { $gte: startOfMonth } }),
    Reservation.countDocuments({ createdAt: { $gte: startOfLastMonth, $lte: endOfLastMonth } }),
    Reservation.countDocuments({ status: { $in: ['PENDING', 'CONFIRMED'] } }),
  ]);

  const growth = (curr: number, prev: number) =>
    prev === 0 ? 100 : Math.round(((curr - prev) / prev) * 100);

  return {
    totalCustomers,
    totalStores,
    totalReservations,
    totalProducts,
    activeReservations,
    customersGrowth: growth(newCustomersThisMonth, newCustomersLastMonth),
    storesGrowth: growth(newStoresThisMonth, newStoresLastMonth),
    reservationsGrowth: growth(reservationsThisMonth, reservationsLastMonth),
  };
}

export async function getRevenueChart(): Promise<object[]> {
  const months = [];
  for (let i = 5; i >= 0; i--) {
    const d = new Date();
    d.setMonth(d.getMonth() - i);
    const start = new Date(d.getFullYear(), d.getMonth(), 1);
    const end = new Date(d.getFullYear(), d.getMonth() + 1, 0);
    const [reservations, completed] = await Promise.all([
      Reservation.countDocuments({ createdAt: { $gte: start, $lte: end } }),
      Reservation.countDocuments({ status: 'COMPLETED', createdAt: { $gte: start, $lte: end } }),
    ]);
    const revenueAgg = await Reservation.aggregate([
      { $match: { status: 'COMPLETED', createdAt: { $gte: start, $lte: end } } },
      { $group: { _id: null, total: { $sum: '$price' } } },
    ]);
    months.push({
      month: start.toLocaleString('en-IN', { month: 'short' }),
      reservations,
      completed,
      revenue: revenueAgg[0]?.total ?? 0,
    });
  }
  return months;
}

// ── Users ─────────────────────────────────────────────────────────────────────

export async function listUsers(page = 1, limit = 10, search = ''): Promise<object> {
  const filter = search
    ? { $or: [{ name: { $regex: search, $options: 'i' } }, { phone: { $regex: search, $options: 'i' } }, { email: { $regex: search, $options: 'i' } }] }
    : {};
  const [users, total] = await Promise.all([
    User.find(filter).select('-otp -otpExpiresAt').sort({ createdAt: -1 }).skip((page - 1) * limit).limit(limit).lean(),
    User.countDocuments(filter),
  ]);
  return { users, total, page, pages: Math.ceil(total / limit) };
}

export async function getUserById(id: string): Promise<object> {
  const user = await User.findById(id).select('-otp -otpExpiresAt').lean();
  if (!user) throw new NotFoundError('User');
  const reservations = await Reservation.find({ customerId: user._id }).sort({ createdAt: -1 }).limit(10).lean();
  return { ...user, reservations };
}

export async function deleteUser(id: string): Promise<void> {
  const user = await User.findById(id);
  if (!user) throw new NotFoundError('User');
  await User.deleteOne({ _id: id });
}

export async function blockUser(id: string, isBlocked: boolean, blockReason = ''): Promise<object> {
  const user = await User.findById(id);
  if (!user) throw new NotFoundError('User');
  (user as any).isBlocked = isBlocked;
  (user as any).blockedAt = isBlocked ? new Date() : undefined;
  (user as any).blockReason = isBlocked ? (blockReason || undefined) : undefined;
  await user.save();
  return { _id: user._id, isBlocked, blockedAt: (user as any).blockedAt, blockReason: (user as any).blockReason };
}

async function countStoreProducts(storeId: Types.ObjectId): Promise<number> {
  const variantIds = await Inventory.distinct('productVariantId', { storeId });
  if (variantIds.length === 0) return 0;
  const productIds = await ProductVariant.distinct('productId', { _id: { $in: variantIds } });
  return productIds.length;
}

// ── Businesses (Retailers) ───────────────────────────────────────────────────

export async function listBusinesses(page = 1, limit = 10, search = ''): Promise<object> {
  const filter: any = {};
  if (search) filter.$or = [
    { ownerName: { $regex: search, $options: 'i' } },
    { businessName: { $regex: search, $options: 'i' } },
    { phone: { $regex: search, $options: 'i' } },
  ];
  const [retailers, total] = await Promise.all([
    Retailer.find(filter).sort({ createdAt: -1 }).skip((page - 1) * limit).limit(limit).lean(),
    Retailer.countDocuments(filter),
  ]);
  const enriched = await Promise.all(retailers.map(async (r) => {
    const store = await Store.findOne({ retailerId: r._id }).select('_id name isActive').lean();
    return { ...r, store };
  }));
  return { businesses: enriched, total, page, pages: Math.ceil(total / limit) };
}

// ── Stores ────────────────────────────────────────────────────────────────────

export async function listStores(page = 1, limit = 10, search = '', status = ''): Promise<object> {
  const filter: any = {};
  if (search) filter.$or = [{ name: { $regex: search, $options: 'i' } }, { address: { $regex: search, $options: 'i' } }];
  if (status === 'active') filter.isActive = true;
  if (status === 'inactive') filter.isActive = false;

  const [stores, total] = await Promise.all([
    Store.find(filter).sort({ createdAt: -1 }).skip((page - 1) * limit).limit(limit).lean(),
    Store.countDocuments(filter),
  ]);

  const enriched = await Promise.all(stores.map(async (s) => {
    const [productCount, reservationCount, retailer] = await Promise.all([
      countStoreProducts(s._id),
      Reservation.countDocuments({ storeId: s._id }),
      Retailer.findById(s.retailerId).select('ownerName businessName phone email').lean(),
    ]);
    return { ...s, productCount, reservationCount, retailer };
  }));

  return { stores: enriched, total, page, pages: Math.ceil(total / limit) };
}

export async function getStoreById(id: string): Promise<object> {
  const store = await Store.findById(id).lean();
  if (!store) throw new NotFoundError('Store');
  const [retailer, productCount, reservationCount, recentReservations] = await Promise.all([
    Retailer.findById(store.retailerId).select('ownerName businessName phone email category verificationStatus isBlocked blockedAt blockReason createdAt updatedAt').lean(),
    countStoreProducts(store._id),
    Reservation.countDocuments({ storeId: store._id }),
    Reservation.find({ storeId: store._id }).sort({ createdAt: -1 }).limit(5).lean(),
  ]);
  return { ...store, retailer, productCount, reservationCount, recentReservations };
}

export async function updateStoreProfile(id: string, input: Record<string, any>): Promise<object> {
  const store = await Store.findById(id);
  if (!store) throw new NotFoundError('Store');
  const retailer = await Retailer.findById(store.retailerId);
  if (!retailer) throw new NotFoundError('Retailer');

  const retailerInput = input.retailer ?? {};
  const storeInput = input.store ?? {};
  for (const field of ['ownerName', 'businessName', 'phone', 'category', 'verificationStatus'] as const) {
    if (retailerInput[field] !== undefined) (retailer as any)[field] = retailerInput[field];
  }
  if (retailerInput.email !== undefined) retailer.email = retailerInput.email || undefined;
  try {
    await retailer.save();
  } catch (error: any) {
    if (error.code === 11000) throw new ConflictError('That business phone number is already in use');
    throw error;
  }

  for (const field of ['name', 'description', 'address', 'phone', 'openingHours', 'categories'] as const) {
    if (storeInput[field] !== undefined) (store as any)[field] = storeInput[field];
  }
  if (storeInput.imageUrl !== undefined) store.images = storeInput.imageUrl ? [storeInput.imageUrl] : [];
  if (storeInput.latitude !== undefined || storeInput.longitude !== undefined) {
    const [currentLongitude, currentLatitude] = store.location.coordinates;
    store.location = {
      type: 'Point',
      coordinates: [storeInput.longitude ?? currentLongitude, storeInput.latitude ?? currentLatitude],
    };
  }
  await store.save();
  return getStoreById(id);
}

export async function setRetailerBlocked(id: string, isBlocked: boolean, blockReason = ''): Promise<object> {
  const store = await Store.findById(id).select('retailerId');
  if (!store) throw new NotFoundError('Store');
  const retailer = await Retailer.findById(store.retailerId);
  if (!retailer) throw new NotFoundError('Retailer');
  retailer.isBlocked = isBlocked;
  retailer.blockedAt = isBlocked ? new Date() : undefined;
  retailer.blockReason = isBlocked ? (blockReason || undefined) : undefined;
  await retailer.save();
  return {
    storeId: store._id,
    retailerId: retailer._id,
    isBlocked: retailer.isBlocked,
    blockedAt: retailer.blockedAt,
    blockReason: retailer.blockReason,
  };
}

export async function listStoreProducts(id: string): Promise<object[]> {
  const store = await Store.findById(id).select('_id');
  if (!store) throw new NotFoundError('Store');
  const inventories = await Inventory.find({ storeId: store._id }).lean();
  const variantIds = inventories.map((inventory) => inventory.productVariantId);
  const variants = await ProductVariant.find({ _id: { $in: variantIds } }).lean();
  const productIds = [...new Set(variants.map((variant) => variant.productId.toString()))];
  const [products, categories] = await Promise.all([
    Product.find({ _id: { $in: productIds } }).sort({ name: 1 }).lean(),
    Category.find().select('name').lean(),
  ]);
  const categoryNames = new Map(categories.map((category) => [category._id.toString(), category.name]));
  const inventoryByVariant = new Map(inventories.map((inventory) => [inventory.productVariantId.toString(), inventory]));

  return products.map((product) => ({
    _id: product._id,
    name: product.name,
    brand: product.brand,
    description: product.description,
    category: product.categoryId ? categoryNames.get(product.categoryId.toString()) ?? '' : '',
    images: product.images,
    isActive: product.isActive,
    variants: variants.filter((variant) => variant.productId.toString() === product._id.toString()).flatMap((variant) => {
      const inventory = inventoryByVariant.get(variant._id.toString());
      if (!inventory) return [];
      const attributes = variant.attributes instanceof Map
        ? Object.fromEntries(variant.attributes)
        : variant.attributes as Record<string, string>;
      return [{
        _id: variant._id,
        size: attributes?.size ?? '',
        color: attributes?.color ?? '',
        price: inventory.price / 100,
        inventory: {
          _id: inventory._id,
          totalStock: inventory.totalStock,
          availableStock: inventory.availableStock,
          reservedStock: inventory.reservedStock,
          soldStock: inventory.soldStock,
          lowStockThreshold: inventory.lowStockThreshold,
        },
      }];
    }),
  }));
}

async function getStoreProductOwner(storeId: string, productId: string): Promise<string> {
  const store = await Store.findById(storeId).select('retailerId');
  if (!store) throw new NotFoundError('Store');
  const product = await Product.findById(productId).select('_id');
  if (!product) throw new NotFoundError('Product');
  const variantIds = await ProductVariant.distinct('_id', { productId: product._id });
  const inventory = await Inventory.findOne({ storeId, productVariantId: { $in: variantIds } }).select('_id');
  if (!inventory) throw new NotFoundError('Store product');
  const sharedInventory = await Inventory.exists({ storeId: { $ne: store._id }, productVariantId: { $in: variantIds } });
  if (sharedInventory) throw new ConflictError('This product is shared with another store and cannot be edited here');
  return store.retailerId.toString();
}

export async function createStoreProduct(id: string, input: Record<string, unknown>): Promise<object> {
  const store = await Store.findById(id).select('retailerId');
  if (!store) throw new NotFoundError('Store');
  return businessService.createProduct(store.retailerId.toString(), input);
}

export async function updateStoreProduct(id: string, productId: string, input: Record<string, unknown>): Promise<object> {
  const retailerId = await getStoreProductOwner(id, productId);
  const variants = input.variants as Array<{ _id?: string; stock: number }>;
  for (const variant of variants) {
    if (!variant._id) continue;
    const inventory = await Inventory.findOne({ storeId: id, productVariantId: variant._id }).select('reservedStock');
    if (inventory && variant.stock < inventory.reservedStock) {
      throw new ConflictError('Stock cannot be lower than the quantity already reserved');
    }
  }
  return businessService.updateProduct(retailerId, productId, input);
}

export async function deleteStoreProduct(id: string, productId: string): Promise<void> {
  const retailerId = await getStoreProductOwner(id, productId);
  await businessService.deleteProduct(retailerId, productId);
}

export async function updateStoreInventory(id: string, inventoryId: string, input: Record<string, unknown>): Promise<object> {
  const inventory = await Inventory.findOne({ _id: inventoryId, storeId: id });
  if (!inventory) throw new NotFoundError('Inventory');
  if (input.stock !== undefined) {
    const stock = input.stock as number;
    if (stock < inventory.reservedStock) throw new ConflictError('Stock cannot be lower than reserved quantity');
    inventory.totalStock = stock;
    inventory.availableStock = stock - inventory.reservedStock;
  }
  if (input.price !== undefined) inventory.price = Math.round((input.price as number) * 100);
  await inventory.save();
  return {
    _id: inventory._id,
    price: inventory.price / 100,
    totalStock: inventory.totalStock,
    availableStock: inventory.availableStock,
    reservedStock: inventory.reservedStock,
    soldStock: inventory.soldStock,
  };
}

export async function listStoreActivity(id: string, page = 1, limit = 20): Promise<object> {
  const store = await Store.findById(id).select('_id');
  if (!store) throw new NotFoundError('Store');
  const [reservations, total] = await Promise.all([
    Reservation.find({ storeId: store._id }).sort({ createdAt: -1 }).skip((page - 1) * limit).limit(limit).lean(),
    Reservation.countDocuments({ storeId: store._id }),
  ]);
  return {
    reservations: reservations.map((reservation) => ({ ...reservation, price: reservation.price / 100 })),
    total,
    page,
    pages: Math.ceil(total / limit),
  };
}

export async function toggleStoreStatus(id: string): Promise<object> {
  const store = await Store.findById(id);
  if (!store) throw new NotFoundError('Store');
  store.isActive = !store.isActive;
  await store.save();
  return { _id: store._id, isActive: store.isActive };
}

// ── Reservations ──────────────────────────────────────────────────────────────

export async function listReservations(page = 1, limit = 10, search = '', status = ''): Promise<object> {
  const filter: any = {};
  if (search) filter.$or = [
    { customerName: { $regex: search, $options: 'i' } },
    { reservationCode: { $regex: search, $options: 'i' } },
    { productName: { $regex: search, $options: 'i' } },
  ];
  if (status) filter.status = status;

  const [reservations, total] = await Promise.all([
    Reservation.find(filter).sort({ createdAt: -1 }).skip((page - 1) * limit).limit(limit).lean(),
    Reservation.countDocuments(filter),
  ]);

  const sanitized = reservations.map((r) => ({ ...r, price: r.price / 100 }));
  return { reservations: sanitized, total, page, pages: Math.ceil(total / limit) };
}

// ── Products ──────────────────────────────────────────────────────────────────

export async function listProducts(page = 1, limit = 10, search = ''): Promise<object> {
  const filter: any = { isActive: true };
  if (search) filter.name = { $regex: search, $options: 'i' };

  const [products, total] = await Promise.all([
    Product.find(filter).sort({ createdAt: -1 }).skip((page - 1) * limit).limit(limit).lean(),
    Product.countDocuments(filter),
  ]);

  const enriched = await Promise.all(products.map(async (p) => {
    const category = p.categoryId ? await Category.findById(p.categoryId).select('name').lean() : null;
    const inventory = await Inventory.findOne({ productVariantId: { $in: await import('../../models/ProductVariant').then(m => m.ProductVariant.distinct('_id', { productId: p._id })) } }).lean();
    return {
      ...p,
      categoryName: category?.name ?? '',
      price: inventory ? inventory.price / 100 : 0,
      stock: inventory?.availableStock ?? 0,
    };
  }));

  return { products: enriched, total, page, pages: Math.ceil(total / limit) };
}

export async function toggleProductStatus(id: string): Promise<object> {
  const product = await Product.findById(id);
  if (!product) throw new NotFoundError('Product');
  product.isActive = !product.isActive;
  await product.save();
  return { _id: product._id, isActive: product.isActive };
}

// ── Categories ────────────────────────────────────────────────────────────────

export async function listAdminCategories(): Promise<object[]> {
  const categories = await Category.find().sort({ name: 1 }).lean();
  return Promise.all(categories.map(async (c) => {
    const storeCount = await Store.countDocuments({ categories: c.name });
    return { ...c, storeCount };
  }));
}

export async function createAdminCategory(name: string, description?: string): Promise<object> {
  const category = await Category.create({ name, description, isActive: true });
  return category;
}

export async function updateAdminCategory(id: string, data: Record<string, unknown>): Promise<object> {
  const category = await Category.findById(id);
  if (!category) throw new NotFoundError('Category');
  if (data.name !== undefined) category.name = data.name as string;
  if (data.description !== undefined) category.description = data.description as string;
  if (data.isActive !== undefined) category.isActive = data.isActive as boolean;
  await category.save();
  return category;
}

export async function deleteAdminCategory(id: string): Promise<void> {
  const category = await Category.findById(id);
  if (!category) throw new NotFoundError('Category');
  await Category.deleteOne({ _id: id });
}

// ── Inquiries ─────────────────────────────────────────────────────────────────

export async function saveInquiry(data: Record<string, unknown>): Promise<object> {
  return Inquiry.create(data);
}

export async function listInquiries(page = 1, limit = 20, status = ''): Promise<object> {
  const filter = status ? { status } : {};
  const [inquiries, total] = await Promise.all([
    Inquiry.find(filter).sort({ createdAt: -1 }).skip((page - 1) * limit).limit(limit).lean(),
    Inquiry.countDocuments(filter),
  ]);
  return { inquiries, total, page, pages: Math.ceil(total / limit) };
}

export async function updateInquiryStatus(id: string, status: 'NEW' | 'READ' | 'CLOSED'): Promise<object> {
  const inquiry = await Inquiry.findByIdAndUpdate(id, { $set: { status } }, { new: true, runValidators: true }).lean();
  if (!inquiry) throw new NotFoundError('Inquiry');
  return inquiry;
}

const DEFAULT_PLATFORM_SETTINGS = {
  key: 'global' as const,
  general: { siteName: 'AasPaas Wala', supportEmail: 'support@aaspaas.in', supportPhone: '', timezone: 'Asia/Kolkata' },
  notifications: { newStore: true, newReservation: true, expiredReservation: false, newCustomer: true },
  security: { twoFactor: false, sessionTimeout: 24, loginAlerts: true },
  appearance: { brandColor: '#f97316' },
};

export async function getPlatformSettings(): Promise<object> {
  const settings = await PlatformSettings.findOneAndUpdate(
    { key: 'global' },
    { $setOnInsert: DEFAULT_PLATFORM_SETTINGS },
    { upsert: true, new: true, setDefaultsOnInsert: true }
  ).lean();
  if (!settings) throw new Error('Could not load platform settings');
  return settings;
}

export async function updatePlatformSettings(data: Record<string, unknown>): Promise<object> {
  const settings = await PlatformSettings.findOneAndUpdate(
    { key: 'global' },
    { $set: data, $setOnInsert: { key: 'global' } },
    { upsert: true, new: true, setDefaultsOnInsert: true, runValidators: true }
  ).lean();
  if (!settings) throw new Error('Could not save platform settings');
  return settings;
}
