<template>
  <section class="flex h-[calc(100vh-var(--header-height))]">
    <ChannelViewAllChannelsList v-if="!channelStore.selectedChannel" />

    <div v-else class="flex-1 flex flex-col min-w-0">
      <ChannelViewHeader :key="channelStore.selectedChannel.id" :channel="channelStore.selectedChannel" />
      <ChannelViewMessages :key="channelStore.selectedChannel.id" :channel="channelStore.selectedChannel" />
      <ChannelViewInput :key="channelStore.selectedChannel.id" :channel="channelStore.selectedChannel" />
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import {
  useChannelStore, ChannelViewAllChannelsList,
  ChannelViewHeader, ChannelViewMessages, ChannelViewInput,
} from '@/pages/channel';

const route = useRoute();
const channelStore = useChannelStore();

function initializeFromRoute() {
  const channelId = route.params.channelId;
  if (!channelId) return channelStore.resetSelectedChannel();

  const channel = channelStore.currentUserChannels
    .find(c => c.id === Number(channelId));

  if (channel) channelStore.changeSelectedChannel(channel.id);
  else channelStore.resetSelectedChannel();
}

watch(
  () => route.params.channelId,
  initializeFromRoute,
  { immediate: true }
);

onMounted(initializeFromRoute);
</script>
