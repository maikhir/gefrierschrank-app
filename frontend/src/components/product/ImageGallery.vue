<template>
  <div v-if="imageUrl" class="image-gallery">
    <!-- Thumbnail Trigger -->
    <div
      @click="openLightbox"
      class="cursor-pointer hover:opacity-75 transition-opacity"
    >
      <img
        :src="getImageUrl(imageUrl, 'medium')"
        :alt="altText"
        class="w-full h-48 object-cover rounded-lg border border-gray-200"
        @error="onImageError"
      />
    </div>

    <!-- Lightbox Modal -->
    <div
      v-if="showLightbox"
      class="fixed inset-0 bg-black bg-opacity-90 z-50 flex items-center justify-center p-4"
      @click="closeLightbox"
    >
      <div class="relative max-w-4xl max-h-full">
        <!-- Close Button -->
        <button
          @click="closeLightbox"
          class="absolute top-4 right-4 text-white hover:text-gray-300 z-10"
        >
          <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
          </svg>
        </button>

        <!-- Full Size Image -->
        <img
          :src="getImageUrl(imageUrl, 'original')"
          :alt="altText"
          class="max-w-full max-h-full object-contain"
          @click.stop
          @error="onImageError"
        />

        <!-- Image Info -->
        <div class="absolute bottom-4 left-4 text-white bg-black bg-opacity-50 rounded px-3 py-2">
          <p class="text-sm font-medium">{{ altText }}</p>
          <p v-if="showImageInfo" class="text-xs text-gray-300">
            Klicken Sie außerhalb des Bildes zum Schließen
          </p>
        </div>

        <!-- Navigation (for future multi-image support) -->
        <div v-if="showNavigation" class="absolute inset-y-0 left-4 flex items-center">
          <button
            @click.stop="previousImage"
            class="text-white hover:text-gray-300 p-2"
            :disabled="!hasPrevious"
          >
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
            </svg>
          </button>
        </div>

        <div v-if="showNavigation" class="absolute inset-y-0 right-4 flex items-center">
          <button
            @click.stop="nextImage"
            class="text-white hover:text-gray-300 p-2"
            :disabled="!hasNext"
          >
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { imageApi } from '@/api/images'

interface Props {
  imageUrl?: string
  altText?: string
  images?: string[] // For future multi-image support
  showImageInfo?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  altText: 'Produktbild',
  showImageInfo: true
})

const emit = defineEmits<{
  imageError: [url: string]
}>()

// State
const showLightbox = ref(false)
const currentImageIndex = ref(0)

// Computed
const showNavigation = computed(() => {
  return props.images && props.images.length > 1
})

const hasPrevious = computed(() => {
  return showNavigation.value && currentImageIndex.value > 0
})

const hasNext = computed(() => {
  return showNavigation.value && currentImageIndex.value < (props.images?.length || 0) - 1
})

const currentImageUrl = computed(() => {
  if (props.images && props.images.length > 0) {
    return props.images[currentImageIndex.value]
  }
  return props.imageUrl
})

// Methods
const openLightbox = () => {
  showLightbox.value = true
  document.body.style.overflow = 'hidden'
}

const closeLightbox = () => {
  showLightbox.value = false
  document.body.style.overflow = ''
}

const previousImage = () => {
  if (hasPrevious.value) {
    currentImageIndex.value--
  }
}

const nextImage = () => {
  if (hasNext.value) {
    currentImageIndex.value++
  }
}

const getImageUrl = (url: string, size: 'thumbnail' | 'medium' | 'original' = 'medium'): string => {
  return imageApi.getImageUrl(url, size)
}

const onImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.warn('Failed to load image:', img.src)
  emit('imageError', img.src)
}

// Keyboard navigation
const handleKeydown = (event: KeyboardEvent) => {
  if (!showLightbox.value) return

  switch (event.key) {
    case 'Escape':
      closeLightbox()
      break
    case 'ArrowLeft':
      previousImage()
      break
    case 'ArrowRight':
      nextImage()
      break
  }
}

// Lifecycle
onMounted(() => {
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
  // Ensure body overflow is reset
  document.body.style.overflow = ''
})
</script>

<style scoped>
.image-gallery {
  @apply w-full;
}

/* Ensure the lightbox is above everything */
.fixed {
  z-index: 9999;
}
</style>