<template>
  <header>
    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container">
        <router-link to="/" class="navbar-brand d-flex align-items-center">
          <img src="../assets/images/logo.png" alt="BidaStore" height="40" class="me-2">
          <span>BidaStore</span>
        </router-link>
        
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <router-link to="/" class="nav-link">Trang chủ</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/products" class="nav-link">Sản phẩm</router-link>
            </li>
            <li class="nav-item">
              <a href="#" class="nav-link">Giới thiệu</a>
            </li>
            <li class="nav-item">
              <a href="#" class="nav-link">Liên hệ</a>
            </li>
          </ul>
          
          <!-- Right aligned nav items -->
          <div class="d-flex align-items-center">
            <!-- Search -->
            <div class="d-flex me-2">
              <input 
                v-model="searchQuery" 
                @keyup.enter="search"
                class="form-control form-control-sm" 
                type="search" 
                placeholder="Tìm kiếm..."
              >
              <button @click="search" class="btn btn-sm btn-outline-light ms-1">
                <i class="bi bi-search"></i>
              </button>
            </div>
            
            <!-- Cart -->
            <router-link to="/cart" class="btn btn-outline-light position-relative me-2">
              <i class="bi bi-cart"></i>
              <span 
                v-if="cartCount > 0" 
                class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger"
              >
                {{ cartCount }}
              </span>
            </router-link>
            
            <!-- User Menu -->
            <div class="dropdown" v-if="isAuthenticated">
              <button class="btn btn-outline-light dropdown-toggle" data-bs-toggle="dropdown">
                <i class="bi bi-person-circle me-1"></i>
                {{ user?.username || 'Tài khoản' }}
              </button>
              <ul class="dropdown-menu dropdown-menu-end">
                <li>
                  <router-link to="/profile" class="dropdown-item">
                    <i class="bi bi-person me-2"></i>Hồ sơ
                  </router-link>
                </li>
                <li>
                  <router-link to="/orders" class="dropdown-item">
                    <i class="bi bi-box-seam me-2"></i>Đơn hàng
                  </router-link>
                </li>
                <li><hr class="dropdown-divider"></li>
                <li>
                  <a @click.prevent="logout" href="#" class="dropdown-item">
                    <i class="bi bi-box-arrow-right me-2"></i>Đăng xuất
                  </a>
                </li>
              </ul>
            </div>
            
            <!-- Login/Register -->
            <div v-else class="d-flex">
              <router-link to="/login" class="btn btn-outline-light me-2">Đăng nhập</router-link>
              <router-link to="/register" class="btn btn-warning">Đăng ký</router-link>
            </div>
          </div>
        </div>
      </div>
    </nav>
  </header>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'

// Import với try-catch để tránh crash
let useCartStore;
try {
  const cartModule = require('../store/cart');
  useCartStore = cartModule.useCartStore;
} catch (error) {
  console.error('Failed to import cart store in Header:', error);
  // Mock store để tránh lỗi
  useCartStore = () => ({
    cart: { items: [] },
    itemCount: 0
  });
}

export default {
  name: 'Header',
  setup() {
    const router = useRouter()
    const authStore = useAuthStore()
    
    // Kiểm tra xem useCartStore có phải là function không
    const cartStore = typeof useCartStore === 'function' ? useCartStore() : { cart: { items: [] }, itemCount: 0 }
    
    const searchQuery = ref('')
    
    const isAuthenticated = computed(() => authStore.isAuthenticated)
    const user = computed(() => authStore.user)
    
    // Thêm kiểm tra để đảm bảo cartStore.itemCount tồn tại
    const cartCount = computed(() => {
      // Kiểm tra nhiều cấp để tránh lỗi
      if (!cartStore) return 0
      if (typeof cartStore.itemCount === 'number') return cartStore.itemCount
      if (typeof cartStore.itemCount === 'function') return cartStore.itemCount()
      
      // Nếu không có itemCount, thử lấy từ cart.items
      if (cartStore.cart?.items?.length) {
        return cartStore.cart.items.reduce((total, item) => total + item.quantity, 0)
      }
      
      return 0
    })
    
    function search() {
      if (searchQuery.value.trim()) {
        router.push({
          path: '/products',
          query: { q: searchQuery.value }
        })
        searchQuery.value = ''
      }
    }
    
    function logout() {
      authStore.logout()
      router.push('/')
    }
    
    return {
      searchQuery,
      isAuthenticated,
      user,
      cartCount,
      search,
      logout
    }
  }
}
</script>