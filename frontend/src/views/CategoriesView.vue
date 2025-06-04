<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="p-6">
    <!-- Header -->
    <div class="mb-8">
      <h2 class="text-2xl font-bold text-secondary-900 mb-2">
        Kategorien verwalten
      </h2>
      <p class="text-secondary-600">
        {{ categories.length }} Kategorien • {{ totalProductsInCategories }} Produkte zugeordnet
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
              placeholder="Kategorien suchen..."
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
            <option value="type">Typ</option>
            <option value="productCount">Produktanzahl</option>
            <option value="storageDays">Lagerdauer</option>
            <option value="created">Erstellt</option>
          </select>
        </div>

        <!-- Add Category Button -->
        <button
          @click="openCreateModal"
          class="btn-primary flex items-center"
        >
          <PlusIcon class="w-4 h-4 mr-2" />
          Neue Kategorie
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="categoriesStore.loading" class="text-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500 mx-auto"></div>
      <p class="mt-4 text-secondary-600">Lade Kategorien...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="categoriesStore.error" class="text-center py-12">
      <div class="text-red-500 mb-4">❌ Fehler beim Laden der Kategorien</div>
      <p class="text-secondary-600">{{ categoriesStore.error }}</p>
      <button @click="categoriesStore.fetchCategories()" class="mt-4 btn-primary">
        Erneut versuchen
      </button>
    </div>

    <!-- Categories List (Compact) -->
    <div v-else class="card overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-secondary-200">
          <!-- Table Header -->
          <thead class="bg-secondary-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-secondary-500 uppercase tracking-wider">
                Kategorie
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-secondary-500 uppercase tracking-wider">
                Produkte
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-secondary-500 uppercase tracking-wider">
                Lagerdauer
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
              v-for="category in filteredCategories" 
              :key="category.id"
              class="hover:bg-secondary-50 transition-colors duration-150"
            >
              <!-- Category Name & Icon -->
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="flex-shrink-0 mr-3">
                    <div v-if="isStandardCategory(category)" class="w-8 h-8 rounded-lg flex items-center justify-center bg-secondary-100">
                      <component :is="getCategoryIcon()" class="w-5 h-5 text-secondary-600" />
                    </div>
                    <div v-else 
                      class="w-8 h-8 rounded-lg flex items-center justify-center"
                      :style="{ backgroundColor: category.color + '20', border: `1px solid ${category.color}30` }"
                    >
                      <!-- Standard Icon -->
                      <component 
                        v-if="getCustomCategoryIcon(category) !== 'custom-svg'"
                        :is="getCustomCategoryIcon(category)" 
                        class="w-5 h-5"
                        :style="{ color: category.color || '#64748b' }"
                      />
                      <!-- Custom SVG Icon -->
                      <div 
                        v-else
                        v-html="getCustomCategoryIconSvg(category)" 
                        class="w-5 h-5 flex items-center justify-center"
                        style="max-width: 20px; max-height: 20px; overflow: hidden;"
                        :style="{ color: category.color || '#64748b' }"
                      ></div>
                    </div>
                  </div>
                  <div>
                    <div class="text-sm font-medium text-secondary-900">{{ category.name }}</div>
                    <div v-if="category.description" class="text-xs text-secondary-500 truncate max-w-xs">
                      {{ category.description }}
                    </div>
                  </div>
                </div>
              </td>
              
              <!-- Product Count -->
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                      :class="category.productCount > 0 ? 'bg-primary-100 text-primary-800' : 'bg-secondary-100 text-secondary-800'">
                  {{ category.productCount }}
                </span>
              </td>
              
              <!-- Storage Days -->
              <td class="px-6 py-4 whitespace-nowrap text-sm text-secondary-900">
                {{ category.defaultStorageDays }} Tage
              </td>
              
              <!-- Category Type -->
              <td class="px-6 py-4 whitespace-nowrap">
                <span v-if="isStandardCategory(category)" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                  Standard
                </span>
                <span v-else class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                  Benutzerdefiniert
                </span>
              </td>
              
              <!-- Actions -->
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <div class="flex items-center justify-end space-x-2">
                  <!-- Quick Actions for Custom Categories -->
                  <template v-if="!isStandardCategory(category)">
                    <button
                      @click="editCategory(category)"
                      class="text-secondary-600 hover:text-secondary-700 p-1 rounded"
                      title="Bearbeiten"
                    >
                      <PencilIcon class="w-4 h-4" />
                    </button>
                    
                    <button
                      @click="duplicateCategory(category)"
                      class="text-secondary-600 hover:text-secondary-700 p-1 rounded"
                      title="Duplizieren"
                    >
                      <DocumentDuplicateIcon class="w-4 h-4" />
                    </button>
                    
                    <button
                      v-if="category.productCount === 0"
                      @click="deleteCategory(category)"
                      class="text-red-600 hover:text-red-700 p-1 rounded"
                      title="Löschen"
                    >
                      <TrashIcon class="w-4 h-4" />
                    </button>
                  </template>
                  
                  <!-- Standard Categories - Only Duplicate -->
                  <template v-else>
                    <button
                      @click="duplicateCategory(category)"
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
    <div v-if="filteredCategories.length === 0 && !categoriesStore.loading" class="text-center py-12">
      <div class="w-24 h-24 mx-auto mb-4 bg-secondary-100 rounded-full flex items-center justify-center">
        <TagIcon class="w-12 h-12 text-secondary-400" />
      </div>
      <h3 class="text-lg font-medium text-secondary-900 mb-2">
        {{ searchQuery ? 'Keine Kategorien gefunden' : 'Noch keine Kategorien' }}
      </h3>
      <p class="text-secondary-500 mb-4">
        {{ searchQuery 
          ? 'Versuche einen anderen Suchbegriff oder lösche den Filter.' 
          : 'Erstelle deine erste Kategorie um Produkte zu organisieren.'
        }}
      </p>
      <button v-if="!searchQuery" @click="openCreateModal" class="btn-primary">
        <PlusIcon class="w-5 h-5 mr-2" />
        Erste Kategorie erstellen
      </button>
    </div>

    <!-- Create/Edit Modal -->
    <CategoryModal
      v-if="showModal"
      :category="editingCategory"
      @close="closeModal"
      @save="handleSaveCategory"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { 
  MagnifyingGlassIcon,
  PlusIcon,
  PencilIcon,
  DocumentDuplicateIcon,
  TrashIcon,
  TagIcon,
  // Standard Category Icons
  CubeIcon,
  BeakerIcon,
  CakeIcon,
  SparklesIcon,
  FireIcon,
  ArchiveBoxIcon,
  // Custom Category Icons
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
} from '@heroicons/vue/24/outline'
import { useCategoriesStore } from '@/stores/categories'
import { useProductsStore } from '@/stores/products'
import CategoryModal from '@/components/categories/CategoryModal.vue'
import type { Category } from '@/api/categories'

