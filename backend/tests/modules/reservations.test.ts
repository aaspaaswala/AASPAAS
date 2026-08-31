import mongoose from 'mongoose';
import { MongoMemoryServer } from 'mongodb-memory-server';
import request from 'supertest';
import { User } from '../../src/models/User';
import { Retailer } from '../../src/models/Retailer';
import { Store } from '../../src/models/Store';
import { Product } from '../../src/models/Product';
import { ProductVariant } from '../../src/models/ProductVariant';
import { Inventory } from '../../src/models/Inventory';
import { Reservation } from '../../src/modules/reservations/reservation.model';
import { Category } from '../../src/modules/business/category.model';
import { signAccessToken } from '../../src/modules/auth/jwt';
import { ROLES, RESERVATION_STATUS } from '../../src/config/constants';
import app from '../../src/app';

let mongod: MongoMemoryServer;
let accessToken: string;
let retailerToken: string;
let storeId: string;
let productId: string;
let variantId: string;
let inventoryId: string;

beforeAll(async () => {
  mongod = await MongoMemoryServer.create({ instance: { launchTimeout: 120000 } });
  await mongoose.connect(mongod.getUri());
}, 120000);

afterAll(async () => {
  await mongoose.disconnect();
  await mongod.stop();
});

afterEach(async () => {
  await Promise.all([
    User.deleteMany({}),
    Retailer.deleteMany({}),
    Store.deleteMany({}),
    Product.deleteMany({}),
    ProductVariant.deleteMany({}),
    Inventory.deleteMany({}),
    Reservation.deleteMany({}),
    Category.deleteMany({}),
  ]);
});

async function createCustomer() {
  const user = await User.create({ phone: '+919876543210', name: 'Test Customer' });
  accessToken = signAccessToken(user._id.toString(), ROLES.CUSTOMER);
  return user;
}

async function createRetailer() {
  const retailer = await Retailer.create({
    ownerName: 'Test Owner',
    businessName: 'Test Business',
    phone: '+919876543211',
    verificationStatus: 'VERIFIED',
  });
  retailerToken = signAccessToken(retailer._id.toString(), ROLES.RETAILER);
  const store = await Store.create({
    retailerId: retailer._id,
    name: 'Test Store',
    address: '123 Main St',
    location: { type: 'Point', coordinates: [75.8, 26.9] },
    openingHours: '9 AM - 9 PM',
    categories: ['Clothing'],
    isActive: true,
  });
  storeId = store._id.toString();
  return { retailer, store };
}

async function createProduct(storeId: string) {
  const product = await Product.create({ name: 'Black Sweater', brand: 'XYZ' });
  productId = product._id.toString();
  const variant = await ProductVariant.create({
    productId: product._id,
    sku: 'SWT-BLK-M',
    attributes: new Map([['size', 'M'], ['color', 'Black']]),
  });
  variantId = variant._id.toString();
  const inventory = await Inventory.create({
    storeId: new mongoose.Types.ObjectId(storeId),
    productVariantId: variant._id,
    price: 149900,
    totalStock: 10,
    availableStock: 10,
    reservedStock: 0,
    lowStockThreshold: 3,
  });
  inventoryId = inventory._id.toString();
  return { product, variant, inventory };
}

describe('Reservation APIs', () => {
  it('creates a reservation and decrements stock', async () => {
    await createCustomer();
    await createRetailer();
    await createProduct(storeId);

    const res = await request(app)
      .post('/api/v1/reservations')
      .set('Authorization', `Bearer ${accessToken}`)
      .send({ variantId, quantity: 2 });

    expect(res.status).toBe(201);
    expect(res.body.success).toBe(true);
    expect(res.body.data.status).toBe('PENDING');
    expect(res.body.data.quantity).toBe(2);

    const inv = await Inventory.findById(inventoryId);
    expect(inv?.availableStock).toBe(8);
    expect(inv?.reservedStock).toBe(2);
  });

  it('returns 409 for insufficient stock', async () => {
    await createCustomer();
    await createRetailer();
    await createProduct(storeId);

    const inventory = await Inventory.findById(inventoryId);
    inventory!.availableStock = 0;
    inventory!.totalStock = 0;
    await inventory!.save();

    const res = await request(app)
      .post('/api/v1/reservations')
      .set('Authorization', `Bearer ${accessToken}`)
      .send({ variantId, quantity: 1 });

    expect(res.status).toBe(409);
    expect(res.body.error.code).toBe('INSUFFICIENT_STOCK');
  });

  it('lists my reservations', async () => {
    await createCustomer();
    await createRetailer();
    await createProduct(storeId);

    await request(app)
      .post('/api/v1/reservations')
      .set('Authorization', `Bearer ${accessToken}`)
      .send({ variantId });

    const res = await request(app)
      .get('/api/v1/reservations')
      .set('Authorization', `Bearer ${accessToken}`);

    expect(res.status).toBe(200);
    expect(res.body.data.reservations.length).toBe(1);
  });

  it('cancels a reservation and restores stock', async () => {
    await createCustomer();
    await createRetailer();
    await createProduct(storeId);

    const createRes = await request(app)
      .post('/api/v1/reservations')
      .set('Authorization', `Bearer ${accessToken}`)
      .send({ variantId });

    const reservationId = createRes.body.data._id;

    const cancelRes = await request(app)
      .post(`/api/v1/reservations/${reservationId}/cancel`)
      .set('Authorization', `Bearer ${accessToken}`);

    expect(cancelRes.status).toBe(200);

    const inv = await Inventory.findById(inventoryId);
    expect(inv?.availableStock).toBe(10);
    expect(inv?.reservedStock).toBe(0);

    const reservation = await Reservation.findById(reservationId);
    expect(reservation?.status).toBe(RESERVATION_STATUS.CANCELLED);
  });

  it('prevents cancelling another users reservation', async () => {
    await createCustomer();
    await createRetailer();
    await createProduct(storeId);

    const createRes = await request(app)
      .post('/api/v1/reservations')
      .set('Authorization', `Bearer ${accessToken}`)
      .send({ variantId });

    const reservationId = createRes.body.data._id;

    const otherUser = await User.create({ phone: '+919876543212', name: 'Other' });
    const otherToken = signAccessToken(otherUser._id.toString(), ROLES.CUSTOMER);

    const res = await request(app)
      .post(`/api/v1/reservations/${reservationId}/cancel`)
      .set('Authorization', `Bearer ${otherToken}`);

    expect(res.status).toBe(403);
  });
});

