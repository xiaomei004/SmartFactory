/**
 * 表单弹窗组合式函数
 */
import { ref } from 'vue'

export function useFormModal<T = Record<string, any>>() {
  const visible = ref(false)
  const editingId = ref<number | null>(null)
  const formData = ref<T>({} as T)

  /** 打开新增 */
  function openCreate(defaultData?: Partial<T>) {
    editingId.value = null
    formData.value = (defaultData || {}) as T
    visible.value = true
  }

  /** 打开编辑 */
  function openEdit(id: number, data: T) {
    editingId.value = id
    formData.value = { ...data }
    visible.value = true
  }

  /** 关闭 */
  function close() {
    visible.value = false
    editingId.value = null
    formData.value = {} as T
  }

  return {
    visible,
    editingId,
    formData,
    openCreate,
    openEdit,
    close,
  }
}
