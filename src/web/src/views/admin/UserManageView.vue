<template>
  <div class="panel-card">
    <h2 class="section-title">用户管理</h2>
    <el-table :data="users" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="role" label="角色">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'" size="small">
            {{ row.role === 'ADMIN' ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const users = ref([
  { id: 1, phone: '13800000000', nickname: 'Admin Demo User', role: 'ADMIN', status: 1, createdAt: '2026-04-20' },
  { id: 2, phone: '13900000000', nickname: 'Rental Demo User', role: 'USER', status: 1, createdAt: '2026-04-21' }
]);
</script>