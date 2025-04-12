<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/authStore';
import { useI18n } from 'vue-i18n';

// Use the i18n composition API
const { t } = useI18n();

// Use the auth store to get auth state
const authStore = useAuthStore();
const isLoggedIn = computed(() => authStore.isLoggedIn);
const userRole = computed(() => authStore.userRole);
const isMobileMenuOpen = ref(false);
const router = useRouter();

// State for highlighting current active route
const activeRoute = ref('');

// Watch for route changes to highlight current page
onMounted(() => {
  activeRoute.value = window.location.pathname;
  
  // Close mobile menu when clicking outside
  document.addEventListener('click', handleOutsideClick);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleOutsideClick);
});

// Toggle mobile menu
const toggleMobileMenu = (event: Event) => {
  event.stopPropagation();
  isMobileMenuOpen.value = !isMobileMenuOpen.value;
};

// Handle navigation
const navigateTo = (path: string, event?: Event) => {
  if (event) event.preventDefault();
  activeRoute.value = path;
  router.push(path);
  isMobileMenuOpen.value = false;
};

// Close menu when clicking outside
const handleOutsideClick = (event: Event) => {
  const navElement = document.querySelector('.app-navigation');
  if (isMobileMenuOpen.value && navElement && !navElement.contains(event.target as Node)) {
    isMobileMenuOpen.value = false;
  }
};

// Check if a route is active
const isActive = (path: string): boolean => {
  return activeRoute.value === path || 
         (path !== '/' && activeRoute.value.startsWith(path));
};

// Handle logout
const logout = () => {
  authStore.logout();
  router.push('/');
  // Close mobile menu if open
  if (isMobileMenuOpen.value) {
    isMobileMenuOpen.value = false;
  }
};
</script>

<template>
  <nav class="app-navigation">
    <!-- Desktop Navigation -->
    <div class="desktop-nav">
      <div class="nav-items">
        <a href="/" 
           @click.prevent="navigateTo('/')" 
           class="nav-link" 
           :class="{ 'active': isActive('/') }">
           <span class="nav-icon">🏠</span>
           <span class="nav-text">{{ t('nav.home') }}</span>
        </a>
        <a href="/products" 
           @click.prevent="navigateTo('/products')" 
           class="nav-link" 
           :class="{ 'active': isActive('/products') }">
           <span class="nav-icon">🥕</span>
           <span class="nav-text">{{ t('nav.products') }}</span>
        </a>
      </div>      <div class="auth-section">
        <template v-if="isLoggedIn">
          <!-- Different dashboard links based on user role -->
          <template v-if="userRole === 'FARMER'">
            <a href="/dashboard/farmer" 
               @click.prevent="navigateTo('/dashboard/farmer')" 
               class="nav-link" 
               :class="{ 'active': isActive('/dashboard/farmer') }">
               <span class="nav-icon">🌱</span>
               <span class="nav-text">{{ t('dashboard.farmer.title') }}</span>
            </a>
          </template>
          <template v-else-if="userRole === 'CONSUMER'">
            <a href="/dashboard/consumer" 
               @click.prevent="navigateTo('/dashboard/consumer')" 
               class="nav-link" 
               :class="{ 'active': isActive('/dashboard/consumer') }">
               <span class="nav-icon">👤</span>
               <span class="nav-text">{{ t('dashboard.consumer.title') }}</span>
            </a>
          </template>
          <template v-else-if="userRole === 'ADMIN'">
            <a href="/dashboard/admin" 
               @click.prevent="navigateTo('/dashboard/admin')" 
               class="nav-link" 
               :class="{ 'active': isActive('/dashboard/admin') }">
               <span class="nav-icon">⚙️</span>
               <span class="nav-text">{{ t('nav.dashboard') }}</span>
            </a>
          </template>
          <button class="nav-button" @click="logout">{{ t('nav.logout') }}</button>
        </template>
        <template v-else>
          <a href="/auth" 
             @click.prevent="navigateTo('/auth')" 
             class="nav-button primary">
             {{ t('nav.login') }} / {{ t('nav.register') }}
          </a>
        </template>
      </div>
    </div>

    <!-- Mobile Navigation Toggle Button -->
    <div class="mobile-menu-toggle" @click="toggleMobileMenu">
      <div class="hamburger-icon" :class="{ 'open': isMobileMenuOpen }">
        <span></span>
        <span></span>
        <span></span>
      </div>
    </div>

    <!-- Mobile Navigation Menu -->
    <div class="mobile-nav" :class="{ 'open': isMobileMenuOpen }">
      <div class="mobile-nav-content">
        <a href="/" 
           @click.prevent="navigateTo('/')" 
           class="nav-link" 
           :class="{ 'active': isActive('/') }">
           <span class="nav-icon">🏠</span>
           <span class="nav-text">{{ t('nav.home') }}</span>
        </a>
        <a href="/products" 
           @click.prevent="navigateTo('/products')" 
           class="nav-link" 
           :class="{ 'active': isActive('/products') }">
           <span class="nav-icon">🥕</span>
           <span class="nav-text">{{ t('nav.products') }}</span>
        </a>        <template v-if="isLoggedIn">
          <!-- Role-based dashboard links for mobile menu -->
          <template v-if="userRole === 'FARMER'">
            <a href="/dashboard/farmer" 
               @click.prevent="navigateTo('/dashboard/farmer')" 
               class="nav-link" 
               :class="{ 'active': isActive('/dashboard/farmer') }">
               <span class="nav-icon">🌱</span>
               <span class="nav-text">{{ t('dashboard.farmer.title') }}</span>
            </a>
          </template>
          <template v-else-if="userRole === 'CONSUMER'">
            <a href="/dashboard/consumer" 
               @click.prevent="navigateTo('/dashboard/consumer')" 
               class="nav-link" 
               :class="{ 'active': isActive('/dashboard/consumer') }">
               <span class="nav-icon">👤</span>
               <span class="nav-text">{{ t('dashboard.consumer.title') }}</span>
            </a>
          </template>
          <template v-else-if="userRole === 'ADMIN'">
            <a href="/dashboard/admin" 
               @click.prevent="navigateTo('/dashboard/admin')" 
               class="nav-link" 
               :class="{ 'active': isActive('/dashboard/admin') }">
               <span class="nav-icon">⚙️</span>
               <span class="nav-text">{{ t('nav.dashboard') }}</span>
            </a>
          </template>
          <button class="nav-button full-width" @click="logout">{{ t('nav.logout') }}</button>
        </template>
        <template v-else>
          <a href="/auth" 
             @click.prevent="navigateTo('/auth')" 
             class="nav-button primary full-width">
             {{ t('nav.login') }} / {{ t('nav.register') }}
          </a>
        </template>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.app-navigation {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  width: 100%;
}

