<template>
  <ScrollArea v-if="chat.messages.length" class="grow">
    <div v-if="!areMembersFriends" class="absolute w-full h-full flex flex-col justify-center items-center">
      <UserX class="h-12 w-12 mx-auto text-muted-foreground mb-4" />
      <h3 class="text-lg font-medium">Not friends</h3>
      <p class="text-muted-foreground">You must be friends to send messages</p>
    </div>

    <ul ref="messagesContainer" class="grow p-4" :class="{ 'opacity-20': !areMembersFriends }">
      <li
        v-for="message in chat.messages"
        :key="message.id"
        class="flex mb-4"
        :class="{ 'justify-end': isMessageFromCurrentUser(message) }"
      >
        <article
          class="max-w-[70%] rounded-lg flex flex-col px-4 py-2 shadow-md"
          :class="[isMessageFromCurrentUser(message)
            ? 'items-end bg-primary text-primary-foreground'
            : 'items-start bg-muted text-secondary-foreground'
          ]"
        >
          <p
            class="text-sm whitespace-pre-wrap break-words"
            :class="[isMessageFromCurrentUser(message)
              ? 'text-end'
              : 'text-start'
            ]"
          >
            {{ message.content }}
          </p>
          <time
            :datetime="getDateISOString(message.createdAt)"
            class="text-xs"
            :class="[isMessageFromCurrentUser(message)
              ? 'text-muted'
              : 'text-muted-foreground'
            ]"
          >
            {{ getRelativeTime(message.createdAt) }}
          </time>
        </article>
      </li>
    </ul>
  </ScrollArea>

  <div v-else class="flex-1 flex flex-col justify-center items-center">
    <MessageCircleDashed class="h-12 w-12 mx-auto text-muted-foreground mb-4" />
    <h3 class="text-lg font-medium">No messages</h3>
    <p class="text-muted-foreground">Send a message to start the conversation</p>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick } from 'vue';
import { MessageCircleDashed, UserX } from 'lucide-vue-next';
import { useUserStore } from '@/stores/user';
import { ScrollArea } from '@/components/ui/scroll-area';
import { getDateISOString, getRelativeTime } from '@/utils';
import type { ChatTable } from '@/types.d';

const props = defineProps<{ chat: ChatTable }>();

const userStore = useUserStore();
const messagesContainer = ref<HTMLElement | null>(null);

const areMembersFriends = computed(() => (
  userStore.user.friendships?.some(f => (
    f.friend.id === props.chat.user2.id
  ))
));

function isMessageFromCurrentUser(message: ChatTable['messages'][number]) {
  return message.sender.id === userStore.user.id;
}

async function scrollToBottom(instant = false) {
  await nextTick();

  messagesContainer.value?.scrollIntoView?.({
    behavior: instant ? 'instant' : 'smooth', block: 'end'
  });
};

watch(() => props.chat.messages.length, () => scrollToBottom());
onMounted(() => scrollToBottom(true));
</script>
