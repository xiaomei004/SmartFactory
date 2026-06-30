<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useAppStore } from '@/stores/app'

const router = useRouter()
const authStore = useAuthStore()
const appStore = useAppStore()

const ROLE_NAMES: Record<number, string> = {
  1: '系统管理员',
  2: '订单管理员',
  3: '计划管理员',
  4: '调度管理员',
  5: '生产员工',
}

const roleName = computed(() => ROLE_NAMES[authStore.roleId] || '未知角色')

async function handleLogout() {
  await authStore.logout()
  router.push('/login')
}
</script>

<template>
  <header class="header-bar">
    <div class="header-left">
      <!-- 折叠按钮 -->
      <el-button
        text
        @click="appStore.toggleSidebar()"
        class="collapse-btn"
      >
        <el-icon :size="20">
          <Fold v-if="!appStore.sidebarCollapsed" />
          <Expand v-else />
        </el-icon>
      </el-button>

      <!-- 面包屑 -->
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="$route.meta.title && $route.path !== '/dashboard'">
          {{ $route.meta.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="header-right">
      <!-- 工厂信息 -->
      <el-tag type="info" size="small" class="factory-tag">
        工厂 #{{ authStore.factoryId }}
      </el-tag>

      <!-- 用户下拉 -->
      <el-dropdown trigger="click">
        <span class="user-info">
          <el-icon :size="18"><UserFilled /></el-icon>
          <span class="user-name">{{ authStore.displayName }}</span>
          <el-icon :size="14"><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item disabled>
              <span class="dropdown-label">用户名：{{ authStore.userName || '-' }}</span>
            </el-dropdown-item>
            <el-dropdown-item disabled>
              <span class="dropdown-label">角色：{{ roleName }}</span>
            </el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">
              <span style="color: #ff4d4f">退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<style scoped>
.header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 20px;
  background: var(--header-bg);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.collapse-btn {
  padding: 4px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.factory-tag {
  font-size: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #333;
  font-size: 14px;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background 0.2s;
}

.user-info:hover {
  background: #f5f7fa;
}

.user-name {
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-label {
  color: #909399;
  font-size: 12px;
}
</style>
