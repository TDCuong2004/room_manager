import { createRouter, createWebHistory } from 'vue-router'

import AdminLayout from '../layouts/AdminLayout.vue'
import Users from '../views/Users.vue'
import Login from '../views/AdminLogin.vue'
import Register from '../views/AdminRegister.vue'
import AdminPostManager from '../views/AdminPostManager.vue'

const routes = [
  {
    path: '/',
    redirect: '/admin/users'
  },

  // 🔐 AUTH
  {
    path: '/login',
    component: Login
  },
  {
    path: '/register',
    component: Register
  },

  // 🔐 ADMIN
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true },
    children: [
      { path: 'users', component: Users },
      { path: 'posts', component: AdminPostManager } // ✅ FIX Ở ĐÂY
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// GUARD
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth && !token) {
    return next('/login')
  }

  if ((to.path === '/login' || to.path === '/register') && token) {
    return next('/admin/users')
  }

  next()
})

export default router