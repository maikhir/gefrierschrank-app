<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="p-6">
    <!-- Header -->
    <div class="mb-8">
      <h2 class="text-2xl font-bold text-secondary-900 mb-2">
        Gefrierschrank Übersicht
        <span v-if="activeFilterName" class="text-lg font-normal text-secondary-600">
          • {{ activeFilterName }}
        </span>
      </h2>
      <p class="text-secondary-600">
        {{ totalProducts }} Produkte insgesamt • {{ expiringProducts.length }} laufen bald ab
      </p>
    </div>

    <!-- Quick Stats -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
      <div class="card p-6">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <div class="w-8 h-8 bg-success-100 rounded-lg flex items-center justify-center">
              <CheckCircleIcon class="w-5 h-5 text-success-600" />
            </div>
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">Frische Produkte</p>
            <p class="text-2xl font-bold text-secondary-900">{{ freshProducts.length }}</p>
          </div>
        </div>
      </div>

      <div class="card p-6">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <div class="w-8 h-8 bg-warning-100 rounded-lg flex items-center justify-center">
              <ExclamationTriangleIcon class="w-5 h-5 text-warning-600" />
            </div>
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">Läuft bald ab</p>
            <p class="text-2xl font-bold text-warning-600">{{ expiringProducts.length }}</p>
          </div>
        </div>
      </div>

      <div class="card p-6">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <div class="w-8 h-8 bg-red-100 rounded-lg flex items-center justify-center">
              <XCircleIcon class="w-5 h-5 text-red-600" />
            </div>
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">Abgelaufen</p>
            <p class="text-2xl font-bold text-red-600">{{ expiredProducts.length }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Filter Bar -->
    <div class="card p-4 mb-6">
      <div class="flex flex-wrap gap-4 items-center justify-between">
        <!-- Quick Filter Buttons with Count -->
        <div class="flex items-center space-x-4">
          <div class="flex items-center space-x-2">
            <button
              @click="selectedFilter = 'all'"
              :class="selectedFilter === 'all' ? 'btn-primary' : 'btn-secondary'"
              class="px-4 py-2 text-sm rounded-md font-medium"
            >
              Alle
            </button>
            <button
              @click="selectedFilter = 'expiring'"
              :class="selectedFilter === 'expiring' ? 'btn-primary' : 'btn-secondary'"
              class="px-4 py-2 text-sm rounded-md font-medium"
            >
              Läuft ab
            </button>
            <button
              @click="selectedFilter = 'expired'"
              :class="selectedFilter === 'expired' ? 'btn-primary' : 'btn-secondary'" 
              class="px-4 py-2 text-sm rounded-md font-medium"
            >
              Abgelaufen
            </button>
          </div>
          
          <!-- Filter Result Count -->
          <div class="text-sm text-secondary-600 bg-secondary-50 px-3 py-2 rounded-md border">
            {{ filteredProducts.length }} 
            {{ filteredProducts.length === 1 ? 'Produkt' : 'Produkte' }}
            <span v-if="searchQuery.trim() || selectedFilter !== 'all' || hasSidebarFilters" class="text-secondary-500">
              (gefiltert)
            </span>
          </div>
          
          <!-- Active Filters Badge -->
          <div v-if="activeFilterCount > 0" class="flex items-center space-x-2">
            <div class="text-xs text-primary-600 bg-primary-50 px-2 py-1 rounded-full border border-primary-200 font-medium">
              {{ activeFilterCount }} Filter aktiv
            </div>
          </div>
        </div>

        <!-- Search & Controls -->
        <div class="flex items-center space-x-4">
          <div class="relative">
            <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-secondary-400" />
            <input
              type="text"
              placeholder="Produkte suchen..."
              class="pl-10 pr-4 py-2 border border-secondary-300 rounded-md text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500 placeholder:text-secondary-600"
              v-model="searchQuery"
            />
          </div>
          
          <!-- Sort Dropdown -->
          <select 
            v-model="sortBy" 
            class="rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
          >
            <option value="name">Name</option>
            <option value="expiration">Ablaufdatum</option>
            <option value="category">Kategorie</option>
            <option value="location">Standort</option>
          </select>
          
          <!-- Pagination Info (Top) -->
          <div v-if="totalPages > 1 || filteredProducts.length > 10" class="flex items-center space-x-2 text-sm text-secondary-600">
            <span>{{ startItem }}-{{ endItem }} von {{ filteredProducts.length }}</span>
            <div v-if="totalPages > 1" class="flex items-center space-x-1">
              <button
                @click="goToPage(currentPage - 1)"
                :disabled="currentPage === 1"
                :class="currentPage === 1 ? 'opacity-30 cursor-not-allowed' : 'hover:text-primary-600'"
                class="p-1 rounded"
              >
                ‹
              </button>
              <span class="px-2 py-1 text-xs bg-primary-100 text-primary-700 rounded">
                {{ currentPage }}/{{ totalPages }}
              </span>
              <button
                @click="goToPage(currentPage + 1)"
                :disabled="currentPage === totalPages"
                :class="currentPage === totalPages ? 'opacity-30 cursor-not-allowed' : 'hover:text-primary-600'"
                class="p-1 rounded"
              >
                ›
              </button>
            </div>
          </div>
          
          <!-- View Toggle -->
          <div class="flex items-center border border-secondary-300 rounded-md overflow-hidden">
            <button
              @click="viewMode = 'cards'"
              :class="viewMode === 'cards' ? 'bg-primary-50 text-primary-700' : 'text-secondary-600 hover:bg-secondary-50'"
              class="px-3 py-2 text-sm font-medium transition-colors duration-150"
            >
              <Squares2X2Icon class="w-4 h-4" />
            </button>
            <button
              @click="viewMode = 'table'"
              :class="viewMode === 'table' ? 'bg-primary-50 text-primary-700' : 'text-secondary-600 hover:bg-secondary-50'"
              class="px-3 py-2 text-sm font-medium transition-colors duration-150 border-l border-secondary-300"
            >
              <TableCellsIcon class="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>


    <!-- Loading State -->
    <div v-if="productsStore.loading" class="text-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500 mx-auto"></div>
      <p class="mt-4 text-secondary-600">Lade Produkte...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="productsStore.error" class="text-center py-12">
      <div class="text-red-500 mb-4">❌ Fehler beim Laden der Produkte</div>
      <p class="text-secondary-600">{{ productsStore.error }}</p>
      <button @click="productsStore.fetchProducts()" class="mt-4 btn-primary">
        Erneut versuchen
      </button>
    </div>

    <!-- Products Display -->
    <div v-else>
      <!-- Cards View -->
      <div v-if="viewMode === 'cards'">
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          <ProductCard
            v-for="product in paginatedProducts"
            :key="product.id"
            :product="product"
            @edit="editProduct"
            @delete="deleteProduct"
            @mark-used="markProductAsUsed"
          />
        </div>
      </div>

      <!-- Table View -->
      <div v-else>
        <ProductTable
          :products="paginatedProducts"
          @toggle-view="viewMode = 'cards'"
          @sort="handleSort"
          @edit="editProduct"
          @delete="deleteProduct"
          @mark-used="markProductAsUsed"
        />
      </div>

      <!-- Pagination Controls -->
      <div v-if="totalPages > 1 || filteredProducts.length > 10" class="mt-8 flex items-center justify-between">
        <div class="flex items-center space-x-2">
          <span class="text-sm text-secondary-600">
            Zeige {{ startItem }}-{{ endItem }} von {{ filteredProducts.length }} Produkten
          </span>
        </div>
        
        <div class="flex items-center space-x-2">
          <!-- Previous Button -->
          <button
            @click="goToPage(currentPage - 1)"
            :disabled="currentPage === 1"
            :class="currentPage === 1 ? 'opacity-50 cursor-not-allowed' : 'hover:bg-secondary-100'"
            class="px-3 py-2 rounded-md border border-secondary-300 text-sm font-medium text-secondary-700 bg-white"
          >
            ← Zurück
          </button>

          <!-- Page Numbers -->
          <div class="flex space-x-1">
            <button
              v-for="page in visiblePages"
              :key="page"
              @click="goToPage(page)"
              :class="page === currentPage ? 'bg-primary-600 text-white' : 'bg-white text-secondary-700 hover:bg-secondary-100'"
              class="px-3 py-2 rounded-md border border-secondary-300 text-sm font-medium"
            >
              {{ page }}
            </button>
          </div>

          <!-- Next Button -->
          <button
            @click="goToPage(currentPage + 1)"
            :disabled="currentPage === totalPages"
            :class="currentPage === totalPages ? 'opacity-50 cursor-not-allowed' : 'hover:bg-secondary-100'"
            class="px-3 py-2 rounded-md border border-secondary-300 text-sm font-medium text-secondary-700 bg-white"
          >
            Weiter →
          </button>
        </div>
        
        <div class="flex items-center space-x-2">
          <span class="text-sm text-secondary-600">Pro Seite:</span>
          <select 
            :value="settingsStore.productsPerPage"
            @change="handlePageSizeChange"
            class="rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
          >
            <option value="10">10</option>
            <option value="20">20</option>
            <option value="50">50</option>
            <option value="100">100</option>
            <option value="250">250</option>
            <option value="500">500</option>
            <option value="1000">Alle</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="filteredProducts.length === 0" class="text-center py-12">
      <div class="w-24 h-24 mx-auto mb-4 bg-secondary-100 rounded-full flex items-center justify-center">
        <ArchiveBoxXMarkIcon class="w-12 h-12 text-secondary-400" />
      </div>
      <h3 class="text-lg font-medium text-secondary-900 mb-2">Keine Produkte gefunden</h3>
      <p class="text-secondary-500 mb-4">
        Füge dein erstes Produkt hinzu oder ändere deine Filter.
      </p>
      <button class="btn-primary">
        <PlusIcon class="w-5 h-5 mr-2" />
        Erstes Produkt hinzufügen
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { 
  CheckCircleIcon, 
  ExclamationTriangleIcon, 
  XCircleIcon,
  ArchiveBoxXMarkIcon,
  PlusIcon,
  MagnifyingGlassIcon,
  Squares2X2Icon,
  TableCellsIcon
} from '@heroicons/vue/24/outline'
import ProductCard from '@/components/product/ProductCard.vue'
import ProductTable from '@/components/product/ProductTable.vue'
import { useProductsStore } from '@/stores/products'
import { useSettingsStore } from '@/stores/settings'
import { useCategoriesStore } from '@/stores/categories'
import { useLocationsStore } from '@/stores/locations'

// Props from layout for sidebar filters
const props = defineProps<{
  sidebarCategoryFilter?: number | null
  sidebarLocationFilter?: number | null
  sidebarStatusFilter?: string | null
}>()

// Use Pinia stores
const productsStore = useProductsStore()
const settingsStore = useSettingsStore()
const categoriesStore = useCategoriesStore()
const locationsStore = useLocationsStore()

const selectedFilter = ref('all')
const sortBy = ref('name')
const searchQuery = ref('')
const currentPage = ref(1)
const viewMode = ref<'cards' | 'table'>('cards')

// Load products and settings on mount
onMounted(() => {
  settingsStore.loadSettings()
  productsStore.fetchProducts({ size: 1000 })
  categoriesStore.fetchCategories()
  locationsStore.fetchLocations()
})

// Reset to page 1 when filters change
watch([selectedFilter, searchQuery, sortBy], () => {
  currentPage.value = 1
})

// Reset to page 1 when page size changes  
watch(() => settingsStore.productsPerPage, () => {
  currentPage.value = 1
})

// Sync top bar filters with sidebar filters
watch(() => props.sidebarStatusFilter, (newStatus) => {
  if (newStatus) {
    selectedFilter.value = newStatus
  } else {
    selectedFilter.value = 'all'
  }
  currentPage.value = 1
})

// Reset page when sidebar filters change
watch([() => props.sidebarCategoryFilter, () => props.sidebarLocationFilter], () => {
  currentPage.value = 1
  if (!props.sidebarStatusFilter) {
    selectedFilter.value = 'all'
  }
})

// Computed properties from store
const totalProducts = computed(() => productsStore.totalProducts)
const expiringProducts = computed(() => productsStore.expiringProducts)
const expiredProducts = computed(() => productsStore.expiredProducts)

const freshProducts = computed(() => 
  productsStore.products.filter(p => {
    if (!p.expirationDate) return true
    const daysUntil = getDaysUntilExpiration(p.expirationDate)
    return daysUntil > 7
  })
)

// Active filter name for header display
const activeFilterName = computed(() => {
  const filters = []
  
  if (props.sidebarCategoryFilter) {
    const category = categoriesStore.categories.find(c => c.id === props.sidebarCategoryFilter)
    if (category) filters.push(category.name)
  }
  
  if (props.sidebarLocationFilter) {
    const location = locationsStore.locations.find(l => l.id === props.sidebarLocationFilter)
    if (location) filters.push(location.name)
  }
  
  if (props.sidebarStatusFilter) {
    switch (props.sidebarStatusFilter) {
      case 'expiring': filters.push('Läuft bald ab'); break
      case 'expired': filters.push('Abgelaufen'); break
    }
  }
  
  if (filters.length === 0) return null
  if (filters.length === 1) return filters[0]
  return filters.join(' • ')
})

// Check if any sidebar filters are active
const hasSidebarFilters = computed(() => {
  return !!(props.sidebarCategoryFilter || props.sidebarLocationFilter || props.sidebarStatusFilter)
})

// Count active filters
const activeFilterCount = computed(() => {
  let count = 0
  if (props.sidebarCategoryFilter) count++
  if (props.sidebarLocationFilter) count++
  if (props.sidebarStatusFilter) count++
  if (searchQuery.value.trim()) count++
  return count
})

const filteredProducts = computed(() => {
  let filtered = productsStore.products

  // Apply sidebar category filter
  if (props.sidebarCategoryFilter) {
    filtered = filtered.filter(product => product.category.id === props.sidebarCategoryFilter)
  }

  // Apply sidebar location filter
  if (props.sidebarLocationFilter) {
    filtered = filtered.filter(product => product.location.id === props.sidebarLocationFilter)
  }

  // Apply sidebar status filter
  if (props.sidebarStatusFilter) {
    if (props.sidebarStatusFilter === 'expiring') {
      filtered = filtered.filter(p => {
        if (!p.expirationDate) return false
        const daysUntil = getDaysUntilExpiration(p.expirationDate)
        return daysUntil <= 7 && daysUntil > 0
      })
    } else if (props.sidebarStatusFilter === 'expired') {
      filtered = filtered.filter(p => {
        if (!p.expirationDate) return false
        const daysUntil = getDaysUntilExpiration(p.expirationDate)
        return daysUntil <= 0
      })
    }
  }

  // Apply search filter
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(product => 
      product.name.toLowerCase().includes(query) ||
      product.category.name.toLowerCase().includes(query) ||
      product.location.name.toLowerCase().includes(query)
    )
  }

  // Apply top bar status filter (only if no sidebar filter is active)
  if (!props.sidebarStatusFilter && selectedFilter.value !== 'all') {
    if (selectedFilter.value === 'fresh') {
      filtered = filtered.filter(p => {
        if (!p.expirationDate) return true
        const daysUntil = getDaysUntilExpiration(p.expirationDate)
        return daysUntil > 7
      })
    } else if (selectedFilter.value === 'expiring') {
      filtered = filtered.filter(p => {
        if (!p.expirationDate) return false
        const daysUntil = getDaysUntilExpiration(p.expirationDate)
        return daysUntil <= 7 && daysUntil > 0
      })
    } else if (selectedFilter.value === 'expired') {
      filtered = filtered.filter(p => {
        if (!p.expirationDate) return false
        const daysUntil = getDaysUntilExpiration(p.expirationDate)
        return daysUntil <= 0
      })
    }
  }

  // Apply sorting
  return filtered.sort((a, b) => {
    switch (sortBy.value) {
      case 'name':
        return a.name.localeCompare(b.name)
      case 'expiration':
        if (!a.expirationDate) return 1
        if (!b.expirationDate) return -1
        return new Date(a.expirationDate).getTime() - new Date(b.expirationDate).getTime()
      case 'category':
        return a.category.name.localeCompare(b.category.name)
      case 'location':
        return a.location.name.localeCompare(b.location.name)
      default:
        return 0
    }
  })
})

