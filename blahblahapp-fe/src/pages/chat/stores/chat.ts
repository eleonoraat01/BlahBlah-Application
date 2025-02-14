import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useIndexedDBStore, STORES } from '@/stores/indexedDB';
import { useRequestStore } from '@/stores/request';
import { useUserStore } from '@/stores/user';
import { useLoadingStore } from '@/stores/loading';
import { getLastMessageTime } from '@/utils';
import type { UserTable, ChatTable, MessageTable } from '@/types.d';

export const useChatStore = defineStore('chat', () => {
  const router = useRouter();
  const indexedDBStore = useIndexedDBStore();
  const requestStore = useRequestStore();
  const userStore = useUserStore();
  const loadingStore = useLoadingStore();

  /* State */
  const searchQuery = ref('');
  const selectedChatId = ref<ChatTable['id'] | null>(null);

  /* Getters */
  const currentUserChats = computed(() => {
    return userStore.chats
      .filter(chat => chat.isActive)
      .sort((a, b) => getLastMessageTime(b.messages) - getLastMessageTime(a.messages));
  });
  const activeChats = computed(() => {
    const query = searchQuery.value.toLowerCase();
    return currentUserChats.value.filter(({ messages, user2 }) =>
      messages.length > 0 && (user2.displayname || user2.username).toLowerCase().includes(query)
    );
  });
  const selectedChat = computed(() =>
    currentUserChats.value.find(chat => chat.id === selectedChatId.value)
  );

  /* Actions */
  /**
   * Creates a new chat with the specified recipient.
   * @param recipient - The user to create a chat with.
   * @returns The newly created chat object.
   * @throws Will throw an error if the database call fails.
   */
  async function initializeNewChat(recipient: UserTable) {
    loadingStore.startLoading();

    try {
      const chat: ChatTable = await requestStore.POST('/chats', { recipient: recipient.id });

      userStore.chats.push(chat);
      indexedDBStore.createTableRow(STORES.CHATS, chat);

      return chat;
    } finally {
      loadingStore.stopLoading();
    }
  }

  /**
   * Opens or creates a chat with a user from the current user's friends list.
   * @param userId - The ID of the user to open a chat with.
   * @throws Will throw an error if the user is not in the friends list or if database calls fail.
   */
  async function openChatWithUser(userId: UserTable['id']) {
    const recipient = userStore.user.friendships?.find(f => (
      f.friend.id === userId
    ))?.friend;

    if (!recipient) return;

    const existingChat = currentUserChats.value.find(
      chat => recipient.id === chat.user2.id
    );

    const chat = existingChat || await initializeNewChat(recipient);
    if (chat) changeSelectedChat(chat.id);
  }

  /**
   * Updates the selected chat based on the given chat ID and navigates to the chat details view.
   * @param chatId - The ID of the chat to select.
   */
  function changeSelectedChat(chatId: ChatTable['id']) {
    selectedChatId.value = chatId;

    const { name, params } = router.currentRoute.value;

    if (name !== 'chat-details' || Number(params.chatId) !== chatId) {
      router.push({ name: 'chat-details', params: { chatId } });
    }
  }

  /**
   * Clears the currently selected chat and navigates to the chat list view.
   */
  function resetSelectedChat() {
    selectedChatId.value = null;

    const { name } = router.currentRoute.value;

    if (name !== 'chat-list') {
      router.push({ name: 'chat-list' });
    }
  }

  /**
   * Sends a message in the currently selected chat.
   * @param content - The content of the message to send.
   * @param isSpecial - Indicates whether the message is a special system message. Defaults to false.
   * @returns The message object if sent successfully.
   */
  async function postMessageToChat(content: string, isSpecial = false) {
    const chat = selectedChat.value;
    if (!content.trim() || !chat) return;

    // Temporary message to display while waiting for the database response
    const tempMessage: MessageTable = {
      id: Date.now(),
      chatId: chat.id,
      sender: userStore.user,
      content,
      isSpecial,
      isActive: true,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    };

    const chatMessages = chat.messages;
    chatMessages.push(tempMessage);

    try {
      const message = await requestStore.POST(
        `/chats/${chat.id}/messages`,
        { content, isSpecial }
      );

      // Replace the temporary message with the actual message from the database
      const index = chatMessages.findIndex(m => m.id === tempMessage.id);
      chatMessages.splice(index, 1, message);

      indexedDBStore.updateTableField(STORES.CHATS, chat.id, 'messages',
        existingMessages => ([...existingMessages, message])
      );

      return message;
    } catch (error) {
      // Revert back to the previous messages state if the database call fails
      const index = chatMessages.findIndex(m => m.id === tempMessage.id);
      if (index !== -1) chatMessages.splice(index, 1);

      throw error; // To be caught by the caller
    }
  }

  return {
    /* State */
    searchQuery,

    /* Getters */
    currentUserChats,
    activeChats,
    selectedChat,

    /* Actions */
    initializeNewChat,
    openChatWithUser,
    changeSelectedChat,
    resetSelectedChat,
    postMessageToChat,
  };
});
