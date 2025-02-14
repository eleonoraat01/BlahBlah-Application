<template>
  <section class="space-y-6 mb-8">
    <header>
      <h1 class="text-3xl font-bold tracking-tight">
        Welcome back, {{ userStore.user.displayname || userStore.user.username }}
      </h1>
      <p class="text-muted-foreground">
        Here's what's happening in your workspace
      </p>
    </header>

    <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
      <HomeViewChannelsCard />
      <HomeViewFriendsCard />
      <HomeViewNotificationsCard />
    </div>

    <HomeViewRecentActivitySection />
  </section>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user';
import {
  HomeViewChannelsCard, HomeViewFriendsCard,
  HomeViewNotificationsCard, HomeViewRecentActivitySection,
} from '@/pages/home';

const userStore = useUserStore();
</script>
