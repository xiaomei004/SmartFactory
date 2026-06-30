/**
 * Axios 实例 + 拦截器
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器：注入用户/工厂 Header
request.interceptors.request.use(
  (config) => {
    // 从 localStorage 读取，authStore 未初始化时兜底
    const userId = localStorage.getItem('userId') || '1'
    const factoryId = localStorage.getItem('factoryId') || '1'

    config.headers['X-User-Id'] = userId
    config.headers['X-Factory-Id'] = factoryId

    return config
  },
  (error) => Promise.reject(error),
)

// 响应拦截器：统一错误提示
request.interceptors.response.use(
  (response) => {
    const data = response.data
    // 业务层错误
    if (data && data.status === 'error') {
      ElMessage.error(data.msg || '操作失败')
    }
    return response
  },
  (error) => {
    ElMessage.error(error.message || '网络请求失败')
    return Promise.reject(error)
  },
)

export default request
