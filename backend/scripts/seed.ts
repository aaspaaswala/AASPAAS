import mongoose from 'mongoose';
import dotenv from 'dotenv';
dotenv.config();

import { User } from '../src/models/User';
import { Retailer } from '../src/models/Retailer';
import { Store } from '../src/models/Store';
import { Product } from '../src/models/Product';
import { ProductVariant } from '../src/models/ProductVariant';
import { Inventory } from '../src/models/Inventory';
import { Reservation } from '../src/modules/reservations/reservation.model';
import { Category } from '../src/modules/business/category.model';

const MONGO_URI = process.env.MONGODB_URI!;

async function seed() {
  if (process.env.NODE_ENV === 'production' || process.env.ALLOW_DESTRUCTIVE_SEED !== 'true') {
    throw new Error('Demo seeding is destructive; set ALLOW_DESTRUCTIVE_SEED=true in a non-production environment.');
  }
  if (!MONGO_URI) throw new Error('MONGODB_URI is required');
  console.log('🌱 Connecting to MongoDB...');
  await mongoose.connect(MONGO_URI);
  console.log('✅ Connected');

  await Promise.all([
    User.deleteMany({}), Retailer.deleteMany({}), Store.deleteMany({}),
    Product.deleteMany({}), ProductVariant.deleteMany({}), Inventory.deleteMany({}),
    Reservation.deleteMany({}), Category.deleteMany({}),
  ]);
  console.log('🗑️  Cleared existing data');

  // Categories
  const categories = await Category.insertMany([
    { name: 'Electronics', description: 'Phones, laptops, gadgets', isActive: true },
    { name: 'Clothing', description: 'Fashion and apparel', isActive: true },
    { name: 'Footwear', description: 'Shoes, sandals, boots', isActive: true },
    { name: 'Grocery', description: 'Daily essentials', isActive: true },
    { name: 'Furniture', description: 'Home and office furniture', isActive: true },
  ]);
  const catMap: Record<string, any> = Object.fromEntries(categories.map((c) => [c.name, c._id]));
  console.log(`✅ ${categories.length} categories`);

  // Customers
  const customers = await User.insertMany([
    { name: 'Rahul Sharma', phone: '+919876543210', email: 'rahul@example.com', subscription: { plan: 'FREE' }, location: undefined },
    { name: 'Priya Mehta', phone: '+918765432109', email: 'priya@example.com', subscription: { plan: 'FREE' }, location: undefined },
    { name: 'Amit Verma', phone: '+917654321098', email: 'amit@example.com', subscription: { plan: 'FREE' }, location: undefined },
    { name: 'Sunita Patel', phone: '+916543210987', email: 'sunita@example.com', subscription: { plan: 'FREE' }, location: undefined },
    { name: 'Vikram Singh', phone: '+915432109876', email: 'vikram@example.com', subscription: { plan: 'FREE' }, location: undefined },
  ]);
  console.log(`✅ ${customers.length} customers`);

  // Retailers
  const retailers = await Retailer.insertMany([
    { ownerName: 'Ramesh Sharma', businessName: 'Sharma Electronics', phone: '+911111111111', email: 'sharma@biz.com', category: 'Electronics', verificationStatus: 'VERIFIED' },
    { ownerName: 'Suresh Gupta', businessName: 'Gupta Fashion House', phone: '+912222222222', email: 'gupta@biz.com', category: 'Clothing', verificationStatus: 'VERIFIED' },
    { ownerName: 'Vijay Modi', businessName: 'Modi Grocery Store', phone: '+913333333333', email: 'modi@biz.com', category: 'Grocery', verificationStatus: 'VERIFIED' },
    { ownerName: 'Priya Patel', businessName: 'Nike Exclusive', phone: '+914444444444', email: 'patel@biz.com', category: 'Footwear', verificationStatus: 'VERIFIED' },
  ]);
  console.log(`✅ ${retailers.length} retailers`);

  // Stores
  const stores = await Store.insertMany([
    { retailerId: retailers[0]._id, name: 'Sharma Electronics', address: 'Shop 12, Connaught Place, New Delhi', location: { type: 'Point', coordinates: [77.2090, 28.6315] }, phone: '+911111111111', openingHours: '10:00 AM – 8:00 PM', categories: ['Electronics'], rating: 4.8, isActive: true },
    { retailerId: retailers[1]._id, name: 'Gupta Fashion House', address: 'Shop 5, Linking Road, Bandra, Mumbai', location: { type: 'Point', coordinates: [72.8347, 19.0596] }, phone: '+912222222222', openingHours: '11:00 AM – 9:00 PM', categories: ['Clothing'], rating: 4.5, isActive: true },
    { retailerId: retailers[2]._id, name: 'Modi Grocery Store', address: 'Plot 8, MI Road, Jaipur', location: { type: 'Point', coordinates: [75.7873, 26.9124] }, phone: '+913333333333', openingHours: '8:00 AM – 10:00 PM', categories: ['Grocery'], rating: 4.7, isActive: true },
    { retailerId: retailers[3]._id, name: 'Nike Exclusive', address: 'UB City Mall, Vittal Mallya Road, Bangalore', location: { type: 'Point', coordinates: [77.5946, 12.9716] }, phone: '+914444444444', openingHours: '10:00 AM – 9:00 PM', categories: ['Footwear'], rating: 4.9, isActive: true },
  ]);
  console.log(`✅ ${stores.length} stores`);

  // Products + Variants + Inventory
  const productsData = [
    { name: 'iPhone 15 Pro', brand: 'Apple', description: 'A17 Pro chip, titanium design', catKey: 'Electronics', storeIdx: 0, variants: [{ attrs: { storage: '128GB', color: 'Black Titanium' }, price: 13490000, stock: 5 }, { attrs: { storage: '256GB', color: 'Natural Titanium' }, price: 14990000, stock: 3 }] },
    { name: 'Samsung Galaxy S24', brand: 'Samsung', description: 'Galaxy AI smartphone', catKey: 'Electronics', storeIdx: 0, variants: [{ attrs: { storage: '128GB', color: 'Onyx Black' }, price: 7999900, stock: 8 }, { attrs: { storage: '256GB', color: 'Marble Gray' }, price: 8999900, stock: 4 }] },
    { name: 'Sony WH-1000XM5', brand: 'Sony', description: 'Noise cancelling headphones', catKey: 'Electronics', storeIdx: 0, variants: [{ attrs: { color: 'Black' }, price: 2999000, stock: 10 }, { attrs: { color: 'Silver' }, price: 2999000, stock: 6 }] },
    { name: "Levi's 511 Slim Jeans", brand: "Levi's", description: 'Classic slim fit jeans', catKey: 'Clothing', storeIdx: 1, variants: [{ attrs: { size: '30', color: 'Dark Blue' }, price: 399900, stock: 15 }, { attrs: { size: '32', color: 'Black' }, price: 399900, stock: 12 }] },
    { name: 'Nike Air Max 270', brand: 'Nike', description: 'Lifestyle shoe with large Air unit', catKey: 'Footwear', storeIdx: 3, variants: [{ attrs: { size: '8', color: 'White' }, price: 1299500, stock: 6 }, { attrs: { size: '9', color: 'Black' }, price: 1299500, stock: 4 }] },
    { name: 'Basmati Rice Premium', brand: 'India Gate', description: 'Long grain aged basmati rice', catKey: 'Grocery', storeIdx: 2, variants: [{ attrs: { weight: '5kg' }, price: 45000, stock: 50 }, { attrs: { weight: '10kg' }, price: 85000, stock: 30 }] },
  ];

  const createdVariants: any[] = [];
  let productCount = 0, variantCount = 0;

  for (const pd of productsData) {
    const product = await Product.create({ name: pd.name, brand: pd.brand, description: pd.description, categoryId: catMap[pd.catKey], images: [], attributes: new Map(), isActive: true });
    productCount++;
    for (const v of pd.variants) {
      const variant = await ProductVariant.create({ productId: product._id, sku: `SKU-${product._id.toString().slice(-6)}-${Date.now().toString(36).toUpperCase()}`, attributes: new Map(Object.entries(v.attrs)), isActive: true });
      variantCount++;
      const inv = await Inventory.create({ storeId: stores[pd.storeIdx]._id, productVariantId: variant._id, price: v.price, totalStock: v.stock, availableStock: v.stock, reservedStock: 0, soldStock: 0, lowStockThreshold: 3 });
      createdVariants.push({ variant, storeId: stores[pd.storeIdx]._id, price: v.price, productName: pd.name, invId: inv._id });
    }
  }
  console.log(`✅ ${productCount} products, ${variantCount} variants`);

  // Reservations
  const now = new Date();
  const resData = [
    { custIdx: 0, varIdx: 0, qty: 1, status: 'PENDING', hoursAgo: 1 },
    { custIdx: 1, varIdx: 4, qty: 1, status: 'CONFIRMED', hoursAgo: 2 },
    { custIdx: 2, varIdx: 3, qty: 2, status: 'COMPLETED', hoursAgo: 24 },
    { custIdx: 3, varIdx: 1, qty: 1, status: 'EXPIRED', hoursAgo: 10 },
    { custIdx: 4, varIdx: 5, qty: 3, status: 'COMPLETED', hoursAgo: 48 },
  ];

  for (const rd of resData) {
    const cv = createdVariants[rd.varIdx];
    const customer = customers[rd.custIdx];
    const store = stores.find((s: any) => s._id.equals(cv.storeId))!;
    const createdAt = new Date(now.getTime() - rd.hoursAgo * 3600000);
    const expiresAt = new Date(createdAt.getTime() + 6 * 3600000);
    const attrStr = Array.from((cv.variant.attributes as Map<string, string>).entries()).map(([k, v]: [string, string]) => `${k}: ${v}`).join(', ');

    await Reservation.create({
      customerId: customer._id, storeId: cv.storeId, productId: cv.variant.productId,
      variantId: cv.variant._id, inventoryId: cv.invId,
      customerName: customer.name, customerMobile: customer.phone,
      productName: cv.productName, variantSku: cv.variant.sku, variantDescription: attrStr,
      storeName: store.name, storeAddress: store.address, storeLocation: store.location, storePhone: store.phone,
      price: cv.price, quantity: rd.qty,
      reservationCode: `RES-${Date.now().toString(36).toUpperCase()}-${Math.random().toString(36).slice(2, 6).toUpperCase()}`,
      durationHours: 6, expiresAt, status: rd.status, createdAt,
      ...(rd.status === 'COMPLETED' ? { completedAt: new Date(expiresAt.getTime() - 3600000) } : {}),
    });
  }
  console.log(`✅ ${resData.length} reservations`);

  console.log('\n🎉 Seed complete!');
  console.log('Demo records were inserted. Provision admin access separately with `npm run admin:bootstrap`.');

  await mongoose.disconnect();
  process.exit(0);
}

seed().catch((err) => { console.error('❌ Seed failed:', err.message); process.exit(1); });
