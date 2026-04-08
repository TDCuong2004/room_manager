<template>
  <div class="modal-overlay" @click.self="close">

    <div class="modal">

      <h3>➕ Thêm dịch vụ</h3>

      <!-- SERVICE -->
      <label>Dịch vụ</label>

      <select v-model="form.serviceId">
        <option disabled value="">Chọn dịch vụ</option>

        <option
          v-for="s in enhancedServices"
          :key="s.id"
          :value="s.id"
          :disabled="s.disabled"
        >
          {{ s.label }}
        </option>
      </select>

      <!-- PRICE -->
      <label>Giá</label>

      <input
        type="number"
        v-model="form.price"
        placeholder="Nhập giá"
      />

      <!-- ACTION -->
      <div class="actions">

        <button class="save-btn" @click="save">
          Lưu
        </button>

        <button class="cancel-btn" @click="close">
          Hủy
        </button>

      </div>

    </div>

  </div>
</template>

<script>
import api from "@/api"

export default {

  props:{
    buildingId:Number,
    services:Array,
    buildingServices:Array
  },

  data(){
    return{
      form:{
        serviceId:"",
        price:""
      }
    }
  },

  computed:{

    // 🔥 nâng cấp: mark service đã có
    enhancedServices(){

      const usedIds = this.buildingServices.map(
        s => s.serviceId || s.service?.id
      )

      return this.services.map(s => ({
        ...s,
        disabled: usedIds.includes(s.id),
        label: `${s.serviceName} (${s.unit || '-'})` +
               (usedIds.includes(s.id) ? " - đã có" : "")
      }))

    }

  },

  methods:{

    close(){
      this.$emit("close")
    },

    async save(){

      if(!this.form.serviceId){
        alert("Chọn dịch vụ")
        return
      }

      if(!this.form.price){
        alert("Nhập giá")
        return
      }

      try{

        await api.post("/building-services",{
          building:{ id:this.buildingId },
          service:{ id:this.form.serviceId },
          price:this.form.price,
          isActive:true
        })

        alert("Thêm thành công")

        this.$emit("saved")

      }catch(err){
        console.error(err)
        alert("Lỗi khi thêm")
      }

    }

  }

}
</script>

<style scoped>

/* 🔥 FIX LỚN NHẤT: đảm bảo nổi trên header */
.modal-overlay{
  position:fixed;
  inset:0;
  background:rgba(0,0,0,0.45);
  backdrop-filter:blur(3px);
  display:flex;
  justify-content:center;
  align-items:center;
  z-index:9999; /* 🔥 FIX */
}

/* MODAL */
.modal{
  width:360px;
  background:white;
  padding:25px;
  border-radius:16px;
  box-shadow:0 20px 50px rgba(0,0,0,0.25);
  animation:fadeIn .25s ease;
}

/* ANIMATION */
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

h3{
  margin-bottom:15px;
  font-size:18px;
  color:#111827;
}

/* INPUT */
label{
  font-size:13px;
  color:#6b7280;
  display:block;
  margin-bottom:5px;
}

select,input{
  width:100%;
  padding:10px;
  margin-bottom:15px;
  border-radius:10px;
  border:1px solid #e5e7eb;
  background:#f9fafb;
  font-size:14px;
}

select:focus,input:focus{
  outline:none;
  border-color:#3b82f6;
  background:white;
}

/* BUTTON */
.actions{
  display:flex;
  justify-content:flex-end;
  gap:10px;
}

.save-btn{
  background:linear-gradient(135deg,#10b981,#059669);
  color:white;
  border:none;
  padding:8px 16px;
  border-radius:20px;
  cursor:pointer;
}

.cancel-btn{
  background:#e5e7eb;
  border:none;
  padding:8px 16px;
  border-radius:20px;
  cursor:pointer;
}

.cancel-btn:hover{
  background:#d1d5db;
}

</style>