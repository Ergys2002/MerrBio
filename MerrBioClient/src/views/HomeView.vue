<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { ref, computed, onMounted, onUnmounted, reactive } from 'vue';
import axios from 'axios';
import '../assets/organic-patterns.css';
import { ProductService } from '@/services/productService';

const { t } = useI18n();

// For parallax effect
const parallaxElement = ref(null);
const lastScrollTop = ref(0);

// For animation on scroll
const howItWorksSection = ref(null);
const staggerElements = ref([]);
const backToTopButton = ref(null);
const heroContentRef = ref(null); // Added for hero animation

// State for products and categories
const featuredProducts = ref([]);
const categories = ref([
  { id: 'all', name: t('products.categories.all') }
]);
const activeCategory = ref('all');
const isLoading = ref(true);
const error = ref(null);

// Recently added products ticker
const recentlyAdded = [
  t('home.recentlyAdded.first'),
  t('home.recentlyAdded.second'),
  t('home.recentlyAdded.third'),
  t('home.recentlyAdded.fourth')
];

// Statistics for the farmer CTA section
const stats = [
  { figure: '500+', label: t('home.stats.farmers') },
  { figure: '2,000+', label: t('home.stats.products') },
  { figure: '10,000+', label: t('home.stats.customers') }
];

// Filter products by category
const filteredProducts = computed(() => {
  if (activeCategory.value === 'all') return featuredProducts.value;
  return featuredProducts.value.filter(product => {
    return product.categories.some(category => category.id === activeCategory.value);
  });
});

// Scroll handling for animations and parallax effect
const handleScroll = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
  
  // Parallax effect
  if (parallaxElement.value) {
    const offset = scrollTop * 0.4;
    parallaxElement.value.style.backgroundPositionY = `-${offset}px`;
  }
  
  // Check if how-it-works section is in viewport
  if (howItWorksSection.value) {
    const rect = howItWorksSection.value.getBoundingClientRect();
    const isVisible = (rect.top <= window.innerHeight * 0.75);
    
    if (isVisible) {
      staggerElements.value.forEach(el => {
        el.classList.add('visible');
      });
    }
  }
  
  // Show/hide back to top button
  if (backToTopButton.value) {
    if (scrollTop > 300) {
      backToTopButton.value.classList.add('visible');
    } else {
      backToTopButton.value.classList.remove('visible');
    }
  }
  
  lastScrollTop.value = scrollTop;
};

// Scroll to top function
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  });
};

// Setup event listeners and initialize elements
onMounted(() => {
  // Get stagger elements for animation
  staggerElements.value = document.querySelectorAll('.stagger-item');
  
  // Add scroll event listener
  window.addEventListener('scroll', handleScroll);
  
  // Initial call to handle elements already in view
  handleScroll();

  // Trigger hero animation
  if (heroContentRef.value) {
    heroContentRef.value.classList.add('animate-in');
  }
  // Fetch products and categories from API
  const fetchData = async () => {
    isLoading.value = true;
    try {
      // Fetch categories first
      const categoryResponse = await axios.get('/api/categories');
      console.log('Categories response:', categoryResponse);
      
      // Check if categories data is valid and has content
      if (categoryResponse.data && Array.isArray(categoryResponse.data)) {
        // Create categories array with 'all' option first
        categories.value = [
          { id: 'all', name: t('products.categories.all') },
          ...categoryResponse.data.map(category => ({
            id: category.id,
            name: category.name
          }))
        ];
      } else {
        console.error('Invalid categories data format:', categoryResponse.data);
        // Keep default 'all' category if response is invalid
        categories.value = [{ id: 'all', name: t('products.categories.all') }];      }      // Fetch latest products using simplified payload
      const productResponse = await ProductService.advancedSearch({ size: 6 });
      
      console.log('Products response:', productResponse);
        // Now correctly handle the response format with content array
      if (productResponse && productResponse.content && Array.isArray(productResponse.content)) {
        // API base URL for image paths
        const API_BASE_URL = 'http://localhost:8080/api/v1';
        
        featuredProducts.value = productResponse.content.map(product => ({
        id: product.id,
        name: product.name,
        description: product.description,
        image: product.thumbnailUrl ? API_BASE_URL + product.thumbnailUrl : 'https://placehold.co/600x400?text=No+Image',
        imageUrls: (product.imageUrls || []).map(url => API_BASE_URL + url),
        price: product.price,
        unit: product.unit ? product.unit.toLowerCase() : 'unit',
        farmer: product.farmerName || 'Unknown Farmer',
        farmerAvatar: 'https://randomuser.me/api/portraits/men/' + Math.floor(Math.random() * 100) + '.jpg',
        categories: Array.isArray(product.categories) ? product.categories : [],
        isOrganic: Boolean(product.isOrganic),
        isInStock: Boolean(product.isInStock),
        isNew: product.createdAt ? new Date(product.createdAt) > new Date(Date.now() - 7 * 24 * 60 * 60 * 1000) : false, // Created in last 7 days
        isPopular: Math.random() > 0.7 // Random for demo purposes
      }));
      } else {
        console.error('Invalid products data format or empty content array:', productResponse.data);
        featuredProducts.value = []; // Set empty array if no valid products
      }
    } catch (err) {
      console.error('Error fetching data:', err);
      error.value = 'Failed to fetch data';
    } finally {
      isLoading.value = false;
    }
  };

  fetchData();
});

