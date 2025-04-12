<script setup lang="ts">
/**
 * ProductList displays a grid of product cards with loading and empty states
 * @component
 */
import { defineProps } from 'vue'
import ProductCard from './ProductCard.vue'
import type { Product } from '@/stores/productStore'

interface Props {
  products: Product[]
  loading: boolean
  error: string | null
}

const props = defineProps<Props>()
</script>

<template>
  <div class="product-list">
    <!-- Loading State -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Loading products...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
      <button class="btn-retry" @click="$emit('retry')">Try Again</button>
    </div>

    <!-- Empty State -->
    <div v-else-if="!products.length" class="empty-state">
      <div class="empty-icon">🌱</div>
      <h3>No Products Found</h3>
      <p>Try adjusting your filters or search terms</p>
    </div>

    <!-- Products Grid -->
    <div v-else class="products-grid">
      <ProductCard
        v-for="product in products"
        :key="product.id"
        :product="product"
      />
    </div>
  </div>
</template>

<style scoped>
.product-list {
  width: 100%;
  min-height: 400px;
}

.products-grid {
  display: grid;
  gap: 24px;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
}

/* Loading State */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: #666;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(76, 175, 80, 0.1);
  border-radius: 50%;
  border-top-color: #4caf50;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Error State */
.error-state {
  text-align: center;
  padding: 60px 0;
  color: #f44336;
}

.btn-retry {
  margin-top: 16px;
  padding: 8px 24px;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-retry:hover {
  background-color: #d32f2f;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #666;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 1.25rem;
  margin-bottom: 8px;
  color: #333;
}

.empty-state p {
  color: #666;
}

/* Responsive adjustments */
@media (max-width: 640px) {
  .products-grid {
    gap: 16px;
    grid-template-columns: 1fr;
  }
}

@media (min-width: 641px) and (max-width: 1024px) {
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
}
</style>
