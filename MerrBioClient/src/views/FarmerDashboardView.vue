<script setup lang="ts">
/**
 * FarmerDashboardView - Main dashboard view for farmers
 * @component
 */
import { ref } from 'vue';
import { useAuthStore } from '../stores/authStore';
import { useI18n } from 'vue-i18n';
import ProductManagement from '../components/dashboard/farmer/ProductManagement.vue';
import OrderRequests from '../components/dashboard/farmer/OrderRequests.vue';

// Get translations
const { t } = useI18n();

// Get auth store for user data
const authStore = useAuthStore();

// Tabs for navigation
const activeTab = ref('products');
const tabs = [
  { id: 'products', label: t('dashboard.farmer.products') },
  { id: 'orders', label: t('dashboard.farmer.orders') },
  { id: 'profile', label: t('profile.title') },
  { id: 'statistics', label: t('dashboard.stats') }
];

// Mock farmer data (to be replaced with real data from authStore)
const farmer = {
  id: 101,
  name: 'Green Valley Farm',
  email: 'farm@greenvalley.com',
  location: 'Tirana, Albania',
  description: 'Organic farm specializing in fresh vegetables and fruits',
  productsCount: 15,
  pendingRequests: 7
};
</script>

<template>
  <div class="farmer-dashboard">
    <header class="dashboard-header">
      <h1>{{ t('dashboard.farmer.title') }}</h1>
      <p>{{ t('dashboard.welcome') }}, {{ farmer.name }}</p>
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
      <!-- Products Management Tab -->
      <section v-if="activeTab === 'products'" class="tab-content">
        <ProductManagement />
      </section>
      
      <!-- Order Requests Tab -->
      <section v-else-if="activeTab === 'orders'" class="tab-content">
        <OrderRequests />
      </section>
      
      <!-- Profile Tab -->
      <section v-else-if="activeTab === 'profile'" class="tab-content">
        <div class="profile-section">
          <h2>Profile Information</h2>
          <form class="profile-form">
            <div class="form-group">
              <label for="farm-name">Farm Name</label>
              <input id="farm-name" type="text" v-model="farmer.name" />
            </div>
            
            <div class="form-group">
              <label for="farm-email">Email</label>
              <input id="farm-email" type="email" v-model="farmer.email" />
            </div>
            
            <div class="form-group">
              <label for="farm-location">Location</label>
              <input id="farm-location" type="text" v-model="farmer.location" />
            </div>
            
            <div class="form-group">
              <label for="farm-description">Description</label>
              <textarea id="farm-description" v-model="farmer.description" rows="4"></textarea>
            </div>
            
            <div class="form-actions">
              <button type="submit" class="btn primary">Save Changes</button>
            </div>
          </form>
        </div>
      </section>
      
      <!-- Statistics Tab -->
      <section v-else-if="activeTab === 'statistics'" class="tab-content">
        <div class="statistics-section">
          <h2>Farm Statistics</h2>
          
          <div class="statistics-grid">
            <div class="statistic-card">
              <h3>Products</h3>
              <div class="statistic-value">{{ farmer.productsCount }}</div>
              <p>Total products listed</p>
            </div>
            
            <div class="statistic-card">
              <h3>Pending Requests</h3>
              <div class="statistic-value">{{ farmer.pendingRequests }}</div>
              <p>Waiting for response</p>
            </div>
            
            <div class="statistic-card">
              <h3>Sales</h3>
              <div class="statistic-value">€1,450</div>
              <p>This month</p>
            </div>
            
            <div class="statistic-card">
              <h3>Customers</h3>
              <div class="statistic-value">24</div>
              <p>Unique customers</p>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
.farmer-dashboard {
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

.profile-form {
  max-width: 600px;
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
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
  font-size: 1rem;
}

.form-actions {
  margin-top: 2rem;
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
  
  .statistics-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  }
}
</style>
