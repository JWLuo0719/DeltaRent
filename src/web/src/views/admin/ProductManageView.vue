<template>
  <div class="panel-card">
    <div class="panel-header">
      <h2 class="section-title">账号管理</h2>
      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索账号名称" clearable class="toolbar-item" />
        <el-button @click="loadProducts">查询</el-button>
        <el-button type="primary" @click="openCreate">新增账号</el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="products" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="账号名称" min-width="180" />
      <el-table-column prop="tagText" label="标签" min-width="160" />
      <el-table-column prop="hourPrice" label="价格" width="110">
        <template #default="{ row }">￥{{ Number(row.hourPrice).toFixed(2) }}/小时</template>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑账号' : '新增账号'" width="640px">
      <el-form label-position="top">
        <div class="grid-2">
          <el-form-item label="账号名称">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="分类">
            <el-input v-model="form.category" />
          </el-form-item>
        </div>
        <div class="grid-2">
          <el-form-item label="标签">
            <el-input v-model="form.tagText" />
          </el-form-item>
          <el-form-item label="小时价">
            <el-input-number v-model="form.hourPrice" :min="0" :precision="2" class="full-width" />
          </el-form-item>
        </div>
        <div class="grid-2">
          <el-form-item label="金币数量">
            <el-input v-model="form.coinAmountText" />
          </el-form-item>
          <el-form-item label="装备等级">
            <el-input v-model="form.equipmentLevelText" />
          </el-form-item>
        </div>
        <div class="grid-2">
          <el-form-item label="仓库价值">
            <el-input v-model="form.warehouseValueText" />
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
        <el-button type="primary" :loading="saving" @click="submitProduct">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { createRental, deleteRental, getRentals, updateRental, updateRentalStatus } from '@/api';
import type { RentalProduct } from '@/types/api';

const loading = ref(false);
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
  coinAmountText: '',
  equipmentLevelText: '',
  warehouseValueText: '',
  status: 'AVAILABLE',
  description: ''
});

const form = reactive<RentalProduct>(emptyForm());

function statusText(status: string) {
  return { AVAILABLE: '可租', MAINTENANCE: '维护中', RENTED: '已租出' }[status] || status;
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
    ElMessage.warning('请填写账号名称');
    return;
  }

  saving.value = true;
  try {
    const payload = {
      name: form.name.trim(),
      category: form.category?.trim(),
      tagText: form.tagText?.trim(),
      hourPrice: form.hourPrice,
      coinAmountText: form.coinAmountText?.trim(),
      equipmentLevelText: form.equipmentLevelText?.trim(),
      warehouseValueText: form.warehouseValueText?.trim(),
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
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.toolbar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-item {
  width: 220px;
}

.grid-2 {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.full-width {
  width: 100%;
}
</style>
