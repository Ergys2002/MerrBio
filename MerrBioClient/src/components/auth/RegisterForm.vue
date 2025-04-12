<script setup lang="ts">
/**
 * RegisterForm component handles new user registration
 * @component
 */
import { ref, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../../stores/authStore';

const { t } = useI18n();

interface Emits {
  (e: 'register-success'): void;
}

const emit = defineEmits<Emits>();
const router = useRouter();
const authStore = useAuthStore();

// Form state - Adjusted to match API requirements
const formData = reactive({
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  confirmPassword: '',
  birthDate: '', // Optional - Use YYYY-MM-DD format for input type="date"
  phoneNumber: '', // Optional
  gender: '' as 'MALE' | 'FEMALE' | 'OTHER' | '', // Optional
  role: 'CONSUMER' as 'FARMER' | 'CONSUMER', // Default role
  acceptTerms: false
});

// Form validation and UI state - Adjusted for new fields
const errors = reactive({
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  confirmPassword: '',
  birthDate: '', // Optional validation
  phoneNumber: '', // Optional validation
  gender: '', // Optional validation
  role: '',
  acceptTerms: '',
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
  errors.firstName = '';
  errors.lastName = '';
  errors.email = '';
  errors.password = '';
  errors.confirmPassword = '';
  errors.birthDate = '';
  errors.phoneNumber = '';
  errors.gender = '';
  errors.role = '';
  errors.acceptTerms = '';
  errors.form = '';
  // First Name validation
  if (!formData.firstName.trim()) {
    errors.firstName = t('auth.errors.firstNameRequired');
    isValid = false;
  }

  // Last Name validation
  if (!formData.lastName.trim()) {
    errors.lastName = t('auth.errors.lastNameRequired');
    isValid = false;
  }
  
  // Email validation
  if (!formData.email) {
    errors.email = t('auth.errors.emailRequired');
    isValid = false;
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
    errors.email = t('auth.errors.emailInvalid');
    isValid = false;
  }
  
  // Password validation
  if (!formData.password) {
    errors.password = t('auth.errors.passwordRequired');
    isValid = false;
  } else if (formData.password.length < 8) {
    errors.password = t('auth.errors.passwordLength');
    isValid = false;
  }
  
  // Confirm password validation
  if (!formData.confirmPassword) {
    errors.confirmPassword = t('auth.errors.confirmPasswordRequired');
    isValid = false;
  } else if (formData.password !== formData.confirmPassword) {
    errors.confirmPassword = t('auth.errors.passwordsNotMatch');
    isValid = false;
  }
  
  // Terms acceptance validation
  if (!formData.acceptTerms) {
    errors.acceptTerms = t('auth.errors.termsRequired');
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
    // Construct user data matching the service requirements
    const userData: any = { // Use a specific interface if defined in authService.ts
      firstName: formData.firstName,
      lastName: formData.lastName,
      email: formData.email,
      password: formData.password,
      role: formData.role,
      // Include optional fields only if they have a value
      ...(formData.birthDate && { birthDate: new Date(formData.birthDate).toISOString() }), // Convert to ISO string
      ...(formData.phoneNumber && { phoneNumber: formData.phoneNumber }),
      ...(formData.gender && { gender: formData.gender }),
    };

    await authStore.register(userData);
    
    // Emit success event
    emit('register-success');
    
    // Redirect based on user role
    if (formData.role === 'FARMER') {
      router.push('/dashboard/farmer');
    } else if (formData.role === 'CONSUMER') {
      router.push('/dashboard/consumer');
    } else {
      router.push('/');
    }
  } catch (error: any) {
    // Use error message from store/service if available
    errors.form = error?.message || 'Registration failed. Please try again.';
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
  <div class="register-form">
    <form @submit.prevent="handleSubmit">
      <!-- Form error message -->
      <div v-if="errors.form" class="form-error">
        {{ errors.form }}
      </div>
      
      <!-- First Name field -->
      <div class="form-group">        <label for="register-firstName">{{ t('auth.firstName') }}</label>
        <input
          id="register-firstName"
          v-model="formData.firstName"
          type="text"
          :placeholder="t('auth.placeholders.firstName')"
          :class="{ 'has-error': errors.firstName }"
        />
        <span v-if="errors.firstName" class="error-message">{{ errors.firstName }}</span>
      </div>

      <!-- Last Name field -->
      <div class="form-group">
        <label for="register-lastName">Last Name</label>
        <input
          id="register-lastName"
          v-model="formData.lastName"
          type="text"
          placeholder="Your last name"
          :class="{ 'has-error': errors.lastName }"
        />
        <span v-if="errors.lastName" class="error-message">{{ errors.lastName }}</span>
      </div>

      <!-- Email field -->
      <div class="form-group">
        <label for="register-email">Email</label>
        <input
          id="register-email"
          v-model="formData.email"
          type="email"
          placeholder="Your email address"
          :class="{ 'has-error': errors.email }"
        />
        <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
      </div>
      
      <!-- Password field -->
      <div class="form-group">
        <label for="register-password">Password</label>
        <div class="password-input-container">
          <input
            id="register-password"
            v-model="formData.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="Create a password"
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
        <span class="password-tip">Use at least 8 characters</span>
      </div>
      
      <!-- Confirm Password field -->
      <div class="form-group">
        <label for="register-confirm-password">Confirm Password</label>
        <input
          id="register-confirm-password"
          v-model="formData.confirmPassword"
          :type="showPassword ? 'text' : 'password'"
          placeholder="Confirm your password"
          :class="{ 'has-error': errors.confirmPassword }"
        />
        <span v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</span>
      </div>
      <!-- Optional Fields -->
      <div class="form-group">
          <label for="register-birthDate">Birth Date (Optional)</label>
          <input
            id="register-birthDate"
            v-model="formData.birthDate"
            type="date"
            :class="{ 'has-error': errors.birthDate }"
          />
          <span v-if="errors.birthDate" class="error-message">{{ errors.birthDate }}</span>
      </div>

       <div class="form-group">
          <label for="register-phoneNumber">Phone Number (Optional)</label>
          <input
            id="register-phoneNumber"
            v-model="formData.phoneNumber"
            type="tel"
            placeholder="Your phone number"
            :class="{ 'has-error': errors.phoneNumber }"
          />
          <span v-if="errors.phoneNumber" class="error-message">{{ errors.phoneNumber }}</span>
      </div>

       <div class="form-group">
          <label for="register-gender">Gender (Optional)</label>
          <select id="register-gender" v-model="formData.gender" :class="{ 'has-error': errors.gender }">
              <option value="">Select Gender</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
              <option value="OTHER">Other</option>
          </select>
          <span v-if="errors.gender" class="error-message">{{ errors.gender }}</span>
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
      
      <!-- Terms and Conditions -->
      <div class="form-group terms-group">
        <label class="terms-label">
          <input 
            type="checkbox" 
            v-model="formData.acceptTerms"
            :class="{ 'has-error': errors.acceptTerms }"
          />
          <span>I accept the <a href="#" class="terms-link">Terms and Conditions</a></span>
        </label>
        <span v-if="errors.acceptTerms" class="error-message">{{ errors.acceptTerms }}</span>
      </div>
      
      <!-- Submit button -->
      <div class="form-actions">
        <button 
          type="submit" 
          class="submit-button"
          :disabled="isSubmitting"
        >
          <span v-if="isSubmitting">Creating Account...</span>
          <span v-else>Create Account</span>
        </button>
      </div>
    </form>
  </div>
</template>

<style scoped>
.register-form {
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

.form-group input[type="text"],
.form-group input[type="email"],
.form-group input[type="password"] {
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

.password-tip {
  display: block;
  color: #666;
  font-size: 12px;
  margin-top: 6px;
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

.terms-group {
  margin-top: 25px;
}

.terms-label {
  display: flex;
  align-items: flex-start;
  cursor: pointer;
}

.terms-label input {
  margin-right: 10px;
  margin-top: 3px;
  cursor: pointer;
}

.terms-link {
  color: #4caf50;
  text-decoration: none;
}

.terms-link:hover {
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