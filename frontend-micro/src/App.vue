<template>
  <div class="app">
    <header>
      <nav class="navbar">
        <div class="logo">
          <router-link to="/">
            <img src="D:\vuejs\frontend-micro\src\assets\images\logo.png" alt="BilliardStore" v-if="false">
            <h1>BilliardStore</h1>
          </router-link>
        </div>
        
        <div class="search-bar">
          <input type="text" placeholder="Tìm kiếm sản phẩm...">
          <button><i class="fas fa-search"></i></button>
        </div>
        
        <div class="nav-links">
          <router-link to="/" class="nav-link">
            <i class="fas fa-home"></i>
            <span>Trang chủ</span>
          </router-link>
          
          <router-link to="/products" class="nav-link">
            <i class="fas fa-th"></i>
            <span>Sản phẩm</span>
          </router-link>
          
          <router-link v-if="isLoggedIn" to="/cart" class="nav-link cart-link">
            <div class="icon-wrapper">
              <i class="fas fa-shopping-cart"></i>
              <span v-if="cartItemCount > 0" class="cart-count">{{ cartItemCount }}</span>
            </div>
            <span>Giỏ hàng</span>
          </router-link>
          
           <div class="nav-dropdown" v-if="isLoggedIn">
            <button class="nav-link dropdown-toggle" @click="toggleDropdown">
              <i class="fas fa-user"></i>
              <span>Tài khoản</span>
            </button>
            <div class="dropdown-menu" :class="{ active: isDropdownOpen }">
              <router-link to="/profile">Thông tin cá nhân</router-link>
              <router-link to="/orders">Đơn hàng của tôi</router-link>
              <a href="#" @click.prevent="logout">Đăng xuất</a>
            </div>
          </div>
          
          <router-link v-if="!isLoggedIn" to="/login" class="nav-link">
            <i class="fas fa-sign-in-alt"></i>
            <span>Đăng nhập</span>
          </router-link>
        </div>
        
        <div class="menu-toggle">
          <i class="fas fa-bars"></i>
        </div>
      </nav>
    </header>
    
    <main>
      <transition name="fade" mode="out-in">
        <router-view></router-view>
      </transition>
    </main>
    
    <footer>
      <div class="footer-content">
        <div class="footer-section">
          <h3>BilliardStore</h3>
          <p>Cửa hàng dụng cụ bida số 1 Việt Nam với hơn 10 năm kinh nghiệm</p>
          <div class="social-links">
            <a href="#" title="Facebook"><i class="fab fa-facebook-f"></i></a>
            <a href="#" title="Instagram"><i class="fab fa-instagram"></i></a>
            <a href="#" title="Youtube"><i class="fab fa-youtube"></i></a>
          </div>
        </div>
        
        <div class="footer-section">
          <h3>Liên hệ</h3>
          <p><i class="fas fa-map-marker-alt"></i> 123 Nguyễn Huệ, Quận 1, TP.HCM</p>
          <p><i class="fas fa-phone"></i> 0123.456.789</p>
          <p><i class="fas fa-envelope"></i> info@billiardstore.vn</p>
        </div>
        
        <div class="footer-section">
          <h3>Thông tin</h3>
          <ul>
            <li><a href="#">Về chúng tôi</a></li>
            <li><a href="#">Chính sách bảo hành</a></li>
            <li><a href="#">Phương thức vận chuyển</a></li>
            <li><a href="#">Chính sách bảo mật</a></li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h3>Đăng ký nhận tin</h3>
          <p>Nhận thông tin sản phẩm mới nhất và khuyến mãi đặc biệt</p>
          <div class="subscribe-form">
            <input type="email" placeholder="Email của bạn">
            <button>Đăng ký</button>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>&copy; 2025 BilliardStore. All rights reserved.</p>
        <div class="payment-methods">
          <i class="fab fa-cc-visa"></i>
          <i class="fab fa-cc-mastercard"></i>
          <i class="fab fa-paypal"></i>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isLoggedIn: false,
      cartItemCount: 0,
      isDropdownOpen: false
    }
  },
  created() {
    // Check if user is logged in
    const userId = localStorage.getItem('userId');
    this.isLoggedIn = !!userId;
    
    // Add event listener for login/logout events
    window.addEventListener('login', () => {
      this.isLoggedIn = true;
      this.fetchCartCount();
    });
    
    window.addEventListener('logout', () => {
      this.isLoggedIn = false;
      this.cartItemCount = 0;
    });
    
    // Fetch cart count if user is logged in
    if (this.isLoggedIn) {
      this.fetchCartCount();
    }
    
    // Listen for cart update events
    window.addEventListener('cart-updated', () => {
      this.fetchCartCount();
    });
  },
  mounted() {
    // Thêm sự kiện click để đóng dropdown khi click ra ngoài
    document.addEventListener('click', (e) => {
      const dropdown = document.querySelector('.nav-dropdown');
      if (dropdown && !dropdown.contains(e.target)) {
        this.isDropdownOpen = false;
      }
    });
  },
  methods: {
    async fetchCartCount() {
      const userId = localStorage.getItem('userId');
      if (!userId) return;
      
      try {
        const response = await fetch(`http://localhost:8888/api/v1/carts/${userId}`);
        const data = await response.json();
        const cart = data.result || data;
        
        if (cart && cart.items) {
          this.cartItemCount = cart.items.reduce((total, item) => total + item.quantity, 0);
        }
      } catch (error) {
        console.error('Error fetching cart count:', error);
      }
    },
    logout() {
      localStorage.removeItem('userId');
      localStorage.removeItem('token');
      localStorage.removeItem('orderId');
      localStorage.removeItem('profileId');
      window.dispatchEvent(new Event('logout'));
      this.$router.push('/login');
    }
  }
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap');
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css');

