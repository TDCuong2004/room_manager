<template>
<div class="preview-page">

  <!-- HEADER -->
  <div class="header">
    <button class="back-btn" @click="$router.back()">← Quay lại</button>
    <h2>📄 Xem trước hợp đồng</h2>
  </div>

  <!-- CONTRACT -->
  <div ref="contractRef" class="contract">

    <div class="page">

      <h3 class="center bold">CỘNG HOÀ XÃ HỘI CHỦ NGHĨA VIỆT NAM</h3>
      <p class="center">Độc lập - Tự do - Hạnh phúc</p>

      <h2 class="center title">HỢP ĐỒNG CHO THUÊ PHÒNG TRỌ</h2>

      <p>Căn cứ Bộ luật Dân sự Số 91/2015/QH13;</p>
      <p>Căn cứ Luật Nhà ở, Luật Đất đai;</p>

      <p>Hôm nay, ngày {{ today }}</p>

      <p class="bold">BÊN A: BÊN CHO THUÊ</p>
      <p>Đại diện: Nguyễn Thị Ngọc Diệp</p>
      <p>CCCD: 001196008731</p>
      <p>Điện thoại: 0888889696</p>

      <p class="bold mt">BÊN B: BÊN THUÊ</p>

      <div v-for="(c,i) in form.customers" :key="i">
        <p>{{ i+1 }}. {{ c.fullName }} - {{ c.phone }}</p>
        <p>Ngày sinh: {{ c.birthYear }}</p>
      </div>

      <p class="bold mt">ĐIỀU 1: ĐỐI TƯỢNG</p>
      <p>
        Bên A cho thuê phòng {{ room.roomName }} tại {{ room.address || "..." }}
      </p>

      <p>
        Thời hạn: {{ form.startDate }} → {{ form.endDate }}
      </p>

      <p class="bold mt">ĐIỀU 2: GIÁ THUÊ</p>
      <p>Giá: {{ formatMoney(form.rentPrice) }} VNĐ</p>
      <p>Tiền cọc: {{ formatMoney(form.deposit) }} VNĐ</p>

      <p>Tiền điện: 4.000đ/kWh</p>
      <p>Tiền nước: 100.000đ/người</p>

      <p class="bold mt">ĐIỀU 3: TRÁCH NHIỆM</p>

      <p>Bên A đảm bảo điện nước</p>
      <p>Bên B thanh toán đúng hạn</p>

      <p class="bold mt">ĐIỀU 4: QUY ĐỊNH</p>

      <ul>
        <li>Không gây ồn</li>
        <li>Không chất cấm</li>
        <li>Giữ vệ sinh</li>
      </ul>

      <p class="bold mt">ĐIỀU 5: CHUNG</p>
      <p>Hai bên cam kết thực hiện</p>

      <br><br>

      <div class="sign">
        <div>
          <p class="bold">BÊN A</p>
          <p>(Ký tên)</p>
        </div>

        <div>
          <p class="bold">BÊN B</p>
          <p>(Ký tên)</p>
        </div>
      </div>

    </div>

  </div>

  <!-- ACTION -->
  <div class="actions">
    <button 
      class="save" 
      @click="save"
      :disabled="isSaving"
    >
      {{ isSaving ? "Đang lưu..." : "💾 Lưu" }}
    </button>

    <button class="export" @click="exportWord">
      📄 Xuất Word
    </button>
  </div>

</div>
</template>

<script>
import api from "@/api"
import { saveAs } from "file-saver"

export default {

  data(){
    return{
      room:{},
      form:{ customers:[] },
      today:"",
      isSaving:false // 
    }
  },

  mounted(){
    const raw = sessionStorage.getItem("contractData")

    if(!raw){
      alert("Không có dữ liệu")
      this.$router.push("/")
      return
    }

    const data = JSON.parse(raw)
    this.room = data.room
    this.form = data.form

    const d = new Date()
    this.today = `${d.getDate()}/${d.getMonth()+1}/${d.getFullYear()}`
  },

  methods:{

    async save(){

      if(this.isSaving) return // 

      try{
        this.isSaving = true

        await api.post("/contracts", this.form)

        alert("Lưu thành công")

        this.$router.push("/dashboard")

      }catch(err){
        console.error(err)
        alert("Lỗi lưu hợp đồng")
      }finally{
        this.isSaving = false
      }
    },

    // =========================
    // EXPORT WORD
    // =========================
    exportWord(){

      const content = this.$refs.contractRef.innerHTML

      const html = `
        <html xmlns:o='urn:schemas-microsoft-com:office:office'
              xmlns:w='urn:schemas-microsoft-com:office:word'>
        <head>
          <meta charset="utf-8">
          <title>Contract</title>
        </head>
        <body>${content}</body>
        </html>
      `

      const blob = new Blob(['\ufeff', html], {
        type: 'application/msword'
      })

      saveAs(blob, "hop-dong.doc")
    },

    formatMoney(v){
      return new Intl.NumberFormat("vi-VN").format(v || 0)
    }

  }
}
</script>

<style scoped>

.preview-page{
  padding:20px;
  background:#f3f4f6;
}

.page{
  background:white;
  width:794px;
  min-height:1123px;
  margin:auto;
  padding:40px;
}

.center{text-align:center;}
.bold{font-weight:bold;}
.mt{margin-top:15px;}

.sign{
  display:flex;
  justify-content:space-between;
  margin-top:50px;
}

.actions{
  text-align:center;
  margin-top:20px;
}

.save{
  background:#22c55e;
  color:white;
  padding:10px 20px;
  border:none;
  border-radius:8px;
}

.save:disabled{
  background:#9ca3af;
  cursor:not-allowed;
}

.export{
  background:#6366f1;
  color:white;
  padding:10px 20px;
  border:none;
  margin-left:10px;
  border-radius:8px;
}

</style>