// Clean up event listeners
onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<template>
  <div class="home-view">
    <!-- Recently Added Products Ticker Removed -->
    <!-- Hero Section with Parallax -->
    <section class="hero-section parallax" ref="parallaxElement">
      <div class="hero-overlay"></div> <!-- Changed class -->
      <div class="hero-container">
        <div class="hero-content" ref="heroContentRef"> <!-- Added ref -->
          <h1>
            {{ t('home.hero.title').split(' ')[0] }} 
            <span class="gradient-text">{{ t('home.hero.title').split(' ').slice(1).join(' ') }}</span>
          </h1>
          <p>{{ t('home.hero.description') }}</p>
          <div class="cta-buttons">
            <router-link to="/products" class="cta-button primary btn-modern pulse-on-hover">
              {{ t('home.hero.browseButton') }}
            </router-link>
            <router-link to="/auth" class="cta-button secondary btn-modern">
              {{ t('home.hero.listButton') }}
            </router-link>
          </div>
        </div>      
      </div>
      <!-- Shape Divider Removed -->
    </section>
    
    <div class="organic-divider"></div>

    <!-- How It Works Section -->
    <section class="how-it-works" ref="howItWorksSection">
      <div class="agriculture-pattern-bg"></div>
      <div class="container">
        <h2 class="section-title">{{ t('home.howItWorks.title') }}</h2>
        <div class="steps-container">
          <div class="step stagger-item hover-grow">
            <div class="step-icon">
              <img src="https://img.icons8.com/fluency/48/null/search-in-list.png" alt="Browse icon" />
            </div>
            <h3>{{ t('home.howItWorks.step1.title') }}</h3>
            <p>{{ t('home.howItWorks.step1.description') }}</p>
          </div>
          <div class="step stagger-item hover-grow">
            <div class="step-icon">
              <img src="https://img.icons8.com/fluency/48/null/add-shopping-cart.png" alt="Request icon" />
            </div>
            <h3>{{ t('home.howItWorks.step2.title') }}</h3>
            <p>{{ t('home.howItWorks.step2.description') }}</p>
          </div>
          <div class="step stagger-item hover-grow">
            <div class="step-icon">
              <img src="https://img.icons8.com/fluency/48/null/organic-food.png" alt="Farm to Table icon" />
            </div>
            <h3>{{ t('home.howItWorks.step3.title') }}</h3>
            <p>{{ t('home.howItWorks.step3.description') }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Featured Products Section -->
    <section class="featured-products">
      <div class="container">
        <h2 class="section-title">{{ t('home.featuredProducts.title') }}</h2>
        
        <!-- Product Filter Tabs -->
        <div class="product-filter-tabs">
          <button 
            v-for="category in categories" 
            :key="category.id"
            :class="{ active: activeCategory === category.id }"
            @click="activeCategory = category.id">
            {{ category.name }}
          </button>
        </div>
        
        <!-- Desktop Product Grid -->
        <div v-if="isLoading" class="loading-state">
          <div class="spinner"></div>
          <p>Loading products...</p>
        </div>
        <div v-else-if="error" class="error-state">
          <p>{{ error }}</p>
          <button @click="fetchData" class="retry-button">Try Again</button>
        </div>
        <div v-else class="products-grid desktop-grid">
          <div v-for="product in filteredProducts" :key="product.id" class="product-card hover-grow">
            <div class="product-image">
              <img :src="product.image" :alt="product.name" />
              <!-- Product Badges -->
              <div class="product-badges">
                <span v-if="product.isNew" class="badge badge-new">{{ t('products.badges.new') }}</span>
                <span v-if="product.isOrganic" class="badge badge-organic">Produkt Organik</span>
                <span v-if="product.isPopular" class="badge badge-popular">{{ t('products.badges.popular') }}</span>
                <span v-if="!product.isInStock" class="badge badge-out-of-stock">{{ t('products.badges.outOfStock') }}</span>
              </div>
              <!-- Category Badge -->
              <div class="category-badge" v-if="product.categories.length > 0">
                {{ product.categories[0].name }}
              </div>
            </div>
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <!-- Farmer Info with Avatar -->
              <div class="farmer-info">
                <img :src="product.farmerAvatar" :alt="product.farmer" class="farmer-avatar" />
                <span class="farmer-name">{{ product.farmer }}</span>
              </div>
              <p class="description">{{ product.description }}</p>
              <div class="product-footer">
                <span class="price">€{{ product.price.toFixed(2) }} / {{ product.unit }}</span>
                <router-link :to="`/products/${product.id}`" class="view-button btn-modern">
                  {{ t('products.viewButton') }}
                </router-link>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Mobile Horizontal Scrolling Product List -->
        <div class="products-scroll-container">
          <div class="scroll-indicator left">‹</div>
          <div v-if="isLoading" class="loading-state">
            <div class="spinner"></div>
            <p>Loading products...</p>
          </div>
          <div v-else-if="error" class="error-state">
            <p>{{ error }}</p>
            <button @click="fetchData" class="retry-button">Try Again</button>
          </div>
          <div v-else class="products-scroll">
            <div v-for="product in filteredProducts" :key="product.id" class="product-card hover-grow">
              <div class="product-image">
                <img :src="product.image" :alt="product.name" />
                <!-- Product Badges -->
                <div class="product-badges">
                  <span v-if="product.isNew" class="badge badge-new">{{ t('products.badges.new') }}</span>
                  <span v-if="product.isOrganic" class="badge badge-organic">{{ t('products.badges.organic') }}</span>
                  <span v-if="!product.isInStock" class="badge badge-out-of-stock">{{ t('products.badges.outOfStock') }}</span>
                </div>
              </div>
              <div class="product-info">
                <h3>{{ product.name }}</h3>
                <!-- Farmer Info with Avatar -->
                <div class="farmer-info">
                  <img :src="product.farmerAvatar" :alt="product.farmer" class="farmer-avatar" />
                  <span class="farmer-name">{{ product.farmer }}</span>
                </div>
                <div class="product-footer">
                  <span class="price">€{{ product.price.toFixed(2) }} / {{ product.unit }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="scroll-indicator right">›</div>
        </div>
        
        <div class="view-all-container">
          <router-link to="/products" class="view-all-button btn-modern">
            {{ t('products.viewAllButton') }}
          </router-link>
        </div>
      </div>
    </section>

    <div class="organic-divider"></div>

    <!-- Farmer CTA Section -->
    <section class="farmer-cta">
      <div class="container">
        <div class="cta-split">
          <div class="cta-image">
            <img src="https://images.unsplash.com/photo-1500937386664-56d1dfef3854?ixlib=rb-4.0.2&auto=format&fit=crop&q=80" :alt="t('home.farmerCta.imageAlt')" />
          </div>
          <div class="cta-content">
            <h2>{{ t('home.farmerCta.title') }}</h2>
            <p>{{ t('home.farmerCta.description') }}</p>
            
            <!-- Animated Statistics -->
            <div class="stats-container">
              <div v-for="(stat, index) in stats" :key="index" class="stat-item">
                <div class="stat-figure">{{ stat.figure }}</div>
                <div class="stat-label">{{ stat.label }}</div>
              </div>
            </div>
            
            <!-- Farmer Testimonials -->
            <div class="farmer-testimonial">
              <div class="testimonial-quote">
                "{{ t('home.farmerCta.testimonial') }}"
              </div>
              <div class="testimonial-author">
                <img src="https://randomuser.me/api/portraits/men/42.jpg" alt="Farmer" class="author-avatar" />
                <div class="author-info">
                  <div class="author-name">Dritan M.</div>
                  <div class="author-role">{{ t('home.farmerCta.testimonialRole') }}</div>
                </div>
              </div>
            </div>
            
            <router-link to="/auth" class="cta-button primary btn-modern">
              {{ t('home.farmerCta.joinButton') }}
            </router-link>
          </div>
        </div>
      </div>
    </section>
      <!-- Trust Indicators -->    <section class="trust-indicators">
      <div class="container">
        <h3>{{ t('home.trustIndicators.title') }}</h3>
        <div class="certification-logos">
          <img src="https://img.icons8.com/color/96/null/natural-food.png" alt="Organic Certification" />
          <img src="https://img.icons8.com/color/96/null/certificate.png" alt="Quality Certified" />
          <img src="https://img.icons8.com/color/96/null/guarantee.png" alt="Satisfaction Guaranteed" />
        </div>
      </div>
    </section>
    
    <!-- Back to Top Button -->
    <div class="back-to-top" ref="backToTopButton" @click="scrollToTop">
      <span>↑</span>
    </div>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Montserrat:wght@400;500;600;700&family=Merriweather:wght@300;400;700&display=swap');

html {
  scroll-behavior: smooth;
}

.home-view {
  --primary-color: #4caf50;
  --secondary-color: #2e7d32;
  --accent-color: #8bc34a;
  --light-color: #f8f9fa;
  --dark-color: #333;
  --earthy-brown: #8d6e63;
  --earthy-tan: #d7ccc8;
  --earthy-green-light: #e8f5e9;
  --earthy-green-dark: #2e7d32;
  --earthy-orange: #ff9800;
  --hero-overlay-start: rgba(30, 80, 32, 0.65); /* Slightly darker, less saturated green */
  --hero-overlay-end: rgba(100, 150, 50, 0.35); /* Muted accent green */
  
  font-family: 'Merriweather', serif;
  color: var(--dark-color);
}

h1, h2, h3, h4, h5, h6, button, .cta-button {
  font-family: 'Montserrat', sans-serif;
}

.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 30px;
}

.section-title {
  text-align: center;
  margin-bottom: 50px;
  font-size: 2.5rem;
  font-weight: 600;
  color: var(--dark-color);
  position: relative;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -15px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), var(--accent-color));
  border-radius: 2px;
}

