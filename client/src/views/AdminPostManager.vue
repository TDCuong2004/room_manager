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
            <div class="h-44 w-full bg-gray-100 flex items-center justify-center overflow-hidden">
              <img
                v-if="p.images?.length"
                :src="p.images[0]"
                class="h-full w-full object-cover"
              />

              <div
                v-else
                class="flex flex-col items-center text-gray-400 text-sm"
              >
                <span class="text-4xl">🖼</span>
                <p>No Image</p>
              </div>
            </div>

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
              <button
                @click="openDetail(p)"
                class="border px-3 py-1 rounded-md text-xs hover:bg-gray-100"
              >
                View Details
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
  <!-- DETAIL MODAL -->
  <div
    v-if="showModal"
    class="fixed inset-0 bg-black/50 flex items-center justify-center z-50"
  >
    <div class="bg-white w-[600px] rounded-2xl overflow-hidden shadow-xl">

      <!-- IMAGE -->
      <div class="p-4">
  
        <!-- MAIN IMAGE -->
        <div class="h-64 bg-gray-100 rounded-xl overflow-hidden flex items-center justify-center">
          <img
            v-if="selectedPost?.images?.length"
            :src="selectedImage"
            class="w-full h-full object-cover"
          />

          <div v-else class="text-gray-400 text-center">
            <div class="text-5xl">🖼</div>
            <p>No Image</p>
          </div>
        </div>

        <!-- THUMBNAILS -->
        <div
          v-if="selectedPost?.images?.length > 1"
          class="flex gap-2 mt-3 overflow-x-auto"
        >
          <img
            v-for="(img, index) in selectedPost.images"
            :key="index"
            :src="img"
            @click="selectedImage = img"
            class="w-20 h-20 object-cover rounded-lg border cursor-pointer hover:opacity-80"
            :class="selectedImage === img ? 'border-red-500 border-2' : ''"
          />
        </div>

      </div>

      <!-- CONTENT -->
      <div class="p-6">
        <div class="flex justify-between items-start mb-4">
          <div>
            <h2 class="text-xl font-semibold">
              {{ selectedPost?.title }}
            </h2>

            <p class="text-red-500 font-semibold mt-1">
              ${{ selectedPost?.price }}
            </p>
          </div>

          <button
            @click="closeModal"
            class="text-gray-400 hover:text-black text-xl"
          >
            ✕
          </button>
        </div>

        <div class="space-y-2 text-sm">
          <p>
            <span class="font-semibold">Author:</span>
            {{ selectedPost?.author }}
          </p>

          <p>
            <span class="font-semibold">Status:</span>
            {{ selectedPost?.status }}
          </p>

          <p v-if="selectedPost?.isViolated" class="text-red-500">
            ⚠ Nội dung vi phạm
          </p>
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
const statusFilter = ref('PUBLISHED')
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
const selectedPost = ref(null)
const showModal = ref(false)
const selectedImage = ref(null)
const openDetail = (post) => {
  selectedPost.value = post
  selectedImage.value = post.images?.[0] || null
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
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
        images: p.images || []
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