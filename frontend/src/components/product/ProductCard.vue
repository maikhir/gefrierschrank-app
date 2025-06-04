<template>
  <div class="card p-4 hover:shadow-lg transition-shadow cursor-pointer">
    <!-- Status Badge & Actions -->
    <div class="flex justify-between items-start mb-3">
      <span :class="statusBadgeClass" class="badge">
        {{ statusText }}
      </span>
      
      <!-- More Menu -->
      <div class="relative">
        <button
          @click="showMenu = !showMenu"
          class="p-1 rounded-md text-secondary-400 hover:text-secondary-600"
        >
          <EllipsisVerticalIcon class="w-5 h-5" />
        </button>
        
        <!-- Dropdown Menu -->
        <div
          v-if="showMenu"
          class="absolute right-0 top-8 w-48 bg-white rounded-md shadow-lg border border-secondary-300 z-10"
          @click.stop
        >
          <button
            @click="handleEdit"
            class="w-full text-left px-4 py-2 text-sm text-secondary-700 hover:bg-secondary-50 flex items-center"
          >
            <PencilIcon class="w-4 h-4 mr-2" />
            Bearbeiten
          </button>
          <button
            @click="handleMarkUsed"
            class="w-full text-left px-4 py-2 text-sm text-secondary-700 hover:bg-secondary-50 flex items-center"
          >
            <CheckIcon class="w-4 h-4 mr-2" />
            Als verbraucht markieren
          </button>
          <button
            @click="handleDelete"
            class="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 flex items-center"
          >
            <TrashIcon class="w-4 h-4 mr-2" />
            Löschen
          </button>
        </div>
      </div>
    </div>

    <!-- Product Image/Icon -->
    <div class="text-center mb-4">
      <div class="w-16 h-16 mx-auto mb-2 bg-secondary-100 rounded-lg flex items-center justify-center">
        <component :is="categoryIcon" class="w-8 h-8 text-secondary-500" />
      </div>
      <h3 class="font-semibold text-secondary-900 text-lg">{{ product.name }}</h3>
      <p class="text-sm text-secondary-600">{{ product.category.name }}</p>
    </div>

    <!-- Product Details -->
    <div class="space-y-2 text-sm text-secondary-600">
      <div class="flex items-center">
        <CalendarIcon class="w-4 h-4 mr-2 flex-shrink-0" />
        <span>Eingefroren: {{ formatDate(product.frozenDate) }}</span>
      </div>
      
      <div class="flex items-center">
        <ClockIcon class="w-4 h-4 mr-2 flex-shrink-0" />
        <span>{{ expirationText }}</span>
      </div>
      
      <div class="flex items-center">
        <ScaleIcon class="w-4 h-4 mr-2 flex-shrink-0" />
        <span>{{ product.quantity }} {{ product.unit }}</span>
      </div>
      
      <div class="flex items-center">
        <MapPinIcon class="w-4 h-4 mr-2 flex-shrink-0" />
        <span>{{ product.location.name }}</span>
      </div>
      
      <div v-if="product.notes" class="flex items-start">
        <DocumentTextIcon class="w-4 h-4 mr-2 flex-shrink-0 mt-0.5" />
        <span class="line-clamp-2">{{ product.notes }}</span>
      </div>
    </div>

    <!-- Action Buttons (Mobile) -->
    <div class="flex space-x-2 mt-4 lg:hidden">
      <button @click="handleEdit" class="btn-secondary flex-1 text-sm">
        <PencilIcon class="w-4 h-4 mr-1" />
        Bearbeiten
      </button>
      <button @click="handleMarkUsed" class="btn-success flex-1 text-sm">
        <CheckIcon class="w-4 h-4 mr-1" />
        Verbraucht
      </button>
    </div>
  </div>

  <!-- Click outside to close menu -->
  <div v-if="showMenu" @click="showMenu = false" class="fixed inset-0 z-0"></div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  EllipsisVerticalIcon,
  PencilIcon,
  CheckIcon,
  TrashIcon,
  CalendarIcon,
  ClockIcon,
  ScaleIcon,
  MapPinIcon,
  DocumentTextIcon
} from '@heroicons/vue/24/outline'

// Category icons - using available icons
import {
  CubeIcon,
  ArchiveBoxIcon
} from '@heroicons/vue/24/outline'

interface Product {
  id: number
  name: string
  category: { id: number; name: string; color: string | null }
  location: { id: number; name: string }
  quantity: number
  unit: string
  frozenDate: string
  expirationDate: string | null
  notes: string | null
}

interface Props {
  product: Product
}

const props = defineProps<Props>()
const emit = defineEmits(['edit', 'delete', 'mark-used'])

const showMenu = ref(false)

// Computed properties
const categoryIcon = computed(() => {
  const categoryName = props.product.category.name.toLowerCase()
  if (categoryName.includes('fleisch')) return CubeIcon
  if (categoryName.includes('gemüse')) return CubeIcon
  if (categoryName.includes('fertig')) return CubeIcon
  return ArchiveBoxIcon
})

const daysUntilExpiration = computed(() => {
  if (!props.product.expirationDate) return null
  const now = new Date()
  const expiry = new Date(props.product.expirationDate)
  const diffTime = expiry.getTime() - now.getTime()
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
})

const expirationStatus = computed(() => {
  if (!props.product.expirationDate) return 'fresh'
  const days = daysUntilExpiration.value!
  if (days < 0) return 'expired'
  if (days <= 1) return 'critical'
  if (days <= 7) return 'expiring'
  return 'fresh'
})

const statusBadgeClass = computed(() => {
  switch (expirationStatus.value) {
    case 'fresh':
      return 'badge-fresh'
    case 'expiring':
      return 'badge-expiring'
    case 'critical':
      return 'badge-critical'
    case 'expired':
      return 'badge-expired'
    default:
      return 'badge-fresh'
  }
})

const statusText = computed(() => {
  switch (expirationStatus.value) {
    case 'fresh':
      return 'Frisch'
    case 'expiring':
      return `${daysUntilExpiration.value} Tage`
    case 'critical':
      return 'Kritisch'
    case 'expired':
      return 'Abgelaufen'
    default:
      return 'Unbekannt'
  }
})

const expirationText = computed(() => {
  if (!props.product.expirationDate) return 'Unbegrenzt haltbar'
  
  const days = daysUntilExpiration.value!
  if (days < 0) return `Abgelaufen (${Math.abs(days)} Tage)`
  if (days === 0) return 'Läuft heute ab'
  if (days === 1) return 'Läuft morgen ab'
  return `Haltbar bis: ${formatDate(props.product.expirationDate)}`
})

// Helper functions
function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleDateString('de-DE', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// Event handlers
function handleEdit() {
  showMenu.value = false
  emit('edit', props.product.id)
}

function handleDelete() {
  showMenu.value = false
  emit('delete', props.product.id)
}

function handleMarkUsed() {
  showMenu.value = false
  emit('mark-used', props.product.id)
}
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>