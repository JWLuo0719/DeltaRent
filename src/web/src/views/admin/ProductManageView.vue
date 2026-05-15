<template>
  <div class="admin-card">
    <div class="panel-header">
      <div>
        <h2 class="section-title">账号管理</h2>
        <p class="section-desc">维护租号大厅的基础字段与扩展展示字段。</p>
      </div>
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索账号名称" clearable class="toolbar-item" />
        <el-button @click="loadProducts">查询</el-button>
        <el-button type="warning" @click="openCreate">新增账号</el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="products" class="product-table">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="账号名称" min-width="220" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="rankText" label="段位" width="110" />
      <el-table-column prop="loginRegion" label="常用地区" width="120" />
      <el-table-column prop="hourPrice" label="价格" width="110">
        <template #default="{ row }">¥{{ Number(row.hourPrice).toFixed(2) }}/小时</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 'AVAILABLE' ? 'success' : row.status === 'MAINTENANCE' ? 'warning' : 'info'" size="small">
            {{ statusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" @click="toggleStatus(row)">
            {{ row.status === 'AVAILABLE' ? '维护' : '上架' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑账号' : '新增账号'" width="920px">
      <el-form label-position="top">
        <div class="grid-3">
          <el-form-item label="账号名称">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="分类">
            <el-input v-model="form.category" />
          </el-form-item>
          <el-form-item label="标签">
            <el-input v-model="form.tagText" placeholder="多个标签用英文逗号分隔" />
          </el-form-item>
        </div>

        <div class="grid-3">
          <el-form-item label="小时价">
            <el-input-number v-model="form.hourPrice" :min="0" :precision="2" class="full-width" />
          </el-form-item>
          <el-form-item label="哈夫币">
            <el-input-number v-model="form.coinAmount" :min="0" class="full-width" />
          </el-form-item>
          <el-form-item label="仓库估值">
            <el-input v-model="form.warehouseValueText" />
          </el-form-item>
        </div>

        <div class="grid-3">
          <el-form-item label="装备等级">
            <el-input v-model="form.equipmentLevelText" />
          </el-form-item>
          <el-form-item label="比例">
            <el-input v-model="form.ratioText" placeholder="例如 1:35" />
          </el-form-item>
          <el-form-item label="保险箱">
            <el-input v-model="form.insuranceBoxText" placeholder="例如 4格 / 9格" />
          </el-form-item>
        </div>

        <div class="grid-3">
          <el-form-item label="体力">
            <el-input v-model="form.staminaText" placeholder="例如 7体7负" />
          </el-form-item>
          <el-form-item label="负重">
            <el-input v-model="form.weightText" placeholder="例如 60" />
          </el-form-item>
          <el-form-item label="段位">
            <el-input v-model="form.rankText" placeholder="例如 黑鹰 / 北极星" />
          </el-form-item>
        </div>

        <div class="grid-3">
          <el-form-item label="常用登录地">
            <el-input v-model="form.loginRegion" placeholder="例如 四川省 / 福建省" />
          </el-form-item>
          <el-form-item label="武器皮肤">
            <el-input v-model="form.weaponSkinText" placeholder="例如 AWM子弹 / M4皮肤" />
          </el-form-item>
          <el-form-item label="干员皮肤">
            <el-input v-model="form.characterSkinText" placeholder="例如 近战皮肤 / 套装" />
          </el-form-item>
        </div>

        <div class="grid-2">
          <el-form-item label="封面图 URL">
            <el-input v-model="form.coverImageUrl" placeholder="可选，后续可替换为真实截图" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status" class="full-width">
              <el-option label="可租" value="AVAILABLE" />
              <el-option label="维护中" value="MAINTENANCE" />
              <el-option label="已租出" value="RENTED" />
            </el-select>
          </el-form-item>
        </div>

        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="warning" :loading="saving" @click="submitProduct">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { createRental, deleteRental, getRentals, updateRental, updateRentalStatus } from '@/api';
import type { RentalProduct } from '@/types/api';

const loading = ref(true);
const saving = ref(false);
const dialogVisible = ref(false);
const keyword = ref('');
const products = ref<RentalProduct[]>([]);

const emptyForm = (): RentalProduct => ({
  id: 0,
  name: '',
  category: '',
  tagText: '',
  hourPrice: 0,
  coinAmount: 0,
  equipmentLevelText: '',
  warehouseValueText: '',
  ratioText: '',
  insuranceBoxText: '',
  staminaText: '',
  weightText: '',
  rankText: '',
  loginRegion: '',
  weaponSkinText: '',
  characterSkinText: '',
  coverImageUrl: '',
  status: 'AVAILABLE',
  description: ''
});

const form = reactive<RentalProduct>(emptyForm());

function statusText(status: string) {
  return {
    AVAILABLE: '可租',
    MAINTENANCE: '维护中',
    RENTED: '已租出'
  }[status] || status;
}

async function loadProducts() {
  loading.value = true;
  try {
    const response = await getRentals({
      keyword: keyword.value || undefined,
      page: 1,
      pageSize: 1000
    });
    if (response.data.success) {
      products.value = response.data.data.list;
      return;
    }
    ElMessage.error(response.data.message || '账号列表加载失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '账号列表加载失败');
  } finally {
    loading.value = false;
  }
}

function applyForm(product: RentalProduct) {
  Object.assign(form, emptyForm(), product);
}

function openCreate() {
  applyForm(emptyForm());
  dialogVisible.value = true;
}

function openEdit(product: RentalProduct) {
  applyForm(product);
  dialogVisible.value = true;
}

async function submitProduct() {
  if (!form.name.trim()) {
    ElMessage.warning('请先填写账号名称');
    return;
  }

  saving.value = true;
  try {
    const payload = {
      name: form.name.trim(),
      category: form.category?.trim(),
      tagText: form.tagText?.trim(),
      hourPrice: form.hourPrice,
      coinAmount: form.coinAmount,
      equipmentLevelText: form.equipmentLevelText?.trim(),
      warehouseValueText: form.warehouseValueText?.trim(),
      ratioText: form.ratioText?.trim(),
      insuranceBoxText: form.insuranceBoxText?.trim(),
      staminaText: form.staminaText?.trim(),
      weightText: form.weightText?.trim(),
      rankText: form.rankText?.trim(),
      loginRegion: form.loginRegion?.trim(),
      weaponSkinText: form.weaponSkinText?.trim(),
      characterSkinText: form.characterSkinText?.trim(),
      coverImageUrl: form.coverImageUrl?.trim(),
      status: form.status,
      description: form.description?.trim()
    };

    const response = form.id
      ? await updateRental(form.id, payload)
      : await createRental(payload);

    if (response.data.success) {
      ElMessage.success(form.id ? '账号已更新' : '账号已创建');
      dialogVisible.value = false;
      await loadProducts();
      return;
    }
    ElMessage.error(response.data.message || '保存失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败');
  } finally {
    saving.value = false;
  }
}

async function toggleStatus(product: RentalProduct) {
  const nextStatus = product.status === 'AVAILABLE' ? 'MAINTENANCE' : 'AVAILABLE';
  const response = await updateRentalStatus(product.id, nextStatus);
  if (response.data.success) {
    ElMessage.success('状态已更新');
    await loadProducts();
    return;
  }
  ElMessage.error(response.data.message || '状态更新失败');
}

function handleDelete(id: number) {
  ElMessageBox.confirm('确定删除该账号吗？', '提示', { type: 'warning' })
    .then(async () => {
      const response = await deleteRental(id);
      if (response.data.success) {
        ElMessage.success('账号已删除');
        await loadProducts();
        return;
      }
      ElMessage.error(response.data.message || '删除失败');
    })
    .catch(() => {});
}

onMounted(loadProducts);
</script>

<style scoped>
.admin-card {
  background: rgba(255, 255, 255, 0.88);
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 22px;
  padding: 24px;
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.06);
}

.panel-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.section-title {
  margin: 0;
}

.section-desc {
  margin: 8px 0 0;
  color: #64748b;
  font-size: 13px;
}

.toolbar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-item {
  width: 220px;
}

.grid-2,
.grid-3 {
  display: grid;
  gap: 12px;
}

.grid-2 {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.grid-3 {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.full-width {
  width: 100%;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-textarea__inner) {
  background: #fff !important;
  border-radius: 12px !important;
  box-shadow: 0 0 0 1px #dbe3ef inset !important;
}

:deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: #fff;
  --el-table-header-bg-color: #f8fafc;
  --el-table-row-hover-bg-color: #fff7e6;
  --el-table-border-color: #e2e8f0;
  --el-table-text-color: #1f2937;
  --el-table-header-text-color: #64748b;
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-table__header th) {
  background: #f8fafc !important;
}

:deep(.el-dialog) {
  border-radius: 20px !important;
  overflow: hidden !important;
}

:deep(.el-dialog__body) {
  padding-top: 8px !important;
}

:deep(.el-form-item__label) {
  color: #475569 !important;
}

@media (max-width: 900px) {
  .panel-header {
    flex-direction: column;
  }

  .grid-2,
  .grid-3 {
    grid-template-columns: 1fr;
  }
}
</style>
