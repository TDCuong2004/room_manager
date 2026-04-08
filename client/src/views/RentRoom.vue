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

        <!-- CCCD -->
        <div class="cccd-box">

          <!-- FRONT -->
          <div class="upload-box">
            <label>CCCD trước</label>
            <input type="file" @change="uploadFile($event, index, 'front')">
            <img v-if="c.cccdFrontImage" :src="c.cccdFrontImage">
          </div>

          <!-- BACK -->
          <div class="upload-box">
            <label>CCCD sau</label>
            <input type="file" @change="uploadFile($event, index, 'back')">
            <img v-if="c.cccdBackImage" :src="c.cccdBackImage">
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

.rent-page{
  max-width:900px;
  margin:auto;
  padding:20px;
}

/* HEADER */
.rent-header{
  display:flex;
  justify-content:space-between;
  align-items:center;
  margin-bottom:20px;
}

/* CARD */
.card{
  background:#fff;
  padding:20px;
  border-radius:16px;
  box-shadow:0 5px 20px rgba(0,0,0,0.08);
  margin-bottom:20px;
}

/* GRID */
.info-grid{
  display:grid;
  grid-template-columns:1fr 1fr;
  gap:10px;
}

.form-row{
  display:grid;
  grid-template-columns:1fr 1fr;
  gap:10px;
}

.form-row input{
  padding:10px;
  border-radius:8px;
  border:1px solid #ddd;
}

/* CUSTOMER */
.customer-card{
  border:1px solid #eee;
  border-radius:12px;
  padding:15px;
  margin-bottom:15px;
  background:#fafafa;
}

.customer-info{
  display:grid;
  grid-template-columns:repeat(4,1fr);
  gap:10px;
  margin-bottom:10px;
}

.customer-info input,
.customer-info select{
  padding:8px;
  border-radius:8px;
  border:1px solid #ddd;
}

/* CCCD */
.cccd-box{
  display:flex;
  gap:20px;
  margin-bottom:10px;
}

.upload-box{
  display:flex;
  flex-direction:column;
  gap:5px;
}

.upload-box img{
  width:120px;
  border-radius:8px;
  border:1px solid #ddd;
}

/* ACTION */
.customer-actions{
  display:flex;
  justify-content:space-between;
  align-items:center;
}

.remove-btn{
  background:#ef4444;
  color:white;
  border:none;
  padding:5px 10px;
  border-radius:6px;
}

/* BUTTON */
.submit-box{
  text-align:center;
}

.submit-btn{
  background:#22c55e;
  color:white;
  padding:12px 25px;
  border:none;
  border-radius:25px;
}

.add-btn{
  background:#3b82f6;
  color:white;
  border:none;
  padding:6px 12px;
  border-radius:20px;
}

.back-btn{
  background:#6b7280;
  color:white;
  border:none;
  padding:6px 10px;
  border-radius:8px;
}

</style>