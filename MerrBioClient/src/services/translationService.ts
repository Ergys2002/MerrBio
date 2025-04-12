import axios from 'axios';

interface TranslationCache {
  [key: string]: string;
}

/**
 * Service to handle translations using an external API and local caching
 */
export class TranslationService {
  private cache: Record<string, TranslationCache>;
  private apiKey: string;
  private translationQueue: Array<{
    text: string;
    targetLang: string;
    resolve: (value: string | null) => void;
  }>;
  private isProcessingQueue: boolean;
  
  constructor() {
    // Initialize cache from localStorage if available
    this.cache = this.loadCache();
    // In a real application, this should be stored securely
    // For this example, we'll set it directly, but in production
    // you would use environment variables or a secured backend
    this.apiKey = import.meta.env.VITE_TRANSLATION_API_KEY || '';
    this.translationQueue = [];
    this.isProcessingQueue = false;
  }

  /**
   * Load translation cache from localStorage
   */
  private loadCache(): Record<string, TranslationCache> {
    const cachedData = localStorage.getItem('translationCache');
    return cachedData ? JSON.parse(cachedData) : {};
  }

  /**
   * Save translation cache to localStorage
   */
  private saveCache(): void {
    localStorage.setItem('translationCache', JSON.stringify(this.cache));
  }

  /**
   * Get cached translation if available
   */
  private getCachedTranslation(text: string, targetLang: string): string | null {
    if (this.cache[targetLang] && this.cache[targetLang][text]) {
      return this.cache[targetLang][text];
    }
    return null;
  }

  /**
   * Cache a translation
   */
  private cacheTranslation(text: string, targetLang: string, translation: string): void {
    if (!this.cache[targetLang]) {
      this.cache[targetLang] = {};
    }
    this.cache[targetLang][text] = translation;
    this.saveCache();
  }

  /**
   * Add a translation request to the queue
   */
  private addToQueue(text: string, targetLang: string): Promise<string | null> {
    return new Promise((resolve) => {
      this.translationQueue.push({ text, targetLang, resolve });
      
      if (!this.isProcessingQueue) {
        this.processQueue();
      }
    });
  }

  /**
   * Process the translation queue in batches
   */
  private async processQueue() {
    if (this.isProcessingQueue || this.translationQueue.length === 0) {
      return;
    }

    this.isProcessingQueue = true;
    
    // Process up to 10 translations at a time (or whatever is appropriate for your API)
    const batch = this.translationQueue.splice(0, 10);
    const textsByLang: Record<string, { texts: string[], resolvers: Array<(value: string | null) => void> }> = {};
    
    // Group by target language
    batch.forEach(({ text, targetLang, resolve }) => {
      if (!textsByLang[targetLang]) {
        textsByLang[targetLang] = { texts: [], resolvers: [] };
      }
      textsByLang[targetLang].texts.push(text);
      textsByLang[targetLang].resolvers.push(resolve);
    });

    // Process each language batch
    for (const [targetLang, { texts, resolvers }] of Object.entries(textsByLang)) {
      try {
        const results = await this.translateBatch(texts, targetLang);
        
        // Resolve all promises with their translations
        texts.forEach((text, index) => {
          const translation = results[index];
          if (translation) {
            this.cacheTranslation(text, targetLang, translation);
          }
          resolvers[index](translation);
        });
      } catch (error) {
        console.error('Translation batch failed:', error);
        // Resolve all with null to indicate failure
        resolvers.forEach(resolve => resolve(null));
      }
    }

    this.isProcessingQueue = false;
    
    // Process next batch if there are more items
    if (this.translationQueue.length > 0) {
      setTimeout(() => this.processQueue(), 100); // Small delay to avoid API rate limits
    }
  }

  /**
   * Translate a batch of texts
   */
  private async translateBatch(texts: string[], targetLang: string): Promise<(string | null)[]> {
    try {
      // For this example, we're using a placeholder for the API call
      // In a real application, you would use a real translation API
      
      // Example using Google Translate API (needs to be set up with proper authentication)
      // const response = await axios.post('https://translation.googleapis.com/language/translate/v2', {
      //   q: texts,
      //   target: targetLang,
      //   source: 'en',
      //   format: 'text'
      // }, {
      //   headers: {
      //     Authorization: `Bearer ${this.apiKey}`
      //   }
      // });
      
      // For this demo, we'll simulate a response
      // In a real application, replace this with actual API calls
      return texts.map(text => {
        // Simple simulation for demonstration (Albanian translations)
        if (targetLang === 'sq') {
          const mockTranslations: Record<string, string> = {
            'Hello': 'Përshëndetje',
            'Welcome': 'Mirë se vini',
            'Products': 'Produktet',
            'Farmers': 'Fermerët',
            'Login': 'Hyrje',
            'Register': 'Regjistrohu'
          };
          
          return mockTranslations[text] || `[${text} në shqip]`; // Placeholder
        }
        
        return text; // Return input text as fallback
      });
    } catch (error) {
      console.error('Translation API error:', error);
      return texts.map(() => null);
    }
  }

  /**
   * Translate a single text string
   */
  public async translate(text: string, targetLang: string): Promise<string | null> {
    // Skip translation if target is already English (our base language)
    if (targetLang === 'en') {
      return text;
    }
    
    // Check cache first
    const cached = this.getCachedTranslation(text, targetLang);
    if (cached) {
      return cached;
    }
    
    return this.addToQueue(text, targetLang);
  }

  /**
   * Preload translations for common UI elements
   */
  public async preloadCommonTranslations(targetLang: string): Promise<void> {
    const commonPhrases = [
      'Home', 'Products', 'Login', 'Register', 'Farmers', 'Consumers', 
      'Profile', 'Settings', 'Logout', 'Search', 'Add to cart', 'Buy now'
    ];
    
    await Promise.all(commonPhrases.map(text => this.translate(text, targetLang)));
  }
}
