export const LOGIN_METHOD_OPTIONS = ['QQ扫码', 'QQ账密', '微信扫码'] as const;

export const COIN_RANGE_OPTIONS = [
  { label: '5千万以下', value: 'coin_lt_5000' },
  { label: '3千万-1.5亿', value: 'coin_3000_15000' },
  { label: '1亿-3亿', value: 'coin_10000_30000' },
  { label: '3亿以上', value: 'coin_gt_30000' }
] as const;

export const INSURANCE_OPTIONS = ['2格', '4格', '6格', '9格'] as const;
export const STAMINA_WEIGHT_OPTIONS = ['1级', '2级', '3级', '4级', '5级', '6级', '7级'] as const;
export const DIVING_LEVEL_OPTIONS = ['1级', '2级', '3级'] as const;

export const KNIFE_SKIN_OPTIONS = [
  '黑海',
  '北极星',
  '信条',
  '怜悯',
  '赤枭',
  '影锋',
  '暗星',
  '电锯',
  '龙牙',
  '处刑者',
  '坠星者'
] as const;

export const OPERATOR_SKIN_OPTIONS = [
  '蚀金玫瑰',
  '天际线',
  '维什戴尔',
  '水墨云图',
  '午夜邮差'
] as const;

export const RANK_OPTIONS = [
  '青铜',
  '白银',
  '黄金',
  '铂金',
  '钻石',
  '黑鹰',
  '三角洲巅峰'
] as const;

export const PRICE_RANGE_OPTIONS = [
  { label: '300以下', value: 'price_lt_300' },
  { label: '300-1000', value: 'price_300_1000' },
  { label: '1000-2000', value: 'price_1000_2000' },
  { label: '2000以上', value: 'price_gt_2000' }
] as const;

export const RENTAL_DAY_OPTIONS = [3, 7, 10, 14, 30, 48] as const;

export const QUICK_ZONE_OPTIONS = [
  { key: 'all', title: '全部账号', desc: '综合浏览区' },
  { key: 'cheap', title: '平价专区', desc: '低价高性价比' },
  { key: 'rich', title: '纯币专区', desc: '高哈夫币账号' },
  { key: 'hawk', title: '黑鹰专区', desc: '高段位资源' },
  { key: 'skin', title: '红皮专区', desc: '干员外观账号' }
] as const;
