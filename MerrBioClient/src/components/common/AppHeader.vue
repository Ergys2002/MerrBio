<script setup lang="ts">
import AppNavigation from './AppNavigation.vue';
import LanguageSelector from './LanguageSelector.vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
</script>

<template>
  <header class="app-header">
    <div class="container">
      <div class="header-content">
        <div class="logo">
          <router-link to="/">
            <img 
              src="@/assets/header_logo.png" 
              alt="MerrBio Logo"
              class="logo-image"
            >
          </router-link>
        </div>
        
        <div class="header-right">
          <LanguageSelector />
          <AppNavigation />
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  background-color: #ffffff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 16px 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo a {
  text-decoration: none;
  display: flex;
  flex-direction: column;
}

.logo h1 {
  color: #4caf50;
  font-size: 1.8rem;
  margin: 0;
  font-weight: 700;
}

.tagline {
  color: #666;
  font-size: 0.8rem;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.language-selector-placeholder {
  cursor: pointer;
  color: #333;
  font-weight: 500;
}

@media (max-width: 767px) {
  .logo h1 {
    font-size: 1.5rem;
  }
  
  .tagline {
    font-size: 0.7rem;
  }
  
  .header-right {
    gap: 16px;
  }
}

.logo-image {
  height: 40px; /* Base height */
  width: auto; /* Maintain aspect ratio */
  max-width: 100%;
}

@media (max-width: 767px) {
  .logo-image {
    height: 30px; /* Smaller on mobile */
  }
}
</style>
