<template>
  <div v-if="profileStore.selectedUser" class="relative h-48 bg-muted">
    <img v-if="profileStore.selectedUser.coverPictureUrl" :src="profileStore.selectedUser.coverPictureUrl" alt="Cover" class="w-full h-full object-cover">
    <div v-else class="w-full h-full bg-inherit" />
    <Button v-if="profileStore.isCurrentUser" size="sm" variant="secondary" class="z-10 absolute bottom-4 right-4" @click="shouldShowImageDialog = true">
      <Camera class="h-4 w-4 mr-2" />
      {{ profileStore.selectedUser.coverPictureUrl ? 'Change Cover' : 'Add Cover' }}
    </Button>
  </div>

  <ProfileViewChangeImageDialog v-model="shouldShowImageDialog" type="coverPictureUrl" />
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { Camera } from 'lucide-vue-next';
import { useProfileStore } from '@/pages/profile';
import { Button } from '@/components/ui/button';
import ProfileViewChangeImageDialog from './dialogs/ProfileViewChangeImageDialog.vue';

const profileStore = useProfileStore();
const shouldShowImageDialog = ref(false);
</script>