// Pagination computed properties
const totalPages = computed(() => {
  const pageSize = settingsStore.productsPerPage
  const totalProducts = filteredProducts.value.length
  
  if (pageSize >= 1000) return 1 // "Alle" option
  return Math.ceil(totalProducts / pageSize)
})

const paginatedProducts = computed(() => {
  const pageSize = settingsStore.productsPerPage
  if (pageSize >= 1000) return filteredProducts.value // "Alle" option
  
  const start = (currentPage.value - 1) * pageSize
  const end = start + pageSize
  return filteredProducts.value.slice(start, end)
})

const startItem = computed(() => {
  const pageSize = settingsStore.productsPerPage
  if (pageSize >= 1000 || filteredProducts.value.length === 0) return 1
  return (currentPage.value - 1) * pageSize + 1
})

const endItem = computed(() => {
  const pageSize = settingsStore.productsPerPage
  if (pageSize >= 1000) return filteredProducts.value.length
  
  const end = currentPage.value * pageSize
  return Math.min(end, filteredProducts.value.length)
})

const visiblePages = computed(() => {
  const total = totalPages.value
  const current = currentPage.value
  const delta = 2 // Number of pages to show on each side of current
  
  if (total <= 7) {
    // Show all pages if total is small
    return Array.from({ length: total }, (_, i) => i + 1)
  }
  
  const range = []
  const rangeWithDots = []
  
  for (let i = 1; i <= total; i++) {
    if (i === 1 || i === total || (i >= current - delta && i <= current + delta)) {
      range.push(i)
    }
  }
  
  let prev = 0
  for (const i of range) {
    if (prev + 1 < i) {
      rangeWithDots.push('...')
    }
    rangeWithDots.push(i)
    prev = i
  }
  
  return rangeWithDots.filter(item => typeof item === 'number')
})

// Helper functions
function getDaysUntilExpiration(expirationDate: string): number {
  const now = new Date()
  const expiry = new Date(expirationDate)
  const diffTime = expiry.getTime() - now.getTime()
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
}

// Event handlers
async function editProduct(productId: number) {
  console.log('Edit product:', productId)
  // TODO: Open edit modal with product data
}

async function deleteProduct(productId: number) {
  if (confirm('Möchten Sie dieses Produkt wirklich löschen?')) {
    try {
      await productsStore.deleteProduct(productId)
    } catch (error) {
      console.error('Failed to delete product:', error)
    }
  }
}

async function markProductAsUsed(productId: number) {
  if (confirm('Produkt als verbraucht markieren?')) {
    try {
      await productsStore.deleteProduct(productId)
    } catch (error) {
      console.error('Failed to mark product as used:', error)
    }
  }
}

// Pagination event handlers
function goToPage(page: number) {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

function handlePageSizeChange(event: Event) {
  const target = event.target as HTMLSelectElement
  const newSize = parseInt(target.value)
  settingsStore.updateSetting('productsPerPage', newSize)
  currentPage.value = 1
}

function handleSort(field: string) {
  sortBy.value = field
}
</script>