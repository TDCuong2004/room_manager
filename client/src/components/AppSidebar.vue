<template>
  <div class="bg-white rounded-2xl border border-gray-200 p-5 shadow-sm sticky top-20">
    <h3 class="font-bold text-gray-900 mb-4 flex items-center gap-2">
      <span class="text-xl">🔍</span> Bộ lọc tìm kiếm
    </h3>

    <div class="flex flex-col gap-4">
      <div class="flex flex-col gap-1.5">
        <label class="text-xs font-black text-gray-500 uppercase tracking-wider ml-1">Địa điểm</label>
        <input 
          v-model="search.location" 
          placeholder="Nhập khu vực..." 
          class="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all text-sm"
        />
      </div>

      <div class="flex flex-col gap-1.5">
        <label class="text-xs font-black text-gray-500 uppercase tracking-wider ml-1">Giá tối đa</label>
        <div class="relative">
          <input 
            v-model="search.maxPrice" 
            type="number" 
            placeholder="VNĐ" 
            class="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all text-sm pr-12"
          />
          <span class="absolute right-4 top-3.5 text-gray-400 text-xs font-bold">VNĐ</span>
        </div>
      </div>

      <div class="flex flex-col gap-1.5">
        <label class="text-xs font-black text-gray-500 uppercase tracking-wider ml-1">Diện tích tối thiểu</label>
        <div class="relative">
          <input 
            v-model="search.area" 
            type="number" 
            placeholder="m²" 
            class="w-full p-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:ring-2 focus:ring-blue-500 focus:bg-white transition-all text-sm pr-10"
          />
          <span class="absolute right-4 top-3.5 text-gray-400 text-xs font-bold">m²</span>
        </div>
      </div>

      <button 
        @click="handleSearch"
        class="mt-2 w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-xl shadow-lg shadow-blue-100 transition-all active:scale-95 flex items-center justify-center gap-2"
      >
        <span>Tìm kiếm ngay</span>
      </button>
      
      <button 
        @click="resetSearch"
        class="w-full py-2 text-gray-400 hover:text-gray-600 text-xs font-semibold transition-colors"
      >
        Xóa bộ lọc
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const search = ref({
  location: '',
  maxPrice: null,
  area: null
})

const handleSearch = () => {
  console.log('Đang tìm kiếm:', search.value)
  // Logic emit hoặc gửi lên store/router ở đây
}

const resetSearch = () => {
  search.value = { location: '', maxPrice: null, area: null }
}
</script>