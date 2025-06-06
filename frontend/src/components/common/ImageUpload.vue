<template>
  <div class="image-upload-container">
    <!-- Display current image if exists -->
    <div v-if="currentDisplayedImage" class="current-image mb-4" :key="forceUpdateKey">
      <div class="relative inline-block">
        <img 
          :src="getImageUrl(currentDisplayedImage, 'medium')" 
          :alt="altText"
          class="w-32 h-32 object-cover rounded-lg border-2 border-gray-200"
          @error="onImageError"
          @load="() => console.log('🖼️ Image loaded successfully:', getImageUrl(currentDisplayedImage, 'medium'))"
          :key="`img-${forceUpdateKey}`"
        />
        <button
          v-if="allowDelete"
          @click="deleteImage"
          class="absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center hover:bg-red-600 transition-colors"
          title="Bild löschen"
        >
          ×
        </button>
      </div>
    </div>

    <!-- Upload area -->
    <div class="upload-area">
      <!-- Hidden file input -->
      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        :capture="mobileCapture ? 'environment' : undefined"
        @change="handleFileSelect"
        class="hidden"
      />

      <!-- Upload button/drop zone -->
      <div
        @click="triggerFileSelect"
        @dragover.prevent="onDragOver"
        @dragleave.prevent="onDragLeave"
        @drop.prevent="onDrop"
        :class="[
          'border-2 border-dashed rounded-lg p-6 text-center cursor-pointer transition-colors',
          isDragging ? 'border-blue-500 bg-blue-50' : 'border-gray-300 hover:border-gray-400',
          isUploading ? 'pointer-events-none opacity-50' : ''
        ]"
      >
        <!-- Upload icon and text -->
        <div v-if="!isUploading" class="space-y-2">
          <svg class="w-12 h-12 mx-auto text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 48 48">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02" />
          </svg>
          <div class="text-sm text-gray-600">
            <p class="font-medium">Klicken zum Hochladen oder Datei hierher ziehen</p>
            <p class="text-xs mt-1">PNG, JPG, GIF, WebP bis zu 10MB</p>
          </div>
          
          <!-- Camera button -->
          <div v-if="mobileCapture && cameraSupported" class="pt-2 flex gap-2">
            <button
              @click.stop="openFileDialog"
              class="inline-flex items-center px-3 py-2 border border-gray-300 shadow-sm text-sm leading-4 font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
              </svg>
              Datei wählen
            </button>
            <button
              @click.stop="openCamera"
              class="inline-flex items-center px-3 py-2 border border-blue-300 shadow-sm text-sm leading-4 font-medium rounded-md text-blue-700 bg-blue-50 hover:bg-blue-100 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
            >
              <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 9a2 2 0 012-2h.93a2 2 0 001.664-.89l.812-1.22A2 2 0 0110.07 4h3.86a2 2 0 011.664.89l.812 1.22A2 2 0 0118.07 7H19a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2V9z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 13a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              Kamera öffnen
            </button>
          </div>
        </div>

        <!-- Loading state -->
        <div v-else class="space-y-2">
          <svg class="animate-spin w-8 h-8 mx-auto text-blue-500" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p class="text-sm text-gray-600">Wird hochgeladen...</p>
        </div>
      </div>

      <!-- Error message -->
      <div v-if="errorMessage" class="mt-2 text-sm text-red-600">
        {{ errorMessage }}
      </div>

      <!-- Upload progress -->
      <div v-if="uploadProgress > 0 && uploadProgress < 100" class="mt-2">
        <div class="bg-gray-200 rounded-full h-2">
          <div 
            class="bg-blue-600 h-2 rounded-full transition-all duration-300"
            :style="{ width: uploadProgress + '%' }"
          ></div>
        </div>
        <p class="text-xs text-gray-500 mt-1">{{ uploadProgress }}% hochgeladen</p>
      </div>
    </div>
  </div>

  <!-- Camera Modal -->
  <div
    v-if="showCameraModal"
    style="position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: red; z-index: 999999; display: flex; align-items: center; justify-content: center;"
  >
      <div style="background: yellow; border: 10px solid black; padding: 20px; width: 500px; height: 400px; display: flex; flex-direction: column;">
        <h1 style="color: black; font-size: 48px; text-align: center;">KAMERA MODAL TEST</h1>
        <button @click="closeCameraModal" style="background: black; color: white; padding: 20px; font-size: 24px; margin-top: 20px;">
          MODAL SCHLIESSEN
        </button>
        <video
          ref="videoElement"
          autoplay
          playsinline
          muted
          style="width: 100%; height: 200px; background: black; margin-top: 20px;"
          @loadedmetadata="onVideoLoaded"
          @error="onVideoError"
        ></video>
        <canvas ref="canvasElement" style="display: none;"></canvas>
      </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { imageApi, type ImageUploadResponse } from '@/api/images'

