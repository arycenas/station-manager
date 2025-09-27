<script lang="ts" setup>
  import { useRouter } from 'vue-router';
  import { useAuthenticationStore } from '../store/authentication';
  import { ref } from 'vue';

  const username = ref('');
  const password = ref('');
  const authStore = useAuthenticationStore();
  const router = useRouter();

  async function handleLogin() {
    await authStore.loginUser(username.value, password.value);
    router.push('/');
  }
</script>

<template>
  <div class="login-page">
    <h1>Login</h1>
    <input v-model="username" placeholder="Username" />
    <input v-model="password" type="password" placeholder="Password" />
    <button @click="handleLogin">Login</button>
  </div>
</template>
