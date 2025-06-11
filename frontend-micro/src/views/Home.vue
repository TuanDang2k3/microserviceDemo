<template>
  <div class="home">
    <div class="hero-slider">
      <div class="hero-slide">
        <div class="slide-content">
          <h1>Nâng tầm trải nghiệm <br>Chơi bida của bạn</h1>
          <p>Khám phá bộ sưu tập gậy bida cao cấp và phụ kiện chính hãng</p>
          <router-link to="/products" class="btn btn-primary">Mua sắm ngay</router-link>
        </div>
        <img src="https://i.imgur.com/LJ7KJn6.jpg" alt="Premium Billiard Cues">
      </div>
    </div>
    
    <section class="featured-categories">
      <div class="section-title">
        <h2>Danh mục nổi bật</h2>
        <p>Lựa chọn từ các thương hiệu hàng đầu thế giới</p>
      </div>
      
      <div class="categories">
        <div class="category" @click="navigateToCategory('premium')">
          <div class="category-image">
            <img src="https://i.imgur.com/LJ7KJn6.jpg" alt="Premium Cues">
          </div>
          <div class="category-content">
            <h3>Gậy bida cao cấp</h3>
            <p>Dành cho người chơi chuyên nghiệp</p>
            <span class="view-more">Xem thêm <i class="fas fa-arrow-right"></i></span>
          </div>
        </div>
        
        <div class="category" @click="navigateToCategory('standard')">
          <div class="category-image">
            <img src="https://i.imgur.com/4cSznJF.jpg" alt="Standard Cues">
          </div>
          <div class="category-content">
            <h3>Gậy bida phổ thông</h3>
            <p>Chất lượng tốt, giá hợp lý</p>
            <span class="view-more">Xem thêm <i class="fas fa-arrow-right"></i></span>
          </div>
        </div>
        
        <div class="category" @click="navigateToCategory('accessories')">
          <div class="category-image">
            <img src="https://i.imgur.com/AQryZ3i.jpg" alt="Accessories">
          </div>
          <div class="category-content">
            <h3>Phụ kiện bida</h3>
            <p>Đa dạng, chính hãng</p>
            <span class="view-more">Xem thêm <i class="fas fa-arrow-right"></i></span>
          </div>
        </div>
      </div>
    </section>
    
    <section class="featured-products">
      <div class="section-title">
        <h2>Sản phẩm nổi bật</h2>
        <p>Những sản phẩm được yêu thích nhất</p>
        <router-link to="/products" class="view-all">Xem tất cả <i class="fas fa-long-arrow-alt-right"></i></router-link>
      </div>
      
      <div class="products-slider" v-if="!loading">
        <div v-if="error" class="error">{{ error }}</div>
        
        <div v-else class="products">
          <div v-for="product in featuredProducts" :key="product.id" class="product-card">
            <div class="product-image">
              <img :src="product.imageUrl" :alt="product.name">
              <div class="product-actions">
                <button @click="addToCart(product)" class="add-to-cart" title="Thêm vào giỏ hàng">
                  <i class="fas fa-shopping-cart"></i>
                </button>
                <router-link :to="`/products/${product.id}`" class="view-details" title="Xem chi tiết">
                  <i class="fas fa-eye"></i>
                </router-link>
              </div>
            </div>
            
            <div class="product-info">
              <div class="product-category">{{ product.category }}</div>
              <h3 class="product-name">{{ product.name }}</h3>
              <div class="product-brand">{{ product.brand }}</div>
              <div class="product-price">{{ formatCurrency(product.price) }}</div>
            </div>
          </div>
        </div>
      </div>
      
      <div v-else class="products-loading">
        <div class="spinner"></div>
        <p>Đang tải sản phẩm...</p>
      </div>
    </section>
    
    <section class="benefits">
      <div class="benefit">
        <div class="benefit-icon">
          <i class="fas fa-medal"></i>
        </div>
        <h3>Sản phẩm chính hãng</h3>
        <p>100% sản phẩm được nhập khẩu chính hãng với giấy tờ đầy đủ</p>
      </div>
      
      <div class="benefit">
        <div class="benefit-icon">
          <i class="fas fa-shipping-fast"></i>
        </div>
        <h3>Giao hàng nhanh chóng</h3>
        <p>Giao hàng toàn quốc từ 1-3 ngày với đội ngũ vận chuyển chuyên nghiệp</p>
      </div>
      
      <div class="benefit">
        <div class="benefit-icon">
          <i class="fas fa-shield-alt"></i>
        </div>
        <h3>Bảo hành dài hạn</h3>
        <p>Tất cả sản phẩm đều được bảo hành chính hãng từ 12-36 tháng</p>
      </div>
      
      <div class="benefit">
        <div class="benefit-icon">
          <i class="fas fa-headset"></i>
        </div>
        <h3>Hỗ trợ 24/7</h3>
        <p>Đội ngũ tư vấn chuyên nghiệp luôn sẵn sàng hỗ trợ bạn mọi lúc</p>
      </div>
    </section>
    
    <section class="brands">
      <div class="section-title">
        <h2>Thương hiệu hợp tác</h2>
      </div>
      
      <div class="brand-logos">
        <div class="brand">Predator</div>
        <div class="brand">McDermott</div>
        <div class="brand">Mezz</div>
        <div class="brand">Pechauer</div>
        <div class="brand">Cuetec</div>
        <div class="brand">Longoni</div>
      </div>
    </section>
    
    <section class="testimonials">
      <div class="section-title">
        <h2>Khách hàng nói gì về chúng tôi</h2>
      </div>
      
      <div class="testimonial-cards">
        <div class="testimonial">
          <div class="testimonial-stars">
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
          </div>
          <p class="testimonial-text">
            "Shop phục vụ tận tâm, gậy bida chính hãng và giá cả hợp lý. Tôi sẽ quay lại ủng hộ shop trong tương lai!"
          </p>
          <div class="testimonial-author">
            <div class="author-avatar">NH</div>
            <div class="author-info">
              <h4>Nguyễn Hoàng</h4>
              <p>Khách hàng thân thiết</p>
            </div>
          </div>
        </div>
        
        <div class="testimonial">
          <div class="testimonial-stars">
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
          </div>
          <p class="testimonial-text">
            "Mua gậy Predator ở đây giá tốt hơn nhiều shop khác, hàng chính hãng và được tư vấn nhiệt tình."
          </p>
          <div class="testimonial-author">
            <div class="author-avatar">TL</div>
            <div class="author-info">
              <h4>Trần Linh</h4>
              <p>Chủ CLB Bida</p>
            </div>
          </div>
        </div>
        
        <div class="testimonial">
          <div class="testimonial-stars">
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star-half-alt"></i>
          </div>
          <p class="testimonial-text">
            "Đã mua nhiều đồ ở đây, từ gậy đến phụ kiện đều chất lượng. Giao hàng nhanh và đóng gói cẩn thận."
          </p>
          <div class="testimonial-author">
            <div class="author-avatar">PH</div>
            <div class="author-info">
              <h4>Phạm Hùng</h4>
              <p>Vận động viên bida</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      featuredProducts: [],
      loading: true,
      error: null
    }
  },
  created() {
    this.fetchFeaturedProducts();
  },
  methods: {
    async fetchFeaturedProducts() {
      try {
        const response = await axios.get('http://localhost:8888/api/v1/products');
        const products = response.data.result || response.data || [];
        
        // Get random products as featured
        const shuffled = [...products].sort(() => 0.5 - Math.random());
        this.featuredProducts = shuffled.slice(0, Math.min(8, products.length));
        this.loading = false;
      } catch (error) {
        console.error('Error fetching featured products:', error);
        this.error = 'Không thể tải sản phẩm. Vui lòng thử lại sau.';
        this.loading = false;
      }
    },
    formatCurrency(amount) {
      return new Intl.NumberFormat('vi-VN', { 
        style: 'currency', 
        currency: 'VND' 
      }).format(amount);
    },
    async addToCart(product) {
      const userId = localStorage.getItem('userId');
      
      if (!userId) {
        this.$router.push('/login');
        return;
      }
      
      try {
        await axios.post(
          `http://localhost:8888/api/v1/carts/${userId}/items`,
          {
            productId: product.id,
            quantity: 1
          }
        );
        
        window.dispatchEvent(new Event('cart-updated'));
        
        // Create notification element
        const notification = document.createElement('div');
        notification.className = 'add-cart-notification';
        notification.innerHTML = `
          <i class="fas fa-check-circle"></i>
          <p>Đã thêm vào giỏ hàng!</p>
        `;
        document.body.appendChild(notification);
        
        // Remove after animation
        setTimeout(() => {
          notification.classList.add('show');
          setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => {
              document.body.removeChild(notification);
            }, 300);
          }, 2000);
        }, 10);
      } catch (error) {
        console.error('Error adding to cart:', error);
        alert('Không thể thêm sản phẩm vào giỏ hàng. Vui lòng thử lại sau.');
      }
    },
    navigateToCategory(category) {
      this.$router.push({
        path: '/products',
        query: { category }
      });
    }
  }
}
</script>