:root {
  --primary-color: #2c3e50;
  --secondary-color: #3498db;
  --accent-color: #e74c3c;
  --text-color: #333;
  --light-gray: #f5f5f5;
  --gray: #95a5a6;
  --border-color: #eaeaea;
  --shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  --radius: 8px;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Poppins', sans-serif;
  color: var(--text-color);
  background-color: #f9f9f9;
  line-height: 1.6;
}

.app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* Header Styles */
header {
  background-color: #fff;
  box-shadow: var(--shadow);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
}

.logo a {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: var(--primary-color);
}

.logo img {
  height: 40px;
  margin-right: 0.5rem;
}

.logo h1 {
  font-size: 1.5rem;
  font-weight: 700;
}

.search-bar {
  flex: 1;
  max-width: 500px;
  display: flex;
  margin: 0 2rem;
  position: relative;
}

.search-bar input {
  width: 100%;
  padding: 0.6rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius);
  outline: none;
  font-size: 0.9rem;
}

.search-bar button {
  position: absolute;
  right: 0;
  top: 0;
  height: 100%;
  padding: 0 1rem;
  background: transparent;
  border: none;
  color: var(--gray);
  cursor: pointer;
  transition: color 0.3s;
}

.search-bar button:hover {
  color: var(--secondary-color);
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.nav-link {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  color: var(--text-color);
  font-size: 0.8rem;
  padding: 0.5rem;
  border-radius: var(--radius);
  transition: all 0.3s;
}

.nav-link i {
  font-size: 1.2rem;
  margin-bottom: 0.2rem;
}

.nav-link:hover, .router-link-active.nav-link {
  color: var(--secondary-color);
  background-color: rgba(52, 152, 219, 0.1);
}

.cart-link {
  position: relative;
}

.icon-wrapper {
  position: relative;
}

.cart-count {
  position: absolute;
  top: -8px;
  right: -8px;
  background-color: var(--accent-color);
  color: white;
  font-size: 0.7rem;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.nav-dropdown {
  position: relative;
}

.dropdown-toggle {
  background: none;
  border: none;
  font-family: inherit;
  cursor: pointer;
}

.dropdown-menu {
  position: absolute;
  right: 0;
  top: calc(100% );  /* Điều chỉnh vị trí để tránh khoảng trống */
  background: white;
  box-shadow: var(--shadow);
  border-radius: var(--radius);
  min-width: 180px;
  padding: 0.5rem 0;
  display: none;
  z-index: 1000;
  border: 1px solid var(--border-color);
}

.dropdown-menu a {
  display: block;
  padding: 0.5rem 1rem;
  color: var(--text-color);
  text-decoration: none;
  transition: background 0.3s;
}

.dropdown-menu a:hover {
  background-color: var(--light-gray);
}

.nav-dropdown:hover .dropdown-menu {
  display: block;
}

.menu-toggle {
  display: none;
  font-size: 1.5rem;
  cursor: pointer;
}

/* Main Content */
main {
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  padding: 2rem;
}

/* Footer Styles */
footer {
  background-color: var(--primary-color);
  color: white;
  padding: 3rem 2rem 1rem;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.footer-section h3 {
  margin-bottom: 1.5rem;
  position: relative;
  padding-bottom: 0.5rem;
  font-size: 1.2rem;
}

.footer-section h3::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 50px;
  height: 2px;
  background-color: var(--secondary-color);
}

.footer-section p, .footer-section ul {
  margin-bottom: 1rem;
  opacity: 0.8;
}

.footer-section ul li {
  margin-bottom: 0.5rem;
}

.footer-section a {
  color: white;
  text-decoration: none;
  transition: opacity 0.3s;
}

.footer-section a:hover {
  opacity: 1;
  color: var(--secondary-color);
}

.social-links {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.social-links a {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.1);
  transition: all 0.3s;
}

.social-links a:hover {
  background-color: var(--secondary-color);
  transform: translateY(-3px);
}

.subscribe-form {
  display: flex;
  margin-top: 1rem;
}

.subscribe-form input {
  flex: 1;
  padding: 0.8rem 1rem;
  border: none;
  border-radius: var(--radius) 0 0 var(--radius);
  outline: none;
}

.subscribe-form button {
  padding: 0 1.5rem;
  background-color: var(--secondary-color);
  color: white;
  border: none;
  border-radius: 0 var(--radius) var(--radius) 0;
  cursor: pointer;
  transition: background-color 0.3s;
}

.subscribe-form button:hover {
  background-color: #2980b9;
}

.footer-bottom {
  max-width: 1400px;
  margin: 2rem auto 0;
  padding-top: 1.5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.payment-methods {
  display: flex;
  gap: 1rem;
  font-size: 1.5rem;
}

/* Transitions */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* Responsive Design */
@media (max-width: 992px) {
  .navbar {
    padding: 1rem;
  }
  
  .search-bar {
    margin: 0 1rem;
  }
}

@media (max-width: 768px) {
  .navbar {
    flex-wrap: wrap;
  }
  
  .logo {
    margin-right: auto;
  }
  
  .menu-toggle {
    display: block;
  }
  
  .search-bar {
    order: 3;
    margin: 1rem 0 0;
    max-width: 100%;
    width: 100%;
  }
  
  .nav-links {
    order: 2;
    display: none;
  }
  
  .nav-links.active {
    display: flex;
    flex-direction: column;
    width: 100%;
  }
  
  .footer-bottom {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
}
</style>