<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="p-6">
    <!-- Header -->
    <div class="mb-8">
      <h2 class="text-2xl font-bold text-secondary-900 mb-2">
        Einstellungen
      </h2>
      <p class="text-secondary-600">
        Verwalte deine App-Einstellungen und Präferenzen
      </p>
    </div>

    <!-- Settings Sections -->
    <div class="space-y-6">
      
      <!-- Notifications -->
      <div class="card p-6">
        <h3 class="text-lg font-semibold text-secondary-900 mb-4">
          Benachrichtigungen
        </h3>
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <div>
              <label class="text-sm font-medium text-secondary-900">Ablaufwarnungen</label>
              <p class="text-sm text-secondary-600">Erhalte Benachrichtigungen für ablaufende Produkte</p>
            </div>
            <button
              @click="toggleSetting('expirationNotifications')"
              :class="settings.expirationNotifications ? 'bg-primary-600' : 'bg-secondary-200'"
              class="relative inline-flex flex-shrink-0 h-6 w-11 border-2 border-transparent rounded-full cursor-pointer transition-colors ease-in-out duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
            >
              <span
                :class="settings.expirationNotifications ? 'translate-x-5' : 'translate-x-0'"
                class="pointer-events-none inline-block h-5 w-5 rounded-full bg-white shadow transform ring-0 transition ease-in-out duration-200"
              />
            </button>
          </div>

          <div class="flex items-center justify-between">
            <div>
              <label class="text-sm font-medium text-secondary-900">Warntage vorher</label>
              <p class="text-sm text-secondary-600">Anzahl Tage vor Ablauf für Warnungen</p>
            </div>
            <select 
              v-model="settings.notificationDays"
              class="rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
            >
              <option value="1">1 Tag</option>
              <option value="3">3 Tage</option>
              <option value="7">7 Tage</option>
              <option value="14">14 Tage</option>
            </select>
          </div>
        </div>
      </div>

      <!-- Data Management -->
      <div class="card p-6">
        <h3 class="text-lg font-semibold text-secondary-900 mb-4">
          Datenverwaltung
        </h3>
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <div>
              <label class="text-sm font-medium text-secondary-900">Automatisches Löschen</label>
              <p class="text-sm text-secondary-600">Lösche abgelaufene Produkte automatisch nach</p>
            </div>
            <select 
              v-model="settings.autoDeleteDays"
              class="rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
            >
              <option value="0">Niemals</option>
              <option value="30">30 Tage</option>
              <option value="60">60 Tage</option>
              <option value="90">90 Tage</option>
            </select>
          </div>

          <div class="pt-4 border-t border-secondary-300">
            <button
              @click="exportData"
              class="btn-secondary mr-3"
            >
              <ArrowDownTrayIcon class="w-4 h-4 mr-2" />
              Daten exportieren
            </button>
            <button
              @click="clearAllData"
              class="text-red-600 hover:text-red-700 font-medium text-sm"
            >
              <TrashIcon class="w-4 h-4 mr-2 inline" />
              Alle Daten löschen
            </button>
          </div>
        </div>
      </div>

      <!-- Standard Categories Management -->
      <div class="card p-6">
        <h3 class="text-lg font-semibold text-secondary-900 mb-4">
          Standard-Kategorien
        </h3>
        <p class="text-sm text-secondary-600 mb-4">
          Verwalte die Standard-Kategorien, die beim ersten Start der App erstellt werden.
        </p>
        
        <div class="space-y-4">
          <!-- Category Count Info -->
          <div class="bg-secondary-50 rounded-lg p-4 border">
            <div class="flex items-center justify-between">
              <div>
                <h4 class="text-sm font-medium text-secondary-900">Aktuelle Kategorien</h4>
                <p class="text-sm text-secondary-600">{{ categoriesStore.categories.length }} Kategorien verfügbar</p>
              </div>
              <button
                @click="refreshCategoriesInfo"
                :disabled="loadingCategories"
                class="text-sm text-primary-600 hover:text-primary-700 font-medium"
              >
                {{ loadingCategories ? 'Laden...' : 'Aktualisieren' }}
              </button>
            </div>
          </div>

          <!-- Standard Categories Actions -->
          <div class="flex flex-wrap gap-3">
            <button
              @click="restoreStandardCategories"
              :disabled="restoringCategories"
              class="btn-secondary flex items-center"
            >
              <ArrowPathIcon v-if="!restoringCategories" class="w-4 h-4 mr-2" />
              <div v-else class="animate-spin rounded-full h-4 w-4 border-b-2 border-secondary-600 mr-2"></div>
              Standard-Kategorien wiederherstellen
            </button>
            
            <button
              @click="toggleEditStandardCategories"
              class="btn-secondary flex items-center"
            >
              <PencilIcon class="w-4 h-4 mr-2" />
              {{ editingStandardCategories ? 'Bearbeitung beenden' : 'Standard-Kategorien bearbeiten' }}
            </button>
            
            <button
              @click="showStandardCategoriesInfo"
              class="text-sm text-secondary-600 hover:text-secondary-700 font-medium border border-secondary-300 px-3 py-2 rounded-md"
            >
              <InformationCircleIcon class="w-4 h-4 mr-1 inline" />
              {{ showingStandardInfo ? 'Info ausblenden' : 'Info anzeigen' }}
            </button>
          </div>
          
          <!-- Editable Standard Categories List -->
          <div v-if="editingStandardCategories" class="mt-4 p-4 bg-yellow-50 rounded-lg border border-yellow-200">
            <div class="flex items-center justify-between mb-3">
              <h4 class="text-sm font-medium text-yellow-900">Standard-Kategorien bearbeiten:</h4>
              <div class="flex gap-2">
                <button
                  @click="addStandardCategory"
                  class="text-xs text-green-600 hover:text-green-700 font-medium border border-green-300 px-2 py-1 rounded"
                >
                  <PlusIcon class="w-3 h-3 mr-1 inline" />
                  Hinzufügen
                </button>
                <button
                  @click="resetToDefaults"
                  class="text-xs text-red-600 hover:text-red-700 font-medium border border-red-300 px-2 py-1 rounded"
                >
                  <ArrowPathIcon class="w-3 h-3 mr-1 inline" />
                  Zurücksetzen
                </button>
              </div>
            </div>
            <div class="space-y-2 text-sm">
              <div v-for="(category, index) in standardCategoriesTemplate" :key="index" class="flex items-center justify-between p-3 bg-white rounded border">
                <div class="flex items-center flex-1">
                  <div 
                    class="w-4 h-4 rounded mr-3 flex-shrink-0" 
                    :style="{ backgroundColor: category.color }"
                  ></div>
                  <div class="flex-1 min-w-0">
                    <div class="font-medium text-yellow-900 truncate">{{ category.name }}</div>
                    <div class="text-xs text-yellow-600 truncate">{{ category.description }}</div>
                  </div>
                  <div class="ml-3 text-xs text-yellow-700">{{ category.defaultStorageDays }} Tage</div>
                </div>
                <div class="flex items-center ml-3 gap-1">
                  <button
                    @click="editStandardCategory(category, index)"
                    class="text-blue-600 hover:text-blue-700 p-1"
                    title="Bearbeiten"
                  >
                    <PencilIcon class="w-3 h-3" />
                  </button>
                  <button
                    @click="deleteStandardCategory(index)"
                    class="text-red-600 hover:text-red-700 p-1"
                    title="Löschen"
                  >
                    <TrashIcon class="w-3 h-3" />
                  </button>
                </div>
              </div>
            </div>
            <div class="mt-3 text-xs text-yellow-600">
              💡 Tipp: Diese Kategorien werden als Vorlage für "Standard-Kategorien wiederherstellen" verwendet.
            </div>
          </div>

          <!-- Standard Categories Info List (if showing info and not editing) -->
          <div v-else-if="showingStandardInfo" class="mt-4 p-4 bg-blue-50 rounded-lg border border-blue-200">
            <h4 class="text-sm font-medium text-blue-900 mb-3">Standard-Kategorien die wiederhergestellt werden:</h4>
            <div class="space-y-2 text-sm text-blue-800">
              <div v-for="category in standardCategoriesTemplate" :key="category.name" class="flex items-center justify-between">
                <div class="flex items-center">
                  <div 
                    class="w-4 h-4 rounded mr-2" 
                    :style="{ backgroundColor: category.color }"
                  ></div>
                  <span class="font-medium">{{ category.name }}</span>
                </div>
                <span class="text-xs">{{ category.defaultStorageDays }} Tage</span>
              </div>
            </div>
            <div class="mt-3 text-xs text-blue-600">
              ⚠️ Hinweis: Kategorien mit gleichen Namen werden nicht doppelt erstellt.
            </div>
          </div>
        </div>
      </div>

      <!-- Custom Icons -->
      <div class="card p-6">
        <h3 class="text-lg font-semibold text-secondary-900 mb-4">
          Eigene Icons
        </h3>
        <p class="text-sm text-secondary-600 mb-4">
          Importiere eigene SVG-Icons für Kategorien und Standorte. Standard-Icons werden verwendet, wenn keine eigenen importiert sind.
        </p>
        
        <div class="space-y-4">
          <!-- Icon Upload -->
          <div>
            <label class="block text-sm font-medium text-secondary-700 mb-2">
              Neue Icons hochladen
            </label>
            <div class="flex items-center space-x-3">
              <input
                ref="iconInput"
                type="file"
                accept=".svg"
                multiple
                @change="handleIconUpload"
                class="hidden"
              />
              <button
                @click="() => { console.log('🖱️ Upload button clicked'); iconInput?.click() }"
                class="btn-secondary flex items-center"
              >
                <ArrowUpTrayIcon class="w-4 h-4 mr-2" />
                SVG-Icons auswählen
              </button>
              <div class="text-xs text-secondary-500">
                <p>Nur SVG-Dateien erlaubt</p>
                <p>• Max. 500KB pro Datei</p>
                <p>• Max. 50 Icons insgesamt</p>
                <p>• Keine Scripts oder gefährlicher Code</p>
              </div>
            </div>
          </div>

          <!-- Custom Icons Grid -->
          <div v-if="customIcons.length > 0">
            <label class="block text-sm font-medium text-secondary-700 mb-2">
              Importierte Icons ({{ customIcons.length }})
            </label>
            <div class="grid grid-cols-8 gap-3 p-4 bg-secondary-50 rounded-lg border">
              <div
                v-for="icon in customIcons"
                :key="icon.id"
                class="relative group"
              >
                <div class="w-12 h-12 p-2 bg-white rounded-lg border border-secondary-200 hover:border-primary-300 transition-colors duration-200">
                  <div
                    v-html="icon.svg"
                    class="w-full h-full text-secondary-600 flex items-center justify-center"
                    style="max-width: 100%; max-height: 100%; overflow: hidden;"
                  ></div>
                </div>
                <div class="absolute -top-2 -right-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                  <button
                    @click="deleteCustomIcon(icon.id)"
                    class="w-6 h-6 bg-red-500 hover:bg-red-600 text-white rounded-full flex items-center justify-center"
                    title="Icon löschen"
                  >
                    <XMarkIcon class="w-3 h-3" />
                  </button>
                </div>
                <p class="text-xs text-secondary-500 mt-1 truncate" :title="icon.name">
                  {{ icon.name }}
                </p>
              </div>
            </div>
          </div>

          <!-- No Icons State -->
          <div v-else class="text-center py-8 bg-secondary-50 rounded-lg border border-dashed border-secondary-300">
            <PhotoIcon class="w-12 h-12 text-secondary-400 mx-auto mb-2" />
            <p class="text-sm text-secondary-600 mb-2">Noch keine eigenen Icons importiert</p>
            <p class="text-xs text-secondary-500">
              Standard-Icons werden für Kategorien und Standorte verwendet
            </p>
          </div>

          <!-- Icon Management Actions -->
          <div v-if="customIcons.length > 0" class="flex items-center justify-between pt-4 border-t border-secondary-200">
            <span class="text-sm text-secondary-600">
              {{ customIcons.length }} Icon{{ customIcons.length !== 1 ? 's' : '' }} importiert
            </span>
            <div class="flex space-x-2">
              <button
                @click="exportCustomIcons"
                class="text-sm text-primary-600 hover:text-primary-700 font-medium"
              >
                Exportieren
              </button>
              <button
                @click="clearAllCustomIcons"
                class="text-sm text-red-600 hover:text-red-700 font-medium"
              >
                Alle löschen
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Display Settings -->
      <div class="card p-6">
        <h3 class="text-lg font-semibold text-secondary-900 mb-4">
          Anzeige
        </h3>
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <div>
              <label class="text-sm font-medium text-secondary-900">Standard-Ansicht</label>
              <p class="text-sm text-secondary-600">Welche Ansicht beim Öffnen der App angezeigt wird</p>
            </div>
            <select 
              v-model="settings.defaultView"
              class="rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
            >
              <option value="all">Alle Produkte</option>
              <option value="expiring">Ablaufende Produkte</option>
              <option value="expired">Abgelaufene Produkte</option>
            </select>
          </div>

          <div class="flex items-center justify-between">
            <div>
              <label class="text-sm font-medium text-secondary-900">Datums-Format</label>
              <p class="text-sm text-secondary-600">Anzeigeformat für Datumsangaben</p>
            </div>
            <select 
              v-model="settings.dateFormat"
              class="rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
            >
              <option value="dd.mm.yyyy">DD.MM.YYYY</option>
              <option value="mm/dd/yyyy">MM/DD/YYYY</option>
              <option value="yyyy-mm-dd">YYYY-MM-DD</option>
            </select>
          </div>

          <div class="flex items-center justify-between">
            <div>
              <label class="text-sm font-medium text-secondary-900">Produkte pro Seite</label>
              <p class="text-sm text-secondary-600">Anzahl der Produkte, die gleichzeitig angezeigt werden</p>
            </div>
            <select 
              v-model="settings.productsPerPage"
              class="rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
            >
              <option value="10">10 Produkte</option>
              <option value="20">20 Produkte</option>
              <option value="50">50 Produkte</option>
              <option value="100">100 Produkte</option>
              <option value="250">250 Produkte</option>
              <option value="500">500 Produkte</option>
              <option value="1000">Alle (1000+)</option>
            </select>
          </div>
        </div>
      </div>

      <!-- System & Debug -->
      <div class="card p-6">
        <h3 class="text-lg font-semibold text-secondary-900 mb-4">
          System & Debug
        </h3>
        <div class="space-y-4">
          <!-- Backend Status -->
          <div class="flex items-center justify-between">
            <div>
              <label class="text-sm font-medium text-secondary-900">Backend-Status</label>
              <p class="text-sm text-secondary-600">Verbindung zum Backend-Server</p>
            </div>
            <div class="flex items-center">
              <div 
                :class="backendStatus.connected ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'"
                class="px-2 py-1 rounded-full text-xs font-medium mr-2"
              >
                {{ backendStatus.connected ? 'Verbunden' : 'Getrennt' }}
              </div>
              <button
                @click="handleBackendCheck"
                :disabled="checkingBackend"
                class="text-sm text-primary-600 hover:text-primary-700 font-medium"
              >
                {{ checkingBackend ? 'Prüfe...' : 'Prüfen' }}
              </button>
            </div>
          </div>

          <!-- Backend URL -->
          <div v-if="backendStatus.connected" class="text-sm text-secondary-600">
            <span class="font-medium">Server:</span> {{ backendStatus.url }}
          </div>

          <!-- Debug Mode Toggle -->
          <div class="flex items-center justify-between pt-2 border-t border-secondary-300">
            <div>
              <label class="text-sm font-medium text-secondary-900">Debug-Modus</label>
              <p class="text-sm text-secondary-600">
                Zeige erweiterte Entwickler-Informationen 
                <span v-if="!isAdmin" class="text-red-600">(Admin-Berechtigung erforderlich)</span>
              </p>
            </div>
            <button
              @click="handleDebugToggle"
              :class="settings.debugMode ? 'bg-primary-600' : 'bg-secondary-200'"
              class="relative inline-flex flex-shrink-0 h-6 w-11 border-2 border-transparent rounded-full cursor-pointer transition-colors ease-in-out duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500"
            >
              <span
                :class="settings.debugMode ? 'translate-x-5' : 'translate-x-0'"
                class="pointer-events-none inline-block h-5 w-5 rounded-full bg-white shadow transform ring-0 transition ease-in-out duration-200"
              />
            </button>
          </div>

          <!-- Debug Info Panel -->
          <div v-if="settings.debugMode" class="mt-4 p-4 bg-secondary-50 rounded-lg border">
            <h4 class="text-sm font-medium text-secondary-900 mb-2">Debug-Informationen</h4>
            <div class="space-y-1 text-xs text-secondary-600 font-mono">
              <div><strong>Frontend URL:</strong> {{ debugInfo.frontendUrl }}</div>
              <div><strong>Backend URL:</strong> {{ backendStatus.url || 'Nicht konfiguriert' }}</div>
              <div><strong>User Agent:</strong> {{ debugInfo.userAgent }}</div>
              <div><strong>Local Storage:</strong> {{ debugInfo.localStorageCount }} Einträge</div>
              <div><strong>Session Storage:</strong> {{ debugInfo.sessionStorageCount }} Einträge</div>
              <div><strong>Viewport:</strong> {{ viewport.width }}x{{ viewport.height }}</div>
              <div><strong>Letzte API-Anfrage:</strong> {{ lastApiCall || 'Keine' }}</div>
              <div v-if="backendStatus.error" class="text-red-600">
                <strong>Backend Fehler:</strong> {{ backendStatus.error }}
              </div>
            </div>
            
            <!-- Debug Actions -->
            <div class="mt-3 flex space-x-2">
              <button
                @click="clearConsole"
                class="px-2 py-1 bg-secondary-200 hover:bg-secondary-300 rounded text-xs font-medium"
              >
                Console leeren
              </button>
              <button
                @click="exportDebugInfo"
                class="px-2 py-1 bg-secondary-200 hover:bg-secondary-300 rounded text-xs font-medium"
              >
                Debug-Info exportieren
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- About -->
      <div class="card p-6">
        <h3 class="text-lg font-semibold text-secondary-900 mb-4">
          Über die App
        </h3>
        <div class="space-y-2 text-sm text-secondary-600">
          <p><strong>Version:</strong> 1.0.0-rc.1</p>
          <p><strong>Letzte Aktualisierung:</strong> {{ lastUpdated }}</p>
          <p><strong>Entwickelt von:</strong> Maik Hirthe</p>
        </div>
      </div>

    </div>

    <!-- Save Button -->
    <div class="mt-8 flex justify-end">
      <button
        @click="saveSettings"
        :disabled="saving"
        class="btn-primary"
      >
        <CheckIcon v-if="!saving" class="w-4 h-4 mr-2" />
        <div v-else class="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
        {{ saving ? 'Speichern...' : 'Einstellungen speichern' }}
      </button>
    </div>

    <!-- Standard Category Edit Modal -->
    <div v-if="showStandardCategoryModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-lg shadow-xl max-w-md w-full">
        <!-- Header -->
        <div class="px-6 py-4 border-b border-secondary-200">
          <h3 class="text-lg font-semibold text-secondary-900">
            {{ editingStandardCategory ? 'Standard-Kategorie bearbeiten' : 'Neue Standard-Kategorie' }}
          </h3>
        </div>

        <!-- Form -->
        <div class="px-6 py-4 space-y-4">
          <!-- Name -->
          <div>
            <label class="block text-sm font-medium text-secondary-700 mb-1">
              Name <span class="text-red-500">*</span>
            </label>
            <input
              v-model="standardCategoryForm.name"
              type="text"
              required
              class="w-full rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
              placeholder="z.B. Fleisch, Gemüse"
            />
          </div>

          <!-- Description -->
          <div>
            <label class="block text-sm font-medium text-secondary-700 mb-1">
              Beschreibung
            </label>
            <input
              v-model="standardCategoryForm.description"
              type="text"
              class="w-full rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
              placeholder="z.B. Fleisch und Geflügel"
            />
          </div>

          <!-- Color -->
          <div>
            <label class="block text-sm font-medium text-secondary-700 mb-1">
              Farbe
            </label>
            <div class="flex items-center space-x-3">
              <input
                v-model="standardCategoryForm.color"
                type="color"
                class="w-12 h-10 border border-secondary-300 rounded-md cursor-pointer"
              />
              <input
                v-model="standardCategoryForm.color"
                type="text"
                class="flex-1 rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
                placeholder="#64748b"
              />
            </div>
          </div>

          <!-- Storage Days -->
          <div>
            <label class="block text-sm font-medium text-secondary-700 mb-1">
              Standard-Lagerdauer (Tage)
            </label>
            <input
              v-model.number="standardCategoryForm.defaultStorageDays"
              type="number"
              min="1"
              max="3650"
              class="w-full rounded-md border border-secondary-300 text-sm text-secondary-900 bg-white focus:ring-primary-500 focus:border-primary-500"
              placeholder="365"
            />
          </div>

          <!-- Preview -->
          <div class="bg-secondary-50 rounded-lg p-4 border">
            <h4 class="text-sm font-medium text-secondary-900 mb-2">Vorschau</h4>
            <div class="flex items-center">
              <div 
                class="w-6 h-6 rounded mr-3" 
                :style="{ backgroundColor: standardCategoryForm.color }"
              ></div>
              <div>
                <div class="font-medium text-secondary-900">
                  {{ standardCategoryForm.name || 'Kategorienname' }}
                </div>
                <div class="text-xs text-secondary-500">
                  {{ standardCategoryForm.description }}
                </div>
              </div>
              <div class="ml-auto text-xs text-secondary-600">
                {{ standardCategoryForm.defaultStorageDays || 0 }} Tage
              </div>
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="px-6 py-4 border-t border-secondary-200 flex justify-end space-x-3">
          <button
            @click="closeStandardCategoryModal"
            class="btn-secondary"
          >
            Abbrechen
          </button>
          <button
            @click="saveStandardCategory"
            :disabled="!standardCategoryForm.name.trim()"
            class="btn-primary"
          >
            {{ editingStandardCategory ? 'Speichern' : 'Hinzufügen' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { 
  CheckIcon,
  ArrowDownTrayIcon,
  TrashIcon,
  ArrowUpTrayIcon,
  XMarkIcon,
  PhotoIcon,
  ArrowPathIcon,
  InformationCircleIcon,
  PencilIcon,
  PlusIcon
} from '@heroicons/vue/24/outline'
import { useSettingsStore, type Settings } from '@/stores/settings'
import { useCategoriesStore } from '@/stores/categories'

const settingsStore = useSettingsStore()
const categoriesStore = useCategoriesStore()
const settings = settingsStore.settings

const saving = ref(false)
const lastUpdated = ref(new Date().toLocaleDateString('de-DE'))
const checkingBackend = ref(false)
const lastApiCall = ref<string | null>(null)
const isAdmin = ref(false)

// Standard Categories Management
const loadingCategories = ref(false)
const restoringCategories = ref(false)
const showingStandardInfo = ref(false)
const editingStandardCategories = ref(false)
const editingStandardCategory = ref<{ name: string; description?: string; color: string; defaultStorageDays: number; icon?: string } | null>(null)
const showStandardCategoryModal = ref(false)

// Default standard categories template
const defaultStandardCategoriesTemplate = [
  {
    name: "Fleisch",
    description: "Fleisch und Geflügel",
    color: "#EF4444",
    defaultStorageDays: 180,
    icon: "ArchiveBoxIcon"
  },
  {
    name: "Gemüse",
    description: "Gefrorenes Gemüse",
    color: "#22C55E",
    defaultStorageDays: 365,
    icon: "ArchiveBoxIcon"
  },
  {
    name: "Fertiggerichte",
    description: "Fertige Mahlzeiten",
    color: "#F59E0B",
    defaultStorageDays: 90,
    icon: "ArchiveBoxIcon"
  },
  {
    name: "Brot & Backwaren",
    description: "Brot, Brötchen und Backwaren",
    color: "#8B5CF6",
    defaultStorageDays: 90,
    icon: "ArchiveBoxIcon"
  },
  {
    name: "Eis & Desserts",
    description: "Eis und gefrorene Nachspeisen",
    color: "#EC4899",
    defaultStorageDays: 365,
    icon: "ArchiveBoxIcon"
  },
  {
    name: "Reste",
    description: "Übriggebliebene Mahlzeiten",
    color: "#6B7280",
    defaultStorageDays: 30,
    icon: "ArchiveBoxIcon"
  }
]

// Editable standard categories template (loaded from localStorage)
const standardCategoriesTemplate = ref([...defaultStandardCategoriesTemplate])

// Standard category form
const standardCategoryForm = ref({
  name: '',
  description: '',
  color: '#64748b',
  defaultStorageDays: 365,
  icon: 'ArchiveBoxIcon'
})

const editingStandardCategoryIndex = ref<number | null>(null)

// Custom Icons
interface CustomIcon {
  id: string
  name: string
  svg: string
  uploadDate: string
}

const customIcons = ref<CustomIcon[]>([])
const iconInput = ref<HTMLInputElement | null>(null)

// Backend status
const backendStatus = ref({
  connected: false,
  url: 'http://localhost:8080/api',
  error: null as string | null
})

// Viewport info for debug
const viewport = ref({
  width: window.innerWidth,
  height: window.innerHeight
})

// Debug info
const debugInfo = ref({
  frontendUrl: window.location.origin,
  userAgent: navigator.userAgent.substring(0, 50) + '...',
  localStorageCount: Object.keys(localStorage).length,
  sessionStorageCount: Object.keys(sessionStorage).length
})

// Standard Categories Template Management Functions
function loadStandardCategoriesTemplate() {
  try {
    const stored = localStorage.getItem('standard-categories-template')
    if (stored) {
      standardCategoriesTemplate.value = JSON.parse(stored)
    }
  } catch (error) {
    console.error('Failed to load standard categories template:', error)
    standardCategoriesTemplate.value = [...defaultStandardCategoriesTemplate]
  }
}

function saveStandardCategoriesTemplate() {
  try {
    localStorage.setItem('standard-categories-template', JSON.stringify(standardCategoriesTemplate.value))
    
    // Notify CategoriesView about changes if it's loaded
    interface WindowWithReload extends Window {
      reloadStandardCategories?: () => void
    }
    const windowWithReload = window as WindowWithReload
    if (typeof windowWithReload.reloadStandardCategories === 'function') {
      windowWithReload.reloadStandardCategories()
    }
  } catch (error) {
    console.error('Failed to save standard categories template:', error)
  }
}

function toggleEditStandardCategories() {
  editingStandardCategories.value = !editingStandardCategories.value
  if (editingStandardCategories.value) {
    showingStandardInfo.value = false
  }
}

function addStandardCategory() {
  resetStandardCategoryForm()
  editingStandardCategory.value = null
  editingStandardCategoryIndex.value = null
  showStandardCategoryModal.value = true
}

function editStandardCategory(category: { name: string; description?: string; color: string; defaultStorageDays: number; icon?: string }, index: number) {
  standardCategoryForm.value = {
    name: category.name,
    description: category.description || '',
    color: category.color,
    defaultStorageDays: category.defaultStorageDays,
    icon: category.icon || 'ArchiveBoxIcon'
  }
  editingStandardCategory.value = category
  editingStandardCategoryIndex.value = index
  showStandardCategoryModal.value = true
}

function deleteStandardCategory(index: number) {
  const category = standardCategoriesTemplate.value[index]
  if (confirm(`Möchten Sie die Standard-Kategorie "${category.name}" wirklich löschen?`)) {
    standardCategoriesTemplate.value.splice(index, 1)
    saveStandardCategoriesTemplate()
  }
}

function resetToDefaults() {
  if (confirm('Möchten Sie die Standard-Kategorien auf die Werkseinstellungen zurücksetzen?')) {
    standardCategoriesTemplate.value = [...defaultStandardCategoriesTemplate]
    saveStandardCategoriesTemplate()
  }
}

function resetStandardCategoryForm() {
  standardCategoryForm.value = {
    name: '',
    description: '',
    color: '#64748b',
    defaultStorageDays: 365,
    icon: 'ArchiveBoxIcon'
  }
}

function closeStandardCategoryModal() {
  showStandardCategoryModal.value = false
  editingStandardCategory.value = null
  editingStandardCategoryIndex.value = null
  resetStandardCategoryForm()
}

function saveStandardCategory() {
  if (!standardCategoryForm.value.name.trim()) {
    return
  }

  const categoryData = {
    name: standardCategoryForm.value.name.trim(),
    description: standardCategoryForm.value.description.trim(),
    color: standardCategoryForm.value.color,
    defaultStorageDays: standardCategoryForm.value.defaultStorageDays,
    icon: standardCategoryForm.value.icon
  }

  if (editingStandardCategoryIndex.value !== null) {
    // Edit existing category
    standardCategoriesTemplate.value[editingStandardCategoryIndex.value] = categoryData
  } else {
    // Add new category
    standardCategoriesTemplate.value.push(categoryData)
  }

  saveStandardCategoriesTemplate()
  closeStandardCategoryModal()
}

onMounted(() => {
  settingsStore.loadSettings()
  checkBackendStatus()
  checkAdminStatus()
  updateDebugInfo()
  loadCustomIcons()
  categoriesStore.fetchCategories()
  loadStandardCategoriesTemplate()
  
  // Update viewport on resize
  window.addEventListener('resize', () => {
    viewport.value.width = window.innerWidth
    viewport.value.height = window.innerHeight
  })
})

function toggleSetting(key: keyof Settings) {
  settingsStore.toggleSetting(key)
}

async function saveSettings() {
  saving.value = true
  try {
    await settingsStore.saveSettings()
    console.log('Settings saved successfully')
  } catch (error) {
    console.error('Failed to save settings:', error)
    alert('Fehler beim Speichern der Einstellungen')
  } finally {
    saving.value = false
  }
}

function exportData() {
  try {
    const data = {
      products: JSON.parse(localStorage.getItem('products') || '[]'),
      categories: JSON.parse(localStorage.getItem('categories') || '[]'),
      locations: JSON.parse(localStorage.getItem('locations') || '[]'),
      settings: settings,
      exportDate: new Date().toISOString()
    }
    
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `gefrierschrank-backup-${new Date().toISOString().split('T')[0]}.json`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Failed to export data:', error)
    alert('Fehler beim Exportieren der Daten')
  }
}

function clearAllData() {
  if (confirm('Möchten Sie wirklich alle Daten löschen? Diese Aktion kann nicht rückgängig gemacht werden.')) {
    if (confirm('Sind Sie sich sicher? Alle Produkte, Kategorien und Standorte werden gelöscht.')) {
      try {
        localStorage.removeItem('products')
        localStorage.removeItem('categories')
        localStorage.removeItem('locations')
        localStorage.removeItem('gefrierschrank-settings')
        alert('Alle Daten wurden erfolgreich gelöscht')
        location.reload()
      } catch (error) {
        console.error('Failed to clear data:', error)
        alert('Fehler beim Löschen der Daten')
      }
    }
  }
}

async function checkBackendStatus() {
  checkingBackend.value = true
  lastApiCall.value = new Date().toLocaleString('de-DE')
  
  try {
    const response = await fetch(`${backendStatus.value.url}/products`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    })
    
    if (response.ok) {
      backendStatus.value.connected = true
      backendStatus.value.error = null
    } else {
      backendStatus.value.connected = false
      backendStatus.value.error = `HTTP ${response.status}: ${response.statusText}`
    }
  } catch (error) {
    backendStatus.value.connected = false
    backendStatus.value.error = error instanceof Error ? error.message : 'Unbekannter Fehler'
  } finally {
    checkingBackend.value = false
  }
}

function clearConsole() {
  console.clear()
  console.log('Console wurde geleert von Gefrierschrank-App Debug-Modus')
}

function exportDebugInfo() {
  const debugData = {
    timestamp: new Date().toISOString(),
    frontend: {
      url: window.location.origin,
      userAgent: navigator.userAgent,
      viewport: viewport.value,
      localStorage: Object.keys(localStorage).length,
      sessionStorage: Object.keys(sessionStorage).length
    },
    backend: {
      url: backendStatus.value.url,
      connected: backendStatus.value.connected,
      error: backendStatus.value.error,
      lastApiCall: lastApiCall.value
    },
    settings: settings
  }
  
  const blob = new Blob([JSON.stringify(debugData, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `gefrierschrank-debug-${new Date().toISOString().split('T')[0]}.json`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
}

function checkAdminStatus() {
  // Check for admin flag in localStorage or special key combination
  const adminFlag = localStorage.getItem('admin-mode')
  const devMode = import.meta.env.DEV
  
  // Admin is true if:
  // 1. Admin flag is set in localStorage
  // 2. Development mode is active
  // 3. URL contains admin parameter
  const urlParams = new URLSearchParams(window.location.search)
  const adminParam = urlParams.get('admin')
  
  isAdmin.value = !!(adminFlag === 'true' || devMode || adminParam === 'true')
  
  console.log('Admin status check:', {
    adminFlag,
    devMode,
    adminParam,
    isAdmin: isAdmin.value
  })
}

function requestAdminAccess() {
  const adminCode = prompt('Admin-Code eingeben:')
  
  // Simple admin code check (in production this should be more secure)
  if (adminCode === 'admin123' || adminCode === 'debug') {
    localStorage.setItem('admin-mode', 'true')
    isAdmin.value = true
    alert('Admin-Modus aktiviert! Sie können jetzt den Debug-Modus nutzen.')
  } else if (adminCode) {
    alert('Falscher Admin-Code!')
  }
}

function handleDebugToggle() {
  console.log('Debug toggle clicked, isAdmin:', isAdmin.value, 'debugMode:', settings.debugMode)
  
  if (isAdmin.value) {
    toggleSetting('debugMode')
    // Update debug info when debug mode changes
    if (settings.debugMode) {
      updateDebugInfo()
    }
  } else {
    requestAdminAccess()
  }
}

function handleBackendCheck() {
  console.log('Backend check clicked')
  checkBackendStatus()
}

function updateDebugInfo() {
  debugInfo.value = {
    frontendUrl: window.location.origin,
    userAgent: navigator.userAgent.substring(0, 50) + '...',
    localStorageCount: Object.keys(localStorage).length,
    sessionStorageCount: Object.keys(sessionStorage).length
  }
}

// Standard Categories Management Functions
async function refreshCategoriesInfo() {
  loadingCategories.value = true
  try {
    await categoriesStore.fetchCategories()
  } catch (error) {
    console.error('Failed to refresh categories:', error)
    alert('Fehler beim Laden der Kategorien')
  } finally {
    loadingCategories.value = false
  }
}

async function restoreStandardCategories() {
  if (!confirm('Möchten Sie die Standard-Kategorien wiederherstellen? Kategorien mit gleichen Namen werden nicht doppelt erstellt.')) {
    return
  }

  restoringCategories.value = true
  
  try {
    // Use the customized template for restoration
    for (const templateCategory of standardCategoriesTemplate.value) {
      try {
        // Check if category with this name already exists
        const existingCategory = categoriesStore.categories.find(
          cat => cat.name.toLowerCase() === templateCategory.name.toLowerCase()
        )
        
        if (existingCategory) {
          continue // Skip existing categories
        }
        
        // Create the category
        await categoriesStore.createCategory({
          name: templateCategory.name,
          description: templateCategory.description,
          color: templateCategory.color,
          defaultStorageDays: templateCategory.defaultStorageDays,
          icon: templateCategory.icon
        })
      } catch (error) {
        console.error(`Failed to create category ${templateCategory.name}:`, error)
      }
    }
    
    // Refresh categories list
    await categoriesStore.fetchCategories()
    
    alert('✅ Standard-Kategorien erfolgreich wiederhergestellt!\n\nKategorien mit gleichen Namen wurden übersprungen.')
    
  } catch (error) {
    console.error('Failed to restore standard categories:', error)
    alert('Fehler beim Wiederherstellen der Standard-Kategorien:\n' + (error instanceof Error ? error.message : error))
  } finally {
    restoringCategories.value = false
  }
}

function showStandardCategoriesInfo() {
  showingStandardInfo.value = !showingStandardInfo.value
}

// Custom Icons Functions
function loadCustomIcons() {
  try {
    const stored = localStorage.getItem('custom-icons')
    if (stored) {
      customIcons.value = JSON.parse(stored)
    }
  } catch (error) {
    console.error('Failed to load custom icons:', error)
  }
}

// Normalize SVG for consistent icon display
function normalizeSvgForIcon(svgContent: string): string {
  try {
    // Parse the SVG to work with it as DOM
    const parser = new DOMParser()
    const doc = parser.parseFromString(svgContent, 'image/svg+xml')
    const svgElement = doc.querySelector('svg')
    
    if (!svgElement) return svgContent
    
    // Remove width and height attributes to allow CSS sizing
    svgElement.removeAttribute('width')
    svgElement.removeAttribute('height')
    
    // Ensure we have a viewBox for proper scaling
    if (!svgElement.getAttribute('viewBox')) {
      // Try to get original dimensions
      const originalWidth = svgElement.getAttribute('data-width') || 
                           svgElement.style.width || 
                           '24'
      const originalHeight = svgElement.getAttribute('data-height') || 
                            svgElement.style.height || 
                            '24'
      
      // Set a default viewBox if none exists
      svgElement.setAttribute('viewBox', `0 0 ${parseFloat(originalWidth)} ${parseFloat(originalHeight)}`)
    }
    
    // Ensure the SVG fills the container
    svgElement.setAttribute('fill', 'currentColor')
    svgElement.style.width = '100%'
    svgElement.style.height = '100%'
    svgElement.style.display = 'block'
    
    // Serialize back to string
    const serializer = new XMLSerializer()
    return serializer.serializeToString(svgElement)
    
  } catch (error) {
    console.error('Failed to normalize SVG:', error)
    // Fallback: basic string replacements
    return svgContent
      .replace(/width="[^"]*"/gi, '')
      .replace(/height="[^"]*"/gi, '')
      .replace(/<svg([^>]*?)>/i, (match, attributes) => {
        // Add viewBox if missing
        if (!attributes.includes('viewBox')) {
          attributes += ' viewBox="0 0 24 24"'
        }
        // Add styling for proper scaling
        attributes += ' fill="currentColor" style="width:100%;height:100%;display:block"'
        return `<svg${attributes}>`
      })
  }
}

function saveCustomIcons() {
  try {
    localStorage.setItem('custom-icons', JSON.stringify(customIcons.value))
  } catch (error) {
    console.error('Failed to save custom icons:', error)
  }
}

async function handleIconUpload(event: Event) {
  console.log('🔍 Icon upload started')
  const input = event.target as HTMLInputElement
  const files = input.files
  
  console.log('📁 Files selected:', files?.length || 0)
  if (!files || files.length === 0) {
    console.log('❌ No files selected')
    return
  }
  
  const newIcons: CustomIcon[] = []
  const errors: string[] = []
  const maxFileSize = 500 * 1024 // 500KB limit
  const maxIcons = 50 // Maximum number of custom icons
  
  // Check if we're approaching the icon limit
  if (customIcons.value.length >= maxIcons) {
    alert(`Maximale Anzahl von ${maxIcons} Icons erreicht. Bitte lösche erst einige Icons.`)
    if (input) input.value = ''
    return
  }
  
  for (const file of files) {
    const fileName = file.name
    console.log(`📄 Processing file: ${fileName}`)
    console.log(`   Type: ${file.type}`)
    console.log(`   Size: ${file.size} bytes`)
    
    // Check file type
    if (!file.type.includes('svg') && !fileName.toLowerCase().endsWith('.svg')) {
      console.log(`❌ ${fileName}: Wrong file type`)
      errors.push(`❌ ${fileName}: Nicht unterstütztes Dateiformat. Nur SVG-Dateien sind erlaubt.`)
      continue
    }
    
    // Check file size
    if (file.size > maxFileSize) {
      errors.push(`❌ ${fileName}: Datei zu groß (${(file.size/1024).toFixed(1)}KB). Maximum: 500KB.`)
      continue
    }
    
    // Check if icon with same name already exists
    if (customIcons.value.some(icon => icon.name === fileName.replace('.svg', ''))) {
      errors.push(`❌ ${fileName}: Icon mit diesem Namen existiert bereits.`)
      continue
    }
    
    // Check if we're exceeding the limit
    if (customIcons.value.length + newIcons.length >= maxIcons) {
      errors.push(`❌ ${fileName}: Maximale Anzahl von ${maxIcons} Icons erreicht.`)
      continue
    }
    
    try {
      console.log(`📖 Reading content of ${fileName}...`)
      const svgContent = await file.text()
      console.log(`   Content length: ${svgContent.length}`)
      console.log(`   First 100 chars: ${svgContent.substring(0, 100)}`)
      
      // Validate SVG structure
      if (!svgContent.trim().startsWith('<svg') && !svgContent.includes('<svg')) {
        console.log(`❌ ${fileName}: Missing <svg> tag`)
        errors.push(`❌ ${fileName}: Keine gültige SVG-Datei (fehlender <svg> Tag).`)
        continue
      }
      
      if (!svgContent.includes('</svg>')) {
        errors.push(`❌ ${fileName}: Unvollständige SVG-Datei (fehlender </svg> Tag).`)
        continue
      }
      
      // Check for potentially dangerous content
      const dangerousPatterns = [
        /<script[^>]*>/i,
        /javascript:/i,
        /data:text\/html/i,
        /vbscript:/i,
        /<iframe[^>]*>/i,
        /<object[^>]*>/i,
        /<embed[^>]*>/i
      ]
      
      const hasDangerousContent = dangerousPatterns.some(pattern => pattern.test(svgContent))
      if (hasDangerousContent) {
        errors.push(`❌ ${fileName}: Sicherheitsrisiko erkannt. SVG enthält potentiell gefährlichen Code.`)
        continue
      }
      
      // Clean and normalize SVG content
      let cleanSvg = svgContent
        .replace(/<script[^>]*>.*?<\/script>/gi, '')
        .replace(/on\w+="[^"]*"/gi, '')
        .replace(/javascript:/gi, '')
        .replace(/vbscript:/gi, '')
        .trim()
      
      // Normalize SVG for proper scaling
      cleanSvg = normalizeSvgForIcon(cleanSvg)
      
      if (cleanSvg.length < 50) { // Very basic check for meaningful content
        errors.push(`❌ ${fileName}: SVG-Inhalt zu kurz oder leer nach Bereinigung.`)
        continue
      }
      
      // Try to parse as XML to validate structure
      try {
        const parser = new DOMParser()
        const doc = parser.parseFromString(cleanSvg, 'image/svg+xml')
        const parserError = doc.querySelector('parsererror')
        if (parserError) {
          errors.push(`❌ ${fileName}: SVG-Struktur ungültig (XML-Parser-Fehler).`)
          continue
        }
      } catch {
        errors.push(`❌ ${fileName}: SVG-Format kann nicht validiert werden.`)
        continue
      }
      
      const icon: CustomIcon = {
        id: crypto.randomUUID(),
        name: fileName.replace('.svg', ''),
        svg: cleanSvg,
        uploadDate: new Date().toISOString()
      }
      
      console.log(`✅ ${fileName}: Successfully processed`)
      newIcons.push(icon)
      
    } catch (error) {
      console.error(`❌ Failed to process ${fileName}:`, error)
      errors.push(`❌ ${fileName}: Unbekannter Fehler beim Verarbeiten der Datei: ${error}`)
    }
  }
  
  console.log(`📊 Processing complete:`)
  console.log(`   New icons: ${newIcons.length}`)
  console.log(`   Errors: ${errors.length}`)
  
  // Show results
  let message = ''
  
  if (newIcons.length > 0) {
    customIcons.value.push(...newIcons)
    saveCustomIcons()
    message += `✅ ${newIcons.length} Icon${newIcons.length !== 1 ? 's' : ''} erfolgreich importiert!\n\n`
  }
  
  if (errors.length > 0) {
    message += `Folgende Dateien konnten nicht importiert werden:\n\n${errors.join('\n')}`
  }
  
  if (message) {
    console.log('📢 Showing result message:', message)
    alert(message)
  } else {
    console.log('⚠️ No message to show - this might be the problem!')
    if (newIcons.length === 0 && errors.length === 0) {
      alert('❓ Kein Icon verarbeitet. Überprüfe die Konsole für Details.')
    }
  }
  
  // Clear input
  if (input) {
    input.value = ''
  }
}

function deleteCustomIcon(iconId: string) {
  if (confirm('Möchten Sie dieses Icon wirklich löschen?')) {
    customIcons.value = customIcons.value.filter(icon => icon.id !== iconId)
    saveCustomIcons()
  }
}

function clearAllCustomIcons() {
  if (confirm('Möchten Sie wirklich alle eigenen Icons löschen?')) {
    customIcons.value = []
    saveCustomIcons()
    alert('Alle eigenen Icons wurden gelöscht.')
  }
}

function exportCustomIcons() {
  if (customIcons.value.length === 0) {
    alert('Keine Icons zum Exportieren vorhanden.')
    return
  }
  
  try {
    const exportData = {
      icons: customIcons.value,
      exportDate: new Date().toISOString(),
      version: '1.0.0'
    }
    
    const blob = new Blob([JSON.stringify(exportData, null, 2)], { type: 'application/json' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `gefrierschrank-icons-${new Date().toISOString().split('T')[0]}.json`
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Failed to export icons:', error)
    alert('Fehler beim Exportieren der Icons')
  }
}

// Export function to make custom icons available to other components
function getCustomIcons(): CustomIcon[] {
  return customIcons.value
}

// Make this available globally
interface WindowWithIcons extends Window {
  getCustomIcons?: () => CustomIcon[]
}
(window as WindowWithIcons).getCustomIcons = getCustomIcons
</script>