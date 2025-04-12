<script setup lang="ts">
/**
 * OrderRequests - Component for farmers to manage incoming purchase requests
 * @component
 */
import { ref, computed } from 'vue';

// Order type definition
interface Order {
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
const sortBy = ref('date');
const sortDirection = ref('desc');
const searchQuery = ref('');

// Mock orders data (to be replaced with API data)
const orders = ref<Order[]>([
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
    productId: 1,
    productName: "Organic Tomatoes",
    quantity: 3,
    unit: "kg",
    price: 3.99,
    totalPrice: 11.97,
    status: "accepted",
    requestDate: "2025-04-09T10:15:00",
    consumer: {
      id: 202,
      name: "John Buyer"
    },
    farmer: {
      id: 101,
      name: "Green Valley Farm"
    }
  },
  {
    id: 3,
    productId: 2,
    productName: "Green Apples",
    quantity: 10,
    unit: "kg",
    price: 2.49,
    totalPrice: 24.90,
    status: "rejected",
    requestDate: "2025-04-08T16:45:00",
    consumer: {
      id: 203,
      name: "Ana Smith"
    },
    farmer: {
      id: 101,
      name: "Green Valley Farm"
    },
    message: "Do you have these available for next week's market?"
  },
  {
    id: 4,
    productId: 3,
    productName: "Farm Fresh Eggs",
    quantity: 2,
    unit: "dozen",
    price: 4.99,
    totalPrice: 9.98,
    status: "completed",
    requestDate: "2025-04-07T09:30:00",
    consumer: {
      id: 201,
      name: "Maria Consumer"
    },
    farmer: {
      id: 101,
      name: "Green Valley Farm"
    }
  },
  {
    id: 5,
    productId: 1,
    productName: "Organic Tomatoes",
    quantity: 2,
    unit: "kg",
    price: 3.99,
    totalPrice: 7.98,
    status: "pending",
    requestDate: "2025-04-11T11:20:00",
    consumer: {
      id: 204,
      name: "Peter Johnson"
    },
    farmer: {
      id: 101,
      name: "Green Valley Farm"
    },
    message: "Are these suitable for sauce making?"
  }
]);

// Filter options
const filterOptions = [
  { value: 'all', label: 'All Orders' },
  { value: 'pending', label: 'Pending' },
  { value: 'accepted', label: 'Accepted' },
  { value: 'rejected', label: 'Rejected' },
  { value: 'completed', label: 'Completed' }
];

// Sort options
const sortOptions = [
  { value: 'date', label: 'Request Date' },
  { value: 'price', label: 'Total Price' },
  { value: 'product', label: 'Product Name' },
  { value: 'customer', label: 'Customer Name' }
];

/**
 * Handle order action (accept or reject)
 */
