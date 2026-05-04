<template>
  <div class="w-64 bg-white shadow flex flex-col justify-between p-4">

    <div>
      <h2 class="text-red-500 font-bold text-lg mb-6">Elevated Admin</h2>

      <div class="flex items-center gap-3 mb-6">
        <img src="https://i.pravatar.cc/40" class="rounded-full" />
        <div>
          <p class="font-semibold text-sm">Curator Admin</p>
          <p class="text-xs text-gray-400">Premium Management</p>
        </div>
      </div>

      <nav class="space-y-2">
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
<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const logout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>