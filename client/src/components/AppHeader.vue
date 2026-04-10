<template>
  <header class="header">
    <div class="top-bar">

      <!-- LEFT -->
      <div class="logo" @click="$router.push('/')">
        🏠 RoomManager
      </div>

      <!-- CENTER MENU -->
      <nav class="center-menu">
        <router-link to="/rooms" class="menu-item">Phòng trống</router-link>
        <router-link to="/areas" class="menu-item">Khu trọ</router-link>
        <router-link to="/guide" class="menu-item">Hướng dẫn thuê</router-link>
      </nav>

      <div class="right-section">

        <!-- ❌ CHƯA LOGIN -->
        <template v-if="!isLoggedIn">
          <router-link to="/login" class="auth-btn">
            Đăng nhập
          </router-link>
        </template>

        <!-- ✅ ĐÃ LOGIN -->
        <div v-else class="user-menu">

          <!-- 🔔 NOTIFICATION -->
          <div class="notification">
            <div class="bell" @click="toggleNotify">
              🔔
              <span v-if="expiringContracts.length > 0" class="badge">
                {{ expiringContracts.length }}
              </span>
            </div>

            <!-- POPUP -->
            <div v-if="showNotify" class="notify-dropdown">
              <div v-if="expiringContracts.length === 0">
                Không có thông báo
              </div>

              <div
                v-for="c in expiringContracts"
                :key="c.id"
                class="notify-item"
              >
                <p>
                  Phòng {{ c.roomName }} sắp hết hạn <br>

                  <small>
                    Người thuê:
                    <span v-if="c.customers?.length">
                      {{ c.customers.join(", ") }}
                    </span>
                    <span v-else>Không có</span>
                  </small>
                </p>

                <button @click="openRenew(c)">Gia hạn</button>
              </div>
            </div>
          </div>

          <!-- AVATAR -->
          <div class="avatar" @click="toggleDropdown">
            <img v-if="user.avatar" :src="user.avatar" />
            <span v-else>
              {{ user.fullName?.charAt(0) || "U" }}
            </span>
          </div>

          <!-- DROPDOWN -->
          <div v-if="showDropdown" class="dropdown">
            <button @click="confirmLogout">Đăng xuất</button>
            <router-link to="/dashboard">Phòng của tôi</router-link>
            <router-link to="/profile">Hồ sơ</router-link>
          </div>

        </div>

      </div>
    </div>

    <!-- SEARCH -->
    <div class="search-wrapper">
      <div class="search-box">

        <div class="search-item">
          <label>Địa điểm</label>
          <input v-model="search.location" placeholder="Nhập khu vực..." />
        </div>

        <div class="divider"></div>

        <div class="search-item">
          <label>Giá tối đa</label>
          <input v-model="search.maxPrice" type="number" placeholder="VNĐ" />
        </div>

        <div class="divider"></div>

        <div class="search-item">
          <label>Diện tích</label>
          <input v-model="search.area" type="number" placeholder="m²" />
        </div>

        <button class="search-btn" @click="handleSearch">
          🔍
        </button>

      </div>
    </div>

  </header>
<!-- 🔥 MODAL GIA HẠN -->
<div v-if="showRenewModal" class="modal-overlay" @click.self="showRenewModal = false">
  <div class="modal-container">
    <div class="modal-header">
      <h3>Gia hạn hợp đồng</h3>
      <button class="close-btn" @click="showRenewModal = false">&times;</button>
    </div>

    <div class="modal-body">
      <div class="info-card">
        <div class="info-row">
          <span class="label">Phòng:</span>
          <span class="value">{{ selectedContract?.roomName }}</span>
        </div>
        <div class="info-row">
          <span class="label">Người thuê:</span>
          <span class="value">
            {{ selectedContract?.customers?.length ? selectedContract.customers.join(", ") : "Không có" }}
          </span>
        </div>
        <div class="info-row">
          <span class="label">Hết hạn hiện tại:</span>
          <span class="value highlight">{{ selectedContract?.endDate }}</span>
        </div>
      </div>

      <div class="input-group">
        <label>Ngày kết thúc mới</label>
        <input
          type="date"
          v-model="newEndDate"
          :min="selectedContract?.endDate"
          class="date-input"
        />
        <small class="hint">Vui lòng chọn ngày kết thúc tiếp theo cho hợp đồng này.</small>
      </div>
    </div>

    <div class="modal-footer">
      <button class="btn-secondary" @click="showRenewModal = false">Hủy bỏ</button>
      <button class="btn-primary" @click="submitRenew">Xác nhận gia hạn</button>
    </div>
  </div>
</div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from "vue"
import { useRouter, useRoute } from "vue-router"
import api from "@/api"

const router = useRouter()
const route = useRoute()

// ================= AUTH =================
const isLoggedIn = ref(false)
const showDropdown = ref(false)
const user = ref({})

// check login
const checkLogin = () => {
  isLoggedIn.value = !!localStorage.getItem("token")
}

