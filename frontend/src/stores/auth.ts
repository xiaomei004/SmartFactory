/**
 * 认证 Store — 用户登录/工厂信息
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi } from '@/api/user'
import { ElMessage } from 'element-plus'

export const useAuthStore = defineStore('auth', () => {
  // ===== 状态 =====
  const userId = ref<number>(Number(localStorage.getItem('userId')) || 1)
  const factoryId = ref<number>(Number(localStorage.getItem('factoryId')) || 1)
  const userName = ref(localStorage.getItem('userName') || '')
  const userRealName = ref(localStorage.getItem('userRealName') || '')
  const roleId = ref<number>(Number(localStorage.getItem('roleId')) || 0)
  const token = ref(localStorage.getItem('token') || '')

  // ===== 计算属性 =====
  const isLoggedIn = computed(() => !!token)
  const displayName = computed(() => userRealName || userName || '未登录')

  // ===== 方法 =====
  async function login(user: string, pass: string): Promise<boolean> {
    try {
      const res = await loginApi(user, pass)
      const data = res.data
      if (data.status === 'ok' && data.user) {
        const u = data.user
        userId.value = u.id
        factoryId.value = u.factoryId
        userName.value = u.userName
        userRealName.value = u.userRealName
        roleId.value = u.roleId
        token.value = 'session' // Session 认证，仅作标记

        // 持久化到 localStorage
        localStorage.setItem('userId', String(u.id))
        localStorage.setItem('factoryId', String(u.factoryId))
        localStorage.setItem('userName', u.userName)
        localStorage.setItem('userRealName', u.userRealName)
        localStorage.setItem('roleId', String(u.roleId))
        localStorage.setItem('token', 'session')

        ElMessage.success(`欢迎回来，${u.userRealName || u.userName}`)
        return true
      } else {
        ElMessage.error(data.msg || '登录失败')
        return false
      }
    } catch {
      ElMessage.error('登录请求失败')
      return false
    }
  }

  async function logout(): Promise<void> {
    try {
      await logoutApi()
    } finally {
      // 无论服务端返回什么都清除本地状态
      userId.value = 1
      factoryId.value = 1
      userName.value = ''
      userRealName.value = ''
      roleId.value = 0
      token.value = ''

      localStorage.removeItem('userId')
      localStorage.removeItem('factoryId')
      localStorage.removeItem('userName')
      localStorage.removeItem('userRealName')
      localStorage.removeItem('roleId')
      localStorage.removeItem('token')

      ElMessage.success('已退出登录')
    }
  }

  /** 从 localStorage 恢复会话 */
  function restoreSession() {
    const t = localStorage.getItem('token')
    if (t) {
      token.value = t
      userId.value = Number(localStorage.getItem('userId')) || 1
      factoryId.value = Number(localStorage.getItem('factoryId')) || 1
      userName.value = localStorage.getItem('userName') || ''
      userRealName.value = localStorage.getItem('userRealName') || ''
      roleId.value = Number(localStorage.getItem('roleId')) || 0
    }
  }

  return {
    userId,
    factoryId,
    userName,
    userRealName,
    roleId,
    token,
    isLoggedIn,
    displayName,
    login,
    logout,
    restoreSession,
  }
})
