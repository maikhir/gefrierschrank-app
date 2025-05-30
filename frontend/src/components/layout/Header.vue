<template>
  <header class="bg-white shadow-sm border-b border-secondary-200">
    <div class="flex items-center justify-between px-4 py-3">
      <!-- Logo & Brand -->
      <div class="flex items-center space-x-4">
        <button 
          @click="toggleSidebar" 
          class="lg:hidden p-2 rounded-md text-secondary-600 hover:bg-secondary-100"
        >
          <Bars3Icon class="h-6 w-6" />
        </button>
        
        <div class="flex items-center space-x-2">
          <div class="text-2xl">🧊</div>
          <h1 class="text-xl font-bold text-secondary-900 hidden sm:block">
            Gefrierschrank-App
          </h1>
        </div>
      </div>

      <!-- Navigation (Desktop) -->
      <nav class="hidden lg:flex items-center space-x-8">
        <a href="/" class="text-secondary-700 hover:text-primary-600 font-medium">Dashboard</a>
        <a href="/categories" class="text-secondary-700 hover:text-primary-600 font-medium">Kategorien</a>
        <a href="/locations" class="text-secondary-700 hover:text-primary-600 font-medium">Standorte</a>
      </nav>
      
      <!-- Search (Desktop) -->
      <div class="hidden md:block flex-1 max-w-lg mx-8">
        <div class="relative">
          <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-secondary-400" />
          <input
            type="text"
            placeholder="Produkte suchen..."
            class="w-full pl-10 pr-4 py-2 border border-secondary-200 rounded-lg text-secondary-900 focus:ring-2 focus:ring-primary-500 focus:border-primary-500 placeholder:text-secondary-600"
            v-model="searchQuery"
            @input="handleSearch"
          />
        </div>
      </div>
      
      <!-- Actions -->
      <div class="flex items-center space-x-3">
        <!-- Search Button (Mobile) -->
        <button class="md:hidden p-2 rounded-md text-secondary-600 hover:bg-secondary-100">
          <MagnifyingGlassIcon class="h-6 w-6" />
        </button>
        
        <!-- Add Product Button -->
        <button 
          @click="showAddProduct"
          class="btn-primary flex items-center space-x-2"
        >
          <PlusIcon class="h-5 w-5" />
          <span class="hidden sm:inline">Hinzufügen</span>
        </button>
        
        <!-- User Menu -->
        <div class="relative">
          <button class="p-2 rounded-full bg-primary-100 text-primary-600">
            <UserIcon class="h-6 w-6" />
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { 
  Bars3Icon, 
  MagnifyingGlassIcon, 
  PlusIcon, 
  UserIcon 
} from '@heroicons/vue/24/outline'

const searchQuery = ref('')

const emit = defineEmits(['toggle-sidebar', 'show-add-product', 'search'])

const toggleSidebar = () => {
  emit('toggle-sidebar')
}

const showAddProduct = () => {
  emit('show-add-product')
}

const handleSearch = () => {
  emit('search', searchQuery.value)
}
</script>