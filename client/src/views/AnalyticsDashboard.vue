<template>
  <div class="min-h-screen bg-[#f5f5f7] p-5 text-[#111827]">

    <div class="max-w-[1400px] mx-auto">

      <!-- HEADER -->
      <div class="flex items-start justify-between mb-6">
        <div>
          <h1 class="text-[34px] font-black leading-none">
            Dashboard
            <span class="text-rose-500">Phân tích</span>
          </h1>

          <p class="text-gray-500 mt-2 text-sm">
            Hiệu suất vận hành và thống kê doanh thu hệ thống phòng trọ.
          </p>
        </div>

        <div class="text-right">
          <p class="uppercase tracking-[2px] text-[9px] text-gray-400 font-bold">
            Cập nhật lần cuối
          </p>

          <p class="font-semibold mt-1 text-xs">
            {{ currentTime }}
          </p>
        </div>
      </div>

      <!-- TOP -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-4 mb-8">

        <!-- EMPTY -->
        <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
          <p class="uppercase tracking-[2px] text-[10px] text-gray-400 font-bold">
            Tổng phòng trống
          </p>

          <div class="flex items-end gap-2 mt-4">
            <h2 class="text-5xl font-black leading-none">
              {{ emptyRooms }}
            </h2>

            <span class="text-rose-500 text-lg mb-1">
              Phòng
            </span>
          </div>

          <p class="text-emerald-600 mt-4 text-xs font-medium">
            ✓ Phòng đang chưa được thuê
          </p>
        </div>

        <!-- RENTED -->
        <div class="bg-white rounded-2xl p-5 border border-gray-100 shadow-sm">
          <p class="uppercase tracking-[2px] text-[10px] text-gray-400 font-bold">
            Tổng phòng đã thuê
          </p>

          <div class="flex items-end gap-2 mt-4">
            <h2 class="text-5xl font-black leading-none">
              {{ rentedRooms }}
            </h2>

            <span class="text-rose-500 text-lg mb-1">
              Phòng
            </span>
          </div>

          <p class="text-rose-500 mt-4 text-xs font-medium">
            Tổng phòng đang hoạt động
          </p>
        </div>

        <!-- OCCUPANCY -->
        <div
          class="relative overflow-hidden rounded-2xl bg-gradient-to-br from-pink-600 via-rose-500 to-red-500 p-5 text-white shadow-lg"
        >

          <div
            class="absolute -right-10 -bottom-10 w-36 h-36 rounded-full bg-white/10"
          />

          <p class="uppercase tracking-[2px] text-[10px] font-bold opacity-80">
            Hiệu suất lấp đầy
          </p>

          <h2 class="text-[58px] font-black leading-none mt-3">
            {{ occupancyRate }}%
          </h2>

          <div class="mt-5">
            <div class="h-2 bg-black/20 rounded-full overflow-hidden">
              <div
                class="h-full bg-white rounded-full"
                :style="{ width: occupancyRate + '%' }"
              />
            </div>
          </div>

        </div>
      </div>

      <!-- BUILDING -->
      <div>

        <div class="flex items-center gap-4 mb-5">
          <h2 class="text-[30px] font-black whitespace-nowrap">
            Hiệu suất từng tòa nhà
          </h2>

          <div class="flex-1 h-[1px] bg-gray-300"></div>
        </div>

        <div class="bg-white rounded-3xl border border-gray-100 overflow-hidden">

          <!-- HEAD -->
          <div
            class="grid grid-cols-12 px-6 py-4 border-b border-gray-100 text-[9px] uppercase tracking-[2px] text-gray-400 font-bold"
          >
            <div class="col-span-4">Tòa nhà</div>
            <div class="col-span-4">Địa chỉ</div>
            <div class="col-span-2">Thống kê</div>
            <div class="col-span-2">Hiệu suất</div>
          </div>

          <!-- ROW -->
          <div
            v-for="item in buildingStats"
            :key="item.id"
            class="grid grid-cols-12 items-center px-6 py-5 border-b border-gray-50 hover:bg-[#fafafa]"
          >

            <div class="col-span-4 flex items-center gap-4">

              <img
                :src="item.image"
                class="w-14 h-14 rounded-2xl object-cover"
                @error="onImageError"
              />

              <div>
                <h3 class="font-black text-base">
                  {{ item.name }}
                </h3>

                <p class="text-gray-400 text-xs mt-1">
                  ID: {{ item.code }}
                </p>
              </div>

            </div>

            <div class="col-span-4">
              <p class="text-gray-600 text-sm leading-relaxed">
                {{ item.address }}
              </p>
            </div>

            <div class="col-span-2 flex gap-8">

              <div>
                <p class="uppercase text-[9px] tracking-[2px] text-gray-400 font-bold">
                  Thuê
                </p>

                <p class="text-xl font-black mt-1">
                  {{ item.rented }}
                </p>
              </div>

              <div>
                <p class="uppercase text-[9px] tracking-[2px] text-gray-400 font-bold">
                  Trống
                </p>

                <p class="text-xl font-black text-rose-500 mt-1">
                  {{ item.empty }}
                </p>
              </div>

            </div>

            <div class="col-span-2">

              <span
                :class="[
                  'font-black text-base',
                  item.performance >= 90
                    ? 'text-emerald-500'
                    : 'text-rose-500'
                ]"
              >
                {{ item.performance }}%
              </span>

              <div class="h-2 bg-gray-100 rounded-full overflow-hidden mt-2">

                <div
                  class="h-full rounded-full"
                  :class="[
                    item.performance >= 90
                      ? 'bg-emerald-500'
                      : 'bg-rose-500'
                  ]"
                  :style="{ width: item.performance + '%' }"
                />

              </div>

            </div>

          </div>

        </div>

      </div>

      <!-- REVENUE -->
      <div class="mb-8">

        <div class="flex items-center gap-4 mb-5">
          <h2 class="text-[30px] font-black whitespace-nowrap">
            Thống kê doanh thu
          </h2>

          <div class="flex-1 h-[1px] bg-gray-300"></div>
        </div>

        <!-- FILTER -->
        <div class="flex gap-4 flex-wrap mb-5">

          <div>
            <p class="uppercase text-[9px] tracking-[2px] text-gray-400 font-bold mb-2">
              Chọn tòa nhà
            </p>

            <select
              v-model="selectedBuilding"
              class="bg-white border border-gray-200 rounded-xl px-4 py-2 min-w-[200px] outline-none text-sm"
            >
              <option value="">
                Tất cả tòa nhà
              </option>

              <option
                v-for="b in buildings"
                :key="b.id"
                :value="b.id"
              >
                {{ b.buildingName }}
              </option>
            </select>
          </div>

          <div>
            <p class="uppercase text-[9px] tracking-[2px] text-gray-400 font-bold mb-2">
              Chọn năm
            </p>

            <select
              v-model="selectedYear"
              class="bg-white border border-gray-200 rounded-xl px-4 py-2 min-w-[110px] outline-none text-sm"
            >
              <option
                v-for="year in years"
                :key="year"
                :value="year"
              >
                {{ year }}
              </option>
            </select>
          </div>

        </div>

        <!-- CONTENT -->
        <div class="grid grid-cols-1 xl:grid-cols-3 gap-4">

          <!-- LEFT -->
          <div class="space-y-4">

            <div
              class="rounded-2xl p-5 bg-gradient-to-br from-black via-[#111] to-[#1f1f1f] text-white"
            >
              <p class="uppercase tracking-[2px] text-[9px] text-gray-400 font-bold">
                Tổng doanh thu
              </p>

              <h3 class="text-[30px] font-black mt-3">
                {{ formatMoney(revenue.totalRevenue) }}đ
              </h3>
            </div>

            <div class="bg-white rounded-2xl p-5 border border-gray-100">
              <div class="flex justify-between items-center">

                <div>
                  <p class="uppercase tracking-[2px] text-[9px] text-gray-400 font-bold">
                    Tiền phòng
                  </p>

                  <h3 class="text-[30px] font-black mt-3">
                    {{ formatMoney(revenue.rentRevenue) }}đ
                  </h3>
                </div>

              </div>
            </div>

            <div class="bg-white rounded-2xl p-5 border border-gray-100">
              <div class="flex justify-between items-center">

                <div>
                  <p class="uppercase tracking-[2px] text-[9px] text-gray-400 font-bold">
                    Dịch Vụ
                  </p>

                  <h3 class="text-[30px] font-black mt-3">
                    {{ formatMoney(revenue.serviceRevenue) }}đ
                  </h3>
                </div>

              </div>
            </div>

          </div>

          <!-- CHART -->
          <div class="xl:col-span-2 bg-white rounded-2xl p-5 border border-gray-100">

            <div class="flex items-center justify-between mb-8">

              <h3 class="text-xl font-black">
                Biểu đồ doanh thu
              </h3>

            </div>

            <!-- BAR -->
            <div class="h-[220px] flex items-end justify-between gap-3">

              <div
                v-for="(item, i) in chartData"
                :key="i"
                class="flex flex-col items-center justify-end flex-1 h-full"
              >

                <div
                  class="w-full max-w-[40px] rounded-t-xl bg-gradient-to-t from-rose-500 to-pink-300 transition-all duration-300 hover:opacity-80"
                  :style="{
                    height: item.value + '%',
                    minHeight: item.realValue > 0 ? '12px' : '0px'
                  }"
                />

                <p class="mt-2 text-[10px] text-gray-500 font-bold">
                  {{ item.month }}
                </p>

                <p class="text-[9px] text-gray-400">
                  {{ formatMoney(item.realValue) }}
                </p>

              </div>

            </div>

          </div>

        </div>

      </div>

    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue"

