<template>
  <div class="panel-card">
    <h2 class="section-title">订单管理</h2>
    <div class="filter-bar">
      <el-select v-model="filterStatus" placeholder="订单状态" clearable>
        <el-option label="全部" value="" />
        <el-option label="待确认" value="WAITING_CONFIRM" />
        <el-option label="进行中" value="IN_PROGRESS" />
        <el-option label="已完成" value="COMPLETED" />
      </el-select>
    </div>
    <el-table :data="orders" stripe>
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column prop="user" label="用户" />
      <el-table-column prop="item" label="账号" />
      <el-table-column prop="rentHours" label="时长" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const filterStatus = ref('');

const orders = ref([
  { orderNo: 'DR20260423001', user: 'jwluo', item: '高战账号 A01', rentHours: 2, status: 'WAITING_CONFIRM' },
  { orderNo: 'DR20260423002', user: 'test_user', item: '活动账号 B02', rentHours: 1, status: 'IN_PROGRESS' }
]);

const statusMap: Record<string, string> = {
  WAITING_CONFIRM: '待确认',
  IN_PROGRESS: '进行中',
  COMPLETED: '已完成',
  CANCELLED: '已取消'
};

function statusText(s: string) { return statusMap[s] || s; }
function statusType(s: string) {
  return s === 'WAITING_CONFIRM' ? 'warning' : s === 'IN_PROGRESS' ? 'primary' : 'success';
}
</script>