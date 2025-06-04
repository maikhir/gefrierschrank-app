<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="p-6">
    <!-- Header -->
    <div class="mb-8">
      <h2 class="text-2xl font-bold text-secondary-900 mb-2">
        Standorte verwalten
      </h2>
      <p class="text-secondary-600">
        {{ locations.length }} Standorte • {{ totalProductsInLocations }} Produkte zugeordnet
      </p>
    </div>

    <!-- Action Bar -->
    <div class="card p-4 mb-6">
      <div class="flex flex-wrap gap-4 items-center justify-between">
        <div class="flex items-center space-x-4">
          <!-- Search -->
          <div class="relative">
            <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-secondary-400" />
            <input
              type="text"
              placeholder="Standorte suchen..."
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
            <option value="productCount">Produktanzahl</option>
            <option value="temperature">Temperatur</option>
            <option value="created">Erstellt</option>
          </select>
        </div>

        <!-- Add Location Button -->
        <button
          @click="openCreateModal"
          class="btn-primary flex items-center"
        >
          <PlusIcon class="w-4 h-4 mr-2" />
          Neuer Standort
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="locationsStore.loading" class="text-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500 mx-auto"></div>
      <p class="mt-4 text-secondary-600">Lade Standorte...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="locationsStore.error" class="text-center py-12">
      <div class="text-red-500 mb-4">❌ Fehler beim Laden der Standorte</div>
      <p class="text-secondary-600">{{ locationsStore.error }}</p>
      <button @click="locationsStore.fetchLocations()" class="mt-4 btn-primary">
        Erneut versuchen
      </button>
    </div>

    <!-- Locations List (Compact) -->
    <div v-else class="card overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-secondary-200">
          <!-- Table Header -->
          <thead class="bg-secondary-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-secondary-500 uppercase tracking-wider">
                Standort
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-secondary-500 uppercase tracking-wider">
                Produkte
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-secondary-500 uppercase tracking-wider">
                Temperatur
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-secondary-500 uppercase tracking-wider">
                Typ
              </th>
              <th scope="col" class="relative px-6 py-3">
                <span class="sr-only">Aktionen</span>
              </th>
            </tr>
          </thead>
          
          <!-- Table Body -->
          <tbody class="bg-white divide-y divide-secondary-200">
            <tr 
              v-for="location in filteredLocations" 
              :key="location.id"
              class="hover:bg-secondary-50 transition-colors duration-150"
            >
              <!-- Location Name & Icon -->
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="flex-shrink-0 mr-3">
                    <div v-if="isStandardLocation(location)" class="w-8 h-8 rounded-lg flex items-center justify-center bg-secondary-100">
                      <component :is="getLocationIcon()" class="w-5 h-5 text-secondary-600" />
                    </div>
                    <div v-else 
                      class="w-8 h-8 rounded-lg flex items-center justify-center"
                      :style="{ backgroundColor: '#3b82f6' + '20', border: `1px solid ${'#3b82f6'}30` }"
                    >
                      <!-- Standard Icon -->
                      <component 
                        v-if="getCustomLocationIcon(location) !== 'custom-svg'"
                        :is="getCustomLocationIcon(location)" 
                        class="w-5 h-5"
                        style="color: #3b82f6"
                      />
                      <!-- Custom SVG Icon -->
                      <div 
                        v-else
                        v-html="getCustomLocationIconSvg(location)" 
                        class="w-5 h-5 flex items-center justify-center"
                        style="max-width: 20px; max-height: 20px; overflow: hidden; color: #3b82f6"
                      ></div>
                    </div>
                  </div>
                  <div>
                    <div class="text-sm font-medium text-secondary-900">{{ location.name }}</div>
                    <div v-if="location.description" class="text-xs text-secondary-500 truncate max-w-xs">
                      {{ location.description }}
                    </div>
                  </div>
                </div>
              </td>
              
              <!-- Product Count -->
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                      :class="location.productCount > 0 ? 'bg-primary-100 text-primary-800' : 'bg-secondary-100 text-secondary-800'">
                  {{ location.productCount }}
                </span>
              </td>
              
              <!-- Temperature -->
              <td class="px-6 py-4 whitespace-nowrap text-sm text-secondary-900">
                {{ (location as LocationWithTemperature).temperature || '-18' }}°C
              </td>
              
              <!-- Location Type -->
              <td class="px-6 py-4 whitespace-nowrap">
                <span v-if="isStandardLocation(location)" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                  Standard
                </span>
                <span v-else class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                  Benutzerdefiniert
                </span>
              </td>
              
              <!-- Actions -->
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <div class="flex items-center justify-end space-x-2">
                  <!-- Quick Actions for Custom Locations -->
                  <template v-if="!isStandardLocation(location)">
                    <button
                      @click="editLocation(location)"
                      class="text-secondary-600 hover:text-secondary-700 p-1 rounded"
                      title="Bearbeiten"
                    >
                      <PencilIcon class="w-4 h-4" />
                    </button>
                    
                    <button
                      @click="duplicateLocation(location)"
                      class="text-secondary-600 hover:text-secondary-700 p-1 rounded"
                      title="Duplizieren"
                    >
                      <DocumentDuplicateIcon class="w-4 h-4" />
                    </button>
                    
                    <button
                      v-if="location.productCount === 0"
                      @click="deleteLocation(location)"
                      class="text-red-600 hover:text-red-700 p-1 rounded"
                      title="Löschen"
                    >
                      <TrashIcon class="w-4 h-4" />
                    </button>
                  </template>
                  
                  <!-- Standard Locations - Only Duplicate -->
                  <template v-else>
                    <button
                      @click="duplicateLocation(location)"
                      class="text-secondary-600 hover:text-secondary-700 p-1 rounded"
                      title="Als Vorlage duplizieren"
                    >
                      <DocumentDuplicateIcon class="w-4 h-4" />
                    </button>
                    <span class="text-xs text-secondary-400 ml-2">Geschützt</span>
                  </template>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="filteredLocations.length === 0 && !locationsStore.loading" class="text-center py-12">
      <div class="w-24 h-24 mx-auto mb-4 bg-secondary-100 rounded-full flex items-center justify-center">
        <MapPinIcon class="w-12 h-12 text-secondary-400" />
      </div>
      <h3 class="text-lg font-medium text-secondary-900 mb-2">
        {{ searchQuery ? 'Keine Standorte gefunden' : 'Noch keine Standorte' }}
      </h3>
      <p class="text-secondary-500 mb-4">
        {{ searchQuery 
          ? 'Versuche einen anderen Suchbegriff oder lösche den Filter.' 
          : 'Erstelle deinen ersten Standort um Produkte zu organisieren.'
        }}
      </p>
      <button v-if="!searchQuery" @click="openCreateModal" class="btn-primary">
        <PlusIcon class="w-5 h-5 mr-2" />
        Ersten Standort erstellen
      </button>
    </div>

    <!-- Create/Edit Modal -->
    <LocationModal
      v-if="showModal"
      :location="editingLocation"
      @close="closeModal"
      @save="handleSaveLocation"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { 
  MagnifyingGlassIcon,
  PlusIcon,
  PencilIcon,
  DocumentDuplicateIcon,
  TrashIcon,
  MapPinIcon,
  // Standard Location Icons
  HomeIcon,
  BuildingOfficeIcon,
  ArchiveBoxIcon,
  BeakerIcon,
  FireIcon,
  CubeTransparentIcon,
  // Custom Location Icons
  HeartIcon,
  StarIcon,
  SunIcon,
  MoonIcon,
  CloudIcon,
  BoltIcon,
  ShieldCheckIcon,
  AcademicCapIcon,
  GlobeAltIcon,
  WrenchScrewdriverIcon,
  GiftIcon
} from '@heroicons/vue/24/outline'
import { useLocationsStore } from '@/stores/locations'
import { useProductsStore } from '@/stores/products'
import LocationModal from '@/components/locations/LocationModal.vue'
import type { Location } from '@/api/locations'
import type { CreateLocationRequest } from '@/api/locations'

