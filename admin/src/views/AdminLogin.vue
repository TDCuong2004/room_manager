<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    
    <div class="bg-white p-8 rounded-2xl shadow w-[400px]">
      <h2 class="text-2xl font-bold mb-6 text-center">Admin Login</h2>

      <input v-model="username" placeholder="Username"
        class="w-full mb-3 p-3 border rounded-lg" />

      <input v-model="password" type="password" placeholder="Password"
        class="w-full mb-4 p-3 border rounded-lg" />

      <button @click="login"
        class="w-full bg-red-500 text-white py-3 rounded-lg hover:bg-red-600">
        Login
      </button>

      <p class="text-center mt-4 text-sm">
        Chưa có tài khoản?
        <span @click="$router.push('/register')" 
              class="text-red-500 cursor-pointer">Đăng ký</span>
      </p>

      <p v-if="error" class="text-red-500 text-sm mt-3 text-center">
        {{ error }}
      </p>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import api from '../api'
import { useRouter } from 'vue-router'

const router = useRouter()

const username = ref('')
const password = ref('')
const error = ref('')

const login = async () => {
  try {
    const res = await api.post('/auth/login', {
      username: username.value,
      password: password.value
    })

    localStorage.setItem('token', res.data.token)

    router.push('/admin') // admin dashboard
  } catch (err) {
    error.value = err.response?.data?.error || 'Login failed'
  }
}
</script>