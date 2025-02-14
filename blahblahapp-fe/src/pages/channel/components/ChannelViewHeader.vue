<template>
  <div class="p-6 space-x-2 border-b border-border flex items-center justify-between">
    <div class="flex items-center gap-3 min-w-0">
      <Button variant="ghost" size="icon" class="shrink-0 rounded-full" @click="channelStore.resetSelectedChannel">
        <ChevronLeft class="h-4 w-4" />
      </Button>

      <div class="space-y-1 min-w-0">
        <h1 class="text-xl font-bold truncate">
          #{{ channel.name }}
        </h1>
        <p class="text-sm text-muted-foreground truncate">
          Created by {{ channelStore.selectedChannelOwner?.displayname || channelStore.selectedChannelOwner?.username }}
        </p>
      </div>
    </div>

    <DropdownMenu>
      <DropdownMenuTrigger as-child>
        <Button variant="ghost" size="icon" class="shrink-0 rounded-full">
          <MoreHorizontal class="h-4 w-4" />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <DropdownMenuItem v-if="channelStore.selectedChannelUserRole !== USER_ROLES.MEMBER" @click="addUserDialogState = true">
          <UserPlus />
          <span>Add User</span>
        </DropdownMenuItem>
        <DropdownMenuItem v-if="channelStore.selectedChannelUserRole === USER_ROLES.OWNER" @click="removeUserDialogState = true">
          <UserMinus />
          <span>Remove User</span>
        </DropdownMenuItem>
        <DropdownMenuItem v-if="channelStore.selectedChannelUserRole !== USER_ROLES.MEMBER" @click="renameChannelDialogState = true">
          <Pen />
          <span>Rename Channel</span>
        </DropdownMenuItem>
        <DropdownMenuItem v-if="channelStore.selectedChannelUserRole !== USER_ROLES.OWNER" @click="leaveChannelDialogState = true">
          <LogOut />
          <span>Leave Channel</span>
        </DropdownMenuItem>
        <DropdownMenuItem v-if="channelStore.selectedChannelUserRole === USER_ROLES.OWNER" class="text-destructive focus:text-destructive" @click="deleteChannelDialogState = true">
          <Trash2 />
          <span>Delete Channel</span>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  </div>

  <ChannelViewAddUserDialog v-model="addUserDialogState" />
  <ChannelViewRemoveUserDialog v-model="removeUserDialogState" />
  <ChannelViewRenameChannelDialog v-model="renameChannelDialogState" />
  <ChannelViewLeaveChannelDialog v-model="leaveChannelDialogState" />
  <ChannelViewDeleteChannelDialog v-model="deleteChannelDialogState" />
</template>

<script setup lang="ts">
import { ref } from 'vue';
import {
  UserPlus, UserMinus, Pen, LogOut,
  MoreHorizontal, ChevronLeft, Trash2,
} from 'lucide-vue-next';
import { useChannelStore } from '@/pages/channel';
import {
  DropdownMenu, DropdownMenuContent,
  DropdownMenuItem, DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Button } from '@/components/ui/button';
import ChannelViewAddUserDialog from './dialogs/ChannelViewAddUserDialog.vue';
import ChannelViewRemoveUserDialog from './dialogs/ChannelViewRemoveUserDialog.vue';
import ChannelViewRenameChannelDialog from './dialogs/ChannelViewRenameChannelDialog.vue';
import ChannelViewLeaveChannelDialog from './dialogs/ChannelViewLeaveChannelDialog.vue';
import ChannelViewDeleteChannelDialog from './dialogs/ChannelViewDeleteChannelDialog.vue';
import { USER_ROLES } from '@/constants';
import type { ChannelTable } from '@/types.d';

defineProps<{ channel: ChannelTable }>();

const channelStore = useChannelStore();
const addUserDialogState = ref(false);
const removeUserDialogState = ref(false);
const renameChannelDialogState = ref(false);
const leaveChannelDialogState = ref(false);
const deleteChannelDialogState = ref(false);
</script>
