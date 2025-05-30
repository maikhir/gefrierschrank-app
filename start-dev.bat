@echo off
REM Gefrierschrank-App Development Startup Script (Windows)
REM Starts backend (Spring Boot) and frontend (Vue.js) simultaneously

echo 🚀 Starting Gefrierschrank-App Development Environment...
echo ==================================================

REM Check if we're in the right directory
if not exist "backend" (
    echo ❌ Error: backend directory not found
    echo Please run this script from the project root directory
    pause
    exit /b 1
)

if not exist "frontend" (
    echo ❌ Error: frontend directory not found
    echo Please run this script from the project root directory
    pause
    exit /b 1
)

echo 📋 Checking prerequisites...

REM Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java is not installed or not in PATH
    pause
    exit /b 1
)

REM Check if Node.js is available
node --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Node.js is not installed or not in PATH
    pause
    exit /b 1
)

REM Check if npm is available
npm --version >nul 2>&1
if errorlevel 1 (
    echo ❌ npm is not installed or not in PATH
    pause
    exit /b 1
)

echo ✅ All prerequisites found
echo.

REM Install frontend dependencies if node_modules doesn't exist
if not exist "frontend\node_modules" (
    echo 📦 Installing frontend dependencies...
    cd frontend
    call npm install
    cd ..
    echo ✅ Frontend dependencies installed
    echo.
)

echo 🔧 Starting services...
echo.

REM Start backend in new window
echo 🟢 Starting Spring Boot backend...
start "Backend - Spring Boot" cmd /k "cd backend && mvnw.cmd spring-boot:run"

REM Wait a moment for backend to start
timeout /t 3 /nobreak >nul

REM Start frontend in new window
echo 🟦 Starting Vue.js frontend...
start "Frontend - Vue.js" cmd /k "cd frontend && npm run dev"

echo.
echo 🎉 Development environment started successfully!
echo ==================================================
echo 🟢 Backend:  Spring Boot running on http://localhost:8080
echo    📋 API:   http://localhost:8080/api
echo    📊 H2 DB: http://localhost:8080/h2-console
echo.
echo 🟦 Frontend: Vue.js running on http://localhost:5173
echo    🌐 App:   http://localhost:5173
echo.
echo ⚠️  Close the terminal windows to stop the services
echo ==================================================
echo.
pause