// load user từ localStorage
const updateUserFromStorage = () => {
  const storedUser = localStorage.getItem("user")
  if (storedUser) {
    user.value = JSON.parse(storedUser)
  }
}
// ================= 🔥 MODAL =================
const showRenewModal = ref(false)
const selectedContract = ref(null)
const newEndDate = ref("")

const openRenew = (contract) => {
  selectedContract.value = contract
  newEndDate.value = ""
  showRenewModal.value = true
}

const submitRenew = async () => {
  if (!newEndDate.value) {
    alert("Vui lòng chọn ngày")
    return
  }

  try {
    await api.post(
      `/contracts/${selectedContract.value.id}/renew`,
      null,
      { params: { endDate: newEndDate.value } }
    )

    alert("Gia hạn thành công")
    showRenewModal.value = false
    loadExpiring()

  } catch (err) {
    console.error(err)
    alert("Lỗi gia hạn")
  }
}
// ================= NOTIFICATION =================
const expiringContracts = ref([])
const showNotify = ref(false)

// load contracts sắp hết hạn
const loadExpiring = async () => {
  if (!localStorage.getItem("token")) return

  try {
    const res = await api.get("/contracts/expiring-soon")
    expiringContracts.value = res.data
  } catch (err) {
    console.error("Load expiring error:", err)
  }
}

// toggle chuông
const toggleNotify = () => {
  showNotify.value = !showNotify.value
}

// format date chuẩn
const formatDate = (input) => {
  const d = new Date(input)
  if (isNaN(d)) return null

  return (
    d.getFullYear() +
    "-" +
    String(d.getMonth() + 1).padStart(2, "0") +
    "-" +
    String(d.getDate()).padStart(2, "0")
  )
}

// click ngoài đóng notify
const handleClickOutside = (e) => {
  const notify = document.querySelector(".notification")
  if (notify && !notify.contains(e.target)) {
    showNotify.value = false
  }
}

// ================= USER =================
const fetchUser = async () => {
  if (!localStorage.getItem("token")) return

  try {
    const res = await api.get("/users/profile")
    user.value = res.data
    localStorage.setItem("user", JSON.stringify(res.data))
  } catch (err) {
    console.error("Profile error:", err)
  }
}

// ================= DROPDOWN =================
const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
}

// logout
const confirmLogout = () => {
  if (confirm("Bạn có chắc chắn muốn đăng xuất không?")) {
    localStorage.removeItem("token")
    localStorage.removeItem("user")

    checkLogin()
    user.value = {}
    expiringContracts.value = []

    showDropdown.value = false
    router.push("/")
  }
}

// ================= SEARCH =================
const search = ref({
  location: "",
  maxPrice: "",
  area: ""
})

const handleSearch = () => {
  router.push({
    path: "/rooms",
    query: search.value
  })
}

// ================= 🔥 CORE FIX =================

// khi login / update user → reload toàn bộ
const handleUserUpdated = () => {
  checkLogin()
  updateUserFromStorage()
  fetchUser()
  loadExpiring()
}

// ================= LIFECYCLE =================
onMounted(() => {
  handleUserUpdated()

  window.addEventListener("userUpdated", handleUserUpdated)
  document.addEventListener("click", handleClickOutside)
})

onBeforeUnmount(() => {
  window.removeEventListener("userUpdated", handleUserUpdated)
  document.removeEventListener("click", handleClickOutside)
})

// ================= WATCH =================
watch(route, () => {
  checkLogin()
  showDropdown.value = false
})
</script>

<style scoped>
.header{
  background:#fff;
  border-bottom:1px solid #eee;
  position:sticky;
  top:0;
  z-index:1000;
}

/* TOP BAR */
.top-bar{
  width:100%;
  padding:0 60px;
  height:75px;
  display:flex;
  align-items:center;
  justify-content:space-between;
}

/* LOGO */
.logo{
  font-size:22px;
  font-weight:700;
  color:#ff385c;
  cursor:pointer;
}

/* MENU */
.center-menu{
  display:flex;
  gap:35px;
  font-weight:600;
}

.menu-item{
  text-decoration:none;
  color:#333;
  padding-bottom:4px;
  position:relative;
  transition:0.2s;
}

.menu-item:hover{
  color:#ff385c;
}

.menu-item.router-link-active{
  color:#ff385c;
}

.menu-item.router-link-active::after{
  content:"";
  position:absolute;
  bottom:-6px;
  left:0;
  width:100%;
  height:2px;
  background:#ff385c;
}

/* RIGHT SECTION */
.right-section{
  display:flex;
  align-items:center;
  gap:18px;
}

/* LOGIN BUTTON */
.auth-btn{
  padding:8px 16px;
  border-radius:20px;
  border:1px solid #ddd;
  text-decoration:none;
  color:#333;
  font-weight:500;
  transition:0.2s;
}

.auth-btn:hover{
  background:#f7f7f7;
}
.user-menu {
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
}

