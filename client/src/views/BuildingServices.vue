<template>
  <div class="service-page">

    <!-- HEADER -->
    <div class="header">
      <button class="back-btn" @click="$emit('backToBuildings')">
        ← Quay lại
      </button>

      <h2>🔧 Dịch vụ của tòa</h2>

      <button class="add-btn" @click="openAdd">
        + Thêm dịch vụ
      </button>
    </div>

    <!-- TABLE -->
    <div class="table-wrapper">
      <table class="service-table">

        <thead>
          <tr>
            <th>Dịch vụ</th>
            <th>Giá</th>
            <th>Trạng thái</th>
            <th></th>
          </tr>
        </thead>

        <tbody>

          <tr v-for="s in buildingServices" :key="s.id">

            <td>
              <div class="service-name">
                {{ s.serviceName }}
                <span class="unit">({{ s.unit || '-' }})</span>
              </div>
            </td>

            <td>{{ formatMoney(s.price) }} đ</td>

            <td>
              <span class="badge active">Đang dùng</span>
            </td>

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
        🚫 Chưa có dịch vụ nào
      </div>
    </div>

    <!-- MODAL -->
    <AddBuildingServiceModal
      v-if="showModal"
      :buildingId="buildingId"
      :services="enhancedServices"
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

  computed:{

    enhancedServices(){

      const usedIds = this.buildingServices.map(s => s.serviceId)

      return this.services.map(s => ({
        ...s,
        disabled: usedIds.includes(s.id),
        label: `${s.serviceName} (${s.unit || '-'})`
      }))

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
        const res = await api.get(`/building-services/${this.buildingId}`)
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
  padding:30px;
}

.header{
  display:flex;
  align-items:center;
  gap:15px;
  margin-bottom:25px;
}

.header h2{
  font-size:20px;
}

.add-btn{
  margin-left:auto;
  background:linear-gradient(135deg,#10b981,#059669);
  border:none;
  color:white;
  padding:10px 18px;
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

.table-wrapper{
  background:white;
  border-radius:12px;
  padding:15px;
  box-shadow:0 5px 20px rgba(0,0,0,0.08);
}

.service-table{
  width:100%;
  border-collapse:collapse;
}

.service-table th{
  text-align:left;
  padding:12px;
  font-size:14px;
  color:#6b7280;
}

.service-table td{
  padding:12px;
  border-top:1px solid #eee;
}

.service-name{
  font-weight:600;
}

.unit{
  font-size:12px;
  color:#9ca3af;
  margin-left:5px;
}

.badge{
  padding:4px 10px;
  border-radius:20px;
  font-size:12px;
}

.badge.active{
  background:#dcfce7;
  color:#16a34a;
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
  text-align:center;
  padding:20px;
  color:#9ca3af;
}

</style>