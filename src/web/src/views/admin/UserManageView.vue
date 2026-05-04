<template>
  <div class="admin-card">
    <div class="panel-header">
      <h2 class="section-title">用户管理</h2>
      <div class="toolbar">
        <el-input v-model="filters.phone" placeholder="按手机号搜索" clearable class="toolbar-item" />
        <el-select v-model="filters.role" placeholder="角色" clearable class="toolbar-item">
          <el-option label="管理员" value="ADMIN" />
          <el-option label="客服" value="CS" />
          <el-option label="普通用户" value="USER" />
        </el-select>
        <el-select v-model="filters.status" placeholder="状态" clearable class="toolbar-item">
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" @click="loadUsers">查询</el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="users">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="phone" label="手机号" min-width="140" />
      <el-table-column prop="nickname" label="昵称" min-width="140" />
      <el-table-column prop="role" label="角色" width="120">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : row.role === 'CS' ? 'warning' : 'info'" size="small">
            {{ roleText(row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" min-width="180" />
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-select
            :model-value="row.role"
            size="small"
            class="inline-select"
            @change="onRoleSelect(row.id, $event)"
          >
            <el-option label="管理员" value="ADMIN" />
            <el-option label="客服" value="CS" />
            <el-option label="普通用户" value="USER" />
          </el-select>
          <el-button
            size="small"
            :type="row.status === 1 ? 'warning' : 'success'"
            @click="handleStatusToggle(row)"
          >
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrap">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @current-change="loadUsers"
        @size-change="loadUsers"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { getAdminUsers, updateAdminUserRole, updateAdminUserStatus } from '@/api';
import type { AdminUser } from '@/types/api';

const loading = ref(true);
const users = ref<AdminUser[]>([]);
const page = ref(1);
const pageSize = ref(10);
const total = ref(0);

const filters = reactive({
  phone: '',
  role: '',
  status: undefined as number | undefined
});

function roleText(role: string) {
  const map: Record<string, string> = { ADMIN: '管理员', CS: '客服', USER: '普通用户' };
  return map[role] || role;
}

async function loadUsers() {
  loading.value = true;
  try {
    const response = await getAdminUsers({
      page: page.value,
      pageSize: pageSize.value,
      phone: filters.phone || undefined,
      role: filters.role || undefined,
      status: filters.status
    });
    if (response.data.success) {
      users.value = response.data.data.list;
      total.value = response.data.data.total;
      return;
    }
    ElMessage.error(response.data.message || '用户列表加载失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '用户列表加载失败');
  } finally {
    loading.value = false;
  }
}

async function handleRoleChange(id: number, role: string) {
  const response = await updateAdminUserRole(id, role);
  if (response.data.success) {
    ElMessage.success('角色已更新');
    await loadUsers();
    return;
  }
  ElMessage.error(response.data.message || '角色更新失败');
}

function onRoleSelect(id: number, value: string | number | boolean) {
  return handleRoleChange(id, String(value));
}

async function handleStatusToggle(row: AdminUser) {
  const nextStatus = row.status === 1 ? 0 : 1;
  const response = await updateAdminUserStatus(row.id, nextStatus);
  if (response.data.success) {
    ElMessage.success(nextStatus === 1 ? '用户已启用' : '用户已禁用');
    await loadUsers();
    return;
  }
  ElMessage.error(response.data.message || '状态更新失败');
}

onMounted(loadUsers);
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
  gap: 16px;
  margin-bottom: 16px;
}

.toolbar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.toolbar-item {
  width: 180px;
}

.inline-select {
  width: 110px;
  margin-right: 8px;
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-input__wrapper) {
  background: #1e293b !important;
  border-color: rgba(96, 165, 250, 0.2) !important;
  box-shadow: none !important;
}
:deep(.el-input__inner) { color: #e2e8f0 !important; }
:deep(.el-input__inner::placeholder) { color: #475569 !important; }
:deep(.el-select .el-input__wrapper) {
  background: #1e293b !important;
  border-color: rgba(96, 165, 250, 0.2) !important;
}
:deep(.el-select__wrapper) {
  background: #1e293b !important;
  border-color: rgba(96, 165, 250, 0.2) !important;
  box-shadow: none !important;
}
:deep(.el-select__placeholder) { color: #64748b !important; }
:deep(.el-select-dropdown) {
  background: #1e293b !important;
  border: 1px solid rgba(96, 165, 250, 0.2) !important;
  border-radius: 12px !important;
}
:deep(.el-select-dropdown__item) { color: #e2e8f0 !important; }
:deep(.el-select-dropdown__item:hover) { background: rgba(96, 165, 250, 0.1) !important; }
:deep(.el-pagination) { --el-pagination-bg-color: transparent !important; }
:deep(.el-pagination button) { background: rgba(255,255,255,0.05) !important; border-color: rgba(96,165,250,0.15) !important; color: #94a3b8 !important; }
:deep(.el-pagination button:hover) { background: rgba(96,165,250,0.15) !important; color: #60a5fa !important; }
:deep(.el-pager li) { background: rgba(255,255,255,0.05) !important; border-color: rgba(96,165,250,0.15) !important; color: #94a3b8 !important; }
:deep(.el-pager li:hover) { color: #60a5fa !important; }
:deep(.el-pager li.is-active) { background: #1e40af !important; border-color: #1e40af !important; color: #fff !important; }
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
