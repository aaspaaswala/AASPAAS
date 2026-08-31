import { Router } from 'express';
import * as authController from './auth.controller';
import { authenticate } from '../../middleware/authenticate';

const router = Router();

// Customer: single endpoint — creates account if new, logs in if exists
router.post('/customer', authController.customerAuth);

router.post('/refresh', authController.refresh);
router.post('/logout', authController.logout);
router.get('/me', authenticate, authController.me);

export default router;
