<script setup lang="ts">
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import type { OrderStats } from '@/types/api'

use([PieChart, TitleComponent, TooltipComponent, LegendComponent, CanvasRenderer])

const props = defineProps<{
  data: OrderStats | null
}>()

const option = computed(() => ({
  title: {
    text: '订单状态分布',
    left: 'center',
    textStyle: {
      fontSize: 14,
      fontWeight: 600,
      color: '#1a2332',
    },
  },
  tooltip: {
    trigger: 'item' as const,
    formatter: '{b}: {c} ({d}%)',
  },
  legend: {
    bottom: 0,
    textStyle: { fontSize: 12 },
  },
  color: ['#1890ff', '#52c41a', '#ff4d4f', '#faad14', '#909399'],
  series: [
    {
      type: 'pie',
      radius: ['45%', '70%'],
      center: ['50%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 4,
        borderColor: '#fff',
        borderWidth: 2,
      },
      label: {
        show: false,
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 16,
          fontWeight: 'bold',
        },
      },
      data: [
        { value: props.data?.notAccepted ?? 0, name: '未接单' },
        { value: props.data?.accepted ?? 0, name: '已接单' },
        { value: props.data?.rejected ?? 0, name: '已拒绝' },
        { value: props.data?.producing ?? 0, name: '生产中' },
        { value: props.data?.completed ?? 0, name: '已完成' },
      ],
    },
  ],
}))
</script>

<template>
  <el-card shadow="never">
    <VChart :option="option" autoresize style="height: 320px" />
  </el-card>
</template>
