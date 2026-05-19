from pathlib import Path
from textwrap import dedent

from reportlab.lib import colors
from reportlab.lib.utils import ImageReader
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.cidfonts import UnicodeCIDFont
from reportlab.pdfgen import canvas


ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "docs" / "report" / "assets"
OUTPUT_DIR = ROOT / "output" / "pdf"
TMP_DIR = ROOT / "tmp" / "pdfs" / "generated_preview"
OUTPUT_PDF = OUTPUT_DIR / "DeltaRent最终汇报.pdf"

PAGE_W = 960
PAGE_H = 540
MARGIN = 40

FONT_CN = "STSong-Light"
FONT_EN = "Helvetica"


def register_fonts():
    pdfmetrics.registerFont(UnicodeCIDFont(FONT_CN))


def wrap_text(text: str, font_name: str, font_size: int, max_width: float):
    lines = []
    current = ""
    for ch in text:
        test = current + ch
        if pdfmetrics.stringWidth(test, font_name, font_size) <= max_width:
            current = test
        else:
            if current:
                lines.append(current)
            current = ch
    if current:
        lines.append(current)
    return lines


def draw_text_block(c, text, x, y, width, font_name=FONT_CN, font_size=18, leading=24, color=colors.HexColor("#1f2937")):
    c.setFillColor(color)
    lines = []
    for para in text.split("\n"):
        if not para.strip():
            lines.append("")
            continue
        lines.extend(wrap_text(para, font_name, font_size, width))
    for i, line in enumerate(lines):
        c.setFont(font_name, font_size)
        c.drawString(x, y - i * leading, line)


def draw_bullets(c, items, x, y, width, font_size=17, bullet_color=colors.HexColor("#d97706"), text_color=colors.HexColor("#334155"), gap=26):
    current_y = y
    for item in items:
        c.setFillColor(bullet_color)
        c.circle(x + 6, current_y - 7, 4, fill=1, stroke=0)
        wrapped = wrap_text(item, FONT_CN, font_size, width - 22)
        c.setFillColor(text_color)
        for idx, line in enumerate(wrapped):
            c.setFont(FONT_CN, font_size)
            c.drawString(x + 20, current_y - idx * 22, line)
        current_y -= max(gap, len(wrapped) * 22 + 8)


def draw_cover(c):
    c.setFillColor(colors.HexColor("#0f172a"))
    c.rect(0, 0, PAGE_W, PAGE_H, fill=1, stroke=0)
    c.setFillColor(colors.HexColor("#f8fafc"))
    c.rect(0, PAGE_H - 90, PAGE_W, 90, fill=1, stroke=0)
    c.setFillColor(colors.HexColor("#0f172a"))
    c.setFont(FONT_CN, 30)
    c.drawString(52, PAGE_H - 50, "三角洲行动账号租赁管理系统")
    c.setFillColor(colors.HexColor("#f59e0b"))
    c.rect(52, PAGE_H - 138, 180, 8, fill=1, stroke=0)
    c.setFillColor(colors.white)
    c.setFont(FONT_EN, 24)
    c.drawString(52, PAGE_H - 190, "PROJECT REPORT")
    c.setFont(FONT_CN, 22)
    c.drawString(52, PAGE_H - 230, "DeltaRent 最终汇报")
    c.setFont(FONT_CN, 18)
    c.drawString(52, PAGE_H - 280, "汇报组：第18组")
    c.drawString(52, PAGE_H - 310, "项目名称：DeltaRent 三角洲行动账号租赁管理系统")
    c.drawString(52, PAGE_H - 340, "版本：结项版")
    c.drawString(52, PAGE_H - 370, "日期：2026年5月")

    c.setFillColor(colors.HexColor("#1e293b"))
    c.roundRect(520, 130, 360, 260, 24, fill=1, stroke=0)
    c.setFillColor(colors.HexColor("#f8fafc"))
    c.setFont(FONT_CN, 20)
    c.drawString(548, 360, "小组成员")
    members = [
        "罗季维 - 项目管理 / 文档整理",
        "朱江南 - 后端开发 / 数据库设计",
        "李彬菲 - 前端开发 / 页面联调",
        "胡万军 - 测试文档 / 用例整理",
    ]
    draw_bullets(c, members, 548, 330, 300, font_size=16, gap=32)


