import { Category } from '../business/category.model';
import { NotFoundError } from '../../utils/errors';

export async function listCategories(): Promise<object[]> {
  return Category.find({ isActive: true }).sort({ name: 1 }).lean();
}

export async function getCategory(id: string): Promise<object> {
  const category = await Category.findById(id).lean();
  if (!category) throw new NotFoundError('Category');
  return category;
}

export async function createCategory(input: Record<string, unknown>): Promise<object> {
  const category = await Category.create({
    name: input.name as string,
    description: input.description as string | undefined,
    isActive: true,
  });
  return category;
}

export async function updateCategory(id: string, input: Record<string, unknown>): Promise<object> {
  const category = await Category.findById(id);
  if (!category) throw new NotFoundError('Category');

  if (input.name !== undefined) category.name = input.name as string;
  if (input.description !== undefined) category.description = input.description as string | undefined;
  if (input.isActive !== undefined) category.isActive = input.isActive as boolean;

  await category.save();
  return category;
}

export async function deleteCategory(id: string): Promise<void> {
  const category = await Category.findById(id);
  if (!category) throw new NotFoundError('Category');
  await Category.deleteOne({ _id: id });
}
