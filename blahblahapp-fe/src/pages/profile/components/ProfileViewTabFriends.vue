<template>
  <TabsContent v-if="profileStore.selectedUser" value="friends">
    <Card>
      <CardHeader class="flex flex-row items-center justify-between">
        <div>
          <h3 class="text-lg font-semibold">
            All Friends
          </h3>
          <p class="text-sm text-muted-foreground">
            People that {{ profileStore.isCurrentUser ? 'you are' : `${profileStore.selectedUser.displayname || profileStore.selectedUser.username} is` }} connected with
          </p>
        </div>
        <Button v-if="profileStore.isCurrentUser" variant="outline" size="sm" @click="shouldShowAddDialog = true">
          <UserPlus class="h-4 w-4 mr-2" />
          Add Friend
        </Button>
      </CardHeader>
      <CardContent class="p-6">
        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
          <div v-for="friend in sortByUsername(profileStore.selectedUser.friendships?.map(f => f.friend))" :key="friend.id" class="group relative flex items-center cursor-pointer p-4 rounded-lg border border-border bg-card hover:border-primary/50 hover:shadow-lg transition-all duration-300" @click="profileStore.viewUserProfile(friend.id)">
            <ProfilePicture :user="friend" class="mr-4 group-hover:scale-110 transition-transform duration-300" />
            <div class="min-w-0">
              <p class="font-medium truncate">
                {{ friend.displayname || friend.username }}
              </p>
            </div>
            <Button v-if="profileStore.isCurrentUser" variant="ghost" size="xs" class="opacity-0 group-hover:opacity-100 absolute right-1 top-1" @click.stop="selectedFriendIdToRemove = friend.id.toString()">
              <UserMinus class="h-4 w-4" />
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  </TabsContent>

  <ProfileViewAddFriendDialog
    v-model="shouldShowAddDialog"
  />
  <ProfileViewRemoveItemDialog
    v-model="selectedFriendIdToRemove"
    type="friendships"
    :title="`Remove ${getName()} from your friends`"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { UserPlus, UserMinus } from 'lucide-vue-next';
import { useUserStore } from '@/stores/user';
import { useProfileStore } from '@/pages/profile';
import { ProfilePicture } from '@/components/shared';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { TabsContent } from '@/components/ui/tabs';
import ProfileViewAddFriendDialog from './dialogs/ProfileViewAddFriendDialog.vue';
import ProfileViewRemoveItemDialog from './dialogs/ProfileViewRemoveItemDialog.vue';
import { sortByUsername } from '@/utils';

const userStore = useUserStore();
const profileStore = useProfileStore();
const shouldShowAddDialog = ref(false);
const selectedFriendIdToRemove = ref<string>('');

function getName() {
  const user = userStore.user.friendships?.find(f => (
    f.friend.id === Number(selectedFriendIdToRemove.value)
  ))?.friend;

  return user ? (user.displayname || user.username) : 'this user';
}
</script>
