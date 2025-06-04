<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="p-6">
    <!-- Header -->
    <div class="mb-8">
      <h2 class="text-2xl font-bold text-secondary-900 mb-2">
        Gefrierschrank Übersicht
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
        <!-- Quick Filter Buttons -->
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

        <!-- Search -->
        <div class="flex items-center space-x-4">
          <div class="relative">
            <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-secondary-400" />
            <input
              type="text"
              placeholder="Produkte suchen..."
              class="pl-10 pr-4 py-2 border border-secondary-200 rounded-md text-sm text-secondary-900 focus:ring-primary-500 focus:border-primary-500 placeholder:text-secondary-600"
              v-model="searchQuery"
            />
          </div>
          
          <!-- Sort Dropdown -->
          <select 
            v-model="sortBy" 
            class="rounded-md border-secondary-200 text-sm focus:ring-primary-500 focus:border-primary-500"
          >
            <option value="name">Name</option>
            <option value="expiration">Ablaufdatum</option>
            <option value="category">Kategorie</option>
            <option value="location">Standort</option>
          </select>
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

    <!-- Products Grid -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
      <ProductCard
        v-for="product in filteredProducts"
        :key="product.id"
        :product="product"
        @edit="editProduct"
        @delete="deleteProduct"
        @mark-used="markProductAsUsed"
      />
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
import { ref, computed, onMounted } from 'vue'
import { 
  CheckCircleIcon, 
  ExclamationTriangleIcon, 
  XCircleIcon,
  ArchiveBoxXMarkIcon,
  PlusIcon,
  MagnifyingGlassIcon
} from '@heroicons/vue/24/outline'
import ProductCard from '@/components/product/ProductCard.vue'
import { useProductsStore } from '@/stores/products'

// Use Pinia store instead of mock data
const productsStore = useProductsStore()

const selectedFilter = ref('all')
const sortBy = ref('name')
const searchQuery = ref('')

// Load products on mount
onMounted(() => {
  productsStore.fetchProducts()
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

const filteredProducts = computed(() => {
  let filtered = productsStore.products

  // Apply search filter
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(product => 
      product.name.toLowerCase().includes(query) ||
      product.category.name.toLowerCase().includes(query) ||
      product.location.name.toLowerCase().includes(query)
    )
  }

  // Apply status filter
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
</script>