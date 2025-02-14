<template>
  <Card class="flex flex-col hover:shadow-lg transition-shadow min-w-0 max-h-80">
    <CardHeader>
      <CardTitle class="flex items-center">
        <Bell class="h-5 w-5 mr-2" />
        Recent Notifications
      </CardTitle>
      <CardDescription>
        Your latest updates
      </CardDescription>
    </CardHeader>
    <ScrollArea>
      <CardContent>
        <ul class="list-none space-y-2">
          <li v-for="notification in homeStore.recentNotifications" :key="notification.id" class="cursor-pointer p-2 rounded-lg hover:bg-accent text-sm" @click="chatStore.changeSelectedChat(notification.chatId)">
            <p class="space-x-2 truncate">
              <span class="text-muted-foreground">From</span>
              <span class="font-medium">{{ notification.sender.displayname || notification.sender.username }}</span>
            </p>
            <p class="mx-3 truncate">
              {{ notification.content }}
            </p>
            <time :datetime="getDateISOString(notification.createdAt)" class="text-nowrap text-xs text-muted-foreground">
              {{ getRelativeTime(notification.createdAt) }}
            </time>
          </li>
        </ul>
      </CardContent>
    </ScrollArea>
  </Card>
</template>

<script setup lang="ts">
import { Bell } from 'lucide-vue-next';
import { useHomeStore } from '@/pages/home';
import { useChatStore } from '@/pages/chat';
import { ScrollArea } from '@/components/ui/scroll-area';
import {
  Card, CardContent,
  CardDescription,
  CardHeader, CardTitle
} from '@/components/ui/card';
import { getDateISOString, getRelativeTime } from '@/utils';

const homeStore = useHomeStore();
const chatStore = useChatStore();
</script>
