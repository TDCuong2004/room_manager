<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-100 to-gray-200">
    <div class="w-full max-w-md bg-white p-8 rounded-2xl shadow-xl">

      <!-- HEADER -->
      <h2 class="text-2xl font-bold text-gray-800">Tạo tài khoản</h2>
      <p class="text-gray-500 text-sm mb-6">
        Đăng ký để sử dụng RoomManager
      </p>

      <form @submit.prevent="handleRegister" class="space-y-4">

        <!-- FULL NAME -->
        <div>
          <label class="text-sm font-semibold text-gray-700">Họ và tên</label>
          <input
            v-model="form.fullName"
            type="text"
            placeholder="Nguyễn Văn A"
            class="w-full mt-1 p-3 border rounded-lg outline-none focus:ring-2 focus:ring-red-400 transition"
            required
          />
        </div>

        <!-- PHONE -->
        <div>
          <label class="text-sm font-semibold text-gray-700">Số điện thoại</label>
          <input
            v-model="form.phone"
            type="text"
            placeholder="0901234567"
            class="w-full mt-1 p-3 border rounded-lg outline-none focus:ring-2 focus:ring-red-400 transition"
            required
          />
        </div>

        <!-- PASSWORD -->
        <div>
          <label class="text-sm font-semibold text-gray-700">Mật khẩu</label>
          <input
            v-model="form.password"
            type="password"
            placeholder="••••••"
            class="w-full mt-1 p-3 border rounded-lg outline-none focus:ring-2 focus:ring-red-400 transition"
            required
          />
        </div>

        <!-- CONFIRM -->
        <div>
          <label class="text-sm font-semibold text-gray-700">Nhập lại mật khẩu</label>
          <input
            v-model="form.confirmPassword"
            type="password"
            placeholder="••••••"
            class="w-full mt-1 p-3 border rounded-lg outline-none focus:ring-2 focus:ring-red-400 transition"
            required
          />
        </div>

        <!-- ERROR -->
        <p v-if="error" class="text-red-500 text-sm">
          {{ error }}
        </p>

        <!-- BUTTON -->
        <button
          type="submit"
          :disabled="loading"
          class="w-full bg-red-500 text-white py-3 rounded-xl font-semibold hover:bg-red-600 transition disabled:opacity-50"
        >
          {{ loading ? "Đang xử lý..." : "Đăng ký" }}
        </button>

        <!-- LOGIN -->
        <p class="text-center text-sm mt-4">
          Đã có tài khoản?
          <router-link to="/login" class="text-red-500 font-semibold hover:underline">
            Đăng nhập
          </router-link>
        </p>

      </form>
    </div>
  </div>
</template>
<script setup>
import { reactive, ref } from "vue"
import { useRouter } from "vue-router"
import api from "@/api" // 🔥 dùng api.js của bạn

const router = useRouter()

const loading = ref(false)
const error = ref("")

const form = reactive({
  fullName: "",
  phone: "",
  password: "",
  confirmPassword: ""
})

const handleRegister = async () => {
  error.value = ""

  // 🔥 validate họ tên
  if (!form.fullName || form.fullName.length < 3) {
    error.value = "Họ tên phải ít nhất 3 ký tự"
    return
  }

  // 🔥 validate SĐT VN chuẩn hơn
  if (!/^(0[3|5|7|8|9])[0-9]{8}$/.test(form.phone)) {
    error.value = "Số điện thoại không hợp lệ"
    return
  }

  // 🔥 validate password >= 8 ký tự
  if (form.password.length < 8) {
    error.value = "Mật khẩu phải ít nhất 8 ký tự"
    return
  }

  // 🔥 confirm password
  if (form.password !== form.confirmPassword) {
    error.value = "Mật khẩu không khớp"
    return
  }

  try {
    loading.value = true

    const res = await api.post("/auth/register", {
      fullName: form.fullName,
      phone: form.phone,
      password: form.password,
      confirmPassword: form.confirmPassword
    })

    if (res.data.token) {
      localStorage.setItem("token", res.data.token)
    }

    alert("Đăng ký thành công!")
    router.push("/")

  } catch (err) {
    error.value =
      err.response?.data?.error ||
      err.response?.data?.message ||
      "Đăng ký thất bại"
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>

</style> 