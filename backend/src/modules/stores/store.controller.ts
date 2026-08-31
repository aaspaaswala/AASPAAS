import { Request, Response, NextFunction } from 'express';
import * as storeService from './store.service';
import { nearbyStoreSchema } from './store.schema';
import { sendSuccess } from '../../utils/response';
import { ValidationError } from '../../utils/errors';

export async function nearby(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const query = nearbyStoreSchema.parse(req.query);
    const stores = await storeService.findNearbyStores({
      latitude: query.latitude,
      longitude: query.longitude,
      radiusKm: query.radius,
      category: query.category,
    });
    sendSuccess(res, stores);
  } catch (err) {
    if ((err as any).name === 'ZodError') return next(new ValidationError((err as Error).message));
    next(err);
  }
}

export async function getById(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const store = await storeService.getStoreById(req.params.id);
    sendSuccess(res, store);
  } catch (err) {
    next(err);
  }
}

export async function getProducts(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const products = await storeService.getStoreProducts(req.params.id);
    sendSuccess(res, products);
  } catch (err) {
    next(err);
  }
}
