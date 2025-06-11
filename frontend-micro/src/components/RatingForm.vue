<template>
  <div class="rating-form">
    <h3>Đánh giá sản phẩm</h3>
    
    <div class="rating-stars">
      <label>Điểm đánh giá:</label>
      <StarRating v-model="rating" />
    </div>
    
    <div class="form-group">
      <label for="comment">Nhận xét:</label>
      <textarea 
        id="comment"
        v-model="comment"
        rows="3"
        placeholder="Nhập nhận xét của bạn về sản phẩm..."
      ></textarea>
    </div>
    
    <button 
      class="btn btn-primary"
      @click="submitRating"
      :disabled="isSubmitting || !rating">
      <i v-if="isSubmitting" class="fas fa-spinner fa-spin"></i>
      {{ isSubmitting ? 'Đang gửi...' : 'Gửi đánh giá' }}
    </button>
  </div>
</template>

<script>
import axios from 'axios';
import StarRating from './StarRating.vue';

export default {
  name: 'RatingForm',
  components: {
    StarRating
  },
  props: {
    productId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      rating: 0,
      comment: '',
      isSubmitting: false
    }
  },
  methods: {
    async submitRating() {
      const userId = localStorage.getItem('userId');
      
      if (!userId) {
        alert('Vui lòng đăng nhập để đánh giá sản phẩm');
        return;
      }
      
      if (!this.rating) {
        alert('Vui lòng chọn số sao đánh giá');
        return;
      }
      
      this.isSubmitting = true;
      
      try {
        const response = await axios.post('http://localhost:8087/ratings', {
          userId: userId,
          productId: this.productId,
          rating: this.rating,
          comment: this.comment
        });
        
        console.log('Rating submitted:', response.data);
        
        // Reset form after successful submission
        this.rating = 0;
        this.comment = '';
        
        // Emit event to parent component to refresh ratings
        this.$emit('rating-submitted');
        
        alert('Cảm ơn bạn đã đánh giá sản phẩm!');
      } catch (error) {
        console.error('Error submitting rating:', error);
        alert('Không thể gửi đánh giá. Vui lòng thử lại sau.');
      } finally {
        this.isSubmitting = false;
      }
    }
  }
}
</script>

<style scoped>
.rating-form {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 1.5rem;
  margin-top: 2rem;
  background-color: #f9f9f9;
}

.rating-form h3 {
  margin-top: 0;
  margin-bottom: 1rem;
}

.rating-stars {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
}

.rating-stars label {
  margin-right: 1rem;
  font-weight: bold;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
}

.form-group textarea {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
}

.btn-primary {
  background: #3498db;
  color: white;
  border: none;
  padding: 0.8rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
</style>