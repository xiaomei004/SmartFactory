<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getOrderList, addOrder, acceptOrder, rejectOrder, completeOrder, deleteOrder } from '@/api/order'
import { addPlan } from '@/api/plan'
import { getProductList } from '@/api/product'
import { usePagination } from '@/composables/usePagination'
import { useFormModal } from '@/composables/useFormModal'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ORDER_STATUS } from '@/types/enums'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { ProductOrder, Product } from '@/types/entities'
import PageHeader from '@/components/common/PageHeader.vue'
import PaginationTable from '@/components/common/PaginationTable.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import FormModal from '@/components/common/FormModal.vue'
import { formatDateTime } from '@/utils/format'
import type { FormInstance, FormItemRule } from 'element-plus'
import { required, requiredSelect, positiveInteger } from '@/utils/validate'

const router = useRouter()
const authStore = useAuthStore()
const { pageNum, pageSize, total, handlePageChange, handleSizeChange, resetPage } = usePagination()
const { visible, editingId, formData, openCreate, close } = useFormModal<any>()

const formRef = ref<FormInstance>()
const formRules: Record<string, FormItemRule[]> = {
  orderSeq: [required('订单号'), { pattern: /^(O|LG)/, message: '订单号需以O或LG开头', trigger: 'blur' }],
  productId: [requiredSelect('产品')],
  productCount: [required('数量'), positiveInteger],
}

const list = ref<ProductOrder[]>([])
const loading = ref(false)
const submitting = ref(false)
const searchSeq = ref('')
const products = ref<Product[]>([])

// 转计划弹窗
const planVisible = ref(false)
const planSubmitting = ref(false)
const currentOrder = ref<ProductOrder | null>(null)
const planForm = reactive({
  planSeq: '',
  planStartDate: '',
  planEndDate: '',
})

async function fetchList() {
  loading.value = true
  try {
    const res = await getOrderList({
      pageNum: pageNum.value, pageSize: pageSize.value,
      factoryId: authStore.factoryId,
      orderSeq: searchSeq.value || undefined,
    })
    list.value = res.data.dataList
    total.value = res.data.pageInfo.total
  } finally { loading.value = false }
}

async function fetchProducts() {
  const res = await getProductList({ pageNum: 1, pageSize: 999, factoryId: authStore.factoryId })
  products.value = res.data.dataList
}

function getProductName(id: number) {
  return products.value.find(p => p.id === id)?.productName || `产品#${id}`
}

function handleCreate() {
  openCreate({ factoryId: authStore.factoryId })
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const d = formData.value
  submitting.value = true
  try {
    await addOrder({
      orderSeq: d.orderSeq, productId: d.productId,
      productCount: d.productCount, endDate: d.endDate || '',
      factoryId: authStore.factoryId,
    })
    ElMessage.success('下单成功')
    close()
    fetchList()
  } finally { submitting.value = false }
}

async function handleAccept(id: number) {
  try {
    await acceptOrder(id)
    ElMessage.success('已接单')
    fetchList()
  } catch { /* error handled by interceptor */ }
}

async function handleReject(row: ProductOrder) {
  try {
    const { value: remark } = await ElMessageBox.prompt(
      '请输入拒绝原因',
      '拒单确认',
      {
        confirmButtonText: '确认拒单',
        cancelButtonText: '取消',
        type: 'warning',
        inputPlaceholder: '请填写拒绝备注...',
        inputValidator: (val) => val?.trim() ? true : '拒绝原因不能为空',
      },
    )
    await rejectOrder(row.id)
    ElMessage.success(`已拒单${remark ? `（原因：${remark}）` : ''}`)
    fetchList()
  } catch { /* cancelled */ }
}

// 转计划
function handleToPlan(row: ProductOrder) {
  currentOrder.value = row
  planForm.planSeq = `P-${row.orderSeq}`
  planForm.planStartDate = ''
  planForm.planEndDate = row.endDate || ''
  planVisible.value = true
}

async function handlePlanSubmit() {
  if (!planForm.planStartDate || !planForm.planEndDate) {
    ElMessage.warning('请填写计划起止日期')
    return
  }
  if (!currentOrder.value) return
  planSubmitting.value = true
  try {
    await addPlan({
      planSeq: planForm.planSeq || `P-${currentOrder.value.orderSeq}`,
      orderId: currentOrder.value.id,
      productId: currentOrder.value.productId,
      planCount: currentOrder.value.productCount,
      deliveryDate: currentOrder.value.endDate || '',
      planStartDate: planForm.planStartDate,
      planEndDate: planForm.planEndDate,
      factoryId: authStore.factoryId,
    })
    ElMessage.success('转计划成功，已生成生产计划')
    planVisible.value = false
    fetchList()
  } catch { /* error handled by interceptor */ }
  finally { planSubmitting.value = false }
}

