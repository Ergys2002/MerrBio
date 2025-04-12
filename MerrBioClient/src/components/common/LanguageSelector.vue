<template>
  <div class="language-selector">
    <div class="dropdown">
      <button 
        class="dropdown-toggle" 
        @click="toggleDropdown"
        aria-haspopup="true"
        :aria-expanded="dropdownOpen"
      >
        <span class="language-flag" :class="currentLanguage">
          {{ currentLanguage === 'en' ? '🇬🇧' : '🇦🇱' }}
        </span>
        <span class="language-name">
          {{ t(`languageSelector.${currentLanguage === 'en' ? 'english' : 'albanian'}`) }}
        </span>
        <span class="dropdown-arrow">▼</span>
      </button>
      
      <div 
        v-show="dropdownOpen" 
        class="dropdown-menu"
        role="menu"
      >
        <button 
          class="language-option" 
          :class="{ active: currentLanguage === 'en' }"
          @click="changeLanguage('en')"
        >
          <span class="language-flag">🇬🇧</span>
          <span>{{ t('languageSelector.english') }}</span>
        </button>
        
        <button 
          class="language-option" 
          :class="{ active: currentLanguage === 'sq' }"
          @click="changeLanguage('sq')"
        >
          <span class="language-flag">🇦🇱</span>
          <span>{{ t('languageSelector.albanian') }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { TranslationService } from '@/services/translationService'

// Initialize i18n
const { t, locale } = useI18n()

// State
const dropdownOpen = ref(false)
const currentLanguage = ref(locale.value)
const translationService = new TranslationService()

// Toggle dropdown
const toggleDropdown = () => {
  dropdownOpen.value = !dropdownOpen.value
}

// Close dropdown when clicking outside
const closeDropdown = (event: MouseEvent) => {
  const target = event.target as HTMLElement
  if (!target.closest('.language-selector')) {
    dropdownOpen.value = false
  }
}

// Change language
const changeLanguage = async (lang: string) => {
  // If changing to a non-English language, preload common translations
  if (lang !== 'en') {
    try {
      // Show loading indicator (optional)
      // You could add a loading state here
      
      // Preload common translations
      await translationService.preloadCommonTranslations(lang)
    } catch (error) {
      console.error('Failed to preload translations:', error)
    }
  }

  // Update the language
  locale.value = lang
  currentLanguage.value = lang
  
  // Store the preference
  localStorage.setItem('language', lang)
  
  // Close the dropdown
  dropdownOpen.value = false
}

// Watch for language changes
watch(() => locale.value, (newLocale) => {
  currentLanguage.value = newLocale
})

// Close dropdown when clicking outside
onMounted(() => {
  document.addEventListener('click', closeDropdown)
})

// Clean up event listener
onMounted(() => {
  return () => {
    document.removeEventListener('click', closeDropdown)
  }
})
</script>

<style scoped>
.language-selector {
  position: relative;
  display: inline-block;
}

.dropdown-toggle {
  display: flex;
  align-items: center;
  padding: 0.5rem 1rem;
  background: transparent;
  border: 1px solid #e2e8f0;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.dropdown-toggle:hover {
  background-color: #f8fafc;
}

.language-flag {
  margin-right: 0.5rem;
  font-size: 1.25rem;
}

.dropdown-arrow {
  margin-left: 0.5rem;
  font-size: 0.75rem;
  transition: transform 0.2s ease;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 0.25rem;
  min-width: 10rem;
  background-color: white;
  border-radius: 0.375rem;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  z-index: 10;
  overflow: hidden;
}

.language-option {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 0.75rem 1rem;
  text-align: left;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.language-option:hover {
  background-color: #f8fafc;
}

.language-option.active {
  background-color: #f0f9ff;
  font-weight: 600;
}

@media (max-width: 640px) {
  .language-name {
    display: none;
  }
  
  .dropdown-toggle {
    padding: 0.5rem;
  }
}
</style>
