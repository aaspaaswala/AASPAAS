import { Request, Response, NextFunction } from 'express';
import * as adminService from './admin.service';
import { sendSuccess } from '../../utils/response';
import {
  adminInventorySchema,
  adminProductSchema,
  adminStoreProfileSchema,
  inquirySchema,
  inquiryStatusSchema,
  platformSettingsSchema,
  retailerBlockSchema,
} from './admin.schema';
import { ValidationError } from '../../utils/errors';

export async function getStats(req: Request, res: Response, next: NextFunction): Promise<void> {
  try { sendSuccess(res, await adminService.getAdminStats()); } catch (e) { next(e); }
}

export async function getRevenueChart(req: Request, res: Response, next: NextFunction): Promise<void> {
  try { sendSuccess(res, await adminService.getRevenueChart()); } catch (e) { next(e); }
}

export async function getUsers(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { page = 1, limit = 10, search = '' } = req.query;
    sendSuccess(res, await adminService.listUsers(+page, +limit, search as string));
  } catch (e) { next(e); }
}

export async function getUser(req: Request, res: Response, next: NextFunction): Promise<void> {
  try { sendSuccess(res, await adminService.getUserById(req.params.id)); } catch (e) { next(e); }
}

export async function deleteUser(req: Request, res: Response, next: NextFunction): Promise<void> {
  try { await adminService.deleteUser(req.params.id); sendSuccess(res, null, 'User deleted'); } catch (e) { next(e); }
}

export async function blockUserAccount(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const isBlocked = req.body.isBlocked === true;
    const blockReason = typeof req.body.blockReason === 'string' ? req.body.blockReason : '';
    sendSuccess(res, await adminService.blockUser(req.params.id, isBlocked, blockReason));
  } catch (e) { next(e); }
}

export async function getBusinesses(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { page = 1, limit = 10, search = '' } = req.query;
    sendSuccess(res, await adminService.listBusinesses(+page, +limit, search as string));
  } catch (e) { next(e); }
}

export async function getStores(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { page = 1, limit = 10, search = '', status = '' } = req.query;
    sendSuccess(res, await adminService.listStores(+page, +limit, search as string, status as string));
  } catch (e) { next(e); }
}

export async function getStore(req: Request, res: Response, next: NextFunction): Promise<void> {
  try { sendSuccess(res, await adminService.getStoreById(req.params.id)); } catch (e) { next(e); }
}

export async function toggleStore(req: Request, res: Response, next: NextFunction): Promise<void> {
  try { sendSuccess(res, await adminService.toggleStoreStatus(req.params.id)); } catch (e) { next(e); }
}

export async function updateStoreProfile(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const input = adminStoreProfileSchema.parse(req.body);
    sendSuccess(res, await adminService.updateStoreProfile(req.params.id, input));
  } catch (e) {
    if (e instanceof Error && e.name === 'ZodError') return next(new ValidationError(e.message));
    next(e);
  }
}

export async function setRetailerBlocked(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { isBlocked, blockReason } = retailerBlockSchema.parse(req.body);
    sendSuccess(res, await adminService.setRetailerBlocked(req.params.id, isBlocked, blockReason));
  } catch (e) {
    if (e instanceof Error && e.name === 'ZodError') return next(new ValidationError(e.message));
    next(e);
  }
}

export async function getStoreProducts(req: Request, res: Response, next: NextFunction): Promise<void> {
  try { sendSuccess(res, await adminService.listStoreProducts(req.params.id)); } catch (e) { next(e); }
}

export async function createStoreProduct(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const input = adminProductSchema.parse(req.body);
    sendSuccess(res, await adminService.createStoreProduct(req.params.id, input), 'Product created', 201);
  } catch (e) {
    if (e instanceof Error && e.name === 'ZodError') return next(new ValidationError(e.message));
    next(e);
  }
}

export async function updateStoreProduct(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const input = adminProductSchema.parse(req.body);
    sendSuccess(res, await adminService.updateStoreProduct(req.params.id, req.params.productId, input));
  } catch (e) {
    if (e instanceof Error && e.name === 'ZodError') return next(new ValidationError(e.message));
    next(e);
  }
}

