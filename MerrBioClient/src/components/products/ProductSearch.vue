<script setup lang="ts">
/**
 * ProductSearch provides search, filter, and sort controls for products
 * @component
 */
import { ref, watch } from 'vue'
import { productCategories } from '@/stores/productStore'
import type { FilterOptions, Product } from '@/stores/productStore'

interface SortOption {
  field: keyof Product | ''
  direction: 'asc' | 'desc'
}

interface EmitEvents {
  (e: 'update:filters', filters: FilterOptions): void;
  (e: 'update:sort', sort: SortOption): void;
}

const emit = defineEmits<EmitEvents>()

// State
const search = ref('')
const selectedCategories = ref<string[]>([])
const minPrice = ref<number | null>(null)
const maxPrice = ref<number | null>(null)
const isOrganic = ref<boolean | null>(null)
const inStock = ref<boolean | null>(null)
const sortField = ref<string>('')
const sortDirection = ref<'asc' | 'desc'>('asc')

// Sort options
const sortOptions = [
  { value: 'name', label: 'Name' },
  { value: 'price', label: 'Price' },
  { value: 'createdAt', label: 'Recently Added' }
]

// Price range presets
const priceRanges = [
  { min: null, max: null, label: 'Any Price' },
  { min: 0, max: 5, label: 'Under €5' },
  { min: 5, max: 10, label: '€5 to €10' },
  { min: 10, max: 20, label: '€10 to €20' },
  { min: 20, max: null, label: 'Over €20' }
]

// Active price range
const activePriceRange = ref(priceRanges[0])

// Update filters with debounce
let debounceTimeout: number | null = null
const updateFilters = () => {
  if (debounceTimeout) clearTimeout(debounceTimeout)
  
  debounceTimeout = window.setTimeout(() => {
  emit('update:filters', {
      search: search.value,
      categories: selectedCategories.value,
      minPrice: minPrice.value,
      maxPrice: maxPrice.value,
      isOrganic: isOrganic.value,
      sortBy: (sortField.value || 'latest') as FilterOptions['sortBy']
    })
  }, 300)
}

// Update sort
const updateSort = () => {
  if (!sortField.value) {
    sortDirection.value = 'asc'
  }
  
  emit('update:sort', {
    field: sortField.value as keyof Product | '',
    direction: sortDirection.value
  })
}

// Watch for changes
watch([search, selectedCategories, isOrganic, inStock], updateFilters)
watch([sortField, sortDirection], updateSort)

// Handle price range selection
const selectPriceRange = (range: typeof priceRanges[0]) => {
  activePriceRange.value = range
  minPrice.value = range.min
  maxPrice.value = range.max
  updateFilters()
}

// Reset all filters
const resetFilters = () => {
  search.value = ''
  selectedCategories.value = []
  activePriceRange.value = priceRanges[0]
  minPrice.value = null
  maxPrice.value = null
  isOrganic.value = null
  inStock.value = null
  sortField.value = ''
  sortDirection.value = 'asc'
  updateFilters()
  updateSort()
}
</script>

