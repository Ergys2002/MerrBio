<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getProductById } from '@/services/productService'; // Assuming this path is correct

const route = useRoute();
const product = ref(null);
const loading = ref(true);
const error = ref<string | null>(null);

onMounted(async () => {
  const productId = route.params.id;
  if (!productId) {
    error.value = 'Product ID not found in route.';
    loading.value = false;
    return;
  }

  try {
    // Assuming productService.getProductById exists and returns the product data
    // Adjust the function call if the actual method name or signature differs
    const response = await getProductById(productId as string);
    product.value = response; // Adjust if the product data is nested in the response
  } catch (err) {
    console.error('Error fetching product:', err);
    error.value = 'Failed to load product details.';
  } finally {
    loading.value = false;
  }
});
// Placeholder for script setup logic
</script>

<template>
  <div class="product-detail-container">
    <div v-if="loading" class="loading-message">Loading product details...</div>
    <div v-else-if="error" class="error-message">{{ error }}</div>
    <div v-else-if="product">
      <h1 class="product-name">{{ product.name }}</h1>
      <img :src="product.imageUrl" :alt="product.name" class="product-image" v-if="product.imageUrl">
      <p class="product-description">{{ product.description }}</p>
      <p class="product-price">Price: ${{ product.price }}</p>
    </div>
    <div v-else>Product not found.</div>
  </div>
</template>

<style scoped>
.product-detail-container {
  display: flex;
  flex-direction: column;
  align-items: center; /* Center items horizontally */
  padding: 20px;
  max-width: 600px; /* Limit container width */
  margin: 20px auto; /* Center container on page */
  border: 1px solid #eee;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.product-image {
  max-width: 100%; /* Make image responsive within container */
  height: auto;
  max-height: 400px; /* Limit image height */
  margin-bottom: 20px; /* Space below image */
  border-radius: 4px;
}

.product-name {
  font-size: 2em;
  margin-bottom: 10px;
  color: #333;
}

.product-description {
  text-align: center; /* Center description text */
  margin-bottom: 15px;
  color: #555;
}

.product-price {
  font-size: 1.2em;
  font-weight: bold;
  color: #007bff; /* Example primary color */
  margin-bottom: 20px;
}

.error-message {
  color: red;
  font-weight: bold;
  text-align: center;
  margin-top: 20px;
}

/* Basic loading style */
.loading-message {
  text-align: center;
  margin-top: 20px;
  color: #777;
}
</style>