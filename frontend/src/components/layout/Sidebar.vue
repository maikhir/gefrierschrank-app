<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <aside class="w-64 bg-white border-r border-secondary-300 h-screen overflow-y-auto">
    <!-- Categories Section -->
    <div class="p-4 border-b border-secondary-300">
      <h2 class="text-sm font-semibold text-secondary-700 uppercase tracking-wide mb-3">
        Kategorien
      </h2>
      <nav class="space-y-1">
        <!-- All Categories Button -->
        <button
          @click="clearCategoryFilter"
          class="w-full flex items-center px-3 py-2 text-sm rounded-md transition-all duration-200"
          :class="selectedCategory === null && selectedLocation === null
            ? 'text-secondary-900 border-2 border-primary-500 bg-white shadow-sm' 
            : 'text-secondary-600 hover:bg-secondary-50 hover:text-secondary-900 border border-transparent'"
        >
          <div class="w-3 h-3 rounded-full mr-3 bg-secondary-400"></div>
          <span class="font-medium">Alle Kategorien</span>
          <span 
            class="ml-auto text-xs px-2 py-0.5 rounded-full font-medium"
            :class="selectedCategory === null && selectedLocation === null
              ? 'bg-primary-500 text-white' 
              : 'bg-secondary-100 text-secondary-600'"
          >
            {{ totalProductsCount }}
          </span>
        </button>
        
        <!-- Individual Categories -->
        <button
          v-for="category in categories"
          :key="category.id"
          @click="filterByCategory(category.id)"
          class="w-full flex items-center px-3 py-2 text-sm rounded-md transition-all duration-200"
          :class="selectedCategory === category.id 
            ? 'text-secondary-900 border-2 border-primary-500 bg-white shadow-sm' 
            : 'text-secondary-600 hover:bg-secondary-50 hover:text-secondary-900 border border-transparent'"
        >
          <div 
            class="w-3 h-3 rounded-full mr-3 transition-all duration-200" 
            :style="{ backgroundColor: category.color || '#64748b' }"
            :class="selectedCategory === category.id ? 'ring-2 ring-primary-400 ring-offset-1' : ''"
          ></div>
          <span class="font-medium">
            {{ category.name }}
          </span>
          <span 
            class="ml-auto text-xs px-2 py-0.5 rounded-full font-medium"
            :class="selectedCategory === category.id 
              ? 'bg-primary-500 text-white' 
              : 'bg-secondary-100 text-secondary-600'"
          >
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
        <!-- All Locations Button -->
        <button
          @click="clearLocationFilter"
          class="w-full flex items-center px-3 py-2 text-sm rounded-md transition-all duration-200"
          :class="selectedLocation === null && selectedCategory === null
            ? 'text-secondary-900 border-2 border-primary-500 bg-white shadow-sm' 
            : 'text-secondary-600 hover:bg-secondary-50 hover:text-secondary-900 border border-transparent'"
        >
          <MapPinIcon class="w-4 h-4 mr-3" />
          <span class="font-medium">Alle Standorte</span>
          <span 
            class="ml-auto text-xs px-2 py-0.5 rounded-full font-medium"
            :class="selectedLocation === null && selectedCategory === null
              ? 'bg-primary-500 text-white' 
              : 'bg-secondary-100 text-secondary-600'"
          >
            {{ totalProductsCount }}
          </span>
        </button>
        
        <!-- Individual Locations -->
        <button
          v-for="location in locations"
          :key="location.id"
          @click="filterByLocation(location.id)"
          class="w-full flex items-center px-3 py-2 text-sm rounded-md transition-all duration-200"
          :class="selectedLocation === location.id 
            ? 'text-secondary-900 border-2 border-primary-500 bg-white shadow-sm' 
            : 'text-secondary-600 hover:bg-secondary-50 hover:text-secondary-900 border border-transparent'"
        >
          <MapPinIcon 
            class="w-4 h-4 mr-3 transition-all duration-200" 
            :class="selectedLocation === location.id ? 'text-primary-600' : ''"
          />
          <span class="font-medium">
            {{ location.name }}
          </span>
          <span 
            class="ml-auto text-xs px-2 py-0.5 rounded-full font-medium"
            :class="selectedLocation === location.id 
              ? 'bg-primary-500 text-white' 
              : 'bg-secondary-100 text-secondary-600'"
          >
            {{ location.count || 0 }}
          </span>
        </button>
      </nav>
    </div>

    <!-- Clear Filters (if multiple active) -->
    <div v-if="hasMultipleFilters" class="p-4 border-t border-secondary-300">
      <button
        @click="clearAllFilters"
        class="w-full flex items-center justify-center px-3 py-2 text-sm rounded-md text-red-600 hover:bg-red-50 hover:text-red-700 border border-red-200 hover:border-red-300 transition-all duration-200 font-medium"
      >
        <XMarkIcon class="w-4 h-4 mr-2" />
        Alle Filter löschen
      </button>
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
import { MapPinIcon, ExclamationTriangleIcon, XCircleIcon, CogIcon, XMarkIcon } from '@heroicons/vue/24/outline'
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

// Computed properties from stores with dynamic counts based on active filters
const categories = computed(() => 
  categoriesStore.categories.map(category => ({
    ...category,
    count: getFilteredCount('category', category.id)
  }))
)

const locations = computed(() => 
  locationsStore.locations.map(location => ({
    ...location,
    count: getFilteredCount('location', location.id)
  }))
)

// Get count for specific filter considering other active filters
const getFilteredCount = (filterType: 'category' | 'location', filterId: number): number => {
  let filtered = productsStore.products

  // If we're counting categories, apply location filter (if active)
  if (filterType === 'category' && selectedLocation.value) {
    filtered = filtered.filter(p => p.location.id === selectedLocation.value)
  }
  
  // If we're counting locations, apply category filter (if active)
  if (filterType === 'location' && selectedCategory.value) {
    filtered = filtered.filter(p => p.category.id === selectedCategory.value)
  }

  // Apply the specific filter we're counting for
  if (filterType === 'category') {
    return filtered.filter(p => p.category.id === filterId).length
  } else {
    return filtered.filter(p => p.location.id === filterId).length
  }
}

const expiringCount = computed(() => productsStore.expiringProducts.length)
const expiredCount = computed(() => productsStore.expiredProducts.length)
const totalProductsCount = computed(() => productsStore.products.length)

const emit = defineEmits(['filter-category', 'filter-location', 'filter-status', 'clear-filters'])

const filterByCategory = (categoryId: number) => {
  selectedCategory.value = selectedCategory.value === categoryId ? null : categoryId
  // Don't clear location filter - allow combination
  emit('filter-category', selectedCategory.value)
}

const filterByLocation = (locationId: number) => {
  selectedLocation.value = selectedLocation.value === locationId ? null : locationId
  // Don't clear category filter - allow combination
  emit('filter-location', selectedLocation.value)
}

const filterByStatus = (status: string) => {
  selectedCategory.value = null
  selectedLocation.value = null
  emit('filter-status', status)
}

const clearCategoryFilter = () => {
  selectedCategory.value = null
  selectedLocation.value = null
  emit('clear-filters')
}

const clearLocationFilter = () => {
  selectedLocation.value = null
  selectedCategory.value = null
  emit('clear-filters')
}

const clearAllFilters = () => {
  selectedCategory.value = null
  selectedLocation.value = null
  emit('clear-filters')
}

// Check if multiple filters are active
const hasMultipleFilters = computed(() => {
  const activeFilters = [selectedCategory.value, selectedLocation.value].filter(f => f !== null)
  return activeFilters.length > 1
})
</script>