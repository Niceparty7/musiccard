import { chromium } from 'playwright-core'

const baseURL = process.env.MUSICCARD_WEB_URL || 'http://localhost:5173'
const phone = process.env.MUSICCARD_TEST_PHONE
const password = process.env.MUSICCARD_TEST_PASSWORD
const executablePath = process.env.PLAYWRIGHT_CHROME || 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe'
if (!phone || !password) throw new Error('请设置 MUSICCARD_TEST_PHONE 和 MUSICCARD_TEST_PASSWORD')

const browser = await chromium.launch({ headless: true, executablePath })
const context = await browser.newContext({ baseURL, viewport: { width: 1440, height: 1000 } })
const page = await context.newPage()
const stamp = Date.now()
const names = {
  tag: `pw_tag_${stamp}`,
  tagUpdated: `pw_tag_updated_${stamp}`,
  category: `pw_category_${stamp}`,
  categoryUpdated: `pw_category_updated_${stamp}`,
  music: `pw_music_${stamp}`,
  musicUpdated: `pw_music_updated_${stamp}`
}
const ids = { tag: null, category: null, music: null }
const report = { steps: [], apiErrors: [] }

page.on('response', async response => {
  if (!response.url().includes('/api/')) return
  if (response.status() >= 400) report.apiErrors.push(`${response.request().method()} ${response.url()} -> ${response.status()}`)
})

async function api(path, options = {}) {
  return page.evaluate(async ({ path, options }) => {
    const response = await fetch(path, { credentials: 'include', ...options })
    return { http: response.status, body: await response.json().catch(() => null) }
  }, { path, options })
}

async function waitMutation(path, method, action) {
  const waiting = page.waitForResponse(response => response.url().includes(path) && response.request().method() === method)
  const [response] = await Promise.all([waiting, action()])
  const body = await response.json().catch(() => null)
  if (response.status() !== 200 || body?.status?.code !== 1001) throw new Error(`${method} ${path} 失败: HTTP ${response.status()} ${JSON.stringify(body)}`)
  return body
}

async function login() {
  await page.goto('/console/login?redirect=%2Fconsole%2Ftags', { waitUntil: 'networkidle' })
  if (!page.url().includes('/console/login')) return
  await page.getByLabel('管理员手机').fill(phone)
  await page.getByLabel('访问密码').fill(password)
  await page.getByRole('button', { name: /验证并进入/ }).click()
  await page.waitForURL(url => url.pathname === '/console/tags', { timeout: 15000 })
  report.steps.push('Console 登录成功')
}

async function testTag() {
  await page.goto('/console/tags', { waitUntil: 'networkidle' })
  await page.getByRole('button', { name: /新建标签/ }).click()
  await page.getByLabel('标签名称').fill(names.tag)
  await page.getByLabel('标签描述').fill('Playwright 临时标签')
  await waitMutation('/api/console/tag/create', 'POST', () => page.getByRole('button', { name: '保存', exact: true }).click())
  await page.getByText(`# ${names.tag}`, { exact: true }).waitFor()
  ids.tag = (await api('/api/console/tag/list')).body.result.list.find(item => item.tag === names.tag)?.id
  if (!ids.tag) throw new Error('新增标签已显示，但列表未返回 id')
  report.steps.push(`标签新增成功 id=${ids.tag}`)

  const card = page.locator('.tag-cloud article').filter({ hasText: names.tag })
  await card.getByRole('button', { name: '编辑' }).click()
  await page.getByLabel('标签名称').fill(names.tagUpdated)
  await waitMutation('/api/console/tag/update', 'PUT', () => page.getByRole('button', { name: '保存', exact: true }).click())
  await page.getByText(`# ${names.tagUpdated}`, { exact: true }).waitFor()
  report.steps.push('标签修改成功')

  const updatedCard = page.locator('.tag-cloud article').filter({ hasText: names.tagUpdated })
  await updatedCard.getByRole('button', { name: '删除' }).click()
  await waitMutation('/api/console/tag/delete', 'DELETE', () => page.locator('.el-message-box__btns .el-button--primary').click())
  await page.getByText(`# ${names.tagUpdated}`, { exact: true }).waitFor({ state: 'detached' })
  ids.tag = null
  report.steps.push('标签删除成功')
}

