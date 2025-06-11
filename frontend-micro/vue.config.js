module.exports = {
  devServer: {
    proxy: {
      '/api/v1': {
        target: 'http://localhost:8888',
        changeOrigin: true
        // Bỏ pathRewrite vì đã có /v1 trong URL
      }
    }
  }
}