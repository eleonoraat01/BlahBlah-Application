import {
  USER_ROLES,
  AVAILABLE_STATUS_TYPES,
  VALID_PROFILE_VIEW_TABS,
  AVAILABLE_SOCIAL_LINKS_TYPES
} from '@/constants';

/** Profile view tab type derived from valid profile view tabs constant */
export type ProfileViewTabType = (typeof VALID_PROFILE_VIEW_TABS)[number];

/** Social link type derived from available social links types constant */
export type ProfileViewSocialLinkType = (typeof AVAILABLE_SOCIAL_LINKS_TYPES)[number];

/** User status type derived from available status types constant */
export type UserStatusType = (typeof AVAILABLE_STATUS_TYPES)[number];

/** User role type derived from user roles constant */
export type UserRoleType = (typeof USER_ROLES)[keyof typeof USER_ROLES];

/** Interface representing a user in the system */
export interface UserTable {
  /** Unique identifier for the user */
  id: number;
  /** Username used for login and identification */
  username: string;
  /** Display name shown in the UI */
  displayname?: string;
  /** Current status of the user */
  status?: UserStatusType;
  /** Array of user's friendship relationships */
  friendships?: {
    /** Unique identifier for the friendship */
    id: number;
    /** The current user in the friendship */
    user: UserTable;
    /** The other user in the friendship */
    friend: UserTable;
    /** Timestamp of when the friendship was created */
    createdAt: number;
    /** Timestamp of when the friendship was last updated */
    updatedAt: number;
  }[];
  /** Array of user's interests */
  interests?: {
    /** Interest or hobby of the user */
    interest: string;
  }[];
  /** Array of user's social media links */
  socialLinks?: {
    /** Type of social media platform */
    platform: ProfileViewSocialLinkType;
    /** URL to the user's profile on the platform */
    url: string;
  }[];
  /** URL to user's profile picture */
  profilePictureUrl?: string;
  /** URL to user's cover picture */
  coverPictureUrl?: string;
  /** User's email address */
  email?: string;
  /** User's phone number */
  phone?: string;
  /** User's physical address */
  address?: string;
  /** User's biography or description */
  bio?: string;
  /** Flag indicating if the user is active */
  isActive?: boolean;
  /** Timestamp of when the user was created */
  createdAt: number;
  /** Timestamp of when the user was last updated */
  updatedAt: number;
}

/** Interface representing a chat between two users */
export interface ChatTable {
  /** Unique identifier for the chat */
  id: number;
  /** The current user in the chat */
  user1: UserTable;
  /** The other user, the recipient, in the chat */
  user2: UserTable;
  /** Array of messages in the chat */
  messages: MessageTable[];
  /** Flag indicating if the chat is active */
  isActive?: boolean;
  /** Timestamp of when the chat was created */
  createdAt: number;
  /** Timestamp of when the chat was last updated */
  updatedAt: number;
}

/** Interface representing a channel or group chat */
export interface ChannelTable {
  /** Unique identifier for the channel */
  id: number;
  /** Name of the channel */
  name: string;
  /** Description of the channel */
  description?: string;
  /** Array of channel members with their roles */
  members: {
    /** Unique identifier for the membership entry */
    id: number;
    /** Role of the user in the channel */
    role: UserRoleType;
    /** The user/member itself */
    user: UserTable;
    /** Flag indicating if the user is active in the channel */
    isActive?: boolean;
    /** Timestamp of when the user joined the channel */
    joinedAt: number;
  }[];
  /** Array of messages in the channel */
  messages: MessageTable[];
  /** Flag indicating if the channel is active */
  isActive?: boolean;
  /** Timestamp of when the channel was created */
  createdAt: number;
  /** Timestamp of when the channel was last updated */
  updatedAt: number;
}

/** Interface representing a message in a chat or channel */
export interface MessageTable {
  /** Unique identifier for the message */
  id: number;
  /** ID of the chat this message belongs to, if any */
  chatId?: number;
  /** ID of the channel this message belongs to, if any */
  channelId?: number;
  /** User who sent the message */
  sender: UserTable;
  /** Content of the message */
  content: string;
  /** Flag indicating if this is a special message */
  isSpecial?: boolean;
  /** Flag indicating if the message is active */
  isActive?: boolean;
  /** Timestamp of when the message was created */
  createdAt: number;
  /** Timestamp of when the message was last updated */
  updatedAt: number;
}
