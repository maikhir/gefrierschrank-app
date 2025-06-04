<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <aside class="w-64 bg-white border-r border-secondary-300 h-screen overflow-y-auto">
    <!-- Categories Section -->
    <div class="p-4 border-b border-secondary-300">
      <h2 class="text-sm font-semibold text-secondary-700 uppercase tracking-wide mb-3">
        Kategorien
      </h2>
      <nav class="space-y-1">
        <button
          v-for="category in categories"
          :key="category.id"
          @click="filterByCategory(category.id)"
          class="w-full flex items-center px-3 py-2 text-sm rounded-md text-secondary-600 hover:bg-secondary-50 hover:text-secondary-900"
          :class="{ 'bg-primary-50 text-primary-700': selectedCategory === category.id }"
        >
          <div 
            class="w-3 h-3 rounded-full mr-3" 
            :style="{ backgroundColor: category.color || '#64748b' }"
          ></div>
          {{ category.name }}
          <span class="ml-auto text-xs bg-secondary-100 text-secondary-600 px-2 py-0.5 rounded-full">
            {{ category.count || 0 }}
          </span>
        </button>
      </nav>
    </div>

    <!-- Locations Section -->
    <div class="p-4">
      <h2 class="text-sm font-semibold text-secondary-700 uppercase tracking-wide mb-3">
        Standorte
      </h2>
      <nav class="space-y-1">
        <button
          v-for="location in locations"
          :key="location.id"
          @click="filterByLocation(location.id)"
          class="w-full flex items-center px-3 py-2 text-sm rounded-md text-secondary-600 hover:bg-secondary-50 hover:text-secondary-900"
          :class="{ 'bg-primary-50 text-primary-700': selectedLocation === location.id }"
        >
          <MapPinIcon class="w-4 h-4 mr-3" />
          {{ location.name }}
          <span class="ml-auto text-xs bg-secondary-100 text-secondary-600 px-2 py-0.5 rounded-full">
            {{ location.count || 0 }}
          </span>
        </button>
      </nav>
    </div>

    <!-- Quick Filters -->
    <div class="p-4 border-t border-secondary-300">
      <h2 class="text-sm font-semibold text-secondary-700 uppercase tracking-wide mb-3">
        Schnellfilter
      </h2>
      <nav class="space-y-1">
        <button
          @click="filterByStatus('expiring')"
          class="w-full flex items-center px-3 py-2 text-sm rounded-md text-orange-600 hover:bg-orange-50"
        >
          <ExclamationTriangleIcon class="w-4 h-4 mr-3" />
          Läuft bald ab
          <span class="ml-auto text-xs bg-orange-100 text-orange-600 px-2 py-0.5 rounded-full">
            {{ expiringCount }}
          </span>
        </button>
        
        <button
          @click="filterByStatus('expired')"
          class="w-full flex items-center px-3 py-2 text-sm rounded-md text-red-600 hover:bg-red-50"
        >
          <XCircleIcon class="w-4 h-4 mr-3" />
          Abgelaufen
          <span class="ml-auto text-xs bg-red-100 text-red-600 px-2 py-0.5 rounded-full">
            {{ expiredCount }}
          </span>
        </button>
      </nav>
    </div>

    <!-- Settings Section -->
    <div class="mt-auto p-4 border-t border-secondary-300">
      <nav class="space-y-1">
        <RouterLink
          to="/settings"
          class="w-full flex items-center px-3 py-2 text-sm rounded-md text-secondary-600 hover:bg-secondary-50 hover:text-secondary-900 transition-colors"
          active-class="bg-primary-50 text-primary-700"
        >
          <CogIcon class="w-4 h-4 mr-3" />
          Einstellungen
        </RouterLink>
      </nav>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { MapPinIcon, ExclamationTriangleIcon, XCircleIcon, CogIcon } from '@heroicons/vue/24/outline'
import { useCategoriesStore } from '@/stores/categories'
import { useLocationsStore } from '@/stores/locations'
import { useProductsStore } from '@/stores/products'

// Use Pinia stores
const categoriesStore = useCategoriesStore()
const locationsStore = useLocationsStore()
const productsStore = useProductsStore()

const selectedCategory = ref<number | null>(null)
const selectedLocation = ref<number | null>(null)

// Load data on mount
onMounted(() => {
  categoriesStore.fetchCategories()
  locationsStore.fetchLocations()
})

// Computed properties from stores
const categories = computed(() => 
  categoriesStore.categories.map(category => ({
    ...category,
    count: productsStore.products.filter(p => p.category.id === category.id).length
  }))
)

const locations = computed(() => 
  locationsStore.locations.map(location => ({
    ...location,
    count: productsStore.products.filter(p => p.location.id === location.id).length
  }))
)

const expiringCount = computed(() => productsStore.expiringProducts.length)
const expiredCount = computed(() => productsStore.expiredProducts.length)

const emit = defineEmits(['filter-category', 'filter-location', 'filter-status'])

const filterByCategory = (categoryId: number) => {
  selectedCategory.value = selectedCategory.value === categoryId ? null : categoryId
  selectedLocation.value = null
  emit('filter-category', selectedCategory.value)
}

const filterByLocation = (locationId: number) => {
  selectedLocation.value = selectedLocation.value === locationId ? null : locationId
  selectedCategory.value = null
  emit('filter-location', selectedLocation.value)
}

const filterByStatus = (status: string) => {
  selectedCategory.value = null
  selectedLocation.value = null
  emit('filter-status', status)
}
</script>