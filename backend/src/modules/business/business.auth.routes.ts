import { Router, Request, Response, NextFunction } from 'express';
import { Retailer } from '../../models/Retailer';
import { Store } from '../../models/Store';
import { signAccessToken, signRefreshToken } from '../auth/jwt';
import { sendSuccess } from '../../utils/response';
import { ValidationError } from '../../utils/errors';
import { ROLES } from '../../config/constants';
import { logger } from '../../utils/logger';
import { Types } from 'mongoose';

const router = Router();

router.post('/send-otp', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { mobile } = req.body;
    if (!mobile) return next(new ValidationError('mobile is required'));

    const existing = await Retailer.findOne({ phone: mobile });
    if (!existing) return next(new ValidationError('Retailer account not found'));

    const otp = Math.floor(100000 + Math.random() * 900000).toString();
    const expiresAt = new Date(Date.now() + 10 * 60 * 1000);
    existing.otp = otp;
    existing.otpExpiresAt = expiresAt;
    await existing.save();

    logger.info('Retailer OTP sent', { phone: mobile });
    sendSuccess(res, null, 'OTP sent successfully');
  } catch (err) {
    if ((err as any).name === 'ZodError') return next(new ValidationError((err as Error).message));
    next(err);
  }
});

router.post('/verify-otp', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { mobile, otp } = req.body;
    if (!mobile || !otp) return next(new ValidationError('mobile and otp are required'));

    const retailer = await Retailer.findOne({ phone: mobile }).select('+otp +otpExpiresAt');
    if (!retailer) return next(new ValidationError('Retailer not found'));
    if (!retailer.otp || retailer.otp !== otp) return next(new ValidationError('Invalid OTP'));
    if (!retailer.otpExpiresAt || retailer.otpExpiresAt < new Date()) return next(new ValidationError('OTP expired'));

    retailer.otp = undefined;
    retailer.otpExpiresAt = undefined;
    await retailer.save();

    const accessToken = signAccessToken(retailer.id, ROLES.RETAILER);
    const refreshToken = signRefreshToken(retailer.id, ROLES.RETAILER);

    const retailerData = {
      _id: retailer._id,
      ownerName: retailer.ownerName,
      businessName: retailer.businessName,
      mobile: retailer.phone,
      email: retailer.email,
      verificationStatus: retailer.verificationStatus,
    };

    sendSuccess(res, { accessToken, refreshToken, retailer: retailerData }, 'Authenticated successfully');
  } catch (err) {
    if ((err as any).name === 'ZodError') return next(new ValidationError((err as Error).message));
    next(err);
  }
});

router.post('/register', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { ownerName, businessName, mobile, email, category, address, latitude, longitude, openingHours } = req.body;

    if (!ownerName || !businessName || !mobile || !address) {
      return next(new ValidationError('ownerName, businessName, mobile, and address are required'));
    }

    const existing = await Retailer.findOne({ phone: mobile });
    if (existing) return next(new ValidationError('Retailer already exists'));

    const retailer = await Retailer.create({
      ownerName,
      businessName,
      phone: mobile,
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
      phone: mobile,
      openingHours: openingHours || '9:00 AM – 9:00 PM',
      categories: category ? [category] : [],
      isActive: false,
    });

    const accessToken = signAccessToken(retailer._id.toString(), ROLES.RETAILER);
    const refreshToken = signRefreshToken(retailer._id.toString(), ROLES.RETAILER);

    const retailerData = {
      _id: retailer._id,
      ownerName: retailer.ownerName,
      businessName: retailer.businessName,
      mobile: retailer.phone,
      email: retailer.email,
      verificationStatus: retailer.verificationStatus,
    };

    logger.info('Retailer registered', { retailerId: retailer._id });
    sendSuccess(res, { accessToken, refreshToken, retailer: retailerData }, 'Registered successfully', 201);
  } catch (err) {
    if ((err as any).name === 'ZodError') return next(new ValidationError((err as Error).message));
    next(err);
  }
});

export default router;
