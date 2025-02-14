<template>
  <Dialog v-if="channelStore.selectedChannel" :open="dialogState" @update:open="dialogState = $event">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Remove User from Channel</DialogTitle>
        <DialogDescription>
          Select a member to remove them from the channel.
        </DialogDescription>
      </DialogHeader>

      <Popover v-model:open="popoverState">
        <PopoverTrigger as-child>
          <Button variant="outline" class="min-w-0 justify-between">
            <span class="truncate">
              {{ (selectedUser?.displayname || selectedUser?.username) || 'Select member...' }}
            </span>
            <ChevronsUpDown class="ml-2 h-4 w-4 shrink-0 opacity-50" />
          </Button>
        </PopoverTrigger>

        <PopoverContent class="min-w-0 p-0">
          <Command>
            <CommandInput placeholder="Search by name..." />
            <CommandEmpty>No members found.</CommandEmpty>
            <CommandList>
              <CommandGroup>
                <CommandItem v-for="member in membersThatCanBeRemoved" :key="member.id" :value="member.displayname || member.username" @select="handleSelect(member)">
                  <Check class="shrink-0 opacity-0 h-4 w-4" :class="{ 'opacity-100': selectedUser?.id === member.id }" />
                  <ProfilePicture :user="member" size="sm" :show-status="false" class="mx-2" />
                  <span class="truncate">
                    {{ member.displayname || member.username }}
                  </span>
                </CommandItem>
              </CommandGroup>
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>

      <DialogFooter class="gap-2">
        <Button type="button" variant="secondary" :disabled="loadingStore.isLoading" @click="dialogState = false">
          Cancel
        </Button>
        <Button type="submit" variant="destructive" :disabled="!selectedUser || loadingStore.isLoading" @click="handleSubmit">
          <template v-if="!loadingStore.isLoading">
            Remove User
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
import { Button } from '@/components/ui/button';
import { toast } from '@/components/ui/toast';
import { sortByUsername } from '@/utils';
import { USER_ROLES } from '@/constants';
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
const channelStore = useChannelStore();
const dialogState = ref(props.modelValue);
const popoverState = ref(false);
const selectedUser = ref<UserTable | null>(null);
const errorMessage = ref<string | null>(null);

const membersThatCanBeRemoved = computed(() => {
  const channel = channelStore.selectedChannel!;

  return sortByUsername(channel.members
    .filter(m => m.role !== USER_ROLES.OWNER)
    .map(m => m.user)
  );
});

watch(() => dialogState.value, (value) => {
  emits('update:modelValue', value);
});
watch(() => props.modelValue, (value) => {
  dialogState.value = value;
});

const handleSelect = (target: UserTable | null) => {
  popoverState.value = false;
  selectedUser.value = selectedUser.value?.id === target?.id ? null : target;
};

async function resetState() {
  dialogState.value = false;
  await nextTick();

  popoverState.value = false;
  selectedUser.value = null;
  errorMessage.value = null;
};

async function handleSubmit() {
  errorMessage.value = null;

  const { selectedChannel } = channelStore;
  const target = selectedUser.value;

  if (!selectedChannel) {
    errorMessage.value = 'No channel selected';
    return;
  }
  if (!target) {
    errorMessage.value = 'Please select a user to remove from the channel.';
    return;
  }

  const targetUserName = target.displayname || target.username;

  try {
    await channelStore.removeUserFromChannel(target.id);

    toast({ description: `${targetUserName} has been successfully removed from the "${selectedChannel.name}" channel` });
    resetState();
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : String(error);
  }
};
</script>
