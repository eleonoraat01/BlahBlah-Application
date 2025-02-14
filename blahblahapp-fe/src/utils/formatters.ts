export function getRelativeTime(timestamp: number) {
  const diffMinutes = Math.floor((Date.now() - timestamp) / 1000 / 60);
  if (diffMinutes <= 1) return 'just now';
  if (diffMinutes < 60) return `${diffMinutes}m ago`;

  const diffHours = Math.floor(diffMinutes / 60);
  if (diffHours < 24) return `${diffHours}h ago`;

  const diffDays = Math.floor(diffHours / 24);
  if (diffDays <= 1) return 'a day ago';
  if (diffDays < 7) return `${diffDays}d ago`;

  return new Date(timestamp).toLocaleDateString();
}

export function getDateISOString(date: ConstructorParameters<typeof Date>[0]) {
  const timeZoneOffset = new Date().getTimezoneOffset() * 60000;
  const adjustedDate = Number(new Date(date)) - timeZoneOffset;
  return new Date(adjustedDate).toISOString();
}
