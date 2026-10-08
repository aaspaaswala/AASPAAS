import { FilterQuery } from 'mongoose';
import { Product, IProduct } from '../../models/Product';
import { ProductVariant } from '../../models/ProductVariant';
import { Inventory } from '../../models/Inventory';
import { Store } from '../../models/Store';
import { Category } from '../business/category.model';
import { NotFoundError } from '../../utils/errors';
import { LOW_STOCK_THRESHOLD } from '../../config/constants';
import { Types } from 'mongoose';

export interface NearbyProductResult {
  product: Partial<IProduct>;
  variant: object;
  store: object;
  price: number;
  availableStock: number;
  stockStatus: 'AVAILABLE' | 'LOW_STOCK' | 'OUT_OF_STOCK';
  distanceKm: number;
  inventoryId: string;
}

export interface NearbyProductQuery {
  latitude: number;
  longitude: number;
  radiusKm?: number;
  q?: string;
  category?: string;
  minPrice?: number;
  maxPrice?: number;
  brand?: string;
}

export interface ProductSearchQuery {
  q: string;
  category?: string;
  brand?: string;
  limit?: number;
  skip?: number;
  latitude?: number;
  longitude?: number;
  radiusKm?: number;
  minPrice?: number;
  maxPrice?: number;
}

function stockStatus(available: number, threshold: number): 'AVAILABLE' | 'LOW_STOCK' | 'OUT_OF_STOCK' {
  if (available === 0) return 'OUT_OF_STOCK';
  if (available <= threshold) return 'LOW_STOCK';
  return 'AVAILABLE';
}

function formatPrice(paise: number): number {
  return paise / 100;
}

async function buildProductDto(product: any, variants: any[], store: any, inventoryMap: Map<string, any>): Promise<any> {
  const variantDtos = variants.map((v: any) => {
    const inv = inventoryMap.get(v._id.toString());
    const st = inv ? stockStatus(inv.availableStock, inv.lowStockThreshold) : 'OUT_OF_STOCK';
    return {
      _id: v._id,
      size: (v.attributes as any)?.get('size') ?? null,
      color: (v.attributes as any)?.get('color') ?? null,
      price: inv ? formatPrice(inv.price) : 0,
      inventory: inv
        ? {
            _id: inv._id,
            totalStock: inv.totalStock,
            availableStock: inv.availableStock,
            reservedStock: inv.reservedStock,
            status: st,
          }
        : null,
    };
  });

  const category = product.categoryId
    ? await Category.findById(product.categoryId).select('name').lean()
    : null;

  return {
    _id: product._id,
    name: product.name,
    brand: product.brand,
    description: product.description,
    imageUrls: product.images,
    category: category?.name ?? '',
    variants: variantDtos,
    store: transformStore(store),
  };
}

function transformStore(store: any): any {
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
    rating: store.rating,
    reviewCount: 0,
    isOpen: true,
    distanceKm: store.distanceKm,
    verificationStatus: store.isActive ? 'VERIFIED' : 'REJECTED',
  };
}

export class MongoProductSearchService {
  async search(query: ProductSearchQuery): Promise<{ products: any[]; stores: any[]; totalCount: number }> {
    const productFilter: FilterQuery<IProduct> = { isActive: true };

    if (query.q) productFilter.$text = { $search: query.q };
    if (query.category) productFilter.categoryId = query.category as any;
    if (query.brand) productFilter.brand = { $regex: query.brand, $options: 'i' };

    const products = await Product.find(productFilter)
      .sort(query.q ? { score: { $meta: 'textScore' } } : { createdAt: -1 })
      .skip(query.skip ?? 0)
      .limit(query.limit ?? 20)
      .lean();

    let stores: any[] = [];
    if (query.latitude != null && query.longitude != null) {
      stores = await Store.find({
        isActive: true,
        location: {
          $nearSphere: {
            $geometry: { type: 'Point', coordinates: [query.longitude, query.latitude] },
            $maxDistance: (query.radiusKm ?? 10) * 1000,
          },
        },
      })
        .select('_id name address location phone openingHours images categories rating isActive')
        .lean();
    }

    const totalCount = products.length;

    return {
      products: await Promise.all(
        products.map(async (p) => {
          const variants = await ProductVariant.find({ productId: p._id, isActive: true }).lean();
          const variantIds = variants.map((v) => v._id);
          const inventories = await Inventory.find({ productVariantId: { $in: variantIds } }).lean();
          const inventoryMap = new Map<string, any>(inventories.map((i) => [i.productVariantId.toString(), i]));
          const firstInventory = inventories[0];
          const storeId = firstInventory?.storeId;
          const store = storeId ? await Store.findById(storeId).lean() : null;
          return buildProductDto(p, variants, store || {}, inventoryMap);
        })
      ),
      stores: stores.map(transformStore),
      totalCount,
    };
  }

