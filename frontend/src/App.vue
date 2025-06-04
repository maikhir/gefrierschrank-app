<template>
  <div id="app" class="min-h-screen bg-secondary-50">
    <Header />
    <div class="flex">
      <Sidebar 
        v-if="!isMobile" 
        @filter-category="handleCategoryFilter"
        @filter-location="handleLocationFilter" 
        @filter-status="handleStatusFilter"
        @clear-filters="handleClearFilters"
      />
      <main class="flex-1">
        <RouterView 
          :sidebar-category-filter="sidebarCategoryFilter"
          :sidebar-location-filter="sidebarLocationFilter"
          :sidebar-status-filter="sidebarStatusFilter"
        />
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

// Sidebar filter state
const sidebarCategoryFilter = ref<number | null>(null)
const sidebarLocationFilter = ref<number | null>(null)
const sidebarStatusFilter = ref<string | null>(null)

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

// Sidebar filter handlers
const handleCategoryFilter = (categoryId: number | null) => {
  sidebarCategoryFilter.value = categoryId
  // Keep location filter - allow combination
  sidebarStatusFilter.value = null
}

const handleLocationFilter = (locationId: number | null) => {
  sidebarLocationFilter.value = locationId
  // Keep category filter - allow combination  
  sidebarStatusFilter.value = null
}

const handleStatusFilter = (status: string) => {
  sidebarStatusFilter.value = status
  sidebarCategoryFilter.value = null
  sidebarLocationFilter.value = null
}

const handleClearFilters = () => {
  sidebarCategoryFilter.value = null
  sidebarLocationFilter.value = null
  sidebarStatusFilter.value = null
}
</script>

<style>
/* Global styles are in main.css */
</style>