async function handleComplete(id: number) {
  try {
    await ElMessageBox.confirm('确定完成该订单吗？将级联完成关联的计划和工单。', '确认完成', { type: 'warning' })
    await completeOrder(id)
    ElMessage.success('已完成')
    fetchList()
  } catch { /* cancelled */ }
}

async function handleDelete(row: ProductOrder) {
  try {
    await ElMessageBox.confirm(`确定删除订单「${row.orderSeq}」吗？`, '确认删除', { type: 'warning' })
    await deleteOrder(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch { /* cancelled */ }
}

function handleSearch() { resetPage(); fetchList() }

onMounted(() => { fetchList(); fetchProducts() })
</script>

<template>
  <div class="list-view">
    <PageHeader title="订单管理" @refresh="fetchList">
      <template #actions>
        <el-input v-model="searchSeq" placeholder="搜索订单号" clearable style="width: 200px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleCreate">新增订单</el-button>
      </template>
    </PageHeader>

    <PaginationTable
      :data="list" :loading="loading"
      :page-num="pageNum" :page-size="pageSize" :total="total"
      @page-change="(p: number) => { handlePageChange(p); fetchList() }"
      @size-change="(s: number) => { handleSizeChange(s); fetchList() }"
    >
      <el-table-column label="ID" width="70">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="router.push(`/orders/${row.id}`)">
            #{{ row.id }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column prop="orderSeq" label="订单号" width="150" />
      <el-table-column label="产品" min-width="140">
        <template #default="{ row }">{{ getProductName(row.productId) }}</template>
      </el-table-column>
      <el-table-column prop="productCount" label="数量" width="100" />
      <el-table-column label="可用产能" width="100">
        <template #default="{ row }">
          <span v-if="row.factoryYield">{{ row.factoryYield }}</span>
          <span v-else class="text-muted">-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <StatusTag :status="row.orderStatus" :map="ORDER_STATUS" />
        </template>
      </el-table-column>
      <el-table-column label="交期" width="120">
        <template #default="{ row }">{{ formatDateTime(row.endDate) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="320" fixed="right">
        <template #default="{ row }">
          <template v-if="row.orderStatus === 10">
            <el-button size="small" type="success" @click="handleAccept(row.id)">接单</el-button>
            <el-button size="small" type="danger" @click="handleReject(row)">拒单</el-button>
          </template>
          <template v-else-if="row.orderStatus === 20">
            <el-button size="small" type="primary" @click="handleToPlan(row)">转计划</el-button>
          </template>
          <template v-else-if="row.orderStatus === 40">
            <el-button size="small" type="success" @click="handleComplete(row.id)">完成</el-button>
          </template>
          <el-button size="small" type="danger" :disabled="row.orderStatus === 40" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </PaginationTable>

    <!-- 新增弹窗 -->
    <FormModal :visible="visible" mode="create" :submitting="submitting" @confirm="handleSubmit" @cancel="close">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="订单号" prop="orderSeq">
          <el-input v-model="formData.orderSeq" placeholder="如 ORD001 或 LG001" />
        </el-form-item>
        <el-form-item label="产品" prop="productId">
          <el-select v-model="formData.productId" placeholder="选择产品" style="width: 100%">
            <el-option v-for="p in products" :key="p.id" :label="`${p.productNum} — ${p.productName}`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="productCount">
          <el-input-number v-model="formData.productCount" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="交货日期">
          <el-date-picker v-model="formData.endDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
    </FormModal>

    <!-- 转计划弹窗 -->
    <el-dialog v-model="planVisible" title="转生产计划" width="520px" :close-on-click-modal="false" destroy-on-close>
      <el-descriptions v-if="currentOrder" :column="2" border size="small" style="margin-bottom: 20px">
        <el-descriptions-item label="订单号">{{ currentOrder.orderSeq }}</el-descriptions-item>
        <el-descriptions-item label="产品">{{ getProductName(currentOrder.productId) }}</el-descriptions-item>
        <el-descriptions-item label="订单数量">{{ currentOrder.productCount }} 件</el-descriptions-item>
        <el-descriptions-item label="交货日期">{{ formatDateTime(currentOrder.endDate)?.split(' ')[0] }}</el-descriptions-item>
      </el-descriptions>
      <el-form :model="planForm" label-width="100px">
        <el-form-item label="计划编号">
          <el-input v-model="planForm.planSeq" placeholder="自动生成" />
        </el-form-item>
        <el-form-item label="计划开始日期" required>
          <el-date-picker v-model="planForm.planStartDate" type="date" placeholder="选择开始日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="计划结束日期" required>
          <el-date-picker v-model="planForm.planEndDate" type="date" placeholder="选择结束日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="planVisible = false">取消</el-button>
        <el-button type="primary" :loading="planSubmitting" @click="handlePlanSubmit">确定转计划</el-button>
      </template>
    </el-dialog>
  </div>
</template>
