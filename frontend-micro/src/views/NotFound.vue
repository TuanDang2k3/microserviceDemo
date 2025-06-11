<template>
  <div class="not-found">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <h2>Không tìm thấy trang</h2>
      <p>Trang bạn đang tìm kiếm không tồn tại hoặc đã được di chuyển.</p>
      <div class="actions">
        <router-link to="/" class="btn btn-primary">
          <i class="fas fa-home"></i> Về trang chủ
        </router-link>
        <button @click="goBack" class="btn btn-outline">
          <i class="fas fa-arrow-left"></i> Quay lại
        </button>
      </div>
    </div>
    
    <div class="not-found-image">
      <img src="https://i.imgur.com/A040Lxr.png" alt="Page not found">
    </div>
  </div>
</template>

<script>
export default {
  methods: {
    goBack() {
      if (window.history.length > 2) {
        this.$router.go(-1);
      } else {
        this.$router.push('/');
      }
    }
  }
}
</script>

<style scoped>
.not-found {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  gap: 3rem;
}

.not-found-content {
  max-width: 500px;
}

.error-code {
  font-size: 6rem;
  font-weight: 700;
  color: #3498db;
  line-height: 1;
  margin-bottom: 1rem;
}

h2 {
  font-size: 2rem;
  margin-bottom: 1rem;
}

p {
  color: #666;
  margin-bottom: 2rem;
  font-size: 1.1rem;
}

.actions {
  display: flex;
  gap: 1rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  font-weight: 500;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  text-decoration: none;
  cursor: pointer;
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

.not-found-image {
  max-width: 400px;
}

.not-found-image img {
  width: 100%;
}

@media (max-width: 992px) {
  .not-found {
    flex-direction: column;
    text-align: center;
  }
  
  .error-code {
    font-size: 5rem;
  }
}

@media (max-width: 576px) {
  .actions {
    flex-direction: column;
  }
}
</style>