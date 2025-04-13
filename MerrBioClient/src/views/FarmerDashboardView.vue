<script setup lang="ts">
/**
 * FarmerDashboardView - Main dashboard view for farmers
 * @component
 */
import { ref, computed } from 'vue';
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

// Format date for display
const formatDate = (dateString: string | undefined): string => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('en-GB', {
    day: '2-digit',
    month: 'short',
    year: 'numeric'
  }).format(date);
};

// Get farmer data from auth store and create an editable form
const initialFarmerData = () => {
  if (!authStore.user) return null;
  return {
    id: authStore.user.id,
    firstName: authStore.user.firstName,
    lastName: authStore.user.lastName,
    email: authStore.user.email,
    farmName: authStore.user.farmName || '',
    location: authStore.user.farmLocation || '',
    description: authStore.user.bio || '',
    isVerified: authStore.user.isVerified ? 'Verified' : 'Not Verified',
    gender: authStore.user.gender || '',
    birthDate: formatDate(authStore.user.birthDate),
    phoneNumber: authStore.user.phoneNumber || '',
    productsCount: 0, // This would come from products store in a real implementation
    pendingRequests: 0 // This would come from orders store in a real implementation
  };
};

// Create reactive farmer data for the form
const farmerData = ref(initialFarmerData());
// Create a computed property to access the data safely
const farmer = computed(() => farmerData.value);

// Track if the form is in edit mode
const isEditingProfile = ref(false);
// Track if the form is being saved
const isSavingProfile = ref(false);
// Track if there are validation errors
const profileErrors = ref({
  firstName: '',
  lastName: '',
  email: '',
  phoneNumber: '',
  farmName: '',
  location: '',
});

// Toggle edit mode for the profile
const toggleEditMode = () => {
  if (isEditingProfile.value) {
    // Reset form data when canceling edit
    farmerData.value = initialFarmerData();
    profileErrors.value = {
      firstName: '',
      lastName: '',
      email: '',
      phoneNumber: '',
      farmName: '',
      location: '',
    };
  }
  isEditingProfile.value = !isEditingProfile.value;
};

// Validate profile data
const validateProfile = () => {
  let isValid = true;
  profileErrors.value = {
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    farmName: '',
    location: '',
  };

  if (!farmerData.value?.firstName?.trim()) {
    profileErrors.value.firstName = t('validation.required');
    isValid = false;
  }

  if (!farmerData.value?.lastName?.trim()) {
    profileErrors.value.lastName = t('validation.required');
    isValid = false;
  }

  if (!farmerData.value?.email?.trim()) {
    profileErrors.value.email = t('validation.required');
    isValid = false;
  } else if (!/^\S+@\S+\.\S+$/.test(farmerData.value.email)) {
    profileErrors.value.email = t('validation.invalidEmail');
    isValid = false;
  }

  if (!farmerData.value?.farmName?.trim()) {
    profileErrors.value.farmName = t('validation.required');
    isValid = false;
  }

  return isValid;
};

// Save profile changes
const saveProfile = async () => {
  if (!validateProfile()) return;

  try {
    isSavingProfile.value = true;
    
    // Here you would make an API call to update the user profile
    // For example:
    // await api.updateProfile({
    //   firstName: farmerData.value.firstName,
    //   lastName: farmerData.value.lastName,
    //   ...etc
    // });

    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // Update the auth store with new data
    // This is just a placeholder, you would update with the response from the API
    authStore.user = {
      ...authStore.user,
      firstName: farmerData.value?.firstName,
      lastName: farmerData.value?.lastName,
      email: farmerData.value?.email,
      phoneNumber: farmerData.value?.phoneNumber,
      farmName: farmerData.value?.farmName,
      farmLocation: farmerData.value?.location,
      bio: farmerData.value?.description
    };

    // Exit edit mode
    isEditingProfile.value = false;
    alert(t('profile.updateSuccess'));
  } catch (error) {
    console.error('Error updating profile:', error);
    alert(t('errors.updateProfileFailed'));
  } finally {
    isSavingProfile.value = false;
  }
};
</script>

