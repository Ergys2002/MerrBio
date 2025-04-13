<script setup lang="ts">
/**
 * ConsumerDashboardView - Main dashboard view for consumers
 * @component
 */
import { ref, computed } from 'vue'; // Combined imports
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

// Get consumer data from auth store and create an editable form
const initialConsumerData = () => {
  if (!authStore.user) return null;
  return {
    id: authStore.user.id,
    firstName: authStore.user.firstName,
    lastName: authStore.user.lastName,
    fullName: `${authStore.user.firstName} ${authStore.user.lastName}`,
    email: authStore.user.email,
    address: authStore.user.address || '',
    phoneNumber: authStore.user.phoneNumber || '',
    gender: authStore.user.gender || '',
    birthDate: formatDate(authStore.user.birthDate),
    pendingRequests: 3, // This would come from requests store in a real implementation
    completedPurchases: 12 // This would come from purchase history in a real implementation
  };
};

// Create reactive consumer data for the form
const consumerData = ref(initialConsumerData());
// Create a computed property to access the data safely
const consumer = computed(() => consumerData.value);

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
  address: '',
});

// Toggle edit mode for the profile
const toggleEditMode = () => {
  if (isEditingProfile.value) {
    // Reset form data when canceling edit
    consumerData.value = initialConsumerData();
    profileErrors.value = {
      firstName: '',
      lastName: '',
      email: '',
      phoneNumber: '',
      address: '',
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
    address: '',
  };

  if (!consumerData.value?.firstName?.trim()) {
    profileErrors.value.firstName = t('validation.required');
    isValid = false;
  }

  if (!consumerData.value?.lastName?.trim()) {
    profileErrors.value.lastName = t('validation.required');
    isValid = false;
  }

  if (!consumerData.value?.email?.trim()) {
    profileErrors.value.email = t('validation.required');
    isValid = false;
  } else if (!/^\S+@\S+\.\S+$/.test(consumerData.value.email)) {
    profileErrors.value.email = t('validation.invalidEmail');
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
    //   firstName: consumerData.value.firstName,
    //   lastName: consumerData.value.lastName,
    //   ...etc
    // });

    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    // Update the auth store with new data
    // This is just a placeholder, you would update with the response from the API
    // Update the auth store, ensuring values are not undefined
    if (authStore.user && consumerData.value) {
        authStore.user = {
          ...authStore.user,
          // Provide default empty strings if values are null/undefined
          firstName: consumerData.value.firstName || '',
          lastName: consumerData.value.lastName || '',
          email: consumerData.value.email || '',
          phoneNumber: consumerData.value.phoneNumber || '',
          // Assuming address might not exist on user type, keep the original handling or adjust based on actual type
          address: consumerData.value.address || ''
        };
    }

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

<template>  <div class="consumer-dashboard">
    <header class="dashboard-header" v-if="consumer">
      <h1>{{ t('dashboard.consumer.title') }}</h1>
      <p>{{ t('dashboard.welcome') }}, {{ consumer.firstName }}</p>
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
      <section v-else-if="activeTab === 'profile' && consumer" class="tab-content">
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
              <!-- Consumer Personal Information -->
              <div class="info-card">
                <h3>Personal Information</h3>
                
                <!-- First Name -->
                <div class="form-group">
                  <label for="firstName">First Name<span class="required">*</span></label>
                  <div v-if="isEditingProfile" class="input-group">
                    <input 
                      id="firstName" 
                      v-model="consumerData!.firstName"
                      type="text" 
                      :disabled="!isEditingProfile || isSavingProfile"
                      :class="{ 'error': profileErrors.firstName }"
                    />
                    <span class="error-message" v-if="profileErrors.firstName">{{ profileErrors.firstName }}</span>
                  </div>
                  <span v-else class="info-value">{{ consumer.firstName }}</span>
                </div>
                
                <!-- Last Name -->
                <div class="form-group">
                  <label for="lastName">Last Name<span class="required">*</span></label>
                  <div v-if="isEditingProfile" class="input-group">
                    <input 
                      id="lastName" 
                      v-model="consumerData!.lastName"
                      type="text" 
                      :disabled="!isEditingProfile || isSavingProfile"
                      :class="{ 'error': profileErrors.lastName }"
                    />
                    <span class="error-message" v-if="profileErrors.lastName">{{ profileErrors.lastName }}</span>
                  </div>
                  <span v-else class="info-value">{{ consumer.lastName }}</span>
                </div>
                
                <!-- Email -->
                <div class="form-group">
                  <label for="email">Email<span class="required">*</span></label>
                  <div v-if="isEditingProfile" class="input-group">
                    <input 
                      id="email" 
                      v-model="consumerData!.email"
                      type="email" 
                      :disabled="!isEditingProfile || isSavingProfile"
                      :class="{ 'error': profileErrors.email }"
                    />
                    <span class="error-message" v-if="profileErrors.email">{{ profileErrors.email }}</span>
                  </div>
                  <span v-else class="info-value">{{ consumer.email }}</span>
                </div>
                
                <!-- Gender (Read-only) -->
                <div class="form-group">
                  <label>Gender</label>
                  <span class="info-value">{{ consumer.gender || 'Not specified' }}</span>
                </div>
                
                <!-- Birth Date (Read-only) -->
                <div class="form-group">
                  <label>Birth Date</label>
                  <span class="info-value">{{ consumer.birthDate || 'Not provided' }}</span>
                </div>
              </div>
              
              <!-- Contact Information -->
              <div class="info-card">
                <h3>Contact Information</h3>
                
                <!-- Phone Number -->
                <div class="form-group">
                  <label for="phoneNumber">Phone Number</label>
                  <div v-if="isEditingProfile" class="input-group">
                    <input 
                      id="phoneNumber" 
                      v-model="consumerData!.phoneNumber"
                      type="tel" 
                      :disabled="!isEditingProfile || isSavingProfile"
                    />
                  </div>
                  <span v-else class="info-value">{{ consumer.phoneNumber || 'Not provided' }}</span>
                </div>
                
                <!-- Delivery Address -->
                <div class="form-group">
                  <label for="address">Delivery Address</label>
                  <div v-if="isEditingProfile" class="input-group">
                    <textarea 
                      id="address" 
                      v-model="consumerData!.address"
                      rows="3" 
                      :disabled="!isEditingProfile || isSavingProfile"
                    ></textarea>
                  </div>
                  <p v-else class="info-value">{{ consumer.address || 'No delivery address provided.' }}</p>
                </div>

                <!-- Account Statistics (Read-only) -->
                <div class="account-stats">
                  <h4>Account Statistics</h4>
                  <div class="stats-grid">
                    <div class="stat-item">
                      <div class="stat-label">Pending Requests</div>
                      <div class="stat-value">{{ consumer.pendingRequests }}</div>
                    </div>
                    <div class="stat-item">
                      <div class="stat-label">Completed Purchases</div>
                      <div class="stat-value">{{ consumer.completedPurchases }}</div>
                    </div>
                  </div>
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
    </main>
  </div>
</template>

<style scoped>
/* Base Styles */
.consumer-dashboard {
  padding: 2rem; /* Increased padding */
  max-width: 1200px;
  margin: 2rem auto; /* Added top/bottom margin */
  background-color: #f9fafb; /* Light background */
  border-radius: 8px; /* Rounded corners for the whole container */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); /* Subtle shadow */
}

/* Header */
.dashboard-header {
  margin-bottom: 2.5rem; /* Increased spacing */
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #e5e7eb; /* Lighter border */
}

.dashboard-header h1 {
  font-size: 2.25rem; /* Larger heading */
  font-weight: 600; /* Slightly bolder */
  margin-bottom: 0.5rem;
  color: #1f2937; /* Darker gray */
}
.dashboard-header p {
  font-size: 1.1rem;
  color: #6b7280; /* Medium gray */
}

/* Navigation Tabs */
.dashboard-nav {
  margin-bottom: 2.5rem;
}

.tab-list {
  display: flex;
  list-style: none;
  padding: 0;
  margin: 0;
  gap: 0.5rem; /* Reduced gap for tighter tabs */
  border-bottom: 1px solid #e5e7eb;
}

.tab-list li {
  padding: 0.8rem 1.5rem;
  cursor: pointer;
  border-bottom: 3px solid transparent; /* Thicker border for active state */
  transition: all 0.2s ease-in-out;
  color: #6b7280; /* Default tab color */
  font-weight: 500;
  margin-bottom: -1px; /* Align bottom border with container border */
}

.tab-list li.active {
  border-bottom-color: hsla(160, 100%, 37%, 1);
  color: hsla(160, 100%, 37%, 1); /* Keep active color */
  font-weight: 600;
}

.tab-list li:hover:not(.active) {
  background-color: hsla(160, 100%, 37%, 0.05); /* Lighter hover */
  color: #374151; /* Darker gray on hover */
}

/* Content Area */
.tab-content {
  min-height: 400px;
  padding-top: 1rem; /* Add some space above content */
}

/* Profile Section Specifics */
.profile-section .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e5e7eb;
}

.profile-section h2 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1f2937;
}

