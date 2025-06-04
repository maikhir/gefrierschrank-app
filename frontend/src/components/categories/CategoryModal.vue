<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
    <div class="bg-white rounded-lg shadow-xl max-w-md w-full max-h-[90vh] overflow-y-auto">
      <!-- Header -->
      <div class="px-6 py-4 border-b border-secondary-200">
        <h3 class="text-lg font-semibold text-secondary-900">
          {{ isEditing ? 'Kategorie bearbeiten' : 'Neue Kategorie erstellen' }}
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
            placeholder="z.B. Fleisch, Gemüse, Milchprodukte"
          />
          <p v-if="errors.name" class="mt-1 text-sm text-red-600">{{ errors.name }}</p>
        </div>

        <!-- Icon Selection -->
        <div>
          <label class="block text-sm font-medium text-secondary-700 mb-1">
            Icon
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

        <!-- Color -->
        <div>
          <label for="color" class="block text-sm font-medium text-secondary-700 mb-1">
            Farbe
          </label>
          <div class="flex items-center space-x-3">
            <!-- Color Input -->
            <input
              id="color"
              v-model="form.color"
              type="color"
              class="w-12 h-10 border border-secondary-300 rounded-md cursor-pointer"
            />
            <!-- Color Input Text -->
            <input
              v-model="form.color"
              type="text"
              class="flex-1 rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
              placeholder="#64748b"
              pattern="^#[0-9A-Fa-f]{6}$"
            />
          </div>
          <!-- Color Presets -->
          <div class="mt-3">
            <p class="text-xs text-secondary-600 mb-2">Häufig verwendete Farben:</p>
            <div class="flex space-x-2">
              <button
                v-for="color in colorPresets"
                :key="color"
                type="button"
                @click="form.color = color"
                class="w-8 h-8 rounded-md border-2 transition-all duration-200"
                :style="{ backgroundColor: color }"
                :class="form.color === color ? 'border-secondary-800' : 'border-secondary-300'"
              ></button>
            </div>
          </div>
        </div>

        <!-- Default Storage Days -->
        <div>
          <label for="defaultStorageDays" class="block text-sm font-medium text-secondary-700 mb-1">
            Standard-Lagerdauer (Tage)
          </label>
          <div class="relative">
            <input
              id="defaultStorageDays"
              v-model.number="form.defaultStorageDays"
              type="number"
              min="1"
              max="3650"
              class="w-full rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
              placeholder="365"
            />
            <div class="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
              <span class="text-secondary-500 text-sm">Tage</span>
            </div>
          </div>
          <!-- Storage Duration Presets -->
          <div class="mt-2">
            <p class="text-xs text-secondary-600 mb-2">Schnellauswahl:</p>
            <div class="flex flex-wrap gap-2">
              <button
                v-for="preset in storageDayPresets"
                :key="preset.days"
                type="button"
                @click="form.defaultStorageDays = preset.days"
                class="px-3 py-1 text-xs rounded-full border transition-colors duration-200"
                :class="form.defaultStorageDays === preset.days 
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
            placeholder="Optionale Beschreibung der Kategorie..."
          ></textarea>
        </div>

        <!-- Preview -->
        <div class="bg-secondary-50 rounded-lg p-4 border">
          <h4 class="text-sm font-medium text-secondary-900 mb-2">Vorschau</h4>
          <div class="flex items-center">
            <div 
              class="w-8 h-8 rounded-lg flex items-center justify-center mr-3"
              :style="{ backgroundColor: form.color + '20', border: `1px solid ${form.color}30` }"
            >
              <!-- Standard Icon -->
              <component 
                v-if="getSelectedIcon() !== 'custom-svg'"
                :is="getSelectedIcon()" 
                class="w-5 h-5"
                :style="{ color: form.color || '#64748b' }"
              />
              <!-- Custom SVG Icon -->
              <div 
                v-else
                v-html="getSelectedIconSvg()" 
                class="w-5 h-5 flex items-center justify-center"
                style="max-width: 20px; max-height: 20px; overflow: hidden;"
                :style="{ color: form.color || '#64748b' }"
              ></div>
            </div>
            <span class="font-medium text-secondary-900">
              {{ form.name || 'Kategorienname' }}
            </span>
            <span class="ml-auto text-xs bg-secondary-100 text-secondary-600 px-2 py-0.5 rounded-full">
              {{ form.defaultStorageDays || 0 }} Tage
            </span>
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
  ArchiveBoxIcon
} from '@heroicons/vue/24/outline'
import type { Category } from '@/api/categories'

