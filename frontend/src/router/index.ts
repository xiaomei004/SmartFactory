import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import AppLayout from '@/components/layout/AppLayout.vue'

/**
 * 角色权限矩阵
 * 1=系统管理员 2=订单管理员 3=计划管理员 4=调度管理员 5=生产员工
 */
export const ROLE_PERMISSIONS: Record<string, number[]> = {
  dashboard: [1, 2, 3, 4, 5],   // 所有人可见
  products:  [1, 2],             // 管理员 + 订单管理员
  equipment: [1, 4],             // 管理员 + 调度管理员
  orders:    [1, 2],             // 管理员 + 订单管理员
  plans:     [1, 3],             // 管理员 + 计划管理员
  schedules: [1, 3, 4],          // 管理员 + 计划管理员 + 调度管理员
  dailyWorks:[1, 5],             // 管理员 + 生产员工
}

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { title: '登录', noAuth: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/RegisterView.vue'),
    meta: { title: '注册', noAuth: true },
  },
  {
    path: '/factory-register',
    name: 'FactoryRegister',
    component: () => import('@/views/auth/FactoryRegisterView.vue'),
    meta: { title: '工厂入驻', noAuth: true },
  },
  {
    path: '/',
    component: AppLayout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '仪表盘', roles: ROLE_PERMISSIONS.dashboard },
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('@/views/product/ProductListView.vue'),
        meta: { title: '产品管理', roles: ROLE_PERMISSIONS.products },
      },
      {
        path: 'equipment',
        name: 'Equipment',
        component: () => import('@/views/equipment/EquipmentListView.vue'),
        meta: { title: '设备管理', roles: ROLE_PERMISSIONS.equipment },
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/order/OrderListView.vue'),
        meta: { title: '订单管理', roles: ROLE_PERMISSIONS.orders },
      },
      {
        path: 'orders/:id',
        name: 'OrderDetail',
        component: () => import('@/views/order/OrderDetailView.vue'),
        meta: { title: '订单详情', roles: ROLE_PERMISSIONS.orders },
      },
      {
        path: 'plans',
        name: 'Plans',
        component: () => import('@/views/plan/PlanListView.vue'),
        meta: { title: '生产计划', roles: ROLE_PERMISSIONS.plans },
      },
      {
        path: 'schedules',
        name: 'Schedules',
        component: () => import('@/views/schedule/ScheduleListView.vue'),
        meta: { title: '生产调度', roles: ROLE_PERMISSIONS.schedules },
      },
      {
        path: 'daily-works',
        name: 'DailyWorks',
        component: () => import('@/views/dailyWork/DailyWorkListView.vue'),
        meta: { title: '生产报工', roles: ROLE_PERMISSIONS.dailyWorks },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/auth/NotFoundView.vue'),
    meta: { title: '404', noAuth: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 导航守卫：登录检查 + 角色权限
router.beforeEach((to, _from, next) => {
  document.title = (to.meta.title as string) || '东软智能制造云平台'

  const token = localStorage.getItem('token')
  const roleId = Number(localStorage.getItem('roleId')) || 0

  if (to.meta.noAuth) {
    // 无需认证页面
    if (to.path === '/login' && token) {
      next('/dashboard')
    } else {
      next()
    }
  } else if (!token) {
    // 未登录
    next('/login')
  } else {
    // 已登录 → 检查角色权限
    const allowedRoles = to.meta.roles as number[] | undefined
    if (allowedRoles && !allowedRoles.includes(roleId)) {
      // 无权限，跳转仪表盘
      next('/dashboard')
    } else {
      next()
    }
  }
})

export default router
