<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <div class="not-found-page">
    <div class="not-found-content">
      <h1>404</h1>
      <p>页面未找到</p>
      <el-button type="primary" @click="router.push('/dashboard')">
        返回首页
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.not-found-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: var(--content-bg);
}

.not-found-content {
  text-align: center;
}

.not-found-content h1 {
  font-size: 96px;
  font-weight: 700;
  color: var(--el-color-primary);
  margin: 0;
  line-height: 1.1;
}

.not-found-content p {
  font-size: 18px;
  color: #909399;
  margin: 12px 0 24px;
}
</style>
