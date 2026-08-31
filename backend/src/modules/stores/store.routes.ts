import { Router } from 'express';
import * as storeController from './store.controller';

const router = Router();

// GET /api/v1/stores/nearby?latitude=26.9&longitude=75.8&radius=5
router.get('/nearby', storeController.nearby);

// GET /api/v1/stores/:id
router.get('/:id', storeController.getById);

// GET /api/v1/stores/:id/products
router.get('/:id/products', storeController.getProducts);

export default router;
