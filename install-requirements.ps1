# LINA Data Portal 개발 환경 설치 스크립트
# 관리자 권한으로 실행 필요

Write-Host "=== LINA Data Portal 개발 환경 설치 ===" -ForegroundColor Green

# Chocolatey 설치 확인
if (!(Get-Command choco -ErrorAction SilentlyContinue)) {
    Write-Host "Chocolatey 설치 중..." -ForegroundColor Yellow
    Set-ExecutionPolicy Bypass -Scope Process -Force
    [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
    iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
}

# Java JDK 17 설치
Write-Host "Java JDK 17 설치 중..." -ForegroundColor Yellow
choco install temurin17 -y

# Maven 설치
Write-Host "Apache Maven 설치 중..." -ForegroundColor Yellow
choco install maven -y

# Git 설치 (이미 설치되어 있지만 최신 버전으로 업데이트)
Write-Host "Git 업데이트 중..." -ForegroundColor Yellow
choco upgrade git -y

# PostgreSQL Client 설치 (선택사항)
$installPsql = Read-Host "PostgreSQL 클라이언트를 설치하시겠습니까? (y/n)"
if ($installPsql -eq 'y' -or $installPsql -eq 'Y') {
    Write-Host "PostgreSQL 클라이언트 설치 중..." -ForegroundColor Yellow
    choco install postgresql -y
}

# 환경변수 새로고침
Write-Host "환경변수 새로고침 중..." -ForegroundColor Yellow
refreshenv

Write-Host "=== 설치 완료! ===" -ForegroundColor Green
Write-Host "새 PowerShell 창을 열고 다음 명령어로 확인하세요:" -ForegroundColor Cyan
Write-Host "java -version" -ForegroundColor White
Write-Host "mvn -version" -ForegroundColor White
Write-Host ""
Write-Host "애플리케이션 실행:" -ForegroundColor Cyan
Write-Host "mvn spring-boot:run" -ForegroundColor White