// Stores
const categoriesStore = useCategoriesStore()
const productsStore = useProductsStore()

// State
const searchQuery = ref('')
const sortBy = ref('name')
const showModal = ref(false)
const editingCategory = ref<Category | null>(null)
const activeActionsMenu = ref<number | null>(null)

// Custom icons state for reactivity
const customIcons = ref<Array<{ id: string; svg: string }>>([])

// Standard categories state for reactivity
const standardCategoryNames = ref<string[]>([])

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

// Load standard categories
const loadStandardCategories = () => {
  standardCategoryNames.value = getStandardCategoryNames()
}

// Load data on mount
onMounted(() => {
  categoriesStore.fetchCategories()
  productsStore.fetchProducts({ size: 1000 })
  loadCustomIcons()
  loadStandardCategories()
  
  // Close dropdown menus when clicking outside
  document.addEventListener('click', closeActionsMenu)
})

onUnmounted(() => {
  document.removeEventListener('click', closeActionsMenu)
})

// Computed properties
const categories = computed(() => 
  categoriesStore.categories.map(category => ({
    ...category,
    productCount: productsStore.products.filter(p => p.category.id === category.id).length
  }))
)

const totalProductsInCategories = computed(() => 
  categories.value.reduce((sum, category) => sum + category.productCount, 0)
)

const filteredCategories = computed(() => {
  let filtered = categories.value

  // Apply search filter
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(category => 
      category.name.toLowerCase().includes(query) ||
      (category.description && category.description.toLowerCase().includes(query))
    )
  }

  // Apply sorting
  return filtered.sort((a, b) => {
    switch (sortBy.value) {
      case 'name':
        return a.name.localeCompare(b.name)
      case 'type':
        // Sort by type: Standard categories first, then custom categories
        const aIsStandard = isStandardCategory(a)
        const bIsStandard = isStandardCategory(b)
        if (aIsStandard === bIsStandard) {
          // If both are the same type, sort by name
          return a.name.localeCompare(b.name)
        }
        // Standard categories come first
        return aIsStandard ? -1 : 1
      case 'productCount':
        return b.productCount - a.productCount
      case 'storageDays':
        return (b.defaultStorageDays || 0) - (a.defaultStorageDays || 0)
      case 'created':
        return new Date(b.createdAt || '').getTime() - new Date(a.createdAt || '').getTime()
      default:
        return 0
    }
  })
})

