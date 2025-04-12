<script setup lang="ts">
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
  birthDate: '', // Required - Use YYYY-MM-DD format for input type="date"
  phoneNumber: '', // Required
  gender: 'MALE' as 'MALE' | 'FEMALE', // Required, default to MALE
  role: 'CONSUMER' as 'FARMER' | 'CONSUMER', // Default role
  // Farmer specific fields
  farmName: '',
  farmLocation: '',
  bio: '', // Optional for farmer
  acceptTerms: false
});

// Form validation and UI state - Adjusted for all fields
const errors = reactive({
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  confirmPassword: '',
  birthDate: '', // Required validation
  phoneNumber: '', // Required validation
  gender: '', // Required validation
  role: '',
  // Farmer specific fields
  farmName: '',
  farmLocation: '',
  bio: '', // Optional validation
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

  // Reset all errors
  errors.firstName = '';
  errors.lastName = '';
  errors.email = '';
  errors.password = '';
  errors.confirmPassword = '';
  errors.birthDate = '';
  errors.phoneNumber = '';
  errors.gender = '';
  errors.role = '';
  errors.farmName = '';
  errors.farmLocation = '';
  errors.bio = '';
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
    // Consider adding backend regex check here if needed
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

  // Birth Date validation
  if (!formData.birthDate) {
    errors.birthDate = t('auth.errors.birthDateRequired'); // Add this translation key
    isValid = false;
  } else {
    // Basic check if it's a valid date string and in the past
    const birthDateObj = new Date(formData.birthDate);
    if (isNaN(birthDateObj.getTime()) || birthDateObj >= new Date()) {
      errors.birthDate = t('auth.errors.birthDateInvalid'); // Add this translation key
      isValid = false;
    }
  }

  // Phone number validation
  if (!formData.phoneNumber.trim()) {
    errors.phoneNumber = t('auth.errors.phoneNumberRequired'); // Add this translation key
    isValid = false;
  } else if (!/^\+?[0-9]{10,15}$/.test(formData.phoneNumber)) {
    // Use backend regex if available
    errors.phoneNumber = t('auth.errors.phoneNumberInvalid');
    isValid = false;
  }

  // Gender validation (already defaults, but check just in case)
  if (!formData.gender) {
    errors.gender = t('auth.errors.genderRequired'); // Add this translation key
    isValid = false;
  }

  // Validate farmer-specific fields if the role is FARMER
  if (formData.role === 'FARMER') {
    // Farm name validation
    if (!formData.farmName.trim()) {
      errors.farmName = t('auth.errors.farmNameRequired');
      isValid = false;
    }

    // Farm location validation
    if (!formData.farmLocation.trim()) {
      errors.farmLocation = t('auth.errors.farmLocationRequired');
      isValid = false;
    }

    // Bio validation (optional, but check max length)
    if (formData.bio && formData.bio.length > 1000) {
      errors.bio = t('auth.errors.bioTooLong');
      isValid = false;
    }
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
    // Base user data - all fields are now required or have defaults
    const baseUserData = {
      firstName: formData.firstName,
      lastName: formData.lastName,
      email: formData.email,
      password: formData.password,
      // Send birthDate as yyyy-MM-dd string (from input)
      birthDate: formData.birthDate,
      phoneNumber: formData.phoneNumber,
      gender: formData.gender,
    };

    // Prepare final user data object based on role
    const userData: any = {
      ...baseUserData,
      role: formData.role // Include role for the store/service logic
    };

    // Add farmer-specific fields if registering as a farmer
    if (formData.role === 'FARMER') {
      userData.farmName = formData.farmName;
      userData.farmLocation = formData.farmLocation;
      // Include bio only if provided, even though it's optional for farmer
      if (formData.bio) {
        userData.bio = formData.bio;
      }
    }

    // Register user with the auth store
    await authStore.register(userData);

    // Emit success event
    emit('register-success');

    // Redirect based on user role
    router.push(formData.role === 'FARMER' ? '/dashboard/farmer' : '/dashboard/consumer');

  } catch (error: any) {
    // Handle API errors and display meaningful message
    if (error.response?.status === 409) {
      errors.form = t('auth.errors.registrationConflict');
    } else if (error.response?.data?.message) {
      errors.form = error.response.data.message;
    } else if (error.message?.includes('null or transient value')) {
      errors.form = t('auth.errors.missingRequiredFields');
    } else {
      errors.form = error?.message || t('auth.errors.registrationFailed');
    }
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
    <form @submit.prevent="handleSubmit" novalidate>
      <!-- Form error message -->
      <div v-if="errors.form" class="form-error alert alert-danger">
        {{ errors.form }}
      </div>

      <!-- First Name field -->
      <div class="form-group">
        <label for="register-firstName">{{ t('auth.firstName') }} *</label>
        <input
          id="register-firstName"
          v-model.trim="formData.firstName"
          type="text"
          :placeholder="t('auth.placeholders.firstName')"
          :class="{ 'has-error': errors.firstName }"
          required
        />
        <span v-if="errors.firstName" class="error-message">{{ errors.firstName }}</span>
      </div>

      <!-- Last Name field -->
      <div class="form-group">
        <label for="register-lastName">{{ t('auth.lastName') }} *</label>
        <input
          id="register-lastName"
          v-model.trim="formData.lastName"
          type="text"
          :placeholder="t('auth.placeholders.lastName')"
          :class="{ 'has-error': errors.lastName }"
          required
        />
        <span v-if="errors.lastName" class="error-message">{{ errors.lastName }}</span>
      </div>

      <!-- Email field -->
      <div class="form-group">
        <label for="register-email">{{ t('common.email') }} *</label>
        <input
          id="register-email"
          v-model.trim="formData.email"
          type="email"
          :placeholder="t('common.email')"
          :class="{ 'has-error': errors.email }"
          required
        />
        <span v-if="errors.email" class="error-message">{{ errors.email }}</span>
      </div>

      <!-- Password field -->
      <div class="form-group">
        <label for="register-password">{{ t('common.password') }} *</label>
        <div class="password-input-container">
          <input
            id="register-password"
            v-model="formData.password"
            :type="showPassword ? 'text' : 'password'"
            :placeholder="t('auth.placeholders.passwordCreate')"
            :class="{ 'has-error': errors.password }"
            required
          />
          <button
            type="button"
            class="password-toggle"
            @click="togglePasswordVisibility"
            :aria-label="showPassword ? t('common.hidePassword') : t('common.showPassword')"
          >
            {{ showPassword ? t('common.hide') : t('common.show') }}
          </button>
        </div>
        <span v-if="errors.password" class="error-message">{{ errors.password }}</span>
        <span class="password-tip">{{ t('auth.passwordHint') }}</span>
      </div>

      <!-- Confirm Password field -->
      <div class="form-group">
        <label for="register-confirm-password">{{ t('auth.confirmPassword') }} *</label>
        <input
          id="register-confirm-password"
          v-model="formData.confirmPassword"
          :type="showPassword ? 'text' : 'password'"
          :placeholder="t('auth.placeholders.passwordConfirm')"
          :class="{ 'has-error': errors.confirmPassword }"
          required
        />
        <span v-if="errors.confirmPassword" class="error-message">{{ errors.confirmPassword }}</span>
      </div>

      <!-- Birth Date Field -->
      <div class="form-group">
          <label for="register-birthDate">{{ t('auth.birthDate') }} *</label>
          <input
            id="register-birthDate"
            v-model="formData.birthDate"
            type="date"
            :class="{ 'has-error': errors.birthDate }"
            required
            :max="new Date().toISOString().split('T')[0]" 
          />
          <span v-if="errors.birthDate" class="error-message">{{ errors.birthDate }}</span>
      </div>

      <!-- Phone Number Field -->
       <div class="form-group">
          <label for="register-phoneNumber">{{ t('auth.phoneNumber') }} *</label>
          <input
            id="register-phoneNumber"
            v-model.trim="formData.phoneNumber"
            type="tel"
            :placeholder="t('auth.placeholders.phoneNumber')"
            :class="{ 'has-error': errors.phoneNumber }"
            required
          />
          <span v-if="errors.phoneNumber" class="error-message">{{ errors.phoneNumber }}</span>
      </div>

      <!-- Gender Field -->
       <div class="form-group">
          <label for="register-gender">{{ t('auth.gender') }} *</label>
          <select
            id="register-gender"
            v-model="formData.gender"
            :class="{ 'has-error': errors.gender }"
            required
          >
              <!-- <option value="" disabled>{{ t('auth.selectGender') }}</option> -->
              <option value="MALE">{{ t('auth.genderMale') }}</option>
              <option value="FEMALE">{{ t('auth.genderFemale') }}</option>
              <!-- Removed OTHER -->
          </select>
          <span v-if="errors.gender" class="error-message">{{ errors.gender }}</span>
      </div>

      <!-- Role selection -->
      <div class="form-group">
        <label>{{ t('auth.iAmA') }} *</label>
        <div class="role-selection">
          <label class="role-option" :class="{ 'active': formData.role === 'CONSUMER' }">
            <input
              type="radio"
              v-model="formData.role"
              value="CONSUMER"
              name="role"
            />
            <span class="role-label">{{ t('auth.roleConsumer') }}</span>
          </label>
          <label class="role-option" :class="{ 'active': formData.role === 'FARMER' }">
            <input
              type="radio"
              v-model="formData.role"
              value="FARMER"
              name="role"
            />
            <span class="role-label">{{ t('auth.roleFarmer') }}</span>
          </label>
        </div>
      </div>

      <!-- Farmer-specific fields (conditionally displayed) -->
      <div v-if="formData.role === 'FARMER'" class="farmer-fields">
        <h3 class="farmer-details-header">{{ t('auth.farmerDetails') }}</h3>

        <!-- Farm Name field -->
        <div class="form-group">
          <label for="register-farm-name">{{ t('auth.farmName') }} *</label>
          <input
            id="register-farm-name"
            v-model.trim="formData.farmName"
            type="text"
            :placeholder="t('auth.placeholders.farmName')"
            :class="{ 'has-error': errors.farmName }"
            required
          />
          <span v-if="errors.farmName" class="error-message">{{ errors.farmName }}</span>
        </div>

        <!-- Farm Location field -->
        <div class="form-group">
          <label for="register-farm-location">{{ t('auth.farmLocation') }} *</label>
          <input
            id="register-farm-location"
            v-model.trim="formData.farmLocation"
            type="text"
            :placeholder="t('auth.placeholders.farmLocation')"
            :class="{ 'has-error': errors.farmLocation }"
            required
          />
          <span v-if="errors.farmLocation" class="error-message">{{ errors.farmLocation }}</span>
        </div>

        <!-- Farm Bio field -->
        <div class="form-group">
          <label for="register-bio">{{ t('auth.bio') }} ({{ t('auth.optional') }})</label>
          <textarea
            id="register-bio"
            v-model="formData.bio"
            rows="4"
            :placeholder="t('auth.placeholders.bio')"
            :class="{ 'has-error': errors.bio }"
            maxlength="1000"
          ></textarea>
          <span v-if="errors.bio" class="error-message">{{ errors.bio }}</span>
          <span class="char-count">{{ formData.bio.length }}/1000</span>
        </div>
      </div>

      <!-- Terms and Conditions -->
      <div class="form-group terms-group">
        <label class="terms-label">
          <input
            type="checkbox"
            v-model="formData.acceptTerms"
            :class="{ 'has-error': errors.acceptTerms }"
            required
          />
          <span>{{ t('auth.accept') }} <a href="/terms" target="_blank" class="terms-link">{{ t('auth.terms') }}</a> *</span>
        </label>
        <span v-if="errors.acceptTerms" class="error-message">{{ errors.acceptTerms }}</span>
      </div>

      <!-- Submit button -->
      <div class="form-actions">
        <button
          type="submit"
          class="submit-button btn primary"
          :disabled="isSubmitting"
        >
          <span v-if="isSubmitting">{{ t('auth.creatingAccount') }}...</span>
          <span v-else>{{ t('auth.createAccount') }}</span>
        </button>
      </div>
    </form>
  </div>
</template>

<style scoped>
.register-form {
  max-width: 500px; /* Slightly wider for better spacing */
  margin: 2rem auto; /* Add some vertical margin */
  padding: 2rem; /* Add padding inside the form container */
  background-color: #ffffff; /* White background */
  border-radius: 8px; /* Rounded corners */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); /* Subtle shadow */
}

.form-group {
  margin-bottom: 1.5rem; /* Consistent spacing */
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem; /* Space between label and input */
  font-weight: 600; /* Make labels bolder */
  color: #333;
  font-size: 0.9rem; /* Slightly smaller label */
}

/* Unified input/select/textarea styles */
.form-group input[type="text"],
.form-group input[type="email"],
.form-group input[type="password"],
.form-group input[type="date"],
.form-group input[type="tel"],
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.8rem 1rem; /* Adjust padding */
  border: 1px solid #d0d0d0; /* Lighter border */
  border-radius: 6px; /* Slightly more rounded */
  font-size: 1rem;
  transition: border-color 0.3s, box-shadow 0.3s;
  background-color: #f9f9f9; /* Light background for inputs */
}

.form-group textarea {
  resize: vertical; /* Allow vertical resize only */
  min-height: 80px;
}

/* Focus styles */
.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: hsla(160, 100%, 37%, 0.8); /* Use primary color */
  box-shadow: 0 0 0 3px hsla(160, 100%, 37%, 0.15); /* Subtle glow */
}