.profile-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); /* Responsive grid */
  gap: 2rem; /* Gap between cards */
  margin-bottom: 2rem;
}

.info-card {
  background-color: #ffffff; /* White background for cards */
  border: 1px solid #e5e7eb; /* Light border */
  border-radius: 8px; /* Rounded corners */
  padding: 1.5rem; /* Padding inside cards */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.03); /* Subtle shadow */
}

.info-card h3 {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 1.5rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #f3f4f6; /* Separator line */
}

/* Form Styling */
.form-group {
  margin-bottom: 1.25rem; /* Slightly reduced margin */
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #374151; /* Darker label color */
  font-size: 0.9rem; /* Slightly smaller label */
}

.form-group .required {
  color: #ef4444; /* Red for required asterisk */
  margin-left: 0.25rem;
}

.input-group {
  position: relative;
}

.form-group input[type="text"],
.form-group input[type="email"],
.form-group input[type="tel"],
.form-group textarea {
  width: 100%;
  padding: 0.75rem 1rem; /* Adjusted padding */
  border: 1px solid #d1d5db; /* Slightly darker border */
  border-radius: 6px; /* More rounded */
  font-size: 1rem;
  color: #1f2937;
  background-color: #f9fafb; /* Light input background */
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: hsla(160, 100%, 37%, 0.8);
  box-shadow: 0 0 0 3px hsla(160, 100%, 37%, 0.2); /* Focus ring */
}

