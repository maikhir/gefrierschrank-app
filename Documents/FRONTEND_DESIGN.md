# Frontend Design Spezifikation

## 📱 Wireframes & Layout-Struktur

### Desktop Layout (≥1024px)
Wireframe showing a desktop web app interface for a
generiertes Wireframe: 


```
┌─────────────────────────────────────────────────────────────┐
│ Header: Logo | Navigation | Search | User Menu              │
├─────────────┬───────────────────────────────────────────────┤
│ Sidebar     │ Main Content Area                             │
│ ┌─────────┐ │ ┌─────────────────────────────────────────────┐ │
│ │Categories│ │ │ Filter Bar: [All][Expiring][Search]       │ │
│ │- Fleisch │ │ ├─────────────────────────────────────────────┤ │
│ │- Gemüse  │ │ │ Product Grid (3-4 columns)                │ │
│ │- Brot    │ │ │ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐           │ │
│ └─────────┘ │ │ │Card1│ │Card2│ │Card3│ │Card4│           │ │
│             │ │ │     │ │     │ │     │ │     │           │ │
│ ┌─────────┐ │ │ └─────┘ └─────┘ └─────┘ └─────┘           │ │
│ │Locations│ │ │ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐           │ │
│ │- Fach 1  │ │ │Card5│ │Card6│ │Card7│ │Card8│           │ │
│ │- Fach 2  │ │ │     │ │     │ │     │ │     │           │ │
│ │- Schublade│ │ │ └─────┘ └─────┘ └─────┘ └─────┘           │ │
│ └─────────┘ │ └─────────────────────────────────────────────┘ │
└─────────────┴───────────────────────────────────────────────┘
```

### Tablet Layout (768px-1023px)
```
┌─────────────────────────────────────────────────────────────┐
│ Header: Logo | Search | Menu Button                         │
├─────────────────────────────────────────────────────────────┤
│ Filter Tabs: [All][Categories][Locations][Expiring]         │
├─────────────────────────────────────────────────────────────┤
│ Product Grid (2 columns)                                    │
│ ┌─────────────────┐ ┌─────────────────┐                     │
│ │     Card 1      │ │     Card 2      │                     │
│ │                 │ │                 │                     │
│ └─────────────────┘ └─────────────────┘                     │
│ ┌─────────────────┐ ┌─────────────────┐                     │
│ │     Card 3      │ │     Card 4      │                     │
│ │                 │ │                 │                     │
│ └─────────────────┘ └─────────────────┘                     │
└─────────────────────────────────────────────────────────────┘
```

### Mobile Layout (≤767px)
```
┌─────────────────────────────────────┐
│ Header: Menu | Logo | Search        │
├─────────────────────────────────────┤
│ Quick Filters: [All][Expiring][+]   │
├─────────────────────────────────────┤
│ Product List (1 column)             │
│ ┌─────────────────────────────────┐ │
│ │         Card 1                  │ │
│ │ [Image] Name      Expires: 3d   │ │
│ │         Location  Qty: 2 kg     │ │
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │         Card 2                  │ │
│ │ [Image] Name      Expires: 1w   │ │
│ │         Location  Qty: 500g     │ │
│ └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```

## 🃏 Produktkarten Design

### Desktop Produktkarte
```
┌─────────────────────────────────┐
│ [Status Badge]    [Mehr-Menü]   │
│                                 │
│      [Produkt Bild/Icon]        │
│                                 │
│      Produktname                │
│      Kategorie                  │
│                                 │
│  📅 Eingefroren: 15.05.2025     │
│  ⏰ Haltbar bis: 15.11.2025     │
│  📦 Menge: 2 kg                 │
│  📍 Standort: Fach 1            │
│                                 │
│ [Bearbeiten] [Verbraucht]       │
└─────────────────────────────────┘
```

### Mobile Produktkarte (Kompakt)
```
┌─────────────────────────────────────────┐
│ [Icon] Produktname        [Status Badge]│
│        Kategorie • Standort             │
│        Menge: 2kg • Haltbar: 3 Tage    │
│                         [Edit] [Done]   │
└─────────────────────────────────────────┘
```

## 📝 Modal/Form Design

