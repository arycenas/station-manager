import axios from 'axios';

const API_URL = 'http://localhost:8080/api/stations';

export async function getStations() {
  return axios.get(API_URL);
}

export async function addStation(name: string, location: string) {
  return axios.post(API_URL, { name, location });
}
