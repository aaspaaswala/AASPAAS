import { Router } from 'express';
import * as productController from './product.controller';

const router = Router();

// GET /api/v1/products/search?q=sweater&brand=XYZ
router.get('/search', productController.search);

// GET /api/v1/products/nearby?latitude=26.9&longitude=75.8&radius=5&q=sweater
router.get('/nearby', productController.nearby);

// GET /api/v1/products/:id
router.get('/:id', productController.getById);

export default router;
