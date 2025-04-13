import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ProductService } from '@/services/productService'

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
  categories: Array<{id: string, name: string, description: string}> // Updated to match backend
  imageUrls: string[]
  thumbnailUrl: string
  farmerId: string
  farmerName: string
  farmLocation: string
  isOrganic: boolean
  isInStock: boolean
  minAvailableQuantity: number // Added to match backend
  maxAvailableQuantity: number // Added to match backend
  minimumOrderQuantity: number // Added to match backend
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

// Mock data for development and testing
export const mockProducts: Product[] = [
  // You can define some mock products here if needed
]

export const useProductStore = defineStore('product', () => {
  // State
  const products = ref<Product[]>([]);
  const categories = ref<any[]>([]);
  const isLoading = ref(false);
  const error = ref<string | null>(null);
  const filters = ref<FilterOptions>({
    search: '',
    categories: [],
    minPrice: null,
    maxPrice: null,
    isOrganic: null,
    sortBy: 'latest'
  });
  const currentPage = ref(1);
  const itemsPerPage = ref(12);
  
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
        product.categories.some((cat) => filters.value.categories.includes(cat.name))
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
  });

  const totalPages = computed(() => 
    Math.ceil(filteredProducts.value.length / itemsPerPage.value)
  );

  const paginatedProducts = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage.value
    const end = start + itemsPerPage.value
    return filteredProducts.value.slice(start, end)
  });
  /**
   * Fetch products for the current farmer
   */
  async function fetchFarmerProducts(farmerId: string, token: string) {
    isLoading.value = true;
    error.value = null;
    
    try {
      const fetchedProducts = await ProductService.getFarmerProducts(farmerId, token);
      // Ensure categories is always an array for each product
      const normalizedProducts = fetchedProducts.map(product => ({
        ...product,
        categories: product.categories || [] // Ensure categories is never null/undefined
      }));
      products.value = normalizedProducts;
    } catch (err: any) {
      console.error('Failed to fetch products:', err);
      error.value = err.message || 'Failed to fetch products';
      products.value = [];
    } finally {
      isLoading.value = false;
    }
  }
  
  /**
   * Fetch all products (for marketplace)
   */
  async function fetchProducts() {
    isLoading.value = true;
    error.value = null;
    
    try {
      // For now use mock data, replace with API call when ready
      await new Promise(resolve => setTimeout(resolve, 1000));
      products.value = mockProducts;
    } catch (err: any) {
      console.error('Error fetching products:', err);
      error.value = 'Failed to load products';
    } finally {
      isLoading.value = false;
    }
  }

  /**
   * Fetch product by ID
   */
  async function fetchProductById(id: string): Promise<Product | null> {
    isLoading.value = true;
    error.value = null;
    
    try {
      // For now use mock data, replace with API call when ready
      await new Promise(resolve => setTimeout(resolve, 500));
      const product = mockProducts.find(p => p.id === id);
      return product || null;
    } catch (err: any) {
      console.error('Error fetching product:', err);
      error.value = 'Failed to load product details';
      return null;
    } finally {
      isLoading.value = false;
    }
  }
  

  /**
   * Fetch all available product categories
   */
  async function fetchCategories(token: string) {
    isLoading.value = true;
    error.value = null;
    try {
      // Assuming ProductService has a getCategories method
      const fetchedCategories = await ProductService.getCategories(token); 
      categories.value = fetchedCategories;
    } catch (err: any) {
      console.error('Failed to fetch categories:', err);
      error.value = err.message || 'Failed to fetch categories';
      categories.value = []; // Reset or keep old categories? Resetting for now.
    } finally {
      isLoading.value = false;
    }
  }

  /**
   * Create a new product
   */
  async function createProduct(productData: FormData, token: string): Promise<Product | null> {
    isLoading.value = true;
    error.value = null;
    
    try {
      const newProduct = await ProductService.createProduct(productData, token);
      products.value.push(newProduct);
      return newProduct;
    } catch (err: any) {
      console.error('Failed to create product:', err);
      error.value = err.message || 'Failed to create product';
      return null;
    } finally {
      isLoading.value = false;
    }
  }
  
  /**
   * Update an existing product
   */
  async function updateProduct(productId: string, productData: any, token: string): Promise<Product | null> {
    isLoading.value = true;
    error.value = null;
    
    try {
      const updatedProduct = await ProductService.updateProduct(productId, productData, token);
      
      // Replace the old product with the updated one
      const index = products.value.findIndex(p => p.id === productId);
      if (index !== -1) {
        products.value[index] = updatedProduct;
      }
      
      return updatedProduct;
    } catch (err: any) {
      console.error('Failed to update product:', err);
      error.value = err.message || 'Failed to update product';
      return null;
    } finally {
      isLoading.value = false;
    }
  }
  
  /**
   * Delete a product
   */
  async function deleteProduct(productId: string, token: string): Promise<boolean> {
    isLoading.value = true;
    error.value = null;
    
    try {
      await ProductService.deleteProduct(productId, token);
      
      // Remove the deleted product from the local state
      products.value = products.value.filter(p => p.id !== productId);
      return true;
    } catch (err: any) {
      console.error('Failed to delete product:', err);
      error.value = err.message || 'Failed to delete product';
      return false;
    } finally {
      isLoading.value = false;
    }
  }
  
  /**
   * Update filters and reset pagination
   */
  function updateFilters(newFilters: Partial<FilterOptions>) {
    filters.value = { ...filters.value, ...newFilters };
    currentPage.value = 1; // Reset to first page when filters change
  }
  
  /**
   * Set current page with validation
   */
  function setPage(page: number) {
    currentPage.value = Math.max(1, Math.min(page, totalPages.value));
  }
  
  /**
   * Go to next page if possible
   */
  function nextPage() {
    if (currentPage.value < totalPages.value) {
      currentPage.value++;
    }
  }
  
  /**
   * Go to previous page if possible
   */
  function prevPage() {
    if (currentPage.value > 1) {
      currentPage.value--;
    }
  }
  
  /**
   * Clear all product-related errors
   */
  function clearErrors() {
    error.value = null;
  }
  
  /**
   * Reset the product store to its initial state
   */
  function $reset() {
    products.value = [];
    isLoading.value = false;
    error.value = null;
    filters.value = {
      search: '',
      categories: [],
      minPrice: null,
      maxPrice: null,
      isOrganic: null,
      sortBy: 'latest'
    };
    currentPage.value = 1;
  }
  
  return {
    // State
    products,
    categories,
    isLoading,
    error,
    filters,
    currentPage,
    itemsPerPage,
    
    // Getters
    filteredProducts,
    totalPages,
    paginatedProducts,
    
    // Actions
    fetchFarmerProducts,
    fetchProducts,
    fetchProductById,
    fetchCategories,
    createProduct,
    updateProduct,
    deleteProduct,
    updateFilters,
    setPage,
    nextPage,
    prevPage,
    clearErrors,
    $reset
  };
});
