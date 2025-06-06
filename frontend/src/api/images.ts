import { apiClient } from './client'

export interface ImageUploadResponse {
  success: boolean
  filename: string
  size: number
  contentType: string
  width: number
  height: number
  thumbnailUrl: string
  mediumUrl: string
  originalUrl: string
}

export interface ImageInfo {
  filename: string
  size: number
  contentType: string
  thumbnailUrl: string
  mediumUrl: string
  originalUrl: string
}

export const imageApi = {
  /**
   * Upload an image file
   */
  async uploadImage(file: File): Promise<ImageUploadResponse> {
    try {
      const formData = new FormData()
      formData.append('file', file)

      const response = await apiClient.post<ImageUploadResponse>('/images/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })

      if (!response.data.success) {
        throw new Error('Upload failed')
      }

      return response.data
    } catch (error: any) {
      console.error('Failed to upload image:', error)
      
      // Extract error message from response if available
      const errorMessage = error.response?.data?.error || 
                          error.response?.data?.message || 
                          error.message || 
                          'Unbekannter Fehler beim Hochladen'
      
      throw new Error(`Bild-Upload fehlgeschlagen: ${errorMessage}`)
    }
  },

  /**
   * Delete an image by filename
   */
  async deleteImage(filename: string): Promise<void> {
    try {
      await apiClient.delete(`/images/${filename}`)
    } catch (error: any) {
      console.error('Failed to delete image:', error)
      
      const errorMessage = error.response?.data?.error || 
                          error.response?.data?.message || 
                          error.message || 
                          'Unbekannter Fehler beim Löschen'
      
      throw new Error(`Bild-Löschung fehlgeschlagen: ${errorMessage}`)
    }
  },

  /**
   * Get image information by filename
   */
  async getImageInfo(filename: string): Promise<ImageInfo> {
    try {
      const response = await apiClient.get<ImageInfo>(`/images/info/${filename}`)
      return response.data
    } catch (error: any) {
      console.error('Failed to get image info:', error)
      
      if (error.response?.status === 404) {
        throw new Error('Bild nicht gefunden')
      }
      
      const errorMessage = error.response?.data?.error || 
                          error.response?.data?.message || 
                          error.message || 
                          'Unbekannter Fehler'
      
      throw new Error(`Fehler beim Laden der Bildinformationen: ${errorMessage}`)
    }
  },

  /**
   * Get image URL for different sizes
   */
  getImageUrl(filename: string, size: 'thumbnail' | 'medium' | 'original' = 'original'): string {
    if (!filename) return ''
    
    // Get base URL for images (without /api suffix) 
    let baseUrl = import.meta.env.VITE_API_BASE_URL || 
      (window.location.hostname === 'localhost' ? 'http://localhost:8080' : `http://${window.location.hostname}:8080`)
    
    // Remove /api suffix if present (since image URLs need direct server access)
    if (baseUrl.endsWith('/api')) {
      baseUrl = baseUrl.slice(0, -4)
    }
    
    // Handle different input formats and extract just the filename
    let cleanFilename = filename
    
    // If it's already a full HTTP URL, extract just the filename
    if (filename.startsWith('http')) {
      const urlParts = filename.split('/api/images/')
      if (urlParts.length > 1) {
        cleanFilename = urlParts[1].split('?')[0] // Remove any existing query params
      }
    }
    // If it's a relative API path, extract just the filename
    else if (filename.startsWith('/api/images/')) {
      cleanFilename = filename.replace('/api/images/', '').split('?')[0]
    }
    // If it already has query params, remove them
    else if (filename.includes('?')) {
      cleanFilename = filename.split('?')[0]
    }
    
    // Now construct the URL with the desired size
    return `${baseUrl}/api/images/${cleanFilename}${size !== 'original' ? `?size=${size}` : ''}`
  },

  /**
   * Check if an image exists
   */
  async imageExists(filename: string): Promise<boolean> {
    try {
      await this.getImageInfo(filename)
      return true
    } catch (error) {
      return false
    }
  },

  /**
   * Get optimized image URL based on context
   */
  getOptimizedImageUrl(filename: string, context: 'thumbnail' | 'card' | 'detail' | 'gallery'): string {
    if (!filename) return ''
    
    const sizeMap = {
      thumbnail: 'thumbnail',
      card: 'medium',
      detail: 'medium',
      gallery: 'original'
    } as const
    
    return this.getImageUrl(filename, sizeMap[context])
  }
}