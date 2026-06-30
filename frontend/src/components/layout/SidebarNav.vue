<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useAuthStore } from '@/stores/auth'
import { ROLE_PERMISSIONS } from '@/router'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const authStore = useAuthStore()

interface MenuItem {
  path: string
  title: string
  icon: string
  permissionKey: string
}

const allMenuItems: MenuItem[] = [
  { path: '/dashboard', title: '仪表盘', icon: 'Odometer', permissionKey: 'dashboard' },
  { path: '/products', title: '产品管理', icon: 'Goods', permissionKey: 'products' },
  { path: '/equipment', title: '设备管理', icon: 'Setting', permissionKey: 'equipment' },
  { path: '/orders', title: '订单管理', icon: 'Document', permissionKey: 'orders' },
  { path: '/plans', title: '生产计划', icon: 'Calendar', permissionKey: 'plans' },
  { path: '/schedules', title: '生产调度', icon: 'Timer', permissionKey: 'schedules' },
  { path: '/daily-works', title: '生产报工', icon: 'EditPen', permissionKey: 'dailyWorks' },
]

// 按角色过滤菜单
const menuItems = computed(() => {
  const role = authStore.roleId
  return allMenuItems.filter((item) => {
    const allowed = ROLE_PERMISSIONS[item.permissionKey]
    return allowed && allowed.includes(role)
  })
})

const activeMenu = computed(() => {
  const exact = menuItems.value.find((m) => m.path === route.path)
  if (exact) return exact.path
  return menuItems.value.find((m) => route.path.startsWith(m.path))?.path || '/dashboard'
})

function navigateTo(path: string) {
  router.push(path)
}
</script>

<template>
  <el-menu
    :default-active="activeMenu"
    :collapse="appStore.sidebarCollapsed"
    background-color="var(--sidebar-bg)"
    text-color="var(--sidebar-text)"
    active-text-color="#ffffff"
    router
    class="sidebar-menu"
  >
    <!-- Logo 区域 -->
    <div class="sidebar-logo">
      <el-icon :size="28" color="#2c5f8a">
        <Monitor />
      </el-icon>
      <span v-show="!appStore.sidebarCollapsed" class="logo-text">东软智造</span>
    </div>

    <el-menu-item
      v-for="item in menuItems"
      :key="item.path"
      :index="item.path"
      @click="navigateTo(item.path)"
    >
      <el-icon>
        <component :is="item.icon" />
      </el-icon>
      <template #title>{{ item.title }}</template>
    </el-menu-item>
  </el-menu>
</template>

<style scoped>
.sidebar-menu {
  height: 100%;
  border-right: none;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 220px;
}

.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 20px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 8px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #c8d6e5;
  white-space: nowrap;
  letter-spacing: 2px;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: var(--sidebar-active) !important;
}

.sidebar-menu .el-menu-item:hover {
  background-color: rgba(44, 95, 138, 0.4) !important;
}
</style>
