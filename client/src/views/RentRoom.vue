<template>
  <div class="min-h-screen bg-gray-50 py-8 px-4 font-sans text-gray-900">
    <div class="max-w-4xl mx-auto">
      
      <header class="flex items-center gap-4 mb-8">
        <button 
          @click="$router.back()" 
          class="bg-white hover:bg-gray-100 text-gray-600 border border-gray-200 px-4 py-2 rounded-xl font-medium transition-all shadow-sm active:scale-95"
        >
          ← Quay lại
        </button>
        <h2 class="text-2xl font-bold text-gray-900 tracking-tight italic">📄 Tạo hợp đồng thuê phòng</h2>
      </header>

      <div v-if="!room.id" class="flex flex-col items-center justify-center py-20 text-gray-400">
        <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600 mb-4"></div>
        <p class="italic">Đang tải dữ liệu phòng...</p>
      </div>

      <div v-else class="space-y-6">
        
        <section class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
          <h3 class="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
            <span class="text-blue-500">🏢</span> Thông tin phòng
          </h3>
          <div class="grid grid-cols-2 md:grid-cols-4 gap-4 p-4 bg-gray-50 rounded-xl">
            <div>
              <p class="text-xs text-gray-500 uppercase font-bold">Tên phòng</p>
              <p class="font-semibold">{{ room.roomName }}</p>
            </div>
            <div>
              <p class="text-xs text-gray-500 uppercase font-bold">Mã phòng</p>
              <p class="font-semibold">{{ room.roomCode }}</p>
            </div>
            <div>
              <p class="text-xs text-gray-500 uppercase font-bold">Giá niêm yết</p>
              <p class="font-bold text-blue-600">{{ formatMoney(room.price) }}đ</p>
            </div>
            <div>
              <p class="text-xs text-gray-500 uppercase font-bold">Sức chứa</p>
              <p class="font-semibold">{{ room.maxPeople }} người</p>
            </div>
          </div>
        </section>
        <section 
          v-if="meterServices.length"
          class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100"
        >
          <h3 class="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
            <span class="text-blue-500">⚡</span> Chỉ số dịch vụ ban đầu
          </h3>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div 
              v-for="s in meterServices" 
              :key="s.id"
              class="space-y-1"
            >
              <label class="text-xs font-bold text-gray-500 ml-1 italic">
                {{ s.serviceName }}
              </label>

              <input
                type="number"
                step="0.01"
                v-model.number="form.meterOldValues[s.id]"
                placeholder="Nhập chỉ số"
                class="w-full p-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
              />
            </div>
          </div>
        </section>
        <section class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
          <h3 class="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
            <span class="text-blue-500">📅</span> Điều khoản hợp đồng
          </h3>
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <div class="space-y-1">
              <label class="text-xs font-bold text-gray-500 ml-1 italic">Ngày bắt đầu</label>
              <input type="date" v-model="form.startDate" class="w-full p-3 bg-white border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition-all">
            </div>
            <div class="space-y-1">
              <label class="text-xs font-bold text-gray-500 ml-1 italic">Ngày kết thúc</label>
              <input type="date" v-model="form.endDate" class="w-full p-3 bg-white border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition-all">
            </div>
            <div class="space-y-1">
              <label class="text-xs font-bold text-gray-500 ml-1 italic">Tiền đặt cọc (VNĐ)</label>
              <input type="number" placeholder="0" v-model="form.deposit" class="w-full p-3 bg-white border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition-all font-bold text-red-600">
            </div>
            <div class="space-y-1">
              <label class="text-xs font-bold text-gray-500 ml-1 italic">Giá thuê thực tế</label>
              <input type="number" placeholder="0" v-model="form.rentPrice" class="w-full p-3 bg-white border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none transition-all font-bold text-blue-600">
            </div>
          </div>
        </section>

        <section class="bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-lg font-bold text-gray-800 flex items-center gap-2">
              <span class="text-blue-500">👥</span> Thành viên thuê phòng
            </h3>
            <button 
              @click="addCustomer" 
              class="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded-xl font-bold shadow-lg shadow-blue-100 transition-all active:scale-95"
            >
              + Thêm người
            </button>
          </div>

          <div v-for="(c, index) in form.customers" :key="index" class="relative bg-gray-50 p-5 rounded-2xl border border-gray-200 mb-6 last:mb-0 transition-all hover:border-blue-200">
            <button 
              @click="removeCustomer(index)" 
              class="absolute -top-3 -right-3 bg-red-100 text-red-600 hover:bg-red-500 hover:text-white w-8 h-8 rounded-full flex items-center justify-center shadow-md transition-all z-10"
            >
              ✕
            </button>

            <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
              <div class="md:col-span-1.5 space-y-1">
                <label class="text-[11px] font-bold text-gray-400 uppercase ml-1">Họ và tên</label>
                <input v-model="c.fullName" placeholder="Nguyễn Văn A" class="w-full p-3 bg-white border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none text-sm">
              </div>
              <div class="space-y-1">
                <label class="text-[11px] font-bold text-gray-400 uppercase ml-1">Số điện thoại</label>
                <input v-model="c.phone" placeholder="09xx..." class="w-full p-3 bg-white border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none text-sm">
              </div>
              <div class="space-y-1">
                <label class="text-[11px] font-bold text-gray-400 uppercase ml-1">Năm sinh</label>
                <input type="number" v-model="c.birthYear" placeholder="2000" class="w-full p-3 bg-white border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none text-sm">
              </div>
              <div class="space-y-1">
                <label class="text-[11px] font-bold text-gray-400 uppercase ml-1">Giới tính</label>
                <select v-model="c.gender" class="w-full p-3 bg-white border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none text-sm cursor-pointer">
                  <option value="MALE">Nam</option>
                  <option value="FEMALE">Nữ</option>
                  <option value="OTHER">Khác</option>
                </select>
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-2">
                <p class="text-xs font-bold text-gray-500 ml-1">Mặt trước CCCD (OCR tự động điền)</p>
                <label class="group relative flex flex-col items-center justify-center h-40 bg-white border-2 border-dashed border-gray-200 rounded-2xl cursor-pointer hover:border-blue-400 hover:bg-blue-50 transition-all overflow-hidden">
                  <input type="file" @change="uploadFile($event, index, 'front')" class="hidden">
                  <img v-if="c.cccdFrontImage" :src="c.cccdFrontImage" class="absolute inset-0 w-full h-full object-cover">
                  <div v-else class="flex flex-col items-center text-gray-400 group-hover:text-blue-500">
                    <span class="text-3xl mb-1">📷</span>
                    <span class="text-xs font-semibold">Tải mặt trước</span>
                  </div>
                </label>
              </div>

              <div class="space-y-2">
                <p class="text-xs font-bold text-gray-500 ml-1">Mặt sau CCCD</p>
                <label class="group relative flex flex-col items-center justify-center h-40 bg-white border-2 border-dashed border-gray-200 rounded-2xl cursor-pointer hover:border-blue-400 hover:bg-blue-50 transition-all overflow-hidden">
                  <input type="file" @change="uploadFile($event, index, 'back')" class="hidden">
                  <img v-if="c.cccdBackImage" :src="c.cccdBackImage" class="absolute inset-0 w-full h-full object-cover">
                  <div v-else class="flex flex-col items-center text-gray-400 group-hover:text-blue-500">
                    <span class="text-3xl mb-1">📷</span>
                    <span class="text-xs font-semibold">Tải mặt sau</span>
                  </div>
                </label>
              </div>
            </div>

            <div class="mt-4 flex items-center justify-end p-2 border-t border-gray-100">
              <label class="flex items-center gap-2 cursor-pointer group">
                <input 
                  type="radio" 
                  name="rep" 
                  :value="index" 
                  v-model="representativeIndex"
                  class="w-4 h-4 text-blue-600 focus:ring-blue-500 border-gray-300"
                >
                <span class="text-sm font-bold text-gray-600 group-hover:text-blue-600 transition-colors">Người đại diện hợp đồng</span>
              </label>
            </div>
          </div>
        </section>

        <div class="py-10 text-center">
          <button 
            @click="goPreview" 
            class="bg-emerald-600 hover:bg-emerald-700 text-white px-12 py-4 rounded-2xl text-lg font-black shadow-xl shadow-emerald-100 hover:shadow-emerald-200 transition-all hover:-translate-y-1 active:scale-95"
          >
            XEM TRƯỚC HỢP ĐỒNG 📄
          </button>
          <p class="mt-3 text-xs text-gray-400 italic font-medium">Vui lòng kiểm tra kỹ thông tin trước khi tiếp tục</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"
