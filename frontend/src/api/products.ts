import { apiClient } from './client'

export interface Product {
  id: number
  name: string
  quantity: number
  unit: string
  frozenDate: string
  expirationDate: string | null
  notes: string | null
  barcode: string | null
  imageUrl: string | null
  userId: number | null
  category: {
    id: number
    name: string
    color: string | null
    description: string | null
    defaultStorageDays: number | null
  }
  location: {
    id: number
    name: string
    description: string | null
    freezerSection: string | null
    sortOrder: number | null
  }
  createdAt: string
  updatedAt: string
  // Additional computed fields from backend
  daysUntilExpiration?: number
  expired?: boolean
  expiringSoon?: boolean
}

export interface CreateProductRequest {
  name: string
  categoryId: number
  locationId: number
  quantity: number
  unit: string
  frozenDate?: string
  expirationDate?: string
  notes?: string
  barcode?: string
  imageUrl?: string
}

export interface ProductFilters {
  search?: string
  categoryId?: number
  locationId?: number
  status?: 'all' | 'expiring' | 'expired' | 'fresh'
  page?: number
  size?: number
  sort?: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

// Get products with optional filters
export const getProducts = async (filters: ProductFilters = {}): Promise<PageResponse<Product>> => {
  const params = new URLSearchParams()
  
  if (filters.search) params.append('search', filters.search)
  if (filters.categoryId) params.append('categoryId', filters.categoryId.toString())
  if (filters.locationId) params.append('locationId', filters.locationId.toString())
  if (filters.status && filters.status !== 'all') params.append('status', filters.status)
  if (filters.page !== undefined) params.append('page', filters.page.toString())
  if (filters.size !== undefined) params.append('size', filters.size.toString())
  if (filters.sort) params.append('sort', filters.sort)

  const response = await apiClient.get(`/products?${params.toString()}`)
  return response.data
}

// Get single product by ID
export const getProduct = async (id: number): Promise<Product> => {
  const response = await apiClient.get(`/products/${id}`)
  return response.data
}

// Create new product
export const createProduct = async (productData: CreateProductRequest): Promise<Product> => {
  const response = await apiClient.post('/products', productData)
  return response.data
}

// Update existing product
export const updateProduct = async (id: number, productData: Partial<CreateProductRequest>): Promise<Product> => {
  const response = await apiClient.put(`/products/${id}`, productData)
  return response.data
}

// Delete product
export const deleteProduct = async (id: number): Promise<void> => {
  await apiClient.delete(`/products/${id}`)
}

// Get expiring products (convenience method)
export const getExpiringProducts = async (days: number = 7): Promise<Product[]> => {
  const response = await apiClient.get(`/products/expiring?days=${days}`)
  return response.data
}