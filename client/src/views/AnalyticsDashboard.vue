<template>
  <div class="min-h-screen bg-[#f6f3f2] p-8 text-[#1f1f1f]">

    <!-- HEADER -->
    <div class="mb-8">
      <h1 class="text-4xl font-black tracking-tight">
        Occupancy & Efficiency Analytics
      </h1>

      <p class="text-sm text-gray-500 mt-2">
        Phân tích chi tiết hiệu suất vận hành hệ thống phòng trọ.
      </p>
    </div>

    <!-- FILTER -->
    <div class="flex items-end gap-4 mb-8 flex-wrap">

      <div>
        <p class="text-[11px] uppercase tracking-widest text-gray-400 font-bold mb-2">
          Thời gian
        </p>

        <select
          v-model="filter"
          class="bg-white border border-gray-200 rounded-2xl px-5 py-3 min-w-[180px] text-sm font-medium outline-none"
        >
          <option value="month">
            Tháng này
          </option>

          <option value="year">
            Năm nay
          </option>
        </select>
      </div>

      <button
        @click="loadData"
        class="bg-black text-white px-7 py-3 rounded-2xl font-semibold hover:scale-[1.02] transition-all"
      >
        Áp dụng
      </button>
    </div>

    <!-- STATS -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-5 mb-8">

      <!-- EMPTY -->
      <div
        class="bg-white rounded-[28px] p-6 border border-gray-100 shadow-sm"
      >
        <p class="text-sm font-semibold text-gray-500">
          Tổng Phòng Trống
        </p>

        <h2 class="text-5xl font-black mt-4">
          {{ emptyRooms }}
        </h2>

        <p class="text-emerald-600 text-sm font-semibold mt-4">
          Phòng đang chưa được thuê
        </p>
      </div>

      <!-- RENTED -->
      <div
        class="bg-white rounded-[28px] p-6 border border-gray-100 shadow-sm"
      >
        <p class="text-sm font-semibold text-gray-500">
          Tổng Phòng Đã Thuê
        </p>

        <h2 class="text-5xl font-black mt-4">
          {{ rentedRooms }}
        </h2>

        <p class="text-emerald-600 text-sm font-semibold mt-4">
          Tổng phòng đang hoạt động
        </p>
      </div>

      <!-- OCCUPANCY -->
      <div
        class="relative overflow-hidden rounded-[28px] bg-gradient-to-br from-pink-700 via-rose-600 to-red-500 p-6 text-white shadow-xl"
      >
        <div
          class="absolute -right-8 -bottom-8 w-40 h-40 rounded-full bg-white/10"
        />

        <p class="text-sm font-semibold opacity-90">
          Hiệu Suất Lấp Đầy
        </p>

        <h2 class="text-5xl font-black mt-4">
          {{ occupancyRate }}%
        </h2>

        <div class="mt-6">
          <div class="h-2 bg-white/20 rounded-full overflow-hidden">
            <div
              class="h-full bg-white rounded-full"
              :style="{ width: occupancyRate + '%' }"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- BUILDINGS -->
    <div class="bg-white rounded-[32px] p-8 border border-gray-100">

      <div class="flex justify-between items-center mb-8">
        <div>
          <h2 class="text-2xl font-black">
            Building Performance
          </h2>

          <p class="text-sm text-gray-500 mt-2">
            Thống kê hiệu suất từng tòa nhà.
          </p>
        </div>
      </div>

      <!-- HEADER -->
      <div
        class="hidden md:grid grid-cols-12 px-6 mb-4 text-[11px] uppercase tracking-widest text-gray-400 font-bold"
      >
        <div class="col-span-5">
          Tòa nhà
        </div>

        <div class="col-span-3">
          Địa chỉ
        </div>

        <div class="col-span-2">
          Thống kê
        </div>

        <div class="col-span-2">
          Hiệu suất
        </div>
      </div>

      <!-- ROW -->
      <div
        v-for="item in buildingStats"
        :key="item.id"
        class="bg-[#faf8f7] rounded-3xl p-5 mb-4 hover:bg-white border border-transparent hover:border-gray-200 transition-all"
      >
        <div class="grid md:grid-cols-12 gap-5 items-center">

          <!-- INFO -->
          <div class="md:col-span-5 flex items-center gap-4">

            <img
              :src="item.image"
              class="w-16 h-16 rounded-2xl object-cover"
              @error="onImageError"
            />

            <div>
              <h3 class="font-black text-[15px]">
                {{ item.name }}
              </h3>

              <p class="text-xs text-gray-400 mt-1">
                ID: {{ item.code }}
              </p>
            </div>
          </div>

          <!-- ADDRESS -->
          <div class="md:col-span-3">
            <p class="font-semibold text-sm">
              {{ item.address }}
            </p>
          </div>

          <!-- STATS -->
          <div class="md:col-span-2 flex gap-6">

            <div>
              <p class="text-[11px] uppercase text-gray-400 font-bold">
                Thuê
              </p>

              <p class="font-black text-lg mt-1">
                {{ item.rented }}
              </p>
            </div>

            <div>
              <p class="text-[11px] uppercase text-gray-400 font-bold">
                Trống
              </p>

              <p class="font-black text-lg text-rose-500 mt-1">
                {{ item.empty }}
              </p>
            </div>
          </div>

          <!-- PERFORMANCE -->
          <div class="md:col-span-2">

            <div class="flex justify-between mb-2">
              <span class="font-black text-sm">
                {{ item.performance }}%
              </span>
            </div>

            <div class="h-2 bg-gray-200 rounded-full overflow-hidden">
              <div
                class="h-full rounded-full"
                :class="{
                  'bg-emerald-500': item.performance >= 90,
                  'bg-rose-500': item.performance >= 70 && item.performance < 90,
                  'bg-red-400': item.performance < 70
                }"
                :style="{ width: item.performance + '%' }"
              />
            </div>

          </div>

        </div>
      </div>

    </div>

  </div>
