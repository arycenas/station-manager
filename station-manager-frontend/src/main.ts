import { createApp } from 'vue';
import './style.css';
import App from './App.vue';
import { createPinia } from 'pinia';
import { router } from './router';
import { useAuthenticationStore } from './store/authentication';

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
app.use(router);

// Initialize authentication state from localStorage
const authStore = useAuthenticationStore();
authStore.initializeAuth();

app.mount('#app');
