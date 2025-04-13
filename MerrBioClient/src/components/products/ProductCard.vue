<script setup lang="ts">
/**
 * ProductCard displays individual product information in a card layout
 * @component
 */
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useRequestStore } from '@/stores/requestStore'
import type { Product } from '@/stores/productStore'

interface Props {
  product: Product
}

const props = defineProps<Props>()
const router = useRouter()
const requestStore = useRequestStore()

const hasActiveRequest = computed(() => requestStore.hasRequest(props.product.id))

const viewProductDetails = () => {
  router.push(`/products/${props.product.id}`)
}

const requestToBuy = () => {
  requestStore.addRequest(props.product.id, props.product.minOrderQuantity)
  // You could add a toast notification here in the future
}
// Computed property for the image URL, handling fallbacks
const displayImageUrl = computed(() => {
 const API_BASE_URL = 'http://localhost:8080/api/v1';
 
 // Use thumbnail if available
 if (props.product.thumbnailUrl) {
   // Check if the URL already contains the base URL
   return props.product.thumbnailUrl.includes('http') 
     ? props.product.thumbnailUrl 
     : `${API_BASE_URL}${props.product.thumbnailUrl}`;
 }
 // Fallback to the first image in imageUrls if available
 if (props.product.imageUrls && props.product.imageUrls.length > 0) {
   const imageUrl = props.product.imageUrls[0];
   return imageUrl.includes('http') 
     ? imageUrl 
     : `${API_BASE_URL}${imageUrl}`;
 }
 // Fallback to a placeholder if no images are available
 return '/images/placeholder-product.png';
});

const formattedPrice = computed(() => {
 return new Intl.NumberFormat('en-US', {
   style: 'currency',
   currency: 'EUR' // Assuming EUR, adjust if needed
 }).format(props.product.price);
});
</script>

<template>
  <div class="product-card">
    <!-- Product badges -->
    <div class="badge-container">
      <span v-if="product.isOrganic" class="badge organic">
        Organic
      </span>
      <span v-if="!product.isInStock" class="badge out-of-stock">
        Out of Stock
      </span>
    </div>

    <!-- Product image -->
    <div class="image-container" @click="viewProductDetails">
      <img 
        :src="displayImageUrl"
        :alt="product.name"
        class="product-image"
      />
    </div>

    <!-- Product info -->
    <div class="product-info">
      <h3 class="product-name" @click="viewProductDetails">
        {{ product.name }}
      </h3>

      <div class="farmer-info">
        <span class="farmer-name">{{ product.farmerName }}</span>
        <span class="farmer-location">{{ product.farmLocation }}</span>
      </div>

      <div class="categories">
        <span 
          v-for="category in product.categories"
          :key="category.id"
          class="category-tag"
        >
          {{ category.name }}
        </span>
      </div>

      <div class="price-section">
        <span class="price">{{ formattedPrice }}</span>
        <span class="unit">/ {{ product.unit }}</span>
      </div>

      <!-- Action buttons -->
      <div class="card-actions">
        <button 
          class="btn-action view"
          @click="viewProductDetails"
        >
          View Details
        </button>
        <button 
          class="btn-action request"
          @click="requestToBuy"
          :class="{ 'requested': hasActiveRequest }"
          :disabled="!product.isInStock || hasActiveRequest"
        >
          {{ hasActiveRequest ? 'Requested' : 'Request to Buy' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  position: relative;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
}

.badge-container {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  z-index: 1;
}

.badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.badge.organic {
  background-color: #4caf50;
  color: white;
}

.badge.out-of-stock {
  background-color: #f44336;
  color: white;
}

.image-container {
  aspect-ratio: 4/3;
  overflow: hidden;
  cursor: pointer;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.image-container:hover .product-image {
  transform: scale(1.05);
}

.product-info {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  flex-grow: 1;
}

.product-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
  cursor: pointer;
  transition: color 0.2s ease;
}

.product-name:hover {
  color: #4caf50;
}

.farmer-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.farmer-name {
  font-size: 0.9rem;
  color: #4caf50;
  font-weight: 500;
}

.farmer-location {
  font-size: 0.8rem;
  color: #666;
}

.categories {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.category-tag {
  padding: 4px 8px;
  background-color: #f5f5f5;
  border-radius: 4px;
  font-size: 0.75rem;
  color: #666;
}

.price-section {
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid #eee;
}

.price {
  font-size: 1.2rem;
  font-weight: 600;
  color: #1a1a1a;
}

.unit {
  font-size: 0.9rem;
  color: #666;
}

.card-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  margin-top: 12px;
}

.btn-action {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-action.view {
  background-color: #f5f5f5;
  color: #1a1a1a;
}

.btn-action.view:hover {
  background-color: #e0e0e0;
}

.btn-action.request {
  background-color: #4caf50;
  color: white;
}

.btn-action.request:hover:not(:disabled) {
  background-color: #43a047;
}

.btn-action.request:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.btn-action.request.requested {
  background-color: #81c784;
  cursor: default;
}

@media (max-width: 640px) {
  .product-card {
    max-width: 100%;
  }

  .card-actions {
    grid-template-columns: 1fr;
  }
}
</style>
