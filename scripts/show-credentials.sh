#!/bin/bash

# Show Credentials Script for Gefrierschrank App
# Displays security credentials and connection information

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_FILE="$SCRIPT_DIR/../backend-dev.log"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

log_header() {
    echo -e "${PURPLE}$1${NC}"
}

echo
log_header "🔐 Gefrierschrank-App Security Credentials"
echo "=========================================="

if [ -f "$LOG_FILE" ]; then
    # Extract password from log file
    PASSWORD=$(grep "Using generated security password:" "$LOG_FILE" | tail -1 | sed 's/.*Using generated security password: \([A-Za-z0-9-]*\).*/\1/')
    
    if [ -n "$PASSWORD" ] && [ "$PASSWORD" != "" ]; then
        log_success "Found security credentials:"
        echo
        echo "👤 Username: user"
        echo "🔐 Password: $PASSWORD"
        echo
        echo "🌐 Access URLs:"
        echo "   API:      http://localhost:8080/api"
        echo "   H2 DB:    http://localhost:8080/h2-console"
        echo "   Frontend: http://localhost:5173"
        echo
        echo "💡 Test API access:"
        echo "   curl -u user:$PASSWORD http://localhost:8080/api/products"
        echo
        echo "📊 H2 Database Connection:"
        echo "   JDBC URL: jdbc:h2:mem:testdb"
        echo "   Username: sa"
        echo "   Password: (empty)"
        echo
        echo "🔧 Quick Actions:"
        echo "   npm run db:status    - Check database status"
        echo "   npm run db:seed      - Add sample data"
        echo "   npm run stop         - Stop all services"
    else
        log_error "Could not find security password in log file"
        log_info "Make sure the backend is running and check backend-dev.log"
        echo
        echo "🚀 Start the application:"
        echo "   npm run dev          - Start both backend and frontend"
        echo "   npm run dev:backend  - Start only backend"
    fi
else
    log_error "Backend log file not found: $LOG_FILE"
    log_info "Start the application first"
    echo
    echo "🚀 Available commands:"
    echo "   npm run dev          - Start both backend and frontend"
    echo "   ./scripts/start-dev.sh - Alternative start script"
    echo
    echo "📁 Expected log location: $LOG_FILE"
fi

echo "=========================================="
echo