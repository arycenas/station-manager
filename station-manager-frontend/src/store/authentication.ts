import { defineStore } from 'pinia';
import { login, register } from '../api/authentication';
import axios from 'axios';

export const useAuthenticationStore = defineStore('authentication', {
  state: () => ({
    token: localStorage.getItem('auth_token') || '',
    refreshToken: localStorage.getItem('refresh_token') || '',
    username: localStorage.getItem('username') || '',
    isAuthenticated: false,
  }),
  actions: {
    async loginUser(username: string, password: string) {
      try {
        const response = await login(username, password);

        // Extract token from the nested response structure
        const jwtResponse = response.data.data; // SuccessResponse.data contains JwtResponse

        this.token = jwtResponse.token;
        this.refreshToken = jwtResponse.refreshToken;
        this.username = username;
        this.isAuthenticated = true;

        // Store in localStorage for persistence
        localStorage.setItem('auth_token', this.token);
        localStorage.setItem('refresh_token', this.refreshToken);
        localStorage.setItem('username', username);

        // Set default authorization header for all future requests
        this.setAuthHeader();

        console.log(response.data.message);

        return response;
      } catch (error) {
        this.logout();
        throw error;
      }
    },

    async registerUser(name: string, username: string, password: string) {
      const response = await register(name, username, password);
      return response;
    },

    logout() {
      this.token = '';
      this.refreshToken = '';
      this.username = '';
      this.isAuthenticated = false;

      // Clear localStorage
      localStorage.removeItem('auth_token');
      localStorage.removeItem('refresh_token');
      localStorage.removeItem('username');

      // Remove authorization header
      delete axios.defaults.headers.common['Authorization'];
    },

    setAuthHeader() {
      if (this.token) {
        axios.defaults.headers.common['Authorization'] = `Bearer ${this.token}`;
      }
    },

    initializeAuth() {
      // Initialize authentication state from localStorage
      const storedToken = localStorage.getItem('auth_token');
      if (storedToken && storedToken.length > 0) {
        this.token = storedToken;
        this.refreshToken = localStorage.getItem('refresh_token') || '';
        this.username = localStorage.getItem('username') || '';
        this.isAuthenticated = true;
        this.setAuthHeader();
        console.log('Auth initialized with token:', this.token.substring(0, 20) + '...');
      } else {
        this.isAuthenticated = false;
        console.log('No valid token found in localStorage');
      }
    },
  },
});
