<template>
  <div class="invoice-page">

    <!-- HEADER -->
    <div class="header">
      <h2>💰 Quản lý hóa đơn</h2>

      <div class="actions">

        <!-- CHỌN TÒA -->
        <select v-model="selectedBuilding" @change="fetchInvoices">
          <option value="">-- Chọn tòa --</option>
          <option v-for="b in buildings" :key="b.id" :value="b.id">
            {{ b.buildingName }}
          </option>
        </select>

        <!-- CHỌN THÁNG -->
        <input type="month" v-model="selectedMonth" @change="fetchInvoices">

        <!-- BUTTON -->
        <button 
          class="gen-btn"
          :disabled="!selectedBuilding || !selectedMonth"
          @click="generateInvoices"
        >
          ⚡ Tính tiền
        </button>

      </div>
    </div>

    <!-- TABLE -->
    <table class="invoice-table">
      <thead>
        <tr>
          <th>Phòng</th>
          <th>Tháng</th>
          <th>Tổng tiền</th>
          <th>Trạng thái</th>
          <th>Chi tiết</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="inv in invoices" :key="inv.id">
          <td>{{ inv.roomName }}</td>
          <td>{{ inv.month }}</td>
          <td>{{ formatMoney(inv.totalAmount) }} đ</td>
          <td>
            <span :class="inv.status === 'PAID' ? 'paid' : 'unpaid'">
              {{ inv.status }}
            </span>
          </td>
          <td>
            <button @click="viewDetail(inv)">
              Xem
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- EMPTY -->
    <div v-if="!invoices.length" class="empty-state">
      Chưa có hóa đơn.
    </div>

    <!-- MODAL DETAIL -->
    <div v-if="showDetail" class="modal-overlay" @click.self="closeDetail">
      <div class="modal-card">
        <h3>Chi tiết hóa đơn</h3>

        <p><b>Phòng:</b> {{ selectedInvoice.roomName }}</p>
        <p><b>Tháng:</b> {{ selectedInvoice.month }}</p>

        <table class="detail-table">
          <tr>
            <th>Dịch vụ</th>
            <th>Số lượng</th>
            <th>Đơn giá</th>
            <th>Thành tiền</th>
          </tr>

          <tr v-for="d in details" :key="d.id">
            <td>{{ d.serviceName }}</td>
            <td>{{ d.quantity }}</td>
            <td>{{ formatMoney(d.unitPrice) }}</td>
            <td>{{ formatMoney(d.amount) }}</td>
          </tr>
        </table>

        <h4 class="total">
          Tổng: {{ formatMoney(selectedInvoice.totalAmount) }} đ
        </h4>

        <button @click="closeDetail">Đóng</button>
      </div>
    </div>

  </div>
</template>

<script>
import axios from "axios"

const api = axios.create({
  baseURL: "http://localhost:3000/api"
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem("token")
  if (token) config.headers.Authorization = "Bearer " + token
  return config
})

export default {

  data() {
    return {
      invoices: [],
      selectedMonth: "",
      selectedBuilding: "",
      buildings: [],

      showDetail: false,
      selectedInvoice: {},
      details: []
    }
  },

  mounted() {
    this.fetchBuildings()
  },

  methods: {

    async fetchBuildings(){
      try{
        const res = await api.get("/buildings")
        this.buildings = res.data
      }catch(err){
        console.error(err)
      }
    },

    async fetchInvoices() {

      if(!this.selectedBuilding || !this.selectedMonth){
        this.invoices = []
        return
      }

      try {
        const res = await api.get("/invoices", {
          params: {
            buildingId: this.selectedBuilding,
            month: this.selectedMonth
          }
        })
        this.invoices = res.data
      } catch (err) {
        console.error(err)
      }
    },

    async generateInvoices() {

      if (!this.selectedMonth || !this.selectedBuilding) {
        alert("Chọn tòa + tháng")
        return
      }

      try {

        await api.post("/invoices/generate", null, {
          params: {
            buildingId: this.selectedBuilding,
            month: this.selectedMonth
          }
        })

        alert("Tính tiền thành công")

        this.fetchInvoices()

      } catch (err) {
        console.error(err)
        alert("Lỗi khi tính tiền")
      }
    },

    async viewDetail(inv) {

      this.selectedInvoice = inv
      this.showDetail = true

      try {
        const res = await api.get(`/invoice-details/${inv.id}`)
        this.details = res.data
      } catch (err) {
        console.error(err)
      }

    },

    closeDetail() {
      this.showDetail = false
    },

    formatMoney(value) {
      return new Intl.NumberFormat("vi-VN").format(value)
    }

  }

}
</script>

<style scoped>

.invoice-page {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.actions {
  display: flex;
  gap: 10px;
}

select, input {
  padding: 6px;
  border-radius: 6px;
  border: 1px solid #ccc;
}

.gen-btn {
  background: #ff5a5f;
  color: white;
  border: none;
  padding: 8px 14px;
  border-radius: 6px;
  cursor: pointer;
}

.gen-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.invoice-table {
  width: 100%;
  margin-top: 20px;
  border-collapse: collapse;
}

.invoice-table th, .invoice-table td {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: center;
}

.paid {
  color: green;
  font-weight: bold;
}

.unpaid {
  color: red;
  font-weight: bold;
}

.empty-state{
  text-align:center;
  margin-top:30px;
  color:#888;
}

/* MODAL */
.modal-overlay {
  position: fixed;
  top:0;
  left:0;
  right:0;
  bottom:0;
  background: rgba(0,0,0,0.4);
  display:flex;
  justify-content:center;
  align-items:center;
}

.modal-card {
  background:white;
  padding:20px;
  border-radius:10px;
  width:500px;
}

.detail-table {
  width:100%;
  margin-top:10px;
  border-collapse: collapse;
}

.detail-table th, .detail-table td {
  border:1px solid #ddd;
  padding:8px;
}

.total {
  margin-top: 10px;
  color: red;
}

</style>