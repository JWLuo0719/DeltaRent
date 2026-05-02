import http from './http';
import type {
  ApiResponse,
  PortalSummary,
  RentalProduct,
  LoginPayload,
  LoginResult,
  CreateOrderPayload,
  CreateOrderResult,
  DashboardOverview,
  SendVerifyCodePayload,
  ResetPasswordPayload
} from '@/types/api';

// ==================== 认证 ====================

/** POST /api/auth/login */
export function login(data: LoginPayload) {
  return http.post<ApiResponse<LoginResult>>('/auth/login', data);
}

/** POST /api/auth/register */
export function register(data: {
  phone: string;
  password: string;
  nickname?: string;
}) {
  return http.post<ApiResponse<void>>('/auth/register', data);
}

/** POST /api/auth/send-verify-code 发送验证码 */
export function sendVerifyCode(data: SendVerifyCodePayload) {
  return http.post<ApiResponse<void>>('/auth/send-verify-code', data);
}

/** POST /api/auth/reset-password 重置密码 */
export function resetPassword(data: ResetPasswordPayload) {
  return http.post<ApiResponse<void>>('/auth/reset-password', data);
}

// ==================== 门户 ====================

/** GET /api/portal/summary */
export function getPortalSummary() {
  return http.get<ApiResponse<PortalSummary>>('/portal/summary');
}

// ==================== 账号产品 ====================

/** GET /api/rentals */
export function getRentals(params?: {
  keyword?: string;
  tags?: string;
  level?: string;
  status?: string;
  sortBy?: string;
  page?: number;
  pageSize?: number;
}) {
  return http.get<ApiResponse<RentalProduct[]>>('/rentals', { params });
}

// ==================== 订单 ====================

/** POST /api/orders */
export function createOrder(data: CreateOrderPayload) {
  return http.post<ApiResponse<CreateOrderResult>>('/orders', data);
}

// ==================== 管理后台 ====================

/** GET /api/dashboard/overview */
export function getDashboardOverview() {
  return http.get<ApiResponse<DashboardOverview>>('/dashboard/overview');
}
