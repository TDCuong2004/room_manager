<template>
  <div class="service-page">

    <div class="header">

      <button class="back-btn" @click="$emit('backToBuildings')">
        ← Quay lại
      </button>

      <h2>🔧 Dịch vụ của tòa</h2>

      <button class="add-btn" @click="openAdd">
        + Thêm dịch vụ
      </button>

    </div>

    <table class="service-table">

      <thead>
        <tr>
          <th>Dịch vụ</th>
          <th>Giá</th>
          <th></th>
        </tr>
      </thead>

      <tbody>

        <tr
          v-for="s in buildingServices"
          :key="s.id"
        >

          <td>{{ s.service?.serviceName }}</td>

          <td>{{ formatMoney(s.price) }} đ</td>

          <td>

            <button
              class="delete-btn"
              @click="deleteService(s.id)"
            >
              Xóa
            </button>

          </td>

        </tr>

      </tbody>

    </table>

    <div v-if="!buildingServices.length" class="empty">
      Chưa có dịch vụ nào
    </div>

    <AddBuildingServiceModal
      v-if="showModal"
      :buildingId="buildingId"
      :services="services"
      :buildingServices="buildingServices"
      @close="showModal=false"
      @saved="reload"
    />

  </div>
</template>

<script>
import api from "@/api"
import AddBuildingServiceModal from "@/Model/AddBuildingServiceModal.vue"

export default {

  props:["buildingId"],

  components:{
    AddBuildingServiceModal
  },

  data(){
    return{
      services:[],
      buildingServices:[],
      showModal:false
    }
  },

  mounted(){

    this.loadServices()

    if(this.buildingId){
      this.loadBuildingServices()
    }

  },

  watch:{
    buildingId(){
      this.loadBuildingServices()
    }
  },

  methods:{

    async loadServices(){

      try{

        const res = await api.get("/services")

        this.services = res.data

      }catch(err){
        console.error(err)
      }

    },

    async loadBuildingServices(){

      try{

        const res = await api.get(
          `/building-services/${this.buildingId}`
        )

        this.buildingServices = res.data

      }catch(err){
        console.error(err)
      }

    },

    openAdd(){
      this.showModal = true
    },

    reload(){
      this.loadBuildingServices()
      this.showModal=false
    },

    async deleteService(id){

      if(!confirm("Xóa dịch vụ này?")) return

      try{

        await api.delete(`/building-services/${id}`)

        this.loadBuildingServices()

      }catch(err){
        console.error(err)
      }

    },

    formatMoney(value){
      return new Intl.NumberFormat("vi-VN").format(value)
    }

  }

}
</script>

<style scoped>

.service-page{
  padding:40px;
}

.header{
  display:flex;
  align-items:center;
  gap:20px;
  margin-bottom:20px;
}

.add-btn{
  margin-left:auto;
  background:#10b981;
  border:none;
  color:white;
  padding:10px 16px;
  border-radius:8px;
  cursor:pointer;
}

.back-btn{
  background:#6b7280;
  color:white;
  border:none;
  padding:8px 14px;
  border-radius:6px;
  cursor:pointer;
}

.service-table{
  width:450px;
  border-collapse:collapse;
}

.service-table th,
.service-table td{
  padding:10px;
  border-bottom:1px solid #ddd;
}

.delete-btn{
  background:#ef4444;
  border:none;
  color:white;
  padding:6px 12px;
  border-radius:6px;
  cursor:pointer;
}

.empty{
  margin-top:20px;
  color:#888;
}

</style>