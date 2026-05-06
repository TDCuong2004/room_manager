
<script>
import api from "@/api"
import { useToast } from "vue-toastification"

export default {
  props: ["room"],

  data() {
    return {
      services: [],
      month: "",
      meterValues: {},
      personValues: {},
      toast: null,
      loading: false
    }
  },

  async mounted() {
    this.toast = useToast()

    // auto chọn tháng hiện tại
    const now = new Date()
    this.month = now.toISOString().slice(0, 7)

    try {
      const res = await api.get(
        `/building-services/${this.room.building.id}`
      )
      this.services = res.data
    } catch (err) {
      this.toast.error(err.response?.data?.message || "Lỗi server")
    }
  },

  methods: {
    async save() {
      if (!this.month) {
        this.toast.warning("Chọn tháng trước")
        return
      }

      const payload = []

      this.services.forEach(s => {
        const type = s.calculationType

        if (type === "BY_METER") {
          if (!this.meterValues[s.serviceId]) return

          payload.push({
            roomId: this.room.id,
            serviceId: s.serviceId,
            month: this.month,
            newValue: this.meterValues[s.serviceId]
          })
        }

        if (type === "BY_PERSON") {
          payload.push({
            roomId: this.room.id,
            serviceId: s.serviceId,
            month: this.month,
            quantity: this.personValues[s.serviceId] || 0
          })
        }

        if (type === "FIXED") {
          payload.push({
            roomId: this.room.id,
            serviceId: s.serviceId,
            month: this.month,
            quantity: 1
          })
        }
      })

      if (!payload.length) {
        this.toast.warning("Chưa nhập dữ liệu")
        return
      }

      try {
        this.loading = true

        await api.post("/meter-readings/bulk", payload)

        this.toast.success("Lưu thành công 🎉")
        this.$emit("saved")

      } catch (err) {
        this.toast.error(err.response?.data?.message || "Lỗi server")
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<template>
  <!-- OVERLAY -->
  <div class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">

    <!-- MODAL -->
    <div class="bg-white w-full max-w-md rounded-2xl shadow-xl p-6 space-y-4">

      <!-- HEADER -->
      <div>
        <h2 class="text-xl font-bold text-gray-800">
          📊 Nhập dịch vụ
        </h2>
        <p class="text-sm text-gray-500 mt-1">
          {{ room.roomName }}
        </p>
      </div>

      <!-- MONTH -->
      <div>
        <label class="text-sm font-medium text-gray-700">
          Tháng
        </label>
        <input
          type="month"
          v-model="month"
          class="w-full mt-1 px-3 py-2 border rounded-lg focus:ring-2 focus:ring-rose-400 outline-none"
        />
      </div>

      <!-- SERVICES -->
      <div v-if="services.length" class="space-y-3 max-h-[300px] overflow-y-auto pr-1">

        <div
          v-for="s in services"
          :key="s.id"
          class="flex flex-col gap-1"
        >
          <label class="text-sm font-medium text-gray-700">
            {{ s.serviceName }}
          </label>

          <!-- BY_METER -->
          <input
            v-if="s.calculationType === 'BY_METER'"
            type="number"
            placeholder="Nhập số mới"
            v-model="meterValues[s.serviceId]"
            class="px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-400 outline-none"
          />

          <!-- BY_PERSON -->
          <input
            v-else-if="s.calculationType === 'BY_PERSON'"
            type="number"
            placeholder="Số lượng"
            v-model="personValues[s.serviceId]"
            class="px-3 py-2 border rounded-lg focus:ring-2 focus:ring-green-400 outline-none"
          />

          <!-- FIXED -->
          <div
            v-else
            class="px-3 py-2 bg-gray-100 text-gray-500 rounded-lg text-sm"
          >
            Phí cố định
          </div>
        </div>

      </div>

      <!-- EMPTY -->
      <p v-else class="text-center text-gray-400 text-sm">
        Tòa này chưa có dịch vụ
      </p>

      <!-- ACTION -->
      <div class="flex justify-end gap-2 pt-2">

        <button
          @click="$emit('close')"
          class="px-4 py-2 rounded-lg bg-gray-200 hover:bg-gray-300 transition"
        >
          Hủy
        </button>

        <button
          @click="save"
          :disabled="loading"
          class="px-4 py-2 rounded-lg bg-rose-500 text-white hover:bg-rose-600 transition disabled:opacity-50"
        >
          <span v-if="loading">Đang lưu...</span>
          <span v-else>Lưu</span>
        </button>

      </div>

    </div>

  </div>
</template>
