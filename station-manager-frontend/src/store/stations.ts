import { defineStore } from 'pinia';
import { getStations, addStation } from '../api/stations';

// Define the StopTime interface
export interface StopTime {
  serviceId: number;
  departureTime: string;
  departureTimestamp: number;
  shape: string;
}

// Define the Route interface
export interface StationRoute {
  routeGroupId: string;
  uri: string;
  name: string;
  stopTimesCount: number;
  stopTimes: StopTime[];
}

// Define the Station interface to match backend structure
export interface Station {
  stationUri: string;
  stationAgency: string;
  stationName: string;
  stationRoutes: StationRoute[];
}

// Define the state interface
interface StationsState {
  stations: Station[];
  isLoading: boolean;
  error: string | null;
}

export const useStationsStore = defineStore('stations', {
  state: (): StationsState => ({
    stations: [],
    isLoading: false,
    error: null,
  }),

  getters: {
    // Get stations count
    stationsCount: (state) => state.stations.length,

    // Get stations by agency
    getStationsByAgency: (state) => (agency: string) => {
      return state.stations.filter((station) => station.stationAgency === agency);
    },

    // Search stations by name
    searchStations: (state) => (query: string) => {
      if (!query.trim()) return state.stations;
      return state.stations.filter((station) =>
        station.stationName.toLowerCase().includes(query.toLowerCase())
      );
    },
  },

  actions: {
    // Fetch all stations from API
    async fetchStations() {
      this.isLoading = true;
      this.error = null;

      try {
        // Debug: Check if token exists and test JWT validation
        const token = localStorage.getItem('auth_token');
        console.log(
          'Fetching stations with token:',
          token ? token.substring(0, 20) + '...' : 'No token'
        );

        const response = await getStations();
        console.log('Stations API response:', response);
        console.log('Response data structure:', JSON.stringify(response.data, null, 2));

        // Check if response has the expected structure
        let stationsData;
        if (response.data && response.data.data) {
          // Backend returns SuccessResponse with data field
          stationsData = response.data.data;
        } else {
          // Fallback in case the structure is different
          stationsData = response.data || [];
        }

        console.log('Parsed stations data:', stationsData);
        this.stations = Array.isArray(stationsData) ? stationsData : [];
        console.log('Stations loaded successfully:', this.stations.length);
      } catch (error) {
        console.error('Failed to fetch stations:', error);
        this.error = 'Failed to load stations. Please try again.';
        this.stations = [];
      } finally {
        this.isLoading = false;
      }
    },

    // Add a new station
    async createStation(stationData: { name: string; location: string }) {
      this.isLoading = true;
      this.error = null;

      try {
        const response = await addStation(stationData.name, stationData.location);

        // Refresh stations list after adding
        await this.fetchStations();

        console.log('Station created successfully');
        return response;
      } catch (error) {
        console.error('Failed to create station:', error);
        this.error = 'Failed to create station. Please try again.';
        throw error;
      } finally {
        this.isLoading = false;
      }
    },

    // Clear error message
    clearError() {
      this.error = null;
    },

    // Save stations to backend
    async saveStations() {
      this.isLoading = true;
      this.error = null;

      try {
        const { saveStations } = await import('../api/stations');
        const response = await saveStations();
        console.log('Stations saved successfully');
        return response;
      } catch (error) {
        console.error('Failed to save stations:', error);
        this.error = 'Failed to save stations. Please try again.';
        throw error;
      } finally {
        this.isLoading = false;
      }
    },

    // Reset store state
    resetStore() {
      this.stations = [];
      this.isLoading = false;
      this.error = null;
    },
  },
});