import api from "@/api"
const monthlyRevenue = ref([])
const buildings = ref([])
const rooms = ref([])
const revenue = ref({
  totalRevenue: 0,
  rentRevenue: 0,
  serviceRevenue: 0
})
const selectedBuilding = ref("")
const currentYear = new Date().getFullYear()

const selectedYear = ref(currentYear)

const years = [
  currentYear - 2,
  currentYear - 1,
  currentYear
]
const currentTime = new Date().toLocaleString("vi-VN", {
  hour: "2-digit",
  minute: "2-digit",
  day: "numeric",
  month: "long"
})

const loadData = async () => {
  try {

    const [buildingRes, roomRes, revenueRes, monthlyRevenueRes] =
      await Promise.all([
        api.get("/buildings"),
        api.get("/rooms/my"),
        api.get("/dashboard/revenue"),
        api.get("/dashboard/monthly-revenue")
      ])

    buildings.value = buildingRes.data
    rooms.value = roomRes.data
    revenue.value = revenueRes.data
    monthlyRevenue.value = monthlyRevenueRes.data

  } catch (err) {
    console.error(err)
  }
}

onMounted(() => {
  loadData()
})

/* =========================
   ROOM STATS
========================= */

const rentedRooms = computed(() => {
  return rooms.value.filter(
    r => r.status === "RENTED"
  ).length
})

