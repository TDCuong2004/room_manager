<template>
  <div
    class="min-h-screen flex items-center justify-center bg-[#e9edf2] px-4"
  >
    <div
      class="w-full max-w-5xl h-[620px] bg-[#f8f8f8] rounded-[40px] shadow-2xl overflow-hidden flex"
    >

      <!-- LEFT PURPLE -->
      <div class="w-[38%] relative overflow-hidden bg-[#f8f8f8]">

        <!-- nền tím -->
        <div
          class="absolute inset-0 bg-[#4b34b3]
                 rounded-r-[120px]
                 rounded-l-[30px]"
        ></div>

        <!-- content -->
        <div
          class="relative z-10 h-full flex flex-col items-center justify-center text-center px-10 text-white"
        >
          <h1 class="text-4xl font-bold mb-5">
            Welcome Back!
          </h1>

          <p class="text-sm leading-7 text-gray-200 mb-10">
            Enter your personal details to use all of site features
          </p>

          <router-link
            to="/login"
            class="border border-white px-10 py-3 rounded-lg text-sm font-semibold tracking-wide hover:bg-white hover:text-[#4b34b3] transition duration-300"
          >
            SIGN IN
          </router-link>

          <p class="absolute bottom-8 text-[10px] tracking-[4px] text-gray-300">
            ROOM MANAGER ECO SYSTEM
          </p>
        </div>
      </div>

      <!-- RIGHT FORM -->
      <div class="flex-1 flex items-center justify-center px-16">
        <div class="w-full max-w-md">

          <h2 class="text-4xl font-bold text-gray-800 text-center mb-8">
            Create account
          </h2>

          <!-- social -->
          <div class="flex justify-center gap-3 mb-4">
            <button
              class="w-10 h-10 border border-gray-300 rounded-md flex items-center justify-center hover:bg-gray-100 transition"
            >
              G+
            </button>

            <button
              class="w-10 h-10 border border-gray-300 rounded-md flex items-center justify-center hover:bg-gray-100 transition"
            >
              f
            </button>


            <button
              class="w-10 h-10 border border-gray-300 rounded-md flex items-center justify-center hover:bg-gray-100 transition"
            >
              in
            </button>
          </div>

          <p class="text-center text-gray-400 text-sm mb-8">
            or use your email for registration
          </p>

          <!-- FORM -->
          <form @submit.prevent="handleRegister" class="space-y-4">

            <input
              v-model="form.fullName"
              type="text"
              placeholder="Name"
              required
              class="w-full bg-[#efefef] rounded-lg px-5 py-4 outline-none border-none focus:ring-2 focus:ring-[#4b34b3]"
            />

            <input
              v-model="form.phone"
              type="text"
              placeholder="Phone"
              required
              class="w-full bg-[#efefef] rounded-lg px-5 py-4 outline-none border-none focus:ring-2 focus:ring-[#4b34b3]"
            />

            <input
              v-model="form.password"
              type="password"
              placeholder="Password"
              required
              class="w-full bg-[#efefef] rounded-lg px-5 py-4 outline-none border-none focus:ring-2 focus:ring-[#4b34b3]"
            />

            <input
              v-model="form.confirmPassword"
              type="password"
              placeholder="Confirm Password"
              required
              class="w-full bg-[#efefef] rounded-lg px-5 py-4 outline-none border-none focus:ring-2 focus:ring-[#4b34b3]"
            />

            <!-- ERROR -->
            <p v-if="error" class="text-red-500 text-sm text-center">
              {{ error }}
            </p>

            <!-- BUTTON -->
            <button
              type="submit"
              :disabled="loading"
              class="w-full py-4 rounded-xl bg-[#4b34b3] text-white font-semibold tracking-wide shadow-lg hover:bg-[#3f2aa3] transition duration-300 disabled:opacity-50"
            >
              {{ loading ? "LOADING..." : "SIGN UP" }}
            </button>

          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue"
import { useRouter } from "vue-router"
import api from "@/api"
import { useToast } from "vue-toastification"

const router = useRouter()
const toast = useToast()

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

  if (!form.fullName || form.fullName.length < 3) {
    error.value = "Họ tên phải ít nhất 3 ký tự"
    return
  }

  if (!/^(0[3|5|7|8|9])[0-9]{8}$/.test(form.phone)) {
    error.value = "Số điện thoại không hợp lệ"
    return
  }

  if (form.password.length < 8) {
    error.value = "Mật khẩu phải ít nhất 8 ký tự"
    return
  }

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

    toast.success("Đăng ký thành công!")

    setTimeout(() => {
      router.push("/")
    }, 1000)

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
input::placeholder {
  color: #9ca3af;
}
</style>