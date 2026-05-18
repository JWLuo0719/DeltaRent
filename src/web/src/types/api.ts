export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface MetricItem {
  label: string;
  value: string;
}

export interface PortalNotice {
  id: number;
  title: string;
  content: string;
}

export interface PortalSummary {
  heroTitle: string;
  heroSubtitle: string;
  metrics: MetricItem[];
  modules: string[];
  notices: PortalNotice[];
}

export interface RentalProduct {
  id: number;
  name: string;
  ownerId?: number;
  category: string;
  tagText: string;
  hourPrice: number;
  price?: number;
  coinAmount: number;
  deposit?: number;
  rentalDays?: number;
  warehouseValueText: string;
  loginMethod?: string;
  ratioText?: string;
  insuranceBoxText?: string;
  insuranceBox?: string;
  staminaText?: string;
  staminaLevel?: number;
  weightText?: string;
  weightLevel?: number;
  rankText?: string;
  kdText?: string;
  kd?: number;
  divingLevelText?: string;
  divingLevel?: number;
  loginRegion?: string;
  tradeTimeText?: string;
  knifeSkinText?: string;
  weaponSkinText?: string;
  characterSkinText?: string;
  level?: number;
  helmetCount?: number;
  armorCount?: number;
  awmAmmoCount?: number;
  nineGridTrialCardCount?: number;
  recentBanRecord?: string;
  coverImageUrl?: string;
  status: 'AVAILABLE' | 'RENTED' | 'MAINTENANCE' | 'PENDING' | 'OFF_SHELF' | string;
  description: string;
  isHot?: boolean;
  tag?: string;
}

export interface RentalListResult {
  list: RentalProduct[];
  total: number;
  allTags: string[];
  allCategories?: string[];
}

export interface AccountDetail extends RentalProduct {
  imageUrls: string[];
  availableDurations: number[];
}

export interface DashboardOrder {
  orderNo: string;
  user: string;
  item: string;
  status: string;
}

export interface DashboardOverview {
  metrics: MetricItem[];
  menus: string[];
  recentOrders: DashboardOrder[];
}

export interface LoginPayload {
  phone: string;
  password: string;
}

export interface LoginResult {
  token: string;
  user: {
    id: number;
    displayName: string;
    role: string;
    phone?: string;
  };
}

export interface CreateOrderPayload {
  accountId: number;
  rentHours: number;
  contactInfo: string;
  remark: string;
}

export interface CreateOrderResult {
  orderNo: string;
  accountId: number;
  rentHours: number;
  amount: number;
  status: string;
  estimatedDelivery: string;
}

export interface SendVerifyCodePayload {
  phone: string;
  type: 'reset_password' | 'register' | 'login';
}

export interface ResetPasswordPayload {
  phone: string;
  verifyCode: string;
  newPassword: string;
}

export interface FaqItem {
  question: string;
  answer: string;
}

export interface OrderSummary {
  id: number;
  orderNo: string;
  item: string;
  user?: string;
  productId?: number;
  unitPrice?: number;
  rentHours: number;
  amount: number;
  status: string;
  contactInfo: string;
  remark: string;
  startTime?: string;
  endTime?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface OrderEvent {
  time: string;
  content: string;
}

export interface OrderDetail extends OrderSummary {
  userId?: number;
  events: OrderEvent[];
}

export interface UserProfile {
  id: number;
  username: string;
  displayName: string;
  nickname: string;
  phone: string;
  role: string;
  createdAt: string;
}

export interface UpdateProfilePayload {
  nickname: string;
}

export interface ChangePasswordPayload {
  oldPassword: string;
  newPassword: string;
}

export interface RentalPublishPayload {
  name: string;
  category: string;
  tagText: string;
  hourPrice: number;
  coinAmount: number;
  deposit?: number;
  rentalDays?: number;
  warehouseValueText: string;
  loginMethod?: string;
  ratioText?: string;
  insuranceBoxText?: string;
  staminaText?: string;
  weightText?: string;
  rankText?: string;
  kdText?: string;
  divingLevelText?: string;
  loginRegion?: string;
  tradeTimeText?: string;
  knifeSkinText?: string;
  weaponSkinText?: string;
  characterSkinText?: string;
  level?: number;
  helmetCount?: number;
  armorCount?: number;
  awmAmmoCount?: number;
  nineGridTrialCardCount?: number;
  recentBanRecord?: string;
  coverImageUrl?: string;
  description: string;
}

export interface SubmitAppealPayload {
  orderType: string;
  orderId: number;
  content: string;
  reason: string;
}

export interface AppealRecord {
  id: number;
  orderType: string;
  orderId: number;
  userId: number;
  content: string;
  reason: string;
  status: 'PENDING' | 'RESOLVED' | 'REJECTED' | string;
  handlerId: number | null;
  handlerRemark: string | null;
  refundAmount: number | null;
  compensation: string | null;
  handledAt: string | null;
  updatedAt: string | null;
  userName?: string;
  handlerName?: string;
  productName?: string;
  orderAmount?: number;
}

export interface HandleAppealPayload {
  status: 'RESOLVED' | 'REJECTED';
  handlerRemark: string;
  refundAmount: number | null;
  compensation: string;
}

export interface PageResult<T> {
  list: T[];
  total: number;
  page: number;
  pageSize: number;
}

export interface AdminUser {
  id: number;
  phone: string;
  nickname: string;
  role: 'ADMIN' | 'USER' | 'CS' | string;
  status: number;
  createdAt: string;
}


export interface NoticeItem {
  id: number;
  title: string;
  content: string;
  status: number;
}
