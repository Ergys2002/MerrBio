<script setup lang="ts">
/**
 * ProductManagement - Component for farmers to manage their products
 * @component
 */
import { ref, reactive, computed } from 'vue';

// Product type definition
interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  unit: string;
  quantity: number;
  category: string;
  image?: string;
  organic: boolean;
  createdAt: string;
}

// Form data for product creation/editing
const productForm = reactive({
  id: null as number | null,
  name: '',
  description: '',
  price: 0,
  unit: 'kg',
  quantity: 0,
  category: '',
  organic: false,
  image: ''
});

// Form validation state
const formErrors = reactive({
  name: '',
  price: '',
  quantity: '',
  category: ''
});

// UI state
const showForm = ref(false);
const isEditing = ref(false);
const isLoading = ref(false);
const searchQuery = ref('');

// Mock product data (to be replaced with store data)
const products = ref<Product[]>([
  {
    id: 1,
    name: 'Organic Tomatoes',
    description: 'Fresh organic tomatoes grown without pesticides',
    price: 3.99,
    unit: 'kg',
    quantity: 50,
    category: 'Vegetables',
    image: 'https://example.com/tomato.jpg',
    organic: true,
    createdAt: '2025-03-15T10:30:00'
  },
  {
    id: 2,
    name: 'Green Apples',
    description: 'Crisp and juicy green apples',
    price: 2.49,
    unit: 'kg',
    quantity: 100,
    category: 'Fruits',
    organic: true,
    createdAt: '2025-03-20T14:45:00'
  },
  {
    id: 3,
    name: 'Farm Fresh Eggs',
    description: 'Free-range chicken eggs',
    price: 4.99,
    unit: 'dozen',
    quantity: 30,
    category: 'Dairy & Eggs',
    organic: true,
    createdAt: '2025-03-25T09:15:00'
  }
]);

// Categories for dropdown
const categories = [
  'Vegetables',
  'Fruits',
  'Dairy & Eggs',
  'Meat',
  'Grains',
  'Herbs',
  'Other'
];

// Unit options
const unitOptions = ['kg', 'g', 'piece', 'bunch', 'liter', 'dozen'];

/**
 * Reset form data and errors
 */
const resetForm = () => {
  productForm.id = null;
  productForm.name = '';
  productForm.description = '';
  productForm.price = 0;
  productForm.unit = 'kg';
  productForm.quantity = 0;
  productForm.category = '';
  productForm.organic = false;
  productForm.image = '';
  
  // Reset errors
  Object.keys(formErrors).forEach(key => {
    formErrors[key as keyof typeof formErrors] = '';
  });
};

/**
 * Open product form for creating new product
 */
const openNewProductForm = () => {
  resetForm();
  isEditing.value = false;
  showForm.value = true;
};

/**
 * Open product form for editing existing product
 */
const editProduct = (product: Product) => {
  // Clone product data to form
  productForm.id = product.id;
  productForm.name = product.name;
  productForm.description = product.description;
  productForm.price = product.price;
  productForm.unit = product.unit;
  productForm.quantity = product.quantity;
  productForm.category = product.category;
  productForm.organic = product.organic;
  productForm.image = product.image || '';
  
  isEditing.value = true;
  showForm.value = true;
};

/**
 * Delete a product
 */
const deleteProduct = (productId: number) => {
  if (confirm('Are you sure you want to delete this product?')) {
    // Filter out the deleted product
    products.value = products.value.filter(p => p.id !== productId);
    // In real implementation, would make API call to delete
  }
};

/**
 * Validate form fields
 * @returns {boolean} True if form is valid
 */
const validateForm = (): boolean => {
  let isValid = true;
  
  // Reset errors
  Object.keys(formErrors).forEach(key => {
    formErrors[key as keyof typeof formErrors] = '';
  });
  
  // Name validation
  if (!productForm.name.trim()) {
    formErrors.name = 'Product name is required';
    isValid = false;
  }
  
  // Price validation
  if (productForm.price <= 0) {
    formErrors.price = 'Price must be greater than zero';
    isValid = false;
  }
  
  // Quantity validation
  if (productForm.quantity <= 0) {
    formErrors.quantity = 'Quantity must be greater than zero';
    isValid = false;
  }
  
  // Category validation
  if (!productForm.category) {
    formErrors.category = 'Category is required';
    isValid = false;
  }
  
  return isValid;
};

