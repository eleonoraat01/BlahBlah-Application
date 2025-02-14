<template>
  <Toaster />

  <div v-if="!isUserAuthenticated">
    <router-view />
  </div>

  <SidebarProvider v-if="isUserLoggedIn">
    <AppSidebar />

    <SidebarInset class="[--header-height:4rem] min-w-0">
      <AppHeader class="h-[--header-height]" />
      <div class="container h-full">
        <router-view />
      </div>
    </SidebarInset>
  </SidebarProvider>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import Toaster from '@/components/ui/toast/Toaster.vue';
import AppSidebar from '@/components/AppSidebar.vue';
import AppHeader from '@/components/AppHeader.vue';
import { SidebarInset, SidebarProvider } from '@/components/ui/sidebar';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();
const isUserAuthenticated = computed(() => !!userStore.token);
const isUserLoggedIn = computed(() => !!userStore.user);
</script>
