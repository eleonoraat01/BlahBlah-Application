import { FacebookIcon, InstagramIcon, LinkedInIcon, XIcon, YouTubeIcon, GitHubIcon, TikTokIcon, PinterestIcon, SnapchatIcon, WhatsAppIcon, TelegramIcon, DiscordIcon, RedditIcon } from 'vue3-simple-icons';
import { Link } from 'lucide-vue-next';
import type { Component } from 'vue';
import type { ProfileViewSocialLinkType } from '@/types.d';

const ICONS: Record<ProfileViewSocialLinkType, Component> = {
  custom: Link,
  facebook: FacebookIcon,
  instagram: InstagramIcon,
  linkedin: LinkedInIcon,
  x: XIcon,
  youtube: YouTubeIcon,
  github: GitHubIcon,
  tiktok: TikTokIcon,
  pinterest: PinterestIcon,
  snapchat: SnapchatIcon,
  whatsapp: WhatsAppIcon,
  telegram: TelegramIcon,
  discord: DiscordIcon,
  reddit: RedditIcon,
};

const PLACEHOLDERS: Record<ProfileViewSocialLinkType, string> = {
  custom: 'https://your-custom-link.com',
  facebook: 'https://facebook.com/your-profile',
  instagram: 'https://instagram.com/your-profile',
  linkedin: 'https://linkedin.com/in/your-profile',
  x: 'https://x.com/your-profile',
  youtube: 'https://youtube.com/your-channel',
  github: 'https://github.com/your-username',
  tiktok: 'https://tiktok.com/@your-username',
  pinterest: 'https://pinterest.com/your-profile',
  snapchat: 'https://snapchat.com/add/your-username',
  whatsapp: 'https://wa.me/your-number',
  telegram: 'https://t.me/your-username',
  discord: 'https://discord.com/users/your-id',
  reddit: 'https://reddit.com/user/your-username',
};

export const getSocialIcon = (type: string) =>
  ICONS[type as ProfileViewSocialLinkType] || ICONS.custom;

export const getUrlPlaceholder = (type: string) =>
  PLACEHOLDERS[type as ProfileViewSocialLinkType] || PLACEHOLDERS.custom;

export const compareURLs = (url1: string | undefined, url2: string | undefined) =>
  Boolean(url1 && url2 && normalizeURL(url1) === normalizeURL(url2));

function normalizeURL(url: string) {
  url = url.trim();

  if (!url.startsWith('http')) url = 'https://' + url;

  return new URL(url).href;
};
