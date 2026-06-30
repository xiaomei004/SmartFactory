<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getProductList, addProduct, editProduct, deleteProduct } from '@/api/product'
import { usePagination } from '@/composables/usePagination'
import { useFormModal } from '@/composables/useFormModal'
import { useAuthStore } from '@/stores/auth'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Product } from '@/types/entities'
import type { UploadFile, FormInstance, FormItemRule } from 'element-plus'
import { required, maxLength } from '@/utils/validate'
import PageHeader from '@/components/common/PageHeader.vue'
import PaginationTable from '@/components/common/PaginationTable.vue'
import FormModal from '@/components/common/FormModal.vue'

const authStore = useAuthStore()
const { pageNum, pageSize, total, handlePageChange, handleSizeChange, resetPage } = usePagination()
const { visible, editingId, formData, openCreate, openEdit, close } = useFormModal<Product & { productImgUrl?: string }>()

const formRef = ref<FormInstance>()
const list = ref<Product[]>([])
const loading = ref(false)
const submitting = ref(false)
const searchName = ref('')

const formRules: Record<string, FormItemRule[]> = {
  productNum: [required('产品编号'), maxLength(30)],
  productName: [required('产品名称'), maxLength(50)],
}

const columns = [
  { prop: 'id', label: 'ID', width: 70 },
  { prop: 'productNum', label: '产品编号', width: 140 },
  { prop: 'productName', label: '产品名称', minWidth: 160 },
  { prop: 'createTime', label: '创建时间', width: 170 },
]

async function fetchList() {
  loading.value = true
  try {
    const res = await getProductList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      factoryId: authStore.factoryId,
      productName: searchName.value || undefined,
    })
    list.value = res.data.dataList
    total.value = res.data.pageInfo.total
  } finally {
    loading.value = false
  }
}

async function handleCreate() {
  openCreate({ factoryId: authStore.factoryId } as any)
}

function handleEdit(row: Product) {
  openEdit(row.id, { ...row })
}

async function handleDelete(row: Product) {
  try {
    await ElMessageBox.confirm(`确定删除产品「${row.productName}」吗？`, '确认删除', {
      type: 'warning',
    })
    await deleteProduct(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch { /* cancelled */ }
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const d = formData.value as any
  submitting.value = true
  try {
    if (editingId.value) {
      await editProduct({ id: editingId.value, productNum: d.productNum, productName: d.productName })
      ElMessage.success('修改成功')
    } else {
      await addProduct({ productNum: d.productNum, productName: d.productName, factoryId: authStore.factoryId })
      ElMessage.success('新增成功')
    }
    close()
    fetchList()
  } finally {
    submitting.value = false
  }
}

// 图片上传
const imagePreviewUrl = ref('')

function handleImageChange(file: UploadFile) {
  const reader = new FileReader()
  reader.onload = (e) => {
    const base64 = e.target?.result as string
    imagePreviewUrl.value = base64
    ;(formData.value as any).productImgUrl = base64
  }
  reader.readAsDataURL(file.raw!)
}

function handleSearch() {
  resetPage()
  fetchList()
}

onMounted(fetchList)
</script>

<template>
  <div class="list-view">
    <PageHeader title="产品管理" @refresh="fetchList">
      <template #actions>
        <el-input
          v-model="searchName"
          placeholder="搜索产品名称"
          clearable
          style="width: 200px"
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleCreate">新增产品</el-button>
      </template>
    </PageHeader>

    <PaginationTable
      :data="list"
      :loading="loading"
      :page-num="pageNum"
      :page-size="pageSize"
      :total="total"
      @page-change="(p: number) => { handlePageChange(p); fetchList() }"
      @size-change="(s: number) => { handleSizeChange(s); fetchList() }"
    >
      <el-table-column
        v-for="col in columns"
        :key="col.prop"
        :prop="col.prop"
        :label="col.label"
        :width="col.width"
        :min-width="col.minWidth"
      />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </PaginationTable>

    <!-- 新增/编辑弹窗 -->
    <FormModal
      :visible="visible"
      :mode="editingId ? 'edit' : 'create'"
      :submitting="submitting"
      @confirm="handleSubmit"
      @cancel="close"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="产品编号" prop="productNum">
          <el-input v-model="(formData as any).productNum" placeholder="如 CP001" />
        </el-form-item>
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="(formData as any).productName" placeholder="请输入产品名称" />
        </el-form-item>
        <el-form-item label="产品图片">
          <div style="display: flex; flex-direction: column; gap: 8px">
            <el-input v-model="(formData as any).productImgUrl" placeholder="输入图片URL或上传文件" />
            <el-upload
              :auto-upload="false"
              :show-file-list="false"
              accept="image/*"
              :on-change="handleImageChange"
              style="display: inline-block"
            >
              <el-button size="small" type="primary" plain>选择本地图片</el-button>
            </el-upload>
            <img
              v-if="imagePreviewUrl || (formData as any).productImgUrl"
              :src="imagePreviewUrl || (formData as any).productImgUrl"
              alt="预览"
              style="max-width: 200px; max-height: 150px; border-radius: 6px; border: 1px solid #ebeef5; object-fit: cover"
            />
          </div>
        </el-form-item>
      </el-form>
    </FormModal>
  </div>
</template>
