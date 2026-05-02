import { createServer } from 'node:http';

const port = Number(process.env.PORT || 8080);

const portalSummary = {
  heroTitle: '三角洲行动账号租赁管理系统',
  heroSubtitle: '一期先跑通账号展示、下单、订单状态和后台看板，确保课程项目可演示、可联调、可扩展。',
  metrics: [
    { label: '在线可租账号', value: '3' },
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
    category: 'premium',
    tagText: '满配仓库,稀有外观',
    hourPrice: 28,
    coinAmountText: '1200万哈夫币',
    equipmentLevelText: '六套毕业装',
    warehouseValueText: '高价值仓库',
    status: 'AVAILABLE',
    description: '顶级作战账号，仓库满配，稀有外观齐全，适合高强度对局。'
  },
  {
    id: 1002,
    name: '活动账号 B02',
    category: 'event',
    tagText: '稀有外观,活动道具',
    hourPrice: 18,
    coinAmountText: '340万哈夫币',
    equipmentLevelText: '中高配作战装',
    warehouseValueText: '活动收藏资源',
    status: 'AVAILABLE',
    description: '包含多种限定活动道具，外观收藏价值高，适合休闲体验。'
  },
  {
    id: 1003,
    name: '新手体验号 C03',
    category: 'trial',
    tagText: '新手试用',
    hourPrice: 9,
    coinAmountText: '80万哈夫币',
    equipmentLevelText: '基础装备',
    warehouseValueText: '入门资源',
    status: 'MAINTENANCE',
    description: '新手入门体验账号，适合首次体验租赁流程，配置基础。'
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

    if (req.method === 'POST' && pathname === '/api/auth/register') {
      const body = await parseBody(req);
      if (!body.phone || !body.password) {
        sendJson(res, 400, fail('手机号和密码不能为空'));
        return;
      }
      sendJson(
        res,
        200,
        ok({
          id: Math.floor(Math.random() * 10000) + 100,
          nickname: body.nickname || body.phone,
          role: 'USER'
        }, '注册成功')
      );
      return;
    }

    if (req.method === 'POST' && pathname === '/api/auth/send-verify-code') {
      const body = await parseBody(req);
      if (!body.phone || !/^1[3-9]\d{9}$/.test(body.phone)) {
        sendJson(res, 400, fail('请输入正确的手机号'));
        return;
      }
      // 模拟发送验证码，3秒后生效（生产环境应调用短信网关）
      setTimeout(() => {
        console.log(`[mock] 验证码已发送到 ${body.phone}，类型: ${body.type || 'reset_password'}`);
      }, 100);
      sendJson(res, 200, ok(null, '验证码已发送'));
      return;
    }

    if (req.method === 'POST' && pathname === '/api/auth/reset-password') {
      const body = await parseBody(req);
      if (!body.phone || !body.verifyCode || !body.newPassword) {
        sendJson(res, 400, fail('参数不完整'));
        return;
      }
      if (body.newPassword.length < 6) {
        sendJson(res, 400, fail('密码长度不能少于6位'));
        return;
      }
      // 模拟重置密码（生产环境应验证验证码并更新数据库）
      sendJson(res, 200, ok(null, '密码重置成功'));
      return;
    }

    if (req.method === 'POST' && pathname === '/api/auth/login') {
      const body = await parseBody(req);
      // 根据手机号返回不同角色，方便前端测试权限
      const roleMap = {
        '13800000000': { id: 1, displayName: 'Admin Demo User', role: 'ADMIN' },
        '13900000000': { id: 2, displayName: 'CS Demo User', role: 'CS' },
        '13700000000': { id: 3, displayName: 'User Demo', role: 'USER' }
      };
      const user = roleMap[body.phone] || { id: 99, displayName: '匿名用户', role: 'USER' };
      sendJson(res, 200, ok({ token: `mock-token-${body.phone}`, user }, '登录成功'));
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