/* Error state */
.form-group input.has-error,
.form-group select.has-error,
.form-group textarea.has-error {
  border-color: #e53935; /* Standard error color */
  background-color: #fff8f8; /* Light red background */
}
.form-group input.has-error:focus,
.form-group select.has-error:focus,
.form-group textarea.has-error:focus {
   box-shadow: 0 0 0 3px rgba(229, 57, 53, 0.15);
}


.error-message {
  display: block;
  color: #e53935;
  font-size: 0.85rem; /* Smaller error text */
  margin-top: 0.4rem; /* Space above error message */
}

/* General form error (e.g., API error) */
.form-error.alert {
  background-color: #ffebee;
  color: #c62828; /* Darker red for better contrast */
  padding: 1rem;
  border-radius: 6px;
  margin-bottom: 1.5rem;
  font-size: 0.9rem;
  border: 1px solid #ef9a9a;
  text-align: center;
}

.password-tip {
  display: block;
  color: #666;
  font-size: 0.8rem;
  margin-top: 0.4rem;
}

/* Role Selection Styling */
.role-selection {
  display: flex;
  gap: 1rem; /* Space between radio options */
  margin-top: 0.5rem;
}

.role-option {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0.6rem 1rem;
  border: 1px solid #d0d0d0;
  border-radius: 6px;
  transition: all 0.3s ease;
  background-color: #f9f9f9;
}

