<template>
  <div id="app" class="min-h-screen bg-secondary-50">
    <Header />
    <div class="flex">
      <Sidebar v-if="!isMobile" />
      <main class="flex-1">
        <RouterView />
      </main>
    </div>
    <MobileMenu v-if="isMobile" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { RouterView } from 'vue-router'
import Header from './components/layout/Header.vue'
import Sidebar from './components/layout/Sidebar.vue'
import MobileMenu from './components/layout/MobileMenu.vue'

// Responsive state
const isMobile = ref(false)

const checkMobile = () => {
  isMobile.value = window.innerWidth < 1024
}

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style>
/* Global styles are in main.css */
</style>