import api from "@/api"

const route = useRoute()
const router = useRouter()
const services = ref([])
const meterServices = ref([])
const room = ref({})
const representativeIndex = ref(0)
const form = ref({
  roomId: null,
  startDate: "",
  endDate: "",
  deposit: "",
  rentPrice: "",
  customers: [],
  meterOldValues: {},
  meterList: []
})

const formatMoney = (v) => new Intl.NumberFormat("vi-VN").format(v || 0)

const fetchRoom = async (id) => {
  try {
    const res = await api.get(`/rooms/${id}`)
    room.value = res.data
    if (!form.value.rentPrice) form.value.rentPrice = room.value.price
  } catch (err) {
    console.error(err)
  }
}

const addCustomer = () => {
  form.value.customers.push({
    fullName: "",
    phone: "",
    birthYear: "",
    gender: "MALE",
    representative: false,
    cccdFrontImage: "",
    cccdBackImage: ""
  })
}

const loadServices = async () => {
  try {
    const buildingId = room.value.building?.id

    if (!buildingId) {
      console.error("❌ Không có buildingId", room.value)
      return
    }

    const res = await api.get(`/building-services/${buildingId}`)

    services.value = res.data

    // 🔥 lọc service tính theo công tơ
    meterServices.value = services.value.filter(
      s => s.calculationType === "BY_METER"
    )

    // 🔥 init giá trị ban đầu = 0
    meterServices.value.forEach(s => {
      form.value.meterOldValues[s.id] = 0
    })

  } catch (e) {
    console.error("LOAD SERVICES ERROR:", e)
  }
}

