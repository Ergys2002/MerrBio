<script setup lang="ts">
/**
 * AdminDashboardView - Main dashboard view for super admin/administrator
 * @component
 */
import { ref, onMounted, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { useAuthStore } from '../stores/authStore';

// Define types for our data
interface User {
  id: string;
  name: string;
  email: string;
  role: string;
  createdAt: string;
  status: string;
}

interface Category {
  id: string;
  name: string;
  description: string;
  createdAt: string;
}

interface Product {
  id: string;
  name: string;
  description: string;
  farmerId: string;
  farmerName: string;
  farmLocation: string;
  price: number;
  unit: string;
  minAvailableQuantity: number;
  maxAvailableQuantity: number;
  minimumOrderQuantity: number;
  categories: Category[];
  isOrganic: boolean;
  isInStock: boolean;
  imageUrls: string[];
  thumbnailUrl: string;
  createdAt: string;
  updatedAt: string;
}

// Get translations
const { t } = useI18n();

// Get auth store for user data
const authStore = useAuthStore();

// Tabs for navigation
const activeTab = ref('users');
const tabs = [
  { id: 'users', label: t('dashboard.admin.users') },
  { id: 'products', label: t('dashboard.admin.products') },
  { id: 'categories', label: t('dashboard.admin.categories') },
  { id: 'statistics', label: t('dashboard.stats') }
];

// Loading states
const isLoading = ref({
  users: false,
  products: false,
  categories: false
});

// Data storage
const users = ref<User[]>([]);
const products = ref<Product[]>([]);
const categories = ref<Category[]>([]);

// Search and filter states
const searchQueries = ref({
  users: '',
  products: '',
  categories: ''
});

// Categories form data
const categoryForm = ref({
  name: '',
  description: ''
});

// Error states
const errors = ref({
  users: null,
  products: null,
  categories: null,
  categoryForm: {
    name: '',
    description: ''
  }
});

// Filter and sort states
const userRoleFilter = ref('all');
const productCategoryFilter = ref('all');
const userSortBy = ref('name');
const productSortBy = ref('name');
const categorySortBy = ref('name');
const sortDirection = ref('asc');

/**
 * Toggle sort direction
 */
const toggleSortDirection = () => {
  sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc';
};

/**
 * Format date
 */
const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
};

// Computed filtered data
const filteredUsers = computed(() => {
  let result = [...users.value];
  
  // Apply search filter
  if (searchQueries.value.users) {
    const query = searchQueries.value.users.toLowerCase();
    result = result.filter(user => 
      user.name.toLowerCase().includes(query) || 
      user.email.toLowerCase().includes(query)
    );
  }
  
  // Apply role filter
  if (userRoleFilter.value !== 'all') {
    result = result.filter(user => user.role === userRoleFilter.value);
  }
  
  // Apply sorting
  result.sort((a, b) => {
    const factor = sortDirection.value === 'asc' ? 1 : -1;
    
    if (userSortBy.value === 'name') {
      return a.name.localeCompare(b.name) * factor;
    } else if (userSortBy.value === 'email') {
      return a.email.localeCompare(b.email) * factor;
    } else if (userSortBy.value === 'date') {
      return (new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()) * factor;
    }
    
    return 0;
  });
  
  return result;
});

const filteredProducts = computed(() => {
  let result = [...products.value];
  
  // Apply search filter
  if (searchQueries.value.products) {
    const query = searchQueries.value.products.toLowerCase();
    result = result.filter(product => 
      product.name.toLowerCase().includes(query) || 
      product.description.toLowerCase().includes(query)
    );
  }
  
  // Apply category filter
  if (productCategoryFilter.value !== 'all') {
    result = result.filter(product => 
      product.categories.some(category => category.id === productCategoryFilter.value)
    );
  }
  
  // Apply sorting
  result.sort((a, b) => {
    const factor = sortDirection.value === 'asc' ? 1 : -1;
    
    if (productSortBy.value === 'name') {
      return a.name.localeCompare(b.name) * factor;
    } else if (productSortBy.value === 'price') {
      return (a.price - b.price) * factor;
    } else if (productSortBy.value === 'date') {
      return (new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()) * factor;
    }
    
    return 0;
  });
  
  return result;
});

