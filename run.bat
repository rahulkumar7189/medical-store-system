@echo off
echo ============================================
echo   Pharmacy Management System - Launcher
echo ============================================
echo.

set MAVEN_HOME=C:\Users\Asus\Downloads\Case\maven\apache-maven-3.9.6
set PATH=%MAVEN_HOME%\bin;%PATH%

echo Starting application...
echo.

cd /d "%~dp0"
call mvn javafx:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Failed to launch. Make sure MySQL is running and .env is configured.
    pause
)
