import axios from 'axios'
import { ElMessage } from 'element-plus'
import { mockResolve } from './mock'

const useMock = import.meta.env.VITE_USE_MOCK === 'true'
let consoleRedirecting = false

const redirectToLogin = scope => {
  if (typeof window === 'undefined') return
  const current = window.location.pathname + window.location.search
  if (scope === 'console') {
    localStorage.removeItem('musiccard_console_user')
    if (!window.location.pathname.startsWith('/console/login') && !consoleRedirecting) {
      consoleRedirecting = true
      window.location.replace(`/console/login?redirect=${encodeURIComponent(current)}`)
    }
    return
  }
  localStorage.removeItem('musiccard_sign')
  localStorage.removeItem('musiccard_user')
  if (!window.location.pathname.startsWith('/app/account')) {
    window.location.replace(`/app/account?redirect=${encodeURIComponent(current)}`)
  }
}

const createClient = (baseURL, scope) => {
  const client = axios.create({
    baseURL,
    timeout: 12000,
    // Console 依赖后端写入的 HttpOnly Cookie；App 依赖 sign 请求头。
    withCredentials: scope === 'console'
  })

  client.interceptors.request.use(config => {
    if (scope === 'app') {
      const sign = localStorage.getItem('musiccard_sign')
      if (sign) config.headers.sign = sign
    }
    return config
  })

  client.interceptors.response.use(response => {
    if (response.config.responseType === 'blob') return response
    const data = response.data
    if (data?.status?.code === 1001) return data

    const error = new Error(data?.status?.msg || '请求失败')
    error.code = data?.status?.code
    if (error.code === 1002) redirectToLogin(scope)
    return Promise.reject(error)
  }, error => {
    const status = error.response?.status
    if (status === 401 || status === 403) {
      redirectToLogin(scope)
    } else if (!useMock) {
      ElMessage.error(error.message || '网络连接失败')
    }
    return Promise.reject(error)
  })

  return config => useMock ? mockResolve(config, scope) : client(config)
}

export const appRequest = createClient(import.meta.env.VITE_APP_API_BASE || '/api/app', 'app')
export const userRequest = createClient(import.meta.env.VITE_USER_API_BASE || '/api/user', 'app')
export const consoleUserRequest = createClient(import.meta.env.VITE_USER_API_BASE || '/api/user', 'console')
export const consoleRequest = createClient(import.meta.env.VITE_CONSOLE_API_BASE || '/api/console', 'console')
export const asForm = params => new URLSearchParams(
  Object.entries(params).filter(([, value]) => value !== '' && value !== null && value !== undefined)
)
