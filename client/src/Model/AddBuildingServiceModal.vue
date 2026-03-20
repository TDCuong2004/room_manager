<template>

<div class="modal-overlay" @click.self="close">

  <div class="modal">

    <h3>Thêm dịch vụ</h3>

    <label>Dịch vụ</label>

    <select v-model="form.serviceId">
      <option disabled value="">Chọn dịch vụ</option>

      <option
        v-for="s in availableServices"
        :key="s.id"
        :value="s.id"
      >
        {{ s.serviceName }}
      </option>
    </select>

    <label>Giá</label>

    <input
      type="number"
      v-model="form.price"
      placeholder="Nhập giá"
    />

    <div class="actions">

      <button class="save-btn" @click="save">
        Lưu
      </button>

      <button class="cancel-btn" @click="close">
        Hủy
      </button>

    </div>

  </div>

</div>

</template>

<script>
import api from "@/api"

export default {

  props:{
    buildingId:Number,
    services:{
      type:Array,
      default:()=>[]
    },
    buildingServices:{
      type:Array,
      default:()=>[]
    }
  },

  data(){
    return{
      form:{
        serviceId:"",
        price:""
      }
    }
  },

  computed:{

    availableServices(){

      const usedIds = this.buildingServices.map(
        s => s.service?.id
      )

      return this.services.filter(
        s => !usedIds.includes(s.id)
      )

    }

  },

  methods:{

    close(){
      this.$emit("close")
    },

    async save(){

      try{

        if(!this.form.serviceId){
          alert("Vui lòng chọn dịch vụ")
          return
        }

        if(!this.form.price){
          alert("Vui lòng nhập giá")
          return
        }

        await api.post(
          "/building-services",
          {
            building:{
              id:this.buildingId
            },
            service:{
              id:this.form.serviceId
            },
            price:this.form.price,
            isActive:true
          }
        )

        alert("Thêm dịch vụ thành công")

        this.form.serviceId=""
        this.form.price=""

        this.$emit("saved")

      }catch(err){

        console.error(err)

        if(err.response){
          alert("Lỗi: " + err.response.status)
        }else{
          alert("Không kết nối được server")
        }

      }

    }

  }

}
</script>

<style scoped>

.modal-overlay{
  position:fixed;
  top:0;
  left:0;
  width:100%;
  height:100%;
  background:rgba(0,0,0,0.4);
  display:flex;
  justify-content:center;
  align-items:center;
}

.modal{
  background:white;
  padding:25px;
  border-radius:10px;
  width:320px;
}

.modal h3{
  margin-bottom:15px;
}

select,input{
  width:100%;
  padding:8px;
  margin-bottom:12px;
}

.actions{
  display:flex;
  justify-content:space-between;
}

.save-btn{
  background:#4CAF50;
  color:white;
  border:none;
  padding:8px 14px;
  cursor:pointer;
}

.cancel-btn{
  background:#f44336;
  color:white;
  border:none;
  padding:8px 14px;
  cursor:pointer;
}

</style>