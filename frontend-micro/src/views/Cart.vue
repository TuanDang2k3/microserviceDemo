<template>
  <div class="cart-page">
    <div class="page-header">
      <h1>Giỏ hàng</h1>
      <div class="breadcrumbs">
        <router-link to="/">Trang chủ</router-link>
        <i class="fas fa-chevron-right"></i>
        <span>Giỏ hàng</span>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>Đang tải giỏ hàng...</p>
    </div>

    <div v-else-if="error" class="error-container">
      <i class="fas fa-exclamation-circle"></i>
      <p>{{ error }}</p>
    </div>

    <div v-else-if="!cart || cart.items.length === 0" class="empty-cart">
      <i class="fas fa-shopping-cart"></i>
      <h2>Giỏ hàng của bạn đang trống</h2>
      <p>Hãy thêm sản phẩm vào giỏ hàng để tiếp tục mua sắm</p>
      <router-link to="/products" class="btn btn-primary">Tiếp tục mua sắm</router-link>
    </div>

    <div v-else class="cart-content">
      <div class="cart-items">
        <div class="cart-header">
          <div class="product-col">Sản phẩm</div>
          <div class="price-col">Đơn giá</div>
          <div class="quantity-col">Số lượng</div>
          <div class="subtotal-col">Thành tiền</div>
          <div class="remove-col"></div>
        </div>
        
        <div v-for="item in cart.items" :key="item.id" class="cart-item">
          <div class="product-col">
            <div class="product-image">
              <img :src="item.productImage || 'https://via.placeholder.com/80'" :alt="item.productName">
            </div>
            <div class="product-name">{{ item.productName }}</div>
          </div>
          
          <div class="price-col">{{ formatCurrency(item.price) }}</div>
          
          <div class="quantity-col">
            <div class="quantity-control">
              <button @click="updateQuantity(item, -1)" :disabled="item.quantity <= 1">-</button>
              <span>{{ item.quantity }}</span>
              <button @click="updateQuantity(item, 1)">+</button>
            </div>
          </div>
          
          <div class="subtotal-col">{{ formatCurrency(item.subtotal) }}</div>
          
          <div class="remove-col">
            <button class="remove-btn" @click="removeItem(item)">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
      </div>
      
      <div class="cart-summary">
        <h3>Tóm tắt đơn hàng</h3>
        
        <div class="summary-row">
          <span>Tổng tiền:</span>
          <span class="total-price">{{ formatCurrency(cart.totalPrice) }}</span>
        </div>
        
        <div class="summary-row">
          <span>Phí vận chuyển:</span>
          <span>{{ formatCurrency(0) }}</span>
        </div>
        
        <div class="summary-row total">
          <span>Thành tiền:</span>
          <span>{{ formatCurrency(cart.totalPrice) }}</span>
        </div>
        
        <button class="btn btn-primary checkout-btn" @click="checkout">
          Đặt hàng ngay
        </button>
        
        <router-link to="/products" class="btn btn-outline">
          Tiếp tục mua sắm
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      cart: null,
      loading: true,
      error: null,
      orderId : localStorage.getItem('orderId')
    }
  },
  created() {
    this.fetchCart();
  },
  methods: {
    
     async checkOrderStatusAndClearCart(orderId) {
      try {
        // Kiểm tra trạng thái đơn hàng
        const response = await axios.get(`http://localhost:8888/api/v1/orders/${orderId}`);
        const order = response.data.result || response.data;
        
        console.log(`Checking order ${orderId} status: ${order.status}`);
        
        // Nếu đơn hàng đã thanh toán, xóa giỏ hàng
        if (order.status === 'PAID') {
          await this.clearCartAfterPayment();
          console.log('Order is paid. Cart cleared successfully.');
          return true;
        } else {
          console.log('Order is not paid yet. Cart remains intact.');
          return false;
        }
      } catch (error) {
        console.error('Error checking order status:', error);
        return false;
      }
    },

    async fetchCart() {
      const userId = localStorage.getItem('userId');
      
      if (!userId) {
        this.$router.push('/login');
        return;
      }
      
      try {
        const response = await axios.get(`http://localhost:8888/api/v1/carts/${userId}`);
        this.cart = response.data.result || response.data;
      } catch (error) {
        console.error('Error fetching cart:', error);
        this.error = 'Không thể tải thông tin giỏ hàng';
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
    async updateQuantity(item, change) {
      const userId = localStorage.getItem('userId');
      const newQuantity = item.quantity + change;
      
      if (newQuantity < 1) return;
      
      try {
        await axios.put(
          `http://localhost:8888/api/v1/carts/${userId}/items/${item.id}`,
          { quantity: newQuantity }
        );
        this.fetchCart();
      } catch (error) {
        console.error('Error updating quantity:', error);
        alert('Không thể cập nhật số lượng sản phẩm');
      }
    },
    async removeItem(item) {
      const userId = localStorage.getItem('userId');
      
      try {
        await axios.delete(
          `http://localhost:8888/api/v1/carts/${userId}/items/${item.id}`
        );
        this.fetchCart();
      } catch (error) {
        console.error('Error removing item:', error);
        alert('Không thể xóa sản phẩm khỏi giỏ hàng');
      }
    },
    checkout() {
      this.$router.push('/checkout');
    }
  }
}
</script>

<style scoped>
.cart-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 2rem;
  text-align: center;
}

