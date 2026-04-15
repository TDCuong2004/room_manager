<template>
  <!-- TOAST -->
  <div
    v-if="toast.show"
    class="fixed top-5 right-5 px-4 py-3 rounded-xl text-white shadow-lg z-[9999] transition-all"
    :class="toast.type === 'success' ? 'bg-green-500' : 'bg-red-500'"
  >
    {{ toast.message }}
  </div>

  <!-- MODAL -->
  <div class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50" @click.self="close">

    <div class="w-full max-w-md bg-white rounded-2xl shadow-2xl p-6 animate-scaleIn">

      <h2 class="text-xl font-bold text-gray-800 mb-6">
        Thêm Nhà Trọ
      </h2>

      <form @submit.prevent="handleSubmit" class="space-y-4">

        <!-- TÊN -->
        <div>
          <label class="text-xs font-semibold text-gray-500 uppercase">Tên nhà trọ</label>
          <input 
            v-model="building.buildingName"
            required
            class="w-full mt-1 p-3 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:ring-2 focus:ring-orange-400 outline-none transition"
          />
        </div>

        <!-- ĐỊA CHỈ -->
        <div>
          <label class="text-xs font-semibold text-gray-500 uppercase">Địa chỉ</label>
          <input 
            v-model="building.address"
            required
            class="w-full mt-1 p-3 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:ring-2 focus:ring-orange-400 outline-none transition"
          />
        </div>

        <!-- MÔ TẢ -->
        <div>
          <label class="text-xs font-semibold text-gray-500 uppercase">Mô tả</label>
          <textarea 
            v-model="building.description"
            class="w-full mt-1 p-3 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:ring-2 focus:ring-orange-400 outline-none transition"
          />
        </div>

        <!-- BUTTON -->
        <div class="flex justify-end gap-3 pt-3">

          <button
            type="button"
            @click="close"
            class="px-4 py-2 rounded-xl bg-gray-100 hover:bg-gray-200 text-gray-600 transition"
          >
            Hủy
          </button>

          <button
            type="submit"
            :disabled="loading"
            class="px-5 py-2 rounded-xl bg-gradient-to-r from-orange-500 to-yellow-500 text-white font-semibold shadow-md hover:opacity-90 active:scale-95 transition flex items-center gap-2"
          >
            <span v-if="loading" class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
            {{ loading ? "Đang thêm..." : "Thêm" }}
          </button>

        </div>

      </form>

    </div>
  </div>
</template>

<script setup>
import { ref } from "vue"
import api from "@/api"

const emit = defineEmits(["close","reload"])

const loading = ref(false)

const building = ref({
  buildingName: "",
  address: "",
  description: ""
})

// TOAST STATE
const toast = ref({
  show: false,
  message: "",
  type: "success"
})

const showToast = (message, type = "success") => {
  toast.value = {
    show: true,
    message,
    type
  }

  setTimeout(() => {
    toast.value.show = false
  }, 2500)
}

const close = () => emit("close")

const handleSubmit = async () => {
  try {
    const token = localStorage.getItem("token")

    if (!token) {
      showToast("Bạn chưa đăng nhập ⚠️", "error")
      return
    }

    loading.value = true

    await api.post("/buildings", building.value)

    showToast("Thêm nhà trọ thành công 🎉", "success")

    setTimeout(() => {
      emit("reload")
      emit("close")
    }, 800)

  } catch (err) {
    console.error(err)
    showToast("Lỗi khi thêm nhà trọ ❌", "error")
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
@keyframes scaleIn {
  from {
    transform: scale(0.9);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.animate-scaleIn {
  animation: scaleIn 0.2s ease;
}
</style>