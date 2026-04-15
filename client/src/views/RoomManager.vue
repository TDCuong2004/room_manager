<template>
  <div class="min-h-screen bg-slate-50 p-6 md:p-10 font-sans">
    <header class="grid grid-cols-1 md:grid-cols-3 items-center gap-4 mb-10">
      <div class="flex justify-start">
        <button 
          @click="goBack" 
          class="flex items-center gap-2 px-4 py-2 bg-white border border-gray-200 text-gray-600 rounded-xl hover:bg-gray-50 hover:shadow-sm transition-all font-medium"
        >
          <span>←</span> Quay lại
        </button>
      </div>

      <h2 class="text-2xl md:text-3xl font-black text-gray-800 text-center flex items-center justify-center gap-2">
        <span class="text-3xl">🏠</span> Danh sách phòng
      </h2>

      <div class="flex justify-end">
        <button 
          @click="openAdd" 
          class="px-6 py-2.5 bg-gradient-to-r from-rose-500 to-orange-500 text-white rounded-full font-bold shadow-lg shadow-rose-200 hover:scale-105 active:scale-95 transition-all"
        >
          + Thêm phòng mới
        </button>
      </div>
    </header>

    <div v-if="rooms.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
      <div 
        v-for="room in rooms" 
        :key="room.id" 
        class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 hover:shadow-xl hover:-translate-y-1 transition-all duration-300 group"
      >
        <div class="flex justify-between items-start mb-4">
          <h3 class="text-xl font-bold text-gray-800 group-hover:text-rose-500 transition-colors">
            {{ room.roomName }}
          </h3>
          <span :class="['px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-wider', statusBadge(room.status)]">
            {{ room.status === 'EMPTY' ? 'Trống' : room.status === 'RENTED' ? 'Đã thuê' : 'Bảo trì' }}
          </span>
        </div>

        <div class="space-y-2 text-sm text-gray-500 mb-6">
          <p class="flex justify-between"><span>Mã phòng:</span> <b class="text-gray-700">{{ room.roomCode }}</b></p>
          <p class="flex justify-between"><span>Giá thuê:</span> <b class="text-rose-600">{{ formatMoney(room.price) }} đ</b></p>
          <p class="flex justify-between"><span>Diện tích:</span> <b class="text-gray-700">{{ room.area }} m²</b></p>
          <div class="pt-2 border-t border-gray-50">
            <div class="flex items-center justify-between">
              <span>Sức chứa:</span>
              <span class="font-bold text-gray-700">{{ room.currentPeople }}/{{ room.maxPeople }} 👤</span>
            </div>
            <div class="w-full bg-gray-100 h-1.5 rounded-full mt-2 overflow-hidden">
              <div 
                class="h-full bg-blue-500 transition-all" 
                :style="{ width: (room.currentPeople / room.maxPeople * 100) + '%' }"
              ></div>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-2">
          <button @click="openEdit(room)" class="py-2 bg-blue-50 text-blue-600 rounded-lg font-bold text-xs hover:bg-blue-100 transition-colors">
            📝 Sửa
          </button>
          <button @click="deleteRoom(room.id)" class="py-2 bg-red-50 text-red-600 rounded-lg font-bold text-xs hover:bg-red-100 transition-colors">
            🗑️ Xóa
          </button>
          <button 
            @click="room.status !== 'RENTED' && openRent(room)"
            :disabled="room.status === 'RENTED'"
            :class="[
              'col-span-1 py-2 rounded-lg font-bold text-xs transition-all',
              room.status === 'RENTED' ? 'bg-gray-100 text-gray-400 cursor-not-allowed' : 'bg-orange-50 text-orange-600 hover:bg-orange-100'
            ]"
          >
            {{ room.status === 'RENTED' ? 'Đã thuê' : '🔑 Thuê' }}
          </button>
          <button @click="openMeter(room)" class="py-2 bg-purple-50 text-purple-600 rounded-lg font-bold text-xs hover:bg-purple-100 transition-colors">
            ⚡ Dịch vụ
          </button>
        </div>
      </div>
    </div>

    <div v-else class="flex flex-col items-center justify-center py-20 text-gray-400 bg-white rounded-3xl border-2 border-dashed border-gray-200">
      <span class="text-6xl mb-4">📭</span>
      <p class="text-lg font-medium">Hiện tại chưa có phòng nào được tạo.</p>
      <button @click="openAdd" class="mt-4 text-rose-500 font-bold hover:underline">Nhấn để thêm phòng đầu tiên</button>
    </div>

    <AddRoom
      v-if="showModal"
      :buildingId="buildingId"
      :room="selectedRoom"
      @close="showModal=false"
      @saved="handleSaved"
    />
    <MeterModal
      v-if="showMeter"
      :room="selectedRoom"
      @close="showMeter=false"
      @saved="handleSaved"
    />
  </div>
</template>

<script>
import axios from "axios"
import AddRoom from "@/Model/AddRoom.vue"
import MeterModal from "@/Model/MeterModal.vue"

const api = axios.create({
  baseURL: "http://localhost:3000/api"
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem("token")
  if(token) config.headers.Authorization = "Bearer " + token
  return config
})

export default {
  components: { AddRoom, MeterModal },
  props: ["buildingId"],
  data() {
    return {
      rooms: [],
      showModal: false,
      selectedRoom: null,
      showMeter: false,
    }
  },
  mounted() {
    this.fetchRooms()
  },
  methods: {
    async fetchRooms() {
      try {
        const res = await api.get(`/rooms/building/${this.buildingId}`)
        this.rooms = res.data
      } catch (err) { console.error(err) }
    },
    openMeter(room) {
      this.selectedRoom = room
      this.showMeter = true
    },
    goBack() { this.$emit("back") },
    openAdd() {
          this.selectedRoom = null
          this.showModal = true
        },
        async openEdit(room) {
      try {
        const res = await api.get(`/rooms/${room.id}`) 
        this.selectedRoom = res.data
        this.showModal = true
      } catch (err) {
        console.error(err)
      }
    },
    handleSaved() {
      this.showModal = false
      this.fetchRooms()
    },
    openRent(room) {
      this.$router.push({ path: `/rent/${room.id}` })
    },
    async deleteRoom(id) {
      if(!confirm("Xóa phòng này?")) return
      try {
        await api.delete(`/rooms/${id}`)
        this.fetchRooms()
      } catch (err) { console.error(err) }
    },
    formatMoney(value) {
      return new Intl.NumberFormat("vi-VN").format(value)
    },
    statusBadge(status) {
      if (status === "EMPTY") return "bg-green-100 text-green-600"
      if (status === "RENTED") return "bg-red-100 text-red-600"
      return "bg-amber-100 text-amber-600"
    }
  }
}
</script>

<style scoped>
/* Tailwind handles almost everything. 
   Custom animations can be added here if needed. */
</style>