import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Product, CreateProductRequest, ProductFilters, PageResponse } from '@/api/products'
import * as productsApi from '@/api/products'

export const useProductsStore = defineStore('products', () => {
  // State
  const products = ref<Product[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const pagination = ref({
    totalElements: 0,
    totalPages: 0,
    size: 20,
    number: 0,
    first: true,
    last: true
  })

  // Getters
  const expiringProducts = computed(() => {
    const now = new Date()
    const sevenDaysFromNow = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000)
    
    return products.value.filter(product => {
      if (!product.expirationDate) return false
      const expirationDate = new Date(product.expirationDate)
      return expirationDate <= sevenDaysFromNow && expirationDate > now
    })
  })

  const expiredProducts = computed(() => {
    const now = new Date()
    
    return products.value.filter(product => {
      if (!product.expirationDate) return false
      const expirationDate = new Date(product.expirationDate)
      return expirationDate <= now
    })
  })

  const productsByCategory = computed(() => {
    return products.value.reduce((acc, product) => {
      const categoryName = product.category.name
      if (!acc[categoryName]) {
        acc[categoryName] = []
      }
      acc[categoryName].push(product)
      return acc
    }, {} as Record<string, Product[]>)
  })

  const totalProducts = computed(() => products.value.length)

  // Actions
  const fetchProducts = async (filters: ProductFilters = {}) => {
    try {
      loading.value = true
      error.value = null
      
      const response: PageResponse<Product> = await productsApi.getProducts(filters)
      
      products.value = response.content
      pagination.value = {
        totalElements: response.totalElements,
        totalPages: response.totalPages,
        size: response.size,
        number: response.number,
        first: response.first,
        last: response.last
      }
    } catch (err: any) {
      error.value = err.response?.data?.message || err.message || 'Failed to fetch products'
      console.error('Error fetching products:', err)
    } finally {
      loading.value = false
    }
  }

  const createProduct = async (productData: CreateProductRequest): Promise<Product> => {
    try {
      loading.value = true
      error.value = null
      
      const newProduct = await productsApi.createProduct(productData)
      products.value.unshift(newProduct)
      pagination.value.totalElements += 1
      
      return newProduct
    } catch (err: any) {
      error.value = err.response?.data?.message || err.message || 'Failed to create product'
      console.error('Error creating product:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateProduct = async (id: number, productData: Partial<CreateProductRequest>): Promise<Product> => {
    try {
      loading.value = true
      error.value = null
      
      const updatedProduct = await productsApi.updateProduct(id, productData)
      
      const index = products.value.findIndex(p => p.id === id)
      if (index !== -1) {
        products.value[index] = updatedProduct
      }
      
      return updatedProduct
    } catch (err: any) {
      error.value = err.response?.data?.message || err.message || 'Failed to update product'
      console.error('Error updating product:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const deleteProduct = async (id: number): Promise<void> => {
    try {
      loading.value = true
      error.value = null
      
      await productsApi.deleteProduct(id)
      
      products.value = products.value.filter(p => p.id !== id)
      pagination.value.totalElements -= 1
    } catch (err: any) {
      error.value = err.response?.data?.message || err.message || 'Failed to delete product'
      console.error('Error deleting product:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const getProductById = (id: number): Product | undefined => {
    return products.value.find(p => p.id === id)
  }

  const clearError = () => {
    error.value = null
  }

  return {
    // State
    products,
    loading,
    error,
    pagination,
    
    // Getters
    expiringProducts,
    expiredProducts,
    productsByCategory,
    totalProducts,
    
    // Actions
    fetchProducts,
    createProduct,
    updateProduct,
    deleteProduct,
    getProductById,
    clearError
  }
})