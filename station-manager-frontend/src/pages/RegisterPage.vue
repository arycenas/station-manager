<script setup lang="ts">
  import { ref } from 'vue';

  const name = ref('');
  const username = ref('');
  const password = ref('');
  const confirmPassword = ref('');

  const handleRegister = () => {
    if (password.value !== confirmPassword.value) {
      alert('Password dan Confirm Password harus sama!');

      return;
    }

    console.log('Register:', {
      name: name.value,
      username: username.value,
      password: password.value,
    });
    // nanti tinggal tembak API backend /register
  };
</script>

<template>
  <div class="register-page">
    <h1>Register</h1>
    <form @submit.prevent="handleRegister">
      <div>
        <label for="name">Name</label>
        <input id="name" v-model="name" type="text" required />
      </div>
      <div>
        <label for="username">Username</label>
        <input id="username" v-model="username" type="text" required />
      </div>

      <div>
        <label for="password">Password</label>
        <input id="password" v-model="password" type="password" required />
      </div>

      <div>
        <label for="confirmPassword">Confirm Password</label>
        <input id="confirmPassword" v-model="confirmPassword" type="password" required />
      </div>

      <button type="submit">Register</button>
    </form>
  </div>
</template>

<style scoped>
  .register-page {
    max-width: 400px;
    margin: 2rem auto;
    padding: 2rem;
    border: 1px solid #ccc;
    border-radius: 8px;
  }
  .register-page h1 {
    margin-bottom: 1rem;
    text-align: center;
  }
  .register-page form div {
    margin-bottom: 1rem;
  }
  .register-page label {
    display: block;
    margin-bottom: 0.5rem;
  }
  .register-page input {
    width: 100%;
    padding: 0.5rem;
  }
  .register-page button {
    width: 100%;
    padding: 0.75rem;
  }
</style>
