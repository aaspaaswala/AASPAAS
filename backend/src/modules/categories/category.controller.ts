import { Request, Response, NextFunction } from 'express';
import * as categoryService from './category.service';
import { sendSuccess } from '../../utils/response';
import { ValidationError, NotFoundError } from '../../utils/errors';

export async function listCategories(_req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const categories = await categoryService.listCategories();
    sendSuccess(res, { categories });
  } catch (err) {
    next(err);
  }
}

export async function getCategory(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const category = await categoryService.getCategory(req.params.id);
    sendSuccess(res, { category });
  } catch (err) {
    next(err);
  }
}

export async function createCategory(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const category = await categoryService.createCategory(req.body);
    sendSuccess(res, { category }, 'Category created', 201);
  } catch (err) {
    if ((err as any).name === 'ZodError') return next(new ValidationError((err as Error).message));
    next(err);
  }
}

export async function updateCategory(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const category = await categoryService.updateCategory(req.params.id, req.body);
    sendSuccess(res, { category });
  } catch (err) {
    if ((err as any).name === 'ZodError') return next(new ValidationError((err as Error).message));
    next(err);
  }
}

export async function deleteCategory(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    await categoryService.deleteCategory(req.params.id);
    sendSuccess(res, null, 'Category deleted');
  } catch (err) {
    next(err);
  }
}
