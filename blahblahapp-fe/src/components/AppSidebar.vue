<template>
  <Sidebar collapsible="offcanvas">
    <SidebarHeader>
      <DropdownMenu>
        <DropdownMenuTrigger as-child>
          <SidebarMenuButton class="h-max w-full p-4 border-b border-sidebar-border hover:bg-sidebar-accent">
            <div class="flex items-center gap-3">
              <ProfilePicture :user="userStore.user" size="lg" />
              <div class="flex-1 min-w-0">
                <h3 class="text-sm font-medium text-sidebar-foreground">
                  {{ userStore.user.displayname || userStore.user.username }}
                </h3>
                <p class="text-xs text-muted-foreground capitalize">
                  {{ userStore.user.status || 'Set status' }}
                </p>
              </div>
            </div>
            <ChevronDown class="shrink-0  ml-auto *:h-4 w-4 text-muted-foreground" />
          </SidebarMenuButton>
        </DropdownMenuTrigger>
        <DropdownMenuContent class="w-56" align="center">
          <DropdownMenuLabel>Set Status</DropdownMenuLabel>
          <DropdownMenuSeparator />
          <DropdownMenuItem :disabled="loadingStore.isLoading" @click="updateStatus('online')">
            <div class="flex items-center gap-2">
              <span class="h-2.5 w-2.5 rounded-full bg-green-500" />
              <span>Online</span>
            </div>
          </DropdownMenuItem>
          <DropdownMenuItem :disabled="loadingStore.isLoading" @click="updateStatus('away')">
            <div class="flex items-center gap-2">
              <span class="h-2.5 w-2.5 rounded-full bg-amber-400" />
              <span>Away</span>
            </div>
          </DropdownMenuItem>
          <DropdownMenuItem :disabled="loadingStore.isLoading" @click="updateStatus('busy')">
            <div class="flex items-center gap-2">
              <span class="h-2.5 w-2.5 rounded-full bg-red-500" />
              <span>Do Not Disturb</span>
            </div>
          </DropdownMenuItem>
          <DropdownMenuItem :disabled="loadingStore.isLoading" @click="updateStatus('offline')">
            <div class="flex items-center gap-2">
              <span class="h-2.5 w-2.5 rounded-full bg-gray-500" />
              <span>Invisible</span>
            </div>
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </SidebarHeader>

    <SidebarContent>
      <SidebarGroup>
        <nav class="p-4 border-b border-sidebar-border">
          <ul class="space-y-2">
            <li v-for="item in navigationItems" :key="item.path">
              <router-link
                :to="item.path"
                class="flex items-center px-3 py-2 rounded-md text-sm"
                :class="[route.matched.some(p => p.path === item.path)
                  ? 'bg-sidebar-accent text-sidebar-accent-foreground'
                  : 'text-muted-foreground hover:bg-sidebar-accent'
                ]"
              >
                <component :is="item.icon" class="h-4 w-4 mr-3" />
                <span>{{ item.title }}</span>
              </router-link>
            </li>
          </ul>
        </nav>
      </SidebarGroup>
      <ScrollArea :key="userStore.user.friendships?.length">
        <SidebarGroup>
          <div class="flex items-center justify-between mb-2">
            <span class="text-xs text-muted-foreground">
              {{ getOnlineUsers(userStore.user.friendships?.map(f => f.friend)).length }} online
            </span>
            <Button variant="ghost" size="sm" @click="shouldShowAddFriendDialog = true">
              <UserPlus class="h-4 w-4" />
            </Button>
          </div>
          <div v-for="friend in sortByStatus(userStore.user.friendships?.map(f => f.friend))" :key="friend.id" class="flex items-center p-3 rounded-md cursor-pointer hover:bg-sidebar-accent" @click="router.push(`/profile/${friend.id}`)">
            <ProfilePicture :user="friend" class="mr-3" />
            <div class="flex-1 min-w-0">
              <div class="flex items-center justify-between">
                <p class="text-sm font-medium text-sidebar-foreground truncate">
                  {{ friend.displayname || friend.username }}
                </p>
              </div>
              <p v-if="friend.status" class="text-xs text-muted-foreground capitalize">
                {{ friend.status }}
              </p>
            </div>
          </div>
        </SidebarGroup>
      </ScrollArea>
      <ProfileViewAddFriendDialog v-model="shouldShowAddFriendDialog" />
    </SidebarContent>

    <SidebarFooter>
      <DropdownMenu>
        <DropdownMenuTrigger as-child>
          <Button variant="outline">
            <span>Switch to</span>
            <span class="relative h-[1.2rem] w-[1.2rem] flex items-center justify-center">
              <MoonIcon class="absolute rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
              <SunIcon class="absolute rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
            </span>
            <span>mode</span>
            <span class="sr-only">Toggle theme</span>
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          <DropdownMenuItem @click="changeTheme('light')">
            <SunIcon />
            <span>Light</span>
          </DropdownMenuItem>
          <DropdownMenuItem @click="changeTheme('dark')">
            <MoonIcon />
            <span>Dark</span>
          </DropdownMenuItem>
          <DropdownMenuItem @click="changeTheme('auto')">
            <MonitorSmartphone />
            <span>System</span>
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      <Dialog :open="shouldShowLogOutDialog" @update:open="shouldShowLogOutDialog = $event">
        <DialogTrigger as-child>
          <Button variant="outline" class="w-full" :disabled="loadingStore.isLoading">
            <LogOut class="h-4 w-4 mr-2" />
            Logout
          </Button>
        </DialogTrigger>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Are you sure you want to logout?</DialogTitle>
            <DialogDescription>
              You will be redirected to the login page.
            </DialogDescription>
          </DialogHeader>

          <DialogFooter class="gap-2">
            <Button type="button" variant="secondary" :disabled="loadingStore.isLoading" @click="shouldShowLogOutDialog = false">
              Cancel
            </Button>
            <Button type="submit" variant="destructive" :disabled="loadingStore.isLoading" @click="handleLogout">
              <template v-if="!loadingStore.isLoading">
                Logout
              </template>
              <template v-else>
                <RotateCw class="w-4 h-4 mr-2 animate-spin" />
                Please wait
              </template>
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </SidebarFooter>
  </Sidebar>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useColorMode, type BasicColorSchema } from '@vueuse/core';
