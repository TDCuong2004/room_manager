<template>
  <div class="p-5">
    <!-- TOAST -->
    <div
      v-if="toast.show"
      :class="[
        'fixed top-5 right-5 px-5 py-3 rounded-xl shadow-lg text-white z-50 transition',
        toast.type === 'success'
          ? 'bg-emerald-500'
          : 'bg-red-500'
      ]"
    >
      {{ toast.message }}
    </div>
    <!-- HEADER -->
    <div class="flex justify-between items-center mb-5">
      <div>
        <h2 class="text-2xl font-bold text-gray-800">
          ⚡ Utility Readings
        </h2>

        <p class="text-sm text-gray-500">
          Nhập và quản lý chỉ số dịch vụ theo phòng
        </p>
      </div>

      <div class="flex gap-3">
        <button
          class="px-4 py-2 rounded-xl bg-gray-200 hover:bg-gray-300 transition"
        >
          Export
        </button>

        <button
          @click="saveAll"
          class="px-4 py-2 rounded-xl bg-rose-500 hover:bg-rose-600 text-white transition"
        >
          Save All
        </button>
      </div>
    </div>

    <!-- FILTER -->
    <div class="flex justify-between items-center mb-5">

      <!-- SERVICES -->
      <div class="flex gap-3">

        <button
          v-for="s in services"
          :key="s.serviceId"
          @click="selectService(s)"
          :class="[
            'px-4 py-2 rounded-full text-sm font-semibold transition',
            selectedServiceId === Number(s.serviceId)
              ? 'bg-rose-500 text-white shadow'
              : 'bg-gray-100 hover:bg-gray-200 text-gray-700'
          ]"
        >
          {{ s.serviceName }}
        </button>

      </div>

      <!-- SELECTORS -->
      <div class="flex gap-3">

        <!-- BUILDING -->
        <select
          v-model="selectedBuildingId"
          @change="onBuildingChange"
          class="border border-gray-300 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-rose-400"
        >
          <option value="">Chọn tòa</option>

          <option
            v-for="b in buildings"
            :key="b.id"
            :value="b.id"
          >
            {{ b.buildingName }}
          </option>
        </select>

        <!-- MONTH -->
        <input
          type="month"
          v-model="selectedMonth"
          @change="loadData"
          class="border border-gray-300 rounded-lg px-3 py-2 outline-none focus:ring-2 focus:ring-rose-400"
        />

      </div>
    </div>

    <!-- TABLE -->
    <div class="bg-white rounded-2xl shadow overflow-hidden">

      <table class="w-full border-collapse">

        <thead class="bg-gray-50">
          <tr>
            <th class="text-left p-4">Phòng</th>
            <th class="text-left p-4">Người thuê</th>
            <th class="text-left p-4">Chỉ số cũ</th>
            <th class="text-left p-4">Chỉ số mới</th>
            <th class="text-left p-4">Tiêu thụ</th>
            <th class="text-left p-4">Trạng thái</th>
          </tr>
        </thead>

        <!-- FORCE RE-RENDER -->
        <tbody :key="selectedServiceId">

          <tr
            v-for="row in readings"
            :key="`${row.roomId}-${selectedServiceId}`"
            class="border-t hover:bg-gray-50 transition"
          >

            <!-- ROOM -->
            <td class="p-4 font-semibold text-gray-700">
              {{ row.roomName }}
            </td>

            <!-- CUSTOMER -->
            <td class="p-4 text-gray-500">
              {{ row.customerName }}
            </td>

            <!-- OLD -->
            <td class="p-4">
              {{ format(row.oldValue) }}
            </td>

            <!-- NEW -->
            <td class="p-4">

              <input
                v-model.number="row.newValue"
                type="number"
                placeholder="Nhập"
                :disabled="row.paid"
                :class="[
                  'w-28 px-3 py-2 rounded-lg border text-sm outline-none transition',
                  row.paid
                    ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                    : 'border-gray-300 focus:ring-2 focus:ring-rose-400'
                ]"
              />

            </td>

            <!-- USAGE -->
            <td class="p-4 font-bold text-gray-800">
              {{ calcUsage(row) }}
            </td>

            <!-- STATUS -->
            <td class="p-4">

              <!-- PAID -->
              <span
                v-if="row.paid"
                class="px-3 py-1 rounded-full text-xs font-bold bg-emerald-100 text-emerald-600"
              >
                Đã thanh toán
              </span>

              <!-- NORMAL -->
              <span
                v-else
                :class="[
                  'px-3 py-1 rounded-full text-xs font-bold',
                  getStatus(row) === 'complete'
                    ? 'bg-green-100 text-green-600'
                    : getStatus(row) === 'missing'
                    ? 'bg-red-100 text-red-500'
                    : 'bg-yellow-100 text-yellow-600'
                ]"
              >
                {{
                  getStatus(row) === 'complete'
                    ? 'Đã nhập'
                    : getStatus(row) === 'missing'
                    ? 'Chưa nhập'
                    : 'Sai dữ liệu'
                }}
              </span>

            </td>

          </tr>

          <!-- EMPTY -->
          <tr v-if="readings.length === 0">
            <td
              colspan="6"
              class="text-center py-10 text-gray-400"
            >
              Không có dữ liệu
            </td>
          </tr>

        </tbody>

      </table>

    </div>

  </div>
