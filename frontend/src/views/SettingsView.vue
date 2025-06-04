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
          <p><strong>Version:</strong> 1.0.0-beta.1</p>
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { 
  CheckIcon,
  ArrowDownTrayIcon,
  TrashIcon
} from '@heroicons/vue/24/outline'

interface Settings {
  expirationNotifications: boolean
  notificationDays: number
  autoDeleteDays: number
  defaultView: string
  dateFormat: string
  debugMode: boolean
}

const settings = ref<Settings>({
  expirationNotifications: true,
  notificationDays: 7,
  autoDeleteDays: 0,
  defaultView: 'all',
  dateFormat: 'dd.mm.yyyy',
  debugMode: false
})

const saving = ref(false)
const lastUpdated = ref(new Date().toLocaleDateString('de-DE'))
const checkingBackend = ref(false)
const lastApiCall = ref<string | null>(null)
const isAdmin = ref(false)

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

onMounted(() => {
  loadSettings()
  checkBackendStatus()
  checkAdminStatus()
  updateDebugInfo()
  
  // Update viewport on resize
  window.addEventListener('resize', () => {
    viewport.value.width = window.innerWidth
    viewport.value.height = window.innerHeight
  })
})

function loadSettings() {
  const savedSettings = localStorage.getItem('gefrierschrank-settings')
  if (savedSettings) {
    try {
      const parsed = JSON.parse(savedSettings)
      Object.assign(settings.value, parsed)
    } catch (error) {
      console.error('Failed to load settings:', error)
    }
  }
}

function toggleSetting(key: keyof Settings) {
  if (typeof settings.value[key] === 'boolean') {
    (settings.value[key] as boolean) = !(settings.value[key] as boolean)
  }
}

async function saveSettings() {
  saving.value = true
  try {
    localStorage.setItem('gefrierschrank-settings', JSON.stringify(settings.value))
    await new Promise(resolve => setTimeout(resolve, 500)) // Simulate API call
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
      settings: settings.value,
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
    settings: settings.value
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
  console.log('Debug toggle clicked, isAdmin:', isAdmin.value, 'debugMode:', settings.value.debugMode)
  
  if (isAdmin.value) {
    toggleSetting('debugMode')
    // Update debug info when debug mode changes
    if (settings.value.debugMode) {
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
</script>