import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'home',
    component: () => import('../views/HomeView.vue')
  },
  {
    path: '/products',
    name: 'products',
    component: () => import('../views/ProductsView.vue')
  },
  {
    path: '/products/:id',
    name: 'product-detail',
    component: () => import('../views/ProductDetailView.vue')
  },
  {
    path: '/auth',
    name: 'auth',
    component: () => import('../views/AuthView.vue')
  },
  {
    path: '/dashboard/farmer',
    name: 'farmer-dashboard',
    component: () => import('../views/FarmerDashboardView.vue'),
    meta: { requiresAuth: true, role: 'FARMER' }
  },
  {
    path: '/dashboard/consumer',
    name: 'consumer-dashboard',
    component: () => import('../views/ConsumerDashboardView.vue'),
    meta: { requiresAuth: true, role: 'CONSUMER' }
  },
  {
    path: '/dashboard/admin',
    name: 'admin-dashboard',
    component: () => import('../views/AdminDashboardView.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

import { useAuthStore } from '../stores/authStore'

// Route guard for protected routes
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth) {
    if (!authStore.isLoggedIn) {
      next('/auth')
      return
    }

    // Only fetch profile when accessing protected routes and profile not already loaded
    if (!authStore.user) {
      try {
        await authStore.fetchUserProfile()
        
        // Check role-based access
        if (to.meta.role && authStore.userRole !== to.meta.role) {
          next('/')
          return
        }
      } catch (error) {
        console.error('Failed to fetch user profile:', error)
        authStore.clearAuthData()
        next('/auth')
        return
      }
    }
  }
  next()
})

export default router