// Props
interface Props {
  currentImageUrl?: string
  altText?: string
  allowDelete?: boolean
  mobileCapture?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  altText: 'Produktbild',
  allowDelete: true,
  mobileCapture: true
})

// Emits
const emit = defineEmits<{
  upload: [result: ImageUploadResponse]
  delete: []
  error: [message: string]
}>()

// Reactive state
const fileInput = ref<HTMLInputElement>()
const videoElement = ref<HTMLVideoElement>()
const canvasElement = ref<HTMLCanvasElement>()
const isDragging = ref(false)
const isUploading = ref(false)
const uploadProgress = ref(0)
const errorMessage = ref('')
const showCameraModal = ref(false)
const cameraStream = ref<MediaStream | null>(null)
const cameraSupported = ref(false)
const internalImageUrl = ref<string>('')
const forceUpdateKey = ref(0)

// Computed property to get current image URL with cache-busting
const currentDisplayedImage = computed(() => {
  const url = internalImageUrl.value || props.currentImageUrl
  if (!url) return null
  
  // Add cache-busting parameter to force browser reload
  const separator = url.includes('?') ? '&' : '?'
  return url + separator + '_cb=' + forceUpdateKey.value
})

// Computed
const isMobile = computed(() => {
  return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
})

// Methods
const triggerFileSelect = () => {
  fileInput.value?.click()
}

const openFileDialog = () => {
  fileInput.value?.click()
}

