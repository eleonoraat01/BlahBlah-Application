import { defineStore } from 'pinia';
import { computed } from 'vue';
import { useUserStore } from '@/stores/user';
import { useChatStore } from '@/pages/chat';
import { useChannelStore } from '@/pages/channel';
import { filterRecentMessages } from '@/utils';

/** The maximum number of active channels to display */
const ACTIVE_CHANNELS_LIMIT = 5;

export const useHomeStore = defineStore('home', () => {
  const userStore = useUserStore();
  const chatStore = useChatStore();
  const channelStore = useChannelStore();

  /* Getters */
  const activeChannels = computed(() => (
    channelStore.currentUserChannels
      .filter(channel => channel.messages.length > 0)
      .slice(0, ACTIVE_CHANNELS_LIMIT)
  ));
  const recentNotifications = computed(() => (
    chatStore.currentUserChats
      .flatMap(chat => chat.messages
        .filter(message => filterRecentMessages(message, userStore.user.id))
        .map(message => ({ ...message, chatId: chat.id }))
      )
      .sort((a, b) => b.createdAt - a.createdAt)
  ));
  const recentActivity = computed(() => (
    channelStore.currentUserChannels
      .flatMap(channel => channel.messages
        .filter(message => filterRecentMessages(message, userStore.user.id))
        .map(message => ({ ...message, title: channel.name, channelId: channel.id }))
      )
      .sort((a, b) => b.createdAt - a.createdAt)
  ));

  return {
    activeChannels,
    recentNotifications,
    recentActivity,
  };
});
