<template>
  <section class="container">
    <Button variant="ghost" class="my-4" @click="router.back()">
      <ArrowLeft class="mr-2 h-4 w-4" />
      Back
    </Button>

    <Card>
      <CardHeader>
        <h1 class="text-3xl font-semibold tracking-tight">Terms of Service</h1>
        <CardDescription>Last updated: January 27, 2025</CardDescription>
      </CardHeader>
      <CardContent class="space-y-6">
        <article>
          <h2 class="text-xl font-semibold">1. Acceptance of Terms</h2>
          <p class="pl-4 text-muted-foreground">
            By accessing and using this service, you accept and agree to be bound by the terms and provisions of this agreement.
          </p>
        </article>
        <article>
          <h2 class="text-xl font-semibold">2. User Account</h2>
          <p class="pl-4 text-muted-foreground">
            You are responsible for maintaining the confidentiality of your account and password. You agree to accept responsibility for all activities that occur under your account.
          </p>
        </article>
        <article>
          <h2 class="text-xl font-semibold">3. Acceptable Use</h2>
          <p class="pl-4 text-muted-foreground">
            You agree not to use the service to:
          </p>
          <ul class="pl-8 text-muted-foreground">
            <li>• Violate any applicable laws or regulations</li>
            <li>• Impersonate any person or entity</li>
            <li>• Interfere with or disrupt the service</li>
            <li>• Collect or store personal data about other users without their consent</li>
          </ul>
        </article>
        <article>
          <h2 class="text-xl font-semibold">4. Service Modifications</h2>
          <p class="pl-4 text-muted-foreground">
            We reserve the right to modify or discontinue the service at any time, with or without notice to you.
          </p>
        </article>
        <article>
          <h2 class="text-xl font-semibold">5. Termination</h2>
          <p class="pl-4 text-muted-foreground">
            We reserve the right to terminate or suspend your account and access to the service at our sole discretion, without notice, for conduct that we believe violates these terms or is harmful to other users, us, or third parties.
          </p>
        </article>
        <article>
          <h2 class="text-xl font-semibold">6. Disclaimer</h2>
          <p class="pl-4 text-muted-foreground">
            The service is provided "as is" without warranty of any kind, either express or implied.
          </p>
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
