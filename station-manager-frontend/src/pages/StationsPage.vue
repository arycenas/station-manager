<script setup lang="ts">
  import { onMounted, ref, computed } from 'vue';
  import { useStationsStore } from '../store/stations';

  const stationsStore = useStationsStore();

  const newName = ref('');
  const newLocation = ref('');
  const searchQuery = ref('');

  // Notification system
  const notification = ref<{ type: 'success' | 'error'; message: string } | null>(null);

  // Track expanded routes for showing stop times
  const expandedRoutes = ref<Set<string>>(new Set());

  // Toggle route expansion
  function toggleRouteExpansion(stationUri: string, routeIndex: number) {
    const routeKey = `${stationUri}-${routeIndex}`;
    if (expandedRoutes.value.has(routeKey)) {
      expandedRoutes.value.delete(routeKey);
    } else {
      expandedRoutes.value.add(routeKey);
    }
  }

  // Check if route is expanded
  function isRouteExpanded(stationUri: string, routeIndex: number): boolean {
    return expandedRoutes.value.has(`${stationUri}-${routeIndex}`);
  }

  // Format departure time for display
  function formatDepartureTime(time: string): string {
    if (!time) return 'N/A';
    // Convert HH:MM:SS to more readable format
    const parts = time.split(':');
    if (parts.length >= 2) {
      return `${parts[0]}:${parts[1]}`;
    }
    return time;
  }

  // Format timestamp to readable date
  function formatTimestamp(timestamp: number): string {
    if (!timestamp) return 'N/A';
    return new Date(timestamp * 1000).toLocaleString();
  }

  // Show notification
  function showNotification(type: 'success' | 'error', message: string) {
    notification.value = { type, message };
    setTimeout(() => {
      notification.value = null;
    }, 5000); // Auto-hide after 5 seconds
  }

  // Clear notification
  function clearNotification() {
    notification.value = null;
  }

  // Computed properties from store
  const filteredStations = computed(() => stationsStore.searchStations(searchQuery.value));
  const isLoading = computed(() => stationsStore.isLoading);
  const error = computed(() => stationsStore.error);
  const stationsCount = computed(() => stationsStore.stationsCount);

  // Load stations when component mounts
  onMounted(async () => {
    await stationsStore.fetchStations();
  });

  // Create new station
  async function createStation() {
    if (!newName.value.trim() || !newLocation.value.trim()) {
      alert('Please fill in both station name and location');
      return;
    }

    try {
      await stationsStore.createStation({
        name: newName.value.trim(),
        location: newLocation.value.trim(),
      });

      // Clear form after successful creation
      newName.value = '';
      newLocation.value = '';
      showNotification('success', 'Station created successfully!');
    } catch (error) {
      console.error('Failed to create station:', error);
      showNotification('error', 'Failed to create station. Please try again.');
    }
  }

  // Save stations to backend
  async function saveStations() {
    try {
      await stationsStore.saveStations();
      // Auto-refresh stations list after successful save
      await stationsStore.fetchStations();
      // Show success message with count
      const count = stationsStore.stationsCount;
      showNotification(
        'success',
        `Stations saved successfully! ${count} stations loaded and displayed automatically.`
      );
    } catch (error) {
      console.error('Failed to save stations:', error);
      showNotification('error', 'Failed to save stations. Please try again.');
    }
  }

  // Refresh stations list
  async function refreshStations() {
    await stationsStore.fetchStations();
  }

  // Clear error message
  function clearError() {
    stationsStore.clearError();
  }
</script>

