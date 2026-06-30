<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getEquipmentList, addEquipment, editEquipment, deleteEquipment, getEquipmentProducts, setEquipmentProducts } from '@/api/equipment'
import { getProductList } from '@/api/product'
import { usePagination } from '@/composables/usePagination'
import { useFormModal } from '@/composables/useFormModal'
import { useAuthStore } from '@/stores/auth'
import { EQUIPMENT_STATUS } from '@/types/enums'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { Equipment, Product } from '@/types/entities'
import type { UploadFile, FormInstance, FormItemRule } from 'element-plus'
import { required, maxLength } from '@/utils/validate'
import PageHeader from '@/components/common/PageHeader.vue'
import PaginationTable from '@/components/common/PaginationTable.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import FormModal from '@/components/common/FormModal.vue'

const authStore = useAuthStore()
const { pageNum, pageSize, total, handlePageChange, handleSizeChange, resetPage } = usePagination()
const { visible, editingId, formData, openCreate, openEdit, close } = useFormModal<any>()

const formRef = ref<FormInstance>()
const formRules: Record<string, FormItemRule[]> = {
  equipmentSeq: [required('设备序列号'), maxLength(30)],
  equipmentName: [required('设备名称'), maxLength(50)],
}

const list = ref<Equipment[]>([])
const loading = ref(false)
const submitting = ref(false)
const searchName = ref('')
const searchProductName = ref('')

// 产品列表（全局加载）
const allProducts = ref<Product[]>([])
// 当前选中关联的产品ID列表
const selectedProductIds = ref<number[]>([])
// 图片预览
const imagePreviewUrl = ref('')

function handleImageChange(file: UploadFile) {
  const reader = new FileReader()
  reader.onload = (e) => {
    const base64 = e.target?.result as string
    imagePreviewUrl.value = base64
    formData.value.equipmentImgUrl = base64
  }
  reader.readAsDataURL(file.raw!)
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getEquipmentList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      factoryId: authStore.factoryId,
      equipmentName: searchName.value || undefined,
      productName: searchProductName.value || undefined,
    })
    list.value = res.data.dataList
    total.value = res.data.pageInfo.total
  } finally {
    loading.value = false
  }
}

async function fetchProducts() {
  const res = await getProductList({ pageNum: 1, pageSize: 999, factoryId: authStore.factoryId })
  allProducts.value = res.data.dataList
}

// 新增设备（默认状态=启用）
function handleCreate() {
  selectedProductIds.value = []
  openCreate({ equipmentStatus: 10, factoryId: authStore.factoryId })
}

// 编辑设备（加载已关联产品）
async function handleEdit(row: Equipment) {
  openEdit(row.id, { ...row })
  try {
    const res = await getEquipmentProducts(row.id)
    selectedProductIds.value = (res.data || []).map((p: any) => p.productId)
  } catch {
    selectedProductIds.value = []
  }
}

