<template>
  <div class="container">

    <!-- CREATE POST -->
    <div class="create-box" @click="openModal">
      <img class="avatar" src="https://i.pravatar.cc/40" />
      <input placeholder="Bạn đang nghĩ gì thế?" readonly />
    </div>

    <!-- POSTS -->
    <div class="post" v-for="post in posts" :key="post.id">

      <div class="post-header">
        <img class="avatar" src="https://i.pravatar.cc/40" />
        <div>
          <div class="name">Chủ trọ</div>
          <div class="time">{{ formatTime(post.createdAt) }}</div>
        </div>
      </div>

      <div class="post-body">
        <h3>{{ post.title }}</h3>
        <p>{{ post.content }}</p>

        <div v-if="post.room" class="room-tag">
          🏠 {{ post.room.roomName }} • {{ post.room.price }}đ
        </div>
      </div>
      
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

        <!-- ✅ THÔNG TIN PHÒNG (đặt ở đây) -->
        <div v-if="selectedRoom" class="room-preview">
          <h4>Thông tin phòng</h4>

          <p>🏠 {{ selectedRoom.roomName }}</p>
          <p>💰 {{ selectedRoom.price }}đ</p>
          <p>📐 {{ selectedRoom.area }} m²</p>
          <p>👤 {{ selectedRoom.maxPeople }} người</p>

          <p v-if="selectedRoom.description">
            {{ selectedRoom.description }}
          </p>
        </div>
        <p v-if="selectedBuildingInfo">
          📍 {{ selectedBuildingInfo.address }}
        </p>

        <!-- ✅ MAP -->
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
    // chỉ load ảnh phòng
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
//Thời gian
const formatTime = (time) => {
  const now = new Date()
  const created = new Date(time)

  const diff = Math.floor((now - created) / 1000) // giây

  if (diff < 60) return "Vừa xong"
  if (diff < 3600) return Math.floor(diff / 60) + " phút trước"
  if (diff < 86400) return Math.floor(diff / 3600) + " giờ trước"

  // nếu quá 1 ngày → hiện ngày
  return created.toLocaleString("vi-VN")
}
// ================= IMAGE =================

const handleImages = (e) => {
  images.value = e.target.files

  for (let f of images.value) {
    const reader = new FileReader()
    reader.onload = (ev) => preview.value.push(ev.target.result)
    reader.readAsDataURL(f)
  }
}

const removeImage = (index) => {
  preview.value.splice(index, 1)
}
// Địa chỉ Tòa
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

  await api.post("/posts", formData, {
    headers: { "Content-Type": "multipart/form-data" }
  })

  closeModal()
  fetchPosts()
}

onMounted(() => {
  fetchPosts()
  fetchRooms()
  fetchBuildings()
})
</script>

<style>
.container {
  max-width: 600px;
  margin: auto;
  padding: 20px;
  background: #f0f2f5;
  min-height: 100vh;
}

/* CREATE */
.create-box {
  display: flex;
  align-items: center;
  gap: 10px;
  background: white;
  padding: 10px 15px;
  border-radius: 20px;
  cursor: pointer;
}

.create-box input {
  border: none;
  background: #f0f2f5;
  padding: 10px;
  border-radius: 20px;
  width: 100%;
}

/* POST */
.post {
  background: white;
  margin-top: 20px;
  padding: 15px;
  border-radius: 15px;
}

.post-header {
  display: flex;
  gap: 10px;
  align-items: center;
}

.name {
  font-weight: bold;
}

.time {
  font-size: 12px;
  color: gray;
}

.room-tag {
  margin-top: 8px;
  background: #e7f3ff;
  padding: 5px 10px;
  border-radius: 10px;
}

/* IMAGES */
.images {
  margin-top: 10px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 5px;
}

.images img {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 10px;
}

/* AVATAR */
.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

/* MODAL */
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal {
  width: 420px;
  background: white;
  padding: 20px;
  border-radius: 15px;
}

.modal input,
.modal textarea,
.modal select {
  width: 100%;
  margin-top: 10px;
  padding: 10px;
  border-radius: 10px;
  border: 1px solid #ddd;
}

/* PREVIEW */
.preview {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin-top: 10px;
}

.preview-item {
  position: relative;
}

.preview-item img {
  width: 70px;
  height: 70px;
  border-radius: 8px;
  object-fit: cover;
}

.remove {
  position: absolute;
  top: -5px;
  right: -5px;
  background: red;
  color: white;
  border-radius: 50%;
  font-size: 12px;
  padding: 2px 5px;
  cursor: pointer;
}

/* BUTTON */
.modal-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.btn {
  flex: 1;
  padding: 12px;
  border-radius: 10px;
  border: none;
  font-weight: bold;
  cursor: pointer;
}

.cancel {
  background: #e4e6eb;
}

.submit {
  background: #1877f2;
  color: white;
}
.room-preview {
  margin-top: 10px;
  padding: 10px;
  background: #f7f7f7;
  border-radius: 10px;
  font-size: 14px;
}
.map-container {
  margin-top: 10px;
  border-radius: 10px;
  overflow: hidden;
}
</style>