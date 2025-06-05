<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="card overflow-hidden">
    <!-- Table Header -->
    <div class="px-6 py-4 border-b border-secondary-200 bg-secondary-50">
      <div class="flex items-center justify-between">
        <h3 class="text-lg font-semibold text-secondary-900">Produktübersicht</h3>
        
        <!-- View Toggle -->
        <div class="flex items-center space-x-2">
          <button
            @click="$emit('toggle-view')"
            class="p-2 rounded-md text-secondary-600 hover:bg-secondary-100"
            title="Zur Karten-Ansicht wechseln"
          >
            <Squares2X2Icon class="w-5 h-5" />
          </button>
        </div>
      </div>
    </div>

    <!-- Table -->
    <div class="overflow-x-auto">
      <table class="min-w-full divide-y divide-secondary-200">
        <!-- Table Head -->
        <thead class="bg-secondary-50">
          <tr>
            <th 
              scope="col" 
              class="px-6 py-3 text-left text-xs font-medium text-secondary-500 uppercase tracking-wider cursor-pointer hover:bg-secondary-100"
              @click="$emit('sort', 'name')"
            >
              <div class="flex items-center space-x-1">
                <span>Produkt</span>
                <ChevronUpDownIcon class="w-4 h-4" />
              </div>
            </th>
            <th 
              scope="col" 
              class="px-6 py-3 text-left text-xs font-medium text-secondary-500 uppercase tracking-wider cursor-pointer hover:bg-secondary-100"
              @click="$emit('sort', 'category')"
            >
              <div class="flex items-center space-x-1">
                <span>Kategorie</span>
                <ChevronUpDownIcon class="w-4 h-4" />
              </div>
            </th>
            <th 
              scope="col" 
              class="px-6 py-3 text-left text-xs font-medium text-secondary-500 uppercase tracking-wider cursor-pointer hover:bg-secondary-100"
              @click="$emit('sort', 'location')"
            >
              <div class="flex items-center space-x-1">
                <span>Standort</span>
                <ChevronUpDownIcon class="w-4 h-4" />
              </div>
            </th>
            <th 
              scope="col" 
              class="px-6 py-3 text-left text-xs font-medium text-secondary-500 uppercase tracking-wider cursor-pointer hover:bg-secondary-100"
              @click="$emit('sort', 'quantity')"
            >
              <div class="flex items-center space-x-1">
                <span>Menge</span>
                <ChevronUpDownIcon class="w-4 h-4" />
              </div>
            </th>
            <th 
              scope="col" 
              class="px-6 py-3 text-left text-xs font-medium text-secondary-500 uppercase tracking-wider cursor-pointer hover:bg-secondary-100"
              @click="$emit('sort', 'expiration')"
            >
              <div class="flex items-center space-x-1">
                <span>Status</span>
                <ChevronUpDownIcon class="w-4 h-4" />
              </div>
            </th>
            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-secondary-500 uppercase tracking-wider">
              Eingefroren
            </th>
            <th scope="col" class="relative px-6 py-3">
              <span class="sr-only">Aktionen</span>
            </th>
          </tr>
        </thead>
        
        <!-- Table Body -->
        <tbody class="bg-white divide-y divide-secondary-200">
          <tr 
            v-for="product in products" 
            :key="product.id"
            class="hover:bg-secondary-50 transition-colors duration-150"
          >
            <!-- Product Name & Icon -->
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center">
                <div 
                  class="w-8 h-8 rounded-lg flex items-center justify-center mr-3 flex-shrink-0"
                  :style="{ backgroundColor: product.category.color + '20', border: `1px solid ${product.category.color}30` }"
                >
                  <div 
                    class="w-3 h-3 rounded-full"
                    :style="{ backgroundColor: product.category.color || '#64748b' }"
                  ></div>
                </div>
                <div>
                  <div class="text-sm font-medium text-secondary-900">{{ product.name }}</div>
                  <div v-if="product.notes" class="text-xs text-secondary-500 truncate max-w-xs">
                    {{ product.notes }}
                  </div>
                </div>
              </div>
            </td>
            
            <!-- Category -->
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center">
                <div 
                  class="w-3 h-3 rounded-full mr-2"
                  :style="{ backgroundColor: product.category.color || '#64748b' }"
                ></div>
                <span class="text-sm text-secondary-900">{{ product.category.name }}</span>
              </div>
            </td>
            
            <!-- Location -->
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center">
                <MapPinIcon class="w-4 h-4 text-secondary-400 mr-2" />
                <span class="text-sm text-secondary-900">{{ product.location.name }}</span>
              </div>
            </td>
            
            <!-- Quantity -->
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center justify-between">
                <div class="flex items-center">
                  <ScaleIcon class="w-4 h-4 text-secondary-400 mr-2" />
                  <span class="text-sm text-secondary-900">{{ product.quantity }} {{ product.unit }}</span>
                </div>
                
                <!-- Quick Quantity Controls -->
                <div class="flex items-center space-x-1 ml-2">
                  <button
                    @click="handleQuantityDecrease(product)"
                    :disabled="!canDecrease(product)"
                    class="w-5 h-5 rounded-full bg-red-100 text-red-600 hover:bg-red-200 disabled:opacity-30 disabled:cursor-not-allowed flex items-center justify-center text-xs font-medium transition-colors"
                  >
                    −
                  </button>
                  <button
                    @click="handleQuantityIncrease(product)"
                    class="w-5 h-5 rounded-full bg-green-100 text-green-600 hover:bg-green-200 flex items-center justify-center text-xs font-medium transition-colors"
                  >
                    +
                  </button>
                </div>
              </div>
            </td>
            
            <!-- Status -->
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="flex items-center">
                <span :class="getStatusBadgeClass(product)" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium">
                  <div 
                    class="w-2 h-2 rounded-full mr-1.5"
                    :class="getStatusDotClass(product)"
                  ></div>
                  {{ getStatusText(product) }}
                </span>
              </div>
            </td>
            
            <!-- Frozen Date -->
            <td class="px-6 py-4 whitespace-nowrap text-sm text-secondary-500">
              {{ formatDate(product.frozenDate) }}
            </td>
            
            <!-- Actions -->
            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
              <div class="flex items-center justify-end space-x-2">
                <!-- Quick Actions (always visible) -->
                <button
                  @click="$emit('mark-used', product.id)"
                  class="text-green-600 hover:text-green-700 p-1 rounded"
                  title="Als verbraucht markieren"
                >
                  <CheckIcon class="w-4 h-4" />
                </button>
                
                <button
                  @click="$emit('edit', product.id)"
                  class="text-secondary-600 hover:text-secondary-700 p-1 rounded"
                  title="Bearbeiten"
                >
                  <PencilIcon class="w-4 h-4" />
                </button>
                
                <!-- More Actions Dropdown -->
                <div class="relative">
                  <button
                    @click="toggleActionsMenu(product.id)"
                    class="text-secondary-400 hover:text-secondary-600 p-1 rounded"
                  >
                    <EllipsisVerticalIcon class="w-4 h-4" />
                  </button>
                  
                  <!-- Dropdown Menu -->
                  <div
                    v-if="activeActionsMenu === product.id"
                    class="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg border border-secondary-200 z-10"
                  >
                    <button
                      @click="duplicateProduct(product)"
                      class="w-full flex items-center px-4 py-2 text-sm text-secondary-700 hover:bg-secondary-50"
                    >
                      <DocumentDuplicateIcon class="w-4 h-4 mr-3" />
                      Duplizieren
                    </button>
                    <button
                      @click="viewDetails(product)"
                      class="w-full flex items-center px-4 py-2 text-sm text-secondary-700 hover:bg-secondary-50"
                    >
                      <EyeIcon class="w-4 h-4 mr-3" />
                      Details anzeigen
                    </button>
                    <div class="border-t border-secondary-200"></div>
                    <button
                      @click="$emit('delete', product.id)"
                      class="w-full flex items-center px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                    >
                      <TrashIcon class="w-4 h-4 mr-3" />
                      Löschen
                    </button>
                  </div>
                </div>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- Empty State -->
    <div v-if="products.length === 0" class="text-center py-12">
      <ArchiveBoxXMarkIcon class="w-12 h-12 text-secondary-400 mx-auto mb-4" />
      <h3 class="text-lg font-medium text-secondary-900 mb-2">Keine Produkte gefunden</h3>
      <p class="text-secondary-500">
        Versuche deine Filter anzupassen oder füge neue Produkte hinzu.
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { 
  ChevronUpDownIcon,
  Squares2X2Icon,
  MapPinIcon,
  ScaleIcon,
  CheckIcon,
  PencilIcon,
  EllipsisVerticalIcon,
  DocumentDuplicateIcon,
  EyeIcon,
  TrashIcon,
  ArchiveBoxXMarkIcon
} from '@heroicons/vue/24/outline'
import type { Product } from '@/api/products'

