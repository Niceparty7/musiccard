<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import BrandMark from '../../components/BrandMark.vue'
import { consoleApi } from '../../api/console'

const route = useRoute()
const loading = ref(false)
const phase = ref(0)
const form = ref({ phone: '', password: '', remember: true })
let frame

const targetPath = () => {
  const redirect = route.query.redirect
  return typeof redirect === 'string' && redirect.startsWith('/console/')
    ? redirect
    : '/console/dashboard'
}

const enterConsole = user => {
  if (user) localStorage.setItem('musiccard_console_user', JSON.stringify(user))
  window.location.replace(targetPath())
}

const restoreConsoleSession = async () => {
  try {
    // 只用于确认浏览器现有 Cookie/Session；不保存额外 token，也不重复登录。
    await consoleApi.statistics()
    enterConsole()
    return true
  } catch {
    return false
  }
}

function tick() {
  phase.value = (phase.value + 1) % 360
  frame = requestAnimationFrame(tick)
}

onMounted(() => {
  frame = requestAnimationFrame(tick)
  restoreConsoleSession()
})
onBeforeUnmount(() => cancelAnimationFrame(frame))

async function login() {
  if (loading.value) return
  if (!/^1\d{10}$/.test(form.value.phone) || !form.value.password) {
    return ElMessage.warning('请填写正确的手机号和密码')
  }
  loading.value = true
  localStorage.removeItem('musiccard_console_user')
  try {
    const { result } = await consoleApi.login(form.value)
    ElMessage.success('身份验证通过')
    enterConsole(result)
  } catch (error) {
    // 后端 4004 表示当前请求已携带登录 Session，先验证 Session 后直接进入后台。
    if (error?.code === 4004 && await restoreConsoleSession()) return
    ElMessage.error(error?.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>
<template><div class="login"><section class="visual"><div class="visual-head"><BrandMark/><span>CONTROL ROOM / ACCESS</span></div><div class="core"><div class="ring r1" :style="{transform:`rotate(${phase}deg)`}"></div><div class="ring r2" :style="{transform:`rotate(${-phase*1.7}deg)`}"></div><div class="ring r3" :style="{transform:`scale(${.94+Math.sin(phase/18)*.08})`}"></div><i :style="{transform:`scale(${.94+Math.sin(phase/10)*.08}) rotate(${phase/7}deg)`}"><b>MC</b></i><em :style="{transform:`rotate(${phase*2}deg)`}"></em><span>AUTH NODE<br><b>READY · {{String(phase%100).padStart(2,'0')}}</b></span></div><div class="telemetry"><span>SESSION / COOKIE</span><span>ENCRYPTION / ACTIVE</span><span>NODE / 8081</span></div></section><section class="form-panel"><div class="form"><span class="eyebrow">AUTHORIZED PERSONNEL ONLY</span><h1>连接控制核心</h1><p>管理音乐内容、分类结构与消息任务。</p><el-form label-position="top"><el-form-item label="管理员手机"><el-input v-model="form.phone" size="large" placeholder="请输入手机号码"/></el-form-item><el-form-item label="访问密码"><el-input v-model="form.password" size="large" type="password" show-password placeholder="请输入密码" @keyup.enter="login"/></el-form-item><el-checkbox v-model="form.remember">保持登录状态</el-checkbox><el-button type="primary" size="large" native-type="button" :loading="loading" @click="login">验证并进入 <span>→</span></el-button></el-form><router-link to="/app/explore">← 返回 App 端</router-link></div></section></div></template>
<style scoped>.login{min-height:100vh;display:grid;grid-template-columns:1.05fr .95fr;background:#fff}.visual{position:relative;overflow:hidden;padding:34px 45px;background:radial-gradient(circle at 50% 50%,#173459,var(--ink-950) 62%);color:#fff}.visual:after{content:"";position:absolute;inset:0;background-image:linear-gradient(rgba(48,217,255,.07) 1px,transparent 1px),linear-gradient(90deg,rgba(48,217,255,.07) 1px,transparent 1px);background-size:42px 42px}.visual-head,.telemetry{position:relative;z-index:2;display:flex;justify-content:space-between;align-items:center}.visual-head>span,.telemetry{font:9px var(--mono);letter-spacing:.15em;color:#6f89a5}.core{position:absolute;inset:50% auto auto 50%;width:420px;aspect-ratio:1;transform:translate(-50%,-50%);display:grid;place-items:center;z-index:2}.ring{position:absolute;border:1px solid rgba(48,217,255,.35);border-radius:50%;transition:transform .03s linear}.r1{inset:0;border-style:dashed}.r2{inset:15%;border-color:rgba(124,108,255,.7)}.r3{inset:30%;box-shadow:0 0 65px rgba(48,217,255,.22)}.core>i{width:110px;aspect-ratio:1;display:grid;place-items:center;border-radius:34px;background:linear-gradient(145deg,var(--cyan),var(--violet));color:var(--ink-950);font:28px var(--mono);font-style:normal;box-shadow:0 0 75px rgba(48,217,255,.55);z-index:2;transition:transform .03s linear}.core>i b{font-weight:400}.core>em{position:absolute;width:68%;aspect-ratio:1;border:1px dashed rgba(184,243,91,.55);border-radius:50%;transition:transform .03s linear}.core>span{position:absolute;bottom:25px;text-align:center;font:9px/1.8 var(--mono);letter-spacing:.14em;color:#7894b1}.core b{color:var(--lime)}.telemetry{position:absolute;left:45px;right:45px;bottom:34px}.form-panel{display:grid;place-items:center;padding:50px}.form{width:min(420px,100%)}.form h1{margin:14px 0 8px;font:54px/.95 var(--display);letter-spacing:-.05em}.form>p{color:var(--muted);margin-bottom:38px}.form :deep(.el-button){width:100%;margin:24px 0}.form>a{font-size:12px;color:var(--muted)}@media(max-width:800px){.login{grid-template-columns:1fr}.visual{min-height:260px}.core{width:220px}.core>i{width:70px;border-radius:22px}.core>span,.telemetry{display:none}.form-panel{padding:45px 22px}.form h1{font-size:42px}}</style>
