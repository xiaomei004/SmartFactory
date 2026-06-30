<script setup lang="ts">
import type { StatusDef } from '@/types/enums'

defineProps<{
  /** 状态值 */
  status: number
  /** 状态映射表 */
  map: Record<number, StatusDef>
}>()
</script>

<template>
  <el-tag
    :type="map[status]?.type || 'info'"
    size="small"
    effect="dark"
  >
    {{ map[status]?.label || `未知(${status})` }}
  </el-tag>
</template>
