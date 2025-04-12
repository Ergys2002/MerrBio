<script setup lang="ts">
/**
 * PurchaseHistory - Component for consumers to view their completed purchases
 * @component
 */
import { ref, computed } from 'vue';

// Purchase type definition
interface Purchase {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  unit: string;
  price: number;
  totalPrice: number;
  purchaseDate: string;
  deliveryDate: string;
  consumer: {
    id: number;
    name: string;
  };
  farmer: {
    id: number;
    name: string;
  };
}

// UI state
const searchQuery = ref('');
const sortBy = ref('date');
const sortDirection = ref('desc');

// Date range filter
const startDate = ref('');
const endDate = ref('');

// Mock purchase history data (to be replaced with API data)
const purchases = ref<Purchase[]>([
  {
    id: 101,
    productId: 12,
    productName: "Free-Range Chicken",
    quantity: 1,
    unit: "piece",
    price: 8.99,
    totalPrice: 8.99,
    purchaseDate: "2025-04-07T09:30:00",
    deliveryDate: "2025-04-08T14:30:00",
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
    id: 102,
    productId: 5,
    productName: "Organic Carrots",
    quantity: 3,
    unit: "kg",
    price: 2.49,
    totalPrice: 7.47,
    purchaseDate: "2025-03-25T10:15:00",
    deliveryDate: "2025-03-26T16:45:00",
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
    id: 103,
    productId: 8,
    productName: "Fresh Honey",
    quantity: 1,
    unit: "jar",
    price: 6.99,
    totalPrice: 6.99,
    purchaseDate: "2025-03-18T11:30:00",
    deliveryDate: "2025-03-20T13:20:00",
    consumer: {
      id: 201,
      name: "Maria Consumer"
    },
    farmer: {
      id: 103,
      name: "Mountain Apiaries"
    }
  },
  {
    id: 104,
    productId: 3,
    productName: "Organic Apples",
    quantity: 4,
    unit: "kg",
    price: 4.99,
    totalPrice: 19.96,
    purchaseDate: "2025-03-10T14:45:00",
    deliveryDate: "2025-03-12T11:15:00",
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
    id: 105,
    productId: 15,
    productName: "Artisan Cheese",
    quantity: 2,
    unit: "piece",
    price: 7.50,
    totalPrice: 15.00,
    purchaseDate: "2025-02-28T09:00:00",
    deliveryDate: "2025-03-01T15:30:00",
    consumer: {
      id: 201,
      name: "Maria Consumer"
    },
    farmer: {
      id: 105,
      name: "Dairy Delights"
    }
  }
]);

// Sort options
const sortOptions = [
  { value: 'date', label: 'Purchase Date' },
  { value: 'price', label: 'Total Price' },
  { value: 'product', label: 'Product Name' },
  { value: 'farmer', label: 'Farmer Name' }
];

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
 * Calculate total spent on all purchases
 */
const totalSpent = computed((): number => {
  return filteredPurchases.value.reduce((sum, purchase) => sum + purchase.totalPrice, 0);
});

/**
 * Filter and sort purchases based on current settings
 */
