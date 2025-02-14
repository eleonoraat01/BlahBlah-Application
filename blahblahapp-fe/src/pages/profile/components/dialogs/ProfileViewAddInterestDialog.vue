<template>
  <Dialog :open="dialogState" @update:open="dialogState = $event">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Add New Interest</DialogTitle>
        <DialogDescription>
          Enter a new interest to add to your profile
        </DialogDescription>
      </DialogHeader>

      <Label class="sr-only" for="addInterest">Enter your interest</Label>
      <Input id="addInterest" v-model="inputValue" type="text" placeholder="Enter your interest" :disabled="loadingStore.isLoading" @keydown.enter.prevent="handleSubmit" />

      <DialogFooter class="gap-2">
        <Button type="button" variant="secondary" :disabled="loadingStore.isLoading" @click="dialogState = false">
          Cancel
        </Button>
        <Button type="submit" :disabled="!inputValue.trim() || loadingStore.isLoading" @click="handleSubmit">
          <template v-if="!loadingStore.isLoading">
            Add Interest
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
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';

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
const dialogState = ref(props.modelValue);
const inputValue = ref('');
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

  inputValue.value = '';
  errorMessage.value = null;
};

async function handleSubmit() {
  inputValue.value = inputValue.value.trim();
  errorMessage.value = null;

  if (!inputValue.value) {
    errorMessage.value = 'Please enter an interest to add';
    return;
  }

  const userInterests = userStore.user.interests || [];

  if (userInterests.some(({ interest }) => (
    interest.toLowerCase() === inputValue.value.toLowerCase()
  ))) {
    errorMessage.value = `This "${inputValue.value}" interest already exists`;
    return;
  }

  try {
    await userStore.updateCurrentUserData({
      interests: [...userInterests, { interest: inputValue.value }]
    });

    resetState();
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : String(error);
  }
};
</script>
