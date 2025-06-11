<template>
  <footer class="bg-dark text-white mt-5 py-4">
    <div class="container">
      <div class="row">
        <div class="col-md-4 mb-4 mb-md-0">
          <h5 class="mb-3">BidaStore</h5>
          <p>Chuyên cung cấp các loại gậy bida chất lượng cao từ các thương hiệu nổi tiếng trên thế giới.</p>
          <div class="d-flex mt-3">
            <a href="#" class="text-white me-3"><i class="bi bi-facebook fs-5"></i></a>
            <a href="#" class="text-white me-3"><i class="bi bi-instagram fs-5"></i></a>
            <a href="#" class="text-white me-3"><i class="bi bi-youtube fs-5"></i></a>
            <a href="#" class="text-white"><i class="bi bi-tiktok fs-5"></i></a>
          </div>
        </div>
        <div class="col-md-4 mb-4 mb-md-0">
          <h5 class="mb-3">Thông tin liên hệ</h5>
          <ul class="list-unstyled">
            <li class="mb-2"><i class="bi bi-geo-alt me-2"></i> 123 Đường ABC, Quận 1, TP.HCM</li>
            <li class="mb-2"><i class="bi bi-telephone me-2"></i> 0123 456 789</li>
            <li class="mb-2"><i class="bi bi-envelope me-2"></i> contact@bidastore.com</li>
            <li><i class="bi bi-clock me-2"></i> 8:00 - 22:00, Thứ 2 - Chủ nhật</li>
          </ul>
        </div>
        <div class="col-md-4">
          <h5 class="mb-3">Danh mục sản phẩm</h5>
          <ul class="list-unstyled">
            <li class="mb-2"><a href="#" class="text-white text-decoration-none">Gậy bida phổ thông</a></li>
            <li class="mb-2"><a href="#" class="text-white text-decoration-none">Gậy bida cao cấp</a></li>
            <li class="mb-2"><a href="#" class="text-white text-decoration-none">Túi đựng gậy</a></li>
            <li><a href="#" class="text-white text-decoration-none">Phụ kiện bida</a></li>
          </ul>
        </div>
      </div>
      <hr class="my-4">
      <div class="text-center">
        <p class="mb-0">© {{ new Date().getFullYear() }} BidaStore. Tất cả quyền được bảo lưu.</p>
      </div>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'Footer'
}
</script>