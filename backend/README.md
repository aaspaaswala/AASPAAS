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

For production email OTP delivery, also set `USE_MOCK_EMAIL_OTP=false` and
configure `EMAIL_OTP_FROM`, `SMTP_HOST`, `SMTP_PORT`, `SMTP_SECURE`,
`SMTP_USER`, and `SMTP_PASSWORD`. Keep these values out of source control.

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

### Provision an admin account

Admin credentials are not created with a shared default. Set these variables in
your local, git-ignored `.env`, then provision or rotate the account:

```text
ADMIN_BOOTSTRAP_EMAIL=you@example.com
ADMIN_BOOTSTRAP_PASSWORD=<unique password with at least 14 characters>
ADMIN_BOOTSTRAP_NAME=Administrator
```

```bash
npm run admin:bootstrap
```

The command stores a bcrypt password hash in MongoDB. It does not print the
password. Use the configured email/password on the admin login screen.

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

OpenAPI JSON is available at `/api/v1/docs` and `/api/v1/openapi.json`.

## Development Notes

- Development/test OTP mocks are enabled outside production; set `MOCK_OTP` locally if deterministic test codes are needed.
- Production requires `USE_MOCK_OTP=false`, `USE_MOCK_EMAIL_OTP=false`, the configured SMS adapter (`SMS_PROVIDER_URL`, `SMS_PROVIDER_TOKEN`, `SMS_SENDER_ID`), and SMTP credentials.
- All times are stored and processed in UTC
- Never commit `.env`
