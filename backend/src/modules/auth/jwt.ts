import jwt from 'jsonwebtoken';
import { env } from '../../config/env';
import { AuthPayload, Role } from '../../types';

export function signAccessToken(id: string, role: Role): string {
  return jwt.sign({ id, role }, env.jwt.accessSecret, {
    expiresIn: env.jwt.accessExpiresIn,
  } as jwt.SignOptions);
}

export function signRefreshToken(id: string, role: Role): string {
  return jwt.sign({ id, role }, env.jwt.refreshSecret, {
    expiresIn: env.jwt.refreshExpiresIn,
  } as jwt.SignOptions);
}

export function verifyAccessToken(token: string): AuthPayload {
  return jwt.verify(token, env.jwt.accessSecret) as AuthPayload;
}

export function verifyRefreshToken(token: string): AuthPayload {
  return jwt.verify(token, env.jwt.refreshSecret) as AuthPayload;
}
