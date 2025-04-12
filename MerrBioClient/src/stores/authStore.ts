import { defineStore } from 'pinia';
import authService from '@/services/authService'; // Import the real service
import apiClient from '@/services/api'; // Import apiClient to potentially fetch user data

// Define the structure for user data fetched from the backend
// Adjust this based on the actual user profile endpoint response
export interface UserProfile {
  id: number; // Or string, depending on your backend
  email: string;
  firstName: string;
  lastName: string;
  role: 'FARMER' | 'CONSUMER' | 'ADMIN'; // Adjust roles as needed
  // Add other relevant user fields
}

export interface AuthState {
  user: UserProfile | null; // Store fetched user profile
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: string | null;
}

// Helper function to get tokens from localStorage
const getStoredTokens = (): { accessToken: string | null; refreshToken: string | null } => {
  if (typeof window !== 'undefined') {
    return {
      accessToken: localStorage.getItem('accessToken'),
      refreshToken: localStorage.getItem('refreshToken'),
    };
  }
  return { accessToken: null, refreshToken: null };
};

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => {
    const { accessToken, refreshToken } = getStoredTokens();
    return {
      user: null, // User profile fetched separately
      accessToken: accessToken,
      refreshToken: refreshToken,
      isAuthenticated: !!accessToken, // Initially based on accessToken presence
      loading: false,
      error: null,
    };
  },

  getters: {
    currentUser: (state: AuthState): UserProfile | null => state.user,
    isLoggedIn: (state: AuthState): boolean => state.isAuthenticated,
    userRole: (state: AuthState): string | null => state.user?.role || null,
    isLoading: (state: AuthState): boolean => state.loading,
    authError: (state: AuthState): string | null => state.error,
  },

  actions: {
    /**
     * Fetch user profile from the backend using the access token.
     * Assumes a GET /users/me endpoint exists. Adjust if necessary.
     */    async fetchUserProfile(): Promise<void> {
      if (!this.accessToken) {
        console.warn('No access token available to fetch user profile.');
        this.$patch({ user: null, isAuthenticated: false });
        return;
      }
      this.$patch({ loading: true, error: null });
      try {
        // Ensure the Authorization header is set for this request
        const response = await apiClient.get<UserProfile>('/users/me');
        if (!response.data) {
          throw new Error('No profile data received');
        }
        this.$patch({ 
          user: response.data, 
          isAuthenticated: true, 
          loading: false,
          error: null
        });
      } catch (error: any) {
        const errorMessage = error.response?.data?.message || error.message || 'Failed to fetch user profile';
        console.error('Profile fetch error:', errorMessage);
        this.$patch({ error: errorMessage, loading: false });
        this.clearAuthData();
        this.$patch({ error: 'Failed to fetch user details.', loading: false });
        // Optionally re-throw or handle differently
      }
    },

    /**
     * Store authentication tokens and update state.
     */
    setAuthData(accessToken: string, refreshToken: string): void {
      this.$patch({
        accessToken: accessToken,
        refreshToken: refreshToken,
        isAuthenticated: true,
        error: null,
      });
      if (typeof window !== 'undefined') {
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
      }
      // Update apiClient default headers (interceptor should also handle this)
      apiClient.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
    },

    /**
     * Clear authentication tokens and user data from state and storage.
     */
    clearAuthData(): void {
      this.$patch({
        user: null,
        accessToken: null,
        refreshToken: null,
        isAuthenticated: false,
        error: null,
      });
      if (typeof window !== 'undefined') {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
      }
      // Remove auth header from apiClient defaults
      delete apiClient.defaults.headers.common['Authorization'];
    },    /**
     * Login user using the authService.
     */    async login(credentials: { email: string; password: string }): Promise<void> {
      this.$patch({ loading: true, error: null });
      try {
        const data = await authService.login(credentials);
        this.setAuthData(data.accessToken, data.refreshToken);
        // Fetch user profile immediately after login to get role information
        await this.fetchUserProfile();
      } catch (error: any) {
        console.error('Store login failed:', error);
        this.clearAuthData(); // Ensure clean state on login failure
        this.$patch({ error: error.message || 'Login failed' });
        throw error; // Re-throw for component handling
      } finally {
        this.$patch({ loading: false });
      }
    },

    /**
     * Register user using the authService.
     * userData structure should match authService.register requirements.
     */
    async register(userData: any): Promise<void> { // Use specific type for userData if possible
      this.$patch({ loading: true, error: null });
      try {
        const data = await authService.register(userData);
        this.setAuthData(data.accessToken, data.refreshToken);
        // Fetch user profile after successful registration
        await this.fetchUserProfile();
      } catch (error: any) {
        console.error('Store registration failed:', error);
        this.clearAuthData(); // Ensure clean state on registration failure
        this.$patch({ error: error.message || 'Registration failed' });
        throw error; // Re-throw for component handling
      } finally {
        this.$patch({ loading: false });
      }
    },

    /**
     * Logout user using the authService.
     */
    async logout(): Promise<void> {
      const tokenToInvalidate = this.refreshToken;
      this.$patch({ loading: true }); // Indicate loading state
      try {
        if (tokenToInvalidate) {
          await authService.logout(tokenToInvalidate);
        } else {
          console.warn('No refresh token found for logout.');
        }
      } catch (error: any) {
        console.error('Store logout failed:', error);
        // Still clear local data even if server logout fails
      } finally {
        this.clearAuthData();
        this.$patch({ loading: false });
        // Optionally redirect to login page
        // router.push('/login'); // If router is accessible here
      }
    },

    /**
     * Check authentication status on app load.
     * If tokens exist, validate them by fetching the user profile.
     */
    async checkAuth(): Promise<void> {
      if (this.accessToken) {
        console.log('checkAuth: Access token found, attempting to fetch user profile.');
        await this.fetchUserProfile(); // This will also handle token validation/expiry
      } else {
        console.log('checkAuth: No access token found.');
        this.clearAuthData(); // Ensure consistent state if no token
      }
    },
  },
});
