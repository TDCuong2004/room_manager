<template>
  <div 
    class="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-[9999]"
    @click.self="close"
  >
    <div class="w-[380px] bg-white rounded-2xl shadow-2xl p-6 animate-[fadeIn_.25s_ease]">
      
      <!-- TITLE -->
      <h3 class="text-lg font-bold text-gray-800 mb-5 flex items-center gap-2">
        ➕ Thêm dịch vụ
      </h3>

      <!-- SERVICE -->
      <label class="text-xs text-gray-500 font-semibold">Dịch vụ</label>

      <select 
        v-model="form.serviceId"
        class="w-full mt-1 mb-4 px-3 py-2 rounded-xl border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition"
      >
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
      <label class="text-xs text-gray-500 font-semibold">Giá</label>

      <input
        type="number"
        v-model="form.price"
        placeholder="Nhập giá"
        class="w-full mt-1 mb-5 px-3 py-2 rounded-xl border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-green-500 focus:bg-white transition"
      />

      <!-- ACTION -->
      <div class="flex justify-end gap-2">

        <button 
          @click="close"
          class="px-4 py-2 rounded-xl bg-gray-100 text-gray-600 text-sm font-semibold hover:bg-gray-200 transition"
        >
          Hủy
        </button>

        <button 
          @click="save"
          class="px-5 py-2 rounded-xl bg-gradient-to-r from-emerald-500 to-green-600 text-white text-sm font-bold shadow hover:scale-105 active:scale-95 transition"
        >
          Lưu
        </button>

      </div>

    </div>
  </div>
</template>

<script>
import api from "@/api"
import { useToast } from "vue-toastification"

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
      const toast = useToast()

      if(!this.form.serviceId){
        toast.warning("⚠️ Vui lòng chọn dịch vụ")
        return
      }

      if(!this.form.price){
        toast.warning("⚠️ Vui lòng nhập giá")
        return
      }

      try{
        await api.post("/building-services",{
          building:{ id:this.buildingId },
          service:{ id:this.form.serviceId },
          price:this.form.price,
          isActive:true
        })

        toast.success("✅ Thêm dịch vụ thành công")

        this.$emit("saved")

      }catch(err){
        console.error(err)
        toast.error("❌ Lỗi khi thêm dịch vụ")
      }
    }

  }

}
</script>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>