<template>
  <ScrollArea v-if="channel.messages.length" class="grow">
    <ul ref="messagesContainer" class="grow p-4">
      <li
        v-for="message in channel.messages"
        :key="message.id"
        class="flex mb-4"
        :class="{ 'justify-end': isMessageFromCurrentUser(message) }"
      >
        <article
          class="flex flex-col group"
          :class="[
            message.isSpecial
              ? 'w-full items-center text-center'
              : isMessageFromCurrentUser(message)
                ? 'max-w-[70%] items-end'
                : 'max-w-[70%] items-start'
          ]"
        >
          <header
            v-if="!message.isSpecial"
            class="flex items-center gap-2 mb-1"
            :class="{ 'flex-row-reverse': isMessageFromCurrentUser(message) }"
          >
            <ProfilePicture :user="message.sender" :show-status="false" size="sm" />
            <span class="text-sm font-medium truncate max-w-40">{{ message.sender.displayname || message.sender.username }}</span>
            <Badge v-if="getSenderRole(message) !== USER_ROLES.MEMBER" variant="outline" class="rounded-full text-xs capitalize">
              {{ getSenderRole(message) }}
            </Badge>
            <div v-if="canChangeRole(message)" class="opacity-0 group-hover:opacity-100 transition-opacity duration-200">
              <TooltipProvider v-if="getSenderRole(message) === USER_ROLES.MEMBER">
                <Tooltip>
                  <TooltipTrigger as-child>
                    <ArrowBigUpDash
                      class="h-4 w-4 cursor-pointer text-green-500 hover:text-green-700"
                      @click="toggleUserRole(message.sender)"
                    />
                  </TooltipTrigger>
                  <TooltipContent class="bg-primary-foreground ring-1">
                    <p class="text-muted-foreground">Promote to admin</p>
                  </TooltipContent>
                </Tooltip>
              </TooltipProvider>
              <TooltipProvider v-if="getSenderRole(message) === USER_ROLES.ADMIN">
                <Tooltip>
                  <TooltipTrigger as-child>
                    <ArrowBigDownDash
                      class="h-4 w-4 cursor-pointer text-red-500 hover:text-red-700"
                      @click="toggleUserRole(message.sender)"
                    />
                  </TooltipTrigger>
                  <TooltipContent class="bg-primary-foreground ring-1">
                    <p class="text-muted-foreground">Demote to member</p>
                  </TooltipContent>
                </Tooltip>
              </TooltipProvider>
            </div>
          </header>
          <article
            class="rounded-lg flex flex-col px-4 py-2 shadow-md"
            :class="[
              message.isSpecial
                ? 'text-center italic text-muted-foreground'
                : isMessageFromCurrentUser(message)
                  ? 'items-end bg-primary text-primary-foreground'
                  : 'items-start bg-muted text-secondary-foreground'
            ]"
          >
            <p
              class="text-sm whitespace-pre-wrap break-words"
              :class="[!message.isSpecial && (
                isMessageFromCurrentUser(message) ? 'text-end' : 'text-start'
              )]"
            >
              {{ message.content }}
            </p>
            <time
              :datetime="getDateISOString(message.createdAt)"
              class="text-xs"
              :class="[
                message.isSpecial
                  ? 'sr-only'
                  : isMessageFromCurrentUser(message)
                    ? 'text-muted'
                    : 'text-muted-foreground'
              ]"
            >
              {{ getRelativeTime(message.createdAt) }}
            </time>
          </article>
        </article>
      </li>
    </ul>
  </ScrollArea>

  <div v-else class="flex-1 flex flex-col justify-center items-center">
    <MessageSquareDashed class="h-12 w-12 mx-auto text-muted-foreground mb-4" />
    <h3 class="text-lg font-medium">No messages</h3>
    <p class="text-muted-foreground">Send a message to start the conversation</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue';
import { MessageSquareDashed, ArrowBigDownDash, ArrowBigUpDash } from 'lucide-vue-next';
import { useUserStore } from '@/stores/user';
import { useChannelStore } from '@/pages/channel';
import { ProfilePicture } from '@/components/shared';
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Badge } from '@/components/ui/badge';
import { toast } from '@/components/ui/toast';
import { getDateISOString, getRelativeTime } from '@/utils';
import { USER_ROLES } from '@/constants';
import type { ChannelTable, UserTable } from '@/types.d';

const props = defineProps<{ channel: ChannelTable }>();

const userStore = useUserStore();
const cannelStore = useChannelStore();
const messagesContainer = ref<HTMLElement | null>(null);

function canChangeRole(message: ChannelTable['messages'][number]) {
  const imTheChannelOwner = props.channel.members.find(m => m.user.id === userStore.user.id)?.role === USER_ROLES.OWNER;
  const itsNotMySelf = message.sender.id !== userStore.user.id;
  const isSenderStillMember = props.channel.members.some(m => m.user.id === message.sender.id);

  return imTheChannelOwner && itsNotMySelf && isSenderStillMember;
};

function getSenderRole(message: ChannelTable['messages'][number]) {
  for (const { user, role } of props.channel.members) {
    if (user.id === message.sender.id) return role;
  }

  return USER_ROLES.MEMBER;
};

async function toggleUserRole(user: UserTable) {
  try {
    await cannelStore.updateUserRole(user);
  } catch (error) {
    const message = error instanceof Error ? error.message : String(error);
    toast({ title: 'Error', description: message, variant: 'destructive' });
  }
};

function isMessageFromCurrentUser(message: ChannelTable['messages'][number]) {
  return message.sender.id === userStore.user.id;
}

async function scrollToBottom(instant = false) {
  await nextTick();

  messagesContainer.value?.scrollIntoView?.({
    behavior: instant ? 'instant' : 'smooth', block: 'end'
  });
};

watch(() => props.channel.messages.length, () => scrollToBottom());
onMounted(() => scrollToBottom(true));
</script>
