import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Category, CreateCategoryRequest } from '@/api/categories'
import * as categoriesApi from '@/api/categories'

export const useCategoriesStore = defineStore('categories', () => {
  // State
  const categories = ref<Category[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const categoriesOptions = computed(() => 
    categories.value.map(category => ({
      value: category.id,
      label: category.name,
      color: category.color
    }))
  )

  const getCategoryById = computed(() => (id: number) => 
    categories.value.find(category => category.id === id)
  )

  // Actions
  const fetchCategories = async () => {
    try {
      loading.value = true
      error.value = null
      
      const response = await categoriesApi.getCategories()
      categories.value = response
    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to fetch categories'
      error.value = (err as { response?: { data?: { message?: string } } })?.response?.data?.message || errorMessage
      console.error('Error fetching categories:', err)
    } finally {
      loading.value = false
    }
  }

  const createCategory = async (categoryData: CreateCategoryRequest): Promise<Category> => {
    try {
      loading.value = true
      error.value = null
      
      const newCategory = await categoriesApi.createCategory(categoryData)
      categories.value.push(newCategory)
      
      return newCategory
    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to create category'
      error.value = (err as { response?: { data?: { message?: string } } })?.response?.data?.message || errorMessage
      console.error('Error creating category:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateCategory = async (id: number, categoryData: Partial<CreateCategoryRequest>): Promise<Category> => {
    try {
      loading.value = true
      error.value = null
      
      const updatedCategory = await categoriesApi.updateCategory(id, categoryData)
      
      const index = categories.value.findIndex(c => c.id === id)
      if (index !== -1) {
        categories.value[index] = updatedCategory
      }
      
      return updatedCategory
    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to update category'
      error.value = (err as { response?: { data?: { message?: string } } })?.response?.data?.message || errorMessage
      console.error('Error updating category:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const deleteCategory = async (id: number): Promise<void> => {
    try {
      loading.value = true
      error.value = null
      
      await categoriesApi.deleteCategory(id)
      categories.value = categories.value.filter(c => c.id !== id)
    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to delete category'
      error.value = (err as { response?: { data?: { message?: string } } })?.response?.data?.message || errorMessage
      console.error('Error deleting category:', err)
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
    categories,
    loading,
    error,
    
    // Getters
    categoriesOptions,
    getCategoryById,
    
    // Actions
    fetchCategories,
    createCategory,
    updateCategory,
    deleteCategory,
    clearError
  }
})