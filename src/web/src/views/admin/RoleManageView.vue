<template>
  <div class="admin-card">
    <div class="panel-header">
      <h2 class="section-title">角色管理</h2>
      <el-button type="primary" @click="openCreate">新增角色</el-button>
    </div>

    <el-table v-loading="loading" :data="roles">
      <el-table-column prop="roleCode" label="角色代码" width="140" />
      <el-table-column prop="roleName" label="角色名称" width="140" />
      <el-table-column prop="description" label="说明" min-width="260" />
      <el-table-column prop="userCount" label="用户数" width="100" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingRoleCode ? '编辑角色' : '新增角色'" width="520px">
      <el-form label-position="top">
        <el-form-item label="角色代码">
          <el-input v-model="form.roleCode" :disabled="!!editingRoleCode" placeholder="如 CS_SUPPORT" />
        </el-form-item>
        <el-form-item label="角色名称">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入角色说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitRole">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { createAdminRole, getAdminRoles, updateAdminRole } from '@/api';
import type { AdminRole } from '@/types/api';

const loading = ref(true);
const saving = ref(false);
const dialogVisible = ref(false);
const editingRoleCode = ref('');
const roles = ref<AdminRole[]>([]);

const form = reactive({
  roleCode: '',
  roleName: '',
  description: ''
});

async function loadRoles() {
  loading.value = true;
  try {
    const response = await getAdminRoles();
    if (response.data.success) {
      roles.value = response.data.data;
      return;
    }
    ElMessage.error(response.data.message || '角色列表加载失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '角色列表加载失败');
  } finally {
    loading.value = false;
  }
}

function openCreate() {
  editingRoleCode.value = '';
  form.roleCode = '';
  form.roleName = '';
  form.description = '';
  dialogVisible.value = true;
}

function openEdit(role: AdminRole) {
  editingRoleCode.value = role.roleCode;
  form.roleCode = role.roleCode;
  form.roleName = role.roleName;
  form.description = role.description;
  dialogVisible.value = true;
}

async function submitRole() {
  if (!form.roleCode.trim() || !form.roleName.trim()) {
    ElMessage.warning('请填写角色代码和角色名称');
    return;
  }

  saving.value = true;
  try {
    const response = editingRoleCode.value
      ? await updateAdminRole(editingRoleCode.value, {
          roleName: form.roleName.trim(),
          description: form.description.trim()
        })
      : await createAdminRole({
          roleCode: form.roleCode.trim().toUpperCase(),
          roleName: form.roleName.trim(),
          description: form.description.trim()
        });

    if (response.data.success) {
      ElMessage.success(editingRoleCode.value ? '角色已更新' : '角色已创建');
      dialogVisible.value = false;
      await loadRoles();
      return;
    }
    ElMessage.error(response.data.message || '保存失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败');
  } finally {
    saving.value = false;
  }
}

onMounted(loadRoles);
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
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

:deep(.el-input__wrapper) {
  background: #fff !important;
  box-shadow: 0 0 0 1px #dbe3ef inset !important;
}
:deep(.el-input__inner) { color: #1f2937 !important; }
:deep(.el-dialog) {
  background: #fffef9 !important;
  border: 1px solid #e2e8f0 !important;
  border-radius: 16px !important;
}
:deep(.el-dialog__title) { color: #1f2937 !important; }
:deep(.el-form-item__label) { color: #64748b !important; }
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
:deep(.el-table__body tr:hover > td) {
  background: #fff7e6 !important;
}
:deep(.el-table td.el-table__cell) {
  border-bottom-color: #e2e8f0 !important;
}
</style>
