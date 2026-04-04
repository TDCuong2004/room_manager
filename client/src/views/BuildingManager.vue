<template>
  <div class="building-manager">

    <!-- HEADER -->
    <div class="building-header">
      <h2>🏢 Tòa nhà của tôi</h2>

      <button class="primary-btn" @click="openAdd">
        + Thêm tòa nhà
      </button>
    </div>

    <!-- LIST -->
    <div v-if="buildings.length > 0" class="building-grid">

      <div
        v-for="b in buildings"
        :key="b.id"
        class="building-card"
      >

        <!-- CLICK CARD → ROOMS -->
        <div class="building-info" @click="goToRooms(b.id)">
          <h3>{{ b.buildingName }}</h3>
          <p class="address">{{ b.address }}</p>
        </div>

        <div class="actions">

          <button
            class="edit-btn"
            @click.stop="openEdit(b)"
          >
            Sửa
          </button>

          <button
            class="delete-btn"
            @click.stop="confirmDelete(b.id)"
          >
            Xóa
          </button>

          <button
            class="service-btn"
            @click.stop="goToServices(b.id)"
          >
            Dịch vụ tòa
          </button>

        </div>

      </div>

    </div>

    <div v-else class="empty-state">
      Bạn chưa có tòa nhà nào.
    </div>

    <!-- MODAL ADD -->
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
    AddBuilding
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

/* ===== BUILDING MANAGER LAYOUT ===== */
.building-manager {
  padding: 40px;
  background: #f4f6f9;
  min-height: 100vh;
}

/* ===== HEADER ===== */
.building-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.building-header h2 {
  font-size: 26px;
  font-weight: 700;
  color: #1f2937;
}

/* ===== PRIMARY BUTTON ===== */
.primary-btn {
  background: linear-gradient(135deg, #ff385c, #ff7a18);
  border: none;
  color: white;
  padding: 12px 22px;
  border-radius: 30px;
  cursor: pointer;
  font-weight: 500;
  font-size: 14px;
  transition: 0.25s ease;
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(255, 122, 24, 0.35);
}

/* ===== GRID ===== */
.building-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

/* ===== CARD ===== */
.building-card {
  background: white;
  padding: 24px;
  border-radius: 20px;
  box-shadow: 0 10px 35px rgba(0, 0, 0, 0.06);
  transition: 0.25s ease;
}

.building-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 18px 45px rgba(0, 0, 0, 0.1);
}

.building-card h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #111827;
}

.address {
  font-size: 14px;
  color: #6b7280;
  margin-top: 8px;
}

/* ===== ACTION BUTTONS ===== */
.actions {
  margin-top: 18px;
  display: flex;
  gap: 10px;
}

.edit-btn {
  background: #3b82f6;
  border: none;
  color: white;
  padding: 7px 14px;
  border-radius: 10px;
  cursor: pointer;
  font-size: 13px;
}

.edit-btn:hover {
  background: #2563eb;
}

.delete-btn {
  background: #ef4444;
  border: none;
  color: white;
  padding: 7px 14px;
  border-radius: 10px;
  cursor: pointer;
  font-size: 13px;
}

.delete-btn:hover {
  background: #dc2626;
}

/* ===== EMPTY STATE ===== */
.empty-state {
  text-align: center;
  margin-top: 60px;
  font-size: 15px;
  color: #9ca3af;
}
.service-btn {
  background: #10b981; /* xanh lá */
  border: none;
  color: white;
  padding: 7px 14px;
  border-radius: 10px;
  cursor: pointer;
  font-size: 13px;
}

.service-btn:hover {
  background: #059669;
}
</style>