.role-option:hover {
  border-color: #a0a0a0;
  background-color: #f0f0f0;
}

.role-option.active {
  border-color: hsla(160, 100%, 37%, 1);
  background-color: hsla(160, 100%, 37%, 0.08);
  box-shadow: 0 0 0 1px hsla(160, 100%, 37%, 1);
}

.role-option input[type="radio"] {
  /* Hide default radio button */
  opacity: 0;
  position: absolute;
  width: 0;
  height: 0;
}

.role-label {
  font-size: 0.95rem;
  font-weight: 500;
  margin-left: 0.5rem; /* Space after (hidden) radio */
}

/* Farmer Specific Fields Styling */
.farmer-fields {
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e0e0e0; /* Separator */
}

.farmer-details-header {
  margin-bottom: 1.5rem;
  font-size: 1.2rem;
  color: #333;
  font-weight: 600;
}

.char-count {
  display: block;
  text-align: right;
  font-size: 0.8rem;
  color: #666;
  margin-top: 0.3rem;
}

/* Terms and Conditions Styling */
.terms-group {
  margin-top: 2rem;
}

.terms-label {
  display: flex;
  align-items: center; /* Align checkbox and text */
  cursor: pointer;
  font-size: 0.9rem;
  color: #555;
}

.terms-label input[type="checkbox"] {
  margin-right: 0.75rem; /* Space between checkbox and text */
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: hsla(160, 100%, 37%, 1); /* Style checkbox color */
}
.terms-label input[type="checkbox"].has-error {
   outline: 1px solid #e53935; /* Indicate error on checkbox */
}


