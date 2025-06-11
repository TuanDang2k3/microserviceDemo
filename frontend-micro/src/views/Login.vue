<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-header">
        <h2>Đăng nhập</h2>
        <p>Chào mừng bạn trở lại</p>
      </div>
      
      <div v-if="error" class="auth-alert error">
        <i class="fas fa-exclamation-circle"></i>
        <span>{{ error }}</span>
      </div>
      
      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label for="username">
            <i class="fas fa-user"></i>
            Tên đăng nhập
          </label>
          <input 
            type="text" 
            id="username" 
            v-model="formData.username" 
            placeholder="Nhập tên đăng nhập"
            required
          />
        </div>
        
        <div class="form-group">
          <label for="password">
            <i class="fas fa-lock"></i>
            Mật khẩu
          </label>
          <div class="password-input">
            <input 
              :type="showPassword ? 'text' : 'password'" 
              id="password" 
              v-model="formData.password" 
              placeholder="Nhập mật khẩu"
              required
            />
            <button 
              type="button" 
              class="toggle-password"
              @click="showPassword = !showPassword"
            >
              <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
            </button>
          </div>
        </div>
        
        <div class="form-options">
          <label class="remember-me">
            <input type="checkbox" v-model="rememberMe">
            <span>Ghi nhớ đăng nhập</span>
          </label>
          <a href="#" class="forgot-password">Quên mật khẩu?</a>
        </div>
        
        <button type="submit" class="btn btn-primary btn-block" :disabled="isLoading">
          <span v-if="isLoading">
            <i class="fas fa-spinner fa-spin"></i> Đang xử lý...
          </span>
          <span v-else>Đăng nhập</span>
        </button>
      </form>
      
      <div class="auth-separator">
        <span>Hoặc</span>
      </div>
      
      <div class="social-auth">
        <button class="btn btn-social btn-google">
          <i class="fab fa-google"></i> Google
        </button>
        <button class="btn btn-social btn-facebook">
          <i class="fab fa-facebook-f"></i> Facebook
        </button>
      </div>
      
      <div class="auth-footer">
        Chưa có tài khoản? <router-link to="/register">Đăng ký ngay</router-link>
      </div>
    </div>
    
    <div class="auth-image">
      <img src="https://i.imgur.com/fQ0bqP5.jpg" alt="Billiard Shop">
      <div class="overlay">
        <div class="overlay-content">
          <h2>Mua sắm đồ bida chính hãng</h2>
          <p>Tự tin vào kỹ năng với thiết bị cao cấp từ các thương hiệu hàng đầu thế giới</p>
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
      formData: {
        username: '',
        password: ''
      },
      showPassword: false,
      rememberMe: false,
      isLoading: false,
      error: null
    }
  },
  methods: {
    async handleLogin() {
      this.isLoading = true;
      this.error = null;
      
      try {
        const response = await axios.post(
          'http://localhost:8888/api/v1/identity/auth/token',
          this.formData
        );
        
        const { token, userId, expiryTime } = response.data.result;
        
        // Save auth data to localStorage
        localStorage.setItem('token', token);
        localStorage.setItem('userId', userId);
        localStorage.setItem('tokenExpiry', expiryTime);
        
        if (this.rememberMe) {
          localStorage.setItem('rememberUser', this.formData.username);
        } else {
          localStorage.removeItem('rememberUser');
        }
        
        // Dispatch login event
        window.dispatchEvent(new Event('login'));
        
        // Redirect to home page
        this.$router.push('/');
      } catch (error) {
        console.error('Login error:', error);
        
        if (error.response && error.response.data && error.response.data.message) {
          this.error = error.response.data.message;
        } else if (error.response && error.response.status === 401) {
          this.error = "Tên đăng nhập hoặc mật khẩu không chính xác";
        } else {
          this.error = "Đã có lỗi xảy ra khi đăng nhập. Vui lòng thử lại sau.";
        }
      } finally {
        this.isLoading = false;
      }
    }
  },
  created() {
    // Check for remembered user
    const rememberedUser = localStorage.getItem('rememberUser');
    if (rememberedUser) {
      this.formData.username = rememberedUser;
      this.rememberMe = true;
    }
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  min-height: 600px;
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
  background: white;
  margin: 0 auto;
  max-width: 1000px;
}

.auth-card {
  flex: 1;
  padding: 3rem;
  display: flex;
  flex-direction: column;
}

.auth-image {
  flex: 1;
  position: relative;
  display: none;
}

@media (min-width: 768px) {
  .auth-image {
    display: block;
  }
}

.auth-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.auth-image .overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(rgba(44, 62, 80, 0.7), rgba(52, 152, 219, 0.7));
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  color: white;
  text-align: center;
}

.overlay-content h2 {
  font-size: 1.8rem;
  margin-bottom: 1rem;
}

.auth-header {
  text-align: center;
  margin-bottom: 2rem;
}

.auth-header h2 {
  font-size: 1.8rem;
  margin-bottom: 0.5rem;
  color: var(--primary-color);
}

.auth-header p {
  color: var(--gray);
}

.auth-alert {
  padding: 1rem;
  border-radius: var(--radius);
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  font-size: 0.9rem;
}

.auth-alert i {
  margin-right: 0.5rem;
  font-size: 1.1rem;
}

.auth-alert.error {
  background-color: rgba(231, 76, 60, 0.1);
  color: #c0392b;
}

.auth-form {
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  font-size: 0.9rem;
}

.form-group label i {
  margin-right: 0.5rem;
  color: var(--gray);
}

.form-group input {
  width: 100%;
  padding: 0.8rem 1rem;
  border: 1px solid #ddd;
  border-radius: var(--radius);
  font-size: 1rem;
  transition: all 0.3s;
}

.form-group input:focus {
  border-color: var(--secondary-color);
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
  outline: none;
}

.password-input {
  position: relative;
}

.toggle-password {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: var(--gray);
  cursor: pointer;
  transition: color 0.3s;
}

.toggle-password:hover {
  color: var(--secondary-color);
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  font-size: 0.9rem;
}

.remember-me {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.remember-me input {
  margin-right: 0.5rem;
}

.forgot-password {
  color: var(--secondary-color);
  text-decoration: none;
  transition: color 0.3s;
}

.forgot-password:hover {
  text-decoration: underline;
}

.btn {
  display: inline-block;
  padding: 0.8rem 1rem;
  border-radius: var(--radius);
  font-weight: 600;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
  font-size: 1rem;
}

.btn-block {
  width: 100%;
}

.btn-primary {
  background-color: var(--secondary-color);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #2980b9;
}

.btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.auth-separator {
  position: relative;
  text-align: center;
  margin: 1.5rem 0;
}

.auth-separator::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background-color: #eee;
}

.auth-separator span {
  position: relative;
  background-color: white;
  padding: 0 1rem;
  color: var(--gray);
  font-size: 0.9rem;
}

.social-auth {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.btn-social {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  color: var(--text-color);
}

.btn-social i {
  margin-right: 0.5rem;
}

.btn-google:hover {
  background-color: #dd4b39;
  color: white;
}

.btn-facebook:hover {
  background-color: #3b5998;
  color: white;
}

.auth-footer {
  text-align: center;
  font-size: 0.9rem;
  color: var(--gray);
}

.auth-footer a {
  color: var(--secondary-color);
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.auth-footer a:hover {
  text-decoration: underline;
}

@media (max-width: 576px) {
  .auth-card {
    padding: 2rem 1.5rem;
  }
}
</style>