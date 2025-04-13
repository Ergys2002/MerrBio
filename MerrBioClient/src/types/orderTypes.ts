/**
 * Represents an item within an order creation request.
 */
export interface OrderCreateItemPayload {
  productId: string; // UUID of the product
  quantity: number;
}

/**
 * Represents the payload for creating a new order.
 */
export interface OrderCreatePayload {
  items: OrderCreateItemPayload[];
  notes?: string; // Optional notes
}

/**
 * Represents a single item within a retrieved order.
 */
export interface OrderItem {
  id: string; // UUID of the OrderItem
  productId: string; // UUID of the Product
  productName: string;
  productThumbnailUrl: string | null; // Assuming it can be null
  quantity: number;
  price: number; // Price per unit at the time of order
  totalPrice: number; // quantity * price
  farmerId: string; // UUID of the Farmer
  farmerName: string;
  farmName: string;
}

/**
 * Represents the status of an order.
 */
export type OrderStatus = 'PROCESSING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED' | 'PENDING'; // Added PENDING based on common patterns

/**
 * Represents a retrieved order object.
 */
export interface Order {
  id: string; // UUID of the Order
  customerId: string; // UUID of the Customer
  customerName: string;
  customerEmail: string;
  items: OrderItem[];
  totalPrice: number;
  status: OrderStatus;
  notes: string | null; // Assuming notes can be null or empty
  createdAt: string; // ISO Date string
  updatedAt: string; // ISO Date string
}