### Produkt hinzufügen Modal
```
┌─────────────────────────────────────────┐
│ Neues Produkt hinzufügen           [X]  │
├─────────────────────────────────────────┤
│                                         │
│ Produktname: [________________]          │
│                                         │
│ Kategorie:   [Dropdown ▼]              │
│ Standort:    [Dropdown ▼]              │
│                                         │
│ Menge:       [____] [Einheit ▼]        │
│                                         │
│ Einfrierdatum: [Heute ▼]               │
│ Ablaufdatum:   [Auto-berechnet]         │
│                                         │
│ Notizen:     [________________]          │
│              [________________]          │
│                                         │
│      [Abbrechen]    [Speichern]         │
└─────────────────────────────────────────┘
```

## 🎯 Navigation & Header

### Desktop Header
```
┌─────────────────────────────────────────────────────────────┐
│ [Logo] Gefrierschrank | Dashboard | Kategorien | Einstellungen │ [Search___] [User ▼] │
└─────────────────────────────────────────────────────────────┘
```

### Mobile Header
```
┌─────────────────────────────────────┐
│ [☰] Gefrierschrank    [🔍] [User]   │
└─────────────────────────────────────┘
```

## 🚨 Status & Warnindikatoren

### Ablauf-Status Badges
- 🟢 **Frisch**: > 7 Tage (grün)
- 🟡 **Bald ablaufend**: 3-7 Tage (gelb)
- 🟠 **Kritisch**: 1-3 Tage (orange)
- 🔴 **Abgelaufen**: < 1 Tag (rot)

### Interaktive Elemente
- Produktkarten: Hover-Effekte
- Buttons: Loading-States
- Forms: Echtzeit-Validierung
- Search: Auto-complete/Suggestions

---

## 🎨 Farbschema & Branding

### Primäre Farbpalette
```css
/* Hauptfarben */
--primary-50:  #f0f9ff;   /* Sehr helles Blau */
--primary-100: #e0f2fe;   /* Helles Blau */
--primary-500: #0ea5e9;   /* Hauptblau (Logo, CTA) */
--primary-600: #0284c7;   /* Dunkles Blau (Hover) */
--primary-700: #0369a1;   /* Sehr dunkles Blau */

/* Sekundärfarben */
--secondary-50:  #f8fafc;   /* Helles Grau */
--secondary-100: #f1f5f9;   /* Layout-Hintergrund */
--secondary-200: #e2e8f0;   /* Borders */
--secondary-600: #475569;   /* Text-Sekundär */
--secondary-900: #0f172a;   /* Text-Primär */
```

### Status-Farbpalette
```css
/* Zustand Farben */
--success-50:  #f0fdf4;   /* Helles Grün */
--success-500: #22c55e;   /* Frisch (>7 Tage) */
--success-600: #16a34a;   /* Hover-Zustand */

--warning-50:  #fffbeb;   /* Helles Gelb */
--warning-500: #f59e0b;   /* Bald ablaufend (3-7 Tage) */
--warning-600: #d97706;   /* Hover-Zustand */

--orange-50:   #fff7ed;   /* Helles Orange */
--orange-500:  #f97316;   /* Kritisch (1-3 Tage) */
--orange-600:  #ea580c;   /* Hover-Zustand */

--red-50:      #fef2f2;   /* Helles Rot */
--red-500:     #ef4444;   /* Abgelaufen (<1 Tag) */
--red-600:     #dc2626;   /* Hover-Zustand */
```

### Logo & Branding
```
🧊 Gefrierschrank-App
─────────────────────

Icon: Gefrierschrank/Schneeflocke Symbol
Font: Inter (modern, lesbar)
Stil: Minimalistisch, freundlich, vertrauenswürdig

Logo-Varianten:
- Horizontal: [Icon] Gefrierschrank
- Vertikal:   [Icon]
              Gefrierschrank
- Icon-only:  [Icon] (Mobile)
```

### Typografie
```css
/* Font Families */
--font-sans: 'Inter', system-ui, sans-serif;
--font-mono: 'Fira Code', monospace;

/* Font Sizes */
--text-xs:   0.75rem;   /* 12px - Labels */
--text-sm:   0.875rem;  /* 14px - Body Small */
--text-base: 1rem;      /* 16px - Body */
--text-lg:   1.125rem;  /* 18px - Subheading */
--text-xl:   1.25rem;   /* 20px - Heading */
--text-2xl:  1.5rem;    /* 24px - Page Title */
--text-3xl:  1.875rem;  /* 30px - Hero */

/* Font Weights */
--font-normal:    400;
--font-medium:    500;
--font-semibold:  600;
--font-bold:      700;
```