const testCameraFixed = async () => {
  console.log('🎬 FIXED CAMERA STARTING...')
  
  let currentStream = null
  let facingMode = 'user'
  let video // Declare video variable in outer scope
  
  const startCamera = async (facing) => {
    if (currentStream) {
      currentStream.getTracks().forEach(track => track.stop())
    }
    
    try {
      currentStream = await navigator.mediaDevices.getUserMedia({ 
        video: { facingMode: facing } 
      })
      video.srcObject = currentStream
      console.log('🎬 Camera switched to:', facing)
    } catch (error) {
      console.error('🎬 Camera switch failed:', error)
      alert('Kamera wechseln fehlgeschlagen: ' + error.message)
    }
  }
  
  try {
    currentStream = await navigator.mediaDevices.getUserMedia({ 
      video: { facingMode: facingMode } 
    })
    console.log('🎬 Camera stream obtained')
    
    // Create video element
    video = document.createElement('video')
    video.autoplay = true
    video.playsInline = true 
    video.muted = true
    video.srcObject = currentStream
    video.style.position = 'fixed'
    video.style.top = '50px'
    video.style.left = '50px'
    video.style.width = '400px'
    video.style.height = '300px'
    video.style.zIndex = '999999'
    video.style.border = '5px solid red'
    video.style.background = 'black'
    
    // Create canvas for capturing photos
    const canvas = document.createElement('canvas')
    canvas.style.display = 'none'
    
    document.body.appendChild(video)
    document.body.appendChild(canvas)
    console.log('🎬 Video element added to page')
    
    // Create button container
    const buttonContainer = document.createElement('div')
    buttonContainer.style.position = 'fixed'
    buttonContainer.style.top = '10px'
    buttonContainer.style.left = '50px'
    buttonContainer.style.zIndex = '9999999'
    buttonContainer.style.display = 'flex'
    buttonContainer.style.gap = '10px'
    
    // Close button
    const closeBtn = document.createElement('button')
    closeBtn.textContent = 'X SCHLIESSEN'
    closeBtn.style.background = 'red'
    closeBtn.style.color = 'white'
    closeBtn.style.padding = '10px'
    closeBtn.style.fontSize = '16px'
    closeBtn.style.border = 'none'
    closeBtn.style.cursor = 'pointer'
    closeBtn.onclick = () => {
      if (currentStream) {
        currentStream.getTracks().forEach(track => track.stop())
      }
      video.remove()
      canvas.remove()
      buttonContainer.remove()
      console.log('🎬 Camera closed')
    }
    
    // Take photo button
    const photoBtn = document.createElement('button')
    photoBtn.textContent = '📸 Foto aufnehmen'
    photoBtn.style.background = 'green'
    photoBtn.style.color = 'white'
    photoBtn.style.padding = '10px'
    photoBtn.style.fontSize = '16px'
    photoBtn.style.border = 'none'
    photoBtn.style.cursor = 'pointer'
    photoBtn.style.borderRadius = '4px'
    photoBtn.onclick = async function() {
      try {
        console.log('📸 Taking photo...')
        
        // Set canvas dimensions to video dimensions
        canvas.width = video.videoWidth
        canvas.height = video.videoHeight
        
        // Draw current video frame to canvas
        const ctx = canvas.getContext('2d')
        ctx.drawImage(video, 0, 0, canvas.width, canvas.height)
        
        // Convert to blob
        canvas.toBlob(async (blob) => {
          if (blob) {
            const file = new File([blob], `camera-photo-${Date.now()}.jpg`, { type: 'image/jpeg' })
            
            try {
              // Upload file first, before closing camera
              console.log('📸 Uploading image...')
              const result = await imageApi.uploadImage(file)
              console.log('📸 Upload successful:', result)
              
              // Close camera after successful upload
              if (currentStream) {
                currentStream.getTracks().forEach(track => track.stop())
              }
              video.remove()
              canvas.remove()
              buttonContainer.remove()
              console.log('📸 Camera closed after upload')
              
              // CRITICAL: Store result in window for immediate access
              window.lastCameraUploadResult = result
              console.log('📸 Stored result in window.lastCameraUploadResult')
              
              // Use localStorage as primary communication method
              localStorage.setItem('pendingProductImage', result.originalUrl)
              localStorage.setItem('pendingProductImageTimestamp', Date.now().toString())
              console.log('📸 Stored in localStorage for immediate processing')
              
              // IMMEDIATELY update ALL ImageUpload instances via direct function calls
              console.log('📸 DIRECT UPDATE: Looking for all ImageUpload instances...')
              
              // Method 1: Update via global active instance
              if (window.activeImageUploadInstance) {
                console.log('📸 DIRECT: Found activeImageUploadInstance, updating...')
                window.activeImageUploadInstance.setImageUrl(result.originalUrl)
              }
              
              // Method 2: Update all mounted instances via global registry
              if (window.allImageUploadInstances && window.allImageUploadInstances.length > 0) {
                console.log(`📸 DIRECT: Found ${window.allImageUploadInstances.length} instances, updating all...`)
                window.allImageUploadInstances.forEach((instance, index) => {
                  console.log(`📸 DIRECT: Updating instance ${index}`)
                  if (instance.setImageUrl) {
                    instance.setImageUrl(result.originalUrl)
                  }
                })
              } else {
                console.log('📸 DIRECT: NO instances found in registry - component was unmounted!')
                
                // Create a delayed update that will trigger when component re-mounts
                window.pendingCameraUploadForNewComponent = {
                  url: result.originalUrl,
                  result: result,
                  timestamp: Date.now()
                }
                console.log('📸 DIRECT: Created pending upload for new component')
              }
              
              // Method 3: Force immediate ProductModal update
              if (window.productModalInstance) {
                console.log('📸 DIRECT: Updating ProductModal...')
                if (window.productModalInstance.updateImageUploadDirectly) {
                  window.productModalInstance.updateImageUploadDirectly(result.originalUrl)
                }
              }
              
              // Trigger a global event that all components can listen to
              const globalEvent = new CustomEvent('cameraUploadComplete', {
                detail: { 
                  url: result.originalUrl,
                  result: result,
                  timestamp: Date.now()
                }
              })
              window.dispatchEvent(globalEvent)
              console.log('📸 Dispatched cameraUploadComplete event')
              
              // DON'T trigger Dashboard refresh immediately - it causes re-mount!
              // Instead, delay it to allow image to appear first
              setTimeout(() => {
                if (window.refreshProductsList) {
                  console.log('📸 Delayed Dashboard products refresh')
                  window.refreshProductsList()
                }
              }, 2000) // 2 seconds delay
              
            } catch (error) {
              console.error('📸 Upload failed:', error)
              alert('Upload fehlgeschlagen: ' + error.message)
              
              // Close camera even if upload failed
              if (currentStream) {
                currentStream.getTracks().forEach(track => track.stop())
              }
              video.remove()
              canvas.remove()
              buttonContainer.remove()
            }
          }
        }, 'image/jpeg', 0.9)
        
      } catch (error) {
        console.error('📸 Photo capture failed:', error)
        alert('Foto fehlgeschlagen: ' + error.message)
      }
    }
    
    // Switch camera button
    const switchBtn = document.createElement('button')
    switchBtn.textContent = '🔄 KAMERA'
    switchBtn.style.background = 'blue'
    switchBtn.style.color = 'white'
    switchBtn.style.padding = '10px'
    switchBtn.style.fontSize = '16px'
    switchBtn.style.border = 'none'
    switchBtn.style.cursor = 'pointer'
    switchBtn.onclick = async () => {
      console.log('🎬 Switching camera from:', facingMode)
      facingMode = facingMode === 'user' ? 'environment' : 'user'
      await startCamera(facingMode)
    }
    
    buttonContainer.appendChild(closeBtn)
    buttonContainer.appendChild(photoBtn)
    buttonContainer.appendChild(switchBtn)
    document.body.appendChild(buttonContainer)
    
    console.log('🎬 Fixed camera ready!')
    
  } catch (error) {
    console.error('🎬 Fixed camera failed:', error)
    alert('Kamera-Test fehlgeschlagen: ' + error.message)
  }
}

