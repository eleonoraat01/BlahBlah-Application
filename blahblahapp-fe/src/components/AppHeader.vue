<template>
  <header class="flex shrink-0 items-center gap-2 px-4 transition-[width,height] ease-linear group-has-[[data-collapsible=icon]]/sidebar-wrapper:h-12">
    <SidebarTrigger class="-ml-1" />
    <Separator orientation="vertical" class="mr-2 h-4" />
    <Breadcrumb>
      <BreadcrumbList>
        <BreadcrumbItem v-for="(item, index) in breadcrumbs" :key="index">
          <router-link :to="item.route" :disabled="item.isLast" :tabindex="item.isLast ? -1 : undefined" :class="[{ 'pointer-events-none': item.isLast }]" class="transition-colors hover:text-foreground">
            {{ item.title }}
          </router-link>
          <BreadcrumbSeparator v-if="!item.isLast" />
        </BreadcrumbItem>
      </BreadcrumbList>
    </Breadcrumb>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Separator } from '@/components/ui/separator';
import { Breadcrumb, BreadcrumbItem, BreadcrumbList, BreadcrumbSeparator } from '@/components/ui/breadcrumb';
import { SidebarTrigger } from '@/components/ui/sidebar';

const route = useRoute();
const router = useRouter();

const breadcrumbs = computed(() => {
  return route.matched.map((matchedRoute, index) => ({
    title: matchedRoute.meta.title,
    route: router.resolve(matchedRoute),
    isLast: index === route.matched.length - 1
  }));
});
</script>
