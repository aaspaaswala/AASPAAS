import { Router } from 'express';
import * as ctrl from './admin.controller';
import { authenticate, requireRole } from '../../middleware/authenticate';

const router = Router();

// Public — inquiry submit (from website)
router.post('/inquiry', ctrl.submitInquiry);

// Admin protected routes
router.use(authenticate, requireRole('ADMIN'));

router.get('/stats', ctrl.getStats);
router.get('/revenue-chart', ctrl.getRevenueChart);

router.get('/users', ctrl.getUsers);
router.get('/users/:id', ctrl.getUser);
router.delete('/users/:id', ctrl.deleteUser);
router.patch('/users/:id/account', ctrl.blockUserAccount);

router.get('/businesses', ctrl.getBusinesses);

router.get('/stores', ctrl.getStores);
router.get('/stores/:id', ctrl.getStore);
router.patch('/stores/:id/toggle', ctrl.toggleStore);
router.put('/stores/:id/profile', ctrl.updateStoreProfile);
router.patch('/stores/:id/account', ctrl.setRetailerBlocked);
router.get('/stores/:id/products', ctrl.getStoreProducts);
router.post('/stores/:id/products', ctrl.createStoreProduct);
router.put('/stores/:id/products/:productId', ctrl.updateStoreProduct);
router.delete('/stores/:id/products/:productId', ctrl.deleteStoreProduct);
router.patch('/stores/:id/inventory/:inventoryId', ctrl.updateStoreInventory);
router.get('/stores/:id/activity', ctrl.getStoreActivity);

router.get('/reservations', ctrl.getReservations);

router.get('/products', ctrl.getProducts);
router.patch('/products/:id/toggle', ctrl.toggleProduct);

router.get('/categories', ctrl.getCategories);
router.post('/categories', ctrl.createCategory);
router.patch('/categories/:id', ctrl.updateCategory);
router.delete('/categories/:id', ctrl.deleteCategory);

router.get('/inquiries', ctrl.getInquiries);
router.patch('/inquiries/:id/status', ctrl.updateInquiryStatus);
router.get('/settings', ctrl.getSettings);
router.put('/settings', ctrl.updateSettings);

export default router;
