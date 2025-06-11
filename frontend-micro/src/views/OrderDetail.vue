<template>
  <div class="order-detail-page">
    <div class="page-header">
      <h1>Chi tiết đơn hàng</h1>
      <div class="breadcrumbs">
        <router-link to="/">Trang chủ</router-link>
        <i class="fas fa-chevron-right"></i>
        <router-link to="/orders">Đơn hàng</router-link>
        <i class="fas fa-chevron-right"></i>
        <span>Chi tiết</span>
      </div>
    </div>
    
    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <p>Đang tải thông tin đơn hàng...</p>
    </div>
    
    <div v-else-if="error" class="error-container">
      <i class="fas fa-exclamation-circle"></i>
      <p>{{ error }}</p>
      <router-link to="/orders" class="btn btn-primary">Quay lại đơn hàng</router-link>
    </div>
    
    <div v-else-if="order" class="order-container">
      <div class="order-header">
        <h2>Đơn hàng #{{ order.orderCode }}</h2>
        <div :class="['order-status', getStatusClass(order.status)]">
          {{ translateStatus(order.status) }}
        </div>
      </div>
      
      <div class="order-info-grid">
        <div class="info-card">
          <h3>Thông tin giao hàng</h3>
          <p><strong>Tên người nhận:</strong> {{ order.customerName }}</p>
          <p><strong>Số điện thoại:</strong> {{ order.phoneNumber }}</p>
          <p><strong>Địa chỉ:</strong> {{ order.shippingAddress }}</p>
        </div>
        
        <div class="info-card">
          <h3>Thông tin đơn hàng</h3>
          <p><strong>Mã đơn hàng:</strong> {{ order.orderCode }}</p>
          <p><strong>Ngày đặt hàng:</strong> {{ formatDate(order.createdAt) }}</p>
          <p><strong>Trạng thái:</strong> {{ translateStatus(order.status) }}</p>
        </div>
      </div>
      
      <div class="order-items">
        <h3>Sản phẩm đã đặt</h3>
        <div class="items-table">
          <div class="items-header">
            <div class="col-product">Sản phẩm</div>
            <div class="col-price">Đơn giá</div>
            <div class="col-quantity">Số lượng</div>
            <div class="col-total">Thành tiền</div>
          </div>
          
          <div v-for="item in order.items" :key="item.id" class="item-row">
            <div class="col-product">
              <div class="product-info">
                <div class="product-image">
                  <img :src="item.productImage || 'https://via.placeholder.com/50'" :alt="item.productName">
                </div>
                <span>{{ item.productName }}</span>
              </div>
            </div>
            <div class="col-price">{{ formatCurrency(item.price) }}</div>
            <div class="col-quantity">{{ item.quantity }}</div>
            <div class="col-total">{{ formatCurrency(item.subtotal) }}</div>
          </div>
        </div>
        
        <div class="order-summary">
          <div class="summary-row">
            <span>Tổng tiền hàng:</span>
            <span>{{ formatCurrency(order.totalAmount) }}</span>
          </div>
          
          <div class="summary-row">
            <span>Phí vận chuyển:</span>
            <span>{{ formatCurrency(0) }}</span>
          </div>
          
          <div class="summary-row total">
            <span>Tổng thanh toán:</span>
            <span>{{ formatCurrency(order.totalAmount) }}</span>
          </div>
        </div>
      </div>
      
      <div class="order-actions">
        <router-link to="/orders" class="btn btn-outline">
          <i class="fas fa-arrow-left"></i> Quay lại đơn hàng
        </router-link>
        
        <a v-if="order.status === 'AWAITING_PAYMENT' && order.paymentUrl" 
           :href="order.paymentUrl" 
           class="btn btn-primary">
          <i class="fas fa-credit-card"></i> Thanh toán ngay
        </a>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      order: null,
      loading: true,
      error: null
    }
  },
  created() {
    this.fetchOrderDetails();
  },
  methods: {
    async fetchOrderDetails() {
      const userId = localStorage.getItem('userId');
      
      if (!userId) {
        this.$router.push('/login');
        return;
      }
      
      try {
        const response = await axios.get(`http://localhost:8888/api/v1/orders/${this.$route.params.id}`);
        this.order = response.data.result || response.data;
        
        // Check if this order belongs to the logged in user
        if (this.order.userId !== userId) {
          this.error = 'Bạn không có quyền xem đơn hàng này';
          this.order = null;
        }
      } catch (error) {
        console.error('Error fetching order details:', error);
        this.error = 'Không thể tải thông tin đơn hàng';
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
.order-detail-page {
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
.error-container {
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

.order-container {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  padding: 2rem;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.order-header h2 {
  margin: 0;
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

.order-info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin-bottom: 2rem;
}

.info-card {
  background: #f9f9f9;
  padding: 1.5rem;
  border-radius: 8px;
}

.info-card h3 {
  margin-top: 0;
  margin-bottom: 1rem;
  font-size: 1.1rem;
}

.info-card p {
  margin: 0.5rem 0;
}

.order-items {
  margin-bottom: 2rem;
}

.order-items h3 {
  margin-bottom: 1rem;
}

.items-table {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
}

.items-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  padding: 0.75rem;
  background: #f9f9f9;
  font-weight: bold;
}

.item-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  padding: 0.75rem;
  border-top: 1px solid #eee;
  align-items: center;
}

.product-info {
  display: flex;
  align-items: center;
}

.product-image {
  width: 50px;
  height: 50px;
  overflow: hidden;
  margin-right: 1rem;
  border-radius: 4px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.order-summary {
  margin-top: 1.5rem;
  background: #f9f9f9;
  padding: 1.5rem;
  border-radius: 8px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin: 0.5rem 0;
}

.summary-row.total {
  border-top: 1px solid #ddd;
  margin-top: 1rem;
  padding-top: 1rem;
  font-weight: bold;
}

.order-actions {
  display: flex;
  gap: 1rem;
}

.btn {
  flex: 1;
  padding: 0.75rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  text-decoration: none;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
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
  .order-info-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .items-header {
    grid-template-columns: 2fr 1fr 1fr;
  }
  
  .col-price {
    display: none;
  }
  
  .item-row {
    grid-template-columns: 2fr 1fr 1fr;
  }
  
  .order-container {
    padding: 1rem;
  }
  
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
    margin-bottom: 1rem;
  }
  
  .order-actions {
    flex-direction: column;
  }
}
</style>