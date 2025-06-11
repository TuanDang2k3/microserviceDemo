<template>
  <div class="products-page">
    <div class="page-header">
      <h1>Sản phẩm bida</h1>
      <div class="breadcrumbs">
        <router-link to="/">Trang chủ</router-link>
        <i class="fas fa-chevron-right"></i>
        <span>Sản phẩm</span>
      </div>
    </div>
    
    <div class="products-container">
      <div class="sidebar">
        <div class="filter-section">
          <h3>Danh mục</h3>
          <div class="filter-options">
            <div class="filter-option">
              <input type="radio" id="all-categories" name="category" value="" v-model="filters.category">
              <label for="all-categories">Tất cả</label>
            </div>
            <div class="filter-option">
              <input type="radio" id="premium" name="category" value="premium" v-model="filters.category">
              <label for="premium">Gậy cao cấp</label>
            </div>
            <div class="filter-option">
              <input type="radio" id="standard" name="category" value="standard" v-model="filters.category">
              <label for="standard">Gậy phổ thông</label>
            </div>
            <div class="filter-option">
              <input type="radio" id="accessories" name="category" value="accessories" v-model="filters.category">
              <label for="accessories">Phụ kiện</label>
            </div>
          </div>
        </div>
        
        <div class="filter-section">
          <h3>Thương hiệu</h3>
          <div class="filter-options">
            <div class="filter-option" v-for="brand in availableBrands" :key="brand">
              <input type="checkbox" :id="brand" :value="brand" v-model="filters.brands">
              <label :for="brand">{{ brand }}</label>
            </div>
          </div>
        </div>
        
        <div class="filter-section">
          <h3>Giá</h3>
          <div class="price-range">
            <div class="price-inputs">
              <div class="price-input">
                <span>Từ</span>
                <input type="number" v-model.number="filters.minPrice" placeholder="0">
              </div>
              <div class="price-input">
                <span>Đến</span>
                <input type="number" v-model.number="filters.maxPrice" placeholder="15000000">
              </div>
            </div>
            <button class="btn btn-apply" @click="applyFilters">Áp dụng</button>
          </div>
        </div>
      </div>
      
      <div class="products-main">
        <div class="products-toolbar">
          <div class="products-count">
            Hiển thị <span>{{ filteredProducts.length }}</span> sản phẩm
          </div>
          
          <div class="products-sorting">
            <label for="sort">Sắp xếp:</label>
            <select id="sort" v-model="sortOption">
              <option value="default">Mặc định</option>
              <option value="price-asc">Giá: Thấp đến cao</option>
              <option value="price-desc">Giá: Cao đến thấp</option>
              <option value="name-asc">Tên: A-Z</option>
              <option value="name-desc">Tên: Z-A</option>
            </select>
          </div>
          
          <div class="view-switcher">
            <button :class="{'active': viewMode === 'grid'}" @click="viewMode = 'grid'">
              <i class="fas fa-th"></i>
            </button>
            <button :class="{'active': viewMode === 'list'}" @click="viewMode = 'list'">
              <i class="fas fa-list"></i>
            </button>
          </div>
        </div>
        
        <div v-if="loading" class="products-loading">
          <div class="spinner"></div>
          <p>Đang tải sản phẩm...</p>
        </div>
        
        <div v-else-if="error" class="products-error">
          <i class="fas fa-exclamation-circle"></i>
          <p>{{ error }}</p>
          <button @click="fetchProducts" class="btn btn-retry">Thử lại</button>
        </div>
        
        <div v-else-if="filteredProducts.length === 0" class="products-empty">
          <i class="fas fa-search"></i>
          <p>Không tìm thấy sản phẩm nào phù hợp với bộ lọc của bạn.</p>
          <button @click="resetFilters" class="btn btn-reset">Đặt lại bộ lọc</button>
        </div>
        
        <div v-else :class="['products-grid', {'products-list': viewMode === 'list'}]">
          <div 
            v-for="product in filteredProducts" 
            :key="product.id" 
            :class="['product-card', {'product-card-list': viewMode === 'list'}]"
          >
            <div class="product-image">
              <img :src="product.imageUrl" :alt="product.name">
              <div class="product-badges">
                <span v-if="isNewProduct(product)" class="badge new">Mới</span>
                <span v-if="isPopularProduct(product)" class="badge popular">Hot</span>
              </div>
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
              <h3 class="product-name">
                <router-link :to="`/products/${product.id}`">{{ product.name }}</router-link>
              </h3>
              <div class="product-brand">{{ product.brand }}</div>
              <div class="product-price">{{ formatCurrency(product.price) }}</div>
              
              <div v-if="viewMode === 'list'" class="product-description">
                {{ truncateText(product.description, 150) }}
              </div>
              
              <div v-if="viewMode === 'list'" class="product-list-actions">
                <button @click="addToCart(product)" class="btn btn-primary add-to-cart-btn">
                  <i class="fas fa-shopping-cart"></i> Thêm vào giỏ hàng
                </button>
                <router-link :to="`/products/${product.id}`" class="btn btn-secondary">
                  Chi tiết
                </router-link>
              </div>
            </div>
          </div>
        </div>
        
        <div class="pagination">
          <button 
            :disabled="currentPage === 1" 
            @click="currentPage--"
            class="btn-page"
          >
            <i class="fas fa-chevron-left"></i>
          </button>
          
          <button 
            v-for="page in totalPages" 
            :key="page"
            :class="['btn-page', {'active': currentPage === page}]"
            @click="currentPage = page"
          >
            {{ page }}
          </button>
          
          <button 
            :disabled="currentPage === totalPages" 
            @click="currentPage++"
            class="btn-page"
          >
            <i class="fas fa-chevron-right"></i>
          </button>
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
      products: [],
      loading: true,
      error: null,
      filters: {
        category: '',
        brands: [],
        minPrice: null,
        maxPrice: null
      },
      sortOption: 'default',
      viewMode: 'grid',
      currentPage: 1,
      itemsPerPage: 12,
      popularProductIds: [] // Giả sử một số ID sản phẩm phổ biến
    };
  },
  computed: {
    availableBrands() {
      // Extract unique brands from products
      const brands = [...new Set(this.products.map(product => product.brand))];
      return brands;
    },
    filteredProducts() {
      let result = [...this.products];
      
      // Apply category filter
      if (this.filters.category) {
        result = result.filter(product => product.category === this.filters.category);
      }
      
      // Apply brand filter
      if (this.filters.brands.length > 0) {
        result = result.filter(product => this.filters.brands.includes(product.brand));
      }
      
      // Apply price filter
      if (this.filters.minPrice !== null) {
        result = result.filter(product => product.price >= this.filters.minPrice);
      }
      
      if (this.filters.maxPrice !== null) {
        result = result.filter(product => product.price <= this.filters.maxPrice);
      }
      
      // Apply sorting
      switch(this.sortOption) {
        case 'price-asc':
          result.sort((a, b) => a.price - b.price);
          break;
        case 'price-desc':
          result.sort((a, b) => b.price - a.price);
          break;
        case 'name-asc':
          result.sort((a, b) => a.name.localeCompare(b.name));
          break;
        case 'name-desc':
          result.sort((a, b) => b.name.localeCompare(a.name));
          break;
      }
      
      // Return paginated results
      const startIndex = (this.currentPage - 1) * this.itemsPerPage;
      return result.slice(startIndex, startIndex + this.itemsPerPage);
    },
    totalPages() {
      if (this.products.length === 0) return 1;
      
      let filteredCount = this.products.length;
      
      // Apply category filter for count
      if (this.filters.category) {
        filteredCount = this.products.filter(product => product.category === this.filters.category).length;
      }
      
      // Apply brand filter for count
      if (this.filters.brands.length > 0) {
        filteredCount = this.products.filter(product => this.filters.brands.includes(product.brand)).length;
      }
      
      // Apply price filter for count
      let filtered = this.products;
      if (this.filters.minPrice !== null) {
        filtered = filtered.filter(product => product.price >= this.filters.minPrice);
      }
      
      if (this.filters.maxPrice !== null) {
        filtered = filtered.filter(product => product.price <= this.filters.maxPrice);
      }
      
      filteredCount = filtered.length;
      
      return Math.ceil(filteredCount / this.itemsPerPage);
    }
  },
  created() {
    this.fetchProducts();
    
    // Check URL parameters for initial filters
    if (this.$route.query.category) {
      this.filters.category = this.$route.query.category;
    }
    
    // Simulate popular products - in real app this data would come from API
    this.popularProductIds = this.getRandomIds(3); // Random 3 products will be marked as "popular"
  },
  methods: {
    async fetchProducts() {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await axios.get('http://localhost:8888/api/v1/products');
        this.products = response.data.result || response.data;
        
        // Initial randomization for new products
        this.products = this.products.map(product => ({
          ...product,
          createdAt: product.createdAt || this.getRandomDate()
        }));
      } catch (error) {
        console.error('Error fetching products:', error);
        this.error = 'Không thể tải danh sách sản phẩm. Vui lòng thử lại sau.';
      } finally {
        this.loading = false;
      }
    },
    formatCurrency(amount) {
      return new Intl.NumberFormat('vi-VN', { 
        style: 'currency', 
        currency: 'VND' 
      }).format(amount);
    },
    truncateText(text, length) {
      if (text.length <= length) return text;
      return text.substr(0, length) + '...';
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
    applyFilters() {
      this.currentPage = 1;
    },
    resetFilters() {
      this.filters = {
        category: '',
        brands: [],
        minPrice: null,
        maxPrice: null
      };
      this.sortOption = 'default';
      this.currentPage = 1;
    },
    isNewProduct(product) {
      // Consider a product "new" if it was created in the last 30 days
      if (!product.createdAt) return false;
      
      const productDate = new Date(product.createdAt);
      const now = new Date();
      const diffTime = Math.abs(now - productDate);
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      return diffDays <= 30;
    },
    isPopularProduct(product) {
      // In a real application, this would be based on sales data
      return this.popularProductIds.includes(product.id);
    },
    getRandomIds(count) {
      const ids = this.products.map(product => product.id);
      const shuffled = [...ids].sort(() => 0.5 - Math.random());
      return shuffled.slice(0, count);
    },
    getRandomDate() {
      // Generate a random date within the last 90 days
      const now = new Date();
      const past = new Date(now.getTime() - Math.random() * 90 * 24 * 60 * 60 * 1000);
      return past.toISOString();
    }
  },
  watch: {
    sortOption() {
      this.currentPage = 1;
    },
    'filters.category'(newCategory) {
      // Update URL when category changes
      this.$router.push({
        query: { ...this.$route.query, category: newCategory || undefined }
      });
    }
  }
}
</script>