.breadcrumbs {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  margin-top: 0.5rem;
  color: #666;
}

.breadcrumbs a {
  color: #3498db;
  text-decoration: none;
}

.loading-container,
.error-container,
.empty-cart {
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

.empty-cart i {
  font-size: 4rem;
  color: #ddd;
  margin-bottom: 1.5rem;
}

.cart-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 2rem;
}

.cart-items {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  overflow: hidden;
}

.cart-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 0.5fr;
  padding: 1rem;
  background: #f9f9f9;
  font-weight: bold;
  border-bottom: 1px solid #eee;
}

.cart-item {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 0.5fr;
  padding: 1.5rem 1rem;
  align-items: center;
  border-bottom: 1px solid #eee;
}

.product-col {
  display: flex;
  align-items: center;
}

.product-image {
  width: 80px;
  height: 80px;
  overflow: hidden;
  margin-right: 1rem;
  border-radius: 4px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.quantity-control {
  display: flex;
  align-items: center;
  background: #f1f1f1;
  border-radius: 4px;
  overflow: hidden;
  width: fit-content;
}

.quantity-control button {
  width: 30px;
  height: 30px;
  border: none;
  background: #ddd;
  cursor: pointer;
}

.quantity-control span {
  padding: 0 1rem;
  min-width: 40px;
  text-align: center;
}

.remove-btn {
  background: none;
  border: none;
  color: #e74c3c;
  font-size: 1rem;
  cursor: pointer;
}

.cart-summary {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  height: fit-content;
}

.cart-summary h3 {
  margin-top: 0;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin: 1rem 0;
}

.summary-row.total {
  border-top: 1px solid #eee;
  padding-top: 1rem;
  font-weight: bold;
  font-size: 1.2rem;
}

.total-price {
  color: #e74c3c;
  font-weight: bold;
}

.btn {
  display: block;
  width: 100%;
  padding: 0.8rem;
  border-radius: 4px;
  font-weight: 600;
  text-align: center;
  cursor: pointer;
  text-decoration: none;
  margin-bottom: 1rem;
}

.btn-primary {
  background: #3498db;
  color: white;
  border: none;
}

.btn-outline {
  background: transparent;
  border: 1px solid #ddd;
  color: #333;
}

@media (max-width: 768px) {
  .cart-content {
    grid-template-columns: 1fr;
  }
  
  .cart-header {
    grid-template-columns: 2fr 1fr 1fr;
  }
  
  .cart-header .price-col, 
  .cart-header .remove-col {
    display: none;
  }
  
  .cart-item {
    grid-template-columns: 2fr 1fr 1fr;
    gap: 0.5rem;
    position: relative;
  }
  
  .price-col, .remove-col {
    display: none;
  }
  
  .remove-btn {
    position: absolute;
    top: 1rem;
    right: 1rem;
  }
}
</style>