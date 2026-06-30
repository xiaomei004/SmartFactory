<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail, acceptOrder, rejectOrder, completeOrder } from '@/api/order'
import { addPlan } from '@/api/plan'
import { getProductList } from '@/api/product'
import { useAuthStore } from '@/stores/auth'
import { ORDER_STATUS } from '@/types/enums'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { ProductOrder, Product } from '@/types/entities'
import StatusTag from '@/components/common/StatusTag.vue'
import { formatDateTime } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const order = ref<ProductOrder | null>(null)
const products = ref<Product[]>([])
const loading = ref(false)

// 转计划弹窗
const planVisible = ref(false)
const planSubmitting = ref(false)
const planForm = ref({
  planSeq: '',
  planStartDate: '',
  planEndDate: '',
})

const STATUS_STEPS = [
  { status: 10, title: '未接单', desc: '订单已创建' },
  { status: 20, title: '已接单', desc: '等待排产' },
  { status: 40, title: '生产中', desc: '已转生产计划' },
  { status: 50, title: '已完成', desc: '订单交付' },
]

const REJECTED_STEP = { status: 30, title: '已拒绝', desc: '订单已拒绝' }

async function fetchOrder() {
  loading.value = true
  try {
    const id = Number(route.params.id)
    const [orderRes, prodRes] = await Promise.all([
      getOrderDetail(id),
      getProductList({ pageNum: 1, pageSize: 999, factoryId: authStore.factoryId }),
    ])
    order.value = orderRes.data
    products.value = prodRes.data.dataList
  } finally {
    loading.value = false
  }
}

function getProductName(id: number) {
  return products.value.find(p => p.id === id)?.productName || `产品#${id}`
}

function getActiveStep(status: number): number {
  if (status === 30) return -1 // 已拒绝 - 特殊处理
  if (status === 10) return 0
  if (status === 20) return 1
  if (status === 40) return 2
  if (status === 50) return 3
  return 0
}

async function handleAccept() {
  if (!order.value) return
  try {
    await acceptOrder(order.value.id)
    ElMessage.success('已接单')
    fetchOrder()
  } catch { /* */ }
}

async function handleReject() {
  if (!order.value) return
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
    await rejectOrder(order.value.id)
    ElMessage.success(`已拒单（原因：${remark}）`)
    fetchOrder()
  } catch { /* cancelled */ }
}

function openPlanDialog() {
  if (!order.value) return
  planForm.value = {
    planSeq: `P-${order.value.orderSeq}`,
    planStartDate: '',
    planEndDate: order.value.endDate || '',
  }
  planVisible.value = true
}

async function handlePlanSubmit() {
  if (!order.value || !planForm.value.planStartDate || !planForm.value.planEndDate) {
    ElMessage.warning('请填写计划起止日期')
    return
  }
  planSubmitting.value = true
  try {
    await addPlan({
      planSeq: planForm.value.planSeq,
      orderId: order.value.id,
      productId: order.value.productId,
      planCount: order.value.productCount,
      deliveryDate: order.value.endDate || '',
      planStartDate: planForm.value.planStartDate,
      planEndDate: planForm.value.planEndDate,
      factoryId: authStore.factoryId,
    })
    ElMessage.success('转计划成功')
    planVisible.value = false
    fetchOrder()
  } catch { /* */ }
  finally { planSubmitting.value = false }
}

async function handleComplete() {
  if (!order.value) return
  try {
    await ElMessageBox.confirm('确定完成该订单吗？将级联完成关联的计划和工单。', '确认完成', { type: 'warning' })
    await completeOrder(order.value.id)
    ElMessage.success('订单已完成')
    fetchOrder()
  } catch { /* */ }
}

onMounted(fetchOrder)
</script>

<template>
  <div class="order-detail">
    <!-- 顶部导航 -->
    <div class="detail-header">
      <el-button text @click="router.push('/orders')">
        <el-icon><ArrowLeft /></el-icon>
        返回订单列表
      </el-button>
      <span class="header-title">订单详情</span>
    </div>

    <el-row :gutter="16" v-loading="loading">
      <!-- 订单基本信息 -->
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">{{ order?.orderSeq || '加载中...' }}</span>
              <StatusTag v-if="order" :status="order.orderStatus" :map="ORDER_STATUS" />
            </div>
          </template>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="订单编号">{{ order?.orderSeq || '-' }}</el-descriptions-item>
            <el-descriptions-item label="产品">{{ getProductName(order?.productId || 0) }}</el-descriptions-item>
            <el-descriptions-item label="订单数量">{{ order?.productCount || 0 }} 件</el-descriptions-item>
            <el-descriptions-item label="可用产能">{{ order?.factoryYield || '-' }}</el-descriptions-item>
            <el-descriptions-item label="交货日期">{{ formatDateTime(order?.endDate)?.split(' ')[0] || '-' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDateTime(order?.createTime) || '-' }}</el-descriptions-item>
            <el-descriptions-item label="工厂ID">{{ order?.factoryId }}</el-descriptions-item>
            <el-descriptions-item label="订单来源">{{ order?.orderSource === 10 ? '线上' : '线下' }}</el-descriptions-item>
          </el-descriptions>

          <!-- 操作按钮 -->
          <div class="detail-actions" v-if="order">
            <template v-if="order.orderStatus === 10">
              <el-button type="success" size="large" @click="handleAccept">接单</el-button>
              <el-button type="danger" size="large" @click="handleReject">拒单</el-button>
            </template>
            <template v-else-if="order.orderStatus === 20">
              <el-button type="primary" size="large" @click="openPlanDialog">转生产计划</el-button>
            </template>
            <template v-else-if="order.orderStatus === 40">
              <el-button type="success" size="large" @click="handleComplete">完成订单</el-button>
            </template>
            <template v-else-if="order.orderStatus === 30">
              <el-tag type="danger" size="large">此订单已被拒绝</el-tag>
            </template>
            <template v-else-if="order.orderStatus === 50">
              <el-tag type="success" size="large">此订单已完成</el-tag>
            </template>
          </div>
        </el-card>
      </el-col>

      <!-- 状态流转 -->
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>
            <span class="card-title">状态流转</span>
          </template>
          <el-steps
            v-if="order && order.orderStatus !== 30"
            direction="vertical"
            :active="getActiveStep(order.orderStatus)"
            process-status="finish"
            finish-status="success"
          >
            <el-step
              v-for="step in STATUS_STEPS"
              :key="step.status"
              :title="step.title"
              :description="step.desc"
            />
          </el-steps>
          <div v-else-if="order" class="rejected-info">
            <el-result icon="error" title="已拒绝" sub-title="该订单已被拒绝，不再流转" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 转计划弹窗 -->
    <el-dialog v-model="planVisible" title="转生产计划" width="520px" :close-on-click-modal="false" destroy-on-close>
      <el-descriptions v-if="order" :column="2" border size="small" style="margin-bottom: 20px">
        <el-descriptions-item label="订单号">{{ order.orderSeq }}</el-descriptions-item>
        <el-descriptions-item label="产品">{{ getProductName(order.productId) }}</el-descriptions-item>
        <el-descriptions-item label="订单数量">{{ order.productCount }} 件</el-descriptions-item>
        <el-descriptions-item label="交货日期">{{ formatDateTime(order.endDate)?.split(' ')[0] }}</el-descriptions-item>
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

<style scoped>
.order-detail {
  max-width: 1200px;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a2332;
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

.detail-actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
}

.rejected-info {
  padding: 20px 0;
}
</style>
