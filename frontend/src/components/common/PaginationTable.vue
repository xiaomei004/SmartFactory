<script setup lang="ts" generic="T extends Record<string, any>">
defineProps<{
  /** 表格数据 */
  data: T[]
  /** 是否加载中 */
  loading: boolean
  /** 当前页 */
  pageNum: number
  /** 每页条数 */
  pageSize: number
  /** 总条数 */
  total: number
  /** 是否显示分页 */
  showPagination?: boolean
}>()

defineEmits<{
  (e: 'page-change', page: number): void
  (e: 'size-change', size: number): void
}>()

defineSlots<{
  default(): any
}>()
</script>

<template>
  <div class="pagination-table">
    <el-table
      :data="data"
      v-loading="loading"
      stripe
      border
      style="width: 100%"
      :header-cell-style="{
        background: '#f5f7fa',
        color: '#606266',
        fontWeight: 600,
        fontSize: '13px',
      }"
      :cell-style="{ fontSize: '13px' }"
      row-key="id"
      highlight-current-row
    >
      <slot />
    </el-table>

    <div v-if="showPagination !== false" class="pagination-wrap">
      <el-pagination
        :current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @current-change="(p: number) => $emit('page-change', p)"
        @size-change="(s: number) => $emit('size-change', s)"
      />
    </div>
  </div>
</template>

<style scoped>
.pagination-table {
  background: var(--card-bg);
  border-radius: 8px;
  padding: 16px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
