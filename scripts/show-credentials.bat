@echo off
REM Show Credentials Script for Gefrierschrank App (Windows)
REM Displays security credentials and connection information

setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
set "LOG_FILE=%SCRIPT_DIR%..\backend-dev.log"

echo.
echo Gefrierschrank-App Security Credentials
echo ==========================================

if exist "%LOG_FILE%" (
    echo Checking log file: %LOG_FILE%
    
    REM Extract password from log file (simplified Windows version)
    for /f "tokens=*" %%a in ('findstr "Using generated security password:" "%LOG_FILE%"') do (
        set "line=%%a"
        for /f "tokens=6" %%b in ("!line!") do (
            set "PASSWORD=%%b"
        )
    )
    
    if defined PASSWORD (
        echo Found security credentials:
        echo.
        echo Username: user
        echo Password: !PASSWORD!
        echo.
        echo Access URLs:
        echo    API:      http://localhost:8080/api
        echo    H2 DB:    http://localhost:8080/h2-console
        echo    Frontend: http://localhost:5173
        echo.
        echo Test API access:
        echo    curl -u user:!PASSWORD! http://localhost:8080/api/products
        echo.
        echo H2 Database Connection:
        echo    JDBC URL: jdbc:h2:mem:testdb
        echo    Username: sa
        echo    Password: ^(empty^)
        echo.
        echo Quick Actions:
        echo    npm run db:status:win  - Check database status
        echo    npm run db:seed:win    - Add sample data
        echo    npm run stop           - Stop all services
    ) else (
        echo Error: Could not find security password in log file
        echo Make sure the backend is running and check backend-dev.log
        echo.
        echo Start the application:
        echo    npm run dev            - Start both backend and frontend
        echo    npm run dev:backend    - Start only backend
    )
) else (
    echo Error: Backend log file not found: %LOG_FILE%
    echo Start the application first
    echo.
    echo Available commands:
    echo    npm run dev              - Start both backend and frontend
    echo    scripts\start-dev.bat    - Alternative start script
    echo.
    echo Expected log location: %LOG_FILE%
)

echo ==========================================
echo.

endlocal