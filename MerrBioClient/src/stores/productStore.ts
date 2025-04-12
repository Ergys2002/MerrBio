import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// Product categories available in the system
export const productCategories = [
  'Vegetables',
  'Fruits',
  'Dairy & Eggs',
  'Meat',
  'Grains',
  'Herbs',
  'Honey',
  'Natural',
  'Organic'
]

export interface Product {
  id: string
  name: string
  description: string
  price: number
  unit: string
  minOrderQuantity: number
  categories: string[]
  imageUrls: string[]
  thumbnailUrl: string
  farmerId: string
  farmerName: string
  farmLocation: string
  isOrganic: boolean
  isInStock: boolean
  quantity: number
  createdAt: string
  updatedAt: string
}

export interface FilterOptions {
  search: string
  categories: string[]
  minPrice: number | null
  maxPrice: number | null
  isOrganic: boolean | null
  sortBy: 'price-asc' | 'price-desc' | 'name-asc' | 'name-desc' | 'latest'
}

// Mock data for development
const mockProducts: Product[] = [
  {
    id: '550e8400-e29b-41d4-a716-446655440000',
    name: 'Organic Fresh Tomatoes',
    description: 'Locally grown organic tomatoes, perfect for salads and cooking. Our tomatoes are grown without pesticides and picked at peak ripeness.',
    price: 3.99,
    unit: 'kg',
    minOrderQuantity: 2,
    categories: ['Vegetables', 'Organic'],
    imageUrls: [
      'https://images.unsplash.com/photo-1592924357229-b2ab2fedcf04?ixlib=rb-4.0.2&auto=format&fit=crop&q=80',
      'https://images.unsplash.com/photo-1546094324-7fd2718befe3?ixlib=rb-4.0.2&auto=format&fit=crop&q=80'
    ],
    thumbnailUrl: 'https://images.unsplash.com/photo-1592924357229-b2ab2fedcf04?ixlib=rb-4.0.2&auto=format&fit=crop&w=300&q=80',
    farmerId: '7c0e5a5d-1234-4321-a123-123456789abc',
    farmerName: 'Green Valley Farm',
    farmLocation: 'Tirana, Albania',
    isOrganic: true,
    isInStock: true,
    quantity: 100,
    createdAt: '2025-04-01T10:00:00Z',
    updatedAt: '2025-04-01T10:00:00Z'
  },
  {
    id: '550e8400-e29b-41d4-a716-446655440001',
    name: 'Farm Fresh Eggs',
    description: 'Free-range eggs from happy chickens. Our hens are fed organic feed and have plenty of space to roam.',
    price: 4.50,
    unit: 'dozen',
    minOrderQuantity: 1,
    categories: ['Eggs', 'Organic'],
    imageUrls: [
      'https://images.unsplash.com/photo-1569127959161-2b1297b2d9a5?ixlib=rb-4.0.2&auto=format&fit=crop&q=80'
    ],
    thumbnailUrl: 'https://images.unsplash.com/photo-1569127959161-2b1297b2d9a5?ixlib=rb-4.0.2&auto=format&fit=crop&w=300&q=80',
    farmerId: '7c0e5a5d-1234-4321-a123-123456789abd',
    farmerName: 'Happy Hen Farm',
    farmLocation: 'Durrës, Albania',
    isOrganic: true,
    isInStock: true,
    quantity: 50,
    createdAt: '2025-04-02T09:00:00Z',
    updatedAt: '2025-04-02T09:00:00Z'
  },
  {
    id: '550e8400-e29b-41d4-a716-446655440002',
    name: 'Fresh Mountain Honey',
    description: 'Pure, raw honey collected from mountain flowers. Unprocessed and naturally sweet.',
    price: 12.99,
    unit: 'jar',
    minOrderQuantity: 1,
    categories: ['Honey', 'Natural'],
    imageUrls: [
      'https://images.unsplash.com/photo-1587049352846-4a222e784d38?ixlib=rb-4.0.2&auto=format&fit=crop&q=80'
    ],
    thumbnailUrl: 'https://images.unsplash.com/photo-1587049352846-4a222e784d38?ixlib=rb-4.0.2&auto=format&fit=crop&w=300&q=80',
    farmerId: '7c0e5a5d-1234-4321-a123-123456789abe',
    farmerName: 'Mountain Bee Farm',
    farmLocation: 'Vlorë, Albania',
    isOrganic: true,
    isInStock: false,
    quantity: 0,
    createdAt: '2025-04-03T11:00:00Z',
    updatedAt: '2025-04-03T11:00:00Z'
  }
]

