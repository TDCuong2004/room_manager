<template>
  <div 
    class="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-[9999]"
    @click.self="close"
  >
    <div class="w-[420px] bg-white rounded-2xl shadow-2xl p-6 animate-[fadeIn_.25s_ease]">

      <!-- TITLE -->
      <h2 class="text-lg font-bold text-gray-800 mb-5 flex items-center gap-2">
        ➕ Thêm dịch vụ
      </h2>

      <form @submit.prevent="handleSubmit" class="space-y-4">

        <!-- SERVICE NAME -->
        <div>
          <label class="text-xs text-gray-500 font-semibold">Tên dịch vụ</label>
          <input 
            v-model="service.serviceName"
            placeholder="VD: Điện, Nước, Internet"
            class="w-full mt-1 px-3 py-2 rounded-xl border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition"
          />
        </div>

        <!-- UNIT -->
        <div>
          <label class="text-xs text-gray-500 font-semibold">Đơn vị</label>
          <input 
            v-model="service.unit"
            placeholder="kWh, m³, người, tháng..."
            class="w-full mt-1 px-3 py-2 rounded-xl border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-green-500 focus:bg-white transition"
          />
        </div>

        <!-- TYPE -->
        <div>
          <label class="text-xs text-gray-500 font-semibold">Kiểu tính</label>

          <select 
            v-model="service.calculationType"
            class="w-full mt-1 px-3 py-2 rounded-xl border border-gray-200 bg-gray-50 text-sm focus:outline-none focus:ring-2 focus:ring-purple-500 focus:bg-white transition"
          >
            <option value="BY_METER">🔢 Theo số (điện, nước)</option>
            <option value="BY_PERSON">👤 Theo người</option>
            <option value="FIXED">💰 Cố định</option>
          </select>
        </div>

        <!-- BUTTON -->
        <div class="flex justify-end gap-2 pt-3">

          <button
            type="button"
            @click="close"
            class="px-4 py-2 rounded-xl bg-gray-100 text-gray-600 text-sm font-semibold hover:bg-gray-200 transition"
          >
            Hủy
          </button>

          <button
            type="submit"
            class="px-5 py-2 rounded-xl bg-gradient-to-r from-blue-500 to-indigo-600 text-white text-sm font-bold shadow hover:scale-105 active:scale-95 transition"
          >
            Thêm
          </button>

        </div>

      </form>

    </div>
  </div>
</template>

<script setup>
import { ref } from "vue"
import axios from "axios"
import { useToast } from "vue-toastification"

const toast = useToast()
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
    toast.warning("⚠️ Nhập tên dịch vụ")
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

    toast.success("✅ Thêm dịch vụ thành công")

    emit("reload")
    emit("close")

  }catch(err){
    console.error(err)
    toast.error("❌ Lỗi khi thêm dịch vụ")
  }
}
</script>

<style scoped>

</style>