<style scoped>
.home {
  max-width: 100%;
  overflow-x: hidden;
}

/* Hero Section */
.hero-slider {
  height: 600px;
  position: relative;
  margin: -2rem -2rem 2rem;
}

.hero-slide {
  height: 100%;
  display: flex;
  position: relative;
  overflow: hidden;
}

.hero-slide::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, rgba(0,0,0,0.8) 0%, rgba(0,0,0,0.4) 50%, rgba(0,0,0,0) 100%);
  z-index: 1;
}

.hero-slide img {
  position: absolute;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  z-index: 0;
}

.slide-content {
  position: relative;
  z-index: 2;
  max-width: 600px;
  padding: 4rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
}

.slide-content h1 {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  line-height: 1.2;
}

.slide-content p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

/* Section Styles */
section {
  margin: 5rem 0;
}

.section-title {
  text-align: center;
  margin-bottom: 3rem;
  position: relative;
}

.section-title h2 {
  font-size: 2rem;
  margin-bottom: 0.5rem;
  position: relative;
  display: inline-block;
}

.section-title h2::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  width: 60px;
  height: 3px;
  background-color: var(--secondary-color);
  transform: translateX(-50%);
}

.section-title p {
  color: var(--gray);
  font-size: 1.1rem;
}

.view-all {
  position: absolute;
  right: 0;
  top: 0;
  font-size: 0.9rem;
  color: var(--secondary-color);
  text-decoration: none;
  display: flex;
  align-items: center;
  transition: all 0.3s;
}

