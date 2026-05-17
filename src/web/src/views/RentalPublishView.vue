<template>
  <div class="publish-page">
    <div class="publish-inner">
      <section class="publish-hero">
        <div>
          <div class="hero-badge">发布出租商品</div>
          <h1>上架你的三角洲账号</h1>
          <p>提交后默认进入待审核状态，管理员或客服确认信息后再上架到租号大厅。</p>
        </div>
      </section>

      <section class="publish-panel">
        <el-form label-position="top">
          <div class="grid-2">
            <el-form-item label="账号标题">
              <el-input v-model="form.name" placeholder="例如：黑鹰 暗星 4格6体7负" />
            </el-form-item>
            <el-form-item label="登录方式">
              <el-select v-model="form.loginMethod" placeholder="请选择登录方式">
                <el-option v-for="item in LOGIN_METHOD_OPTIONS" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </div>

          <div class="grid-3">
            <el-form-item label="哈夫币数额(万)">
              <el-input-number v-model="form.coinAmount" :min="0" class="full-width" />
            </el-form-item>
            <el-form-item label="租金">
              <el-input-number v-model="form.hourPrice" :min="0" :precision="2" class="full-width" />
            </el-form-item>
            <el-form-item label="押金">
              <el-input-number v-model="form.deposit" :min="0" :precision="2" class="full-width" />
            </el-form-item>
          </div>

          <div class="grid-3">
            <el-form-item label="保险箱">
              <el-select v-model="form.insuranceBoxText" placeholder="请选择保险箱">
                <el-option v-for="item in INSURANCE_OPTIONS" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
            <el-form-item label="体力等级">
              <el-select v-model="form.staminaText" placeholder="请选择体力等级">
                <el-option v-for="item in STAMINA_WEIGHT_OPTIONS" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
            <el-form-item label="负重等级">
              <el-select v-model="form.weightText" placeholder="请选择负重等级">
                <el-option v-for="item in STAMINA_WEIGHT_OPTIONS" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </div>

          <div class="grid-3">
            <el-form-item label="段位">
              <el-select v-model="form.rankText" placeholder="请选择段位">
                <el-option v-for="item in RANK_OPTIONS" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
            <el-form-item label="KD">
              <el-input v-model="form.kdText" placeholder="例如 2.2" />
            </el-form-item>
            <el-form-item label="潜水等级">
              <el-select v-model="form.divingLevelText" placeholder="请选择潜水等级">
                <el-option v-for="item in DIVING_LEVEL_OPTIONS" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </div>

          <div class="grid-3">
            <el-form-item label="比例">
              <el-input v-model="form.ratioText" placeholder="例如 1:35" />
            </el-form-item>
            <el-form-item label="方便交易时间">
              <el-input v-model="form.tradeTimeText" placeholder="例如 00:00-24:00" />
            </el-form-item>
            <el-form-item label="租期(天)">
              <el-select v-model="form.rentalDays" placeholder="请选择租期">
                <el-option v-for="item in RENTAL_DAY_OPTIONS" :key="item" :label="`${item}天`" :value="item" />
              </el-select>
            </el-form-item>
          </div>

          <div class="grid-3">
            <el-form-item label="刀皮">
              <el-select v-model="form.knifeSkinText" placeholder="请选择刀皮">
                <el-option v-for="item in KNIFE_SKIN_OPTIONS" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
            <el-form-item label="干员红皮">
              <el-select v-model="form.characterSkinText" placeholder="请选择红皮">
                <el-option v-for="item in OPERATOR_SKIN_OPTIONS" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
            <el-form-item label="常用登录地区">
              <el-input v-model="form.loginRegion" placeholder="例如 四川省" />
            </el-form-item>
          </div>

          <div class="grid-2">
            <el-form-item label="武器皮肤 / 额外资源">
              <el-input v-model="form.weaponSkinText" placeholder="例如 AWM子弹、六甲、六头" />
            </el-form-item>
            <el-form-item label="封面图 URL">
              <el-input v-model="form.coverImageUrl" placeholder="暂用图片链接，后续可替换上传" />
            </el-form-item>
          </div>

          <div class="grid-2">
            <el-form-item label="分类">
              <el-input v-model="form.category" placeholder="例如 黑鹰专区 / 纯币专区 / 红皮专区" />
            </el-form-item>
            <el-form-item label="标签">
              <el-input v-model="form.tagText" placeholder="多个标签用英文逗号分隔" />
            </el-form-item>
          </div>

          <el-form-item label="仓库价值 / 简介">
            <el-input v-model="form.warehouseValueText" placeholder="例如 纯币 9800万，4格，6体7负" />
          </el-form-item>

          <el-form-item label="备注描述">
            <el-input v-model="form.description" type="textarea" :rows="4" placeholder="补充说明账号亮点、资源细节、注意事项" />
          </el-form-item>

          <div class="actions">
            <el-button @click="resetForm">重置</el-button>
            <el-button type="warning" :loading="submitting" @click="submitPublish">提交上架</el-button>
          </div>
        </el-form>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { publishRental } from '@/api';