def draw_title_bar(c, section_no, title_cn, title_en):
    c.setFillColor(colors.HexColor("#fff7ed"))
    c.rect(0, 0, PAGE_W, PAGE_H, fill=1, stroke=0)
    c.setFillColor(colors.HexColor("#0f172a"))
    c.rect(0, PAGE_H - 88, PAGE_W, 88, fill=1, stroke=0)
    c.setFillColor(colors.HexColor("#f59e0b"))
    c.setFont(FONT_EN, 46)
    c.drawString(MARGIN, PAGE_H - 58, f"{section_no:02d}")
    c.setFillColor(colors.white)
    c.setFont(FONT_CN, 24)
    c.drawString(120, PAGE_H - 48, title_cn)
    c.setFont(FONT_EN, 14)
    c.drawString(122, PAGE_H - 72, title_en)


def draw_footer(c, page_no):
    c.setStrokeColor(colors.HexColor("#cbd5e1"))
    c.line(MARGIN, 24, PAGE_W - MARGIN, 24)
    c.setFillColor(colors.HexColor("#64748b"))
    c.setFont(FONT_EN, 10)
    c.drawRightString(PAGE_W - MARGIN, 10, f"{page_no}")


def draw_image_fit(c, path, x, y, w, h, border=True, bg=colors.white):
    if bg:
        c.setFillColor(bg)
        c.roundRect(x, y, w, h, 10, fill=1, stroke=0)
    img = ImageReader(str(path))
    iw, ih = img.getSize()
    scale = min(w / iw, h / ih)
    dw = iw * scale
    dh = ih * scale
    dx = x + (w - dw) / 2
    dy = y + (h - dh) / 2
    c.drawImage(img, dx, dy, dw, dh, preserveAspectRatio=True, mask="auto")
    if border:
        c.setStrokeColor(colors.HexColor("#cbd5e1"))
        c.roundRect(x, y, w, h, 10, fill=0, stroke=1)


def draw_content_page(c):
    draw_title_bar(c, 0, "目录", "Contents")
    blocks = [
        ("01", "项目与成员分工"),
        ("02", "功能展示"),
        ("03", "核心设计与技术实现"),
        ("04", "测试结果与 Git 记录"),
        ("05", "总结与答辩要点"),
    ]
    x_positions = [70, 520]
    y_positions = [370, 260, 150]
    idx = 0
    for y in y_positions:
        for x in x_positions:
            if idx >= len(blocks):
                break
            no, label = blocks[idx]
            c.setFillColor(colors.white)
            c.roundRect(x, y, 360, 78, 16, fill=1, stroke=0)
            c.setStrokeColor(colors.HexColor("#e2e8f0"))
            c.roundRect(x, y, 360, 78, 16, fill=0, stroke=1)
            c.setFillColor(colors.HexColor("#d97706"))
            c.setFont(FONT_EN, 22)
            c.drawString(x + 22, y + 42, no)
            c.setFillColor(colors.HexColor("#0f172a"))
            c.setFont(FONT_CN, 20)
            c.drawString(x + 90, y + 40, label)
            idx += 1


def draw_team_page(c):
    draw_title_bar(c, 1, "小组成员分工情况", "Division of labor among team members")
    cards = [
        ("罗季维", "组长 / 项目管理", ["需求分析", "计划书与结项报告", "项目推进与整合"]),
        ("朱江南", "后端开发", ["Spring Boot 3 接口实现", "数据库设计与 SQL 脚本", "权限与订单逻辑"]),
        ("李彬菲", "前端开发", ["Vue 页面实现", "前后台联调", "界面样式优化"]),
        ("胡万军", "测试与文档", ["测试用例编写", "测试报告整理", "结项材料校对"]),
    ]
    positions = [(55, 295), (505, 295), (55, 100), (505, 100)]
    for (name, role, tasks), (x, y) in zip(cards, positions):
        c.setFillColor(colors.white)
        c.roundRect(x, y, 400, 155, 18, fill=1, stroke=0)
        c.setStrokeColor(colors.HexColor("#e2e8f0"))
        c.roundRect(x, y, 400, 155, 18, fill=0, stroke=1)
        c.setFillColor(colors.HexColor("#0f172a"))
        c.setFont(FONT_CN, 22)
        c.drawString(x + 24, y + 118, name)
        c.setFillColor(colors.HexColor("#d97706"))
        c.setFont(FONT_CN, 16)
        c.drawString(x + 24, y + 92, role)
        draw_bullets(c, tasks, x + 24, y + 68, 350, font_size=14, gap=24)