/* Recently Added Ticker Styles Removed */
/* Hero Section with Parallax */
.hero-section {
  height: 85vh; /* Slightly reduced height */
  min-height: 600px;
  position: relative;
  display: flex;
  align-items: center;
  background-image: url('https://images.unsplash.com/photo-1500937386664-56d1dfef3854?ixlib=rb-4.0.2&auto=format&fit=crop&q=80');
  background-size: cover;
  background-position: center;
  color: white;
  overflow: hidden; /* Keep hidden to contain divider */
}

.hero-overlay { /* New overlay style */
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--hero-overlay-start), var(--hero-overlay-end)),
              url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='40' height='40' viewBox='0 0 40 40'%3E%3Cg fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M0 38.59l2.83-2.83 1.41 1.41L1.41 40H0v-1.41zM0 1.4l2.83 2.83 1.41-1.41L1.41 0H0v1.41zM38.59 40l-2.83-2.83 1.41-1.41L40 38.59V40h-1.41zM40 1.41l-2.83 2.83-1.41-1.41L38.59 0H40v1.41zM20 18.6l2.83-2.83 1.41 1.41L21.41 20l2.83 2.83-1.41 1.41L20 21.41l-2.83 2.83-1.41-1.41L18.59 20l-2.83-2.83 1.41-1.41L20 18.59z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  z-index: 0;
}