export async function deleteStoreProduct(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    await adminService.deleteStoreProduct(req.params.id, req.params.productId);
    sendSuccess(res, null, 'Product deleted');
  } catch (e) { next(e); }
}

export async function updateStoreInventory(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const input = adminInventorySchema.parse(req.body);
    sendSuccess(res, await adminService.updateStoreInventory(req.params.id, req.params.inventoryId, input));
  } catch (e) {
    if (e instanceof Error && e.name === 'ZodError') return next(new ValidationError(e.message));
    next(e);
  }
}

export async function getStoreActivity(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const page = Math.max(1, Number(req.query.page) || 1);
    const limit = Math.min(100, Math.max(1, Number(req.query.limit) || 20));
    sendSuccess(res, await adminService.listStoreActivity(req.params.id, page, limit));
  } catch (e) { next(e); }
}

export async function getReservations(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { page = 1, limit = 10, search = '', status = '' } = req.query;
    sendSuccess(res, await adminService.listReservations(+page, +limit, search as string, status as string));
  } catch (e) { next(e); }
}

export async function getProducts(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { page = 1, limit = 10, search = '' } = req.query;
    sendSuccess(res, await adminService.listProducts(+page, +limit, search as string));
  } catch (e) { next(e); }
}

export async function toggleProduct(req: Request, res: Response, next: NextFunction): Promise<void> {
  try { sendSuccess(res, await adminService.toggleProductStatus(req.params.id)); } catch (e) { next(e); }
}

export async function getCategories(req: Request, res: Response, next: NextFunction): Promise<void> {
  try { sendSuccess(res, await adminService.listAdminCategories()); } catch (e) { next(e); }
}

export async function createCategory(req: Request, res: Response, next: NextFunction): Promise<void> {
  try { sendSuccess(res, await adminService.createAdminCategory(req.body.name, req.body.description), 'Category created', 201); } catch (e) { next(e); }
}

export async function updateCategory(req: Request, res: Response, next: NextFunction): Promise<void> {
  try { sendSuccess(res, await adminService.updateAdminCategory(req.params.id, req.body)); } catch (e) { next(e); }
}

export async function deleteCategory(req: Request, res: Response, next: NextFunction): Promise<void> {
  try { await adminService.deleteAdminCategory(req.params.id); sendSuccess(res, null, 'Category deleted'); } catch (e) { next(e); }
}

export async function submitInquiry(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const inquiry = inquirySchema.parse(req.body);
    sendSuccess(res, await adminService.saveInquiry(inquiry), 'Inquiry submitted', 201);
  } catch (e) {
    if (e instanceof Error && e.name === 'ZodError') return next(new ValidationError(e.message));
    next(e);
  }
}

export async function getInquiries(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const page = Math.max(1, Number(req.query.page) || 1);
    const limit = Math.min(100, Math.max(1, Number(req.query.limit) || 20));
    const status = typeof req.query.status === 'string' ? req.query.status : '';
    sendSuccess(res, await adminService.listInquiries(page, limit, status));
  } catch (e) { next(e); }
}

export async function updateInquiryStatus(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { status } = inquiryStatusSchema.parse(req.body);
    sendSuccess(res, await adminService.updateInquiryStatus(req.params.id, status));
  } catch (e) {
    if (e instanceof Error && e.name === 'ZodError') return next(new ValidationError(e.message));
    next(e);
  }
}

export async function getSettings(_req: Request, res: Response, next: NextFunction): Promise<void> {
  try { sendSuccess(res, await adminService.getPlatformSettings()); } catch (e) { next(e); }
}

export async function updateSettings(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const settings = platformSettingsSchema.parse(req.body);
    sendSuccess(res, await adminService.updatePlatformSettings(settings), 'Settings saved');
  } catch (e) {
    if (e instanceof Error && e.name === 'ZodError') return next(new ValidationError(e.message));
    next(e);
  }
}
