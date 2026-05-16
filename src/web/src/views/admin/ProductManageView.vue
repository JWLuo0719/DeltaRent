<template>
  <div class="admin-card">
    <div class="panel-header">
      <div>
        <h2 class="section-title">账号管理</h2>
        <p class="section-desc">维护租号大厅与玩家上架共用的三角洲字段。</p>
      </div>
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索标题、段位、刀皮" clearable class="toolbar-item" />
        <el-button @click="loadProducts">查询</el-button>
        <el-button type="warning" @click="openCreate">新增账号</el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="products" class="product-table">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="标题" min-width="240" />
      <el-table-column prop="loginMethod" label="上号方式" width="120" />
      <el-table-column prop="rankText" label="段位" width="110" />
      <el-table-column prop="insuranceBoxText" label="保险箱" width="100" />
      <el-table-column prop="characterSkinText" label="红皮" min-width="120" />
      <el-table-column prop="hourPrice" label="租金" width="110">
        <template #default="{ row }">¥{{ Number(row.hourPrice || 0).toFixed(0) }}</template>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑账号' : '新增账号'" width="980px">
      <el-form label-position="top">
        <div class="grid-3">
          <el-form-item label="标题">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="上号方式">
            <el-select v-model="form.loginMethod" placeholder="请选择上号方式">
              <el-option v-for="item in LOGIN_METHOD_OPTIONS" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="分类">
            <el-input v-model="form.category" placeholder="例如 黑鹰专区 / 红皮专区" />
          </el-form-item>
        </div>

        <div class="grid-3">
          <el-form-item label="哈夫币(万)">
            <el-input-number v-model="form.coinAmount" :min="0" class="full-width" />
          </el-form-item>
          <el-form-item label="租金">
            <el-input-number v-model="form.hourPrice" :min="0" :precision="2" class="full-width" />
          </el-form-item>
          <el-form-item label="押金">
            <el-input-number v-model="form.deposit" :min="0" :precision="2" class="full-width" />
          </el-form-item>
        </div>

        <div class="grid-3">
          <el-form-item label="保险箱">
            <el-select v-model="form.insuranceBoxText" placeholder="请选择保险箱">
              <el-option v-for="item in INSURANCE_OPTIONS" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="体力等级">
            <el-select v-model="form.staminaText" placeholder="请选择体力等级">
              <el-option v-for="item in STAMINA_WEIGHT_OPTIONS" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="负重等级">
            <el-select v-model="form.weightText" placeholder="请选择负重等级">
              <el-option v-for="item in STAMINA_WEIGHT_OPTIONS" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
        </div>

        <div class="grid-3">
          <el-form-item label="段位">
            <el-select v-model="form.rankText" placeholder="请选择段位">
              <el-option v-for="item in RANK_OPTIONS" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="KD">
            <el-input v-model="form.kdText" placeholder="例如 2.2" />
          </el-form-item>
          <el-form-item label="潜水等级">
            <el-select v-model="form.divingLevelText" placeholder="请选择潜水等级">
              <el-option v-for="item in DIVING_LEVEL_OPTIONS" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
        </div>

        <div class="grid-3">
          <el-form-item label="比例">
            <el-input v-model="form.ratioText" placeholder="例如 1:35" />
          </el-form-item>
          <el-form-item label="方便交易时间">
            <el-input v-model="form.tradeTimeText" placeholder="例如 00:00-24:00" />
          </el-form-item>
          <el-form-item label="租期(天)">
            <el-input-number v-model="form.rentalDays" :min="1" class="full-width" />
          </el-form-item>
        </div>

        <div class="grid-3">
          <el-form-item label="刀皮">
            <el-select v-model="form.knifeSkinText" placeholder="请选择刀皮">
              <el-option v-for="item in KNIFE_SKIN_OPTIONS" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="红皮">
            <el-select v-model="form.characterSkinText" placeholder="请选择红皮">
              <el-option v-for="item in OPERATOR_SKIN_OPTIONS" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="常用登录地区">
            <el-input v-model="form.loginRegion" placeholder="例如 四川省" />
          </el-form-item>
        </div>

        <div class="grid-2">
          <el-form-item label="武器皮肤 / 额外资源">
            <el-input v-model="form.weaponSkinText" placeholder="例如 AWM子弹、六甲、六头" />
          </el-form-item>
          <el-form-item label="封面图 URL">
            <el-input v-model="form.coverImageUrl" placeholder="暂用图片链接" />
          </el-form-item>
        </div>

        <div class="grid-2">
          <el-form-item label="标签">
            <el-input v-model="form.tagText" placeholder="多个标签用英文逗号分隔" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status" class="full-width">
              <el-option label="可租" value="AVAILABLE" />
              <el-option label="待审核 / 维护" value="MAINTENANCE" />
              <el-option label="已租出" value="RENTED" />
            </el-select>
          </el-form-item>
        </div>

        <el-form-item label="仓库价值 / 简介">
          <el-input v-model="form.warehouseValueText" />
        </el-form-item>

        <el-form-item label="备注描述">
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
import {
  DIVING_LEVEL_OPTIONS,
  INSURANCE_OPTIONS,
  KNIFE_SKIN_OPTIONS,
  LOGIN_METHOD_OPTIONS,
  OPERATOR_SKIN_OPTIONS,
  RANK_OPTIONS,
  STAMINA_WEIGHT_OPTIONS
} from '@/constants/rental';

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
  deposit: 0,
  rentalDays: 7,
  equipmentLevelText: '',
  warehouseValueText: '',
  loginMethod: '',
  ratioText: '',
  insuranceBoxText: '',
  staminaText: '',
  weightText: '',
  rankText: '',
  kdText: '',
  divingLevelText: '',
  loginRegion: '',
  tradeTimeText: '',
  knifeSkinText: '',
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
    MAINTENANCE: '待审核 / 维护',
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
    ElMessage.warning('请先填写标题');
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
      deposit: form.deposit,
      rentalDays: form.rentalDays,
      equipmentLevelText: form.equipmentLevelText?.trim() || form.rankText?.trim(),
      warehouseValueText: form.warehouseValueText?.trim(),
      loginMethod: form.loginMethod,
      ratioText: form.ratioText?.trim(),
      insuranceBoxText: form.insuranceBoxText,
      staminaText: form.staminaText,
      weightText: form.weightText,
      rankText: form.rankText,
      kdText: form.kdText?.trim(),
      divingLevelText: form.divingLevelText,
      loginRegion: form.loginRegion?.trim(),
      tradeTimeText: form.tradeTimeText?.trim(),
      knifeSkinText: form.knifeSkinText,
      weaponSkinText: form.weaponSkinText?.trim(),
      characterSkinText: form.characterSkinText,
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
  width: 240px;
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
}

:deep(.el-table__header th) {
  background: #f8fafc !important;
}

:deep(.el-dialog) {
  border-radius: 20px !important;
  overflow: hidden !important;
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
