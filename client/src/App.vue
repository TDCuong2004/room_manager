
<template>
  <div class="min-h-screen bg-gray-50 flex flex-col font-sans">
    <AppHeader class="sticky top-0 z-40 bg-white border-b border-gray-200" />

    <div class="flex flex-1 max-w-[1400px] w-full mx-auto px-4 md:px-6 lg:px-8 gap-8">
      
      <aside 
        v-if="route.path === '/'" 
        class="hidden lg:block w-80 py-8"
      >
        <AppSidebar />
      </aside>

      <main class="flex-1 py-8 min-w-0">
        <router-view />
      </main>

    </div>

    <AppFooter />
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router';
import AppHeader from './components/AppHeader.vue';
import AppFooter from './components/AppFooter.vue';
import AppSidebar from './components/AppSidebar.vue';

const route = useRoute();
</script>