def draw_feature_overview(c):
    draw_title_bar(c, 2, "功能展示", "Function display")
    c.setFillColor(colors.white)
    c.roundRect(50, 90, 860, 340, 20, fill=1, stroke=0)
    c.setStrokeColor(colors.HexColor("#e2e8f0"))
    c.roundRect(50, 90, 860, 340, 20, fill=0, stroke=1)
    left = [
        "游客：首页、租号大厅、公告浏览",
        "普通用户：注册、登录、下单、查看订单、提交申诉",
        "我要上架：用户可提交账号并进入待审核状态",
    ]
    right = [
        "后台看板：统计指标与最近订单",
        "后台管理：用户、账号、订单、公告管理",
        "安全控制：JWT 登录态 + RBAC 权限访问",
    ]
    c.setFillColor(colors.HexColor("#0f172a"))
    c.setFont(FONT_CN, 20)
    c.drawString(80, 390, "系统最终交付功能")
    draw_bullets(c, left, 80, 350, 360, font_size=16, gap=32)
    draw_bullets(c, right, 480, 350, 360, font_size=16, gap=32)
    c.setFillColor(colors.HexColor("#475569"))
    draw_text_block(
        c,
        "系统已覆盖从游客浏览、用户下单、用户上架到后台管理的完整课程项目链路，支撑最终答辩演示。",
        80,
        150,
        760,
        font_size=15,
        leading=22,
    )


def draw_screenshot_page(c, section_no, title_cn, title_en, images):
    draw_title_bar(c, section_no, title_cn, title_en)
    for item in images:
        x, y, w, h, filename, caption = item
        draw_image_fit(c, ASSETS / filename, x, y, w, h)
        c.setFillColor(colors.HexColor("#0f172a"))
        c.setFont(FONT_CN, 12)
        c.drawCentredString(x + w / 2, y - 16, caption)


def draw_arch_page(c):
    draw_title_bar(c, 3, "核心设计思路与技术实现", "Core design ideas and technical realization")
    draw_image_fit(c, ASSETS / "fig_architecture.png", 70, 165, 360, 245)
    c.setFillColor(colors.HexColor("#0f172a"))
    c.setFont(FONT_CN, 18)
    c.drawString(470, 390, "总体架构")
    bullets = [
        "前端采用 Vue 3 + TypeScript + Vite，负责页面路由、状态管理与交互展示",
        "后端采用 Spring Boot 3 + Spring Security + JWT，负责认证授权与业务接口",
        "MySQL 负责持久化用户、账号、订单、公告、申诉等核心数据",
        "系统采用前后端分离结构，便于模块化开发、联调与后续扩展",
    ]
    draw_bullets(c, bullets, 470, 352, 400, font_size=15, gap=28)


def draw_db_page(c):
    draw_title_bar(c, 3, "数据库设计", "Database design")
    draw_image_fit(c, ASSETS / "fig_er.png", 55, 120, 480, 320)
    c.setFillColor(colors.HexColor("#0f172a"))
    c.setFont(FONT_CN, 18)
    c.drawString(575, 395, "关键实体关系")
    bullets = [
        "sys_user 与 sys_role 通过 sys_user_role 建立多对多关系",
        "rental_product 记录账号商品、价格、皮肤、段位、押金等字段",
        "rental_order 保存订单快照、状态流转、联系方式和时间信息",
        "appeal_record 关联订单、申诉用户和处理人，用于售后处理",
        "notice 与 operation_log 分别支撑内容运营和后台行为审计",
    ]
    draw_bullets(c, bullets, 575, 360, 320, font_size=15, gap=26)


