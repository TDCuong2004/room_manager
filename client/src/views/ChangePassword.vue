<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center px-4">

    <div class="w-full max-w-md bg-white rounded-2xl shadow-lg p-8">

      <!-- TITLE -->
      <h1 class="text-2xl font-bold text-center text-blue-600 mb-2">
        Đổi mật khẩu
      </h1>

      <p class="text-center text-gray-400 text-sm mb-6">
        Cập nhật mật khẩu mới để bảo mật tài khoản
      </p>

      <!-- FORM -->
      <form @submit.prevent="handleChangePassword" class="space-y-4">

        <!-- OLD PASSWORD -->
        <div>
          <label class="block text-sm font-medium mb-1">
            Mật khẩu cũ
          </label>

          <input
            v-model="oldPassword"
            type="password"
            required
            class="w-full border border-gray-300 rounded-xl px-4 py-3 outline-none focus:ring-2 focus:ring-rose-400"
          />
        </div>

        <!-- NEW PASSWORD -->
        <div>
          <label class="block text-sm font-medium mb-1">
            Mật khẩu mới
          </label>

          <input
            v-model="newPassword"
            type="password"
            required
            class="w-full border border-gray-300 rounded-xl px-4 py-3 outline-none focus:ring-2 focus:ring-rose-400"
          />
        </div>

        <!-- CONFIRM -->
        <div>
          <label class="block text-sm font-medium mb-1">
            Xác nhận mật khẩu
          </label>

          <input
            v-model="confirmPassword"
            type="password"
            required
            class="w-full border border-gray-300 rounded-xl px-4 py-3 outline-none focus:ring-2 focus:ring-rose-400"
          />
        </div>

        <!-- ERROR -->
        <div
          v-if="error"
          class="bg-red-50 text-red-500 text-sm px-4 py-3 rounded-xl"
        >
          {{ error }}
        </div>

        <!-- SUCCESS -->
        <div
          v-if="success"
          class="bg-green-50 text-green-600 text-sm px-4 py-3 rounded-xl"
        >
          {{ success }}
        </div>

        <!-- BUTTON -->
        <button
          type="submit"
          :disabled="loading"
          class="w-full bg-blue-600 hover:bg-blue-500 text-white font-semibold py-3 rounded-xl transition disabled:opacity-50"
        >
          {{ loading ? "Đang xử lý..." : "Đổi mật khẩu" }}
        </button>

      </form>

    </div>

  </div>
</template>

<script setup>
import { ref } from "vue"
import api from "@/api"
import { useRouter } from "vue-router"

const router = useRouter()

const oldPassword = ref("")
const newPassword = ref("")
const confirmPassword = ref("")

const loading = ref(false)
const error = ref("")
const success = ref("")

const handleChangePassword = async () => {

  error.value = ""
  success.value = ""

  // validate
  if (newPassword.value.length < 6) {
    error.value = "Mật khẩu mới tối thiểu 6 ký tự"
    return
  }

  if (newPassword.value !== confirmPassword.value) {
    error.value = "Xác nhận mật khẩu không khớp"
    return
  }

  try {

    loading.value = true

    await api.put("/auth/change-password", {
      oldPassword: oldPassword.value,
      newPassword: newPassword.value,
      confirmPassword: confirmPassword.value
    })

    success.value = "Đổi mật khẩu thành công"

    oldPassword.value = ""
    newPassword.value = ""
    confirmPassword.value = ""

    // tự chuyển sau 1.5s
    setTimeout(() => {
      router.push("/")
    }, 1500)

  } catch (err) {

    console.error(err)

    error.value =
      err.response?.data ||
      "Đổi mật khẩu thất bại"

  } finally {

    loading.value = false
  }
}
</script>