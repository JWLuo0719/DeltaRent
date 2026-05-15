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
  category: string;
  tagText: string;
  hourPrice: number;
  coinAmount: number;
  equipmentLevelText: string;
  warehouseValueText: string;
  ratioText?: string;
  insuranceBoxText?: string;
  staminaText?: string;
  weightText?: string;
  rankText?: string;
  loginRegion?: string;
  weaponSkinText?: string;
  characterSkinText?: string;
  coverImageUrl?: string;
  status: 'AVAILABLE' | 'RENTED' | 'MAINTENANCE' | string;
  description: string;
  isHot?: boolean;
  tag?: string;
  price?: string;
  equipmentLevel?: string;
  warehouseValue?: string;
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

export interface SubmitAppealPayload {
  orderType: string;
  orderId: number;
  content: string;
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

export interface AdminRole {
  roleCode: string;
  roleName: string;
  description: string;
  userCount: number;
}

export interface RolePayload {
  roleCode?: string;
  roleName: string;
  description: string;
}

export interface NoticeItem {
  id: number;
  title: string;
  content: string;
  status: number;
}
