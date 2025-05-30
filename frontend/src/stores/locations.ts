import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Location, CreateLocationRequest } from '@/api/locations'
import * as locationsApi from '@/api/locations'

export const useLocationsStore = defineStore('locations', () => {
  // State
  const locations = ref<Location[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const locationsOptions = computed(() => 
    locations.value.map(location => ({
      value: location.id,
      label: location.name,
      section: location.freezerSection
    }))
  )

  const locationsBySection = computed(() => {
    return locations.value.reduce((acc, location) => {
      const section = location.freezerSection || 'Andere'
      if (!acc[section]) {
        acc[section] = []
      }
      acc[section].push(location)
      return acc
    }, {} as Record<string, Location[]>)
  })

  const getLocationById = computed(() => (id: number) => 
    locations.value.find(location => location.id === id)
  )

  // Actions
  const fetchLocations = async () => {
    try {
      loading.value = true
      error.value = null
      
      const response = await locationsApi.getLocations()
      locations.value = response.sort((a, b) => {
        if (a.sortOrder !== null && b.sortOrder !== null) {
          return a.sortOrder - b.sortOrder
        }
        return a.name.localeCompare(b.name)
      })
    } catch (err: any) {
      error.value = err.response?.data?.message || err.message || 'Failed to fetch locations'
      console.error('Error fetching locations:', err)
    } finally {
      loading.value = false
    }
  }

  const createLocation = async (locationData: CreateLocationRequest): Promise<Location> => {
    try {
      loading.value = true
      error.value = null
      
      const newLocation = await locationsApi.createLocation(locationData)
      locations.value.push(newLocation)
      
      // Re-sort locations
      locations.value.sort((a, b) => {
        if (a.sortOrder !== null && b.sortOrder !== null) {
          return a.sortOrder - b.sortOrder
        }
        return a.name.localeCompare(b.name)
      })
      
      return newLocation
    } catch (err: any) {
      error.value = err.response?.data?.message || err.message || 'Failed to create location'
      console.error('Error creating location:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateLocation = async (id: number, locationData: Partial<CreateLocationRequest>): Promise<Location> => {
    try {
      loading.value = true
      error.value = null
      
      const updatedLocation = await locationsApi.updateLocation(id, locationData)
      
      const index = locations.value.findIndex(l => l.id === id)
      if (index !== -1) {
        locations.value[index] = updatedLocation
      }
      
      return updatedLocation
    } catch (err: any) {
      error.value = err.response?.data?.message || err.message || 'Failed to update location'
      console.error('Error updating location:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const deleteLocation = async (id: number): Promise<void> => {
    try {
      loading.value = true
      error.value = null
      
      await locationsApi.deleteLocation(id)
      locations.value = locations.value.filter(l => l.id !== id)
    } catch (err: any) {
      error.value = err.response?.data?.message || err.message || 'Failed to delete location'
      console.error('Error deleting location:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const clearError = () => {
    error.value = null
  }

  return {
    // State
    locations,
    loading,
    error,
    
    // Getters
    locationsOptions,
    locationsBySection,
    getLocationById,
    
    // Actions
    fetchLocations,
    createLocation,
    updateLocation,
    deleteLocation,
    clearError
  }
})