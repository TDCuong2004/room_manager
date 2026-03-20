<template>
  <div class="services-page">

    <!-- HEADER -->
    <div class="services-header">

      <h2>🛠 Quản lý dịch vụ</h2>

      <button class="primary-btn" @click="openAdd">
        + Thêm dịch vụ
      </button>

    </div>

    <!-- LIST -->
    <div v-if="services.length" class="services-grid">

      <div
        v-for="s in services"
        :key="s.id"
        class="service-card"
      >

        <h3>{{ s.serviceName }}</h3>

        <p v-if="s.unit">
          Đơn vị: {{ s.unit }}
        </p>

        <p>
          Loại: {{ formatType(s.calculationType) }}
        </p>

        <div class="actions">

          <button
            class="edit-btn"
            @click="openEdit(s)"
          >
            Sửa
          </button>

          <button
            class="delete-btn"
            @click="deleteService(s.id)"
          >
            Xóa
          </button>

        </div>

      </div>

    </div>

    <div v-else class="empty-state">
      Chưa có dịch vụ nào.
    </div>

    <!-- ADD / EDIT MODAL -->
    <AddServiceModal
      v-if="showModal"
      :service="editingService"
      @close="closeModal"
      @reload="reload"
    />

  </div>
</template>

<script>
import axios from "axios"
import AddServiceModal from "@/Model/AddServiceModal.vue"

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
    AddServiceModal
  },

  data() {
    return {

      services: [],
      showModal: false,
      editingService: null

    }
  },

  mounted() {

    this.fetchServices()

  },

  methods: {

    async fetchServices(){

      try{

        const res = await api.get("/services")
        this.services = res.data

      }catch(err){

        console.error(err)

      }

    },

    openAdd(){

      this.editingService = null
      this.showModal = true

    },

    openEdit(service){

      this.editingService = service
      this.showModal = true

    },

    async deleteService(id){

      if(!confirm("Xóa dịch vụ này?")) return

      try{

        await api.delete(`/services/${id}`)
        this.fetchServices()

      }catch(err){

        console.error(err)

      }

    },

    closeModal(){

      this.showModal = false

    },

    reload(){

      this.fetchServices()
      this.closeModal()

    },
    formatType(type){

      if(type === "BY_METER") return "Theo đồng hồ"
      if(type === "BY_PERSON") return "Theo người"
      if(type === "FIXED") return "Cố định"

      return type

    },
    formatMoney(value){

      return new Intl.NumberFormat("vi-VN").format(value)

    }

  }

}
</script>

<style scoped>

.services-page{
  padding:40px;
  background:#f4f6f9;
  min-height:100vh;
}

.services-header{
  display:flex;
  justify-content:space-between;
  align-items:center;
  margin-bottom:35px;
}

.services-header h2{
  font-size:26px;
  font-weight:700;
}

.primary-btn{
  background:linear-gradient(135deg,#ff385c,#ff7a18);
  border:none;
  color:white;
  padding:10px 18px;
  border-radius:25px;
  cursor:pointer;
}

.services-grid{
  display:grid;
  grid-template-columns:repeat(auto-fill,minmax(240px,1fr));
  gap:20px;
}

.service-card{
  background:white;
  padding:22px;
  border-radius:18px;
  box-shadow:0 10px 30px rgba(0,0,0,0.05);
  transition:0.25s;
}

.service-card:hover{
  transform:translateY(-5px);
}

.service-card h3{
  margin:0;
  font-size:18px;
  font-weight:600;
}

.actions{
  margin-top:15px;
  display:flex;
  gap:10px;
}

.edit-btn{
  background:#3b82f6;
  color:white;
  border:none;
  padding:6px 12px;
  border-radius:8px;
  cursor:pointer;
}

.delete-btn{
  background:#ef4444;
  color:white;
  border:none;
  padding:6px 12px;
  border-radius:8px;
  cursor:pointer;
}

.empty-state{
  text-align:center;
  margin-top:80px;
  color:#888;
}

</style>