.hero-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 30px;
  z-index: 1; /* Ensure content is above overlay */
  position: relative;
}

.hero-content {
  max-width: 700px; /* Slightly wider */
  opacity: 0; /* Start hidden for animation */
  transform: translateY(20px); /* Start slightly lower */
  transition: opacity 0.8s ease-out, transform 0.8s ease-out;
}

.hero-content.animate-in {
  opacity: 1;
  transform: translateY(0);
}

.hero-content h1 {
  font-size: 4rem; /* Increased size */
  margin-bottom: 25px; /* More space */
  line-height: 1.25;
  font-weight: 700;
  text-shadow: 1px 1px 3px rgba(0, 0, 0, 0.3); /* Subtle text shadow */
}

.hero-content .gradient-text {
  background: linear-gradient(90deg, var(--accent-color), #ffffff);
  -webkit-background-clip: text;
  background-clip: text; /* Added standard property */
  -webkit-text-fill-color: transparent;
  padding-bottom: 5px; /* Add padding for visual effect */
}

.hero-content p {
  font-size: 1.25rem; /* Slightly larger paragraph */
  margin-bottom: 40px; /* Increased spacing */
  max-width: 650px; /* Slightly wider */
  color: var(--light-color); /* Ensure text is readable */
  line-height: 1.6; /* Adjusted line height */
  font-family: 'Merriweather', serif; /* Keep serif font */
  font-weight: 300; /* Keep lighter weight */
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2); /* Keep text shadow */
}

