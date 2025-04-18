<script setup lang="ts">
/**
 * ProductDetailView displays detailed information about a product
 * @component
 */
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useProductStore } from '@/stores/productStore'
import { useAuthStore } from '@/stores/authStore' // Added
import { ProductService } from '@/services/productService'
import { OrderService } from '@/services/orderService' // Added
import type { Product } from '@/stores/productStore'
import type { OrderCreatePayload } from '@/types/orderTypes' // Added

const route = useRoute()
const router = useRouter()
const { t } = useI18n()
const productStore = useProductStore()
const authStore = useAuthStore() // Added

const product = ref<Product | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)
const selectedImageIndex = ref(0)
const quantity = ref(0)
const message = ref('') // Will be used as 'notes' for the order
const orderSubmitting = ref(false) // Added
const orderError = ref<string | null>(null) // Added
const orderSuccessMessage = ref<string | null>(null) // Added

const formattedPrice = computed(() => {
  if (!product.value) return ''
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'EUR'
  }).format(product.value.price)
})

const totalPrice = computed(() => {
  if (!product.value) return ''
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'EUR'
  }).format(product.value.price * quantity.value)
})

onMounted(async () => {
  const productId = route.params.id as string
  if (!productId) {
    error.value = 'Invalid product ID'
    loading.value = false
    return
  }
  try {
    // Fetch product directly from API using ProductService
    const productData = await ProductService.getProductById(productId)
    if (productData) {
      // Format image URLs correctly to include the complete API path
      const API_BASE_URL = 'http://localhost:8080/api/v1'
      
      // Create a copy of the product data with corrected image URLs
      const formattedProduct = {
        ...productData,
        thumbnailUrl: productData.thumbnailUrl ? `${API_BASE_URL}${productData.thumbnailUrl}` : null,
        imageUrls: (productData.imageUrls || []).map(url => `${API_BASE_URL}${url}`)
      }
      
      product.value = formattedProduct
      // Set initial quantity to minimum order quantity or 1 if not available
      quantity.value = productData.minimumOrderQuantity || 1
    } else {
      error.value = 'Product not found'
    }
  } catch (err) {
    console.error('Error fetching product:', err)
    error.value = 'Failed to load product details'
  } finally {
    loading.value = false
  }
})

const placeOrder = async () => {
  if (!product.value || orderSubmitting.value) return

  // 1. Check if user is logged in
  if (!authStore.isLoggedIn) {
    orderError.value = 'You must be logged in to place an order.'
    // Optionally redirect to login after a delay or provide a login button
    // router.push('/login');
    return;
  }

  // 2. Reset state
  orderSubmitting.value = true
  orderError.value = null
  orderSuccessMessage.value = null

  // 3. Construct payload
  const payload: OrderCreatePayload = {
    items: [
      {
        productId: product.value.id,
        quantity: quantity.value,
      },
    ],
    notes: message.value || undefined, // Send notes only if provided
  }

  // 4. Call API
  try {
    const createdOrder = await OrderService.createOrder(payload)
    console.log('Order created:', createdOrder)
    orderSuccessMessage.value = `Order placed successfully! Order ID: ${createdOrder.id}`
    
    // Optionally clear form or disable button further
    // quantity.value = product.value.minimumOrderQuantity || 1; // Reset quantity
    // message.value = '';

    // Redirect after a short delay
    setTimeout(() => {
      // Redirect to a dedicated order confirmation page or the consumer's order list
      router.push('/dashboard/consumer/orders') // Assuming this route exists
    }, 3000) // 3-second delay

  } catch (err: any) {
    console.error('Error placing order:', err)
    orderError.value = err.message || 'Failed to place order. Please try again.'
  } finally {
    orderSubmitting.value = false
  }
}

const goBack = () => {
  router.back()
}

const selectImage = (index: number) => {
  selectedImageIndex.value = index
}
</script>

