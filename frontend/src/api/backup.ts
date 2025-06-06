import { apiClient } from './client'

export interface BackupMetadata {
  totalProducts: number
  totalCategories: number
  totalLocations: number
  applicationVersion: string
  description: string
}

export interface BackupData {
  backupTimestamp: string
  version: string
  metadata: BackupMetadata
  categories: any[]
  locations: any[]
  products: any[]
}

export interface RestoreStats {
  categoriesImported: number
  categoriesSkipped: number
  locationsImported: number
  locationsSkipped: number
  productsImported: number
  productsSkipped: number
  totalProcessed: number
}

export interface ConflictInfo {
  type: string
  name: string
  resolution: string
  details?: string
}

export interface RestoreResult {
  success: boolean
  message: string
  stats?: RestoreStats
  errors?: string[]
  warnings?: string[]
  conflicts?: ConflictInfo[]
}

export type ConflictResolution = 'SKIP' | 'OVERWRITE' | 'RENAME'

export const backupApi = {
  /**
   * Export backup data and trigger download
   */
  async exportBackup(format: 'json' | 'zip' = 'json'): Promise<void> {
    try {
      const response = await apiClient.get(`/backup/export?format=${format}`, {
        responseType: 'blob'
      })
      
      // Create blob URL and trigger download
      const blob = new Blob([response.data])
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      
      // Extract filename from Content-Disposition header or create default
      const contentDisposition = response.headers['content-disposition']
      let filename = `gefrierschrank-backup-${new Date().toISOString().slice(0, 19).replace(/:/g, '-')}.${format}`
      
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename="(.+)"/)
        if (filenameMatch) {
          filename = filenameMatch[1]
        }
      }
      
      link.download = filename
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
      
    } catch (error) {
      console.error('Failed to export backup:', error)
      throw new Error('Backup-Export fehlgeschlagen')
    }
  },

  /**
   * Import backup from file
   */
  async importBackup(
    file: File,
    clearExistingData: boolean = false,
    conflictResolution: ConflictResolution = 'SKIP',
    preserveIds: boolean = false
  ): Promise<RestoreResult> {
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('clearExistingData', clearExistingData.toString())
      formData.append('conflictResolution', conflictResolution)
      formData.append('preserveIds', preserveIds.toString())

      const response = await apiClient.post<RestoreResult>('/backup/import', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })

      return response.data
    } catch (error: any) {
      console.error('Failed to import backup:', error)
      
      // Extract error message from response if available
      const errorMessage = error.response?.data?.message || error.response?.data || error.message || 'Unknown error'
      
      if (error.response?.data && typeof error.response.data === 'object') {
        return error.response.data
      }
      
      throw new Error(`Backup-Import fehlgeschlagen: ${errorMessage}`)
    }
  },

  /**
   * Restore from backup data object
   */
  async restoreFromBackup(
    backup: BackupData,
    options: {
      clearExistingData?: boolean
      conflictResolution?: ConflictResolution
      preserveIds?: boolean
    } = {}
  ): Promise<RestoreResult> {
    try {
      const restoreRequest = {
        backup,
        options: {
          clearExistingData: false,
          skipConflicts: false,
          preserveIds: false,
          conflictResolution: 'SKIP' as ConflictResolution,
          ...options
        }
      }

      const response = await apiClient.post<RestoreResult>('/backup/restore', restoreRequest)
      return response.data
    } catch (error: any) {
      console.error('Failed to restore backup:', error)
      
      const errorMessage = error.response?.data?.message || error.message || 'Unknown error'
      
      if (error.response?.data && typeof error.response.data === 'object') {
        return error.response.data
      }
      
      throw new Error(`Backup-Wiederherstellung fehlgeschlagen: ${errorMessage}`)
    }
  },

  /**
   * Clear all data
   */
  async clearAllData(): Promise<void> {
    try {
      await apiClient.delete('/backup/clear')
    } catch (error) {
      console.error('Failed to clear data:', error)
      throw new Error('Fehler beim Löschen der Daten')
    }
  },

  /**
   * Validate backup file
   */
  async validateBackup(file: File): Promise<string> {
    try {
      const formData = new FormData()
      formData.append('file', file)

      const response = await apiClient.get<string>('/backup/validate', {
        params: { file: formData }
      })

      return response.data
    } catch (error: any) {
      console.error('Failed to validate backup:', error)
      const errorMessage = error.response?.data || error.message || 'Unknown error'
      throw new Error(`Backup-Validierung fehlgeschlagen: ${errorMessage}`)
    }
  }
}