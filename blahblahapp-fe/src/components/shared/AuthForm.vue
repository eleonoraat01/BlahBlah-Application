<template>
  <section class="flex flex-col place-self-center justify-center gap-8 max-w-md w-full h-svh">
    <router-link :to="alternateAuthRoute" :class="cn(
      buttonVariants({ variant: 'ghost' }),
      'absolute right-4 top-4 md:right-8 md:top-8'
    )">
      {{ alternateAuthText }}
    </router-link>

    <Card class="hover:shadow-lg transition-shadow duration-200">
      <CardHeader class="text-center">
        <h1 class="text-2xl font-semibold tracking-tight">{{ title }}</h1>
        <CardDescription>{{ description }}</CardDescription>
      </CardHeader>

      <CardContent>
        <form class="space-y-3" @submit.prevent="$emit('submit', $event)">
          <div class="space-y-1">
            <slot name="form-fields" />
          </div>
          <Button type="submit" class="w-full" :disabled="loadingStore.isLoading">
            <LoaderCircle v-if="loadingStore.isLoading" class="mr-2 h-4 w-4 animate-spin" />
            {{ submitButtonText }}
          </Button>
        </form>
      </CardContent>

      <CardFooter class="justify-center">
        <slot name="footer" />
      </CardFooter>
    </Card>

    <Separator class="uppercase" label="Or continue with" />

    <div class="flex justify-evenly">
      <Button variant="outline" type="button" :disabled="loadingStore.isLoading">
        <GoogleIcon class="mr-2 h-4 w-4" />
        Google
      </Button>
      <Button variant="outline" type="button" :disabled="loadingStore.isLoading">
        <AppleIcon class="mr-2 h-4 w-4" />
        Apple
      </Button>
      <Button variant="outline" type="button" :disabled="loadingStore.isLoading">
        <GitHubIcon class="mr-2 h-4 w-4" />
        GitHub
      </Button>
    </div>
  </section>
</template>

<script setup lang="ts">
import { GoogleIcon, AppleIcon, GitHubIcon } from 'vue3-simple-icons';
import { useLoadingStore } from '@/stores/loading';
import { Card, CardContent, CardHeader, CardFooter, CardDescription } from '@/components/ui/card';
import { Button, buttonVariants } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { LoaderCircle } from 'lucide-vue-next';
import { cn } from '@/lib/utils';

const loadingStore = useLoadingStore();

defineProps<{
  title: string;
  description: string;
  submitButtonText: string;
  alternateAuthRoute: string;
  alternateAuthText: string;
}>();

defineEmits<{
  submit: [e: Event]
}>();
</script>