const removeCustomer = (index) => {
  form.value.customers.splice(index, 1)
}

const uploadFile = async (event, index, type) => {
  const file = event.target.files[0]
  if (!file) return

  try {
    // 1. Upload file
    const uploadData = new FormData()
    uploadData.append("file", file)
    const uploadRes = await api.post("/upload", uploadData, {
      headers: { "Content-Type": "multipart/form-data" }
    })

    if (type === "front") {
      form.value.customers[index].cccdFrontImage = uploadRes.data
      
      // 2. OCR tự động
      const ocrData = new FormData()
      ocrData.append("file", file)
      const ocrRes = await api.post("/ocr", ocrData, {
        headers: { "Content-Type": "multipart/form-data" }
      })

      const data = ocrRes.data
      if (data.name) form.value.customers[index].fullName = data.name
      if (data.dob) {
        const year = data.dob.split("/")[2]
        form.value.customers[index].birthYear = year
      }
    } else {
      form.value.customers[index].cccdBackImage = uploadRes.data
    }
  } catch (err) {
    console.error("Upload/OCR Error:", err)
    alert("Không thể xử lý ảnh hoặc OCR thất bại")
  }
}

const goPreview = () => {
  if (!form.value.startDate || !form.value.endDate) {
    alert("Vui lòng nhập thời hạn hợp đồng")
    return
  }

  if (!form.value.customers.length) {
    alert("Cần thêm ít nhất một người thuê")
    return
  }

  form.value.customers.forEach((c, i) => {
    c.representative = i === representativeIndex.value
  })

  // 🔥 build meter list
  form.value.meterList = meterServices.value.map(s => ({
    serviceId: s.id,
    oldValue: form.value.meterOldValues[s.id]
  }))

  console.log("FORM SEND:", form.value)

  sessionStorage.setItem("contractData", JSON.stringify({
    room: room.value,
    form: form.value
  }))

  router.push("/contract-preview")
}

onMounted(async () => {
  const roomId = route.params.roomId
  form.value.roomId = roomId

  await fetchRoom(roomId)
  await loadServices() // 🔥 thêm dòng này
})
</script>

<style scoped>
/* Custom Chrome Scrollbar */
::-webkit-scrollbar {
  width: 6px;
}
::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}
</style>