// Modal functions
const openCreateModal = () => {
  editingCategory.value = null
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  editingCategory.value = null
}

const handleSaveCategory = async (categoryData: Partial<Category>) => {
  try {
    // Convert Category data to CreateCategoryRequest format
    const requestData = {
      name: categoryData.name || '',
      description: categoryData.description || undefined,
      color: categoryData.color || undefined,
      defaultStorageDays: categoryData.defaultStorageDays || undefined,
      icon: categoryData.icon || undefined
    }
    
    if (editingCategory.value) {
      await categoriesStore.updateCategory(editingCategory.value.id, requestData)
    } else {
      await categoriesStore.createCategory(requestData)
    }
    
    // Reload custom icons and standard categories to ensure UI updates
    loadCustomIcons()
    loadStandardCategories()
    
    closeModal()
  } catch (error) {
    console.error('Error saving category:', error)
  }
}

// Category actions
const editCategory = (category: Category) => {
  editingCategory.value = category
  showModal.value = true
  activeActionsMenu.value = null
}

const duplicateCategory = async (category: Category) => {
  try {
    const duplicatedData = {
      name: `${category.name} (Kopie)`,
      color: category.color || undefined,
      defaultStorageDays: category.defaultStorageDays || undefined,
      description: category.description ? `${category.description} (Kopie)` : undefined,
      icon: category.icon || undefined
    }
    await categoriesStore.createCategory(duplicatedData)
    activeActionsMenu.value = null
  } catch (error) {
    console.error('Error duplicating category:', error)
  }
}

const deleteCategory = async (category: Category & { productCount: number }) => {
  if (category.productCount > 0) {
    alert('Diese Kategorie kann nicht gelöscht werden, da sie noch Produkte enthält.')
    return
  }

  if (confirm(`Möchten Sie die Kategorie "${category.name}" wirklich löschen?`)) {
    try {
      await categoriesStore.deleteCategory(category.id)
      activeActionsMenu.value = null
    } catch (error) {
      console.error('Error deleting category:', error)
    }
  }
}

// Actions menu (commented out as not currently used but may be needed later)
// const toggleActionsMenu = (categoryId: number) => {
//   activeActionsMenu.value = activeActionsMenu.value === categoryId ? null : categoryId
// }

const closeActionsMenu = () => {
  activeActionsMenu.value = null
}

// Load standard categories from settings (configurable)
const getStandardCategoryNames = (): string[] => {
  try {
    const stored = localStorage.getItem('standard-categories-template')
    if (stored) {
      const template = JSON.parse(stored)
      return template.map((cat: { name: string }) => cat.name)
    }
  } catch (error) {
    console.error('Failed to load standard categories template:', error)
  }
  
  // Fallback to default standard categories
  return [
    'Fleisch',
    'Gemüse',
    'Fertiggerichte', 
    'Brot & Backwaren',
    'Eis & Desserts',
    'Reste'
  ]
}

// Helper functions for standard categories
const isStandardCategory = (category: Category): boolean => {
  return standardCategoryNames.value.includes(category.name)
}

const getCategoryIcon = () => {
  // Alle Standardkategorien verwenden das Standard-Icon
  return ArchiveBoxIcon
}

// Icon mapping for custom categories
const iconComponents = {
  ArchiveBoxIcon,
  CubeIcon,
  BeakerIcon,
  CakeIcon,
  SparklesIcon,
  FireIcon,
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

const getCustomCategoryIcon = (category: Category) => {
  const iconName = category.icon
  
  // If no icon is set, use default
  if (!iconName) {
    return ArchiveBoxIcon
  }
  
  // Check if it's a standard heroicon
  const standardIcon = iconComponents[iconName as keyof typeof iconComponents]
  if (standardIcon) {
    return standardIcon
  }
  
  // It's a custom icon - return special marker
  return 'custom-svg'
}

const getCustomCategoryIconSvg = (category: Category) => {
  const iconName = category.icon
  if (!iconName) return ''
  
  // Use reactive customIcons instead of localStorage directly
  const customIcon = customIcons.value.find((icon) => icon.id === iconName)
  return customIcon?.svg || ''
}

// Make reload functions available globally for Settings
interface WindowWithReload extends Window {
  reloadStandardCategories?: () => void
}
(window as WindowWithReload).reloadStandardCategories = loadStandardCategories
</script>