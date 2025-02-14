<template>
  <Card class="flex flex-col min-w-0">
    <CardHeader>
      <CardTitle>Recent Activity</CardTitle>
      <CardDescription>
        Latest messages and updates across your channels
      </CardDescription>
    </CardHeader>
    <CardContent>
      <ul class="list-none space-y-4">
        <li v-for="activity in homeStore.recentActivity" :key="activity.id" class="cursor-pointer flex items-start gap-4 p-2 rounded-lg hover:bg-accent" @click="channelStore.changeSelectedChannel(activity.channelId)">
          <ProfilePicture :user="activity.sender" :show-status="false" size="sm" />
          <article class="flex-1 space-y-1 min-w-0">
            <header class="flex items-center">
              <p class="flex space-x-2 min-w-0">
                <span class="font-medium truncate">{{ activity.sender.displayname || activity.sender.username }}</span>
                <span class="mx-2 text-muted-foreground">in</span>
                <span class="font-medium truncate"># {{ activity.title }}</span>
              </p>
            </header>
            <p class="text-sm text-muted-foreground truncate">
              {{ activity.content }}
            </p>
            <time :datetime="getDateISOString(activity.createdAt)" class="text-nowrap text-xs text-muted-foreground">
              {{ getRelativeTime(activity.createdAt) }}
            </time>
          </article>
        </li>
      </ul>
    </CardContent>
  </Card>
</template>

<script setup lang="ts">
import { useHomeStore } from '@/pages/home';
import { useChannelStore } from '@/pages/channel';
import { getDateISOString, getRelativeTime } from '@/utils';
import {
  Card, CardContent,
  CardDescription,
  CardHeader, CardTitle
} from '@/components/ui/card';
import { ProfilePicture } from '@/components/shared';

const homeStore = useHomeStore();
const channelStore = useChannelStore();
</script>
