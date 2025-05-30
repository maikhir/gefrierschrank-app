#!/bin/bash

# Gefrierschrank-App Development Startup Script
# Starts backend (Spring Boot) and frontend (Vue.js) simultaneously
# Usage: ./start-dev.sh [start|stop|restart]

ACTION=${1:-start}

if [ "$ACTION" = "stop" ]; then
    echo "🛑 Stopping Gefrierschrank-App Development Environment..."
    echo "======================================================"
    
    # Kill processes on ports 8080 and 5173
    echo "🔍 Stopping services on ports 8080 and 5173..."
    
    # Kill backend (port 8080)
    BACKEND_PID=$(lsof -ti:8080)
    if [ ! -z "$BACKEND_PID" ]; then
        kill -9 $BACKEND_PID
        echo "✅ Stopped backend (PID: $BACKEND_PID)"
    else
        echo "ℹ️  No backend process found on port 8080"
    fi
    
    # Kill frontend (port 5173)
    FRONTEND_PID=$(lsof -ti:5173)
    if [ ! -z "$FRONTEND_PID" ]; then
        kill -9 $FRONTEND_PID
        echo "✅ Stopped frontend (PID: $FRONTEND_PID)"
    else
        echo "ℹ️  No frontend process found on port 5173"
    fi
    
    # Kill Spring Boot processes
    pkill -f "spring-boot:run" 2>/dev/null && echo "✅ Stopped Spring Boot processes" || echo "ℹ️  No Spring Boot processes found"
    
    # Kill Vite processes
    pkill -f "vite" 2>/dev/null && echo "✅ Stopped Vite processes" || echo "ℹ️  No Vite processes found"
    
    echo ""
    echo "🎉 All services stopped successfully!"
    echo "======================================================"
    exit 0
fi

if [ "$ACTION" = "restart" ]; then
    echo "🔄 Restarting Gefrierschrank-App Development Environment..."
    $0 stop
    sleep 2
    $0 start
    exit 0
fi

echo "🚀 Starting Gefrierschrank-App Development Environment..."
echo "=================================================="

# Check if we're in the right directory
if [ ! -d "backend" ] || [ ! -d "frontend" ]; then
    echo "❌ Error: Please run this script from the project root directory"
    echo "   Expected directories: backend/ and frontend/"
    exit 1
fi

# Function to handle cleanup on script termination
cleanup() {
    echo ""
    echo "🛑 Shutting down services..."
    jobs -p | xargs -r kill
    echo "✅ All services stopped"
    exit 0
}

# Set up trap to catch Ctrl+C and cleanup
trap cleanup SIGINT SIGTERM

echo "📋 Checking prerequisites..."

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed or not in PATH"
    exit 1
fi

# Check if Node.js is available
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed or not in PATH"
    exit 1
fi

# Check if npm is available
if ! command -v npm &> /dev/null; then
    echo "❌ npm is not installed or not in PATH"
    exit 1
fi

echo "✅ All prerequisites found"

# Check if ports are already in use
echo "🔍 Checking if ports are available..."
if lsof -ti:8080 > /dev/null 2>&1; then
    echo "⚠️  Port 8080 is already in use. Stopping existing process..."
    lsof -ti:8080 | xargs kill -9
    sleep 1
fi

if lsof -ti:5173 > /dev/null 2>&1; then
    echo "⚠️  Port 5173 is already in use. Stopping existing process..."
    lsof -ti:5173 | xargs kill -9
    sleep 1
fi

echo "✅ Ports 8080 and 5173 are available"
echo ""

# Install frontend dependencies if node_modules doesn't exist
if [ ! -d "frontend/node_modules" ]; then
    echo "📦 Installing frontend dependencies..."
    cd frontend
    npm install
    cd ..
    echo "✅ Frontend dependencies installed"
    echo ""
fi

echo "🔧 Starting services..."
echo ""

# Start backend in background
echo "🟢 Starting Spring Boot backend..."
cd backend
./mvnw spring-boot:run > ../backend-dev.log 2>&1 &
BACKEND_PID=$!
cd ..

# Wait for backend to start and extract security password
echo "⏳ Waiting for backend to start..."
sleep 5

# Extract security password from log
echo "🔐 Extracting security credentials..."
SECURITY_PASSWORD=""
for i in {1..20}; do
    if [ -f "backend-dev.log" ]; then
        SECURITY_PASSWORD=$(grep "Using generated security password:" backend-dev.log | sed 's/.*Using generated security password: //' | tr -d '\r\n' | awk '{print $1}')
        if [ ! -z "$SECURITY_PASSWORD" ]; then
            break
        fi
    fi
    sleep 1
done

# Start frontend in background
echo "🟦 Starting Vue.js frontend..."
cd frontend
npm run dev > ../frontend-dev.log 2>&1 &
FRONTEND_PID=$!
cd ..

echo ""
echo "🎉 Development environment started successfully!"
echo "=================================================="
echo "🟢 Backend:  Spring Boot running on http://localhost:8080"
echo "   📋 API:   http://localhost:8080/api"
echo "   📊 H2 DB: http://localhost:8080/h2-console"
if [ ! -z "$SECURITY_PASSWORD" ]; then
    echo "   🔐 Security Password: $SECURITY_PASSWORD"
    echo "   👤 Default User: user"
else
    echo "   ⚠️  Could not extract security password - check backend-dev.log"
fi
echo ""
echo "🟦 Frontend: Vue.js running on http://localhost:5173"
echo "   🌐 App:   http://localhost:5173"
echo ""
echo "📝 Logs:"
echo "   Backend:  tail -f backend-dev.log"
echo "   Frontend: tail -f frontend-dev.log"
echo ""
echo "💡 Quick Access:"
echo "   curl -u user:$SECURITY_PASSWORD http://localhost:8080/api/products"
echo ""
echo "⚠️  Press Ctrl+C to stop all services"
echo "=================================================="

# Wait for both processes
wait $BACKEND_PID $FRONTEND_PID