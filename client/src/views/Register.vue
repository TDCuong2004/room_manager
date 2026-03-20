<template>
  <div class="register-page">
    <div class="register-card">
      <h2>Tạo tài khoản</h2>
      <p class="subtitle">Chào mừng bạn đến với RoomManager</p>

      <form @submit.prevent="handleRegister">

        <div class="form-group">
          <label>Họ và tên</label>
          <input v-model="form.name" type="text" required />
        </div>

        <div class="form-group">
          <label>Email</label>
          <input v-model="form.email" type="email" required />
        </div>

        <div class="form-group">
          <label>Mật khẩu</label>
          <input v-model="form.password" type="password" required />
        </div>

        <div class="form-group">
          <label>Nhập lại mật khẩu</label>
          <input v-model="form.confirmPassword" type="password" required />
        </div>

        <button type="submit" class="register-btn">
          Đăng ký
        </button>

        <p class="login-link">
          Đã có tài khoản?
          <router-link to="/login">Đăng nhập</router-link>
        </p>

      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue"
import { useRouter } from "vue-router"

const router = useRouter()

const form = reactive({
  name: "",
  email: "",
  password: "",
  confirmPassword: ""
})

const handleRegister = () => {
  if (form.password !== form.confirmPassword) {
    alert("Mật khẩu không khớp!")
    return
  }

  // TODO: gọi API backend sau
  console.log("Đăng ký:", form)

  alert("Đăng ký thành công!")
  router.push("/login")
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f7f7f7;
}

.register-card {
  background: white;
  padding: 40px;
  width: 420px;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
}

h2 {
  margin-bottom: 5px;
}

.subtitle {
  font-size: 14px;
  color: #717171;
  margin-bottom: 25px;
}

.form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 18px;
}

.form-group label {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 6px;
}

.form-group input {
  padding: 12px;
  border-radius: 10px;
  border: 1px solid #ddd;
  font-size: 14px;
  transition: 0.2s ease;
}

.form-group input:focus {
  border-color: #ff385c;
  outline: none;
}

.register-btn {
  width: 100%;
  padding: 14px;
  border-radius: 999px;
  background: #ff385c;
  color: white;
  border: none;
  font-weight: 600;
  cursor: pointer;
  margin-top: 10px;
  transition: 0.2s ease;
}

.register-btn:hover {
  transform: scale(1.02);
}

.login-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
}

.login-link a {
  color: #ff385c;
  font-weight: 600;
  text-decoration: none;
}
</style>