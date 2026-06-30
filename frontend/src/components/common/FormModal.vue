<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  visible: boolean
  /** 'create' | 'edit' */
  mode: 'create' | 'edit'
  /** 弹窗标题（自动根据 mode 生成，也可覆盖） */
  title?: string
  /** 是否提交中 */
  submitting?: boolean
  /** 弹窗宽度 */
  width?: string
}>()

const emits = defineEmits<{
  (e: 'confirm'): void
  (e: 'cancel'): void
}>()

const dialogTitle = computed(() => {
  if (props.title) return props.title
  return props.mode === 'create' ? '新增' : '编辑'
})

defineSlots<{
  default(): any
}>()
</script>

<template>
  <el-dialog
    :model-value="visible"
    :title="dialogTitle"
    :width="width || '560px'"
    :close-on-click-modal="false"
    destroy-on-close
    @close="$emit('cancel')"
  >
    <slot />

    <template #footer>
      <el-button @click="$emit('cancel')">取消</el-button>
      <el-button
        type="primary"
        :loading="submitting"
        @click="$emit('confirm')"
      >
        确定
      </el-button>
    </template>
  </el-dialog>
</template>
