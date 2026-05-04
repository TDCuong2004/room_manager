<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="w-full max-w-md bg-white p-8 rounded-2xl shadow-lg">

      <h2 class="text-2xl font-bold text-center text-rose-500 mb-6">
        Đăng Nhập
      </h2>

      <form @submit.prevent="handleLogin" class="space-y-4">
        
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            Tên đăng nhập
          </label>
          <input
            v-model="username"
            required
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-rose-400"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            Mật khẩu
          </label>
          <input
            type="password"
            v-model="password"
            required
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-rose-400"
          />
        </div>

        <button
          type="submit"
          class="w-full py-2 bg-rose-500 text-white rounded-lg font-semibold hover:bg-rose-600 transition"
        >
          Đăng nhập
        </button>
      </form>

      <div class="flex justify-between mt-4 text-sm">
        <router-link to="/forgot-password" class="text-rose-500 hover:underline">
          Quên mật khẩu?
        </router-link>
        <router-link to="/register" class="text-rose-500 hover:underline">
          Đăng ký
        </router-link>
      </div>

      <p v-if="error" class="text-red-500 text-center mt-3">
        {{ error }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue"
import api from "@/api"
import { useRouter } from "vue-router"

const router = useRouter()

const username = ref("")
const password = ref("")
const error = ref("")

const handleLogin = async () => {
  error.value = ""

  try {
    const res = await api.post("/auth/login", {
      username: username.value,
      password: password.value
    })

    const { token, role } = res.data

    if (!token) throw new Error("No token")

    // lưu token + role
    localStorage.setItem("token", token)
    localStorage.setItem("role", role)

    // đợi interceptor
    await new Promise(r => setTimeout(r, 50))

    const profileRes = await api.get("/users/profile")
    localStorage.setItem("user", JSON.stringify(profileRes.data))

    window.dispatchEvent(new Event("userUpdated"))

    // 🔥 ĐIỀU HƯỚNG THEO ROLE
    if (role === "ADMIN") {
      router.push("/admin/users")
    } else {
      router.push("/")
    }

  } catch (err) {
    console.error(err)
    error.value = "Sai tài khoản hoặc mật khẩu!"
  }
}
</script>