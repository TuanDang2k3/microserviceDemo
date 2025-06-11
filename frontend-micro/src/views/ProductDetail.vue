<template>
  <div class="product-detail-page">
    <!-- Breadcrumb -->
    <div class="breadcrumb">
      <router-link to="/">Trang chủ</router-link>
      <i class="fas fa-chevron-right"></i>
      <router-link to="/products">Sản phẩm</router-link>
      <i class="fas fa-chevron-right"></i>
      <span>{{ product ? product.name : 'Đang tải...' }}</span>
    </div>

    <!-- Loading state -->
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Đang tải thông tin sản phẩm...</p>
    </div>

    <!-- Error state -->
    <div v-else-if="error" class="error-message">
      <i class="fas fa-exclamation-triangle"></i>
      <h3>Có lỗi xảy ra</h3>
      <p>{{ error }}</p>
      <button @click="fetchProductDetails" class="btn-retry">Thử lại</button>
    </div>

    <!-- Product content -->
    <div v-else-if="product" class="product-content">
      <div class="product-images">
        <div class="main-image">
          <img :src="product.imageUrl" :alt="product.name">
        </div>
      </div>

      <div class="product-info">
        <div class="product-badges">
          <span v-if="product.available" class="badge in-stock">Còn hàng</span>
          <span v-else class="badge out-of-stock">Hết hàng</span>
        </div>

        <h1 class="product-title">{{ product.name }}</h1>
        
        <div class="product-brand">
          <span>Thương hiệu:</span> {{ product.brand }}
        </div>
        
        <div class="product-price">
          {{ formatCurrency(product.price) }}
        </div>

        <div class="product-short-desc">
          {{ product.description }}
        </div>

        <div class="product-actions">
          <div class="quantity">
            <button @click="updateQuantity(-1)" :disabled="quantity <= 1">-</button>
            <input type="number" v-model.number="quantity" min="1" :max="product.stockQuantity">
            <button @click="updateQuantity(1)" :disabled="quantity >= product.stockQuantity">+</button>
          </div>
          
          <button 
            class="btn-add-cart" 
            @click="addToCart"
            :disabled="!product.available || product.stockQuantity <= 0"
          >
            <i class="fas fa-shopping-cart"></i>
            Thêm vào giỏ hàng
          </button>
        </div>

        <div class="product-meta">
          <div class="meta-item">
            <i class="fas fa-box"></i>
            <span>Tồn kho:</span> {{ product.stockQuantity }} sản phẩm
          </div>
          <div class="meta-item">
            <i class="fas fa-tag"></i>
            <span>Loại:</span> {{ product.category }}
          </div>
          <div class="meta-item">
            <i class="fas fa-barcode"></i>
            <span>Mã sản phẩm:</span> {{ product.id.substring(0, 8) }}...
          </div>
        </div>

        <!-- Product features -->
        <div class="product-features">
          <div class="feature">
            <i class="fas fa-truck"></i>
            <span>Giao hàng nhanh chóng</span>
          </div>
          <div class="feature">
            <i class="fas fa-shield-alt"></i>
            <span>Bảo hành chính hãng</span>
          </div>
          <div class="feature">
            <i class="fas fa-exchange-alt"></i>
            <span>Đổi trả trong 7 ngày</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Product tabs -->
    <div v-if="product" class="product-tabs">
      <div class="tabs-header">
        <button 
          :class="['tab-btn', { active: activeTab === 'description' }]"
          @click="activeTab = 'description'"
        >
          Chi tiết sản phẩm
        </button>
        <button 
          :class="['tab-btn', { active: activeTab === 'reviews' }]"
          @click="activeTab = 'reviews'"
        >
          Đánh giá ({{ ratings.length }})
        </button>
      </div>

      <div class="tabs-content">
        <!-- Description tab -->
        <div v-show="activeTab === 'description'" class="tab-content">
          <div class="product-specs">
            <h3>Thông số kỹ thuật</h3>
            <table>
              <tr>
                <td>Tên sản phẩm</td>
                <td>{{ product.name }}</td>
              </tr>
              <tr>
                <td>Thương hiệu</td>
                <td>{{ product.brand }}</td>
              </tr>
              <tr>
                <td>Danh mục</td>
                <td>{{ product.category }}</td>
              </tr>
              <tr>
                <td>Tình trạng</td>
                <td>{{ product.available ? 'Còn hàng' : 'Hết hàng' }}</td>
              </tr>
              <tr>
                <td>Số lượng</td>
                <td>{{ product.stockQuantity }}</td>
              </tr>
            </table>
          </div>

          <div class="product-full-desc">
            <h3>Mô tả sản phẩm</h3>
            <div class="desc-content">
              <p>{{ product.description }}</p>
              
              <!-- Phần mô tả bổ sung -->
              <div v-if="product.category === 'premium'" class="additional-desc">
                <h4>Đặc điểm nổi bật</h4>
                <ul>
                  <li>Chế tác thủ công từ gỗ cao cấp</li>
                  <li>Trọng lượng và cân bằng hoàn hảo</li>
                  <li>Đầu gậy chất lượng cao với khả năng dẫn lực tuyệt vời</li>
                  <li>Kiểu dáng sang trọng với các chi tiết khảm tinh tế</li>
                  <li>Công nghệ chống vặn hiện đại</li>
                </ul>
              </div>
            </div>
          </div>
        </div>

        <!-- Reviews tab -->
        <div v-show="activeTab === 'reviews'" class="tab-content">
          <div class="reviews-summary">
            <div class="rating-average">
              <div class="rating-number">
                {{ ratings.length > 0 ? (ratings.reduce((sum, r) => sum + r.rating, 0) / ratings.length).toFixed(1) : '0.0' }}
              </div>
              <div class="rating-stars">
                <i v-for="i in 5" :key="i" :class="getStarClass(i, ratings.length > 0 ? (ratings.reduce((sum, r) => sum + r.rating, 0) / ratings.length) : 0)"></i>
              </div>
              <div class="rating-count">{{ ratings.length }} đánh giá</div>
            </div>
          </div>

          <div v-if="ratings.length === 0" class="no-reviews">
            <i class="far fa-comment-alt"></i>
            <p>Sản phẩm chưa có đánh giá nào</p>
          </div>

          <div v-else class="reviews-list">
            <div v-for="review in ratings" :key="review.id" class="review">
              <div class="review-header">
                <div class="review-stars">
                  <i v-for="i in 5" :key="i" :class="getStarClass(i, review.rating)"></i>
                </div>
                <div class="review-date">{{ formatDate(review.createdAt) }}</div>
              </div>
              <div class="review-content">{{ review.comment }}</div>
            </div>
          </div>

          <div v-if="isLoggedIn && !hasUserReviewed" class="add-review">
            <h3>Viết đánh giá của bạn</h3>
            <div class="rating-select">
              <span>Đánh giá:</span>
              <div class="star-rating">
                <i v-for="i in 5" 
                   :key="i" 
                   :class="getStarClass(i, newRating.rating)"
                   @click="newRating.rating = i"></i>
              </div>
            </div>
            <div class="review-form">
              <textarea 
                v-model="newRating.comment"
                placeholder="Nhập nhận xét của bạn về sản phẩm này..."
                rows="3"
              ></textarea>
              <button @click="submitRating" class="btn-submit">Gửi đánh giá</button>
            </div>
          </div>
          
          <div v-else-if="isLoggedIn && hasUserReviewed" class="already-reviewed">
            <p>Bạn đã đánh giá sản phẩm này. Cảm ơn bạn đã đóng góp ý kiến!</p>
          </div>

          <div v-else class="login-to-review">
            <p>Vui lòng <router-link to="/login">đăng nhập</router-link> để viết đánh giá.</p>
          </div>
          
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {

  data() {
    return {
      product: null,
      ratings: [],
      loading: true,
      error: null,
      quantity: 1,
      activeTab: 'description',
      newRating: {
        rating: 5,
        comment: ''
      },
      id : '',
    }
  },
  computed: {
    isLoggedIn() {
      return !!localStorage.getItem('userId');
    },
    hasUserReviewed() {
      const userId = localStorage.getItem('userId');
      if (!userId || !this.ratings || !this.ratings.length) return false;
      
      return this.ratings.some(rating => rating.userId === userId);
    }
  },
  created() {
    this.id = this.$route.params.id;
    this.fetchProductDetails();
    this.fetchProductRatings();
  },
  methods: {
    async fetchProductDetails() {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await axios.get(`http://localhost:8888/api/v1/products/${this.id}`);
        this.product = response.data.result || response.data;
      } catch (error) {
        console.error('Error fetching product details:', error);
        this.error = 'Không thể tải thông tin sản phẩm. Vui lòng thử lại sau.';
      } finally {
        this.loading = false;
      }
    },
    async fetchProductRatings() {
      // Đảm bảo this.id đã được định nghĩa
      console.log('id',this.id);
      if (!this.id) {
        console.warn('ProductID is undefined, cannot fetch ratings');
        return;
      }
      
      try {
        // Sửa endpoint từ /rating/ thành /ratings/ theo đúng API của bạn
        const response = await axios.get(`http://localhost:8888/api/v1/ratings/product/${this.id}`);
        
        // Xử lý cấu trúc dữ liệu phân trang từ API response
        if (response.data && response.data.result) {
          // Kiểm tra xem có thuộc tính content không (dữ liệu phân trang)
          if (response.data.result.content) {
            this.ratings = response.data.result.content;
          } else {
            this.ratings = response.data.result;
          }
        } else {
          this.ratings = [];
        }
        
        console.log('Product ratings loaded:', this.ratings.length);
      } catch (error) {
        console.error('Error fetching product ratings:', error);
        this.ratings = []; // Đặt mảng trống trong trường hợp lỗi
      }
    },
    formatCurrency(amount) {
      return new Intl.NumberFormat('vi-VN', { 
        style: 'currency', 
        currency: 'VND' 
      }).format(amount);
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return new Intl.DateTimeFormat('vi-VN', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      }).format(date);
    },
    updateQuantity(change) {
      const newQuantity = this.quantity + change;
      if (newQuantity >= 1 && newQuantity <= this.product.stockQuantity) {
        this.quantity = newQuantity;
      }
    },
    async addToCart() {
      const userId = localStorage.getItem('userId');
      
      if (!userId) {
        this.$router.push('/login');
        return;
      }
      
      try {
        await axios.post(
          `http://localhost:8888/api/v1/carts/${userId}/items`,
          {
            productId: this.product.id,
            quantity: this.quantity
          }
        );
        
        window.dispatchEvent(new Event('cart-updated'));
        alert('Sản phẩm đã được thêm vào giỏ hàng!');
      } catch (error) {
        console.error('Error adding to cart:', error);
        alert('Không thể thêm sản phẩm vào giỏ hàng. Vui lòng thử lại sau.');
      }
    },
    async submitRating() {
      const userId = localStorage.getItem('userId');
      
      if (!userId) {
        this.$router.push('/login');
        return;
      }
      
      if (!this.newRating.comment.trim()) {
        alert('Vui lòng nhập nhận xét của bạn');
        return;
      }
      
      try {
        // Sửa endpoint từ rating thành ratings (thêm s)
        await axios.post(
          'http://localhost:8888/api/v1/ratings',
          {
            userId: userId,
            productId: this.id, // Đảm bảo dùng this.id thay vì this.product.id
            rating: this.newRating.rating,
            comment: this.newRating.comment
          }
        );
        
        // Reset form and refresh ratings
        this.newRating.comment = '';
        this.newRating.rating = 5;
        
        // Đảm bảo đợi fetchProductRatings hoàn thành trước khi hiển thị thông báo
        await this.fetchProductRatings();
        
        alert('Cảm ơn bạn đã đánh giá sản phẩm!');
      } catch (error) {
        console.error('Error submitting rating:', error);
        alert('Không thể gửi đánh giá. Vui lòng thử lại sau.');
      }
    },
    getStarClass(position, rating) {
      if (rating >= position) return 'fas fa-star';
      if (rating >= position - 0.5) return 'fas fa-star-half-alt';
      return 'far fa-star';
    }
  }
}
</script>