const filteredCategories = computed(() => {
  let result = [...categories.value];
  
  // Apply search filter
  if (searchQueries.value.categories) {
    const query = searchQueries.value.categories.toLowerCase();
    result = result.filter(category => 
      category.name.toLowerCase().includes(query) || 
      (category.description && category.description.toLowerCase().includes(query))
    );
  }
  
  // Apply sorting
  result.sort((a, b) => {
    const factor = sortDirection.value === 'asc' ? 1 : -1;
    
    if (categorySortBy.value === 'name') {
      return a.name.localeCompare(b.name) * factor;
    } else if (categorySortBy.value === 'date') {
      return (new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()) * factor;
    }
    
    return 0;
  });
  
  return result;
});

/**
 * Fetch users from API
 */
const fetchUsers = async () => {
  try {
    isLoading.value.users = true;
    errors.value.users = null;
    
    // Replace with actual API call
    // const response = await api.get('/users');
    // users.value = response.data;
    
    // Mock data for development
    users.value = [
      {
        id: '1',
        name: 'John Farmer',
        email: 'john@farm.com',
        role: 'FARMER',
        createdAt: '2025-03-15T10:30:00Z',
        status: 'active'
      },
      {
        id: '2',
        name: 'Maria Consumer',
        email: 'maria@example.com',
        role: 'CONSUMER',
        createdAt: '2025-03-20T14:45:00Z',
        status: 'active'
      },
      {
        id: '3',
        name: 'Admin User',
        email: 'admin@merrbio.com',
        role: 'ADMIN',
        createdAt: '2025-01-01T09:00:00Z',
        status: 'active'
      },
      {
        id: '4',
        name: 'Dritan Farmer',
        email: 'dritan@farm.com',
        role: 'FARMER',
        createdAt: '2025-04-01T11:20:00Z',
        status: 'active'
      }
    ];
  } catch (error: any) {
    console.error('Error fetching users:', error);
    errors.value.users = error.message || 'Failed to load users';
  } finally {
    isLoading.value.users = false;
  }
};

/**
 * Fetch products from API
 */
const fetchProducts = async () => {
  try {
    isLoading.value.products = true;
    errors.value.products = null;
    
    // Using the GET /products API endpoint provided
    const response = await fetch('/api/products');
    
    if (!response.ok) {
      throw new Error(`API request failed with status ${response.status}`);
    }
    
    const data = await response.json();
    products.value = data;
    
    // Fallback to mock data if the API returns an empty array
    if (!products.value || products.value.length === 0) {
      products.value = [
        {
          id: '1',
          name: 'Organic Tomatoes',
          description: 'Fresh organic tomatoes grown locally',
          farmerId: '1',
          farmerName: 'John Farmer',
          farmLocation: 'Tirana',
          price: 3.99,
          unit: 'KILOGRAM',
          minAvailableQuantity: 5,
          maxAvailableQuantity: 100,
          minimumOrderQuantity: 2,
          categories: [
            { id: '1', name: 'Vegetables', description: 'Fresh vegetables', createdAt: '2025-01-15T08:00:00Z' }
          ],
          isOrganic: true,
          isInStock: true,
          imageUrls: ['https://example.com/tomato.jpg'],
          thumbnailUrl: 'https://example.com/tomato-thumb.jpg',
          createdAt: '2025-03-01T10:00:00Z',
          updatedAt: '2025-03-01T10:00:00Z'
        },
        {
          id: '2',
          name: 'Free-Range Eggs',
          description: 'Free-range eggs from happy chickens',
          farmerId: '1',
          farmerName: 'John Farmer',
          farmLocation: 'Tirana',
          price: 4.50,
          unit: 'DOZEN',
          minAvailableQuantity: 10,
          maxAvailableQuantity: 50,
          minimumOrderQuantity: 1,
          categories: [
            { id: '2', name: 'Dairy & Eggs', description: 'Dairy products and eggs', createdAt: '2025-01-15T08:05:00Z' }
          ],
          isOrganic: true,
          isInStock: true,
          imageUrls: ['https://example.com/eggs.jpg'],
          thumbnailUrl: 'https://example.com/eggs-thumb.jpg',
          createdAt: '2025-03-05T09:30:00Z',
          updatedAt: '2025-03-05T09:30:00Z'
        }
      ];
    }
  } catch (error: any) {
    console.error('Error fetching products:', error);
    errors.value.products = error.message || 'Failed to load products';
  } finally {
    isLoading.value.products = false;
  }
};

