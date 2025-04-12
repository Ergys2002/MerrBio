import apiClient from './api'; // Now imports api.ts
import type { AxiosError } from 'axios';

// --- Interfaces based on Swagger Documentation ---

// Base registration data shared by both farmer and customer
interface BaseRegisterData {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  birthDate?: string; // Optional based on your form/requirements
  phoneNumber?: string; // Optional based on your form/requirements
  gender?: 'MALE' | 'FEMALE' | 'OTHER'; // Optional based on your form/requirements
}

// Customer-specific registration data
interface CustomerRegisterData extends BaseRegisterData {}

// Farmer-specific registration data
interface FarmerRegisterData extends BaseRegisterData {
  farmName: string;
  farmLocation: string;
  bio?: string;
}

// Type for registration data that includes role for internal use
interface RegisterUserData extends BaseRegisterData {
  role: 'FARMER' | 'CONSUMER' | 'ADMIN';
}

interface LoginCredentials {
  email: string;
  password: string;
}

interface AuthResponse {
  accessToken: string;
  refreshToken: string;
}

interface RefreshTokenRequest {
  refreshToken: string;
}

interface RefreshTokenResponse extends AuthResponse {
  tokenType?: string;
}

interface LogoutRequest {
  refreshToken: string;
}

// Define a type for the error structure from the backend if known
// Example: interface ApiError { message: string; statusCode?: number; errors?: Record<string, string[]> }
type ApiError = any; // Use a more specific type if available

// Helper to handle API errors
const handleApiError = (error: unknown): ApiError => {
    const axiosError = error as AxiosError<ApiError>;
    console.error('API Error:', axiosError.response?.data || axiosError.message);
    // Return the structured error data from the response if available, otherwise a generic error
    return axiosError.response?.data || { message: axiosError.message || 'An unknown error occurred' };
};


// --- Service Functions ---

/**
 * Register a new customer.
 */
export const registerCustomer = async (userData: CustomerRegisterData): Promise<AuthResponse> => {
  try {
    const response = await apiClient.post<AuthResponse>('/auth/register/customer', userData);
    return response.data;
  } catch (error) {
    throw handleApiError(error);
  }
};

/**
 * Register a new farmer.
 */
export const registerFarmer = async (userData: FarmerRegisterData): Promise<AuthResponse> => {
  try {
    const response = await apiClient.post<AuthResponse>('/auth/register/farmer', userData);
    return response.data;
  } catch (error) {
    throw handleApiError(error);
  }
};

/**
 * Register a new user based on their role.
 */
export const register = async (userData: RegisterUserData): Promise<AuthResponse> => {
  try {
    const { role, ...userDataWithoutRole } = userData;
    
    if (role === 'FARMER') {
      // Type assertion since we know this will have the farmer fields
      return await registerFarmer(userDataWithoutRole as FarmerRegisterData);
    } else {
      // Default to customer registration for any non-farmer role
      return await registerCustomer(userDataWithoutRole as CustomerRegisterData);
    }
  } catch (error) {
    throw handleApiError(error);
  }
};

/**
 * Log in a user.
 */
export const login = async (credentials: LoginCredentials): Promise<AuthResponse> => {
  try {
    const response = await apiClient.post<AuthResponse>('/auth/login', credentials);
    // Store tokens immediately after login (can also be done in the store action)
    // This local storage interaction might be better centralized in the auth store actions
    if (typeof window !== 'undefined' && response.data.accessToken && response.data.refreshToken) {
      localStorage.setItem('accessToken', response.data.accessToken);
      localStorage.setItem('refreshToken', response.data.refreshToken);
      // Interceptor should handle setting the default header, but explicit setting can be done here if needed
      // apiClient.defaults.headers.common['Authorization'] = `Bearer ${response.data.accessToken}`;
    }
    return response.data;
  } catch (error) {
     // Clear any potentially stale tokens on login failure
     if (typeof window !== 'undefined') {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
     }
    throw handleApiError(error);
  }
};

/**
 * Log out a user using their refresh token.
 */
export const logout = async (refreshToken: string): Promise<void> => { // Returns void or a specific success type
  try {
    const requestBody: LogoutRequest = { refreshToken };
    await apiClient.post('/auth/logout', requestBody);
    // Clear tokens from storage (best handled centrally in the auth store logout action)
    // if (typeof window !== 'undefined') {
    //   localStorage.removeItem('accessToken');
    //   localStorage.removeItem('refreshToken');
    //   delete apiClient.defaults.headers.common['Authorization'];
    // }
  } catch (error) {
    // Even if logout fails on the server, the client should proceed as if logged out
    // Token clearing should happen regardless (handled in store action)
    console.error('Logout API call failed:', handleApiError(error));
    // Re-throwing might not be necessary if the store handles cleanup anyway
    // throw handleApiError(error);
  }
};

/**
 * Log out from all devices for the current user.
 * Requires the user to be authenticated (valid accessToken).
 */
export const logoutAll = async (): Promise<void> => { // Returns void or a specific success type
  try {
    // This endpoint requires authentication (accessToken in header via interceptor)
    await apiClient.post('/auth/logout-all');
    // Clear tokens from storage (best handled centrally in the auth store logout action)
  } catch (error) {
    console.error('Logout from all devices failed:', handleApiError(error));
    // Token clearing should happen regardless (handled in store action)
    // throw handleApiError(error);
  }
};

/**
 * Refresh the access token using a refresh token.
 * Note: This is primarily handled by the response interceptor in api.ts.
 * This function might be useful for manual refresh scenarios if needed.
 */
export const refreshToken = async (token: string): Promise<RefreshTokenResponse> => {
  try {
    const requestBody: RefreshTokenRequest = { refreshToken: token };
    const response = await apiClient.post<RefreshTokenResponse>('/auth/refresh-token', requestBody);
    // Update stored tokens (best handled centrally in the auth store)
    // if (typeof window !== 'undefined' && response.data.accessToken && response.data.refreshToken) {
    //   localStorage.setItem('accessToken', response.data.accessToken);
    //   localStorage.setItem('refreshToken', response.data.refreshToken);
    //   apiClient.defaults.headers.common['Authorization'] = `Bearer ${response.data.accessToken}`;
    // }
    return response.data;
  } catch (error) {
    console.error('Token refresh failed:', handleApiError(error));
    // Handle refresh failure (e.g., logout user - best handled in store)
    // if (typeof window !== 'undefined') {
    //   localStorage.removeItem('accessToken');
    //   localStorage.removeItem('refreshToken');
    //   delete apiClient.defaults.headers.common['Authorization'];
    //   window.location.href = '/login';
    // }
    throw handleApiError(error);
  }
};

// Exporting all functions as a single object can also be convenient
const authService = {
  register,
  login,
  logout,
  logoutAll,
  refreshToken,
};

export default authService;