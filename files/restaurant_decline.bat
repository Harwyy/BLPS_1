@echo off
set BASE_URL=http://localhost:8080/api/restaurant

echo ===== PUT /api/restaurant/decline  =====
curl -s -X PUT %BASE_URL%/decline ^
  -H "X-Restaurant-Id: 1" ^
  -H "Content-Type: application/json" ^
  -d "3" ^
  | powershell -Command "$raw = ($input | Out-String); try { $raw | ConvertFrom-Json | ConvertTo-Json -Depth 10 } catch { Write-Host 'Ответ не является JSON:' $raw }"

echo.
pause