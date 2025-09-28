import axiosInstance from './axios-config';

export async function getStations() {
  return axiosInstance.get('/stations');
}

export async function addStation(name: string, location: string) {
  return axiosInstance.post('/stations/save', { name, location });
}

export async function saveStations() {
  return axiosInstance.post('/stations/save');
}
