import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import axios from 'axios';
import './assets/css/main.css';
axios.defaults.baseURL = process.env.VUE_APP_API_URL || 'http://localhost:8888'
// Axios defaults
axios.defaults.headers.common['Content-Type'] = 'application/json';
axios.defaults.headers.common['Accept'] = 'application/json';

// Add request interceptor to inject token
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// Add response interceptor to handle auth errors
axios.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    if (error.response && error.response.status === 401) {
      // Clear auth data and redirect to login
      localStorage.removeItem('token');
      localStorage.removeItem('userId');
      window.dispatchEvent(new Event('logout'));
      router.push('/login');
    }
    return Promise.reject(error);
  }
);

// Create and mount Vue app
createApp(App)
  .use(router)
  .mount('#app');