describe('Business APIs', () => {
  it('returns dashboard stats', async () => {
    const { store } = await createRetailer();
    await createProduct(store._id.toString());

    const res = await request(app)
      .get('/api/v1/business/dashboard')
      .set('Authorization', `Bearer ${retailerToken}`);

    expect(res.status).toBe(200);
    expect(res.body.data.totalProducts).toBe(1);
  });

  it('returns my products', async () => {
    const { store } = await createRetailer();
    await createProduct(store._id.toString());

    const res = await request(app)
      .get('/api/v1/business/products')
      .set('Authorization', `Bearer ${retailerToken}`);

    expect(res.status).toBe(200);
    expect(res.body.data.length).toBe(1);
    expect(res.body.data[0].variants.length).toBe(1);
  });

  it('creates a product with variants and inventory', async () => {
    const { store } = await createRetailer();

    const res = await request(app)
      .post('/api/v1/business/products')
      .set('Authorization', `Bearer ${retailerToken}`)
      .send({
        name: 'New Product',
        brand: 'BrandX',
        category: 'Electronics',
        variants: [{ size: 'L', color: 'Red', price: 999.0, stock: 5 }],
      });

    expect(res.status).toBe(201);
    expect(res.body.data.name).toBe('New Product');
    expect(res.body.data.variants.length).toBe(1);
    expect(res.body.data.variants[0].inventory.totalStock).toBe(5);
  });

  it('updates inventory stock respecting invariant', async () => {
    const { store } = await createRetailer();
    await createProduct(store._id.toString());

    const res = await request(app)
      .put(`/api/v1/business/inventory/${inventoryId}`)
      .set('Authorization', `Bearer ${retailerToken}`)
      .send({ stock: 20 });

    expect(res.status).toBe(200);
    expect(res.body.data.totalStock).toBe(20);
    expect(res.body.data.availableStock).toBe(20);
  });

  it('rejects inventory update below reserved stock', async () => {
    const { store } = await createRetailer();
    await createProduct(store._id.toString());

    const inventory = await Inventory.findById(inventoryId);
    inventory!.reservedStock = 5;
    inventory!.totalStock = 15;
    inventory!.availableStock = 10;
    await inventory!.save();

    const res = await request(app)
      .put(`/api/v1/business/inventory/${inventoryId}`)
      .set('Authorization', `Bearer ${retailerToken}`)
      .send({ stock: 3 });

    expect(res.status).toBe(409);
  });

  it('returns 403 for customer accessing business APIs', async () => {
    await createCustomer();

    const res = await request(app)
      .get('/api/v1/business/dashboard')
      .set('Authorization', `Bearer ${accessToken}`);

    expect(res.status).toBe(403);
  });
});

describe('Category APIs', () => {
  it('lists categories', async () => {
    await Category.create({ name: 'Clothing', isActive: true });

    const res = await request(app).get('/api/v1/categories');
    expect(res.status).toBe(200);
    expect(res.body.data.categories.length).toBe(1);
  });

  it('returns 403 for non-admin accessing admin category routes', async () => {
    await createCustomer();
    const res = await request(app)
      .post('/api/v1/admin/categories')
      .set('Authorization', `Bearer ${accessToken}`)
      .send({ name: 'Electronics' });

    expect(res.status).toBe(403);
  });
});