<template>
  <div class="product-search">
    <!-- Search Section -->
    <div class="search-section">
      <input
        type="text"
        v-model="search"
        placeholder="Search products..."
        class="search-input"
      />
    </div>

    <!-- Filter Section -->
    <div class="filter-section">
      <h3>Filters</h3>

      <!-- Categories -->
      <div class="filter-group">
        <label>Categories</label>
        <div class="category-tags">
          <button
            v-for="category in productCategories"
            :key="category"
            class="category-tag"
            :class="{ active: selectedCategories.includes(category) }"
            @click="selectedCategories = selectedCategories.includes(category)
              ? selectedCategories.filter(c => c !== category)
              : [...selectedCategories, category]"
          >
            {{ category }}
          </button>
        </div>
      </div>

      <!-- Price Range -->
      <div class="filter-group">
        <label>Price Range</label>
        <div class="price-range-buttons">
          <button
            v-for="range in priceRanges"
            :key="range.label"
            class="price-range-btn"
            :class="{ active: range === activePriceRange }"
            @click="selectPriceRange(range)"
          >
            {{ range.label }}
          </button>
        </div>
      </div>

      <!-- Toggle Filters -->
      <div class="filter-group toggles">
        <!-- Organic Filter -->
        <button
          class="toggle-btn"
          :class="{ active: isOrganic === true }"
          @click="isOrganic = isOrganic === true ? null : true"
        >
          <span class="toggle-icon">🌿</span>
          Organic Only
        </button>

        <!-- In Stock Filter -->
        <button
          class="toggle-btn"
          :class="{ active: inStock === true }"
          @click="inStock = inStock === true ? null : true"
        >
          <span class="toggle-icon">✓</span>
          In Stock Only
        </button>
      </div>
    </div>

    <!-- Sort Section -->
    <div class="sort-section">
      <div class="sort-controls">
        <select v-model="sortField" class="sort-select">
          <option value="">Sort by...</option>
          <option
            v-for="option in sortOptions"
            :key="option.value"
            :value="option.value"
          >
            {{ option.label }}
          </option>
        </select>
        <button
          v-if="sortField"
          class="sort-direction"
          @click="sortDirection = sortDirection === 'asc' ? 'desc' : 'asc'"
        >
          {{ sortDirection === 'asc' ? '↑' : '↓' }}
        </button>
      </div>

      <!-- Reset Filters -->
      <button class="reset-btn" @click="resetFilters">
        Reset All Filters
      </button>
    </div>
  </div>
</template>

<style scoped>
.product-search {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.search-section {
  margin-bottom: 24px;
}

.search-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.search-input:focus {
  border-color: #4caf50;
  outline: none;
}

.filter-section {
  border-top: 1px solid #e0e0e0;
  padding-top: 24px;
  margin-bottom: 24px;
}

.filter-section h3 {
  font-size: 1.1rem;
  margin-bottom: 16px;
  color: #333;
}

.filter-group {
  margin-bottom: 24px;
}

.filter-group label {
  display: block;
  font-weight: 500;
  margin-bottom: 12px;
  color: #666;
}

.category-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.category-tag {
  padding: 6px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  background: none;
  font-size: 0.9rem;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.category-tag:hover {
  border-color: #4caf50;
  color: #4caf50;
}

.category-tag.active {
  background: #4caf50;
  border-color: #4caf50;
  color: white;
}

.price-range-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.price-range-btn {
  padding: 6px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  background: none;
  font-size: 0.9rem;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.price-range-btn:hover {
  border-color: #4caf50;
  color: #4caf50;
}

.price-range-btn.active {
  background: #4caf50;
  border-color: #4caf50;
  color: white;
}

.toggles {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.toggle-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: none;
  font-size: 0.9rem;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.toggle-btn:hover {
  border-color: #4caf50;
  color: #4caf50;
}

.toggle-btn.active {
  background: #4caf50;
  border-color: #4caf50;
  color: white;
}

.toggle-icon {
  font-size: 1.1rem;
}

.sort-section {
  border-top: 1px solid #e0e0e0;
  padding-top: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.sort-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-select {
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 0.9rem;
  color: #333;
}

.sort-direction {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  background: none;
  font-size: 1.1rem;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.sort-direction:hover {
  border-color: #4caf50;
  color: #4caf50;
}

.reset-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  background: #f5f5f5;
  color: #666;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.reset-btn:hover {
  background: #e0e0e0;
}

@media (max-width: 640px) {
  .product-search {
    padding: 16px;
  }

  .sort-section {
    flex-direction: column;
    align-items: stretch;
  }

  .sort-controls {
    width: 100%;
  }

  .sort-select {
    flex-grow: 1;
  }

  .reset-btn {
    width: 100%;
  }
}
</style>
