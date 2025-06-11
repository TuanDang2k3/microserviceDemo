<template>
  <div class="container py-5">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <div class="card">
          <div class="card-body text-center p-5">
            <div v-if="loading" class="mb-4">
              <div class="spinner-border text-warning" role="status">
                <span class="visually-hidden">Đang xử lý...</span>
              </div>
              <p class="mt-3">Đang xử lý thanh toán, vui lòng đợi...</p>
            </div>
            
            <div v-else-if="paymentStatus === 'success'">
              <div class="text-success mb-4">
                <i class="bi bi-check-circle-fill display-1"></i>
              </div>
              <h2 class="mb-3">Thanh toán thành công!</h2>
              <p class="mb-4 text-muted">Cảm ơn bạn đã mua hàng tại BidaStore. Đơn hàng của bạn đang được xử lý.</p>
              
              <div class="row justify-content-center">
                <div class="col-lg-8">
                  <div class="card bg-light mb-4">
                    <div class="card-body">
                      <div class="d-flex justify-content-between mb-2">
                        <span>Mã đơn hàng:</span>
                        <span class="fw-bold">{{ paymentInfo.orderCode }}</span>
                      </div>
                      <div class="d-flex justify-content-between mb-2">
                        <span>Số tiền:</span>
                        <span class="fw-bold">{{ formatPrice(paymentInfo.amount) }}</span>
                      </div>
                      <div class="d-flex justify-content-between">
                        <span>Thời gian:</span>
                        <span>{{ formatDate(paymentInfo.createdAt) }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="d-flex justify-content-center gap-3">
                <router-link :to="`/orders/${paymentInfo.orderId}`" class="btn btn-primary">
                  Xem chi tiết đơn hàng
                </router-link>
                <router-link to="/products" class="btn btn-outline-secondary">
                  Tiếp tục mua sắm
                </router-link>
              </div>
            </div>
            
            <div v-else>
              <div class="text-danger mb-4">
                <i class="bi bi-x-circle-fill display-1"></i>
              </div>
              <h2 class="mb-3">Thanh toán thất bại!</h2>
              <p class="mb-4 text-muted">Đã có lỗi xảy ra trong quá trình thanh toán. Vui lòng thử lại hoặc chọn phương thức thanh toán khác.</p>
              
              <div class="d-flex justify-content-center gap-3">
                <router-link :to="`/orders/${orderId}`" class="btn btn-warning">
                  Thử lại
                </router-link>
                <router-link to="/orders" class="btn btn-outline-secondary">
                  Xem đơn hàng
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

export default {
  name: 'PaymentCallback',
  setup() {
    const route = useRoute()
    const router = useRouter()
    
    const loading = ref(true)
    const paymentStatus = ref('pending')
    const paymentInfo = ref({})
    const orderId = ref('')
    
    function processCallbackParams() {
      const params = route.query
      
      // VNPay specific parameters
      const vnp_ResponseCode = params.vnp_ResponseCode
      const vnp_TxnRef = params.vnp_TxnRef // Typically contains the order ID
      
      orderId.value = vnp_TxnRef
      
      // Check for successful payment (VNPay)
      if (vnp_ResponseCode === '00') {
        paymentStatus.value = 'success'
        paymentInfo.value = {
          orderId: vnp_TxnRef,
          orderCode: params.vnp_OrderInfo?.replace('Payment for order ', '') || 'N/A',
          amount: parseInt(params.vnp_Amount) / 100, // VNPay amount is in cents
          createdAt: new Date(),
          transactionId: params.vnp_TransactionNo
        }
      } else {
        paymentStatus.value = 'failed'
      }
      
      loading.value = false
    }
    
    function formatPrice(price) {
      return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
      }).format(price)
    }
    
    function formatDate(date) {
      if (!date) return ''
      const options = { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric',
        hour: 'numeric',
        minute: 'numeric'
      }
      return new Date(date).toLocaleDateString('vi-VN', options)
    }
    
    onMounted(() => {
      // Short timeout to show the loading spinner briefly
      setTimeout(() => {
        processCallbackParams()
      }, 1000)
    })
    
    return {
      loading,
      paymentStatus,
      paymentInfo,
      orderId,
      formatPrice,
      formatDate
    }
  }
}
</script>