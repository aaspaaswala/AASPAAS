import { Store, IStore } from '../../models/Store';
import { Inventory } from '../../models/Inventory';
import { ProductVariant } from '../../models/ProductVariant';
import { Product } from '../../models/Product';
import { NotFoundError } from '../../utils/errors';
import { Types } from 'mongoose';

export interface NearbyStoreQuery {
  latitude: number;
  longitude: number;
  radiusKm?: number;
  category?: string;
}

export interface StoreProductItem {
  product: object;
  variant: object;
  price: number;
  priceFormatted: string;
  stockStatus: 'AVAILABLE' | 'LOW_STOCK' | 'OUT_OF_STOCK';
  inventoryId: string;
}

export async function findNearbyStores(query: NearbyStoreQuery): Promise<object[]> {
  const { latitude, longitude, radiusKm = 5, category } = query;

  const filter: Record<string, unknown> = {
    isActive: true,
    location: {
      $nearSphere: {
        $geometry: { type: 'Point', coordinates: [longitude, latitude] },
        $maxDistance: radiusKm * 1000,
      },
    },
  };

  if (category) filter.categories = category;

  const stores = await Store.find(filter)
    .select('-__v')
    .lean();

  return stores.map(transformStore);
}

export async function getStoreById(storeId: string): Promise<object> {
  const store = await Store.findById(storeId).lean();
  if (!store) throw new NotFoundError('Store');
  return transformStore(store);
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
    rating: store.rating ?? null,
    reviewCount: 0,
    isOpen: true,
    distanceKm: store.distanceKm ?? null,
    verificationStatus: store.isActive ? 'VERIFIED' : 'REJECTED',
    createdAt: store.createdAt,
    updatedAt: store.updatedAt,
  };
}

export async function getStoreProducts(storeId: string): Promise<object[]> {
  const inventories = await Inventory.find({ storeId: new Types.ObjectId(storeId) }).lean();
  if (inventories.length === 0) return [];

  const variantIds = inventories.map((i) => i.productVariantId);
  const variants = await ProductVariant.find({ _id: { $in: variantIds }, isActive: true }).lean();
  const variantMap = new Map(variants.map((v) => [v._id.toString(), v]));

  const productIds = [...new Set(variants.map((v) => v.productId.toString()))];
  const products = await Product.find({ _id: { $in: productIds }, isActive: true }).lean();
  const productMap = new Map(products.map((p) => [p._id.toString(), p]));

  const inventoryMap = new Map<string, any>();
  for (const inv of inventories) {
    inventoryMap.set(inv.productVariantId.toString(), inv);
  }

  const results: any[] = [];

  for (const product of products) {
    const productVariants = variants.filter((v) => v.productId.toString() === product._id.toString());
    const variantDtos = productVariants.map((v) => {
      const inv = inventoryMap.get(v._id.toString());
      let stockStatus: string;
      if (!inv) stockStatus = 'OUT_OF_STOCK';
      else if (inv.availableStock === 0) stockStatus = 'OUT_OF_STOCK';
      else if (inv.availableStock <= inv.lowStockThreshold) stockStatus = 'LOW_STOCK';
      else stockStatus = 'AVAILABLE';

      return {
        _id: v._id,
        size: (v.attributes as any)?.get('size') ?? null,
        color: (v.attributes as any)?.get('color') ?? null,
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

    results.push({
      _id: product._id,
      name: product.name,
      brand: product.brand,
      description: product.description,
      imageUrls: product.images,
      category: product.categoryId || '',
      variants: variantDtos,
    });
  }

  return results;
}