.form-group input:disabled,
.form-group textarea:disabled {
  background-color: #e5e7eb; /* Disabled background */
  cursor: not-allowed;
  opacity: 0.7;
}

.form-group textarea {
  min-height: 80px; /* Ensure textarea has some height */
  resize: vertical; /* Allow vertical resize */
}

.info-value {
  display: block;
  padding: 0.75rem 0; /* Align with input padding */
  color: #4b5563; /* Slightly lighter text for non-edit values */
  font-size: 1rem;
  min-height: 40px; /* Ensure consistent height with inputs */
}

p.info-value { /* Specific styling for paragraph info values like address */
  line-height: 1.5;
  min-height: 80px; /* Match textarea height */
}

.error-message {
  color: #ef4444;
  font-size: 0.875rem;
  margin-top: 0.25rem;
  display: block;
}

input.error {
  border-color: #ef4444;
}
input.error:focus {
   box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.2);
}

/* Account Stats */
.account-stats {
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #f3f4f6;
}

.account-stats h4 {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #374151;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.stat-item {
  background-color: #f3f4f6; /* Light gray background for stats */
  padding: 0.75rem 1rem;
  border-radius: 6px;
  text-align: center;
}

.stat-label {
  font-size: 0.85rem;
  color: #6b7280;
  margin-bottom: 0.25rem;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1f2937;
}


/* Buttons */
.form-actions {
  margin-top: 2.5rem;
  display: flex;
  justify-content: flex-end; /* Align buttons to the right */
  gap: 1rem; /* Space between buttons */
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 6px; /* Consistent rounding */
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500; /* Medium weight */
  transition: all 0.2s ease;
  display: inline-flex; /* Align icon and text if needed */
  align-items: center;
  justify-content: center;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn.primary {
  background-color: hsla(160, 100%, 37%, 1);
  color: white;
}
.btn.primary:hover:not(:disabled) {
  background-color: hsla(160, 100%, 30%, 1); /* Darker shade on hover */
}

.btn.secondary {
  background-color: #e5e7eb; /* Light gray */
  color: #374151; /* Dark text */
  border: 1px solid #d1d5db; /* Subtle border */
}
.btn.secondary:hover:not(:disabled) {
  background-color: #d1d5db; /* Darker gray on hover */
}

.edit-btn { /* Specific styling for the top edit button */
  background-color: transparent;
  color: hsla(160, 100%, 37%, 1);
  border: 1px solid hsla(160, 100%, 37%, 0.5);
  padding: 0.5rem 1rem; /* Smaller padding */
}
.edit-btn:hover:not(:disabled) {
  background-color: hsla(160, 100%, 37%, 0.1);
}

/* Loading States */
.loading-message, .loading-profile {
  text-align: center;
  padding: 3rem;
  color: #6b7280;
  font-size: 1.1rem;
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .consumer-dashboard {
    padding: 1rem;
    margin: 1rem auto;
  }

  .dashboard-header h1 {
    font-size: 1.8rem;
  }
  .dashboard-header p {
    font-size: 1rem;
  }

  .tab-list {
    gap: 0.25rem;
    flex-wrap: wrap; /* Allow tabs to wrap */
  }
  .tab-list li {
    padding: 0.75rem 1rem;
    font-size: 0.9rem;
    flex-grow: 1; /* Make tabs fill width when wrapped */
    text-align: center;
  }

  .profile-info {
    grid-template-columns: 1fr; /* Stack cards vertically */
    gap: 1.5rem;
  }

  .form-actions {
    flex-direction: column; /* Stack buttons vertically */
  }
  .form-actions .btn {
    width: 100%; /* Make buttons full width */
  }
}

@media (max-width: 480px) {
  .dashboard-header h1 {
    font-size: 1.6rem;
  }
  .stats-grid {
    grid-template-columns: 1fr; /* Stack stats vertically */
  }
}
</style>