<template>  <div class="farmer-dashboard">
    <header class="dashboard-header" v-if="farmer">
      <h1>{{ t('dashboard.farmer.title') }}</h1>
      <p>{{ t('dashboard.welcome') }}, {{ farmer.firstName }}</p>
    </header>
    <div v-else class="loading-message">
      <p>{{ t('common.loading') }}</p>
    </div>
    
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
        <!-- Profile Tab -->      <section v-else-if="activeTab === 'profile' && farmer" class="tab-content">
        <div class="profile-section">
          <div class="section-header">
            <h2>Profile Information</h2>
            <button 
              v-if="!isEditingProfile" 
              @click="toggleEditMode" 
              class="btn edit-btn"
              title="Edit Profile"
            >
              Edit Profile
            </button>
          </div>

          <!-- Profile Form -->
          <form @submit.prevent="saveProfile" class="profile-form">
            <div class="profile-info">
              <!-- Farmer Information -->
              <div class="info-card">
                <h3>Farmer Information</h3>
                
                <!-- First Name -->
                <div class="form-group">
                  <label for="firstName">First Name<span class="required">*</span></label>
                  <div v-if="isEditingProfile" class="input-group">
                    <input 
                      id="firstName" 
                      v-model="farmerData.firstName" 
                      type="text" 
                      :disabled="!isEditingProfile || isSavingProfile"
                      :class="{ 'error': profileErrors.firstName }"
                    />
                    <span class="error-message" v-if="profileErrors.firstName">{{ profileErrors.firstName }}</span>
                  </div>
                  <span v-else class="info-value">{{ farmer.firstName }}</span>
                </div>
                
                <!-- Last Name -->
                <div class="form-group">
                  <label for="lastName">Last Name<span class="required">*</span></label>
                  <div v-if="isEditingProfile" class="input-group">
                    <input 
                      id="lastName" 
                      v-model="farmerData.lastName" 
                      type="text" 
                      :disabled="!isEditingProfile || isSavingProfile"
                      :class="{ 'error': profileErrors.lastName }"
                    />
                    <span class="error-message" v-if="profileErrors.lastName">{{ profileErrors.lastName }}</span>
                  </div>
                  <span v-else class="info-value">{{ farmer.lastName }}</span>
                </div>
                
                <!-- Email -->
                <div class="form-group">
                  <label for="email">Email<span class="required">*</span></label>
                  <div v-if="isEditingProfile" class="input-group">
                    <input 
                      id="email" 
                      v-model="farmerData.email" 
                      type="email" 
                      :disabled="!isEditingProfile || isSavingProfile"
                      :class="{ 'error': profileErrors.email }"
                    />
                    <span class="error-message" v-if="profileErrors.email">{{ profileErrors.email }}</span>
                  </div>
                  <span v-else class="info-value">{{ farmer.email }}</span>
                </div>
                
                <!-- Phone Number -->
                <div class="form-group">
                  <label for="phoneNumber">Phone Number</label>
                  <div v-if="isEditingProfile" class="input-group">
                    <input 
                      id="phoneNumber" 
                      v-model="farmerData.phoneNumber" 
                      type="tel" 
                      :disabled="!isEditingProfile || isSavingProfile"
                    />
                  </div>
                  <span v-else class="info-value">{{ farmer.phoneNumber || 'Not provided' }}</span>
                </div>
                
                <!-- Gender (Read-only) -->
                <div class="form-group">
                  <label>Gender</label>
                  <span class="info-value">{{ farmer.gender || 'Not specified' }}</span>
                </div>
                
                <!-- Birth Date (Read-only) -->
                <div class="form-group">
                  <label>Birth Date</label>
                  <span class="info-value">{{ farmer.birthDate || 'Not provided' }}</span>
                </div>
              </div>
              
              <!-- Farm Information -->
              <div class="info-card">
                <h3>Farm Information</h3>
                
                <!-- Farm Name -->
                <div class="form-group">
                  <label for="farmName">Farm Name<span class="required">*</span></label>
                  <div v-if="isEditingProfile" class="input-group">
                    <input 
                      id="farmName" 
                      v-model="farmerData.farmName" 
                      type="text" 
                      :disabled="!isEditingProfile || isSavingProfile"
                      :class="{ 'error': profileErrors.farmName }"
                    />
                    <span class="error-message" v-if="profileErrors.farmName">{{ profileErrors.farmName }}</span>
                  </div>
                  <span v-else class="info-value">{{ farmer.farmName || 'Not provided' }}</span>
                </div>
                
                <!-- Farm Location -->
                <div class="form-group">
                  <label for="location">Farm Location<span class="required">*</span></label>
                  <div v-if="isEditingProfile" class="input-group">
                    <input 
                      id="location" 
                      v-model="farmerData.location" 
                      type="text" 
                      :disabled="!isEditingProfile || isSavingProfile"
                      :class="{ 'error': profileErrors.location }"
                    />
                    <span class="error-message" v-if="profileErrors.location">{{ profileErrors.location }}</span>
                  </div>
                  <span v-else class="info-value">{{ farmer.location || 'Not provided' }}</span>
                </div>
                
                <!-- Status (Read-only) -->
                <div class="form-group">
                  <label>Status</label>
                  <span class="info-value" :class="{ 'verified': farmer.isVerified === 'Verified' }">{{ farmer.isVerified }}</span>
                </div>
                
                <!-- Bio -->
                <div class="form-group">
                  <label for="description">Bio</label>
                  <div v-if="isEditingProfile" class="input-group">
                    <textarea 
                      id="description" 
                      v-model="farmerData.description" 
                      rows="4" 
                      :disabled="!isEditingProfile || isSavingProfile"
                    ></textarea>
                  </div>
                  <p v-else class="info-value bio">{{ farmer.description || 'No bio provided.' }}</p>
                </div>
              </div>
            </div>
            
            <!-- Form Actions -->
            <div v-if="isEditingProfile" class="form-actions">
              <button 
                type="button" 
                @click="toggleEditMode" 
                class="btn secondary"
                :disabled="isSavingProfile"
              >
                Cancel
              </button>
              <button 
                type="submit" 
                class="btn primary"
                :disabled="isSavingProfile"
              >
                {{ isSavingProfile ? 'Saving...' : 'Save Changes' }}
              </button>
            </div>
          </form>
        </div>
      </section>
      <section v-else-if="activeTab === 'profile'" class="tab-content loading-profile">
        <p>{{ t('common.loading') }}...</p>
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

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.profile-info {
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
}

