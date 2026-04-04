<template>
<div class="rooms-page">

  <div class="rooms-header">

    <div class="header-left">
      <button class="back-btn" @click="goBack">
        ← Quay lại
      </button>
    </div>

    <h2 class="header-title">
      🏠 Danh sách phòng
    </h2>

    <div class="header-right">
      <button class="primary-btn" @click="openAdd">
        + Thêm phòng
      </button>
    </div>

  </div>

  <!-- LIST ROOM -->

  <div v-if="rooms.length" class="rooms-grid">

    <div
      v-for="room in rooms"
      :key="room.id"
      class="room-card"
    >

      <h3>{{ room.roomName }}</h3>

      <p>Mã: {{ room.roomCode }}</p>
      <p>Giá: {{ formatMoney(room.price) }} đ</p>
      <p>Diện tích: {{ room.area }} m²</p>
      <p>Số người tối đa: {{ room.maxPeople }}</p>
      <p>Số người hiện tại: {{ room.currentPeople }}</p>

      <p>
        Trạng thái:
        <span :class="statusClass(room.status)">
          {{ room.status }}
        </span>
      </p>

      <div class="room-actions">

        <button class="edit-btn" @click="openEdit(room)">
          Sửa
        </button>

        <button class="delete-btn" @click="deleteRoom(room.id)">
          Xóa
        </button>
        <button
          :class="['rent-btn', room.status === 'RENTED' ? 'rented' : '']"
          @click="room.status !== 'RENTED' && openRent(room)"
        >
          {{ room.status === 'RENTED' ? 'Đã có người thuê' : 'Cho thuê' }}
        </button>
        <button class="service-btn" @click="openMeter(room)">
          Dịch vụ
        </button>
      </div>

    </div>

  </div>

  <div v-else class="empty-state">
    Chưa có phòng nào.
  </div>

  <!-- ADD ROOM MODAL -->

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

  if(token){
    config.headers.Authorization = "Bearer " + token
  }

  return config

})

export default {

  components:{
    AddRoom,
    MeterModal
  },

  props:["buildingId"],

  data(){
    return{

      rooms:[],
      showModal:false,
      selectedRoom:null,
      showMeter:false,
    }
  },

  mounted(){
    this.fetchRooms()
  },

  methods:{

    async fetchRooms(){

      try{

        const res = await api.get(
          `/rooms/building/${this.buildingId}`
        )

        this.rooms = res.data

      }catch(err){

        console.error(err)

      }

    },
    openMeter(room){
      this.selectedRoom = room
      this.showMeter = true
    },
    goBack(){
      this.$emit("back")
    },

    openAdd(){

      this.selectedRoom = null
      this.showModal = true

    },

    openEdit(room){

      this.selectedRoom = room
      this.showModal = true

    },

    handleSaved(){

      this.showModal = false
      this.fetchRooms()

    },
    openRent(room){

  this.$router.push({
    path:`/rent/${room.id}`
  })

},
    async deleteRoom(id){

      if(!confirm("Xóa phòng này?")) return

      try{

        await api.delete(`/rooms/${id}`)

        this.fetchRooms()

      }catch(err){

        console.error(err)

      }

    },

    formatMoney(value){
      return new Intl.NumberFormat("vi-VN").format(value)
    },

    statusClass(status){

      if(status === "EMPTY") return "available"

      if(status === "RENTED") return "occupied"

      return "maintenance"

    }

  }

}
</script>

<style scoped>
.rooms-page{
  padding:50px 80px;
  background:#f3f4f6;
  min-height:100vh;
}

/* HEADER */

.rooms-header{
  display:grid;
  grid-template-columns:1fr 1fr 1fr;
  align-items:center;
  margin-bottom:40px;
}

.header-left{
  justify-self:start;
}

.header-title{
  text-align:center;
  font-size:26px;
  font-weight:700;
  color:#1f2937;
}

.header-right{
  justify-self:end;
}

.back-btn{
  background:#e5e7eb;
  border:none;
  padding:8px 14px;
  border-radius:8px;
  cursor:pointer;
}

.back-btn:hover{
  background:#d1d5db;
}

/* ADD BUTTON */

.primary-btn{
  background:linear-gradient(135deg,#ff385c,#ff7a18);
  border:none;
  color:white;
  padding:10px 18px;
  border-radius:25px;
  font-weight:600;
  cursor:pointer;
}

.primary-btn:hover{
  opacity:0.9;
}

/* GRID */

.rooms-grid{
  display:grid;
  grid-template-columns:repeat(auto-fill,minmax(260px,1fr));
  gap:30px;
}

/* CARD */

.room-card{
  background:white;
  padding:22px;
  border-radius:18px;
  box-shadow:0 8px 30px rgba(0,0,0,0.06);
  transition:0.25s;
}

.room-card:hover{
  transform:translateY(-6px);
}

/* ROOM TITLE */

.room-card h3{
  margin-bottom:8px;
  font-size:18px;
}

/* TEXT */

.room-card p{
  font-size:14px;
  color:#6b7280;
  margin:4px 0;
}

/* STATUS */

.available{
  color:#16a34a;
  font-weight:600;
}

.occupied{
  color:#dc2626;
  font-weight:600;
}

.maintenance{
  color:#f59e0b;
  font-weight:600;
}

/* ACTION BUTTONS */

.room-actions{
  margin-top:15px;
  display:flex;
  gap:10px;
}

.edit-btn{
  background:#3b82f6;
  color:white;
  border:none;
  padding:6px 12px;
  border-radius:8px;
  cursor:pointer;
}

.delete-btn{
  background:#ef4444;
  color:white;
  border:none;
  padding:6px 12px;
  border-radius:8px;
  cursor:pointer;
}

.edit-btn:hover{
  opacity:0.9;
}

.delete-btn:hover{
  opacity:0.9;
}
.rent-btn {
  background: #f59e0b; /* vàng */
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.2s;
}

.rent-btn:hover {
  opacity: 0.9;
}

/* khi đã thuê */
.rent-btn.rented {
  background: #9ca3af; /* xám */
  cursor: not-allowed;
}
/* EMPTY */

.empty-state{
  text-align:center;
  color:#9ca3af;
  margin-top:80px;
  font-size:16px;
}
.service-btn{
  background:#8b5cf6;
  color:white;
  border:none;
  padding:6px 12px;
  border-radius:8px;
  cursor:pointer;
}

.service-btn:hover{
  opacity:0.9;
}
</style>