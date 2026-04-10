<template>
  <div class="container">

    <!-- CREATE POST -->
    <div class="create-box" @click="openModal">
      <img
        class="avatar"
        :src="currentUser?.avatar || 'https://i.pravatar.cc/40'"
      />
      <input placeholder="Bạn đang nghĩ gì thế?" readonly />
    </div>

    <!-- POSTS -->
    <div class="post" v-for="post in posts" :key="post.id">

      <div class="post-header">
  <img
    class="avatar"
    :src="post.userAvatar || 'https://i.pravatar.cc/40'"
  />

  <div>
    <div class="name">
      {{ post.userName || "Người dùng" }}
          </div>
          <div class="time">{{ formatTime(post.createdAt) }}</div>
        </div>
      </div>
      <p class="phone" v-if="post.phone">
        📞 {{ post.phone }}
      </p>
      <div class="post-body">
        <h3>{{ post.title }}</h3>
        <p>{{ post.content }}</p>

        <!-- 📍 ADDRESS -->
        <p class="address" v-if="post.address">
          📍 {{ post.address }}
        </p>

        <!-- 🏠 ROOM -->
        <div v-if="post.room" class="room-tag">
          🏠 {{ post.room.roomName }} • {{ post.room.price }}đ
        </div>
      </div>

      <!-- 🗺️ MAP -->
      <div v-if="post.address" class="map-container">
        <iframe
          :src="getMapEmbedUrl(post.address)"
          width="100%"
          height="200"
          style="border:0; border-radius:10px"
          loading="lazy">
        </iframe>
      </div>

      <!-- 🖼️ IMAGES -->
      <div v-if="post.images?.length" class="images">
        <img v-for="img in post.images" :src="img" :key="img" />
      </div>

    </div>

    <!-- MODAL -->
    <div v-if="showModal" class="overlay" @click.self="closeModal">
      <div class="modal">

        <h2>Tạo bài viết</h2>

        <input v-model="form.title" placeholder="Tiêu đề" />
        <textarea v-model="form.content" placeholder="Bạn đang nghĩ gì?"></textarea>

        <!-- CHỌN TÒA -->
        <select v-model="selectedBuilding">
          <option value="">-- Chọn tòa --</option>
          <option v-for="b in buildings" :key="b.id" :value="b.id">
            {{ b.buildingName }}
          </option>
        </select>

        <!-- CHỌN PHÒNG -->
        <select v-model="form.roomId">
          <option value="">-- Chọn phòng --</option>
          <option v-for="r in filteredRooms" :key="r.id" :value="r.id">
            {{ r.roomName }} - {{ r.price }}đ
          </option>
        </select>

        <!-- ROOM INFO -->
        <div v-if="selectedRoom" class="room-preview">
          <h4>Thông tin phòng</h4>
          <p>🏠 {{ selectedRoom.roomName }}</p>
          <p>💰 {{ selectedRoom.price }}đ</p>
          <p>📐 {{ selectedRoom.area }} m²</p>
          <p>👤 {{ selectedRoom.maxPeople }} người</p>
        </div>

        <!-- ADDRESS -->
        <p v-if="selectedBuildingInfo">
          📍 {{ selectedBuildingInfo.address }}
        </p>

        <!-- MAP -->
        <div v-if="selectedBuildingInfo" class="map-container">
          <iframe
            :src="getMapEmbedUrl(selectedBuildingInfo.address)"
            width="100%"
            height="250"
            style="border:0; border-radius:10px"
            loading="lazy">
          </iframe>
        </div>

        <!-- UPLOAD -->
        <input type="file" multiple @change="handleImages" />

        <!-- PREVIEW -->
        <div class="preview">
          <div v-for="(img, index) in preview" :key="index" class="preview-item">
            <img :src="img" />
            <span class="remove" @click="removeImage(index)">✕</span>
          </div>
        </div>

        <!-- ACTION -->
        <div class="modal-actions">
          <button class="btn cancel" @click="closeModal">Hủy</button>
          <button class="btn submit" @click="createPost">Đăng bài</button>
        </div>

      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from "vue"
import api from "@/api"

const posts = ref([])
const rooms = ref([])
const buildings = ref([])

const showModal = ref(false)
const selectedBuilding = ref("")
const selectedRoom = ref(null)

const currentUser = ref(JSON.parse(localStorage.getItem("user") || "{}"))

const form = ref({
  title: "",
  content: "",
  roomId: ""
})

const images = ref([])
const preview = ref([])

// ================= API =================
const fetchPosts = async () => {
  const res = await api.get("/posts")
  posts.value = res.data
}

const fetchRooms = async () => {
  const res = await api.get("/rooms")
  rooms.value = res.data
}

const fetchBuildings = async () => {
  const res = await api.get("/buildings")
  buildings.value = res.data
}

// ================= FILTER =================
const filteredRooms = computed(() => {
  return rooms.value.filter(r =>
    r.building?.id == selectedBuilding.value &&
    r.status === "EMPTY"
  )
})

// ================= WATCH =================
watch(() => form.value.roomId, (id) => {
  selectedRoom.value = rooms.value.find(r => r.id == id)

  if (selectedRoom.value) {
    preview.value = [...(selectedRoom.value.images || [])]
  }
})

// ================= UI =================
const openModal = () => showModal.value = true

const closeModal = () => {
  showModal.value = false
  selectedBuilding.value = ""
  form.value = { title: "", content: "", roomId: "" }
  preview.value = []
  images.value = []
}

// ================= TIME =================
const formatTime = (time) => {
  const now = new Date()
  const created = new Date(time)
  const diff = Math.floor((now - created) / 1000)

  if (diff < 60) return "Vừa xong"
  if (diff < 3600) return Math.floor(diff / 60) + " phút trước"
  if (diff < 86400) return Math.floor(diff / 3600) + " giờ trước"

  return created.toLocaleString("vi-VN")
}

