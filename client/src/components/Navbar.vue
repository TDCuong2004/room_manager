<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'

const user = ref({})

const fetchProfile = async () => {
  const res = await api.get('/users/profile')
  user.value = res.data
}

onMounted(fetchProfile)
</script>

<template>
  <div class="bg-white px-6 py-3 shadow flex justify-between items-center">

    <!-- SEARCH -->
    <input
      type="text"
      placeholder="Search members..."
      class="px-4 py-2 border rounded-lg w-96"
    />

    <!-- RIGHT -->
    <div class="flex items-center gap-4">
      🔔

      <img
        :src="user.avatar || '/avatar-default.png'"
        class="w-10 h-10 rounded-full"
        @error="e => e.target.src = '/avatar-default.jpg'"
      />
    </div>

  </div>
</template>