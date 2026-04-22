<template>
  <div class="page-shell stack">
    <section class="hero-card">
      <h1 class="page-title">资源号租赁大厅</h1>
      <p class="page-subtitle">一期先完成列表、详情、下单、状态跟踪四个核心环节。</p>
    </section>

    <section class="table-card">
      <div class="toolbar" style="margin-bottom: 16px;">
        <el-input placeholder="搜索资源号标签" style="max-width: 260px;" />
        <el-select placeholder="时长筛选" style="max-width: 180px;">
          <el-option label="1小时" value="1h" />
          <el-option label="6小时" value="6h" />
          <el-option label="24小时" value="24h" />
        </el-select>
        <el-button type="primary">筛选</el-button>
      </div>
      <el-table :data="products" style="width: 100%;">
        <el-table-column prop="name" label="资源号" />
        <el-table-column prop="tag" label="标签" />
        <el-table-column prop="price" label="参考价格" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作" width="180">
          <template #default>
            <el-button type="primary" link>查看详情</el-button>
            <el-button type="primary" link>立即租用</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<script setup lang="ts">
const products = [
  { name: '高战资源号 A01', tag: '满配仓库', price: '￥28 / 小时', status: '可租' },
  { name: '回收测试号 B02', tag: '新手体验', price: '￥12 / 小时', status: '可租' },
  { name: '活动资源号 C03', tag: '稀有外观', price: '￥36 / 小时', status: '维护中' }
];
</script>
