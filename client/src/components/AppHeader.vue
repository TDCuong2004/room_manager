<template>
  <header class="bg-white border-b sticky top-0 z-50">
    <div class="flex items-center justify-between px-[60px] h-[75px]">

      <!-- LOGO -->
      <div 
        @click="$router.push('/')"
        class="text-[22px] font-bold text-rose-500 cursor-pointer"
      >
        🏠 RoomManager
      </div>

      <!-- MENU -->
      <nav class="flex gap-[35px] font-semibold">
        <router-link 
          to="/rooms"
          class="text-gray-800 hover:text-rose-500 transition"
          active-class="text-rose-500"
        >
          Phòng trống
        </router-link>

        <router-link 
          to="/guide"
          class="text-gray-800 hover:text-rose-500 transition"
          active-class="text-rose-500"
        >
          Hướng dẫn thuê
        </router-link>
      </nav>

      <!-- RIGHT -->
      <div class="flex items-center gap-[18px] relative">

        <!-- CHƯA LOGIN -->
        <template v-if="!isLoggedIn">
          <router-link 
            to="/login"
            class="px-4 py-2 border border-gray-300 rounded-full hover:bg-gray-100"
          >
            Đăng nhập
          </router-link>
        </template>

        <!-- ĐÃ LOGIN -->
        <div v-else class="flex items-center gap-3">

          <!-- 🔔 NOTIFY -->
          <div ref="notifyRef" class="relative">
            <div 
              @click.stop="toggleNotify"
              class="w-[36px] h-[36px] flex items-center justify-center rounded-full bg-gray-100 hover:bg-gray-200 cursor-pointer relative"
            >
              🔔
              <span 
                v-if="expiringContracts.length"
                class="absolute -top-1 -right-1 bg-rose-500 text-white text-[10px] w-4 h-4 flex items-center justify-center rounded-full"
              >
                {{ expiringContracts.length }}
              </span>
            </div>

            <div 
              v-if="showNotify"
              class="absolute right-0 top-[45px] w-[280px] bg-white rounded-xl shadow-xl p-3 z-50"
            >
              <div v-if="expiringContracts.length === 0" class="text-sm text-gray-500">
                Không có thông báo
              </div>

              <div 
                v-for="c in expiringContracts"
                :key="c.id"
                class="p-2 rounded-lg hover:bg-gray-50"
              >
                <p class="text-sm">
                  Phòng <b>{{ c.roomName }}</b> sắp hết hạn
                </p>

                <button 
                  @click="openRenew(c)"
                  class="mt-2 text-xs px-3 py-1 bg-rose-500 text-white rounded-md"
                >
                  Gia hạn
                </button>
              </div>
            </div>
          </div>

          <!-- AVATAR -->
          <div ref="avatarRef" class="relative">
            <div 
              @click.stop="toggleDropdown"
              class="w-[38px] h-[38px] rounded-full bg-rose-500 text-white flex items-center justify-center cursor-pointer overflow-hidden"
            >
              <img 
                v-if="user.avatar" 
                :src="user.avatar" 
                class="w-full h-full object-cover"
              />
              <span v-else>
                {{ user.fullName?.charAt(0) || "U" }}
              </span>
            </div>

            <div 
              v-if="showDropdown"
              class="absolute right-0 top-[50px] w-[190px] bg-white rounded-xl shadow-xl overflow-hidden"
            >
              <button 
                @click="showLogoutModal = true"
                class="w-full text-left px-4 py-3 hover:bg-gray-100"
              >
                Đăng xuất
              </button>

              <router-link 
                to="/dashboard"
                class="block px-4 py-3 hover:bg-gray-100"
              >
                Phòng của tôi
              </router-link>

              <router-link 
                to="/profile"
                class="block px-4 py-3 hover:bg-gray-100"
              >
                Hồ sơ
              </router-link>
            </div>
          </div>

        </div>
      </div>
    </div>
    <!-- CONFIRM LOGOUT MODAL -->
    <div 
      v-if="showLogoutModal" 
      class="fixed inset-0 z-[9999] flex items-center justify-center"
    >
      <!-- overlay -->
      <div 
        class="absolute inset-0 bg-black/50 backdrop-blur-sm"
        @click="showLogoutModal = false"
      ></div>

      <!-- modal -->
      <div class="relative z-10 bg-white rounded-2xl shadow-xl w-[320px] p-6 text-center">
        <h3 class="text-lg font-bold mb-3">Xác nhận đăng xuất</h3>
        <p class="text-sm text-gray-500 mb-5">
          Bạn có chắc muốn đăng xuất không?
        </p>

        <div class="flex gap-3">
          <button 
            @click="showLogoutModal = false"
            class="flex-1 py-2 bg-gray-100 hover:bg-gray-200 rounded-lg"
          >
            Hủy
          </button>

          <button 
            @click="logout"
            class="flex-1 py-2 bg-rose-500 text-white hover:bg-rose-600 rounded-lg"
          >
            Đăng xuất
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue"
import { useRouter } from "vue-router"
import api from "@/api"
const showLogoutModal = ref(false)
const router = useRouter()

const isLoggedIn = ref(false)
const showDropdown = ref(false)
const showNotify = ref(false)

const user = ref({})
const expiringContracts = ref([])

// refs để detect click ngoài
const notifyRef = ref(null)
const avatarRef = ref(null)

// ================= AUTH =================
const checkLogin = () => {
  isLoggedIn.value = !!localStorage.getItem("token")
}

const fetchUser = async () => {
  if (!isLoggedIn.value) return
  const res = await api.get("/users/profile")
  user.value = res.data
}

// ================= TOGGLE =================
const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
  showNotify.value = false
}

const toggleNotify = () => {
  showNotify.value = !showNotify.value
  showDropdown.value = false
}

// ================= CLICK OUTSIDE =================
const handleClickOutside = (e) => {
  if (
    notifyRef.value &&
    !notifyRef.value.contains(e.target)
  ) {
    showNotify.value = false
  }

  if (
    avatarRef.value &&
    !avatarRef.value.contains(e.target)
  ) {
    showDropdown.value = false
  }
}

// ================= LOGOUT =================
const logout = () => {
  localStorage.clear()

  window.dispatchEvent(new Event("userUpdated"))

  isLoggedIn.value = false
  showLogoutModal.value = false

  router.push("/")
}
// ================= DATA =================
const loadExpiring = async () => {
  if (!isLoggedIn.value) return
  const res = await api.get("/contracts/expiring-soon")
  expiringContracts.value = res.data
}

const handleUserUpdated = () => {
  checkLogin()

  const storedUser = localStorage.getItem("user")
  user.value = storedUser ? JSON.parse(storedUser) : {}

  fetchUser()
  loadExpiring()
}
// ================= LIFECYCLE =================
onMounted(() => {
  checkLogin()
  fetchUser()
  loadExpiring()

  window.addEventListener("userUpdated", handleUserUpdated)

  document.addEventListener("click", handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener("click", handleClickOutside)
  window.removeEventListener("userUpdated", handleUserUpdated)
})
</script>

<style>
a {
  text-decoration: none !important;
}
</style>