def draw_tech_page(c):
    draw_title_bar(c, 3, "关键实现", "Implementation highlights")
    left = [
        "JWT 登录成功后返回 token，前端通过 Axios 拦截器统一附带 Authorization 请求头",
        "基于 Spring Security 的 RBAC 权限控制区分游客、用户、客服、管理员访问范围",
        "订单服务层对重复下单、不可租账号、非法状态跳转做显式拦截",
    ]
    right = [
        "“我要上架”页面根据哈夫币数额与比例自动计算租金",
        "后台管理支持用户、账号、订单、公告的 CRUD 与状态维护",
        "前端使用动态路由导入优化加载，并统一维护 API 调用层",
    ]
    c.setFillColor(colors.white)
    c.roundRect(50, 110, 400, 310, 18, fill=1, stroke=0)
    c.roundRect(510, 110, 400, 310, 18, fill=1, stroke=0)
    c.setStrokeColor(colors.HexColor("#e2e8f0"))
    c.roundRect(50, 110, 400, 310, 18, fill=0, stroke=1)
    c.roundRect(510, 110, 400, 310, 18, fill=0, stroke=1)
    c.setFillColor(colors.HexColor("#0f172a"))
    c.setFont(FONT_CN, 18)
    c.drawString(74, 385, "后端实现")
    c.drawString(534, 385, "前端实现")
    draw_bullets(c, left, 74, 348, 350, font_size=15, gap=30)
    draw_bullets(c, right, 534, 348, 350, font_size=15, gap=30)


def draw_test_page(c):
    draw_title_bar(c, 4, "测试结果", "Test results")
    c.setFillColor(colors.white)
    c.roundRect(55, 95, 850, 345, 18, fill=1, stroke=0)
    c.setStrokeColor(colors.HexColor("#e2e8f0"))
    c.roundRect(55, 95, 850, 345, 18, fill=0, stroke=1)
    c.setFillColor(colors.HexColor("#0f172a"))
    c.setFont(FONT_CN, 22)
    c.drawString(80, 395, "测试结论")
    bullets = [
        "完成功能测试用例 110 个，全部通过，通过率 100%",
        "覆盖模块：注册、登录、首页、租号大厅、我要上架、我的订单、个人中心、后台管理、游客访问、找回密码",
        "前端 `vue-tsc` 与 `vite build` 通过",
        "后端 `gradlew test` 通过，服务层测试覆盖订单与账号核心业务规则",
        "后端健康检查接口 `/api/health` 正常返回，系统达到可演示状态",
    ]
    draw_bullets(c, bullets, 80, 350, 770, font_size=16, gap=32)
    c.setFillColor(colors.HexColor("#f59e0b"))
    c.setFont(FONT_EN, 46)
    c.drawString(690, 130, "110 / 110")
    c.setFont(FONT_CN, 18)
    c.drawString(690, 105, "功能测试全部通过")


def draw_git_page(c, commits):
    draw_title_bar(c, 4, "项目的 Git 更新历史记录", "Git history")
    c.setFillColor(colors.white)
    c.roundRect(55, 95, 850, 345, 18, fill=1, stroke=0)
    c.setStrokeColor(colors.HexColor("#e2e8f0"))
    c.roundRect(55, 95, 850, 345, 18, fill=0, stroke=1)
    c.setFillColor(colors.HexColor("#0f172a"))
    c.setFont(FONT_CN, 20)
    c.drawString(80, 400, "近期代表性提交")
    y = 365
    for commit in commits[:8]:
        sha, msg = commit.split(" ", 1)
        c.setFillColor(colors.HexColor("#d97706"))
        c.circle(86, y + 3, 4, fill=1, stroke=0)
        c.setFillColor(colors.HexColor("#0f172a"))
        c.setFont(FONT_EN, 11)
        c.drawString(100, y, sha)
        wrapped = wrap_text(msg, FONT_CN, 14, 680)
        for idx, line in enumerate(wrapped):
            c.setFont(FONT_CN, 14)
            c.drawString(165, y - idx * 18, line)
        y -= max(34, len(wrapped) * 18 + 10)
    c.setFillColor(colors.HexColor("#475569"))
    c.setFont(FONT_CN, 14)
    c.drawString(80, 120, "仓库地址：https://github.com/JWLuo0719/DeltaRent")


