<template>
  <div class="page-shell">
    <section class="panel-card" style="max-width: 460px; margin: 48px auto;">
      <h1 class="page-title" style="font-size: 28px;">账号登录</h1>
      <p class="page-subtitle">当前已接入 Mock 登录接口，便于先联调登录流程与权限入口。</p>
      <el-form label-position="top" style="margin-top: 24px;" @submit.prevent="handleLogin">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" :loading="submitting" style="width: 100%;" @click="handleLogin">登录</el-button>
      </el-form>
      <el-alert
        v-if="loginMessage"
        style="margin-top: 16px;"
        type="success"
        :closable="false"
        :title="loginMessage"
      />
    </section>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import http from '@/api/http';
import type { ApiResponse, LoginPayload, LoginResult } from '@/types/api';

const form = reactive<LoginPayload>({
  username: 'demo_admin',
  password: '123456'
});

const submitting = ref(false);
const loginMessage = ref('');

async function handleLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码');
    return;
  }

  submitting.value = true;
  try {
    const response = await http.post<ApiResponse<LoginResult>>('/auth/login', form);
    if (response.data.success) {
      const result = response.data.data;
      localStorage.setItem('mock_token', result.token);
      loginMessage.value = `登录成功：${result.user.displayName}（${result.user.role}）`;
      ElMessage.success(response.data.message);
      return;
    }

    ElMessage.error(response.data.message || '登录失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '登录失败');
  } finally {
    submitting.value = false;
  }
}
</script>
