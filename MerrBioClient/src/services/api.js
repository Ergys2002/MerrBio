import axios from 'axios';

// Create an Axios instance
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api/v1', // Adjust if your API base URL is different
  headers: {
    'Content-Type': 'application/json',
  },
});

// Optional: Add interceptors for request/response handling (e.g., adding auth tokens)
apiClient.interceptors.request.use(
  (config) => {
    // Add authorization token to headers if available
    const token = localStorage.getItem('accessToken'); // Or get from your state management
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Optional: Add interceptor for response error handling (e.g., refreshing tokens)
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    // Example: Handle token expiry and refresh
    if (error.response.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        // Call your refresh token endpoint
        const refreshToken = localStorage.getItem('refreshToken'); // Or get from state
        const { data } = await axios.post('http://localhost:8080/api/v1/auth/refresh-token', { refreshToken });
        localStorage.setItem('accessToken', data.accessToken);
        localStorage.setItem('refreshToken', data.refreshToken);
        apiClient.defaults.headers.common['Authorization'] = `Bearer ${data.accessToken}`;
        originalRequest.headers['Authorization'] = `Bearer ${data.accessToken}`;
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