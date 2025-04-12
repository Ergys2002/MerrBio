<script setup lang="ts">
/**
 * ProductCard component displays individual product information in a card layout
 * @component
 */
import { defineProps } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

interface Farmer {
  id: number;
  name: string;
  location: string;
}

interface ProductProps {
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

const props = defineProps<ProductProps>();

/**
 * Navigate to product detail page
 */
const viewProductDetails = () => {
  router.push(`/products/${props.id}`);
};

/**
 * Handle request to buy product
 */
const requestToBuy = () => {
  // In a real implementation, this would open a modal or navigate to checkout
  // For now, we'll just log and navigate to the product detail page
  console.log('Request to buy product:', props.id);
  router.push(`/products/${props.id}`);
};
</script>

<template>
  <div class="product-card">
    <div class="product-badge" v-if="organic">
      <span>Organic</span>
    </div>
    
    <div class="product-image">
      <img :src="image" :alt="name" @click="viewProductDetails" />
    </div>
    
    <div class="product-info">
      <h3 class="product-name" @click="viewProductDetails">{{ name }}</h3>
      <p class="product-farmer">by {{ farmer.name }}</p>
      <p class="product-price">{{ price.toFixed(2) }}€ / {{ unit }}</p>
      
      <div class="product-actions">
        <button class="view-button" @click="viewProductDetails">
          View Details
        </button>
        <button class="buy-button" @click="requestToBuy">
          Request to Buy
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  position: relative;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.product-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background-color: #4caf50;
  color: white;
  font-size: 0.75rem;
  padding: 4px 8px;
  border-radius: 4px;
  z-index: 1;
}

.product-image {
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
  cursor: pointer;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-info {
  padding: 16px;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.product-name {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
  cursor: pointer;
}

.product-name:hover {
  color: #4caf50;
}

.product-farmer {
  font-size: 0.9rem;
  color: #4caf50;
  margin-bottom: 8px;
}

.product-price {
  font-weight: 600;
  font-size: 1.1rem;
  margin-bottom: 16px;
}

.product-actions {
  margin-top: auto;
  display: flex;
  gap: 8px;
}

.view-button, .buy-button {
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.1s;
  flex: 1;
}

.view-button {
  background-color: #f5f5f5;
  color: #333;
}

.view-button:hover {
  background-color: #e0e0e0;
}

.buy-button {
  background-color: #4caf50;
  color: white;
}

.buy-button:hover {
  background-color: #3d8b40;
}

.view-button:active, .buy-button:active {
  transform: scale(0.98);
}
</style>
