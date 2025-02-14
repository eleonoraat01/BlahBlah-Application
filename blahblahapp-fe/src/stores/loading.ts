import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useLoadingStore = defineStore('loading', () => {
  const loadingQueue = ref(0);
  const isLoading = computed(() => loadingQueue.value > 0);

  /**
   * Increments the loading queue and sets the cursor to 'wait'.
   */
  function startLoading() {
    loadingQueue.value++;
    document.body.style.cursor = 'wait';
  }

  /**
   * Decrements the loading queue or clears it entirely, and resets the cursor if empty.
   * @param everything - If true, clears the entire loading queue. Defaults to false.
   */
  function stopLoading(everything = false) {
    if (everything) loadingQueue.value = 0;
    else loadingQueue.value = Math.max(0, loadingQueue.value - 1);

    if (loadingQueue.value === 0) document.body.style.cursor = 'default';
  }

  return {
    isLoading,
    startLoading,
    stopLoading,
  };
});
