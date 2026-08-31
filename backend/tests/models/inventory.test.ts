import mongoose from 'mongoose';
import { MongoMemoryServer } from 'mongodb-memory-server';
import { Product } from '../../src/models/Product';
import { ProductVariant } from '../../src/models/ProductVariant';
import { Inventory } from '../../src/models/Inventory';

let mongod: MongoMemoryServer;

beforeAll(async () => {
  mongod = await MongoMemoryServer.create();
  await mongoose.connect(mongod.getUri());
}, 60000);

afterAll(async () => {
  await mongoose.disconnect();
  await mongod.stop();
});

afterEach(async () => {
  await Promise.all([
    Product.deleteMany({}),
    ProductVariant.deleteMany({}),
    Inventory.deleteMany({}),
  ]);
});

// ── Helpers ───────────────────────────────────────────────────────────────────

const makeProduct = (overrides = {}) =>
  new Product({ name: 'Black Sweater', brand: 'XYZ', ...overrides });

const makeVariant = (productId: mongoose.Types.ObjectId, overrides = {}) =>
  new ProductVariant({
    productId,
    sku: 'SWT-BLK-M',
    attributes: new Map([['size', 'M'], ['color', 'Black']]),
    ...overrides,
  });

const makeInventory = (
  storeId: mongoose.Types.ObjectId,
  variantId: mongoose.Types.ObjectId,
  overrides = {}
) =>
  new Inventory({
    storeId,
    productVariantId: variantId,
    price: 149900,
    totalStock: 5,
    availableStock: 5,
    reservedStock: 0,
    ...overrides,
  });

// ── Product ───────────────────────────────────────────────────────────────────

describe('Product model', () => {
  it('creates a valid product', async () => {
    const p = await makeProduct().save();
    expect(p._id).toBeDefined();
    expect(p.name).toBe('Black Sweater');
    expect(p.isActive).toBe(true);
  });

  it('rejects a product without name', async () => {
    await expect(new Product({ brand: 'XYZ' }).save()).rejects.toThrow();
  });

  it('stores flexible attributes', async () => {
    const p = await new Product({
      name: 'iPhone 15',
      attributes: new Map([['storage', '128GB'], ['color', 'Black']]),
    }).save();
    expect(p.attributes.get('storage')).toBe('128GB');
  });

  it('defaults isActive to true', async () => {
    const p = await makeProduct().save();
    expect(p.isActive).toBe(true);
  });
});

// ── ProductVariant ────────────────────────────────────────────────────────────

describe('ProductVariant model', () => {
  it('creates a valid variant', async () => {
    const product = await makeProduct().save();
    const v = await makeVariant(product._id as mongoose.Types.ObjectId).save();
    expect(v._id).toBeDefined();
    expect(v.productId.toString()).toBe(product._id.toString());
    expect(v.sku).toBe('SWT-BLK-M');
  });

  it('uppercases SKU on save', async () => {
    const product = await makeProduct().save();
    const v = await makeVariant(product._id as mongoose.Types.ObjectId, { sku: 'swt-blk-m' }).save();
    expect(v.sku).toBe('SWT-BLK-M');
  });

  it('rejects a variant without productId', async () => {
    await expect(new ProductVariant({ sku: 'SWT-BLK-M' }).save()).rejects.toThrow();
  });

  it('rejects a variant without SKU', async () => {
    const product = await makeProduct().save();
    await expect(
      new ProductVariant({ productId: product._id }).save()
    ).rejects.toThrow();
  });

  it('enforces unique SKU within the same product', async () => {
    const product = await makeProduct().save();
    const pid = product._id as mongoose.Types.ObjectId;
    await makeVariant(pid).save();
    await expect(makeVariant(pid).save()).rejects.toThrow();
  });

  it('allows same SKU across different products', async () => {
    const p1 = await makeProduct().save();
    const p2 = await makeProduct({ name: 'Blue Sweater' }).save();
    await makeVariant(p1._id as mongoose.Types.ObjectId).save();
    await expect(
      makeVariant(p2._id as mongoose.Types.ObjectId).save()
    ).resolves.toBeDefined();
  });

  it('stores flexible variant attributes', async () => {
    const product = await makeProduct().save();
    const v = await new ProductVariant({
      productId: product._id,
      sku: 'IPHONE-128-BLK',
      attributes: new Map([['storage', '128GB'], ['color', 'Black'], ['ram', '6GB']]),
    }).save();
    expect(v.attributes.get('ram')).toBe('6GB');
  });
});

