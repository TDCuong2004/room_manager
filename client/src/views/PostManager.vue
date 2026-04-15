<template>
  <div>

    <!-- TITLE -->
    <h2 class="text-2xl font-bold mb-6">📄 Quản lý bài đăng</h2>

    <!-- LIST -->
    <div v-if="posts.length" class="grid gap-5">

      <div
        v-for="post in posts"
        :key="post.id"
        class="bg-white rounded-2xl shadow p-4 flex gap-4"
      >

        <!-- IMAGE -->
        <img
          :src="getImageUrl(post.images?.[0])"
          class="w-32 h-24 object-cover rounded-xl"
        />

        <!-- CONTENT -->
        <div class="flex-1">

          <h3 class="font-bold text-lg">{{ post.title }}</h3>

          <p class="text-gray-500 text-sm">
            {{ post.address }}
          </p>

          <p class="text-orange-500 font-bold mt-1">
            {{ formatPrice(post.price) }}
          </p>

          <!-- STATUS -->
          <div class="mt-2">
            <span
              class="px-3 py-1 rounded-full text-xs font-semibold"
              :class="getStatusClass(post)"
            >
              {{ getStatusText(post) }}
            </span>
          </div>

        </div>

        <!-- ACTION -->
        <div class="flex flex-col justify-between items-end">

          <button
            @click="confirmDelete(post.id)"
            class="text-red-500 hover:text-red-700 text-sm"
          >
            🗑 Xóa
          </button>

          <p class="text-xs text-gray-400">
            {{ formatDate(post.createdAt) }}
          </p>

        </div>

      </div>

    </div>

    <div v-else class="text-center text-gray-400">
      Chưa có bài đăng nào
    </div>

    <!-- CONFIRM MODAL -->
    <div v-if="showConfirm" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white p-6 rounded-2xl shadow-xl w-80 text-center">

        <h3 class="font-bold text-lg mb-3">
          Xóa bài viết?
        </h3>

        <p class="text-sm text-gray-500 mb-5">
          Hành động này không thể hoàn tác
        </p>

        <div class="flex justify-center gap-3">
          <button
            @click="showConfirm = false"
            class="px-4 py-2 bg-gray-100 rounded-xl"
          >
            Hủy
          </button>

          <button
            @click="deletePost"
            class="px-4 py-2 bg-red-500 text-white rounded-xl"
          >
            Xóa
          </button>
        </div>

      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import api from "@/api"

const posts = ref([])
const showConfirm = ref(false)
const selectedId = ref(null)

// ===== LOAD DATA =====
const loadPosts = async () => {
  try {
    const res = await api.get("/posts/my")
    posts.value = res.data
  } catch (err) {
    console.error(err)
  }
}

onMounted(loadPosts)

// ===== DELETE =====
const confirmDelete = (id) => {
  selectedId.value = id
  showConfirm.value = true
}

const deletePost = async () => {
  try {
     await api.put(`/posts/${selectedId.value}/hide`)
    posts.value = posts.value.filter(p => p.id !== selectedId.value)
    showConfirm.value = false
  } catch (err) {
    console.error(err)
  }
}

// ===== HELPER =====
const formatPrice = (price) => {
  return new Intl.NumberFormat("vi-VN").format(price) + " đ"
}

const formatDate = (date) => {
  return new Date(date).toLocaleDateString("vi-VN")
}

// STATUS UI
const getStatusText = (post) => {
  if (post.room && post.room.status !== "EMPTY") return "Đã có người thuê"
  return "Đang hiển thị"
}

const getStatusClass = (post) => {
  if (post.room && post.room.status !== "EMPTY") {
    return "bg-gray-200 text-gray-600"
  }
  return "bg-green-100 text-green-600"
}
const getImageUrl = (img) => {
  if (!img) return ""

  if (img.startsWith("http")) return img

  return "http://localhost:3000" + img
}

</script>