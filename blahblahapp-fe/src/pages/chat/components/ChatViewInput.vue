<template>
  <section class="p-4 border-t border-border">
    <form class="flex items-center gap-2" @submit.prevent="handleSubmit">
      <AutosizeTextarea :key="chat.id" v-model="message" :disabled="!areMembersFriends" @submit="handleSubmit" />
      <Button type="submit" :disabled="!message?.trim() || !areMembersFriends">
        <Send class="h-4 w-4" />
      </Button>
    </form>
  </section>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { Send } from 'lucide-vue-next';
import { useUserStore } from '@/stores/user';
import { useChatStore } from '@/pages/chat';
import { AutosizeTextarea } from '@/components/shared';
import { Button } from '@/components/ui/button';
import type { ChatTable } from '@/types.d';

const props = defineProps<{ chat: ChatTable }>();

const userStore = useUserStore();
const chatStore = useChatStore();
const message = ref('');

const areMembersFriends = computed(() => (
  userStore.user.friendships?.some(f => (
    f.friend.id === props.chat.user2.id
  ))
));

function handleSubmit() {
  if (!message.value?.trim()) return;
  const messageContent = message.value;

  try {
    chatStore.postMessageToChat(message.value);
    message.value = '';
  } catch (error) {
    message.value = messageContent;
    console.error(error);
  }
}
</script>
