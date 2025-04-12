<script setup lang="ts">
/**
 * AuthView component provides a tabbed interface for login and registration
 * @component
 */
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';
import LoginForm from '../components/auth/LoginForm.vue';
import RegisterForm from '../components/auth/RegisterForm.vue';

const { t } = useI18n();

// Tab state management
const activeTab = ref<'login' | 'register'>('login');

// Tab switching functions
const showLoginTab = () => {
  activeTab.value = 'login';
};

const showRegisterTab = () => {
  activeTab.value = 'register';
};

// Success handlers
const handleLoginSuccess = () => {
  // Currently handled within the form component via router push
  // This is a hook for any additional UI logic after successful login
};

const handleRegisterSuccess = () => {
  // Currently handled within the form component via router push
  // This is a hook for any additional UI logic after successful registration
};
</script>

<template>
  <div class="auth-view">
    <div class="container">
      <div class="auth-container">
        <div class="auth-header">          <h1>{{ t('auth.welcome') }}</h1>
          <p>{{ t('auth.joinCommunity') }}</p>
        </div>
        
        <div class="auth-tabs">
          <div
            class="tab-item"
            :class="{ active: activeTab === 'login' }"
            @click="showLoginTab"
          >            {{ t('auth.login') }}
          </div>
          <div
            class="tab-item"
            :class="{ active: activeTab === 'register' }"
            @click="showRegisterTab"
          >
            {{ t('auth.register') }}
          </div>
          <div class="tab-slider" :class="activeTab"></div>
        </div>
        
        <div class="auth-content">
          <transition name="fade" mode="out-in">
            <div v-if="activeTab === 'login'" key="login" class="form-container">
              <LoginForm @login-success="handleLoginSuccess" />
            </div>
            <div v-else key="register" class="form-container">
              <RegisterForm @register-success="handleRegisterSuccess" />
            </div>
          </transition>
        </div>
        
        <div class="auth-footer">
          <div v-if="activeTab === 'login'">            <p>{{ t('auth.newToMerrBio') }} <a href="#" @click.prevent="showRegisterTab">{{ t('auth.register') }}</a></p>
          </div>
          <div v-else>
            <p>{{ t('auth.haveAccount') }} <a href="#" @click.prevent="showLoginTab">{{ t('auth.login') }}</a></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-view {
  padding: 40px 0;
  min-height: calc(100vh - 200px); /* Adjust based on header/footer height */
  display: flex;
  align-items: center;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  width: 100%;
}

.auth-container {
  max-width: 500px;
  margin: 0 auto;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  padding: 40px;
  position: relative;
  overflow: hidden;
}

.auth-header {
  text-align: center;
  margin-bottom: 30px;
}

.auth-header h1 {
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 10px;
}

.auth-header p {
  color: #666;
  font-size: 1rem;
}

.auth-tabs {
  display: flex;
  position: relative;
  margin-bottom: 30px;
  border-bottom: 1px solid #eee;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 15px 0;
  font-weight: 600;
  color: #666;
  cursor: pointer;
  transition: color 0.3s;
  position: relative;
  z-index: 2;
}

.tab-item.active {
  color: #4caf50;
}

.tab-slider {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 3px;
  background-color: #4caf50;
  transition: transform 0.3s ease;
  width: 50%;
  border-radius: 3px 3px 0 0;
}

.tab-slider.login {
  transform: translateX(0);
}

.tab-slider.register {
  transform: translateX(100%);
}

.auth-content {
  position: relative;
  min-height: 400px;
}

.form-container {
  width: 100%;
}

.auth-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.auth-footer p {
  color: #666;
  font-size: 0.9rem;
}

.auth-footer a {
  color: #4caf50;
  font-weight: 600;
  text-decoration: none;
}

.auth-footer a:hover {
  text-decoration: underline;
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Responsive adjustments */
@media (max-width: 600px) {
  .auth-container {
    padding: 30px 20px;
    box-shadow: none;
    border: 1px solid #eee;
  }
  
  .auth-view {
    padding: 20px 0;
  }
  
  .auth-header h1 {
    font-size: 1.5rem;
  }
}
</style>