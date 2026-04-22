<template>
  <div class="page-shell">
    <section class="panel-card" style="max-width: 460px; margin: 48px auto;">
      <h1 class="page-title" style="font-size: 28px;">账号登录</h1>
      <p class="page-subtitle">当前为原型登录页，后续接入 JWT 登录接口与验证码能力。</p>
      <el-form label-position="top" style="margin-top: 24px;">
        <el-form-item label="用户名">
          <el-input placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-button type="primary" style="width: 100%;">登录</el-button>
      </el-form>
    </section>
  </div>
</template>
