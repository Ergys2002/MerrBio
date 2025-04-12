import { defineStore } from 'pinia';

interface Farmer {
  id: number;
  name: string;
  location: string;
}

export interface Product {
  id: number;
  name: string;
  description: string;
  image: string;
  price: number;
  unit: string;
  category: string;
  organic: boolean;
  farmer: Farmer;
}

interface FilterOptions {
  search: string;
  category: string;
  minPrice: number | null;
  maxPrice: number | null;
  organic: boolean | null;
}

interface ProductState {
  products: Product[];
  loading: boolean;
  error: string | null;
  filters: FilterOptions;
  currentPage: number;
  totalPages: number;
  productsPerPage: number;
}

export const useProductStore = defineStore('products', {
  state: (): ProductState => ({
    products: [],
    loading: false,
    error: null,
    filters: {
      search: '',
      category: '',
      minPrice: null,
      maxPrice: null,
      organic: null,
    },
    currentPage: 1,
    totalPages: 1,
    productsPerPage: 9,
  }),
  getters: {
    /**
     * Get products filtered by current filter settings
     */
    filteredProducts: (state): Product[] => {
      return state.products.filter(product => {
        // Search filter
        if (state.filters.search && 
            !product.name.toLowerCase().includes(state.filters.search.toLowerCase()) &&
            !product.description.toLowerCase().includes(state.filters.search.toLowerCase())) {
          return false;
        }

        // Category filter
        if (state.filters.category && product.category !== state.filters.category) {
          return false;
        }

        // Price range filter - min price
        if (state.filters.minPrice !== null && product.price < state.filters.minPrice) {
          return false;
        }

        // Price range filter - max price
        if (state.filters.maxPrice !== null && product.price > state.filters.maxPrice) {
          return false;
        }

        // Organic filter
        if (state.filters.organic !== null && product.organic !== state.filters.organic) {
          return false;
        }

        return true;
      });
    },

    /**
     * Get paginated products based on current page and filters
     */
    paginatedProducts: (state): Product[] => {
      // Using self-reference pattern to avoid circular reference issues
      const self = useProductStore();
      const filtered = self.filteredProducts;
      const start = (state.currentPage - 1) * state.productsPerPage;
      const end = start + state.productsPerPage;
      return filtered.slice(start, end);
    },

    /**
     * Calculate total pages based on filtered products and products per page
     */
    calculatedTotalPages: (): number => {
      // Using self-reference pattern to avoid circular reference issues  
      const self = useProductStore();
      const filtered = self.filteredProducts;
      return Math.ceil(filtered.length / self.productsPerPage);
    },
  },

  actions: {
    /**
     * Fetch products from API
     * For now, this uses mock data
     */
    async fetchProducts() {
      this.loading = true;
      this.error = null;
      
      try {
        // Simulate API call with setTimeout
        await new Promise(resolve => setTimeout(resolve, 800));
        
        // Mock data - in real app, this would come from API
        this.products = [
          {
            id: 1,
            name: "Fresh Organic Tomatoes",
            description: "Locally grown organic tomatoes, perfect for salads and cooking.",
            image: "https://images.unsplash.com/photo-1592924357229-b2ab2fedcf04?ixlib=rb-4.0.2&auto=format&fit=crop&q=80",
            price: 3.99,
            unit: "kg",
            category: "vegetables",
            organic: true,
            farmer: {
              id: 101,
              name: "Green Valley Farm",
              location: "Tirana"
            }
          },
          {
            id: 2,
            name: "Free Range Eggs",
            description: "Farm fresh free-range eggs from happy chickens.",
            image: "https://images.unsplash.com/photo-1569127959161-2b1297b2d9a5?ixlib=rb-4.0.2&auto=format&fit=crop&q=80",
            price: 4.50,
            unit: "dozen",
            category: "eggs",
            organic: true,
            farmer: {
              id: 102,
              name: "Happy Hen Farm",
              location: "Durres"
            }
          },
          {
            id: 3,
            name: "Organic Lettuce",
            description: "Crisp and fresh organic lettuce harvested daily.",
            image: "https://images.unsplash.com/photo-1622205313162-be1d5712a43f?ixlib=rb-4.0.2&auto=format&fit=crop&q=80",
            price: 2.25,
            unit: "head",
            category: "vegetables",
            organic: true,
            farmer: {
              id: 103,
              name: "Sunshine Greens",
              location: "Tirana"
            }
          },
          {
            id: 4,
            name: "Mountain Honey",
            description: "Pure, natural honey harvested from mountain flowers.",
            image: "https://images.unsplash.com/photo-1587049352851-8d4e89133924?ixlib=rb-4.0.2&auto=format&fit=crop&q=80",
            price: 12.99,
            unit: "jar",
            category: "honey",
            organic: true,
            farmer: {
              id: 104,
              name: "Bee Haven Apiary",
              location: "Vlorë"
            }
          },
          {
            id: 5,
            name: "Fresh Strawberries",
            description: "Sweet and juicy strawberries, perfect for desserts.",
            image: "https://images.unsplash.com/photo-1518635017480-87f5c963de4f?ixlib=rb-4.0.2&auto=format&fit=crop&q=80",
            price: 5.99,
            unit: "basket",
            category: "fruits",
            organic: false,
            farmer: {
              id: 105,
              name: "Berry Good Farm",
              location: "Shkodër"
            }
          },
          {
            id: 6,
            name: "Grass-Fed Beef",
            description: "Premium cuts from grass-fed, pasture-raised cattle.",
            image: "https://images.unsplash.com/photo-1607623814075-e51df1bdc82f?ixlib=rb-4.0.2&auto=format&fit=crop&q=80",
            price: 22.50,
            unit: "kg",
            category: "meat",
            organic: false,
            farmer: {
              id: 106,
              name: "Green Pastures Ranch",
              location: "Elbasan"
            }
          },
          {
            id: 7,
            name: "Fresh Milk",
            description: "Creamy, non-homogenized milk from grass-fed cows.",
            image: "https://images.unsplash.com/photo-1563636619-e9143da7973b?ixlib=rb-4.0.2&auto=format&fit=crop&q=80",
            price: 2.75,
            unit: "liter",
            category: "dairy",
            organic: true,
            farmer: {
              id: 107,
              name: "Happy Cow Dairy",
              location: "Tirana"
            }
          },
          {
            id: 8,
            name: "Fresh Basil",
            description: "Aromatic fresh basil, perfect for Italian dishes.",
            image: "https://images.unsplash.com/photo-1527792492728-08d07d011113?ixlib=rb-4.0.2&auto=format&fit=crop&q=80",
            price: 1.99,
            unit: "bunch",
            category: "herbs",
            organic: true,
            farmer: {
              id: 108,
              name: "Herbal Haven",
              location: "Korçë"
            }
          },
          {
            id: 9,
            name: "Organic Carrots",
            description: "Sweet and crunchy organic carrots.",
            image: "https://images.unsplash.com/photo-1447175008436-054170c2e979?ixlib=rb-4.0.2&auto=format&fit=crop&q=80",
            price: 2.49,
            unit: "kg",
            category: "vegetables",
            organic: true,
            farmer: {
              id: 109,
              name: "Root Vegetable Farm",
              location: "Fier"
            }
          },
          {
            id: 10,
            name: "Fresh Apples",
            description: "Crisp and juicy apples picked at peak ripeness.",
            image: "https://images.unsplash.com/photo-1570913149827-d2ac84ab3f9a?ixlib=rb-4.0.2&auto=format&fit=crop&q=80",
            price: 3.49,
            unit: "kg",
            category: "fruits",
            organic: false,
            farmer: {
              id: 110,
              name: "Orchard Hills",
              location: "Berat"
            }
          },
          {
            id: 11,
            name: "Artisanal Cheese",
            description: "Handcrafted cheese made with traditional methods.",
            image: "https://images.unsplash.com/photo-1452195100486-9cc805987862?ixlib=rb-4.0.2&auto=format&fit=crop&q=80",
            price: 8.99,
            unit: "piece",
            category: "dairy",
            organic: false,
            farmer: {
              id: 111,
              name: "Mountain Creamery",
              location: "Gjirokastër"
            }
          },
          {
            id: 12,
            name: "Fresh Cucumbers",
            description: "Cool and refreshing cucumbers, perfect for salads.",
            image: "https://images.unsplash.com/photo-1602491674275-316d95591c51?ixlib=rb-4.0.2&auto=format&fit=crop&q=80",
            price: 2.29,
            unit: "kg",
            category: "vegetables",
            organic: false,
            farmer: {
              id: 112,
              name: "Fresh Greens Farm",
              location: "Tirana"
            }
          },
          {
            id: 13,
            name: "Fresh Thyme",
            description: "Aromatic thyme, freshly harvested.",
            image: "https://images.unsplash.com/photo-1619728027007-f13887ec40eb?ixlib=rb-4.0.2&auto=format&fit=crop&q=80",
            price: 1.79,
            unit: "bunch",
            category: "herbs",
            organic: true,
            farmer: {
              id: 113,
              name: "Herb Garden",
              location: "Sarandë"
            }
          },
          {
            id: 14,
            name: "Organic Potatoes",
            description: "Versatile organic potatoes for all your cooking needs.",
            image: "https://images.unsplash.com/photo-1518977676601-b53f82aba655?ixlib=rb-4.0.2&auto=format&fit=crop&q=80",
            price: 2.99,
            unit: "kg",
            category: "vegetables",
            organic: true,
            farmer: {
              id: 114,
              name: "Earth's Bounty Farm",
              location: "Lushnjë"
            }
          },
          {
            id: 15,
            name: "Free Range Chicken",
            description: "Pasture-raised chicken with no antibiotics.",
            image: "https://images.unsplash.com/photo-1587593810167-a84920ea0781?ixlib=rb-4.0.2&auto=format&fit=crop&q=80",
            price: 15.99,
            unit: "whole",
            category: "meat",
            organic: false,
            farmer: {
              id: 115,
              name: "Happy Birds Farm",
              location: "Pogradec"
            }
          }
        ];

        // Update total pages
        this.totalPages = this.calculatedTotalPages;
        
      } catch (error) {
        this.error = 'Failed to fetch products. Please try again later.';
        console.error('Error fetching products:', error);
      } finally {
        this.loading = false;
      }
    },

    /**
     * Update filters
     */
    updateFilters(newFilters: FilterOptions) {
      this.filters = { ...newFilters };
      this.currentPage = 1; // Reset to first page when filters change
      this.totalPages = this.calculatedTotalPages;
    },

    /**
     * Set current page
     */
    setPage(page: number) {
      if (page < 1) page = 1;
      if (page > this.totalPages) page = this.totalPages;
      this.currentPage = page;
    },

    /**
     * Next page
     */
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },

    /**
     * Previous page
     */
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    }
  }
});
