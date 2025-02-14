<template>
  <Dialog v-if="channelStore.selectedChannel" :open="dialogState" @update:open="dialogState = $event">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Leave Channel</DialogTitle>
        <DialogDescription>
          Are you sure you want to leave the "{{ channelStore.selectedChannel.name }}" channel?
          <br>
          You will need to be re-invited to access the channel content again.
        </DialogDescription>
      </DialogHeader>

      <DialogFooter class="gap-2">
        <Button type="button" variant="secondary" :disabled="loadingStore.isLoading" @click="dialogState = false">
          Cancel
        </Button>
        <Button variant="destructive" :disabled="loadingStore.isLoading" @click="handleSubmit">
          <template v-if="!loadingStore.isLoading">
            Leave
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
import { nextTick, ref, watch } from 'vue';
import { AlertCircle, RotateCw } from 'lucide-vue-next';
import { useLoadingStore } from '@/stores/loading';
import { useChannelStore } from '@/pages/channel';
import {
  Dialog, DialogContent, DialogDescription,
  DialogFooter, DialogHeader, DialogTitle
} from '@/components/ui/dialog';
import { Alert, AlertTitle, AlertDescription } from '@/components/ui/alert';
import { Button } from '@/components/ui/button';
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
const errorMessage = ref<string | null>(null);

watch(() => dialogState.value, (value) => {
  emits('update:modelValue', value);
});
watch(() => props.modelValue, (value) => {
  dialogState.value = value;
});

async function resetState() {
  dialogState.value = false;
  await nextTick();

  errorMessage.value = null;
};

async function handleSubmit() {
  const selectedChannel = channelStore.selectedChannel;
  if (!selectedChannel) return;

  const channelName = selectedChannel.name;
  errorMessage.value = null;

  try {
    await channelStore.removeUserFromChannel();

    toast({ description: `You have successfully left the "${channelName}" channel` });
    resetState();
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : String(error);
  }
};
</script>
