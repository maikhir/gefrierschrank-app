<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
    <div class="bg-white rounded-lg shadow-xl max-w-md w-full max-h-[90vh] overflow-y-auto">
      <!-- Header -->
      <div class="px-6 py-4 border-b border-secondary-200">
        <h3 class="text-lg font-semibold text-secondary-900">
          {{ isEditing ? 'Standort bearbeiten' : 'Neuen Standort erstellen' }}
        </h3>
      </div>

      <!-- Form -->
      <form @submit.prevent="handleSubmit" class="px-6 py-4 space-y-4">
        <!-- Name -->
        <div>
          <label for="name" class="block text-sm font-medium text-secondary-700 mb-1">
            Name <span class="text-red-500">*</span>
          </label>
          <input
            id="name"
            v-model="form.name"
            type="text"
            required
            class="w-full rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
            placeholder="z.B. Gefrierschrank, Tiefkühltruhe, Keller"
          />
          <p v-if="errors.name" class="mt-1 text-sm text-red-600">{{ errors.name }}</p>
        </div>

        <!-- Icon Selection -->
        <div>
          <label class="block text-sm font-medium text-secondary-700 mb-1">
            Symbol
          </label>
          <div class="grid grid-cols-6 gap-2">
            <button
              v-for="iconOption in iconOptions"
              :key="iconOption.name"
              type="button"
              @click="form.icon = iconOption.name"
              class="p-3 rounded-lg border-2 transition-all duration-200 hover:bg-secondary-50"
              :class="form.icon === iconOption.name ? 'border-primary-500 bg-primary-50' : 'border-secondary-300'"
              :title="iconOption.label"
            >
              <!-- Standard Icon -->
              <component 
                v-if="iconOption.type === 'standard'" 
                :is="iconOption.component" 
                class="w-5 h-5 text-secondary-600 mx-auto" 
              />
              <!-- Custom SVG Icon -->
              <div 
                v-else 
                v-html="('svg' in iconOption) ? iconOption.svg : ''" 
                class="w-5 h-5 text-secondary-600 mx-auto flex items-center justify-center"
                style="max-width: 20px; max-height: 20px; overflow: hidden;"
              ></div>
            </button>
          </div>
        </div>

        <!-- Temperature -->
        <div>
          <label for="temperature" class="block text-sm font-medium text-secondary-700 mb-1">
            Temperatur (°C)
          </label>
          <div class="relative">
            <input
              id="temperature"
              v-model.number="form.temperature"
              type="number"
              min="-30"
              max="10"
              step="1"
              class="w-full rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500 pr-16"
              placeholder="-18"
              style="appearance: textfield; -moz-appearance: textfield;"
              @wheel.prevent
            />
            <div class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none temperature-unit">
              <span class="text-secondary-500 text-sm bg-white px-1">°C</span>
            </div>
          </div>
          <!-- Temperature Presets -->
          <div class="mt-2">
            <p class="text-xs text-secondary-600 mb-2">Schnellauswahl:</p>
            <div class="flex flex-wrap gap-2">
              <button
                v-for="preset in temperaturePresets"
                :key="preset.temp"
                type="button"
                @click="form.temperature = preset.temp"
                class="px-3 py-1 text-xs rounded-full border transition-colors duration-200"
                :class="form.temperature === preset.temp 
                  ? 'border-primary-500 bg-primary-50 text-primary-700' 
                  : 'border-secondary-300 bg-white text-secondary-600 hover:bg-secondary-50'"
              >
                {{ preset.label }}
              </button>
            </div>
          </div>
        </div>

        <!-- Description -->
        <div>
          <label for="description" class="block text-sm font-medium text-secondary-700 mb-1">
            Beschreibung
          </label>
          <textarea
            id="description"
            v-model="form.description"
            rows="3"
            class="w-full rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
            placeholder="Optionale Beschreibung des Standorts..."
          ></textarea>
        </div>

        <!-- Preview -->
        <div class="bg-secondary-50 rounded-lg p-4 border">
          <h4 class="text-sm font-medium text-secondary-900 mb-2">Vorschau</h4>
          <div class="flex items-center">
            <div 
              class="w-8 h-8 rounded-lg flex items-center justify-center mr-3"
              style="background-color: #3b82f620; border: 1px solid #3b82f630"
            >
              <!-- Standard Icon -->
              <component 
                v-if="getSelectedIcon() !== 'custom-svg'"
                :is="getSelectedIcon()" 
                class="w-5 h-5"
                style="color: #3b82f6"
              />
              <!-- Custom SVG Icon -->
              <div 
                v-else
                v-html="getSelectedIconSvg()" 
                class="w-5 h-5 flex items-center justify-center"
                style="max-width: 20px; max-height: 20px; overflow: hidden; color: #3b82f6"
              ></div>
            </div>
            <div class="flex-1">
              <div class="font-medium text-secondary-900">
                {{ form.name || 'Standortname' }}
              </div>
              <div class="text-xs text-secondary-500">
                {{ form.temperature || -18 }}°C
              </div>
            </div>
          </div>
          <p v-if="form.description" class="text-sm text-secondary-600 mt-2">
            {{ form.description }}
          </p>
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
          {{ isEditing ? 'Speichern' : 'Erstellen' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { 
  CubeTransparentIcon
} from '@heroicons/vue/24/outline'
import type { Location } from '@/api/locations'

// Extended location type with temperature field
interface LocationWithTemperature extends Location {
  temperature?: number
}

// Props
const props = defineProps<{
  location?: Location | null
}>()

// Emits
const emit = defineEmits<{
  close: []
  save: [data: Partial<Location>]
}>()

// State
const loading = ref(false)
const errors = ref<Record<string, string>>({})

const form = ref({
  name: '',
  temperature: -18,
  description: '',
  icon: 'CubeTransparentIcon'
})

// Computed
const isEditing = computed(() => !!props.location)

const isFormValid = computed(() => {
  return form.value.name.trim().length > 0 && 
         form.value.temperature >= -30 && 
         form.value.temperature <= 10
})

// Temperature presets
const temperaturePresets = [
  { label: '-18°C (Standard)', temp: -18 },
  { label: '-20°C (Sehr kalt)', temp: -20 },
  { label: '-15°C (Mild)', temp: -15 },
  { label: '-10°C (Eisschrank)', temp: -10 },
  { label: '0°C (Kühlschrank)', temp: 0 }
]

// Standard icon options (always available)
const standardIconOptions = [
  { name: 'CubeTransparentIcon', component: CubeTransparentIcon, label: 'Standard Gefrierschrank', type: 'standard' },
]

interface CustomLocationIconOption {
  name: string
  component: null
  label: string
  type: 'custom'
  svg: string
}

// Get custom icons from localStorage
const getCustomIcons = (): CustomLocationIconOption[] => {
  try {
    const stored = localStorage.getItem('custom-icons')
    if (stored) {
      const customIcons = JSON.parse(stored)
      return customIcons.map((icon: { id: string; name: string; svg: string }) => ({
        name: icon.id,
        component: null, // Will render as SVG
        label: icon.name,
        type: 'custom' as const,
        svg: icon.svg
      }))
    }
  } catch (error) {
    console.error('Failed to load custom icons:', error)
  }
  return []
}

// Combined icon options
const iconOptions = computed(() => {
  const customIcons = getCustomIcons()
  return [...standardIconOptions, ...customIcons]
})

// Helper function to get selected icon component or SVG
const getSelectedIcon = () => {
  const iconOption = iconOptions.value.find(option => option.name === form.value.icon)
  if (!iconOption) {
    return CubeTransparentIcon // Fallback to standard icon
  }
  
  if (iconOption.type === 'standard') {
    return iconOption.component
  }
  
  // For custom icons, return a special marker
  return 'custom-svg'
}

const getSelectedIconSvg = () => {
  const iconOption = iconOptions.value.find(option => option.name === form.value.icon)
  return ('svg' in iconOption! && iconOption.svg) ? iconOption.svg : ''
}

// Watch for location prop changes
watch(() => props.location, (newLocation) => {
  if (newLocation) {
    form.value = {
      name: newLocation.name,
      temperature: (newLocation as LocationWithTemperature).temperature || -18,
      description: newLocation.description || '',
      icon: (newLocation as Location & { icon?: string }).icon || 'CubeTransparentIcon'
    }
  } else {
    // Reset form for new location
    form.value = {
      name: '',
      temperature: -18,
      description: '',
      icon: 'CubeTransparentIcon'
    }
  }
}, { immediate: true })

// Initialize form
onMounted(() => {
  if (props.location) {
    form.value = {
      name: props.location.name,
      temperature: (props.location as LocationWithTemperature).temperature || -18,
      description: props.location.description || '',
      icon: (props.location as Location & { icon?: string }).icon || 'CubeTransparentIcon'
    }
  }
})

// Form validation
const validateForm = () => {
  errors.value = {}
  
  if (!form.value.name.trim()) {
    errors.value.name = 'Name ist erforderlich'
  }
  
  if (form.value.name.trim().length > 50) {
    errors.value.name = 'Name darf maximal 50 Zeichen lang sein'
  }
  
  if (form.value.temperature < -30 || form.value.temperature > 10) {
    errors.value.temperature = 'Temperatur muss zwischen -30°C und 10°C liegen'
  }
  
  return Object.keys(errors.value).length === 0
}

// Submit handler
const handleSubmit = async () => {
  if (!validateForm()) return
  
  loading.value = true
  
  try {
    const locationData = {
      name: form.value.name.trim(),
      temperature: form.value.temperature,
      description: form.value.description.trim() || undefined,
      icon: form.value.icon
    }
    
    emit('save', locationData)
  } catch (error) {
    console.error('Error submitting form:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* Hide number input spinner arrows completely */
input[type="number"]::-webkit-outer-spin-button,
input[type="number"]::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
  display: none !important;
  visibility: hidden !important;
  opacity: 0 !important;
  width: 0 !important;
  height: 0 !important;
}

input[type="number"] {
  -moz-appearance: textfield !important;
  appearance: textfield !important;
}

/* Additional fallback for stubborn browsers */
input[type="number"]::-webkit-spinner-button {
  display: none !important;
  -webkit-appearance: none !important;
}

/* Ensure the temperature unit is always visible */
.temperature-unit {
  pointer-events: none;
  z-index: 10;
}
</style>