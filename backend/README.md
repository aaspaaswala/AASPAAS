# Aas Paas Wala — Backend

Hyperlocal offline shopping discovery platform backend.

## Stack

- Node.js + TypeScript
- Express.js
- MongoDB + Mongoose
- JWT (access + refresh tokens)
- Zod validation
- Winston logging

## Setup

### 1. Install dependencies

```bash
npm install
```

### 2. Configure environment

```bash
cp .env.example .env
```

Edit `.env` and set at minimum:

- `MONGODB_URI` — your MongoDB connection string
- `JWT_ACCESS_SECRET` — random string, min 32 chars
- `JWT_REFRESH_SECRET` — random string, min 32 chars

### 3. Run in development

```bash
npm run dev
```

### 4. Build for production

```bash
npm run build
npm start
```

### 5. Run tests

```bash
npm test
```

### 6. Seed development data

```bash
npm run seed
```

## Health Check

```
GET /health
```

Response:

```json
{ "status": "ok", "service": "aas-paas-wala-backend" }
```

## API

All API routes are versioned under `/api/v1/`.

See Swagger docs at `/api/v1/docs` (available after Step 2.17).

## Development Notes

- `USE_MOCK_OTP=true` skips real SMS — OTP is always `MOCK_OTP` value (default `123456`)
- All times are stored and processed in UTC
- Never commit `.env`
