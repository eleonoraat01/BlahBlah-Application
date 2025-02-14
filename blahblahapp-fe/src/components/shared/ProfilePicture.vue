<template>
  <figure class="relative flex items-center justify-center">
    <Avatar :class="[sizeClasses[size]]" :shape="shape">
      <AvatarImage :key="imageSrc" :src="imageSrc" :alt="user.displayname || user.username" loading="lazy" />
      <AvatarFallback :delay-ms="100">
        <User class="h-full w-full" :size="fallbackIconSize[size]" />
      </AvatarFallback>
    </Avatar>
    <span v-if="showStatus && userStatus" :key="userStatus" class="absolute bottom-0 right-0 border-2 rounded-full border-primary-foreground" :class="[statusColorClasses[userStatus], statusSizeClasses[size]]" />
  </figure>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { User } from 'lucide-vue-next';
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar';
import type { UserTable } from '@/types.d';

interface ProfilePictureProps {
  user: Pick<UserTable, 'displayname' | 'username' | 'profilePictureUrl' | 'status'>;
  showStatus?: boolean;
  size?: keyof typeof sizeClasses;
  shape?: 'circle' | 'square';
}

const props = withDefaults(defineProps<ProfilePictureProps>(), {
  showStatus: true,
  size: 'md',
  shape: 'circle',
});

const userStatus = computed(() => {
  return props.user.status;
});

const imageSrc = computed(() => {
  if (props.user.profilePictureUrl) return props.user.profilePictureUrl;

  const name = (props.user.displayname || props.user.username)
    .replace(/[\p{Emoji}\uFE0F\u200D]/gu, '') // Remove emojis and zero-width joiners
    .split(/[\s\p{P}\p{S}]+/u) // Split by spaces and punctuation
    .filter(Boolean) // Remove empty strings
    .join('+');

  return `https://ui-avatars.com/api/?name=${name}&background=random`;
});

const sizeClasses = {
  sm: 'h-8 w-8',
  md: 'h-10 w-10',
  lg: 'h-12 w-12',
  full: 'h-full w-full'
} as const;

const statusColorClasses = {
  online: 'bg-green-500',
  offline: 'bg-gray-500',
  away: 'bg-amber-400',
  busy: 'bg-red-500'
} as const;

const statusSizeClasses = {
  sm: 'h-2.5 w-2.5',
  md: 'h-3.5 w-3.5',
  lg: 'h-4 w-4',
  full: 'h-1/4 w-1/4'
} as const;

const fallbackIconSize = {
  sm: 25,
  md: 35,
  lg: 40,
  full: 100
} as const;
</script>