.view-all i {
  margin-left: 0.5rem;
  transition: transform 0.3s;
}

.view-all:hover {
  color: #2980b9;
}

.view-all:hover i {
  transform: translateX(5px);
}

/* Featured Categories */
.categories {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2rem;
}

.category {
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--shadow);
  transition: all 0.3s;
  cursor: pointer;
  position: relative;
  background: white;
}

.category:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

.category-image {
  height: 200px;
  overflow: hidden;
}

.category-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.category:hover .category-image img {
  transform: scale(1.1);
}

.category-content {
  padding: 1.5rem;
}

.category-content h3 {
  margin: 0 0 0.5rem;
  font-size: 1.2rem;
}

.category-content p {
  color: var(--gray);
  margin-bottom: 1rem;
  font-size: 0.9rem;
}

.view-more {
  color: var(--secondary-color);
  font-size: 0.9rem;
  font-weight: 500;
  display: flex;
  align-items: center;
}

.view-more i {
  margin-left: 0.5rem;
  transition: transform 0.3s;
}

.category:hover .view-more i {
  transform: translateX(5px);
}

/* Products */
.products-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 0;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border-left-color: var(--secondary-color);
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.products {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
}

.product-card {
  background: white;
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--shadow);
  transition: all 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

.product-image {
  height: 250px;
  position: relative;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.product-card:hover .product-image img {
  transform: scale(1.1);
}

.product-actions {
  position: absolute;
  bottom: -50px;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 1rem;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.9);
  transition: bottom 0.3s;
}

.product-card:hover .product-actions {
  bottom: 0;
}

.product-actions button, .product-actions a {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
  color: white;
  text-decoration: none;
}

.add-to-cart {
  background-color: var(--secondary-color);
  border: none;
}