.cta-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-top: 20px;
  /* Add staggered animation delay */
  & > * {
    opacity: 0;
    transform: translateY(10px);
    animation: buttonFadeIn 0.6s ease-out forwards;
  }
  & > *:nth-child(1) { animation-delay: 0.6s; }
  & > *:nth-child(2) { animation-delay: 0.75s; }
}

@keyframes buttonFadeIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.cta-button {
  display: inline-block;
  padding: 15px 32px;
  border-radius: 50px;
  font-weight: 600;
  font-size: 1.1rem;
  text-decoration: none;
  transition: all 0.3s ease, transform 0.15s ease; /* Added transform transition */
}

.cta-button.primary {
  background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
  color: white;
  border: none;
}

.cta-button.primary:hover {
  transform: translateY(-4px) scale(1.02); /* More pronounced hover */
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.35);
}

.cta-button.secondary {
  background: rgba(255, 255, 255, 0.9);
  color: var(--primary-color);
  border: none;
}

.cta-button.secondary:hover {
  background: white;
  color: var(--secondary-color); /* Darker green text on hover */
  transform: translateY(-4px) scale(1.02); /* More pronounced hover */
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
}

/* How It Works Section */
.how-it-works {
  padding: 100px 0; /* Increased padding */
  background-color: white; /* Ensure background is white */
  z-index: 1; /* Ensure content is above the divider pseudo-elements */
  position: relative; /* Needed for z-index */
}

