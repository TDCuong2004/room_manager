import axios from "axios"
import router from "@/router"

const api = axios.create({
  baseURL: "http://localhost:3000/api"
})

api.interceptors.request.use(config => {

  const token = localStorage.getItem("token")

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

// 🔥 Bắt JWT hết hạn
api.interceptors.response.use(
  response => response,
  error => {

    const status = error.response?.status

    if (status === 401 || status === 403) {

      localStorage.clear()

      window.dispatchEvent(
        new Event("userUpdated")
      )

      router.push("/")
    }

    return Promise.reject(error)
  }
)

export default api