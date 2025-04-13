<template>
  <footer class="app-footer">
    <div class="footer-container">
      <div class="footer-content">
        <!-- Column 1: Brand Info -->
        <div class="footer-column brand-info">
          <h3 class="footer-brand-name">MerrBio</h3>
          <p class="tagline">Connecting you with fresh, local, organic produce.</p>
        </div>

        <!-- Column 2: Quick Links -->
        <div class="footer-column quick-links">
          <h4>Quick Links</h4>
          <ul>
            <li><router-link to="/">Home</router-link></li>
            <li><router-link to="/products">Products</router-link></li>
            <li><router-link to="/register">Register</router-link></li>
            <!-- Removed About Us and Contact as per request -->
          </ul>
        </div>

        <!-- Removed Social Media Column -->
          </div>

      <!-- Bottom Row: Copyright & Legal -->
      <div class="footer-bottom">
        <p class="copyright">&copy; {{ currentYear }} MerrBio. All rights reserved.</p>
        <div class="legal-links">
          <router-link to="/privacy">Privacy Policy</router-link>
          <span>|</span>
          <router-link to="/terms">Terms of Service</router-link>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const currentYear = computed(() => new Date().getFullYear());

// If you use icon libraries like Font Awesome, ensure they are set up in your project.
// You might need to install them (e.g., `npm install @fortawesome/fontawesome-free`)
// and import the necessary CSS in your main entry file (e.g., main.ts).
</script>

<style scoped>
.app-footer {
  background-color: #1f2937; /* Even darker background */
  color: #9ca3af; /* Adjusted text color for new background */
  padding: 4rem 0 2rem 0;
  margin-top: 5rem;
  border-top: 3px solid hsla(160, 100%, 37%, 0.8); /* Accent color top border */
  font-size: 0.95rem;
}

.footer-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem; /* Consistent padding */
}

.footer-content {
  display: grid; /* Use grid for better control */
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); /* Responsive columns */
  justify-content: space-between;
  gap: 2rem; /* Space between columns */
  margin-bottom: 3rem; /* Space before bottom row */
  padding-bottom: 2rem; /* Space within content area */
  border-bottom: 1px solid #34495e; /* Subtle separator */
}

.footer-column {
  flex: 1; /* Allow columns to grow */
  /* min-width removed as grid handles sizing */
}

.footer-column h4 {
  color: #e5e7eb; /* Adjusted heading color */
  font-size: 1.1rem;
  margin-bottom: 1.5rem;
  font-weight: 600;
  position: relative;
  padding-bottom: 0.5rem;
}

/* Underline effect for headings */
.footer-column h4::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 40px;
  height: 2px;
  background-color: hsla(160, 100%, 37%, 0.8); /* Use brand accent color */
}

.brand-info .footer-brand-name {
  font-size: 1.8rem;
  font-weight: bold;
  color: #f9fafb; /* Slightly off-white brand name */
  margin-bottom: 0.75rem;
}

.brand-info .tagline {
  color: #d1d5db; /* Adjusted tagline color */
  line-height: 1.6;
}

.quick-links ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.quick-links li {
  margin-bottom: 0.8rem;
}

.quick-links a {
  color: #d1d5db; /* Adjusted link color */
  text-decoration: none;
  transition: color 0.3s ease, padding-left 0.3s ease;
}

.quick-links a:hover {
  color: #ffffff;
  background-color: rgba(255, 255, 255, 0.05); /* Subtle background highlight on hover */
  padding-left: 8px;
}

/* Styles for removed social media column are gone */

.footer-bottom {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  padding-top: 1.5rem;
  font-size: 0.9rem;
  color: #9ca3af; /* Adjusted bottom row color */
}

.copyright {
  margin: 0;
}

.legal-links a {
  color: #9ca3af; /* Adjusted legal link color */
  text-decoration: none;
  margin: 0 0.5rem;
  transition: color 0.3s ease;
}

.legal-links a:hover {
  color: #e5e7eb; /* Adjusted legal link hover color */
}

.legal-links span {
  color: #4b5563; /* Adjusted separator color */
  margin: 0 0.25rem;
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .footer-content {
    flex-direction: column; /* Stack columns vertically */
    align-items: flex-start; /* Align items to the start */
    gap: 2.5rem; /* Increase gap when stacked */
  }

  .footer-column {
    min-width: 100%; /* Make columns full width */
  }

  .footer-bottom {
    flex-direction: column;
    align-items: center; /* Center bottom content */
    text-align: center;
  }

  .legal-links {
    margin-top: 0.5rem;
  }
}

@media (max-width: 480px) {
  .app-footer {
    padding: 3rem 0 1.5rem 0;
  }
  .footer-container {
    padding: 0 1rem;
  }
  .footer-column h4 {
    font-size: 1rem;
  }
  .brand-info .footer-brand-name {
    font-size: 1.6rem;
  }
}
</style>