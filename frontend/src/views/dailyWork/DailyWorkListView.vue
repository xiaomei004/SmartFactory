<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDailyWorkList, reportDailyWork, completeDailyWorkReport, deleteDailyWork } from '@/api/dailyWork'
import { getScheduleList } from '@/api/schedule'
import { usePagination } from '@/composables/usePagination'
import { useFormModal } from '@/composables/useFormModal'
import { useAuthStore } from '@/stores/auth'
import { WORK_COMPLETE_FLAG } from '@/types/enums'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { DailyWork, ProductSchedule } from '@/types/entities'
import PageHeader from '@/components/common/PageHeader.vue'
import PaginationTable from '@/components/common/PaginationTable.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import FormModal from '@/components/common/FormModal.vue'
import { formatDateTime } from '@/utils/format'

const authStore = useAuthStore()
const { pageNum, pageSize, total, handlePageChange, handleSizeChange, resetPage } = usePagination()
const { visible, formData, openCreate, close } = useFormModal<any>()

const list = ref<DailyWork[]>([])
const loading = ref(false)
const submitting = ref(false)
const producingSchedules = ref<ProductSchedule[]>([])

async function fetchList() {
  loading.value = true
  try {
    const res = await getDailyWorkList({
      pageNum: pageNum.value, pageSize: pageSize.value,
      factoryId: authStore.factoryId,
    })
    list.value = res.data.dataList
    total.value = res.data.pageInfo.total
  } finally { loading.value = false }
}

async function fetchSchedules() {
  const res = await getScheduleList({ pageNum: 1, pageSize: 999, factoryId: authStore.factoryId })
  producingSchedules.value = res.data.dataList.filter(s => s.scheduleStatus === 20)
}

function handleCreate() {
  finishReport.value = false
  openCreate({ factoryId: authStore.factoryId })
}

const finishReport = ref(false)

async function handleSubmit() {
  const d = formData.value
  if (!d.scheduleId) { ElMessage.warning('请选择工单'); return }
  if (d.workingCount == null || d.qualifiedCount == null) {
    ElMessage.warning('请填写加工数量和合格数量')
    return
  }
  submitting.value = true
  try {
    await reportDailyWork({
      scheduleId: d.scheduleId,
      workingCount: d.workingCount || 0,
      qualifiedCount: d.qualifiedCount || 0,
      unqualifiedCout: d.unqualifiedCout || 0,
      factoryId: authStore.factoryId,
    })
    // 如果勾选了"结束报工"，找到刚创建的报工记录并结束
    if (finishReport.value) {
      const res = await getDailyWorkList({ pageNum: 1, pageSize: 99, factoryId: authStore.factoryId })
      const latest = res.data.dataList
        .filter(w => w.scheduleId === d.scheduleId && w.completeFlag === 1)
        .sort((a, b) => b.id - a.id)[0]
      if (latest) {
        await completeDailyWorkReport(latest.id)
        ElMessage.success('报工成功，已结束工单')
      } else {
        ElMessage.success('报工成功')
      }
    } else {
      ElMessage.success('报工成功')
    }
    close()
    fetchList()
  } finally { submitting.value = false }
}

async function handleCompleteReport(row: DailyWork) {
  try {
    await ElMessageBox.confirm('确定结束该报工吗？', '确认', { type: 'warning' })
    await completeDailyWorkReport(row.id)
    ElMessage.success('报工已结束')
    fetchList()
  } catch { /* */ }
}

async function handleDelete(row: DailyWork) {
  try {
    await ElMessageBox.confirm('确定删除该报工记录吗？', '确认删除', { type: 'warning' })
    await deleteDailyWork(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch { /* */ }
}

onMounted(() => { fetchList(); fetchSchedules() })
</script>

<template>
  <div class="list-view">
    <PageHeader title="生产报工" @refresh="fetchList">
      <template #actions>
        <el-button type="primary" @click="handleCreate">新增报工</el-button>
      </template>
    </PageHeader>

    <PaginationTable
      :data="list" :loading="loading"
      :page-num="pageNum" :page-size="pageSize" :total="total"
      @page-change="(p: number) => { handlePageChange(p); fetchList() }"
      @size-change="(s: number) => { handleSizeChange(s); fetchList() }"
    >
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="scheduleId" label="工单ID" width="90" />
      <el-table-column prop="equipmentSeq" label="设备序列号" width="140" />
      <el-table-column prop="workingCount" label="加工数" width="100" />
      <el-table-column prop="qualifiedCount" label="合格数" width="100" />
      <el-table-column prop="unqualifiedCout" label="不合格数" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <StatusTag :status="row.completeFlag" :map="WORK_COMPLETE_FLAG" />
        </template>
      </el-table-column>
      <el-table-column label="开始时间" width="170">
        <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.completeFlag === 1"
            size="small"
            type="warning"
            @click="handleCompleteReport(row)"
          >
            结束
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </PaginationTable>

    <!-- 新增报工弹窗 -->
    <FormModal :visible="visible" mode="create" :submitting="submitting" @confirm="handleSubmit" @cancel="close">
      <el-form :model="formData" label-width="90px">
        <el-form-item label="关联工单">
          <el-select v-model="formData.scheduleId" placeholder="选择生产中工单" style="width: 100%">
            <el-option v-for="s in producingSchedules" :key="s.id" :label="`${s.scheduleSeq} (设备#${s.equipmentId})`" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="加工数量">
          <el-input-number v-model="formData.workingCount" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="合格数量">
          <el-input-number v-model="formData.qualifiedCount" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="不合格数量">
          <el-input-number v-model="formData.unqualifiedCout" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="">
          <el-checkbox v-model="finishReport">
            结束报工（勾选后工单将标记为已完成）
          </el-checkbox>
        </el-form-item>
      </el-form>
    </FormModal>
  </div>
</template>
