<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 p-4">

    <div class="w-full max-w-md bg-white rounded-2xl shadow-lg p-6 space-y-5">

      <!-- AVATAR -->
      <div class="flex flex-col items-center">

        <div class="w-28 h-28 rounded-full overflow-hidden bg-gray-100 flex items-center justify-center text-3xl font-bold text-gray-500">
          <img
            v-if="user.avatar"
            :src="user.avatar"
            class="w-full h-full object-cover"
          />
          <span v-else>
            {{ user.fullName?.charAt(0) || "U" }}
          </span>
        </div>

        <label class="mt-3 cursor-pointer text-sm bg-[#4CAF50] text-white px-4 py-1.5 rounded-lg hover:bg-[#43A047] transition">
          Thay đổi avatar
          <input type="file" class="hidden" accept="image/*" @change="handleAvatar"/>
        </label>

      </div>

      <!-- NAME -->
      <h2 class="text-center text-lg font-semibold text-gray-800">
        {{ user.fullName || "User" }}
      </h2>

      <!-- FORM -->
      <div class="space-y-3">

        <div>
          <label class="text-sm font-medium text-gray-600">Họ tên</label>
          <input v-model="user.fullName"
            class="w-full mt-1 px-3 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-green-400 outline-none"
          />
        </div>

        <div>
          <label class="text-sm font-medium text-gray-600">Số điện thoại</label>
          <input v-model="user.phone"
            class="w-full mt-1 px-3 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-green-400 outline-none"
          />
        </div>

        <div>
          <label class="text-sm font-medium text-gray-600">Email</label>
          <input v-model="user.email"
            class="w-full mt-1 px-3 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-green-400 outline-none"
          />
        </div>

        <!-- BANK -->
        <div>
          <label class="text-sm font-medium text-gray-600">Ngân hàng</label>
          <select
            v-model="selectedBank"
            @change="handleBankChange"
            class="w-full mt-1 px-3 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-green-400 outline-none"
          >
            <option value="">-- Chọn ngân hàng --</option>
            <option v-for="b in banks" :key="b.bin" :value="b">
              {{ b.shortName }}
            </option>
          </select>
        </div>

        <div>
          <label class="text-sm font-medium text-gray-600">Mã ngân hàng</label>
          <input v-model="user.bankCode" readonly
            class="w-full mt-1 px-3 py-2 border border-gray-200 rounded-lg bg-gray-50 text-gray-500"
          />
        </div>

        <div>
          <label class="text-sm font-medium text-gray-600">Số tài khoản</label>
          <input v-model="user.bankAccount"
            class="w-full mt-1 px-3 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-green-400 outline-none"
            placeholder="VD: 0353260919"
          />
        </div>

      </div>
      <div class="space-y-3 border-t pt-4">
        <h3 class="font-semibold text-gray-700">
          Thông tin CCCD
        </h3>

        <div>
          <label class="text-sm font-medium text-gray-600">
            Số CCCD
          </label>
          <input
            v-model="user.cccdNumber"
            class="w-full mt-1 px-3 py-2 border border-gray-200 rounded-lg"
          />
        </div>
        <div>
          <label class="text-sm font-medium text-gray-600">
            Ngày sinh
          </label>

          <input
            type="date"
            v-model="user.dateOfBirth"
            class="w-full mt-1 px-3 py-2 border border-gray-200 rounded-lg"
          />
        </div>
        <div class="grid grid-cols-2 gap-4">

          <!-- Mặt trước -->
          <div>
            <p class="text-xs font-semibold text-gray-500 mb-2">
              Mặt trước CCCD
            </p>

            <div class="relative">
              <label
                class="relative flex items-center justify-center h-36 border-2 border-dashed rounded-xl cursor-pointer overflow-hidden"
              >
                <input
                  type="file"
                  class="hidden"
                  accept="image/*"
                  @change="uploadCCCD($event,'front')"
                />

                <img
                  v-if="user.cccdFrontImage"
                  :src="user.cccdFrontImage"
                  class="absolute inset-0 w-full h-full object-cover"
                />

                <span
                  v-else
                  class="text-gray-400"
                >
                  📷 Tải ảnh
                </span>
              </label>

              <button
                v-if="user.cccdFrontImage"
                type="button"
                @click="removeCCCDImage('front')"
                class="absolute top-2 right-2 w-7 h-7 rounded-full bg-red-500 text-white font-bold hover:bg-red-600"
              >
                ✕
              </button>
            </div>
          </div>

          <!-- Mặt sau -->
          <div>
            <p class="text-xs font-semibold text-gray-500 mb-2">
              Mặt sau CCCD
            </p>

            <div class="relative">
              <label
                class="relative flex items-center justify-center h-36 border-2 border-dashed rounded-xl cursor-pointer overflow-hidden"
              >
                <input
                  type="file"
                  class="hidden"
                  accept="image/*"
                  @change="uploadCCCD($event,'back')"
                />

                <img
                  v-if="user.cccdBackImage"
                  :src="user.cccdBackImage"
                  class="absolute inset-0 w-full h-full object-cover"
                />

                <span
                  v-else
                  class="text-gray-400"
                >
                  📷 Tải ảnh
                </span>
              </label>

              <button
                v-if="user.cccdBackImage"
                type="button"
                @click="removeCCCDImage('back')"
                class="absolute top-2 right-2 w-7 h-7 rounded-full bg-red-500 text-white font-bold hover:bg-red-600"
              >
                ✕
              </button>
            </div>
          </div>

        </div>
      </div>
      <!-- BUTTON -->
      <button
        @click="updateProfile"
        :disabled="loading"
        class="w-full py-2 bg-[#4CAF50] text-white rounded-lg font-semibold hover:bg-[#43A047] transition"
      >
        <span v-if="loading">Đang cập nhật...</span>
        <span v-else>Cập nhật</span>
      </button>

    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import api from "@/api"
