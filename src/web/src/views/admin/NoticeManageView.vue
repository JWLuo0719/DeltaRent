<template>
  <div class="panel-card">
    <h2 class="section-title">公告管理</h2>
    <el-table :data="notices" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '显示' : '隐藏' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const notices = ref([
  { id: 1, title: '租赁须知', content: '下单前请先确认租赁时长、账号标签和交付说明。', status: 1 },
  { id: 2, title: '测试说明', content: '当前为课程原型环境，所有数据均为模拟演示数据。', status: 1 }
]);
</script>