// Props
defineProps<{
  products: Product[]
}>()

// Emits
const emit = defineEmits<{
  'toggle-view': []
  'sort': [field: string]
  'edit': [id: number]
  'delete': [id: number]
  'mark-used': [id: number]
  'quantity-change': [id: number, newQuantity: number]
}>()

// State
const activeActionsMenu = ref<number | null>(null)

// Unit step mapping for quantity adjustments
const getQuantityStep = (unit: string): number => {
  switch (unit) {
    case 'g':
    case 'ml':
    case 'Stück':
    case 'Packung':
    case 'Dose':
    case 'Glas':
      return 1
    case 'kg':
    case 'l':
      return 0.1
    default:
      return 0.1
  }
}

// Lifecycle
onMounted(() => {
  document.addEventListener('click', closeActionsMenu)
})

onUnmounted(() => {
  document.removeEventListener('click', closeActionsMenu)
})

// Helper functions
const getDaysUntilExpiration = (product: Product): number | null => {
  if (!product.expirationDate) return null
  const now = new Date()
  const expiry = new Date(product.expirationDate)
  const diffTime = expiry.getTime() - now.getTime()
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
}

const getExpirationStatus = (product: Product): 'fresh' | 'expiring' | 'critical' | 'expired' => {
  if (!product.expirationDate) return 'fresh'
  const days = getDaysUntilExpiration(product)!
  if (days < 0) return 'expired'
  if (days <= 1) return 'critical'
  if (days <= 7) return 'expiring'
  return 'fresh'
}

