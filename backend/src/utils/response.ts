import { Response } from 'express';

export const sendSuccess = (
  res: Response,
  data: unknown,
  message = 'Success',
  statusCode = 200
): void => {
  res.status(statusCode).json({ success: true, data, message });
};

export const sendError = (
  res: Response,
  code: string,
  message: string,
  statusCode = 400
): void => {
  res.status(statusCode).json({ success: false, error: { code, message } });
};
