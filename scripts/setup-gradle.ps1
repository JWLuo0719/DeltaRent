$projectRoot = Split-Path -Parent $PSScriptRoot
$toolsRoot = Join-Path $projectRoot '.tools'
$zipPath = Join-Path $toolsRoot 'gradle-8.10.2-bin.zip'
$extractRoot = Join-Path $toolsRoot 'gradle-8.10.2'

if (-not (Test-Path $toolsRoot)) {
  New-Item -ItemType Directory -Path $toolsRoot | Out-Null
}

if (Test-Path $extractRoot) {
  Write-Host "Gradle 已存在：$extractRoot"
  exit 0
}

$url = 'https://services.gradle.org/distributions/gradle-8.10.2-bin.zip'
Write-Host "Downloading $url"
Invoke-WebRequest -Uri $url -OutFile $zipPath

Write-Host "Extracting to $toolsRoot"
Expand-Archive -Path $zipPath -DestinationPath $toolsRoot -Force

Write-Host "Gradle 已安装到：$extractRoot"
