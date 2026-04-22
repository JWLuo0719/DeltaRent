<template>
  <div class="page-shell stack">
    <section class="hero-card">
      <h1 class="page-title">哈夫币回收申请</h1>
      <p class="page-subtitle">用户填写区服、数量、截图等信息，后台审核报价并推进订单状态。</p>
    </section>
    <section class="panel-card">
      <el-form label-position="top">
        <div class="grid-2">
          <el-form-item label="游戏区服">
            <el-select placeholder="请选择区服">
              <el-option label="微信区" value="wechat" />
              <el-option label="QQ区" value="qq" />
            </el-select>
          </el-form-item>
          <el-form-item label="哈夫币数量">
            <el-input placeholder="请输入数量" />
          </el-form-item>
        </div>
        <div class="grid-2">
          <el-form-item label="联系方式">
            <el-input placeholder="请输入QQ / 微信 / 手机号" />
          </el-form-item>
          <el-form-item label="截图说明">
            <el-input placeholder="请填写截图备注" />
          </el-form-item>
        </div>
        <el-form-item label="备注">
          <el-input type="textarea" :rows="4" placeholder="补充描述当前账号、库存和交易说明" />
        </el-form-item>
        <el-button type="primary">提交申请</el-button>
      </el-form>
    </section>
  </div>
</template>
