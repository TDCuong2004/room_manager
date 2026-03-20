<template>
<div class="rent-page">

  <div class="rent-header">
    <button class="back-btn" @click="$router.back()">← Quay lại</button>
    <h2>📄 Tạo hợp đồng thuê phòng</h2>
  </div>

  <!-- ROOM INFO -->
  <div class="card room-info">
    <h3>Thông tin phòng</h3>

    <p><b>Tên phòng:</b> {{ room.roomName }}</p>
    <p><b>Mã phòng:</b> {{ room.roomCode }}</p>
    <p><b>Giá:</b> {{ formatMoney(room.price) }} đ</p>
    <p><b>Số người tối đa:</b> {{ room.maxPeople }}</p>
  </div>

  <!-- CONTRACT INFO -->
  <div class="card">
    <h3>Thông tin hợp đồng</h3>

    <div class="form-row">
      <label>Ngày bắt đầu</label>
      <input type="date" v-model="form.startDate">
    </div>

    <div class="form-row">
      <label>Ngày kết thúc</label>
      <input type="date" v-model="form.endDate">
    </div>

    <div class="form-row">
      <label>Tiền cọc</label>
      <input type="number" v-model="form.deposit">
    </div>

    <div class="form-row">
      <label>Giá thuê</label>
      <input type="number" v-model="form.rentPrice">
    </div>

  </div>

  <!-- CUSTOMERS -->
  <div class="card">

    <div class="customer-header">
      <h3>Người thuê</h3>

      <button class="add-btn" @click="addCustomer">
        + Thêm người
      </button>
    </div>

    <div
      v-for="(c,index) in form.customers"
      :key="index"
      class="customer-row"
    >

      <input
        placeholder="Tên"
        v-model="c.fullName"
      >

      <input
        placeholder="SĐT"
        v-model="c.phone"
      >

      <input
        type="number"
        placeholder="Năm sinh"
        v-model="c.birthYear"
      >

      <select v-model="c.gender">
        <option value="MALE">Nam</option>
        <option value="FEMALE">Nữ</option>
        <option value="OTHER">Khác</option>
      </select>

      <label class="rep-check">
        <input
          type="radio"
          name="representative"
          :value="index"
          v-model="representativeIndex"
        >
        Đại diện
      </label>

      <button
        class="remove-btn"
        @click="removeCustomer(index)"
      >
        X
      </button>

    </div>

  </div>

  <!-- SUBMIT -->
  <div class="submit-box">
    <button class="submit-btn" @click="createContract">
      Tạo hợp đồng
    </button>
  </div>

</div>
</template>

<script>

import axios from "axios"

const api = axios.create({
  baseURL:"http://localhost:3000/api"
})

api.interceptors.request.use(config=>{

  const token = localStorage.getItem("token")

  if(token){
    config.headers.Authorization = "Bearer "+token
  }

  return config

})

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

      try{

        const res = await api.get(`/rooms/${id}`)

        this.room = res.data

        this.form.rentPrice = this.room.price

      }catch(err){
        console.error(err)
      }

    },

    addCustomer(){

      this.form.customers.push({
        fullName:"",
        phone:"",
        birthYear:"",
        gender:"MALE",
        customerId:null,
        representative:false
      })

    },

    removeCustomer(index){

      this.form.customers.splice(index,1)

    },

    async createContract(){

      if(!this.form.customers.length){
        alert("Thêm ít nhất 1 người thuê")
        return
      }

      try{

        // tạo customer trước
        for(let c of this.form.customers){

          const res = await api.post("/customers",{
            fullName:c.fullName,
            phone:c.phone,
            birthYear:c.birthYear,
            gender:c.gender
          })

          c.customerId = res.data.id

        }

        // set representative
        this.form.customers.forEach((c,i)=>{
          c.representative = i === this.representativeIndex
        })

        await api.post("/contracts",this.form)

        alert("Tạo hợp đồng thành công")

        this.$router.push("/dashboard")

      }catch(err){

        console.error(err)

      }

    },

    formatMoney(value){

      return new Intl.NumberFormat("vi-VN").format(value)

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

.rent-header{
display:flex;
align-items:center;
gap:20px;
margin-bottom:20px;
}

.card{
background:#fff;
padding:20px;
border-radius:10px;
box-shadow:0 2px 10px rgba(0,0,0,0.1);
margin-bottom:20px;
}

.form-row{
display:flex;
flex-direction:column;
margin-bottom:15px;
}

.form-row input{
padding:8px;
border:1px solid #ccc;
border-radius:5px;
}

.customer-header{
display:flex;
justify-content:space-between;
align-items:center;
margin-bottom:10px;
}

.customer-row{
display:flex;
align-items:center;
gap:10px;
margin-bottom:10px;
flex-wrap:wrap;
}

.customer-row input,
.customer-row select{
padding:6px;
border:1px solid #ccc;
border-radius:5px;
}

.rep-check{
font-size:14px;
}

.add-btn{
background:#3498db;
color:white;
border:none;
padding:8px 12px;
border-radius:5px;
cursor:pointer;
}

.remove-btn{
background:#e74c3c;
color:white;
border:none;
padding:5px 10px;
border-radius:5px;
cursor:pointer;
}

.submit-box{
text-align:center;
}

.submit-btn{
background:#2ecc71;
color:white;
border:none;
padding:12px 30px;
font-size:16px;
border-radius:8px;
cursor:pointer;
}

.back-btn{
background:#555;
color:white;
border:none;
padding:8px 12px;
border-radius:5px;
cursor:pointer;
}

</style>