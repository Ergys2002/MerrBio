<script setup lang="ts">
/**
 * FavoriteFarmers - Component for consumers to track their favorite farmers
 * @component
 */
import { ref, computed } from 'vue';

// Farmer type definition
interface Farmer {
  id: number;
  name: string;
  location: string;
  rating: number;
  productCategories: string[];
  totalOrders: number;
  imageUrl?: string;
  description?: string;
  isFavorite: boolean;
}

// UI state
const searchQuery = ref('');

// Mock favorite farmers data
const farmers = ref<Farmer[]>([
  {
    id: 101,
    name: 'Green Valley Farm',
    location: 'Tirana, Albania',
    rating: 4.8,
    productCategories: ['Vegetables', 'Fruits', 'Herbs'],
    totalOrders: 15,
    imageUrl: 'https://example.com/farm1.jpg',
    description: 'Organic farm specializing in fresh vegetables and fruits.',
    isFavorite: true
  },
  {
    id: 102,
    name: 'Sunny Fields',
    location: 'Durrës, Albania',
    rating: 4.5,
    productCategories: ['Fruits', 'Nuts'],
    totalOrders: 8,
    description: 'Family-owned orchard producing seasonal fruits and nuts.',
    isFavorite: true
  },
  {
    id: 103,
    name: 'Mountain Apiaries',
    location: 'Vlorë, Albania',
    rating: 5.0,
    productCategories: ['Honey', 'Bee Products'],
    totalOrders: 3,
    imageUrl: 'https://example.com/farm3.jpg',
    description: 'Natural honey and bee products from the Albanian mountains.',
    isFavorite: true
  },
  {
    id: 104,
    name: 'Happy Poultry Farm',
    location: 'Shkodër, Albania',
    rating: 4.2,
    productCategories: ['Eggs', 'Poultry'],
    totalOrders: 5,
    description: 'Free-range poultry farm offering eggs and meat.',
    isFavorite: true
  }
]);

/**
 * Filter farmers based on search query
 */
const filteredFarmers = computed(() => {
  if (!searchQuery.value) return farmers.value;
  
  const query = searchQuery.value.toLowerCase();
  return farmers.value.filter(farmer => 
    farmer.name.toLowerCase().includes(query) || 
    farmer.location.toLowerCase().includes(query) ||
    farmer.description?.toLowerCase().includes(query) ||
    farmer.productCategories.some(category => category.toLowerCase().includes(query))
  );
});

/**
 * Remove farmer from favorites
 */
const removeFromFavorites = (farmerId: number) => {
  if (confirm('Are you sure you want to remove this farmer from your favorites?')) {
    const farmerIndex = farmers.value.findIndex(f => f.id === farmerId);
    if (farmerIndex !== -1) {
      // In a real implementation, would make API call
      farmers.value.splice(farmerIndex, 1);
    }
  }
};

/**
 * Generate star rating HTML
 */
const renderStars = (rating: number): string => {
  const fullStars = Math.floor(rating);
  const halfStar = rating - fullStars >= 0.5;
  const emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
  
  return '★'.repeat(fullStars) + (halfStar ? '½' : '') + '☆'.repeat(emptyStars);
};

/**
 * Navigate to farmer's products
 */
const viewFarmerProducts = (farmerId: number) => {
  // Would use router to navigate
  console.log(`Navigate to farmer ${farmerId} products`);
};
</script>

<template>
  <div class="favorite-farmers">
    <div class="header-actions">
      <h2>My Favorite Farmers</h2>
      <div class="search-box">
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="Search favorites..." 
        />
      </div>
    </div>
    
    <!-- Farmers Grid -->
    <div v-if="filteredFarmers.length" class="farmers-grid">
      <div 
        v-for="farmer in filteredFarmers" 
        :key="farmer.id"
        class="farmer-card"
      >
        <div class="farmer-actions">
          <button class="btn-icon unfavorite" @click="removeFromFavorites(farmer.id)" title="Remove from favorites">
            ❤️
          </button>
        </div>
        
        <div class="farmer-image">
          <img v-if="farmer.imageUrl" :src="farmer.imageUrl" :alt="farmer.name" />
          <div v-else class="image-placeholder">
            {{ farmer.name.charAt(0) }}
          </div>
        </div>
        
        <div class="farmer-info">
          <h3>{{ farmer.name }}</h3>
          <p class="farmer-location">{{ farmer.location }}</p>
          
          <div class="farmer-rating">
            <span class="stars" v-html="renderStars(farmer.rating)"></span>
            <span class="rating-value">{{ farmer.rating }}</span>
          </div>
          
          <p class="farmer-description" v-if="farmer.description">
            {{ farmer.description }}
          </p>
          
          <div class="farmer-categories">
            <span
              v-for="(category, index) in farmer.productCategories"
              :key="index"
              class="category-tag"
            >
              {{ category }}
            </span>
          </div>
          
          <div class="order-history">
            <span>{{ farmer.totalOrders }} orders placed</span>
          </div>
        </div>
        
        <div class="farmer-actions footer">
          <button class="btn primary" @click="viewFarmerProducts(farmer.id)">
            View Products
          </button>
        </div>
      </div>
    </div>
    
    <!-- Empty state -->
    <div v-else class="empty-state">
      <p v-if="searchQuery">No farmers match your search.</p>
      <p v-else>You haven't added any farmers to your favorites yet.</p>
      <button class="btn primary">Browse Farmers</button>
    </div>
  </div>
</template>

<style scoped>
.favorite-farmers {
  width: 100%;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.search-box input {
  padding: 0.75rem;
  border: 1px solid #d0d0d0;
  border-radius: 4px;
  width: 250px;
}

.farmers-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.farmer-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  background-color: white;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  position: relative;
}

.farmer-actions {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
}

.farmer-actions.footer {
  position: static;
  padding: 1rem;
  border-top: 1px solid #e0e0e0;
}

.btn-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(255, 255, 255, 0.8);
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.2rem;
}

.farmer-image {
  height: 160px;
  overflow: hidden;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.farmer-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  font-size: 4rem;
  color: #999;
  font-weight: bold;
}

.farmer-info {
  padding: 1rem;
  flex-grow: 1;
}

.farmer-info h3 {
  margin-top: 0;
  margin-bottom: 0.25rem;
}

.farmer-location {
  margin-top: 0;
  margin-bottom: 0.5rem;
  color: #666;
  font-style: italic;
}

.farmer-rating {
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.stars {
  color: #faad14;
}

.rating-value {
  font-weight: 600;
}

.farmer-description {
  color: #555;
  font-size: 0.9rem;
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.farmer-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.category-tag {
  background-color: #f0f7ff;
  color: #1890ff;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
}

.order-history {
  font-size: 0.85rem;
  color: #666;
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
  }
  
  .search-box input {
    width: 100%;
  }
}
</style>
