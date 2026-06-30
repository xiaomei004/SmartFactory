/**
 * 分页组合式函数
 */
import { ref, reactive } from 'vue'

export function usePagination(defaultPageSize = 10) {
  const pageNum = ref(1)
  const pageSize = ref(defaultPageSize)
  const total = ref(0)

  /** 页码/页大小变化 */
  function handlePageChange(page: number) {
    pageNum.value = page
  }

  function handleSizeChange(size: number) {
    pageSize.value = size
    pageNum.value = 1
  }

  /** 重置到第一页 */
  function resetPage() {
    pageNum.value = 1
  }

  return {
    pageNum,
    pageSize,
    total,
    handlePageChange,
    handleSizeChange,
    resetPage,
  }
}
