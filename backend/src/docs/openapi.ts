export const openApiDocument = {
  openapi: '3.0.3',
  info: {
    title: 'Aas Paas Wala API',
    version: '1.0.0',
    description:
      'Public API for customer, business, and admin flows including authentication, products, reservations, and categories.',
  },
  servers: [{ url: 'http://localhost:3000/api/v1', description: 'Local development server' }],
  security: [{ bearerAuth: [] }],
  components: {
    securitySchemes: {
      bearerAuth: {
        type: 'http',
        scheme: 'bearer',
        bearerFormat: 'JWT',
      },
    },
    schemas: {
      AuthResponse: {
        type: 'object',
        properties: {
          success: { type: 'boolean' },
          data: {
            type: 'object',
            properties: {
              accessToken: { type: 'string' },
              refreshToken: { type: 'string' },
              user: { type: 'object' },
              retailer: { type: 'object' },
              admin: { type: 'object' },
              isNew: { type: 'boolean' },
            },
          },
          message: { type: 'string' },
        },
      },
      ErrorResponse: {
        type: 'object',
        properties: {
          success: { type: 'boolean', example: false },
          error: {
            type: 'object',
            properties: {
              code: { type: 'string', example: 'UNAUTHORIZED' },
              message: { type: 'string' },
            },
          },
        },
      },
    },
  },
  paths: {
    '/health': {
      get: {
        summary: 'Health check',
        responses: {
          '200': {
            description: 'Service is healthy',
            content: {
              'application/json': {
                example: { status: 'ok', service: 'aas-paas-wala-backend' },
              },
            },
          },
        },
      },
    },
    '/auth/customer/email/request': {
      post: {
        summary: 'Request email OTP for customer login',
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                type: 'object',
                required: ['email'],
                properties: { email: { type: 'string', format: 'email' } },
              },
            },
          },
        },
        responses: {
          '200': { description: 'OTP generated' },
          '401': { $ref: '#/components/schemas/ErrorResponse' },
        },
      },
    },
    '/auth/customer/email/verify': {
      post: {
        summary: 'Verify customer email OTP',
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                type: 'object',
                required: ['email', 'otp'],
                properties: {
                  email: { type: 'string', format: 'email' },
                  otp: { type: 'string', minLength: 6, maxLength: 6 },
                  name: { type: 'string', nullable: true },
                  dob: { type: 'string', nullable: true },
                },
              },
            },
          },
        },
        responses: {
          '200': { description: 'Customer authenticated', content: { 'application/json': { schema: { $ref: '#/components/schemas/AuthResponse' } } } },
          '401': { $ref: '#/components/schemas/ErrorResponse' },
        },
      },
    },
    '/business/auth/login': {
      post: {
        summary: 'Business login with phone or email OTP flow',
        responses: { '200': { description: 'Business auth token' } },
      },
    },
    '/admin/auth/login': {
      post: {
        summary: 'Admin login',
        requestBody: {
          required: true,
          content: {
            'application/json': {
              schema: {
                type: 'object',
                required: ['email', 'password'],
                properties: {
                  email: { type: 'string', format: 'email' },
                  password: { type: 'string' },
                },
              },
            },
          },
        },
        responses: {
          '200': { description: 'Admin authenticated' },
          '401': { $ref: '#/components/schemas/ErrorResponse' },
        },
      },
    },
  },
} as const;
