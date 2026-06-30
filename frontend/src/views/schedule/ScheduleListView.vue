<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getScheduleList, addSchedule, assignEquipment, startSchedule, completeSchedule, deleteSchedule } from '@/api/schedule'
import { getPlanList } from '@/api/plan'
import { getEquipmentList, getEquipmentProducts } from '@/api/equipment'
import { getProductList } from '@/api/product'
import { usePagination } from '@/composables/usePagination'
import { useFormModal } from '@/composables/useFormModal'
import { useAuthStore } from '@/stores/auth'
import { SCHEDULE_STATUS } from '@/types/enums'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { ProductSchedule, ProductPlan, Equipment, Product } from '@/types/entities'
import PageHeader from '@/components/common/PageHeader.vue'
import PaginationTable from '@/components/common/PaginationTable.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import FormModal from '@/components/common/FormModal.vue'
import { formatDateTime } from '@/utils/format'
import type { FormInstance, FormItemRule } from 'element-plus'
import { required, requiredSelect, positiveInteger } from '@/utils/validate'

const authStore = useAuthStore()
const { pageNum, pageSize, total, handlePageChange, handleSizeChange, resetPage } = usePagination()
const { visible, formData, openCreate, close } = useFormModal<any>()

const formRef = ref<FormInstance>()
const formRules: Record<string, FormItemRule[]> = {
  scheduleSeq: [required('工单号')],
  planId: [requiredSelect('关联计划')],
  scheduleCount: [required('数量'), positiveInteger],
}

const list = ref<ProductSchedule[]>([])
const loading = ref(false)
const submitting = ref(false)
const searchSeq = ref('')
const plans = ref<ProductPlan[]>([])
const equipments = ref<Equipment[]>([])
const products = ref<Product[]>([])

// 设备可生产产品映射：equipmentId → productIds
const equipmentProductMap = ref<Record<number, number[]>>({})

// 分配设备
const assignVisible = ref(false)
const assignScheduleId = ref(0)
const assignEquipmentId = ref<number | null>(null)
const scheduleProductId = ref(0)

// 仅列出可生产该产品的设备
const assignableEquipments = computed(() => {
  if (!scheduleProductId.value) return equipments.value
  return equipments.value.filter((e) => {
    const productIds = equipmentProductMap.value[e.id] || []
    // 未关联任何产品的设备也可选（首次分配）
    return productIds.length === 0 || productIds.includes(scheduleProductId.value)
  })
})

async function fetchList() {
  loading.value = true
  try {
    const res = await getScheduleList({
      pageNum: pageNum.value, pageSize: pageSize.value,
      factoryId: authStore.factoryId,
      scheduleSeq: searchSeq.value || undefined,
    })
    list.value = res.data.dataList
    total.value = res.data.pageInfo.total
  } finally { loading.value = false }
}

async function fetchRefs() {
  const [pRes, eRes, prRes] = await Promise.all([
    getPlanList({ pageNum: 1, pageSize: 999, factoryId: authStore.factoryId }),
    getEquipmentList({ pageNum: 1, pageSize: 999, factoryId: authStore.factoryId }),
    getProductList({ pageNum: 1, pageSize: 999, factoryId: authStore.factoryId }),
  ])
  plans.value = pRes.data.dataList.filter(p => p.planStatus === 20)
  equipments.value = eRes.data.dataList.filter(e => e.equipmentStatus === 10)
  products.value = prRes.data.dataList

  // 加载所有启用设备的可生产产品映射
  const enabledEquipments = equipments.value
  const map: Record<number, number[]> = {}
  await Promise.all(
    enabledEquipments.map(async (e) => {
      try {
        const res = await getEquipmentProducts(e.id)
        map[e.id] = (res.data || []).map((ep: any) => ep.productId)
      } catch {
        map[e.id] = []
      }
    }),
  )
  equipmentProductMap.value = map
}

function getProductName(id: number) {
  return products.value.find(p => p.id === id)?.productName || `产品#${id}`
}

function getEquipmentName(id: number) {
  return equipments.value.find(e => e.id === id)?.equipmentName || (id ? `设备#${id}` : '未分配')
}

function handleCreate() { openCreate({ factoryId: authStore.factoryId }) }

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const d = formData.value
  submitting.value = true
  try {
    await addSchedule({
      scheduleSeq: d.scheduleSeq, planId: d.planId, productId: d.productId,
      scheduleCount: d.scheduleCount, startDate: d.startDate || '', endDate: d.endDate || '',
      factoryId: authStore.factoryId,
    })
    ElMessage.success('创建成功')
    close()
    fetchList()
  } finally { submitting.value = false }
}

