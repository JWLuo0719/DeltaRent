<template>
  <div class="page-shell stack">
    <section class="hero-card">
      <h1 class="page-title">账号租赁大厅</h1>
      <p class="page-subtitle">当前页面已接入列表接口，用于联调账号展示、筛选和下单入口。</p>
    </section>

    <section class="table-card">
      <div class="toolbar" style="margin-bottom: 16px;">
        <el-input v-model="keyword" placeholder="搜索账号标签" style="max-width: 260px;" />
        <el-select v-model="duration" placeholder="时长筛选" style="max-width: 180px;">
          <el-option label="1小时" value="1h" />
          <el-option label="6小时" value="6h" />
          <el-option label="24小时" value="24h" />
        </el-select>
        <el-button type="primary" @click="loadRentals">刷新列表</el-button>
      </div>
      <el-table :data="filteredProducts" style="width: 100%;" v-loading="loading">
        <el-table-column prop="name" label="账号" />
        <el-table-column prop="tag" label="标签" />
        <el-table-column prop="price" label="参考价格" />
        <el-table-column prop="coinAmount" label="哈夫币" />
        <el-table-column prop="equipmentLevel" label="装备水平" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作" width="180">
          <template #default>
            <el-button type="primary" link @click="$router.push('/orders/create')">立即租用</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import http from '@/api/http';
import type { ApiResponse, RentalProduct } from '@/types/api';

const keyword = ref('');
const duration = ref('');
const loading = ref(false);
const products = ref<RentalProduct[]>([]);

const filteredProducts = computed(() =>
  products.value.filter((item) => {
    if (!keyword.value) {
      return true;
    }

    return [item.name, item.tag, item.coinAmount, item.equipmentLevel].some((field) =>
      field.toLowerCase().includes(keyword.value.toLowerCase())
    );
  })
);

async function loadRentals() {
  loading.value = true;
  try {
    const response = await http.get<ApiResponse<RentalProduct[]>>('/rentals', {
      params: {
        duration: duration.value || undefined
      }
    });
    if (response.data.success) {
      products.value = response.data.data;
      return;
    }

    ElMessage.error(response.data.message || '账号列表加载失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '账号列表加载失败');
  } finally {
    loading.value = false;
  }
}

onMounted(loadRentals);
</script>
