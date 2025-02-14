<template>
  <Dialog :open="!!dialogState" @update:open="dialogState = ''">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>{{ title }}</DialogTitle>
        <DialogDescription>
          Are you sure you want to remove "{{ modelValue }}" from your list?
          <br>
          This action is irreversible. To restore it, you'll need to add it again.
        </DialogDescription>
      </DialogHeader>

      <DialogFooter class="gap-2">
        <Button type="button" variant="secondary" :disabled="loadingStore.isLoading" @click="dialogState = ''">
          Cancel
        </Button>
        <Button variant="destructive" :disabled="loadingStore.isLoading" @click="handleRemove">
          <template v-if="!loadingStore.isLoading">
            Remove
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
import { useUserStore } from '@/stores/user';
import {
  Dialog, DialogContent, DialogDescription,
  DialogFooter, DialogHeader, DialogTitle
} from '@/components/ui/dialog';
import { Alert, AlertTitle, AlertDescription } from '@/components/ui/alert';
import { Button } from '@/components/ui/button';

interface Emits {
  'update:modelValue': [value: string];
}
interface Props {
  modelValue: string;
  type: 'friendships' | 'interests' | 'socialLinks'
  title: string;
}

const loadingStore = useLoadingStore();
const userStore = useUserStore();
const emits = defineEmits<Emits>();
const props = defineProps<Props>();

const dialogState = ref(props.modelValue);
const errorMessage = ref<string | null>(null);

watch(() => dialogState.value, (value) => {
  emits('update:modelValue', value);
});
watch(() => props.modelValue, (value) => {
  dialogState.value = value;
});

async function resetState() {
  dialogState.value = '';
  await nextTick();

  errorMessage.value = null;
};

async function handleRemove() {
  errorMessage.value = null;

  try {
    await processUserDataRemoval();
    resetState();
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : String(error);
  }
}

async function processUserDataRemoval() {
  const { type, modelValue } = props;

  switch (type) {
    case 'friendships': {
      await userStore.removeFriend(Number(modelValue));
      break;
    }

    case 'interests': {
      const updatedData = userStore.user.interests?.filter(item => item.interest !== modelValue);
      await userStore.updateCurrentUserData({ interests: updatedData });
      break;
    }

    case 'socialLinks': {
      const updatedData = userStore.user.socialLinks?.filter(item => item.url !== modelValue);
      await userStore.updateCurrentUserData({ socialLinks: updatedData });
      break;
    }

    default: {
      throw new Error(`Unsupported removal type: ${type}`);
    }
  }
}
</script>