// Props
const props = defineProps<{
  category?: Category | null
}>()

// Emits
const emit = defineEmits<{
  close: []
  save: [data: Partial<Category>]
}>()

// State
const loading = ref(false)
const errors = ref<Record<string, string>>({})

const form = ref({
  name: '',
  color: '#64748b',
  defaultStorageDays: 365,
  description: '',
  icon: 'ArchiveBoxIcon'
})

// Computed
const isEditing = computed(() => !!props.category)

const isFormValid = computed(() => {
  return form.value.name.trim().length > 0 && 
         form.value.defaultStorageDays > 0 &&
         /^#[0-9A-Fa-f]{6}$/i.test(form.value.color)
})

// Color presets
const colorPresets = [
  '#ef4444', // Red
  '#f59e0b', // Amber  
  '#22c55e', // Green
  '#06b6d4', // Cyan
  '#3b82f6', // Blue
  '#8b5cf6', // Violet
  '#ec4899', // Pink
  '#64748b'  // Slate
]

// Storage day presets
const storageDayPresets = [
  { label: '1 Woche', days: 7 },
  { label: '2 Wochen', days: 14 },
  { label: '1 Monat', days: 30 },
  { label: '2 Monate', days: 60 },
  { label: '3 Monate', days: 90 },
  { label: '6 Monate', days: 180 },
  { label: '1 Jahr', days: 365 }
]

// Standard icon options (always available)
const standardIconOptions = [
  { name: 'ArchiveBoxIcon', component: ArchiveBoxIcon, label: 'Standard', type: 'standard' },
]

interface CustomIconOption {
  name: string
  component: null
  label: string
  type: 'custom'
  svg: string
}

// Get custom icons from localStorage
const getCustomIcons = (): CustomIconOption[] => {
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
    return ArchiveBoxIcon // Fallback to standard icon
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

// Watch for category prop changes
watch(() => props.category, (newCategory) => {
  if (newCategory) {
    form.value = {
      name: newCategory.name,
      color: newCategory.color || '#64748b',
      defaultStorageDays: newCategory.defaultStorageDays || 365,
      description: newCategory.description || '',
      icon: newCategory.icon || 'ArchiveBoxIcon'
    }
  } else {
    // Reset form for new category
    form.value = {
      name: '',
      color: '#64748b',
      defaultStorageDays: 365,
      description: '',
      icon: 'ArchiveBoxIcon'
    }
  }
}, { immediate: true })

// Initialize form
onMounted(() => {
  if (props.category) {
    form.value = {
      name: props.category.name,
      color: props.category.color || '#64748b',
      defaultStorageDays: props.category.defaultStorageDays || 365,
      description: props.category.description || '',
      icon: props.category.icon || 'ArchiveBoxIcon'
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
  
  if (!/^#[0-9A-Fa-f]{6}$/i.test(form.value.color)) {
    errors.value.color = 'Ungültiges Farbformat (muss #RRGGBB sein)'
  }
  
  if (form.value.defaultStorageDays < 1 || form.value.defaultStorageDays > 3650) {
    errors.value.defaultStorageDays = 'Lagerdauer muss zwischen 1 und 3650 Tagen liegen'
  }
  
  return Object.keys(errors.value).length === 0
}

// Submit handler
const handleSubmit = async () => {
  if (!validateForm()) return
  
  loading.value = true
  
  try {
    const categoryData: Partial<Category> = {
      name: form.value.name.trim(),
      color: form.value.color,
      defaultStorageDays: form.value.defaultStorageDays,
      description: form.value.description.trim() || undefined,
      icon: form.value.icon
    }
    
    emit('save', categoryData)
  } catch (error) {
    console.error('Error submitting form:', error)
  } finally {
    loading.value = false
  }
}
</script>