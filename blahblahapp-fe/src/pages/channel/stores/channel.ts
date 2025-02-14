import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useIndexedDBStore, STORES } from '@/stores/indexedDB';
import { useRequestStore } from '@/stores/request';
import { useUserStore } from '@/stores/user';
import { useLoadingStore } from '@/stores/loading';
import { getLastMessageTime } from '@/utils';
import { USER_ROLES } from '@/constants';
import type { UserTable, ChannelTable, MessageTable } from '@/types.d';

export const useChannelStore = defineStore('channel', () => {
  const router = useRouter();
  const indexedDBStore = useIndexedDBStore();
  const requestStore = useRequestStore();
  const userStore = useUserStore();
  const loadingStore = useLoadingStore();

  /* State */
  const selectedChannelId = ref<ChannelTable['id'] | null>(null);

  /* Getter */
  const currentUserChannels = computed(() => {
    return userStore.channels
      .filter(channel => channel.isActive)
      .sort((a, b) => getLastMessageTime(b.messages) - getLastMessageTime(a.messages));
  });
  const selectedChannel = computed(() =>
    currentUserChannels.value.find(channel => channel.id === selectedChannelId.value)
  );
  const selectedChannelUserRole = computed(() => {
    const currentUserId = userStore.user.id;
    const members = selectedChannel.value?.members || [];

    for (const { user, role } of members) {
      if (user.id === currentUserId) return role;
    }

    return USER_ROLES.MEMBER;
  });
  const selectedChannelOwner = computed(() => {
    const members = selectedChannel.value?.members || [];
    return members.find(m => m.role === USER_ROLES.OWNER)?.user;
  });

  /* Actions */
  /**
   * Creates a new channel with the specified name and description.
   * @param name - The name of the new channel.
   * @param description - An optional description for the channel.
   * @returns The newly created channel object.
   * @throws Will throw an error if the database call fails.
   */
  async function initializeNewChannel({ name, description = '' }: { name: string; description?: string }) {
    loadingStore.startLoading();

    try {
      const channel: ChannelTable = await requestStore.POST('/channels', { name, description });

      userStore.channels.push(channel);
      indexedDBStore.createTableRow(STORES.CHANNELS, channel);

      return channel;
    } finally {
      loadingStore.stopLoading();
    }
  }

  /**
   * Updates the selected channel based on the given channel ID and navigates to the channel details view.
   * @param channelId - The ID of the channel to select.
   */
  function changeSelectedChannel(channelId: ChannelTable['id']) {
    selectedChannelId.value = channelId;

    const { name, params } = router.currentRoute.value;

    if (name !== 'channel-details' || Number(params.channelId) !== channelId) {
      router.push({ name: 'channel-details', params: { channelId } });
    }
  }

  /**
   * Clears the currently selected channel and navigates to the channel list view.
   */
  function resetSelectedChannel() {
    selectedChannelId.value = null;

    const { name } = router.currentRoute.value;

    if (name !== 'channel-list') {
      router.push({ name: 'channel-list' });
    }
  }

  /**
   * Sends a message to the currently selected channel.
   * @param content - The content of the message.
   * @param isSpecial - Indicates whether the message is a special system message. Defaults to false.
   * @returns The message object if sent successfully.
   */
  async function postMessageToChannel(content: string, isSpecial = false) {
    const channel = selectedChannel.value;
    if (!content.trim() || !channel) return;

    // Temporary message to display while waiting for the database response
    const tempMessage: MessageTable = {
      id: Date.now(),
      channelId: selectedChannel.value.id,
      sender: userStore.user,
      content,
      isSpecial,
      isActive: true,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    };

    const channelMessages = channel.messages;
    channelMessages.push(tempMessage);

    try {
      const message: MessageTable = await requestStore.POST(
        `/channels/${channel.id}/messages`,
        { content, isSpecial }
      );

      // Replace the temporary message with the actual message from the database
      const index = channelMessages.findIndex(m => m.id === tempMessage.id);
      channelMessages.splice(index, 1, message);

      indexedDBStore.updateTableField(STORES.CHANNELS, channel.id, 'messages',
        existingMessages => ([...existingMessages, message])
      );

      return tempMessage;
    } catch (error) {
      // Revert back to the previous messages state if the database call fails
      const index = channelMessages.findIndex(m => m.id === tempMessage.id);
      if (index !== -1) channelMessages.splice(index, 1);

      throw error; // To be caught by the caller
    }
  }

  /**
   * Updates the name and description of the currently selected channel.
   * @param name - The new name of the channel.
   * @param description - An optional new description for the channel.
   * @throws Will throw an error if no channel is selected, the name is empty, or the database call fails.
   */
  async function updateSelectedChannel({ name, description = '' }: { name: string; description?: string }) {
    const channel = selectedChannel.value;
    if (!channel) throw new Error('No channel selected');

    name = name.trim();
    description = description.trim();

    if (!name) throw new Error('Please enter a channel name');

    loadingStore.startLoading();

    try {
      const message = name !== channel.name
        ? `Channel renamed to ${name}`
        : 'Channel description updated';

      await requestStore.PATCH(
        `/channels/${channel.id}`,
        { name, description }
      );

      Object.assign(channel, { name, description });
      indexedDBStore.updateTableRow(STORES.CHANNELS, channel.id, { name, description });

      postMessageToChannel(message, true);
    } finally {
      loadingStore.stopLoading();
    }
  }

  /**
   * Deletes the currently selected channel.
   * @throws Will throw an error if no channel is selected or the database call fails.
   */
  async function deleteSelectedChannel() {
    const channel = selectedChannel.value;
    if (!channel) throw new Error('No channel selected');

    loadingStore.startLoading();

    try {
      await requestStore.DELETE(
        `/channels/${channel.id}`
      );

      userStore.channels = userStore.channels.filter(c => c.id !== channel.id);
      indexedDBStore.deleteTableRow(STORES.CHANNELS, channel.id);
    } finally {
      loadingStore.stopLoading();
    }
  }

  /**
   * Adds a user to the currently selected channel based on the user object or fetched from the user ID.
   * @param target - The user object or user ID to add to the channel.
   * @throws Will throw an error if no channel is selected, the user is not found, the user is already a member, or database call fails.
   */
  async function addUserToChannel(target: UserTable | UserTable['id']) {
    const channel = selectedChannel.value;
    if (!channel) throw new Error('No channel selected');

    const user = typeof target === 'object' ? target : (await userStore.getUserById(target));
    if (!user) throw new Error('User not found');

    if (channel.members.some(m => m.user.id === user.id)) {
      throw new Error('User is already a member of the channel');
    }

    loadingStore.startLoading();

    try {
      const member: ChannelTable['members'][number] = await requestStore.POST(
        `/channels/${channel.id}/members`,
        { userId: user.id, role: USER_ROLES.MEMBER }
      );

      channel.members.push(member);
      indexedDBStore.updateTableField(STORES.CHANNELS, channel.id, 'members',
        existingMembers => ([...existingMembers, member])
      );

      postMessageToChannel(`${user.displayname || user.username} has joined the channel`, true);
      return member;
    } finally {
      loadingStore.stopLoading();
    }
  }

  /**
   * Removes a user from the currently selected channel, or the current user if no user ID is provided.
   * @param userId - The ID of the user to remove. If omitted, the current user is removed.
   * @throws Will throw an error if no channel is selected, the user is not found, or database call fails.
   */
  async function removeUserFromChannel(userId?: UserTable['id']) {
    const channel = selectedChannel.value;
    if (!channel) throw new Error('No channel selected');

    const user = userId ? (await userStore.getUserById(userId)) : userStore.user;
    if (!user) throw new Error('User not found');

    loadingStore.startLoading();

    try {
      await requestStore.DELETE(
        `/channels/${channel.id}/members/${user.id}`
      );

      if (user.id === userStore.user.id) {
        // If current user is leaving, remove the entire channel from their channel list
        userStore.channels = userStore.channels.filter(c => c.id !== channel.id);
        indexedDBStore.deleteTableRow(STORES.CHANNELS, channel.id);
      } else {
        // For other users, keep the channel but filter them out of its members list
        channel.members = channel.members.filter(m => m.user.id !== user.id);
        indexedDBStore.updateTableField(STORES.CHANNELS, channel.id, 'members',
          members => members.filter(m => m.user.id !== user.id)
        );
      }

      postMessageToChannel(`${user.displayname || user.username} has left the channel`, true);
    } finally {
      loadingStore.stopLoading();
    }
  }

  /**
   * Updates the role of a user in the currently selected channel. Promotes to admin if the user is not already an admin, or demotes to member if the user is already an admin.
   * @throws Will throw an error if no channel is selected or the database call fails.
   */
  async function updateUserRole(user: UserTable) {
    const channel = selectedChannel.value;
    if (!channel) throw new Error('No channel selected');

    const target = channel.members.find(m => m.user.id === user.id);
    if (!target) throw new Error('User not found in channel');

    const previousRole = target.role;
    const isAdmin = target.role === USER_ROLES.ADMIN;
    const newRole = isAdmin ? USER_ROLES.MEMBER : USER_ROLES.ADMIN;
    const action = isAdmin ? 'demote' : 'promote';

    target.role = newRole;
    const systemMessage = await postMessageToChannel(`${user.displayname || user.username} has been ${action}d to ${newRole}`, true);

    try {
      await requestStore.PATCH(
        `/channels/${channel.id}/members/${user.id}/${action}`,
        { channelId: channel.id, userId: user.id, role: newRole }
      );

      indexedDBStore.updateTableField(STORES.CHANNELS, channel.id, 'members',
        members => members.map(m => (m.id === target.id ? { ...m, role: newRole } : m))
      );
    } catch (error) {
      target.role = previousRole;

      const index = channel.messages.findIndex(m => m.id === systemMessage?.id);
      if (index !== -1) channel.messages.splice(index, 1);

      throw error; // To be caught by the caller
    }
  };

  return {
    /* Getters */
    currentUserChannels,
    selectedChannel,
    selectedChannelUserRole,
    selectedChannelOwner,

    /* Actions */
    initializeNewChannel,
    changeSelectedChannel,
    resetSelectedChannel,
    postMessageToChannel,
    updateSelectedChannel,
    deleteSelectedChannel,
    addUserToChannel,
    removeUserFromChannel,
    updateUserRole,
  };
});