const handleOrderAction = (order: Order, action: 'accept' | 'reject' | 'complete') => {
  const orderIndex = orders.value.findIndex(o => o.id === order.id);
  if (orderIndex !== -1) {
    // In a real app, would make API call before updating local state
    isLoading.value = true;
    
    setTimeout(() => {
      if (action === 'accept') {
        orders.value[orderIndex].status = 'accepted';
      } else if (action === 'reject') {
        orders.value[orderIndex].status = 'rejected';
      } else if (action === 'complete') {
        orders.value[orderIndex].status = 'completed';
      }
      isLoading.value = false;
    }, 500); // Simulated delay
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
 * Filter and sort orders based on current settings
 */
const filteredAndSortedOrders = computed(() => {
  // Filter by status
  let result = orders.value.filter(order => {
    if (activeFilter.value === 'all') return true;
    return order.status === activeFilter.value;
  });
  
  // Filter by search query
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(
      order => order.productName.toLowerCase().includes(query) ||
               order.consumer.name.toLowerCase().includes(query) ||
               (order.message && order.message.toLowerCase().includes(query))
    );
  }
  
  // Sort results
  return result.sort((a, b) => {
    let comparison = 0;
    
    if (sortBy.value === 'date') {
      comparison = new Date(a.requestDate).getTime() - new Date(b.requestDate).getTime();
    } else if (sortBy.value === 'price') {
      comparison = a.totalPrice - b.totalPrice;
    } else if (sortBy.value === 'product') {
      comparison = a.productName.localeCompare(b.productName);
    } else if (sortBy.value === 'customer') {
      comparison = a.consumer.name.localeCompare(b.consumer.name);
    }
    
    return sortDirection.value === 'desc' ? -comparison : comparison;
  });
});

/**
 * Toggle sort direction
 */
const toggleSortDirection = () => {
  sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc';
};

/**
 * Get background color based on order status
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
</script>

<template>
  <div class="order-requests">
    <div class="header-actions">
      <h2>Order Requests</h2>
      <div class="actions">
        <div class="search-box">
          <input 
            type="text" 
            v-model="searchQuery" 
            placeholder="Search orders..." 
          />
        </div>
      </div>
    </div>
    
    <!-- Filters and Sort -->
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
      
      <div class="sort">
        <label for="sort-select">Sort by:</label>
        <select id="sort-select" v-model="sortBy">
          <option v-for="option in sortOptions" :key="option.value" :value="option.value">
            {{ option.label }}
          </option>
        </select>
        <button class="sort-dir-btn" @click="toggleSortDirection">
          {{ sortDirection === 'asc' ? '↑' : '↓' }}
        </button>
      </div>
    </div>
    
    <!-- Orders List -->
    <div v-if="filteredAndSortedOrders.length" class="orders-list">
      <div 
        v-for="order in filteredAndSortedOrders" 
        :key="order.id"
        class="order-card"
      >
        <div class="order-header">
          <div class="order-id">Order #{{ order.id }}</div>
          <div 
            class="order-status"
            :style="{ backgroundColor: getStatusColor(order.status) }"
          >
            {{ order.status.charAt(0).toUpperCase() + order.status.slice(1) }}
          </div>
        </div>
        
        <div class="order-details">
          <div class="order-info">
            <div class="info-row">
              <span class="label">Product:</span>
              <span class="value">{{ order.productName }}</span>
            </div>
            <div class="info-row">
              <span class="label">Quantity:</span>
              <span class="value">{{ order.quantity }} {{ order.unit }}</span>
            </div>
            <div class="info-row">
              <span class="label">Price:</span>
              <span class="value">€{{ order.price }} / {{ order.unit }}</span>
            </div>
            <div class="info-row">
              <span class="label">Total:</span>
              <span class="value total-price">€{{ order.totalPrice.toFixed(2) }}</span>
            </div>
          </div>
          
          <div class="consumer-info">
            <div class="info-row">
              <span class="label">Customer:</span>
              <span class="value">{{ order.consumer.name }}</span>
            </div>
            <div class="info-row">
              <span class="label">Requested:</span>
              <span class="value">{{ formatDate(order.requestDate) }}</span>
            </div>
            <div class="info-row message" v-if="order.message">
              <span class="label">Message:</span>
              <span class="value">{{ order.message }}</span>
            </div>
          </div>
        </div>
        
        <div class="order-actions" v-if="order.status === 'pending'">
          <button 
            class="btn reject"
            @click="handleOrderAction(order, 'reject')"
            :disabled="isLoading"
          >
            Reject
          </button>
          <button 
            class="btn accept"
            @click="handleOrderAction(order, 'accept')"
            :disabled="isLoading"
          >
            Accept
          </button>
        </div>
        
        <div class="order-actions" v-else-if="order.status === 'accepted'">
          <button 
            class="btn complete"
            @click="handleOrderAction(order, 'complete')"
            :disabled="isLoading"
          >
            Mark as Completed
          </button>
        </div>
      </div>
    </div>
    
    <!-- Empty state -->
    <div v-else class="empty-state">
      <p v-if="searchQuery || activeFilter !== 'all'">No orders match your filters.</p>
      <p v-else>You don't have any order requests yet.</p>
    </div>
  </div>
</template>

<style scoped>
.order-requests {
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
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

.sort {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.sort select {
  padding: 0.5rem;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
}

.sort-dir-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f9f9f9;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
  cursor: pointer;
}

.orders-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.order-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  background-color: white;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background-color: #f9f9f9;
  border-bottom: 1px solid #e0e0e0;
}

.order-id {
  font-weight: 600;
}

.order-status {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  color: white;
  font-size: 0.8rem;
  font-weight: 500;
}

.order-details {
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  flex-grow: 1;
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

.order-actions {
  display: flex;
  padding: 1rem;
  gap: 0.5rem;
  border-top: 1px solid #e0e0e0;
}

.btn {
  flex: 1;
  padding: 0.75rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn.reject {
  background-color: #fff1f0;
  color: #f5222d;
  border: 1px solid #ffa39e;
}

.btn.reject:hover {
  background-color: #fff1f0;
}

.btn.accept {
  background-color: hsla(160, 100%, 37%, 1);
  color: white;
}

.btn.accept:hover {
  background-color: hsla(160, 90%, 30%, 1);
}

.btn.complete {
  background-color: #e6f7ff;
  color: #1890ff;
  border: 1px solid #91d5ff;
}

.btn.complete:hover {
  background-color: #bae7ff;
}

.empty-state {
  text-align: center;
  padding: 3rem 1rem;
  background-color: #f9f9f9;
  border-radius: 8px;
  color: #666;
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
  
  .filters-container {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .orders-list {
    grid-template-columns: 1fr;
  }
}
</style>