  async searchNearby({
    latitude,
    longitude,
    radiusKm = 5,
    q,
    category,
    minPrice,
    maxPrice,
    brand,
  }: NearbyProductQuery): Promise<any[]> {
    const nearbyStores = await Store.find({
      isActive: true,
      location: {
        $nearSphere: {
          $geometry: { type: 'Point', coordinates: [longitude, latitude] },
          $maxDistance: radiusKm * 1000,
        },
      },
    })
      .select('_id name address location phone openingHours images categories rating isActive')
      .lean();

    if (nearbyStores.length === 0) return [];

    const storeIds = nearbyStores.map((s) => s._id);
    const storeMap = new Map<string, any>(nearbyStores.map((s) => [s._id.toString(), s]));

    const invFilter: FilterQuery<typeof Inventory> = {
      storeId: { $in: storeIds },
      availableStock: { $gt: 0 },
    };
    if (minPrice !== undefined) invFilter.price = { ...(invFilter.price as any), $gte: minPrice * 100 };
    if (maxPrice !== undefined) invFilter.price = { ...(invFilter.price as any), $lte: maxPrice * 100 };

    const inventories = await Inventory.find(invFilter)
      .populate('productVariantId')
      .lean();

    if (inventories.length === 0) return [];

    const variantIds = inventories.map((i) => (i.productVariantId as any)._id ?? i.productVariantId);
    const variants = await ProductVariant.find({ _id: { $in: variantIds }, isActive: true }).lean();
    const variantMap = new Map<string, any>(variants.map((v) => [v._id.toString(), v]));

    const productIds = [...new Set(variants.map((v) => v.productId.toString()))];
    const productFilter: FilterQuery<IProduct> = {
      _id: { $in: productIds },
      isActive: true,
    };
    if (q) productFilter.$text = { $search: q };
    if (brand) productFilter.brand = { $regex: brand, $options: 'i' };
    if (category) productFilter.categoryId = category as any;

    const products = await Product.find(productFilter).lean();
    const productMap = new Map<string, any>(products.map((p) => [p._id.toString(), p]));

    const inventoryMap = new Map<string, any>();
    for (const inv of inventories) {
      inventoryMap.set(inv.productVariantId.toString(), inv);
    }

    const results: any[] = [];

    for (const inv of inventories) {
      const variant = variantMap.get(inv.productVariantId.toString());
      if (!variant) continue;

      const product = productMap.get(variant.productId.toString());
      if (!product) continue;

      const store = storeMap.get(inv.storeId.toString());
      if (!store) continue;

      const variantDtos = [variant].map((v: any) => {
        const invData = inventoryMap.get(v._id.toString());
        const st = invData ? stockStatus(invData.availableStock, invData.lowStockThreshold) : 'OUT_OF_STOCK';
        return {
          _id: v._id,
          size: (v.attributes as any)?.get('size') ?? null,
          color: (v.attributes as any)?.get('color') ?? null,
          price: invData ? formatPrice(invData.price) : 0,
          inventory: invData
            ? {
                _id: invData._id,
                totalStock: invData.totalStock,
                availableStock: invData.availableStock,
                reservedStock: invData.reservedStock,
                status: st,
              }
            : null,
        };
      });

      const category = product.categoryId
        ? await Category.findById(product.categoryId).select('name').lean()
        : null;

      results.push({
        _id: product._id,
        name: product.name,
        brand: product.brand,
        description: product.description,
        imageUrls: product.images,
        category: category?.name ?? '',
        variants: variantDtos,
        store: transformStore(store),
      });
    }

    return results.sort((a, b) => (a.store?.distanceKm ?? 0) - (b.store?.distanceKm ?? 0));
  }

