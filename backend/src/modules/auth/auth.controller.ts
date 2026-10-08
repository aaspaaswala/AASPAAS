import { Request, Response, NextFunction } from 'express';
import * as authService from './auth.service';
import {
  customerRegisterSchema,
  phoneOtpRequestSchema,
  phoneOtpVerifySchema,
  emailOtpRequestSchema,
  emailOtpVerifySchema,
  refreshSchema,
  socialAuthSchema,
} from './auth.schema';
import { sendSuccess } from '../../utils/response';
import { ValidationError } from '../../utils/errors';
import { AuthRequest } from '../../types';

// Compatibility endpoint: requesting a phone code never authenticates the caller.
export async function customerAuth(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { phone } = customerRegisterSchema.parse(req.body);
    await authService.requestCustomerPhoneOtp(phone);
    sendSuccess(res, null, 'If the number can be used, a verification code has been sent');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
}

export async function requestCustomerPhoneOtp(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { phone } = phoneOtpRequestSchema.parse(req.body);
    await authService.requestCustomerPhoneOtp(phone);
    sendSuccess(res, null, 'If the number can be used, a verification code has been sent');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
}

export async function verifyCustomerPhoneOtp(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { phone, otp, name, email, dob } = phoneOtpVerifySchema.parse(req.body);
    const result = await authService.verifyCustomerPhoneOtp(phone, otp, name, email, dob);
    sendSuccess(res, result, result.isNew ? 'Account created successfully' : 'Logged in successfully');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
}

export async function requestCustomerEmailOtp(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { email } = emailOtpRequestSchema.parse(req.body);
    await authService.requestCustomerEmailOtp(email);
    sendSuccess(res, null, 'If the email is registered, an OTP has been sent');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
}

export async function verifyCustomerEmailOtp(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { email, otp, name, dob } = emailOtpVerifySchema.parse(req.body);
    const result = await authService.verifyCustomerEmailOtp(email, otp, name, dob || undefined);
    sendSuccess(res, result, result.isNew ? 'Account created successfully' : 'Logged in successfully');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
}

export async function customerSocialAuth(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { provider, idToken, name } = socialAuthSchema.parse(req.body);
    const result = await authService.socialCustomerAuth(provider, idToken, name);
    sendSuccess(res, result, result.isNew ? 'Account created successfully' : 'Logged in successfully');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
}

// POST /auth/refresh
export async function refresh(req: Request, res: Response, next: NextFunction): Promise<void> {
  try {
    const { refreshToken } = refreshSchema.parse(req.body);
    const tokens = await authService.refreshTokens(refreshToken);
    sendSuccess(res, tokens, 'Tokens refreshed');
  } catch (err) {
    if (err instanceof Error && err.name === 'ZodError') return next(new ValidationError(err.message));
    next(err);
  }
}

// POST /auth/logout
export async function logout(_req: Request, res: Response): Promise<void> {
  sendSuccess(res, null, 'Logged out successfully');
}

// GET /auth/me
export async function me(req: AuthRequest, res: Response, next: NextFunction): Promise<void> {
  try {
    const { id, role } = req.auth!;
    const data = await authService.getMe(id, role);
    sendSuccess(res, data);
  } catch (err) {
    next(err);
  }
}
