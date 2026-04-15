<template>
  <div class="min-h-screen bg-gray-100 py-6 px-4 font-sans text-gray-900">
    <div class="max-w-[850px] mx-auto space-y-4">
      
      <div 
        class="bg-white p-4 rounded-xl shadow-sm border border-gray-200 flex items-center gap-3 cursor-pointer hover:bg-gray-50 transition-colors"
        @click="handleOpenModal"
      >
        <!-- CHỈ HIỆN AVATAR KHI LOGIN -->
        <img
          v-if="isLoggedIn"
          class="w-10 h-10 rounded-full object-cover border border-gray-100"
          :src="currentUser?.avatar || 'https://i.pravatar.cc/40'"
        />

        <div class="flex-1 bg-gray-100 hover:bg-gray-200 py-2.5 px-5 rounded-full text-gray-500 text-[15px] transition-colors font-medium">
          {{ isLoggedIn ? "Bạn đang nghĩ gì thế?" : "Đăng nhập để đăng bài..." }}
        </div>
      </div>

      <div v-for="post in posts" :key="post.id" class="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
        <div class="p-4 flex items-center gap-3 border-b border-gray-50">
          <img
            class="w-11 h-11 rounded-full border border-gray-100 object-cover"
            :src="post.userAvatar || 'https://i.pravatar.cc/40'"
          />
          <div class="flex-1">
            <div class="font-bold text-gray-900 text-[15px] leading-tight">{{ post.userName || "Người dùng" }}</div>
            <div class="text-xs text-gray-400 mt-0.5">{{ formatTime(post.createdAt) }}</div>
          </div>
        </div>

        <div class="px-4 py-4 space-y-3">
          <div v-if="post.phone" class="text-blue-600 font-bold text-sm flex items-center gap-1.5">
            <span class="bg-blue-100 p-1 rounded-md text-[10px]">📞</span> {{ post.phone }}
          </div>
          
          <h3 class="font-bold text-lg text-gray-900 leading-snug">{{ post.title }}</h3>
          <p class="text-gray-700 leading-relaxed text-[15px] whitespace-pre-wrap">{{ post.content }}</p>

          <div class="flex flex-wrap items-center gap-3">
            <button 
              v-if="post.address" 
              @click="openGoogleMaps(post.address)"
              class="text-sm text-gray-600 flex items-center gap-2 hover:bg-blue-50 hover:text-blue-600 transition-all bg-gray-50 px-3 py-2 rounded-xl border border-gray-100 group"
            >
              <span class="group-hover:scale-110 transition-transform text-lg leading-none">📍</span>
              <span class="font-medium">{{ post.address }}</span>
            </button>

            <div v-if="post.room" class="inline-flex items-center gap-2 bg-green-50 text-green-700 px-3 py-1.5 rounded-xl text-sm font-bold border border-green-100">
              🏠 {{ post.room.roomName }} <span class="text-green-200">|</span> {{ formatMoney(post.room.price) }}đ
            </div>
          </div>
        </div>

        <div v-if="post.images?.length" class="px-4 pb-4">
          <div class="grid gap-2" :class="post.images.length > 2 ? 'grid-cols-3' : 'grid-cols-2'">
            <img 
              v-for="(img, index) in post.images" 
              :key="img" 
              :src="img" 
              @click="openLightbox(post.images, index)"
              class="w-full aspect-square object-cover rounded-xl border border-gray-200 hover:opacity-90 transition-all cursor-pointer shadow-sm" 
            />
          </div>
        </div>
      </div>
    </div>

    <div v-if="lightbox.show" class="fixed inset-0 z-[10000] flex items-center justify-center p-4 md:p-10">
      <div class="absolute inset-0 bg-black/85 backdrop-blur-sm" @click="closeLightbox"></div>

      <div class="relative z-10 bg-gray-900 rounded-3xl overflow-hidden shadow-2xl flex flex-col md:flex-row max-w-6xl w-full h-full max-h-[85vh] border border-white/10">
        
        <div class="flex-1 relative flex items-center justify-center p-4 bg-black">
          <button @click="closeLightbox" class="absolute top-4 left-4 text-white/70 hover:text-white z-20 transition-colors flex items-center gap-2 bg-black/40 px-3 py-1.5 rounded-full text-sm">
            <span>✕</span> Đóng
          </button>

          <button v-if="lightbox.images.length > 1" @click="prevImage" class="absolute left-4 top-1/2 -translate-y-1/2 bg-white/10 hover:bg-white/20 text-white p-4 rounded-full z-20 text-2xl transition-all">‹</button>
          <button v-if="lightbox.images.length > 1" @click="nextImage" class="absolute right-4 top-1/2 -translate-y-1/2 bg-white/10 hover:bg-white/20 text-white p-4 rounded-full z-20 text-2xl transition-all">›</button>

          <img 
            :src="lightbox.images[lightbox.index]" 
            class="max-w-full max-h-full object-contain pointer-events-none transition-all duration-300 select-none"
          />
        </div>

        <div 
          v-if="lightbox.images.length > 1"
          class="w-full md:w-32 bg-gray-800/50 p-4 flex md:flex-col gap-3 overflow-x-auto md:overflow-y-auto custom-scrollbar border-t md:border-t-0 md:border-l border-white/10"
        >
          <img 
            v-for="(img, index) in lightbox.images" 
            :key="'thumb-'+index"
            :src="img" 
            @click="setLightboxIndex(index)"
            class="w-16 h-16 md:w-24 md:h-24 object-cover rounded-xl cursor-pointer shrink-0 transition-all border-2"
            :class="lightbox.index === index ? 'border-blue-500 scale-95 opacity-100 ring-4 ring-blue-500/20' : 'border-transparent opacity-40 hover:opacity-100'"
          />
        </div>
      </div>
    </div>

    <div v-if="showModal" class="fixed inset-0 z-[9999] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-black/60 backdrop-blur-sm" @click="closeModal"></div>
      <div class="bg-white w-full max-w-[500px] rounded-2xl shadow-2xl relative z-10 flex flex-col max-h-[85vh]">
        <div class="p-4 border-b border-gray-100 text-center sticky top-0 bg-white rounded-t-2xl z-20 font-bold text-lg">Tạo bài viết</div>
        <div class="p-5 overflow-y-auto space-y-5 custom-scrollbar">
          <input v-model="form.title" placeholder="Tiêu đề bài viết" class="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition-all" />
          <textarea v-model="form.content" placeholder="Bạn đang nghĩ gì?" class="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none min-h-[120px] resize-none"></textarea>
          <div class="grid grid-cols-2 gap-3">
            <select v-model="selectedBuilding" class="p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none"><option value="">-- Chọn tòa --</option><option v-for="b in buildings" :key="b.id" :value="b.id">{{ b.buildingName }}</option></select>
            <select v-model="form.roomId" class="p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none"><option value="">-- Chọn phòng --</option><option v-for="r in filteredRooms" :key="r.id" :value="r.id">{{ r.roomName }} - {{ formatMoney(r.price) }}đ</option></select>
          </div>
          <div v-if="selectedRoom" class="bg-blue-50 p-4 rounded-xl border-l-4 border-blue-500 flex justify-between">
            <div class="text-sm">🏠 <b>{{ selectedRoom.roomName }}</b><br><span class="text-xs text-gray-500">{{ selectedBuildingInfo?.address }}</span></div>
            <b class="text-blue-700">{{ formatMoney(selectedRoom.price) }}đ</b>
          </div>
          <div class="space-y-3">
            <label class="block w-full border-2 border-dashed border-gray-200 hover:border-blue-400 hover:bg-blue-50 rounded-xl p-6 text-center cursor-pointer group transition-all font-semibold text-gray-500">
              <input type="file" multiple @change="handleImages" class="hidden" />📷 Thêm hình ảnh
            </label>
            <div v-if="preview.length" class="grid grid-cols-4 gap-2">
              <div v-for="(img, index) in preview" :key="index" class="relative aspect-square">
                <img :src="img" class="w-full h-full object-cover rounded-lg border shadow-sm" />
                <button @click="removeImage(index)" class="absolute -top-1.5 -right-1.5 bg-red-500 text-white w-5 h-5 rounded-full text-[10px] flex items-center justify-center">✕</button>
              </div>
            </div>
          </div>
        </div>
        <div class="p-4 border-t flex gap-3"><button @click="closeModal" class="flex-1 py-3 bg-gray-100 hover:bg-gray-200 text-gray-700 font-bold rounded-xl transition-colors">Hủy</button><button @click="createPost" class="flex-1 py-3 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-xl shadow-lg shadow-blue-200 transition-all active:scale-95">Đăng bài</button></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed, onBeforeUnmount } from "vue"