def draw_summary_page(c):
    draw_title_bar(c, 5, "项目总结", "Conclusion")
    left = [
        "完成从游客浏览、用户下单、用户上架到后台管理的完整业务闭环",
        "实现了前后端分离、JWT 鉴权、RBAC 权限控制和数据库主链路",
        "测试文档、结项报告、架构图、ER 图与页面截图已同步整理",
    ]
    right = [
        "系统为课程原型，不接入真实支付、真实短信和自动化上号能力",
        "后续可继续扩展对象存储、消息通知、操作审计和性能优化",
        "当前版本已满足课程最终展示、讲解和答辩需要",
    ]
    c.setFillColor(colors.white)
    c.roundRect(55, 95, 405, 330, 18, fill=1, stroke=0)
    c.roundRect(500, 95, 405, 330, 18, fill=1, stroke=0)
    c.setStrokeColor(colors.HexColor("#e2e8f0"))
    c.roundRect(55, 95, 405, 330, 18, fill=0, stroke=1)
    c.roundRect(500, 95, 405, 330, 18, fill=0, stroke=1)
    c.setFillColor(colors.HexColor("#0f172a"))
    c.setFont(FONT_CN, 20)
    c.drawString(80, 390, "完成情况")
    c.drawString(525, 390, "后续方向")
    draw_bullets(c, left, 80, 350, 350, font_size=16, gap=34)
    draw_bullets(c, right, 525, 350, 350, font_size=16, gap=34)


def build_pdf():
    register_fonts()
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    c = canvas.Canvas(str(OUTPUT_PDF), pagesize=(PAGE_W, PAGE_H))
    commits = [
        "33d3349 修正问题工作区的问题",
        "d80d6c3 Merge branch 'myfix'",
        "763db06 Merge branch 'docs-test'",
        "0dde72e 调整下单页金额为租金 + 押金 + 服务费(租金5%)，完善订单状态流转",
        "9916a34 把小时改成对应天数",
        "be8e6d7 给客服添加处理售后的功能",
        "4534779 docs: 添加修复验证记录（4个Bug全部验证通过）",
        "1f9eaa1 docs: 补充Bug记录、界面演示截图及测试文档",
    ]

    pages = [
        lambda: draw_cover(c),
        lambda: draw_content_page(c),
        lambda: draw_team_page(c),
        lambda: draw_feature_overview(c),
        lambda: draw_screenshot_page(
            c, 2, "功能展示 - 用户认证与首页", "Function display - auth and portal",
            [
                (55, 250, 260, 180, "01-login.png", "登录页"),
                (335, 250, 260, 180, "02-register.png", "注册页"),
                (615, 250, 290, 180, "03-home-1.png", "首页顶部"),
                (120, 30, 720, 190, "03-home-2.png", "首页内容区"),
            ],
        ),
        lambda: draw_screenshot_page(
            c, 2, "功能展示 - 租号与下单", "Function display - rentals and order creation",
            [
                (55, 125, 410, 290, "04-rentals.png", "租号大厅"),
                (495, 125, 410, 290, "05-order-create.png", "创建订单页"),
            ],
        ),
        lambda: draw_screenshot_page(
            c, 2, "功能展示 - 上架与订单中心", "Function display - publish and order center",
            [
                (40, 250, 215, 165, "06-publish.png", "我要上架"),
                (275, 250, 215, 165, "07-orders.png", "我的订单"),
                (510, 250, 215, 165, "08-order-detail.png", "订单详情"),
                (745, 250, 175, 165, "09-profile.png", "个人中心"),
            ],
        ),
        lambda: draw_screenshot_page(
            c, 2, "功能展示 - 后台管理", "Function display - admin console",
            [
                (40, 275, 260, 145, "10-admin-dashboard.png", "后台看板"),
                (320, 275, 260, 145, "11-admin-users.png", "用户管理"),
                (600, 275, 260, 145, "12-admin-accounts.png", "账号管理"),
                (180, 70, 260, 145, "13-admin-orders.png", "订单管理"),
                (520, 70, 260, 145, "14-admin-notice.png", "公告管理"),
            ],
        ),
        lambda: draw_arch_page(c),
        lambda: draw_db_page(c),
        lambda: draw_tech_page(c),
        lambda: draw_test_page(c),
        lambda: draw_git_page(c, commits),
        lambda: draw_summary_page(c),
    ]

    for idx, page in enumerate(pages, start=1):
        page()
        draw_footer(c, idx)
        c.showPage()

    c.save()
    return len(pages)


if __name__ == "__main__":
    count = build_pdf()
    print(f"generated: {OUTPUT_PDF}")
    print(f"pages: {count}")
