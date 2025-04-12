import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Product } from './productStore'

export interface PurchaseRequest {
  productId: string
  quantity: number
  message?: string
  requestedAt: string
}

export interface RequestState {
  pendingRequests: PurchaseRequest[]
}

export const useRequestStore = defineStore('requests', () => {
  const state = ref<RequestState>({
    pendingRequests: []
  })

  // Getters
  const requestCount = computed(() => state.value.pendingRequests.length)

  // Actions
  const addRequest = (productId: string, quantity: number, message?: string) => {
    const existingRequest = state.value.pendingRequests.find(r => r.productId === productId)
    
    if (existingRequest) {
      // Update existing request
      existingRequest.quantity = quantity
      existingRequest.message = message
      existingRequest.requestedAt = new Date().toISOString()
    } else {
      // Add new request
      state.value.pendingRequests.push({
        productId,
        quantity,
        message,
        requestedAt: new Date().toISOString()
      })
    }
  }

  const removeRequest = (productId: string) => {
    state.value.pendingRequests = state.value.pendingRequests.filter(
      r => r.productId !== productId
    )
  }

  const clearRequests = () => {
    state.value.pendingRequests = []
  }

  const hasRequest = (productId: string) => {
    return state.value.pendingRequests.some(r => r.productId === productId)
  }

  return {
    requestCount,
    pendingRequests: computed(() => state.value.pendingRequests),
    addRequest,
    removeRequest,
    clearRequests,
    hasRequest
  }
})
