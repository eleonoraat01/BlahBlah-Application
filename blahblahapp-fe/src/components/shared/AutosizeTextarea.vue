<template>
  <div ref="element" :contenteditable="!isDisabled" role="textbox" spellcheck="true" aria-multiline="true" :aria-placeholder="placeholder" :aria-disabled="isDisabled" :class="cn('flex-1 min-h-10 max-h-40 overflow-y-auto whitespace-pre-wrap break-all rounded-md border border-input bg-transparent px-3 py-2 text-sm shadow-sm cursor-text focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring aria-disabled:cursor-not-allowed aria-disabled:opacity-50', props.class)" @keydown.enter="handleEnter" @input="handleInput" />
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from 'vue';
import { cn } from '@/lib/utils';
import type { HTMLAttributes } from 'vue';

interface Emits {
  'update:modelValue': [value: string];
  'submit': [value: string];
}
interface Props {
  modelValue?: string;
  placeholder?: string;
  disabled?: boolean,
  class?: HTMLAttributes['class'];
}

const emits = defineEmits<Emits>();
const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  placeholder: 'Enter your message...',
  disabled: false,
  class: '',
});

const element = ref<HTMLDivElement>();
const isDisabled = ref(props.disabled);

watch(() => props.modelValue, (newValue) => {
  if (newValue === element.value?.textContent) return;
  element.value!.textContent = newValue;
});
watch(() => props.disabled, (newValue) => {
  isDisabled.value = newValue;
});

function handleInput() {
  const text = element.value?.textContent;
  emits('update:modelValue', text || '');

  nextTick(() => (
    !text && (element.value!.innerHTML = '')
  ));
}

function handleEnter(event: KeyboardEvent) {
  if (event.shiftKey) return;
  event.preventDefault();

  const text = element.value?.textContent;
  if (!text?.trim()) return;

  emits('submit', text);
  emits('update:modelValue', '');
  element.value!.textContent = '';
}
</script>

<style scoped>
[contenteditable]:empty::before {
  content: attr(aria-placeholder);
  color: hsl(var(--muted-foreground));
  pointer-events: none;
}
</style>