const testCamera = async () => {
  console.log('🎬 SIMPLE CAMERA TEST STARTING...')
  
  let currentStream = null
  let facingMode = 'user' // Start with front camera
  
  let video, canvas // Declare here so they're accessible in startCamera
  
  // Define startCamera function after video/canvas are created
  const startCamera = async (facing) => {
    console.log('🎬 Starting camera with facing mode:', facing)
    
    // Stop current stream if exists
    if (currentStream) {
      currentStream.getTracks().forEach(track => track.stop())
    }
    
    try {
      currentStream = await navigator.mediaDevices.getUserMedia({ 
        video: { facingMode: facing } 
      })
      video.srcObject = currentStream
      console.log('🎬 Camera switched to:', facing)
    } catch (error) {
      console.error('🎬 Camera switch failed:', error)
      alert('Kamera wechseln fehlgeschlagen: ' + error.message)
    }
  }

  try {
    currentStream = await navigator.mediaDevices.getUserMedia({ 
      video: { facingMode: facingMode } 
    })
    console.log('🎬 Camera stream obtained')
    
    // Create video element
    video = document.createElement('video')
    video.autoplay = true
    video.playsInline = true 
    video.muted = true
    video.srcObject = currentStream
    video.style.position = 'fixed'
    video.style.top = '50px'
    video.style.left = '50px'
    video.style.width = '400px'
    video.style.height = '300px'
    video.style.zIndex = '999999'
    video.style.border = '5px solid red'
    video.style.background = 'black'
    
    // Create canvas for capturing photos
    canvas = document.createElement('canvas')
    canvas.style.display = 'none'
    
    document.body.appendChild(video)
    document.body.appendChild(canvas)
    console.log('🎬 Video element added to page')
    
    // Create button container
    const buttonContainer = document.createElement('div')
    buttonContainer.style.position = 'fixed'
    buttonContainer.style.top = '10px'
    buttonContainer.style.left = '50px'
    buttonContainer.style.zIndex = '9999999'
    buttonContainer.style.display = 'flex'
    buttonContainer.style.gap = '10px'
    
    // Close button
    const closeBtn = document.createElement('button')
    closeBtn.textContent = 'X SCHLIESSEN'
    closeBtn.style.background = 'red'
    closeBtn.style.color = 'white'
    closeBtn.style.padding = '10px'
    closeBtn.style.fontSize = '16px'
    closeBtn.style.border = 'none'
    closeBtn.style.cursor = 'pointer'
    closeBtn.onclick = () => {
      if (currentStream) {
        currentStream.getTracks().forEach(track => track.stop())
      }
      video.remove()
      canvas.remove()
      buttonContainer.remove()
      console.log('🎬 Camera test closed')
    }
    
    // Take photo button
    const photoBtn = document.createElement('button')
    photoBtn.textContent = '📸 FOTO'
    photoBtn.style.background = 'green'
    photoBtn.style.color = 'white'
    photoBtn.style.padding = '10px'
    photoBtn.style.fontSize = '16px'
    photoBtn.style.border = 'none'
    photoBtn.style.cursor = 'pointer'
    photoBtn.onclick = () => {
      alert('📸 FOTO Button wurde geklickt!')
      
      // Set canvas dimensions to video dimensions
      canvas.width = video.videoWidth
      canvas.height = video.videoHeight
      
      // Draw current video frame to canvas
      const ctx = canvas.getContext('2d')
      ctx.drawImage(video, 0, 0, canvas.width, canvas.height)
      
      alert('📸 Foto aufgenommen, beginne Upload...')
      
      // Convert to blob and upload
      canvas.toBlob(async (blob) => {
        if (blob) {
          alert('📸 Blob erstellt, starte Upload...')
          console.log('🎬 Photo captured, uploading...')
          const file = new File([blob], `camera-photo-${Date.now()}.jpg`, { type: 'image/jpeg' })
          
          try {
            alert('🎬 Beginne Upload...')
            const result = await imageApi.uploadImage(file)
            alert('🎬 Upload erfolgreich! Sende Event...')
            console.log('🎬 Photo uploaded successfully:', result)
            console.log('🎬 Emitting upload event with result:', result)
            
            // Emit the upload event to parent component
            emit('upload', result)
            alert('🎬 Event gesendet!')
            
            // Close camera after successful upload
            if (currentStream) {
              currentStream.getTracks().forEach(track => track.stop())
            }
            video.remove()
            canvas.remove()
            buttonContainer.remove()
            
            console.log('🎬 Upload event emitted, camera closed')
            // Don't show alert - let the parent component handle the UI update
          } catch (error) {
            console.error('🎬 Photo upload failed:', error)
            alert('Foto-Upload fehlgeschlagen: ' + error.message)
          }
        } else {
          alert('📸 Fehler: Blob konnte nicht erstellt werden!')
        }
      }, 'image/jpeg', 0.9)
    }
    
    // Switch camera button
    const switchBtn = document.createElement('button')
    switchBtn.textContent = '🔄 KAMERA'
    switchBtn.style.background = 'blue'
    switchBtn.style.color = 'white'
    switchBtn.style.padding = '10px'
    switchBtn.style.fontSize = '16px'
    switchBtn.style.border = 'none'
    switchBtn.style.cursor = 'pointer'
    switchBtn.onclick = async () => {
      console.log('🎬 Switching camera from:', facingMode)
      facingMode = facingMode === 'user' ? 'environment' : 'user'
      await startCamera(facingMode)
    }
    
    buttonContainer.appendChild(closeBtn)
    buttonContainer.appendChild(photoBtn)
    buttonContainer.appendChild(switchBtn)
    document.body.appendChild(buttonContainer)
    
    console.log('🎬 Buttons added - Close, Photo, Switch Camera')
    
  } catch (error) {
    console.error('🎬 Camera test failed:', error)
    alert('Kamera-Test fehlgeschlagen: ' + error.message)
  }
}

