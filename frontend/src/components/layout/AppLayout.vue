<script setup lang="ts">
import SidebarNav from './SidebarNav.vue'
import HeaderBar from './HeaderBar.vue'
import { useAppStore } from '@/stores/app'

const appStore = useAppStore()
</script>

<template>
  <div class="app-layout">
    <!-- 侧边栏 -->
    <aside
      class="app-sidebar"
      :class="{ collapsed: appStore.sidebarCollapsed }"
    >
      <SidebarNav />
    </aside>

    <!-- 右侧主体 -->
    <div class="app-main">
      <HeaderBar />
      <main class="app-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.app-sidebar {
  flex-shrink: 0;
  transition: width 0.3s;
  overflow: hidden;
}

.app-sidebar:not(.collapsed) {
  width: 220px;
}

.app-sidebar.collapsed {
  width: 64px;
}

.app-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.app-content {
  flex: 1;
  background: var(--content-bg);
  padding: 20px;
  overflow: auto;
}
</style>
