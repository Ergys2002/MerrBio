import axios from 'axios';
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios';

// Create an Axios instance
const apiClient: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api/v1', // Adjust if your API base URL is different
  headers: {
    'Content-Type': 'application/json',
  },
});

// Optional: Add interceptors for request/response handling (e.g., adding auth tokens)
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Add authorization token to headers if available
    const token = localStorage.getItem('accessToken'); // Or get from your state management
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: any) => {
    return Promise.reject(error);
  }
);

interface RefreshTokenResponse {
  accessToken: string;
  refreshToken: string;
}

// Optional: Add interceptor for response error handling (e.g., refreshing tokens)
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  async (error: any) => {
    const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };
    // Example: Handle token expiry and refresh
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        // Call your refresh token endpoint
        const refreshToken = localStorage.getItem('refreshToken'); // Or get from state
        const { data } = await axios.post<RefreshTokenResponse>(
          'http://localhost:8080/api/v1/auth/refresh-token', 
          { refreshToken }
        );
        localStorage.setItem('accessToken', data.accessToken);
        localStorage.setItem('refreshToken', data.refreshToken);
        apiClient.defaults.headers.common['Authorization'] = `Bearer ${data.accessToken}`;
        if (originalRequest.headers) {
          originalRequest.headers['Authorization'] = `Bearer ${data.accessToken}`;
        }
        return apiClient(originalRequest);
      } catch (refreshError) {
        // Handle refresh token failure (e.g., logout user)
        console.error('Unable to refresh token:', refreshError);
        // Logout user logic here
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        // Redirect to login or dispatch logout action
        window.location.href = '/login'; // Simple redirect, adjust as needed
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  }
);

export default apiClient;
