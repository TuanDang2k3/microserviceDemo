<template>
  <div class="profile-page">
    <div class="container">
      <div class="page-header">
        <h1>Thông tin tài khoản</h1>
        <div class="breadcrumbs">
          <router-link to="/">Trang chủ</router-link>
          <i class="fas fa-chevron-right"></i>
          <span>Thông tin tài khoản</span>
        </div>
      </div>

      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>Đang tải thông tin...</p>
      </div>

      <div v-else-if="error" class="error-message">
        <i class="fas fa-exclamation-circle"></i>
        <p>{{ error }}</p>
        <button @click="fetchUserProfile" class="btn btn-primary">Thử lại</button>
      </div>

      <div v-else class="profile-content">
        <div class="profile-sidebar">
          <div class="user-avatar">
            <div class="avatar-placeholder">
              {{ getInitials() }}
            </div>
            <h3>{{ `${profile.firstName} ${profile.lastName}` }}</h3>
          </div>

          <div class="sidebar-menu">
            <a href="#personal-info" class="active">
              <i class="fas fa-user"></i>
              Thông tin cá nhân
            </a>
            <router-link to="/orders">
              <i class="fas fa-shopping-bag"></i>
              Đơn hàng của tôi
            </router-link>
            <a href="#" @click.prevent="logout">
              <i class="fas fa-sign-out-alt"></i>
              Đăng xuất
            </a>
          </div>
        </div>

        <div class="profile-main">
          <div id="personal-info" class="profile-section">
            <div class="section-header">
              <h2>Thông tin cá nhân</h2>
              <button class="btn btn-edit" @click="editMode = !editMode">
                <i class="fas" :class="editMode ? 'fa-times' : 'fa-pen'"></i>
                {{ editMode ? 'Hủy' : 'Chỉnh sửa' }}
              </button>
            </div>

            <div v-if="editMode" class="edit-form">
              <form @submit.prevent="updateProfile">
                <div class="form-row">
                  <div class="form-group">
                    <label for="firstName">Họ</label>
                    <input type="text" id="firstName" v-model="formData.firstName" required>
                  </div>
                  <div class="form-group">
                    <label for="lastName">Tên</label>
                    <input type="text" id="lastName" v-model="formData.lastName" required>
                  </div>
                </div>

               <div class="form-group">
                <label for="dob">Ngày sinh</label>
                <input type="date" id="dob" v-model="formData.dob">
              </div>

                <div class="form-group">
                  <label for="city">Thành phố</label>
                  <input type="text" id="city" v-model="formData.city">
                </div>

                <div class="form-actions">
                  <button type="submit" class="btn btn-primary" :disabled="isUpdating">
                    <i v-if="isUpdating" class="fas fa-spinner fa-spin"></i>
                    {{ isUpdating ? 'Đang cập nhật...' : 'Lưu thay đổi' }}
                  </button>
                </div>
              </form>
            </div>

            <div v-else class="profile-info">
              <div class="info-item">
                <div class="info-label">Họ và tên</div>
                <div class="info-value">{{ `${profile.firstName} ${profile.lastName}` }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">Ngày sinh</div>
                <div class="info-value">{{ formatDate(profile.dob) || 'Chưa cung cấp' }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">Tên đăng nhập</div>
                <div class="info-value">{{ profile.username }}</div>
              </div>
              <div class="info-item">
                <div class="info-label">Thành phố</div>
                <div class="info-value">{{ profile.city || 'Chưa cung cấp' }}</div>
              </div>
            </div>
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
      profile: {},
      loading: true,
      error: null,
      editMode: false,
      isUpdating: false,
      isChangingPassword: false,
      formData: {
        firstName: '',
        lastName: '',
        dob: '',
        city: ''
      },
      passwordForm: {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    };
  },
  created() {
    this.fetchUserProfile();
  },
  methods: {
    // Cập nhật hàm fetchUserProfile để xử lý người dùng mới
    async fetchUserProfile() {

    

    
    this.loading = true;
    this.error = null;
    
    try {

      
      // Khởi tạo profile chỉ với thông tin cơ bản
     
      
      // Lấy profileId từ localStorage nếu có
      const profileId = localStorage.getItem('profileId');
      
      if (profileId) {
        try {
          // Thử lấy profile với profileId nếu đã tồn tại
          const profileResponse = await axios.get(`http://localhost:8085/profile/users/${profileId}`);
          const profileData = profileResponse.data.result || profileResponse.data;
          
          // Nếu có profile, bổ sung dữ liệu
          this.profile = {
            ...this.profile,
            ...profileData,
            id: profileId
          };
        } catch (profileError) {
          console.log('Error fetching profile:', profileError);
          // Profile không tồn tại hoặc lỗi, chuyển sang chế độ chỉnh sửa
          this.editMode = true;
        }
      } else {
        // Chưa có profileId -> người dùng mới, chuyển sang chế độ chỉnh sửa
        console.log('No profile ID found, switching to edit mode');
        this.editMode = true;
      }
      
      // Khởi tạo formData với các trường trống (KHÔNG điền username)
      this.formData = {
        firstName: this.profile.firstName || '',
        lastName: this.profile.lastName || '',
        dob: this.profile.dob || '',
        city: this.profile.city || ''
      };
    } catch (error) {
      console.error('Error fetching user identity:', error);
      this.error = 'Không thể tải thông tin người dùng. Vui lòng thử lại sau.';
    } finally {
      this.loading = false;
    }
  },
    getInitials() {
      if (!this.profile.firstName && !this.profile.lastName) {
        return 'U';
      }
      
      const firstInitial = this.profile.firstName ? this.profile.firstName.charAt(0) : '';
      const lastInitial = this.profile.lastName ? this.profile.lastName.charAt(0) : '';
      
      return `${firstInitial}${lastInitial}`.toUpperCase();
    },
    async updateProfile() {
      this.isUpdating = true;
      
      try {
        // Chuẩn bị dữ liệu profile
        const profileData = {
          firstName: this.formData.firstName,
          lastName: this.formData.lastName,
          dob: this.formData.dob,
          city: this.formData.city
          // Có thể thêm dob (ngày sinh) nếu cần
          // dob: this.formData.dob
        };
        
        let response;
        const profileId = localStorage.getItem('profileId');
        
        if (!profileId) {
          // Nếu không có profileId -> tạo mới profile
          console.log('Creating new profile');
          response = await axios.post(
            `http://localhost:8085/profile/users`,
            profileData
          );
          
          // Lấy và lưu profileId từ response
          const newProfileId = response.data.result?.id || response.data.id;
          if (newProfileId) {
            localStorage.setItem('profileId', newProfileId);
            console.log('Saved new profileId:', newProfileId);
          } else {
            console.error('Could not get profileId from response', response.data);
          }
        } else {
          // Nếu đã có profileId -> cập nhật profile hiện có
          console.log('Updating existing profile:', profileId);
          response = await axios.put(
            `http://localhost:8085/profile/users/${profileId}`,
            profileData
          );
        }
        
        // Làm mới dữ liệu profile
        await this.fetchUserProfile();
        
        // Thoát chế độ chỉnh sửa
        this.editMode = false;
        
        // Hiển thị thông báo thành công
        alert(!profileId ? 'Hồ sơ đã được tạo thành công!' : 'Thông tin đã được cập nhật thành công!');
      } catch (error) {
        console.error('Error updating profile:', error);
        alert('Không thể cập nhật thông tin. Vui lòng thử lại sau.');
      } finally {
        this.isUpdating = false;
      }
    },
    async changePassword() {
      // Validate password match
      if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
        alert('Mật khẩu mới không khớp với mật khẩu xác nhận!');
        return;
      }
      
      const userId = localStorage.getItem('userId');
      
      if (!userId) {
        this.$router.push('/login');
        return;
      }
      
      this.isChangingPassword = true;
      
      try {
        // Change password via identity service
        await axios.post(
          `http://localhost:8888/api/v1/identity/users/${userId}/change-password`,
          {
            currentPassword: this.passwordForm.currentPassword,
            newPassword: this.passwordForm.newPassword
          }
        );
        
        // Reset form
        this.passwordForm = {
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
        };
        
        // Show success message
        alert('Mật khẩu đã được thay đổi thành công!');
      } catch (error) {
        console.error('Error changing password:', error);
        
        if (error.response && error.response.status === 401) {
          alert('Mật khẩu hiện tại không chính xác!');
        } else {
          alert('Không thể thay đổi mật khẩu. Vui lòng thử lại sau.');
        }
      } finally {
        this.isChangingPassword = false;
      }
    },
    logout() {
      localStorage.removeItem('userId');
      localStorage.removeItem('token');
      window.dispatchEvent(new Event('logout'));
      this.$router.push('/login');
    },
    formatDate(dateString) {
  if (!dateString) return null;
  
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  } catch (error) {
    console.error('Error formatting date:', error);
    return dateString;
  }
},
  }
}
</script>

