<template>
  <!-- TOAST -->
  <div
    v-if="toast.show"
    class="fixed top-5 right-5 px-4 py-3 rounded-xl text-white shadow-lg z-[9999]"
    :class="toast.type === 'success' ? 'bg-green-500' : 'bg-red-500'"
  >
    {{ toast.message }}
  </div>

  <!-- MODAL -->
  <div class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50" @click.self="$emit('close')">

    <div class="w-full max-w-md bg-white rounded-2xl shadow-2xl p-6 animate-scaleIn">

      <h2 class="text-xl font-bold text-gray-800 mb-5">
        {{ room ? "Sửa phòng" : "Thêm phòng" }}
      </h2>

      <div class="space-y-4">

  <!-- TÊN -->
  <div>
    <label class="text-xs font-semibold text-gray-500 uppercase">Tên phòng</label>
    <input 
      v-model="form.roomName"
      class="w-full mt-1 p-3 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:ring-2 focus:ring-orange-400 outline-none transition"
    />
  </div>

  <!-- GIÁ -->
  <div>
    <label class="text-xs font-semibold text-gray-500 uppercase">Giá</label>
    <input 
      type="number"
      min="0"
      v-model.number="form.price"
      class="w-full mt-1 p-3 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:ring-2 focus:ring-orange-400 outline-none transition"
    />
  </div>

  <!-- DIỆN TÍCH -->
  <div>
    <label class="text-xs font-semibold text-gray-500 uppercase">Diện tích</label>
    <input 
      type="number"
      min="0"
      v-model.number="form.area"
      class="w-full mt-1 p-3 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:ring-2 focus:ring-orange-400 outline-none transition"
    />
  </div>

  <!-- MAX PEOPLE -->
  <div>
    <label class="text-xs font-semibold text-gray-500 uppercase">Số người</label>
    <input 
      type="number"
      min="1"
      v-model.number="form.maxPeople"
      class="w-full mt-1 p-3 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:ring-2 focus:ring-orange-400 outline-none transition"
    />
  </div>

  <!-- STATUS -->
  <div>
    <label class="text-xs font-semibold text-gray-500 uppercase">Trạng thái</label>
    <select 
      v-model="form.status"
      class="w-full mt-1 p-3 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:ring-2 focus:ring-orange-400 outline-none transition"
    >
      <option value="EMPTY">EMPTY</option>
      <option value="RENTED">RENTED</option>
      <option value="MAINTENANCE">MAINTENANCE</option>
    </select>
  </div>

  <!-- MÔ TẢ -->
  <div>
    <label class="text-xs font-semibold text-gray-500 uppercase">Mô tả</label>
    <textarea 
      v-model="form.description"
      class="w-full mt-1 p-3 rounded-xl border border-gray-200 bg-gray-50 focus:bg-white focus:ring-2 focus:ring-orange-400 outline-none transition"
    ></textarea>
  </div>

  <!-- ẢNH -->
  <div>
    <label class="text-xs font-semibold text-gray-500 uppercase">Ảnh phòng</label>
    <input type="file" multiple @change="handleImages" class="mt-1"/>

    <div v-if="previewImages.length" class="flex flex-wrap gap-2 mt-2">
      <div
        v-for="(img, i) in previewImages"
        :key="i"
        class="relative"
      >
        <img
          :src="img"
          class="w-16 h-16 object-cover rounded-lg border"
        />

        <!-- ❌ nút x -->
        <button
          @click="removeImage(i)"
          class="absolute -top-2 -right-2 bg-red-500 text-white w-5 h-5 rounded-full text-xs flex items-center justify-center shadow"
        >
          ✕
        </button>
      </div>
    </div>
  </div>

  <!-- BUTTON -->
  <div class="flex justify-end gap-3 pt-3">

    <button
      @click="$emit('close')"
      class="px-4 py-2 bg-gray-100 hover:bg-gray-200 rounded-xl"
    >
      Hủy
    </button>

    <button
      @click="save"
      :disabled="loading"
      class="px-5 py-2 bg-gradient-to-r from-orange-500 to-pink-500 text-white rounded-xl flex items-center gap-2 disabled:opacity-50"
    >
      <span v-if="loading" class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
      {{ loading ? "Đang lưu..." : (room ? "Cập nhật" : "Thêm") }}
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
      loading:false,

      form:{
        roomName:"",
        price:0,
        area:0,
        maxPeople:1,
        status:"EMPTY",
        description:""
      },

      images:[],
      previewImages:[],

      toast:{
        show:false,
        message:"",
        type:"success"
      }
    }
  },

  mounted() {
    if (this.room) {
      // set form
      this.form = {
        roomName: this.room.roomName,
        price: this.room.price,
        area: this.room.area,
        maxPeople: this.room.maxPeople,
        status: this.room.status,
        description: this.room.description
      }

      // 🔥 THÊM ĐOẠN NÀY (quan trọng)
      if (this.room.images && this.room.images.length) {
        this.previewImages = this.room.images.map(img => {
          return "http://localhost:3000" + img.imageUrl
        })
      }
    }
  },

  methods:{
    showToast(msg, type="success"){
      this.toast = { show:true, message:msg, type }
      setTimeout(()=> this.toast.show=false, 2500)
    },
    removeImage(index) {
      this.previewImages.splice(index, 1)
      this.images.splice(index, 1)
    },
    handleImages(e){
      const files = e.target.files
      this.images = files
      this.previewImages = []

      for(let i=0;i<files.length;i++){
        const reader = new FileReader()
        reader.onload = e => this.previewImages.push(e.target.result)
        reader.readAsDataURL(files[i])
      }
    },

    async save(){

      // ✅ VALIDATE
      if(!this.form.roomName){
        return this.showToast("Nhập tên phòng", "error")
      }

      if(this.form.price < 0){
        return this.showToast("Giá không được âm", "error")
      }

      if(this.form.area < 0){
        return this.showToast("Diện tích không hợp lệ", "error")
      }

      try{
        this.loading = true

        const formData = new FormData()

        Object.keys(this.form).forEach(k=>{
          formData.append(k, this.form[k])
        })

        formData.append("buildingId", this.buildingId)

        for (let i = 0; i < this.images.length; i++) {
          formData.append("images", this.images[i])
        }

        if(this.room){
          await api.put(`/rooms/${this.room.id}`, formData)
          this.showToast("Cập nhật thành công 🎉")
        }else{
          await api.post(`/rooms`, formData)
          this.showToast("Thêm phòng thành công 🎉")
        }

        setTimeout(()=>{
          this.$emit("saved")
        }, 800)

      }catch(err){
        console.error(err)

        const msg = err.response?.data?.error 

        if (msg) {
          this.showToast(msg, "error")
        } else {
          this.showToast("Có lỗi xảy ra ❌", "error")
        }
      }
      finally{
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>

@keyframes scaleIn {
  from { transform: scale(0.9); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}
.animate-scaleIn {
  animation: scaleIn 0.2s ease;
}
</style>