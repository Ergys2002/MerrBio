<script setup lang="ts">
/**
 * ProductSearch component provides search and filter functionality for products
 * @component
 */
import { ref, watch } from 'vue';

interface FilterOptions {
  search: string;
  category: string;
  minPrice: number | null;
  maxPrice: number | null;
  organic: boolean | null;
}

interface EmitEvents {
  (e: 'update:filters', filters: FilterOptions): void;
}

const emit = defineEmits<EmitEvents>();

// Category options
const categories = [
  { value: '', label: 'All Categories' },
  { value: 'vegetables', label: 'Vegetables' },
  { value: 'fruits', label: 'Fruits' },
  { value: 'dairy', label: 'Dairy Products' },
  { value: 'meat', label: 'Meat & Poultry' },
  { value: 'eggs', label: 'Eggs' },
  { value: 'honey', label: 'Honey & Bee Products' },
  { value: 'herbs', label: 'Herbs & Spices' }
];

// Filter state
const filters = ref<FilterOptions>({
  search: '',
  category: '',
  minPrice: null,
  maxPrice: null,
  organic: null
});

// Price range options
const priceRanges = [
  { value: 'all', label: 'Any Price', min: null, max: null },
  { value: 'under5', label: 'Under 5€', min: 0, max: 5 },
  { value: '5to10', label: '5€ to 10€', min: 5, max: 10 },
  { value: '10to20', label: '10€ to 20€', min: 10, max: 20 },
  { value: 'over20', label: 'Over 20€', min: 20, max: null }
];

const selectedPriceRange = ref('all');

// Watch for changes in price range selection
watch(selectedPriceRange, (newValue) => {
  const range = priceRanges.find(r => r.value === newValue);
  if (range) {
    filters.value.minPrice = range.min;
    filters.value.maxPrice = range.max;
    updateFilters();
  }
});

// Watch for changes in any filter and emit the update
watch(filters, () => {
  updateFilters();
}, { deep: true });

// Reset all filters
const resetFilters = () => {
  filters.value = {
    search: '',
    category: '',
    minPrice: null,
    maxPrice: null,
    organic: null
  };
  selectedPriceRange.value = 'all';
};

// Emit filter updates
const updateFilters = () => {
  emit('update:filters', { ...filters.value });
};

// Handle search input with debounce
let debounceTimeout: NodeJS.Timeout | null = null;
const handleSearchInput = (event: Event) => {
  const target = event.target as HTMLInputElement;
  
  if (debounceTimeout) {
    clearTimeout(debounceTimeout);
  }
  
  debounceTimeout = setTimeout(() => {
    filters.value.search = target.value;
    updateFilters();
  }, 300);
};
</script>

<template>
  <div class="product-search">
    <!-- Search Bar -->
    <div class="search-bar">
      <input
        type="text"
        placeholder="Search products..."
        :value="filters.search"
        @input="handleSearchInput"
      />
    </div>
    
    <div class="filters-section">
      <h3>Filters</h3>
      
      <!-- Category Filter -->
      <div class="filter-group">
        <label for="category">Category</label>
        <select 
          id="category"
          v-model="filters.category"
        >
          <option 
            v-for="category in categories" 
            :key="category.value" 
            :value="category.value"
          >
            {{ category.label }}
          </option>
        </select>
      </div>
      
      <!-- Price Range Filter -->
      <div class="filter-group">
        <label for="price-range">Price Range</label>
        <select 
          id="price-range"
          v-model="selectedPriceRange"
        >
          <option 
            v-for="range in priceRanges" 
            :key="range.value" 
            :value="range.value"
          >
            {{ range.label }}
          </option>
        </select>
      </div>
      
      <!-- Organic Filter -->
      <div class="filter-group checkbox-group">
        <label class="checkbox-container">
          <input 
            type="checkbox"
            v-model="filters.organic"
            :indeterminate="filters.organic === null"
            @click="filters.organic = filters.organic === true ? null : true"
          />
          <span class="checkbox-label">Organic Products Only</span>
        </label>
      </div>
      
      <!-- Reset Filters Button -->
      <button class="reset-button" @click="resetFilters">
        Reset Filters
      </button>
    </div>
  </div>
</template>

<style scoped>
.product-search {
  margin-bottom: 24px;
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-bar {
  margin-bottom: 16px;
}

.search-bar input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.search-bar input:focus {
  outline: none;
  border-color: #4caf50;
}

.filters-section h3 {
  margin: 0 0 16px 0;
  font-size: 1.1rem;
  color: #333;
  font-weight: 600;
}

.filter-group {
  margin-bottom: 16px;
}

.filter-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-size: 0.9rem;
}

.filter-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background-color: #fff;
  font-size: 0.95rem;
  transition: border-color 0.3s;
}

.filter-group select:focus {
  outline: none;
  border-color: #4caf50;
}

.checkbox-group {
  display: flex;
  align-items: center;
}

.checkbox-container {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.checkbox-container input {
  margin-right: 8px;
  cursor: pointer;
}

.checkbox-label {
  font-size: 0.95rem;
  color: #555;
}

.reset-button {
  width: 100%;
  padding: 10px;
  background-color: #f5f5f5;
  color: #555;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-top: 8px;
}

.reset-button:hover {
  background-color: #e8e8e8;
}

@media (min-width: 768px) {
  .filters-section {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }
  
  .filters-section h3 {
    grid-column: span 2;
  }
  
  .reset-button {
    grid-column: span 2;
  }
}
</style>
