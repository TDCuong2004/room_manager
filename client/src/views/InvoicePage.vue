<template>
  <div class="p-6 bg-slate-50 min-h-screen font-sans">
    <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
      <h2 class="text-2xl font-black text-slate-800 flex items-center gap-2">
        <span class="text-3xl">💰</span> Quản lý hóa đơn
      </h2>

      <div class="flex flex-wrap items-center gap-3 w-full md:w-auto">
        <select 
          v-model="selectedBuilding" 
          @change="fetchInvoices"
          class="flex-1 md:flex-none p-2.5 bg-white border border-slate-200 rounded-xl outline-none focus:ring-2 focus:ring-rose-500 transition-all text-sm font-medium"
        >
          <option value="">-- Chọn tòa --</option>
          <option v-for="b in buildings" :key="b.id" :value="b.id">
            {{ b.buildingName }}
          </option>
        </select>

        <input 
          type="month" 
          v-model="selectedMonth" 
          @change="fetchInvoices"
          class="flex-1 md:flex-none p-2.5 bg-white border border-slate-200 rounded-xl outline-none focus:ring-2 focus:ring-rose-500 transition-all text-sm font-medium"
        >

        <button 
          class="px-5 py-2.5 bg-rose-500 hover:bg-rose-600 disabled:bg-slate-300 text-white font-bold rounded-xl shadow-lg shadow-rose-200 disabled:shadow-none transition-all active:scale-95 flex items-center gap-2"
          :disabled="!selectedBuilding || !selectedMonth"
          @click="generateInvoices"
        >
          <span>⚡</span> Tính tiền
        </button>
      </div>
    </div>

    <div class="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-slate-50 border-b border-slate-200">
              <th class="p-4 font-bold text-slate-600 text-sm">Phòng</th>
              <th class="p-4 font-bold text-slate-600 text-sm">Tháng</th>
              <th class="p-4 font-bold text-slate-600 text-sm">Tổng tiền</th>
              <th class="p-4 font-bold text-slate-600 text-sm">Trạng thái</th>
              <th class="p-4 font-bold text-slate-600 text-sm text-center">Hành động</th>
            </tr>
          </thead>

          <tbody class="divide-y divide-slate-100">
            <tr v-for="inv in invoices" :key="inv.id" class="hover:bg-slate-50/50 transition-colors">
              <td class="p-4 font-bold text-slate-700">{{ inv.roomName }}</td>
              <td class="p-4 text-slate-500 text-sm italic">{{ inv.month }}</td>
              <td class="p-4 font-black text-rose-600">{{ formatMoney(inv.totalAmount) }} đ</td>
              <td class="p-4">
                <span 
                  :class="[
                    'px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-wider',
                    inv.status === 'PAID' ? 'bg-emerald-100 text-emerald-600' : 'bg-orange-100 text-orange-600'
                  ]"
                >
                  {{ inv.status === 'PAID' ? 'Đã thanh toán' : 'Chờ thu' }}
                </span>
              </td>

              <td class="p-4">
                <div class="flex items-center justify-center gap-2">
                  <button @click="viewDetail(inv)" class="p-2 bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100 transition-colors title='Xem chi tiết'">
                    👁️
                  </button>
                  <button @click="generateQR(inv)" class="p-2 bg-purple-50 text-purple-600 rounded-lg hover:bg-purple-100 transition-colors title='Tạo mã QR'">
                    QR
                  </button>
                  <button 
                    v-if="inv.status === 'UNPAID'"
                    @click="markPaid(inv)"
                    class="px-3 py-1.5 bg-emerald-500 text-white text-xs font-bold rounded-lg hover:bg-emerald-600 transition-all shadow-md shadow-emerald-100"
                  >
                    Thu tiền
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="!invoices.length" class="p-20 text-center">
        <p class="text-slate-400 font-medium italic">Chưa có hóa đơn nào được tìm thấy cho kỳ này.</p>
      </div>
    </div>

    <div v-if="showDetail" class="fixed inset-0 z-[9999] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-slate-900/60 backdrop-blur-sm" @click="closeDetail"></div>
      <div class="bg-white w-full max-w-lg rounded-3xl shadow-2xl relative z-10 overflow-hidden animate-in fade-in zoom-in duration-200">
        <div class="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
          <h3 class="text-xl font-black text-slate-800 uppercase tracking-tight">Chi tiết hóa đơn</h3>
          <span class="px-3 py-1 bg-blue-100 text-blue-600 rounded-lg font-bold text-xs">{{ selectedInvoice.roomName }}</span>
        </div>

        <div class="p-6 space-y-4">
          <table class="w-full text-sm">
            <thead class="text-slate-400 border-b">
              <tr>
                <th class="pb-2 text-left font-bold">Dịch vụ</th>
                <th class="pb-2 text-center">SL</th>
                <th class="pb-2 text-right font-bold">Thành tiền</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-50">
              <tr v-for="d in details" :key="d.id">
                <td class="py-3 font-medium text-slate-700">{{ d.serviceName }}</td>
                <td class="py-3 text-center text-slate-500">{{ d.quantity }}</td>
                <td class="py-3 text-right font-bold text-slate-900">{{ formatMoney(d.amount) }}</td>
              </tr>
            </tbody>
          </table>

          <div class="pt-4 border-t-2 border-dashed border-slate-100 flex justify-between items-center">
            <span class="font-bold text-slate-500 uppercase text-xs">Tổng cộng</span>
            <span class="text-2xl font-black text-rose-600">{{ formatMoney(selectedInvoice.totalAmount) }} đ</span>
          </div>
        </div>

        <div class="p-4 bg-slate-50 flex justify-end">
          <button @click="closeDetail" class="px-6 py-2 bg-slate-200 hover:bg-slate-300 text-slate-700 font-bold rounded-xl transition-colors">Đóng</button>
        </div>
      </div>
    </div>

    <div v-if="showQR" class="fixed inset-0 z-[9999] flex items-center justify-center p-4">
      <div class="absolute inset-0 bg-slate-900/80 backdrop-blur-md" @click="showQR = false"></div>
      <div class="bg-white w-full max-w-sm rounded-[2.5rem] p-8 shadow-2xl relative z-10 text-center animate-in zoom-in duration-300">
        <h3 class="text-lg font-black text-slate-800 mb-2 uppercase tracking-wide">Quét mã thanh toán</h3>
        
        <div class="bg-slate-50 p-4 rounded-3xl border border-slate-100 my-6">
          <img v-if="qrUrl" :src="qrUrl" alt="QR Code" class="w-full aspect-square rounded-2xl shadow-inner mx-auto" />
        </div>

        <div class="space-y-1 mb-8">
          <p class="text-slate-400 text-xs font-bold uppercase tracking-widest">{{ selectedInvoice.roomName }} • THÁNG {{ selectedInvoice.month }}</p>
          <p class="text-3xl font-black text-rose-600">{{ formatMoney(selectedInvoice.totalAmount) }} đ</p>
          <div class="mt-4 p-3 bg-rose-50 rounded-xl inline-block">
            <p class="text-[10px] text-rose-400 font-black uppercase mb-1">Nội dung chuyển khoản</p>
            <p class="text-sm font-bold text-rose-700 uppercase tracking-tighter">PHONG_{{ selectedInvoice.roomName }}_T{{ selectedInvoice.month }}</p>
          </div>
        </div>

        <button @click="showQR = false" class="w-full py-4 bg-slate-900 hover:bg-black text-white font-black rounded-2xl transition-all shadow-xl shadow-slate-200 uppercase tracking-widest text-sm">
          Đã xong
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
      selectedMonth: "", // Sẽ được set mặc định trong mounted
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
    this.setDefaultMonth()
    this.fetchBuildings()
  },

  methods: {
    // TỰ ĐỘNG LẤY THÁNG/NĂM HIỆN TẠI
    setDefaultMonth() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      this.selectedMonth = `${year}-${month}`
    },

    async fetchBuildings() {
      try {
        const res = await api.get("/buildings")
        this.buildings = res.data
      } catch (err) { console.error(err) }
    },

    async fetchInvoices() {
      if (!this.selectedBuilding || !this.selectedMonth) {
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
      } catch (err) { console.error(err) }
    },

    async generateInvoices() {
      if (!this.selectedMonth || !this.selectedBuilding) {
        alert("Vui lòng chọn đầy đủ tòa và tháng")
        return
      }
      try {
        await api.post("/invoices/generate", null, {
          params: {
            buildingId: this.selectedBuilding,
            month: this.selectedMonth
          }
        })
        alert("🚀 Tính tiền thành công!")
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
      } catch (err) { console.error(err) }
    },

    generateQR(inv) {
      this.selectedInvoice = inv
      const bankCode = "970422" // MB Bank
      const accountNo = "0353260919" 
      const amount = inv.totalAmount
      const content = `PHONG_${inv.roomName}_T${inv.month}`
      this.qrUrl = `https://img.vietqr.io/image/${bankCode}-${accountNo}-compact.png?amount=${amount}&addInfo=${content}`
      this.showQR = true
    },

    async markPaid(inv) {
      if (!confirm("Xác nhận phòng này đã nộp tiền?")) return
      try {
        await api.put(`/invoices/${inv.id}/pay`)
        inv.status = "PAID"
      } catch (err) {
        console.error(err)
        alert("Lỗi cập nhật trạng thái")
      }
    },

    closeDetail() { this.showDetail = false },

    formatMoney(value) {
      return new Intl.NumberFormat("vi-VN").format(value || 0)
    }
  }
}
</script>

<style scoped>
/* Thêm hiệu ứng cho modal mượt mà hơn */
.animate-in {
  animation: modalIn 0.3s ease-out;
}
@keyframes modalIn {
  from { opacity: 0; transform: scale(0.95) translateY(10px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

/* Custom scrollbar cho các bảng dài */
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 10px;
}
</style>