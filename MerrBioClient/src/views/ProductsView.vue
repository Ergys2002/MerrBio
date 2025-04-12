<script setup lang="ts">
/**
 * ProductsView displays the products page with search, filters, and product listings
 * @component
 */
import { computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useProductStore } from '@/stores/productStore'
import type { FilterOptions, SortOption } from '@/stores/productStore'
import ProductSearch from '@/components/products/ProductSearch.vue'
import ProductList from '@/components/products/ProductList.vue'

// Initialize i18n
const { t } = useI18n()

// Initialize store
const productStore = useProductStore()

// Computed properties from store
const loading = computed(() => productStore.loading)
const error = computed(() => productStore.error)
const products = computed(() => productStore.paginatedProducts)
const totalPages = computed(() => productStore.totalPages)
const currentPage = computed(() => productStore.currentPage)
const filteredProductsCount = computed(() => productStore.filteredProducts.length)

// Page control methods
const goToPage = (page: number) => productStore.setPage(page)
const nextPage = () => productStore.nextPage()
const prevPage = () => productStore.prevPage()

// Filter and sort handlers
const handleFilterUpdate = (filters: FilterOptions) => {
  productStore.updateFilters(filters)
}

const handleSortUpdate = (sort: SortOption) => {
  productStore.updateSort(sort)
}

// Generate page numbers for pagination
const paginationPages = computed(() => {
  const pages: (number | string)[] = []
  const maxPagesToShow = 5
  
  if (totalPages.value <= maxPagesToShow) {
    for (let i = 1; i <= totalPages.value; i++) {
      pages.push(i)
    }
  } else {
    const halfWindow = Math.floor(maxPagesToShow / 2)
    let startPage = currentPage.value - halfWindow
    let endPage = currentPage.value + halfWindow
    
    if (startPage < 1) {
      endPage += (1 - startPage)
      startPage = 1
    }
    if (endPage > totalPages.value) {
      startPage -= (endPage - totalPages.value)
      endPage = totalPages.value
    }
    
    startPage = Math.max(1, startPage)
    
    if (startPage > 1) {
      pages.push(1)
      if (startPage > 2) pages.push('...')
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i)
    }
    
    if (endPage < totalPages.value) {
      if (endPage < totalPages.value - 1) pages.push('...')
      pages.push(totalPages.value)
    }
  }
  
  return pages
})

// Retry loading if there was an error
const retryLoading = () => {
  productStore.fetchProducts()
}

// Fetch products on mount
onMounted(() => {
  productStore.fetchProducts()
})
</script>

<template>
  <div class="products-view">
    <div class="container">
      <!-- Page Header -->
      <div class="page-header">
        <h1>{{ t('products.title') }}</h1>
        <p>{{ t('products.description') }}</p>
      </div>

      <!-- Main Content Grid -->
      <div class="content-grid">
        <!-- Sidebar with filters -->
        <aside class="sidebar">
          <ProductSearch
            @update:filters="handleFilterUpdate"
            @update:sort="handleSortUpdate"
          />
        </aside>

        <!-- Main content area -->
        <main class="main-content">
          <!-- Results Summary -->
          <div v-if="!loading" class="results-summary">
            <p v-if="filteredProductsCount === 0">
              {{ t('products.noResults') }}
            </p>
            <p v-else-if="filteredProductsCount === 1">
              {{ t('products.oneResult') }}
            </p>
            <p v-else>
              {{ filteredProductsCount }} {{ t('products.multipleResults') }}
            </p>
          </div>

          <!-- Product List -->
          <ProductList
            :products="products"
            :loading="loading"
            :error="error"
            @retry="retryLoading"
          />

          <!-- Pagination -->
          <div v-if="totalPages > 1 && !loading" class="pagination">
            <!-- Previous Page -->
            <button
              class="pagination-btn prev"
              :disabled="currentPage === 1"
              @click="prevPage"
            >
              ←
            </button>

            <!-- Page Numbers -->
            <div class="page-numbers">
              <button
                v-for="page in paginationPages"
                :key="page"
                class="page-number"
                :class="{
                  active: page === currentPage,
                  disabled: page === '...'
                }"
                @click="page !== '...' && goToPage(Number(page))"
                :disabled="page === '...'"
              >
                {{ page }}
              </button>
            </div>

            <!-- Next Page -->
            <button
              class="pagination-btn next"
              :disabled="currentPage === totalPages"
              @click="nextPage"
            >
              →
            </button>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<style scoped>
.products-view {
  padding: 40px 0;
  min-height: 100vh;
  background-color: #f9fafb;
}

.container {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h1 {
  font-size: 2.5rem;
  color: #1a1a1a;
  margin-bottom: 12px;
}

.page-header p {
  font-size: 1.1rem;
  color: #666;
  max-width: 600px;
  margin: 0 auto;
}

.content-grid {
  display: grid;
  gap: 32px;
  grid-template-columns: 300px 1fr;
  align-items: start;
}

.sidebar {
  position: sticky;
  top: 24px;
}

.results-summary {
  margin-bottom: 24px;
  color: #666;
}

.pagination {
  margin-top: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
}

.pagination-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: white;
  color: #666;
  font-size: 1.2rem;
  cursor: pointer;
  transition: all 0.2s;
}

.pagination-btn:hover:not(:disabled) {
  border-color: #4caf50;
  color: #4caf50;
}

.pagination-btn:disabled {
  background-color: #f5f5f5;
  color: #ccc;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 8px;
}

.page-number {
  min-width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: white;
  color: #666;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.page-number:hover:not(:disabled):not(.active) {
  border-color: #4caf50;
  color: #4caf50;
}

.page-number.active {
  background: #4caf50;
  border-color: #4caf50;
  color: white;
  cursor: default;
}

.page-number.disabled {
  border: none;
  cursor: default;
}

@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 260px 1fr;
    gap: 24px;
  }
}

@media (max-width: 768px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: static;
  }

  .page-header h1 {
    font-size: 2rem;
  }

  .pagination {
    flex-wrap: wrap;
  }
}
</style>
