<template>
  <div
    class="min-h-screen flex items-center justify-center bg-[#e9edf2] px-4"
  >
    <div
      class="w-full max-w-5xl h-[620px] bg-[#f8f8f8] rounded-[40px] shadow-2xl overflow-hidden flex"
    >
      <!-- LEFT -->
      <div
        class="w-1/2 flex flex-col justify-center px-16 bg-[#f8f8f8]"
      >
        <div class="max-w-sm mx-auto w-full">
          <h2 class="text-5xl font-bold text-gray-800 mb-8 text-center">
            Sign In
          </h2>

          <!-- social -->
          <div class="flex justify-center gap-3 mb-5">
            <button
              class="w-11 h-11 border border-gray-300 rounded-md flex items-center justify-center hover:bg-gray-100 transition"
            >
              G+
            </button>

            <button
              class="w-11 h-11 border border-gray-300 rounded-md flex items-center justify-center hover:bg-gray-100 transition"
            >
              f
            </button>

            <button
              class="w-11 h-11 border border-gray-300 rounded-md flex items-center justify-center hover:bg-gray-100 transition"
            >
              in
            </button>
          </div>

          <p class="text-center text-gray-400 text-sm mb-10">
            or use your Phone and password
          </p>

          <!-- form -->
          <form @submit.prevent="handleLogin" class="space-y-5">
            <div>
              <input
                v-model="username"
                type="text"
                placeholder="Phone"
                required
                class="w-full bg-[#f1f1f1] border-none rounded-lg px-5 py-4 outline-none focus:ring-2 focus:ring-[#4b34b3]"
              />
            </div>

            <div>
              <input
                type="password"
                v-model="password"
                placeholder="Password"
                required
                class="w-full bg-[#f1f1f1] border-none rounded-lg px-5 py-4 outline-none focus:ring-2 focus:ring-[#4b34b3]"
              />
            </div>

            <div class="text-center">
              <router-link
                to="/forgot-password"
                class="text-sm text-gray-400 hover:text-[#4b34b3] transition"
              >
                Forget Your Password?
              </router-link>
            </div>

            <button
              type="submit"
              class="w-full py-4 rounded-xl bg-[#4b34b3] text-white font-semibold tracking-wide shadow-lg hover:scale-[1.02] hover:bg-[#3f2aa3] transition duration-300"
            >
              SIGN IN
            </button>
          </form>

          <p
            v-if="error"
            class="text-red-500 text-center mt-4 text-sm"
          >
            {{ error }}
          </p>
        </div>
      </div>

      <!-- RIGHT -->
      <div class="w-1/2 relative overflow-hidden">

        <!-- nền tím -->
        <div
          class="absolute inset-0 bg-[#4b34b3]
                rounded-l-[160px]
                rounded-r-[40px]"
        ></div>

        <!-- content -->
        <div
          class="relative z-10 h-full flex flex-col items-center justify-center px-14 text-center text-white"
        >
          <h1 class="text-5xl font-bold mb-6">
            Hello, Friend!
          </h1>

          <p class="text-lg text-gray-200 leading-8 mb-10">
            Register with your personal details to start your
            journey into premium room management.
          </p>

          <router-link
            to="/register"
            class="border-2 border-white px-12 py-4 rounded-xl font-semibold tracking-wide hover:bg-white hover:text-[#4b34b3] transition duration-300"
          >
            SIGN UP
          </router-link>
        </div>
      </div>
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

    localStorage.setItem("token", token)
    localStorage.setItem("role", role)

    await new Promise(r => setTimeout(r, 50))

    const profileRes = await api.get("/users/profile")
    localStorage.setItem("user", JSON.stringify(profileRes.data))

    window.dispatchEvent(new Event("userUpdated"))

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

<style scoped>
input::placeholder {
  color: #9ca3af;
}
</style>