</template>

<script>
import api from "@/api"

export default {

  data() {
    return {

      selectedBuildingId: "",
      selectedServiceId: null,
      selectedMonth: "",

      buildings: [],
      services: [],
      readings: [],

      // ✅ THÊM Ở ĐÂY
      toast: {
        show: false,
        message: "",
        type: "success"
      }

    }
  },
  mounted() {

    const now = new Date()

    this.selectedMonth = now.toISOString().slice(0, 7)

    this.loadBuildings()
  },

  methods: {
    showToast(message, type = "success") {

      this.toast.message = message
      this.toast.type = type
      this.toast.show = true

      setTimeout(() => {
        this.toast.show = false
      }, 3000)
    },
    // ================= BUILDINGS =================
    async loadBuildings() {

      try {

        const res = await api.get("/buildings")

        this.buildings = res.data

      } catch (e) {

        console.error(e)
      }
    },

    // ================= CHANGE BUILDING =================
    async onBuildingChange() {

      this.selectedServiceId = null
      this.services = []
      this.readings = []

      if (!this.selectedBuildingId) return

      try {

        const res = await api.get(
          `/building-services/${this.selectedBuildingId}/meter`
        )

        this.services = res.data

        console.log("SERVICES:", this.services)

        // AUTO SELECT FIRST SERVICE
        if (this.services.length > 0) {

          this.selectedServiceId = Number(
            this.services[0].serviceId
          )

          await this.loadData()
        }

      } catch (e) {

        console.error(e)
      }
    },

    // ================= SELECT SERVICE =================
    async selectService(service) {

      this.selectedServiceId = Number(service.serviceId)

      console.log("SELECTED:", this.selectedServiceId)

      this.readings = []

      await this.loadData()
    },

    // ================= LOAD DATA =================
    async loadData() {

      if (
        !this.selectedBuildingId ||
        !this.selectedServiceId ||
        !this.selectedMonth
      ) {
        return
      }

      try {

        console.log("CALL API:", {
          month: this.selectedMonth,
          serviceId: this.selectedServiceId,
          buildingId: this.selectedBuildingId
        })

        const res = await api.get("/meter-readings", {
          params: {
            month: this.selectedMonth,
            serviceId: this.selectedServiceId,
            buildingId: this.selectedBuildingId
          }
        })

        console.log("READINGS:", res.data)

        this.readings = [...res.data]

      } catch (e) {

        console.error("LOAD DATA ERROR:", e)
      }
    },

    // ================= FORMAT =================
    format(val) {

      if (val == null) return "--"

      return Number(val).toLocaleString()
    },

    // ================= USAGE =================
    calcUsage(row) {

      if (
        row.newValue == null ||
        row.oldValue == null
      ) {
        return "--"
      }

      return (
        row.newValue - row.oldValue
      ).toFixed(2)
    },

    // ================= STATUS =================
    getStatus(row) {

      if (row.newValue == null) {
        return "missing"
      }

      if (row.newValue < row.oldValue) {
        return "error"
      }

      return "complete"
    },

    // ================= SAVE =================
    async saveAll() {

      try {

        // ❌ CHECK SAI DỮ LIỆU
        const invalid = this.readings.find(r =>
          !r.paid &&
          r.newValue != null &&
          r.newValue < r.oldValue
        )

        if (invalid) {

          this.showToast(
            `Phòng ${invalid.roomName} có chỉ số mới nhỏ hơn chỉ số cũ`,
            "error"
          )

          return
        }

        const payload = this.readings

          .filter(r =>
            !r.paid &&
            r.newValue != null
          )

          .map(r => ({
            roomId: r.roomId,
            serviceId: this.selectedServiceId,
            month: this.selectedMonth,
            newValue: r.newValue
          }))

        if (payload.length === 0) {

          this.showToast(
            "Không có dữ liệu cần lưu",
            "error"
          )

          return
        }

        await api.post(
          "/meter-readings/bulk",
          payload
        )

        this.showToast("Lưu thành công!")

        await this.loadData()

      } catch (e) {

        console.error(e)

        this.showToast(
          "Lỗi khi lưu dữ liệu",
          "error"
        )
      }
    }

  }

}
</script>