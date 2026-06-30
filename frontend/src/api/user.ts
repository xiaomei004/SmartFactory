/**
 * 用户 API
 */
import request from './request'
import type { LoginResult, Result } from '@/types/api'

/** 登录 */
export function login(userName: string, userPasswd: string) {
  return request.post<LoginResult>('/user/login', { userName, userPasswd })
}

/** 注册 */
export function register(data: {
  userName: string
  userPasswd: string
  userRealName: string
  roleId: number
  factoryId: number
}) {
  return request.post<Result>('/user/register', data)
}

/** 退出 */
export function logout() {
  return request.post<Result>('/user/logout', {})
}
