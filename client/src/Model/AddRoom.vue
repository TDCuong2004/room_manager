<template>
  <div class="modal-overlay" @click.self="$emit('close')">

    <div class="modal-card">

      <div class="modal-scroll">

        <h2>{{ room ? "Sửa phòng" : "Thêm phòng" }}</h2>

        <!-- FORM -->

        <div class="form-group">
          <label>Mã phòng</label>
          <input v-model="form.roomCode">
        </div>

        <div class="form-group">
          <label>Tên phòng</label>
          <input v-model="form.roomName">
        </div>

        <div class="form-group">
          <label>Giá</label>
          <input type="number" v-model="form.price">
        </div>

        <div class="form-group">
          <label>Diện tích</label>
          <input type="number" v-model="form.area">
        </div>

        <div class="form-group">
          <label>Số người tối đa</label>
          <input type="number" v-model="form.maxPeople">
        </div>

        <div class="form-group">
          <label>Trạng thái</label>
          <select v-model="form.status">
            <option value="EMPTY">EMPTY</option>
            <option value="RENTED">RENTED</option>
            <option value="MAINTENANCE">MAINTENANCE</option>
          </select>
        </div>

        <div class="form-group">
          <label>Mô tả</label>
          <textarea v-model="form.description"></textarea>
        </div>

        <!-- 🔥 IMAGE UPLOAD -->

        <div class="form-group">
          <label>Ảnh phòng</label>
          <input type="file" multiple @change="handleImages" />

          <!-- PREVIEW -->
          <div class="preview-list" v-if="previewImages.length">
            <img 
              v-for="(img, index) in previewImages" 
              :key="index" 
              :src="img" 
            />
          </div>
        </div>

        <!-- BUTTON -->

        <div class="modal-actions">

          <button class="primary-btn" @click="save">
            {{ room ? "Cập nhật" : "Thêm" }}
          </button>

          <button class="cancel-btn" @click="$emit('close')">
            Hủy
          </button>

        </div>

      </div>

    </div>

  </div>
</template>

<script>
import api from "@/api"

export default {

  props:{
    buildingId:Number,
    room:Object
  },

  data(){
    return{

      form:{
        roomCode:"",
        roomName:"",
        price:0,
        area:0,
        maxPeople:1,
        status:"EMPTY",
        description:""
      },

      images:[],          // file thật
      previewImages:[],   // preview ảnh
      isEditing:false

    }
  },

  mounted(){

    if(this.room){
      this.isEditing = true

      this.form = {
        roomCode: this.room.roomCode,
        roomName: this.room.roomName,
        price: this.room.price,
        area: this.room.area,
        maxPeople: this.room.maxPeople,
        status: this.room.status,
        description: this.room.description
      }
    }

  },

  methods:{

    handleImages(e){
      const files = e.target.files
      this.images = files

      this.previewImages = []

      for(let i = 0; i < files.length; i++){
        const reader = new FileReader()

        reader.onload = (event) => {
          this.previewImages.push(event.target.result)
        }

        reader.readAsDataURL(files[i])
      }
    },

    async save(){

      if(!this.form.roomCode){
        alert("Nhập mã phòng")
        return
      }

      try{

        const formData = new FormData()

        formData.append("roomCode", this.form.roomCode)
        formData.append("roomName", this.form.roomName)
        formData.append("price", this.form.price)
        formData.append("area", this.form.area)
        formData.append("maxPeople", this.form.maxPeople)
        formData.append("status", this.form.status)
        formData.append("description", this.form.description)
        formData.append("buildingId", this.buildingId)

        // append ảnh
        for(let i = 0; i < this.images.length; i++){
          formData.append("images", this.images[i])
        }

        if(this.isEditing){

          await api.put(`/rooms/${this.room.id}`, formData, {
            headers:{ "Content-Type": "multipart/form-data" }
          })

        }else{

          await api.post(`/rooms`, formData, {
            headers:{ "Content-Type": "multipart/form-data" }
          })

        }

        this.$emit("saved")

      }catch(err){

        console.error(err)

      }

    }

  }

}
</script>

<style scoped>

.modal-overlay{
  position:fixed;
  inset:0;
  background:rgba(0,0,0,0.45);
  backdrop-filter:blur(3px);
  display:flex;
  justify-content:center;
  align-items:center;
  z-index:1000;
}

.modal-card{
  width:380px;
  background:white;
  border-radius:18px;
  box-shadow:0 20px 50px rgba(0,0,0,0.2);
  overflow:hidden;
  animation:fadeIn .25s ease;
}

.modal-scroll{
  max-height:85vh;
  overflow-y:auto;
  padding:25px;
}

.modal-scroll::-webkit-scrollbar{
  width:6px;
}

.modal-scroll::-webkit-scrollbar-thumb{
  background:#cbd5e1;
  border-radius:10px;
}

.modal-scroll h2{
  margin-bottom:18px;
  font-size:20px;
  font-weight:700;
  color:#374151;
}

.form-group{
  margin-bottom:12px;
}

.form-group label{
  display:block;
  font-size:13px;
  margin-bottom:4px;
  color:#4b5563;
}

.form-group input,
.form-group select,
.form-group textarea{
  width:100%;
  padding:8px 10px;
  border:1px solid #d1d5db;
  border-radius:10px;
  font-size:13px;
  outline:none;
}

.form-group textarea{
  min-height:55px;
}

/* 🔥 PREVIEW IMAGE */

.preview-list{
  display:flex;
  gap:10px;
  margin-top:10px;
  flex-wrap:wrap;
}

.preview-list img{
  width:70px;
  height:70px;
  object-fit:cover;
  border-radius:8px;
  border:1px solid #ddd;
}

/* BUTTON */

.modal-actions{
  display:flex;
  justify-content:flex-end;
  gap:10px;
  margin-top:15px;
}

.primary-btn{
  background:linear-gradient(135deg,#ff385c,#ff7a18);
  border:none;
  color:white;
  padding:8px 16px;
  border-radius:20px;
  cursor:pointer;
}

.cancel-btn{
  background:#e5e7eb;
  border:none;
  padding:8px 14px;
  border-radius:20px;
  cursor:pointer;
}

@keyframes fadeIn{
  from{
    transform:scale(.9);
    opacity:0;
  }
  to{
    transform:scale(1);
    opacity:1;
  }
}

</style>