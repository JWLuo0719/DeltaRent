<template>
  <div class="page-shell stack">
    <section class="hero-card">
      <h1 class="page-title">创建账号租赁订单</h1>
      <p class="page-subtitle">当前页面已接入 Mock 下单接口，可直接验证表单提交和订单返回结果。</p>
    </section>
    <section class="panel-card">
      <el-form label-position="top">
        <div class="grid-2">
          <el-form-item label="账号编号">
            <el-select v-model="form.accountId" placeholder="请选择账号">
              <el-option label="高战账号 A01" :value="1001" />
              <el-option label="活动账号 B02" :value="1002" />
              <el-option label="新手体验号 C03" :value="1003" />
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
            <el-input v-model="form.contactInfo" placeholder="请输入QQ / 微信 / 手机号" />
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
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { createOrder } from '@/api';

const form = reactive<CreateOrderPayload>({
  accountId: 1001,
  rentHours: 1,
  contactInfo: '',
  remark: ''
});

const submitting = ref(false);
const resultMessage = ref('');

async function submitOrder() {
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
</script>
