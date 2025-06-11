<template>
  <div class="orders-page">
    <div class="page-header">
      <h1>Đơn hàng của tôi</h1>
      <div class="breadcrumbs">
        <router-link to="/">Trang chủ</router-link>
        <i class="fas fa-chevron-right"></i>
        <span>Đơn hàng</span>
      </div>
    </div>
    
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>Đang tải đơn hàng...</p>
    </div>
    
    <div v-else-if="error" class="error-container">
      <i class="fas fa-exclamation-circle"></i>
      <p>{{ error }}</p>
    </div>
    
    <div v-else-if="orders.length === 0" class="empty-orders">
      <i class="fas fa-shopping-bag"></i>
      <h2>Bạn chưa có đơn hàng nào</h2>
      <p>Hãy tiếp tục khám phá các sản phẩm của chúng tôi</p>
      <router-link to="/products" class="btn btn-primary">Mua sắm ngay</router-link>
    </div>
    
    <div v-else class="orders-list">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <div class="order-id">
            <span>Mã đơn hàng:</span> {{ order.orderCode }}
          </div>
          <div class="order-date">
            <span>Ngày đặt:</span> {{ formatDate(order.createdAt) }}
          </div>
          <div :class="['order-status', getStatusClass(order.status)]">
            {{ translateStatus(order.status) }}
          </div>
        </div>
        
        <div class="order-items">
          <div v-for="item in order.items.slice(0, 3)" :key="item.id" class="order-item">
            <div class="item-image">
              <img :src="item.productImage || 'https://via.placeholder.com/50'" :alt="item.productName">
            </div>
            <div class="item-name">{{ item.productName }}</div>
            <div class="item-details">
              <span class="item-price">{{ formatCurrency(item.price) }}</span>
              <span class="item-quantity">x{{ item.quantity }}</span>
            </div>
          </div>
          
          <div v-if="order.items.length > 3" class="more-items">
            + {{ order.items.length - 3 }} sản phẩm khác
          </div>
        </div>
        
        <div class="order-footer">
          <div class="order-total">
            <span>Tổng tiền:</span>
            <span class="total-price">{{ formatCurrency(order.totalAmount) }}</span>
          </div>
          
          <div class="order-actions">
            <router-link :to="`/orders/${order.id}`" class="btn btn-outline">
              Xem chi tiết
            </router-link>
            
            <a v-if="order.status === 'AWAITING_PAYMENT' && order.paymentUrl" 
              :href="order.paymentUrl" 
              class="btn btn-primary">
              Thanh toán ngay
            </a>
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
      orders: [],
      loading: true,
      error: null
    }
  },
  created() {
    this.fetchOrders();
  },
  methods: {
    shouldShowPayButton(order) {
      console.log(`Order ${order.orderCode}: status=${order.status}, paymentUrl=${order.paymentUrl || 'undefined'}`);
      return order && 
            (order.status === 'AWAITING_PAYMENT' || order.status === 'PENDING') && 
            order.paymentUrl;
    },
    
    // Hàm kiểm tra trạng thái đơn hàng định kỳ (nếu cần)
    async checkOrderStatus(orderId) {
      try {
        const response = await axios.get(`http://localhost:8888/api/v1/orders/${orderId}`);
        const order = response.data.result || response.data;
        
        // Nếu đơn hàng đã thanh toán, xóa giỏ hàng
        if (order.status === 'PAID') {
          await this.clearCart();
        }
        
        return order.status;
      } catch (error) {
        console.error('Error checking order status:', error);
      }
    },
    
    // Xóa giỏ hàng
    async clearCart() {
      const userId = localStorage.getItem('userId');
      if (!userId) return;
      
      try {
        await axios.delete(`http://localhost:8888/api/v1/carts/${userId}`);
        console.log('Cart cleared after payment confirmation');
      } catch (error) {
        console.error('Error clearing cart:', error);
      }
    },
    async fetchOrders() {
      const userId = localStorage.getItem('userId');
      
      if (!userId) {
        this.$router.push('/login');
        return;
      }
      
      try {
        const response = await axios.get(`http://localhost:8888/api/v1/orders/user/${userId}`);
        this.orders = response.data.result || response.data || [];
        
        // Sort by date (newest first)
        this.orders.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
      } catch (error) {
        console.error('Error fetching orders:', error);
        this.error = 'Không thể tải danh sách đơn hàng';
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
    formatDate(dateString) {
      const date = new Date(dateString);
      return new Intl.DateTimeFormat('vi-VN', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }).format(date);
    },
    translateStatus(status) {
      const statusMap = {
        'AWAITING_PAYMENT': 'Chờ thanh toán',
        'PAID': 'Đã thanh toán',
        'PROCESSING': 'Đang xử lý',
        'SHIPPED': 'Đang giao hàng',
        'DELIVERED': 'Đã giao hàng',
        'CANCELLED': 'Đã hủy',
        'FAILED': 'Thất bại'
      };
      
      return statusMap[status] || status;
    },
    getStatusClass(status) {
      const classMap = {
        'AWAITING_PAYMENT': 'status-waiting',
        'PAID': 'status-success',
        'PROCESSING': 'status-processing',
        'SHIPPED': 'status-processing',
        'DELIVERED': 'status-success',
        'CANCELLED': 'status-danger',
        'FAILED': 'status-danger'
      };
      
      return classMap[status] || '';
    }
  }
}
</script>

<style scoped>
.orders-page {
  max-width: 1000px;
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
.empty-orders {
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

.empty-orders i {
  font-size: 4rem;
  color: #ddd;
  margin-bottom: 1.5rem;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.order-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #f9f9f9;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.order-id span,
.order-date span {
  color: #666;
  font-size: 0.9rem;
}

.order-status {
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  font-size: 0.9rem;
}

.status-waiting {
  background: #fffde7;
  color: #ffa000;
}

.status-processing {
  background: #e3f2fd;
  color: #1976d2;
}

.status-success {
  background: #e8f5e9;
  color: #388e3c;
}

.status-danger {
  background: #ffebee;
  color: #d32f2f;
}

.order-items {
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.order-item {
  display: flex;
  align-items: center;
  margin-bottom: 0.75rem;
}

.item-image {
  width: 50px;
  height: 50px;
  overflow: hidden;
  margin-right: 1rem;
  border-radius: 4px;
  background: #f9f9f9;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-name {
  flex: 1;
}

.item-details {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.item-price {
  font-weight: 600;
}

.item-quantity {
  color: #666;
  font-size: 0.9rem;
}

.more-items {
  text-align: center;
  color: #666;
  font-style: italic;
  padding-top: 0.5rem;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.order-total span {
  margin-right: 0.5rem;
}

.total-price {
  font-weight: bold;
  color: #e74c3c;
}

.order-actions {
  display: flex;
  gap: 0.75rem;
}

.btn {
  padding: 0.6rem 1rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  text-decoration: none;
  text-align: center;
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
  .order-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .order-footer {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .order-actions {
    width: 100%;
  }
  
  .btn {
    flex: 1;
    padding: 0.6rem 0;
  }
}
</style>