  async getProductById(productId: string): Promise<any> {
    const product = await Product.findById(productId).lean();
    if (!product) throw new NotFoundError('Product');

    const variants = await ProductVariant.find({ productId, isActive: true }).lean();
    const variantIds = variants.map((v) => v._id);
    const inventories = await Inventory.find({ productVariantId: { $in: variantIds }, availableStock: { $gt: 0 } }).lean();

    const inventoryMap = new Map<string, any>(inventories.map((i) => [i.productVariantId.toString(), i]));

    const storeIds = [...new Set(inventories.map((i) => i.storeId.toString()))];
    const stores = storeIds.length > 0 ? await Store.find({ _id: { $in: storeIds }, isActive: true }).lean() : [];
    const storeMap = new Map<string, any>(stores.map((s) => [s._id.toString(), s]));
    const firstStore = stores[0] ? storeMap.get(inventories[0].storeId.toString()) : null;

    const variantDtos = variants.map((v: any) => {
      const inv = inventoryMap.get(v._id.toString());
      const st = inv ? stockStatus(inv.availableStock, inv.lowStockThreshold) : 'OUT_OF_STOCK';
      return {
        _id: v._id,
        size: (v.attributes as any)?.get('size') ?? null,
        color: (v.attributes as any)?.get('color') ?? null,
        price: inv ? formatPrice(inv.price) : 0,
        inventory: inv
          ? {
              _id: inv._id,
              totalStock: inv.totalStock,
              availableStock: inv.availableStock,
              reservedStock: inv.reservedStock,
              status: st,
            }
          : null,
      };
    });

    const category = product.categoryId
      ? await Category.findById(product.categoryId).select('name').lean()
      : null;

    return {
      _id: product._id,
      name: product.name,
      brand: product.brand,
      description: product.description,
      imageUrls: product.images,
      category: category?.name ?? '',
      variants: variantDtos,
      store: transformStore(firstStore || {}),
    };
  }

  async getProductPriceComparison(
    productId: string,
    latitude?: number,
    longitude?: number,
    radiusKm = 10
  ): Promise<Array<{ store: object; variantId: string; price: number; availableStock: number; distanceKm?: number }>> {
    const product = await Product.findById(productId).lean();
    if (!product) throw new NotFoundError('Product');

    const variants = await ProductVariant.find({ productId, isActive: true }).lean();
    const variantIds = variants.map((v) => v._id);

    let nearbyStores: any[] = [];
    if (latitude != null && longitude != null) {
      nearbyStores = await Store.find({
        isActive: true,
        location: {
          $nearSphere: {
            $geometry: { type: 'Point', coordinates: [longitude, latitude] },
            $maxDistance: radiusKm * 1000,
          },
        },
      })
        .select('_id name address location phone openingHours images categories rating isActive')
        .lean();
    }

    const inventories = await Inventory.find({
      productVariantId: { $in: variantIds },
      availableStock: { $gt: 0 },
      ...(nearbyStores.length > 0 ? { storeId: { $in: nearbyStores.map((s) => s._id) } } : {}),
    }).lean();

    const storeMap = new Map<string, any>(nearbyStores.map((s) => [s._id.toString(), s]));

    return inventories.map((inv: any) => ({
      store: transformStore(storeMap.get(inv.storeId.toString()) || {}),
      variantId: inv.productVariantId.toString(),
      price: formatPrice(inv.price),
      availableStock: inv.availableStock,
      distanceKm: storeMap.get(inv.storeId.toString())?.distanceKm,
    }));
  }
}

export const productSearchService = new MongoProductSearchService();
