<template>
  <section class="p-4 border-t border-border">
    <form class="flex items-center gap-2" @submit.prevent="handleSubmit">
      <AutosizeTextarea :key="channel.id" v-model="message" @submit="handleSubmit" />
      <Button type="submit" :disabled="!message?.trim()">
        <Send class="h-4 w-4" />
      </Button>
    </form>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { Send } from 'lucide-vue-next';
import { useChannelStore } from '@/pages/channel';
import { AutosizeTextarea } from '@/components/shared';
import { Button } from '@/components/ui/button';
import type { ChannelTable } from '@/types.d';

defineProps<{ channel: ChannelTable }>();

const channelStore = useChannelStore();
const message = ref('');

function handleSubmit() {
  if (!message.value?.trim()) return;
  const messageContent = message.value;

  try {
    channelStore.postMessageToChannel(message.value);
    message.value = '';
  } catch (error) {
    message.value = messageContent;
    console.error(error);
  }
}
</script>
