import { STATUS_PRIORITY } from '@/constants';
import type { UserTable } from '@/types.d';

export function getOnlineUsers(list?: UserTable[]) {
  if (!list) return [];

  return list.filter(friend => friend.status && friend.status !== 'offline');
}

export function sortByStatus(list?: UserTable[]) {
  if (!list) return [];

  return [...list].sort((a, b) => {
    const statusDiff = compareUserStatus(a, b);
    return statusDiff !== 0 ? statusDiff : compareUserName(a, b);
  });
}

export function sortByUsername(list?: UserTable[]) {
  if (!list) return [];

  return [...list].sort((a, b) => {
    const nameDiff = compareUserName(a, b);
    return nameDiff !== 0 ? nameDiff : compareUserStatus(a, b);
  });
}

function compareUserStatus(a: UserTable, b: UserTable): number {
  const priorityA = a.status ? STATUS_PRIORITY[a.status] : Number.MAX_SAFE_INTEGER;
  const priorityB = b.status ? STATUS_PRIORITY[b.status] : Number.MAX_SAFE_INTEGER;
  return priorityA - priorityB;
}

function compareUserName(a: UserTable, b: UserTable): number {
  const nameA = a.displayname || a.username;
  const nameB = b.displayname || b.username;
  return nameA.localeCompare(nameB);
}
