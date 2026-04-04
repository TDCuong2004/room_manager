<template>
  <div class="dashboard">

    <div class="sidebar">
      <h3>Dashboard</h3>

      <button @click="goBuildings">
        🏢 Buildings
      </button>
      <button @click="goServices">
        🛠 Services
      </button>
      <button @click="goInvoices">
        💰 Invoices
      </button>
    </div>

    <div class="content">

      <BuildingManager
        v-if="currentView === 'buildings'"
        @openBuildingServices="openBuildingServices"
        @openRooms="openRooms"
      />
      <RoomManager
        v-if="currentView === 'rooms'"
        :buildingId="buildingId"
        @back="goBuildings"
      />
      <Services v-if="currentView === 'services'" />
      <InvoicePage v-if="currentView === 'invoices'" />
      <BuildingServices
        v-if="currentView === 'buildingServices'"
        :buildingId="buildingId"
        @backToBuildings="goBuildings"
      />

    </div>

  </div>
</template>

<script setup>
import { ref } from "vue"
import BuildingManager from "@/views/BuildingManager.vue"
import Services from "@/views/ServicePage.vue"
import BuildingServices from "@/views/BuildingServices.vue"
import RoomManager from "@/views/RoomManager.vue"
import InvoicePage from "@/views/InvoicePage.vue"
const currentView = ref("buildings")
const buildingId = ref(null)

const openRooms = (id) => {
  buildingId.value = id
  currentView.value = "rooms"
}

const openBuildingServices = (id) => {
  buildingId.value = id
  currentView.value = "buildingServices"
}

const goBuildings = () => {
  currentView.value = "buildings"
}

const goServices = () => {
  currentView.value = "services"
}
const goInvoices = () => {
  currentView.value = "invoices"
}
</script>

<style scoped>
.dashboard {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 220px;
  background: #2c3e50;
  padding: 20px;
  color: white;
}

.sidebar button{
  width:100%;
  padding:10px;
  margin-top:10px;
  border:none;
  background:#34495e;
  color:white;
  cursor:pointer;
  border-radius:6px;
}

.sidebar button:hover{
  background:#1abc9c;
}

.content {
  flex: 1;
  padding: 30px;
  background: #f5f6fa;
}
</style>