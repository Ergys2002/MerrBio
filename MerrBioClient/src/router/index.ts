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

// Route guard placeholder for protected routes
router.beforeEach((to, from, next) => {
  // This will be implemented later with proper authentication
  // For now, let's just allow access to all routes
  if (to.meta.requiresAuth) {
    // Check authentication status here later
    next() // For now, just proceed
  } else {
    next()
  }
})

export default router