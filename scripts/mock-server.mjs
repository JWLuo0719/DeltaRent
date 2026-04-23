import { createServer } from 'node:http';

const port = Number(process.env.PORT || 8080);

const portalSummary = {
  heroTitle: '三角洲行动账号租赁管理系统',
  heroSubtitle: '一期先跑通账号展示、下单、订单状态和后台看板，确保课程项目可演示、可联调、可扩展。',
  metrics: [
    { label: '在线可租账号', value: '18' },
    { label: '今日新增订单', value: '12' },
    { label: '订单完成率', value: '97.4%' }
  ],
  modules: ['用户认证', '账号展示', '账号租赁', '订单中心', '售后申诉', '后台审核', '公告管理', '数据看板'],
  notices: [
    { id: 1, title: '租赁须知', content: '下单前请先确认租赁时长、账号标签和交付说明。' },
    { id: 2, title: '测试说明', content: '当前为课程原型环境，所有数据均为模拟演示数据。' }
  ]
};

const rentals = [
  {
    id: 1001,
    name: '高战账号 A01',
    tag: '满配仓库',
    price: '￥28 / 小时',
    status: '可租',
    coinAmount: '1200万哈夫币',
    equipmentLevel: '六套毕业装',
    warehouseValue: '高价值仓库'
  },
  {
    id: 1002,
    name: '活动账号 B02',
    tag: '稀有外观',
    price: '￥18 / 小时',
    status: '可租',
    coinAmount: '340万哈夫币',
    equipmentLevel: '中高配作战装',
    warehouseValue: '活动收藏资源'
  },
  {
    id: 1003,
    name: '新手体验号 C03',
    tag: '新手试用',
    price: '￥9 / 小时',
    status: '维护中',
    coinAmount: '80万哈夫币',
    equipmentLevel: '基础装备',
    warehouseValue: '入门资源'
  }
];

const dashboardOverview = {
  metrics: [
    { label: '待确认订单', value: '6' },
    { label: '进行中订单', value: '9' },
    { label: '今日新增用户', value: '4' }
  ],
  menus: ['用户管理', '账号管理', '租赁订单管理', '售后申诉管理', '公告管理', '价格规则管理'],
  recentOrders: [
    { orderNo: 'DR20260423001', user: 'jwluo', item: '高战账号 A01', status: '待确认' },
    { orderNo: 'DR20260423002', user: 'test_user', item: '活动账号 B02', status: '进行中' }
  ]
};

function sendJson(res, statusCode, payload) {
  res.writeHead(statusCode, {
    'Content-Type': 'application/json; charset=utf-8',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET,POST,PUT,DELETE,OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type, Authorization'
  });
  res.end(JSON.stringify(payload));
}

function parseBody(req) {
  return new Promise((resolve, reject) => {
    let body = '';
    req.on('data', (chunk) => {
      body += chunk;
    });
    req.on('end', () => {
      if (!body) {
        resolve({});
        return;
      }

      try {
        resolve(JSON.parse(body));
      } catch (error) {
        reject(error);
      }
    });
    req.on('error', reject);
  });
}

function ok(data, message = 'ok') {
  return { success: true, message, data };
}

function fail(message) {
  return { success: false, message, data: null };
}

const server = createServer(async (req, res) => {
  if (!req.url || !req.method) {
    sendJson(res, 400, fail('invalid request'));
    return;
  }

  if (req.method === 'OPTIONS') {
    sendJson(res, 200, ok(null));
    return;
  }

  const url = new URL(req.url, `http://localhost:${port}`);
  const { pathname } = url;

  try {
    if (req.method === 'GET' && pathname === '/api/health') {
      sendJson(res, 200, ok({ service: 'mock-api', status: 'UP', phase: 'frontend-linking' }));
      return;
    }

    if (req.method === 'GET' && pathname === '/api/portal/summary') {
      sendJson(res, 200, ok(portalSummary));
      return;
    }

    if (req.method === 'GET' && pathname === '/api/rentals') {
      sendJson(res, 200, ok(rentals));
      return;
    }

    if (req.method === 'GET' && pathname === '/api/dashboard/overview') {
      sendJson(res, 200, ok(dashboardOverview));
      return;
    }

    if (req.method === 'POST' && pathname === '/api/auth/login') {
      const body = await parseBody(req);
      sendJson(
        res,
        200,
        ok({
          token: 'mock-token-20260423',
          user: {
            id: 1,
            username: body.username || 'demo_user',
            displayName: '课程演示账号',
            role: 'ADMIN'
          }
        }, '登录成功')
      );
      return;
    }

    if (req.method === 'POST' && pathname === '/api/orders') {
      const body = await parseBody(req);
      sendJson(
        res,
        200,
        ok({
          orderNo: `DR${Date.now()}`,
          accountId: body.accountId || 1001,
          rentHours: body.rentHours || 1,
          status: 'WAITING_CONFIRM',
          estimatedDelivery: '5分钟内由客服确认'
        }, '订单已提交')
      );
      return;
    }

    sendJson(res, 404, fail(`route not found: ${pathname}`));
  } catch (error) {
    sendJson(res, 500, fail(error instanceof Error ? error.message : 'server error'));
  }
});

server.listen(port, () => {
  console.log(`[mock-api] listening on http://localhost:${port}`);
});
