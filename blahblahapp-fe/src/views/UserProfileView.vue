<template>
  <ProfileViewSkeleton v-if="profileStore.loadingUserProfile" class="h-full flex items-center justify-center" />

  <section v-else-if="!profileStore.selectedUser" class="h-full flex items-center justify-center bg-background">
    <div class="text-center">
      <UserX class="h-12 w-12 mx-auto text-muted-foreground mb-4" />
      <h3 class="text-lg font-medium text-foreground">
        User not found
      </h3>
      <p class="text-muted-foreground mt-2">
        This profile doesn't exist or has been removed
      </p>
      <Button variant="outline" class="mt-6" @click="router.push('/profile')">
        <ArrowLeft class="h-4 w-4 mr-2" />
        Back to my profile
      </Button>
    </div>
  </section>

  <section v-else :key="profileStore.selectedUser.id" class="h-full overflow-auto bg-background mb-8">
    <ProfileViewCover />

    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
      <ProfileViewHeader />

      <Tabs v-model="profileStore.activeTabName" :default-value="DEFAULT_PROFILE_VIEW_TAB" class="space-y-6" @update:model-value="profileStore.setActiveTab">
        <TabsList>
          <TabsTrigger v-for="tab in VALID_PROFILE_VIEW_TABS" :key="tab" :value="tab" class="capitalize">
            {{ tab }}
          </TabsTrigger>
        </TabsList>

        <ProfileViewTabAbout />
        <ProfileViewTabFriends :key="profileStore.selectedUser.friendships?.length" />
        <ProfileViewTabInterests :key="profileStore.selectedUser.interests?.length" />
        <ProfileViewTabSocial :key="profileStore.selectedUser.socialLinks?.length" />
      </Tabs>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ArrowLeft, UserX } from 'lucide-vue-next';
import {
  ProfileViewCover, ProfileViewHeader, ProfileViewSkeleton,
  ProfileViewTabAbout, ProfileViewTabFriends,
  ProfileViewTabInterests, ProfileViewTabSocial,
  useProfileStore,
} from '@/pages/profile';
import { Button } from '@/components/ui/button';
import { Tabs, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { VALID_PROFILE_VIEW_TABS, DEFAULT_PROFILE_VIEW_TAB } from '@/constants';

const route = useRoute();
const router = useRouter();
const profileStore = useProfileStore();

watch(() => route.params.profileId, newId => (
  profileStore.changeSelectedUser(Number(newId))
), { immediate: false });

onMounted(async () => {
  await profileStore.changeSelectedUser(Number(route.params.profileId));
  if (profileStore.selectedUser) profileStore.viewUserProfile(profileStore.selectedUser.id);

  const currentHash = route.hash.slice(1);
  if (currentHash) profileStore.setActiveTab(currentHash);
});
</script>
