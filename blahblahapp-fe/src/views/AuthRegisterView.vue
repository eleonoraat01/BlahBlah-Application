<template>
  <AuthForm title="Create an account" description="Enter your details to get started" submit-button-text="Create Account" alternate-auth-route="/auth/login" alternate-auth-text="Sign in" @submit="handleSubmit">
    <template #form-fields>
      <Label class="sr-only" for="auth_email">Email</Label>
      <Input id="auth_email" v-model="email" class="[&:user-invalid]:border-destructive" placeholder="Email" type="email" autocapitalize="none" autocomplete="email" required :disabled="loadingStore.isLoading" pattern="\S+" title="Email must not contain spaces." />

      <Label class="sr-only" for="auth_username">Username</Label>
      <Input id="auth_username" v-model="username" class="[&:user-invalid]:border-destructive" placeholder="Username" type="text" autocapitalize="none" autocomplete="username" required :disabled="loadingStore.isLoading" pattern="\S+" title="Username must not contain spaces." />

      <Label class="sr-only" for="auth_password">Password</Label>
      <div class="relative">
        <Input id="auth_password" v-model="password" class="[&:user-invalid]:border-destructive" placeholder="Password" :type="showPassword ? 'text' : 'password'" autocapitalize="none" autocomplete="current-password" required :disabled="loadingStore.isLoading" pattern="\S+" title="Password must not contain spaces." />
        <Button variant="ghost" size="icon" type="button" class="absolute inset-y-0 right-0" :aria-label="showPassword ? 'Hide password' : 'Show password'" :disabled="loadingStore.isLoading" @click.prevent="showPassword = !showPassword">
          <EyeIcon v-if="showPassword" />
          <EyeOffIcon v-else />
        </Button>
      </div>
    </template>
    <template #footer>
      <p class="px-8 text-center text-sm text-muted-foreground">
        By clicking continue, you agree to our
        <router-link to="/terms" class="underline underline-offset-4 hover:text-primary">
          Terms of Service
        </router-link>
        and
        <router-link to="/privacy" class="underline underline-offset-4 hover:text-primary">
          Privacy Policy
        </router-link>
        .
      </p>
    </template>
  </AuthForm>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { EyeIcon, EyeOffIcon } from 'lucide-vue-next';
import { useLoadingStore } from '@/stores/loading';
import { useRequestStore } from '@/stores/request';
import { useUserStore } from '@/stores/user';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { toast } from '@/components/ui/toast';
import AuthForm from '@/components/shared/AuthForm.vue';

const router = useRouter();
const loadingStore = useLoadingStore();
const requestStore = useRequestStore();
const userStore = useUserStore();

const email = ref('');
const username = ref('');
const password = ref('');
const showPassword = ref(false);

async function handleSubmit() {
  if (!username.value.trim() || !password.value.trim() || !email.value.trim()) {
    toast({ title: 'Error', description: 'Please fill in all fields', variant: 'destructive' });
    return;
  }

  loadingStore.startLoading();

  try {
    const data = await requestStore.POST('/auth/register', {
      email: email.value.trim(),
      username: username.value.trim(),
      password: password.value.trim(),
    });

    await userStore.setUser(data);
    router.push({ name: 'home' });

    toast({ description: 'Your account has been created' });
  } catch (error) {
    const message = error instanceof Error ? error.message : String(error);
    toast({ title: 'Error', description: message, variant: 'destructive' });
  } finally {
    loadingStore.stopLoading();
  }
};
</script>
