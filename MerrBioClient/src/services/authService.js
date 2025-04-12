import apiClient from './api';

/**
 * Register a new user.
 * @param {object} userData - User registration data.
 * @param {string} userData.email
 * @param {string} userData.password
 * @param {string} userData.firstName
 * @param {string} userData.lastName
 * @param {string} userData.birthDate - ISO 8601 format
 * @param {string} userData.phoneNumber
 * @param {string} userData.role - e.g., 'FARMER', 'CONSUMER'
 * @param {string} userData.gender - e.g., 'MALE', 'FEMALE'
 * @returns {Promise<object>} - Promise resolving with { accessToken, refreshToken }
 */
export const register = async (userData) => {
  try {
    const response = await apiClient.post('/auth/register', userData);
    return response.data; // { accessToken, refreshToken }
  } catch (error) {
    console.error('Registration failed:', error.response?.data || error.message);
    throw error.response?.data || new Error('Registration failed');
  }
};

/**
 * Log in a user.
 * @param {object} credentials - User login credentials.
 * @param {string} credentials.email
 * @param {string} credentials.password
 * @returns {Promise<object>} - Promise resolving with { accessToken, refreshToken }
 */
export const login = async (credentials) => {
  try {
    const response = await apiClient.post('/auth/login', credentials);
    // Store tokens upon successful login (e.g., in localStorage or state management)
    if (response.data.accessToken && response.data.refreshToken) {
      localStorage.setItem('accessToken', response.data.accessToken);
      localStorage.setItem('refreshToken', response.data.refreshToken);
      // Update apiClient default headers if needed (interceptor already does this)
      // apiClient.defaults.headers.common['Authorization'] = `Bearer ${response.data.accessToken}`;
    }
    return response.data; // { accessToken, refreshToken }
  } catch (error) {
    console.error('Login failed:', error.response?.data || error.message);
    // Clear any potentially stale tokens on login failure
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    throw error.response?.data || new Error('Login failed');
  }
};

/**
 * Log out a user using their refresh token.
 * @param {string} refreshToken - The refresh token to invalidate.
 * @returns {Promise<object>} - Promise resolving with an empty object on success.
 */
export const logout = async (refreshToken) => {
  try {
    // Make sure to pass the refreshToken in the request body
    const response = await apiClient.post('/auth/logout', { refreshToken });
    // Clear tokens from storage on successful logout
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    // Remove auth header from subsequent requests
    delete apiClient.defaults.headers.common['Authorization'];
    return response.data; // {}
  } catch (error) {
    console.error('Logout failed:', error.response?.data || error.message);
    // Even if logout fails on the server, clear local tokens
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    delete apiClient.defaults.headers.common['Authorization'];
    throw error.response?.data || new Error('Logout failed');
  }
};

/**
 * Log out from all devices for the current user.
 * Requires the user to be authenticated (valid accessToken).
 * @returns {Promise<object>} - Promise resolving with an empty object on success.
 */
export const logoutAll = async () => {
  try {
    // This endpoint likely requires authentication (accessToken in header)
    const response = await apiClient.post('/auth/logout-all');
    // Clear tokens from storage on successful logout
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    delete apiClient.defaults.headers.common['Authorization'];
    return response.data; // {}
  } catch (error) {
    console.error('Logout from all devices failed:', error.response?.data || error.message);
    // Consider clearing local tokens even on failure depending on desired UX
    // localStorage.removeItem('accessToken');
    // localStorage.removeItem('refreshToken');
    // delete apiClient.defaults.headers.common['Authorization'];
    throw error.response?.data || new Error('Logout from all devices failed');
  }
};

/**
 * Refresh the access token using a refresh token.
 * Note: This is also handled by the response interceptor in api.js,
 * but can be called manually if needed.
 * @param {string} refreshToken - The refresh token.
 * @returns {Promise<object>} - Promise resolving with { accessToken, refreshToken, tokenType }
 */
export const refreshToken = async (refreshToken) => {
  try {
    const response = await apiClient.post('/auth/refresh-token', { refreshToken });
    // Update stored tokens
    if (response.data.accessToken && response.data.refreshToken) {
      localStorage.setItem('accessToken', response.data.accessToken);
      localStorage.setItem('refreshToken', response.data.refreshToken);
      // Update apiClient default headers
      apiClient.defaults.headers.common['Authorization'] = `Bearer ${response.data.accessToken}`;
    }
    return response.data; // { accessToken, refreshToken, tokenType }
  } catch (error) {
    console.error('Token refresh failed:', error.response?.data || error.message);
    // Handle refresh failure (e.g., logout user)
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    delete apiClient.defaults.headers.common['Authorization'];
    // Redirect to login or dispatch logout action
    window.location.href = '/login'; // Simple redirect, adjust as needed
    throw error.response?.data || new Error('Token refresh failed');
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