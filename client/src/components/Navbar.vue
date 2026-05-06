<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'

const user = ref({})
const showMenu = ref(false)
const menuRef = ref(null)
const router = useRouter()

// fetch user
const fetchProfile = async () => {
  try {
    const res = await api.get('/users/profile')
    user.value = res.data
  } catch (err) {
    console.error('Lỗi load profile:', err)
  }
}

// chuyển trang profile
const goProfile = () => {
  showMenu.value = false
  router.push('/profile')
}

// logout
const logout = () => {
  localStorage.removeItem('token') // tùy bạn lưu gì thì xóa đó
  showMenu.value = false
  router.push('/login')
}

// click ngoài để đóng menu
const handleClickOutside = (event) => {
  if (menuRef.value && !menuRef.value.contains(event.target)) {
    showMenu.value = false
  }
}

onMounted(() => {
  fetchProfile()
  document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<template>
  <div class="bg-white px-6 py-3 shadow flex justify-between items-center">

    <!-- SEARCH -->
    <input
      type="text"
      placeholder="Search members..."
      class="px-4 py-2 border rounded-lg w-96"
    />

    <!-- RIGHT -->
    <div class="flex items-center gap-4 relative" ref="menuRef">
      
      <!-- NOTI -->
      <span class="text-xl cursor-pointer">🔔</span>

      <!-- AVATAR -->
      <img
        :src="user.avatar || '/avatar-default.jpg'"
        class="w-10 h-10 rounded-full cursor-pointer object-cover border"
        @click.stop="showMenu = !showMenu"
        @error="e => e.target.src = '/avatar-default.jpg'"
      />

      <!-- DROPDOWN -->
      <div
        v-if="showMenu"
        class="absolute right-0 top-12 bg-white shadow-lg rounded-xl w-52 border overflow-hidden z-50"
      >
        <!-- USER INFO -->
        <div class="px-4 py-3 border-b">
          <p class="font-semibold text-sm">
            {{ user.name || 'User' }}
          </p>
          <p class="text-xs text-gray-500">
            {{ user.email || '' }}
          </p>
        </div>

        <!-- MENU -->
        <button
          @click="goProfile"
          class="w-full text-left px-4 py-2 hover:bg-gray-100 transition"
        >
          👤 Thông tin cá nhân
        </button>

        <button
          @click="logout"
          class="w-full text-left px-4 py-2 text-red-500 hover:bg-gray-100 transition"
        >
          🚪 Đăng xuất
        </button>
      </div>

    </div>
  </div>
</template>