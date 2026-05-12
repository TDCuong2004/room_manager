<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    
    <div class="w-full max-w-md bg-white p-8 rounded-2xl shadow-lg">

      <!-- TITLE -->
      <h2 class="text-2xl font-bold text-center text-rose-500 mb-6">
        Quên Mật Khẩu
      </h2>

      <p class="text-sm text-gray-400 text-center mb-6">
        Nhập email để nhận mật khẩu mới
      </p>

      <!-- FORM -->
      <form @submit.prevent="handleForgotPassword" class="space-y-4">

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            Email
          </label>

          <input
            type="email"
            v-model="email"
            required
            placeholder="example@gmail.com"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-rose-400"
          />
        </div>

        <button
          type="submit"
          :disabled="loading"
          class="w-full py-2 bg-rose-500 text-white rounded-lg font-semibold hover:bg-rose-600 transition disabled:opacity-50"
        >
          {{ loading ? "Đang gửi..." : "Gửi mật khẩu mới" }}
        </button>

      </form>

      <!-- SUCCESS -->
      <p
        v-if="success"
        class="text-green-600 text-sm text-center mt-4"
      >
        {{ success }}
      </p>

      <!-- ERROR -->
      <p
        v-if="error"
        class="text-red-500 text-sm text-center mt-4"
      >
        {{ error }}
      </p>

      <!-- BACK -->
      <div class="text-center mt-6">
        <router-link
          to="/login"
          class="text-rose-500 hover:underline text-sm"
        >
          ← Quay lại đăng nhập
        </router-link>
      </div>

    </div>

  </div>
</template>

<script setup>
import { ref } from "vue"
import api from "@/api"

const email = ref("")
const loading = ref(false)

const success = ref("")
const error = ref("")

const handleForgotPassword = async () => {

  success.value = ""
  error.value = ""

  try {

    loading.value = true

    await api.post("/auth/forgot-password", null, {
      params: {
        email: email.value
      }
    })

    success.value =
      "Mật khẩu mới đã được gửi về email của bạn"

    email.value = ""

  } catch (err) {

    console.error(err)

    error.value =
      err?.response?.data ||
      "Không thể gửi mật khẩu mới"

  } finally {
    loading.value = false
  }
}
</script>