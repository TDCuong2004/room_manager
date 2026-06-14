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

    <div
      class="w-full max-w-md bg-white rounded-2xl shadow-2xl p-6 animate-scaleIn
      max-h-[90vh] overflow-y-auto custom-scroll"
    >

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
  <div class="relative">
    <input
      :value="formatMoneyInput(form.price)"
      @input="handlePriceInput"
      placeholder="Nhập giá"
      class="w-full mt-1 p-3 pr-16 rounded-xl border border-gray-300 bg-white
            focus:ring-2 focus:ring-green-500 focus:border-green-500 outline-none transition"
    />

    <span class="absolute right-3 inset-y-0 flex items-center text-gray-500 text-sm">
      VNĐ
    </span>
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
  <label class="text-xs font-semibold text-gray-500 uppercase block mb-2">
    Ảnh phòng
  </label>

  <!-- UPLOAD BOX -->
  <label
    class="group relative flex flex-col items-center justify-center w-full min-h-[150px]
    border-2 border-dashed border-gray-300 rounded-2xl bg-gray-50 hover:bg-orange-50
    hover:border-orange-400 transition-all cursor-pointer overflow-hidden"
  >

    <input
      type="file"
      multiple
      @change="handleImages"
      class="hidden"
    />

    <!-- EMPTY -->
    <div
      v-if="!previewImages.length"
      class="flex flex-col items-center justify-center py-8"
    >
      <div
        class="w-14 h-14 rounded-2xl bg-white shadow flex items-center justify-center text-2xl
        group-hover:scale-110 transition-all"
      >
        📸
      </div>

      <p class="mt-4 font-semibold text-gray-700">
        Tải ảnh phòng
      </p>

      <p class="text-sm text-gray-400 mt-1">
        PNG, JPG hoặc WEBP
      </p>
    </div>

    <!-- PREVIEW -->
    <div
      v-else
      class="grid grid-cols-3 gap-3 p-4 w-full"
    >
      <div
        v-for="(img, i) in previewImages"
        :key="i"
        class="relative group/img"
      >
        <img
          :src="img"
          class="w-full h-28 object-cover rounded-xl border border-gray-200 shadow-sm"
        />

        <!-- overlay -->
        <div
          class="absolute inset-0 bg-black/30 opacity-0 group-hover/img:opacity-100
          transition-all rounded-xl"
        />

        <!-- delete -->
        <button
          type="button"
          @click.stop="removeImage(i)"
          class="absolute top-2 right-2 w-7 h-7 rounded-full bg-red-500 text-white
          flex items-center justify-center text-xs shadow-lg opacity-0
          group-hover/img:opacity-100 transition-all"
        >
          ✕
        </button>
      </div>

      <!-- ADD MORE -->
      <div
        class="h-28 rounded-xl border-2 border-dashed border-gray-300
        flex items-center justify-center text-3xl text-gray-400 bg-white"
      >
        +
      </div>
    </div>

  </label>
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
  <div
  v-if="showCheckoutModal"
  class="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-[9999]"
>
  <div class="bg-white w-[450px] rounded-3xl p-6 shadow-2xl">

    <div class="text-center">

      <div
        class="w-16 h-16 mx-auto rounded-full bg-orange-100 flex items-center justify-center text-3xl"
      >
        ⚠️
      </div>

      <h3 class="text-xl font-bold mt-4">
        Xác nhận trả phòng
      </h3>

      <p class="text-gray-500 mt-2">
        Thao tác này sẽ kết thúc hợp đồng hiện tại.
      </p>

    </div>

    <div class="mt-5 bg-gray-50 rounded-2xl p-4">

      <div class="flex items-center gap-3 py-2">
        <span class="text-green-500">✓</span>
        <span>Hợp đồng → TERMINATED</span>
      </div>

      <div class="flex items-center gap-3 py-2">
        <span class="text-green-500">✓</span>
        <span>Khách thuê → INACTIVE</span>
      </div>

      <div class="flex items-center gap-3 py-2">
        <span class="text-green-500">✓</span>
        <span>Phòng → EMPTY</span>
      </div>

      <div class="flex items-center gap-3 py-2">
        <span class="text-blue-500">ℹ</span>
        <span>Giữ nguyên lịch sử hóa đơn</span>
      </div>

    </div>

    <div class="flex gap-3 mt-6">

      <button
        @click="showCheckoutModal = false"
        class="flex-1 py-3 rounded-xl bg-gray-100 hover:bg-gray-200 font-semibold"
      >
        Hủy
      </button>

      <button
        @click="confirmCheckout"
        class="flex-1 py-3 rounded-xl bg-rose-500 text-white hover:bg-rose-600 font-bold"
      >
        Xác nhận trả phòng
      </button>

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
      oldStatus: null,
      showCheckoutModal: false,
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
      this.oldStatus = this.room.status
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
    formatMoneyInput(value){
      if (!value) return ""
      return Number(value).toLocaleString("vi-VN")
    },

    handlePriceInput(e){
      const raw = e.target.value.replace(/\D/g, "") // chỉ giữ số
      this.form.price = raw
      e.target.value = this.formatMoneyInput(raw)
    },
    removeImage(index) {
      this.previewImages.splice(index, 1)
      this.images.splice(index, 1)
    },
    handleImages(e){

      const files = Array.from(e.target.files)

      files.forEach(file => {

        // thêm vào danh sách cũ
        this.images.push(file)

        // preview
        const reader = new FileReader()

        reader.onload = ev => {
          this.previewImages.push(ev.target.result)
        }

        reader.readAsDataURL(file)
      })

      // reset input để chọn lại cùng ảnh vẫn trigger change
      e.target.value = ""
    },

    async confirmCheckout() {

      this.showCheckoutModal = false

      const formData = new FormData()

      Object.keys(this.form).forEach(k => {
        formData.append(k, this.form[k])
      })

      formData.append("buildingId", this.buildingId)

      await api.put(`/rooms/${this.room.id}/checkout`)

      this.showToast("Trả phòng thành công 🎉")

      setTimeout(() => {
        this.$emit("saved")
      }, 800)
    },
    async save(){

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

        if (this.room) {

          // RENTED -> EMPTY
          if (
            this.oldStatus === "RENTED" &&
            this.form.status === "EMPTY"
          ) {
            this.showCheckoutModal = true
            return
          }
          await api.put(`/rooms/${this.room.id}`, formData)
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
.custom-scroll::-webkit-scrollbar{
  width:6px;
}

.custom-scroll::-webkit-scrollbar-thumb{
  background:#d1d5db;
  border-radius:999px;
}

.custom-scroll::-webkit-scrollbar-track{
  background:transparent;
}
</style>