<script lang="ts" setup>
  import { onMounted, ref } from 'vue';
  import { addStation, getStations } from '../api/stations';

  const stations = ref<{ id: string; name: string; location: string }[]>([]);
  const newName = ref('');
  const newLocation = ref('');

  async function loadStations() {
    const response = await getStations();
    stations.value = response.data;
  }

  async function createStation() {
    await addStation(newName.value, newLocation.value);
    await loadStations();
  }

  onMounted(() => {
    createStation();
  });
</script>

<template>
  <div class="stations-page">
    <h1>Stations</h1>
    <ul>
      <li v-for="s in stations" :key="s.id">{{ s.name }} - {{ s.location }}</li>
    </ul>

    <h2>Add New Station</h2>
    <input v-model="newName" placeholder="Station Name" />
    <input v-model="newLocation" placeholder="Location" />
    <button @click="createStation">Add</button>
  </div>
</template>
