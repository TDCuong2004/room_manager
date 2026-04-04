<template>
  <div class="modal-overlay" @click.self="close">

    <div class="modal-card">
      <h2>Thêm Dịch Vụ</h2>

      <form @submit.prevent="handleSubmit">

        <div class="form-group">
          <label>Mã dịch vụ</label>
          <input v-model="service.serviceCode" required />
        </div>

        <div class="form-group">
          <label>Tên dịch vụ</label>
          <input v-model="service.serviceName" required />
        </div>

        <div class="form-group">
          <label>Đơn vị</label>
          <input v-model="service.unit" placeholder="kWh, m3, người, tháng..." />
        </div>

        <div class="form-group">
          <label>Kiểu tính</label>

          <select v-model="service.calculationType">

            <option value="BY_METER">
              BY_METER (tính theo số lượng)
            </option>

            <option value="BY_PERSON">
              BY_PERSON (tính theo người)
            </option>

            <option value="FIXED">
              FIXED (cố định)
            </option>

          </select>

        </div>

        <div class="button-group">

          <button type="submit">
            Thêm
          </button>

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

const service = ref({
  serviceCode:"",
  serviceName:"",
  unit:"",
  calculationType:""
})

const close = () => {
  emit("close")
}

const handleSubmit = async () => {

  try{

    const token = localStorage.getItem("token")

    if(!token){
      alert("Bạn chưa đăng nhập!")
      return
    }

    await axios.post(
      "http://localhost:3000/api/services",
      service.value,
      {
        headers:{
          Authorization:`Bearer ${token}`
        }
      }
    )

    alert("Thêm dịch vụ thành công")

    emit("reload")
    emit("close")

  }catch(err){

    console.error(err)
    alert("Lỗi khi thêm dịch vụ")

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

input,textarea,select{
  width:100%;
  padding:12px;
  border-radius:10px;
  border:1px solid #e5e7eb;
  font-size:14px;
  background:#f9fafb;
}

input:focus,textarea:focus,select:focus{
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