import api from "@/api"

// --- DATA ---
const posts = ref([])
const rooms = ref([])
const buildings = ref([])
const showModal = ref(false)
const selectedBuilding = ref("")
const selectedRoom = ref(null)
const currentUser = ref(JSON.parse(localStorage.getItem("user") || "{}"))
const form = ref({ title: "", content: "", roomId: "" })
const images = ref([])
const preview = ref([])
const isLoggedIn = ref(!!localStorage.getItem("token"))
const lightbox = ref({ show: false, images: [], index: 0 })

// --- UTILS ---
const formatMoney = (v) => new Intl.NumberFormat("vi-VN").format(v || 0)

const openGoogleMaps = (address) => {
  if (!address) return
  const url = `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(address)}`
  window.open(url, '_blank')
}

const formatTime = (time) => {
  const diff = Math.floor((new Date() - new Date(time)) / 1000)
  if (diff < 60) return "Vừa xong"
  if (diff < 3600) return Math.floor(diff / 60) + " phút trước"
  if (diff < 86400) return Math.floor(diff / 3600) + " giờ trước"
  return new Date(time).toLocaleDateString("vi-VN")
}

// --- LIGHTBOX LOGIC ---
const openLightbox = (imageArray, index) => {
  lightbox.value.images = imageArray
  lightbox.value.index = index
  lightbox.value.show = true
  document.body.style.overflow = 'hidden'
  window.addEventListener('keydown', handleKeydown)
}

