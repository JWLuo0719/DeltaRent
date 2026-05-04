<template>
  <div class="page-shell">
    <div class="page-header-card">
      <h2 class="page-title">个人资料</h2>
    </div>

    <div v-if="loading" class="profile-card">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-if="loading" class="profile-card">
      <el-skeleton :rows="4" animated />
    </div>

    <template v-if="!loading">

    <div class="profile-card">
      <el-form :model="form" label-width="80px" class="profile-form">
        <el-form-item label="手机号">
          <el-input v-model="form.phone" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="角色">
          <el-input :model-value="roleText" disabled />
        </el-form-item>
        <el-form-item label="注册时间">
          <el-input :model-value="form.createdAt" disabled />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="profile-card">
      <h3 class="card-subtitle">修改密码</h3>
      <el-form :model="pwdForm" label-width="80px" class="pwd-form">
        <el-form-item label="原密码">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码（至少6位）" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="pwdSaving" @click="handleChangePwd">修改密码</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="profile-card danger-zone">
      <h3 class="card-subtitle">账号安全</h3>
      <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
    </div>

    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { changeMyPassword, getMyProfile, updateMyProfile } from '@/api';
import { useAuthStore } from '@/stores/auth';

const router = useRouter();
const auth = useAuthStore();

const loading = ref(true);
const saving = ref(false);
const pwdSaving = ref(false);
const originalNickname = ref('');

const form = reactive({
  phone: '',
  nickname: '',
  createdAt: '',
  role: ''
});

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

const roleText = computed(() => {
  const map: Record<string, string> = { ADMIN: '管理员', USER: '普通用户', CS: '客服' };
  return map[form.role || auth.user?.role || ''] || '未知';
});

async function loadProfile() {
  loading.value = true;
  try {
    const response = await getMyProfile();
    if (response.data.success) {
      const profile = response.data.data;
      form.phone = profile.phone;
      form.nickname = profile.nickname;
      form.createdAt = profile.createdAt;
      form.role = profile.role;
      originalNickname.value = profile.nickname;
      return;
    }
    ElMessage.error(response.data.message || '资料加载失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '资料加载失败');
  } finally {
    loading.value = false;
  }
}

async function handleSave() {
  if (!form.nickname.trim()) {
    ElMessage.warning('请输入昵称');
    return;
  }
  saving.value = true;
  try {
    const response = await updateMyProfile({ nickname: form.nickname.trim() });
    if (response.data.success) {
      const profile = response.data.data;
      form.nickname = profile.nickname;
      originalNickname.value = profile.nickname;
      if (auth.user) {
        auth.setAuth(auth.token!, {
          ...auth.user,
          displayName: profile.nickname
        });
      }
      ElMessage.success('资料已保存');
      return;
    }
    ElMessage.error(response.data.message || '保存失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '保存失败');
  } finally {
    saving.value = false;
  }
}

function handleReset() {
  form.nickname = originalNickname.value;
}

async function handleChangePwd() {
  if (!pwdForm.oldPassword) {
    ElMessage.warning('请输入原密码');
    return;
  }
  if (!pwdForm.newPassword || pwdForm.newPassword.length < 6) {
    ElMessage.warning('新密码至少 6 位');
    return;
  }
  if (pwdForm.newPassword !== pwdForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致');
    return;
  }

  pwdSaving.value = true;
  try {
    const response = await changeMyPassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    });
    if (response.data.success) {
      ElMessage.success('密码已修改');
      pwdForm.oldPassword = '';
      pwdForm.newPassword = '';
      pwdForm.confirmPassword = '';
      return;
    }
    ElMessage.error(response.data.message || '密码修改失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '密码修改失败');
  } finally {
    pwdSaving.value = false;
  }
}

function handleLogout() {
  auth.logout();
  router.push('/home');
}

onMounted(loadProfile);
</script>

<style scoped>
.page-shell {
  min-height: 100vh;
  background-color: #0f1c33;
  background-image: linear-gradient(135deg, #060d1a 0%, #0f1c33 50%, #0a1525 100%);
  color: #e2e8f0;
  padding: 24px;
}

.page-header-card {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(96, 165, 250, 0.12);
  border-radius: 16px;
  padding: 24px 28px;
  margin-bottom: 16px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #f1f5f9;
  letter-spacing: 1px;
}

.profile-card {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(96, 165, 250, 0.1);
  border-radius: 16px;
  padding: 24px 28px;
  margin-bottom: 16px;
}

.card-subtitle {
  margin: 0 0 20px;
  font-size: 16px;
  font-weight: 600;
  color: #f1f5f9;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(96, 165, 250, 0.1);
}

.danger-zone {
  border-color: rgba(239, 68, 68, 0.2);
}

:deep(.el-form-item__label) {
  color: #94a3b8 !important;
  font-size: 13px;
}

:deep(.el-input__wrapper) {
  background: #1e293b !important;
  border-color: rgba(96, 165, 250, 0.2) !important;
  box-shadow: none !important;
}

:deep(.el-input__inner) { color: #e2e8f0 !important; }
:deep(.el-input__inner::placeholder) { color: #475569 !important; }
:deep(.el-input.is-disabled .el-input__wrapper) {
  background: rgba(30, 41, 59, 0.5) !important;
  border-color: rgba(96, 165, 250, 0.1) !important;
}
:deep(.el-input.is-disabled .el-input__inner) { color: #64748b !important; }

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #1e40af 0%, #1e3a8a 100%) !important;
  border: none !important;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%) !important;
}

:deep(.el-button:not(.is-plain):not(.el-button--primary)) {
  background: rgba(255,255,255,0.05) !important;
  border-color: rgba(96,165,250,0.2) !important;
  color: #94a3b8 !important;
}

:deep(.el-button:not(.is-plain):not(.el-button--primary):hover) {
  background: rgba(96,165,250,0.1) !important;
  color: #60a5fa !important;
}
</style>
