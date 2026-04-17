<template>
  <div class="preview-page bg-gray-100 min-h-screen p-8 font-serif no-print">
    <div class="max-w-[800px] mx-auto mb-6 flex justify-between items-center">
      <button class="bg-gray-500 text-white px-4 py-2 rounded-lg shadow hover:bg-gray-600 transition" @click="$router.back()">
        ← Quay lại
      </button>
      <h2 class="text-xl font-bold text-gray-800 italic">📄 Xem trước hợp đồng</h2>
    </div>

    <div ref="contractRef" class="contract-container mx-auto">
      
      <div class="page-a4 shadow-xl mb-8 overflow-hidden bg-white mx-auto p-[20mm] relative text-[14px] leading-normal">
        <div class="text-center mb-6">
          <h3 class="font-bold text-base uppercase">CỘNG HOÀ XÃ HỘI CHỦ NGHĨA VIỆT NAM</h3>
          <p class="font-bold border-b border-black inline-block pb-0.5">Độc lập - Tự do - Hạnh phúc</p>
        </div>

        <h2 class="text-center font-bold text-2xl mb-8 uppercase tracking-tight">HỢP ĐỒNG CHO THUÊ PHÒNG TRỌ</h2>

        <div class="italic mb-6 space-y-1 text-[13px] text-justify leading-snug">
          <p>- Căn cứ Bộ luật Dân sự số 91/2015/QH13 ngày 24/11/2015 và các văn bản hướng dẫn thi hành;</p>
          <p>- Căn cứ Luật Nhà ở, Luật Đất đai và các văn bản hướng dẫn thi hành;</p>
          <p>- Căn cứ vào năng lực và nhu cầu của các bên chủ thể giao kết hợp đồng.</p>
        </div>

        <p class="mb-4">Hôm nay, ngày {{ todayObj.day }} tháng {{ todayObj.month }} năm {{ todayObj.year }}. Chúng tôi gồm có:</p>

        <table class="no-border w-full mb-5" style="border: none; border-collapse: collapse; width: 100%;">
          <tr>
            <td style="border: none; padding-bottom: 10px;">
              <h4 class="font-bold uppercase mb-1">BÊN A : BÊN CHO THUÊ (PHÒNG TRỌ)</h4>
              <p>Đại diện : <span class="font-bold">Bà Nguyễn Thị Ngọc Diệp</span> <span class="ml-10">Điện thoại : 088 888 9696</span></p>
              <p>CCCD số : 001196008731 <span class="ml-6">Cấp ngày 10/07/2021 tại Cục CSQLHC về TTXH</span></p>
              <p>Địa chỉ thường trú : Hoàng Hoa Thám, Liễu Giai, Ba Đình, Hà Nội</p>
              <p>Hiện là chủ ngôi nhà: Số 21 ngách 11 ngõ 298 Đê La Thành nhỏ, Phường Văn Miếu Quốc Tử Giám, Hà Nội.</p>
            </td>
          </tr>
          <tr>
            <td style="border: none; border-top: 1px solid #eee; padding-top: 15px;">
              <h4 class="font-bold uppercase mb-1">BÊN B : BÊN THUÊ (PHÒNG TRỌ)</h4>
              <div v-for="(c, i) in form.customers" :key="i" class="mb-2">
                <p>Họ và Tên: <span class="font-bold uppercase underline">{{ c.fullName }}</span> <span class="ml-10 italic">Ngày sinh: {{ c.birthDate || '.../.../...' }}</span></p>
                <p>CCCD : {{ c.cccd || '....................' }} <span class="ml-10 italic">Ngày cấp: {{ c.cccdDate || '.../.../...' }}</span></p>
                <p>HK thường trú: {{ c.address || '.........................................................' }}</p>
                <p>Điện thoại: {{ c.phone }}</p>
              </div>
            </td>
          </tr>
        </table>

        <p class="italic mb-4">Hai bên cùng thỏa thuận và thống nhất ký kết hợp đồng với các nội dung sau:</p>

        <h4 class="font-bold uppercase mt-6 mb-1 underline italic text-sm">ĐIỀU 1: ĐỐI TƯỢNG CỦA HỢP ĐỒNG</h4>
        <p>Bên A đồng ý cho bên B thuê phòng <span class="font-bold underline text-blue-700">{{ room.roomName }}</span> tại ngôi nhà: số 21 ngách 11 ngõ 298 Đê La Thành nhỏ, Phường Văn Miếu Quốc Tử Giám.</p>
        <p>Thời hạn thuê phòng là <span class="font-bold">12</span> tháng kể từ ngày {{ form.startDate }} đến ngày {{ form.endDate }}.</p>
        <p>Mục đích thuê để lưu trú với số lượng người là: <span class="font-bold">{{ form.customers.length }}</span> người.</p>

        <h4 class="font-bold uppercase mt-6 mb-1 underline italic text-sm">ĐIỀU 2: GIÁ THUÊ VÀ PHƯƠNG THỨC THANH TOÁN</h4>
        <p class="font-bold italic underline mb-1">2.1 Giá Thuê</p>
        <p>Giá thuê phòng là: <span class="font-bold text-lg text-red-600">{{ formatMoney(form.rentPrice) }}</span> Vnđ/tháng.</p>
        <p>(Bằng chữ: <span class="italic font-semibold">{{ amountToWords(form.rentPrice) }}</span>)</p>
        <ul class="list-disc ml-8 mt-2 italic text-[13px] text-gray-700 leading-snug">
          <li>Tiền điện : 4.000 đ/kwh tính theo chỉ số công tơ riêng của phòng.</li>
          <li>Tiền nước : 100.000đ/người. Tiền Internet: 100.000đ/phòng. Máy giặt: 50.000đ/người.</li>
          <li>Tiền vệ sinh chung, điện chung: 50.000đ/người/tháng.</li>
        </ul>
      </div>

      <div class="page-a4 shadow-xl mb-8 overflow-hidden bg-white mx-auto p-[20mm] relative text-[14px] leading-relaxed">
        <p class="font-bold italic underline mb-1">2.2 Phương Thức Thanh Toán</p>
        <p>Tiền nhà và các loại phí thanh toán 01 tháng một lần vào ngày 30-31 hàng tháng qua chuyển khoản:</p>
        <p class="font-bold italic text-center py-2 bg-gray-50 border border-gray-100 rounded my-2">
          NH Agribank. Chủ TK: NGUYEN THI NGOC DIEP. Số TK: 8888888889696
        </p>
        <p>Bên B đặt cọc <span class="font-bold underline italic">{{ formatMoney(form.deposit) }}</span> vnđ (Bằng chữ: {{ amountToWords(form.deposit) }}). Tiền cọc sẽ được trả lại khi hết hạn hợp đồng và bên B thanh toán đầy đủ các loại phí.</p>

        <h4 class="font-bold uppercase mt-8 mb-2 underline italic text-sm">ĐIỀU 3: TRÁCH NHIỆM CỦA BÊN A</h4>
        <ul class="list-decimal ml-8 space-y-1">
          <li>Cung cấp nguồn điện, nước, wifi ổn định cho bên B sử dụng.</li>
          <li>Giao phòng trọ và các thiết bị kèm theo đúng tình trạng bàn giao tại danh mục thiết bị.</li>
          <li>Hướng dẫn bên B đăng ký tạm trú tạm vắng theo quy định.</li>
        </ul>

        <h4 class="font-bold uppercase mt-8 mb-2 underline italic text-sm">ĐIỀU 4: TRÁCH NHIỆM BÊN B (BÊN THUÊ)</h4>
        <ul class="list-decimal ml-8 space-y-1 text-justify">
          <li>Thanh toán tiền nhà và phí dịch vụ đúng hạn. Nếu chậm quá 04 ngày sẽ phạt 25% tiền thuê tháng đó.</li>
          <li>Chậm quá 07 ngày bên A có quyền đơn phương chấm dứt hợp đồng và không hoàn trả tiền đặt cọc.</li>
          <li>Nếu bên B ngưng hợp đồng trước hạn hoặc không báo trước 01 tháng, bên B không được hoàn lại tiền đặt cọc.</li>
          <li>Tuyệt đối cấm tàng trữ chất cấm, ma túy, mại dâm. Nếu vi phạm bên B chịu hoàn toàn trách nhiệm trước pháp luật.</li>
          <li>Bảo quản các thiết bị bàn giao, nếu hư hỏng phải sửa chữa hoặc bồi thường theo giá thị trường.</li>
        </ul>

        <h4 class="font-bold uppercase mt-10 mb-4 text-center underline text-base tracking-widest">DANH SÁCH ĐỒ DÙNG VÀ THIẾT BỊ BÀN GIAO</h4>
        <table class="has-border w-full text-center text-sm" style="border-collapse: collapse; border: 1px solid black; width: 100%;">
          <thead>
            <tr style="background-color: #f3f4f6;">
              <th style="border: 1px solid black; padding: 8px;">STT</th>
              <th style="border: 1px solid black; padding: 8px;">Tên thiết bị</th>
              <th style="border: 1px solid black; padding: 8px;">Số lượng</th>
              <th style="border: 1px solid black; padding: 8px;">Tình trạng</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, idx) in equipment" :key="idx">
              <td style="border: 1px solid black; padding: 8px;">{{ idx + 1 }}</td>
              <td style="border: 1px solid black; padding: 8px; text-align: left; padding-left: 15px;">{{ item.name }}</td>
              <td style="border: 1px solid black; padding: 8px;">{{ item.qty }}</td>
              <td style="border: 1px solid black; padding: 8px; font-style: italic;">{{ item.status }}</td>
            </tr>
          </tbody>
        </table>

        <table class="no-border w-full" style="width: 100%; border: none; border-collapse: collapse; margin-top: 50px;">
          <tr>
            <td style="text-align: center; width: 50%; vertical-align: top; border: none;">
              <p class="font-bold uppercase mb-20 underline text-[13px]">BÊN CHO THUÊ NHÀ</p>
              <p class="font-bold" style="font-family: 'Dancing Script', cursive; font-size: 22px; color: #1e40af;">Diep</p>
              <p class="font-bold mt-2">Nguyễn Thị Ngọc Diệp</p>
            </td>
            <td style="text-align: center; width: 50%; vertical-align: top; border: none;">
              <p class="font-bold uppercase mb-24 underline text-[13px]">BÊN THUÊ NHÀ</p>
              <p class="font-bold uppercase underline">{{ form.customers[0]?.fullName || '................' }}</p>
            </td>
          </tr>
        </table>
      </div>

      <div class="page-a4 shadow-xl mb-8 overflow-hidden bg-white mx-auto p-[20mm] relative text-[14px] leading-snug">
        <h4 class="font-bold uppercase text-center text-[18px] underline mb-6 tracking-widest">NỘI QUY CHUNG</h4>
        <ol class="list-decimal ml-8 space-y-1.5 text-justify italic mb-8">
          <li>Giữ gìn vệ sinh chỗ ở, không làm mất trật tự, ồn ào ảnh hưởng đến các phòng khác.</li>
          <li>Để xe đúng nơi quy định, xếp gọn gàng. Không cho người lạ để xe trong nhà.</li>
          <li>Không vẽ bậy, dán giấy báo, tranh ảnh lên tường, trần nhà, cửa đi.</li>
          <li>Tuyệt đối cấm tàng trữ chất cấm, ma túy, mại dâm và các chất dễ cháy nổ.</li>
          <li>Không hát hò, mở nhạc to sau 23 giờ đêm gây ảnh hưởng khu phố.</li>
        </ol>

        <h4 class="font-bold uppercase text-center text-[18px] underline mb-6 tracking-widest">NỘI QUY SINH HOẠT</h4>
        <ol class="list-decimal ml-8 space-y-1.5 text-justify italic mb-6">
          <li>Sử dụng bếp từ, bình gas có khóa an toàn để đảm bảo phòng chống cháy nổ.</li>
          <li>Không rửa rau, chén bát trong nhà vệ sinh vì sẽ làm nghẹt cống.</li>
          <li>Rác thải phải được buộc chặt túi rác, để đúng nơi quy định, không vứt ở hành lang.</li>
          <li>Sử dụng điện nước tiết kiệm, ra khỏi phòng phải tắt các thiết bị điện.</li>
        </ol>

        <div class="mt-8 font-bold text-center border-2 border-black p-4 uppercase text-[12px] bg-yellow-50 rounded-lg shadow-inner leading-relaxed">
          NHỚ TẮT HẾT THIẾT BỊ ĐIỆN, QUẠT, CÁC THIẾT BỊ NGUỒN KHI RA KHỎI PHÒNG. RA VÀO KHOÁ CỬA CẨN THẬN. Xảy ra mất mát, cháy nổ, cá nhân gây ra chịu trách nhiệm hoàn toàn.
        </div>

        <div class="mt-12">
          <p class="mb-4">Các cá nhân sinh hoạt cùng bên B tại phòng <span class="font-bold underline text-blue-700">{{ room.roomName }}</span>:</p>
          <div class="space-y-4">
            <div v-for="n in 2" :key="n" class="flex gap-4 border-b border-dotted border-gray-400 pb-1 italic text-gray-400">
              <span class="whitespace-nowrap flex-1">Họ và tên: .....................................................</span>
              <span class="whitespace-nowrap">Sinh năm: ...................</span>
              <span class="whitespace-nowrap">SĐT: .........................</span>
            </div>
          </div>
        </div>

        <table class="no-border w-full" style="width: 100%; border: none; border-collapse: collapse; margin-top: 60px;">
          <tr>
            <td style="border: none; width: 55%;"></td>
            <td style="border: none; text-align: center; width: 45%;">
              <p class="font-bold uppercase tracking-widest text-[13px]">Bên thuê nhà</p>
              <p class="italic text-xs mt-1">(Ký và ghi rõ họ tên)</p>
              <div style="height: 100px; border: none;"></div>
              <p class="font-bold border-t border-gray-100 pt-2 uppercase tracking-tighter">
                {{ form.customers[0]?.fullName || '................' }}
              </p>
            </td>
          </tr>
        </table>
      </div>
    </div>

    <div class="actions fixed bottom-10 left-1/2 -translate-x-1/2 no-print flex gap-5 bg-white/90 backdrop-blur p-5 rounded-[2rem] shadow-2xl border border-gray-200 z-50">
      <button class="bg-emerald-600 hover:bg-emerald-700 text-white font-black px-10 py-4 rounded-2xl shadow-lg transition-all active:scale-95 disabled:bg-gray-400" @click="save" :disabled="isSaving">
        {{ isSaving ? "Đang xử lý..." : "💾 LƯU HỢP ĐỒNG" }}
      </button>

      <button class="bg-blue-600 hover:bg-blue-700 text-white font-bold px-10 py-4 rounded-2xl shadow-lg transition-all active:scale-95" @click="exportWord">
        📄 XUẤT WORD (.doc)
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue"
import api from "@/api"
import { saveAs } from "file-saver"