const openCamera = async () => {
  // Store reference to current component state before camera opens
  console.log('📸 CAMERA OPENING: Storing component state...')
  window.imageUploadStateBeforeCamera = {
    internalImageUrl: internalImageUrl.value,
    forceUpdateKey: forceUpdateKey.value,
    componentActive: true
  }
  
  // Use the working camera implementation
  await testCameraFixed()
}

const closeCameraModal = () => {
  showCameraModal.value = false
  if (cameraStream.value) {
    cameraStream.value.getTracks().forEach(track => track.stop())
    cameraStream.value = null
  }
}

const capturePhoto = async () => {
  console.log('📸 Capture photo clicked')
  
  // Find video element
  let video = videoElement.value
  if (!video) {
    video = document.querySelector('video') as HTMLVideoElement
  }
  
  // Find canvas element  
  let canvas = canvasElement.value
  if (!canvas) {
    canvas = document.querySelector('canvas') as HTMLCanvasElement
  }
  
  if (!video || !canvas) {
    console.error('📸 Video or canvas not found')
    return
  }

  const context = canvas.getContext('2d')
  if (!context) return

  // Set canvas size to video size
  canvas.width = video.videoWidth
  canvas.height = video.videoHeight

  // Draw current video frame to canvas
  context.drawImage(video, 0, 0, canvas.width, canvas.height)

  // Convert canvas to blob
  canvas.toBlob(async (blob) => {
    if (blob) {
      console.log('📸 Photo captured, uploading...')
      
      // Create a File object from the blob
      const file = new File([blob], `camera-photo-${Date.now()}.jpg`, {
        type: 'image/jpeg'
      })
      
      try {
        const result = await imageApi.uploadImage(file)
        console.log('📸 Upload successful:', result)
        
        // Close camera modal
        closeCameraModal()
        
        // Emit upload event
        emit('upload', result)
        console.log('📸 Upload event emitted')
        
      } catch (error) {
        console.error('📸 Upload failed:', error)
        errorMessage.value = 'Upload fehlgeschlagen: ' + error.message
      }
    }
  }, 'image/jpeg', 0.9)
}

