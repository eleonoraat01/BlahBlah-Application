<template>
  <Card class="flex flex-col hover:shadow-lg transition-shadow min-w-0 max-h-80">
    <CardHeader>
      <CardTitle class="flex items-center">
        <MessageSquare class="h-5 w-5 mr-2" />
        Active Channels
      </CardTitle>
      <CardDescription>
        Your most active conversations
      </CardDescription>
    </CardHeader>
    <ScrollArea>
      <CardContent>
        <ul class="list-none space-y-2">
          <li v-for="channel in homeStore.activeChannels" :key="channel.id" class="cursor-pointer flex items-center justify-between gap-2 p-2 rounded-lg hover:bg-accent" @click="channelStore.changeSelectedChannel(channel.id)">
            <span class="text-sm truncate"># {{ channel.name }}</span>
            <Badge>{{ channel.messages.filter(m => !m.isSpecial).length }}</Badge>
          </li>
        </ul>
      </CardContent>
    </ScrollArea>
  </Card>
</template>

<script setup lang="ts">
import { MessageSquare } from 'lucide-vue-next';
import { useHomeStore } from '@/pages/home';
import { useChannelStore } from '@/pages/channel';
import { ScrollArea } from '@/components/ui/scroll-area';
import {
  Card, CardContent,
  CardDescription,
  CardHeader, CardTitle
} from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';

const homeStore = useHomeStore();
const channelStore = useChannelStore();
</script>
