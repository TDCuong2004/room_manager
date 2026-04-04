<template>
  <div class="modal-overlay">

    <div class="modal">

      <h2>📊 Nhập dịch vụ</h2>

      <p class="room-name">
        {{ room.roomName }}
      </p>

      <!-- MONTH -->
      <label>Tháng</label>
      <input type="month" v-model="month" />

      <!-- SERVICES -->
      <div
        v-for="s in services"
        :key="s.id"
        class="form-group"
      >

        <label>
          {{ s.serviceName }}
        </label>

        <!-- BY_METER -->
        <input
          v-if="s.calculationType === 'BY_METER'"
          type="number"
          placeholder="Số mới"
          v-model="meterValues[s.serviceId]"
        />

        <!-- BY_PERSON -->
        <input
          v-else-if="s.calculationType === 'BY_PERSON'"
          type="number"
          placeholder="Số lượng"
          v-model="personValues[s.serviceId]"
        />

        <!-- FIXED -->
        <input
          v-else
          disabled
          value="Phí cố định"
        />

      </div>

      <!-- EMPTY -->
      <p v-if="!services.length" class="empty">
        Tòa này chưa có dịch vụ
      </p>

      <!-- ACTION -->
      <div class="actions">
        <button class="save" @click="save">
          Lưu
        </button>
        <button class="cancel" @click="$emit('close')">
          Hủy
        </button>
      </div>

    </div>

  </div>
</template>

<script>
import api from "@/api"

export default {

  props:["room"],

  data(){
    return{
      services:[],
      month:"",
      meterValues:{},
      personValues:{}
    }
  },

  async mounted(){

    try{

      const res = await api.get(
        `/building-services/${this.room.building.id}`
      )

      console.log("services:", res.data)

      this.services = res.data

    }catch(err){
        console.error("ERROR:", err.response?.data || err)
        alert(err.response?.data?.message || "Lỗi server")
    }

  },

  methods:{

    async save(){

      if(!this.month){
        alert("Chọn tháng trước")
        return
      }

      const payload = []

      this.services.forEach(s => {

        const type = s.calculationType

        // BY_METER
        if(type === "BY_METER"){

          if(!this.meterValues[s.serviceId]) return

          payload.push({
            roomId:this.room.id,
            serviceId:s.serviceId,
            month:this.month,
            newValue:this.meterValues[s.serviceId]
          })
        }

        // BY_PERSON
        if(type === "BY_PERSON"){

          payload.push({
            roomId:this.room.id,
            serviceId:s.serviceId,
            month:this.month,
            quantity:this.personValues[s.serviceId] || 0
          })
        }

        // FIXED
        if(type === "FIXED"){

          payload.push({
            roomId:this.room.id,
            serviceId:s.serviceId,
            month:this.month,
            quantity:1
          })
        }

      })

      if(!payload.length){
        alert("Chưa nhập dữ liệu")
        return
      }

      try{

        await api.post("/meter-readings/bulk", payload)

        alert("Lưu thành công")
        this.$emit("saved")

      }catch(err){
        console.error("ERROR:", err.response?.data || err)
        alert(err.response?.data?.message || "Lỗi server")
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
  right:0;
  bottom:0;
  background:rgba(0,0,0,0.4);
  display:flex;
  justify-content:center;
  align-items:center;
  z-index:1000;
}

.modal{
  background:white;
  padding:25px;
  border-radius:16px;
  width:400px;
  box-shadow:0 10px 40px rgba(0,0,0,0.2);
}

.form-group{
  margin:12px 0;
  display:flex;
  flex-direction:column;
}

input{
  padding:8px;
  border-radius:8px;
  border:1px solid #ddd;
}

.actions{
  display:flex;
  justify-content:flex-end;
  gap:10px;
  margin-top:15px;
}

.save{
  background:#ff385c;
  color:white;
  border:none;
  padding:8px 16px;
  border-radius:8px;
}

.cancel{
  background:#ccc;
  border:none;
  padding:8px 16px;
  border-radius:8px;
}

.empty{
  margin-top:10px;
  color:#888;
}
</style>