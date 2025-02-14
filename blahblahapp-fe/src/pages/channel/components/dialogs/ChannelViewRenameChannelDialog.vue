<template>
  <Dialog v-if="channelStore.selectedChannel" :open="dialogState" @update:open="dialogState = $event">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Update Channel</DialogTitle>
        <DialogDescription>
          Update the name and/or description of your channel.
        </DialogDescription>
      </DialogHeader>

      <div class="grid gap-4 py-4">
        <Label class="sr-only" for="channelName">New Channel Name</Label>
        <Input id="channelName" v-model="updatedChannelName" :disabled="loadingStore.isLoading" placeholder="Channel Name" class="rounded-lg" />
        <Label class="sr-only" for="channelDescription">New Channel Description</Label>
        <Textarea id="channelDescription" v-model="updatedChannelDescription" :disabled="loadingStore.isLoading" placeholder="Channel Description" class="rounded-lg min-h-20" />
      </div>

      <DialogFooter class="gap-2">
        <Button type="button" variant="secondary" :disabled="loadingStore.isLoading" @click="dialogState = false">
          Cancel
        </Button>
        <Button type="submit" :disabled="!isFormChanged || !updatedChannelName.trim() || loadingStore.isLoading" @click="handleSubmit">
          <template v-if="!loadingStore.isLoading">
            Update
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
import { computed, nextTick, ref, watch } from 'vue';
import { AlertCircle, RotateCw } from 'lucide-vue-next';
import { useLoadingStore } from '@/stores/loading';
import { useChannelStore } from '@/pages/channel';
import {
  Dialog, DialogContent, DialogDescription,
  DialogFooter, DialogHeader, DialogTitle
} from '@/components/ui/dialog';
import { Alert, AlertTitle, AlertDescription } from '@/components/ui/alert';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import { toast } from '@/components/ui/toast';

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
const updatedChannelName = ref(channelStore.selectedChannel?.name || '');
const updatedChannelDescription = ref(channelStore.selectedChannel?.description || '');
const errorMessage = ref<string | null>(null);

const isFormChanged = computed(() => {
  const newName = updatedChannelName.value.trim();
  const oldName = channelStore.selectedChannel?.name.trim();
  const newDesc = updatedChannelDescription.value.trim();
  const oldDesc = channelStore.selectedChannel?.description?.trim() || '';

  return newName !== oldName || newDesc !== oldDesc;
});

watch(() => dialogState.value, (value) => {
  emits('update:modelValue', value);

  if (value && channelStore.selectedChannel && !loadingStore.isLoading) {
    updatedChannelName.value = channelStore.selectedChannel.name || '';
    updatedChannelDescription.value = channelStore.selectedChannel.description || '';
  }
});
watch(() => props.modelValue, (value) => {
  dialogState.value = value;
});

async function resetState() {
  dialogState.value = false;
  await nextTick();

  updatedChannelName.value = channelStore.selectedChannel?.name || '';
  updatedChannelDescription.value = channelStore.selectedChannel?.description || '';
  errorMessage.value = null;
};

async function handleSubmit() {
  errorMessage.value = null;

  if (!isFormChanged.value) {
    errorMessage.value = 'No changes detected';
    return;
  }

  try {
    await channelStore.updateSelectedChannel({
      name: updatedChannelName.value.trim(),
      description: updatedChannelDescription.value.trim()
    });

    toast({ description: 'Channel information updated successfully' });
    resetState();
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : String(error);
  }
};
</script>
