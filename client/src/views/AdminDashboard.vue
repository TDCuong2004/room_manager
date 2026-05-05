<template>
  <div class="p-6 bg-gray-100 min-h-screen">

    <!-- HEADER -->
    <h1 class="text-2xl font-bold mb-1">
      Thống kê hệ thống
    </h1>
    <p class="text-gray-400 mb-6">
      Tổng quan hoạt động và tăng trưởng của nền tảng.
    </p>

    <!-- KPI -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">

      <div class="bg-white p-5 rounded-xl shadow-sm">
        <p class="text-gray-400 text-sm">Tổng chủ trọ</p>
        <h2 class="text-2xl font-bold mt-2">{{ data.totalUsers }}</h2>
      </div>

      <div class="bg-white p-5 rounded-xl shadow-sm">
        <p class="text-gray-400 text-sm">Tổng tòa nhà</p>
        <h2 class="text-2xl font-bold mt-2">{{ data.totalBuildings }}</h2>
      </div>

      <div class="bg-white p-5 rounded-xl shadow-sm">
        <p class="text-gray-400 text-sm">Tổng phòng</p>
        <h2 class="text-2xl font-bold mt-2">{{ data.totalRooms }}</h2>
      </div>

      <div class="bg-white p-5 rounded-xl shadow-sm">
        <p class="text-gray-400 text-sm">Tổng bài đăng</p>
        <h2 class="text-2xl font-bold mt-2">{{ data.totalPosts }}</h2>
      </div>

    </div>

    <!-- CHART + RIGHT -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">

      <!-- BIỂU ĐỒ -->
      <div class="bg-white p-6 rounded-xl shadow col-span-2">
        <div class="flex justify-between mb-4">
          <h3 class="font-semibold">Doanh thu hệ thống</h3>
          <button class="text-red-500 text-sm">Xuất báo cáo</button>
        </div>

        <div class="flex items-end gap-6 h-56">
          <div
            v-for="m in data.monthlyRevenue"
            :key="m.month"
            class="flex flex-col items-center w-full"
          >
            <div
              class="bg-red-500 w-3 rounded"
              :style="{ height: calcHeight(m.revenue) }"
            ></div>

            <span class="text-xs mt-2 text-gray-400">
              {{ m.month }}
            </span>

            <span class="text-[10px] text-gray-300">
              {{ format(m.revenue) }} đ
            </span>
          </div>
        </div>
      </div>

      <!-- PHÂN LOẠI -->
      <div class="bg-white p-6 rounded-xl shadow">
        <h3 class="font-semibold mb-4">Phân loại phòng</h3>

        <div class="space-y-4 text-sm">

          <div v-for="p in portfolio" :key="p.name">
            <div class="flex justify-between mb-1">
              <span>{{ p.name }}</span>
              <span>{{ p.value }}%</span>
            </div>

            <div class="h-2 bg-gray-200 rounded">
              <div
                class="h-2 rounded"
                :class="p.color"
                :style="{ width: p.value + '%' }"
              ></div>
            </div>
          </div>

        </div>
      </div>

    </div>

    <!-- INSIGHT -->
    <div class="bg-white p-6 rounded-xl shadow">

      <h3 class="font-semibold mb-2">
        Thống kê tăng trưởng
      </h3>

      <p class="text-gray-600 text-sm mb-4">
        Hệ thống đang phát triển ổn định với số lượng chủ trọ và bài đăng tăng lên.
      </p>

      <div class="flex gap-6 flex-wrap">

        <div class="bg-gray-50 p-4 rounded-lg">
          <p class="text-gray-400 text-sm">Chủ trọ mới</p>
          <h2 class="text-xl font-bold mt-1">
            +{{ data.newUsers }}
          </h2>
        </div>

        <div class="bg-gray-50 p-4 rounded-lg">
          <p class="text-gray-400 text-sm">Bài đăng hoạt động</p>
          <h2 class="text-xl font-bold mt-1">
            {{ data.activePosts }}
          </h2>
        </div>

      </div>

    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'

// ================= STATE =================
const data = ref({
  totalUsers: 0,
  totalBuildings: 0,
  totalRooms: 0,
  totalPosts: 0,
  monthlyRevenue: [],
  newUsers: 0,
  activePosts: 0
})

// demo portfolio (có thể lấy BE sau)
const portfolio = ref([
  { name: 'Luxury Suites', value: 42, color: 'bg-red-500' },
  { name: 'Urban Studios', value: 35, color: 'bg-pink-400' },
  { name: 'Shared Rooms', value: 15, color: 'bg-green-400' },
  { name: 'Penthouse', value: 8, color: 'bg-black' }
])

// ================= FETCH =================
const fetchDashboard = async () => {
  try {
    const res = await api.get('/admin/dashboard')

    // 👉 MAP CHUẨN DATA TỪ BE
    data.value = {
      totalUsers: res.data.totalUsers || 0,
      totalBuildings: res.data.totalBuildings || 0,
      totalRooms: res.data.totalRooms || 0,
      totalPosts: res.data.totalPosts || 0,
      monthlyRevenue: res.data.monthlyRevenue || [],
      newUsers: res.data.newUsers || 0,
      activePosts: res.data.activePosts || 0
    }

  } catch (err) {
    console.error("Dashboard error:", err)
  }
}

// ================= HELPER =================
const format = (n) => {
  if (!n) return 0
  return new Intl.NumberFormat('vi-VN').format(n)
}

// scale chiều cao chart
const calcHeight = (value) => {
  if (!value) return '0px'

  // lấy max để scale đẹp
  const max = Math.max(...data.value.monthlyRevenue.map(m => m.revenue || 0))

  return (value / max * 180) + 'px'
}

// ================= INIT =================
onMounted(fetchDashboard)
</script>