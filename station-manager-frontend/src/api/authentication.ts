import axiosInstance from './axios-config';

export async function login(username: string, password: string) {
  return axiosInstance.post('/authentication/login', { username, password });
}

export async function register(name: string, username: string, password: string) {
  return axiosInstance.post('/authentication/register', { name, username, password });
}
