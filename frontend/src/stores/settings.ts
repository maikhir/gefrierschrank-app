import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface Settings {
  expirationNotifications: boolean
  notificationDays: number
  autoDeleteDays: number
  defaultView: string
  dateFormat: string
  productsPerPage: number
  debugMode: boolean
}

export const useSettingsStore = defineStore('settings', () => {
  // State
  const settings = ref<Settings>({
    expirationNotifications: true,
    notificationDays: 7,
    autoDeleteDays: 0,
    defaultView: 'all',
    dateFormat: 'dd.mm.yyyy',
    productsPerPage: 20,
    debugMode: false
  })

  const loading = ref(false)

  // Getters
  const productsPerPage = computed(() => settings.value.productsPerPage)
  const defaultView = computed(() => settings.value.defaultView)
  const dateFormat = computed(() => settings.value.dateFormat)

  // Actions
  const loadSettings = () => {
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

  const saveSettings = async () => {
    loading.value = true
    try {
      localStorage.setItem('gefrierschrank-settings', JSON.stringify(settings.value))
      await new Promise(resolve => setTimeout(resolve, 500)) // Simulate API call
      console.log('Settings saved successfully')
    } catch (error) {
      console.error('Failed to save settings:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const updateSetting = <K extends keyof Settings>(key: K, value: Settings[K]) => {
    settings.value[key] = value
    // Auto-save settings when they change
    saveSettings()
  }

  const toggleSetting = (key: keyof Settings) => {
    if (typeof settings.value[key] === 'boolean') {
      (settings.value[key] as boolean) = !(settings.value[key] as boolean)
      saveSettings()
    }
  }

  return {
    // State
    settings,
    loading,
    
    // Getters
    productsPerPage,
    defaultView,
    dateFormat,
    
    // Actions
    loadSettings,
    saveSettings,
    updateSetting,
    toggleSetting
  }
})