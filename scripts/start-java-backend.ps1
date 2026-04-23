$projectRoot = Split-Path -Parent $PSScriptRoot
$serverRoot = Join-Path $projectRoot 'src\\server'
$jdkHome = 'D:\ProgrammingLanguage\Java\Jdk-21'
$gradleHome = Join-Path $projectRoot '.tools\\gradle-8.10.2'
$gradleExe = Join-Path $gradleHome 'bin\\gradle.bat'

if (-not (Test-Path $jdkHome)) {
  Write-Error "未找到 JDK 21：$jdkHome"
  exit 1
}

$env:JAVA_HOME = $jdkHome
$env:Path = "$jdkHome\\bin;$env:Path"

if (-not (Test-Path $gradleExe)) {
  Write-Error "未找到本地 Gradle：$gradleExe。请先完成 Gradle 下载。"
  exit 1
}

Set-Location $serverRoot
& $gradleExe bootRun --args="--spring.profiles.active=local"