import {
  DIVING_LEVEL_OPTIONS,
  INSURANCE_OPTIONS,
  KNIFE_SKIN_OPTIONS,
  LOGIN_METHOD_OPTIONS,
  OPERATOR_SKIN_OPTIONS,
  RANK_OPTIONS,
  RENTAL_DAY_OPTIONS,
  STAMINA_WEIGHT_OPTIONS
} from '@/constants/rental';
import type { RentalPublishPayload } from '@/types/api';

const submitting = ref(false);

const createEmptyForm = (): RentalPublishPayload => ({
  name: '',
  category: '',
  tagText: '',
  hourPrice: 0,
  coinAmount: 0,
  deposit: 0,
  rentalDays: 7,
  warehouseValueText: '',
  loginMethod: '',
  ratioText: '',
  insuranceBoxText: '',
  staminaText: '',
  weightText: '',
  rankText: '',
  kdText: '',
  divingLevelText: '',
  loginRegion: '',
  tradeTimeText: '',
  knifeSkinText: '',
  weaponSkinText: '',
  characterSkinText: '',
  coverImageUrl: '',
  description: ''
});

const form = reactive<RentalPublishPayload>(createEmptyForm());

function resetForm() {
  Object.assign(form, createEmptyForm());
}

async function submitPublish() {
  if (!form.name.trim()) {
    ElMessage.warning('请先填写账号标题');
    return;
  }
  if (!form.loginMethod) {
    ElMessage.warning('请选择登录方式');
    return;
  }
  if (!form.hourPrice || form.hourPrice <= 0) {
    ElMessage.warning('请填写正确的租金');
    return;
  }
  if (!form.coinAmount || form.coinAmount <= 0) {
    ElMessage.warning('请填写哈夫币数额');
    return;
  }

  submitting.value = true;
  try {
    const response = await publishRental({
      ...form,
      warehouseValueText: form.warehouseValueText || '',
      category: form.category || '玩家上架'
    });
    if (response.data.success) {
      ElMessage.success('提交成功，已进入待审核状态');
      resetForm();
      return;
    }
    ElMessage.error(response.data.message || '提交失败');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '提交失败');
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.publish-page {
  min-height: calc(100vh - 64px);
  background: transparent;
}

.publish-inner {
  max-width: 1180px;
  margin: 0 auto;
  padding: 24px;
}

.publish-hero,
.publish-panel {
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 24px;
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.06);
}

.publish-hero {
  padding: 28px 30px;
  margin-bottom: 18px;
}

.hero-badge {
  display: inline-flex;
  height: 30px;
  align-items: center;
  padding: 0 12px;
  border-radius: 999px;
  background: #fff0d0;
  color: #9b5d00;
  font-size: 12px;
  font-weight: 800;
}

.publish-hero h1 {
  margin: 16px 0 10px;
  font-size: 34px;
  color: #1f2937;
}

.publish-hero p {
  margin: 0;
  color: #64748b;
}

.publish-panel {
  padding: 26px;
}

.grid-2,
.grid-3 {
  display: grid;
  gap: 14px;
}

.grid-2 {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.grid-3 {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.full-width {
  width: 100%;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-textarea__inner) {
  background: #fff !important;
  border-radius: 12px !important;
  box-shadow: 0 0 0 1px #dbe3ef inset !important;
}

@media (max-width: 900px) {
  .grid-2,
  .grid-3 {
    grid-template-columns: 1fr;
  }
}
</style>
