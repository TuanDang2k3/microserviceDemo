<template>
  <div class="register-page">
    <div class="auth-container">
      <div class="auth-header">
        <h2>Đăng ký tài khoản</h2>
        <p>Tham gia cùng BilliardStore ngay hôm nay</p>
      </div>
      
      <div v-if="error" class="error-alert">
        <i class="fas fa-exclamation-circle"></i>
        {{ error }}
      </div>
      
      <form @submit.prevent="handleRegister" class="register-form">
        <div class="form-group">
          <label for="username">Tên đăng nhập</label>
          <input 
            type="text" 
            id="username" 
            v-model="formData.username" 
            required
          />
        </div>
        
        <div class="form-group">
          <label for="email">Email</label>
          <input 
            type="email" 
            id="email" 
            v-model="formData.email" 
            required
          />
        </div>
        
        <div class="form-group">
          <label for="password">Mật khẩu</label>
          <div class="password-input">
            <input 
              :type="showPassword ? 'text' : 'password'" 
              id="password" 
              v-model="formData.password" 
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
        
        <div class="form-row">
          <div class="form-group">
            <label for="firstName">Họ</label>
            <input 
              type="text" 
              id="firstName" 
              v-model="formData.firstName" 
              required
            />
          </div>
          
          <div class="form-group">
            <label for="lastName">Tên</label>
            <input 
              type="text" 
              id="lastName" 
              v-model="formData.lastName" 
              required
            />
          </div>
        </div>
        
        <div class="form-group">
          <label for="city">Thành phố</label>
          <input 
            type="text" 
            id="city" 
            v-model="formData.city"
          />
        </div>
        
        <div class="form-check">
          <input type="checkbox" id="terms" v-model="agreeTerms" required />
          <label for="terms">
            Tôi đồng ý với <a href="#">Điều khoản sử dụng</a> và <a href="#">Chính sách bảo mật</a>
          </label>
        </div>
        
        <button type="submit" class="btn-register" :disabled="isLoading || !agreeTerms">
          <span v-if="isLoading"><i class="fas fa-spinner fa-spin"></i> Đang xử lý...</span>
          <span v-else>Đăng ký</span>
        </button>
      </form>
      
      <div class="auth-footer">
        Đã có tài khoản? <router-link to="/login">Đăng nhập ngay</router-link>
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
        email: '',
        password: '',
        firstName: '',
        lastName: '',
        city: ''
      },
      showPassword: false,
      agreeTerms: false,
      isLoading: false,
      error: null
    }
  },
  methods: {
    async handleRegister() {
      this.isLoading = true;
      this.error = null;
      
      try {
        // Step 1: Create user in identity service
        const identityPayload = {
          username: this.formData.username,
          password: this.formData.password,
          email: this.formData.email
        };
        
        const identityResponse = await axios.post(
          'http://localhost:8888/api/v1/identity/users',
          identityPayload
        );
        
        const userId = identityResponse.data.result.id;
        
        // Step 2: Create user profile
        const profilePayload = {
          username: this.formData.username,
          password: this.formData.password,
          firstName: this.formData.firstName,
          lastName: this.formData.lastName,
          city: this.formData.city
        };
        
        await axios.post(
          'http://localhost:8888/api/v1/profile/users',
          profilePayload
        );
        
        // Step 3: Login user automatically
        const loginResponse = await axios.post(
          'http://localhost:8888/api/v1/identity/auth/token',
          {
            username: this.formData.username,
            password: this.formData.password
          }
        );
        
        const { token, expiryTime } = loginResponse.data.result;
        
        // Save auth data to localStorage
        localStorage.setItem('token', token);
        localStorage.setItem('userId', userId);
        localStorage.setItem('tokenExpiry', expiryTime);
        
        // Dispatch login event for App.vue
        window.dispatchEvent(new Event('login'));
        
        // Redirect to home page
        this.$router.push('/');
      } catch (error) {
        console.error('Registration error:', error);
        
        if (error.response && error.response.data && error.response.data.message) {
          this.error = error.response.data.message;
        } else {
          this.error = "Đã có lỗi xảy ra khi đăng ký. Vui lòng thử lại sau.";
        }
      } finally {
        this.isLoading = false;
      }
    }
  }
}
</script>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  padding: 2rem 0;
}

.auth-container {
  background: white;
  max-width: 600px;
  width: 100%;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  padding: 2rem;
}

.auth-header {
  text-align: center;
  margin-bottom: 2rem;
}

.auth-header h2 {
  font-size: 1.8rem;
  margin-bottom: 0.5rem;
  color: #333;
}

.auth-header p {
  color: #666;
}

.error-alert {
  background: #ffebee;
  color: #d32f2f;
  padding: 0.75rem;
  border-radius: 4px;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
}

.error-alert i {
  margin-right: 0.5rem;
}

.register-form {
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
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
  color: #666;
  cursor: pointer;
}

.form-check {
  display: flex;
  align-items: flex-start;
  margin-bottom: 1.5rem;
}

.form-check input {
  width: auto;
  margin-right: 0.5rem;
  margin-top: 0.3rem;
}

.form-check label {
  margin-bottom: 0;
  font-size: 0.9rem;
  line-height: 1.4;
}

.form-check a {
  color: #3498db;
}

.btn-register {
  width: 100%;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 0.8rem;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
}

.btn-register:disabled {
  background: #95a5a6;
  cursor: not-allowed;
}

.auth-footer {
  text-align: center;
  margin-top: 1rem;
  color: #666;
}

.auth-footer a {
  color: #3498db;
  font-weight: 500;
  text-decoration: none;
}

@media (max-width: 600px) {
  .form-row {
    grid-template-columns: 1fr;
    gap: 0;
  }
}
</style>