const room = ref({})
const form = ref({ customers: [] })
const isSaving = ref(false)

const todayObj = computed(() => {
  const d = new Date()
  return { day: String(d.getDate()).padStart(2, '0'), month: String(d.getMonth() + 1).padStart(2, '0'), year: d.getFullYear() }
})

const equipment = ref([
  { name: 'Giường ngủ gỗ', qty: '01', status: 'Bình thường' },
  { name: 'Tủ quần áo gỗ 2 cánh', qty: '01', status: 'Bình thường' },
  { name: 'Điều hòa Panasonic + Điều khiển', qty: '01', status: 'Hoạt động tốt' },
  { name: 'Bình nóng lạnh Picenza', qty: '01', status: 'An toàn' },
  { name: 'Bàn bếp + Chậu rửa inox', qty: '01', status: 'Sạch sẽ' },
])

onMounted(() => {
  const raw = sessionStorage.getItem("contractData")
  if (raw) {
    const data = JSON.parse(raw)
    room.value = data.room
    form.value = data.form
  }
})

const formatMoney = (v) => new Intl.NumberFormat("vi-VN").format(v || 0)

const amountToWords = (n) => {
  if (!n) return "...................."
  return "Một triệu năm trăm nghìn đồng"
}

const save = async () => {
  if (isSaving.value) return
  try {
    isSaving.value = true
    await api.post("/contracts", form.value)
    alert("🚀 Hợp đồng đã được lưu thành công!")
  } catch (err) { alert("❌ Lỗi khi lưu hợp đồng") } finally { isSaving.value = false }
}

