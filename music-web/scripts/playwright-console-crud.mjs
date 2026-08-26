import { chromium } from 'playwright-core'

const baseURL = process.env.MUSICCARD_WEB_URL || 'http://localhost:5173'
const phone = process.env.MUSICCARD_TEST_PHONE
const password = process.env.MUSICCARD_TEST_PASSWORD
const executablePath = process.env.PLAYWRIGHT_CHROME || 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe'

if (!phone || !password) throw new Error('请设置 MUSICCARD_TEST_PHONE 和 MUSICCARD_TEST_PASSWORD')

const browser = await chromium.launch({ headless: true, executablePath })
const context = await browser.newContext({ baseURL, viewport: { width: 1440, height: 1000 } })
const page = await context.newPage()
const report = { consoleErrors: [], failedRequests: [] }
page.on('console', message => {
  if (message.type() === 'error') report.consoleErrors.push(message.text())
})
page.on('requestfailed', request => report.failedRequests.push(`${request.method()} ${request.url()} ${request.failure()?.errorText}`))

async function responseJson(response) {
  try { return await response.json() } catch { return null }
}

try {
  await page.goto('/console/login?redirect=%2Fconsole%2Ftags', { waitUntil: 'networkidle' })
  if (page.url().includes('/console/login')) {
    await page.getByLabel('管理员手机').fill(phone)
    await page.getByLabel('访问密码').fill(password)
    const loginWait = page.waitForResponse(response => response.url().includes('/api/user/user/login/web'))
    await page.getByRole('button', { name: /验证并进入/ }).click()
    const loginResponse = await loginWait
    report.loginHttp = loginResponse.status()
    report.loginBody = await responseJson(loginResponse)
    await page.waitForURL(url => url.pathname === '/console/tags', { timeout: 15000 }).catch(async error => {
      report.loginFailureUrl = page.url()
      report.loginMessages = await page.locator('.el-message').allTextContents()
      console.log(JSON.stringify(report, null, 2))
      throw error
    })
  }
  report.loginReachedTags = new URL(page.url()).pathname === '/console/tags'

  const initialList = await page.evaluate(() => fetch('/api/console/tag/list', { credentials: 'include' }).then(response => response.json()))
  const initialRows = initialList?.result?.list || []
  report.tagListContainsId = initialRows.length > 0 && initialRows.every(row => row.id != null)

  await page.getByRole('button', { name: /新建标签/ }).click()
  const tagLabel = page.locator('.el-dialog .el-form-item').filter({ hasText: '标签名称' }).locator('.el-form-item__label')
  report.tagNameRequiredMark = await tagLabel.evaluate(element => getComputedStyle(element, '::before').content)
  const testName = `pw_tag_${Date.now()}`
  await page.getByLabel('标签名称').fill(testName)
  await page.getByLabel('标签描述').fill('Playwright 临时测试，执行后清理')
  const createWait = page.waitForResponse(response => response.url().includes('/api/console/tag/create') && response.request().method() === 'POST')
  await page.getByRole('button', { name: '保存', exact: true }).click()
  const createResponse = await createWait
  const createBody = await responseJson(createResponse)
  report.tagCreateHttp = createResponse.status()
  report.tagCreateCode = createBody?.status?.code
  await page.waitForTimeout(600)
  report.tagVisibleAfterRefresh = await page.getByText(`# ${testName}`, { exact: true }).isVisible().catch(() => false)

  const cleanup = await page.evaluate(async name => {
    for (let id = 1; id <= 200; id += 1) {
      try {
        const info = await fetch(`/api/console/tag/info?id=${id}`, { credentials: 'include' }).then(response => response.json())
        if (info?.status?.code === 1001 && info?.result?.tagName === name) {
          const removed = await fetch(`/api/console/tag/delete?id=${id}`, { method: 'DELETE', credentials: 'include' }).then(response => response.json())
          return { id, code: removed?.status?.code }
        }
      } catch {}
    }
    return null
  }, testName)
  report.cleanup = cleanup

  await page.goto('/console/categories', { waitUntil: 'networkidle' })
  const categoryPayload = await page.evaluate(() => fetch('/api/console/category/list', { credentials: 'include' }).then(response => response.json()))
  const categories = categoryPayload?.result?.list || []
  report.categoryListContainsId = categories.length > 0 && categories.every(row => row.id != null)
  await page.getByRole('button', { name: /新建分类/ }).click()
  for (const label of ['分类名称', '分类封面', '分类描述']) {
    const locator = page.locator('.el-dialog .el-form-item').filter({ hasText: label }).locator('.el-form-item__label')
    report[`${label}RequiredMark`] = await locator.evaluate(element => getComputedStyle(element, '::before').content)
  }

  await page.goto('/console/music', { waitUntil: 'networkidle' })
  const musicPayload = await page.evaluate(() => fetch('/api/console/music/list?page=1', { credentials: 'include' }).then(response => response.json()))
  const musicRows = musicPayload?.result?.list || []
  report.musicListContainsId = musicRows.length > 0 && musicRows.every(row => row.id != null)

  console.log(JSON.stringify(report, null, 2))
} finally {
  await browser.close()
}
