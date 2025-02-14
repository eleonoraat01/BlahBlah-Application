<template>
  <Dialog :open="dialogState" @update:open="dialogState = $event">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Create New Channel</DialogTitle>
        <DialogDescription>
          Create a new channel to start chatting with others.
        </DialogDescription>
      </DialogHeader>

      <div class="grid gap-4 py-4">
        <Label class="sr-only" for="channelName">Channel Name</Label>
        <Input id="channelName" v-model="newChannelName" placeholder="Channel Name" class="rounded-lg" />
        <Label class="sr-only" for="channelDescription">Channel Description</Label>
        <Textarea id="channelDescription" v-model="newChannelDescription" placeholder="Channel Description" class="rounded-lg min-h-20" />
      </div>

      <DialogFooter class="gap-2">
        <Button type="button" variant="secondary" :disabled="loadingStore.isLoading" @click="dialogState = false">
          Cancel
        </Button>
        <Button type="submit" :disabled="!newChannelName.trim() || loadingStore.isLoading" @click="handleSubmit">
          <template v-if="!loadingStore.isLoading">
            Create
          </template>
          <template v-else>
            <RotateCw class="w-4 h-4 mr-2 animate-spin" />
            Please wait
          </template>
        </Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>

<script setup lang="ts">
import { nextTick, ref, watch } from 'vue';
import { RotateCw } from 'lucide-vue-next';
import { useLoadingStore } from '@/stores/loading';
import { useChannelStore } from '@/pages/channel';
import {
  Dialog, DialogContent, DialogHeader,
  DialogTitle, DialogDescription, DialogFooter
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
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
const newChannelName = ref('');
const newChannelDescription = ref('');
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

  newChannelName.value = '';
  newChannelDescription.value = '';
  errorMessage.value = null;
};

async function handleSubmit() {
  newChannelName.value = newChannelName.value.trim();
  newChannelDescription.value = newChannelDescription.value.trim();
  errorMessage.value = null;

  if (!newChannelName.value) {
    errorMessage.value = 'Please enter a channel name';
    return;
  }

  try {
    await channelStore.initializeNewChannel({
      name: newChannelName.value,
      description: newChannelDescription.value,
    });

    toast({ description: `You have successfully created the "${newChannelName.value}" channel` });
    resetState();
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : String(error);
  }
};
</script>
