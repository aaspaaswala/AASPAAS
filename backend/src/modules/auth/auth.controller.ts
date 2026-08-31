import { Request, Response, NextFunction } from 'express';
import * as authService from './auth.service';
import { customerRegisterSchema, customerLoginSchema, refreshSchema } from './auth.schema';
import { sendSuccess } from '../../utils/response';
import { ValidationError } from '../../utils/errors';
import { AuthRequest } from '../../types';

// POST /auth/register  — customer register or login (creates account if new)
export async function customerAuth(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { phone, name } = customerRegisterSchema.parse(req.body);
    const result = await authService.registerOrLoginCustomer(phone, name);
    sendSuccess(res, result, result.isNew ? 'Account created successfully' : 'Logged in successfully');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
}

// POST /auth/refresh
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

// POST /auth/logout
export async function logout(_req: Request, res: Response): Promise<void> {
  sendSuccess(res, null, 'Logged out successfully');
}

// GET /auth/me
export async function me(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const { id, role } = req.auth!;
    const data = await authService.getMe(id, role);
    sendSuccess(res, data);
  } catch (err) {
    next(err);
  }
}
