<template>
  <div class="flex h-screen bg-gray-100 font-sans text-gray-900">
    
    <aside class="w-64 bg-slate-800 text-white flex flex-col shadow-xl shrink-0">
      <div class="p-6 border-b border-slate-700">
        <h3 class="text-xl font-black tracking-widest uppercase text-emerald-400">
          Dashboard
        </h3>
      </div>

      <nav class="flex-1 p-4 space-y-2 overflow-y-auto custom-scrollbar">
        <button 
          @click="goBuildings" 
          :class="[navItemClass, currentView === 'buildings' || currentView === 'rooms' ? activeClass : inactiveClass]"
        >
          <span class="text-lg">🏢</span> Buildings
        </button>

        <button 
          @click="goServices" 
          :class="[navItemClass, currentView === 'services' ? activeClass : inactiveClass]"
        >
          <span class="text-lg">🛠</span> Services
        </button>

        <button 
          @click="goUtility" 
          :class="[navItemClass, currentView === 'utility' ? activeClass : inactiveClass]"
        >
          <span class="text-lg">⚡</span> Utility Readings
        </button>

        <button 
          @click="goInvoices" 
          :class="[navItemClass, currentView === 'invoices' ? activeClass : inactiveClass]"
        >
          <span class="text-lg">💰</span> Invoices
        </button>
        <button 
          @click="goPosts" 
          :class="[navItemClass, currentView === 'posts' ? activeClass : inactiveClass]"
        >
          <span class="text-lg">📝</span> Posts
        </button>
      </nav>

      <div class="p-4 border-t border-slate-700 bg-slate-900/50">
        <div class="flex items-center gap-3">
          <div class="w-8 h-8 rounded-full bg-emerald-500 flex items-center justify-center font-bold">AD</div>
          <p class="text-xs font-semibold text-slate-400 uppercase tracking-tighter text-ellipsis overflow-hidden">Admin Manager</p>
        </div>
      </div>
    </aside>

    <main class="flex-1 overflow-y-auto bg-[#f8fafc] p-8 custom-scrollbar relative">
      <div class="max-w-7xl mx-auto">
        
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

        <UtilityReadings v-if="currentView === 'utility'" />
        
        <Services v-if="currentView === 'services'" />
        
        <InvoicePage v-if="currentView === 'invoices'" />
        <PostManager v-if="currentView === 'posts'" />
        <BuildingServices
          v-if="currentView === 'buildingServices'"
          :buildingId="buildingId"
          @backToBuildings="goBuildings"
        />

      </div>
    </main>

  </div>
</template>

<script setup>
import { ref } from "vue"
import BuildingManager from "@/views/BuildingManager.vue"
import Services from "@/views/ServicePage.vue"
import BuildingServices from "@/views/BuildingServices.vue"
import RoomManager from "@/views/RoomManager.vue"
import InvoicePage from "@/views/InvoicePage.vue"
import UtilityReadings from "@/views/UtilityReadings.vue"
import PostManager from "@/views/PostManager.vue"
const currentView = ref("buildings")
const buildingId = ref(null)

// Class Tailwind cho Sidebar
const navItemClass = "w-full flex items-center gap-3 px-4 py-3 rounded-xl font-semibold transition-all duration-200 outline-none"
const activeClass = "bg-emerald-500 text-white shadow-lg shadow-emerald-500/20"
const inactiveClass = "text-slate-400 hover:bg-slate-700 hover:text-white"

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
const goUtility = () => {
  currentView.value = "utility"
}
const goServices = () => {
  currentView.value = "services"
}
const goInvoices = () => {
  currentView.value = "invoices"
}
const goPosts = () => {
  currentView.value = "posts"
}
</script>

<style scoped>
/* Custom Scrollbar cho Dashboard */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}
aside .custom-scrollbar::-webkit-scrollbar-thumb {
  background: #475569;
}
</style>