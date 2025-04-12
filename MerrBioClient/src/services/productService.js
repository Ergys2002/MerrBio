/**
 * Fetches product details by ID. Simulates an API call.
 * @param {string|number} id - The ID of the product to fetch.
 * @returns {Promise<object>} A promise that resolves with the product details.
 */
export const getProductById = (id) => {
  return new Promise((resolve) => {
    // Simulate network delay
    setTimeout(() => {
      const product = {
        id: id,
        name: 'Sample Product',
        description: 'This is a detailed description of the sample product.',
        price: 9.99,
        imageUrl: `https://via.placeholder.com/150/0000FF/808080?text=Product+${id}` // Example image URL
      };
      resolve(product);
    }, 500); // Simulate 500ms delay
  });
};