/**
 * Fetch categories from API
 */
const fetchCategories = async () => {
  try {
    isLoading.value.categories = true;
    errors.value.categories = null;
    
    // API call
    try {
      const response = await fetch('/api/categories');
      
      if (response.ok) {
        const data = await response.json();
        categories.value = data;
      } else {
        throw new Error(`Failed to fetch categories: ${response.status}`);
      }
    } catch (apiError) {
      console.error('API error:', apiError);
      // Fallback to mock data if API fails
      categories.value = [
        {
          id: '1',
          name: 'Vegetables',
          description: 'Fresh vegetables',
          createdAt: '2025-01-15T08:00:00Z'
        },
        {
          id: '2',
          name: 'Dairy & Eggs',
          description: 'Dairy products and eggs',
          createdAt: '2025-01-15T08:05:00Z'
        },
        {
          id: '3',
          name: 'Fruits',
          description: 'Fresh fruits',
          createdAt: '2025-01-15T08:10:00Z'
        }
      ];
    }
  } catch (error: any) {
    console.error('Error fetching categories:', error);
    errors.value.categories = error.message || 'Failed to load categories';
  } finally {
    isLoading.value.categories = false;
  }
};

/**
 * Delete user
 */
const deleteUser = async (userId: string) => {
  if (confirm(t('dashboard.admin.confirmDeleteUser'))) {
    try {
      // API call
      const response = await fetch(`/api/users/${userId}`, { 
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${authStore.accessToken || ''}`
        }
      });
      
      if (!response.ok) {
        throw new Error(`Failed to delete user: ${response.status}`);
      }
      
      // Remove from local state
      users.value = users.value.filter(user => user.id !== userId);
      
      alert(t('dashboard.admin.userDeleted'));
    } catch (error: any) {
      console.error('Error deleting user:', error);
      alert(error.message || t('dashboard.admin.deleteUserFailed'));
    }
  }
};

/**
 * Toggle user status (active/inactive)
 */
const toggleUserStatus = async (userId: string, currentStatus: string) => {
  try {
    const newStatus = currentStatus === 'active' ? 'inactive' : 'active';
    
    // API call
    // await fetch(`/api/users/${userId}/status`, {
    //   method: 'PATCH',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify({ status: newStatus })
    // });
    
    // Update local state
    users.value = users.value.map(user => {
      if (user.id === userId) {
        return { ...user, status: newStatus };
      }
      return user;
    });
  } catch (error: any) {
    console.error('Error updating user status:', error);
    alert(error.message || t('dashboard.admin.updateStatusFailed'));
  }
};

/**
 * Delete product using DELETE /products/{id} endpoint
 */
const deleteProduct = async (productId: string) => {
  if (confirm(t('dashboard.admin.confirmDeleteProduct'))) {
    try {
      // API call using the DELETE /products/{id} endpoint provided
      const response = await fetch(`/api/products/${productId}`, { 
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authStore.accessToken || ''}`
        }
      });
      
      if (!response.ok) {
        throw new Error(`API request failed with status ${response.status}`);
      }
      
      // Remove from local state after successful API call
      products.value = products.value.filter(product => product.id !== productId);
      
      alert(t('dashboard.admin.productDeleted'));
    } catch (error: any) {
      console.error('Error deleting product:', error);
      alert(error.message || t('dashboard.admin.deleteProductFailed'));
    }
  }
};

/**
 * Delete category
 */
const deleteCategory = async (categoryId: string) => {
  if (confirm(t('dashboard.admin.confirmDeleteCategory'))) {
    try {
      // API call
      // await fetch(`/api/categories/${categoryId}`, { method: 'DELETE' });
      
      // Remove from local state
      categories.value = categories.value.filter(category => category.id !== categoryId);
      
      alert(t('dashboard.admin.categoryDeleted'));
    } catch (error: any) {
      console.error('Error deleting category:', error);
      alert(error.message || t('dashboard.admin.deleteCategoryFailed'));
    }
  }
};

