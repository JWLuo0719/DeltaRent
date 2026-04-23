$root = Split-Path -Parent $PSScriptRoot

Start-Process powershell -ArgumentList @(
  '-NoExit',
  '-Command',
  "Set-Location '$root'; npm run dev:mock"
)

Start-Process powershell -ArgumentList @(
  '-NoExit',
  '-Command',
  "Set-Location '$root'; npm run dev:web"
)
