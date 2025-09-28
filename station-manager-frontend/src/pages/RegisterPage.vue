<script setup lang="ts">
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { useAuthenticationStore } from '../store/authentication';

  const name = ref('');
  const username = ref('');
  const password = ref('');
  const confirmPassword = ref('');
  const errorMessage = ref('');
  const isLoading = ref(false);

  const router = useRouter();
  const authStore = useAuthenticationStore();

  const validateForm = (): boolean => {
    errorMessage.value = '';

    if (!name.value.trim()) {
      errorMessage.value = 'Name is required';
      return false;
    }

    if (!username.value.trim()) {
      errorMessage.value = 'Username is required';
      return false;
    }

    if (password.value.length < 6) {
      errorMessage.value = 'Password must be at least 6 characters long';
      return false;
    }

    if (password.value !== confirmPassword.value) {
      errorMessage.value = 'Passwords do not match';
      return false;
    }

    return true;
  };

  const handleRegister = async () => {
    if (!validateForm()) {
      return;
    }

    isLoading.value = true;
    errorMessage.value = '';

    try {
      await authStore.registerUser(name.value, username.value, password.value);
      // Redirect to login page after successful registration
      router.push('/login');
    } catch (error) {
      console.error('Registration failed:', error);
      errorMessage.value = 'Registration failed. Please try again.';
    } finally {
      isLoading.value = false;
    }
  };
</script>

<template>
  <div class="page-background">
    <div class="register-page">
      <div class="form-header">
        <h1>Create Account</h1>
        <p class="subtitle">Join us and manage your stations</p>
      </div>

      <div v-if="errorMessage" class="error-message">
        <svg class="error-icon" viewBox="0 0 20 20" fill="currentColor">
          <path
            fill-rule="evenodd"
            d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
            clip-rule="evenodd"
          />
        </svg>
        {{ errorMessage }}
      </div>

      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="name">Full Name</label>
          <input
            id="name"
            v-model="name"
            type="text"
            :disabled="isLoading"
            required
            placeholder="Enter your full name"
          />
        </div>

        <div class="form-group">
          <label for="username">Username</label>
          <input
            id="username"
            v-model="username"
            type="text"
            :disabled="isLoading"
            required
            placeholder="Choose a username"
          />
        </div>

        <div class="form-group">
          <label for="password">Password</label>
          <input
            id="password"
            v-model="password"
            type="password"
            :disabled="isLoading"
            required
            placeholder="Create a secure password"
          />
        </div>

        <div class="form-group">
          <label for="confirmPassword">Confirm Password</label>
          <input
            id="confirmPassword"
            v-model="confirmPassword"
            type="password"
            :disabled="isLoading"
            required
            placeholder="Confirm your password"
          />
        </div>

        <button type="submit" :disabled="isLoading" class="submit-button">
          <span v-if="!isLoading">Create Account</span>
          <span v-else class="loading-content">
            <svg class="loading-spinner" viewBox="0 0 24 24">
              <circle
                cx="12"
                cy="12"
                r="10"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                opacity="0.3"
              />
              <path
                d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                fill="currentColor"
              />
            </svg>
            Creating Account...
          </span>
        </button>
      </form>

      <p class="login-link">
        Already have an account?
        <router-link to="/login">Sign in here</router-link>
      </p>
    </div>
  </div>
</template>