/**
 * Create new category
 */
const createCategory = async () => {
  // Reset form errors
  errors.value.categoryForm = { name: '', description: '' };
  
  // Validate form
  if (!categoryForm.value.name.trim()) {
    errors.value.categoryForm.name = t('validation.required');
    return;
  }
  
  try {
    // API call
    // const response = await fetch('/api/categories', {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify({
    //     name: categoryForm.value.name.trim(),
    //     description: categoryForm.value.description.trim()
    //   })
    // });
    // const newCategory = await response.json();
    
    // Mock API response
    const newCategory = {
      id: Date.now().toString(),
      name: categoryForm.value.name.trim(),
      description: categoryForm.value.description.trim(),
      createdAt: new Date().toISOString()
    };
    
    // Add to local state
    categories.value.push(newCategory);
    
    // Reset form
    categoryForm.value = {
      name: '',
      description: ''
    };
    
    alert(t('dashboard.admin.categoryCreated'));
  } catch (error: any) {
    console.error('Error creating category:', error);
    alert(error.message || t('dashboard.admin.createCategoryFailed'));
  }
};

/**
 * Load data on component mount
 */
onMounted(() => {
  fetchUsers();
  fetchProducts();
  fetchCategories();
});
</script>

<template>
  <div class="admin-dashboard">
    <header class="dashboard-header">
      <h1>{{ t('dashboard.admin.title') }}</h1>
      <p>{{ t('dashboard.welcome') }}, Admin</p>
    </header>
    
    <!-- Dashboard Navigation -->
    <nav class="dashboard-nav">
      <ul class="tab-list">
        <li 
          v-for="tab in tabs" 
          :key="tab.id" 
          :class="{ active: activeTab === tab.id }"
          @click="activeTab = tab.id"
        >
          {{ tab.label }}
        </li>
      </ul>
    </nav>
    
    <!-- Dashboard Content -->
    <main class="dashboard-content">
      <!-- Users Management Tab -->
      <section v-if="activeTab === 'users'" class="tab-content">
        <div class="section-header">
          <h2>{{ t('dashboard.admin.manageUsers') }}</h2>
          <div class="search-filters">
            <div class="search-box">
              <input
                type="text"
                v-model="searchQueries.users"
                :placeholder="t('dashboard.admin.searchUsers')"
              />
            </div>
            <div class="filter-sort">
              <select v-model="userRoleFilter">
                <option value="all">{{ t('dashboard.admin.allRoles') }}</option>
                <option value="FARMER">{{ t('roles.farmer') }}</option>
                <option value="CONSUMER">{{ t('roles.consumer') }}</option>
                <option value="ADMIN">{{ t('roles.admin') }}</option>
              </select>
              <select v-model="userSortBy">
                <option value="name">{{ t('dashboard.admin.sortByName') }}</option>
                <option value="email">{{ t('dashboard.admin.sortByEmail') }}</option>
                <option value="date">{{ t('dashboard.admin.sortByDate') }}</option>
              </select>
              <button class="sort-dir-btn" @click="toggleSortDirection">
                {{ sortDirection === 'asc' ? '↑' : '↓' }}
              </button>
            </div>
          </div>
        </div>
        
        <div class="data-container">
          <div v-if="isLoading.users" class="loading">
            {{ t('loading') }}
          </div>
          <div v-else-if="errors.users" class="error-message">
            {{ errors.users }}
            <button @click="fetchUsers">{{ t('retry') }}</button>
          </div>
          <div v-else-if="filteredUsers.length === 0" class="empty-state">
            {{ t('dashboard.admin.noUsersFound') }}
          </div>
          <div v-else class="data-table users-table">
            <table>
              <thead>
                <tr>
                  <th>{{ t('dashboard.admin.userName') }}</th>
                  <th>{{ t('dashboard.admin.email') }}</th>
                  <th>{{ t('dashboard.admin.role') }}</th>
                  <th>{{ t('dashboard.admin.registrationDate') }}</th>
                  <th>{{ t('dashboard.admin.status') }}</th>
                  <th>{{ t('dashboard.admin.actions') }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in filteredUsers" :key="user.id">
                  <td>{{ user.name }}</td>
                  <td>{{ user.email }}</td>
                  <td>{{ t(`roles.${user.role.toLowerCase()}`) }}</td>
                  <td>{{ formatDate(user.createdAt) }}</td>
                  <td>
                    <span 
                      class="status-badge"
                      :class="user.status"
                    >
                      {{ t(`status.${user.status}`) }}
                    </span>
                  </td>
                  <td class="actions">
                    <button 
                      class="btn-icon toggle-status"
                      @click="toggleUserStatus(user.id, user.status)"
                      :title="user.status === 'active' ? t('dashboard.admin.deactivate') : t('dashboard.admin.activate')"
                    >
                      {{ user.status === 'active' ? '🔒' : '🔓' }}
                    </button>
                    <button 
                      class="btn-icon delete"
                      @click="deleteUser(user.id)"
                      :title="t('dashboard.admin.delete')"
                    >
                      🗑️
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>
      
      <!-- Products Management Tab -->
      <section v-else-if="activeTab === 'products'" class="tab-content">
        <div class="section-header">
          <h2>{{ t('dashboard.admin.manageProducts') }}</h2>
          <div class="search-filters">
            <div class="search-box">
              <input
                type="text"
                v-model="searchQueries.products"
                :placeholder="t('dashboard.admin.searchProducts')"
              />
            </div>
            <div class="filter-sort">
              <select v-model="productCategoryFilter">
                <option value="all">{{ t('dashboard.admin.allCategories') }}</option>
                <option 
                  v-for="category in categories" 
                  :key="category.id" 
                  :value="category.id"
                >
                  {{ category.name }}
                </option>
              </select>
              <select v-model="productSortBy">
                <option value="name">{{ t('dashboard.admin.sortByName') }}</option>
                <option value="price">{{ t('dashboard.admin.sortByPrice') }}</option>
                <option value="date">{{ t('dashboard.admin.sortByDate') }}</option>
              </select>
              <button class="sort-dir-btn" @click="toggleSortDirection">
                {{ sortDirection === 'asc' ? '↑' : '↓' }}
              </button>
            </div>
          </div>
        </div>
        
        <div class="data-container">
          <div v-if="isLoading.products" class="loading">
            {{ t('loading') }}
          </div>
          <div v-else-if="errors.products" class="error-message">
            {{ errors.products }}
            <button @click="fetchProducts">{{ t('retry') }}</button>
          </div>
          <div v-else-if="filteredProducts.length === 0" class="empty-state">
            {{ t('dashboard.admin.noProductsFound') }}
          </div>
          <div v-else class="data-table products-table">
            <table>
              <thead>
                <tr>
                  <th>{{ t('dashboard.admin.productName') }}</th>
                  <th>{{ t('dashboard.admin.farmer') }}</th>
                  <th>{{ t('dashboard.admin.price') }}</th>
                  <th>{{ t('dashboard.admin.category') }}</th>
                  <th>{{ t('dashboard.admin.organic') }}</th>
                  <th>{{ t('dashboard.admin.inStock') }}</th>
                  <th>{{ t('dashboard.admin.actions') }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="product in filteredProducts" :key="product.id">
                  <td class="product-info">
                    <div class="product-image" v-if="product.thumbnailUrl">
                      <img :src="product.thumbnailUrl" :alt="product.name" />
                    </div>
                    <div class="product-details">
                      <h4>{{ product.name }}</h4>
                      <p class="truncate-text">{{ product.description }}</p>
                    </div>
                  </td>
                  <td>{{ product.farmerName }}</td>
                  <td>€{{ product.price }} / {{ product.unit }}</td>
                  <td>
                    <span 
                      v-for="category in product.categories" 
                      :key="category.id"
                      class="category-tag"
                    >
                      {{ category.name }}
                    </span>
                  </td>
                  <td>{{ product.isOrganic ? '✅' : '❌' }}</td>
                  <td>{{ product.isInStock ? '✅' : '❌' }}</td>
                  <td class="actions">
                    <button 
                      class="btn-icon view"
                      @click="$router.push(`/products/${product.id}`)"
                      :title="t('dashboard.admin.view')"
                    >
                      👁️
                    </button>
                    <button 
                      class="btn-icon delete"
                      @click="deleteProduct(product.id)"
                      :title="t('dashboard.admin.delete')"
                    >
                      🗑️
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>
      
      <!-- Categories Management Tab -->
      <section v-else-if="activeTab === 'categories'" class="tab-content">
        <div class="section-header">
          <h2>{{ t('dashboard.admin.manageCategories') }}</h2>
          <div class="search-filters">
            <div class="search-box">
              <input
                type="text"
                v-model="searchQueries.categories"
                :placeholder="t('dashboard.admin.searchCategories')"
              />
            </div>
            <div class="filter-sort">
              <select v-model="categorySortBy">
                <option value="name">{{ t('dashboard.admin.sortByName') }}</option>
                <option value="date">{{ t('dashboard.admin.sortByDate') }}</option>
              </select>
              <button class="sort-dir-btn" @click="toggleSortDirection">
                {{ sortDirection === 'asc' ? '↑' : '↓' }}
              </button>
            </div>
          </div>
        </div>
        
        <!-- Category Creation Form -->
        <div class="form-container">
          <h3>{{ t('dashboard.admin.createCategory') }}</h3>
          <form @submit.prevent="createCategory" class="category-form">
            <div class="form-group">
              <label for="category-name">{{ t('dashboard.admin.categoryName') }} *</label>
              <input 
                id="category-name" 
                type="text" 
                v-model="categoryForm.name"
                :class="{ error: errors.categoryForm.name }"
              />
              <span v-if="errors.categoryForm.name" class="field-error">
                {{ errors.categoryForm.name }}
              </span>
            </div>
            
            <div class="form-group">
              <label for="category-description">{{ t('dashboard.admin.description') }}</label>
              <textarea 
                id="category-description" 
                v-model="categoryForm.description"
                rows="3"
              ></textarea>
            </div>
            
            <div class="form-actions">
              <button type="submit" class="btn primary">
                {{ t('dashboard.admin.addCategory') }}
              </button>
            </div>
          </form>
        </div>
        
        <!-- Categories List -->
        <div class="data-container">
          <div v-if="isLoading.categories" class="loading">
            {{ t('common.loading') }}
          </div>
          <div v-else-if="errors.categories" class="error-message">
            {{ errors.categories }}
            <button @click="fetchCategories">{{ t('common.retry') }}</button>
          </div>
          <div v-else-if="filteredCategories.length === 0" class="empty-state">
            {{ t('dashboard.admin.noCategoriesFound') }}
          </div>
          <div v-else class="data-table categories-table">
            <table>
              <thead>
                <tr>
                  <th>{{ t('dashboard.admin.categoryName') }}</th>
                  <th>{{ t('dashboard.admin.description') }}</th>
                  <th>{{ t('dashboard.admin.createdAt') }}</th>
                  <th>{{ t('dashboard.admin.actions') }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="category in filteredCategories" :key="category.id">
                  <td>{{ category.name }}</td>
                  <td>{{ category.description }}</td>
                  <td>{{ formatDate(category.createdAt) }}</td>
                  <td class="actions">
                    <button 
                      class="btn-icon delete"
                      @click="deleteCategory(category.id)"
                      :title="t('dashboard.admin.delete')"
                    >
                      🗑️
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>
      
      <!-- Statistics Tab -->
      <section v-else-if="activeTab === 'statistics'" class="tab-content">
        <div class="statistics-section">
          <h2>{{ t('dashboard.admin.platformStatistics') }}</h2>
          
          <div class="statistics-grid">
            <div class="statistic-card">
              <h3>{{ t('dashboard.admin.totalUsers') }}</h3>
              <div class="statistic-value">{{ users.length }}</div>
              <p>{{ t('dashboard.admin.registeredUsers') }}</p>
            </div>
            
            <div class="statistic-card">
              <h3>{{ t('dashboard.admin.totalProducts') }}</h3>
              <div class="statistic-value">{{ products.length }}</div>
              <p>{{ t('dashboard.admin.listedProducts') }}</p>
            </div>
            
            <div class="statistic-card">
              <h3>{{ t('dashboard.admin.totalCategories') }}</h3>
              <div class="statistic-value">{{ categories.length }}</div>
              <p>{{ t('dashboard.admin.productCategories') }}</p>
            </div>
            
            <div class="statistic-card">
              <h3>{{ t('dashboard.admin.activeFarmers') }}</h3>
              <div class="statistic-value">
                {{ users.filter(user => user.role === 'FARMER' && user.status === 'active').length }}
              </div>
              <p>{{ t('dashboard.admin.farmersOnPlatform') }}</p>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
.admin-dashboard {
  padding: 2rem 1rem;
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard-header {
  margin-bottom: 2rem;
}

.dashboard-header h1 {
  font-size: 2rem;
  margin-bottom: 0.5rem;
  color: #2c3e50;
}

.dashboard-nav {
  margin-bottom: 2rem;
  border-bottom: 1px solid #e0e0e0;
}

.tab-list {
  display: flex;
  list-style: none;
  padding: 0;
  margin: 0;
  gap: 1rem;
}

.tab-list li {
  padding: 0.75rem 1.5rem;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.3s ease;
}

.tab-list li.active {
  border-bottom-color: hsla(160, 100%, 37%, 1);
  color: hsla(160, 100%, 37%, 1);
}

.tab-list li:hover {
  background-color: hsla(160, 100%, 37%, 0.1);
}

.tab-content {
  min-height: 400px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.section-header h2 {
  font-size: 1.5rem;
  margin: 0;
}

.search-filters {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.search-box input {
  padding: 0.75rem;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
  min-width: 250px;
}

.filter-sort {
  display: flex;
  gap: 0.5rem;
}

.filter-sort select {
  padding: 0.75rem;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
}

.sort-dir-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f9f9f9;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1.2rem;
}

.data-container {
  margin-top: 2rem;
}

.loading, 
.error-message,
.empty-state {
  padding: 2rem;
  text-align: center;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.error-message {
  color: #e53935;
}

.data-table {
  width: 100%;
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

.status-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
}

.status-badge.active {
  background-color: #e8f5e9;
  color: #43a047;
}

.status-badge.inactive {
  background-color: #ffebee;
  color: #e53935;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.btn-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.2s ease;
}

.btn-icon.edit:hover {
  background-color: #e3f2fd;
  border-color: #90caf9;
}

.btn-icon.delete:hover {
  background-color: #ffebee;
  border-color: #ef9a9a;
}

.btn-icon.view:hover {
  background-color: #e8f5e9;
  border-color: #a5d6a7;
}

.btn-icon.toggle-status:hover {
  background-color: #f3e5f5;
  border-color: #ce93d8;
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

.product-details h4 {
  margin: 0 0 0.5rem;
}

.truncate-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 300px;
  margin: 0;
  font-size: 0.9rem;
  color: #666;
}

.category-tag {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  background-color: #f1f1f1;
  border-radius: 4px;
  font-size: 0.85rem;
  margin: 0 0.25rem 0.25rem 0;
}

.form-container {
  background-color: #f9f9f9;
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.form-container h3 {
  margin-top: 0;
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
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

.form-group input.error,
.form-group textarea.error,
.form-group select.error {
  border-color: #e53935;
}

.field-error {
  color: #e53935;
  font-size: 0.85rem;
  margin-top: 0.25rem;
  display: block;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
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
  background-color: #f1f1f1;
  color: #333;
}

.statistics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 1.5rem;
  margin-top: 2rem;
}

.statistic-card {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.statistic-card h3 {
  font-size: 1.1rem;
  margin-bottom: 1rem;
  color: #666;
}

.statistic-value {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
  color: hsla(160, 100%, 37%, 1);
}

@media (max-width: 768px) {
  .tab-list {
    flex-wrap: wrap;
  }
  
  .tab-list li {
    flex-grow: 1;
    text-align: center;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-filters {
    width: 100%;
    flex-direction: column;
  }
  
  .search-box {
    width: 100%;
  }
  
  .search-box input {
    width: 100%;
  }
  
  .filter-sort {
    width: 100%;
    justify-content: space-between;
  }
  
  .filter-sort select {
    flex-grow: 1;
  }
  
  .statistics-grid {
    grid-template-columns: 1fr;
  }
  
  .form-actions {
    justify-content: center;
  }
  
  .btn {
    width: 100%;
  }
}
</style>
