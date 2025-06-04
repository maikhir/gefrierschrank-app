import { apiClient } from './client'

export interface Category {
  id: number
  name: string
  description: string | null
  color: string | null
  defaultStorageDays: number | null
  icon: string | null
  createdAt: string
  updatedAt: string
}

export interface CreateCategoryRequest {
  name: string
  description?: string
  color?: string
  defaultStorageDays?: number
  icon?: string
}

// Get all categories
export const getCategories = async (): Promise<Category[]> => {
  const response = await apiClient.get('/categories')
  return response.data
}

// Get single category by ID
export const getCategory = async (id: number): Promise<Category> => {
  const response = await apiClient.get(`/categories/${id}`)
  return response.data
}

// Create new category
export const createCategory = async (categoryData: CreateCategoryRequest): Promise<Category> => {
  const response = await apiClient.post('/categories', categoryData)
  return response.data
}

// Update existing category
export const updateCategory = async (id: number, categoryData: Partial<CreateCategoryRequest>): Promise<Category> => {
  const response = await apiClient.put(`/categories/${id}`, categoryData)
  return response.data
}

// Delete category
export const deleteCategory = async (id: number): Promise<void> => {
  await apiClient.delete(`/categories/${id}`)
}