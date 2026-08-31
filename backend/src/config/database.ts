import mongoose from 'mongoose';
import { MongoMemoryServer } from 'mongodb-memory-server';
import { env } from './env';
import { logger } from '../utils/logger';

export async function connectDatabase(): Promise<void> {
  mongoose.connection.on('disconnected', () => logger.warn('MongoDB disconnected'));
  mongoose.connection.on('error', (err) => logger.error('MongoDB error', { err }));

  try {
    await mongoose.connect(env.mongoUri);
    logger.info(`MongoDB connected: ${env.mongoUri}`);
    return;
  } catch (error) {
    if (!env.useMemoryMongo) {
      throw error;
    }

    logger.warn('Primary MongoDB connection failed; starting in-memory MongoDB for local development');
    const memoryServer = await MongoMemoryServer.create();
    const memoryUri = memoryServer.getUri();

    await mongoose.connect(memoryUri);
    logger.info(`MongoDB connected via in-memory server: ${memoryUri}`);
  }
}
