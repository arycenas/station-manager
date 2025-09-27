import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

export async function login(username: string, password: string) {
  return axios.post(`${API_URL}/auth/login`, { username, password });
}

export async function register(name: string, username: string, password: string) {
  return axios.post(`${API_URL}/auth/register`, { name, username, password });
}