@media (min-width: 768px) {
  .profile-info {
    grid-template-columns: 1fr 1fr;
  }
}

.info-card {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  margin-bottom: 1rem;
}

.info-card h3 {
  font-size: 1.2rem;
  margin-bottom: 1.5rem;
  color: #2c3e50;
  border-bottom: 1px solid #e0e0e0;
  padding-bottom: 0.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  font-weight: 500;
  margin-bottom: 0.5rem;
  color: #333;
}

.required {
  color: #f56c6c;
  margin-left: 4px;
}

.info-value {
  display: block;
  padding: 0.75rem 0;
  color: #606060;
}

.verified {
  color: #67c23a;
  font-weight: bold;
}

.input-group {
  position: relative;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
  font-size: 1rem;
  transition: border 0.3s;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: hsla(160, 100%, 37%, 1);
}

.form-group input.error,
.form-group textarea.error {
  border-color: #f56c6c;
}

.error-message {
  color: #f56c6c;
  font-size: 0.85rem;
  margin-top: 0.25rem;
  display: block;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid #e0e0e0;
}

.bio {
  white-space: pre-line;
  line-height: 1.5;
}

.loading-profile {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
  color: #909399;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
  font-weight: 500;
}

.btn.primary {
  background-color: hsla(160, 100%, 37%, 1);
  color: white;
}

.btn.primary:hover {
  background-color: hsla(160, 100%, 30%, 1);
}

.btn.secondary {
  background-color: #f2f2f2;
  color: #606266;
}

.btn.secondary:hover {
  background-color: #e6e6e6;
}

.btn.edit-btn {
  background-color: #ecf5ff;
  color: #409eff;
  border: 1px solid #d9ecff;
}

.btn.edit-btn:hover {
  background-color: #d9ecff;
  border-color: #c6e2ff;
}

.btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
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
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
  }
}
</style>
