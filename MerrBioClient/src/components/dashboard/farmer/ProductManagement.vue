<script setup lang="ts">
/**
 * ProductManagement - Component for farmers to manage their products
 * @component
 */
import { ref, reactive, computed, onMounted } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import { useProductStore } from '@/stores/productStore';
import axios from 'axios';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const authStore = useAuthStore();
const productStore = useProductStore();

// Product type definition matching backend model
interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  unit: string;
  minAvailableQuantity: number;
  maxAvailableQuantity: number;
  minimumOrderQuantity: number;
  categories: Array<{id: string, name: string, description: string}>;
  isOrganic: boolean;
  isInStock: boolean;
  imageUrls: string[];
  thumbnailUrl: string;
  createdAt: string;
  updatedAt: string;
}

interface Category {
  id: string;
  name: string;
  description: string;
}

// Form data for product creation/editing
const productForm = reactive({
  id: null as string | null,
  name: '',
  description: '',
  price: 0,
  unit: 'KILOGRAM',
  minAvailableQuantity: 0,
  maxAvailableQuantity: 0,
  minimumOrderQuantity: 1,
  categoryIds: [] as string[],
  isOrganic: false,
  thumbnailUrl: '',
  thumbnail: null as File | null,
  images: [] as File[]
});

// Form validation state
const formErrors = reactive({
  name: '',
  price: '',
  minAvailableQuantity: '',
  categoryIds: ''
});

// UI state
const showForm = ref(false);
const isEditing = ref(false);
const isLoading = ref(false);
const searchQuery = ref('');
const products = ref<Product[]>([]);
const categories = ref<Category[]>([]);
const loadingInitial = ref(true);
const loadingError = ref<string | null>(null);

// Unit options based on backend enum
const unitOptions = [
  { value: 'KILOGRAM', label: 'Kilogram (kg)' },
  { value: 'GRAM', label: 'Gram (g)' },
  { value: 'POUND', label: 'Pound (lb)' },
  { value: 'OUNCE', label: 'Ounce (oz)' },
  { value: 'LITER', label: 'Liter (L)' },
  { value: 'MILLILITER', label: 'Milliliter (mL)' },
  { value: 'GALLON', label: 'Gallon (gal)' },
  { value: 'PIECE', label: 'Piece (pc)' },
  { value: 'DOZEN', label: 'Dozen (dz)' },
  { value: 'BUNDLE', label: 'Bundle' },
  { value: 'BOX', label: 'Box' },
  { value: 'CRATE', label: 'Crate' },
  { value: 'BASKET', label: 'Basket' },
  { value: 'SACK', label: 'Sack' },
  { value: 'BOTTLE', label: 'Bottle' },
  { value: 'JAR', label: 'Jar' },
  { value: 'PACKET', label: 'Packet' }
];

// Store actions will be called directly where needed (e.g., onMounted, submit)

/**
 * Reset form data and errors
 */
const resetForm = () => {
  productForm.id = null;
  productForm.name = '';
  productForm.description = '';
  productForm.price = 0;
  productForm.unit = 'KILOGRAM';
  productForm.minAvailableQuantity = 0;
  productForm.maxAvailableQuantity = 0;
  productForm.minimumOrderQuantity = 1;
  productForm.categoryIds = [];
  productForm.isOrganic = false;
  productForm.thumbnailUrl = '';
  productForm.thumbnail = null;
  productForm.images = [];
  
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
  productForm.minAvailableQuantity = product.minAvailableQuantity;
  productForm.maxAvailableQuantity = product.maxAvailableQuantity;
  productForm.minimumOrderQuantity = product.minimumOrderQuantity;
  productForm.categoryIds = product.categories.map(cat => cat.id);
  productForm.isOrganic = product.isOrganic;
  productForm.thumbnailUrl = product.thumbnailUrl;
  
  isEditing.value = true;
  showForm.value = true;
};

/**
 * Delete a product
 */