.view-details {
  background-color: var(--primary-color);
}

.add-to-cart:hover, .view-details:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 10px rgba(0, 0, 0, 0.1);
}

.product-info {
  padding: 1.5rem;
}

.product-category {
  font-size: 0.8rem;
  color: var(--secondary-color);
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 0.5rem;
}

.product-name {
  margin: 0 0 0.5rem;
  font-size: 1.1rem;
  height: 2.8rem;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-brand {
  color: var(--gray);
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.product-price {
  font-weight: 600;
  font-size: 1.2rem;
  color: var(--accent-color);
}

/* Benefits */
.benefits {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
  margin: 5rem 0;
}

.benefit {
  background: white;
  border-radius: var(--radius);
  padding: 2rem;
  text-align: center;
  box-shadow: var(--shadow);
  transition: all 0.3s;
}

.benefit:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

.benefit-icon {
  width: 70px;
  height: 70px;
  background-color: rgba(52, 152, 219, 0.1);
  color: var(--secondary-color);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1.8rem;
  border-radius: 50%;
  margin: 0 auto 1.5rem;
}

.benefit h3 {
  margin: 0 0 1rem;
  font-size: 1.1rem;
}

.benefit p {
  color: var(--gray);
  font-size: 0.9rem;
  line-height: 1.6;
}

/* Brands */
.brand-logos {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 2rem;
}

.brand {
  flex: 1;
  min-width: 120px;
  text-align: center;
  font-size: 1.5rem;
  font-weight: 700;
  color: #aaa;
  padding: 1rem;
  transition: all 0.3s;
}

.brand:hover {
  color: var(--secondary-color);
}

/* Testimonials */
.testimonial-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2rem;
}

.testimonial {
  background: white;
  border-radius: var(--radius);
  padding: 2rem;
  box-shadow: var(--shadow);
  transition: all 0.3s;
}

.testimonial:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

.testimonial-stars {
  color: #f39c12;
  margin-bottom: 1rem;
}

.testimonial-text {
  font-style: italic;
  margin-bottom: 1.5rem;
  line-height: 1.8;
}

.testimonial-author {
  display: flex;
  align-items: center;
}

.author-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: var(--secondary-color);
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: 600;
  margin-right: 1rem;
}

.author-info h4 {
  margin: 0;
  font-size: 1rem;
}

.author-info p {
  margin: 0;
  font-size: 0.8rem;
  color: var(--gray);
}

/* Button */
.btn {
  display: inline-block;
  padding: 0.8rem 2rem;
  font-weight: 600;
  border-radius: var(--radius);
  text-decoration: none;
  transition: all 0.3s;
  text-align: center;
}

.btn-primary {
  background-color: var(--secondary-color);
  color: white;
}

.btn-primary:hover {
  background-color: #2980b9;
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

/* Add to cart notification */
.add-cart-notification {
  position: fixed;
  top: 20px;
  right: 20px;
  background: white;
  border-left: 4px solid var(--secondary-color);
  padding: 1rem 1.5rem;
  border-radius: var(--radius);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 1rem;
  z-index: 9999;
  transform: translateX(120%);
  transition: transform 0.3s ease;
}

.add-cart-notification.show {
  transform: translateX(0);
}

.add-cart-notification i {
  color: var(--secondary-color);
  font-size: 1.5rem;
}

.add-cart-notification p {
  margin: 0;
  font-weight: 500;
}

/* Responsive */
@media (max-width: 1200px) {
  .products {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .benefits {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .products {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .testimonial-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-slider {
    height: 500px;
  }
  
  .slide-content {
    padding: 2rem;
  }
  
  .slide-content h1 {
    font-size: 2rem;
  }
  
  .categories {
    grid-template-columns: 1fr;
  }
  
  .products {
    grid-template-columns: repeat(2, 1fr);
    gap: 1.5rem;
  }
  
  .testimonial-cards {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 576px) {
  .hero-slider {
    height: 400px;
  }
  
  .products {
    grid-template-columns: 1fr;
  }
  
  .benefits {
    grid-template-columns: 1fr;
  }
  
  .brand {
    min-width: 100px;
  }
}
</style>