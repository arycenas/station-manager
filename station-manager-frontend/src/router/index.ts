import { createRouter, createWebHistory } from 'vue-router';
import LoginPage from '../pages/LoginPage.vue';
import StationsPage from '../pages/StationsPage.vue';
import RegisterPage from '../pages/RegisterPage.vue';
import { useAuthenticationStore } from '../store/authentication';

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginPage },
  { path: '/register', component: RegisterPage },
  {
    path: '/stations',
    component: StationsPage,
    meta: { requiresAuth: true }, // Add authentication requirement
  },
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Navigation guard to protect authenticated routes
router.beforeEach((to, _from, next) => {
  const authStore = useAuthenticationStore();

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    // If route requires auth but user is not authenticated, redirect to login
    next('/login');
  } else if (to.path === '/login' && authStore.isAuthenticated) {
    // If user is already authenticated and tries to go to login, redirect to stations
    next('/stations');
  } else {
    next();
  }
});
