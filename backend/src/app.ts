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
import adminRouter from './modules/admin/admin.routes';
import adminAuthRouter from './modules/admin/admin.auth.routes';
import { openApiDocument } from './docs/openapi';

const app = express();

// Security headers
app.use(helmet());

// CORS
const corsOrigins = [...env.corsOrigins];
if (env.nodeEnv === 'development') {
  corsOrigins.push(
    ...env.corsOrigins
      .filter((origin) => origin.startsWith('http://localhost:'))
      .map((origin) => origin.replace('http://localhost:', 'http://127.0.0.1:'))
  );
}
app.use(cors({ origin: corsOrigins, credentials: true }));

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

// API docs
app.get('/api/v1/docs', (_req, res) => {
  res.json(openApiDocument);
});

app.get('/api/v1/openapi.json', (_req, res) => {
  res.json(openApiDocument);
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
app.use('/api/v1/admin/auth', adminAuthRouter);
app.use('/api/v1/admin', adminRouter);

// 404 + error handler — must be last
app.use(notFound);
app.use(errorHandler);

export default app;
