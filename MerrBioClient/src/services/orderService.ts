import axiosInstance from './axiosConfig';
import type { Order, OrderCreatePayload } from '@/types/orderTypes'; // Assuming types exist or will be created

/**
 * Service for handling order-related API calls.
 */
export const OrderService = {
  /**
   * Creates a new order.
   * @param payload - The order creation payload.
   * @returns A promise that resolves with the created order data.
   */
  async createOrder(payload: OrderCreatePayload): Promise<Order> {
    try {
      const response = await axiosInstance.post<Order>('/orders', payload);
      return response.data;
    } catch (error) {
      console.error('Error creating order:', error);
      // Consider more specific error handling based on API responses
      throw new Error('Failed to create order'); 
    }
  },

  // Add other order-related methods here (e.g., getOrderById, getCustomerOrders)
};