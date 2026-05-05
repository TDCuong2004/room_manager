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

      <!-- FILTER -->
      <select v-model="filterStatus" class="border px-3 py-2 rounded">
        <option value="">All</option>
        <option v-for="s in statusOptions" :key="s" :value="s">
          {{ s }}
        </option>
      </select>
    </div>

    <!-- TOAST -->
    <div v-if="toast" class="fixed top-5 right-5 bg-green-500 text-white px-4 py-2 rounded shadow">
      {{ toast }}
    </div>

    <!-- TABLE -->
    <div class="bg-white rounded-xl shadow overflow-hidden">
      <table class="w-full text-left">
        <thead class="bg-gray-100 text-sm text-gray-500">
          <tr>
            <th class="p-4">Member</th>
            <th>Role</th>
            <th>Status</th>
            <th>Email</th>
            <th class="text-center">Actions</th>
          </tr>
        </thead>

        <tbody>
          <tr 
            v-for="u in filteredUsers" 
            :key="u.id"
            :class="['border-t', u.status === 'DELETED' ? 'opacity-50' : '']"
          >
            <td class="p-4 flex items-center gap-3">
              <img :src="u.avatar" class="w-10 h-10 rounded-full"/>
              <div>
                <p class="font-semibold">{{ u.name }}</p>
                <p class="text-sm text-gray-400">{{ u.phone }}</p>
              </div>
            </td>

            <td>
              <span :class="roleClass(u.role)" class="px-2 py-1 rounded text-sm">
                {{ u.role }}
              </span>
            </td>

            <td>
              <span :class="statusClass(u.status)" class="px-2 py-1 rounded text-sm">
                {{ u.status }}
              </span>
            </td>

            <td>{{ u.email || '---' }}</td>

            <td class="text-center space-x-2">
              <button 
                @click="editUser(u)" 
                class="text-blue-500"
                :disabled="u.status === 'DELETED'"
              >
                ✏️
              </button>

              <button 
                @click="openDeleteModal(u.id)" 
                class="text-red-500"
                :disabled="u.status === 'DELETED'"
              >
                🗑️
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- EDIT MODAL -->
    <div v-if="showModal" class="fixed inset-0 bg-black/30 flex justify-center items-center">
      <div class="bg-white p-6 rounded w-[420px]">

        <h2 class="font-bold text-lg mb-4">Edit User</h2>

        <input v-model="selectedUser.name" placeholder="Full Name" class="input"/>
        <input v-model="selectedUser.phone" placeholder="Phone" class="input"/>
        <input v-model="selectedUser.email" placeholder="Email" class="input"/>

        <input v-model="selectedUser.bankName" placeholder="Bank Name" class="input"/>
        <input v-model="selectedUser.bankCode" placeholder="Bank Code" class="input"/>
        <input v-model="selectedUser.bankAccount" placeholder="Bank Account" class="input"/>

        <select v-model="selectedUser.status" class="input">
          <option v-for="s in statusOptions" :key="s" :value="s">
            {{ s }}
          </option>
        </select>

        <div class="flex justify-end gap-2 mt-4">
          <button @click="showModal=false">Cancel</button>
          <button @click="saveUser" class="bg-blue-500 text-white px-3 py-1 rounded">
            Save
          </button>
        </div>

      </div>
    </div>

    <!-- DELETE MODAL -->
    <div v-if="showDeleteModal" class="fixed inset-0 bg-black/30 flex justify-center items-center">
      <div class="bg-white p-6 rounded w-[350px] text-center">
        <h2 class="text-lg font-semibold mb-4">Xác nhận xoá?</h2>
        <p class="text-gray-500 mb-4">User sẽ chuyển sang trạng thái DELETED</p>

        <div class="flex justify-center gap-3">
          <button @click="showDeleteModal=false">Huỷ</button>
          <button @click="confirmDelete" class="bg-red-500 text-white px-3 py-1 rounded">
            Xoá
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../api'

// STATE
const users = ref([])
const statusOptions = ref([])
const showModal = ref(false)
const showDeleteModal = ref(false)
const selectedUser = ref({})
const deleteId = ref(null)
const filterStatus = ref('')
const toast = ref('')

const fetchUsers = async () => {
  const res = await api.get('/users')

  users.value = res.data.map(u => ({
    id: u.id,
    name: u.fullName,
    phone: u.phone,
    email: u.email,
    role: u.role,
    status: u.status,
    bankName: u.bankName,
    bankCode: u.bankCode,
    bankAccount: u.bankAccount,
    avatar: u.avatar || '/avatar-default.jpg' // ✅ FIX
  }))
}

const fetchStatuses = async () => {
  const res = await api.get('/users/statuses')
  statusOptions.value = res.data
}

// FILTER LOGIC (🔥 FIX QUAN TRỌNG)
const filteredUsers = computed(() => {
  // ALL → ẩn DELETED
  if (!filterStatus.value) {
    return users.value.filter(u => u.status !== 'DELETED')
  }

  // chọn DELETED → chỉ show DELETED
  return users.value.filter(u => u.status === filterStatus.value)
})

// UI
const roleClass = (r) =>
  r === 'ADMIN' ? 'bg-red-100 text-red-500' : 'bg-blue-100 text-blue-500'

const statusClass = (s) => {
  if (s === 'ACTIVE') return 'bg-green-100 text-green-600'
  if (s === 'INACTIVE') return 'bg-yellow-100 text-yellow-600'
  return 'bg-gray-200 text-gray-500'
}

// ACTION
const editUser = (u) => {
  selectedUser.value = { ...u }
  showModal.value = true
}

const saveUser = async () => {
  await api.put(`/users/${selectedUser.value.id}`, selectedUser.value)

  const index = users.value.findIndex(u => u.id === selectedUser.value.id)
  if (index !== -1) users.value[index] = { ...selectedUser.value }

  showToast('Cập nhật thành công ✅')
  showModal.value = false
}

// DELETE FLOW (🔥 NO ALERT)
const openDeleteModal = (id) => {
  deleteId.value = id
  showDeleteModal.value = true
}

const confirmDelete = async () => {
  await api.put(`/users/${deleteId.value}/delete`)
  showDeleteModal.value = false
  showToast('Đã xoá user ✅')
  fetchUsers()
}

// TOAST
const showToast = (msg) => {
  toast.value = msg
  setTimeout(() => toast.value = '', 2000)
}

// INIT
onMounted(() => {
  fetchUsers()
  fetchStatuses()
})
</script>

<style>
.input {
  width: 100%;
  border: 1px solid #ddd;
  padding: 8px;
  margin-bottom: 8px;
  border-radius: 6px;
}
</style>