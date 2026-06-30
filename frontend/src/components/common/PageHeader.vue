<script setup lang="ts">
defineProps<{
  title: string
  description?: string
}>()

defineEmits<{
  (e: 'refresh'): void
}>()
</script>

<template>
  <div class="page-header">
    <div class="page-header-left">
      <h2 class="page-title">{{ title }}</h2>
      <p v-if="description" class="page-desc">{{ description }}</p>
    </div>
    <div class="page-header-right">
      <slot name="actions">
        <el-button @click="$emit('refresh')" :icon="'Refresh'">
          刷新
        </el-button>
      </slot>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.page-header-left {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a2332;
  margin: 0;
}

.page-desc {
  font-size: 13px;
  color: #909399;
  margin: 0;
}

.page-header-right {
  display: flex;
  gap: 8px;
}
</style>