### Schatten & Radius
```css
/* Schatten */
--shadow-sm:  0 1px 2px 0 rgb(0 0 0 / 0.05);
--shadow:     0 1px 3px 0 rgb(0 0 0 / 0.1);
--shadow-md:  0 4px 6px -1px rgb(0 0 0 / 0.1);
--shadow-lg:  0 10px 15px -3px rgb(0 0 0 / 0.1);

/* Border Radius */
--radius-sm:  0.125rem;  /* 2px */
--radius:     0.25rem;   /* 4px */
--radius-md:  0.375rem;  /* 6px */
--radius-lg:  0.5rem;    /* 8px */
--radius-xl:  0.75rem;   /* 12px */
```

### UI-Komponenten Styling
```css
/* Buttons */
.btn-primary {
  background: var(--primary-500);
  color: white;
  border-radius: var(--radius-md);
  padding: 0.5rem 1rem;
  font-weight: var(--font-medium);
}

.btn-primary:hover {
  background: var(--primary-600);
}

/* Cards */
.card {
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow);
  border: 1px solid var(--secondary-200);
}

.card:hover {
  box-shadow: var(--shadow-md);
}

/* Status Badges */
.badge-fresh {
  background: var(--success-50);
  color: var(--success-600);
  border: 1px solid var(--success-200);
}

.badge-expiring {
  background: var(--warning-50);
  color: var(--warning-600);
  border: 1px solid var(--warning-200);
}

.badge-critical {
  background: var(--orange-50);
  color: var(--orange-600);
  border: 1px solid var(--orange-200);
}

.badge-expired {
  background: var(--red-50);
  color: var(--red-600);
  border: 1px solid var(--red-200);
}
```

### Dark Mode (Optional für V2)
```css
/* Dark Mode Palette */
--dark-bg-primary:    #0f172a;
--dark-bg-secondary:  #1e293b;
--dark-text-primary:  #f8fafc;
--dark-text-secondary: #94a3b8;
```

---

## 🏗️ Komponenten-Hierarchie & Struktur

### Verzeichnisstruktur
```
frontend/
├── public/
│   ├── index.html
│   └── favicon.ico
├── src/
│   ├── main.js                 # App Entry Point
│   ├── App.vue                 # Root Component
│   ├── router/
│   │   └── index.js           # Vue Router
│   ├── stores/                # Pinia Stores
│   │   ├── auth.js
│   │   ├── products.js
│   │   ├── categories.js
│   │   ├── locations.js
│   │   └── ui.js
│   ├── api/                   # API Layer
│   │   ├── client.js          # HTTP Client
│   │   ├── products.js
│   │   ├── categories.js
│   │   └── locations.js
│   ├── components/            # Reusable Components
│   │   ├── ui/                # Base UI Components
│   │   │   ├── Button.vue
│   │   │   ├── Card.vue
│   │   │   ├── Modal.vue
│   │   │   ├── Badge.vue
│   │   │   ├── Input.vue
│   │   │   ├── Select.vue
│   │   │   └── Loading.vue
│   │   ├── layout/            # Layout Components
│   │   │   ├── Header.vue
│   │   │   ├── Sidebar.vue
│   │   │   ├── Navigation.vue
│   │   │   └── MobileMenu.vue
│   │   └── product/           # Product-specific Components
│   │       ├── ProductCard.vue
│   │       ├── ProductGrid.vue
│   │       ├── ProductForm.vue
│   │       ├── ProductModal.vue
│   │       ├── ProductFilter.vue
│   │       └── ProductSearch.vue
│   ├── views/                 # Page Components
│   │   ├── Dashboard.vue      # Main Inventory View
│   │   ├── Categories.vue     # Category Management
│   │   ├── Locations.vue      # Location Management
│   │   └── Settings.vue       # App Settings
│   ├── composables/           # Vue Composables
│   │   ├── useProducts.js     # Product CRUD logic
│   │   ├── useAuth.js         # Authentication logic
│   │   ├── useFilter.js       # Filtering logic
│   │   └── useResponsive.js   # Responsive utilities
│   ├── utils/                 # Utility Functions
│   │   ├── date.js            # Date formatting
│   │   ├── validation.js      # Form validation
│   │   └── constants.js       # App constants
│   └── assets/                # Static Assets
│       ├── css/
│       │   └── main.css       # Global Styles
│       └── images/
├── package.json
├── vite.config.js
├── tailwind.config.js
└── postcss.config.js
```

