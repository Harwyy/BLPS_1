@echo off
set BASE_URL=http://localhost:8080/api/order

echo ===== POST /api/order/check =====
curl -s -X POST %BASE_URL%/check ^
  -H "Content-Type: application/json" ^
  -d "{\"userId\":1,\"restaurantId\":1,\"items\":[{\"productId\":3,\"quantity\":2},{\"productId\":3,\"quantity\":1}]}" ^
  | powershell -Command "$raw = ($input | Out-String); try { $raw | ConvertFrom-Json | ConvertTo-Json -Depth 10 } catch { Write-Host 'Ответ не является JSON:' $raw }"


echo.
pause