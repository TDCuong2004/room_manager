import { createRouter, createWebHistory } from "vue-router"

// ===== IMPORT LAYOUT =====
import UserLayout from "../layouts/UserLayout.vue"
import AdminLayout from "../layouts/AdminLayout.vue"

// ===== IMPORT PAGE =====
import Home from "../views/Home.vue"
import Login from "../views/Login.vue"
import Register from "../views/Register.vue"
import AddBuilding from "../Model/AddBuilding.vue"
import BuildingManager from "../views/BuildingManager.vue"
import RoomManager from "../views/RoomManager.vue"
import ProfileUser from "../views/ProfileUser.vue"
import Dashboard from "../views/Dashboard.vue"
import ServicePage from "../views/ServicePage.vue"
import RentRoom from "@/views/RentRoom.vue"
import BuildingServices from "@/views/BuildingServices.vue"
import InvoicePage from "@/views/InvoicePage.vue"
import ContractPreview from "@/views/ContractPreview.vue"
import RoomDetail from "@/views/RoomDetail.vue"
import Guide from "@/views/Guide.vue"

// ===== ADMIN =====
import AdminUsersManager from "../views/AdminUsers.vue"
import AdminPostManager from "../views/AdminPostManager.vue"

const routes = [
  // ================= AUTH =================
  {
    path: "/login",
    component: Login
  },
  {
    path: "/register",
    component: Register
  },

  // ================= USER =================
  {
    path: "/",
    component: UserLayout,
    children: [
      { path: "", component: Home },
      { path: "add-building", component: AddBuilding },
      { path: "buildingmanager", component: BuildingManager },
      { path: "dashboard", component: Dashboard },
      { path: "servicepage", component: ServicePage },
      { path: "buildings/:id/rooms", component: RoomManager },
      { path: "profile", component: ProfileUser },
      { path: "building/:id/services", component: BuildingServices },
      { path: "rent/:roomId", component: RentRoom, props: true },
      { path: "invoices", component: InvoicePage },
      { path: "contract-preview", component: ContractPreview },
      { path: "room/:id", component: RoomDetail },
      { path: "guide", component: Guide }
    ]
  },

  // ================= ADMIN =================
  {
    path: "/admin",
    component: AdminLayout,
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: "",
        redirect: "/admin/users"
      },
      {
        path: "users",
        component: AdminUsersManager
      },
      {
        path: "posts",
        component: AdminPostManager
      }
    ]
  }
]

// ===== CREATE ROUTER =====
const router = createRouter({
  history: createWebHistory(),
  routes
})

// ===== ROUTER GUARD =====
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token")
  const role = localStorage.getItem("role")

  // 🔐 cần login
  if (to.meta.requiresAuth && !token) {
    return next("/login")
  }

  // 🔐 chỉ admin
  if (to.meta.requiresAdmin && role !== "ADMIN") {
    return next("/")
  }

  // 🔁 đã login thì không vào login/register
  if ((to.path === "/login" || to.path === "/register") && token) {
    if (role === "ADMIN") {
      return next("/admin/users")
    }
    return next("/")
  }

  next()
})

export default router