const filteredPurchases = computed(() => {
  // Filter by search query
  let result = purchases.value;
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(
      purchase => purchase.productName.toLowerCase().includes(query) ||
                 purchase.farmer.name.toLowerCase().includes(query)
    );
  }
  
  // Filter by date range
  if (startDate.value) {
    const start = new Date(startDate.value).getTime();
    result = result.filter(purchase => new Date(purchase.purchaseDate).getTime() >= start);
  }
  
  if (endDate.value) {
    const end = new Date(endDate.value).getTime();
    result = result.filter(purchase => new Date(purchase.purchaseDate).getTime() <= end);
  }
  
  // Sort results
  return result.sort((a, b) => {
    let comparison = 0;
    
    if (sortBy.value === 'date') {
      comparison = new Date(a.purchaseDate).getTime() - new Date(b.purchaseDate).getTime();
    } else if (sortBy.value === 'price') {
      comparison = a.totalPrice - b.totalPrice;
    } else if (sortBy.value === 'product') {
      comparison = a.productName.localeCompare(b.productName);
    } else if (sortBy.value === 'farmer') {
      comparison = a.farmer.name.localeCompare(b.farmer.name);
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
 * Reset date filters
 */
const resetDateFilters = () => {
  startDate.value = '';
  endDate.value = '';
};

/**
 * View purchase details or receipt (would navigate to detail view)
 */
const viewPurchaseDetails = (purchaseId: number) => {
  console.log(`View details for purchase #${purchaseId}`);
  // Would use router to navigate to purchase detail view
};

/**
 * Reorder the same products
 */
const reorderProduct = (purchase: Purchase) => {
  console.log(`Reorder product ${purchase.productName} from ${purchase.farmer.name}`);
  // Would navigate to product page or create new request
  alert(`Creating new request for ${purchase.productName} from ${purchase.farmer.name}`);
};
</script>

<template>
  <div class="purchase-history">
    <div class="header-actions">
      <h2>Purchase History</h2>
      <div class="search-box">
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="Search purchases..." 
        />
      </div>
    </div>
    
    <!-- Filters and Sort -->
    <div class="filters-container">
      <div class="date-filters">
        <div class="date-range">
          <label for="start-date">From:</label>
          <input id="start-date" type="date" v-model="startDate" />
        </div>
        <div class="date-range">
          <label for="end-date">To:</label>
          <input id="end-date" type="date" v-model="endDate" />
        </div>
        <button 
          v-if="startDate || endDate" 
          class="btn-text" 
          @click="resetDateFilters"
        >
          Reset dates
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
    
    <!-- Total Spent Summary -->
    <div class="total-summary">
      <div class="summary-card">
        <div class="summary-label">Total Spent</div>
        <div class="summary-value">€{{ totalSpent.toFixed(2) }}</div>
        <div class="summary-details">Across {{ filteredPurchases.length }} purchases</div>
      </div>
    </div>
    
    <!-- Purchase List -->
    <div v-if="filteredPurchases.length" class="purchase-list">
      <table>
        <thead>
          <tr>
            <th>Product</th>
            <th>Quantity</th>
            <th>Price</th>
            <th>Farmer</th>
            <th>Date</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="purchase in filteredPurchases" :key="purchase.id">
            <td>{{ purchase.productName }}</td>
            <td>{{ purchase.quantity }} {{ purchase.unit }}</td>
            <td class="price-cell">€{{ purchase.totalPrice.toFixed(2) }}</td>
            <td>{{ purchase.farmer.name }}</td>
            <td>{{ formatDate(purchase.purchaseDate) }}</td>
            <td class="actions-cell">
              <button 
                class="btn-icon details" 
                @click="viewPurchaseDetails(purchase.id)"
                title="View purchase details"
              >
                📋
              </button>
              <button 
                class="btn-icon reorder" 
                @click="reorderProduct(purchase)"
                title="Reorder this product"
              >
                🔄
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- Empty state -->
    <div v-else class="empty-state">
      <p v-if="searchQuery || startDate || endDate">No purchases match your filters.</p>
      <p v-else>You haven't made any purchases yet.</p>
      <button class="btn primary">Browse Products</button>
    </div>
  </div>
</template>

<style scoped>
.purchase-history {
  width: 100%;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
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

.date-filters {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.date-range input {
  padding: 0.5rem;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
}

.btn-text {
  background: none;
  border: none;
  color: hsla(160, 100%, 37%, 1);
  cursor: pointer;
  font-size: 0.9rem;
  padding: 0;
  text-decoration: underline;
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

.total-summary {
  margin-bottom: 2rem;
}

.summary-card {
  background-color: hsla(160, 100%, 37%, 0.1);
  border: 1px solid hsla(160, 100%, 37%, 0.2);
  border-radius: 8px;
  padding: 1.5rem;
  text-align: center;
}

.summary-label {
  font-size: 0.9rem;
  color: #666;
}

.summary-value {
  font-size: 2.2rem;
  font-weight: 600;
  color: hsla(160, 100%, 37%, 1);
  margin: 0.5rem 0;
}

.summary-details {
  font-size: 0.9rem;
  color: #666;
}

.purchase-list {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  background-color: #f9f9f9;
  text-align: left;
  padding: 1rem;
  font-weight: 600;
  border-bottom: 1px solid #e0e0e0;
}

td {
  padding: 1rem;
  border-bottom: 1px solid #e0e0e0;
  vertical-align: middle;
}

.price-cell {
  font-weight: 500;
  color: hsla(160, 100%, 37%, 1);
}

.actions-cell {
  display: flex;
  gap: 0.5rem;
}

.btn-icon {
  width: 32px;
  height: 32px;
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

.btn-icon:hover {
  background-color: #f0f0f0;
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

.btn.primary {
  background-color: hsla(160, 100%, 37%, 1);
  color: white;
  display: inline-block;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

@media (max-width: 768px) {
  .header-actions {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-box {
    width: 100%;
  }
  
  .search-box input {
    width: 100%;
  }
  
  .filters-container {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .date-filters {
    width: 100%;
  }
  
  .date-range {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .date-range input {
    width: 100%;
  }
  
  .sort {
    width: 100%;
    margin-top: 1rem;
  }
  
  .sort select {
    flex-grow: 1;
  }
  
  th:nth-child(2),
  td:nth-child(2),
  th:nth-child(4),
  td:nth-child(4) {
    display: none;
  }
}
</style>