const checkCameraSupport = async () => {
  try {
    if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
      cameraSupported.value = true
    }
  } catch (error) {
    cameraSupported.value = false
  }
}

const onVideoLoaded = () => {
  console.log('Video metadata loaded, dimensions:', 
    videoElement.value?.videoWidth, 'x', videoElement.value?.videoHeight)
}

const onVideoError = (event: Event) => {
  console.error('Video error:', event)
  errorMessage.value = 'Fehler beim Laden der Kamera-Vorschau.'
}

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    uploadFile(file)
  }
}

const onDragOver = (event: DragEvent) => {
  event.preventDefault()
  isDragging.value = true
}

const onDragLeave = (event: DragEvent) => {
  event.preventDefault()
  isDragging.value = false
}

const onDrop = (event: DragEvent) => {
  event.preventDefault()
  isDragging.value = false
  
  const files = event.dataTransfer?.files
  if (files && files.length > 0) {
    uploadFile(files[0])
  }
}

const uploadFile = async (file: File) => {
  errorMessage.value = ''
  
  // Validate file
  if (!validateFile(file)) {
    return
  }

  isUploading.value = true
  uploadProgress.value = 0

  try {
    // Simulate progress
    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value += 10
      }
    }, 100)

    const result = await imageApi.uploadImage(file)
    
    clearInterval(progressInterval)
    uploadProgress.value = 100
    
    console.log('🚀 Upload successful, emitting event immediately:', result)
    
    // Update internal image URL immediately for local display
    internalImageUrl.value = result.originalUrl
    console.log('🚀 Updated internal image URL:', result.originalUrl)
    
    // Force component re-render
    forceImageUpdate()
    
    // Emit immediately - don't wait for timeout
    emit('upload', result)
    console.log('🚀 Upload event emitted')
    
    // Brief delay to show completion UI only
    setTimeout(() => {
      isUploading.value = false
      uploadProgress.value = 0
    }, 500)
    
  } catch (error) {
    isUploading.value = false
    uploadProgress.value = 0
    const message = error instanceof Error ? error.message : 'Upload fehlgeschlagen'
    errorMessage.value = message
    emit('error', message)
  }
}

const validateFile = (file: File): boolean => {
  // Check file type
  if (!file.type.startsWith('image/')) {
    errorMessage.value = 'Bitte wählen Sie eine Bilddatei aus'
    return false
  }

  // Check file size (10MB)
  const maxSize = 10 * 1024 * 1024
  if (file.size > maxSize) {
    errorMessage.value = 'Datei ist zu groß. Maximum: 10MB'
    return false
  }

  // Check allowed formats
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    errorMessage.value = 'Dateityp nicht unterstützt. Erlaubt: JPG, PNG, GIF, WebP'
    return false
  }

  return true
}

const deleteImage = () => {
  internalImageUrl.value = ''
  forceImageUpdate()
  emit('delete')
}

const onImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  console.warn('Failed to load image:', img.src)
  // Could emit an error or show a placeholder
}

