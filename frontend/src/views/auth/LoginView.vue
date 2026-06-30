<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  userName: 'admin',
  userPasswd: '123456',
})

const rules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  userPasswd: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  const ok = await authStore.login(form.userName, form.userPasswd)
  loading.value = false

  if (ok) {
    router.push('/dashboard')
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <!-- 头部 -->
      <div class="login-header">
        <el-icon :size="48" color="#2c5f8a">
          <Monitor />
        </el-icon>
        <h1>东软智能制造云平台</h1>
        <p>SmartFactory Manufacturing Cloud</p>
      </div>

      <!-- 表单 -->
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        @submit.prevent="handleLogin"
        @keyup.enter="handleLogin"
      >
        <el-form-item label="用户名" prop="userName">
          <el-input
            v-model="form.userName"
            placeholder="请输入用户名"
            size="large"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="密码" prop="userPasswd">
          <el-input
            v-model="form.userPasswd"
            type="password"
            placeholder="请输入密码"
            show-password
            size="large"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-btn"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 底部 -->
      <div class="login-footer">
        <span>还没有账号？</span>
        <router-link to="/register">立即注册</router-link>
        <span style="margin: 0 8px">|</span>
        <router-link to="/factory-register">工厂入驻</router-link>
      </div>
    </div>

    <div class="login-copyright">
      Copyright &copy; 2026 东软集团 · SmartFactory v1.0
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a2332 0%, #2c5f8a 100%);
}

.login-card {
  width: 400px;
  max-width: 90vw;
  background: #fff;
  border-radius: 12px;
  padding: 40px 36px 32px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header h1 {
  font-size: 22px;
  font-weight: 600;
  color: #1a2332;
  margin: 12px 0 6px;
}

.login-header p {
  font-size: 13px;
  color: #909399;
  margin: 0;
}

.login-btn {
  width: 100%;
}

.login-footer {
  text-align: center;
  font-size: 13px;
  color: #909399;
}

.login-footer a {
  color: var(--el-color-primary);
  text-decoration: none;
  margin-left: 4px;
}

.login-copyright {
  margin-top: 24px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}
</style>