<style scoped>
.profile-page {
  padding: 2rem 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 2rem;
  text-align: center;
}

.page-header h1 {
  margin-bottom: 0.5rem;
}

.breadcrumbs {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  color: #666;
  font-size: 0.9rem;
}

.breadcrumbs a {
  color: #3498db;
  text-decoration: none;
}

.breadcrumbs i {
  font-size: 0.7rem;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem 0;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top: 4px solid #3498db;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  text-align: center;
  padding: 2rem;
  background: #ffebee;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.error-message i {
  font-size: 3rem;
  color: #e74c3c;
  margin-bottom: 1rem;
}

.profile-content {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 2rem;
}

.profile-sidebar {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  overflow: hidden;
}

.user-avatar {
  padding: 2rem;
  text-align: center;
  border-bottom: 1px solid #eee;
}

.avatar-placeholder {
  width: 100px;
  height: 100px;
  background: #3498db;
  color: white;
  border-radius: 50%;
  font-size: 2.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1rem;
}

.user-avatar h3 {
  margin: 0;
  font-size: 1.2rem;
}

.sidebar-menu a {
  display: flex;
  align-items: center;
  padding: 1rem 1.5rem;
  color: #333;
  text-decoration: none;
  border-left: 3px solid transparent;
}

.sidebar-menu a i {
  margin-right: 0.75rem;
  width: 20px;
  text-align: center;
}

