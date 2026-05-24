<template>
  <div
    class="min-h-screen flex items-center justify-center bg-[#e9edf2] px-4"
  >
    <div
      class="w-full max-w-5xl h-[620px] bg-[#f8f8f8] rounded-[40px] shadow-2xl overflow-hidden flex"
    >

      <!-- LEFT -->
      <div class="w-[40%] relative overflow-hidden bg-[#f8f8f8]">

        <!-- nền tím -->
        <div
          class="absolute inset-0 bg-[#4b34b3]
                 rounded-r-[140px]
                 rounded-l-[30px]"
        ></div>

        <!-- content -->
        <div
          class="relative z-10 h-full flex flex-col items-center justify-center text-center px-10 text-white"
        >
          <h1 class="text-4xl font-bold mb-5">
            Forgot Password?
          </h1>

          <p class="text-sm leading-7 text-gray-200 mb-10">
            Enter your email address and we’ll send you a new password instantly.
          </p>

          <router-link
            to="/login"
            class="border border-white px-10 py-3 rounded-lg text-sm font-semibold tracking-wide hover:bg-white hover:text-[#4b34b3] transition duration-300"
          >
            BACK TO LOGIN
          </router-link>

          <p class="absolute bottom-8 text-[10px] tracking-[4px] text-gray-300">
            ROOM MANAGER ECO SYSTEM
          </p>
        </div>
      </div>

      <!-- RIGHT FORM -->
      <div class="flex-1 flex items-center justify-center px-16">

        <div class="w-full max-w-md">

          <!-- TITLE -->
          <h2 class="text-4xl font-bold text-gray-800 text-center mb-3">
            Reset Password
          </h2>

          <p class="text-center text-gray-400 text-sm mb-10">
            Use your email to receive a new password
          </p>

          <!-- FORM -->
          <form
            @submit.prevent="handleForgotPassword"
            class="space-y-5"
          >

            <div>
              <input
                type="email"
                v-model="email"
                required
                placeholder="example@gmail.com"
                class="w-full bg-[#efefef] rounded-lg px-5 py-4 outline-none border-none focus:ring-2 focus:ring-[#4b34b3]"
              />
            </div>

            <!-- BUTTON -->
            <button
              type="submit"
              :disabled="loading"
              class="w-full py-4 rounded-xl bg-[#4b34b3] text-white font-semibold tracking-wide shadow-lg hover:bg-[#3f2aa3] transition duration-300 disabled:opacity-50"
            >
              {{ loading ? "SENDING..." : "SEND NEW PASSWORD" }}
            </button>

          </form>

          <!-- SUCCESS -->
          <p
            v-if="success"
            class="text-green-600 text-sm text-center mt-5"
          >
            {{ success }}
          </p>

          <!-- ERROR -->
          <p
            v-if="error"
            class="text-red-500 text-sm text-center mt-5"
          >
            {{ error }}
          </p>

        </div>
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

<style scoped>
input::placeholder {
  color: #9ca3af;
}
</style>