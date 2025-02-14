<template>
  <section class="flex flex-col place-self-center justify-center gap-8 max-w-md w-full h-svh">
    <Card class="shadow-lg">
      <CardHeader class="text-center">
        <h1 class="text-9xl font-bold tracking-tighter text-foreground/90">
          404
        </h1>
        <CardDescription class="text-base font-light">
          We couldn't find the page you're looking for. It might have been moved, deleted, or never existed.
        </CardDescription>
      </CardHeader>

      <CardContent class="p-6 space-y-4">
        <Button class="w-full h-12 group" @click="router.push('/')">
          <Home class="w-4 h-4 transition-transform group-hover:scale-110" />
          <span class="font-medium">Return Home</span>
        </Button>

        <Button variant="ghost" class="w-full h-12 group" @click="router.back()">
          <ArrowLeft class="w-4 h-4 mr-2 transition-transform group-hover:-translate-x-1" />
          <span class="font-medium">Go Back</span>
        </Button>
      </CardContent>
    </Card>
  </section>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { Home, ArrowLeft } from 'lucide-vue-next';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardDescription } from '@/components/ui/card';

const router = useRouter();
</script>
