import { Request, Response, NextFunction } from 'express';
import * as businessService from './business.service';
import { sendSuccess } from '../../utils/response';
import { ValidationError } from '../../utils/errors';
import { AuthRequest } from '../../types';

export async function getDashboard(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const stats = await businessService.getDashboardStats(req.auth!.id);
    sendSuccess(res, stats);
  } catch (err) {
    next(err);
  }
}

export async function getStore(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const store = await businessService.getMyStore(req.auth!.id);
    sendSuccess(res, store);
  } catch (err) {
    next(err);
  }
}

export async function updateStore(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const store = await businessService.updateMyStore(req.auth!.id, req.body);
    sendSuccess(res, store);
  } catch (err) {
    if ((err as any).name === 'ZodError') return next(new ValidationError((err as Error).message));
    next(err);
  }
}

export async function getProducts(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const products = await businessService.getMyProducts(req.auth!.id);
    sendSuccess(res, products);
  } catch (err) {
    next(err);
  }
}

export async function createProduct(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const product = await businessService.createProduct(req.auth!.id, req.body);
    sendSuccess(res, product, 'Product created', 201);
  } catch (err) {
    if ((err as any).name === 'ZodError') return next(new ValidationError((err as Error).message));
    next(err);
  }
}

export async function updateProduct(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const product = await businessService.updateProduct(req.auth!.id, req.params.id, req.body);
    sendSuccess(res, product);
  } catch (err) {
    if ((err as any).name === 'ZodError') return next(new ValidationError((err as Error).message));
    next(err);
  }
}

export async function deleteProduct(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    await businessService.deleteProduct(req.auth!.id, req.params.id);
    sendSuccess(res, null, 'Product deleted');
  } catch (err) {
    next(err);
  }
}

export async function updateInventory(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const inventory = await businessService.updateInventory(req.auth!.id, req.params.id, req.body);
    sendSuccess(res, inventory);
  } catch (err) {
    if ((err as any).name === 'ZodError') return next(new ValidationError((err as Error).message));
    next(err);
  }
}

export async function getReservations(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const reservations = await businessService.getReservations(req.auth!.id);
    sendSuccess(res, reservations);
  } catch (err) {
    next(err);
  }
}

export async function confirmReservation(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const reservation = await businessService.confirmReservation(req.auth!.id, req.params.id);
    sendSuccess(res, null, 'Reservation confirmed');
  } catch (err) {
    next(err);
  }
}

export async function completeReservation(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const reservation = await businessService.completeReservation(req.auth!.id, req.params.id);
    sendSuccess(res, null, 'Reservation completed');
  } catch (err) {
    next(err);
  }
}

export async function cancelReservation(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const reservation = await businessService.cancelReservation(req.auth!.id, req.params.id);
    sendSuccess(res, null, 'Reservation cancelled');
  } catch (err) {
    next(err);
  }
}
