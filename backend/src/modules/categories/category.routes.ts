import { Router } from 'express';
import * as categoryController from './category.controller';

const router = Router();

router.get('/', categoryController.listCategories);
router.get('/:id', categoryController.getCategory);

export default router;