const deleteProduct = async (productId: string) => {
  if (confirm(t('products.confirmDelete'))) {
    try {
      isLoading.value = true;
      const success = await productStore.deleteProduct(productId, authStore.accessToken || '');
      if (!success) {
        alert(productStore.error || t('errors.deleteProductFailed'));
      }
      // products.value will be updated by the store
    } catch (error: any) {
      console.error('Error deleting product:', error);
      alert(productStore.error || t('errors.deleteProductFailed'));
    } finally {
      isLoading.value = false;
    }
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
    formErrors.name = t('validation.productNameRequired');
    isValid = false;
  }
  
  // Price validation
  if (productForm.price <= 0) {
    formErrors.price = t('validation.priceGreaterThanZero');
    isValid = false;
  }
  
  // Quantity validation
  if (productForm.minAvailableQuantity <= 0) {
    formErrors.minAvailableQuantity = t('validation.quantityGreaterThanZero');
    isValid = false;
  }
  
  // Category validation
  if (!productForm.categoryIds.length) {
    formErrors.categoryIds = t('validation.categoryRequired');
    isValid = false;
  }
  
  return isValid;
};

/**
 * Submit product form (create or update)
 */
const submitProductForm = async () => {
  if (!validateForm()) return;
  isLoading.value = true;
  try {
    if (isEditing.value && productForm.id) {
      // Update existing product
      const updated = await productStore.updateProduct(productForm.id, productForm, authStore.accessToken || '');
      if (!updated) {
        alert(productStore.error || t('errors.saveProductFailed'));
        return;
      }    } else {
      // Prepare form data for multipart/form-data request
      const formData = new FormData();
      formData.append('name', productForm.name);
      formData.append('description', productForm.description);
      formData.append('price', productForm.price.toString());
      formData.append('unit', productForm.unit);
      formData.append('minAvailableQuantity', productForm.minAvailableQuantity.toString());
      formData.append('maxAvailableQuantity', productForm.maxAvailableQuantity.toString());
      formData.append('minimumOrderQuantity', productForm.minimumOrderQuantity.toString());
      formData.append('isOrganic', productForm.isOrganic.toString());
      productForm.categoryIds.forEach(categoryId => {
        formData.append('categoryIds', String(categoryId));
      });
      
      // Add thumbnail file (required by backend API)
      if (productForm.thumbnail) {
        formData.append('thumbnail', productForm.thumbnail);
      } else {
        alert(t('validation.thumbnailRequired') || 'Please select a thumbnail image for the product');
        return;
      }
      
      // Add additional product images (optional)
      if (productForm.images.length > 0) {
        productForm.images.forEach(image => {
          formData.append('images', image);
        });
      }
      const created = await productStore.createProduct(formData, authStore.accessToken || '');
      if (!created) {
        alert(productStore.error || t('errors.saveProductFailed'));
        return;
      }
    }
    // Refresh product list
    // Refresh product list by calling store action directly
    const userId = authStore.user?.id;
    const token = authStore.accessToken || '';
    if (userId) {
      await productStore.fetchFarmerProducts(String(userId), token);
      products.value = productStore.products; // Update local ref
    } else {
      console.error("Cannot refresh products after save: User ID not found.");
      // Consider showing an error message to the user here
    }
    showForm.value = false;
    resetForm();
  } catch (error: any) {
    console.error('Error saving product:', error);
    alert(productStore.error || t('errors.saveProductFailed'));
  } finally {
    isLoading.value = false;
  }
};

/**
 * Handle file input change for product thumbnail
 */
const handleThumbnailChange = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files.length > 0) {
    productForm.thumbnail = input.files[0];
  }
};

/**
 * Handle file input change for product images
 */
const handleFileInputChange = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (input.files) {
    productForm.images = Array.from(input.files);
  }
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
    product.categories.some(cat => cat.name.toLowerCase().includes(query))
  );
}); // <-- Added missing closing parenthesis and brace

/**
 * Retry fetching farmer products, called from error state button
 */
async function retryFetchProducts() {
  loadingInitial.value = true; // Show loading indicator again
  loadingError.value = null;
  const userId = authStore.user?.id;
  const token = authStore.accessToken || '';

  if (!userId || authStore.user?.role !== 'FARMER') {
    loadingError.value = t('errors.notAFarmer');
    loadingInitial.value = false;
    return;
  }

  try {
    await productStore.fetchFarmerProducts(String(userId), token);
    products.value = productStore.products; // Update local ref
  } catch (error: any) {
    console.error('Error retrying fetch products:', error);
    loadingError.value = productStore.error || t('errors.fetchProductsFailed');
  } finally {
    loadingInitial.value = false;
  }
};

