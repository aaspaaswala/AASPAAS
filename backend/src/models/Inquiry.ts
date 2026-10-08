import mongoose, { Document, Schema } from 'mongoose';

export type InquiryStatus = 'NEW' | 'READ' | 'CLOSED';

export interface IInquiry extends Document {
  type: string;
  name: string;
  business?: string;
  city?: string;
  phone?: string;
  email: string;
  subject?: string;
  message?: string;
  status: InquiryStatus;
  createdAt: Date;
  updatedAt: Date;
}

const inquirySchema = new Schema<IInquiry>(
  {
    type: { type: String, required: true, trim: true, maxlength: 50 },
    name: { type: String, required: true, trim: true, maxlength: 100 },
    business: { type: String, trim: true, maxlength: 120 },
    city: { type: String, trim: true, maxlength: 100 },
    phone: { type: String, trim: true, maxlength: 20 },
    email: { type: String, required: true, trim: true, lowercase: true, maxlength: 254 },
    subject: { type: String, trim: true, maxlength: 120 },
    message: { type: String, trim: true, maxlength: 5000 },
    status: { type: String, enum: ['NEW', 'READ', 'CLOSED'], default: 'NEW', required: true },
  },
  { timestamps: true }
);

inquirySchema.index({ createdAt: -1 });
inquirySchema.index({ status: 1, createdAt: -1 });

export const Inquiry = mongoose.model<IInquiry>('Inquiry', inquirySchema);
