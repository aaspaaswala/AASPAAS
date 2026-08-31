import { Router } from 'express';
import * as businessController from './business.controller';
import { authenticate, requireRole } from '../../middleware/authenticate';
import { ROLES } from '../../config/constants';

const router = Router();

router.use(authenticate);
router.use(requireRole(ROLES.RETAILER));

router.get('/dashboard', businessController.getDashboard);
router.get('/store', businessController.getStore);
router.put('/store', businessController.updateStore);
router.get('/products', businessController.getProducts);
router.post('/products', businessController.createProduct);
router.put('/products/:id', businessController.updateProduct);
router.delete('/products/:id', businessController.deleteProduct);
router.put('/inventory/:id', businessController.updateInventory);
router.get('/reservations', businessController.getReservations);
router.post('/reservations/:id/confirm', businessController.confirmReservation);
router.post('/reservations/:id/complete', businessController.completeReservation);
router.post('/reservations/:id/cancel', businessController.cancelReservation);

export default router;
