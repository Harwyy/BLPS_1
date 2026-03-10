@echo off
set BASE_URL=http://localhost:8080/api/order

echo ===== POST /api/order/info =====
curl -X GET %BASE_URL%/info -H "X-Order-Id: 1"
| powershell -Command "$raw = ($input | Out-String); try { $raw | ConvertFrom-Json | ConvertTo-Json -Depth 10 } catch { Write-Host 'Ответ не является JSON:' $raw }"


echo.
pause