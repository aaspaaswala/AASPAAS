import { Request } from 'express';
import { Types } from 'mongoose';
import { ROLES } from '../config/constants';

export type Role = (typeof ROLES)[keyof typeof ROLES];

export interface AuthPayload {
  id: string;
  role: Role;
}

export interface AuthRequest extends Request {
  auth?: AuthPayload;
}

export interface GeoPoint {
  type: 'Point';
  coordinates: [number, number]; // [longitude, latitude]
}

export interface PaginationQuery {
  page?: number;
  limit?: number;
}

export type ObjectId = Types.ObjectId;
