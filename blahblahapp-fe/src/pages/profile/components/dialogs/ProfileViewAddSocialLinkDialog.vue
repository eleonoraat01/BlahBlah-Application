<template>
  <Dialog :open="dialogState" @update:open="dialogState = $event">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Add Social Link</DialogTitle>
        <DialogDescription>
          Add a new social media profile link
        </DialogDescription>
      </DialogHeader>

      <div class="space-y-2">
        <Label for="platform">Platform</Label>
        <Select v-model="selectValue" :disabled="loadingStore.isLoading" required>
          <SelectTrigger id="platform">
            <SelectValue placeholder="Select a platform" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem v-for="platform in remainingSocialLinks" :key="platform" :value="platform">
              <div class="flex items-center">
                <component :is="getSocialIcon(platform)" class="h-4 w-4 mr-2" />
                <span class="capitalize">{{ platform }}</span>
              </div>
            </SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div class="space-y-2">
        <Label for="addSocialLink">URL</Label>
        <Input id="addSocialLink" v-model="inputValue" type="url" :placeholder="getUrlPlaceholder(selectValue)" :disabled="loadingStore.isLoading" @keydown.enter.prevent="handleSubmit" />
      </div>

      <DialogFooter class="gap-2">
        <Button type="button" variant="secondary" :disabled="loadingStore.isLoading" @click="dialogState = false">
          Cancel
        </Button>
        <Button type="submit" :disabled="!inputValue.trim() || loadingStore.isLoading" @click="handleSubmit">
          <template v-if="!loadingStore.isLoading">
            Add Link
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
import { useUserStore } from '@/stores/user';
import {
  Dialog, DialogContent, DialogDescription,
  DialogFooter, DialogHeader, DialogTitle
} from '@/components/ui/dialog';
import {
  Select, SelectContent, SelectItem,
  SelectTrigger, SelectValue,
} from '@/components/ui/select';
import { Alert, AlertTitle, AlertDescription } from '@/components/ui/alert';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { DEFAULT_SOCIAL_LINKS_TYPE, AVAILABLE_SOCIAL_LINKS_TYPES } from '@/constants';
import { compareURLs, getSocialIcon, getUrlPlaceholder } from '../../utils/socialLinks';
import type { ProfileViewSocialLinkType } from '@/types.d';

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
const selectValue = ref<ProfileViewSocialLinkType>(DEFAULT_SOCIAL_LINKS_TYPE);
const errorMessage = ref<string | null>(null);

const remainingSocialLinks = computed(() => {
  const usedSocialLinks = (userStore.user.socialLinks || [])
    .map(link => link.platform);

  const availableSocialLink = AVAILABLE_SOCIAL_LINKS_TYPES.filter(
    type => type !== DEFAULT_SOCIAL_LINKS_TYPE && !usedSocialLinks.includes(type)
  );

  return [DEFAULT_SOCIAL_LINKS_TYPE, ...availableSocialLink];
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

  inputValue.value = '';
  errorMessage.value = null;
};

async function handleSubmit() {
  inputValue.value = inputValue.value.trim();
  errorMessage.value = null;

  if (!inputValue.value) {
    errorMessage.value = 'Please enter a social link to add';
    return;
  }

  const userSocialLinks = userStore.user.socialLinks || [];

  if (userSocialLinks.some(link => (
    compareURLs(link.url, inputValue.value)
  ))) {
    errorMessage.value = `This ${selectValue.value} social link already exists`;
    return;
  }

  try {
    await userStore.updateCurrentUserData({
      socialLinks: [...userSocialLinks, { platform: selectValue.value, url: inputValue.value }],
    });

    resetState();
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : String(error);
  }
};
</script>
