<template>
  <div class="p-6 bg-gray-100 min-h-screen" v-if="room">

    <!-- BACK -->
    <button
      @click="$router.back()"
      class="mb-4 px-4 py-2 bg-white rounded-lg shadow hover:bg-gray-100"
    >
      ← Quay lại
    </button>

    <!-- IMAGES -->
    <div class="grid grid-cols-3 gap-4 mb-6">
      <div class="col-span-2">
        <img
          :src="getImage(room.images?.[0])"
          class="w-full h-[300px] object-cover rounded-xl"
        />
      </div>

      <div class="grid grid-cols-2 gap-3">
        <img
          v-for="(img, index) in room.images?.slice(1,5)"
          :key="index"
          :src="getImage(img)"
          class="w-full h-[145px] object-cover rounded-xl"
        />
      </div>
    </div>

    <!-- TITLE -->
    <div class="flex justify-between items-center mb-4">
      <div>
        <p :class="statusClass" class="text-xs px-2 py-1 rounded mb-2 inline-block">
          {{ statusText }}
        </p>
        <h1 class="text-2xl font-bold">{{ room.roomName }}</h1>

        <p class="text-gray-400 text-xs">
        📍 {{ room.building?.address }}
        </p>
      </div>

      <div class="text-right">
        <p class="text-sm text-gray-400">GIÁ THUÊ</p>
        <p class="text-2xl font-bold text-red-600">
          {{ formatPrice(room.price) }} VND
        </p>
      </div>
    </div>

    <div class="grid grid-cols-3 gap-6">

      <!-- LEFT -->
      <div class="col-span-2 space-y-6">

        <!-- SERVICES -->
        <div class="bg-white p-4 rounded-xl shadow">
          <h3 class="font-semibold mb-3">Dịch vụ phòng</h3>

          <div class="grid grid-cols-3 gap-3 text-sm">
            <div
              v-for="s in services"
              :key="s.id"
              class="bg-gray-100 px-3 py-2 rounded-lg"
            >
              {{ s.serviceName }}
            </div>
          </div>
        </div>

        <!-- CONTRACT -->
        <div class="bg-white p-4 rounded-xl shadow">
          <h3 class="font-semibold mb-3">Hợp đồng</h3>

          <div v-if="contract">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <p class="text-xs text-gray-400">Bắt đầu</p>
                <p>{{ contract.startDate }}</p>
              </div>

              <div>
                <p class="text-xs text-gray-400">Kết thúc</p>
                <p>{{ contract.endDate }}</p>
              </div>

              <div>
                <p class="text-xs text-gray-400">Tiền cọc</p>
                <p class="font-bold">
                  {{ formatPrice(contract.deposit) }} đ
                </p>
              </div>
            </div>
          </div>

          <p v-else class="text-gray-400">Chưa có hợp đồng</p>
        </div>

        <div class="bg-white p-4 rounded-xl shadow">
            <h3 class="font-semibold mb-3">Lịch sử thanh toán gần đây</h3>

            <div v-if="payments.length">
                <div
                v-for="(p, index) in payments"
                :key="index"
                class="flex justify-between items-center border-b py-3 text-sm"
                >
                <div>
                    <p class="font-medium">
                    {{ p.title }}
                    </p>
                    <p class="text-gray-400 text-xs">
                    {{ formatDate(p.paidAt) }}
                    </p>
                </div>

                <p class="font-semibold text-green-600">
                    {{ formatPrice(p.amount) }} đ
                </p>
                </div>
            </div>

            <p v-else class="text-gray-400 text-sm">
                Chưa có thanh toán
            </p>
            </div>

      </div>

      <!-- RIGHT -->
      <div class="space-y-4">

        <div class="bg-white p-4 rounded-xl shadow text-center">
            <img
                :src="tenant?.cccdFrontImage || defaultAvatar"
                class="w-16 h-16 rounded-full mx-auto mb-2 object-cover"
            />

            <p class="font-semibold">
                {{ tenant?.fullName || "Chưa có người thuê" }}
            </p>

            <p class="text-gray-400 text-sm">Người thuê</p>

            <div v-if="tenant" class="mt-3 text-sm text-left space-y-1">
                <p>📞 {{ tenant.phone }}</p>
                <p>🎂 {{ tenant.birthYear }}</p>
                <p>👤 {{ tenant.gender }}</p>

                <p
                v-if="tenant.representative"
                class="text-green-600 text-xs"
                >
                Đại diện hợp đồng
                </p>
            </div>
        </div>

        <!-- METER -->
        <!-- <div class="bg-white p-4 rounded-xl shadow">
          <h4 class="font-semibold mb-2">Chỉ số</h4>

          <div
            v-for="m in meters"
            :key="m.id"
            class="flex justify-between text-sm"
          >
            <span>{{ m.serviceName }}</span>
            <span>{{ m.value }}</span>
          </div>
        </div> -->

      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue"
import { useRoute } from "vue-router"
import axios from "axios"

const route = useRoute()

const room = ref(null)
const services = ref([])
const contract = ref(null)
const tenant = ref(null)
const meters = ref([])
const payments = ref([])

const defaultAvatar = "https://i.pravatar.cc/100"

const api = axios.create({
  baseURL: "http://localhost:3000/api"
})

api.interceptors.request.use(config => {
  const token = localStorage.getItem("token")
  if(token) config.headers.Authorization = "Bearer " + token
  return config
})

// ================= FIX IMAGE =================
const getImage = (img) => {
  if (!img) return "https://picsum.photos/600/400"

  // nếu backend trả object
  if (img.imageUrl) {
    return "http://localhost:3000" + img.imageUrl
  }

  return img
}

const formatDate = (d) => {
  if (!d) return ""
  return new Date(d).toLocaleDateString("vi-VN")
}

// ================= FETCH =================
const fetchData = async () => {
  const roomId = route.params.id

  try {
    // ROOM
    const resRoom = await api.get(`/rooms/${roomId}`)
    room.value = resRoom.data

    // SERVICES
    const resServices = await api.get(`/rooms/${roomId}/services`)
    services.value = resServices.data
    try {
    const resContract = await api.get(`/contracts/room/${roomId}`)
    contract.value = resContract.data
    } catch {
    contract.value = null
    }
    try {
    const resPayments = await api.get(`/invoices/room/${roomId}/paid`)
    payments.value = resPayments.data
    } catch {
    payments.value = []
    }
    // ================= TENANT =================
    try {
    const resTenant = await api.get(`/contracts/room/${roomId}/tenant`)
    tenant.value = resTenant.data
    } catch {
    tenant.value = null
    }

    // METER
    const buildingId = room.value.building?.id
    if (buildingId) {
      const resMeter = await api.get(`/building-services/${buildingId}/meter`)
      meters.value = resMeter.data
    }

  } catch (err) {
    console.error(err)
  }
}

onMounted(fetchData)

// ================= UI =================
const formatPrice = (v) => Number(v || 0).toLocaleString("vi-VN")

const statusText = computed(() => {
  if (!room.value) return ""
  if (room.value.status === "EMPTY") return "Trống"
  if (room.value.status === "RENTED") return "Đang thuê"
  return "Bảo trì"
})

const statusClass = computed(() => {
  if (!room.value) return ""
  if (room.value.status === "EMPTY") return "bg-green-500 text-white"
  if (room.value.status === "RENTED") return "bg-red-500 text-white"
  return "bg-yellow-500 text-white"
})
</script>