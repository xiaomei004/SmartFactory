<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { registerFactory, getFactoryDetail } from '@/api/factory'
import { register } from '@/api/user'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)
const step = ref(1) // 1=工厂信息, 2=管理员账号

const factoryForm = reactive({
  factoryName: '',
  factoryAddr: '',
  factoryWorker: 100,
})

const adminForm = reactive({
  userName: '',
  userPasswd: '',
  confirmPasswd: '',
  userRealName: '',
})

const validateConfirmPass = (_rule: any, value: string, callback: any) => {
  if (value !== adminForm.userPasswd) {
    callback(new Error('两次密码不一致'))
  } else {
    callback()
  }
}

const factoryRules = {
  factoryName: [{ required: true, message: '请输入工厂名称', trigger: 'blur' }],
}

const adminRules = {
  userName: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名2-20个字符', trigger: 'blur' },
  ],
  userPasswd: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' },
  ],
  confirmPasswd: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPass, trigger: 'blur' },
  ],
  userRealName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
}

async function nextStep() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  step.value = 2
}

function prevStep() {
  step.value = 1
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    // 1. 注册工厂
    const factoryRes = await registerFactory({
      factoryName: factoryForm.factoryName,
      factoryAddr: factoryForm.factoryAddr,
      factoryWorker: factoryForm.factoryWorker,
    })
    if (factoryRes.data.status !== 'ok') {
      ElMessage.error(factoryRes.data.msg || '工厂注册失败')
      return
    }

    // 2. 尝试获取工厂ID（通过详情接口，暂时使用默认factoryId=1）
    // 实际部署时后端register应返回factoryId
    const factoryId = 1

    // 3. 注册管理员账号
    const regRes = await register({
      userName: adminForm.userName,
      userPasswd: adminForm.userPasswd,
      userRealName: adminForm.userRealName,
      roleId: 1, // 系统管理员
      factoryId,
    })
    if (regRes.data.status === 'ok') {
      ElMessage.success('工厂注册成功！请登录')
      router.push('/login')
    }
  } catch {
    ElMessage.error('注册请求失败')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="factory-register-page">
    <div class="register-card">
      <div class="register-header">
        <el-icon :size="40" color="#2c5f8a">
          <OfficeBuilding />
        </el-icon>
        <h1>工厂入驻</h1>
        <p>注册您的工厂并创建管理员账号</p>
      </div>

      <!-- 步骤条 -->
      <el-steps :active="step" align-center style="margin-bottom: 28px">
        <el-step title="工厂信息" />
        <el-step title="管理员账号" />
      </el-steps>

      <!-- Step 1: 工厂信息 -->
      <el-form
        v-show="step === 1"
        ref="formRef"
        :model="factoryForm"
        :rules="factoryRules"
        label-position="top"
        @keyup.enter="nextStep"
      >
        <el-form-item label="工厂名称" prop="factoryName">
          <el-input v-model="factoryForm.factoryName" placeholder="请输入工厂名称（唯一）" />
        </el-form-item>
        <el-form-item label="工厂地址">
          <el-input v-model="factoryForm.factoryAddr" placeholder="请输入工厂地址（选填）" />
        </el-form-item>
        <el-form-item label="员工人数">
          <el-input-number v-model="factoryForm.factoryWorker" :min="1" :max="999999" style="width: 100%" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="step-btn" @click="nextStep">
            下一步 — 创建管理员
          </el-button>
        </el-form-item>
      </el-form>

      <!-- Step 2: 管理员账号 -->
      <el-form
        v-show="step === 2"
        ref="formRef"
        :model="adminForm"
        :rules="adminRules"
        label-position="top"
        @keyup.enter="handleSubmit"
      >
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="adminForm.userName" placeholder="2-20个字符，登录使用" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="userRealName">
          <el-input v-model="adminForm.userRealName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="userPasswd">
          <el-input v-model="adminForm.userPasswd" type="password" placeholder="至少6位密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPasswd">
          <el-input v-model="adminForm.confirmPasswd" type="password" placeholder="再次输入密码" show-password />
        </el-form-item>
        <el-form-item>
          <div style="display: flex; gap: 12px; width: 100%">
            <el-button class="step-btn" style="flex: 1" @click="prevStep">上一步</el-button>
            <el-button type="primary" :loading="loading" class="step-btn" style="flex: 2" @click="handleSubmit">
              完成注册
            </el-button>
          </div>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <span>已有工厂？</span>
        <router-link to="/login">直接登录</router-link>
        <span style="margin: 0 8px">|</span>
        <router-link to="/register">注册用户</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.factory-register-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a2332 0%, #2c5f8a 100%);
}

.register-card {
  width: 460px;
  max-width: 90vw;
  background: #fff;
  border-radius: 12px;
  padding: 36px 36px 28px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.2);
}

.register-header {
  text-align: center;
  margin-bottom: 20px;
}

.register-header h1 {
  font-size: 20px;
  font-weight: 600;
  color: #1a2332;
  margin: 10px 0 4px;
}

.register-header p {
  font-size: 13px;
  color: #909399;
  margin: 0;
}

.step-btn {
  width: 100%;
}

.register-footer {
  text-align: center;
  font-size: 13px;
  color: #909399;
  margin-top: 8px;
}

.register-footer a {
  color: var(--el-color-primary);
  text-decoration: none;
  margin-left: 4px;
}
</style>
