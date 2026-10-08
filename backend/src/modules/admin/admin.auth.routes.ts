import { Router, Request, Response, NextFunction } from 'express';
import rateLimit from 'express-rate-limit';
import bcrypt from 'bcryptjs';
import { signAccessToken, signRefreshToken } from '../auth/jwt';
import { sendSuccess } from '../../utils/response';
import { UnauthorizedError } from '../../utils/errors';
import { ROLES } from '../../config/constants';
import { Admin } from '../../models/Admin';
import { env } from '../../config/env';

const router = Router();

const loginLimit = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: env.nodeEnv === 'development' ? 25 : 5,
  standardHeaders: true,
  legacyHeaders: false,
});

// POST /admin/auth/login
router.post('/login', loginLimit, async (req: Request, res: Response, next: NextFunction) => {
  try {
    const email = typeof req.body.email === 'string' ? req.body.email.trim().toLowerCase() : '';
    const password = typeof req.body.password === 'string' ? req.body.password : '';
    if (!email || !password) throw new UnauthorizedError('Invalid credentials');
    const admin = await Admin.findOne({ email, isActive: true }).select('+passwordHash');
    if (!admin || !(await bcrypt.compare(password, admin.passwordHash))) {
      throw new UnauthorizedError('Invalid credentials');
    }

    const accessToken = signAccessToken(admin.id, ROLES.ADMIN);
    const refreshToken = signRefreshToken(admin.id, ROLES.ADMIN);

    sendSuccess(res, {
      accessToken,
      refreshToken,
      admin: { id: admin.id, name: admin.name, email: admin.email, role: admin.role },
    }, 'Logged in successfully');
  } catch (e) { next(e); }
});

export default router;
