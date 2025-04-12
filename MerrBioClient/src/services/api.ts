import axios from 'axios';
import type { AxiosInstance, AxiosError, InternalAxiosRequestConfig, AxiosResponse } from 'axios';

// Define a type for the refresh token response data
interface RefreshTokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType?: string; // Optional based on your API
}

// Create an Axios instance with type annotation
const apiClient: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api/v1', // Adjust if your API base URL is different
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig): InternalAxiosRequestConfig => {
    // Add authorization token to headers if available
    // Ensure localStorage access is safe (check if window is defined)
    const token = typeof window !== 'undefined' ? localStorage.getItem('accessToken') : null;
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError): Promise<AxiosError> => {
    return Promise.reject(error);
  }
);

// Response interceptor
apiClient.interceptors.response.use(
  (response: AxiosResponse): AxiosResponse => {
    return response;
  },
  async (error: AxiosError): Promise<AxiosResponse | AxiosError> => {
    // Add type check for error.config and error.response
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    if (!originalRequest || !error.response) {
      return Promise.reject(error);
    }

    // Handle token expiry and refresh (status 401 Unauthorized)
    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true; // Mark request to prevent infinite retry loops
      try {
        const refreshToken = typeof window !== 'undefined' ? localStorage.getItem('refreshToken') : null;
        if (!refreshToken) {
          console.error('No refresh token available.');
          // Handle logout or redirect logic here if needed
          if (typeof window !== 'undefined') window.location.href = '/login';
          return Promise.reject(error); // Reject if no refresh token
        }

        // Use axios directly for the refresh token request to avoid interceptor loop
        const { data } = await axios.post<RefreshTokenResponse>(
          `${apiClient.defaults.baseURL}/auth/refresh-token`, // Use baseURL from apiClient
           { refreshToken }
        );

        // Update tokens in localStorage
        if (typeof window !== 'undefined') {
          localStorage.setItem('accessToken', data.accessToken);
          localStorage.setItem('refreshToken', data.refreshToken);
        }

        // Update the default Authorization header for subsequent requests
        apiClient.defaults.headers.common['Authorization'] = `Bearer ${data.accessToken}`;

        // Update the Authorization header of the original failed request
        if (originalRequest.headers) {
            originalRequest.headers['Authorization'] = `Bearer ${data.accessToken}`;
        }


        // Retry the original request with the new token
        return apiClient(originalRequest);

      } catch (refreshError: any) {
        // Handle refresh token failure (e.g., refresh token expired or invalid)
        console.error('Unable to refresh token:', refreshError.response?.data || refreshError.message);

        // Clear tokens and redirect to login
        if (typeof window !== 'undefined') {
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          delete apiClient.defaults.headers.common['Authorization'];
          window.location.href = '/login'; // Redirect to login page
        }
        return Promise.reject(refreshError); // Reject the promise after handling
      }
    }

    // For errors other than 401 or if retry already happened, reject the promise
    return Promise.reject(error);
  }
);

export default apiClient;