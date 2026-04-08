<template>
  <div class="wrapper">
    <div class="card">
      <h2>Đăng Nhập</h2>

      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>Tên đăng nhập</label>
          <input v-model="username" required />
        </div>

        <div class="form-group">
          <label>Mật khẩu</label>
          <input type="password" v-model="password" required />
        </div>

        <button type="submit">Đăng nhập</button>
      </form>

      <div class="extra-links">
        <router-link to="/forgot-password">Quên mật khẩu?</router-link>
        <router-link to="/register">Chưa có tài khoản? Đăng ký</router-link>
      </div>

      <p v-if="error" class="error">{{ error }}</p>
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
    // ✅ dùng đúng api instance
    const res = await api.post("/auth/login", {
      username: username.value,
      password: password.value
    })

    const token = res.data.token

    if (!token) {
      throw new Error("Không nhận được token")
    }

    // ✅ lưu token TRƯỚC
    localStorage.setItem("token", token)

    // ✅ QUAN TRỌNG: đợi 1 tick để interceptor lấy token
    await new Promise(resolve => setTimeout(resolve, 50))

    // ✅ gọi profile
    const profileRes = await api.get("/users/profile")

    localStorage.setItem("user", JSON.stringify(profileRes.data))

    // cập nhật header
    window.dispatchEvent(new Event("userUpdated"))

    router.push("/")

  } catch (err) {
    console.error(err)
    error.value = "Sai tài khoản hoặc mật khẩu!"
  }
}
</script>

<style scoped>
.wrapper {
  height: calc(100vh - 150px);
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f7f7f7;
}

.card {
  width: 360px;
  padding: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}

h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #ff385c;
}

.form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 15px;
}

input {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
}

button {
  width: 100%;
  padding: 10px;
  background: #ff385c;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
}

button:hover {
  opacity: 0.9;
}

.extra-links {
  margin-top: 15px;
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.extra-links a {
  text-decoration: none;
  color: #ff385c;
}

.error {
  color: red;
  text-align: center;
  margin-top: 10px;
}
</style>