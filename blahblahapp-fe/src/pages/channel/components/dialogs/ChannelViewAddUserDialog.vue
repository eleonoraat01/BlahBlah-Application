<template>
  <Dialog v-if="channelStore.selectedChannel" :open="dialogState" @update:open="dialogState = $event">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Add User to Channel</DialogTitle>
        <DialogDescription>
          Select a friend from your list or enter a User ID to add them to the channel.
        </DialogDescription>
      </DialogHeader>

      <Popover v-model:open="popoverState">
        <PopoverTrigger as-child>
          <Button :disabled="!!inputValue.trim()" variant="outline" class="min-w-0 justify-between">
            <span class="truncate">
              {{ (selectedUser?.displayname || selectedUser?.username) || 'Select friend...' }}
            </span>
            <ChevronsUpDown class="ml-2 h-4 w-4 shrink-0 opacity-50" />
          </Button>
        </PopoverTrigger>

        <PopoverContent class=" min-w-0 p-0">
          <Command>
            <CommandInput placeholder="Search by name..." />
            <CommandEmpty>No friends found.</CommandEmpty>
            <CommandList>
              <CommandGroup>
                <CommandItem v-for="friend in remainingFriends" :key="friend.id" :value="friend.displayname || friend.username" @select="handleSelect(friend)">
                  <Check class="shrink-0 opacity-0 h-4 w-4" :class="{ 'opacity-100': selectedUser?.id === friend.id }" />
                  <ProfilePicture :user="friend" size="sm" :show-status="false" class="mx-2" />
                  <span class="truncate">
                    {{ friend.displayname || friend.username }}
                  </span>
                </CommandItem>
              </CommandGroup>
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>
      <Separator class="my-2" label="Or" />

      <Label class="sr-only" for="userId">Enter User ID</Label>
      <Input id="userId" v-model="inputValue" :disabled="!!selectedUser" placeholder="Enter User ID" @keydown.enter.prevent="handleSubmit" />

      <DialogFooter class="gap-2">
        <Button type="button" variant="secondary" :disabled="loadingStore.isLoading" @click="dialogState = false">
          Cancel
        </Button>
        <Button type="submit" :disabled="(!selectedUser && !inputValue.trim()) || loadingStore.isLoading" @click="handleSubmit">
          <template v-if="!loadingStore.isLoading">
            Add User
          </template>
          <template v-else>
            <RotateCw class="w-4 h-4 mr-2 animate-spin" />
            Please wait
          </template>
        </Button>
      </DialogFooter>

      <Alert v-if="errorMessage" variant="destructive" class="mt-4">
        <AlertCircle class="h-4 w-4" />
        <AlertTitle>Error</AlertTitle>
        <AlertDescription>{{ errorMessage }}</AlertDescription>
      </Alert>
    </DialogContent>
  </Dialog>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, computed } from 'vue';
import { AlertCircle, Check, ChevronsUpDown, RotateCw } from 'lucide-vue-next';
import { useLoadingStore } from '@/stores/loading';
import { useUserStore } from '@/stores/user';
import { useChannelStore } from '@/pages/channel';
import { ProfilePicture } from '@/components/shared';
import {
  Dialog, DialogContent, DialogDescription,
  DialogFooter, DialogHeader, DialogTitle
} from '@/components/ui/dialog';
import {
  Popover, PopoverContent, PopoverTrigger
} from '@/components/ui/popover';
import {
  Command, CommandInput, CommandList,
  CommandEmpty, CommandGroup, CommandItem
} from '@/components/ui/command';
import { Alert, AlertTitle, AlertDescription } from '@/components/ui/alert';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { toast } from '@/components/ui/toast';
import { sortByUsername } from '@/utils';
import type { UserTable } from '@/types.d';

interface Emits {
  'update:modelValue': [value: boolean];
}
interface Props {
  modelValue: boolean;
}

const emits = defineEmits<Emits>();
const props = defineProps<Props>();

const loadingStore = useLoadingStore();
const userStore = useUserStore();
const channelStore = useChannelStore();
const dialogState = ref(props.modelValue);
const popoverState = ref(false);
const selectedUser = ref<UserTable | null>(null);
const inputValue = ref('');
const errorMessage = ref<string | null>(null);

const remainingFriends = computed(() => {
  const members = channelStore.selectedChannel?.members || [];

  return sortByUsername((userStore.user.friendships || [])
    .filter(f => !members.some(m => m.user.id === f.friend.id))
    .map(f => f.friend)
  );
});

watch(() => dialogState.value, (value) => {
  emits('update:modelValue', value);
});
watch(() => props.modelValue, (value) => {
  dialogState.value = value;
});

const handleSelect = (target: UserTable | null) => {
  selectedUser.value = selectedUser.value?.id === target?.id ? null : target;
};

async function resetState() {
  dialogState.value = false;
  await nextTick();

  inputValue.value = '';
  popoverState.value = false;
  selectedUser.value = null;
  errorMessage.value = null;
};

async function handleSubmit() {
  errorMessage.value = null;

  const { selectedChannel } = channelStore;
  const target = selectedUser.value || Number(inputValue.value.trim());

  if (!selectedChannel) {
    errorMessage.value = 'No channel selected';
    return;
  }
  if (!selectedUser.value && isNaN(Number(target))) {
    errorMessage.value = 'Please select a user to add to the channel.';
    return;
  }

  try {
    const { user } = await channelStore.addUserToChannel(target);

    toast({ description: `${user.displayname || user.username} has been successfully added to the "${selectedChannel.name}" channel` });
    resetState();
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : String(error);
  }
};
</script>
