export const USER_ROLES = {
  OWNER: 'owner',
  ADMIN: 'admin',
  MEMBER: 'member',
} as const;

export const STATUS_PRIORITY = { online: 1, busy: 2, away: 3, offline: 4 };
export const AVAILABLE_STATUS_TYPES = [
  'online',
  'busy',
  'away',
  'offline',
] as const;

export const DEFAULT_PROFILE_VIEW_TAB = 'about';
export const VALID_PROFILE_VIEW_TABS = [
  DEFAULT_PROFILE_VIEW_TAB,
  'friends',
  'interests',
  'social'
] as const;

export const DEFAULT_SOCIAL_LINKS_TYPE = 'custom';
export const AVAILABLE_SOCIAL_LINKS_TYPES = [
  DEFAULT_SOCIAL_LINKS_TYPE,
  'facebook',
  'instagram',
  'linkedin',
  'x',
  'youtube',
  'github',
  'tiktok',
  'pinterest',
  'snapchat',
  'whatsapp',
  'telegram',
  'discord',
  'reddit',
] as const;
