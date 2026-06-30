<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const roleOptions = [
  { id: 1, name: '系统管理员' },
  { id: 2, name: '订单管理员' },
  { id: 3, name: '计划管理员' },
  { id: 4, name: '调度管理员' },
  { id: 5, name: '生产员工' },
]

const form = reactive({
  userName: '',
  userPasswd: '',
  confirmPasswd: '',
  userRealName: '',
  roleId: 5,
  factoryId: 1,
})

const validateConfirmPass = (_rule: any, value: string, callback: any) => {
  if (value !== form.userPasswd) {
    callback(new Error('两次密码不一致'))
  } else {
    callback()
  }
}

const rules = {
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

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await register({
      userName: form.userName,
      userPasswd: form.userPasswd,
      userRealName: form.userRealName,
      roleId: form.roleId,
      factoryId: form.factoryId,
    })
    if (res.data.status === 'ok') {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <el-icon :size="40" color="#2c5f8a">
          <Monitor />
        </el-icon>
        <h1>注册新账号</h1>
        <p>加入东软智能制造云平台</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleRegister"
        @keyup.enter="handleRegister"
      >
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="2-20个字符" />
        </el-form-item>

        <el-form-item label="真实姓名" prop="userRealName">
          <el-input v-model="form.userRealName" placeholder="请输入真实姓名" />
        </el-form-item>

        <el-form-item label="角色" prop="roleId">
          <el-select v-model="form.roleId" style="width: 100%">
            <el-option
              v-for="r in roleOptions"
              :key="r.id"
              :label="r.name"
              :value="r.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="密码" prop="userPasswd">
          <el-input
            v-model="form.userPasswd"
            type="password"
            placeholder="至少6位密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPasswd">
          <el-input
            v-model="form.confirmPasswd"
            type="password"
            placeholder="再次输入密码"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleRegister"
            class="register-btn"
          >
            注 册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <span>已有账号？</span>
        <router-link to="/login">返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a2332 0%, #2c5f8a 100%);
}

.register-card {
  width: 420px;
  max-width: 90vw;
  background: #fff;
  border-radius: 12px;
  padding: 36px 36px 28px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.2);
}

.register-header {
  text-align: center;
  margin-bottom: 28px;
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

.register-btn {
  width: 100%;
}

.register-footer {
  text-align: center;
  font-size: 13px;
  color: #909399;
}

.register-footer a {
  color: var(--el-color-primary);
  text-decoration: none;
  margin-left: 4px;
}
</style>