function openAssign(row: ProductSchedule) {
  assignScheduleId.value = row.id
  assignEquipmentId.value = row.equipmentId || null
  scheduleProductId.value = row.productId
  assignVisible.value = true
}

async function handleAssign() {
  if (!assignEquipmentId.value) { ElMessage.warning('请选择设备'); return }
  try {
    await assignEquipment(assignScheduleId.value, assignEquipmentId.value)
    ElMessage.success('设备分配成功')
    assignVisible.value = false
    fetchList()
  } catch { /* */ }
}

async function handleStart(id: number) {
  try { await startSchedule(id); ElMessage.success('已启动'); fetchList() } catch { /* */ }
}

async function handleComplete(id: number) {
  try {
    await ElMessageBox.confirm('确定完成该工单吗？', '确认', { type: 'warning' })
    await completeSchedule(id)
    ElMessage.success('已完成')
    fetchList()
  } catch { /* */ }
}

async function handleDelete(row: ProductSchedule) {
  try {
    await ElMessageBox.confirm(`确定删除工单「${row.scheduleSeq}」吗？`, '确认删除', { type: 'warning' })
    await deleteSchedule(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch { /* */ }
}

function handleSearch() { resetPage(); fetchList() }

onMounted(() => { fetchList(); fetchRefs() })
</script>

<template>
  <div class="list-view">
    <PageHeader title="生产调度" @refresh="fetchList">
      <template #actions>
        <el-input v-model="searchSeq" placeholder="搜索工单号" clearable style="width: 200px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-button type="primary" @click="handleCreate">新增工单</el-button>
      </template>
    </PageHeader>

    <PaginationTable
      :data="list" :loading="loading"
      :page-num="pageNum" :page-size="pageSize" :total="total"
      @page-change="(p: number) => { handlePageChange(p); fetchList() }"
      @size-change="(s: number) => { handleSizeChange(s); fetchList() }"
    >
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="scheduleSeq" label="工单号" width="140" />
      <el-table-column label="产品" min-width="130">
        <template #default="{ row }">{{ getProductName(row.productId) }}</template>
      </el-table-column>
      <el-table-column prop="scheduleCount" label="数量" width="80" />
      <el-table-column label="设备" min-width="130">
        <template #default="{ row }">{{ getEquipmentName(row.equipmentId) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <StatusTag :status="row.scheduleStatus" :map="SCHEDULE_STATUS" />
        </template>
      </el-table-column>
      <el-table-column label="开始日期" width="120">
        <template #default="{ row }">{{ formatDateTime(row.startDate)?.split(' ')[0] }}</template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openAssign(row)">分配设备</el-button>
          <template v-if="row.scheduleStatus === 10">
            <el-button size="small" type="success" @click="handleStart(row.id)">启动</el-button>
          </template>
          <template v-else-if="row.scheduleStatus === 20">
            <el-button size="small" type="primary" @click="handleComplete(row.id)">完成</el-button>
          </template>
          <el-button size="small" type="danger" :disabled="row.scheduleStatus !== 10" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </PaginationTable>

    <!-- 新增弹窗 -->
    <FormModal :visible="visible" mode="create" :submitting="submitting" width="640px" @confirm="handleSubmit" @cancel="close">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
        <el-form-item label="工单号" prop="scheduleSeq">
          <el-input v-model="formData.scheduleSeq" placeholder="如 SCH001" />
        </el-form-item>
        <el-form-item label="关联计划" prop="planId">
          <el-select v-model="formData.planId" placeholder="选择已启动计划" style="width: 100%" @change="(val: number) => { const p = plans.find(x => x.id === val); if (p) { formData.productId = p.productId; formData.scheduleCount = p.planCount } }">
            <el-option v-for="p in plans" :key="p.id" :label="`${p.planSeq} (${p.planCount}件)`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="scheduleCount">
          <el-input-number v-model="formData.scheduleCount" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="formData.startDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="formData.endDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
    </FormModal>

    <!-- 分配设备弹窗 -->
    <el-dialog v-model="assignVisible" title="分配设备" width="450px" :close-on-click-modal="false">
      <el-form label-width="80px">
        <el-form-item label="选择设备">
          <el-select v-model="assignEquipmentId" placeholder="选择可生产该产品的设备" style="width: 100%">
            <el-option v-for="e in assignableEquipments" :key="e.id" :label="`${e.equipmentSeq} — ${e.equipmentName}`" :value="e.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssign">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
