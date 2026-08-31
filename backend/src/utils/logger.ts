import winston from 'winston';
import { env } from '../config/env';

const fmt = winston.format;

export const logger = winston.createLogger({
  level: env.nodeEnv === 'production' ? 'info' : 'debug',
  format: fmt.combine(
    fmt.timestamp(),
    env.nodeEnv === 'production'
      ? fmt.json()
      : fmt.combine(fmt.colorize(), fmt.printf(({ timestamp, level, message, ...meta }) => {
          const extra = Object.keys(meta).length ? ' ' + JSON.stringify(meta) : '';
          return `${timestamp} [${level}] ${message}${extra}`;
        }))
  ),
  transports: [new winston.transports.Console()],
});
