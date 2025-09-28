import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance
const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

// Request interceptor to add auth token
axiosInstance.interceptors.request.use(
  (config) => {
    // Get token from localStorage directly to avoid Pinia context issues
    const token = localStorage.getItem('auth_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('Axios: Adding Authorization header:', `Bearer ${token.substring(0, 20)}...`);
      console.log('Axios: Full token (first 50 chars):', token.substring(0, 50));
      console.log('Axios: Request URL:', config.url);
      console.log('Axios: Request method:', config.method);

      // Add debug test for JWT validation if this is a stations request
      if (config.url?.includes('/stations')) {
        console.log('Axios: This is a stations request - we can test JWT at /api/debug/test-token');
      }
    } else {
      console.log('Axios: No token found in localStorage');
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle token expiration
axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('Axios response error:', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      headers: error.response?.headers,
      url: error.config?.url,
    });

    if (error.response?.status === 401) {
      // Token expired or invalid - clear local storage
      localStorage.removeItem('auth_token');
      localStorage.removeItem('refresh_token');
      localStorage.removeItem('username');

      // Redirect to login page
      window.location.href = '/login';
    } else if (error.response?.status === 403) {
      console.error('Access forbidden - check JWT token and server logs');
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
