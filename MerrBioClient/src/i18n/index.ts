import { createI18n } from 'vue-i18n'
import en from '../locales/en.json'
import sq from '../locales/sq.json'
import { TranslationService } from '../services/translationService'

type SupportedLocales = 'en' | 'sq'

// Define a recursive type that allows string indexing
interface NestedMessages {
  [key: string]: string | NestedMessages
}

// Type for the messages in your i18n instance
type Messages = {
  [key in SupportedLocales]: NestedMessages
}

// Create the i18n instance
const i18n = createI18n({
  legacy: false,
  locale: (localStorage.getItem('language') || 'sq') as SupportedLocales,
  fallbackLocale: 'sq',
  messages: {
    'en': en,
    'sq': sq
  },
  missing: (locale: string, key: string): string => {
    if (locale !== 'en' && key) {
      const translationService = new TranslationService()
      setTimeout(() => {
        translationService.translate(key, locale).then(translation => {
          // Check if locale is a supported locale before indexing
          if (translation && (locale === 'en' || locale === 'sq')) {
            const path = key.split('.')
            let current = i18n.global.messages.value[locale as SupportedLocales] as NestedMessages
            
            for (let i = 0; i < path.length - 1; i++) {
              if (!current[path[i]]) {
                current[path[i]] = {}
              }
              current = current[path[i]] as NestedMessages
            }
            
            current[path[path.length - 1]] = translation
          }
        })
      }, 0)
    }
    return key
  }
})

export default i18n