<style scoped>
  .page-background {
    min-height: 100vh;
    background: linear-gradient(135deg, #f0f9ff 0%, #e0e7ff 50%, #fdf2f8 100%);
    padding: 2rem 1rem;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow-x: hidden;
  }

  .page-background::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image:
      radial-gradient(circle at 25% 25%, rgba(139, 92, 246, 0.05) 0%, transparent 50%),
      radial-gradient(circle at 75% 75%, rgba(236, 72, 153, 0.05) 0%, transparent 50%),
      radial-gradient(circle at 50% 50%, rgba(59, 130, 246, 0.03) 0%, transparent 50%);
    z-index: -1;
  }

  .register-page {
    max-width: 420px;
    width: 100%;
    padding: 2.5rem;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(139, 92, 246, 0.1);
    border-radius: 24px;
    box-shadow:
      0 20px 40px rgba(139, 92, 246, 0.1),
      0 8px 16px rgba(0, 0, 0, 0.05);
    position: relative;
    animation: slideUp 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  }

  .form-header {
    text-align: center;
    margin-bottom: 2.5rem;
  }

  .form-header h1 {
    margin: 0 0 0.5rem 0;
    color: #4c1d95;
    font-size: 2.25rem;
    font-weight: 700;
    background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 50%, #ec4899 100%);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    letter-spacing: -0.02em;
  }

  .subtitle {
    color: #6b7280;
    font-size: 1rem;
    margin: 0;
    font-weight: 400;
  }

  .error-message {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    background: linear-gradient(135deg, #fef2f2 0%, #fdf2f8 100%);
    color: #be185d;
    padding: 1rem 1.25rem;
    border-radius: 16px;
    margin-bottom: 1.5rem;
    border: 1px solid rgba(244, 114, 182, 0.3);
    font-size: 0.9rem;
    font-weight: 500;
    box-shadow: 0 4px 12px rgba(190, 24, 93, 0.08);
  }

  .error-icon {
    width: 20px;
    height: 20px;
    flex-shrink: 0;
  }

  .form-group {
    margin-bottom: 1.5rem;
    animation: fadeInUp 0.6s cubic-bezier(0.4, 0, 0.2, 1);
    animation-fill-mode: both;
  }

  .form-group:nth-child(1) {
    animation-delay: 0.1s;
  }

  .form-group:nth-child(2) {
    animation-delay: 0.2s;
  }

  .form-group:nth-child(3) {
    animation-delay: 0.3s;
  }

  .form-group:nth-child(4) {
    animation-delay: 0.4s;
  }

  .form-group label {
    display: block;
    margin-bottom: 0.75rem;
    font-weight: 600;
    color: #374151;
    font-size: 0.95rem;
    letter-spacing: 0.01em;
  }

  .form-group input {
    width: 100%;
    padding: 1rem 1.25rem;
    border: 2px solid #e5e7eb;
    border-radius: 16px;
    font-size: 1rem;
    background: rgba(255, 255, 255, 0.8);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-sizing: border-box;
    color: #374151;
  }

  .form-group input::placeholder {
    color: #9ca3af;
    font-weight: 400;
  }

  .form-group input:focus {
    outline: none;
    border-color: #8b5cf6;
    background: rgba(255, 255, 255, 0.95);
    box-shadow:
      0 0 0 3px rgba(139, 92, 246, 0.1),
      0 4px 12px rgba(139, 92, 246, 0.15);
    transform: translateY(-2px);
  }

  .form-group input:disabled {
    background: rgba(243, 244, 246, 0.8);
    cursor: not-allowed;
    opacity: 0.7;
    border-color: #d1d5db;
  }

  .submit-button {
    width: 100%;
    padding: 1rem 1.5rem;
    background: linear-gradient(135deg, #8b5cf6 0%, #a855f7 50%, #c084fc 100%);
    color: white;
    border: none;
    border-radius: 16px;
    font-size: 1.1rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 8px 20px rgba(139, 92, 246, 0.3);
    letter-spacing: 0.01em;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
  }

  .submit-button:hover:not(:disabled) {
    background: linear-gradient(135deg, #7c3aed 0%, #9333ea 50%, #a855f7 100%);
    transform: translateY(-3px);
    box-shadow: 0 12px 28px rgba(139, 92, 246, 0.4);
  }

  .submit-button:active:not(:disabled) {
    transform: translateY(-1px);
    box-shadow: 0 8px 20px rgba(139, 92, 246, 0.3);
  }

  .submit-button:disabled {
    background: linear-gradient(135deg, #9ca3af 0%, #d1d5db 100%);
    cursor: not-allowed;
    transform: none;
    box-shadow: 0 4px 12px rgba(156, 163, 175, 0.2);
  }

  .loading-content {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }

  .loading-spinner {
    width: 20px;
    height: 20px;
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }

  .login-link {
    text-align: center;
    margin-top: 2rem;
    color: #6b7280;
    font-size: 0.95rem;
  }

  .login-link a {
    color: #8b5cf6;
    text-decoration: none;
    font-weight: 600;
    background: linear-gradient(135deg, #8b5cf6 0%, #a855f7 100%);
    -webkit-background-clip: text;
    background-clip: text;
    -webkit-text-fill-color: transparent;
    transition: all 0.3s ease;
    position: relative;
  }

  .login-link a::after {
    content: '';
    position: absolute;
    bottom: -3px;
    left: 0;
    width: 0;
    height: 2px;
    background: linear-gradient(135deg, #8b5cf6 0%, #a855f7 100%);
    transition: width 0.3s ease;
    border-radius: 1px;
  }

  .login-link a:hover::after {
    width: 100%;
  }

  @keyframes slideUp {
    from {
      opacity: 0;
      transform: translateY(30px) scale(0.95);
    }
    to {
      opacity: 1;
      transform: translateY(0) scale(1);
    }
  }

  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(15px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  /* Responsive design */
  @media (max-width: 480px) {
    .page-background {
      padding: 1rem;
    }

    .register-page {
      padding: 2rem;
    }

    .form-header h1 {
      font-size: 1.875rem;
    }
  }
</style>
