<template>
  <TabsContent v-if="profileStore.selectedUser" value="social">
    <Card>
      <CardHeader class="flex flex-row items-center justify-between">
        <div>
          <h3 class="text-lg font-semibold">
            Social Links
          </h3>
          <p class="text-sm text-muted-foreground">
            Connect with {{ profileStore.isCurrentUser ? 'me' : profileStore.selectedUser.displayname || profileStore.selectedUser.username }} on social media
          </p>
        </div>
        <Button v-if="profileStore.isCurrentUser" variant="outline" size="sm" @click="shouldShowAddNewDialog = true">
          <Plus class="h-4 w-4 mr-2" />
          Add Link
        </Button>
      </CardHeader>
      <CardContent>
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          <div v-for="({ platform, url }) in profileStore.selectedUser.socialLinks" :key="platform" class="group relative flex items-center p-4 rounded-lg border border-border bg-card hover:border-primary/50 hover:shadow-lg transition-all duration-300">
            <a :href="url" target="_blank" rel="noopener noreferrer" class="flex items-center min-w-0">
              <div class="w-2/5 max-w-14 aspect-square rounded-full bg-muted flex items-center justify-center mr-4 group-hover:scale-110 transition-transform duration-300">
                <component :is="getSocialIcon(platform)" class="h-6 w-6 text-muted-foreground" />
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center">
                  <p class="font-medium text-foreground capitalize mb-1">
                    {{ platform || DEFAULT_SOCIAL_LINKS_TYPE }}
                  </p>
                  <ExternalLink class="h-4 w-4 text-muted-foreground ml-2 opacity-0 group-hover:opacity-100 transition-all duration-300" />
                </div>
                <p class="truncate text-sm text-muted-foreground">
                  {{ url }}
                </p>
              </div>
            </a>
            <Button v-if="profileStore.isCurrentUser" variant="ghost" size="xs" class="opacity-0 group-hover:opacity-100 absolute right-1 top-1" @click="selectedSocialToRemove = url">
              <X class="h-4 w-4" />
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  </TabsContent>

  <ProfileViewAddSocialLinkDialog
    v-model="shouldShowAddNewDialog"
  />
  <ProfileViewRemoveItemDialog
    v-model="selectedSocialToRemove"
    type="socialLinks"
    title="Remove Social Link"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ExternalLink, Plus, X } from 'lucide-vue-next';
import { useProfileStore } from '@/pages/profile';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { TabsContent } from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import { getSocialIcon } from '../utils/socialLinks';
import ProfileViewAddSocialLinkDialog from './dialogs/ProfileViewAddSocialLinkDialog.vue';
import ProfileViewRemoveItemDialog from './dialogs/ProfileViewRemoveItemDialog.vue';
import { DEFAULT_SOCIAL_LINKS_TYPE } from '@/constants';

const profileStore = useProfileStore();
const shouldShowAddNewDialog = ref(false);
const selectedSocialToRemove = ref<string>('');
</script>