.terms-link {
  color: hsla(160, 100%, 37%, 1);
  text-decoration: none;
  font-weight: 500;
}

.terms-link:hover {
  text-decoration: underline;
}

/* Form Actions (Submit Button) */
.form-actions {
  margin-top: 2rem;
}

.submit-button.btn.primary {
  width: 100%;
  padding: 0.9rem 1.5rem; /* Larger button padding */
  background-color: hsla(160, 100%, 37%, 1);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 1.1rem; /* Larger font size */
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
}

.submit-button.btn.primary:hover {
  background-color: hsla(160, 100%, 30%, 1); /* Darker shade on hover */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.submit-button.btn.primary:disabled {
  background-color: #a5d6a7; /* Disabled color */
  cursor: not-allowed;
  box-shadow: none;
}

/* Password Toggle Button */
.password-input-container {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: 1rem; /* Position inside the input padding */
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #666;
  cursor: pointer;
  font-size: 0.85rem; /* Smaller toggle text */
  padding: 0.2rem 0.4rem; /* Small padding */
}

.password-toggle:hover {
  color: hsla(160, 100%, 37%, 1);
}

/* Responsive Adjustments */
@media (max-width: 600px) {
  .register-form {
    padding: 1.5rem;
    margin: 1rem auto;
  }

  .role-selection {
    flex-direction: column;
    gap: 0.8rem;
  }

  .role-option {
    width: 100%;
    justify-content: center; /* Center text in column layout */
  }
}
</style>