const getImageUrl = (url: string, size: 'thumbnail' | 'medium' | 'original' = 'original'): string => {
  if (!url) return ''
  
  // Remove existing cache-busting parameter to avoid duplicates
  const cleanUrl = url.split('_cb=')[0].replace(/[&?]$/, '')
  const baseUrl = imageApi.getImageUrl(cleanUrl, size)
  
  // Add fresh cache-busting parameter
  const separator = baseUrl.includes('?') ? '&' : '?'
  return baseUrl + separator + '_cb=' + Date.now()
}

const forceImageUpdate = () => {
  forceUpdateKey.value++
  console.log('🔄 Force update triggered, key:', forceUpdateKey.value)
}

// Watch for changes in currentImageUrl prop
watch(() => props.currentImageUrl, (newUrl) => {
  if (newUrl !== internalImageUrl.value) {
    internalImageUrl.value = newUrl || ''
    forceImageUpdate()
    console.log('📷 Synced internal image URL with prop:', newUrl)
  }
}, { immediate: true })

// Debug watch for currentDisplayedImage (remove for production)
// watch(currentDisplayedImage, (newUrl, oldUrl) => {
//   console.log('🖼️ currentDisplayedImage changed from:', oldUrl, 'to:', newUrl)
// }, { immediate: true })

// Debug watch for forceUpdateKey (remove for production) 
// watch(forceUpdateKey, (newKey, oldKey) => {
//   console.log('🔄 forceUpdateKey changed from:', oldKey, 'to:', newKey)
// })

// Global event listener for image updates
const handleGlobalImageUpdate = (event: CustomEvent) => {
  console.log('🌍 ImageUpload: Received global image update event:', event.detail)
  internalImageUrl.value = event.detail.url
  forceImageUpdate()
}

// Global event listener for camera upload completion
const handleCameraUploadComplete = (event: CustomEvent) => {
  console.log('📸 ImageUpload: Received camera upload complete event:', event.detail)
  
  // Update internal image immediately
  internalImageUrl.value = event.detail.url
  forceImageUpdate()
  
  // Also emit the upload event for parent component
  if (event.detail.result) {
    emit('upload', event.detail.result)
    console.log('📸 Emitted upload event from camera completion')
  }
}

// Store instance reference for cleanup
let instanceRef: any = null

