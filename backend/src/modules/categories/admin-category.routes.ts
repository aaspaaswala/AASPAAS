import { Router } from 'express';
import * as categoryController from './category.controller';
import { authenticate, requireRole } from '../../middleware/authenticate';
import { ROLES } from '../../config/constants';

const router = Router();

router.use(authenticate);
router.use(requireRole(ROLES.ADMIN));

router.post('/', categoryController.createCategory);
router.put('/:id', categoryController.updateCategory);
router.delete('/:id', categoryController.deleteCategory);

export default router;
