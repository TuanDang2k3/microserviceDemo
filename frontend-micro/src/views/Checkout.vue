<template>
  <div class="checkout-page">
    <div class="page-header">
      <h1>Thanh toán</h1>
      <div class="breadcrumbs">
        <router-link to="/">Trang chủ</router-link>
        <i class="fas fa-chevron-right"></i>
        <router-link to="/cart">Giỏ hàng</router-link>
        <i class="fas fa-chevron-right"></i>
        <span>Thanh toán</span>
      </div>
    </div>
    
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>Đang tải thông tin...</p>
    </div>
    
    <div v-else-if="error" class="error-container">
      <i class="fas fa-exclamation-circle"></i>
      <p>{{ error }}</p>
      <router-link to="/cart" class="btn btn-primary">Quay lại giỏ hàng</router-link>
    </div>
    
    <div v-else-if="!cart || cart.items.length === 0" class="empty-checkout">
      <i class="fas fa-shopping-cart"></i>
      <h2>Giỏ hàng của bạn đang trống</h2>
      <p>Vui lòng thêm sản phẩm vào giỏ hàng để tiến hành thanh toán</p>
      <router-link to="/products" class="btn btn-primary">Mua sắm ngay</router-link>
    </div>
    
    <div v-else class="checkout-content">
      <div class="checkout-form">
        <h3>Thông tin giao hàng</h3>
        <form @submit.prevent="placeOrder">
          <div class="form-row">
            <div class="form-group">
              <label for="customerName">Họ và tên</label>
              <input
                type="text"
                id="customerName"
                v-model="formData.customerName"
                required
                placeholder="Nhập họ tên người nhận"
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label for="phoneNumber">Số điện thoại</label>
              <input
                type="tel"
                id="phoneNumber"
                v-model="formData.phoneNumber"
                required
                placeholder="Nhập số điện thoại"
                pattern="[0-9]{10}"
              />
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label for="shippingAddress">Địa chỉ giao hàng</label>
              <textarea
                id="shippingAddress"
                v-model="formData.shippingAddress"
                required
                placeholder="Nhập địa chỉ giao hàng"
                rows="3"
              ></textarea>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label for="note">Ghi chú (tùy chọn)</label>
              <textarea
                id="note"
                v-model="formData.note"
                placeholder="Ghi chú về đơn hàng, ví dụ: thời gian giao hàng"
                rows="2"
              ></textarea>
            </div>
          </div>
          
          <button type="submit" class="btn btn-primary" :disabled="isProcessing">
            {{ isProcessing ? 'Đang xử lý...' : 'Đặt hàng' }}
          </button>
        </form>
      </div>
      
      <div class="order-summary">
        <h3>Đơn hàng của bạn</h3>
        
        <div class="order-items">
          <div v-for="item in cart.items" :key="item.id" class="order-item">
            <div class="item-info">
              <span class="item-quantity">{{ item.quantity }}x</span>
              <span class="item-name">{{ item.productName }}</span>
            </div>
            <span class="item-price">{{ formatCurrency(item.subtotal) }}</span>
          </div>
        </div>
        
        <div class="order-totals">
          <div class="total-row">
            <span>Tổng tiền hàng:</span>
            <span>{{ formatCurrency(cart.totalPrice) }}</span>
          </div>
          
          <div class="total-row">
            <span>Phí vận chuyển:</span>
            <span>{{ formatCurrency(0) }}</span>
          </div>
          
          <div class="total-row final">
            <span>Tổng thanh toán:</span>
            <span class="final-price">{{ formatCurrency(cart.totalPrice) }}</span>
          </div>
        </div>
        
        <div class="payment-methods">
          <p><i class="fas fa-info-circle"></i> Thanh toán qua VNPAY sẽ được thực hiện sau khi đặt hàng</p>
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
      cart: null,
      loading: true,
      error: null,
      isProcessing: false,
      formData: {
        customerName: '',
        phoneNumber: '',
        shippingAddress: '',
        note: ''
      }
    }
  },
  created() {
    this.fetchCart();
    this.fetchUserProfile();
  },
  methods: {
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
    async fetchUserProfile() {
      const userId = localStorage.getItem('userId');
      
      if (!userId) return;
      
      try {
        const response = await axios.get(`http://localhost:8888/api/v1/profile/users/${userId}`);
        const profile = response.data.result || response.data;
        
        // Pre-fill form with user profile data
        this.formData.customerName = `${profile.firstName || ''} ${profile.lastName || ''}`.trim();
        
        if (profile.city) {
          this.formData.shippingAddress = profile.city;
        }
      } catch (error) {
        console.error('Error fetching user profile:', error);
      }
    },
    formatCurrency(amount) {
      return new Intl.NumberFormat('vi-VN', { 
        style: 'currency', 
        currency: 'VND' 
      }).format(amount);
    },
    async placeOrder() {
      const userId = localStorage.getItem('userId');
      
      if (!userId) {
        this.$router.push('/login');
        return;
      }
      
      this.isProcessing = true;
      
      try {
        const orderData = {
          userId: userId,
          customerName: this.formData.customerName,
          phoneNumber: this.formData.phoneNumber,
          shippingAddress: this.formData.shippingAddress
        };
        
        const response = await axios.post(
          'http://localhost:8888/api/v1/orders',
          orderData
        );
        
        const order = response.data.result;

          localStorage.setItem('orderId', order.id);

          console.log(`Đơn hàng 5 sẽ tự động hủy sau 5 phút nếu không thanh toán`);

        setTimeout(() => {
          // Kiểm tra xem OrderID trong localStorage có trùng với orderId hiện tại không
          if (localStorage.getItem('orderId') === order.id) {
            // Xóa orderId khỏi localStorage
            localStorage.removeItem('orderId');
            console.log(`Đã xóa đơn hàng ${order.id} khỏi localStorage sau 5 phút`);
          }
        }, 5 * 60 * 1000);
        // If payment URL is available, redirect to payment page
        if (order.paymentUrl) {
          window.location.href = order.paymentUrl;
        } else {
          // Otherwise, show success message and go to order page
          alert('Đơn hàng đã được đặt thành công!');
          this.$router.push('/orders');
        }
      } catch (error) {
        console.error('Error placing order:', error);
        alert('Không thể đặt hàng. Vui lòng thử lại sau.');
      } finally {
        this.isProcessing = false;
      }
    },
    

  }
}
</script>

