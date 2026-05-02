<template>
  <div class="page-shell stack">
    <section class="hero-card">
      <h1 class="page-title">创建账号租赁订单</h1>
      <p class="page-subtitle">当前页面已接入真实后端接口，可直接选择账号并提交订单。</p>
    </section>
    <section class="panel-card">
      <el-form label-position="top">
        <div class="grid-2">
          <el-form-item label="账号编号">
            <el-select v-model="form.accountId" placeholder="请选择账号" filterable :loading="accountsLoading">
              <el-option
                v-for="item in accounts"
                :key="item.id"
                :label="`${item.name}（￥${item.hourPrice}/小时）`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="租赁时长">
            <el-select v-model="form.rentHours" placeholder="请选择时长">
              <el-option label="1小时" :value="1" />
              <el-option label="6小时" :value="6" />
              <el-option label="24小时" :value="24" />
            </el-select>
          </el-form-item>
        </div>
        <div class="grid-2">
          <el-form-item label="联系方式">
            <el-input v-model="form.contactInfo" placeholder="请输入 QQ / 微信 / 手机号" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="form.remark" placeholder="可填写交付时间、使用偏好等补充信息" />
          </el-form-item>
        </div>
        <el-button type="primary" :loading="submitting" @click="submitOrder">提交订单</el-button>
      </el-form>

      <el-alert
        v-if="resultMessage"
        style="margin-top: 20px;"
        type="success"
        :closable="false"
        :title="resultMessage"
      />
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { createOrder, getRentals } from '@/api';
import type { CreateOrderPayload, RentalProduct } from '@/types/api';

const route = useRoute();

const accounts = ref<RentalProduct[]>([]);
const accountsLoading = ref(false);
const submitting = ref(false);
const resultMessage = ref('');

const form = reactive<CreateOrderPayload>({
  accountId: Number(route.query.accountId) || 0,
  rentHours: 1,
  contactInfo: '',
  remark: ''
});

async function loadAccounts() {
  accountsLoading.value = true;
  try {
    const response = await getRentals({ status: 'AVAILABLE' });
    if (response.data.success) {
      accounts.value = response.data.data;
      if (!form.accountId && accounts.value.length > 0) {
        form.accountId = accounts.value[0].id;
      }
      return;
    }
    ElMessage.error(response.data.message || '账号列表加载失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '账号列表加载失败');
  } finally {
    accountsLoading.value = false;
  }
}

async function submitOrder() {
  if (!form.accountId) {
    ElMessage.warning('请先选择账号');
    return;
  }
  if (!form.contactInfo.trim()) {
    ElMessage.warning('请先填写联系方式');
    return;
  }

  submitting.value = true;
  try {
    const response = await createOrder(form);
    if (response.data.success) {
      const result = response.data.data;
      resultMessage.value = `订单 ${result.orderNo} 已创建，状态：${result.status}，${result.estimatedDelivery}`;
      ElMessage.success(response.data.message);
      return;
    }

    ElMessage.error(response.data.message || '提交订单失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '提交订单失败');
  } finally {
    submitting.value = false;
  }
}

onMounted(loadAccounts);
</script>
