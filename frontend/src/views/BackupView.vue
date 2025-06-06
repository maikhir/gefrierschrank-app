<template>
  <div class="container mx-auto px-4 py-6">
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold text-gray-900">
        Datensicherung
      </h1>
    </div>

    <!-- Export Section -->
    <div class="bg-white rounded-lg shadow mb-6">
      <div class="p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">
          Daten exportieren
        </h2>
        <p class="text-gray-600 mb-4">
          Erstellen Sie ein Backup aller Ihrer Daten (Produkte, Kategorien, Standorte).
        </p>
        
        <div class="flex flex-col sm:flex-row gap-4">
          <button
            @click="exportBackup('json')"
            :disabled="isExporting"
            class="flex items-center justify-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg v-if="isExporting" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            JSON Export
          </button>
          
          <button
            @click="exportBackup('zip')"
            :disabled="isExporting"
            class="flex items-center justify-center px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            <svg v-if="isExporting" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            ZIP Export
          </button>
        </div>
      </div>
    </div>

    <!-- Import Section -->
    <div class="bg-white rounded-lg shadow mb-6">
      <div class="p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">
          Daten importieren
        </h2>
        <p class="text-gray-600 mb-4">
          Stellen Sie Ihre Daten aus einer Backup-Datei wieder her.
        </p>

        <!-- File Upload -->
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-2">
            Backup-Datei auswählen
          </label>
          <input
            ref="fileInput"
            type="file"
            accept=".json,.zip"
            @change="handleFileSelect"
            class="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
          />
        </div>

        <!-- Import Options -->
        <div class="mb-4 space-y-3">
          <label class="flex items-center">
            <input
              v-model="importOptions.clearExistingData"
              type="checkbox"
              class="rounded border-gray-300 text-blue-600 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
            />
            <span class="ml-2 text-sm text-gray-700">
              Alle vorhandenen Daten vor dem Import löschen
            </span>
          </label>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">
              Konfliktbehandlung
            </label>
            <select
              v-model="importOptions.conflictResolution"
              class="block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50 text-gray-900 bg-white"
            >
              <option value="SKIP">Überspringen</option>
              <option value="OVERWRITE">Überschreiben</option>
              <option value="RENAME">Umbenennen</option>
            </select>
          </div>

          <label class="flex items-center">
            <input
              v-model="importOptions.preserveIds"
              type="checkbox"
              class="rounded border-gray-300 text-blue-600 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
            />
            <span class="ml-2 text-sm text-gray-700">
              Original-IDs beibehalten
            </span>
          </label>
        </div>

        <!-- Import Button -->
        <button
          @click="importBackup"
          :disabled="!selectedFile || isImporting"
          class="flex items-center justify-center px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <svg v-if="isImporting" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          Backup importieren
        </button>
      </div>
    </div>

    <!-- Danger Zone -->
    <div class="bg-white rounded-lg shadow border-red-200">
      <div class="p-6">
        <h2 class="text-lg font-semibold text-red-600 mb-4">
          Gefahrenbereich
        </h2>
        <p class="text-gray-600 mb-4">
          Diese Aktion löscht alle Ihre Daten unwiderruflich.
        </p>
        
        <button
          @click="showClearConfirmation = true"
          :disabled="isClearing"
          class="flex items-center justify-center px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <svg v-if="isClearing" class="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          Alle Daten löschen
        </button>
      </div>
    </div>

    <!-- Import Result Modal -->
    <div v-if="importResult" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50" @click="importResult = null">
      <div class="relative top-20 mx-auto p-5 border w-11/12 md:w-2/3 lg:w-1/2 shadow-lg rounded-md bg-white" @click.stop>
        <div class="mt-3">
          <div class="flex items-center mb-4">
            <div class="mx-auto flex-shrink-0 flex items-center justify-center h-12 w-12 rounded-full" 
                 :class="importResult.success ? 'bg-green-100' : 'bg-red-100'">
              <svg v-if="importResult.success" class="h-6 w-6 text-green-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
              </svg>
              <svg v-else class="h-6 w-6 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </div>
            <div class="ml-4">
              <h3 class="text-lg font-medium text-gray-900">
                Import {{ importResult.success ? 'erfolgreich' : 'fehlgeschlagen' }}
              </h3>
            </div>
          </div>

          <div class="mb-4">
            <p class="text-sm text-gray-500 mb-2">{{ importResult.message }}</p>
            
            <!-- Statistics -->
            <div v-if="importResult.stats" class="bg-gray-50 rounded p-3 mb-3">
              <h4 class="font-medium text-gray-900 mb-2">Statistiken:</h4>
              <div class="grid grid-cols-2 gap-2 text-sm text-gray-700">
                <div>Kategorien: {{ importResult.stats.categoriesImported }} importiert, {{ importResult.stats.categoriesSkipped }} übersprungen</div>
                <div>Standorte: {{ importResult.stats.locationsImported }} importiert, {{ importResult.stats.locationsSkipped }} übersprungen</div>
                <div>Produkte: {{ importResult.stats.productsImported }} importiert, {{ importResult.stats.productsSkipped }} übersprungen</div>
              </div>
            </div>

            <!-- Conflicts -->
            <div v-if="importResult.conflicts && importResult.conflicts.length > 0" class="bg-yellow-50 rounded p-3 mb-3">
              <h4 class="font-medium text-yellow-800 mb-2">Konflikte:</h4>
              <ul class="text-sm text-yellow-700">
                <li v-for="conflict in importResult.conflicts" :key="conflict.name">
                  {{ conflict.type }}: {{ conflict.name }} - {{ conflict.resolution }}
                </li>
              </ul>
            </div>

            <!-- Errors -->
            <div v-if="importResult.errors && importResult.errors.length > 0" class="bg-red-50 rounded p-3 mb-3">
              <h4 class="font-medium text-red-800 mb-2">Fehler:</h4>
              <ul class="text-sm text-red-700">
                <li v-for="error in importResult.errors" :key="error">{{ error }}</li>
              </ul>
            </div>

            <!-- Warnings -->
            <div v-if="importResult.warnings && importResult.warnings.length > 0" class="bg-orange-50 rounded p-3">
              <h4 class="font-medium text-orange-800 mb-2">Warnungen:</h4>
              <ul class="text-sm text-orange-700">
                <li v-for="warning in importResult.warnings" :key="warning">{{ warning }}</li>
              </ul>
            </div>
          </div>

          <div class="flex justify-end">
            <button
              @click="importResult = null"
              class="px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400"
            >
              Schließen
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Clear Confirmation Modal -->
    <div v-if="showClearConfirmation" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50" @click="showClearConfirmation = false">
      <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white" @click.stop>
        <div class="mt-3 text-center">
          <div class="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-red-100 mb-4">
            <svg class="h-6 w-6 text-red-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
            </svg>
          </div>
          <h3 class="text-lg font-medium text-gray-900 mb-2">
            Alle Daten löschen?
          </h3>
          <p class="text-sm text-gray-500 mb-4">
            Diese Aktion kann nicht rückgängig gemacht werden. Alle Produkte, Kategorien und Standorte werden dauerhaft gelöscht.
          </p>
          <div class="flex justify-center space-x-3">
            <button
              @click="showClearConfirmation = false"
              class="px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400"
            >
              Abbrechen
            </button>
            <button
              @click="clearAllData"
              class="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
            >
              Löschen
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { backupApi } from '../api/backup'
import { useCategoriesStore } from '@/stores/categories'
import { useLocationsStore } from '@/stores/locations'
import { useProductsStore } from '@/stores/products'