async function testCategory() {
  await page.goto('/console/categories', { waitUntil: 'networkidle' })
  await page.getByRole('button', { name: /新建分类/ }).click()
  await page.getByLabel('分类名称').fill(names.category)
  await page.getByLabel('分类封面').fill('https://example.com/musiccard-playwright-category.jpg')
  await page.getByLabel('分类描述').fill('Playwright 临时分类')
  await waitMutation('/api/console/category/create', 'POST', () => page.getByRole('button', { name: '保存', exact: true }).click())
  await page.getByRole('heading', { name: names.category, exact: true }).waitFor()
  ids.category = (await api('/api/console/category/list')).body.result.list.find(item => item.typeName === names.category)?.id
  if (!ids.category) throw new Error('新增分类已显示，但列表未返回 id')
  report.steps.push(`分类新增成功 id=${ids.category}`)

  const card = page.locator('.category-grid article').filter({ hasText: names.category })
  await card.getByRole('button', { name: '编辑' }).click()
  await page.getByLabel('分类名称').fill(names.categoryUpdated)
  await waitMutation('/api/console/category/update', 'PUT', () => page.getByRole('button', { name: '保存', exact: true }).click())
  await page.getByRole('heading', { name: names.categoryUpdated, exact: true }).waitFor()
  report.steps.push('分类修改成功')

  const updatedCard = page.locator('.category-grid article').filter({ hasText: names.categoryUpdated })
  await updatedCard.getByRole('button', { name: '删除' }).click()
  await waitMutation('/api/console/category/delete', 'DELETE', () => page.locator('.el-message-box__btns .el-button--primary').click())
  await page.getByRole('heading', { name: names.categoryUpdated, exact: true }).waitFor({ state: 'detached' })
  ids.category = null
  report.steps.push('分类删除成功')
}

async function testMusic() {
  await page.goto('/console/music', { waitUntil: 'networkidle' })
  await page.getByRole('button', { name: /新增音乐/ }).click()
  await page.getByLabel('歌曲名称').fill(names.music)
  await page.getByLabel('音乐人').fill('Playwright Artist')
  await page.getByLabel('封面 URL').fill('https://example.com/musiccard-playwright-music.jpg')
  const created = await waitMutation('/api/console/music/create', 'POST', () => page.getByRole('button', { name: '保存内容' }).click())
  ids.music = Number(String(created.result).match(/\d+/)?.[0]) || null
  if (!ids.music) throw new Error(`音乐新增成功但未解析到 id: ${created.result}`)
  report.steps.push(`未选分类的音乐新增成功 id=${ids.music}`)

  await page.getByPlaceholder('搜索歌曲名称').fill(names.music)
  await page.getByRole('button', { name: '筛选内容' }).click()
  const row = page.locator('.el-table__body tr').filter({ hasText: names.music })
  await row.waitFor()
  await row.getByRole('button', { name: '编辑' }).click()
  await page.getByRole('dialog', { name: '编辑音乐' }).waitFor()
  const typeIdValue = await page.getByLabel('分类 ID').inputValue()
  if (typeIdValue !== '') throw new Error(`未分类音乐的 typeId 应为空，实际为 ${typeIdValue}`)
  await page.getByLabel('歌曲名称').fill(names.musicUpdated)
  await waitMutation('/api/console/music/update', 'PUT', () => page.getByRole('button', { name: '保存内容' }).click())
  report.steps.push('新增后立即修改音乐成功')

  await page.getByPlaceholder('搜索歌曲名称').fill(names.musicUpdated)
  await page.getByRole('button', { name: '筛选内容' }).click()
  const updatedRow = page.locator('.el-table__body tr').filter({ hasText: names.musicUpdated })
  await updatedRow.waitFor()
  await updatedRow.getByRole('button', { name: '删除' }).click()
  await waitMutation('/api/console/music/delete', 'DELETE', () => page.locator('.el-message-box__btns .el-button--primary').click())
  ids.music = null
  report.steps.push('音乐删除成功')
}

async function cleanup() {
  if (ids.music) await api(`/api/console/music/delete?id=${ids.music}`, { method: 'DELETE' }).catch(() => null)
  if (ids.category) await api(`/api/console/category/delete?id=${ids.category}`, { method: 'DELETE' }).catch(() => null)
  if (ids.tag) await api(`/api/console/tag/delete?id=${ids.tag}`, { method: 'DELETE' }).catch(() => null)
}

try {
  await login()
  await testTag()
  await testCategory()
  await testMusic()
  report.passed = true
} catch (error) {
  report.passed = false
  report.error = error.stack || String(error)
} finally {
  await cleanup()
  console.log(JSON.stringify(report, null, 2))
  await browser.close()
}

if (!report.passed) process.exitCode = 1