const getStatusBadgeClass = (product: Product): string => {
  const status = getExpirationStatus(product)
  switch (status) {
    case 'fresh':
      return 'bg-green-100 text-green-800'
    case 'expiring':
      return 'bg-yellow-100 text-yellow-800'
    case 'critical':
      return 'bg-orange-100 text-orange-800'
    case 'expired':
      return 'bg-red-100 text-red-800'
    default:
      return 'bg-gray-100 text-gray-800'
  }
}

const getStatusDotClass = (product: Product): string => {
  const status = getExpirationStatus(product)
  switch (status) {
    case 'fresh':
      return 'bg-green-500'
    case 'expiring':
      return 'bg-yellow-500'
    case 'critical':
      return 'bg-orange-500'
    case 'expired':
      return 'bg-red-500'
    default:
      return 'bg-gray-500'
  }
}

const getStatusText = (product: Product): string => {
  const status = getExpirationStatus(product)
  const days = getDaysUntilExpiration(product)
  
  switch (status) {
    case 'fresh':
      return days ? `${days} Tage` : 'Frisch'
    case 'expiring':
      return `${days} Tage`
    case 'critical':
      return days === 0 ? 'Heute' : '1 Tag'
    case 'expired':
      return 'Abgelaufen'
    default:
      return 'Unbekannt'
  }
}

const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('de-DE', {
    day: '2-digit',
    month: '2-digit',
    year: '2-digit'
  })
}

// Actions
const toggleActionsMenu = (productId: number) => {
  activeActionsMenu.value = activeActionsMenu.value === productId ? null : productId
}

const closeActionsMenu = () => {
  activeActionsMenu.value = null
}

const duplicateProduct = (product: Product) => {
  // TODO: Implement duplication logic
  console.log('Duplicate product:', product.name)
  closeActionsMenu()
}

const viewDetails = (product: Product) => {
  // TODO: Implement details view
  console.log('View details for:', product.name)
  closeActionsMenu()
}

// Quantity management functions
const canDecrease = (product: Product): boolean => {
  const step = getQuantityStep(product.unit)
  return product.quantity > step
}

const handleQuantityIncrease = (product: Product) => {
  const step = getQuantityStep(product.unit)
  const newQuantity = Math.round((product.quantity + step) * 10) / 10
  emit('quantity-change', product.id, newQuantity)
}

const handleQuantityDecrease = (product: Product) => {
  const step = getQuantityStep(product.unit)
  const newQuantity = Math.round((product.quantity - step) * 10) / 10
  
  if (newQuantity >= step) {
    emit('quantity-change', product.id, newQuantity)
  }
}
</script>