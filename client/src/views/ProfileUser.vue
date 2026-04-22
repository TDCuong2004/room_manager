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
      <label>Ngân hàng</label>
      <select v-model="selectedBank" @change="handleBankChange">
        <option value="">-- Chọn ngân hàng --</option>
        <option v-for="b in banks" :key="b.bin" :value="b">
          {{ b.shortName }}
        </option>
      </select>

      <label>Mã ngân hàng</label>
      <input v-model="user.bankCode" readonly />

      <label>Số tài khoản</label>
      <input v-model="user.bankAccount" placeholder="VD: 0353260919"/>
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
  avatar: "",

  bankName: "",
  bankCode: "",
  bankAccount: ""
})
const banks = ref([])

const fetchBanks = async () => {
  const res = await fetch("https://api.vietqr.io/v2/banks")
  const data = await res.json()
  banks.value = data.data
}
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

  // tìm bank tương ứng
  selectedBank.value = banks.value.find(
    b => b.code === user.value.bankCode || b.bin === user.value.bankCode
  )
}

const selectedBank = ref(null)

const handleBankChange = () => {
  user.value.bankName = selectedBank.value.shortName
  user.value.bankCode = selectedBank.value.bin
}
// ================= UPDATE =================
const updateProfile = async () => {
  const res = await api.put("/users/profile", user.value)

  user.value = res.data
  localStorage.setItem("user", JSON.stringify(res.data))

  window.dispatchEvent(new Event("userUpdated"))

  alert("Cập nhật thành công")
}

onMounted(async () => {
  await fetchBanks()
  await fetchProfile()
})
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
}.info select{
  width:100%;
  padding:8px;
  margin:6px 0 15px;
  border:1px solid #ddd;
  border-radius:6px;
  background:white;
  font-size:14px;
  appearance: none; /* bỏ style mặc định */
  -webkit-appearance: none;
  -moz-appearance: none;

  /* icon dropdown custom */
  background-image: url("data:image/svg+xml;utf8,<svg fill='gray' height='20' viewBox='0 0 24 24' width='20' xmlns='http://www.w3.org/2000/svg'><path d='M7 10l5 5 5-5z'/></svg>");
  background-repeat: no-repeat;
  background-position: right 10px center;
  background-size:16px;
  padding-right:35px;
}
</style>