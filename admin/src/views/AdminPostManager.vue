<template>
  <div class="flex bg-[#f6f6f6] min-h-screen">

    <!-- CONTENT -->
    <div class="flex-1 p-8">

      <!-- HEADER -->
      <div class="flex justify-between items-center mb-6">
        <div>
          <h1 class="text-2xl font-semibold">Content Management</h1>
          <p class="text-gray-400 text-sm">
            Manage all published property listings across all regions.
          </p>
        </div>

        <div class="flex gap-3">
          <button class="bg-gray-200 px-4 py-2 rounded-lg text-sm">
            Export CSV
          </button>
          <button class="bg-red-500 text-white px-4 py-2 rounded-lg text-sm">
            + Create Listing
          </button>
        </div>
      </div>

      <!-- FILTER BAR -->
      <div class="flex items-center gap-4 bg-white p-4 rounded-xl shadow-sm mb-6">

        <span class="text-xs text-gray-400">FILTERS:</span>

        <select v-model="statusFilter" class="border px-3 py-2 rounded-lg text-sm">
          <option value="">Status: All Status</option>
          <option value="ACTIVE">Active</option>
          <option value="FLAGGED">Flagged</option>
          <option value="REPORTED">Reported</option>
        </select>

        <button @click="clearFilter" class="ml-auto text-red-500 text-sm">
          Clear all filters
        </button>
      </div>

      <!-- GRID -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">

        <div
          v-for="p in filteredPosts"
          :key="p.id"
          class="bg-white rounded-2xl shadow-sm hover:shadow-md transition overflow-hidden border"
        >

          <!-- IMAGE -->
          <div class="relative">
            <img
              :src="p.image"
              class="h-44 w-full object-cover"
            />

            <!-- BADGE -->
            <span
              class="absolute top-3 left-3 text-[10px] font-semibold px-2 py-1 rounded-full text-white"
              :class="statusClass(p.status)"
            >
              {{ p.status }}
            </span>
          </div>

          <!-- CONTENT -->
          <div class="p-4">
            <h3 class="font-semibold text-sm leading-tight mb-1">
              {{ p.title }}
            </h3>

            <p class="text-red-500 font-semibold text-sm mb-1">
              ${{ p.price }}
            </p>

            <p class="text-xs text-gray-400 mb-3">
              {{ p.author }}
            </p>

            <!-- BUTTONS -->
            <div class="flex flex-wrap gap-2">
              <button class="border px-3 py-1 rounded-md text-xs hover:bg-gray-100">
                View Details
              </button>
              <button class="border px-3 py-1 rounded-md text-xs hover:bg-gray-100">
                Edit Post
              </button>
              <button
                @click="deletePost(p.id)"
                class="border px-3 py-1 rounded-md text-xs text-red-500 hover:bg-red-50"
              >
                Delete Post
              </button>
            </div>
          </div>

        </div>

      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../api'

const posts = ref([])
const loading = ref(false)
const statusFilter = ref('')

// fetch data
const fetchPosts = async () => {
  loading.value = true
  try {
    const res = await api.get('/posts')

    posts.value = res.data.map(p => ({
      id: p.id,
      title: p.title,
      price: p.price,
      author: p.userName || 'Unknown',
      status: p.status || 'ACTIVE',
      image: p.images?.[0] || `https://picsum.photos/300/200?random=${p.id}`
    }))

  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

// filter
const filteredPosts = computed(() => {
  if (!statusFilter.value) return posts.value
  return posts.value.filter(p => p.status === statusFilter.value)
})

const clearFilter = () => {
  statusFilter.value = ''
}

// status UI
const statusClass = (status) => {
  if (status === 'ACTIVE') return 'bg-green-500'
  if (status === 'FLAGGED') return 'bg-orange-500'
  if (status === 'REPORTED') return 'bg-red-500'
  return 'bg-gray-400'
}

// delete
const deletePost = async (id) => {
  if (!confirm('Delete this post?')) return

  try {
    await api.delete(`/posts/${id}`)
    posts.value = posts.value.filter(p => p.id !== id)
  } catch (err) {
    console.error(err)
    alert('Delete failed')
  }
}

onMounted(fetchPosts)
</script>