<template>
  <div class="product-detail">
    <div class="container">
      <!-- Back Button -->
      <button class="btn-back" @click="goBack">
        ← Back to Products
      </button>

      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Loading product details...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="error-state">
        <p>{{ error }}</p>
        <button class="btn primary" @click="goBack">
          Return to Products
        </button>
      </div>

      <!-- Product Details -->
      <div v-else-if="product" class="product-content">
        <!-- Image Gallery -->
        <div class="gallery-section">
          <div class="main-image">
            <img 
              :src="product.imageUrls[selectedImageIndex]" 
              :alt="product.name"
            />
          </div>
          <div v-if="product.imageUrls.length > 1" class="image-thumbnails">
            <button
              v-for="(url, index) in product.imageUrls"
              :key="index"
              class="thumbnail-btn"
              :class="{ active: selectedImageIndex === index }"
              @click="selectImage(index)"
            >
              <img :src="url" :alt="`${product.name} view ${index + 1}`" />
            </button>
          </div>
        </div>

        <!-- Product Info -->
        <div class="info-section">
          <div class="product-badges">
            <span v-if="product.isOrganic" class="badge organic">
              Organic
            </span>
            <span 
              class="badge stock"
              :class="product.isInStock ? 'in-stock' : 'out-of-stock'"
            >
              {{ product.isInStock ? 'In Stock' : 'Out of Stock' }}
            </span>
          </div>

          <h1 class="product-name">{{ product.name }}</h1>

          <div class="categories">
            <span 
              v-for="category in product.categories"
              :key="category.id"
              class="category-tag"
            >
              {{ category }}
            </span>
          </div>

          <div class="price-section">
            <span class="price">{{ formattedPrice }}</span>
            <span class="unit">/ {{ product.unit }}</span>
          </div>

          <div class="description">
            <h2>Description</h2>
            <p>{{ product.description }}</p>
          </div>

          <div class="farmer-info">
            <h2>About the Farmer</h2>
            <div class="farmer-details">
              <span class="farmer-name">{{ product.farmerName }}</span>
              <span class="farmer-location">{{ product.farmLocation }}</span>
            </div>
          </div>

          <!-- Purchase Request Form -->
          <form 
            v-if="product.isInStock"
            class="order-form"
            @submit.prevent="placeOrder"
          >
            <div class="form-group">
              <label for="quantity">Quantity ({{ product.unit }})</label>
              <div class="quantity-input">
                <button 
                  type="button"
                  class="qty-btn"
                  :disabled="quantity <= product.minOrderQuantity"
                  @click="quantity = Math.max(product.minOrderQuantity, quantity - 1)"
                >
                  -
                </button>
                <input
                  id="quantity"
                  v-model.number="quantity"
                  type="number"
                  :min="product.minOrderQuantity"
                  :step="1"
                  required
                />
                <button 
                  type="button"
                  class="qty-btn"
                  @click="quantity++"
                >
                  +
                </button>
              </div>
              <span class="min-order">
                Minimum order: {{ product.minOrderQuantity }} {{ product.unit }}
              </span>
            </div>

            <div class="form-group">
              <label for="message">Message to Farmer (Optional)</label>
              <textarea
                id="message"
                v-model="message"
                rows="3"
                placeholder="Any special requests or questions?"
              ></textarea>
            </div>

            <div class="total-section">
              <span class="total-label">Total:</span>
              <span class="total-price">{{ totalPrice }}</span>
            </div>

            <!-- Order Submission Status -->
            <div v-if="orderSubmitting" class="form-status submitting">
              Placing your order... <span class="spinner small"></span>
            </div>
            <div v-if="orderError" class="form-status error">
              {{ orderError }}
            </div>
             <div v-if="orderSuccessMessage" class="form-status success">
              {{ orderSuccessMessage }}
            </div>

            <button
              type="submit"
              class="btn primary submit-btn"
              :disabled="quantity < product.minOrderQuantity || orderSubmitting || !!orderSuccessMessage"
            >
              {{ orderSubmitting ? 'Placing Order...' : (orderSuccessMessage ? 'Order Placed!' : 'Place Order') }}
            </button>
          </form>

          <!-- Already Requested Message -->
          <!-- Removed hasActiveRequest block -->

          <!-- Out of Stock Message -->
          <div v-else class="out-of-stock-message">
            <p>
              This product is currently out of stock.
              Check back later or contact the farmer for more information.
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.product-detail {
  padding: 40px 0;
  min-height: 100vh;
  background-color: #f9fafb;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.btn-back {
  margin-bottom: 32px;
  padding: 8px 16px;
  background: none;
  border: none;
  color: #666;
  font-size: 1rem;
  cursor: pointer;
  transition: color 0.2s;
}

.btn-back:hover {
  color: #4caf50;
}

/* Loading State */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
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

