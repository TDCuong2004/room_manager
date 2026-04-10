<template>
<div class="rent-page">

  <!-- HEADER -->
  <div class="rent-header">
    <button class="back-btn" @click="$router.back()">← Quay lại</button>
    <h2>📄 Tạo hợp đồng thuê phòng</h2>
  </div>

  <!-- LOADING -->
  <div v-if="!room.id" class="loading">
    Đang tải phòng...
  </div>

  <!-- CONTENT -->
  <div v-else>

    <!-- ROOM INFO -->
    <div class="card">
      <h3>Thông tin phòng</h3>

      <div class="info-grid">
        <p><b>Tên:</b> {{ room.roomName }}</p>
        <p><b>Mã:</b> {{ room.roomCode }}</p>
        <p><b>Giá:</b> {{ formatMoney(room.price) }} đ</p>
        <p><b>Tối đa:</b> {{ room.maxPeople }} người</p>
      </div>
    </div>

    <!-- CONTRACT -->
    <div class="card">
      <h3>Thông tin hợp đồng</h3>

      <div class="form-row">
        <input type="date" v-model="form.startDate">
        <input type="date" v-model="form.endDate">
        <input type="number" placeholder="Tiền cọc" v-model="form.deposit">
        <input type="number" placeholder="Giá thuê" v-model="form.rentPrice">
      </div>
    </div>

    <!-- CUSTOMERS -->
    <div class="card">

      <div class="customer-header">
        <h3>Người thuê</h3>
        <button class="add-btn" @click="addCustomer">+ Thêm</button>
      </div>

      <div v-for="(c,index) in form.customers" :key="index" class="customer-card">

        <!-- INFO -->
        <div class="customer-info">
          <input placeholder="Tên" v-model="c.fullName">
          <input placeholder="SĐT" v-model="c.phone">
          <input type="number" placeholder="Năm sinh" v-model="c.birthYear">

          <select v-model="c.gender">
            <option value="MALE">Nam</option>
            <option value="FEMALE">Nữ</option>
            <option value="OTHER">Khác</option>
          </select>
        </div>

        <!-- CCCD BOX -->
        <div class="cccd-box">
          <!-- FRONT -->
          <div class="upload-area">
            <label class="upload-label">
              <span class="upload-title">CCCD mặt trước</span>
              <div class="upload-placeholder" :class="{ 'has-img': c.cccdFrontImage }">
                <img v-if="c.cccdFrontImage" :src="c.cccdFrontImage">
                <div v-else class="placeholder-content">
                  <span class="icon">📷</span>
                  <span>Bấm để tải ảnh</span>
                </div>
                <input type="file" @change="uploadFile($event, index, 'front')" hidden>
              </div>
            </label>
          </div>

          <!-- BACK -->
          <div class="upload-area">
            <label class="upload-label">
              <span class="upload-title">CCCD mặt sau</span>
              <div class="upload-placeholder" :class="{ 'has-img': c.cccdBackImage }">
                <img v-if="c.cccdBackImage" :src="c.cccdBackImage">
                <div v-else class="placeholder-content">
                  <span class="icon">📷</span>
                  <span>Bấm để tải ảnh</span>
                </div>
                <input type="file" @change="uploadFile($event, index, 'back')" hidden>
              </div>
            </label>
          </div>
        </div>

        <!-- ACTION -->
        <div class="customer-actions">
          <label>
            <input type="radio"
                   name="rep"
                   :value="index"
                   v-model="representativeIndex">
            Đại diện
          </label>

          <button class="remove-btn" @click="removeCustomer(index)">X</button>
        </div>

      </div>

    </div>

    <!-- SUBMIT -->
    <div class="submit-box">
      <button class="submit-btn" @click="goPreview">
        Xem trước hợp đồng
      </button>
    </div>

  </div>

</div>
</template>

<script>
import api from "@/api"

