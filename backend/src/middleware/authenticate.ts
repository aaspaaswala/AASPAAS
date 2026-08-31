import { Response, NextFunction } from 'express';
import { verifyAccessToken } from '../modules/auth/jwt';
import { UnauthorizedError, ForbiddenError } from '../utils/errors';
import { AuthRequest, Role } from '../types';

export function authenticate(req: AuthRequest, _res: Response, next: NextFunction): void {
  const header = req.headers.authorization;
  if (!header?.startsWith('Bearer ')) return next(new UnauthorizedError('No token provided'));
  try {
    req.auth = verifyAccessToken(header.slice(7));
    next();
  } catch {
    next(new UnauthorizedError('Token expired or invalid'));
  }
}

export function requireRole(...roles: Role[]) {
  return (req: AuthRequest, _res: Response, next: NextFunction): void => {
    if (!req.auth || !roles.includes(req.auth.role)) {
      return next(new ForbiddenError('Insufficient permissions'));
    }
    next();
  };
}