const emptyRooms = computed(() => {
  return rooms.value.filter(
    r => r.status === "EMPTY"
  ).length
})

const occupancyRate = computed(() => {

  if (!rooms.value.length) return 0

  return Math.round(
    (rentedRooms.value / rooms.value.length) * 100
  )
})

/* =========================
   BUILDING STATS
========================= */

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
      code: building.buildingCode || `B${building.id}`,
      address: building.address,
      rented,
      empty,
      performance,

      image:
        building.imageUrl ||
        "https://images.unsplash.com/photo-1494526585095-c41746248156?q=80&w=400"
    }
  })
})

/* =========================
   REVENUE
========================= */

const filteredRooms = computed(() => {

  if (!selectedBuilding.value) {
    return rooms.value
  }

  return rooms.value.filter(
    r => r.building?.id === selectedBuilding.value
  )
})

const chartData = computed(() => {

  const data = Array(12).fill(0)

  monthlyRevenue.value.forEach(item => {

    if (!item.month) return

    const [year, month] = item.month.split("-")

    if (Number(year) !== Number(selectedYear.value)) {
      return
    }

    data[Number(month) - 1] = Number(item.revenue)
  })

  const max = Math.max(...data, 1)

  return data.map((value, index) => ({
    month: `T${index + 1}`,
    realValue: value,

    value:
      value > 0
        ? (value / max) * 100
        : 4
  }))
})


const formatMoney = (value) => {
  return new Intl.NumberFormat("vi-VN").format(value || 0)
}

const onImageError = (e) => {
  e.target.src =
    "https://cdn-icons-png.flaticon.com/512/1046/1046857.png"
}
</script>