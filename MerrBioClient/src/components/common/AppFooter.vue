<template>
  <footer class="app-footer">
    <div class="container">
      <p>&copy; 2025 MerrBio. All rights reserved.</p>
      <div class="footer-links">
        <router-link to="/privacy">Privacy Policy</router-link>
        <router-link to="/terms">Terms of Service</router-link>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
// No script logic needed for this basic footer yet
</script>

<style scoped>
.app-footer {
  background-color: #f8f9fa;
  padding: 20px 0;
  margin-top: 40px;
  border-top: 1px solid #e9ecef;
  color: #6c757d;
  text-align: center;
}

.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-links {
  margin-top: 10px;
}

.footer-links a {
  color: #6c757d;
  text-decoration: none;
  margin: 0 10px;
  transition: color 0.3s;
}

.footer-links a:hover {
  color: #4caf50;
}
</style>