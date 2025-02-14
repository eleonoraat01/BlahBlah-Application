<template>
  <div class="w-full">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-semibold">
        Channels
      </h1>
      <Button class="rounded-full" @click="createChannelDialogState = true">
        <Plus class="h-4 w-4 mr-2" />
        New Channel
      </Button>
    </div>

    <ScrollArea class="h-[calc(100vh-8rem)]">
      <div class="grid grid-cols-1 gap-4 pb-8 md:grid-cols-2 lg:grid-cols-3">
        <Card v-for="channel in channelStore.currentUserChannels" :key="channel.id" class="flex flex-col hover:shadow-lg transition-all duration-200">
          <CardHeader class="flex-1">
            <CardTitle class="py-1 truncate">
              #{{ channel.name }}
            </CardTitle>
            <CardDescription v-if="channel.description" class="line-clamp-4 whitespace-pre-wrap break-words">
              {{ channel.description }}
            </CardDescription>
          </CardHeader>

          <CardContent>
            <div class="flex items-center text-sm text-muted-foreground">
              <Users class="h-4 w-4 mr-2" />
              {{ channel.members.length }} members
            </div>
            <div class="text-sm text-muted-foreground mt-1 capitalize">
              Your role: {{ getUserRole(channel) }}
            </div>
          </CardContent>

          <CardFooter>
            <Button class="w-full rounded-full" variant="outline" @click="channelStore.changeSelectedChannel(channel.id)">
              Open Channel
            </Button>
          </CardFooter>
        </Card>
      </div>
    </ScrollArea>
  </div>

  <ChannelViewCreateChannelDialog v-model="createChannelDialogState" />
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { Plus, Users } from 'lucide-vue-next';
import { useUserStore } from '@/stores/user';
import { useChannelStore } from '@/pages/channel';
import {
  Card, CardHeader, CardContent,
  CardTitle, CardDescription, CardFooter
} from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';
import ChannelViewCreateChannelDialog from './dialogs/ChannelViewCreateChannelDialog.vue';
import { USER_ROLES } from '@/constants';
import type { ChannelTable } from '@/types.d';

const userStore = useUserStore();
const channelStore = useChannelStore();
const createChannelDialogState = ref(false);

const getUserRole = (channel: ChannelTable) => {
  const currentUserId = userStore.user.id;

  for (const { user, role } of channel.members) {
    if (user.id === currentUserId) return role;
  }

  return USER_ROLES.MEMBER;
};
</script>
