<template>
<div class="profile-page">

  <div class="profile-card">

    <div class="avatar-wrapper">

      <div class="avatar">
        <img v-if="user.avatar" :src="user.avatar"/>
        <span v-else>
          {{ user.fullName?.charAt(0) || "U" }}
        </span>
      </div>

      <label for="avatarUpload" class="change-avatar">
        Thay đổi avatar
      </label>

      <input
        id="avatarUpload"
        type="file"
        accept="image/*"
        @change="handleAvatar"
      />

    </div>

    <h2>{{ user.fullName }}</h2>

    <div class="info">

      <label>Họ tên</label>
      <input v-model="user.fullName"/>

      <label>Số điện thoại</label>
      <input v-model="user.phone"/>

      <label>Email</label>
      <input v-model="user.email"/>

    </div>

    <button @click="updateProfile">
      Cập nhật
    </button>

  </div>

</div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import api from "@/api"

const user = ref({
  fullName: "",
  phone: "",
  email: "",
  avatar: ""
})

// 🔥 upload avatar
const handleAvatar = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  try {
    const formData = new FormData()
    formData.append("file", file)

    const res = await api.post("/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    })

    // ✅ lưu URL trả về
    user.value.avatar = res.data

  } catch (err) {
    console.error(err)
    alert("Upload avatar thất bại")
  }
}

// ================= GET PROFILE =================
const fetchProfile = async () => {
  const res = await api.get("/users/profile")
  user.value = res.data
}

// ================= UPDATE =================
const updateProfile = async () => {
  const res = await api.put("/users/profile", user.value)

  user.value = res.data
  localStorage.setItem("user", JSON.stringify(res.data))

  window.dispatchEvent(new Event("userUpdated"))

  alert("Cập nhật thành công")
}

onMounted(fetchProfile)
</script>

<style scoped>
.profile-page{
  display:flex;
  justify-content:center;
  align-items:center;
  min-height:100vh;
  background:#f4f6f9;
}

.profile-card{
  width:380px;
  background:white;
  padding:30px;
  border-radius:12px;
  box-shadow:0 8px 20px rgba(0,0,0,0.08);
  text-align:center;
}

.avatar-wrapper input{
  display:none;
}

.change-avatar{
  display:inline-block;
  margin-top:10px;
  padding:6px 14px;
  font-size:14px;
  background:#4CAF50;
  color:white;
  border-radius:6px;
  cursor:pointer;
  transition:0.2s;
}

.change-avatar:hover{
  background:#43a047;
}
.avatar{
  width:110px;
  height:110px;
  margin:auto;
  border-radius:50%;
  overflow:hidden;
  background:#eee;
  display:flex;
  align-items:center;
  justify-content:center;
  font-size:36px;
  font-weight:bold;
}

.avatar img{
  width:100%;
  height:100%;
  object-fit:cover;
}

.file-input{
  margin-top:10px;
}

.info{
  text-align:left;
  margin-top:20px;
}

.info label{
  font-size:14px;
  font-weight:600;
}

.info input{
  width:100%;
  padding:8px;
  margin:6px 0 15px;
  border:1px solid #ddd;
  border-radius:6px;
}

button{
  width:100%;
  padding:10px;
  border:none;
  background:#4CAF50;
  color:white;
  font-weight:bold;
  border-radius:6px;
  cursor:pointer;
  transition:0.2s;
}

button:hover{
  background:#43a047;
}
</style>