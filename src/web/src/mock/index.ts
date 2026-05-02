import type { ApiResponse, PortalSummary, RentalProduct, LoginResult, CreateOrderResult, DashboardOverview } from '@/types/api';

function result<T>(data: T, message = '操作成功'): ApiResponse<T> {
  return { success: true, message, data };
}

function error(message = '操作失败'): ApiResponse<null> {
  return { success: false, message, data: null as any };
}

const mockUsers = [
  { id: 1, displayName: '管理员小明', role: 'ADMIN', phone: '13800000001', password: '123456' },
  { id: 2, displayName: '客服小红', role: 'CS', phone: '13800000002', password: '123456' },
  { id: 3, displayName: '普通用户小刚', role: 'USER', phone: '13800000003', password: '123456' },
];

export default [
  // POST /api/auth/login
  {
    url: '/api/auth/login',
    method: 'post',
    response: ({ body }: { body: { phone: string; password: string } }) => {
      const user = mockUsers.find(u => u.phone === body.phone && u.password === body.password);
      if (user) {
        return result<LoginResult>({
          token: 'mock_token_' + Date.now(),
          user: { id: user.id, displayName: user.displayName, role: user.role }
        }, '登录成功');
      }
      return error('手机号或密码错误');
    }
  },

  // POST /api/auth/register
  {
    url: '/api/auth/register',
    method: 'post',
    response: ({ body }: { body: { phone: string; password: string; nickname?: string } }) => {
      if (mockUsers.find(u => u.phone === body.phone)) {
        return error('手机号已注册');
      }
      const newUser = { id: Date.now(), displayName: body.nickname || '新用户', role: 'USER', phone: body.phone, password: body.password };
      mockUsers.push(newUser);
      return result({ token: 'mock_token_' + Date.now(), user: { id: newUser.id, displayName: newUser.displayName, role: newUser.role } });
    }
  },

  // POST /api/auth/send-verify-code
  {
    url: '/api/auth/send-verify-code',
    method: 'post',
    response: () => result(null)
  },

  // POST /api/auth/reset-password
  {
    url: '/api/auth/reset-password',
    method: 'post',
    response: () => result(null)
  },

  // GET /api/portal/summary
  {
    url: '/api/portal/summary',
    method: 'get',
    response: (): ApiResponse<PortalSummary> => result<PortalSummary>({
      heroTitle: '三角洲行动账号租赁管理系统',
      heroSubtitle: '安全可靠、极速交付的专业租赁平台',
      metrics: [
        { label: '在架账号', value: '128' },
        { label: '完成订单', value: '2,847' },
        { label: '用户好评', value: '99.2%' },
      ],
      modules: ['账号管理', '订单系统', '用户管理', '客服系统', '数据分析'],
      notices: [
        { id: 1, title: '系统升级通知', content: '平台将于本周日凌晨2:00-6:00进行系统升级' },
        { id: 2, title: '新增账号类型', content: '哈夫币仓库账号已上线，欢迎选购' },
      ]
    })
  },

  // GET /api/rentals
  {
    url: '/api/rentals',
    method: 'get',
    response: (): ApiResponse<RentalProduct[]> => result<RentalProduct[]>([
      { id: 1, name: '满配突击小队', tag: '限定版', price: '88', status: '可租', coinAmount: '500万', equipmentLevel: '满配', warehouseValue: '120万' },
      { id: 2, name: '狙击精英号', tag: '稀有', price: '68', status: '可租', coinAmount: '300万', equipmentLevel: '高配', warehouseValue: '80万' },
      { id: 3, name: '新手入门号', tag: '普通', price: '28', status: '可租', coinAmount: '50万', equipmentLevel: '基础', warehouseValue: '10万' },
      { id: 4, name: '土豪金账号', tag: '传说', price: '168', status: '已租', coinAmount: '1000万', equipmentLevel: '满配', warehouseValue: '200万' },
      { id: 5, name: '哈夫币仓库', tag: '特殊', price: '128', status: '可租', coinAmount: '999万', equipmentLevel: '仓库号', warehouseValue: '50万' },
      { id: 6, name: '医疗兵账号', tag: '普通', price: '38', status: '可租', coinAmount: '100万', equipmentLevel: '中配', warehouseValue: '30万' },
    ])
  },

  // POST /api/orders
  {
    url: '/api/orders',
    method: 'post',
    response: ({ body }: { body: { accountId: number; rentHours: number; contactInfo: string; remark?: string } }) => result<CreateOrderResult>({
      orderNo: 'DR' + Date.now(),
      accountId: body.accountId,
      rentHours: body.rentHours,
      status: '待发货',
      estimatedDelivery: '10分钟内'
    })
  },

  // GET /api/dashboard/overview
  {
    url: '/api/dashboard/overview',
    method: 'get',
    response: (): ApiResponse<DashboardOverview> => result<DashboardOverview>({
      metrics: [
        { label: '今日订单', value: '24' },
        { label: '在租账号', value: '36' },
        { label: '新增用户', value: '8' },
        { label: '收入金额', value: '¥3,280' },
      ],
      menus: ['订单管理', '账号管理', '用户管理', '角色管理', '公告管理', '数据统计'],
      recentOrders: [
        { orderNo: 'DR20240502001', user: '张三', item: '满配突击小队', status: '进行中' },
        { orderNo: 'DR20240502002', user: '李四', item: '狙击精英号', status: '已完成' },
        { orderNo: 'DR20240502003', user: '王五', item: '新手入门号', status: '待发货' },
      ]
    })
  },
];