<style scoped>
.product-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem 0;
}

/* Breadcrumb */
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 2rem;
  font-size: 0.9rem;
}

.breadcrumb a {
  color: #3498db;
  text-decoration: none;
}

.breadcrumb i {
  color: #999;
  font-size: 0.7rem;
}

.breadcrumb span {
  color: #666;
}

/* Loading */
.loading {
  text-align: center;
  padding: 3rem 0;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top: 4px solid #3498db;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Error message */
.error-message {
  text-align: center;
  padding: 3rem;
  background: #ffebee;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.error-message i {
  color: #e74c3c;
  font-size: 3rem;
  margin-bottom: 1rem;
}

.btn-retry {
  background: #3498db;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 1rem;
}

/* Product content */
.product-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin-bottom: 3rem;
}

/* Product images */
.product-images {
  background: white;
  border-radius: 8px;
  padding: 1rem;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.main-image {
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.main-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

/* Product info */
.product-info {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.product-badges {
  margin-bottom: 1rem;
}

.badge {
  display: inline-block;
  padding: 0.3rem 0.7rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 600;
}

.in-stock {
  background: #e8f5e9;
  color: #388e3c;
}

.out-of-stock {
  background: #ffebee;
  color: #d32f2f;
}

.product-title {
  font-size: 1.8rem;
  margin: 0 0 1rem;
}

.product-brand {
  color: #666;
  margin-bottom: 1rem;
}

.product-brand span {
  font-weight: 500;
}

.product-price {
  font-size: 1.8rem;
  color: #e74c3c;
  font-weight: 600;
  margin-bottom: 1.5rem;
}

.product-short-desc {
  margin-bottom: 1.5rem;
  line-height: 1.6;
  color: #555;
}

/* Product actions */
.product-actions {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.quantity {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 4px;
  overflow: hidden;
}

.quantity button {
  width: 36px;
  height: 36px;
  border: none;
  background: #eee;
  cursor: pointer;
  font-size: 1rem;
}

.quantity input {
  width: 50px;
  height: 36px;
  border: none;
  text-align: center;
  background: #f5f5f5;
  font-size: 1rem;
}

.btn-add-cart {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 0 1rem;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-add-cart:hover {
  background: #2980b9;
}

.btn-add-cart:disabled {
  background: #95a5a6;
  cursor: not-allowed;
}

/* Product meta */
.product-meta {
  background: #f9f9f9;
  border-radius: 4px;
  padding: 1rem;
  margin-bottom: 1.5rem;
}

.meta-item {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
}

.meta-item:last-child {
  margin-bottom: 0;
}

.meta-item i {
  width: 20px;
  color: #3498db;
  margin-right: 0.5rem;
}

.meta-item span {
  font-weight: 500;
  margin-right: 0.5rem;
}

/* Product features */
.product-features {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.feature {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  background: #f9f9f9;
  padding: 1rem;
  border-radius: 4px;
}

.feature i {
  font-size: 1.5rem;
  color: #3498db;
  margin-bottom: 0.5rem;
}

.feature span {
  font-size: 0.9rem;
  color: #555;
}

/* Tabs */
.product-tabs {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.tabs-header {
  display: flex;
  background: #f5f5f5;
}

.tab-btn {
  padding: 1rem 1.5rem;
  background: none;
  border: none;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  border-bottom: 2px solid transparent;
}

.tab-btn.active {
  background: white;
  border-bottom: 2px solid #3498db;
  color: #3498db;
}

.tab-content {
  padding: 2rem;
}

/* Product specs & description */
.product-specs {
  margin-bottom: 2rem;
}

.product-specs h3, 
.product-full-desc h3 {
  margin-top: 0;
  margin-bottom: 1rem;
  font-size: 1.3rem;
}

.product-specs table {
  width: 100%;
  border-collapse: collapse;
}

.product-specs table tr {
  border-bottom: 1px solid #eee;
}

.product-specs table td {
  padding: 0.75rem;
}

.product-specs table td:first-child {
  width: 30%;
  font-weight: 500;
  color: #555;
}

.product-full-desc {
  line-height: 1.7;
}

.desc-content {
  color: #555;
}

.additional-desc {
  margin-top: 1.5rem;
}

.additional-desc h4 {
  margin: 1rem 0 0.5rem;
}

.additional-desc ul {
  padding-left: 1.5rem;
}

.additional-desc li {
  margin-bottom: 0.5rem;
}

/* Reviews */
.reviews-summary {
  margin-bottom: 2rem;
  text-align: center;
}

.rating-average {
  display: inline-block;
}

.rating-number {
  font-size: 3rem;
  font-weight: bold;
  line-height: 1;
  color: #333;
}

.rating-stars {
  color: #f39c12;
  font-size: 1.3rem;
  margin: 0.5rem 0;
}

.rating-count {
  color: #666;
  font-size: 0.9rem;
}

.no-reviews {
  text-align: center;
  padding: 2rem;
  color: #666;
}

.no-reviews i {
  font-size: 2rem;
  color: #ddd;
  margin-bottom: 1rem;
}

.reviews-list {
  margin-bottom: 2rem;
}

.review {
  padding: 1.5rem;
  border-bottom: 1px solid #eee;
}

.review:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.75rem;
}

.review-stars {
  color: #f39c12;
}

.review-date {
  color: #999;
  font-size: 0.9rem;
}

.review-content {
  line-height: 1.6;
  color: #555;
}

/* Add review */
.add-review {
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid #eee;
}

.add-review h3 {
  margin-top: 0;
  margin-bottom: 1rem;
}

.rating-select {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.star-rating {
  color: #ddd;
  font-size: 1.3rem;
  cursor: pointer;
}

.star-rating i {
  margin-right: 0.3rem;
}

.star-rating i.fas {
  color: #f39c12;
}

.review-form textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  margin-bottom: 1rem;
  font-family: inherit;
  font-size: 0.9rem;
}

.btn-submit {
  background: #3498db;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
}

.login-to-review {
  text-align: center;
  padding: 1.5rem;
  background: #f9f9f9;
  border-radius: 4px;
  margin-top: 1rem;
}

.login-to-review a {
  color: #3498db;
  font-weight: 500;
}

/* Thêm CSS cho thông báo đã đánh giá */
.already-reviewed {
  text-align: center;
  padding: 1.5rem;
  background: #e8f5e9;
  border-radius: 4px;
  margin-top: 1rem;
  color: #2e7d32;
}

/* Responsive design */
@media (max-width: 992px) {
  .product-content {
    grid-template-columns: 1fr;
  }
  
  .main-image {
    height: 350px;
  }
}

@media (max-width: 768px) {
  .product-features {
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .tabs-header {
    flex-direction: column;
  }
  
  .tab-btn {
    text-align: left;
    border-left: 2px solid transparent;
    border-bottom: none;
  }
  
  .tab-btn.active {
    border-left: 2px solid #3498db;
    border-bottom: none;
  }
  
  .tab-content {
    padding: 1.5rem;
  }
}
</style>