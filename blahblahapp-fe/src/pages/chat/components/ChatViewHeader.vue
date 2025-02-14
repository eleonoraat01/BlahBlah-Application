<template>
  <section class="p-4 space-x-2 border-b border-border flex items-center justify-between">
    <header class="flex items-center gap-4 min-w-0">
      <ProfilePicture :user="chat.user2" :show-status="areMembersFriends" />
      <article class="min-w-0">
        <h2 class="font-medium truncate">
          {{ chat.user2.displayname || chat.user2.username }}
        </h2>
        <p v-if="chat.user2.status && areMembersFriends" class="text-xs text-muted-foreground capitalize">
          {{ chat.user2.status }}
        </p>
      </article>
    </header>
    <Button variant="ghost" size="icon" class="shrink-0 rounded-full" @click="chatStore.resetSelectedChat">
      <X class="text-muted-foreground" />
    </Button>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { X } from 'lucide-vue-next';
import { useUserStore } from '@/stores/user';
import { useChatStore } from '@/pages/chat';
import { ProfilePicture } from '@/components/shared';
import { Button } from '@/components/ui/button';
import type { ChatTable } from '@/types.d';

const props = defineProps<{ chat: ChatTable }>();

const userStore = useUserStore();
const chatStore = useChatStore();

const areMembersFriends = computed(() => (
  userStore.user.friendships?.some(f => (
    f.friend.id === props.chat.user2.id
  ))
));
</script>
