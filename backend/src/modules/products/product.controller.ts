import { Request, Response, NextFunction } from 'express';
import { productSearchService } from './product.search.service';
import { searchQuerySchema, nearbyQuerySchema } from './product.schema';
import { sendSuccess } from '../../utils/response';
import { ValidationError } from '../../utils/errors';
import { AuthRequest } from '../../types';

export async function search(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const query = searchQuerySchema.parse(req.query);
    const result = await productSearchService.search({
      q: query.q,
      category: query.category,
      brand: query.brand,
      limit: query.limit,
      skip: query.skip,
      latitude: query.latitude,
      longitude: query.longitude,
      radiusKm: query.radiusKm,
      minPrice: query.minPrice,
      maxPrice: query.maxPrice,
    });
    sendSuccess(res, result);
  } catch (err) {
    if ((err as any).name === 'ZodError') return next(new ValidationError((err as Error).message));
    next(err);
  }
}

export async function nearby(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const query = nearbyQuerySchema.parse(req.query);
    const results = await productSearchService.searchNearby({
      latitude: query.latitude,
      longitude: query.longitude,
      radiusKm: query.radius,
      q: query.q,
      category: query.category,
      brand: query.brand,
      minPrice: query.minPrice,
      maxPrice: query.maxPrice,
    });
    sendSuccess(res, results);
  } catch (err) {
    if ((err as any).name === 'ZodError') return next(new ValidationError((err as Error).message));
    next(err);
  }
}

export async function getById(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const { latitude, longitude, radius } = req.query as { latitude?: string; longitude?: string; radius?: string };
    const product = await productSearchService.getProductById(req.params.id);
    sendSuccess(res, product);
  } catch (err) {
    next(err);
  }
}

export async function priceComparison(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const { latitude, longitude, radius } = req.query as { latitude?: string; longitude?: string; radius?: string };
    const result = await productSearchService.getProductPriceComparison(
      req.params.id,
      latitude ? parseFloat(latitude) : undefined,
      longitude ? parseFloat(longitude) : undefined,
      radius ? parseFloat(radius) : 10
    );
    sendSuccess(res, result);
  } catch (err) {
    next(err);
  }
}
