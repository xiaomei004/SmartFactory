<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboardStatistics } from '@/api/dashboard'
import { getEquipmentList } from '@/api/equipment'
import { useAuthStore } from '@/stores/auth'
import { EQUIPMENT_STATUS } from '@/types/enums'
import type { DashboardData } from '@/types/api'
import type { Equipment } from '@/types/entities'
import EquipmentStatsPanel from '@/components/charts/EquipmentStatsPanel.vue'
import OrderStatusChart from '@/components/charts/OrderStatusChart.vue'
import YearlySummaryChart from '@/components/charts/YearlySummaryChart.vue'
import StatusTag from '@/components/common/StatusTag.vue'

const authStore = useAuthStore()
const dashboardData = ref<DashboardData | null>(null)
const equipments = ref<Equipment[]>([])
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try {
    const [statsRes, equipRes] = await Promise.all([
      getDashboardStatistics(authStore.factoryId),
      getEquipmentList({ pageNum: 1, pageSize: 99, factoryId: authStore.factoryId }),
    ])
    dashboardData.value = statsRes.data
    equipments.value = equipRes.data.dataList
  } finally {
    loading.value = false
  }
}

onMounted(fetchData)
</script>

<template>
  <div class="dashboard">
    <!-- 设备统计卡片 + 效率指标 -->
    <EquipmentStatsPanel
      :data="dashboardData?.equipment ?? null"
      :loading="loading"
    />

    <!-- 图表区 -->
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <OrderStatusChart :data="dashboardData?.order ?? null" />
      </el-col>
      <el-col :span="12">
        <YearlySummaryChart :data="dashboardData?.yearlySummary ?? []" />
      </el-col>
    </el-row>

    <!-- 设备信息列表 -->
    <el-card shadow="never" style="margin-top: 16px">
      <template #header>
        <div class="card-header">
          <span class="card-title">设备信息列表</span>
          <span class="card-subtitle">共 {{ equipments.length }} 台设备</span>
        </div>
      </template>
      <el-table
        :data="equipments"
        v-loading="loading"
        stripe
        size="small"
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266', fontWeight: 600, fontSize: '13px' }"
      >
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="equipmentSeq" label="设备序列号" width="150" />
        <el-table-column prop="equipmentName" label="设备名称" min-width="160" />
        <el-table-column label="运行状态" width="110">
          <template #default="{ row }">
            <StatusTag :status="row.equipmentStatus" :map="EQUIPMENT_STATUS" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">
            {{ row.createTime?.split(' ')[0] || '-' }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.dashboard {
  max-width: 1400px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #1a2332;
}

.card-subtitle {
  font-size: 12px;
  color: #909399;
}
</style>