export default {

  data(){
    return{
      room:{},
      representativeIndex:0,

      form:{
        roomId:null,
        startDate:"",
        endDate:"",
        deposit:"",
        rentPrice:"",
        customers:[]
      }
    }
  },

  mounted(){
    const roomId = this.$route.params.roomId
    this.form.roomId = roomId
    this.fetchRoom(roomId)
  },

  methods:{

    async fetchRoom(id){
      const res = await api.get(`/rooms/${id}`)
      this.room = res.data

      if(!this.form.rentPrice){
        this.form.rentPrice = this.room.price
      }
    },

    addCustomer(){
      this.form.customers.push({
        fullName:"",
        phone:"",
        birthYear:"",
        gender:"MALE",
        representative:false,
        cccdFrontImage:"",
        cccdBackImage:""
      })
    },

    removeCustomer(index){
      this.form.customers.splice(index,1)
    },

    async uploadFile(event, index, type){
      const file = event.target.files[0]
      if(!file) return

      try{

        // =========================
        // 1. UPLOAD ẢNH
        // =========================
        const uploadData = new FormData()
        uploadData.append("file", file)

        const uploadRes = await api.post("/upload", uploadData, {
          headers: { "Content-Type": "multipart/form-data" }
        })

        if(type === "front"){
          this.form.customers[index].cccdFrontImage = uploadRes.data

          // =========================
          // 2. OCR (PHẢI TẠO MỚI)
          // =========================
          const ocrData = new FormData()
          ocrData.append("file", file)

          const ocrRes = await api.post("/ocr", ocrData, {
            headers: { "Content-Type": "multipart/form-data" }
          })

          const data = ocrRes.data

          console.log("OCR RESULT:", data)

          // =========================
          // AUTO FILL
          // =========================
          if(data.name){
            this.form.customers[index].fullName = data.name
          }

          if(data.dob){
            // FPT trả dạng dd/MM/yyyy
            const year = data.dob.split("/")[2]
            this.form.customers[index].birthYear = year
          }

        }else{
          this.form.customers[index].cccdBackImage = uploadRes.data
        }

      }catch(err){
        console.error("UPLOAD/OCR ERROR:", err.response?.data || err)
        alert("Upload hoặc OCR thất bại")
      }
    },

    goPreview(){

      if(!this.form.startDate || !this.form.endDate){
        alert("Nhập ngày hợp đồng")
        return
      }

      if(!this.form.customers.length){
        alert("Thêm người thuê")
        return
      }

      this.form.customers.forEach((c,i)=>{
        c.representative = i === this.representativeIndex
      })

      sessionStorage.setItem("contractData", JSON.stringify({
        room:this.room,
        form:this.form
      }))

      this.$router.push("/contract-preview")
    },

    formatMoney(v){
      return new Intl.NumberFormat("vi-VN").format(v || 0)
    }

  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.rent-page {
  max-width: 900px;
  margin: 30px auto;
  padding: 0 20px;
  font-family: 'Inter', sans-serif;
  color: #1e293b;
  background-color: #f8fafc;
  min-height: 100vh;
}

/* --- HEADER --- */
.rent-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.rent-header h2 {
  font-size: 24px;
  font-weight: 700;
  margin: 0;
  color: #0f172a;
}

.back-btn {
  background: #ffffff;
  color: #64748b;
  border: 1px solid #e2e8f0;
  padding: 8px 16px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.back-btn:hover {
  background: #f1f5f9;
  color: #0f172a;
}

/* --- CARD TỔNG THỂ --- */
.card {
  background: #ffffff;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
  border: 1px solid #f1f5f9;
}

.card h3 {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 20px;
  color: #334155;
  display: flex;
  align-items: center;
  gap: 10px;
}

/* --- THÔNG TIN PHÒNG (GRID) --- */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  background: #f1f5f9;
  padding: 16px;
  border-radius: 12px;
}

.info-grid p {
  margin: 0;
  font-size: 15px;
}

.info-grid b {
  color: #64748b;
  font-weight: 500;
  margin-right: 5px;
}

/* --- FORM NHẬP LIỆU --- */
.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

input[type="text"],
input[type="number"],
input[type="date"],
select {
  width: 100%;
  padding: 12px 16px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  font-size: 15px;
  transition: all 0.2s;
  background: #ffffff;
  outline: none;
}

input:focus, select:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

/* --- KHU VỰC NGƯỜI THUÊ --- */
.customer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.customer-card {
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  background: #ffffff;
  position: relative;
  transition: transform 0.2s;
}

.customer-card:hover {
  border-color: #cbd5e1;
}

/* Chia tỷ lệ cột: Tên rộng nhất (2 phần), SĐT (1.5 phần), Năm sinh & Giới tính (1 phần) */
.customer-info {
  display: grid;
  grid-template-columns: 2fr 1.5fr 0.8fr 1fr; 
  gap: 15px;
  margin-bottom: 20px;
}

/* Tùy chỉnh riêng cho các ô input */
.customer-info input, 
.customer-info select {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  color: #334155;
  background-color: #ffffff;
  transition: all 0.2s ease;
}

/* Hiệu ứng khi nhấn vào ô nhập liệu */
.customer-info input:focus, 
.customer-info select:focus {
  border-color: #3b82f6;
  background-color: #fff;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  outline: none;
}

/* Làm cho Placeholder (chữ gợi ý) trông nhẹ nhàng hơn */
.customer-info input::placeholder {
  color: #94a3b8;
  font-size: 13px;
}

/* Responsive: Khi màn hình nhỏ (Mobile), tự động chia lại hàng */
@media (max-width: 768px) {
  .customer-info {
    grid-template-columns: 1fr 1fr; /* Chia 2 cột mỗi hàng trên mobile */
  }
}

@media (max-width: 480px) {
  .customer-info {
    grid-template-columns: 1fr; /* 1 cột mỗi hàng trên điện thoại nhỏ */
  }
}

/* --- UPLOAD CCCD STYLE MỚI --- */
.cccd-box {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.upload-area {
  flex: 1;
}

.upload-title {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #64748b;
  margin-bottom: 8px;
}

.upload-placeholder {
  border: 2px dashed #e2e8f0;
  border-radius: 12px;
  height: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  background: #f8fafc;
  overflow: hidden;
  transition: all 0.2s;
}

.upload-placeholder:hover {
  border-color: #3b82f6;
  background: #f0f7ff;
}

.upload-placeholder.has-img {
  border-style: solid;
}

.placeholder-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #94a3b8;
  font-size: 13px;
}

.placeholder-content .icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.upload-placeholder img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* --- ACTION BUTTONS --- */
.customer-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #f1f5f9;
}

.customer-actions label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #334155;
  cursor: pointer;
}

.remove-btn {
  background: #fee2e2;
  color: #ef4444;
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.2s;
}

.remove-btn:hover {
  background: #ef4444;
  color: white;
}

.add-btn {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(59, 130, 246, 0.2);
}

.add-btn:hover {
  background: #2563eb;
}

/* --- NÚT GỬI (SUBMIT) --- */
.submit-box {
  margin: 40px 0;
  text-align: center;
}

.submit-btn {
  background: #10b981;
  color: white;
  padding: 16px 40px;
  border: none;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 10px 15px -3px rgba(16, 185, 129, 0.3);
  transition: all 0.2s;
}

.submit-btn:hover {
  background: #059669;
  transform: translateY(-2px);
  box-shadow: 0 20px 25px -5px rgba(16, 185, 129, 0.2);
}

/* Loading state */
.loading {
  text-align: center;
  padding: 50px;
  color: #64748b;
  font-style: italic;
}
</style>