const closeLightbox = () => {
  lightbox.value.show = false
  document.body.style.overflow = ''
  window.removeEventListener('keydown', handleKeydown)
}

const setLightboxIndex = (i) => lightbox.value.index = i
const nextImage = () => lightbox.value.index = (lightbox.value.index + 1) % lightbox.value.images.length
const prevImage = () => lightbox.value.index = (lightbox.value.index - 1 + lightbox.value.images.length) % lightbox.value.images.length

const handleKeydown = (e) => {
  if (!lightbox.value.show) return
  if (e.key === 'Escape') closeLightbox()
  if (e.key === 'ArrowRight') nextImage()
  if (e.key === 'ArrowLeft') prevImage()
}

// --- API LOGIC ---
const fetchPosts = async () => { 
  const res = await api.get("/posts"); 
  
  posts.value = [...res.data].sort(
    (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
  )
}
const fetchRooms = async () => { const res = await api.get("/rooms"); rooms.value = res.data }
const fetchBuildings = async () => { const res = await api.get("/buildings"); buildings.value = res.data }

const filteredRooms = computed(() => rooms.value.filter(r => r.building?.id == selectedBuilding.value && r.status === "EMPTY"))

watch(() => form.value.roomId, async (id) => {
  selectedRoom.value = rooms.value.find(r => r.id == id)

  if (selectedRoom.value) {

    const urls = (selectedRoom.value.images || []).map(img => {
      if (typeof img === "string") return img
      if (img.imageUrl) return "http://localhost:3000" + img.imageUrl
      return ""
    })

    preview.value = urls

    // 🔥 convert sang file luôn
    images.value = []

    for (let url of urls) {
      if (!url) continue
      try {
        const file = await convertUrlToFile(url)
        images.value.push(file)
      } catch (e) {
        console.error("Convert lỗi:", url)
      }
    }
  }
})

const openModal = () => showModal.value = true
const closeModal = () => { showModal.value = false; selectedBuilding.value = ""; form.value = { title: "", content: "", roomId: "" }; preview.value = []; images.value = [] }

const handleImages = async (e) => {
  const files = Array.from(e.target.files)

  for (let file of files) {
    images.value.push(file)

    const base64 = await new Promise((resolve) => {
      const reader = new FileReader()
      reader.onload = (ev) => resolve(ev.target.result)
      reader.readAsDataURL(file)
    })

    preview.value.push(base64)
  }

  e.target.value = ""
}

const removeImage = (index) => {
  preview.value.splice(index, 1)
  images.value.splice(index, 1)
}

const selectedBuildingInfo = computed(() => {
  if (!selectedRoom.value) return null
  return buildings.value.find(b => Number(b.id) === Number(selectedRoom.value.building?.id || selectedRoom.value.buildingId))
})

const createPost = async () => {
  const formData = new FormData()

  formData.append("title", form.value.title)
  formData.append("content", form.value.content)
  formData.append("roomId", form.value.roomId)

  // ✅ tất cả ảnh (cũ + mới) đều là file
  images.value.forEach(file => {
    formData.append("images", file)
  })

  await api.post("/posts", formData)

  closeModal()
  fetchPosts()
}


const handleOpenModal = () => {
  if (!isLoggedIn.value) {
    router.push("/login")
    return
  }
  openModal()
}

const convertUrlToFile = async (url) => {
  const res = await fetch(url)
  const blob = await res.blob()

  const filename = url.split("/").pop()

  return new File([blob], filename, { type: blob.type })
}

onMounted(() => {
  fetchPosts()
  fetchRooms()
  fetchBuildings()

  window.addEventListener("userUpdated", () => {
    isLoggedIn.value = !!localStorage.getItem("token")
    currentUser.value = JSON.parse(localStorage.getItem("user") || "{}")
  })
})
onBeforeUnmount(() => { window.removeEventListener('keydown', handleKeydown); document.body.style.overflow = '' })
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 4px; height: 4px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(0, 0, 0, 0.1); border-radius: 10px; }
.bg-gray-800\/50 .custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(255, 255, 255, 0.2); }
</style>