<style scoped>
.products-page {
  width: 100%;
}

.page-header {
  margin-bottom: 2rem;
  text-align: center;
}

.page-header h1 {
  font-size: 2.2rem;
  margin-bottom: 0.5rem;
}

.breadcrumbs {
  font-size: 0.9rem;
  color: var(--gray);
}

.breadcrumbs a {
  color: var(--secondary-color);
  text-decoration: none;
}

.breadcrumbs i {
  font-size: 0.7rem;
  margin: 0 0.5rem;
}

.products-container {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 2rem;
}

/* Sidebar Styles */
.sidebar {
  position: sticky;
  top: 100px;
  height: fit-content;
}

.filter-section {
  background-color: white;
  border-radius: var(--radius);
  padding: 1.5rem;
  box-shadow: var(--shadow);
  margin-bottom: 1.5rem;
}

.filter-section h3 {
  font-size: 1.1rem;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #eee;
}

.filter-options {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.filter-option {
  display: flex;
  align-items: center;
}

.filter-option input[type="checkbox"],
.filter-option input[type="radio"] {
  margin-right: 0.5rem;
}

.price-range {
  margin-top: 1rem;
}

.price-inputs {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.price-input {
  flex: 1;
}

.price-input span {
  display: block;
  font-size: 0.8rem;
  margin-bottom: 0.25rem;
  color: var(--gray);
}

.price-input input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: var(--radius);
}

.btn-apply {
  width: 100%;
  background-color: var(--secondary-color);
  color: white;
  border: none;
  padding: 0.5rem;
  border-radius: var(--radius);
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-apply:hover {
  background-color: #2980b9;
}

/* Products Main Content */
.products-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: white;
  padding: 1rem;
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  margin-bottom: 2rem;
}

.products-count span {
  font-weight: 600;
  color: var(--secondary-color);
}

.products-sorting {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.products-sorting label {
  font-size: 0.9rem;
}

.products-sorting select {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: var(--radius);
  background-color: white;
}

.view-switcher {
  display: flex;
  gap: 0.5rem;
}

.view-switcher button {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: var(--radius);
  cursor: pointer;
  transition: all 0.3s;
}

.view-switcher button.active {
  background-color: var(--secondary-color);
  color: white;
  border-color: var(--secondary-color);
}

/* Loading, Error, Empty States */
.products-loading,
.products-error,
.products-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem 0;
  background-color: white;
  border-radius: var(--radius);
  box-shadow: var(--shadow);
}

.products-error i,
.products-empty i {
  font-size: 3rem;
  color: var(--gray);
  margin-bottom: 1rem;
}

.products-error p,
.products-empty p {
  color: var(--gray);
  margin-bottom: 1.5rem;
  text-align: center;
}

.btn-retry,
.btn-reset {
  background-color: var(--secondary-color);
  color: white;
  border: none;
  padding: 0.5rem 1.5rem;
  border-radius: var(--radius);
  cursor: pointer;
  transition: all 0.3s;
}

.btn-retry:hover,
.btn-reset:hover {
  background-color: #2980b9;
}

/* Products Grid & List Views */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
}

