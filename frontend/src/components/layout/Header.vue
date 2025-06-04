<!-- eslint-disable vue/multi-word-component-names -->
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
        <div class="relative" ref="userMenuRef">
          <button 
            @click="toggleUserMenu"
            class="p-2 rounded-full bg-primary-100 text-primary-600 hover:bg-primary-200 transition-colors"
            :class="{ 'bg-primary-200': showUserMenu }"
          >
            <UserIcon class="h-6 w-6" />
          </button>
          
          <!-- User Dropdown -->
          <div
            v-if="showUserMenu"
            class="absolute right-0 top-12 w-56 bg-white rounded-lg shadow-lg border border-secondary-200 py-2 z-50"
          >
            <!-- User Info -->
            <div class="px-4 py-3 border-b border-secondary-100">
              <div class="flex items-center space-x-3">
                <div class="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center">
                  <UserIcon class="w-5 h-5 text-primary-600" />
                </div>
                <div>
                  <div class="font-medium text-secondary-900">Max Mustermann</div>
                  <div class="text-sm text-secondary-500">Administrator</div>
                </div>
              </div>
            </div>
            
            <!-- Menu Items -->
            <nav class="py-2">
              <RouterLink
                to="/settings"
                @click="closeUserMenu"
                class="flex items-center px-4 py-2 text-sm text-secondary-700 hover:bg-secondary-50 hover:text-secondary-900 transition-colors"
              >
                <CogIcon class="w-4 h-4 mr-3" />
                Einstellungen
              </RouterLink>
              
              <RouterLink
                to="/profile"
                @click="closeUserMenu"
                class="flex items-center px-4 py-2 text-sm text-secondary-700 hover:bg-secondary-50 hover:text-secondary-900 transition-colors"
              >
                <UserCircleIcon class="w-4 h-4 mr-3" />
                Profil
              </RouterLink>
              
              <div class="border-t border-secondary-100 my-2"></div>
              
              <button
                @click="handleLogout"
                class="w-full flex items-center px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors"
              >
                <ArrowRightOnRectangleIcon class="w-4 h-4 mr-3" />
                Abmelden
              </button>
            </nav>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { RouterLink } from 'vue-router'
import { 
  Bars3Icon, 
  MagnifyingGlassIcon, 
  PlusIcon, 
  UserIcon,
  CogIcon,
  UserCircleIcon,
  ArrowRightOnRectangleIcon
} from '@heroicons/vue/24/outline'

const searchQuery = ref('')
const showUserMenu = ref(false)
const userMenuRef = ref<HTMLElement>()

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

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

const closeUserMenu = () => {
  showUserMenu.value = false
}

const handleLogout = () => {
  // TODO: Implement logout logic
  console.log('Logout clicked')
  closeUserMenu()
}

// Close dropdown when clicking outside
const handleClickOutside = (event: MouseEvent) => {
  if (userMenuRef.value && !userMenuRef.value.contains(event.target as Node)) {
    showUserMenu.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>