import {
  MoonIcon, SunIcon, MonitorSmartphone,
  UserPlus, ChevronDown, LogOut, RotateCw
} from 'lucide-vue-next';
import { useLoadingStore } from '@/stores/loading';
import { useRequestStore } from '@/stores/request';
import { useUserStore } from '@/stores/user';
import { ProfilePicture } from '@/components/shared';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';
import {
  Dialog, DialogContent, DialogDescription,
  DialogFooter, DialogHeader, DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import {
  DropdownMenu, DropdownMenuContent,
  DropdownMenuItem, DropdownMenuTrigger,
  DropdownMenuSeparator, DropdownMenuLabel
} from '@/components/ui/dropdown-menu';
import {
  Sidebar, SidebarContent, SidebarFooter,
  SidebarGroup, SidebarHeader, SidebarMenuButton,
} from '@/components/ui/sidebar';
import { toast } from '@/components/ui/toast';
import ProfileViewAddFriendDialog from '@/pages/profile/components/dialogs/ProfileViewAddFriendDialog.vue';
import { getOnlineUsers, sortByStatus } from '@/utils';
import type { UserStatusType } from '@/types.d';

const router = useRouter();
const route = useRoute();
const loadingStore = useLoadingStore();
const requestStore = useRequestStore();
const userStore = useUserStore();
const shouldShowAddFriendDialog = ref(false);
const shouldShowLogOutDialog = ref(false);

const navigationItems = computed(() =>
  router.options.routes
    .filter(route => (
      !route.meta?.isPublic
    ))
    .map(route => ({
      path: route.path,
      icon: route.meta!.icon,
      title: route.meta?.title || route.name,
    }))
);

const themeMode = useColorMode({ disableTransition: false });
const themeMetaTag = document.querySelector('meta[name="color-scheme"]') ||
  (() => {
    const newMetaTag = document.head.appendChild(document.createElement('meta'));
    Object.assign(newMetaTag, { name: 'color-scheme', content: 'light dark' });
    return newMetaTag;
  })();

function changeTheme(theme: BasicColorSchema) {
  themeMode.value = theme;
  themeMetaTag.setAttribute('content',
    theme === 'auto' ? 'light dark' : theme
  );
};

async function updateStatus(status: UserStatusType) {
  if (userStore.user.status === status) return;

  try {
    await userStore.updateCurrentUserData({ status });
  } catch (error) {
    const message = error instanceof Error ? error.message : String(error);
    toast({ title: 'Error', description: message, variant: 'destructive' });
  }
};

async function handleLogout() {
  try {
    await requestStore.POST('/auth/logout', {});
    userStore.token = null;
    await router.push('/auth/login');
    shouldShowLogOutDialog.value = false;
    userStore.clearUser();
  } catch (error) {
    const message = error instanceof Error ? error.message : String(error);
    toast({ title: 'Error', description: message, variant: 'destructive' });
  }
};
</script>