/**
 * Submit product form (create or update)
 */
const submitProductForm = () => {
  if (!validateForm()) return;
  
  isLoading.value = true;
  
  // Simulate API call
  setTimeout(() => {
    if (isEditing.value && productForm.id) {
      // Update existing product
      const index = products.value.findIndex(p => p.id === productForm.id);
      if (index !== -1) {
        products.value[index] = {
          ...productForm,
          id: productForm.id,
          createdAt: products.value[index].createdAt
        } as Product;
      }
    } else {
      // Create new product with generated ID
      const newProduct: Product = {
        ...productForm,
        id: Math.max(0, ...products.value.map(p => p.id)) + 1,
        createdAt: new Date().toISOString()
      } as Product;
      
      products.value.push(newProduct);
    }
    
    isLoading.value = false;
    showForm.value = false;
    resetForm();
  }, 600); // Simulate network delay
};

/**
 * Filter products based on search query
 */
const filteredProducts = computed(() => {
  if (!searchQuery.value) return products.value;
  
  const query = searchQuery.value.toLowerCase();
  return products.value.filter(product => 
    product.name.toLowerCase().includes(query) || 
    product.description.toLowerCase().includes(query) ||
    product.category.toLowerCase().includes(query)
  );
});
</script>

<template>
  <div class="product-management">
    <div class="header-actions">
      <h2>Product Management</h2>
      <div class="actions">
        <div class="search-box">
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="Search products..." 
          />
        </div>
        <button @click="openNewProductForm" class="btn primary">
          Add New Product
        </button>
      </div>
    </div>

    <!-- Product Form Modal -->
    <div v-if="showForm" class="product-form-modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditing ? 'Edit Product' : 'Add New Product' }}</h3>
          <button class="close-btn" @click="showForm = false">&times;</button>
        </div>
        
        <form @submit.prevent="submitProductForm" class="product-form">
          <div class="form-group">
            <label for="product-name">Product Name *</label>
            <input 
              id="product-name" 
              type="text" 
              v-model="productForm.name" 
              :class="{ 'error': formErrors.name }"
            />
            <span v-if="formErrors.name" class="error-message">{{ formErrors.name }}</span>
          </div>
          
          <div class="form-group">
            <label for="product-desc">Description</label>
            <textarea 
              id="product-desc" 
              v-model="productForm.description" 
              rows="3"
            ></textarea>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label for="product-price">Price (€) *</label>
              <input 
                id="product-price" 
                type="number" 
                step="0.01" 
                min="0" 
                v-model.number="productForm.price"
                :class="{ 'error': formErrors.price }"
              />
              <span v-if="formErrors.price" class="error-message">{{ formErrors.price }}</span>
            </div>
            
            <div class="form-group">
              <label for="product-unit">Unit</label>
              <select id="product-unit" v-model="productForm.unit">
                <option v-for="unit in unitOptions" :key="unit" :value="unit">
                  {{ unit }}
                </option>
              </select>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label for="product-quantity">Quantity *</label>
              <input 
                id="product-quantity" 
                type="number" 
                min="0" 
                v-model.number="productForm.quantity"
                :class="{ 'error': formErrors.quantity }"
              />
              <span v-if="formErrors.quantity" class="error-message">{{ formErrors.quantity }}</span>
            </div>
            
            <div class="form-group">
              <label for="product-category">Category *</label>
              <select 
                id="product-category" 
                v-model="productForm.category"
                :class="{ 'error': formErrors.category }"
              >
                <option value="">Select a category</option>
                <option v-for="category in categories" :key="category" :value="category">
                  {{ category }}
                </option>
              </select>
              <span v-if="formErrors.category" class="error-message">{{ formErrors.category }}</span>
            </div>
          </div>
          
          <div class="form-group checkbox">
            <input id="product-organic" type="checkbox" v-model="productForm.organic" />
            <label for="product-organic">Organic Product</label>
          </div>
          
          <div class="form-group">
            <label for="product-image">Image URL</label>
            <input id="product-image" type="text" v-model="productForm.image" />
          </div>
          
          <div class="form-actions">
            <button type="button" @click="showForm = false" class="btn secondary">
              Cancel
            </button>
            <button type="submit" class="btn primary" :disabled="isLoading">
              {{ isLoading ? 'Saving...' : (isEditing ? 'Update Product' : 'Add Product') }}
            </button>
          </div>
        </form>
      </div>
    </div>
    
    <!-- Products List -->
    <div v-if="filteredProducts.length" class="products-list">
      <table>
        <thead>
          <tr>
            <th>Product</th>
            <th>Price</th>
            <th>Quantity</th>
            <th>Category</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in filteredProducts" :key="product.id">
            <td class="product-info">
              <div class="product-image" v-if="product.image">
                <img :src="product.image" :alt="product.name" />
              </div>
              <div v-else class="product-image placeholder">
                <span>No Image</span>
              </div>
              <div class="product-details">
                <h4>{{ product.name }}</h4>
                <p class="product-description">{{ product.description }}</p>
              </div>
            </td>
            <td>€{{ product.price }} / {{ product.unit }}</td>
            <td>{{ product.quantity }} {{ product.unit }}</td>
            <td>{{ product.category }}</td>
            <td class="actions">
              <button @click="editProduct(product)" class="btn-icon edit">
                Edit
              </button>
              <button @click="deleteProduct(product.id)" class="btn-icon delete">
                Delete
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- Empty state -->
    <div v-else-if="!isLoading" class="empty-state">
      <p v-if="searchQuery">No products match your search.</p>
      <p v-else>You haven't added any products yet.</p>
      <button @click="openNewProductForm" class="btn primary">
        Add Your First Product
      </button>
    </div>
  </div>
