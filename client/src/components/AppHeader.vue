<template>
  <header class="bg-white border-b sticky top-0 z-50">
    <div class="flex items-center justify-between px-[60px] h-[75px]">

      <div 
        @click="$router.push('/')"
        class="flex items-center gap-2 text-[22px] font-bold text-[#4CAF50] cursor-pointer"
      >
        <img 
          src="../assets/home-logo.jpg" 
          class="h-18 object-contain"
        />
        <span>RoomManager</span>
      </div>

      <!-- MENU -->
      <nav class="flex gap-[35px] font-semibold">
        

        <router-link 
          to="/guide"
          class="text-gray-800 hover:text-[#4CAF50] transition"
          active-class="text-[#4CAF50]"
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
                v-if="unreadCount"
                class="absolute -top-1 -right-1 bg-[#4CAF50] text-white text-[10px] w-4 h-4 flex items-center justify-center rounded-full"
              >
                {{ unreadCount }}
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
                class="mb-3 p-3 rounded-xl border border-rose-100 bg-white shadow-sm"
              >
                <div class="flex items-center justify-between mb-2">
                  <p class="font-semibold text-gray-800">
                    🏠 Phòng {{ c.roomName }}
                  </p>

                  <span
                    class="px-2 py-1 text-xs bg-rose-50 text-rose-500 rounded-full"
                  >
                    Sắp hết hạn
                  </span>
                </div>

                <!-- Người thuê -->
                <div class="flex items-center gap-2 text-sm mb-2">
                  <span>👤</span>
                  <span class="text-gray-500">Người thuê:</span>
                  <span class="font-medium">
                    {{ c.representativeName }}
                  </span>
                </div>

                <!-- Ngày hết hạn -->
                <div class="flex items-center gap-2 text-sm mb-3">
                  <span>📅</span>
                  <span class="text-gray-500">Hết hạn:</span>
                  <span class="font-medium text-green-600">
                    {{ formatDate(c.endDate) }}
                  </span>
                </div>

                <button
                  @click="openRenew(c)"
                  class="w-full py-2 bg-[#4CAF50] hover:bg-[#43A047] text-white rounded-lg text-sm font-medium transition"
                >
                  Gia hạn ngay
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

            <img 
              v-else
              src="/avatar-default.jpg"
              class="w-full h-full object-cover"
            />
            </div>

            <div 
              v-if="showDropdown"
              class="absolute right-0 top-[50px] w-[190px] bg-white rounded-xl shadow-xl overflow-hidden"
            >
              <router-link 
                to="/dashboard"
                class="block px-4 py-3 hover:bg-gray-100"
                @click="showDropdown = false"
              >
                Phòng của tôi
              </router-link>

              <router-link 
                to="/profile"
                class="block px-4 py-3 hover:bg-gray-100"
                @click="showDropdown = false"
              >
                Hồ sơ
              </router-link>
              <router-link 
                to="/change-password"
                class="block px-4 py-3 hover:bg-gray-100"
                @click="showDropdown = false"
              >
                Đổi mật khẩu
              </router-link>
              <button 
                @click="showLogoutModal = true"
                class="w-full text-left px-4 py-3 hover:bg-gray-100"
              >
                Đăng xuất
              </button>
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
            class="flex-1 py-2 bg-blue-600 text-white hover:bg-blue-500 rounded-lg"
          >
            Đăng xuất
          </button>
        </div>
      </div>
    </div>
    <!-- RENEW CONTRACT MODAL -->
      <div
        v-if="showRenewModal"
        class="fixed inset-0 z-[9999] flex items-center justify-center"
      >
        <!-- overlay -->
        <div
          class="absolute inset-0 bg-black/50"
          @click="showRenewModal = false"
        ></div>

        <!-- modal -->
        <div class="relative z-10 bg-white rounded-2xl shadow-xl w-[500px] p-6">

          <h3 class="text-xl font-bold text-gray-800 mb-5">
            Gia hạn hợp đồng
          </h3>

          <!-- Thông tin hiện tại -->
          <div class="bg-gray-50 border rounded-xl p-4 mb-5">

            <div class="flex justify-between mb-3">
              <span class="text-gray-500">Phòng</span>
              <span class="font-semibold">
                {{ selectedContract?.roomName }}
              </span>
            </div>

            <div class="flex justify-between mb-3">
              <span class="text-gray-500">Người thuê</span>
              <span class="font-semibold">
                {{ selectedContract?.representativeName }}
              </span>
            </div>

            <div class="flex justify-between mb-3">
              <span class="text-gray-500">Ngày bắt đầu</span>
              <span class="font-medium">
                {{ formatDate(selectedContract?.startDate) }}
              </span>
            </div>

            <div class="flex justify-between">
              <span class="text-gray-500">Ngày hết hạn hiện tại</span>
              <span class="font-semibold text-red-500">
                {{ formatDate(selectedContract?.endDate) }}
              </span>
            </div>

          </div>

          <!-- Chọn ngày mới -->
          <div>
            <label class="block text-sm font-medium mb-2">
              Ngày kết thúc mới
            </label>

            <input
              v-model="renewEndDate"
              type="date"
              :min="selectedContract?.endDate"
              class="w-full border rounded-xl px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />

            <p class="text-xs text-gray-500 mt-2">
              Ngày mới phải lớn hơn ngày hết hạn hiện tại.
            </p>
          </div>

          <div class="flex justify-end gap-3 mt-6">

            <button
              @click="showRenewModal = false"
              class="px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
            >
              Hủy
            </button>

            <button
              @click="confirmRenew"
              class="px-4 py-2 bg-green-700 text-white rounded-lg hover:bg-green-600 transition"
            >
              Gia hạn
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
import { useToast } from "vue-toastification"
import { computed } from "vue"

const unreadCount = computed(() =>
  expiringContracts.value.filter(
    c => !viewedContracts.value.includes(c.id)
  ).length
)
const toast = useToast()
const showRenewModal = ref(false)
const selectedContract = ref(null)
const renewEndDate = ref("")

const showLogoutModal = ref(false)
const router = useRouter()

const isLoggedIn = ref(false)
const showDropdown = ref(false)
const showNotify = ref(false)
const formatDate = (date) => {
  return new Date(date).toLocaleDateString("vi-VN")
}
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
// Gia Hạn
const openRenew = (contract) => {

  markAsViewed(contract.id)

  selectedContract.value = contract

  // không điền sẵn
  renewEndDate.value = ""

  showRenewModal.value = true
}

const viewedContracts = ref(
  JSON.parse(localStorage.getItem("viewedContracts") || "[]")
)
const markAsViewed = (id) => {
  if (!viewedContracts.value.includes(id)) {
    viewedContracts.value.push(id)

    localStorage.setItem(
      "viewedContracts",
      JSON.stringify(viewedContracts.value)
    )
  }
}
const confirmRenew = async () => {
  if (!renewEndDate.value) {
    toast.warning("Vui lòng chọn ngày kết thúc")
    return
  }

  try {
    await api.post(
      `/contracts/${selectedContract.value.id}/renew`,
      null,
      {
        params: {
          endDate: renewEndDate.value
        }
      }
    )

    toast.success("Gia hạn hợp đồng thành công")


    showRenewModal.value = false

    await loadExpiring()

  } catch (err) {
    console.error(err)

    toast.error(
      err?.response?.data?.error ||
      "Gia hạn hợp đồng thất bại"
    )
  }
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