/* Product Content */
.product-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 48px;
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* Gallery Section */
.gallery-section {
  position: sticky;
  top: 32px;
}

.main-image {
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 16px;
}

.main-image img {
  width: 100%;
  height: auto;
  display: block;
}

.image-thumbnails {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.thumbnail-btn {
  width: 80px;
  height: 80px;
  border: 2px solid transparent;
  border-radius: 8px;
  padding: 0;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.2s;
}

.thumbnail-btn img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-btn.active {
  border-color: #4caf50;
}

/* Info Section */
.product-badges {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
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

.badge.stock {
  background-color: #f5f5f5;
}

.badge.in-stock {
  color: #4caf50;
}

.badge.out-of-stock {
  color: #f44336;
}

.product-name {
  font-size: 2rem;
  color: #1a1a1a;
  margin-bottom: 16px;
}

.categories {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 24px;
}

.category-tag {
  padding: 4px 12px;
  background-color: #f5f5f5;
  border-radius: 20px;
  font-size: 0.9rem;
  color: #666;
}

.price-section {
  margin-bottom: 32px;
}

.price {
  font-size: 2rem;
  font-weight: 600;
  color: #1a1a1a;
}

.unit {
  font-size: 1.1rem;
  color: #666;
}

.description,
.farmer-info {
  margin-bottom: 32px;
}

.description h2,
.farmer-info h2 {
  font-size: 1.2rem;
  color: #1a1a1a;
  margin-bottom: 12px;
}

.description p {
  color: #666;
  line-height: 1.6;
}

.farmer-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.farmer-name {
  font-size: 1.1rem;
  color: #4caf50;
  font-weight: 500;
}

.farmer-location {
  font-size: 0.9rem;
  color: #666;
}

/* Request Form */
.order-form {
  border-top: 1px solid #e0e0e0;
  padding-top: 32px;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  font-weight: 500;
  margin-bottom: 8px;
  color: #1a1a1a;
}

.quantity-input {
  display: flex;
  align-items: center;
  gap: 12px;
  max-width: 200px;
}

.quantity-input input {
  width: 80px;
  text-align: center;
  padding: 8px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
}

.qty-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  background: none;
  font-size: 1.2rem;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.qty-btn:hover:not(:disabled) {
  border-color: #4caf50;
  color: #4caf50;
}

.qty-btn:disabled {
  background-color: #f5f5f5;
  color: #ccc;
  cursor: not-allowed;
}

.min-order {
  display: block;
  font-size: 0.9rem;
  color: #666;
  margin-top: 8px;
}

textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 0.9rem;
  resize: vertical;
}

.total-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  padding: 16px;
  background-color: #f9fafb;
  border-radius: 8px;
}

.total-label {
  font-weight: 500;
  color: #1a1a1a;
}

.total-price {
  font-size: 1.5rem;
  font-weight: 600;
  color: #4caf50;
}

/* Form Status Messages */
.form-status {
  padding: 12px;
  margin-bottom: 16px;
  border-radius: 6px;
  text-align: center;
  font-size: 0.9rem;
}

.form-status.submitting {
  background-color: #e3f2fd;
  color: #1e88e5;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.form-status.error {
  background-color: #ffebee;
  color: #f44336;
}

.form-status.success {
  background-color: #e8f5e9;
  color: #4caf50;
}

.spinner.small {
  width: 16px;
  height: 16px;
  border-width: 2px;
  border-top-color: #1e88e5; /* Match submitting text color */
  margin: 0; /* Reset margin if needed */
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  width: 100%;
}

.btn.primary {
  background-color: #4caf50;
  color: white;
}

.btn.primary:hover:not(:disabled) {
  background-color: #43a047;
}

.btn.primary:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.btn.secondary {
  background-color: #f5f5f5;
  color: #1a1a1a;
}

.btn.secondary:hover {
  background-color: #e0e0e0;
}

/* Removed .request-status styles */
.out-of-stock-message {
  text-align: center;
  padding: 32px;
  background-color: #f9fafb;
  border-radius: 8px;
}

.out-of-stock-message p {
  margin-bottom: 16px;
  color: #666;
}

@media (max-width: 1024px) {
  .product-content {
    gap: 32px;
    padding: 24px;
  }
}

@media (max-width: 768px) {
  .product-content {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .gallery-section {
    position: static;
  }

  .thumbnail-btn {
    width: 64px;
    height: 64px;
  }
}
</style>