<template>
  <Dialog :open="dialogState" @update:open="dialogState = $event">
    <DialogContent>
      <DialogHeader>
        <DialogTitle class="capitalize">
          Update {{ imageText }}
        </DialogTitle>
        <DialogDescription>
          Enter a secure URL for your new {{ imageText.toLowerCase() }}
        </DialogDescription>
      </DialogHeader>

      <div class="space-y-2">
        <Label :for="props.type">Image URL</Label>
        <Input :id="props.type" v-model="inputValue" type="url" placeholder="https://example.com/image" :disabled="loadingStore.isLoading" @keydown.enter.prevent="handleImageUpdate" />
      </div>

      <p class="text-sm text-muted-foreground">
        Supported formats: JPG, JPEG, PNG, WEBP, GIF. Must be a secure HTTPS URL.
      </p>

      <DialogFooter class="gap-2">
        <Button v-if="!!currentImageSrc" variant="destructive" :disabled="loadingStore.isLoading" @click="handleImageRemove">
          Remove Photo
        </Button>
        <Button type="button" variant="secondary" :disabled="loadingStore.isLoading" @click="dialogState = false">
          Cancel
        </Button>
        <Button type="submit" :disabled="!inputValue.trim() || loadingStore.isLoading" @click="handleImageUpdate">
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
import { ref, watch, computed, nextTick } from 'vue';
import { AlertCircle, RotateCw } from 'lucide-vue-next';
import { useLoadingStore } from '@/stores/loading';
import { useUserStore } from '@/stores/user';
import { useProfileStore } from '@/pages/profile';
import {
  Dialog, DialogContent, DialogDescription,
  DialogFooter, DialogHeader, DialogTitle
} from '@/components/ui/dialog';
import { Alert, AlertTitle, AlertDescription } from '@/components/ui/alert';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { toast } from '@/components/ui/toast';
import type { ImageUpdatePayload } from '@/pages/profile/stores/profile';

const loadingStore = useLoadingStore();
const userStore = useUserStore();
const profileStore = useProfileStore();

interface Emits {
  'update:modelValue': [value: boolean];
}
interface Props {
  modelValue: boolean;
  type: ImageUpdatePayload['type'];
}

const emits = defineEmits<Emits>();
const props = defineProps<Props>();

const dialogState = ref(props.modelValue);
const inputValue = ref(userStore.user[props.type] || '');
const errorMessage = ref<string | null>(null);

const currentImageSrc = computed(() => {
  return userStore.user[props.type];
});

const imageText = computed(() => {
  return props.type === 'profilePictureUrl'
    ? 'Profile picture'
    : 'Cover photo';
});

watch(() => dialogState.value, (value) => {
  emits('update:modelValue', value);
});
watch(() => props.modelValue, (value) => {
  dialogState.value = value;
});

async function resetState() {
  dialogState.value = false;
  await nextTick();

  inputValue.value = userStore.user[props.type] || '';
  errorMessage.value = null;
};

async function handleImageUpdate() {
  inputValue.value = inputValue.value.trim();
  errorMessage.value = null;

  if (!inputValue.value) {
    errorMessage.value = 'Please enter a valid image URL';
    return;
  }

  try {
    await profileStore.updateImage({
      type: props.type,
      url: inputValue.value
    });

    toast({ description: `${imageText.value} updated successfully` });
    resetState();
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : String(error);
  }
}

async function handleImageRemove() {
  errorMessage.value = null;

  try {
    await profileStore.updateImage({ type: props.type, url: '' });

    toast({ description: `${imageText.value} removed successfully` });
    resetState();
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : String(error);
  }
}
</script>
