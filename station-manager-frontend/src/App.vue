<script setup lang="ts">
  import { RouterView } from 'vue-router';
</script>

<template>
  <div id="app">
    <header>
      <h1>🚉 Station Manager</h1>
    </header>

    <!-- Halaman dari router akan muncul di sini -->
    <RouterView />

    <footer>
      <p>© 2025 Station Manager</p>
    </footer>
  </div>
</template>

<style scoped>
  #app {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
    font-family: Arial, sans-serif;
  }

  header {
    margin-bottom: 20px;
    text-align: center;
  }

  footer {
    margin-top: 40px;
    text-align: center;
    font-size: 0.8rem;
    color: #666;
  }
</style>
