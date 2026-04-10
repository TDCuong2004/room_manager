<template>
  <div class="contract-page">
    
    <!-- THANH ĐIỀU HƯỚNG TRÊN CÙNG (ẨN KHI IN) -->
    <div class="no-print top-actions">
      <button class="btn-secondary" @click="$router.back()">← Quay lại chỉnh sửa</button>
      <div>
        <button class="btn-success" @click="saveData">💾 Lưu hệ thống</button>
        <button class="btn-primary" @click="exportPDF">🖨️ In ra file PDF / Máy in</button>
      </div>
    </div>

    <!-- KHU VỰC HIỂN THỊ TRANG A4 -->
    <div class="document-container">
      
      <!-- TRANG 1: THÔNG TIN HỢP ĐỒNG & ĐIỀU 1, ĐIỀU 2 -->
      <div class="a4-page">
        <div class="text-center">
          <h4 class="bold">CỘNG HOÀ XÃ HỘI CHỦ NGHĨA VIỆT NAM</h4>
          <h4 class="bold">Độc lập - Tự do - Hạnh phúc</h4>
          <div class="divider-line"></div>
        </div>

        <div class="text-center mt-30">
          <h2 class="bold uppercase font-20">HỢP ĐỒNG CHO THUÊ PHÒNG TRỌ</h2>
        </div>

        <div class="legal-grounds italic mt-20">
          <p>- Căn cứ Bộ luật Dân sự số 91/2015/QH13 ngày 24/11/2015 và các văn bản hướng dẫn thi hành;</p>
          <p>- Căn cứ Luật Nhà ở, Luật Đất đai và các văn bản hướng dẫn thi hành;</p>
          <p>- Căn cứ vào năng lực và nhu cầu của các bên chủ thể giao kết hợp đồng.</p>
        </div>

        <p class="mt-20">Hôm nay, ngày {{ contract.day }} tháng {{ contract.month }} năm {{ contract.year }}. Chúng tôi gồm có:</p>

        <!-- BÊN A -->
        <div class="mt-15">
          <h4 class="bold">BÊN A : BÊN CHO THUÊ (PHÒNG TRỌ)</h4>
          <p>Đại diện : <span class="bold">{{ landlord.fullName }}</span> <span class="ml-50">Điện thoại : {{ landlord.phone }}</span></p>
          <p>CCCD số : {{ landlord.cccd }} <span class="ml-30">Cấp ngày : {{ landlord.cccdDate }}</span> <span class="ml-30">tại {{ landlord.cccdPlace }}</span></p>
          <p>Địa chỉ thường trú : {{ landlord.address }}</p>
          <p>Hiện là chủ ngôi nhà : Số 21 ngách 11 ngõ 298 Đê La Thành nhỏ, Phường Văn Miếu Quốc Tử Giám, Thành phố Hà Nội.</p>
        </div>

        <!-- BÊN B -->
        <div class="mt-15">
          <h4 class="bold">BÊN B : BÊN THUÊ (PHÒNG TRỌ)</h4>
          <p>Họ và Tên : <span class="bold uppercase">{{ tenant.fullName }}</span> <span class="ml-50">Ngày sinh : {{ tenant.dob }}</span></p>
          <p>CCCD : {{ tenant.cccd }} <span class="ml-30">Ngày cấp : {{ tenant.cccdDate }}</span> <span class="ml-30">Nơi cấp : {{ tenant.cccdPlace }}</span></p>
          <p>HK thường trú : {{ tenant.address }} <span class="ml-50">Điện thoại : {{ tenant.phone }}</span></p>
          <p>Nơi công tác : ...........................................................................................................................</p>
        </div>

        <p class="italic mt-15">Hai bên cùng thỏa thuận và đồng ý với nội dung sau :</p>

        <div class="article mt-10">
          <h4 class="bold">ĐIỀU 1: ĐỐI TƯỢNG CỦA HỢP ĐỒNG</h4>
          <p>Bên A đồng ý cho bên B thuê phòng <span class="bold">{{ room.roomName }}</span> tại ngôi nhà: số 21 ngách 11 ngõ 298 Đê La Thành nhỏ, Phường Văn Miếu Quốc Tử Giám.</p>
          <p>Thời hạn thuê phòng là <span class="bold">{{ contract.duration }}</span> tháng kể từ ngày {{ contract.startDate }} đến ngày {{ contract.endDate }}</p>
          <p>• Mục đích thuê để lưu trú và sinh hoạt theo nhu cầu của bên B với số lượng người ở là: <span class="bold">{{ contract.memberCount }}</span> người.</p>
        </div>

        <div class="article mt-10">
          <h4 class="bold">ĐIỀU 2: GIÁ THUÊ VÀ PHƯƠNG THỨC THANH TOÁN</h4>
          <p><span class="bold">2.1 Giá Thuê:</span> Hai bên nhất trí giá thuê tại điều 1 của hợp đồng này là <span class="bold">{{ formatMoney(room.price) }}</span> Vnđ/tháng</p>
          <p>(Bằng chữ: <span class="italic">{{ priceInWords }}</span>)</p>
          <ul class="sub-list">
            <li>• Tiền điện : <span class="bold">4.000 đ/kwh</span> tính theo chỉ số công tơ riêng của phòng.</li>
            <li>• Tiền nước: 100.000đ/người. Tiền Internet: 100.000đ/phòng. Máy giặt: 50.000đ/người.</li>
            <li>• Tiền vệ sinh chung, điện chung: 50.000đ/người/tháng.</li>
            <li class="italic">(Các loại phí tính trên đầu người sẽ thu theo số người đã đăng ký lưu trú trong bản hợp đồng này)</li>
          </ul>
          <p><span class="bold">2.2 Phương Thức Thanh Toán:</span> Tiền thuê phòng và các loại chi phí sinh hoạt bên B thanh toán cho bên A 01 tháng 1 lần vào ngày 30-31 hàng tháng.</p>
          <p>Thông tin chuyển khoản: <span class="bold">NH Agribank. Chủ TK: NGUYEN THI NGOC DIEP. Số TK: 8888888889696</span></p>
          <p>Bên B đặt cọc <span class="bold">{{ formatMoney(contract.deposit) }}</span> vnđ (Bằng chữ: <span class="italic">{{ depositInWords }}</span>) cho bên A vào ngày ký hợp đồng. Tiền đặt cọc sẽ được trả lại cho bên B khi hết hợp đồng thuê phòng trọ và thanh toán đầy đủ tiền điện, nước, phí dịch vụ và các khoản khác liên quan theo đúng hợp đồng.</p>
        </div>
      </div> <!-- Hết trang 1 -->


      <!-- TRANG 2: ĐIỀU 3,4,5 VÀ BÀN GIAO TÀI SẢN -->
      <div class="a4-page page-break">
        <div class="article">
          <h4 class="bold">ĐIỀU 3: TRÁCH NHIỆM CỦA BÊN A</h4>
          <p>Tạo điều kiện để bên B thực hiện theo hợp đồng. Cung cấp nguồn điện, nước, wifi cho bên B. Giao phòng trọ, trang thiết bị trong phòng trọ cho bên B đúng ngày ký hợp đồng. Hướng dẫn bên B đăng ký tạm trú tạm vắng theo QĐ của PL.</p>
        </div>

        <div class="article mt-10">
          <h4 class="bold">ĐIỀU 4: TRÁCH NHIỆM BÊN B</h4>
          <p>• Trả tiền thuê phòng trọ hàng kỳ theo hợp đồng, không ở quá số người đã đăng ký.</p>
          <p>• Đồ đạc, thiết bị trong phòng đã lắp đặt, bên B có trách nhiệm bảo quản cẩn thận không làm hư hỏng mất mát. Nếu xảy ra sự cố, bên B có trách nhiệm tự khắc phục để không ảnh hưởng các tầng khác.</p>
          <p>• <span class="bold">Nếu bên B chậm thanh toán tiền nhà quá 04 ngày thì phải chịu phạt 25% tiền thuê nhà của 01 tháng.</span> Bên B chậm thanh toán quá 07 ngày thì bên A có quyền đơn phương chấm dứt hợp đồng và không hoàn lại tiền đặt cọc.</p>
          <p>• Bên B không được ngưng hợp đồng trước thời hạn, nếu muốn chuyển đi phải báo trước 01 tháng và tìm người thay thế. Nếu không báo trước đủ 01 tháng thì không được hoàn lại tiền đặt cọc.</p>
          <p>• Cấm tàng trữ, sử dụng chất cấm, ma tuý, mại dâm. Nếu vi phạm chịu trách nhiệm hình sự và đền bù cho bên A.</p>
        </div>

        <div class="article mt-10">
          <h4 class="bold">ĐIỀU 5: ĐIỀU KHOẢN CHUNG</h4>
          <p>Hai bên cam kết thực hiện đúng các điều khoản ghi trong hợp đồng. Hợp đồng lập thành 02 bản có giá trị ngang nhau, mỗi bên giữ 01 bản.</p>
        </div>

        <div class="signature-section mt-30">
          <div class="col text-center">
            <p class="bold">BÊN CHO THUÊ NHÀ</p>
            <p class="italic">(Ký tên)</p>
            <div class="sign-space"></div>
            <p class="bold">{{ landlord.fullName }}</p>
          </div>
          <div class="col text-center">
            <p class="bold">BÊN THUÊ NHÀ</p>
            <p class="italic">(Ký tên)</p>
            <div class="sign-space"></div>
            <p class="bold">{{ tenant.fullName }}</p>
          </div>
        </div>

        <div class="furniture-table-section mt-40">
          <h4 class="bold uppercase text-center">DANH SÁCH ĐỒ DÙNG VÀ THIẾT BỊ BÊN A BÀN GIAO</h4>
          <table class="furniture-table">
            <thead>
              <tr>
                <th style="width: 10%">STT</th>
                <th style="width: 40%">Tên loại</th>
                <th style="width: 20%">Số lượng</th>
                <th style="width: 30%">Tình trạng</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, idx) in furniture" :key="idx">
                <td>{{ idx + 1 }}</td>
                <td class="text-left">{{ item.name }}</td>
                <td>{{ item.qty }}</td>
                <td>{{ item.status }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div> <!-- Hết trang 2 -->

      <!-- TRANG 3: NỘI QUY CHUNG VÀ SINH HOẠT -->
      <div class="a4-page page-break">
        <div class="text-center">
          <h3 class="bold uppercase">NỘI QUY CHUNG</h3>
        </div>
        <div class="rules-box mt-10">
          <p>1. Giữ gìn vệ sinh chỗ ở, không làm mất trật tự, ồn ào ảnh hưởng phòng khác.</p>
          <p>2. Bảo quản tài sản trang thiết bị, không khoan đục, xê dịch tài sản trong phòng.</p>
          <p>3. Để xe đúng nơi quy định, xếp gọn gàng. Không cho người lạ để xe trong nhà.</p>
          <p>4. Không vẽ bậy, dán giấy báo, tranh ảnh lên tường, trần nhà.</p>
          <p>5. Phơi quần áo đúng nơi quy định, không phơi đồ ngay hành lang.</p>
          <p>6. Cấm tàng trữ chất cấm, tụ tập bạn bè về phòng nhậu gây ồn ào.</p>
          <p>7. Tuân thủ tốt công tác PCCC, không sử dụng chất gây nổ.</p>
          <p>8. Không hát hò, la hét, mở nhạc to sau 23 giờ đêm.</p>
        </div>

        <div class="text-center mt-20">
          <h3 class="bold uppercase">NỘI QUY SINH HOẠT</h3>
        </div>
        <div class="rules-box mt-10">
          <p>1. Nấu ăn sử dụng bếp từ, bình gas có khóa an toàn.</p>
          <p>2. Không bỏ rác, giấy vệ sinh, vỏ trứng gây nghẹt bồn cầu.</p>
          <p>3. Không rửa rau, chén bát trong nhà vệ sinh vì sẽ làm nghẹt cống.</p>
          <p>4. Rác phải thu gom gọn gàng, buộc chặt bịch và vứt đúng nơi quy định.</p>
          <p class="bold italic">NHỚ TẮT HẾT THIẾT BỊ ĐIỆN, QUẠT KHI RA KHỎI PHÒNG. RA VÀO KHÓA CỬA CẨN THẬN.</p>
        </div>

        <div class="penalty mt-15">
          <p>Chú ý: Nội quy sinh hoạt mang tính răn đe, nhắc nhở có ý thức giữ gìn vệ sinh chung:</p>
          <p>• Với những cá nhân cố tình vi phạm, phạt <span class="bold">300.000đ / lần đầu tiên</span>. Phạt <span class="bold">500.000đ lần kế tiếp</span>.</p>
        </div>

        <div class="roommate-list mt-20">
          <h4 class="bold">CÁC THÀNH VIÊN Ở CÙNG PHÒNG</h4>
          <table class="furniture-table">
            <thead>
              <tr>
                <th>Họ và tên</th>
                <th>Sinh năm</th>
                <th>SĐT</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="i in 4" :key="i">
                <td height="30"></td>
                <td></td>
                <td></td>
              </tr>
            </tbody>
          </table>
        </div>
        <p class="italic mt-15">Bên B đồng ý chấp hành đầy đủ nội quy và xác thực thông tin các cá nhân sinh hoạt chung là chính xác.</p>
        
        <div class="text-right mt-20">
          <p class="bold">BÊN THUÊ NHÀ</p>
          <p class="italic">(Ký tên)</p>
        </div>
      </div> <!-- Hết trang 3 -->

    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue"

// DỮ LIỆU CHỦ NHÀ CỐ ĐỊNH THEO ẢNH
const landlord = reactive({
  fullName: "Nguyễn Thị Ngọc Diệp",
  phone: "088 888 9696",
  cccd: "001196008731",
  cccdDate: "10/07/2021",
  cccdPlace: "Cục CSQLHC về TTXH",
  address: "Hoàng Hoa Thám, Liễu Giai, Ba Đình, Hà Nội"
})

// DỮ LIỆU NGƯỜI THUÊ LẤY TỪ ROUTER/API (Nếu có)
const contractData = JSON.parse(sessionStorage.getItem("contractData") || "{}")

const tenant = reactive(contractData.form?.customers?.[0] || {
  fullName: "TẠ ĐÌNH CƯƠNG",
  dob: "17/12/2004",
  cccd: "033204010116",
  cccdDate: "17/12/2021",
  cccdPlace: "Bắc Giang",
  address: "Hồng Giang, Lục Ngạn, Bắc Giang",
  phone: "035 326 0919"
})

const room = reactive(contractData.room || {
  roomName: "101",
  price: 1500000
})

const contract = reactive({
  day: "01", month: "10", year: "2025",
  startDate: "01/10/2025",
  endDate: "30/09/2026",
  duration: 12,
  deposit: contractData.form?.deposit || 1500000,
  memberCount: contractData.form?.customers?.length || 1
})

const priceInWords = "Một triệu năm trăm nghìn đồng"
const depositInWords = "Một triệu năm trăm nghìn đồng"

// THIẾT BỊ BÀN GIAO MẶC ĐỊNH
const furniture = [
  { name: "Giường", qty: "01", status: "Bình thường" },
  { name: "Tủ quần áo", qty: "01", status: "Bình thường" },
  { name: "Điều hòa + điều khiển", qty: "01", status: "Bình thường" },
  { name: "Bình nóng lạnh", qty: "01", status: "Bình thường" },
  { name: "Bàn bếp + bồn rửa", qty: "01", status: "Bình thường" }
]

const formatMoney = (val) => new Intl.NumberFormat('vi-VN').format(val)

const exportPDF = () => {
  window.print() // Mở hộp thoại in, người dùng chọn Lưu thành PDF
}

const saveData = () => {
  alert("Lưu dữ liệu thành công!")
}
</script>

<style scoped>
/* RESET & KHUNG BAO NGOÀI */
.contract-page {
  background: #f4f5f7;
  padding: 40px 0;
  min-height: 100vh;
}

/* ĐỊNH DẠNG KHỔ A4 (Quan trọng nhất) */
.document-container {
  display: flex;
  flex-direction: column;
  gap: 30px; /* Tạo khoảng cách giữa các trang trên Web */
}

.a4-page {
  width: 210mm;
  min-height: 297mm;
  background: #ffffff;
  margin: 0 auto;
  padding: 20mm 25mm 20mm 25mm; /* Lề chuẩn in ấn văn bản */
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  font-family: "Times New Roman", Times, serif;
  font-size: 13pt; /* Cỡ chữ in chuẩn */
  line-height: 1.5;
  color: #000;
  box-sizing: border-box;
}

/* LỚP TIỆN ÍCH */
.text-center { text-align: center; }
.text-right { text-align: right; }
.text-left { text-align: left; }
.bold { font-weight: bold; }
.italic { font-style: italic; }
.uppercase { text-transform: uppercase; }
.mt-10 { margin-top: 10px; }
.mt-15 { margin-top: 15px; }
.mt-20 { margin-top: 20px; }
.mt-30 { margin-top: 30px; }
.mt-40 { margin-top: 40px; }
.ml-30 { margin-left: 30px; }
.ml-50 { margin-left: 50px; }
.font-20 { font-size: 20pt; }

.divider-line {
  width: 150px;
  height: 1.5px;
  background: #000;
  margin: 5px auto;
}

.article { margin-top: 15px; }
.sub-list { list-style: none; padding-left: 15px; }

/* CHỮ KÝ */
.signature-section {
  display: flex;
  justify-content: space-between;
}
.signature-section .col { width: 45%; }
.sign-space { height: 100px; }

/* BẢNG TÀI SẢN */
.furniture-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}
.furniture-table th, .furniture-table td {
  border: 1px solid #000;
  padding: 8px;
  text-align: center;
}

/* CÁC NÚT THAO TÁC */
.top-actions {
  display: flex;
  justify-content: space-between;
  max-width: 210mm;
  margin: 0 auto 20px auto;
  background: white;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}

.top-actions button {
  padding: 10px 18px;
  border-radius: 8px;
  border: none;
  font-weight: bold;
  cursor: pointer;
  margin-left: 10px;
  font-size: 14px;
}

.btn-secondary { background: #e2e8f0; color: #4a5568; }
.btn-primary { background: #1a73e8; color: white; }
.btn-success { background: #10b981; color: white; }

/* CHUẨN IN FILE PDF BẰNG LỆNH IN CỦA TRÌNH DUYỆT */
@media print {
  .no-print { display: none !important; } /* Ẩn hết các nút điều hướng */
  .contract-page { padding: 0; background: white; }
  .a4-page {
    margin: 0;
    box-shadow: none;
    padding: 20mm 25mm;
    width: 210mm;
    min-height: 297mm;
  }
  .page-break { page-break-before: always; } /* Tự động chia trang đúng cách không bị rách chữ */
}
</style>