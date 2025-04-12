<script setup lang="ts">
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

// Placeholder data for featured products - this will be replaced with real data from the API later
const featuredProducts = [
  {
    id: 1,
    name: t('home.featuredProducts.tomatoes.name'),
    description: t('home.featuredProducts.tomatoes.description'),
    image: 'https://images.unsplash.com/photo-1592924357229-b2ab2fedcf04?ixlib=rb-4.0.2&auto=format&fit=crop&q=80',
    price: 3.99,
    unit: t('products.units.kg'),
    farmer: t('home.featuredProducts.tomatoes.farmer')
  },
  {
    id: 2,
    name: t('home.featuredProducts.eggs.name'),
    description: t('home.featuredProducts.eggs.description'),
    image: 'https://images.unsplash.com/photo-1569127959161-2b1297b2d9a5?ixlib=rb-4.0.2&auto=format&fit=crop&q=80',
    price: 4.50,
    unit: t('products.units.dozen'),
    farmer: t('home.featuredProducts.eggs.farmer')
  },
  {
    id: 3,
    name: t('home.featuredProducts.lettuce.name'),
    description: t('home.featuredProducts.lettuce.description'),
    image: 'https://images.unsplash.com/photo-1622205313162-be1d5712a43f?ixlib=rb-4.0.2&auto=format&fit=crop&q=80',
    price: 2.25,
    unit: t('products.units.head'),
    farmer: t('home.featuredProducts.lettuce.farmer')
  }
];
</script>

<template>
  <div class="home-view">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="container">
        <div class="hero-content">
          <h1>{{ t('home.hero.title') }}</h1>
          <p>{{ t('home.hero.description') }}</p>
          <div class="cta-buttons">
            <router-link to="/products" class="cta-button primary">{{ t('home.hero.browseButton') }}</router-link>
            <router-link to="/auth" class="cta-button secondary">{{ t('home.hero.listButton') }}</router-link>
          </div>
        </div>
        <div class="hero-image">
          <img src="https://images.unsplash.com/photo-1500937386664-56d1dfef3854?ixlib=rb-4.0.2&auto=format&fit=crop&q=80" :alt="t('home.hero.imageAlt')" />
        </div>
      </div>
    </section>

    <!-- How It Works Section -->
    <section class="how-it-works">
      <div class="container">
        <h2>How MerrBio Works</h2>
        <div class="steps-container">
          <div class="step">
            <div class="step-icon">1</div>
            <h3>Browse Products</h3>
            <p>Explore fresh, locally grown products from farms near you.</p>
          </div>
          <div class="step">
            <div class="step-icon">2</div>
            <h3>Request to Purchase</h3>
            <p>Select products you want and request to purchase them directly.</p>
          </div>
          <div class="step">
            <div class="step-icon">3</div>
            <h3>Farm to Table</h3>
            <p>Arrange pickup or delivery with the farmer for the freshest experience.</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Featured Products Section -->
    <section class="featured-products">
      <div class="container">
        <h2>Featured Products</h2>
        <div class="products-grid">
          <div v-for="product in featuredProducts" :key="product.id" class="product-card">
            <div class="product-image">
              <img :src="product.image" :alt="product.name" />
            </div>
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <p class="farmer">by {{ product.farmer }}</p>
              <p class="description">{{ product.description }}</p>
              <div class="product-footer">
                <span class="price">${{ product.price }} / {{ product.unit }}</span>
                <router-link :to="`/products/${product.id}`" class="view-button">View</router-link>
              </div>
            </div>
          </div>
        </div>
        <div class="view-all-container">
          <router-link to="/products" class="view-all-button">View All Products</router-link>
        </div>
      </div>
    </section>

    <!-- Farmer CTA Section -->
    <section class="farmer-cta">
      <div class="container">
        <div class="cta-content">
          <h2>Are You a Farmer?</h2>
          <p>Join MerrBio to connect directly with consumers and sell your fresh produce without middlemen.</p>
          <router-link to="/auth" class="cta-button primary">Join as a Farmer</router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home-view {
  --primary-color: #4caf50;
  --secondary-color: #2e7d32;
  --light-color: #f8f9fa;
  --dark-color: #333;
}