async function handleDelete(row: Equipment) {
  try {
    await ElMessageBox.confirm(`确定删除设备「${row.equipmentName}」吗？`, '确认删除', { type: 'warning' })
    await deleteEquipment(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch { /* cancelled */ }
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const d = formData.value
  submitting.value = true
  try {
    if (editingId.value) {
      // 编辑模式
      await editEquipment({ id: editingId.value, equipmentSeq: d.equipmentSeq, equipmentName: d.equipmentName, equipmentStatus: d.equipmentStatus })
      await setEquipmentProducts(editingId.value, selectedProductIds.value)
      ElMessage.success('修改成功')
    } else {
      // 新增模式：先创建设备，再从列表中匹配以设置产品关联
      await addEquipment({ equipmentSeq: d.equipmentSeq, equipmentName: d.equipmentName, equipmentStatus: d.equipmentStatus, factoryId: authStore.factoryId })
      ElMessage.success('新增成功')
      // 刷新列表查找新建设备ID
      if (selectedProductIds.value.length > 0) {
        const res = await getEquipmentList({ pageNum: 1, pageSize: 99, factoryId: authStore.factoryId, equipmentName: d.equipmentName })
        const created = res.data.dataList.find(e => e.equipmentSeq === d.equipmentSeq)
        if (created) {
          await setEquipmentProducts(created.id, selectedProductIds.value)
        }
      }
    }
    close()
    fetchList()
  } finally {
    submitting.value = false
  }
}

// 便捷关联产品弹窗（列表中的"产品"按钮 — 保留原功能）
const productVisible = ref(false)
const currentEquipId = ref(0)
const linkedProductIds = ref<number[]>([])
const productLoading = ref(false)

async function openProducts(row: Equipment) {
  currentEquipId.value = row.id
  productVisible.value = true
  productLoading.value = true
  try {
    const linkedRes = await getEquipmentProducts(row.id)
    linkedProductIds.value = (linkedRes.data || []).map((p: any) => p.productId)
  } finally {
    productLoading.value = false
  }
}

async function handleSetProducts() {
  productLoading.value = true
  try {
    await setEquipmentProducts(currentEquipId.value, linkedProductIds.value)
    ElMessage.success('关联产品已保存')
    productVisible.value = false
  } finally {
    productLoading.value = false
  }
}

function handleSearch() {
  resetPage()
  fetchList()
}

onMounted(() => { fetchList(); fetchProducts() })
</script>

<template>
  <div class="list-view">
    <PageHeader title="设备管理" @refresh="fetchList">
      <template #actions>
        <el-input v-model="searchName" placeholder="搜索设备名称" clearable style="width: 180px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-input v-model="searchProductName" placeholder="搜索产品名称" clearable style="width: 180px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleCreate">新增设备</el-button>
      </template>
    </PageHeader>

    <PaginationTable
      :data="list" :loading="loading"
      :page-num="pageNum" :page-size="pageSize" :total="total"
      @page-change="(p: number) => { handlePageChange(p); fetchList() }"
      @size-change="(s: number) => { handleSizeChange(s); fetchList() }"
    >
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="equipmentSeq" label="设备序列号" width="140" />
      <el-table-column prop="equipmentName" label="设备名称" min-width="160" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <StatusTag :status="row.equipmentStatus" :map="EQUIPMENT_STATUS" />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openProducts(row)">产品</el-button>
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </PaginationTable>

    <!-- 新增/编辑弹窗（含产品关联） -->
    <FormModal :visible="visible" :mode="editingId ? 'edit' : 'create'" :submitting="submitting" width="640px" @confirm="handleSubmit" @cancel="close">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
        <el-form-item label="设备序列号" prop="equipmentSeq">
          <el-input v-model="formData.equipmentSeq" placeholder="如 EQ001" />
        </el-form-item>
        <el-form-item label="设备名称" prop="equipmentName">
          <el-input v-model="formData.equipmentName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="formData.equipmentStatus" style="width: 100%">
            <el-option v-for="(v, k) in EQUIPMENT_STATUS" :key="k" :label="v.label" :value="Number(k)" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备图片">
          <div style="display: flex; flex-direction: column; gap: 8px">
            <el-input v-model="formData.equipmentImgUrl" placeholder="输入图片URL或上传文件" />
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
              v-if="imagePreviewUrl || formData.equipmentImgUrl"
              :src="imagePreviewUrl || formData.equipmentImgUrl"
              alt="预览"
              style="max-width: 200px; max-height: 150px; border-radius: 6px; border: 1px solid #ebeef5; object-fit: cover"
            />
          </div>
        </el-form-item>
        <el-divider content-position="left" style="margin: 16px 0 12px">
          <span style="font-size: 13px; color: #909399">可生产产品</span>
        </el-divider>
        <el-form-item label="">
          <el-checkbox-group v-model="selectedProductIds">
            <div v-for="p in allProducts" :key="p.id" style="margin-bottom: 6px">
              <el-checkbox :value="p.id" :label="p.id">
                {{ p.productNum }} — {{ p.productName }}
              </el-checkbox>
            </div>
          </el-checkbox-group>
          <div v-if="allProducts.length === 0" style="color: #c0c4cc; font-size: 13px">
            暂无产品，请先在"产品管理"中添加产品
          </div>
        </el-form-item>
      </el-form>
    </FormModal>

    <!-- 便捷关联产品弹窗（保留原有功能） -->
    <el-dialog v-model="productVisible" title="关联产品" width="500px" :close-on-click-modal="false">
      <el-checkbox-group v-model="linkedProductIds" v-loading="productLoading">
        <div v-for="p in allProducts" :key="p.id" style="margin-bottom: 8px">
          <el-checkbox :value="p.id" :label="p.id">
            {{ p.productNum }} — {{ p.productName }}
          </el-checkbox>
        </div>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="productVisible = false">取消</el-button>
        <el-button type="primary" :loading="productLoading" @click="handleSetProducts">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
