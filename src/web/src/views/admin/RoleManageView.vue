<template>
  <div class="panel-card">
    <h2 class="section-title">角色管理</h2>
    <el-table :data="roles" stripe>
      <el-table-column prop="roleCode" label="角色代码" />
      <el-table-column prop="roleName" label="角色名称" />
      <el-table-column prop="desc" label="说明" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const roles = ref([
  { roleCode: 'ADMIN', roleName: '管理员', desc: '系统管理员，拥有全部权限' },
  { roleCode: 'CS', roleName: '客服', desc: '客服/审核员，可处理订单和售后' },
  { roleCode: 'USER', roleName: '普通用户', desc: '注册用户，可租赁账号和查看订单' }
]);
</script>