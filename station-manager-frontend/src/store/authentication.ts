import { defineStore } from 'pinia';
import { login, register } from '../api/authentication';

export const useAuthenticationStore = defineStore('authentication', {
  state: () => ({
    token: '' as string,
    username: '' as string,
  }),
  actions: {
    async loginUser(username: string, password: string) {
      const response = await login(username, password);
      this.token = response.data.token;
      this.username = username;
    },
    async registerUser(name: string, username: string, password: string) {
      await register(name, username, password);
    },
  },
});
