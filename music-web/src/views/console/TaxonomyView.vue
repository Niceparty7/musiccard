<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AlbumArtwork from '../../components/AlbumArtwork.vue'
import { consoleApi } from '../../api/console'

const props = defineProps({ mode: String })
const rows = ref([])
const dialog = ref(false)
const loading = ref(false)
const saving = ref(false)
const isCategory = computed(() => props.mode === 'category')
const title = computed(() => isCategory.value ? '分类声场' : '标签系统')
const form = reactive({ id: null, typeName: '', typeImage: '', typeDesc: '', parentId: 0, tagName: '', tagDesc: '' })

async function load() {
  loading.value = true
  try {
    const { result } = isCategory.value ? await consoleApi.categories() : await consoleApi.tags()
    rows.value = result.list || []
  } catch (error) {
    ElMessage.error(error?.message || '列表加载失败')
  } finally {
    loading.value = false
  }
}

function open(row) {
  Object.assign(form, { id: null, typeName: '', typeImage: '', typeDesc: '', parentId: 0, tagName: '', tagDesc: '', ...(row || {}) })
  if (!isCategory.value && row?.tag) form.tagName = row.tag
  dialog.value = true
}

function validate() {
  if (isCategory.value) {
    if (!form.typeName.trim()) return '请输入分类名称'
    if (!form.typeImage.trim()) return '请输入分类封面 URL'
    if (!form.typeDesc.trim()) return '请输入分类描述'
  } else if (!form.tagName.trim()) return '请输入标签名称'
  return ''
}

async function save() {
  const message = validate()
  if (message) return ElMessage.warning(message)
  saving.value = true
  try {
    if (isCategory.value) await consoleApi.saveCategory(form)
    else await consoleApi.saveTag(form)
    ElMessage.success(form.id ? '已更新' : '已创建')
    dialog.value = false
    await load()
  } catch (error) {
    ElMessage.error(error?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function remove(row) {
  if (!row.id) return ElMessage.warning('接口未返回记录 ID，无法安全删除')
  try {
    await ElMessageBox.confirm(`确认删除“${row.typeName || row.tagName || row.tag}”？`, '删除确认', { type: 'warning' })
    if (isCategory.value) await consoleApi.deleteCategory(row.id)
    else await consoleApi.deleteTag(row.id)
    ElMessage.success('已删除')
    await load()
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') ElMessage.error(error?.message || '删除失败')
  }
}

watch(() => props.mode, load)
onMounted(load)
</script>

<template>
  <div>
    <div class="top">
      <div><span class="eyebrow">{{ isCategory ? 'FREQUENCY ARCHITECTURE' : 'METADATA NETWORK' }}</span><h1 class="page-title">{{ title }}</h1><p>{{ isCategory ? '定义内容的主声场与子分类结构。' : '用轻量标签建立跨分类的发现路径。' }}</p></div>
      <el-button type="primary" @click="open()">＋ 新建{{ isCategory ? '分类' : '标签' }}</el-button>
    </div>
    <div v-if="isCategory" v-loading="loading" class="category-grid">
      <article v-for="(row, i) in rows" :key="row.id || `${row.typeName}-${i}`">
        <AlbumArtwork :item="{ ...row, id: row.id || i + 1, musicName: row.typeName, ar: 1.45 }" />
        <div class="category-copy"><span>FIELD / {{ String(i + 1).padStart(2, '0') }}</span><h2>{{ row.typeName }}</h2><p>{{ row.typeDesc || '尚未填写分类描述' }}</p><div><el-tag v-for="child in row.children" :key="child.id || child.typeName" size="small" effect="plain" round>{{ child.typeName }}</el-tag></div></div>
        <footer><button @click="open(row)">编辑</button><button @click="remove(row)">删除</button></footer>
      </article>
    </div>
    <section v-else v-loading="loading" class="tag-cloud">
      <article v-for="(row, i) in rows" :key="row.id || `${row.tagName || row.tag}-${i}`">
        <span class="dot" :style="{ background: ['#30d9ff', '#7c6cff', '#ff6f91', '#b8f35b'][i % 4] }"></span>
        <div><h3># {{ row.tagName || row.tag }}</h3><p>{{ row.tagDesc || '用于发现相近声音与情绪' }}</p></div>
        <b>{{ String((i + 1) * 18).padStart(3, '0') }}<small>关联音乐</small></b>
        <div><el-button link @click="open(row)">编辑</el-button><el-button link type="danger" @click="remove(row)">删除</el-button></div>
      </article>
    </section>
    <el-dialog v-model="dialog" :title="`${form.id ? '编辑' : '新建'}${isCategory ? '分类' : '标签'}`" width="min(560px,92vw)">
      <el-form label-position="top">
        <template v-if="isCategory">
          <el-form-item label="分类名称" required><el-input v-model="form.typeName" /></el-form-item>
          <el-form-item label="分类封面" required><el-input v-model="form.typeImage" placeholder="图片 URL" /></el-form-item>
          <el-form-item label="父分类 ID"><el-input-number v-model="form.parentId" :min="0" /></el-form-item>
          <el-form-item label="分类描述" required><el-input v-model="form.typeDesc" type="textarea" :rows="4" /></el-form-item>
        </template>
        <template v-else>
          <el-form-item label="标签名称" required><el-input v-model="form.tagName" /></el-form-item>
          <el-form-item label="标签描述"><el-input v-model="form.tagDesc" type="textarea" :rows="4" /></el-form-item>
        </template>
      </el-form>
      <template #footer><el-button @click="dialog = false">取消</el-button><el-button type="primary" :loading="saving" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.top{display:flex;align-items:end;justify-content:space-between;margin-bottom:30px}.top p{color:var(--muted)}.category-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:17px;min-height:160px}.category-grid article{position:relative;background:#fff;border:1px solid var(--line);border-radius:22px;overflow:hidden}.category-grid :deep(.art){border-radius:0;min-height:210px}.category-copy{padding:19px}.category-copy>span{font:8px var(--mono);letter-spacing:.14em;color:var(--violet)}.category-copy h2{font:28px var(--display);margin:7px 0}.category-copy p{min-height:40px;color:var(--muted);font-size:11px}.category-copy>div{display:flex;gap:6px}.category-grid footer{display:flex;border-top:1px solid var(--line)}.category-grid footer button{flex:1;padding:11px;border:0;background:#fff;font-size:11px;cursor:pointer}.category-grid footer button+button{border-left:1px solid var(--line);color:#db5070}.tag-cloud{display:grid;grid-template-columns:1fr 1fr;gap:13px;min-height:160px}.tag-cloud article{display:grid;grid-template-columns:12px 1fr auto auto;align-items:center;gap:15px;padding:20px;background:#fff;border:1px solid var(--line);border-radius:18px}.dot{width:9px;height:9px;border-radius:50%;box-shadow:0 0 10px currentColor}.tag-cloud h3{margin:0;font-size:14px}.tag-cloud p{margin:5px 0 0;color:var(--muted);font-size:10px}.tag-cloud>b{font:24px var(--display);text-align:right}.tag-cloud b small{display:block;font:8px var(--mono);color:var(--muted)}@media(max-width:1000px){.category-grid{grid-template-columns:1fr 1fr}.tag-cloud{grid-template-columns:1fr}}@media(max-width:600px){.top{align-items:start}.top .el-button{margin-top:5px}.category-grid{grid-template-columns:1fr}.tag-cloud article{grid-template-columns:10px 1fr auto}.tag-cloud article>div:last-child{grid-column:2/4}.category-grid :deep(.art){min-height:180px}}
</style>
