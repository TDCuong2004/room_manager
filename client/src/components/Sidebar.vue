<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted } from 'vue'
import api from '../api'

const router = useRouter()
const user = ref({})

const fetchProfile = async () => {
  const res = await api.get('/users/profile')
  user.value = res.data
}

const logout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}

onMounted(fetchProfile)
</script>

<template>
  <div class="w-64 bg-white shadow flex flex-col justify-between p-4">

    <div>
      <h2 class="text-red-500 font-bold text-lg mb-6">Elevated Admin</h2>

      <div class="flex items-center gap-3 mb-6">
        <img
          :src="user.avatar || '/avatar-default.jpg'"
          class="w-10 h-10 rounded-full"
          @error="e => e.target.src = '/avatar-default.jpg'"
        />
        <div>
          <p class="font-semibold text-sm">{{ user.fullName }}</p>
          <p class="text-xs text-gray-400">{{ user.role }}</p>
        </div>
      </div>

      <nav class="space-y-2">
        <router-link
          to="/admin/AdminDashboard"
          class="block px-3 py-2 rounded-lg hover:bg-gray-100"
          active-class="bg-red-100 text-red-500"
        >
          📊 Dashboard
        </router-link>
        <router-link
          to="/admin/users"
          class="block px-3 py-2 rounded-lg hover:bg-gray-100"
          active-class="bg-red-100 text-red-500"
        >
          👤 Users
        </router-link>

        <router-link
          to="/admin/posts"
          class="block px-3 py-2 rounded-lg hover:bg-gray-100"
          active-class="bg-red-100 text-red-500"
        >
          📄 Posts
        </router-link>
      </nav>
    </div>

    <div class="text-sm text-gray-400 space-y-2">
      <p>❓ Help Center</p>
      <p 
        class="cursor-pointer hover:text-red-500"
        @click="logout"
      >
        🚪 Logout
      </p>
    </div>

  </div>
</template>