.agriculture-pattern-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='100' height='100' viewBox='0 0 100 100'%3E%3Cpath fill='none' stroke='%23e8f5e9' stroke-width='2' d='M25,25 L75,25 L75,75 L25,75 Z' /%3E%3Cpath fill='none' stroke='%23c8e6c9' stroke-width='1' d='M40,40 L60,40 L60,60 L40,60 Z' /%3E%3Ccircle cx='25' cy='25' r='3' fill='%23a5d6a7' /%3E%3Ccircle cx='75' cy='25' r='3' fill='%23a5d6a7' /%3E%3Ccircle cx='75' cy='75' r='3' fill='%23a5d6a7' /%3E%3Ccircle cx='25' cy='75' r='3' fill='%23a5d6a7' /%3E%3C/svg%3E");
  opacity: 0.1; /* Slightly more subtle */
}

.steps-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: 50px; /* Increased gap */
  position: relative;
  z-index: 1;
}

.step {
  text-align: center;
  padding: 30px;
  border-radius: 12px;
  background-color: white;
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.07); /* Softer shadow */
  transition: all 0.3s ease;
  position: relative;
  border: 1px solid var(--earthy-tan); /* Subtle border */
}

.step-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
  color: white;
  border-radius: 50%;
  margin: 0 auto 20px;
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.35); /* Slightly stronger shadow */
  transition: transform 0.3s ease;
}

.step-icon img {
  width: 40px;
  height: 40px;
}

.step:hover .step-icon {
  transform: rotateY(180deg);
}

.step h3 {
  margin-bottom: 15px;
  color: var(--dark-color);
  font-size: 1.4rem;
  font-weight: 600;
}

.step p {
  color: #666;
  line-height: 1.6;
}

/* Featured Products Section */
.featured-products {
  padding: 100px 0; /* Increased padding */
  background-color: var(--earthy-green-light); /* Ensure background matches divider */
  z-index: 1; /* Ensure content is above the divider pseudo-elements */
  position: relative; /* Needed for z-index */
}

/* Product Filter Tabs */
.product-filter-tabs {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 40px;
}

.product-filter-tabs button {
  background: transparent;
  border: 2px solid var(--primary-color);
  color: var(--primary-color);
  padding: 10px 20px; /* Slightly larger */
  border-radius: 30px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.product-filter-tabs button.active,
.product-filter-tabs button:hover {
  background-color: var(--primary-color);
  color: white;
}

/* Product Grid */
.products-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 40px; /* Increased gap */
}

.product-card {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.07); /* Refined shadow */
  transition: all 0.3s ease;
  position: relative;
}

.product-card.hover-grow:hover {
  transform: translateY(-8px); /* More lift */
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.12); /* Enhanced shadow */
}

.product-card.hover-grow:hover .product-image img {
  transform: scale(1.05); /* Subtle zoom on image */
}

.product-image {
  height: 200px;
  overflow: hidden;
  position: relative;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease-out; /* Smoother transition */
}

.product-badges {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  flex-direction: column;
  gap: 5px;
  z-index: 2;
}

