# Copilot Instructions

This project is a Vue.js 3 frontend application for MerrBio - a platform connecting local farmers with consumers looking to buy fresh, organic, and local products.

## Project Overview

MerrBio enables:
* Farmers to register and publish their agricultural products
* Consumers to browse, search, and request to purchase these products
* Simple user management with distinct farmer and consumer roles
* Direct communication between farmers and consumers

## Coding Standards
* Use camelCase for variable and method names
* Use PascalCase for component names and imports
* Use single quotes for strings
* Use 2 spaces for indentation
* Use Composition API with `<script setup>` syntax
* Use TypeScript for type safety
* Use descriptive variable names
* Include JSDoc comments for components and complex functions
* Organize imports with Vue imports first, then third-party, then local
* Include error handling for API calls
* Use Pinia for state management
* Implement responsive design (mobile and desktop)
* Use Vue Router for navigation
* Follow Vue Style Guide best practices
* Use i18n for multilingual support (Albanian and English)

## Core Features

### Authentication System
* Login/signup for farmers and consumers with role selection
* Form validation for all inputs
* Protected routes based on authentication status

### Farmer Dashboard
* CRUD operations for products (add, edit, delete)
* View incoming purchase requests
* Manage product inventory and details

### Consumer Features
* Browse products with filtering/search options
* Product detail view
* Submit purchase requests to farmers

### UI Components
* Responsive navigation
* Product cards/list views
* Forms with validation
* Language switcher

## Bonus Features
* Messaging system between farmers and consumers
* Super admin dashboard for managing all users and products
* Language toggle between Albanian and English