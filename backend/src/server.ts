import { env } from './config/env';
import { connectDatabase } from './config/database';
import { logger } from './utils/logger';
import app from './app';
import { expireOldReservations } from './modules/reservations/reservation.service';

async function bootstrap(): Promise<void> {
  await connectDatabase();

  const server = app.listen(env.port, () => {
    logger.info(`🚀  Server running on port ${env.port} [${env.nodeEnv}]`);
  });

  const shutdown = (signal: string) => {
    logger.info(`${signal} received — shutting down`);
    server.close(() => process.exit(0));
  };

  process.on('SIGTERM', () => shutdown('SIGTERM'));
  process.on('SIGINT', () => shutdown('SIGINT'));

  const expiryIntervalMs = 60_000;
  setInterval(() => {
    expireOldReservations().catch((err) => {
      logger.error('Reservation expiry job failed', { err });
    });
  }, expiryIntervalMs);

  logger.info('Reservation expiry scheduler started', { intervalMs: expiryIntervalMs });
}

bootstrap().catch((err) => {
  logger.error('Failed to start server', { err });
  process.exit(1);
});