// Extended location type with temperature field
interface LocationWithTemperature extends Location {
  temperature?: number
}

// Stores
const locationsStore = useLocationsStore()
const productsStore = useProductsStore()

// State
const searchQuery = ref('')
const sortBy = ref('name')
const showModal = ref(false)
const editingLocation = ref<Location | null>(null)

// Custom icons state for reactivity
const customIcons = ref<Array<{ id: string; svg: string }>>([])

// Load custom icons
const loadCustomIcons = () => {
  try {
    const stored = localStorage.getItem('custom-icons')
    if (stored) {
      customIcons.value = JSON.parse(stored)
    }
  } catch (error) {
    console.error('Failed to load custom icons:', error)
  }
}

// Load data on mount
onMounted(() => {
  locationsStore.fetchLocations()
  productsStore.fetchProducts({ size: 1000 })
  loadCustomIcons()
})

// Computed properties
const locations = computed(() => 
  locationsStore.locations.map(location => ({
    ...location,
    productCount: productsStore.products.filter(p => p.location.id === location.id).length
  }))
)

const totalProductsInLocations = computed(() => 
  locations.value.reduce((sum, location) => sum + location.productCount, 0)
)

const filteredLocations = computed(() => {
  let filtered = locations.value

  // Apply search filter
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(location => 
      location.name.toLowerCase().includes(query) ||
      (location.description && location.description.toLowerCase().includes(query))
    )
  }

  // Apply sorting
  return filtered.sort((a, b) => {
    switch (sortBy.value) {
      case 'name':
        return a.name.localeCompare(b.name)
      case 'productCount':
        return b.productCount - a.productCount
      case 'temperature':
        return ((b as LocationWithTemperature).temperature || -18) - ((a as LocationWithTemperature).temperature || -18)
      case 'created':
        return new Date(b.createdAt || '').getTime() - new Date(a.createdAt || '').getTime()
      default:
        return 0
    }
  })
})

