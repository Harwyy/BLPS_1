@echo off
chcp 65001 > nul
set BASE_URL=http://localhost:8080/blps-0.0.1-SNAPSHOT/api/auth

echo ===== POST /api/auth/login =====
curl -s -X POST "%BASE_URL%/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"client1\",\"password\":\"password\"}" ^
  | powershell -Command "$raw = ($input | Out-String); try { $raw | ConvertFrom-Json | ConvertTo-Json -Depth 10 } catch { Write-Host 'Ответ не является JSON:' $raw }"

echo.
pause