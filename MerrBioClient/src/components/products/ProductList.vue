<script setup lang="ts">
/**
 * ProductList component displays a grid of product cards
 * @component
 */
import { defineProps, computed } from 'vue';
import ProductCard from './ProductCard.vue';

interface Farmer {
  id: number;
  name: string;
  location: string;
}

interface Product {
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

interface Props {
  products: Product[];
  loading: boolean;
}

const props = defineProps<Props>();

const hasProducts = computed(() => props.products && props.products.length > 0);
</script>

<template>
  <div class="product-list-container">
    <!-- Loading State -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Loading products...</p>
    </div>
    
    <!-- Empty State -->
    <div v-else-if="!hasProducts" class="empty-state">
      <p>No products found matching your criteria</p>
      <p class="suggestion">Try adjusting your filters or search terms</p>
    </div>
    
    <!-- Product Grid -->
    <div v-else class="products-grid">
      <ProductCard
        v-for="product in products"
        :key="product.id"
        v-bind="product"
      />
    </div>
  </div>
</template>

<style scoped>
.product-list-container {
  width: 100%;
}

.products-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
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
  border: 4px solid rgba(76, 175, 80, 0.2);
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

/* Empty State */
.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #666;
}

.empty-state p {
  margin: 0 0 8px 0;
  font-size: 1.1rem;
}

.suggestion {
  font-size: 0.9rem;
  color: #888;
}

/* Responsive Grid */
@media (min-width: 640px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
}

@media (min-width: 1024px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
  }
}
</style>
