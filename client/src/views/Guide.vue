<template>
  <div class="bg-gray-100 min-h-screen font-sans text-gray-900">

    <!-- HERO -->
    <section class="max-w-6xl mx-auto px-6 py-10 grid md:grid-cols-2 gap-8 items-center">
      <div>
        <p class="text-xs font-bold text-rose-500 uppercase mb-2">
          Cẩm nang thuê phòng
        </p>

        <h1 class="text-4xl font-extrabold leading-tight mb-4">
          Tìm nơi ở <span class="text-rose-500">hoàn hảo</span> của bạn.
        </h1>

        <p class="text-gray-500 text-sm leading-relaxed">
          Hướng dẫn chi tiết từ việc tìm kiếm đến khi nhận chìa khóa,
          giúp bạn thuê phòng an toàn và nhanh chóng nhất.
        </p>
      </div>

      <img 
        src="https://images.unsplash.com/photo-1505693416388-ac5ce068fe85"
        class="rounded-2xl object-cover w-full h-[260px]"
      />
    </section>

    <!-- SEARCH GUIDE -->
    <section class="max-w-6xl mx-auto px-6 py-6 grid md:grid-cols-3 gap-6">

      <!-- LEFT -->
      <div class="md:col-span-2 bg-white rounded-2xl p-6 shadow border">
        <h2 class="font-bold text-lg mb-4">Cách tìm phòng trống</h2>

        <div class="space-y-4 text-sm text-gray-600">
          <div class="flex gap-3">
            <div class="text-rose-500 font-bold">1</div>
            <div>
              <b>Chọn khu vực</b>
              <p>Nhập tên quận, phường hoặc đường bạn muốn sinh sống.</p>
            </div>
          </div>

          <div class="flex gap-3">
            <div class="text-rose-500 font-bold">2</div>
            <div>
              <b>Lọc theo giá & tiện ích</b>
              <p>Thiết lập khoảng giá phù hợp và các tiện ích cần thiết.</p>
            </div>
          </div>

          <div class="flex gap-3">
            <div class="text-rose-500 font-bold">3</div>
            <div>
              <b>Duyệt theo Map</b>
              <p>Sử dụng bản đồ để xem trực quan các phòng trống.</p>
            </div>
          </div>
        </div>

        <div class="flex gap-2 mt-4">
          <span class="text-xs bg-gray-100 px-3 py-1 rounded-full">#TìmPhòngNhanh</span>
          <span class="text-xs bg-gray-100 px-3 py-1 rounded-full">#LọcTiệnÍch</span>
        </div>
      </div>

      <!-- MAP CARD -->
      <div class="bg-gradient-to-br from-rose-500 to-pink-600 text-white rounded-2xl p-6 flex flex-col justify-center items-center text-center shadow">
        <div class="text-3xl mb-2">🔍</div>
        <p class="font-bold text-lg">Duyệt theo Map</p>
        <p class="text-xs mt-1 opacity-80">
          Tìm phòng nhanh hơn bằng bản đồ
        </p>
        <button class="mt-4 bg-white text-rose-500 px-4 py-2 rounded-lg text-sm font-semibold hover:bg-gray-100">
          Thử ngay
        </button>
      </div>

    </section>

    <!-- STATUS -->
    <section class="max-w-6xl mx-auto px-6 py-6 grid md:grid-cols-2 gap-6">
      
      <div class="bg-white p-6 rounded-2xl border shadow">
        <h3 class="font-bold mb-3">Trạng thái phòng</h3>

        <div class="space-y-3 text-sm">
          <div class="flex items-center gap-2">
            <span class="w-3 h-3 bg-green-500 rounded-full"></span>
            Trống - Có thể thuê ngay
          </div>

          <div class="flex items-center gap-2">
            <span class="w-3 h-3 bg-gray-400 rounded-full"></span>
            Đã thuê - Không khả dụng
          </div>
        </div>
      </div>

      <div class="bg-white p-6 rounded-2xl border shadow">
        <h3 class="font-bold mb-3">Xác nhận trạng thái</h3>

        <ul class="text-sm text-gray-600 space-y-2">
          <li>✔ Cập nhật theo thời gian thực</li>
          <li>✔ Lọc riêng phòng còn trống</li>
          <li>✔ Hiển thị rõ bằng màu sắc</li>
        </ul>
      </div>

    </section>

    <!-- CONTACT -->
    <section class="bg-gray-100 py-10 mt-6">
      <div class="max-w-6xl mx-auto px-6 text-center">

        <h2 class="font-bold text-lg mb-6">Kết nối với Chủ trọ</h2>

        <!-- ICON BIG CIRCLE -->
        <div class="grid grid-cols-2 md:grid-cols-4 gap-6 mb-6">

          <div class="flex flex-col items-center">
            <div class="w-20 h-20 bg-gray-200 rounded-full flex items-center justify-center text-xl mb-2">
              👁
            </div>
            <p class="text-sm font-medium">1. Xem thông tin</p>
          </div>

          <div class="flex flex-col items-center">
            <div class="w-20 h-20 bg-gray-200 rounded-full flex items-center justify-center text-xl mb-2">
              📋
            </div>
            <p class="text-sm font-medium">2. Sao chép SĐT</p>
          </div>

          <div class="flex flex-col items-center">
            <div class="w-20 h-20 bg-gray-200 rounded-full flex items-center justify-center text-xl mb-2">
              💬
            </div>
            <p class="text-sm font-medium">3. Mở Zalo</p>
          </div>

          <div class="flex flex-col items-center">
            <div class="w-20 h-20 bg-gray-200 rounded-full flex items-center justify-center text-xl mb-2">
              📩
            </div>
            <p class="text-sm font-medium">4. Nhắn tin</p>
          </div>

        </div>

        <!-- ZALO BOX -->
        <div class="bg-blue-50 border rounded-xl p-4 flex justify-between items-center">
          <p class="text-sm text-gray-700">
            💬 Hỗ trợ nhanh qua Zalo
          </p>
          <button class="bg-blue-500 text-white px-4 py-2 rounded-lg text-sm">
            Nhắn cho chúng tôi
          </button>
        </div>

      </div>
    </section>

    <!-- TIPS -->
    <section class="max-w-6xl mx-auto px-6 py-10">
    <div class="bg-[#0f0f0f] text-white rounded-2xl p-8 relative overflow-hidden">

        <!-- BACKGROUND ICON -->
        <div class="absolute right-6 top-4 opacity-10 text-[120px]">
        🛡️
        </div>

        <!-- TITLE -->
        <h3 class="text-lg font-semibold mb-6">
        Mẹo thuê phòng <span class="text-rose-500">An Toàn</span>
        </h3>

        <!-- CONTENT -->
        <div class="grid md:grid-cols-3 gap-6">

        <!-- ITEM -->
        <div class="flex gap-3">
            <div class="w-8 h-8 bg-[#1f1f1f] rounded-lg flex items-center justify-center text-rose-500">
            👁
            </div>
            <div>
            <p class="font-medium text-sm mb-1">Xem phòng thực tế</p>
            <p class="text-xs text-gray-400 leading-relaxed">
                Luôn đến xem phòng trực tiếp để kiểm tra cơ sở vật chất trước khi đặt cọc hoặc hoàn thiện.
            </p>
            </div>
        </div>

        <!-- ITEM -->
        <div class="flex gap-3">
            <div class="w-8 h-8 bg-[#1f1f1f] rounded-lg flex items-center justify-center text-rose-500">
            📄
            </div>
            <div>
            <p class="font-medium text-sm mb-1">Hợp đồng rõ ràng</p>
            <p class="text-xs text-gray-400 leading-relaxed">
                Đảm bảo đọc kỹ điều khoản và giá điện, nước, internet và tiền cọc được ghi rõ trong hợp đồng.
            </p>
            </div>
        </div>

        <!-- ITEM -->
        <div class="flex gap-3">
            <div class="w-8 h-8 bg-[#1f1f1f] rounded-lg flex items-center justify-center text-rose-500">
            💳
            </div>
            <div>
            <p class="font-medium text-sm mb-1">Thanh toán minh bạch</p>
            <p class="text-xs text-gray-400 leading-relaxed">
                Sử dụng chuyển khoản hoặc có biên nhận cho mọi khoản thanh toán liên quan đến thuê phòng.
            </p>
            </div>
        </div>

        </div>

    </div>
    </section>

  </div>
</template>

<script setup>
</script>

<script setup>
</script>

<script setup>
</script>