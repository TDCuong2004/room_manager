import { createRouter, createWebHistory } from "vue-router"

// Import các page
import Home from "../views/Home.vue"
import Login from "../views/Login.vue"
import AddBuilding from "../Model/AddBuilding.vue"
import Register from "../views/Register.vue"
import BuildingManager from "../views/BuildingManager.vue"
import RoomManager from "../views/RoomManager.vue"
import ProfileUser from "../views/ProfileUser.vue"
import Dashboard from "../views/Dashboard.vue"
import ServicePage from "../views/ServicePage.vue"
import RentRoom from "@/views/RentRoom.vue"
import BuildingServices from "@/views/BuildingServices.vue"
import InvoicePage from "@/views/InvoicePage.vue"
import ContractPreview from "@/views/ContractPreview.vue"
const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
  },
  {
    path: "/login",
    name: "Login",
    component: Login,
  },
  {
    path: "/add-building",
    name: "Add-building",
    component: AddBuilding,
  },
  {
    path: "/register",
    name: "Register",
    component: Register
  },
  {
    path: "/buildingmanager",
    name: "BuildingManager",
    component: BuildingManager
  },
  {
    path: "/dashboard",
    name: "Dashboard",
    component: Dashboard
  },
  {
    path: "/servicepage",
    name: "ServicePage",
    component: ServicePage
  },
  {
    path: "/buildings/:id/rooms",
    name: "Rooms",
    component: RoomManager
  },
  {
    path: "/profile",
    name: "Profile",
    component: () => import("../views/ProfileUser.vue")
  },
  {
    path: "/building/:id/services",
    name: "BuildingServices",
    component: BuildingServices
  },
  {
    path: "/rent/:roomId",
    name: "rentRoom",
    component: RentRoom,
    props: true
  },
  {
    path: "/invoices",
    component: () => import("../views/InvoicePage.vue")
  },
  {
    path: "/contract-preview",
    component: ContractPreview
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  linkActiveClass: "",         
  linkExactActiveClass: "" 
})

export default router