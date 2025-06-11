<template>
  <div class="product-card">
    <div class="product-image">
      <img :src="product.image || 'https://via.placeholder.com/300'" :alt="product.name">
    </div>
    
    <div class="product-details">
      <h3 class="product-name">{{ product.name }}</h3>
      
      <!-- Thêm đánh giá sao -->
      <div class="product-rating">
        <StarRating 
          :rating="product.averageRating || 0" 
          :readOnly="true"
          :showCount="true"
          :count="product.ratingCount || 0" 
        />
      </div>
      
      <div class="product-price">{{ formatCurrency(product.price) }}</div>
      
      <div class="product-actions">
        <button class="btn-view" @click="viewDetails">
          <i class="fas fa-eye"></i>
          Chi tiết
        </button>
        
        <button class="btn-add" @click="addToCart">
          <i class="fas fa-shopping-cart"></i>
          Thêm vào giỏ
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import StarRating from './StarRating.vue';

export default {
  components: {
    StarRating
  },
  // Phần code khác giữ nguyên
}
</script>

<style scoped>
/* Các style hiện tại giữ nguyên */

.product-rating {
  margin: 0.5rem 0;
}
</style>