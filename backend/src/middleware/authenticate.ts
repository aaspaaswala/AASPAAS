import { Response, NextFunction } from 'express';
import { verifyAccessToken } from '../modules/auth/jwt';
import { UnauthorizedError, ForbiddenError } from '../utils/errors';
import { AuthRequest, Role } from '../types';
import { ROLES } from '../config/constants';
import { Retailer } from '../models/Retailer';

export async function authenticate(req: AuthRequest, _res: Response, next: NextFunction): Promise<void> {
  const header = req.headers.authorization;
  if (!header?.startsWith('Bearer ')) return next(new UnauthorizedError('No token provided'));
  try {
    req.auth = verifyAccessToken(header.slice(7));
  } catch {
    return next(new UnauthorizedError('Token expired or invalid'));
  }

  if (req.auth.role === ROLES.RETAILER) {
    try {
      const retailer = await Retailer.findById(req.auth.id).select('isBlocked');
      if (!retailer || retailer.isBlocked) {
        return next(new UnauthorizedError('Business account is blocked or unavailable'));
      }
    } catch (error) {
      return next(error);
    }
  }

  next();
}

export function requireRole(...roles: Role[]) {
  return (req: AuthRequest, _res: Response, next: NextFunction): void => {
    if (!req.auth || !roles.includes(req.auth.role)) {
      return next(new ForbiddenError('Insufficient permissions'));
    }
    next();
  };
}
