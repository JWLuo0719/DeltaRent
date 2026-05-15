import http from './http';
import type {
  ApiResponse,
  PortalSummary,
  RentalProduct,
  RentalListResult,
  LoginPayload,
  LoginResult,
  CreateOrderPayload,
  CreateOrderResult,
  DashboardOverview,
  SendVerifyCodePayload,
  ResetPasswordPayload,
  OrderSummary,
  OrderDetail,
  UserProfile,
  UpdateProfilePayload,
  ChangePasswordPayload,
  SubmitAppealPayload,
  PageResult,
  AdminUser,
  AdminRole,
  RolePayload,
  NoticeItem
} from '@/types/api';

export function login(data: LoginPayload) {
  return http.post<ApiResponse<LoginResult>>('/auth/login', data);
}

export function register(data: {
  phone: string;
  password: string;
  nickname?: string;
}) {
  return http.post<ApiResponse<void>>('/auth/register', data);
}

export function sendVerifyCode(data: SendVerifyCodePayload) {
  return http.post<ApiResponse<void>>('/auth/send-verify-code', data);
}

export function resetPassword(data: ResetPasswordPayload) {
  return http.post<ApiResponse<void>>('/auth/reset-password', data);
}

export function getPortalSummary() {
  return http.get<ApiResponse<PortalSummary>>('/portal/summary');
}

// ==================== 账号产品 ====================

/** GET /api/rentals */
export function getRentals(params?: {
  keyword?: string;
  tags?: string;
  category?: string;
  level?: string;
  status?: string;
  sortBy?: string;
  page?: number;
  pageSize?: number;
}) {
  return http.get<ApiResponse<RentalListResult>>('/rentals', { params });
}

export function createOrder(data: CreateOrderPayload) {
  return http.post<ApiResponse<CreateOrderResult>>('/orders', data);
}

export function getMyOrders(params?: { status?: string }) {
  return http.get<ApiResponse<OrderSummary[]>>('/orders/my', { params });
}

export function getOrderDetail(orderNo: string) {
  return http.get<ApiResponse<OrderDetail>>(`/orders/${orderNo}`);
}

export function cancelOrder(orderNo: string) {
  return http.put<ApiResponse<void>>(`/orders/${orderNo}/cancel`);
}

export function submitAppeal(data: SubmitAppealPayload) {
  return http.post<ApiResponse<void>>('/appeals', data);
}

export function getDashboardOverview() {
  return http.get<ApiResponse<DashboardOverview>>('/dashboard/overview');
}

export function getAdminOrders(params?: { status?: string }) {
  return http.get<ApiResponse<OrderSummary[]>>('/orders', { params });
}

export function updateOrderStatus(id: number, status: string) {
  return http.put<ApiResponse<void>>(`/orders/${id}/status`, { status });
}

export function getAdminUsers(params?: {
  page?: number;
  pageSize?: number;
  phone?: string;
  role?: string;
  status?: number;
}) {
  return http.get<ApiResponse<PageResult<AdminUser>>>('/admin/users', { params });
}

export function updateAdminUserRole(id: number, role: string) {
  return http.put<ApiResponse<void>>(`/admin/users/${id}/role`, { role });
}

export function updateAdminUserStatus(id: number, status: number) {
  return http.put<ApiResponse<void>>(`/admin/users/${id}/status`, { status });
}

export function getAdminRoles() {
  return http.get<ApiResponse<AdminRole[]>>('/admin/roles');
}

export function createAdminRole(data: RolePayload) {
  return http.post<ApiResponse<AdminRole>>('/admin/roles', data);
}

export function updateAdminRole(roleCode: string, data: RolePayload) {
  return http.put<ApiResponse<void>>(`/admin/roles/${roleCode}`, data);
}

export function createRental(data: Partial<RentalProduct>) {
  return http.post<ApiResponse<RentalProduct>>('/rentals', data);
}

export function updateRental(id: number, data: Partial<RentalProduct>) {
  return http.put<ApiResponse<void>>(`/rentals/${id}`, data);
}

export function deleteRental(id: number) {
  return http.delete<ApiResponse<void>>(`/rentals/${id}`);
}

export function updateRentalStatus(id: number, status: string) {
  return http.put<ApiResponse<void>>(`/rentals/${id}/status`, { status });
}

export function getAdminNotices() {
  return http.get<ApiResponse<NoticeItem[]>>('/notices/all');
}

export function createNotice(data: Pick<NoticeItem, 'title' | 'content' | 'status'>) {
  return http.post<ApiResponse<NoticeItem>>('/notices', data);
}

export function updateNotice(id: number, data: Pick<NoticeItem, 'title' | 'content' | 'status'>) {
  return http.put<ApiResponse<void>>(`/notices/${id}`, data);
}

export function deleteNotice(id: number) {
  return http.delete<ApiResponse<void>>(`/notices/${id}`);
}

export function getMyProfile() {
  return http.get<ApiResponse<UserProfile>>('/users/me');
}

export function updateMyProfile(data: UpdateProfilePayload) {
  return http.put<ApiResponse<UserProfile>>('/users/me', data);
}

export function changeMyPassword(data: ChangePasswordPayload) {
  return http.put<ApiResponse<void>>('/users/me/password', data);
}