.product-badges .badge {
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.badge-new {
  background-color: var(--earthy-orange);
  color: white;
}

.badge-popular {
  background-color: var(--primary-color);
  color: white;
}

.category-badge {
  position: absolute;
  bottom: 10px;
  left: 10px;
  background-color: rgba(46, 125, 50, 0.7); /* Darker green overlay */
  color: white;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 600;
}

.product-info {
  padding: 20px;
}

.product-info h3 {
  font-size: 1.3rem;
  margin-bottom: 10px;
  color: var(--dark-color);
  font-weight: 600;
}

.farmer-info {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.farmer-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 8px;
  border: 2px solid var(--primary-color);
}

.farmer-name {
  font-size: 0.9rem;
  color: var(--primary-color);
  font-weight: 500;
}

.description {
  color: #666;
  margin-bottom: 15px;
  line-height: 1.5;
  font-size: 0.95rem;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.price {
  font-weight: bold;
  color: var(--dark-color);
  font-size: 1.1rem;
}

.view-button {
  display: inline-block;
  padding: 8px 16px;
  background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
  color: white;
  border-radius: 30px;
  text-decoration: none;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  font-weight: 600;
}

.view-button:hover {
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

/* Mobile Horizontal Scrolling for Products */
.products-scroll-container {
  position: relative;
  display: none;
}

.products-scroll {
  display: flex;
  overflow-x: auto;
  gap: 25px; /* Increased gap */
  padding: 15px 10px;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none; /* for Firefox */
}

.products-scroll::-webkit-scrollbar {
  display: none; /* for Chrome, Safari */
}

.scroll-indicator {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  font-size: 1.5rem;
  color: var(--primary-color);
  cursor: pointer;
  z-index: 2;
  transition: all 0.3s ease;
}

.scroll-indicator:hover {
  background: white;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.scroll-indicator.left {
  left: 5px;
}

.scroll-indicator.right {
  right: 5px;
}

.view-all-container {
  text-align: center;
  margin-top: 50px;
}

.view-all-button {
  display: inline-block;
  padding: 14px 30px;
  background-color: transparent;
  color: var(--primary-color);
  border: 2px solid var(--primary-color);
  border-radius: 50px;
  font-weight: 600;
  font-size: 1.1rem;
  text-decoration: none;
  transition: all 0.3s ease;
}

.view-all-button:hover {
  background-color: var(--primary-color);
  color: white;
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(76, 175, 80, 0.3);
}

/* Farmer CTA Section */
.farmer-cta {
  padding: 100px 0; /* Increased padding */
  background-color: white; /* Ensure background matches divider */
  z-index: 1; /* Ensure content is above the divider pseudo-elements */
  position: relative; /* Needed for z-index */
}

.cta-split {
  display: grid;
  grid-template-columns: 1fr;
  gap: 60px; /* Increased gap */
  align-items: center;
}

.cta-image {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15); /* Enhanced shadow */
  height: 100%;
}

.cta-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease-out; /* Smoother transition */
}

.cta-image:hover img {
  transform: scale(1.03); /* Subtle zoom */
}

.cta-content {
  padding: 20px;
}

.cta-content h2 {
  font-size: 2.4rem; /* Slightly larger */
  margin-bottom: 20px;
  color: var(--dark-color);
  font-weight: 700;
}

.cta-content p {
  margin-bottom: 30px;
  font-size: 1.1rem;
  color: #555;
  line-height: 1.6;
}

/* Animated Statistics */
.stats-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: 25px; /* Increased gap */
  margin-bottom: 40px; /* More space */
}

.stat-item {
  text-align: center;
  padding: 20px;
  background-color: var(--earthy-green-light);
  border-radius: 8px;
  transition: background-color 0.3s ease;
}

.stat-item:hover {
  background-color: var(--earthy-tan); /* Subtle hover effect */
}

.stat-figure {
  font-size: 2.2rem; /* Slightly larger */
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: 5px;
}

.stat-label {
  font-size: 0.9rem;
  color: #555;
}

/* Farmer Testimonial */
.farmer-testimonial {
  background-color: #fff;
  padding: 25px;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.06); /* Refined shadow */
  margin-bottom: 30px;
  border-left: 4px solid var(--primary-color);
}

.testimonial-quote {
  font-style: italic;
  margin-bottom: 15px;
  font-size: 1rem;
  color: #555;
  line-height: 1.6;
}

.testimonial-author {
  display: flex;
  align-items: center;
}

.author-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  margin-right: 15px;
}

.author-info {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 600;
  color: var(--dark-color);
  margin-bottom: 2px;
}

.author-role {
  font-size: 0.85rem;
  color: #777;
}

/* Trust Indicators */
.trust-indicators {
  padding: 60px 0; /* Adjusted padding */
  background-color: var(--earthy-green-light);
  text-align: center;
}

.trust-indicators h3 {
  margin-bottom: 30px;
  font-size: 1.8rem; /* Slightly larger */
  font-weight: 600;
}

.certification-logos {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 40px; /* Increased gap */
}

