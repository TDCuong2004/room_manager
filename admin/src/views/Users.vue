<template>
  <div>

    <!-- HEADER -->
    <div class="flex justify-between items-center mb-6">
      <div>
        <h1 class="text-2xl font-bold">Community Members</h1>
        <p class="text-gray-500 text-sm">
          Manage roles, permissions and account status
        </p>
      </div>

      <button class="bg-red-500 text-white px-4 py-2 rounded-lg">
        + New Listing
      </button>
    </div>

    <!-- LOADING -->
    <div v-if="loading" class="text-center py-10 text-gray-400">
      Loading users...
    </div>

    <!-- ERROR -->
    <div v-if="error" class="text-center py-10 text-red-500">
      {{ error }}
    </div>

    <!-- TABLE -->
    <div v-if="!loading && !error" class="bg-white rounded-xl shadow overflow-hidden">
      <table class="w-full text-left">
        <thead class="bg-gray-100 text-sm text-gray-500">
          <tr>
            <th class="p-4">Member</th>
            <th>Role</th>
            <th>Email</th>
            <th class="text-center">Actions</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="u in users" :key="u.id" class="border-t hover:bg-gray-50">

            <!-- USER -->
            <td class="p-4 flex items-center gap-3">
              <img
                :src="u.avatar"
                class="w-10 h-10 rounded-full object-cover"
              />
              <div>
                <p class="font-semibold">{{ u.name }}</p>
                <p class="text-sm text-gray-400">{{ u.phone }}</p>
              </div>
            </td>

            <!-- ROLE -->
            <td>
              <span
                class="px-3 py-1 rounded-full text-sm"
                :class="roleClass(u.role)"
              >
                {{ u.role }}
              </span>
            </td>

            <!-- EMAIL -->
            <td class="text-gray-500 text-sm">
              {{ u.email || '---' }}
            </td>

            <td class="text-center space-x-2">
            <button @click="editUser(u)" class="text-blue-500">✏️</button>
            <button @click="deleteUser(u.id)" class="text-red-500">🗑️</button>
            </td>

          </tr>
        </tbody>
      </table>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'

// state
const users = ref([])
const loading = ref(false)
const error = ref('')

// fetch data
const fetchUsers = async () => {
  loading.value = true
  error.value = ''

  try {
    const res = await api.get('/users')

    users.value = res.data.map(u => ({
      id: u.id,
      name: u.fullName || 'No Name',
      phone: u.phone || '---',
      email: u.email,
      role: u.role,
      avatar: u.avatar
        ? u.avatar
        : `https://i.pravatar.cc/100?u=${u.id}` // 🔥 fallback unique
    }))

  } catch (err) {
    console.error(err)
    error.value = 'Không tải được danh sách user'
  } finally {
    loading.value = false
  }
}

// role UI
const roleClass = (role) => {
  if (role === 'ADMIN') return 'bg-red-100 text-red-500'
  if (role === 'USER') return 'bg-blue-100 text-blue-500'
  return 'bg-gray-100 text-gray-500'
}
const deleteUser = async (id) => {
  if (!confirm('Xóa user này?')) return

  try {
    await api.delete(`/users/${id}`)
    users.value = users.value.filter(u => u.id !== id)
  } catch (err) {
    console.error(err)
    alert('Xóa thất bại')
  }
}
// lifecycle
onMounted(fetchUsers)
</script>