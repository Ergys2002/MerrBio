import { defineStore } from 'pinia';

export interface User {
  id: number;
  name: string;
  email: string;
  password?: string; // Not stored in real applications, only for mock
  role: 'FARMER' | 'CONSUMER' | 'ADMIN';
}

export interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: string | null;
}

// Mock users for testing
const mockUsers: User[] = [
  {
    id: 1,
    name: 'John Farmer',
    email: 'farmer@example.com',
    password: 'password123', // In real app, passwords would be hashed
    role: 'FARMER'
  },
  {
    id: 2,
    name: 'Jane Consumer',
    email: 'consumer@example.com',
    password: 'password123',
    role: 'CONSUMER'
  },
  {
    id: 3,
    name: 'Admin User',
    email: 'admin@example.com',
    password: 'admin123',
    role: 'ADMIN'
  }
];

// Define the auth store with proper typing
export const useAuthStore = defineStore('auth', {
  state: (): AuthState => {
    // Try to get stored user data from localStorage
    const storedUser = localStorage.getItem('user');
    
    return {
      user: storedUser ? JSON.parse(storedUser) : null,
      isAuthenticated: !!storedUser,
      loading: false,
      error: null
    };
  },
  
  getters: {
    /**
     * Get current user
     */
    currentUser: (state: AuthState) => state.user,
    
    /**
     * Check if user is authenticated
     */
    isLoggedIn: (state: AuthState) => state.isAuthenticated,
    
    /**
     * Get user role
     */
    userRole: (state: AuthState) => state.user?.role || null
  },
    actions: {
    /**
     * Login user with email and password
     */
    async login(email: string, password: string): Promise<User> {
      this.$patch({ loading: true, error: null });
      
      try {
        // Simulate API call delay
        await new Promise(resolve => setTimeout(resolve, 800));
        
        // Find user with matching credentials in mock data
        const user = mockUsers.find(
          u => u.email.toLowerCase() === email.toLowerCase() && u.password === password
        );
        
        if (!user) {
          throw new Error('Invalid email or password');
        }
        
        // Remove password before storing user data
        const { password: _, ...userWithoutPassword } = user;
          // Set auth state and store in localStorage
        this.$patch({
          user: userWithoutPassword,
          isAuthenticated: true
        });
        localStorage.setItem('user', JSON.stringify(userWithoutPassword));
        
        return user;      } catch (error: any) {
        this.$patch({ error: error.message || 'Login failed' });
        throw error;
      } finally {
        this.$patch({ loading: false });
      }
    },
      /**
     * Register a new user
     */
    async register(userData: { name: string; email: string; password: string; role: 'FARMER' | 'CONSUMER' }): Promise<Omit<User, 'password'>> {
      this.$patch({ loading: true, error: null });
      
      try {
        // Simulate API call delay
        await new Promise(resolve => setTimeout(resolve, 800));
        
        // Check if email already exists
        const existingUser = mockUsers.find(
          u => u.email.toLowerCase() === userData.email.toLowerCase()
        );
        
        if (existingUser) {
          throw new Error('Email already in use');
        }
        
        // Create new user with generated ID
        const newUser: User = {
          id: Math.max(0, ...mockUsers.map(u => u.id)) + 1,
          name: userData.name,
          email: userData.email,
          password: userData.password, // In a real app, this would be hashed
          role: userData.role
        };
        
        // Add to mock users array (in a real app, this would be saved in a database)
        mockUsers.push(newUser);
          // Remove password before storing user data
        const { password: _, ...userWithoutPassword } = newUser;
        
        // Set auth state and store in localStorage
        this.$patch({
          user: userWithoutPassword,
          isAuthenticated: true
        });
        localStorage.setItem('user', JSON.stringify(userWithoutPassword));
        
        return userWithoutPassword;      } catch (error: any) {
        this.$patch({ error: error.message || 'Registration failed' });
        throw error;
      } finally {
        this.$patch({ loading: false });
      }
    },
    
    /**
     * Logout current user
     */
    logout() {
      this.user = null;
      this.isAuthenticated = false;
      localStorage.removeItem('user');
    },
    
    /**
     * Check authentication status
     */
    checkAuth() {
      const storedUser = localStorage.getItem('user');
      if (storedUser) {
        this.user = JSON.parse(storedUser);
        this.isAuthenticated = true;
      }
    }
  }
});