### Komponenten-Architektur

#### 1. Layout-Komponenten
```vue
<!-- App.vue -->
<template>
  <div id="app" class="min-h-screen bg-secondary-50">
    <Header />
    <div class="flex">
      <Sidebar v-if="!isMobile" />
      <main class="flex-1">
        <router-view />
      </main>
    </div>
    <MobileMenu v-if="isMobile" />
  </div>
</template>

<!-- Header.vue -->
<template>
  <header class="bg-white shadow-sm border-b">
    <div class="flex items-center justify-between px-4 py-3">
      <div class="flex items-center space-x-4">
        <button @click="toggleSidebar" class="md:hidden">
          <MenuIcon />
        </button>
        <Logo />
      </div>
      
      <ProductSearch class="hidden md:block" />
      
      <div class="flex items-center space-x-3">
        <button class="btn-primary md:hidden">
          <SearchIcon />
        </button>
        <UserMenu />
      </div>
    </div>
  </header>
</template>
```

#### 2. Product-Komponenten
```vue
<!-- ProductCard.vue -->
<template>
  <div class="card product-card" :class="statusClass">
    <div class="p-4">
      <div class="flex justify-between items-start mb-3">
        <Badge :status="expirationStatus" />
        <ProductMenu :product="product" />
      </div>
      
      <div class="text-center mb-4">
        <div class="w-16 h-16 mx-auto mb-2 bg-secondary-100 rounded-lg flex items-center justify-center">
          <component :is="categoryIcon" class="w-8 h-8 text-secondary-500" />
        </div>
        <h3 class="font-semibold text-secondary-900">{{ product.name }}</h3>
        <p class="text-sm text-secondary-600">{{ product.category.name }}</p>
      </div>
      
      <div class="space-y-2 text-sm text-secondary-600">
        <div class="flex items-center">
          <CalendarIcon class="w-4 h-4 mr-2" />
          <span>Eingefroren: {{ formatDate(product.frozenDate) }}</span>
        </div>
        <div class="flex items-center">
          <ClockIcon class="w-4 h-4 mr-2" />
          <span>Haltbar bis: {{ formatDate(product.expirationDate) }}</span>
        </div>
        <div class="flex items-center">
          <ScaleIcon class="w-4 h-4 mr-2" />
          <span>{{ product.quantity }} {{ product.unit }}</span>
        </div>
        <div class="flex items-center">
          <LocationIcon class="w-4 h-4 mr-2" />
          <span>{{ product.location.name }}</span>
        </div>
      </div>
      
      <div class="flex space-x-2 mt-4">
        <button @click="editProduct" class="btn-secondary flex-1">
          <EditIcon class="w-4 h-4 mr-1" />
          Bearbeiten
        </button>
        <button @click="markAsUsed" class="btn-success flex-1">
          <CheckIcon class="w-4 h-4 mr-1" />
          Verbraucht
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
// Component logic here...
</script>
```

#### 3. Form-Komponenten
```vue
<!-- ProductForm.vue -->
<template>
  <form @submit.prevent="submitForm" class="space-y-6">
    <div>
      <label class="block text-sm font-medium text-secondary-700 mb-1">
        Produktname *
      </label>
      <Input 
        v-model="form.name" 
        :error="errors.name"
        placeholder="z.B. Rindfleisch" 
        required 
      />
    </div>
    
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div>
        <label class="block text-sm font-medium text-secondary-700 mb-1">
          Kategorie *
        </label>
        <Select 
          v-model="form.categoryId" 
          :options="categories"
          :error="errors.categoryId"
          placeholder="Kategorie wählen" 
        />
      </div>
      
      <div>
        <label class="block text-sm font-medium text-secondary-700 mb-1">
          Standort *
        </label>
        <Select 
          v-model="form.locationId" 
          :options="locations"
          :error="errors.locationId"
          placeholder="Standort wählen" 
        />
      </div>
    </div>
    
    <div class="grid grid-cols-2 gap-4">
      <div>
        <label class="block text-sm font-medium text-secondary-700 mb-1">
          Menge *
        </label>
        <Input 
          v-model="form.quantity" 
          type="number" 
          step="0.01"
          :error="errors.quantity"
          placeholder="1.5" 
        />
      </div>
      
      <div>
        <label class="block text-sm font-medium text-secondary-700 mb-1">
          Einheit *
        </label>
        <Select 
          v-model="form.unit" 
          :options="units"
          :error="errors.unit"
          placeholder="kg" 
        />
      </div>
    </div>
    
    <div class="flex space-x-4">
      <button type="button" @click="$emit('cancel')" class="btn-secondary flex-1">
        Abbrechen
      </button>
      <button type="submit" :disabled="!isValid" class="btn-primary flex-1">
        <Loading v-if="loading" class="w-4 h-4 mr-2" />
        {{ isEditing ? 'Aktualisieren' : 'Hinzufügen' }}
      </button>
    </div>
  </form>
</template>
```

