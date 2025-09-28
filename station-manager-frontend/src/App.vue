<script setup lang="ts">
  import { RouterView, useRouter } from 'vue-router';
  import { useAuthenticationStore } from './store/authentication';
  import { computed } from 'vue';

  const router = useRouter();
  const authStore = useAuthenticationStore();

  const isAuthenticated = computed(() => authStore.isAuthenticated);

  const handleLogout = () => {
    authStore.logout();
    router.push('/login');
  };
</script>

<template>
  <div id="app">
    <header>
      <div class="header-content">
        <h1>🚉 Station Manager</h1>
        <div v-if="isAuthenticated" class="user-info">
          <span>Welcome, {{ authStore.username }}!</span>
          <button class="logout-btn" @click="handleLogout">Logout</button>
        </div>
      </div>
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
    min-height: 100vh;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background: linear-gradient(135deg, #f6f8fc 0%, #e9ecef 100%);
  }

  header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 1rem 0;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    margin-bottom: 0;
  }

  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 2rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .header-content h1 {
    margin: 0;
    font-size: 1.8rem;
    font-weight: 700;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 1.5rem;
    font-size: 1rem;
    color: white;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    padding: 0.75rem 1.25rem;
    border-radius: 50px;
    border: 1px solid rgba(255, 255, 255, 0.2);
  }

  .user-info span {
    font-weight: 600;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
  }

  .logout-btn {
    background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
    color: white;
    border: none;
    padding: 0.6rem 1.2rem;
    border-radius: 25px;
    cursor: pointer;
    font-size: 0.9rem;
    font-weight: 600;
    transition: all 0.3s ease;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  }

  .logout-btn:hover {
    background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
    transform: translateY(-1px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  }

  .logout-btn:active {
    transform: translateY(0);
  }

  footer {
    background: #1f2937;
    color: #9ca3af;
    text-align: center;
    padding: 2rem 0;
    margin-top: 3rem;
    font-size: 0.9rem;
  }

  footer p {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 2rem;
  }

  @media (max-width: 768px) {
    .header-content {
      flex-direction: column;
      gap: 1.5rem;
      padding: 0 1rem;
    }

    .header-content h1 {
      font-size: 1.5rem;
    }

    .user-info {
      flex-direction: column;
      gap: 1rem;
      padding: 1rem;
      text-align: center;
    }

    .logout-btn {
      padding: 0.7rem 1.5rem;
    }
  }

  @media (max-width: 480px) {
    .user-info {
      padding: 0.75rem 1rem;
      gap: 0.75rem;
    }

    .user-info span {
      font-size: 0.9rem;
    }

    .logout-btn {
      padding: 0.6rem 1.2rem;
      font-size: 0.85rem;
    }
  }
</style>
