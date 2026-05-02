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
  coinAmountText: string;
  equipmentLevelText: string;
  warehouseValueText: string;
  status: 'AVAILABLE' | 'RENTED' | 'MAINTENANCE' | string;
  description: string;
  isHot?: boolean;
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
