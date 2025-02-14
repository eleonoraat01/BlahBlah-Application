import type { MessageTable, UserTable } from '@/types.d';

/** The time interval in milliseconds, used to filter out old messages */
const MESSAGE_THROTTLE_PERIOD = 10 * 1000 * 60;

export function getLastMessageTime(messages: MessageTable[]) {
  for (let i = messages.length - 1; i >= 0; i--) {
    const { isSpecial, createdAt } = messages[i];
    if (!isSpecial) return createdAt;
  }
  return 0;
};

export function filterRecentMessages(message: MessageTable, currentUserId: UserTable['id']) {
  return !message.isSpecial &&
    message.sender.id !== currentUserId &&
    (Date.now() - message.createdAt <= MESSAGE_THROTTLE_PERIOD);
};
