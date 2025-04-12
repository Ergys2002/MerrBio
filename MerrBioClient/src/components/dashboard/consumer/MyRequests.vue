<script setup lang="ts">
/**
 * MyRequests - Component for consumers to track their purchase requests
 * @component
 */
import { ref, computed } from 'vue';

// Request type definition
interface Request {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  unit: string;
  price: number;
  totalPrice: number;
  status: 'pending' | 'accepted' | 'rejected' | 'completed';
  requestDate: string;
  consumer: {
    id: number;
    name: string;
  };
  farmer: {
    id: number;
    name: string;
  };
  message?: string;
}

// UI state
const isLoading = ref(false);
const activeFilter = ref('all');
const searchQuery = ref('');

// Mock requests data (to be replaced with API data)
const requests = ref<Request[]>([
  {
    id: 1,
    productId: 3,
    productName: "Organic Apples",
    quantity: 5,
    unit: "kg",
    price: 4.99,
    totalPrice: 24.95,
    status: "pending",
    requestDate: "2025-04-10T14:30:00",
    consumer: {
      id: 201,
      name: "Maria Consumer"
    },
    farmer: {
      id: 101,
      name: "Green Valley Farm"
    },
    message: "Please deliver to my address on Saturday morning."
  },
  {
    id: 2,
    productId: 5,
    productName: "Organic Carrots",
    quantity: 2,
    unit: "kg",
    price: 2.49,
    totalPrice: 4.98,
    status: "accepted",
    requestDate: "2025-04-09T10:15:00",
    consumer: {
      id: 201,
      name: "Maria Consumer"
    },
    farmer: {
      id: 102,
      name: "Sunny Fields"
    }
  },
  {
    id: 3,
    productId: 8,
    productName: "Fresh Honey",
    quantity: 2,
    unit: "jar",
    price: 6.99,
    totalPrice: 13.98,
    status: "rejected",
    requestDate: "2025-04-08T16:45:00",
    consumer: {
      id: 201,
      name: "Maria Consumer"
    },
    farmer: {
      id: 103,
      name: "Mountain Apiaries"
    },
    message: "Is this raw unfiltered honey?"
  },
  {
    id: 4,
    productId: 12,
    productName: "Free-Range Chicken",
    quantity: 1,
    unit: "piece",
    price: 8.99,
    totalPrice: 8.99,
    status: "completed",
    requestDate: "2025-04-07T09:30:00",
    consumer: {
      id: 201,
      name: "Maria Consumer"
    },
    farmer: {
      id: 104,
      name: "Happy Poultry Farm"
    }
  },
  {
    id: 5,
    productId: 7,
    productName: "Organic Potatoes",
    quantity: 3,
    unit: "kg",
    price: 1.99,
    totalPrice: 5.97,
    status: "pending",
    requestDate: "2025-04-11T11:20:00",
    consumer: {
      id: 201,
      name: "Maria Consumer"
    },
    farmer: {
      id: 101,
      name: "Green Valley Farm"
    }
  }
]);

// Filter options
const filterOptions = [
  { value: 'all', label: 'All Requests' },
  { value: 'pending', label: 'Pending' },
  { value: 'accepted', label: 'Accepted' },
  { value: 'rejected', label: 'Rejected' },
  { value: 'completed', label: 'Completed' }
];

/**
 * Handle cancel request action
 */
const cancelRequest = (request: Request) => {
  if (request.status !== 'pending') {
    alert('Only pending requests can be cancelled.');
    return;
  }

  if (confirm('Are you sure you want to cancel this request?')) {
    const requestIndex = requests.value.findIndex(r => r.id === request.id);
    if (requestIndex !== -1) {
      // In a real app, would make API call before updating local state
      isLoading.value = true;
      
      setTimeout(() => {
        requests.value.splice(requestIndex, 1);
        isLoading.value = false;
      }, 500); // Simulated delay
    }
  }
};

/**
 * Format date for display
 */
const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return new Intl.DateTimeFormat('en-GB', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
};

/**
 * Filter requests based on current settings
 */
const filteredRequests = computed(() => {
  // Filter by status
  let result = requests.value.filter(request => {
    if (activeFilter.value === 'all') return true;
    return request.status === activeFilter.value;
  });
  
  // Filter by search query
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(
      request => request.productName.toLowerCase().includes(query) ||
                request.farmer.name.toLowerCase().includes(query) ||
                (request.message && request.message.toLowerCase().includes(query))
    );
  }
  
  // Sort by date (newest first)
  return result.sort((a, b) => {
    return new Date(b.requestDate).getTime() - new Date(a.requestDate).getTime();
  });
});

/**
 * Get status icon based on request status
 */
const getStatusIcon = (status: string): string => {
  switch (status) {
    case 'pending': return '⏳';
    case 'accepted': return '✅';
    case 'rejected': return '❌';
    case 'completed': return '🏁';
    default: return '❓';
  }
};

/**
 * Get background color based on request status
 */
const getStatusColor = (status: string): string => {
  switch (status) {
    case 'pending': return 'var(--status-pending, #faad14)';
    case 'accepted': return 'var(--status-accepted, #52c41a)';
    case 'rejected': return 'var(--status-rejected, #f5222d)';
    case 'completed': return 'var(--status-completed, #1890ff)';
    default: return '#999';
  }
};

/**
 * Get readable text for request status
 */
const getStatusText = (status: string): string => {
  switch (status) {
    case 'pending': return 'Waiting for farmer response';
    case 'accepted': return 'Request accepted! Awaiting delivery';
    case 'rejected': return 'Request declined by farmer';
    case 'completed': return 'Purchase completed';
    default: return 'Unknown status';
  }
};
</script>

