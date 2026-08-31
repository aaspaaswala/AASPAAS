import express from 'express';
import helmet from 'helmet';
import cors from 'cors';
import rateLimit from 'express-rate-limit';

import { env } from './config/env';
import { requestLogger } from './middleware/requestLogger';
import { notFound } from './middleware/notFound';
import { errorHandler } from './middleware/errorHandler';
import authRouter from './modules/auth/auth.routes';
import productRouter from './modules/products/product.routes';
import storeRouter from './modules/stores/store.routes';
import reservationRouter from './modules/reservations/reservation.routes';
import businessRouter from './modules/business/business.routes';
import businessAuthRouter from './modules/business/business.auth.routes';
import categoryRouter from './modules/categories/category.routes';
import adminCategoryRouter from './modules/categories/admin-category.routes';

const app = express();

// Security headers
app.use(helmet());

// CORS
app.use(cors({ origin: env.corsOrigins, credentials: true }));

// Body parsing
app.use(express.json({ limit: '10kb' }));
app.use(express.urlencoded({ extended: true }));

// Request logging
app.use(requestLogger);

// Global rate limit
app.use(
  '/api',
  rateLimit({
    windowMs: 15 * 60 * 1000, // 15 minutes
    max: 200,
    standardHeaders: true,
    legacyHeaders: false,
    message: { success: false, error: { code: 'RATE_LIMITED', message: 'Too many requests.' } },
  })
);

// Health check
app.get('/health', (_req, res) => {
  res.json({ status: 'ok', service: 'aas-paas-wala-backend' });
});

// API routes
app.use('/api/v1/auth', authRouter);
app.use('/api/v1/products', productRouter);
app.use('/api/v1/stores', storeRouter);
app.use('/api/v1/reservations', reservationRouter);
app.use('/api/v1/business/auth', businessAuthRouter);
app.use('/api/v1/business', businessRouter);
app.use('/api/v1/categories', categoryRouter);
app.use('/api/v1/admin/categories', adminCategoryRouter);

// 404 + error handler — must be last
app.use(notFound);
app.use(errorHandler);

export default app;
