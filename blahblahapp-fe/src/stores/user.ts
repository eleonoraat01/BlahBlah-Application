import { defineStore } from 'pinia';
import { ref, type Ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { clone } from 'ramda';
import { useIndexedDBStore, STORES } from '@/stores/indexedDB';
import { useRequestStore } from '@/stores/request';
import { useLoadingStore } from '@/stores/loading';
import type { UserTable, ChatTable, ChannelTable } from '@/types.d';

const AUTH_TOKEN_KEY = 'blahblah_auth_token';

export const useUserStore = defineStore('user', () => {
  const router = useRouter();
  const loadingStore = useLoadingStore();
  const requestStore = useRequestStore();
  const indexedDBStore = useIndexedDBStore();

  /* State */
  const token = ref<string | null>(null);
  const user = ref<UserTable>() as Ref<UserTable>;
  const chats = ref<ChatTable[]>() as Ref<ChatTable[]>;
  const channels = ref<ChannelTable[]>() as Ref<ChannelTable[]>;

  /* Actions */
  async function setUser(data: { token: string; user: UserTable; chats: ChatTable[], channels: ChannelTable[] }) {
    await indexedDBStore.initDB();

    [
      [user.value],
      chats.value,
      channels.value,
    ] = await Promise.all([
      indexedDBStore.createTableRow(STORES.USER, data.user),
      indexedDBStore.createTableRow(STORES.CHATS, ...data.chats),
      indexedDBStore.createTableRow(STORES.CHANNELS, ...data.channels),
    ]);

    token.value = data.token;
    localStorage.setItem(AUTH_TOKEN_KEY, data.token);
  }

  function clearUser() {
    indexedDBStore.deleteDB();

    // @ts-expect-error Clear user reference
    user.value = null;
    // @ts-expect-error Clear chats reference
    chats.value = null;
    // @ts-expect-error Clear channels reference
    channels.value = null;

    token.value = null;
    localStorage.removeItem(AUTH_TOKEN_KEY);
  }

  async function getUserById(userId: UserTable['id']): Promise<UserTable | undefined> {
    loadingStore.startLoading();

    try {
      return requestStore.GET(`/users/${userId}`);
    } finally {
      loadingStore.stopLoading();
    }
  }

  async function updateCurrentUserData(data: Partial<UserTable>) {
    loadingStore.startLoading();

    try {
      const updatedData = clone(data);
      const userId = user.value.id;

      [user.value] = await Promise.all([
        indexedDBStore.updateTableRow(STORES.USER, userId, updatedData),
        requestStore.PATCH(`/users/${userId}`, updatedData),
      ]);
    } finally {
      loadingStore.stopLoading();
    }
  }

  async function addFriend(target: UserTable | UserTable['id']) {
    loadingStore.startLoading();

    try {
      const friend = typeof target === 'object' ? target : (await getUserById(target));
      if (!friend) throw new Error('User not found');

      if (user.value.friendships?.some(f => f.friend.id === friend.id)) {
        throw new Error('You are already friends with this user');
      }

      const friendship: NonNullable<UserTable['friendships']>[number] =
        await requestStore.POST('/friendships', { friendId: friend.id });

      user.value.friendships ||= [];
      user.value.friendships.push(friendship);

      indexedDBStore.updateTableField(STORES.USER, user.value.id, 'friendships',
        existingFriendships => ([...(existingFriendships || []), friendship])
      );
    } finally {
      loadingStore.stopLoading();
    }
  }

  async function removeFriend(userId: UserTable['id']) {
    loadingStore.startLoading();

    try {
      const friendship = user.value.friendships?.find(f => f.friend.id === userId);
      if (!friendship) throw new Error('You are not friends with this user');

      await requestStore.DELETE(`/friendships/${friendship.id}`);

      user.value.friendships = user.value.friendships?.filter(f => f.id !== friendship.id);
      indexedDBStore.updateTableField(STORES.USER, user.value.id, 'friendships',
        existingFriendships => existingFriendships?.filter(f => f.id !== friendship.id)
      );
    } finally {
      loadingStore.stopLoading();
    }
  }

  onMounted(async () => {
    [
      [user.value],
      chats.value,
      channels.value,
    ] = await Promise.all([
      indexedDBStore.getAllTableRows(STORES.USER),
      indexedDBStore.getAllTableRows(STORES.CHATS),
      indexedDBStore.getAllTableRows(STORES.CHANNELS),
    ]);

    token.value = localStorage.getItem(AUTH_TOKEN_KEY);

    if (token.value) {
      const route = router.currentRoute.value;
      if (route.name === 'login' || route.name === 'register') {
        return router.push(route.redirectedFrom || '/');
      }
    }
  });

  return {
    /* State */
    token,
    user,
    chats,
    channels,

    /* Actions */
    setUser,
    clearUser,
    getUserById,
    updateCurrentUserData,
    addFriend,
    removeFriend,
  };
});
