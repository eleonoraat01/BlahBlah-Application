<template>
  <section class="container">
    <Button variant="ghost" class="my-4" @click="router.back()">
      <ArrowLeft class="mr-2 h-4 w-4" />
      Back
    </Button>

    <Card>
      <CardHeader>
        <h1 class="text-3xl font-semibold tracking-tight">Privacy Policy</h1>
        <CardDescription>Last updated: January 27, 2025</CardDescription>
      </CardHeader>
      <CardContent class="space-y-6">
        <article>
          <h2 class="text-xl font-semibold">1. Information We Collect</h2>
          <p class="pl-4 text-muted-foreground">
            We collect information you provide directly to us:
          </p>
          <ul class="pl-8 text-muted-foreground">
            <li>• Account information (name, email, username)</li>
            <li>• Profile information you choose to share</li>
            <li>• Communications you send to us</li>
          </ul>
        </article>

        <article>
          <h2 class="text-xl font-semibold">2. How We Use Information</h2>
          <p class="pl-4 text-muted-foreground">
            We use the information we collect to:
          </p>
          <ul class="pl-8 text-muted-foreground">
            <li>• Provide, maintain, and improve our services</li>
            <li>• Process your transactions</li>
            <li>• Send you technical notices and support messages</li>
            <li>• Respond to your comments and questions</li>
          </ul>
        </article>

        <article>
          <h2 class="text-xl font-semibold">3. Information Sharing</h2>
          <p class="pl-4 text-muted-foreground">
            We do not share your personal information with third parties except:
          </p>
          <ul class="pl-8 text-muted-foreground">
            <li>• With your consent</li>
            <li>• To comply with legal obligations</li>
            <li>• To protect our rights and safety</li>
          </ul>
        </article>

        <article>
          <h2 class="text-xl font-semibold">4. Data Security</h2>
          <p class="pl-4 text-muted-foreground">
            We implement reasonable security measures to protect your personal information from unauthorized access and disclosure.
          </p>
        </article>

        <article>
          <h2 class="text-xl font-semibold">5. Your Rights</h2>
          <p class="pl-4 text-muted-foreground">
            You have the right to:
          </p>
          <ul class="pl-8 text-muted-foreground">
            <li>• Access your personal information</li>
            <li>• Update or correct your information</li>
            <li>• Request deletion of your information</li>
            <li>• Opt out of marketing communications</li>
          </ul>
        </article>
      </CardContent>
    </Card>
  </section>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { ArrowLeft } from 'lucide-vue-next';
import { Card, CardContent, CardHeader, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';

const router = useRouter();
</script>
