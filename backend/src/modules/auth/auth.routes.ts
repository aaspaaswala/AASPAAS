import { Router } from 'express';
import * as authController from './auth.controller';
import { authenticate } from '../../middleware/authenticate';
import rateLimit from 'express-rate-limit';

const router = Router();
const otpRequestLimit = rateLimit({ windowMs: 15 * 60 * 1000, max: 5, standardHeaders: true, legacyHeaders: false });
const otpVerifyLimit = rateLimit({ windowMs: 15 * 60 * 1000, max: 10, standardHeaders: true, legacyHeaders: false });

// Customer: single endpoint — creates account if new, logs in if exists
router.post('/customer', otpRequestLimit, authController.customerAuth);
router.post('/customer/phone/request', otpRequestLimit, authController.requestCustomerPhoneOtp);
router.post('/customer/phone/verify', otpVerifyLimit, authController.verifyCustomerPhoneOtp);
router.post('/customer/email/request', otpRequestLimit, authController.requestCustomerEmailOtp);
router.post('/customer/email/verify', otpVerifyLimit, authController.verifyCustomerEmailOtp);
router.post('/customer/social', authController.customerSocialAuth);

router.post('/refresh', authController.refresh);
router.post('/logout', authController.logout);
router.get('/me', authenticate, authController.me);

export default router;