/* AVATAR */
.avatar{
  width:38px;
  height:38px;
  border-radius:50%;
  overflow:hidden;
  background:#ff385c;
  color:white;
  display:flex;
  align-items:center;
  justify-content:center;
  font-weight:bold;
  cursor:pointer;
  transition:0.2s;
}

.avatar:hover{
  transform:scale(1.05);
}

.avatar img{
  width:100%;
  height:100%;
  object-fit:cover;
}

/* DROPDOWN */
.dropdown{
  position:absolute;
  right:0;
  top:50px;
  background:white;
  border-radius:12px;
  width:190px;
  box-shadow:0 10px 30px rgba(0,0,0,0.12);
  overflow:hidden;
  animation:fadeDown 0.2s ease;
}

.dropdown a,
.dropdown button{
  display:block;
  width:100%;
  padding:12px 16px;
  text-align:left;
  background:none;
  border:none;
  text-decoration:none;
  color:#333;
  cursor:pointer;
  font-size:14px;
}

.dropdown a:hover,
.dropdown button:hover{
  background:#f6f6f6;
}

/* SEARCH WRAPPER */
.search-wrapper{
  display:flex;
  justify-content:center;
  padding:20px 0 25px;
}

/* SEARCH BOX */
.search-box{
  display:flex;
  align-items:center;
  background:white;
  border-radius:40px;
  box-shadow:0 10px 30px rgba(0,0,0,0.08);
  padding:12px 25px;
  gap:25px;
  width:850px;
  transition:0.2s;
}

.search-box:hover{
  box-shadow:0 12px 35px rgba(0,0,0,0.12);
}

/* SEARCH ITEM */
.search-item{
  display:flex;
  flex-direction:column;
}

.search-item label{
  font-size:12px;
  font-weight:600;
  color:#666;
}

.search-item input{
  border:none;
  outline:none;
  font-size:14px;
  width:150px;
}

/* DIVIDER */
.divider{
  width:1px;
  height:35px;
  background:#eee;
}

/* SEARCH BUTTON */
.search-btn{
  width:42px;
  height:42px;
  border-radius:50%;
  background:#ff385c;
  color:white;
  border:none;
  font-size:18px;
  cursor:pointer;
  transition:0.2s;
}

.search-btn:hover{
  transform:scale(1.08);
}

/* ANIMATION */
@keyframes fadeDown{
  from{
    opacity:0;
    transform:translateY(-10px);
  }
  to{
    opacity:1;
    transform:translateY(0);
  }
}

.notification {
  position: relative;
  display: flex;
  align-items: center;
}

.bell {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: 0.2s;
  font-size: 16px;
}

.bell:hover {
  background: #e5e7eb;
}

.badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #ff385c;
  color: white;
  font-size: 10px;
  min-width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-weight: 600;
}
.notify-dropdown {
  position: absolute;
  top: 45px;
  right: 0;
  width: 280px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 15px 40px rgba(0,0,0,0.15);
  padding: 10px;
  z-index: 999;
}
.notify-item {
  padding: 10px;
  border-radius: 8px;
  transition: 0.2s;
}

.notify-item:hover {
  background: #f9fafb;
}

.notify-item p {
  font-size: 13px;
  margin-bottom: 6px;
}

.notify-item button {
  background: #ff385c;
  border: none;
  color: white;
  padding: 5px 10px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
}
/* --- MODERN MODAL STYLES --- */

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px); /* Làm mờ nền sau modal */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  animation: fadeIn 0.3s ease;
}

.modal-container {
  background: white;
  width: 100%;
  max-width: 450px;
  border-radius: 16px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  animation: slideUp 0.3s ease-out;
}

.modal-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #222;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #666;
  cursor: pointer;
  line-height: 1;
}

.modal-body {
  padding: 24px;
}

/* Thẻ chứa thông tin hiện tại */
.info-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 24px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-row .label {
  color: #717171;
}

.info-row .value {
  font-weight: 600;
  color: #222;
}

.info-row .value.highlight {
  color: #ff385c;
}

/* Input Group */
.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-group label {
  font-size: 14px;
  font-weight: 600;
  color: #222;
}

.date-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 15px;
  font-family: inherit;
  transition: border-color 0.2s;
  outline: none;
}

.date-input:focus {
  border-color: #ff385c;
  box-shadow: 0 0 0 3px rgba(255, 56, 92, 0.1);
}

.hint {
  font-size: 12px;
  color: #717171;
}

/* Footer & Buttons */
.modal-footer {
  padding: 16px 24px;
  background: #f8f9fa;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn-primary {
  background: #ff385c;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
}

.btn-primary:hover {
  background: #e31c5f;
}

.btn-secondary {
  background: white;
  color: #222;
  border: 1px solid #ddd;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
}

.btn-secondary:hover {
  background: #f7f7f7;
  border-color: #222;
}

/* Animations */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}
</style>