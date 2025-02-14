<template>
  <TabsContent v-if="profileStore.selectedUser" value="about" class="space-y-6">
    <Card>
      <CardHeader class="flex flex-row items-center justify-between">
        <div>
          <h3 class="text-lg font-semibold">Contact Information</h3>
          <p class="text-sm text-muted-foreground">
            Reach out to {{ profileStore.isCurrentUser ? 'me' : profileStore.selectedUser.displayname || profileStore.selectedUser.username }}
          </p>
        </div>
        <Button v-if="profileStore.isCurrentUser && !isEditing" variant="outline" size="sm" @click="toggleEditMode">
          <Edit class="h-4 w-4 mr-2" />
          Edit Profile
        </Button>
      </CardHeader>
      <CardContent class="space-y-4">
        <form v-if="isEditing" ref="formRef" class="space-y-4" @submit.prevent="handleSubmit" @input="checkFormChanges">
          <div class="space-y-4">
            <div class="flex items-center">
              <Label class="sr-only" for="displayname">Display Name</Label>
              <User class="shrink-0 h-5 w-5 text-muted-foreground mr-3" />
              <Input id="displayname" name="displayname" type="text" autocomplete="name" :default-value="userStore.user.displayname || userStore.user.username" placeholder="Display Name" />
            </div>
            <div class="flex items-center">
              <Label class="sr-only" for="bio">Bio</Label>
              <Text class="shrink-0 h-5 w-5 text-muted-foreground mr-3" />
              <Textarea id="bio" name="bio" :default-value="userStore.user.bio" placeholder="Bio" class="min-h-[100px]" />
            </div>
            <div class="flex items-center">
              <Label class="sr-only" for="email">Email</Label>
              <Mail class="shrink-0 h-5 w-5 text-muted-foreground mr-3" />
              <Input id="email" name="email" type="email" autocomplete="email" :default-value="userStore.user.email" placeholder="Email" required />
            </div>
            <div class="flex items-center">
              <Label class="sr-only" for="phone">Phone</Label>
              <Phone class="shrink-0 h-5 w-5 text-muted-foreground mr-3" />
              <Input id="phone" name="phone" type="tel" autocomplete="tel" :default-value="userStore.user.phone" placeholder="Phone" />
            </div>
            <div class="flex items-center">
              <Label class="sr-only" for="address">Address</Label>
              <MapPin class="shrink-0 h-5 w-5 text-muted-foreground mr-3" />
              <Input id="address" name="address" type="text" autocomplete="street-address" :default-value="userStore.user.address" placeholder="Address" />
            </div>
          </div>
          <div class="flex justify-end space-x-2">
            <Button type="button" variant="outline" :disabled="loadingStore.isLoading" @click="toggleEditMode">
              Cancel
            </Button>
            <Button type="submit" :disabled="loadingStore.isLoading || !hasChanges">
              <template v-if="!loadingStore.isLoading">Save Changes</template>
              <template v-else>
                <RotateCw class="w-4 h-4 mr-2 animate-spin" />
                Please wait
              </template>
            </Button>
          </div>
        </form>
        <template v-else>
          <div v-if="profileStore.selectedUser.email" class="flex items-center">
            <Mail class="shrink-0 h-5 w-5 text-muted-foreground mr-3" />
            <span>{{ profileStore.selectedUser.email }}</span>
          </div>
          <div v-if="profileStore.selectedUser.phone" class="flex items-center">
            <Phone class="shrink-0 h-5 w-5 text-muted-foreground mr-3" />
            <span>{{ profileStore.selectedUser.phone }}</span>
          </div>
          <div v-if="profileStore.selectedUser.address" class="flex items-center">
            <MapPin class="shrink-0 h-5 w-5 text-muted-foreground mr-3" />
            <span>{{ profileStore.selectedUser.address }}</span>
          </div>
        </template>
      </CardContent>
    </Card>

    <Card>
      <CardHeader class="text-lg font-semibold">Basic Information</CardHeader>
      <CardContent class="space-y-4">
        <section class="grid grid-cols-2 gap-4">
          <article>
            <p class="text-sm text-muted-foreground">User ID</p>
            <p>{{ profileStore.selectedUser.id }}</p>
          </article>
          <article>
            <p class="text-sm text-muted-foreground">User Name</p>
            <p>{{ profileStore.selectedUser.username }}</p>
          </article>
          <article>
            <p class="text-sm text-muted-foreground">Joined</p>
            <p>{{ new Date(profileStore.selectedUser.createdAt).toLocaleDateString() }}</p>
          </article>
          <article v-if="!profileStore.isCurrentUser">
            <p class="text-sm text-muted-foreground">Mutual Friends</p>
            <p>{{ mutualFriendsWithCurrent }}</p>
          </article>
        </section>
      </CardContent>
    </Card>
  </TabsContent>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { User, Text, Mail, MapPin, Phone, Edit, RotateCw } from 'lucide-vue-next';
import { useLoadingStore } from '@/stores/loading';
import { useUserStore } from '@/stores/user';
import { useProfileStore } from '@/pages/profile';
import { Textarea } from '@/components/ui/textarea';
import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { TabsContent } from '@/components/ui/tabs';
import { toast } from '@/components/ui/toast';
import type { UserTable } from '@/types.d';

const loadingStore = useLoadingStore();
const profileStore = useProfileStore();
const userStore = useUserStore();

const formRef = ref<HTMLFormElement>();
const isEditing = ref(false);
const hasChanges = ref(false);

const mutualFriendsWithCurrent = computed(() => {
  if (profileStore.isCurrentUser || !profileStore.selectedUser) return 0;

  const currentFriends = userStore.user.friendships;
  const selectedFriends = profileStore.selectedUser.friendships;

  return selectedFriends?.filter(({ friend }) => (
    currentFriends?.some(({ friend: { id } }) => id === friend.id)
  )).length || 0;
});

function checkFormChanges() {
  if (!formRef.value) return;

  const formData = new FormData(formRef.value);

  hasChanges.value = Array.from(formData.entries()).some(([key, value]) => {
    const currentValue = userStore.user[key as keyof UserTable];
    return (value as string).trim() !== (currentValue as string)?.trim();
  });
};

function toggleEditMode() {
  isEditing.value = !isEditing.value;
  hasChanges.value = false;
};

async function handleSubmit() {
  if (!formRef.value) return;

  const formData = new FormData(formRef.value);
  const data = Object.fromEntries(formData);

  if (!data.email) {
    toast({ title: 'Error', description: 'Email is required', variant: 'destructive' });
    return;
  }

  try {
    await userStore.updateCurrentUserData(data);

    isEditing.value = false;
    hasChanges.value = false;
    toast({ description: 'Profile updated successfully' });
  } catch (error) {
    const message = error instanceof Error ? error.message : String(error);
    toast({ title: 'Error', description: message, variant: 'destructive' });
  }
};
</script>
