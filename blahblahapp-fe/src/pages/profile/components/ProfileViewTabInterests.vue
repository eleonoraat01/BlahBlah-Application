<template>
  <TabsContent v-if="profileStore.selectedUser" value="interests">
    <Card>
      <CardHeader class="flex flex-row items-center justify-between">
        <div>
          <h3 class="text-lg font-semibold">
            Interests
          </h3>
          <p class="text-sm text-muted-foreground">
            Topics that {{ profileStore.isCurrentUser ? 'you' : profileStore.selectedUser.displayname || profileStore.selectedUser.username }} are interested in
          </p>
        </div>
        <Button v-if="profileStore.isCurrentUser" variant="outline" size="sm" @click="shouldShowAddNewDialog = true">
          <Plus class="h-4 w-4 mr-2" />
          Add Interest
        </Button>
      </CardHeader>
      <CardContent>
        <div class="grid grid-cols-2 md:grid-cols-3 gap-4">
          <div v-for="{ interest } in profileStore.selectedUser.interests" :key="interest" class="group relative flex items-center p-4 rounded-lg border border-border bg-card hover:border-primary/50 hover:shadow-lg transition-all duration-300">
            <div class="w-2/5 max-w-14 aspect-square rounded-full bg-primary/10 flex items-center justify-center mr-[10%] group-hover:scale-110 transition-transform duration-300">
              <Heart class="h-1/2 w-1/2 text-primary" />
            </div>
            <div class="flex-1 min-w-0">
              <p class="truncate font-medium text-foreground">
                {{ interest }}
              </p>
            </div>
            <Button v-if="profileStore.isCurrentUser" variant="ghost" size="xs" class="opacity-0 group-hover:opacity-100 absolute right-1 top-1" @click="selectedInterestToRemove = interest">
              <X class="h-4 w-4" />
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  </TabsContent>

  <ProfileViewAddInterestDialog
    v-model="shouldShowAddNewDialog"
  />
  <ProfileViewRemoveItemDialog
    v-model="selectedInterestToRemove"
    type="interests"
    title="Remove Interest"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { Heart, Plus, X } from 'lucide-vue-next';
import { useProfileStore } from '@/pages/profile';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { TabsContent } from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import ProfileViewAddInterestDialog from './dialogs/ProfileViewAddInterestDialog.vue';
import ProfileViewRemoveItemDialog from './dialogs/ProfileViewRemoveItemDialog.vue';

const profileStore = useProfileStore();
const shouldShowAddNewDialog = ref(false);
const selectedInterestToRemove = ref<string>('');
</script>