### State Management (Pinia Stores)

#### Products Store
```javascript
// stores/products.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as productsApi from '@/api/products'

export const useProductsStore = defineStore('products', () => {
  // State
  const products = ref([])
  const loading = ref(false)
  const error = ref(null)
  
  // Getters
  const expiringProducts = computed(() => 
    products.value.filter(p => p.daysUntilExpiration <= 7)
  )
  
  const productsByCategory = computed(() => 
    products.value.reduce((acc, product) => {
      const category = product.category.name
      if (!acc[category]) acc[category] = []
      acc[category].push(product)
      return acc
    }, {})
  )
  
  // Actions
  async function fetchProducts(filters = {}) {
    try {
      loading.value = true
      const response = await productsApi.getProducts(filters)
      products.value = response.data.content
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }
  
  async function createProduct(productData) {
    try {
      const response = await productsApi.createProduct(productData)
      products.value.unshift(response.data)
      return response.data
    } catch (err) {
      error.value = err.message
      throw err
    }
  }
  
  async function updateProduct(id, productData) {
    try {
      const response = await productsApi.updateProduct(id, productData)
      const index = products.value.findIndex(p => p.id === id)
      if (index !== -1) {
        products.value[index] = response.data
      }
      return response.data
    } catch (err) {
      error.value = err.message
      throw err
    }
  }
  
  async function deleteProduct(id) {
    try {
      await productsApi.deleteProduct(id)
      products.value = products.value.filter(p => p.id !== id)
    } catch (err) {
      error.value = err.message
      throw err
    }
  }
  
  return {
    // State
    products,
    loading,
    error,
    // Getters  
    expiringProducts,
    productsByCategory,
    // Actions
    fetchProducts,
    createProduct,
    updateProduct,
    deleteProduct
  }
})
```

### Composables für Wiederverwendbare Logik

```javascript
// composables/useProducts.js
import { useProductsStore } from '@/stores/products'
import { storeToRefs } from 'pinia'

export function useProducts() {
  const store = useProductsStore()
  const { products, loading, error, expiringProducts } = storeToRefs(store)
  
  return {
    // State
    products,
    loading,
    error,
    expiringProducts,
    // Actions
    fetchProducts: store.fetchProducts,
    createProduct: store.createProduct,
    updateProduct: store.updateProduct,
    deleteProduct: store.deleteProduct
  }
}

// composables/useFilter.js
import { ref, computed } from 'vue'

export function useFilter(items) {
  const searchQuery = ref('')
  const selectedCategory = ref(null)
  const selectedLocation = ref(null)
  const statusFilter = ref('all') // all, expiring, fresh
  
  const filteredItems = computed(() => {
    let filtered = items.value
    
    // Search filter
    if (searchQuery.value) {
      filtered = filtered.filter(item => 
        item.name.toLowerCase().includes(searchQuery.value.toLowerCase())
      )
    }
    
    // Category filter
    if (selectedCategory.value) {
      filtered = filtered.filter(item => 
        item.category.id === selectedCategory.value
      )
    }
    
    // Location filter
    if (selectedLocation.value) {
      filtered = filtered.filter(item => 
        item.location.id === selectedLocation.value
      )
    }
    
    // Status filter
    if (statusFilter.value === 'expiring') {
      filtered = filtered.filter(item => 
        item.daysUntilExpiration <= 7 && item.daysUntilExpiration > 0
      )
    } else if (statusFilter.value === 'fresh') {
      filtered = filtered.filter(item => 
        item.daysUntilExpiration > 7
      )
    }
    
    return filtered
  })
  
  function clearFilters() {
    searchQuery.value = ''
    selectedCategory.value = null
    selectedLocation.value = null
    statusFilter.value = 'all'
  }
  
  return {
    searchQuery,
    selectedCategory,
    selectedLocation,
    statusFilter,
    filteredItems,
    clearFilters
  }
}
```