.certification-logos img {
  height: 65px; /* Slightly larger */
  opacity: 0.8;
  transition: all 0.3s ease;
}

.certification-logos img:hover {
  opacity: 1;
  transform: scale(1.1);
}

/* Organic Divider */
.organic-divider {
  height: 80px; /* Slightly taller */
  background-color: var(--color-background); /* Match background */
  position: relative;
  overflow: hidden;
  margin-top: -1px; /* Overlap slightly to prevent gaps */
  margin-bottom: -1px; /* Overlap slightly to prevent gaps */
}

.organic-divider::before,
.organic-divider::after {
  content: '';
  position: absolute;
  left: 50%;
  min-width: 300vw; /* Ensure it covers the width */
  min-height: 300vw; /* Ensure it covers the height */
  background-color: white; /* Color of the section above/below */
  animation: rotate 15s infinite linear; /* Slower rotation */
  z-index: 0;
}

.organic-divider::before {
  bottom: 15vh; /* Adjust vertical position */
  border-radius: 45%; /* Adjust shape */
  background-color: var(--earthy-green-light); /* Color of the section below */
  opacity: 0.8; /* Adjust opacity */
}

.organic-divider::after {
  bottom: 12vh; /* Adjust vertical position */
  border-radius: 47%; /* Adjust shape */
  opacity: 0.5; /* Adjust opacity */
  animation-direction: reverse; /* Rotate opposite direction */
  background-color: var(--earthy-tan); /* Add another layer */
}

@keyframes rotate {
  0% { transform: translate(-50%, 0) rotateZ(0deg); }
  50% { transform: translate(-50%, -2%) rotateZ(180deg); }
  100% { transform: translate(-50%, 0%) rotateZ(360deg); }
}

/* Shape Divider */
.hero-shape-divider {
    position: absolute;
    bottom: -1px; /* Adjust to prevent gaps */
    left: 0;
    width: 100%;
    overflow: hidden;
    line-height: 0;
    transform: rotate(180deg); /* Flip the SVG */
    z-index: 1; /* Ensure it's above background but below content if needed */
}

.hero-shape-divider svg {
    position: relative;
    display: block;
    width: calc(100% + 1.3px);
    height: 80px; /* Adjust height as needed */
}

.hero-shape-divider .shape-fill {
    fill: white; /* Ensure divider matches the next section's background (How It Works) */
}

/* Media Queries for Responsiveness */
@media (min-width: 768px) {
  .stats-container {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .cta-split {
    grid-template-columns: 1fr 1fr;
  }
  
  .steps-container {
    grid-template-columns: repeat(3, 1fr);
  }
  
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .desktop-grid {
    display: grid;
  }
  
  .products-scroll-container {
    display: none;
  }
}

@media (min-width: 1024px) {
  .hero-content h1 {
    font-size: 4.2rem; /* Adjust for larger screens */
  }
  .products-grid {
    grid-template-columns: repeat(3, 1fr); /* 3 columns on larger screens */
  }
}

@media (max-width: 767px) {
  .desktop-grid {
    display: none;
  }
  
  .products-scroll-container {
    display: block;
  }
  
  .products-scroll .product-card {
    min-width: 270px;
  }
  
  .hero-content h1 {
    font-size: 2.8rem; /* Adjust for smaller screens */
  }
  
  .hero-content p {
    font-size: 1.1rem;
  }
  .cta-buttons {
    justify-content: center; /* Center buttons on mobile */
  }
  .hero-shape-divider svg {
    height: 50px; /* Adjust divider height for mobile */
  }
}

/* Loading State */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: #666;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(76, 175, 80, 0.1);
  border-radius: 50%;
  border-top-color: #4caf50;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* Error State */
.error-state {
  text-align: center;
  padding: 60px 0;
  color: #f44336;
}

.retry-button {
  margin-top: 16px;
  padding: 8px 24px;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.retry-button:hover {
  background-color: #d32f2f;
}
</style>