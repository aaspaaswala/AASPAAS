import { env } from '../../config/env';
import { UnauthorizedError } from '../../utils/errors';

export type SocialProvider = 'google' | 'facebook';

export interface SocialProfile {
  providerId: string;
  email: string;
  name?: string;
  profileImage?: string;
}

export async function verifySocialToken(provider: SocialProvider, token: string): Promise<SocialProfile> {
  if (provider === 'google') {
    const response = await fetch(`https://oauth2.googleapis.com/tokeninfo?id_token=${encodeURIComponent(token)}`);
    if (!response.ok) throw new UnauthorizedError('Invalid Google token');
    const data = (await response.json()) as Record<string, string>;
    if (data.email_verified !== 'true' || !data.email || !data.sub) {
      throw new UnauthorizedError('Google account email is not verified');
    }
    if (env.social.googleClientId && data.aud !== env.social.googleClientId) {
      throw new UnauthorizedError('Google token audience mismatch');
    }
    return { providerId: data.sub, email: data.email.toLowerCase(), name: data.name, profileImage: data.picture };
  }

  const response = await fetch(
    `https://graph.facebook.com/me?fields=id,name,email,picture.type(large)&access_token=${encodeURIComponent(token)}`
  );
  if (!response.ok) throw new UnauthorizedError('Invalid Facebook token');
  const data = (await response.json()) as { id?: string; name?: string; email?: string; picture?: { data?: { url?: string } } };
  if (!data.id || !data.email) throw new UnauthorizedError('Facebook email permission is required');
  return { providerId: data.id, email: data.email.toLowerCase(), name: data.name, profileImage: data.picture?.data?.url };
}