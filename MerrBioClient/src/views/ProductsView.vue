<script setup lang="ts">
/**
 * ProductsView displays the products page with search, filters, and product listings
 * @component
 */
import { onMounted, ref, computed } from 'vue';
import { useProductStore } from '../stores/productStore';

import ProductSearch from '../components/products/ProductSearch.vue';
import ProductList from '../components/products/ProductList.vue';

// Initialize the product store
const productStore = useProductStore();

// Get computed properties from the store
const loading = computed(() => productStore.loading);
const products = computed(() => productStore.paginatedProducts);
const totalPages = computed(() => productStore.totalPages);
const currentPage = computed(() => productStore.currentPage);
const filteredProductsCount = computed(() => productStore.filteredProducts.length);

// Fetch products on component mount
onMounted(async () => {
  await productStore.fetchProducts();
});

// Handle filter updates
const updateFilters = (filters) => {
  productStore.updateFilters(filters);
};

// Pagination methods
const nextPage = () => productStore.nextPage();
const prevPage = () => productStore.prevPage();
const goToPage = (page: number) => productStore.setPage(page);

// Generate array of page numbers for pagination
const paginationPages = computed(() => {
  const pages = [];
  const maxPagesToShow = 5;
  
  if (totalPages.value <= maxPagesToShow) {
    // If we have less than or equal to maxPagesToShow pages, show them all
    for (let i = 1; i <= totalPages.value; i++) {
      pages.push(i);
    }
  } else {
    // Otherwise show a window of pages around current page
    const halfWindow = Math.floor(maxPagesToShow / 2);
    let startPage = currentPage.value - halfWindow;
    let endPage = currentPage.value + halfWindow;
    
    // Adjust if window is outside of valid range
    if (startPage < 1) {
      endPage += (1 - startPage);
      startPage = 1;
    }
      if (endPage > totalPages.value) {
      startPage -= (endPage - totalPages.value);
      endPage = totalPages.value;
    }
    
    // Ensure startPage is never less than 1
    startPage = Math.max(1, startPage);
    
    for (let i = startPage; i <= Math.min(endPage, totalPages.value); i++) {
      pages.push(i);
    }
    
    // Add first page indicator if needed
    if (startPage > 1) {
      pages.unshift(1);
      if (startPage > 2) pages.splice(1, 0, '...');
    }
    
    // Add last page indicator if needed
    if (endPage < totalPages.value) {
      if (endPage < totalPages.value - 1) pages.push('...');
      pages.push(totalPages.value);
    }
  }
  
  return pages;
});
</script>

<template>
  <div class="products-view">
    <div class="container">
      <!-- Page header -->
      <div class="page-header">
        <h1>Browse Fresh Products</h1>
        <p>Discover fresh and local products directly from farmers near you</p>
      </div>
      
      <div class="products-content">
        <!-- Sidebar with filters -->
        <div class="sidebar">
          <ProductSearch @update:filters="updateFilters" />
        </div>
        
        <!-- Main content area -->
        <div class="main-content">
          <!-- Results summary -->
          <div v-if="!loading" class="results-summary">
            <p>
              <span v-if="filteredProductsCount === 1">
                1 product found
              </span>
              <span v-else>
                {{ filteredProductsCount }} products found
              </span>
            </p>
          </div>
          
          <!-- Product listing -->
          <ProductList :products="products" :loading="loading" />
          
          <!-- Pagination -->
          <div v-if="totalPages > 1 && !loading" class="pagination">
            <button 
              class="pagination-button" 
              @click="prevPage" 
              :disabled="currentPage === 1"
            >
              &laquo; Prev
            </button>
            
            <div class="page-numbers">
              <button
                v-for="page in paginationPages"
                :key="page"
                :class="[
                  'page-number', 
                  page === currentPage ? 'active' : '',
                  page === '...' ? 'ellipsis' : ''
                ]"
                @click="page !== '...' && goToPage(Number(page))"
                :disabled="page === '...'"
              >
                {{ page }}
              </button>
            </div>
            
            <button 
              class="pagination-button" 
              @click="nextPage" 
              :disabled="currentPage === totalPages"
            >
              Next &raquo;
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.products-view {
  padding: 40px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  margin-bottom: 32px;
  text-align: center;
}

.page-header h1 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
  font-size: 1.1rem;
}

.products-content {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}

.results-summary {
  margin-bottom: 16px;
  font-size: 0.95rem;
  color: #666;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 32px;
  gap: 8px;
}

.pagination-button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background-color: #fff;
  border-radius: 4px;
  cursor: pointer;
  color: #333;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.pagination-button:hover:not(:disabled) {
  background-color: #f5f5f5;
  border-color: #ccc;
}

.pagination-button:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 4px;
}

.page-number {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #ddd;
  background-color: #fff;
  border-radius: 4px;
  cursor: pointer;
  color: #333;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.page-number:hover:not(:disabled):not(.active) {
  background-color: #f5f5f5;
  border-color: #ccc;
}

.page-number.active {
  background-color: #4caf50;
  color: white;
  border-color: #4caf50;
  cursor: default;
}

.page-number.ellipsis {
  border: none;
  cursor: default;
}

.page-number:disabled {
  cursor: default;
}

@media (min-width: 768px) {
  .products-content {
    grid-template-columns: 280px 1fr;
  }
}

@media (max-width: 767px) {
  .sidebar {
    order: 1;
  }
  
  .main-content {
    order: 2;
  }
}
</style>