</template>

<style scoped>
.product-management {
  position: relative;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.actions {
  display: flex;
  gap: 1rem;
}

.search-box input {
  padding: 0.75rem;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
  width: 250px;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.btn.primary {
  background-color: hsla(160, 100%, 37%, 1);
  color: white;
}

.btn.secondary {
  background-color: #e0e0e0;
  color: #333;
}

.product-form-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 700px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e0e0e0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

.product-form {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-row {
  display: flex;
  gap: 1rem;
}

.form-row .form-group {
  flex: 1;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group.checkbox {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.form-group.checkbox input {
  width: auto;
}

.error {
  border-color: #ff4d4f !important;
}

.error-message {
  color: #ff4d4f;
  font-size: 0.85rem;
  margin-top: 0.25rem;
  display: block;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
}

/* Products list styling */
.products-list {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #e0e0e0;
}

th {
  background-color: #f9f9f9;
  font-weight: 600;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-image.placeholder {
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 0.8rem;
}

.product-details h4 {
  margin: 0 0 0.5rem;
}

.product-description {
  font-size: 0.9rem;
  color: #666;
  margin: 0;
  max-width: 300px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.btn-icon {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  font-size: 0.9rem;
  border-radius: 4px;
}

.btn-icon.edit {
  color: #4096ff;
}

.btn-icon.edit:hover {
  background-color: rgba(64, 150, 255, 0.1);
}

.btn-icon.delete {
  color: #ff4d4f;
}

.btn-icon.delete:hover {
  background-color: rgba(255, 77, 79, 0.1);
}

.empty-state {
  text-align: center;
  padding: 3rem 1rem;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.empty-state p {
  margin-bottom: 1.5rem;
  color: #666;
}

@media (max-width: 768px) {
  .header-actions {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .actions {
    width: 100%;
  }
  
  .search-box,
  .search-box input {
    width: 100%;
  }
  
  .btn {
    width: 100%;
  }
  
  .form-row {
    flex-direction: column;
  }
  
  .product-info {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