.products-list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
}

.product-card {
  background-color: white;
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: var(--shadow);
  transition: all 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

.product-card-list {
  display: grid;
  grid-template-columns: 250px 1fr;
  align-items: stretch;
}

.product-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.product-card-list .product-image {
  height: 100%;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-badges {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  z-index: 1;
}

.badge {
  padding: 0.25rem 0.5rem;
  border-radius: 3px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.badge.new {
  background-color: var(--secondary-color);
  color: white;
}

.badge.popular {
  background-color: var(--accent-color);
  color: white;
}

.product-actions {
  position: absolute;
  bottom: -50px;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 1rem;
  padding: 0.75rem;
  background-color: rgba(255, 255, 255, 0.9);
  transition: bottom 0.3s;
}

.product-card:hover .product-actions {
  bottom: 0;
}

.product-actions button,
.product-actions a {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.3s;
  text-decoration: none;
}

.add-to-cart {
  background-color: var(--secondary-color);
  color: white;
  border: none;
  cursor: pointer;
}

.view-details {
  background-color: var(--primary-color);
  color: white;
}

.add-to-cart:hover,
.view-details:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 10px rgba(0, 0, 0, 0.1);
}

.product-info {
  padding: 1.25rem;
}

.product-card-list .product-info {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
}

.product-category {
  text-transform: uppercase;
  color: var(--secondary-color);
  font-size: 0.75rem;
  letter-spacing: 1px;
  margin-bottom: 0.5rem;
}

.product-name {
  font-size: 1.1rem;
  margin: 0 0 0.5rem;
  line-height: 1.4;
  height: 3.1rem;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-card-list .product-name {
  height: auto;
}

.product-name a {
  color: var(--text-color);
  text-decoration: none;
  transition: color 0.3s;
}

.product-name a:hover {
  color: var(--secondary-color);
}

.product-brand {
  color: var(--gray);
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.product-price {
  font-weight: 600;
  font-size: 1.2rem;
  color: var(--accent-color);
}

.product-description {
  margin: 1rem 0;
  color: var(--gray);
  font-size: 0.9rem;
  line-height: 1.6;
}

.product-list-actions {
  margin-top: auto;
  display: flex;
  gap: 1rem;
}

.add-to-cart-btn {
  flex: 2;
}

.btn-secondary {
  flex: 1;
  background-color: #f1f1f1;
  color: var(--text-color);
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: var(--radius);
  text-decoration: none;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}

.btn-primary {
  background-color: var(--secondary-color);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: var(--radius);
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.btn-primary:hover {
  background-color: #2980b9;
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
  gap: 0.5rem;
}

.btn-page {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: var(--radius);
  cursor: pointer;
  transition: all 0.3s;
}

.btn-page.active {
  background-color: var(--secondary-color);
  color: white;
  border-color: var(--secondary-color);
}

.btn-page:hover:not(:disabled):not(.active) {
  background-color: #f1f1f1;
}

.btn-page:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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

/* Responsive Design */
@media (max-width: 992px) {
  .products-container {
    grid-template-columns: 220px 1fr;
    gap: 1.5rem;
  }
  
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }
  
  .product-card-list {
    grid-template-columns: 200px 1fr;
  }
}

@media (max-width: 768px) {
  .products-container {
    grid-template-columns: 1fr;
  }
  
  .sidebar {
    position: static;
    margin-bottom: 1.5rem;
  }
  
  .products-toolbar {
    flex-wrap: wrap;
    gap: 1rem;
  }
  
  .products-count {
    width: 100%;
    text-align: center;
  }
  
  .products-sorting {
    margin-left: auto;
  }
  
  .product-card-list {
    grid-template-columns: 1fr;
  }
  
  .product-card-list .product-image {
    height: 200px;
  }
}

@media (max-width: 576px) {
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 1rem;
  }
}
</style>