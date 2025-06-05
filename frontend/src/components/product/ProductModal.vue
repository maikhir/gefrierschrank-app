<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
    <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
      <!-- Header -->
      <div class="px-6 py-4 border-b border-secondary-200">
        <h3 class="text-lg font-semibold text-secondary-900">
          {{ isEditing ? 'Produkt bearbeiten' : 'Neues Produkt hinzufügen' }}
        </h3>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleSubmit" class="px-6 py-4 space-y-6">
        <!-- Basic Information -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <!-- Product Name -->
          <div class="md:col-span-2">
            <label for="name" class="block text-sm font-medium text-secondary-700 mb-1">
              Produktname <span class="text-red-500">*</span>
            </label>
            <input
              id="name"
              v-model="form.name"
              type="text"
              required
              class="w-full rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
              placeholder="z.B. Hähnchenbrust, TK-Erbsen, Vanilleeis"
            />
            <p v-if="errors.name" class="mt-1 text-sm text-red-600">{{ errors.name }}</p>
          </div>

          <!-- Category -->
          <div>
            <label for="categoryId" class="block text-sm font-medium text-secondary-700 mb-1">
              Kategorie <span class="text-red-500">*</span>
            </label>
            <select
              id="categoryId"
              v-model="form.categoryId"
              required
              class="w-full rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
            >
              <option value="">Kategorie auswählen...</option>
              <option
                v-for="category in categories"
                :key="category.id"
                :value="category.id"
              >
                {{ category.name }}
              </option>
            </select>
            <p v-if="errors.categoryId" class="mt-1 text-sm text-red-600">{{ errors.categoryId }}</p>
          </div>

          <!-- Location -->
          <div>
            <label for="locationId" class="block text-sm font-medium text-secondary-700 mb-1">
              Standort <span class="text-red-500">*</span>
            </label>
            <select
              id="locationId"
              v-model="form.locationId"
              required
              class="w-full rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
            >
              <option value="">Standort auswählen...</option>
              <option
                v-for="location in locations"
                :key="location.id"
                :value="location.id"
              >
                {{ location.name }}
              </option>
            </select>
            <p v-if="errors.locationId" class="mt-1 text-sm text-red-600">{{ errors.locationId }}</p>
          </div>
        </div>

        <!-- Quantity and Unit -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div class="md:col-span-2">
            <label for="quantity" class="block text-sm font-medium text-secondary-700 mb-1">
              Menge <span class="text-red-500">*</span>
            </label>
            <input
              id="quantity"
              v-model.number="form.quantity"
              type="number"
              :min="getQuantityStep(form.unit)"
              :step="getQuantityStep(form.unit)"
              required
              class="w-full rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
              placeholder="z.B. 500, 1.5"
            />
            <p v-if="errors.quantity" class="mt-1 text-sm text-red-600">{{ errors.quantity }}</p>
          </div>

          <div>
            <label for="unit" class="block text-sm font-medium text-secondary-700 mb-1">
              Einheit <span class="text-red-500">*</span>
            </label>
            <select
              id="unit"
              v-model="form.unit"
              required
              class="w-full rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
            >
              <option v-for="unit in unitOptions" :key="unit" :value="unit">
                {{ unit }}
              </option>
            </select>
            <p v-if="errors.unit" class="mt-1 text-sm text-red-600">{{ errors.unit }}</p>
          </div>
        </div>

        <!-- Dates -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <!-- Frozen Date -->
          <div>
            <label for="frozenDate" class="block text-sm font-medium text-secondary-700 mb-1">
              Einfrierdatum
            </label>
            <input
              id="frozenDate"
              v-model="form.frozenDate"
              type="date"
              class="w-full rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
            />
            <p class="mt-1 text-xs text-secondary-500">Standard: Heute</p>
          </div>

          <!-- Expiration Date -->
          <div>
            <label for="expirationDate" class="block text-sm font-medium text-secondary-700 mb-1">
              Haltbarkeitsdatum
            </label>
            <input
              id="expirationDate"
              v-model="form.expirationDate"
              type="date"
              class="w-full rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
            />
            <p class="mt-1 text-xs text-secondary-500">
              <span v-if="calculatedExpirationDate">
                Empfohlen: {{ formatDate(calculatedExpirationDate) }}
              </span>
              <button
                v-if="calculatedExpirationDate && form.expirationDate !== calculatedExpirationDate"
                type="button"
                @click="form.expirationDate = calculatedExpirationDate"
                class="ml-2 text-primary-600 hover:text-primary-700 text-xs underline"
              >
                Übernehmen
              </button>
            </p>
          </div>
        </div>

        <!-- Optional Information -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <!-- Notes -->
          <div>
            <label for="notes" class="block text-sm font-medium text-secondary-700 mb-1">
              Notizen
            </label>
            <textarea
              id="notes"
              v-model="form.notes"
              rows="3"
              class="w-full rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
              placeholder="z.B. Bio-Qualität, Marke, besondere Hinweise..."
            ></textarea>
          </div>

          <!-- Barcode -->
          <div>
            <label for="barcode" class="block text-sm font-medium text-secondary-700 mb-1">
              Barcode
            </label>
            <input
              id="barcode"
              v-model="form.barcode"
              type="text"
              class="w-full rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
              placeholder="EAN-Code oder Produktnummer"
            />
          </div>
        </div>


        <!-- Preview -->
        <div class="bg-secondary-50 rounded-lg p-4 border">
          <h4 class="text-sm font-medium text-secondary-900 mb-2">Vorschau</h4>
          <div class="flex items-center space-x-3">
            <div class="w-10 h-10 rounded-lg flex items-center justify-center"
                 :style="{ backgroundColor: selectedCategory?.color + '20' || '#64748b20', border: `1px solid ${selectedCategory?.color || '#64748b'}30` }">
              <div class="w-3 h-3 rounded-full"
                   :style="{ backgroundColor: selectedCategory?.color || '#64748b' }"></div>
            </div>
            <div class="flex-1">
              <p class="font-medium text-secondary-900">
                {{ form.name || 'Produktname' }}
              </p>
              <p class="text-sm text-secondary-600">
                {{ selectedCategory?.name || 'Kategorie' }} • {{ selectedLocation?.name || 'Standort' }}
              </p>
              <p class="text-xs text-secondary-500">
                {{ form.quantity || 0 }} {{ form.unit }} 
                <span v-if="form.expirationDate">
                  • Läuft ab am {{ formatDate(form.expirationDate) }}
                </span>
              </p>
            </div>
          </div>
        </div>
      </form>

      <!-- Actions -->
      <div class="px-6 py-4 border-t border-secondary-200 flex justify-end space-x-3">
        <button
          type="button"
          @click="$emit('close')"
          class="btn-secondary"
        >
          Abbrechen
        </button>
        <button
          @click="handleSubmit"
          :disabled="!isFormValid || loading"
          class="btn-primary"
        >
          <div v-if="loading" class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
          {{ isEditing ? 'Speichern' : 'Hinzufügen' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import type { Product, CreateProductRequest } from '@/api/products'
import type { Category } from '@/api/categories'
import type { Location } from '@/api/locations'

// Props
const props = defineProps<{
  product?: Product | null
  categories: Category[]
  locations: Location[]
}>()

// Emits
const emit = defineEmits<{
  close: []
  save: [data: CreateProductRequest]
}>()

// State
const loading = ref(false)
const errors = ref<Record<string, string>>({})

// Form data
const form = ref<CreateProductRequest & { id?: number }>({
  name: '',
  categoryId: 0,
  locationId: 0,
  quantity: 1,
  unit: 'g',
  frozenDate: new Date().toISOString().split('T')[0],
  expirationDate: '',
  notes: '',
  barcode: ''
})

// Unit options (removed 'Becher')
const unitOptions = ['g', 'kg', 'ml', 'l', 'Stück', 'Packung', 'Dose', 'Glas']

// Unit step mapping for quantity inputs
const getQuantityStep = (unit: string): string => {
  switch (unit) {
    case 'g':
    case 'ml':
    case 'Stück':
    case 'Packung':
    case 'Dose':
    case 'Glas':
      return '1'
    case 'kg':
    case 'l':
      return '0.1'
    default:
      return '0.1'
  }
}

// Computed properties
const isEditing = computed(() => !!props.product)

const selectedCategory = computed(() => 
  props.categories.find(cat => cat.id === form.value.categoryId)
)

const selectedLocation = computed(() => 
  props.locations.find(loc => loc.id === form.value.locationId)
)

const calculatedExpirationDate = computed(() => {
  if (!selectedCategory.value?.defaultStorageDays || !form.value.frozenDate) return null
  
  const frozenDate = new Date(form.value.frozenDate)
  const expirationDate = new Date(frozenDate)
  expirationDate.setDate(expirationDate.getDate() + selectedCategory.value.defaultStorageDays)
  
  return expirationDate.toISOString().split('T')[0]
})

const isFormValid = computed(() => {
  return form.value.name.trim() && 
         form.value.categoryId > 0 && 
         form.value.locationId > 0 && 
         form.value.quantity > 0 && 
         form.value.unit.trim()
})

// Initialize form when product prop changes
watch(() => props.product, (newProduct) => {
  if (newProduct) {
    form.value = {
      id: newProduct.id,
      name: newProduct.name,
      categoryId: newProduct.category.id,
      locationId: newProduct.location.id,
      quantity: newProduct.quantity,
      unit: newProduct.unit,
      frozenDate: newProduct.frozenDate.split('T')[0],
      expirationDate: newProduct.expirationDate ? newProduct.expirationDate.split('T')[0] : '',
      notes: newProduct.notes || '',
      barcode: newProduct.barcode || ''
    }
  } else {
    // Reset form for new product
    form.value = {
      name: '',
      categoryId: 0,
      locationId: 0,
      quantity: 1,
      unit: 'g',
      frozenDate: new Date().toISOString().split('T')[0],
      expirationDate: '',
      notes: '',
      barcode: ''
    }
  }
}, { immediate: true })

// Auto-set expiration date when category changes
watch(() => form.value.categoryId, () => {
  if (!isEditing.value && calculatedExpirationDate.value) {
    form.value.expirationDate = calculatedExpirationDate.value
  }
})

// Auto-adjust quantity when unit changes to match step requirements
watch(() => form.value.unit, (newUnit, oldUnit) => {
  if (oldUnit && newUnit !== oldUnit) {
    adjustQuantityToUnit(newUnit)
  }
})

// Methods
function formatDate(dateString: string): string {
  return new Date(dateString).toLocaleDateString('de-DE')
}

function adjustQuantityToUnit(unit: string) {
  const step = parseFloat(getQuantityStep(unit))
  const currentQuantity = form.value.quantity
  
  // Round up to the nearest step value
  const adjustedQuantity = Math.ceil(currentQuantity / step) * step
  
  // Ensure we have at least the minimum step
  form.value.quantity = Math.max(adjustedQuantity, step)
  
  // Round to avoid floating point precision issues
  form.value.quantity = Math.round(form.value.quantity * 10) / 10
}


function validateForm(): boolean {
  errors.value = {}
  
  if (!form.value.name.trim()) {
    errors.value.name = 'Produktname ist erforderlich'
  }
  
  if (form.value.name.length > 100) {
    errors.value.name = 'Produktname darf maximal 100 Zeichen lang sein'
  }
  
  if (!form.value.categoryId) {
    errors.value.categoryId = 'Kategorie ist erforderlich'
  }
  
  if (!form.value.locationId) {
    errors.value.locationId = 'Standort ist erforderlich'
  }
  
  const minQuantity = parseFloat(getQuantityStep(form.value.unit))
  if (form.value.quantity < minQuantity) {
    errors.value.quantity = `Menge muss mindestens ${minQuantity} ${form.value.unit} sein`
  }
  
  if (!form.value.unit.trim()) {
    errors.value.unit = 'Einheit ist erforderlich'
  }
  
  return Object.keys(errors.value).length === 0
}

async function handleSubmit() {
  if (!validateForm()) return
  
  loading.value = true
  
  try {
    const productData: CreateProductRequest = {
      name: form.value.name.trim(),
      categoryId: form.value.categoryId,
      locationId: form.value.locationId,
      quantity: form.value.quantity,
      unit: form.value.unit,
      frozenDate: form.value.frozenDate || undefined,
      expirationDate: form.value.expirationDate || undefined,
      notes: form.value.notes?.trim() || undefined,
      barcode: form.value.barcode?.trim() || undefined
    }
    
    emit('save', productData)
  } catch (error) {
    console.error('Error submitting form:', error)
  } finally {
    loading.value = false
  }
}

// Initialize form on mount
onMounted(() => {
  if (props.product) {
    // Form is already initialized via watcher
  } else if (calculatedExpirationDate.value) {
    form.value.expirationDate = calculatedExpirationDate.value
  }
})
</script>