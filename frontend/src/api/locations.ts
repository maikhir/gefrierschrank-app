import { apiClient } from './client'

export interface Location {
  id: number
  name: string
  description: string | null
  freezerSection: string | null
  sortOrder: number | null
  createdAt: string
  updatedAt: string
}

export interface CreateLocationRequest {
  name: string
  description?: string
  freezerSection?: string
  sortOrder?: number
}

// Get all locations
export const getLocations = async (): Promise<Location[]> => {
  const response = await apiClient.get('/locations')
  return response.data
}

// Get single location by ID
export const getLocation = async (id: number): Promise<Location> => {
  const response = await apiClient.get(`/locations/${id}`)
  return response.data
}

// Create new location
export const createLocation = async (locationData: CreateLocationRequest): Promise<Location> => {
  const response = await apiClient.post('/locations', locationData)
  return response.data
}

// Update existing location
export const updateLocation = async (id: number, locationData: Partial<CreateLocationRequest>): Promise<Location> => {
  const response = await apiClient.put(`/locations/${id}`, locationData)
  return response.data
}

// Delete location
export const deleteLocation = async (id: number): Promise<void> => {
  await apiClient.delete(`/locations/${id}`)
}