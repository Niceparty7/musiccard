import { chromium } from 'playwright-core'

const baseURL = process.env.MUSICCARD_WEB_URL || 'http://localhost:5173'
const phone = process.env.MUSICCARD_TEST_PHONE
const password = process.env.MUSICCARD_TEST_PASSWORD
const executablePath = process.env.PLAYWRIGHT_CHROME || 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe'
const cookieName = 'MUSICCARD_SESSION'

if (!phone || !password) throw new Error('请设置 MUSICCARD_TEST_PHONE 和 MUSICCARD_TEST_PASSWORD')

const browser = await chromium.launch({ headless: true, executablePath })
const context = await browser.newContext({ baseURL, viewport: { width: 1440, height: 900 } })
const page = await context.newPage()
const report = { steps: [] }

async function login() {
  await page.goto('/console/login?redirect=%2Fconsole%2Fdashboard', { waitUntil: 'networkidle' })
  if (!page.url().includes('/console/login')) return
  await page.getByLabel('管理员手机').fill(phone)
  await page.getByLabel('访问密码').fill(password)
  await page.getByRole('button', { name: /验证并进入/ }).click()
  await page.waitForURL(url => url.pathname === '/console/dashboard', { timeout: 15000 })
}

async function sessionCookie() {
  return (await context.cookies()).find(cookie => cookie.name === cookieName)
}

async function logout() {
  await page.getByRole('button', { name: '退出登录' }).click()
  await Promise.all([
    page.waitForURL(url => url.pathname === '/console/login', { timeout: 15000 }),
    page.getByRole('button', { name: '确认退出' }).click()
  ])
}

try {
  await login()
  const firstCookie = await sessionCookie()
  if (!firstCookie?.value) throw new Error('首次登录后未生成 MUSICCARD_SESSION')
  report.steps.push('首次登录已生成 Session Cookie')

  await logout()
  if (await sessionCookie()) throw new Error('退出后 MUSICCARD_SESSION 仍然存在')
  report.steps.push('退出后 Session Cookie 已删除')

  await login()
  const secondCookie = await sessionCookie()
  if (!secondCookie?.value) throw new Error('再次登录后未生成 MUSICCARD_SESSION')
  if (secondCookie.value === firstCookie.value) throw new Error('再次登录沿用了旧 Session Cookie')
  report.steps.push('再次登录已生成不同的新 Session Cookie')

  await logout()
  report.steps.push('测试结束已退出，未遗留登录态')
  report.passed = true
} catch (error) {
  report.passed = false
  report.error = error.stack || String(error)
} finally {
  console.log(JSON.stringify(report, null, 2))
  await browser.close()
}

if (!report.passed) process.exitCode = 1
