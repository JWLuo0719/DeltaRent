#!/usr/bin/env bash
# ============================================================
# DeltaRent API 冒烟测试脚本
# 用法: bash scripts/test-api.sh [BASE_URL]
# ============================================================
set -euo pipefail

BASE_URL="${1:-http://localhost:8080}"
PASS=0
FAIL=0
TOKEN=""

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

ok()  { echo -e "  ${GREEN}PASS${NC} $1"; PASS=$((PASS + 1)); }
fail() { echo -e "  ${RED}FAIL${NC} $1 — $2"; FAIL=$((FAIL + 1)); }

do_curl() {
  local method="$1" url="$2" data="${3:-}" expected_code="${4:-200}"
  local hdr_auth=()
  if [ -n "$TOKEN" ]; then
    hdr_auth=(-H "Authorization: Bearer $TOKEN")
  fi

  if [ -n "$data" ]; then
    resp=$(curl -s -w "\n%{http_code}" -X "$method" "$BASE_URL$url" \
      "${hdr_auth[@]}" -H "Content-Type: application/json" -d "$data" \
      --connect-timeout 5 --max-time 10 2>/dev/null) || true
  else
    resp=$(curl -s -w "\n%{http_code}" -X "$method" "$BASE_URL$url" \
      "${hdr_auth[@]}" --connect-timeout 5 --max-time 10 2>/dev/null) || true
  fi

  echo "$resp"
}

check() {
  local desc="$1" method="$2" url="$3" data="${4:-}" expected_code="${5:-200}"
  local resp http_code
  resp=$(do_curl "$method" "$url" "$data" "$expected_code")
  http_code=$(echo "$resp" | tail -1)
  local body
  body=$(echo "$resp" | sed '$d')

  if [ "$http_code" = "$expected_code" ]; then
    ok "$desc"
  else
    fail "$desc" "expected HTTP $expected_code, got $http_code body=${body:0:120}"
  fi
}

echo "=============================================="
echo " DeltaRent API Smoke Test"
echo " Target: $BASE_URL"
echo "=============================================="
echo ""

# ---- 1. Public endpoints ----
echo "--- 1. Public ---"

check "GET  /api/health"              GET  "/api/health"
check "GET  /api/portal/summary"      GET  "/api/portal/summary"
check "GET  /api/rentals"             GET  "/api/rentals"
check "GET  /api/rentals/1001"        GET  "/api/rentals/1001"
check "GET  /api/notices"             GET  "/api/notices"
check "GET  /api/notices/1"           GET  "/api/notices/1"

# ---- 2. Auth ----
echo ""
echo "--- 2. Auth ---"

check "POST /api/auth/login"          POST "/api/auth/login" \
  '{"username":"admin","password":"123456"}'

# Get token
TOKEN=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo -e "  ${RED}FAIL${NC} cannot get JWT token"
  FAIL=$((FAIL + 1))
  exit 1
fi
echo -e "  ${GREEN}PASS${NC} got token (${TOKEN:0:30}...)"
PASS=$((PASS + 1))

check "POST /api/auth/register (dup)" POST "/api/auth/register" \
  '{"username":"admin","password":"123456","nickname":"dup","phone":"13800000000"}'

# ---- 3. Orders ----
echo ""
echo "--- 3. Orders ---"

check "POST /api/orders"              POST "/api/orders" \
  '{"accountId":1001,"rentHours":3,"contactInfo":"13800000000","remark":"smoke test"}'

check "GET  /api/orders/my"           GET  "/api/orders/my"
check "GET  /api/orders"              GET  "/api/orders"
check "GET  /api/orders/1"            GET  "/api/orders/1"
check "PUT  /api/orders/1/status"     PUT  "/api/orders/1/status" \
  '{"status":"IN_PROGRESS"}'

# ---- 4. Notices ----
echo ""
echo "--- 4. Notices ---"

check "GET  /api/notices/all"         GET  "/api/notices/all"
check "POST /api/notices"             POST "/api/notices" \
  '{"title":"Smoke Test","content":"Auto test","status":1}'
check "PUT  /api/notices/1"           PUT  "/api/notices/1" \
  '{"title":"Updated","content":"Updated content","status":1}'
check "DELETE /api/notices/2"         DELETE "/api/notices/2"

# ---- 5. Rentals ----
echo ""
echo "--- 5. Rentals ---"

check "POST /api/rentals"             POST "/api/rentals" \
  '{"name":"Test","category":"test","tagText":"tag","hourPrice":"10","coinAmountText":"1000","equipmentLevelText":"Basic","warehouseValueText":"Low","status":"AVAILABLE","description":"test"}'
check "PUT  /api/rentals/1001/status" PUT  "/api/rentals/1001/status" \
  '{"status":"MAINTENANCE"}'
check "PUT  /api/rentals/1001/status" PUT  "/api/rentals/1001/status" \
  '{"status":"AVAILABLE"}'

# ---- 6. Appeals ----
echo ""
echo "--- 6. Appeals ---"

check "POST /api/appeals"             POST "/api/appeals" \
  '{"orderType":"RENTAL","orderId":1,"content":"smoke test appeal"}'
check "GET  /api/appeals/my"          GET  "/api/appeals/my"
check "GET  /api/appeals"             GET  "/api/appeals"
check "PUT  /api/appeals/1/handle"    PUT  "/api/appeals/1/handle" \
  '{"status":"RESOLVED"}'

# ---- 7. Dashboard ----
echo ""
echo "--- 7. Dashboard ---"

check "GET  /api/dashboard/overview"  GET  "/api/dashboard/overview"

# ---- Summary ----
echo ""
echo "=============================================="
echo " Result: ${GREEN}$PASS passed${NC} / ${RED}$FAIL failed${NC}"
echo " Total:  $((PASS + FAIL)) endpoints"
echo "=============================================="

[ "$FAIL" -eq 0 ] || exit 1