</template>

<script setup>
import axios from "axios"
import { computed, onMounted, ref } from "vue"

const API = "http://localhost:3000/api"

const buildings = ref([])
const rooms = ref([])

const loading = ref(false)

const filter = ref("month")

const loadData = async () => {
  try {

    loading.value = true

    const [buildingRes, roomRes] = await Promise.all([
      axios.get(`${API}/buildings`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`
        }
      }),

      axios.get(`${API}/rooms`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`
        }
      })
    ])

    buildings.value = buildingRes.data
    rooms.value = roomRes.data

  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})

const rentedRooms = computed(() => {
  return rooms.value.filter(r => r.status === "RENTED").length
})

const emptyRooms = computed(() => {
  return rooms.value.filter(r => r.status === "EMPTY").length
})

const occupancyRate = computed(() => {

  if (!rooms.value.length) return 0

  return Math.round(
    (rentedRooms.value / rooms.value.length) * 100
  )
})

const buildingStats = computed(() => {

  return buildings.value.map(building => {

    const buildingRooms = rooms.value.filter(
      r => r.building?.id === building.id
    )

    const rented = buildingRooms.filter(
      r => r.status === "RENTED"
    ).length

    const empty = buildingRooms.filter(
      r => r.status === "EMPTY"
    ).length

    const performance = buildingRooms.length
      ? Math.round((rented / buildingRooms.length) * 100)
      : 0

    return {
      id: building.id,
      name: building.buildingName,
      code: building.buildingCode || "N/A",
      address: building.address,
      rented,
      empty,
      performance,

      image:
        "https://images.unsplash.com/photo-1494526585095-c41746248156?q=80&w=400"
    }
  })
})

const onImageError = (e) => {
  e.target.src =
    "https://cdn-icons-png.flaticon.com/512/1046/1046857.png"
}
</script>