// ── Inventory ─────────────────────────────────────────────────────────────────

describe('Inventory model', () => {
  const storeId = new mongoose.Types.ObjectId();

  it('creates valid inventory', async () => {
    const product = await makeProduct().save();
    const variant = await makeVariant(product._id as mongoose.Types.ObjectId).save();
    const inv = await makeInventory(storeId, variant._id as mongoose.Types.ObjectId).save();
    expect(inv._id).toBeDefined();
    expect(inv.price).toBe(149900);
    expect(inv.totalStock).toBe(5);
    expect(inv.availableStock).toBe(5);
    expect(inv.reservedStock).toBe(0);
    expect(inv.soldStock).toBe(0);
  });

  it('rejects missing storeId', async () => {
    const product = await makeProduct().save();
    const variant = await makeVariant(product._id as mongoose.Types.ObjectId).save();
    await expect(
      new Inventory({ productVariantId: variant._id, price: 149900, totalStock: 5, availableStock: 5 }).save()
    ).rejects.toThrow();
  });

  it('rejects missing productVariantId', async () => {
    await expect(
      new Inventory({ storeId, price: 149900, totalStock: 5, availableStock: 5 }).save()
    ).rejects.toThrow();
  });

  it('rejects negative price', async () => {
    const product = await makeProduct().save();
    const variant = await makeVariant(product._id as mongoose.Types.ObjectId).save();
    await expect(
      makeInventory(storeId, variant._id as mongoose.Types.ObjectId, { price: -100 }).save()
    ).rejects.toThrow();
  });

  it('rejects negative stock values', async () => {
    const product = await makeProduct().save();
    const variant = await makeVariant(product._id as mongoose.Types.ObjectId).save();
    await expect(
      makeInventory(storeId, variant._id as mongoose.Types.ObjectId, {
        totalStock: 5,
        availableStock: -1,
        reservedStock: 0,
      }).save()
    ).rejects.toThrow();
  });

  it('rejects invariant violation: availableStock + reservedStock !== totalStock', async () => {
    const product = await makeProduct().save();
    const variant = await makeVariant(product._id as mongoose.Types.ObjectId).save();
    await expect(
      makeInventory(storeId, variant._id as mongoose.Types.ObjectId, {
        totalStock: 5,
        availableStock: 3,
        reservedStock: 1, // 3 + 1 = 4 ≠ 5
      }).save()
    ).rejects.toThrow(/invariant/);
  });

  it('enforces unique storeId + productVariantId', async () => {
    const product = await makeProduct().save();
    const variant = await makeVariant(product._id as mongoose.Types.ObjectId).save();
    const vid = variant._id as mongoose.Types.ObjectId;
    await makeInventory(storeId, vid).save();
    await expect(makeInventory(storeId, vid).save()).rejects.toThrow();
  });

  it('allows same variant in different stores', async () => {
    const product = await makeProduct().save();
    const variant = await makeVariant(product._id as mongoose.Types.ObjectId).save();
    const vid = variant._id as mongoose.Types.ObjectId;
    const storeB = new mongoose.Types.ObjectId();
    await makeInventory(storeId, vid).save();
    await expect(makeInventory(storeB, vid).save()).resolves.toBeDefined();
  });

  it('defaults lowStockThreshold to 3', async () => {
    const product = await makeProduct().save();
    const variant = await makeVariant(product._id as mongoose.Types.ObjectId).save();
    const inv = await makeInventory(storeId, variant._id as mongoose.Types.ObjectId).save();
    expect(inv.lowStockThreshold).toBe(3);
  });
});
