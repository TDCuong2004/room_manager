<template>
  <div class="min-h-screen bg-[#f7f7f8] p-8">

    <!-- Header -->
    <div class="flex items-center justify-between mb-8">
      <div>
        <h2 class="text-3xl font-bold text-gray-800">
          🏢 Tòa nhà của tôi
        </h2>
        <p class="text-sm text-gray-500 mt-1">
          Quản lý danh sách bất động sản và dịch vụ đi kèm.
        </p>
      </div>

      <button
        @click="openAdd"
        class="bg-rose-600 hover:bg-rose-700 text-white px-6 py-3 rounded-xl font-semibold transition"
      >
        + Thêm tòa nhà
      </button>
    </div>

    <!-- Grid -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">

      <!-- Card -->
      <div
        v-for="b in buildings"
        :key="b.id"
        class="bg-white rounded-3xl p-5 shadow-sm hover:shadow-lg transition cursor-pointer relative"
      >
        <!-- Badge -->
        <span
          class="absolute top-4 right-4 text-[10px] px-3 py-1 rounded-full bg-green-100 text-green-700 font-semibold"
        >
          HOẠT ĐỘNG
        </span>

        <!-- Icon -->
        <div
          class="w-12 h-12 rounded-xl bg-rose-50 flex items-center justify-center mb-5"
        >
          <BuildingOffice2Icon class="w-6 h-6 text-rose-500" />
        </div>

        <!-- Info -->
        <div @click="goToRooms(b.id)">
          <h3 class="font-bold text-xl text-gray-800">
            {{ b.buildingName }}
          </h3>

          <p class="flex items-start gap-1 text-gray-500 text-sm mt-2">
            <MapPinIcon class="w-4 h-4 mt-0.5 flex-shrink-0" />
            <span>{{ b.address }}</span>
          </p>
        </div>

        <!-- Buttons -->
        <div class="mt-5 grid grid-cols-2 gap-2">

          <button
            @click.stop="openEdit(b)"
            class="flex items-center justify-center gap-1 py-2 rounded-lg bg-indigo-50 text-indigo-600 text-sm hover:bg-indigo-100"
          >
            <PencilSquareIcon class="w-4 h-4" />
            <span>Sửa</span>
          </button>

          <button
            @click.stop="confirmDelete(b.id)"
            class="flex items-center justify-center gap-1 py-2 rounded-lg bg-red-50 text-red-500 text-sm hover:bg-red-100"
          >
            <TrashIcon class="w-4 h-4" />
            <span>Xóa</span>
          </button>

        </div>

        <button
          @click.stop="goToServices(b.id)"
          class="mt-3 w-full py-2 rounded-lg bg-emerald-100 text-emerald-700 text-sm font-medium hover:bg-emerald-200"
        >
          ⚡ Dịch vụ tòa
        </button>

      </div>

      <!-- Add New -->
      <div
        @click="openAdd"
        class="border-2 border-dashed border-rose-200 rounded-3xl flex flex-col items-center justify-center min-h-[290px] cursor-pointer hover:bg-rose-50 transition"
      >
        <div
          class="w-14 h-14 rounded-full bg-gray-100 flex items-center justify-center text-2xl text-gray-500"
        >
          ➕
        </div>

        <p class="mt-5 font-semibold text-gray-700">
          Thêm địa điểm mới
        </p>

        <p class="text-sm text-gray-400 mt-2 text-center px-4">
          Mở rộng danh mục quản lý của bạn
        </p>
      </div>

    </div>

    <!-- Modal -->
    <AddBuilding
      v-if="showAdd"
      @close="showAdd = false"
      @reload="fetchBuildings"
    />

  </div>
</template>
<script>
import axios from "axios"
import AddBuilding from "@/Model/AddBuilding.vue"
import {
  BuildingOffice2Icon,
  MapPinIcon,
  PencilSquareIcon,
  TrashIcon,
  PlusIcon,
} from "@heroicons/vue/24/outline";
const api = axios.create({
  baseURL: "http://localhost:3000/api"
})

api.interceptors.request.use(config => {

  const token = localStorage.getItem("token")

  if (token) {
    config.headers.Authorization = "Bearer " + token
  }

  return config
})

export default {

  components:{
    AddBuilding,
    BuildingOffice2Icon,
    MapPinIcon,
    PencilSquareIcon,
    TrashIcon,
    PlusIcon,
  },

  data() {
    return {
      buildings: [],
      showAdd: false
    }
  },

  mounted() {

    const token = localStorage.getItem("token")

    if (!token) {
      alert("Bạn chưa đăng nhập!")
      this.$router.push("/login")
      return
    }

    this.fetchBuildings()
  },

  methods: {

    async fetchBuildings() {

      try {

        const res = await api.get("/buildings")
        this.buildings = res.data

      } catch (err) {

        console.error(err)

      }

    },

    // 🔥 mở BuildingServices trong Dashboard
    goToServices(buildingId) {

      this.$emit("openBuildingServices", buildingId)

    },

    goToRooms(buildingId) {

      this.$emit("openRooms", buildingId)

    },

    openAdd() {

      this.showAdd = true

    },

    openEdit(building) {

      console.log("Sửa", building)

    },

    async confirmDelete(id) {

      if (!confirm("Bạn có chắc muốn xóa?")) return

      try {

        await api.delete(`/buildings/${id}`)
        await this.fetchBuildings()

      } catch (err) {

        console.error(err)

      }

    }

  }

}
</script>
<style scoped>

</style>