/* Desktop Navigation */
.desktop-nav {
  display: none;
  width: 100%;
  justify-content: space-between;
  align-items: center;
}

.nav-items {
  display: flex;
  align-items: center;
  gap: 12px;
}

.auth-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-link {
  position: relative;
  display: flex;
  align-items: center;
  gap: 6px;
  text-decoration: none;
  color: #333;
  font-weight: 500;
  padding: 10px 16px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.nav-link:hover {
  background-color: rgba(76, 175, 80, 0.08);
  color: #4caf50;
}

.nav-link.active {
  color: #4caf50;
  background-color: rgba(76, 175, 80, 0.12);
  font-weight: 600;
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  height: 3px;
  width: 20px;
  background-color: #4caf50;
  border-radius: 2px;
}

.nav-icon {
  font-size: 1.1rem;
}

.nav-text {
  font-size: 1rem;
}

.nav-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
  background-color: #f5f5f5;
  border: none;
  color: #333;
  font-size: 0.95rem;
}

.nav-button:hover {
  background-color: #e8e8e8;
}

.nav-button.primary {
  background-color: #4caf50;
  color: white;
}

.nav-button.primary:hover {
  background-color: #3d8b40;
}

.full-width {
  width: 100%;
  text-align: center;
}

/* Mobile Menu Toggle */
.mobile-menu-toggle {
  display: block;
  z-index: 50;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.mobile-menu-toggle:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.hamburger-icon {
  width: 24px;
  height: 20px;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.hamburger-icon span {
  display: block;
  height: 3px;
  width: 100%;
  background-color: #333;
  border-radius: 3px;
  transition: all 0.3s ease;
}

.hamburger-icon.open span:nth-child(1) {
  transform: translateY(8px) rotate(45deg);
}

.hamburger-icon.open span:nth-child(2) {
  opacity: 0;
}

.hamburger-icon.open span:nth-child(3) {
  transform: translateY(-8px) rotate(-45deg);
}

/* Mobile Navigation */
.mobile-nav {
  display: flex;
  flex-direction: column;
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  background-color: #fff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  padding: 0;
  border-radius: 12px;
  width: 250px;
  transform: translateY(-20px) scale(0.95);
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  z-index: 40;
  overflow: hidden;
}

.mobile-nav.open {
  transform: translateY(0) scale(1);
  opacity: 1;
  visibility: visible;
}

.mobile-nav-content {
  display: flex;
  flex-direction: column;
  padding: 16px;
  gap: 8px;
}

.mobile-nav .nav-link {
  padding: 12px 16px;
  border-radius: 8px;
  display: flex;
  gap: 10px;
}

.mobile-nav .nav-button {
  margin-top: 8px;
  padding: 12px 20px;
}

@media (min-width: 768px) {
  .desktop-nav {
    display: flex;
  }

  .mobile-menu-toggle {
    display: none;
  }

  .mobile-nav {
    display: none;
  }
}
</style>