export const useProductStore = defineStore('product', () => {
  // State
  const products = ref<Product[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const filters = ref<FilterOptions>({
    search: '',
    categories: [],
    minPrice: null,
    maxPrice: null,
    isOrganic: null,
    sortBy: 'latest'
  })
  const currentPage = ref(1)
  const itemsPerPage = ref(12)

  // Getters
  const filteredProducts = computed(() => {
    let result = [...products.value]

    // Apply search filter
    if (filters.value.search) {
      const searchLower = filters.value.search.toLowerCase()
      result = result.filter(product => 
        product.name.toLowerCase().includes(searchLower) ||
        product.description.toLowerCase().includes(searchLower) ||
        product.farmerName.toLowerCase().includes(searchLower)
      )
    }

    // Apply category filter
    if (filters.value.categories.length > 0) {
      result = result.filter(product =>
        product.categories.some(cat => filters.value.categories.includes(cat))
      )
    }

    // Apply price range filter
    if (filters.value.minPrice !== null) {
      result = result.filter(product => product.price >= filters.value.minPrice!)
    }
    if (filters.value.maxPrice !== null) {
      result = result.filter(product => product.price <= filters.value.maxPrice!)
    }

    // Apply organic filter
    if (filters.value.isOrganic !== null) {
      result = result.filter(product => product.isOrganic === filters.value.isOrganic)
    }

    // Apply sorting
    switch (filters.value.sortBy) {
      case 'price-asc':
        result.sort((a, b) => a.price - b.price)
        break
      case 'price-desc':
        result.sort((a, b) => b.price - a.price)
        break
      case 'name-asc':
        result.sort((a, b) => a.name.localeCompare(b.name))
        break
      case 'name-desc':
        result.sort((a, b) => b.name.localeCompare(a.name))
        break
      case 'latest':
        result.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
        break
    }

    return result
  })

  const totalPages = computed(() => 
    Math.ceil(filteredProducts.value.length / itemsPerPage.value)
  )

  const paginatedProducts = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value
    const end = start + itemsPerPage.value
    return filteredProducts.value.slice(start, end)
  })

  // Actions
  const fetchProducts = async () => {
    loading.value = true
    error.value = null
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 1000))
      products.value = mockProducts
    } catch (err) {
      console.error('Error fetching products:', err)
      error.value = 'Failed to load products'
    } finally {
      loading.value = false
    }
  }

  const fetchProductById = async (id: string): Promise<Product | null> => {
    loading.value = true
    error.value = null
    try {
      // Simulate API call
      await new Promise(resolve => setTimeout(resolve, 500))
      const product = mockProducts.find(p => p.id === id)
      return product || null
    } catch (err) {
      console.error('Error fetching product:', err)
      error.value = 'Failed to load product details'
      return null
    } finally {
      loading.value = false
    }
  }

  const updateFilters = (newFilters: Partial<FilterOptions>) => {
    filters.value = { ...filters.value, ...newFilters }
    currentPage.value = 1 // Reset to first page when filters change
  }

  const setPage = (page: number) => {
    currentPage.value = Math.max(1, Math.min(page, totalPages.value))
  }

  const nextPage = () => {
    if (currentPage.value < totalPages.value) {
      currentPage.value++
    }
  }

  const prevPage = () => {
    if (currentPage.value > 1) {
      currentPage.value--
    }
  }

  return {
    // State
    products,
    loading,
    error,
    filters,
    currentPage,
    itemsPerPage,
    // Getters
    filteredProducts,
    totalPages,
    paginatedProducts,
    // Actions
    fetchProducts,
    fetchProductById,
    updateFilters,
    setPage,
    nextPage,
    prevPage
  }
})
