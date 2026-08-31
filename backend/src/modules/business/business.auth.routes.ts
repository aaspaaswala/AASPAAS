import { Router, Request, Response, NextFunction } from 'express';
import { Retailer } from '../../models/Retailer';
import { Store } from '../../models/Store';
import { signAccessToken, signRefreshToken } from '../auth/jwt';
import { sendSuccess } from '../../utils/response';
import { ValidationError, NotFoundError } from '../../utils/errors';
import { ROLES } from '../../config/constants';
import { logger } from '../../utils/logger';

const router = Router();

// POST /business/auth/login — direct login with phone number
router.post('/login', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { mobile } = req.body;
    if (!mobile) return next(new ValidationError('mobile is required'));

    const retailer = await Retailer.findOne({ phone: mobile });
    if (!retailer) return next(new NotFoundError('Retailer account not found. Please register first.'));

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

    logger.info('Retailer logged in', { phone: mobile });
    sendSuccess(res, { accessToken, refreshToken, retailer: retailerData }, 'Logged in successfully');
  } catch (err) {
    next(err);
  }
});

// POST /business/auth/register — register new business (creates retailer + store)
router.post('/register', async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { ownerName, businessName, mobile, email, category, address, latitude, longitude, openingHours } = req.body;

    if (!ownerName || !businessName || !mobile || !address) {
      return next(new ValidationError('ownerName, businessName, mobile, and address are required'));
    }

    const existing = await Retailer.findOne({ phone: mobile });
    if (existing) return next(new ValidationError('An account with this mobile already exists. Please login.'));

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
    next(err);
  }
});

export default router;
