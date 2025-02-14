import { defineStore } from 'pinia';
import { computed, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useLoadingStore } from '@/stores/loading';
import { useUserStore } from '@/stores/user';
import { VALID_PROFILE_VIEW_TABS, DEFAULT_PROFILE_VIEW_TAB } from '@/constants';
import { compareURLs } from '../utils/socialLinks';
import type { UserTable, ProfileViewTabType } from '@/types.d';

export interface ImageUpdatePayload {
  type: 'profilePictureUrl' | 'coverPictureUrl';
  url: string | null;
}

export const useProfileStore = defineStore('profile', () => {
  const router = useRouter();
  const loadingStore = useLoadingStore();
  const userStore = useUserStore();

  /* State */
  const selectedUser = ref<UserTable>();
  const activeTabName = ref<ProfileViewTabType>(DEFAULT_PROFILE_VIEW_TAB);
  const loadingUserData = ref(false);

  /* Getters */
  const isCurrentUser = computed(() => {
    const profileId = router.currentRoute.value.params.profileId;
    return !profileId || userStore.user.id === Number(profileId);
  });
  const isSelectedFriend = computed(() => {
    if (!selectedUser.value || isCurrentUser.value) return false;
    return userStore.user.friendships?.some(f => (
      f.friend.id === selectedUser.value?.id
    ));
  });
  const loadingUserProfile = computed(() => loadingUserData.value);

  /* Actions */
  async function changeSelectedUser(userId?: UserTable['id']) {
    setActiveTab('');

    try {
      if (isCurrentUser.value) {
        selectedUser.value = userStore.user;
      } else if (userId) {
        loadingUserData.value = true;

        const targetUser = await userStore.getUserById(userId);
        if (!targetUser) throw new Error('User not found');

        selectedUser.value = targetUser;
      } else {
        throw new Error('No user selected');
      }
    } catch (error) {
      selectedUser.value = undefined;
      console.error(error);
    } finally {
      loadingUserData.value = false;
    }
  }

  function viewUserProfile(userId: UserTable['id']) {
    const isOtherUser = userId !== userStore.user.id;
    const profileUrl = isOtherUser ? `/profile/${userId}` : '/profile';

    setActiveTab('');
    router.push(profileUrl);
  };

  function setActiveTab(payload: string | number) {
    const newTab = typeof payload === 'number'
      ? (VALID_PROFILE_VIEW_TABS[payload] || DEFAULT_PROFILE_VIEW_TAB)
      : VALID_PROFILE_VIEW_TABS.includes(payload as ProfileViewTabType)
        ? payload as ProfileViewTabType
        : DEFAULT_PROFILE_VIEW_TAB;

    activeTabName.value = newTab;
    router.replace({ hash: `#${newTab}` });
  }

  async function _isValidImageUrl(url: string) {
    loadingStore.startLoading();

    try {
      const response = await fetch(url, { method: 'HEAD' });
      const contentType = response.headers.get('Content-Type');
      return contentType && contentType.startsWith('image/');
    } finally {
      loadingStore.stopLoading();
    }
  }

  async function updateImage({ type, url }: ImageUpdatePayload) {
    if (url && compareURLs(userStore.user[type], url)) {
      throw new Error('The provided URL is the same as the current one');
    }

    if (url && !(await _isValidImageUrl(url))) {
      throw new Error('The provided URL does not point to a valid image');
    }

    await userStore.updateCurrentUserData({ [type]: url });
  }

  watch(() => userStore.user, () => {
    if (!isCurrentUser.value) return;
    selectedUser.value = userStore.user;
  });

  return {
    /* State */
    selectedUser,
    activeTabName,

    /* Getters */
    isCurrentUser,
    isSelectedFriend,
    loadingUserProfile,

    /* Actions */
    changeSelectedUser,
    setActiveTab,
    viewUserProfile,
    updateImage,
  };
});
