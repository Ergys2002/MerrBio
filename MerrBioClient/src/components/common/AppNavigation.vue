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
        <!-- Dashboard link when logged in -->
        <template v-if="isLoggedIn">
          <a v-if="userRole === 'FARMER'"
             href="/dashboard/farmer"
             @click.prevent="navigateTo('/dashboard/farmer')"
             class="nav-link"
             :class="{ 'active': isActive('/dashboard/farmer') }">
             <span class="nav-icon">🌱</span>
             <span class="nav-text">{{ t('dashboard.farmer.title') }}</span>
          </a>
          <a v-else-if="userRole === 'CONSUMER'"
             href="/dashboard/consumer"
             @click.prevent="navigateTo('/dashboard/consumer')"
             class="nav-link"
             :class="{ 'active': isActive('/dashboard/consumer') }">
             <span class="nav-icon">👤</span>
             <span class="nav-text">{{ t('dashboard.consumer.title') }}</span>
          </a>
          <a v-else-if="userRole === 'ADMIN'"
             href="/dashboard/admin"
             @click.prevent="navigateTo('/dashboard/admin')"
             class="nav-link"
             :class="{ 'active': isActive('/dashboard/admin') }">
             <span class="nav-icon">⚙️</span>
             <span class="nav-text">{{ t('nav.dashboard') }}</span>
          </a>
        </template>
      </div>
      <div class="auth-section">
        <template v-if="isLoggedIn">
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
        </a>
        <!-- Dashboard link in mobile menu -->
        <template v-if="isLoggedIn">
          <a v-if="userRole === 'FARMER'"
             href="/dashboard/farmer"
             @click.prevent="navigateTo('/dashboard/farmer')"
             class="nav-link"
             :class="{ 'active': isActive('/dashboard/farmer') }">
             <span class="nav-icon">🌱</span>
             <span class="nav-text">{{ t('dashboard.farmer.title') }}</span>
          </a>
          <a v-else-if="userRole === 'CONSUMER'"
             href="/dashboard/consumer"
             @click.prevent="navigateTo('/dashboard/consumer')"
             class="nav-link"
             :class="{ 'active': isActive('/dashboard/consumer') }">
             <span class="nav-icon">👤</span>
             <span class="nav-text">{{ t('dashboard.consumer.title') }}</span>
          </a>
          <a v-else-if="userRole === 'ADMIN'"
             href="/dashboard/admin"
             @click.prevent="navigateTo('/dashboard/admin')"
             class="nav-link"
             :class="{ 'active': isActive('/dashboard/admin') }">
             <span class="nav-icon">⚙️</span>
             <span class="nav-text">{{ t('nav.dashboard') }}</span>
          </a>
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
  gap: 16px;
}

.auth-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* Modern Nav Link Style */
.nav-link {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  color: #333;
  font-weight: 500;
  padding: 10px 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
  overflow: hidden;
}

/* Background shift effect on hover */
.nav-link::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(120deg, rgba(76, 175, 80, 0.08) 0%, rgba(76, 175, 80, 0.12) 100%);
  border-radius: 12px;
  opacity: 0;
  transform: translateY(100%);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  z-index: -1;
}

.nav-link:hover::before {
  opacity: 1;
  transform: translateY(0);
}

.nav-link:hover {
  color: #4caf50;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

/* Active link styling */
.nav-link.active {
  color: #4caf50;
  font-weight: 600;
  background: linear-gradient(120deg, rgba(76, 175, 80, 0.1) 0%, rgba(76, 175, 80, 0.16) 100%);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.1);
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  height: 3px;
  width: 100%;
  background: linear-gradient(90deg, #4caf50, #8bc34a);
  border-radius: 3px 3px 0 0;
  transform-origin: center;
  animation: linkIndicator 0.3s ease-out forwards;
}

@keyframes linkIndicator {
  from { transform: scaleX(0); }
  to { transform: scaleX(1); }
}

.nav-icon {
  font-size: 1.2rem;
  transition: transform 0.3s ease;
}

.nav-link:hover .nav-icon {
  transform: scale(1.2);
}

.nav-text {
  font-size: 1rem;
}

/* Modern button styling */
.nav-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 10px 20px;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  text-decoration: none;
  background-color: #f8f8f8;
  border: none;
  color: #333;
  font-size: 0.95rem;
  position: relative;
  overflow: hidden;
}

.nav-button::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 150%;
  height: 150%;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  transform: translate(-50%, -50%) scale(0);
  transition: transform 0.5s;
}

.nav-button:hover::before {
  transform: translate(-50%, -50%) scale(1);
}

.nav-button:hover {
  background-color: #f0f0f0;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.nav-button.primary {
  background: linear-gradient(135deg, #4caf50, #8bc34a);
  color: white;
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.2);
}

.nav-button.primary:hover {
  background: linear-gradient(135deg, #43a047, #7cb342);
  box-shadow: 0 6px 16px rgba(76, 175, 80, 0.3);
}

.nav-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.full-width {
  width: 100%;
  text-align: center;
}

/* Modern hamburger menu */
.mobile-menu-toggle {
  display: block;
  z-index: 50;
  cursor: pointer;
  padding: 10px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.mobile-menu-toggle:hover {
  background-color: rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.hamburger-icon {
  width: 24px;
  height: 20px;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  transition: transform 0.3s ease;
}

.hamburger-icon span {
  display: block;
  height: 2px;
  width: 100%;
  background-color: #333;
  border-radius: 4px;
  transition: all 0.4s cubic-bezier(0.68, -0.6, 0.32, 1.6);
}

.hamburger-icon.open {
  transform: rotate(180deg);
}

.hamburger-icon.open span:nth-child(1) {
  transform: rotate(45deg) translate(6px, 6px);
  width: 80%;
}

.hamburger-icon.open span:nth-child(2) {
  opacity: 0;
  transform: translateX(20px);
}

.hamburger-icon.open span:nth-child(3) {
  transform: rotate(-45deg) translate(6px, -6px);
  width: 80%;
}

/* Modern mobile navigation */
.mobile-nav {
  display: flex;
  flex-direction: column;
  position: absolute;
  top: calc(100% + 15px);
  right: 0;
  background-color: #fff;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(0, 0, 0, 0.05);
  padding: 0;
  border-radius: 16px;
  width: 280px;
  transform: translateY(-20px) scale(0.95);
  opacity: 0;
  visibility: hidden;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  z-index: 40;
  overflow: hidden;
  backdrop-filter: blur(10px);
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
  padding: 14px 16px;
  border-radius: 12px;
  display: flex;
  gap: 10px;
  animation: fadeIn 0.5s ease forwards;
  opacity: 0;
  transform: translateY(10px);
}

@keyframes fadeIn {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.mobile-nav .nav-link:nth-child(1) {
  animation-delay: 0.1s;
}

.mobile-nav .nav-link:nth-child(2) {
  animation-delay: 0.15s;
}

.mobile-nav .nav-link:nth-child(3) {
  animation-delay: 0.2s;
}

.mobile-nav .nav-button {
  margin-top: 12px;
  padding: 14px 20px;
  animation: fadeIn 0.5s 0.25s ease forwards;
  opacity: 0;
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

@media (max-width: 768px) {
  .mobile-menu-toggle {
    margin-right: -8px;
  }
}
</style>