<template>
  <div class="my-requests">
    <div class="header-actions">
      <h2>My Purchase Requests</h2>
      <div class="actions">
        <div class="search-box">
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="Search requests..." 
          />
        </div>
      </div>
    </div>
    
    <!-- Filters -->
    <div class="filters-container">
      <div class="filters">
        <button 
          v-for="option in filterOptions" 
          :key="option.value"
          :class="['filter-btn', { active: activeFilter === option.value }]"
          @click="activeFilter = option.value"
        >
          {{ option.label }}
        </button>
      </div>
    </div>
    
    <!-- Requests List -->
    <div v-if="filteredRequests.length" class="requests-list">
      <div 
        v-for="request in filteredRequests" 
        :key="request.id"
        class="request-card"
      >
        <div class="request-header">
          <div class="request-id">Request #{{ request.id }}</div>
          <div 
            class="request-status"
            :style="{ backgroundColor: getStatusColor(request.status) }"
          >
            {{ getStatusIcon(request.status) }} {{ request.status.charAt(0).toUpperCase() + request.status.slice(1) }}
          </div>
        </div>
        
        <div class="request-details">
          <div class="product-info">
            <h3>{{ request.productName }}</h3>
            <p class="farmer-name">from {{ request.farmer.name }}</p>
            
            <div class="info-row">
              <span class="label">Quantity:</span>
              <span class="value">{{ request.quantity }} {{ request.unit }}</span>
            </div>
            <div class="info-row">
              <span class="label">Price:</span>
              <span class="value">€{{ request.price }} / {{ request.unit }}</span>
            </div>
            <div class="info-row">
              <span class="label">Total:</span>
              <span class="value total-price">€{{ request.totalPrice.toFixed(2) }}</span>
            </div>
          </div>
          
          <div class="status-info">
            <div class="info-row">
              <span class="label">Requested:</span>
              <span class="value">{{ formatDate(request.requestDate) }}</span>
            </div>
            <div class="info-row">
              <span class="label">Status:</span>
              <span class="value status-text">{{ getStatusText(request.status) }}</span>
            </div>
            <div class="info-row message" v-if="request.message">
              <span class="label">Your message:</span>
              <span class="value">{{ request.message }}</span>
            </div>
          </div>
        </div>
        
        <!-- Actions for pending requests -->
        <div class="request-actions" v-if="request.status === 'pending'">
          <button 
            class="btn cancel"
            @click="cancelRequest(request)"
            :disabled="isLoading"
          >
            Cancel Request
          </button>
        </div>
        
        <!-- Actions for accepted requests -->
        <div class="request-actions info-message" v-else-if="request.status === 'accepted'">
          <p>Contact the farmer for delivery details</p>
        </div>
        
        <!-- Actions for rejected requests -->
        <div class="request-actions info-message rejected" v-else-if="request.status === 'rejected'">
          <p>Try contacting the farmer for more information</p>
        </div>
      </div>
    </div>
    
    <!-- Empty state -->
    <div v-else class="empty-state">
      <p v-if="searchQuery || activeFilter !== 'all'">No requests match your filters.</p>
      <p v-else>You haven't made any purchase requests yet.</p>
      <button class="btn primary">Browse Products</button>
    </div>
  </div>
</template>

<style scoped>
.my-requests {
  width: 100%;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.search-box input {
  padding: 0.75rem;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
  width: 250px;
}

.filters-container {
  margin-bottom: 1.5rem;
}

.filters {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 0.5rem 1rem;
  background: none;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.filter-btn.active {
  background-color: hsla(160, 100%, 37%, 1);
  color: white;
  border-color: hsla(160, 100%, 37%, 1);
}

.requests-list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
}

.request-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  background-color: white;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.request-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background-color: #f9f9f9;
  border-bottom: 1px solid #e0e0e0;
}

.request-id {
  font-weight: 600;
}

.request-status {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  color: white;
  font-size: 0.8rem;
  font-weight: 500;
}

.request-details {
  padding: 1rem;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.product-info h3 {
  margin-top: 0;
  margin-bottom: 0.25rem;
}

.farmer-name {
  color: #666;
  margin-top: 0;
  margin-bottom: 1rem;
  font-style: italic;
}

.info-row {
  display: flex;
  margin-bottom: 0.5rem;
}

.label {
  font-weight: 500;
  width: 80px;
  color: #666;
}

.value {
  flex-grow: 1;
}

.total-price {
  font-weight: 600;
  color: hsla(160, 100%, 37%, 1);
}

.status-text {
  font-style: italic;
}

.message {
  display: flex;
  flex-direction: column;
}

.message .label {
  margin-bottom: 0.25rem;
}

.message .value {
  background-color: #f9f9f9;
  padding: 0.5rem;
  border-radius: 4px;
  font-style: italic;
}

.request-actions {
  padding: 1rem;
  border-top: 1px solid #e0e0e0;
}

.info-message {
  text-align: center;
  font-style: italic;
  color: #1890ff;
  background-color: #e6f7ff;
}

.info-message.rejected {
  color: #f5222d;
  background-color: #fff1f0;
}

.btn {
  display: block;
  width: 100%;
  padding: 0.75rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  text-align: center;
}

.btn.cancel {
  background-color: #fff1f0;
  color: #f5222d;
  border: 1px solid #ffa39e;
}

.btn.cancel:hover {
  background-color: #ffccc7;
}

.btn.primary {
  background-color: hsla(160, 100%, 37%, 1);
  color: white;
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

.empty-state .btn {
  max-width: 200px;
  margin: 0 auto;
}

@media (max-width: 768px) {
  .header-actions {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .search-box input {
    width: 100%;
  }
  
  .filters {
    width: 100%;
  }
  
  .filter-btn {
    flex-grow: 1;
    text-align: center;
  }
  
  .request-details {
    grid-template-columns: 1fr;
  }
}
</style>
