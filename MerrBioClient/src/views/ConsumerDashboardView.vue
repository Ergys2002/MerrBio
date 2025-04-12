<script setup lang="ts">
/**
 * ConsumerDashboardView - Main dashboard view for consumers
 * @component
 */
import { ref } from 'vue';
import { useAuthStore } from '../stores/authStore';
import { useI18n } from 'vue-i18n';
import MyRequests from '../components/dashboard/consumer/MyRequests.vue';
import FavoriteFarmers from '../components/dashboard/consumer/FavoriteFarmers.vue';
import PurchaseHistory from '../components/dashboard/consumer/PurchaseHistory.vue';

// Get translations
const { t } = useI18n();

// Get auth store for user data
const authStore = useAuthStore();

// Tabs for navigation
const activeTab = ref('requests');
const tabs = [
  { id: 'requests', label: t('dashboard.consumer.myRequests') },
  { id: 'favorites', label: t('dashboard.consumer.favoriteFarmers') },
  { id: 'history', label: t('dashboard.consumer.purchaseHistory') },
  { id: 'profile', label: t('profile.title') }
];

// Mock consumer data (to be replaced with real data from authStore)
const consumer = {
  id: 201,
  name: 'Maria Consumer',
  email: 'maria@example.com',
  address: 'Rruga Myslym Shyri, Tirana, Albania',
  phone: '+355 69 123 4567',
  pendingRequests: 3,
  completedPurchases: 12
};
</script>

<template>
  <div class="consumer-dashboard">
    <header class="dashboard-header">
      <h1>{{ t('dashboard.consumer.title') }}</h1>
      <p>{{ t('dashboard.welcome') }}, {{ consumer.name }}</p>
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
      <!-- My Requests Tab -->
      <section v-if="activeTab === 'requests'" class="tab-content">
        <MyRequests />
      </section>
      
      <!-- Favorite Farmers Tab -->
      <section v-else-if="activeTab === 'favorites'" class="tab-content">
        <FavoriteFarmers />
      </section>
      
      <!-- Purchase History Tab -->
      <section v-else-if="activeTab === 'history'" class="tab-content">
        <PurchaseHistory />
      </section>
      
      <!-- Profile Tab -->
      <section v-else-if="activeTab === 'profile'" class="tab-content">
        <div class="profile-section">
          <h2>Profile Information</h2>
          <form class="profile-form">
            <div class="form-group">
              <label for="consumer-name">Full Name</label>
              <input id="consumer-name" type="text" v-model="consumer.name" />
            </div>
            
            <div class="form-group">
              <label for="consumer-email">Email</label>
              <input id="consumer-email" type="email" v-model="consumer.email" />
            </div>
            
            <div class="form-group">
              <label for="consumer-address">Delivery Address</label>
              <textarea id="consumer-address" v-model="consumer.address" rows="2"></textarea>
            </div>
            
            <div class="form-group">
              <label for="consumer-phone">Phone Number</label>
              <input id="consumer-phone" type="tel" v-model="consumer.phone" />
            </div>
            
            <div class="form-actions">
              <button type="submit" class="btn primary">Save Changes</button>
            </div>
          </form>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
.consumer-dashboard {
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

@media (max-width: 768px) {
  .tab-list {
    flex-wrap: wrap;
  }
  
  .tab-list li {
    flex-grow: 1;
    text-align: center;
  }
}
</style>
