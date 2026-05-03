$javaHome = "C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot"
$userPath = [Environment]::GetEnvironmentVariable('PATH', 'User')
if ($userPath -notlike "*$javaHome*") {
    [Environment]::SetEnvironmentVariable('PATH', "$javaHome\bin;$userPath", 'User')
}
[Environment]::SetEnvironmentVariable('JAVA_HOME', $javaHome, 'User')
Write-Host "JAVA_HOME set to: $javaHome"
Write-Host "Java bin added to PATH"