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
          <th>Hành động</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="inv in invoices" :key="inv.id">
          <td>{{ inv.roomName }}</td>
          <td>{{ inv.month }}</td>
          <td>{{ formatMoney(inv.totalAmount) }} đ</td>
          <td>
            <span :class="inv.status === 'PAID' ? 'paid' : 'unpaid'">
              {{ inv.status === 'PAID' ? 'Đã thanh toán' : 'Chưa thanh toán' }}
            </span>
          </td>

          <td>
            <div class="actions-btn">

              <button class="btn view" @click="viewDetail(inv)">
                Xem
              </button>

              <button class="btn qr" @click="generateQR(inv)">
                QR
              </button>

              <button 
                v-if="inv.status === 'UNPAID'"
                class="btn pay"
                @click="markPaid(inv)"
              >
                Thanh toán
              </button>

            </div>
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
          <thead>
            <tr>
              <th>Dịch vụ</th>
              <th>Số lượng</th>
              <th>Đơn giá</th>
              <th>Thành tiền</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="d in details" :key="d.id">
              <td>{{ d.serviceName }}</td>
              <td>{{ d.quantity }}</td>
              <td>{{ formatMoney(d.unitPrice) }}</td>
              <td>{{ formatMoney(d.amount) }}</td>
            </tr>
          </tbody>
        </table>

        <h4 class="total">
          Tổng: {{ formatMoney(selectedInvoice.totalAmount) }} đ
        </h4>

        <button @click="closeDetail">Đóng</button>
      </div>
    </div>

    <!-- MODAL QR -->
    <div v-if="showQR" class="modal-overlay" @click.self="showQR = false">
      <div class="modal-card qr-card">

        <h3>💳 Quét mã để thanh toán</h3>

        <img v-if="qrUrl" :src="qrUrl" alt="QR Code" />

        <p class="qr-title">
          {{ selectedInvoice.roomName }} - {{ selectedInvoice.month }}
        </p>

        <p class="qr-amount">
          {{ formatMoney(selectedInvoice.totalAmount) }} đ
        </p>

        <p class="qr-note">
          Nội dung: PHONG_{{ selectedInvoice.roomName }}_T{{ selectedInvoice.month }}
        </p>

        <button class="close-btn" @click="showQR = false">
          Đóng
        </button>

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
      details: [],

      showQR: false,
      qrUrl: ""
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

    generateQR(inv) {

      this.selectedInvoice = inv

      const bankCode = "970422" // MB Bank (đổi theo bank bạn)
      const accountNo = "0353260919" // STK của bạn

      const amount = inv.totalAmount
      const content = `PHONG_${inv.roomName}_T${inv.month}`

      this.qrUrl = `https://img.vietqr.io/image/${bankCode}-${accountNo}-compact.png?amount=${amount}&addInfo=${content}`

      this.showQR = true
    },

    async markPaid(inv){

      if(!confirm("Xác nhận đã thanh toán?")) return

      try{
        await api.put(`/invoices/${inv.id}/pay`)
        inv.status = "PAID"
      }catch(err){
        console.error(err)
        alert("Lỗi cập nhật trạng thái")
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

.invoice-table th {
  background:#f8f9fa;
}

.invoice-table tr:hover {
  background:#f1f1f1;
}

/* STATUS */
.paid {
  color:#2ecc71;
  background:#eafaf1;
  padding:4px 8px;
  border-radius:6px;
}

.unpaid {
  color:#e74c3c;
  background:#fdecea;
  padding:4px 8px;
  border-radius:6px;
}

.empty-state{
  text-align:center;
  margin-top:30px;
  color:#888;
}

/* BUTTONS */
.actions-btn{
  display:flex;
  gap:6px;
  justify-content:center;
}

.btn{
  border:none;
  padding:6px 10px;
  border-radius:6px;
  cursor:pointer;
  font-size:13px;
  transition:0.2s;
}

.btn.view{
  background:#3498db;
  color:white;
}

.btn.qr{
  background:#9b59b6;
  color:white;
}

.btn.pay{
  background:#2ecc71;
  color:white;
}

.btn:hover{
  opacity:0.8;
}
/* ===== MODAL OVERLAY ===== */
.modal-overlay {
  position: fixed;
  top:0;
  left:0;
  right:0;
  bottom:0;
  background: rgba(0,0,0,0.45);
  backdrop-filter: blur(4px);
  display:flex;
  justify-content:center;
  align-items:center;
  z-index:999;
}

/* ===== MODAL CARD ===== */
.modal-card {
  background:white;
  padding:24px;
  border-radius:16px;
  width:360px;
  max-width:90%;
  text-align:center;
  box-shadow:0 20px 60px rgba(0,0,0,0.2);
  animation:fadeIn 0.25s ease;
}

/* ===== ANIMATION ===== */
@keyframes fadeIn {
  from {
    opacity:0;
    transform:translateY(20px);
  }
  to {
    opacity:1;
    transform:translateY(0);
  }
}

/* ===== QR CARD ===== */
.qr-card h3 {
  margin-bottom:10px;
}

/* ===== QR IMAGE ===== */
.qr-card img {
  width:220px;
  height:220px;
  object-fit:contain;
  margin:16px auto;
  display:block;

  border-radius:12px;
  border:1px solid #eee;
  padding:10px;
  background:#fafafa;
}

/* ===== TEXT ===== */
.qr-title {
  font-weight:600;
  margin-top:8px;
}

.qr-amount {
  color:#e74c3c;
  font-size:20px;
  font-weight:bold;
  margin:10px 0;
}

.qr-note {
  font-size:13px;
  color:#666;
  background:#f3f4f6;
  padding:6px 10px;
  border-radius:6px;
  display:inline-block;
}

/* ===== BUTTON ===== */
.close-btn {
  margin-top:15px;
  background:#ff5a5f;
  color:white;
  border:none;
  padding:8px 18px;
  border-radius:8px;
  cursor:pointer;
  font-weight:500;
  transition:0.2s;
}

.close-btn:hover {
  opacity:0.9;
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
.modal-overlay {
  z-index: 9999;
}
</style>