<template>
  <div class="stations-page">
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">Stations Management</h1>
        <p class="hero-subtitle">Manage your transport stations efficiently</p>
        <div class="stats-bar">
          <div class="stat-item">
            <span class="stat-number">{{ stationsCount }}</span>
            <span class="stat-label">Total Stations</span>
          </div>
          <div class="action-buttons">
            <button :disabled="isLoading" class="btn-secondary" @click="refreshStations">
              {{ isLoading ? 'Loading...' : '🔄 Refresh' }}
            </button>
            <button :disabled="isLoading" class="btn-primary" @click="saveStations">
              {{ isLoading ? 'Saving...' : '💾 Save Stations' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <!-- Error Message -->
      <div v-if="error" class="error-message">
        <span>{{ error }}</span>
        <button class="close-error" @click="clearError">×</button>
      </div>

      <!-- Notification -->
      <div v-if="notification" :class="['notification', `notification-${notification.type}`]">
        <span class="notification-icon">
          {{ notification.type === 'success' ? '✅' : '❌' }}
        </span>
        <span class="notification-message">{{ notification.message }}</span>
        <button class="close-notification" @click="clearNotification">×</button>
      </div>

      <!-- Search Section -->
      <div class="search-section">
        <div class="search-container">
          <div class="search-icon">🔍</div>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search stations by name..."
            class="search-input"
          />
        </div>
      </div>

      <!-- Stations List -->
      <div class="stations-container">
        <div v-if="isLoading" class="loading-state">
          <div class="loading-spinner" />
          <p>Loading stations...</p>
        </div>

        <div v-else-if="filteredStations.length === 0" class="empty-state">
          <div class="empty-icon">🚇</div>
          <h3>{{ searchQuery ? 'No stations found' : 'No stations available' }}</h3>
          <p>
            {{
              searchQuery
                ? 'Try adjusting your search criteria.'
                : 'Add some stations to get started.'
            }}
          </p>
        </div>

        <div v-else class="stations-grid">
          <div v-for="station in filteredStations" :key="station.stationUri" class="station-card">
            <div class="station-header">
              <h3 class="station-name">{{ station.stationName }}</h3>
              <div class="station-badge">{{ station.stationAgency }}</div>
            </div>
            <div class="station-details">
              <div class="detail-item">
                <span class="detail-label">URI:</span>
                <span class="detail-value">{{ station.stationUri }}</span>
              </div>
              <div class="detail-item routes-section">
                <span class="detail-label">Routes:</span>
                <div
                  v-if="station.stationRoutes && station.stationRoutes.length > 0"
                  class="routes-container"
                >
                  <div
                    v-for="(route, index) in station.stationRoutes"
                    :key="index"
                    class="route-item"
                  >
                    <div
                      class="route-header"
                      @click="toggleRouteExpansion(station.stationUri, index)"
                    >
                      <div class="route-title-section">
                        <span class="route-name">{{ (route as any).name || 'Unnamed Route' }}</span>
                        <span class="route-id">ID: {{ (route as any).routeGroupId || 'N/A' }}</span>
                      </div>
                      <div class="route-actions">
                        <span class="stop-times-count">
                          {{ (route as any).stopTimesCount || 0 }} stops
                        </span>
                        <span
                          class="expand-icon"
                          :class="{ expanded: isRouteExpanded(station.stationUri, index) }"
                        >
                          {{ isRouteExpanded(station.stationUri, index) ? '▼' : '▶' }}
                        </span>
                      </div>
                    </div>

                    <div class="route-details">
                      <span class="route-uri">{{ (route as any).uri || 'No URI' }}</span>
                    </div>

                    <!-- Expandable Stop Times Section -->
                    <div
                      v-if="isRouteExpanded(station.stationUri, index)"
                      class="stop-times-section"
                    >
                      <div class="stop-times-header">
                        <h4>🚏 Stop Times Schedule</h4>
                      </div>
                      <div
                        v-if="(route as any).stopTimes && (route as any).stopTimes.length > 0"
                        class="stop-times-list"
                      >
                        <div
                          v-for="(stopTime, stopIndex) in (route as any).stopTimes"
                          :key="stopIndex"
                          class="stop-time-item"
                        >
                          <div class="stop-time-info">
                            <div class="stop-time-primary">
                              <span class="departure-time"
                                >🕐 {{ formatDepartureTime((stopTime as any).departureTime) }}</span
                              >
                              <span class="service-id"
                                >Service #{{ (stopTime as any).serviceId || 'N/A' }}</span
                              >
                            </div>
                            <div class="stop-time-secondary">
                              <span class="timestamp"
                                >📅
                                {{ formatTimestamp((stopTime as any).departureTimestamp) }}</span
                              >
                              <span v-if="(stopTime as any).shape" class="shape"
                                >🛤️ {{ (stopTime as any).shape }}</span
                              >
                            </div>
                          </div>
                        </div>
                      </div>
                      <div v-else class="no-stop-times">
                        <span class="no-data-text">📭 No stop times available for this route</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-else class="no-routes">
                  <span class="no-routes-text">No routes available</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Add New Station Form -->
      <div class="add-station-section">
        <div class="form-container">
          <h2 class="form-title">Add New Station</h2>
          <form class="station-form" @submit.prevent="createStation">
            <div class="form-group">
              <label for="stationName">Station Name:</label>
              <input
                id="stationName"
                v-model="newName"
                type="text"
                placeholder="Enter station name"
                required
              />
            </div>
            <div class="form-group">
              <label for="stationLocation">Location:</label>
              <input
                id="stationLocation"
                v-model="newLocation"
                type="text"
                placeholder="Enter location"
                required
              />
            </div>
            <button type="submit" :disabled="isLoading" class="btn-primary btn-full">
              {{ isLoading ? 'Adding...' : '➕ Add Station' }}
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
  .stations-page {
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }

  /* Hero Section */
  .hero-section {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.9) 0%, rgba(118, 75, 162, 0.9) 100%);
    color: white;
    padding: 4rem 0 3rem;
    text-align: center;
  }

  .hero-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 2rem;
  }

  .hero-title {
    font-size: 3rem;
    font-weight: 700;
    margin: 0 0 1rem;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  }

  .hero-subtitle {
    font-size: 1.2rem;
    opacity: 0.9;
    margin: 0 0 3rem;
  }

  .stats-bar {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 2rem;
    flex-wrap: wrap;
  }

  .stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    padding: 1.5rem;
    border-radius: 12px;
    border: 1px solid rgba(255, 255, 255, 0.2);
  }

  .stat-number {
    font-size: 2.5rem;
    font-weight: 700;
    line-height: 1;
  }

  .stat-label {
    font-size: 0.9rem;
    opacity: 0.8;
    margin-top: 0.5rem;
  }

  .action-buttons {
    display: flex;
    gap: 1rem;
    flex-wrap: wrap;
  }

  /* Main Content */
  .main-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem;
    background: white;
    min-height: calc(100vh - 200px);
    margin-top: -2rem;
    border-radius: 24px 24px 0 0;
    box-shadow: 0 -10px 25px rgba(0, 0, 0, 0.1);
  }

  /* Buttons */
  .btn-primary,
  .btn-secondary {
    padding: 0.75rem 1.5rem;
    border-radius: 12px;
    font-weight: 600;
    font-size: 0.95rem;
    border: none;
    cursor: pointer;
    transition: all 0.3s ease;
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
  }

  .btn-primary {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  }

  .btn-primary:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(102, 126, 234, 0.6);
  }

  .btn-secondary {
    background: rgba(255, 255, 255, 0.2);
    color: white;
    border: 1px solid rgba(255, 255, 255, 0.3);
  }

  .btn-secondary:hover:not(:disabled) {
    background: rgba(255, 255, 255, 0.3);
    transform: translateY(-2px);
  }

  .btn-full {
    width: 100%;
    justify-content: center;
  }

  .btn-primary:disabled,
  .btn-secondary:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    transform: none;
  }

  /* Error Message */
  .error-message {
    background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
    color: #dc2626;
    padding: 1rem 1.5rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border: 1px solid #fca5a5;
  }

  .close-error {
    background: none;
    border: none;
    color: #dc2626;
    font-size: 1.5rem;
    cursor: pointer;
    padding: 0;
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  /* Notification Styles */
  .notification {
    padding: 1rem 1.5rem;
    border-radius: 12px;
    margin-bottom: 1rem;
    display: flex;
    align-items: center;
    gap: 0.75rem;
    font-weight: 500;
    animation: slideIn 0.3s ease-out;
  }

  .notification-success {
    background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
    color: #065f46;
    border: 1px solid #10b981;
  }

  .notification-error {
    background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
    color: #dc2626;
    border: 1px solid #f87171;
  }

  .notification-icon {
    font-size: 1.2rem;
  }

  .notification-message {
    flex: 1;
  }

  .close-notification {
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    padding: 0;
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: currentColor;
    opacity: 0.7;
    transition: opacity 0.2s;
  }

  .close-notification:hover {
    opacity: 1;
  }

  @keyframes slideIn {
    from {
      transform: translateY(-20px);
      opacity: 0;
    }
    to {
      transform: translateY(0);
      opacity: 1;
    }
  }

  /* Search Section */
  .search-section {
    margin-bottom: 2rem;
  }

  .search-container {
    position: relative;
    max-width: 500px;
    margin: 0 auto;
  }

  .search-icon {
    position: absolute;
    left: 1rem;
    top: 50%;
    transform: translateY(-50%);
    color: #6b7280;
    font-size: 1.2rem;
  }

  .search-input {
    width: 100%;
    padding: 1rem 1rem 1rem 3rem;
    border: 2px solid #e5e7eb;
    border-radius: 12px;
    font-size: 1rem;
    background: #f9fafb;
    transition: all 0.3s ease;
  }

  .search-input:focus {
    outline: none;
    border-color: #667eea;
    background: white;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
  }

  /* Stations Container */
  .stations-container {
    margin-bottom: 3rem;
  }

  .loading-state {
    text-align: center;
    padding: 4rem 2rem;
    color: #6b7280;
  }

  .loading-spinner {
    width: 40px;
    height: 40px;
    margin: 0 auto 1rem;
    border: 4px solid #e5e7eb;
    border-top: 4px solid #667eea;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }

  .empty-state {
    text-align: center;
    padding: 4rem 2rem;
    color: #6b7280;
    background: #f9fafb;
    border-radius: 12px;
    border: 2px dashed #d1d5db;
  }

  .empty-icon {
    font-size: 4rem;
    margin-bottom: 1rem;
    opacity: 0.6;
  }

  .empty-state h3 {
    margin: 0 0 1rem;
    color: #374151;
    font-size: 1.5rem;
  }

  .empty-state p {
    margin: 0;
    font-size: 1rem;
    opacity: 0.8;
  }

  /* Stations Grid */
  .stations-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 1.5rem;
    margin-bottom: 2rem;
  }

  .station-card {
    background: white;
    border: 1px solid #e5e7eb;
    border-radius: 16px;
    padding: 1.5rem;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
  }

  .station-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 4px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }

  .station-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
    border-color: #667eea;
  }

  .station-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 1rem;
    gap: 1rem;
  }

  .station-name {
    margin: 0;
    font-size: 1.25rem;
    font-weight: 700;
    color: #1f2937;
    line-height: 1.3;
    flex: 1;
  }

  .station-badge {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 0.375rem 0.75rem;
    border-radius: 20px;
    font-size: 0.75rem;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    white-space: nowrap;
    box-shadow: 0 2px 4px rgba(102, 126, 234, 0.3);
  }

  .station-details {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
  }

  .detail-item {
    display: flex;
    align-items: flex-start;
    gap: 0.5rem;
    font-size: 0.9rem;
  }

  .detail-label {
    font-weight: 600;
    color: #6b7280;
    min-width: 45px;
    flex-shrink: 0;
  }

  .detail-value {
    color: #374151;
    word-break: break-all;
    line-height: 1.4;
  }

  .detail-value.routes-count {
    background: #f3f4f6;
    padding: 0.25rem 0.5rem;
    border-radius: 12px;
    font-weight: 600;
    color: #4b5563;
    font-size: 0.8rem;
  }

  /* Routes Section Styles */
  .routes-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }

  .routes-container {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    width: 100%;
  }

  .route-item {
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 0.75rem;
    transition: all 0.2s ease;
  }

  .route-item:hover {
    border-color: #667eea;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
  }

  .route-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    cursor: pointer;
    user-select: none;
    transition: background-color 0.2s ease;
    padding: 0.5rem;
    border-radius: 6px;
    margin: -0.5rem -0.5rem 0.5rem -0.5rem;
  }

  .route-header:hover {
    background-color: rgba(102, 126, 234, 0.05);
  }

  .route-name {
    font-weight: 600;
    color: #1e40af;
    font-size: 0.9rem;
  }

  .route-id {
    background: #667eea;
    color: white;
    padding: 0.2rem 0.5rem;
    border-radius: 12px;
    font-size: 0.7rem;
    font-weight: 600;
  }

  .route-details {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 0.5rem;
  }

  .route-uri {
    color: #64748b;
    font-size: 0.75rem;
    font-family: monospace;
    background: #f1f5f9;
    padding: 0.2rem 0.4rem;
    border-radius: 4px;
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .stop-times-count {
    background: #10b981;
    color: white;
    padding: 0.2rem 0.5rem;
    border-radius: 12px;
    font-size: 0.7rem;
    font-weight: 600;
    white-space: nowrap;
  }

  .no-routes {
    width: 100%;
  }

  .no-routes-text {
    color: #9ca3af;
    font-style: italic;
    font-size: 0.85rem;
  }

  /* Enhanced Route Header for Expandable Interface */

  .route-title-section {
    display: flex;
    flex-direction: column;
    gap: 0.25rem;
    flex: 1;
  }

  .route-actions {
    display: flex;
    align-items: center;
    gap: 0.75rem;
  }

  .expand-icon {
    font-size: 0.8rem;
    color: #667eea;
    transition: transform 0.2s ease;
    min-width: 12px;
    text-align: center;
  }

  .expand-icon.expanded {
    transform: rotate(0deg);
  }

  /* Stop Times Section Styles */
  .stop-times-section {
    margin-top: 1rem;
    padding-top: 1rem;
    border-top: 2px solid #e5e7eb;
    animation: expandIn 0.3s ease-out;
  }

  @keyframes expandIn {
    from {
      opacity: 0;
      max-height: 0;
      padding-top: 0;
      margin-top: 0;
    }
    to {
      opacity: 1;
      max-height: 500px;
      padding-top: 1rem;
      margin-top: 1rem;
    }
  }

  .stop-times-header {
    margin-bottom: 0.75rem;
  }

  .stop-times-header h4 {
    margin: 0;
    font-size: 1rem;
    color: #374151;
    font-weight: 600;
  }

  .stop-times-list {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    max-height: 300px;
    overflow-y: auto;
  }

  .stop-time-item {
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 0.75rem;
    transition: all 0.2s ease;
  }

  .stop-time-item:hover {
    background: #f1f5f9;
    border-color: #cbd5e1;
    transform: translateX(2px);
  }

  .stop-time-info {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .stop-time-primary {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .departure-time {
    font-weight: 600;
    color: #1e40af;
    font-size: 0.95rem;
  }

  .service-id {
    background: #3b82f6;
    color: white;
    padding: 0.2rem 0.5rem;
    border-radius: 12px;
    font-size: 0.7rem;
    font-weight: 600;
  }

  .stop-time-secondary {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 0.8rem;
    color: #64748b;
  }

  .timestamp {
    font-family: monospace;
  }

  .shape {
    background: #f3f4f6;
    padding: 0.2rem 0.4rem;
    border-radius: 4px;
    font-size: 0.7rem;
  }

  .no-stop-times {
    padding: 2rem;
    text-align: center;
    background: #f9fafb;
    border-radius: 8px;
    border: 1px dashed #d1d5db;
  }

  .no-data-text {
    color: #6b7280;
    font-style: italic;
  }

  .stations-list ul {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .station-item {
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 1.5rem;
    margin-bottom: 1rem;
    transition: box-shadow 0.2s;
  }

  .station-item:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .station-info h3 {
    margin: 0 0 0.5rem 0;
    color: #1e40af;
    font-size: 1.25rem;
  }

  .station-info p {
    margin: 0.25rem 0;
    color: #4b5563;
    font-size: 0.9rem;
  }

  .add-station-section {
    background: #f8fafc;
    padding: 2rem;
    border-radius: 12px;
    border: 1px solid #e2e8f0;
  }

  .add-station-section h2 {
    margin-top: 0;
    color: #1f2937;
  }

  .station-form {
    display: grid;
    gap: 1.5rem;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }

  .form-group label {
    font-weight: 600;
    color: #374151;
  }

  .form-group input {
    padding: 0.75rem;
    border: 2px solid #e5e7eb;
    border-radius: 8px;
    font-size: 1rem;
    transition: border-color 0.2s;
  }

  .form-group input:focus {
    outline: none;
    border-color: #3b82f6;
  }

  .submit-btn {
    background: #10b981;
    color: white;
    border: none;
    padding: 0.75rem 1.5rem;
    border-radius: 8px;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.2s;
    justify-self: start;
  }

  .submit-btn:hover:not(:disabled) {
    background: #059669;
  }

  .submit-btn:disabled {
    background: #9ca3af;
    cursor: not-allowed;
  }

  /* Responsive design */
  @media (max-width: 768px) {
    .hero-title {
      font-size: 2rem;
    }

    .stats-bar {
      flex-direction: column;
      gap: 1rem;
    }

    .action-buttons {
      justify-content: center;
    }

    .main-content {
      padding: 1rem;
      margin-top: -1rem;
    }

    .stations-grid {
      grid-template-columns: 1fr;
      gap: 1rem;
    }

    .station-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 0.5rem;
    }

    .station-badge {
      align-self: flex-start;
    }

    .form-container {
      padding: 1rem;
    }
  }

  @media (max-width: 480px) {
    .hero-content {
      padding: 0 1rem;
    }

    .hero-title {
      font-size: 1.75rem;
    }

    .hero-subtitle {
      font-size: 1rem;
    }

    .stat-item {
      padding: 1rem;
    }

    .stat-number {
      font-size: 2rem;
    }

    .station-card {
      padding: 1rem;
    }

    .station-name {
      font-size: 1.1rem;
    }

    .route-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 0.5rem;
    }

    .route-details {
      flex-direction: column;
      align-items: flex-start;
      gap: 0.5rem;
    }

    .route-uri {
      max-width: 100%;
    }

    .route-actions {
      flex-direction: column;
      align-items: flex-end;
      gap: 0.5rem;
    }

    .stop-time-primary,
    .stop-time-secondary {
      flex-direction: column;
      align-items: flex-start;
      gap: 0.25rem;
    }

    .stop-times-list {
      max-height: 200px;
    }
  }
</style>