// This code was moved into the corrected filteredProducts definition above
// Removed extraneous closing brackets from previous incorrect nesting

// Load products and categories when component mounts
onMounted(async () => {
  loadingInitial.value = true;
  loadingError.value = null;
  const userId = authStore.user?.id;
  const token = authStore.accessToken || '';

  if (!userId || authStore.user?.role !== 'FARMER') {
    loadingError.value = t('errors.notAFarmer');
    loadingInitial.value = false;
    return;
  }

  try {
    // Fetch categories first (or in parallel if independent)
    await productStore.fetchCategories(token);
    categories.value = productStore.categories; // Update local categories ref

    // Fetch farmer products
    await productStore.fetchFarmerProducts(String(userId), token);
    products.value = productStore.products; // Update local products ref

  } catch (error: any) {
    console.error('Error fetching initial data:', error);
    // Use specific error from store if available, otherwise generic
    loadingError.value = productStore.error || t('errors.fetchDataFailed');
  } finally {
    loadingInitial.value = false;
  }
});
</script>

<template>
  <div class="product-management">
    <div class="header-actions">
      <h2>{{ t('products.management') }}</h2>
      <div class="actions">
        <div class="search-box">
          <input 
            type="text" 
            v-model="searchQuery" 
            :placeholder="t('products.searchPlaceholder')" 
          />
        </div>
        <button @click="openNewProductForm" class="btn primary">
          {{ t('products.addNew') }}
        </button>
      </div>
    </div>

    <!-- Loading state -->
    <div v-if="loadingInitial" class="loading-state">
      <p>{{ t('common.loading') }}</p>
    </div>
    
    <!-- Error state -->
    <div v-else-if="loadingError" class="error-state">
      <p>{{ loadingError }}</p>
      <button @click="retryFetchProducts" class="btn primary">
        {{ t('common.retry') }}
      </button>
    </div>

    <!-- Product Form Modal -->
    <div v-if="showForm" class="product-form-modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditing ? t('products.editProduct') : t('products.addProduct') }}</h3>
          <button class="close-btn" @click="showForm = false">&times;</button>
        </div>
        
        <form @submit.prevent="submitProductForm" class="product-form">
          <div class="form-group">
            <label for="product-name">Titulli*</label>
            <input 
              id="product-name" 
              type="text" 
              v-model="productForm.name" 
              :class="{ 'error': formErrors.name }"
            />
            <span v-if="formErrors.name" class="error-message">{{ formErrors.name }}</span>
          </div>
          
          <div class="form-group">
            <label for="product-desc">Pershkrimi</label>
            <textarea 
              id="product-desc" 
              v-model="productForm.description" 
              rows="3"
            ></textarea>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label for="product-price">Cmimi (€) *</label>
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
              <label for="product-unit">Njesia</label>
              <select id="product-unit" v-model="productForm.unit">
                <option v-for="unit in unitOptions" :key="unit.value" :value="unit.value">
                  {{ unit.label }}
                </option>
              </select>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label for="product-min-quantity">Gjendja Min *</label>
              <input 
                id="product-min-quantity" 
                type="number" 
                min="0" 
                v-model.number="productForm.minAvailableQuantity"
                :class="{ 'error': formErrors.minAvailableQuantity }"
              />
              <span v-if="formErrors.minAvailableQuantity" class="error-message">
                {{ formErrors.minAvailableQuantity }}
              </span>
            </div>
            
            <div class="form-group">
              <label for="product-max-quantity">Gjendja Max *</label>
              <input 
                id="product-max-quantity" 
                type="number" 
                min="0" 
                v-model.number="productForm.maxAvailableQuantity"
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label for="product-min-order">Porosia Min</label>
              <input 
                id="product-min-order" 
                type="number" 
                min="1" 
                v-model.number="productForm.minimumOrderQuantity"
              />
            </div>
            
            <div class="form-group">
              <label for="product-category">Kategoria *</label>
              <select 
                multiple
                id="product-category" 
                v-model="productForm.categoryIds"
                :class="{ 'error': formErrors.categoryIds }"
              >
                <option v-for="category in categories" :key="category.id" :value="category.id">
                  {{ category.name }}
                </option>
              </select>
              <span v-if="formErrors.categoryIds" class="error-message">{{ formErrors.categoryIds }}</span>
            </div>
          </div>
            <div class="form-group checkbox">
            <input id="product-organic" type="checkbox" v-model="productForm.isOrganic" />
            <label for="product-organic">Produkt Organik</label>
          </div>
          
          <div class="form-group">
            <label for="product-thumbnail">Product Thumbnail*</label>
            <input 
              id="product-thumbnail" 
              type="file" 
              accept="image/*" 
              @change="handleThumbnailChange" 
            />
            <small>Main image that will be displayed for this product</small>
          </div>
          
          <div class="form-group">
            <label for="product-images">Image</label>
            <input 
              id="product-images" 
              type="file" 
              multiple 
              accept="image/*" 
              @change="handleFileInputChange" 
            />
            <small>Additional product images (optional)</small>
          </div>
          
          <div class="form-actions">
            <button type="button" @click="showForm = false" class="btn secondary">
              {{ t('common.cancel') }}
            </button>
            <button type="submit" class="btn primary" :disabled="isLoading">
              {{ isLoading ? t('common.saving') : (isEditing ? t('common.update') : t('common.add')) }}
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
            <th>{{ t('products.table.product') }}</th>
            <th>{{ t('products.table.price') }}</th>
            <th>{{ t('products.table.quantity') }}</th>
            <th>{{ t('products.table.category') }}</th>
            <th>{{ t('products.table.actions') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in filteredProducts" :key="product.id">
            <td class="product-info">
              <div class="product-image" v-if="product.thumbnailUrl">
                <img :src="product.thumbnailUrl" :alt="product.name" />
              </div>
              <div v-else class="product-image placeholder">
                <span>{{ t('products.noImage') }}</span>
              </div>
              <div class="product-details">
                <h4>{{ product.name }}</h4>
                <p class="product-description">{{ product.description }}</p>
              </div>            </td>
            <td>€{{ product.price }} / {{ product.unit }}</td>
            <td>{{ product.minAvailableQuantity }} - {{ product.maxAvailableQuantity }} {{ product.unit }}</td>
            <td>{{ product.categories && Array.isArray(product.categories) ? product.categories.map(c => c.name).join(', ') : '' }}</td>
            <td class="actions">
              <button @click="editProduct(product)" class="btn-icon edit">
                {{ t('common.edit') }}
              </button>
              <button @click="deleteProduct(product.id)" class="btn-icon delete">
                {{ t('common.delete') }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- Empty state -->
    <div v-else-if="!loadingInitial && !loadingError" class="empty-state">
      <p v-if="searchQuery">{{ t('products.noProductsMatch') }}</p>
      <p v-else>{{ t('products.noProductsYet') }}</p>
      <button @click="openNewProductForm" class="btn primary">
        {{ t('products.addFirstProduct') }}
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

.loading-state, .error-state, .empty-state {
  text-align: center;
  padding: 2rem;
  background-color: #f9f9f9;
  border-radius: 8px;
  margin: 2rem 0;
}

.error-state {
  color: #ff4d4f;
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
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f0f0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-image.placeholder {
  color: #999;
  font-size: 0.8rem;
  text-align: center;
}

.product-details h4 {
  margin: 0 0 0.25rem;
}

.product-description {
  margin: 0;
  font-size: 0.9rem;  color: #666;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  max-width: 400px;
}

.actions {
  white-space: nowrap;
}

.btn-icon {
  padding: 0.5rem 0.75rem;
  margin-right: 0.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
}

.btn-icon.edit {
  background-color: #e6f7ff;
  color: #1890ff;
}

.btn-icon.delete {
  background-color: #fff1f0;
  color: #ff4d4f;
}
</style>
