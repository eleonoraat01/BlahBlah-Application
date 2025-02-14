<template>
  <div class="h-full overflow-auto bg-background">
    <div class="relative h-48 bg-muted">
      <Skeleton class="w-full h-full" />
    </div>
    <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 w-full">
      <div class="relative -mt-16 pb-8">
        <div class="flex flex-col sm:flex-row items-center sm:items-end space-y-4 sm:space-y-0">
          <div class="relative">
            <Skeleton class="h-32 w-32 ring-4 ring-background" />
          </div>
          <div class="sm:ml-6 flex flex-col sm:flex-row items-center sm:items-end justify-between flex-1">
            <div class="text-center sm:text-left w-full">
              <Skeleton class="h-6 w-48 mb-2" />
              <Skeleton class="h-4 w-full" />
            </div>
            <div class="flex gap-2 mt-4 sm:mt-0 sm:ml-6">
              <Skeleton class="h-9 w-28" />
              <Skeleton class="h-9 w-28" />
            </div>
          </div>
        </div>
      </div>
      <div class="space-y-6">
        <div class="flex gap-2">
          <Skeleton class="h-7 w-16" />
          <Skeleton class="h-7 w-16" />
          <Skeleton class="h-7 w-16" />
          <Skeleton class="h-7 w-16" />
        </div>
        <div class="space-y-6">
          <div class="p-6 border rounded-lg border-border bg-card">
            <Skeleton class="h-6 w-48 mb-4" />
            <Skeleton class="h-4 w-full mb-2" />
            <Skeleton class="h-4 w-2/3" />
          </div>
          <div class="p-6 border rounded-lg border-border bg-card">
            <Skeleton class="h-6 w-48 mb-4" />
            <div class="grid grid-cols-2 gap-4">
              <Skeleton class="h-4 w-full" />
              <Skeleton class="h-4 w-full" />
              <Skeleton class="h-4 w-full" />
              <Skeleton class="h-4 w-full" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Skeleton } from '@/components/ui/skeleton';
</script>