.sidebar-menu a.active {
  border-left-color: #3498db;
  background: #f0f8ff;
  color: #3498db;
}

.sidebar-menu a:hover:not(.active) {
  background: #f9f9f9;
}

.profile-main {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.profile-section {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.section-header h2 {
  margin: 0;
  font-size: 1.5rem;
}

.btn {
  padding: 0.6rem 1.2rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-primary {
  background: #3498db;
  color: white;
}

.btn-edit {
  background: #f1f1f1;
  color: #333;
}

.btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.info-item {
  margin-bottom: 1.5rem;
}

.info-label {
  font-weight: 600;
  margin-bottom: 0.3rem;
  color: #666;
  font-size: 0.9rem;
}

.info-value {
  font-size: 1.1rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-group {
  margin-bottom: 1.2rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-group input:disabled {
  background: #f9f9f9;
  cursor: not-allowed;
}

.form-group .text-muted {
  color: #666;
  font-size: 0.8rem;
  margin-top: 0.3rem;
}

.form-actions {
  margin-top: 1.5rem;
}

@media (max-width: 992px) {
  .profile-content {
    grid-template-columns: 1fr;
  }
  
  .profile-sidebar {
    margin-bottom: 1.5rem;
  }
  
  .sidebar-menu {
    display: flex;
    overflow-x: auto;
  }
  
  .sidebar-menu a {
    border-left: none;
    border-bottom: 3px solid transparent;
    flex-shrink: 0;
  }
  
  .sidebar-menu a.active {
    border-left-color: transparent;
    border-bottom-color: #3498db;
  }
}

@media (max-width: 576px) {
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>