.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* Hero Section */
.hero-section {
  padding: 60px 0;
  background-color: var(--light-color);
}

.hero-section .container {
  display: grid;
  grid-template-columns: 1fr;
  gap: 40px;
}

.hero-content h1 {
  font-size: 2.5rem;
  margin-bottom: 20px;
  color: var(--dark-color);
}

.hero-content p {
  font-size: 1.2rem;
  margin-bottom: 30px;
  color: #555;
}

.cta-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.cta-button {
  display: inline-block;
  padding: 12px 24px;
  border-radius: 6px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
}

.cta-button.primary {
  background-color: var(--primary-color);
  color: white;
}

.cta-button.primary:hover {
  background-color: var(--secondary-color);
}

.cta-button.secondary {
  background-color: transparent;
  color: var(--primary-color);
  border: 2px solid var(--primary-color);
}

.cta-button.secondary:hover {
  background-color: rgba(76, 175, 80, 0.1);
}

.hero-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 10px;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

/* How It Works Section */
.how-it-works {
  padding: 60px 0;
  background-color: white;
}

.how-it-works h2 {
  text-align: center;
  margin-bottom: 40px;
  color: var(--dark-color);
}

.steps-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: 30px;
}

.step {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
  background-color: white;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

.step-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background-color: var(--primary-color);
  color: white;
  border-radius: 50%;
  margin: 0 auto 15px;
  font-size: 1.5rem;
  font-weight: bold;
}

.step h3 {
  margin-bottom: 10px;
  color: var(--dark-color);
}

/* Featured Products Section */
.featured-products {
  padding: 60px 0;
  background-color: var(--light-color);
}

.featured-products h2 {
  text-align: center;
  margin-bottom: 40px;
  color: var(--dark-color);
}

.products-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 30px;
}

.product-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-info {
  padding: 20px;
}

.product-info h3 {
  margin-bottom: 5px;
  color: var(--dark-color);
}

.farmer {
  color: var(--primary-color);
  font-size: 0.9rem;
  margin-bottom: 10px;
}

.description {
  color: #666;
  margin-bottom: 15px;
  line-height: 1.5;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}

.price {
  font-weight: bold;
  color: var(--dark-color);
}

.view-button {
  display: inline-block;
  padding: 8px 16px;
  background-color: var(--primary-color);
  color: white;
  border-radius: 4px;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.view-button:hover {
  background-color: var(--secondary-color);
}

.view-all-container {
  text-align: center;
  margin-top: 40px;
}

.view-all-button {
  display: inline-block;
  padding: 12px 24px;
  background-color: transparent;
  color: var(--primary-color);
  border: 2px solid var(--primary-color);
  border-radius: 6px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
}

.view-all-button:hover {
  background-color: rgba(76, 175, 80, 0.1);
}

/* Farmer CTA Section */
.farmer-cta {
  padding: 60px 0;
  background-color: var(--primary-color);
  color: white;
}

.cta-content {
  text-align: center;
  max-width: 800px;
  margin: 0 auto;
}

.cta-content h2 {
  margin-bottom: 20px;
}

.cta-content p {
  margin-bottom: 30px;
  font-size: 1.1rem;
}

.farmer-cta .cta-button.primary {
  background-color: white;
  color: var(--primary-color);
}

.farmer-cta .cta-button.primary:hover {
  background-color: rgba(255, 255, 255, 0.9);
}

/* Media Queries for Responsiveness */
@media (min-width: 768px) {
  .hero-section .container {
    grid-template-columns: 1fr 1fr;
    align-items: center;
  }

  .steps-container {
    grid-template-columns: repeat(3, 1fr);
  }

  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 1024px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .hero-content h1 {
    font-size: 3rem;
  }
}
</style>
