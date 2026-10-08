import dotenv from 'dotenv';
import { z } from 'zod';
import mongoose from 'mongoose';
import bcrypt from 'bcryptjs';
import { Admin } from '../src/models/Admin';

dotenv.config();

const envValues = {
  ...process.env,
  ADMIN_BOOTSTRAP_EMAIL: process.env.ADMIN_BOOTSTRAP_EMAIL ?? process.env.ADMIN_EMAIL,
  ADMIN_BOOTSTRAP_PASSWORD: process.env.ADMIN_BOOTSTRAP_PASSWORD ?? process.env.ADMIN_PASSWORD,
  ADMIN_BOOTSTRAP_NAME: process.env.ADMIN_BOOTSTRAP_NAME ?? 'Administrator',
};

const config = z.object({
  MONGODB_URI: z.string().min(1),
  ADMIN_BOOTSTRAP_EMAIL: z.string().email(),
  ADMIN_BOOTSTRAP_PASSWORD: z.string().min(8),
  ADMIN_BOOTSTRAP_NAME: z.string().trim().min(1).max(100).default('Administrator'),
}).parse(envValues);

async function bootstrapAdmin(): Promise<void> {
  await mongoose.connect(config.MONGODB_URI);
  const email = config.ADMIN_BOOTSTRAP_EMAIL.toLowerCase();
  const passwordHash = await bcrypt.hash(config.ADMIN_BOOTSTRAP_PASSWORD, 12);
  await Admin.findOneAndUpdate(
    { email },
    { $set: { email, passwordHash, name: config.ADMIN_BOOTSTRAP_NAME, isActive: true, role: 'ADMIN' } },
    { upsert: true, new: true, setDefaultsOnInsert: true, runValidators: true }
  );
  console.info('Admin account provisioned.');
  await mongoose.disconnect();
}

bootstrapAdmin().catch(async (error: unknown) => {
  console.error('Admin provisioning failed:', error instanceof Error ? error.message : 'Unknown error');
  await mongoose.disconnect();
  process.exitCode = 1;
});
