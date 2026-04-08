<template>
  <div class="modal-overlay" @click.self="close">

    <div class="modal-card">
      <h2>Thêm Nhà Trọ</h2>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label>Tên nhà trọ</label>
          <input v-model="building.buildingName" required />
        </div>

        <div class="form-group">
          <label>Địa chỉ</label>
          <input v-model="building.address" required />
        </div>

        <div class="form-group">
          <label>Mô tả</label>
          <textarea v-model="building.description"></textarea>
        </div>

        <div class="button-group">
          <button type="submit">Thêm</button>

          <button
            type="button"
            class="cancel-btn"
            @click="close"
          >
            Hủy
          </button>
        </div>

      </form>
    </div>

  </div>
</template>

<script setup>
import { ref } from "vue"
import axios from "axios"

const emit = defineEmits(["close","reload"])

const building = ref({
  buildingName: "",
  address: "",
  description: ""
})

const close = () => {
  emit("close")
}

const handleSubmit = async () => {
  try {

    const token = localStorage.getItem("token")

    if (!token) {
      alert("Bạn chưa đăng nhập!")
      return
    }

    await axios.post(
      "http://localhost:3000/api/buildings",
      building.value,
      {
        headers:{
          Authorization:`Bearer ${token}`
        }
      }
    )

    alert("Thêm thành công")

    emit("reload") // load lại list
    emit("close")  // đóng modal

  } catch(err) {
    console.error(err)
    alert("Lỗi khi thêm nhà trọ")
  }
}
</script>

<style scoped>

.modal-overlay{
  position:fixed;
  inset:0;
  background:rgba(0,0,0,0.55);
  display:flex;
  justify-content:center;
  align-items:center;
  z-index:9999;
  backdrop-filter:blur(3px);
}

.modal-card{
  width:480px;
  background:white;
  padding:35px;
  border-radius:20px;
  box-shadow:0 25px 60px rgba(0,0,0,0.2);
  animation:popup .25s ease;
}

@keyframes popup{
  from{
    transform:scale(.9);
    opacity:0;
  }
  to{
    transform:scale(1);
    opacity:1;
  }
}

h2{
  margin-bottom:25px;
  font-size:22px;
  color:#1f2937;
}

.form-group{
  margin-bottom:18px;
}

input,textarea{
  width:100%;
  padding:12px;
  border-radius:10px;
  border:1px solid #e5e7eb;
  font-size:14px;
  background:#f9fafb;
}

input:focus,textarea:focus{
  outline:none;
  border-color:#ff512f;
}

textarea{
  min-height:90px;
  resize:none;
}

.button-group{
  margin-top:20px;
  display:flex;
  justify-content:flex-end;
  gap:10px;
}

button{
  padding:10px 20px;
  border-radius:25px;
  border:none;
  cursor:pointer;
  font-size:14px;
}

button[type="submit"]{
  background:linear-gradient(135deg,#ff512f,#f09819);
  color:white;
}

.cancel-btn{
  background:#e5e7eb;
}

.cancel-btn:hover{
  background:#d1d5db;
}

</style>