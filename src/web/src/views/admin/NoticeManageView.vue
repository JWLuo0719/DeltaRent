<template>
  <div class="admin-card">
    <div class="panel-header">
      <h2 class="section-title">公告管理</h2>
      <el-button type="primary" @click="openCreate">新增公告</el-button>
    </div>

    <el-table v-loading="loading" :data="notices">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '显示' : '隐藏' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" @click="toggleStatus(row)">
            {{ row.status === 1 ? '隐藏' : '显示' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑公告' : '新增公告'" width="600px">
      <el-form label-position="top">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="6" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" class="full-width">
            <el-option label="显示" :value="1" />
            <el-option label="隐藏" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitNotice">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { createNotice, deleteNotice, getAdminNotices, updateNotice } from '@/api';
import type { NoticeItem } from '@/types/api';

const loading = ref(true);
const saving = ref(false);
const dialogVisible = ref(false);
const notices = ref<NoticeItem[]>([]);

const emptyForm = (): NoticeItem => ({
  id: 0,
  title: '',
  content: '',
  status: 1
});

const form = reactive<NoticeItem>(emptyForm());

async function loadNotices() {
  loading.value = true;
  try {
    const response = await getAdminNotices();
    if (response.data.success) {
      notices.value = response.data.data;
      return;
    }
    ElMessage.error(response.data.message || '公告列表加载失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '公告列表加载失败');
  } finally {
    loading.value = false;
  }
}

function openCreate() {
  Object.assign(form, emptyForm());
  dialogVisible.value = true;
}

function openEdit(notice: NoticeItem) {
  Object.assign(form, emptyForm(), notice);
  dialogVisible.value = true;
}

async function submitNotice() {
  if (!form.title.trim() || !form.content.trim()) {
    ElMessage.warning('请填写标题和内容');
    return;
  }

  saving.value = true;
  try {
    const payload = {
      title: form.title.trim(),
      content: form.content.trim(),
      status: form.status
    };
    const response = form.id
      ? await updateNotice(form.id, payload)
      : await createNotice(payload);

    if (response.data.success) {
      ElMessage.success(form.id ? '公告已更新' : '公告已创建');
      dialogVisible.value = false;
      await loadNotices();
      return;
    }
    ElMessage.error(response.data.message || '保存失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败');
  } finally {
    saving.value = false;
  }
}

async function toggleStatus(notice: NoticeItem) {
  const response = await updateNotice(notice.id, {
    title: notice.title,
    content: notice.content,
    status: notice.status === 1 ? 0 : 1
  });
  if (response.data.success) {
    ElMessage.success('状态已更新');
    await loadNotices();
    return;
  }
  ElMessage.error(response.data.message || '状态更新失败');
}

function handleDelete(id: number) {
  ElMessageBox.confirm('确定删除该公告吗？', '提示', { type: 'warning' })
    .then(async () => {
      const response = await deleteNotice(id);
      if (response.data.success) {
        ElMessage.success('公告已删除');
        await loadNotices();
        return;
      }
      ElMessage.error(response.data.message || '删除失败');
    })
    .catch(() => {});
}

onMounted(loadNotices);
</script>

<style scoped>
.admin-card {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(96, 165, 250, 0.1);
  border-radius: 16px;
  padding: 24px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.full-width {
  width: 100%;
}

:deep(.el-input__wrapper) {
  background: #1e293b !important;
  border-color: rgba(96, 165, 250, 0.2) !important;
  box-shadow: none !important;
}
:deep(.el-input__inner) { color: #e2e8f0 !important; }
:deep(.el-input__inner::placeholder) { color: #475569 !important; }
:deep(.el-dialog) {
  background: #1e293b !important;
  border: 1px solid rgba(96, 165, 250, 0.2) !important;
  border-radius: 16px !important;
}
:deep(.el-dialog__header) { color: #f1f5f9 !important; }
:deep(.el-dialog__title) { color: #f1f5f9 !important; }
:deep(.el-form-item__label) { color: #94a3b8 !important; }
:deep(.el-table) {
  background: transparent !important;
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: rgba(96, 165, 250, 0.08);
  --el-table-row-hover-bg-color: rgba(96, 165, 250, 0.1);
  --el-table-border-color: rgba(96, 165, 250, 0.15);
  --el-table-text-color: #e2e8f0;
  --el-table-header-text-color: #94a3b8;
}
:deep(.el-table__header th) {
  background: rgba(96, 165, 250, 0.08) !important;
}
:deep(.el-table__body tr) {
  background: transparent !important;
}
:deep(.el-table__body tr:hover > td) {
  background: rgba(96, 165, 250, 0.1) !important;
}
:deep(.el-table td.el-table__cell) {
  border-bottom-color: rgba(96, 165, 250, 0.1) !important;
}
</style>