// ================= IMAGE =================
const handleImages = (e) => {
  images.value = e.target.files
  preview.value = []

  for (let f of images.value) {
    const reader = new FileReader()
    reader.onload = (ev) => preview.value.push(ev.target.result)
    reader.readAsDataURL(f)
  }
}

const removeImage = (index) => {
  preview.value.splice(index, 1)
}

// ================= MAP =================
const selectedBuildingInfo = computed(() => {
  if (!selectedRoom.value) return null

  return buildings.value.find(
    b => Number(b.id) === Number(selectedRoom.value.building?.id || selectedRoom.value.buildingId)
  )
})

const getMapEmbedUrl = (address) => {
  return `https://www.google.com/maps?q=${encodeURIComponent(address)}&output=embed`
}

// ================= CREATE =================
const createPost = async () => {
  const formData = new FormData()

  formData.append("title", form.value.title)
  formData.append("content", form.value.content)
  formData.append("roomId", form.value.roomId)

  for (let f of images.value) {
    formData.append("images", f)
  }

  await api.post("/posts", formData)

  closeModal()
  fetchPosts()
}

onMounted(() => {
  fetchPosts()
  fetchRooms()
  fetchBuildings()
})
</script>

<style scoped>
/* Tổng thể nền và font */
.container {
  max-width: 650px;
  margin: auto;
  padding: 24px 15px;
  background: #f0f2f5;
  min-height: 100vh;
  font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
}

/* --- BOX TẠO BÀI VIẾT --- */
.create-box {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #ffffff;
  padding: 12px 16px;
  border-radius: 12px;
  cursor: pointer;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  transition: background 0.2s;
}

.create-box:hover {
  background: #f5f5f5;
}

.create-box .avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.create-box input {
  flex: 1;
  border: none;
  outline: none;
  background: #f0f2f5;
  padding: 10px 20px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 15px;
  color: #65676b;
}

/* --- BÀI VIẾT (POST) --- */
.post {
  background: #ffffff;
  margin-top: 16px;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e5e5;
}

.post-header {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 12px;
}

.post-header .avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  border: 1px solid #eee;
}

.name {
  font-weight: 600;
  color: #050505;
  font-size: 15px;
}

.time {
  font-size: 13px;
  color: #65676b;
}

/* Nội dung bài viết */
.post-body h3 {
  margin: 8px 0;
  font-size: 18px;
  color: #1c1e21;
  line-height: 1.4;
}

.post-body p {
  font-size: 15px;
  color: #4b4b4b;
  line-height: 1.5;
  margin-bottom: 10px;
}

.phone {
  font-weight: 600;
  color: #1877f2;
  margin-bottom: 8px;
  font-size: 14px;
}

.address {
  font-size: 13px;
  color: #65676b;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* Thẻ phòng */
.room-tag {
  display: inline-block;
  margin-top: 12px;
  background: #e7f3ff;
  color: #1877f2;
  padding: 6px 12px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 14px;
}

/* Hình ảnh bài viết */
.images {
  margin-top: 15px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.images img {
  width: 100%;
  aspect-ratio: 4 / 3; /* Giữ tỉ lệ ảnh đồng nhất */
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
}

/* Bản đồ bài viết */
.map-container {
  margin-top: 15px;
  border: 1px solid #ddd;
  border-radius: 10px;
  overflow: hidden;
  height: 200px;
}

/* --- MODAL TẠO BÀI --- */
.overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px); /* Làm mờ nền */
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal {
  width: 500px;
  max-width: 90vw;
  max-height: 85vh;
  overflow-y: auto;
  background: #ffffff;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.2);
}

.modal h2 {
  text-align: center;
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

/* Input, Select, Textarea trong modal */
.modal input,
.modal textarea,
.modal select {
  width: 100%;
  margin-top: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  border: 1px solid #ddd;
  background: #f7f8fa;
  font-size: 15px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.modal input:focus, 
.modal textarea:focus, 
.modal select:focus {
  outline: none;
  border-color: #1877f2;
  box-shadow: 0 0 0 2px rgba(24, 119, 242, 0.1);
  background: #fff;
}

.modal textarea {
  min-height: 100px;
  resize: vertical;
}

/* Xem trước thông tin phòng */
.room-preview {
  margin-top: 15px;
  padding: 15px;
  background: #f0f7ff;
  border-left: 4px solid #1877f2;
  border-radius: 4px 8px 8px 4px;
}

.room-preview h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #1877f2;
  text-transform: uppercase;
}

.room-preview p {
  margin: 4px 0;
  font-size: 14px;
  color: #444;
}

/* Upload & Preview ảnh */
.preview {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 10px;
  margin-top: 15px;
}

.preview-item {
  position: relative;
  width: 80px;
  height: 80px;
}

.preview-item img {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  object-fit: cover;
  border: 1px solid #ddd;
}

.remove {
  position: absolute;
  top: -6px;
  right: -6px;
  background: #ff3b30;
  color: white;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

/* Nút bấm điều hướng */
.modal-actions {
  position: sticky;
  bottom: -24px;
  background: white;
  padding: 15px 0 0 0;
  margin-top: 20px;
  display: flex;
  gap: 12px;
  border-top: 1px solid #eee;
}

.btn {
  flex: 1;
  padding: 12px;
  border-radius: 8px;
  border: none;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.cancel {
  background: #e4e6eb;
  color: #4b4b4b;
}

.submit {
  background: #1877f2;
  color: white;
}

.btn:hover {
  opacity: 0.9;
}

/* Scrollbar cho Modal */
.modal::-webkit-scrollbar {
  width: 6px;
}
.modal::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 10px;
}
</style>