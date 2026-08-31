import { Request, Response, NextFunction } from 'express';
import * as reservationService from './reservation.service';
import { createReservationSchema } from './reservation.schema';
import { sendSuccess } from '../../utils/response';
import { ValidationError } from '../../utils/errors';
import { AuthRequest } from '../../types';

export async function createReservation(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const { variantId, quantity } = createReservationSchema.parse(req.body);
    const reservation = await reservationService.createReservation(req.auth!, { variantId, quantity });
    sendSuccess(res, reservation, 'Reservation created', 201);
  } catch (err) {
    if ((err as any).name === 'ZodError') return next(new ValidationError((err as Error).message));
    next(err);
  }
}

export async function listReservations(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const reservations = await reservationService.getMyReservations(req.auth!);
    sendSuccess(res, { reservations, count: (reservations as any[]).length });
  } catch (err) {
    next(err);
  }
}

export async function getReservation(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const reservation = await reservationService.getReservationById(req.auth!, req.params.id);
    sendSuccess(res, { reservation });
  } catch (err) {
    next(err);
  }
}

export async function cancelReservation(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    await reservationService.cancelMyReservation(req.auth!, req.params.id);
    sendSuccess(res, null, 'Reservation cancelled');
  } catch (err) {
    next(err);
  }
}
