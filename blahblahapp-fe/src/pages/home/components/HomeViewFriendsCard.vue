<template>
  <Card class="flex flex-col hover:shadow-lg transition-shadow min-w-0 max-h-80">
    <CardHeader>
      <CardTitle class="flex items-center">
        <Users class="h-5 w-5 mr-2" />
        Online Friends
      </CardTitle>
      <CardDescription>
        Friends currently active
      </CardDescription>
    </CardHeader>
    <ScrollArea>
      <CardContent>
        <ul class="list-none space-y-2">
          <li v-for="friend in currentUserOnlineFriends" :key="friend.id" class="cursor-pointer flex items-center p-2 rounded-lg hover:bg-accent" @click="chatStore.openChatWithUser(friend.id)">
            <ProfilePicture :user="friend" size="sm" class="mr-2" />
            <span class="text-sm truncate">{{ friend.displayname || friend.username }}</span>
          </li>
        </ul>
      </CardContent>
    </ScrollArea>
  </Card>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { Users } from 'lucide-vue-next';
import { useUserStore } from '@/stores/user';
import { useChatStore } from '@/pages/chat';
import { ProfilePicture } from '@/components/shared';
import { ScrollArea } from '@/components/ui/scroll-area';
import {
  Card, CardContent,
  CardDescription,
  CardHeader, CardTitle
} from '@/components/ui/card';
import { getOnlineUsers, sortByStatus } from '@/utils';

const userStore = useUserStore();
const chatStore = useChatStore();

const currentUserOnlineFriends = computed(() => (
  sortByStatus(getOnlineUsers(userStore.user.friendships?.map(f => f.friend)))
));
</script>
