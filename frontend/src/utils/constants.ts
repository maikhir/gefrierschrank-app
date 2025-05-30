// Common units for products
export const PRODUCT_UNITS = [
  { value: 'kg', label: 'Kilogramm' },
  { value: 'g', label: 'Gramm' },
  { value: 'l', label: 'Liter' },
  { value: 'ml', label: 'Milliliter' },
  { value: 'stück', label: 'Stück' },
  { value: 'packung', label: 'Packung' },
  { value: 'portion', label: 'Portion' },
  { value: 'dose', label: 'Dose' },
  { value: 'glas', label: 'Glas' },
  { value: 'beutel', label: 'Beutel' }
]

// Status badges configuration
export const STATUS_CONFIG = {
  fresh: {
    label: 'Frisch',
    color: 'success',
    bgClass: 'badge-fresh',
    icon: '✓'
  },
  expiring: {
    label: 'Läuft ab',
    color: 'warning',
    bgClass: 'badge-expiring',
    icon: '⚠'
  },
  critical: {
    label: 'Kritisch',
    color: 'orange',
    bgClass: 'badge-critical',
    icon: '🚨'
  },
  expired: {
    label: 'Abgelaufen',
    color: 'red',
    bgClass: 'badge-expired',
    icon: '❌'
  }
}

// Pagination defaults
export const PAGINATION_DEFAULTS = {
  pageSize: 20,
  pageSizes: [10, 20, 50, 100]
}

// API configuration
export const API_CONFIG = {
  timeout: 10000,
  retryAttempts: 3,
  retryDelay: 1000
}

// Form validation rules
export const VALIDATION_RULES = {
  product: {
    name: {
      required: true,
      minLength: 2,
      maxLength: 200
    },
    quantity: {
      required: true,
      min: 0.01,
      max: 9999.99
    },
    unit: {
      required: true
    },
    categoryId: {
      required: true
    },
    locationId: {
      required: true
    }
  },
  category: {
    name: {
      required: true,
      minLength: 2,
      maxLength: 100
    },
    defaultStorageDays: {
      min: 1,
      max: 3650 // 10 years
    }
  },
  location: {
    name: {
      required: true,
      minLength: 2,
      maxLength: 100
    },
    sortOrder: {
      min: 0,
      max: 999
    }
  }
}

// Color palette for categories
export const CATEGORY_COLORS = [
  '#ef4444', // red
  '#f97316', // orange
  '#f59e0b', // amber
  '#eab308', // yellow
  '#84cc16', // lime
  '#22c55e', // green
  '#10b981', // emerald
  '#14b8a6', // teal
  '#06b6d4', // cyan
  '#0ea5e9', // sky
  '#3b82f6', // blue
  '#6366f1', // indigo
  '#8b5cf6', // violet
  '#a855f7', // purple
  '#d946ef', // fuchsia
  '#ec4899', // pink
  '#f43f5e'  // rose
]

// Default storage days by category type
export const DEFAULT_STORAGE_DAYS = {
  'Fleisch': 365,
  'Fisch': 180,
  'Gemüse': 365,
  'Obst': 365,
  'Brot': 90,
  'Fertiggerichte': 180,
  'Milchprodukte': 60,
  'Suppen': 180,
  'Desserts': 90,
  'Andere': 180
}

// App configuration
export const APP_CONFIG = {
  name: 'Gefrierschrank-App',
  version: '1.0.0',
  description: 'Moderne Web-Anwendung zur Verwaltung von Gefrierschrank-Inhalten'
}