// Reset file input when component unmounts or image changes
onMounted(() => {
  // Clear any previous file selection
  if (fileInput.value) {
    fileInput.value.value = ''
  }
  
  // Check camera support
  checkCameraSupport()
  
  // CRITICAL: Check for pending camera upload immediately on mount
  const pendingImage = localStorage.getItem('pendingProductImage')
  const pendingTimestamp = localStorage.getItem('pendingProductImageTimestamp')
  const lastUploadResult = window.lastCameraUploadResult
  const pendingForNewComponent = window.pendingCameraUploadForNewComponent
  
  // Method 1: Check localStorage
  if (pendingImage && pendingTimestamp) {
    const timestamp = parseInt(pendingTimestamp)
    const timeDiff = Date.now() - timestamp
    
    // If upload was within last 5 seconds, it's probably for us
    if (timeDiff < 5000) {
      console.log('🔥 ImageUpload: Found recent camera upload on mount:', pendingImage)
      internalImageUrl.value = pendingImage
      forceImageUpdate()
      
      // Emit upload event if we have the full result
      if (lastUploadResult) {
        console.log('🔥 Emitting upload event for pending camera result')
        emit('upload', lastUploadResult)
      }
      
      // Clear the pending data
      localStorage.removeItem('pendingProductImageTimestamp')
      console.log('🔥 Processed pending camera upload')
    }
  }
  
  // Method 2: Check for pending upload created for new component
  if (pendingForNewComponent) {
    const timeDiff = Date.now() - pendingForNewComponent.timestamp
    
    // If upload was within last 10 seconds
    if (timeDiff < 10000) {
      console.log('🔥 ImageUpload: Found pending upload for new component:', pendingForNewComponent.url)
      internalImageUrl.value = pendingForNewComponent.url
      forceImageUpdate()
      
      // Emit upload event
      if (pendingForNewComponent.result) {
        console.log('🔥 Emitting upload event for new component pending result')
        emit('upload', pendingForNewComponent.result)
      }
      
      // Clear the pending data
      delete window.pendingCameraUploadForNewComponent
      console.log('🔥 Processed pending upload for new component')
    }
  }
  
  // Register global function for force updating this component
  const componentId = Date.now() + Math.random()
  window[`forceImageUpdate_${componentId}`] = forceImageUpdate
  
  // Also register as latest active instance
  window.activeImageUploadInstance = {
    forceUpdate: forceImageUpdate,
    setImageUrl: (url: string) => {
      internalImageUrl.value = url
      forceImageUpdate()
      console.log('🖼️ ImageUpload: Set image URL via global function:', url)
    }
  }
  
  // Register in global instances array for multiple instance support
  if (!window.allImageUploadInstances) {
    window.allImageUploadInstances = []
  }
  const instanceId = Date.now() + Math.random()
  instanceRef = {
    id: instanceId,
    setImageUrl: (url: string) => {
      internalImageUrl.value = url
      forceImageUpdate()
      console.log(`🖼️ ImageUpload[${instanceId}]: Set image URL via registry:`, url)
    },
    forceUpdate: forceImageUpdate
  }
  window.allImageUploadInstances.push(instanceRef)
  console.log(`🖼️ ImageUpload: Registered instance ${instanceId} in global registry (${window.allImageUploadInstances.length} total)`)
  
  // Listen for global image update events
  window.addEventListener('imageUploadUpdate', handleGlobalImageUpdate)
  window.addEventListener('cameraUploadComplete', handleCameraUploadComplete)
  
  // Start polling for pending camera uploads every 500ms
  const pollInterval = setInterval(() => {
    const pendingImage = localStorage.getItem('pendingProductImage')
    const pendingTimestamp = localStorage.getItem('pendingProductImageTimestamp')
    
    if (pendingImage && pendingTimestamp && internalImageUrl.value !== pendingImage) {
      const timestamp = parseInt(pendingTimestamp)
      const timeDiff = Date.now() - timestamp
      
      // If upload was within last 10 seconds and we haven't processed it yet
      if (timeDiff < 10000) {
        console.log('🔥 Poll: Found pending camera upload:', pendingImage)
        internalImageUrl.value = pendingImage
        forceImageUpdate()
        
        // Emit upload event if we have the full result
        if (window.lastCameraUploadResult) {
          console.log('🔥 Poll: Emitting upload event for pending camera result')
          emit('upload', window.lastCameraUploadResult)
          window.lastCameraUploadResult = null // Clear after use
        }
        
        // Clear the pending data
        localStorage.removeItem('pendingProductImageTimestamp')
        console.log('🔥 Poll: Processed pending camera upload')
      }
    }
  }, 500)
  
  // Store interval for cleanup
  window.imageUploadPollInterval = pollInterval
  
  console.log('🖼️ ImageUpload: Registered global force update function, event listeners, and polling')
})

// Cleanup global references
onUnmounted(() => {
  if (window.activeImageUploadInstance) {
    delete window.activeImageUploadInstance
  }
  
  // Remove from global instances array
  if (window.allImageUploadInstances) {
    window.allImageUploadInstances = window.allImageUploadInstances.filter(instance => 
      instance.setImageUrl !== instanceRef.setImageUrl
    )
    console.log(`🖼️ ImageUpload: Removed instance from registry (${window.allImageUploadInstances.length} remaining)`)
  }
  
  window.removeEventListener('imageUploadUpdate', handleGlobalImageUpdate)
  window.removeEventListener('cameraUploadComplete', handleCameraUploadComplete)
  
  // Clear polling interval
  if (window.imageUploadPollInterval) {
    clearInterval(window.imageUploadPollInterval)
    delete window.imageUploadPollInterval
  }
})

// Expose methods for parent component access via ref
defineExpose({
  setImageUrl: (url: string) => {
    console.log('🔗 ImageUpload: setImageUrl called via ref:', url)
    internalImageUrl.value = url
    forceImageUpdate()
  },
  forceUpdate: () => {
    console.log('🔗 ImageUpload: forceUpdate called via ref')
    forceImageUpdate()
  },
  getCurrentImageUrl: () => internalImageUrl.value
})
</script>

<style scoped>
.image-upload-container {
  @apply w-full;
}

.upload-area {
  @apply w-full;
}

/* Ensure proper aspect ratio for image previews */
.current-image img {
  aspect-ratio: 1 / 1;
}
</style>