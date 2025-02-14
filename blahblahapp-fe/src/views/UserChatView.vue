<template>
  <section class="flex h-[calc(100vh-var(--header-height))]">
    <ChatViewSidebar />

    <div class="flex-1 flex flex-col min-w-0">
      <div v-if="!chatStore.selectedChat" class="flex-1 flex flex-col justify-center items-center">
        <MessageCircleX class="h-12 w-12 mx-auto text-muted-foreground mb-4" />
        <h3 class="text-lg font-medium">No chat selected</h3>
        <p class="text-muted-foreground">Choose a conversation to start messaging</p>
      </div>

      <template v-else>
        <ChatViewHeader :key="chatStore.selectedChat.id" :chat="chatStore.selectedChat" />
        <ChatViewMessages :key="chatStore.selectedChat.id" :chat="chatStore.selectedChat" />
        <ChatViewInput :key="chatStore.selectedChat.id" :chat="chatStore.selectedChat" />
      </template>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { MessageCircleX } from 'lucide-vue-next';
import {
  useChatStore, ChatViewSidebar, ChatViewHeader,
  ChatViewMessages, ChatViewInput
} from '@/pages/chat';

const route = useRoute();
const chatStore = useChatStore();

function initializeFromRoute() {
  const chatId = route.params.chatId;
  if (!chatId) return chatStore.resetSelectedChat();

  const chat = chatStore.currentUserChats
    .find(c => c.id === Number(chatId));

  if (chat) chatStore.changeSelectedChat(chat.id);
  else chatStore.resetSelectedChat();
}

watch(
  () => route.params.chatId,
  initializeFromRoute,
  { immediate: true }
);

onMounted(initializeFromRoute);
</script>
