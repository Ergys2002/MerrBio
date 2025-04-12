<script setup lang="ts">
/**
 * LoginForm component handles user authentication with email and password
 * @component
 */
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/authStore';

interface Emits {
  (e: 'login-success'): void;
}

const emit = defineEmits<Emits>();
const router = useRouter();
const authStore = useAuthStore();

// Form state
const formData = reactive({
  email: '',
  password: '',
  role: 'CONSUMER' as 'FARMER' | 'CONSUMER'
});

// Form validation and UI state
const errors = reactive({
  email: '',
  password: '',
  form: ''
});
const isSubmitting = ref(false);
const showPassword = ref(false);

/**
 * Validate form fields
 * @returns {boolean} True if form is valid
 */
const validateForm = (): boolean => {
  let isValid = true;
  
  // Reset errors
  errors.email = '';
  errors.password = '';
  errors.form = '';
  
  // Email validation
  if (!formData.email) {
    errors.email = 'Email is required';
    isValid = false;
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
    errors.email = 'Please enter a valid email address';
    isValid = false;
  }
  
  // Password validation
  if (!formData.password) {
    errors.password = 'Password is required';
    isValid = false;
  }
  
  return isValid;
};

/**
 * Handle form submission
 */
const handleSubmit = async () => {
  if (!validateForm()) return;
  
  isSubmitting.value = true;
  
  try {
    await authStore.login(formData.email, formData.password);
    
    // Emit success event
    emit('login-success');
    
    // Redirect based on user role
    const role = authStore.userRole;
    if (role === 'FARMER') {
      router.push('/dashboard/farmer');
    } else if (role === 'CONSUMER') {
      router.push('/dashboard/consumer');
    } else if (role === 'ADMIN') {
      router.push('/dashboard/admin');
    } else {
      router.push('/');
    }
  } catch (error: any) {
    errors.form = error.message || 'Login failed. Please check your credentials.';
  } finally {
    isSubmitting.value = false;
  }
};

/**
 * Toggle password visibility
 */
const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value;
};
</script>

<template>
  <div class="login-form">
    <form @submit.prevent="handleSubmit">
      <!-- Form error message -->
      <div v-if="errors.form" class="form-error">
        {{ errors.form }}
      </div>
      
      <!-- Email field -->
      <div class="form-group">
        <label for="login-email">Email</label>
        <input
          id="login-email"
          v-model="formData.email"
          type="email"
          placeholder="Your email address"
          :class="{ 'has-error': errors.email }"
        />
        <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
      </div>
      
      <!-- Password field -->
      <div class="form-group">
        <label for="login-password">Password</label>
        <div class="password-input-container">
          <input
            id="login-password"
            v-model="formData.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="Your password"
            :class="{ 'has-error': errors.password }"
          />
          <button 
            type="button" 
            class="password-toggle"
            @click="togglePasswordVisibility"
          >
            {{ showPassword ? 'Hide' : 'Show' }}
          </button>
        </div>
        <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
      </div>
      
      <!-- Role selection -->
      <div class="form-group">
        <label>I am a:</label>
        <div class="role-selection">
          <label class="role-option">
            <input 
              type="radio" 
              v-model="formData.role" 
              value="CONSUMER" 
            />
            <span class="role-label">Consumer</span>
          </label>
          <label class="role-option">
            <input 
              type="radio" 
              v-model="formData.role" 
              value="FARMER" 
            />
            <span class="role-label">Farmer</span>
          </label>
        </div>
      </div>
      
      <!-- Forgot password link -->
      <div class="forgot-password">
        <a href="#">Forgot password?</a>
      </div>
      
      <!-- Submit button -->
      <div class="form-actions">
        <button 
          type="submit" 
          class="submit-button"
          :disabled="isSubmitting"
        >
          <span v-if="isSubmitting">Logging in...</span>
          <span v-else>Login</span>
        </button>
      </div>
    </form>
  </div>
</template>

<style scoped>
.login-form {
  max-width: 400px;
  margin: 0 auto;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-group input[type="email"],
.form-group input[type="password"],
.form-group input[type="text"] {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 16px;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #4caf50;
  box-shadow: 0 0 0 2px rgba(76, 175, 80, 0.2);
}

.form-group input.has-error {
  border-color: #f44336;
}

.error-message {
  display: block;
  color: #f44336;
  font-size: 14px;
  margin-top: 5px;
}

.form-error {
  background-color: #ffebee;
  color: #f44336;
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 20px;
  font-size: 14px;
  text-align: center;
}

.role-selection {
  display: flex;
  gap: 20px;
  margin-top: 8px;
}

.role-option {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.role-option input {
  margin-right: 8px;
  cursor: pointer;
}

.role-label {
  font-size: 16px;
}

.forgot-password {
  text-align: right;
  margin-bottom: 20px;
}

.forgot-password a {
  color: #4caf50;
  text-decoration: none;
  font-size: 14px;
}

.forgot-password a:hover {
  text-decoration: underline;
}

.form-actions {
  margin-top: 30px;
}

.submit-button {
  width: 100%;
  padding: 12px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-button:hover {
  background-color: #3d8b40;
}

.submit-button:disabled {
  background-color: #a5d6a7;
  cursor: not-allowed;
}

.password-input-container {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  font-size: 14px;
}

.password-toggle:hover {
  color: #4caf50;
}
</style>