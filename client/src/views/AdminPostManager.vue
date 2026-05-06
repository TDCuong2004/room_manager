<template>
  <div class="flex bg-[#f6f6f6] min-h-screen">
    <div class="flex-1 p-8">

      <!-- HEADER -->
      <div class="flex justify-between items-center mb-6">
        <div>
          <h1 class="text-2xl font-semibold">Content Management</h1>
          <p class="text-gray-400 text-sm">
            Manage all property listings across all statuses.
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

      <!-- FILTER -->
      <div class="flex items-center gap-4 bg-white p-4 rounded-xl shadow-sm mb-6">
        <span class="text-xs text-gray-400">FILTERS:</span>

        <select v-model="statusFilter" class="border px-3 py-2 rounded-lg text-sm">
          <option value="">All</option>
          <option value="DRAFT">Draft</option>
          <option value="PUBLISHED">Published</option>
          <option value="HIDDEN">Hidden</option>
        </select>

        <button @click="clearFilter" class="ml-auto text-red-500 text-sm">
          Clear
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
            <img :src="p.image" class="h-44 w-full object-cover" />

            <!-- STATUS -->
            <span
              class="absolute top-3 left-3 text-[10px] font-semibold px-2 py-1 rounded-full text-white"
              :class="statusClass(p.status)"
            >
              {{ p.status }}
            </span>

            <!-- VIOLATION -->
            <span
              v-if="p.isViolated"
              class="absolute top-3 right-3 text-[10px] px-2 py-1 bg-red-500 text-white rounded-full"
            >
              ⚠ BAD
            </span>
          </div>

          <!-- CONTENT -->
          <div class="p-4">
            <h3 class="font-semibold text-sm mb-1">
              {{ p.title }}
            </h3>

            <p class="text-red-500 font-semibold text-sm mb-1">
              ${{ p.price }}
            </p>

            <p class="text-xs text-gray-400 mb-2">
              {{ p.author }}
            </p>

            <p v-if="p.isViolated" class="text-xs text-red-500 mb-2">
              ⚠ Nội dung vi phạm
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
import { ref, onMounted, watch } from 'vue'
import api from '../api'
import { computed } from 'vue'
const posts = ref([])
const statusFilter = ref('')
const filteredPosts = computed(() => {
  return posts.value
})
// ===== TEXT FILTER (AI CHECK) =====
const normalize = (text) => {
  return text
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
}

const badWords = ["dit", "djt", "fuck", "lon", "cho", "ngu", "me may", "sex"]

const buildRegex = (word) => {
  return new RegExp(word.split('').join('[\\W_]*'), 'i')
}

const checkViolation = (text) => {
  if (!text) return false

  const t = normalize(text)

  for (let w of badWords) {
    if (buildRegex(w).test(t)) return true
  }

  if (/(.)\1{5,}/.test(t)) return true
  if (/(may|tao).*(ngu|cho)/i.test(t)) return true

  return false
}

// ===== FETCH =====
const fetchPosts = async () => {
  try {
    const res = await api.get('/posts/admin', {
      params: {
        status: statusFilter.value || null
      }
    })

    posts.value = res.data.map(p => {
      const content = (p.title || '') + ' ' + (p.content || '')

      return {
        id: p.id,
        title: p.title,
        price: p.price,
        author: p.userName || 'Unknown',
        status: p.status,
        isViolated: checkViolation(content),
        image: p.images?.[0] || `https://picsum.photos/300/200?random=${p.id}`
      }
    })

  } catch (err) {
    console.error(err)
  }
}

// ===== FILTER =====
watch(statusFilter, () => {
  fetchPosts()
})

const clearFilter = () => {
  statusFilter.value = ''
  fetchPosts()
}

// ===== UI =====
const statusClass = (status) => {
  if (status === 'PUBLISHED') return 'bg-green-500'
  if (status === 'DRAFT') return 'bg-gray-400'
  if (status === 'HIDDEN') return 'bg-red-500'
  return 'bg-gray-300'
}

const deletePost = async (id) => {
  if (!confirm('Hide this post?')) return

  try {
    await api.put(`/posts/${id}/hide`)

    // update UI ngay lập tức
    const post = posts.value.find(p => p.id === id)
    if (post) post.status = 'HIDDEN'

  } catch (err) {
    console.error(err)
    alert('Hide failed')
  }
}

onMounted(fetchPosts)
</script>