// Stores
const categoriesStore = useCategoriesStore()
const locationsStore = useLocationsStore()
const productsStore = useProductsStore()

const isExporting = ref(false)
const isImporting = ref(false)
const isClearing = ref(false)
const selectedFile = ref<File | null>(null)
const fileInput = ref<HTMLInputElement>()
const importResult = ref<any>(null)
const showClearConfirmation = ref(false)

const importOptions = ref({
  clearExistingData: false,
  conflictResolution: 'SKIP' as 'SKIP' | 'OVERWRITE' | 'RENAME',
  preserveIds: false
})

const exportBackup = async (format: 'json' | 'zip') => {
  isExporting.value = true
  try {
    await backupApi.exportBackup(format)
  } catch (error) {
    console.error('Export failed:', error)
    alert('Export fehlgeschlagen. Bitte versuchen Sie es erneut.')
  } finally {
    isExporting.value = false
  }
}

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    selectedFile.value = target.files[0]
  }
}

const importBackup = async () => {
  if (!selectedFile.value) return
  
  isImporting.value = true
  try {
    const result = await backupApi.importBackup(
      selectedFile.value,
      importOptions.value.clearExistingData,
      importOptions.value.conflictResolution,
      importOptions.value.preserveIds
    )
    
    importResult.value = result
    
    // Aktualisiere alle Stores nach erfolgreichem Import
    if (result.success) {
      await Promise.all([
        categoriesStore.fetchCategories(),
        locationsStore.fetchLocations(),
        productsStore.fetchProducts()
      ])
    }
    
    // Reset file input
    if (fileInput.value) {
      fileInput.value.value = ''
    }
    selectedFile.value = null
    
  } catch (error) {
    console.error('Import failed:', error)
    importResult.value = {
      success: false,
      message: 'Import fehlgeschlagen: ' + (error as Error).message,
      errors: [(error as Error).message]
    }
  } finally {
    isImporting.value = false
  }
}

const clearAllData = async () => {
  isClearing.value = true
  showClearConfirmation.value = false
  
  try {
    await backupApi.clearAllData()
    
    // Aktualisiere alle Stores nach dem Löschen
    await Promise.all([
      categoriesStore.fetchCategories(),
      locationsStore.fetchLocations(),
      productsStore.fetchProducts()
    ])
    
    alert('Alle Daten wurden erfolgreich gelöscht.')
  } catch (error) {
    console.error('Clear failed:', error)
    alert('Fehler beim Löschen der Daten. Bitte versuchen Sie es erneut.')
  } finally {
    isClearing.value = false
  }
}
</script>