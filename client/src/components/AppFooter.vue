<template>
  <footer class="footer">
    <div class="footer-container">

      <!-- COLUMN 1 -->
      <div class="footer-column">
        <h3>Giới thiệu</h3>
        <router-link to="/about">Về chúng tôi</router-link>
        <router-link to="/policy">Chính sách bảo mật</router-link>
        <router-link to="/terms">Điều khoản sử dụng</router-link>
        <router-link to="/guide">Hướng dẫn thuê</router-link>
      </div>

      <!-- COLUMN 2 -->
      <div class="footer-column">
        <h3>Hỗ trợ</h3>
        <router-link to="/faq">Câu hỏi thường gặp</router-link>
        <router-link to="/contact">Liên hệ</router-link>
        <router-link to="/report">Báo sự cố</router-link>
        <router-link to="/support">Trung tâm hỗ trợ</router-link>
      </div>

      <!-- COLUMN 3 -->
      <div class="footer-column">
        <h3>Dịch vụ</h3>
        <router-link to="/login">Phòng trống</router-link>
        <router-link to="/areas">Khu trọ</router-link>
        <router-link to="/contracts">Hợp đồng</router-link>
        <router-link to="/payment">Thanh toán</router-link>
      </div>

      <!-- COLUMN 4 -->
      <div class="footer-column">
        <h3>Liên hệ</h3>
        <p>📍 Hà Nội, Việt Nam</p>
        <p>📞 0123 456 789</p>
        <p>📧 roommanager@gmail.com</p>
        <div class="social">
          <span>🌐 Facebook</span>
          <span>📸 Instagram</span>
        </div>
      </div>

    </div>

    <!-- BOTTOM BAR -->
    <div class="footer-bottom">
      © 2026 RoomManager - Hệ thống quản lý nhà trọ. All rights reserved.
    </div>
  </footer>
</template>

<style scoped>
.footer {
  background: #f7f7f7;
  margin-top: 60px;
  padding-top: 50px;
  border-top: 1px solid #eaeaea;
}

.footer-container {
  max-width: 1200px;
  margin: auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 40px;
}

.footer-column h3 {
  font-size: 16px;
  margin-bottom: 15px;
  font-weight: 600;
}

.footer-column a {
  display: block;
  text-decoration: none;
  color: #555;
  margin-bottom: 8px;
  font-size: 14px;
  transition: 0.2s;
}

.footer-column a:hover {
  color: #ff385c;
}

.footer-column p {
  font-size: 14px;
  margin-bottom: 6px;
  color: #555;
}

.social span {
  display: block;
  font-size: 14px;
  margin-top: 5px;
  cursor: pointer;
}

.social span:hover {
  color: #ff385c;
}

.footer-bottom {
  text-align: center;
  padding: 20px;
  margin-top: 40px;
  border-top: 1px solid #ddd;
  font-size: 14px;
  color: #777;
}

/* Responsive */
@media (max-width: 900px) {
  .footer-container {
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;
  }
}

@media (max-width: 500px) {
  .footer-container {
    grid-template-columns: 1fr;
  }
}
</style>