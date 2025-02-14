<template>
  <div v-if="profileStore.selectedUser" class="relative -mt-16 pb-8">
    <div class="flex flex-col items-center space-y-4 sm:flex-row sm:items-end sm:space-y-0">
      <div class="relative">
        <ProfilePicture :user="profileStore.selectedUser" :show-status="false" :size="'full'" :shape="'square'" class="h-32 w-32 ring-4 ring-background" />
        <Button v-if="profileStore.isCurrentUser" size="sm" variant="secondary" class="absolute bottom-0 right-0" @click="shouldShowImageDialog = true">
          <Camera class="h-4 w-4" />
        </Button>
      </div>

      <div class="flex-1 flex items-center justify-between sm:ml-6 sm:flex-row sm:items-end ">
        <div class="text-center sm:text-left">
          <h1 class="text-2xl font-bold text-foreground">
            {{ profileStore.selectedUser.displayname || profileStore.selectedUser.username }}
          </h1>
          <p v-if="profileStore.selectedUser.bio" class="text-muted-foreground mt-1">
            {{ profileStore.selectedUser.bio }}
          </p>
        </div>

        <div v-if="!profileStore.isCurrentUser" class="flex gap-2 mt-4 sm:mt-0">
          <template v-if="profileStore.isSelectedFriend">
            <Button variant="outline" @click="selectedFriendIdToRemove = profileStore.selectedUser.id">
              <UserMinus class="h-4 w-4 mr-2" />
              Unfriend
            </Button>
            <Button variant="secondary" @click="chatStore.openChatWithUser(profileStore.selectedUser.id)">
              <MessageCircle class="h-4 w-4 mr-2" />
              Message
            </Button>
          </template>
          <Button v-else :disabled="loadingStore.isLoading" @click="handleAddFriend">
            <template v-if="!loadingStore.isLoading">
              <UserPlus class="h-4 w-4 mr-2" />
              Add Friend
            </template>
            <template v-else>
              <RotateCw class="w-4 h-4 mr-2 animate-spin" />
              Please wait
            </template>
          </Button>
        </div>
      </div>
    </div>
  </div>

  <ProfileViewChangeImageDialog
    v-model="shouldShowImageDialog"
    type="profilePictureUrl"
  />
  <ProfileViewRemoveItemDialog
    v-model="selectedFriendIdToRemove"
    type="friendships"
    :title="`Remove ${getName()} from your friends`"
  />
</template>

<script setup lang="ts">
import { ref, toRaw } from 'vue';
import { Camera, MessageCircle, UserPlus, UserMinus, RotateCw } from 'lucide-vue-next';
import { useLoadingStore } from '@/stores/loading';
import { useUserStore } from '@/stores/user';
import { useProfileStore } from '@/pages/profile';
import { useChatStore } from '@/pages/chat';
import { ProfilePicture } from '@/components/shared';
import { Button } from '@/components/ui/button';
import { toast } from '@/components/ui/toast';
import ProfileViewChangeImageDialog from './dialogs/ProfileViewChangeImageDialog.vue';
import ProfileViewRemoveItemDialog from './dialogs/ProfileViewRemoveItemDialog.vue';

const loadingStore = useLoadingStore();
const userStore = useUserStore();
const profileStore = useProfileStore();
const chatStore = useChatStore();

const shouldShowImageDialog = ref(false);
const selectedFriendIdToRemove = ref<string>('');

async function handleAddFriend() {
  if (!profileStore.selectedUser) return;

  try {
    await userStore.addFriend(toRaw(profileStore.selectedUser));
    toast({ description: 'Friend added successfully' });
  } catch (error) {
    const message = error instanceof Error ? error.message : String(error);
    toast({ title: 'Error', description: message, variant: 'destructive' });
  }
}

function getName() {
  const user = userStore.user.friendships?.find(f => (
    f.friend.id === Number(selectedFriendIdToRemove.value)
  ))?.friend;

  return user ? (user.displayname || user.username) : '';
}
</script>
