@echo off
set BASE_URL=http://localhost:8080/api/courier

echo ===== PUT /api/courier/pickup =====
curl -s -X PUT %BASE_URL%/pickup ^
  -H "X-Courier-Id: 1" ^
  -H "Content-Type: application/json" ^
  -d "1" ^
  | powershell -Command "$raw = ($input | Out-String); try { $raw | ConvertFrom-Json | ConvertTo-Json -Depth 10 } catch { Write-Host 'Ответ не является JSON:' $raw }"

echo.
pause