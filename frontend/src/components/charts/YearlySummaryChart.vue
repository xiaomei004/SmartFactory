<script setup lang="ts">
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { BarChart, LineChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import type { YearlySummaryItem } from '@/types/api'

use([BarChart, LineChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent, CanvasRenderer])

const props = defineProps<{
  data: YearlySummaryItem[]
}>()

const months = computed(() => props.data.map((d) => {
  const parts = d.month.split('-')
  return parts[1] ? `${parts[1]}月` : d.month
}))

const option = computed(() => ({
  title: {
    text: '年度月度汇总',
    left: 'center',
    textStyle: {
      fontSize: 14,
      fontWeight: 600,
      color: '#1a2332',
    },
  },
  tooltip: {
    trigger: 'axis' as const,
  },
  legend: {
    data: ['订单', '计划', '工单'],
    bottom: 0,
    textStyle: { fontSize: 12 },
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '12%',
    top: '16%',
    containLabel: true,
  },
  xAxis: {
    type: 'category',
    data: months.value,
    axisTick: { alignWithLabel: true },
  },
  yAxis: {
    type: 'value',
    minInterval: 1,
  },
  color: ['#2c5f8a', '#52c41a', '#faad14'],
  series: [
    {
      name: '订单',
      type: 'bar',
      data: props.data.map((d) => d.orders),
      barMaxWidth: 20,
      itemStyle: { borderRadius: [4, 4, 0, 0] },
    },
    {
      name: '计划',
      type: 'line',
      data: props.data.map((d) => d.plans),
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
    },
    {
      name: '工单',
      type: 'line',
      data: props.data.map((d) => d.schedules),
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
    },
  ],
}))
</script>

<template>
  <el-card shadow="never">
    <VChart :option="option" autoresize style="height: 320px" />
  </el-card>
</template>
