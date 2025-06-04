<template>
  <div class="lg:hidden">
    <!-- Mobile menu placeholder for now -->
    <div class="fixed bottom-0 left-0 right-0 bg-white border-t border-secondary-300 p-4">
      <div class="flex justify-around">
        <button class="flex flex-col items-center space-y-1 text-secondary-600">
          <HomeIcon class="h-6 w-6" />
          <span class="text-xs">Home</span>
        </button>
        <button class="flex flex-col items-center space-y-1 text-secondary-600">
          <FunnelIcon class="h-6 w-6" />
          <span class="text-xs">Filter</span>
        </button>
        <button class="flex flex-col items-center space-y-1 text-primary-600">
          <PlusCircleIcon class="h-6 w-6" />
          <span class="text-xs">Hinzufügen</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { HomeIcon, FunnelIcon, PlusCircleIcon } from '@heroicons/vue/24/outline'
</script>