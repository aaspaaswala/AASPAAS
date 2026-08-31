import { Request, Response, NextFunction } from 'express';
import * as authService from './auth.service';
import { sendOtpSchema, verifyOtpSchema, refreshSchema } from './auth.schema';
import { sendSuccess } from '../../utils/response';
import { ValidationError } from '../../utils/errors';
import { AuthRequest } from '../../types';
import { ROLES } from '../../config/constants';

export async function sendOtp(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { phone, role } = sendOtpSchema.parse(req.body);
    if (role === ROLES.CUSTOMER) {
      await authService.sendCustomerOtp(phone);
    } else {
      await authService.sendRetailerOtp(phone);
    }
    sendSuccess(res, null, 'OTP sent successfully');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
}

export async function verifyOtp(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { phone, otp, role } = verifyOtpSchema.parse(req.body);
    let result;
    if (role === ROLES.CUSTOMER) {
      result = await authService.verifyCustomerOtp(phone, otp);
    } else {
      result = await authService.verifyRetailerOtp(phone, otp);
    }
    sendSuccess(res, result, 'Authenticated successfully');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
}

export async function refresh(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { refreshToken } = refreshSchema.parse(req.body);
    const tokens = await authService.refreshTokens(refreshToken);
    sendSuccess(res, tokens, 'Tokens refreshed');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
}

export async function logout(_req: Request, res: Response): Promise<void> {
  // Stateless JWT — client discards tokens. Future: add token blocklist here.
  sendSuccess(res, null, 'Logged out successfully');
}

export async function me(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const { id, role } = req.auth!;
    const data = await authService.getMe(id, role);
    sendSuccess(res, data);
  } catch (err) {
    next(err);
  }
}
