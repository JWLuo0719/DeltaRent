<template>
  <div class="panel-card">
    <h2 class="section-title">账号管理</h2>
    <el-table :data="products" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="账号名称" />
      <el-table-column prop="tag" label="标签" />
      <el-table-column prop="price" label="价格" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 'AVAILABLE' ? 'success' : 'warning'" size="small">
            {{ row.status === 'AVAILABLE' ? '可租' : '维护中' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const products = ref([
  { id: 1001, name: '高战账号 A01', tag: '满配仓库', price: '¥28/小时', status: 'AVAILABLE' },
  { id: 1002, name: '活动账号 B02', tag: '稀有外观', price: '¥18/小时', status: 'AVAILABLE' },
  { id: 1003, name: '新手体验号 C03', tag: '新手试用', price: '¥9/小时', status: 'MAINTENANCE' }
]);
</script>