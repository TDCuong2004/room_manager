<template>
  <div class="modal-overlay" @click.self="close">

    <div class="modal-card">
      <h2>➕ Thêm dịch vụ</h2>

      <form @submit.prevent="handleSubmit">

        <!-- SERVICE NAME -->
        <div class="form-group">
          <label>Tên dịch vụ</label>
          <input 
            v-model="service.serviceName" 
            placeholder="VD: Điện, Nước, Internet"
          />
        </div>

        <!-- UNIT -->
        <div class="form-group">
          <label>Đơn vị</label>
          <input 
            v-model="service.unit" 
            placeholder="kWh, m³, người, tháng..."
          />
        </div>

        <!-- TYPE -->
        <div class="form-group">
          <label>Kiểu tính</label>

          <select v-model="service.calculationType">

            <option value="BY_METER">🔢 Theo số (điện, nước)</option>
            <option value="BY_PERSON">👤 Theo người</option>
            <option value="FIXED">💰 Cố định</option>

          </select>
        </div>

        <!-- BUTTON -->
        <div class="button-group">

          <button type="submit" class="btn primary">
            Thêm
          </button>

          <button
            type="button"
            class="btn cancel"
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
  serviceName:"",
  unit:"",
  calculationType:"BY_METER"
})

const close = () => {
  emit("close")
}

const handleSubmit = async () => {

  if(!service.value.serviceName){
    alert("Nhập tên dịch vụ")
    return
  }

  try{

    const token = localStorage.getItem("token")

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
  background:rgba(0,0,0,0.5);
  backdrop-filter:blur(4px);
  display:flex;
  justify-content:center;
  align-items:center;
  z-index:9999;
}

.modal-card{
  width:420px;
  background:white;
  padding:28px;
  border-radius:18px;
  box-shadow:0 25px 60px rgba(0,0,0,0.25);
  animation:fadeIn .25s ease;
}

@keyframes fadeIn{
  from{
    transform:translateY(20px);
    opacity:0;
  }
  to{
    transform:translateY(0);
    opacity:1;
  }
}

h2{
  margin-bottom:20px;
  font-size:20px;
  color:#111827;
}

.form-group{
  margin-bottom:15px;
}

.form-group label{
  display:block;
  font-size:13px;
  margin-bottom:5px;
  color:#6b7280;
}

input,select{
  width:100%;
  padding:10px;
  border-radius:10px;
  border:1px solid #e5e7eb;
  background:#f9fafb;
  font-size:14px;
}

input:focus,select:focus{
  outline:none;
  border-color:#3b82f6;
  background:white;
}

.button-group{
  margin-top:20px;
  display:flex;
  justify-content:flex-end;
  gap:10px;
}

.btn{
  padding:9px 18px;
  border-radius:25px;
  border:none;
  cursor:pointer;
  font-size:14px;
}

.btn.primary{
  background:linear-gradient(135deg,#3b82f6,#2563eb);
  color:white;
}

.btn.cancel{
  background:#e5e7eb;
}

.btn.cancel:hover{
  background:#d1d5db;
}

</style>
