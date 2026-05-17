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
            <el-form-item label="账号标题 / 仓库简介">
              <el-input
                v-model="form.name"
                placeholder="例如：纯币 1.83亿｜比例 1:38｜4格｜7体6负｜六头1六甲6｜AWM子弹10"
              />
            </el-form-item>
          </div>

          <div class="grid-2">
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
            <el-form-item label="比例">
              <el-input-number v-model="ratioValue" :min="1" :precision="0" class="full-width" />
              <p class="field-tip">固定为 1：{{ ratioValue || 'xx' }}，表示 1 元人民币 = {{ ratioValue || 'xx' }} 万哈夫币</p>
            </el-form-item>
            <el-form-item label="押金">
              <el-input-number v-model="form.deposit" :min="0" :precision="0" :step="1" step-strictly class="full-width" />
            </el-form-item>
          </div>

          <div class="rent-preview">
            <span>自动计算租金：¥{{ form.hourPrice || 0 }}</span>
            <span>扣除 5% 手续费后到手：¥{{ netRent }}</span>
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
            <el-form-item label="等级">
              <el-input-number v-model="form.level" :min="0" class="full-width" />
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
              <el-select v-model="selectedKnifeSkins" multiple collapse-tags collapse-tags-tooltip placeholder="请选择刀皮">
                <el-option v-for="item in KNIFE_SKIN_OPTIONS" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
            <el-form-item label="干员红皮">
              <el-select v-model="selectedOperatorSkins" multiple collapse-tags collapse-tags-tooltip placeholder="请选择红皮">
                <el-option v-for="item in OPERATOR_SKIN_OPTIONS" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
            <el-form-item label="常用登录地区">
              <el-input v-model="form.loginRegion" placeholder="例如 四川省" />
            </el-form-item>
          </div>

          <div class="grid-2">
            <el-form-item label="封面图 URL">
              <el-input v-model="form.coverImageUrl" placeholder="暂用图片链接，后续可替换上传" />
            </el-form-item>
          </div>

          <div class="grid-3">
            <el-form-item label="六头数量">
              <el-input-number v-model="form.helmetCount" :min="0" class="full-width" />
            </el-form-item>
            <el-form-item label="六甲数量">
              <el-input-number v-model="form.armorCount" :min="0" class="full-width" />
            </el-form-item>
            <el-form-item label="AWM子弹">
              <el-input-number v-model="form.awmAmmoCount" :min="0" class="full-width" />
            </el-form-item>
          </div>

          <div class="grid-2">
            <el-form-item label="9格体验卡数量">
              <el-input-number v-model="form.nineGridTrialCardCount" :min="0" class="full-width" />
            </el-form-item>
            <el-form-item label="近90天有无封禁记录">
              <el-select v-model="form.recentBanRecord" placeholder="请选择">
                <el-option label="无" value="无" />
                <el-option label="有" value="有" />
              </el-select>
            </el-form-item>
          </div>

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
import { computed, reactive, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
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
const router = useRouter();
const selectedKnifeSkins = ref<string[]>([]);
const selectedOperatorSkins = ref<string[]>([]);
const ratioValue = ref<number | undefined>();

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
  level: 0,
  loginRegion: '',
  tradeTimeText: '',
  knifeSkinText: '',
  weaponSkinText: '',
  characterSkinText: '',
  helmetCount: 0,
  armorCount: 0,
  awmAmmoCount: 0,
  nineGridTrialCardCount: 0,
  recentBanRecord: '',
  coverImageUrl: '',
  description: ''
});

const form = reactive<RentalPublishPayload>(createEmptyForm());
const netRent = computed(() => Number((Number(form.hourPrice || 0) * 0.95).toFixed(2)));

watch(() => [form.coinAmount, ratioValue.value] as const, syncRentFromRatio);

watch(selectedKnifeSkins, value => {
  form.knifeSkinText = value.join(',');
});

watch(selectedOperatorSkins, value => {
  form.characterSkinText = value.join(',');
});

function resetForm() {
  Object.assign(form, createEmptyForm());
  selectedKnifeSkins.value = [];
  selectedOperatorSkins.value = [];
  ratioValue.value = undefined;
}

function syncRentFromRatio() {
  const ratio = Number(ratioValue.value || 0);
  const coinAmount = Number(form.coinAmount || 0);
  if (!ratio || !coinAmount) return;
  form.ratioText = `1:${ratio}`;
  form.hourPrice = Number((coinAmount / ratio).toFixed(2));
}

async function submitPublish() {
  if (!form.name.trim()) {
    ElMessage.warning('请先填写账号标题 / 仓库简介');
    return;
  }
  if (!form.loginMethod) {
    ElMessage.warning('请选择登录方式');
    return;
  }
  if (!form.coinAmount || form.coinAmount <= 0) {
    ElMessage.warning('请填写哈夫币数额');
    return;
  }
  syncRentFromRatio();
  if (!ratioValue.value || !form.hourPrice || form.hourPrice <= 0) {
    ElMessage.warning('请填写正确的比例');
    return;
  }

  submitting.value = true;
  try {
    const response = await publishRental({
      ...form,
      ratioText: `1:${ratioValue.value}`,
      deposit: Math.trunc(Number(form.deposit || 0)),
      warehouseValueText: form.name.trim(),
      category: form.category || '玩家上架'
    });
    if (response.data.success) {
      ElMessage.success('提交成功，已进入待审核状态');
      resetForm();
      router.push('/rentals');
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

.rent-preview {
  display: flex;
  justify-content: flex-end;
  gap: 18px;
  margin: -4px 0 14px;
  color: #64748b;
  font-size: 13px;
}

.rent-preview span:first-child {
  color: #f05b2c;
  font-weight: 800;
}

.field-tip {
  margin: 6px 0 0;
  color: #f59e0b;
  font-size: 12px;
  line-height: 1.5;
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
