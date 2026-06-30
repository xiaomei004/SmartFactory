<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPlanList, addPlan, startPlan, completePlan, deletePlan } from '@/api/plan'
import { getOrderList } from '@/api/order'
import { getProductList } from '@/api/product'
import { usePagination } from '@/composables/usePagination'
import { useFormModal } from '@/composables/useFormModal'
import { useAuthStore } from '@/stores/auth'
import { PLAN_STATUS } from '@/types/enums'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { ProductPlan, ProductOrder, Product } from '@/types/entities'
import PageHeader from '@/components/common/PageHeader.vue'
import PaginationTable from '@/components/common/PaginationTable.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import FormModal from '@/components/common/FormModal.vue'
import { formatDateTime } from '@/utils/format'
import type { FormInstance, FormItemRule } from 'element-plus'
import { required, requiredSelect, positiveInteger } from '@/utils/validate'

const authStore = useAuthStore()
const { pageNum, pageSize, total, handlePageChange, handleSizeChange, resetPage } = usePagination()
const { visible, editingId, formData, openCreate, close } = useFormModal<any>()

const formRef = ref<FormInstance>()
const formRules: Record<string, FormItemRule[]> = {
  planSeq: [required('计划号')],
  orderId: [requiredSelect('关联订单')],
  planCount: [required('计划数量'), positiveInteger],
}

const list = ref<ProductPlan[]>([])
const loading = ref(false)
const submitting = ref(false)
const searchSeq = ref('')
const orders = ref<ProductOrder[]>([])
const products = ref<Product[]>([])

async function fetchList() {
  loading.value = true
  try {
    const res = await getPlanList({
      pageNum: pageNum.value, pageSize: pageSize.value,
      factoryId: authStore.factoryId,
      planSeq: searchSeq.value || undefined,
    })
    list.value = res.data.dataList
    total.value = res.data.pageInfo.total
  } finally { loading.value = false }
}

async function fetchRefs() {
  const [oRes, pRes] = await Promise.all([
    getOrderList({ pageNum: 1, pageSize: 999, factoryId: authStore.factoryId }),
    getProductList({ pageNum: 1, pageSize: 999, factoryId: authStore.factoryId }),
  ])
  orders.value = oRes.data.dataList.filter(o => o.orderStatus === 20)
  products.value = pRes.data.dataList
}

function getProductName(id: number) {
  return products.value.find(p => p.id === id)?.productName || `产品#${id}`
}

function handleCreate() { openCreate({ factoryId: authStore.factoryId }) }

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const d = formData.value
  submitting.value = true
  try {
    await addPlan({
      planSeq: d.planSeq, orderId: d.orderId, productId: d.productId,
      planCount: d.planCount, deliveryDate: d.deliveryDate || '',
      planStartDate: d.planStartDate || '', planEndDate: d.planEndDate || '',
      factoryId: authStore.factoryId,
    })
    ElMessage.success('创建成功')
    close()
    fetchList()
  } finally { submitting.value = false }
}

async function handleStart(id: number) {
  try {
    await startPlan(id)
    ElMessage.success('已启动')
    fetchList()
  } catch { /* */ }
}

async function handleComplete(id: number) {
  try {
    await ElMessageBox.confirm('确定完成该计划吗？', '确认', { type: 'warning' })
    await completePlan(id)
    ElMessage.success('已完成')
    fetchList()
  } catch { /* */ }
}

async function handleDelete(row: ProductPlan) {
  try {
    await ElMessageBox.confirm(`确定删除计划「${row.planSeq}」吗？`, '确认删除', { type: 'warning' })
    await deletePlan(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch { /* */ }
}

function handleSearch() { resetPage(); fetchList() }

onMounted(() => { fetchList(); fetchRefs() })
</script>

<template>
  <div class="list-view">
    <PageHeader title="生产计划" @refresh="fetchList">
      <template #actions>
        <el-input v-model="searchSeq" placeholder="搜索计划号" clearable style="width: 200px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleCreate">新增计划</el-button>
      </template>
    </PageHeader>

    <PaginationTable
      :data="list" :loading="loading"
      :page-num="pageNum" :page-size="pageSize" :total="total"
      @page-change="(p: number) => { handlePageChange(p); fetchList() }"
      @size-change="(s: number) => { handleSizeChange(s); fetchList() }"
    >
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="planSeq" label="计划号" width="140" />
      <el-table-column label="产品" min-width="140">
        <template #default="{ row }">{{ getProductName(row.productId) }}</template>
      </el-table-column>
      <el-table-column prop="planCount" label="计划数量" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <StatusTag :status="row.planStatus" :map="PLAN_STATUS" />
        </template>
      </el-table-column>
      <el-table-column label="开始日期" width="120">
        <template #default="{ row }">{{ formatDateTime(row.planStartDate)?.split(' ')[0] }}</template>
      </el-table-column>
      <el-table-column label="结束日期" width="120">
        <template #default="{ row }">{{ formatDateTime(row.planEndDate)?.split(' ')[0] }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <template v-if="row.planStatus === 10">
            <el-button size="small" type="success" @click="handleStart(row.id)">启动</el-button>
          </template>
          <template v-else-if="row.planStatus === 20">
            <el-button size="small" type="primary" @click="handleComplete(row.id)">完成</el-button>
          </template>
          <el-button size="small" type="danger" :disabled="row.planStatus !== 10" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </PaginationTable>

    <!-- 新增弹窗 -->
    <FormModal :visible="visible" mode="create" :submitting="submitting" width="640px" @confirm="handleSubmit" @cancel="close">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
        <el-form-item label="计划号" prop="planSeq">
          <el-input v-model="formData.planSeq" placeholder="如 P001" />
        </el-form-item>
        <el-form-item label="关联订单" prop="orderId">
          <el-select v-model="formData.orderId" placeholder="选择已接单订单" style="width: 100%" @change="(val: number) => { const o = orders.find(x => x.id === val); if (o) { formData.productId = o.productId; formData.planCount = o.productCount; formData.deliveryDate = o.endDate } }">
            <el-option v-for="o in orders" :key="o.id" :label="`${o.orderSeq} (${o.productCount}件)`" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划数量" prop="planCount">
          <el-input-number v-model="formData.planCount" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="formData.planStartDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="formData.planEndDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
    </FormModal>
  </div>
</template>
