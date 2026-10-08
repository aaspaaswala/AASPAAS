import { Router, Request, Response, NextFunction } from 'express';
import { Retailer } from '../../models/Retailer';
import { Store } from '../../models/Store';
import { signAccessToken, signRefreshToken } from '../auth/jwt';
import { sendSuccess } from '../../utils/response';
import { ValidationError, NotFoundError } from '../../utils/errors';
import { ROLES } from '../../config/constants';
import { logger } from '../../utils/logger';
import * as authService from '../auth/auth.service';
import { emailOtpRequestSchema, emailOtpVerifySchema, socialAuthSchema } from '../auth/auth.schema';
import { phoneOtpRequestSchema, phoneOtpVerifySchema } from '../auth/auth.schema';
import rateLimit from 'express-rate-limit';

const router = Router();
const otpRequestLimit = rateLimit({ windowMs: 15 * 60 * 1000, max: 5, standardHeaders: true, legacyHeaders: false });
const otpVerifyLimit = rateLimit({ windowMs: 15 * 60 * 1000, max: 10, standardHeaders: true, legacyHeaders: false });

// POST /business/auth/email/request
router.post('/email/request', otpRequestLimit, async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { email } = emailOtpRequestSchema.parse(req.body);
    await authService.requestRetailerEmailOtp(email);
    sendSuccess(res, null, 'If the email is registered, an OTP has been sent');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
});

// POST /business/auth/email/verify
router.post('/email/verify', otpVerifyLimit, async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { email, otp } = emailOtpVerifySchema.parse(req.body);
    const result = await authService.verifyRetailerEmailOtp(email, otp);
    sendSuccess(res, result, 'Logged in successfully');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
});

// POST /business/auth/social
router.post('/social', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { provider, idToken } = socialAuthSchema.parse(req.body);
    const result = await authService.socialRetailerAuth(provider, idToken);
    sendSuccess(res, result, 'Logged in successfully');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
});

// POST /business/auth/login — direct login with phone number
router.post('/phone/request', otpRequestLimit, async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { phone } = phoneOtpRequestSchema.parse(req.body);
    await authService.requestRetailerPhoneOtp(phone);
    sendSuccess(res, null, 'If the number can be used, a verification code has been sent');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
});

router.post('/login', otpRequestLimit, async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { phone } = phoneOtpRequestSchema.parse(req.body);
    await authService.requestRetailerPhoneOtp(phone);
    sendSuccess(res, null, 'If the number can be used, a verification code has been sent');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
});

router.post('/phone/verify', otpVerifyLimit, async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { phone, otp } = phoneOtpVerifySchema.pick({ phone: true, otp: true }).parse(req.body);
    const result = await authService.verifyRetailerPhoneOtp(phone, otp);
    sendSuccess(res, result, 'Logged in successfully');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
});

// POST /business/auth/register — register new business (creates retailer + store)
router.post('/register', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { ownerName, businessName, phone, email, category, address, latitude, longitude, openingHours } = req.body;

    if (!ownerName || !businessName || !phone || !address) {
      return next(new ValidationError('ownerName, businessName, phone, and address are required'));
    }

    const existing = await Retailer.findOne({ phone });
    if (existing) return next(new ValidationError('An account with this mobile already exists. Please login.'));

    const retailer = await Retailer.create({
      ownerName,
      businessName,
      phone,
      email,
      category,
      verificationStatus: 'PENDING',
    });

    const lat = typeof latitude === 'number' ? latitude : 0;
    const lng = typeof longitude === 'number' ? longitude : 0;

    await Store.create({
      retailerId: retailer._id,
      name: businessName,
      address,
      location: { type: 'Point', coordinates: [lng, lat] },
      phone,
      openingHours: openingHours || '9:00 AM – 9:00 PM',
      categories: category ? [category] : [],
      isActive: false,
    });

    await authService.requestRetailerPhoneOtp(phone);
    logger.info('Retailer registered pending phone verification', { retailerId: retailer._id });
    sendSuccess(res, { otpRequired: true }, 'Verification code sent', 201);
  } catch (err) {
    next(err);
  }
});

export default router;
