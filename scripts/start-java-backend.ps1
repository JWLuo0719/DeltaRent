$projectRoot = Split-Path -Parent $PSScriptRoot
$serverRoot = Join-Path $projectRoot 'src\server'
$jdkHome = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot'
$gradleHome = Join-Path $projectRoot '.tools\gradle-8.14'
$gradleExe = Join-Path $gradleHome 'bin\gradle.bat'

if (-not (Test-Path $jdkHome)) {
  Write-Error "JDK 21 was not found at $jdkHome"
  exit 1
}

if (-not (Test-Path $gradleExe)) {
  Write-Error "Gradle was not found at $gradleExe"
  exit 1
}

$env:JAVA_HOME = $jdkHome
$env:Path = "$jdkHome\bin;$env:Path"

$env:JAVA_HOME = $jdkHome
$env:Path = "$jdkHome\bin;$env:Path"

Set-Location $serverRoot
& $gradleExe bootRun
