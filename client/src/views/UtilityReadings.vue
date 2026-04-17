<template>
  <div class="utility-page">

    <!-- HEADER -->
    <div class="page-header">
      <div>
        <h2>⚡ Utility Readings</h2>
        <p>Nhập và quản lý chỉ số dịch vụ theo phòng</p>
      </div>

      <div class="actions">
        <button class="btn secondary">Export</button>
        <button class="btn primary" @click="saveAll">Save All</button>
      </div>
    </div>

    <!-- FILTER -->
    <div class="filters">

      <!-- 🔥 SERVICES -->
      <div class="tabs">
        <button
          v-for="s in services"
          :key="s.id"
          :class="['tab', selectedServiceId === s.id ? 'active' : '']"
          @click="selectService(s)"
        >
          {{ s.serviceName }}
        </button>
      </div>

      <!-- 🔥 SELECTORS -->
      <div class="selectors">

        <!-- CHỌN TÒA -->
        <select v-model="selectedBuildingId" @change="onBuildingChange">
          <option value="">Chọn tòa</option>
          <option v-for="b in buildings" :key="b.id" :value="b.id">
            {{ b.buildingName }}
          </option>
        </select>

        <!-- CHỌN THÁNG -->
        <input type="month" v-model="selectedMonth" @change="loadData"/>

      </div>
    </div>

    <!-- TABLE -->
    <div class="table-wrapper">
      <table>
        <thead>
          <tr>
            <th>Phòng</th>
            <th>Người thuê</th>
            <th>Chỉ số cũ</th>
            <th>Chỉ số mới</th>
            <th>Tiêu thụ</th>
            <th>Trạng thái</th>
          </tr>
        </thead>

        <tbody>
          <tr 
            v-for="row in readings" 
            :key="row.roomId"
            class="border-b hover:bg-gray-50 transition"
          >
            <td class="p-3 font-semibold text-gray-700">
              {{ row.roomName }}
            </td>

            <td class="p-3 text-gray-500">
              {{ row.customerName }}
            </td>

            <td class="p-3">
              {{ format(row.oldValue) }}
            </td>

            <td class="p-3">
              <input
                v-model.number="row.newValue"
                type="number"
                placeholder="Nhập"
                :disabled="row.paid"
                :class="[
                  'w-24 px-2 py-1 rounded-lg border text-sm outline-none transition',
                  row.paid
                    ? 'bg-gray-100 text-gray-400 cursor-not-allowed'
                    : 'border-gray-300 focus:ring-2 focus:ring-rose-400'
                ]"
              />
            </td>

            <td class="p-3 font-bold text-gray-800">
              {{ calcUsage(row) }}
            </td>

            <td class="p-3">
              <!-- Ưu tiên PAID -->
              <span
                v-if="row.paid"
                class="px-3 py-1 text-xs font-bold rounded-full bg-emerald-100 text-emerald-600"
              >
                Đã thanh toán
              </span>

              <span
                v-else
                :class="[
                  'px-3 py-1 text-xs font-bold rounded-full',
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
      selectedServiceId: "",
      selectedMonth: "",

      buildings: [],
      services: [],
      readings: []
    }
  },

  mounted() {
    const now = new Date()
    this.selectedMonth = now.toISOString().slice(0, 7)

    this.loadBuildings()
  },

  methods: {

    // LOAD BUILDINGS
    async loadBuildings() {
      try {
        const res = await api.get("/buildings")
        this.buildings = res.data
      } catch (e) {
        console.error(e)
      }
    },

    // CHANGE BUILDING
    async onBuildingChange() {
      this.selectedServiceId = ""
      this.services = []
      this.readings = []

      if (!this.selectedBuildingId) return

      try {
        const res = await api.get(`/building-services/${this.selectedBuildingId}/meter`)

        this.services = res.data

        // ✅ FIX QUAN TRỌNG
        if (this.services.length > 0) {
          this.selectedServiceId = this.services[0].serviceId
          console.log("Auto serviceId:", this.selectedServiceId)
          this.loadData()
        }

      } catch (e) {
        console.error(e)
      }
    },

    // SELECT SERVICE
    selectService(s) {
      this.selectedServiceId = s.serviceId   // ✅ FIX
      console.log("Selected serviceId:", this.selectedServiceId)
      this.loadData()
    },

    // LOAD DATA
    async loadData() {
      if (!this.selectedServiceId || !this.selectedMonth) return

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

        this.readings = res.data

      } catch (e) {
        console.error("LOAD DATA ERROR:", e)
      }
    },

    // FORMAT
    format(val) {
      return val?.toLocaleString()
    },

    // USAGE
    calcUsage(row) {
      if (row.newValue == null || row.oldValue == null) return "--"
      return (row.newValue - row.oldValue).toFixed(2)
    },

    // STATUS
    getStatus(row) {
      if (!row.newValue && row.newValue !== 0) return "missing"
      if (row.newValue < row.oldValue) return "error"
      return "complete"
    },

    // SAVE ALL
    async saveAll() {
      try {
        const payload = this.readings
          .filter(r => !r.paid) // 🔥 CHẶN PAID
          .map(r => ({
            roomId: r.roomId,
            serviceId: this.selectedServiceId,
            month: this.selectedMonth,
            newValue: r.newValue
          }))

        if (payload.length === 0) {
          alert("Không có dữ liệu cần lưu")
          return
        }

        await api.post("/meter-readings/bulk", payload)

        alert("Lưu thành công!")

      } catch (e) {
        console.error(e)
        alert("Lỗi khi lưu")
      }
    }
  }
}
</script>

<style scoped>
.utility-page {
  padding: 20px;
}

/* HEADER */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header p {
  color: #777;
  font-size: 14px;
}

/* BUTTON */
.btn {
  padding: 8px 14px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  margin-left: 10px;
}

.primary {
  background: #ff4d6d;
  color: white;
}

.secondary {
  background: #eee;
}

/* FILTER */
.filters {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.tabs {
  display: flex;
  gap: 10px;
}

.tab {
  padding: 6px 12px;
  border-radius: 20px;
  background: #f1f1f1;
  cursor: pointer;
}

.tab.active {
  background: #ff4d6d;
  color: white;
}

.selectors select,
.selectors input {
  margin-left: 10px;
  padding: 6px;
  border-radius: 6px;
  border: 1px solid #ccc;
}

/* TABLE */
.table-wrapper {
  background: white;
  border-radius: 12px;
  padding: 10px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  text-align: left;
  padding: 10px;
  background: #f9f9f9;
}

td {
  padding: 10px;
  border-top: 1px solid #eee;
}

/* INPUT */
.input {
  width: 100px;
  padding: 5px;
  border-radius: 6px;
  border: 1px solid #ccc;
}

/* STATUS */
.status {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
}

.complete {
  background: #d4edda;
  color: #28a745;
}

.missing {
  background: #ffe5e5;
  color: #dc3545;
}

.error {
  background: #fff3cd;
  color: #ff9800;
}

.usage {
  font-weight: bold;
}
</style>