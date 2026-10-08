import mongoose from 'mongoose';
import { MongoMemoryServer } from 'mongodb-memory-server';
import request from 'supertest';
import { Retailer } from '../../src/models/Retailer';
import { Store } from '../../src/models/Store';
import { Product } from '../../src/models/Product';
import { ProductVariant } from '../../src/models/ProductVariant';
import { Inventory } from '../../src/models/Inventory';
import { signAccessToken } from '../../src/modules/auth/jwt';
import { ROLES } from '../../src/config/constants';
import app from '../../src/app';

let mongod: MongoMemoryServer;

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
    Retailer.deleteMany({}),
    Store.deleteMany({}),
    Product.deleteMany({}),
    ProductVariant.deleteMany({}),
    Inventory.deleteMany({}),
  ]);
});

describe('Admin store account controls', () => {
  it('blocks an existing retailer session and restores it when unblocked', async () => {
    const retailer = await Retailer.create({
      ownerName: 'Test Owner',
      businessName: 'Test Business',
      phone: '+919876543211',
      verificationStatus: 'VERIFIED',
    });
    const store = await Store.create({
      retailerId: retailer._id,
      name: 'Test Store',
      address: '123 Main St',
      location: { type: 'Point', coordinates: [75.8, 26.9] },
      openingHours: '9 AM - 9 PM',
      categories: ['Clothing'],
      isActive: true,
    });
    const retailerToken = signAccessToken(retailer._id.toString(), ROLES.RETAILER);
    const adminToken = signAccessToken('admin-test-id', ROLES.ADMIN);

    const beforeBlock = await request(app)
      .get('/api/v1/business/store')
      .set('Authorization', `Bearer ${retailerToken}`);
    expect(beforeBlock.status).toBe(200);

    const blockResponse = await request(app)
      .patch(`/api/v1/admin/stores/${store._id}/account`)
      .set('Authorization', `Bearer ${adminToken}`)
      .send({ isBlocked: true, blockReason: 'Policy review' });
    expect(blockResponse.status).toBe(200);
    expect(blockResponse.body.data.isBlocked).toBe(true);

    const blockedAccess = await request(app)
      .get('/api/v1/business/store')
      .set('Authorization', `Bearer ${retailerToken}`);
    expect(blockedAccess.status).toBe(401);

    const unblockResponse = await request(app)
      .patch(`/api/v1/admin/stores/${store._id}/account`)
      .set('Authorization', `Bearer ${adminToken}`)
      .send({ isBlocked: false });
    expect(unblockResponse.status).toBe(200);

    const restoredAccess = await request(app)
      .get('/api/v1/business/store')
      .set('Authorization', `Bearer ${retailerToken}`);
    expect(restoredAccess.status).toBe(200);
  });

  it('edits a store profile and returns unique products with their inventory and activity', async () => {
    const retailer = await Retailer.create({
      ownerName: 'Test Owner',
      businessName: 'Test Business',
      phone: '+919876543211',
      verificationStatus: 'VERIFIED',
    });
    const store = await Store.create({
      retailerId: retailer._id,
      name: 'Test Store',
      address: '123 Main St',
      location: { type: 'Point', coordinates: [75.8, 26.9] },
      openingHours: '9 AM - 9 PM',
      categories: ['Clothing'],
      isActive: true,
    });
    const product = await Product.create({ name: 'Test Jacket', categoryId: undefined });
    const variants = await Promise.all([
      ProductVariant.create({ productId: product._id, sku: 'JACKET-BLK-M', attributes: new Map([['size', 'M']]) }),
      ProductVariant.create({ productId: product._id, sku: 'JACKET-BLK-L', attributes: new Map([['size', 'L']]) }),
    ]);
    await Inventory.create(variants.map((variant) => ({
      storeId: store._id,
      productVariantId: variant._id,
      price: 249900,
      totalStock: 8,
      availableStock: 8,
      reservedStock: 0,
      soldStock: 0,
      lowStockThreshold: 3,
    })));
    const adminToken = signAccessToken('admin-test-id', ROLES.ADMIN);

    const updateResponse = await request(app)
      .put(`/api/v1/admin/stores/${store._id}/profile`)
      .set('Authorization', `Bearer ${adminToken}`)
      .send({ retailer: { ownerName: 'Updated Owner' }, store: { name: 'Updated Store' } });
    expect(updateResponse.status).toBe(200);
    expect(updateResponse.body.data.retailer.ownerName).toBe('Updated Owner');
    expect(updateResponse.body.data.name).toBe('Updated Store');
    expect(updateResponse.body.data.productCount).toBe(1);

    const productsResponse = await request(app)
      .get(`/api/v1/admin/stores/${store._id}/products`)
      .set('Authorization', `Bearer ${adminToken}`);
    expect(productsResponse.status).toBe(200);
    expect(productsResponse.body.data).toHaveLength(1);
    expect(productsResponse.body.data[0].variants).toHaveLength(2);
    expect(productsResponse.body.data[0].variants[0].price).toBe(2499);

    const activityResponse = await request(app)
      .get(`/api/v1/admin/stores/${store._id}/activity`)
      .set('Authorization', `Bearer ${adminToken}`);
    expect(activityResponse.status).toBe(200);
    expect(activityResponse.body.data.total).toBe(0);
  });
});