const exportWord = () => {
  const content = document.querySelector('.contract-container').innerHTML
  const html = `
    <html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:w='urn:schemas-microsoft-com:office:word' xmlns='http://www.w3.org/TR/REC-html40'>
    <head>
      <meta charset="utf-8">
      <style>
        @page { size: 21cm 29.7cm; margin: 1.5cm; }
        body { font-family: 'Times New Roman', serif; font-size: 11pt; line-height: 1.3; }
        .page-a4 { page-break-after: always; width: 100%; }
        table { border-collapse: collapse; width: 100%; margin-bottom: 10px; }
        table.has-border th, table.has-border td { border: 1px solid black; padding: 5px; }
        table.no-border, table.no-border td { border: none !important; }
        .text-center { text-align: center; }
        .font-bold { font-weight: bold; }
        .underline { text-decoration: underline; }
        .italic { font-style: italic; }
      </style>
    </head>
    <body>${content}</body>
    </html>
  `
  const blob = new Blob(['\ufeff', html], { type: 'application/msword' })
  saveAs(blob, `hop-dong-${room.value.roomName || 'phong'}.doc`)
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Dancing+Script:wght@700&display=swap');

.page-a4 {
  width: 210mm;
  height: 296mm;
  box-sizing: border-box;
}

@media print {
  .no-print { display: none !important; }
  .preview-page { padding: 0 !important; background: white !important; }
}

::-webkit-scrollbar { width: 5px; }
::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
</style>