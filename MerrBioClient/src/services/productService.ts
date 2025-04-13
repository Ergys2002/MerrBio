// Product interfaces from backend
import axios from 'axios';
import type { Product } from '@/stores/productStore';

// API base URL - pointing to the backend server, not the frontend
const API_URL = 'http://localhost:8080/api/v1';

// Interface for product search request (matching backend)
export interface ProductSearchRequest {
  farmerId?: string;
  categoryIds?: string[];
  isOrganic?: boolean;
  showOutOfStock?: boolean;
  minPrice?: number;
  maxPrice?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
}

/**
 * ProductService - Handles all API calls for products
 */
export class ProductService {  /**
   * Get products for a specific farmer
   * @param farmerId - The ID of the farmer
   * @param token - JWT access token
   * @returns Promise resolving to array of products
   */
  static async getFarmerProducts(farmerId: string, token: string): Promise<Product[]> {
    try {
      console.log(`Making request to: ${API_URL}/products/farmer/${farmerId}`);
      const response = await axios.get(`${API_URL}/products/farmer/${farmerId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      
      // Make sure we're always returning an array
      if (!response.data || !Array.isArray(response.data)) {
        console.warn('API did not return an array for products, returning empty array');
        return [];
      }
      
      return response.data;
    } catch (error: any) {
      console.error('Error fetching farmer products:', error);
      throw error.response?.data || { message: 'Failed to fetch products' };
    }
  }

  /**
   * Create a new product
   * @param productData - FormData object containing product data
   * @param token - JWT access token
   * @returns Promise resolving to created product
   */  static async createProduct(productData: FormData, token: string): Promise<Product> {
    try {
      const response = await axios.post(`${API_URL}/products`, productData, {
        headers: {
          Authorization: `Bearer ${token}`
          // Don't manually set Content-Type for multipart/form-data
          // Axios will set it automatically with the correct boundary
        }
      });
      return response.data;
    } catch (error: any) {
      console.error('Error creating product:', error);
      throw error.response?.data || { message: 'Failed to create product' };
    }
  }

  /**
   * Update an existing product
   * @param productId - ID of the product to update
   * @param productData - Updated product data
   * @param token - JWT access token
   * @returns Promise resolving to updated product
   */
  static async updateProduct(productId: string, productData: any, token: string): Promise<Product> {
    try {
      const response = await axios.put(`${API_URL}/products/${productId}`, productData, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      return response.data;
    } catch (error: any) {
      console.error('Error updating product:', error);
      throw error.response?.data || { message: 'Failed to update product' };
    }
  }

  /**
   * Delete a product
   * @param productId - ID of the product to delete
   * @param token - JWT access token
   * @returns Promise resolving to deletion success
   */
  static async deleteProduct(productId: string, token: string): Promise<boolean> {
    try {
      await axios.delete(`${API_URL}/products/${productId}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      return true;
    } catch (error: any) {
      console.error('Error deleting product:', error);
      throw error.response?.data || { message: 'Failed to delete product' };
    }
  }
  /**
   * Get all available product categories
   * @param token - JWT access token
   * @returns Promise resolving to array of categories
   */
  static async getCategories(token: string): Promise<Array<{id: string, name: string, description: string}>> {
    try {
      const response = await axios.get(`${API_URL}/categories`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      return response.data;
    } catch (error: any) {
      console.error('Error fetching categories:', error);
      throw error.response?.data || { message: 'Failed to fetch categories' };
    }
  }
  
  /**
   * Get all products
   * @param token - Optional JWT access token
   * @returns Promise resolving to array of products
   */
  static async getAllProducts(token?: string): Promise<Product[]> {
    try {
      const headers: Record<string, string> = {};
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
      
      const response = await axios.get(`${API_URL}/products`, { headers });
      return response.data;
    } catch (error: any) {
      console.error('Error fetching all products:', error);
      throw error.response?.data || { message: 'Failed to fetch products' };
    }
  }
  
  /**
   * Get a product by its ID
   * @param productId - ID of the product to fetch
   * @param token - Optional JWT access token
   * @returns Promise resolving to product
   */
  static async getProductById(productId: string, token?: string): Promise<Product> {
    try {
      const headers: Record<string, string> = {};
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
      
      const response = await axios.get(`${API_URL}/products/${productId}`, { headers });
      return response.data;
    } catch (error: any) {
      console.error('Error fetching product details:', error);
      throw error.response?.data || { message: 'Failed to fetch product details' };
    }
  }
  
  /**
   * Search products by keyword
   * @param keyword - Search term
   * @param token - Optional JWT access token
   * @returns Promise resolving to array of products
   */
  static async searchProducts(keyword: string, token?: string): Promise<Product[]> {
    try {
      const headers: Record<string, string> = {};
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
      
      const response = await axios.get(`${API_URL}/products/search?keyword=${encodeURIComponent(keyword)}`, { headers });
      return response.data;
    } catch (error: any) {
      console.error('Error searching products:', error);
      throw error.response?.data || { message: 'Failed to search products' };
    }
  }
  
  /**
   * Get products by category
   * @param categoryId - ID of the category
   * @param token - Optional JWT access token
   * @returns Promise resolving to array of products
   */
  static async getProductsByCategory(categoryId: string, token?: string): Promise<Product[]> {
    try {
      const headers: Record<string, string> = {};
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
      
      const response = await axios.get(`${API_URL}/products/category/${categoryId}`, { headers });
      return response.data;
    } catch (error: any) {
      console.error('Error fetching products by category:', error);
      throw error.response?.data || { message: 'Failed to fetch products by category' };
    }
  }    /**
   * Advanced product search with filtering, pagination, and sorting
   * @param searchParams - Search parameters object
   * @param query - Optional search keyword
   * @param token - Optional JWT access token
   * @returns Promise resolving to paginated product results
   */
  static async advancedSearch(searchParams: ProductSearchRequest, query?: string, token?: string): Promise<any> {
    try {
      const headers: Record<string, string> = {
        'Content-Type': 'application/json'
      };
      
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
      
      const url = query 
        ? `${API_URL}/products/advanced-search?query=${encodeURIComponent(query)}`
        : `${API_URL}/products/advanced-search`;
      
      // Construct the request body dynamically based on provided searchParams
      const requestBody: Partial<ProductSearchRequest> = {};

      // Always include size, use default if not provided
      requestBody.size = searchParams.size ?? 10; 

      // Include categoryIds only if it exists and is not empty
      if (searchParams.categoryIds && searchParams.categoryIds.length > 0) {
        requestBody.categoryIds = searchParams.categoryIds;
      }

      // Include other parameters only if they are explicitly provided (not null or undefined)
      if (searchParams.page !== undefined && searchParams.page !== null) requestBody.page = searchParams.page;
      if (searchParams.sortBy !== undefined && searchParams.sortBy !== null) requestBody.sortBy = searchParams.sortBy;
      if (searchParams.sortDirection !== undefined && searchParams.sortDirection !== null) requestBody.sortDirection = searchParams.sortDirection;
      if (searchParams.isOrganic !== undefined && searchParams.isOrganic !== null) requestBody.isOrganic = searchParams.isOrganic;
      if (searchParams.showOutOfStock !== undefined && searchParams.showOutOfStock !== null) requestBody.showOutOfStock = searchParams.showOutOfStock;
      if (searchParams.minPrice !== undefined && searchParams.minPrice !== null) requestBody.minPrice = searchParams.minPrice;
      if (searchParams.maxPrice !== undefined && searchParams.maxPrice !== null) requestBody.maxPrice = searchParams.maxPrice;
      if (searchParams.farmerId !== undefined && searchParams.farmerId !== null) requestBody.farmerId = searchParams.farmerId;
      
      const response = await axios.post(url, requestBody, { headers });
      
      return response.data;
    } catch (error: any) {
      console.error('Error in advanced product search:', error);
      throw error.response?.data || { message: 'Failed to perform advanced search' };
    }
  }
};
