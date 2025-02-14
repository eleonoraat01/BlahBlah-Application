<template>
  <aside class="flex flex-col w-80 border-r border-border">
    <header class="pb-4 pr-4 border-b border-border">
      <h1 class="text-2xl font-semibold mb-6">
        Messages
      </h1>
      <div class="relative">
        <Label class="sr-only" for="searchBar">Search conversations</Label>
        <Input id="searchBar" v-model="chatStore.searchQuery" placeholder="Search conversations..." class="w-full" />
        <Search class="absolute right-3 top-2.5 h-4 w-4 text-muted-foreground" />
      </div>
    </header>

    <ScrollArea class="grow">
      <ul class="list-none">
        <li v-for="chat in chatStore.activeChats" :key="chat.id" class="p-3 cursor-pointer hover:bg-accent" :class="{ 'bg-accent': chatStore.selectedChat?.id === chat.id }" @click="chatStore.changeSelectedChat(chat.id)">
          <article class="flex items-center gap-3">
            <ProfilePicture :user="chat.user2" :show-status="areMembersFriends(chat)" />
            <article class="flex-1 min-w-0">
              <header class="flex justify-between">
                <h3 class="text-sm font-medium truncate">
                  {{ chat.user2.displayname || chat.user2.username }}
                </h3>
                <time v-if="chat.messages.length" class="text-nowrap text-xs text-muted-foreground">
                  {{ getRelativeTime(getLastMessage(chat).createdAt) }}
                </time>
              </header>
              <p v-if="chat.messages.length" class="text-xs text-muted-foreground truncate">
                {{ getLastMessage(chat).content }}
              </p>
            </article>
          </article>
        </li>
      </ul>
    </ScrollArea>
  </aside>
</template>

<script setup lang="ts">
import { Search } from 'lucide-vue-next';
import { useUserStore } from '@/stores/user';
import { useChatStore } from '@/pages/chat';
import { ProfilePicture } from '@/components/shared';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { getRelativeTime } from '@/utils';
import type { ChatTable } from '@/types.d';

const userStore = useUserStore();
const chatStore = useChatStore();

function getLastMessage(chat: ChatTable) {
  return chat.messages[chat.messages.length - 1];
}

function areMembersFriends(chat: ChatTable) {
  return userStore.user.friendships?.some(f => (
    f.friend.id === chat.user2.id
  ));
}
</script>