<style scoped>
.checkout-page {
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
.empty-checkout {
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

.empty-checkout i {
  font-size: 4rem;
  color: #ddd;
  margin-bottom: 1.5rem;
}

.checkout-content {
  display: grid;
  grid-template-columns: 3fr 2fr;
  gap: 2rem;
}

.checkout-form,
.order-summary {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.checkout-form h3,
.order-summary h3 {
  margin-top: 0;
  padding-bottom: 1rem;
  margin-bottom: 1.5rem;
  border-bottom: 1px solid #eee;
}

.form-row {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
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
  margin-top: 1.5rem;
}

.btn-primary {
  background: #3498db;
  color: white;
  border: none;
}

.btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.order-items {
  margin-bottom: 1.5rem;
}

.order-item {
  display: flex;
  justify-content: space-between;
  padding: 0.75rem 0;
  border-bottom: 1px solid #eee;
}

.item-info {
  display: flex;
  gap: 0.5rem;
}

.item-quantity {
  font-weight: 600;
  color: #3498db;
}

.order-totals {
  background: #f9f9f9;
  padding: 1rem;
  border-radius: 4px;
}

.total-row {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
}

.total-row.final {
  border-top: 1px solid #ddd;
  margin-top: 0.5rem;
  padding-top: 1rem;
  font-weight: 600;
  font-size: 1.1rem;
}

.final-price {
  color: #e74c3c;
}

.payment-methods {
  margin-top: 1.5rem;
  font-size: 0.9rem;
}

.payment-methods p {
  color: #666;
}

.payment-methods i {
  color: #3498db;
  margin-right: 0.5rem;
}

@media (max-width: 768px) {
  .checkout-content {
    grid-template-columns: 1fr;
  }
}
</style>