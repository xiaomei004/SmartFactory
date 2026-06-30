<script setup lang="ts">
import type { EquipmentStats } from '@/types/api'

defineProps<{
  data: EquipmentStats | null
  loading: boolean
}>()
</script>

<template>
  <el-row :gutter="16">
    <!-- 设备总数 -->
    <el-col :span="4">
      <el-card shadow="never" class="stat-card">
        <div class="stat-label">设备总数</div>
        <div class="stat-value">{{ data?.total ?? '-' }}</div>
        <div class="stat-sub">台</div>
      </el-card>
    </el-col>

    <!-- 启用 -->
    <el-col :span="4">
      <el-card shadow="never" class="stat-card stat-success">
        <div class="stat-label"><span class="dot green"></span>启用</div>
        <div class="stat-value">{{ data?.enabled ?? '-' }}</div>
        <div class="stat-sub">台</div>
      </el-card>
    </el-col>

    <!-- 停用 -->
    <el-col :span="4">
      <el-card shadow="never" class="stat-card stat-warning">
        <div class="stat-label"><span class="dot orange"></span>停用</div>
        <div class="stat-value">{{ data?.disabled ?? '-' }}</div>
        <div class="stat-sub">台</div>
      </el-card>
    </el-col>

    <!-- 故障 -->
    <el-col :span="4">
      <el-card shadow="never" class="stat-card stat-danger">
        <div class="stat-label"><span class="dot red"></span>故障</div>
        <div class="stat-value">{{ data?.fault ?? '-' }}</div>
        <div class="stat-sub">台</div>
      </el-card>
    </el-col>

    <!-- 生产中 -->
    <el-col :span="4">
      <el-card shadow="never" class="stat-card stat-primary">
        <div class="stat-label"><span class="dot blue"></span>生产中</div>
        <div class="stat-value">{{ data?.producing ?? '-' }}</div>
        <div class="stat-sub">台</div>
      </el-card>
    </el-col>

    <!-- 待机 -->
    <el-col :span="4">
      <el-card shadow="never" class="stat-card">
        <div class="stat-label"><span class="dot gray"></span>待机</div>
        <div class="stat-value">{{ data?.standby ?? '-' }}</div>
        <div class="stat-sub">台</div>
      </el-card>
    </el-col>
  </el-row>

  <!-- 效率指标 -->
  <el-row :gutter="16" style="margin-top: 16px">
    <el-col :span="6">
      <el-card shadow="never">
        <div class="efficiency-item">
          <span class="efficiency-label">开机率</span>
          <el-progress
            :percentage="data?.startupRate ?? 0"
            :stroke-width="16"
            :color="'#52c41a'"
          />
        </div>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="never">
        <div class="efficiency-item">
          <span class="efficiency-label">运行率</span>
          <el-progress
            :percentage="data?.runningRate ?? 0"
            :stroke-width="16"
            :color="'#1890ff'"
          />
        </div>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="never">
        <div class="efficiency-item">
          <span class="efficiency-label">故障率</span>
          <el-progress
            :percentage="data?.faultRate ?? 0"
            :stroke-width="16"
            :color="'#ff4d4f'"
          />
        </div>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="never">
        <div class="efficiency-item">
          <span class="efficiency-label">综合效率</span>
          <el-progress
            :percentage="data?.efficiency ?? 0"
            :stroke-width="16"
            :color="'#2c5f8a'"
          />
        </div>
      </el-card>
    </el-col>
  </el-row>
</template>

<style scoped>
.stat-card {
  text-align: center;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}
.dot.green { background: #52c41a; }
.dot.orange { background: #faad14; }
.dot.red { background: #ff4d4f; }
.dot.blue { background: #1890ff; }
.dot.gray { background: #909399; }

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1a2332;
  margin: 4px 0;
}

.stat-sub {
  font-size: 12px;
  color: #c0c4cc;
}

.stat-success .stat-value { color: #52c41a; }
.stat-warning .stat-value { color: #faad14; }
.stat-danger .stat-value { color: #ff4d4f; }
.stat-primary .stat-value { color: #1890ff; }

.efficiency-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.efficiency-label {
  font-size: 13px;
  color: #606266;
}
</style>
