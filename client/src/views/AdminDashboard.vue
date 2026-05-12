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
    <div class="mb-6">

      <!-- BIỂU ĐỒ -->
      <div class="bg-white p-6 rounded-xl shadow">
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