import { useToast } from "vue-toastification"

const toast = useToast()

const user = ref({
  fullName: "",
  phone: "",
  email: "",
  avatar: "",

  bankName: "",
  bankCode: "",
  bankAccount: "",

  cccdNumber: "",
  cccdFrontImage: "",
  cccdBackImage: "",
  cccdIssueDate: "",
  cccdIssuePlace: "",
  dateOfBirth: ""
})

const banks = ref([])
const selectedBank = ref(null)
const loading = ref(false)

const convertDate = (dateStr) => {
  if (!dateStr) return ""

  const parts = dateStr.split("/")

  if (parts.length !== 3) return ""

  return `${parts[2]}-${parts[1]}-${parts[0]}`
}
// ================= BANK =================
const fetchBanks = async () => {
  try {
    const res = await fetch("https://api.vietqr.io/v2/banks")
    const data = await res.json()
    banks.value = data.data
  } catch (err) {
    toast.error("Không tải được danh sách ngân hàng")
  }
}

const handleBankChange = () => {
  if (!selectedBank.value) return
  user.value.bankName = selectedBank.value.shortName
  user.value.bankCode = selectedBank.value.bin
}

// ================= AVATAR =================
const handleAvatar = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  try {
    const formData = new FormData()
    formData.append("file", file)

    const res = await api.post("/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" }
    })

    user.value.avatar = res.data
    toast.success("Upload avatar thành công")

  } catch (err) {
    toast.error("Upload avatar thất bại")
  }
}

// ================= PROFILE =================
const fetchProfile = async () => {
  try {
    const res = await api.get("/users/profile")
    user.value = res.data

    selectedBank.value = banks.value.find(
      b => b.bin === user.value.bankCode
    )
  } catch (err) {
    toast.error("Không tải được thông tin user")
  }
}

const removeCCCDImage = (type) => {
  if (type === "front") {
    user.value.cccdFrontImage = ""

    // xoá dữ liệu OCR
    user.value.cccdNumber = ""
    user.value.dateOfBirth = ""
  } else {
    user.value.cccdBackImage = ""
  }
}
const uploadCCCD = async (event, type) => {
  const file = event.target.files[0]
  if (!file) return

  try {
    // upload ảnh
    const formData = new FormData()
    formData.append("file", file)

    const uploadRes = await api.post("/upload", formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    })

    if (type === "front") {
      user.value.cccdFrontImage = uploadRes.data

      // OCR
      const ocrData = new FormData()
      ocrData.append("file", file)

      const ocrRes = await api.post("/ocr", ocrData, {
        headers: {
          "Content-Type": "multipart/form-data"
        }
      })

      const data = ocrRes.data

      if (data.name)
        user.value.fullName = data.name

      if (data.id)
        user.value.cccdNumber = data.id
      if (data.dob)
        user.value.dateOfBirth = convertDate(data.dob)
      // if (data.issueDate)
      //   user.value.cccdIssueDate = convertDate(data.issueDate)

      // if (data.issuePlace)
      //   user.value.cccdIssuePlace = data.issuePlace
      toast.success("Đã quét CCCD")
    } else {
      user.value.cccdBackImage = uploadRes.data
    }

  } catch (err) {
    toast.error("OCR thất bại")
  }
}
// ================= UPDATE =================
const updateProfile = async () => {
  try {
    loading.value = true

    const res = await api.put("/users/profile", user.value)

    user.value = res.data
    localStorage.setItem("user", JSON.stringify(res.data))
    window.dispatchEvent(new Event("userUpdated"))

    toast.success("Cập nhật thành công 🎉")

  } catch (err) {
    toast.error(err.response?.data?.message || "Lỗi cập nhật")
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await fetchBanks()
  await fetchProfile()
})
</script>