// Standard locations configuration
const standardLocations = [
  'Gefrierschrank',
  'Tiefkühltruhe',
  'Gefrierfach',
  'Keller Tiefkühler',
  'Garage Gefrierschrank',
  'Büro Gefrierschrank'
]

// Helper functions for standard locations
const isStandardLocation = (location: Location): boolean => {
  return standardLocations.includes(location.name)
}

const getLocationIcon = () => {
  // Alle Standard-Standorte verwenden das Standard-Icon
  return CubeTransparentIcon
}

// Icon mapping for custom locations
const iconComponents = {
  ArchiveBoxIcon,
  BuildingOfficeIcon,
  BeakerIcon,
  FireIcon,
  CubeTransparentIcon,
  HeartIcon,
  StarIcon,
  SunIcon,
  MoonIcon,
  CloudIcon,
  BoltIcon,
  ShieldCheckIcon,
  AcademicCapIcon,
  GlobeAltIcon,
  WrenchScrewdriverIcon,
  HomeIcon,
  GiftIcon
}

const getCustomLocationIcon = (location: Location) => {
  const iconName = (location as Location & { icon?: string }).icon
  
  // If no icon is set, use default
  if (!iconName) {
    return CubeTransparentIcon
  }
  
  // Check if it's a standard heroicon
  const standardIcon = iconComponents[iconName as keyof typeof iconComponents]
  if (standardIcon) {
    return standardIcon
  }
  
  // It's a custom icon - return special marker
  return 'custom-svg'
}

const getCustomLocationIconSvg = (location: Location) => {
  const iconName = (location as Location & { icon?: string }).icon
  if (!iconName) return ''
  
  // Use reactive customIcons instead of localStorage directly
  const customIcon = customIcons.value.find((icon) => icon.id === iconName)
  return customIcon?.svg || ''
}

// Modal functions
const openCreateModal = () => {
  editingLocation.value = null
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  editingLocation.value = null
}

const handleSaveLocation = async (locationData: Partial<Location>) => {
  try {
    if (editingLocation.value) {
      await locationsStore.updateLocation(editingLocation.value.id, locationData as Partial<CreateLocationRequest>)
    } else {
      await locationsStore.createLocation(locationData as CreateLocationRequest)
    }
    
    // Reload custom icons to ensure UI updates
    loadCustomIcons()
    
    closeModal()
  } catch (error) {
    console.error('Error saving location:', error)
  }
}

// Location actions
const editLocation = (location: Location) => {
  editingLocation.value = location
  showModal.value = true
}

const duplicateLocation = async (location: Location) => {
  try {
    const duplicatedData = {
      name: `${location.name} (Kopie)`,
      temperature: (location as LocationWithTemperature).temperature,
      description: location.description ? `${location.description} (Kopie)` : undefined,
      icon: (location as Location & { icon?: string }).icon
    }
    await locationsStore.createLocation(duplicatedData)
  } catch (error) {
    console.error('Error duplicating location:', error)
  }
}

const deleteLocation = async (location: Location & { productCount: number }) => {
  if (location.productCount > 0) {
    alert('Dieser Standort kann nicht gelöscht werden, da er noch Produkte enthält.')
    return
  }

  if (confirm(`Möchten Sie den Standort "${location.name}" wirklich löschen?`)) {
    try {
      await locationsStore.deleteLocation(location.id)
    } catch (error) {
      console.error('Error deleting location:', error)
    }
  }
}
</script>