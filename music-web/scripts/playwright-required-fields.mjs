import { chromium } from 'playwright-core'

const phone = process.env.MUSICCARD_TEST_PHONE
const password = process.env.MUSICCARD_TEST_PASSWORD
if (!phone || !password) throw new Error('请设置 MUSICCARD_TEST_PHONE 和 MUSICCARD_TEST_PASSWORD')
const browser = await chromium.launch({ headless: true, executablePath: 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe' })
const page = await browser.newPage({ baseURL: process.env.MUSICCARD_WEB_URL || 'http://localhost:5173' })
const result = {}

async function hasStar(label) {
  const locator = page.locator('.el-form-item').filter({ has: page.locator(`label:text-is("${label}")`) }).locator('.el-form-item__label')
  const content = await locator.first().evaluate(element => getComputedStyle(element, '::before').content)
  return content.includes('*')
}

try {
  await page.goto('/app/account', { waitUntil: 'networkidle' })
  result.appLoginPhone = await hasStar('手机号码')
  result.appLoginPassword = await hasStar('密码')
  await page.getByRole('button', { name: '注册', exact: true }).click()
  result.appRegisterName = await hasStar('昵称')
  result.appRegisterPhone = await hasStar('手机号码')
  result.appRegisterPassword = await hasStar('密码')
  result.appRegisterGenderOptional = !(await hasStar('性别'))

  await page.goto('/console/login?redirect=%2Fconsole%2Fmusic', { waitUntil: 'networkidle' })
  result.consoleLoginPhone = await hasStar('管理员手机')
  result.consoleLoginPassword = await hasStar('访问密码')
  await page.getByLabel('管理员手机').fill(phone)
  await page.getByLabel('访问密码').fill(password)
  await page.getByRole('button', { name: /验证并进入/ }).click()
  await page.waitForURL(url => url.pathname === '/console/music')

  await page.getByRole('button', { name: /新增音乐/ }).click()
  result.musicName = await hasStar('歌曲名称')
  result.musicSinger = await hasStar('音乐人')
  result.musicCover = await hasStar('封面 URL')
  result.musicAlbumOptional = !(await hasStar('专辑名称'))
  await page.getByRole('button', { name: '取消', exact: true }).click()

  await page.goto('/console/sms', { waitUntil: 'networkidle' })
  result.smsPhone = await hasStar('接收手机号码')
  result.passed = Object.values(result).every(Boolean)
  console.log(JSON.stringify(result, null, 2))
} finally {
  await browser.close()
}

if (!result.passed) process.exitCode = 1
