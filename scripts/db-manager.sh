#!/bin/bash

# Database Manager Script for Gefrierschrank App
# Usage:
#   ./scripts/db-manager.sh seed [count]   - Generate sample data
#   ./scripts/db-manager.sh clean          - Clean/reset database
#   ./scripts/db-manager.sh status         - Show database status

API_BASE="http://localhost:8080/api"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Helper functions
log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

log_header() {
    echo -e "${PURPLE}$1${NC}"
}

# Check if curl is available
check_dependencies() {
    if ! command -v curl &> /dev/null; then
        log_error "curl is required but not installed."
        exit 1
    fi
    
    if ! command -v jq &> /dev/null; then
        log_warning "jq is not installed. JSON output will be less pretty."
    fi
}

# Make HTTP request
make_request() {
    local method="$1"
    local endpoint="$2"
    local data="$3"
    local url="${API_BASE}${endpoint}"
    
    if [ "$method" = "GET" ]; then
        curl -s -X GET \
             -H "Content-Type: application/json" \
             "$url"
    elif [ "$method" = "POST" ]; then
        curl -s -X POST \
             -H "Content-Type: application/json" \
             -d "$data" \
             "$url"
    elif [ "$method" = "DELETE" ]; then
        curl -s -X DELETE \
             -H "Content-Type: application/json" \
             "$url"
    fi
}

# Check if backend is running
check_backend() {
    log_info "Checking backend connection..."
    local response
    response=$(curl -s -o /dev/null -w "%{http_code}" "${API_BASE}/products?size=1")
    
    if [ "$response" != "200" ]; then
        log_error "Backend not reachable at ${API_BASE}"
        log_error "Make sure the backend is running on http://localhost:8080"
        exit 1
    fi
    log_success "Backend is running"
}

# Create categories
create_categories() {
    log_header "📁 Creating categories..."
    
    local categories=(
        '{"name":"Fleisch","color":"#ef4444","defaultStorageDays":365,"description":"Fleisch und Geflügel"}'
        '{"name":"Fisch","color":"#06b6d4","defaultStorageDays":180,"description":"Fisch und Meeresfrüchte"}'
        '{"name":"Gemüse","color":"#22c55e","defaultStorageDays":365,"description":"Gefrorenes Gemüse"}'
        '{"name":"Obst","color":"#f59e0b","defaultStorageDays":365,"description":"Gefrorenes Obst"}'
        '{"name":"Brot","color":"#8b5cf6","defaultStorageDays":90,"description":"Brot und Backwaren"}'
        '{"name":"Fertiggerichte","color":"#ec4899","defaultStorageDays":180,"description":"Fertiggerichte und Convenience"}'
        '{"name":"Milchprodukte","color":"#3b82f6","defaultStorageDays":60,"description":"Milch, Käse, Joghurt"}'
        '{"name":"Andere","color":"#6b7280","defaultStorageDays":180,"description":"Sonstige Produkte"}'
    )
    
    for category_data in "${categories[@]}"; do
        local name=$(echo "$category_data" | grep -o '"name":"[^"]*"' | cut -d'"' -f4)
        local response
        response=$(make_request "POST" "/categories" "$category_data")
        
        if echo "$response" | grep -q '"id"'; then
            log_success "Created category: $name"
        else
            log_warning "Category $name might already exist"
        fi
    done
}

# Create locations
create_locations() {
    log_header "📍 Creating locations..."
    
    local locations=(
        '{"name":"Hauptfach","description":"Hauptgefrierfach","freezerSection":"MAIN","sortOrder":1}'
        '{"name":"Oberes Fach","description":"Oberes Gefrierfach","freezerSection":"TOP","sortOrder":2}'
        '{"name":"Mittleres Fach","description":"Mittleres Gefrierfach","freezerSection":"MIDDLE","sortOrder":3}'
        '{"name":"Unteres Fach","description":"Unteres Gefrierfach","freezerSection":"BOTTOM","sortOrder":4}'
        '{"name":"Schublade 1","description":"Erste Schublade","freezerSection":"DRAWER_1","sortOrder":5}'
        '{"name":"Schublade 2","description":"Zweite Schublade","freezerSection":"DRAWER_2","sortOrder":6}'
        '{"name":"Türfach","description":"Gefriertür","freezerSection":"DOOR","sortOrder":7}'
    )
    
    for location_data in "${locations[@]}"; do
        local name=$(echo "$location_data" | grep -o '"name":"[^"]*"' | cut -d'"' -f4)
        local response
        response=$(make_request "POST" "/locations" "$location_data")
        
        if echo "$response" | grep -q '"id"'; then
            log_success "Created location: $name"
        else
            log_warning "Location $name might already exist"
        fi
    done
}

# Generate random date
random_date() {
    local days_back="$1"
    local days_forward="$2"
    local base_date
    base_date=$(date +%s)
    local random_offset=$((RANDOM % (days_back + days_forward) - days_back))
    local random_timestamp=$((base_date + random_offset * 86400))
    date -r "$random_timestamp" "+%Y-%m-%d"
}

# Generate random float
random_float() {
    local min="$1"
    local max="$2"
    local range=$((max - min))
    local random_part=$((RANDOM % (range * 100)))
    echo "scale=2; $min + $random_part / 100" | bc
}

# Create sample products
create_sample_products() {
    local count="$1"
    log_header "🥘 Creating $count sample products..."
    
    # Get categories and locations
    local categories_response
    local locations_response
    categories_response=$(make_request "GET" "/categories")
    locations_response=$(make_request "GET" "/locations")
    
    if [ -z "$categories_response" ] || [ -z "$locations_response" ]; then
        log_error "Could not fetch categories or locations"
        return 1
    fi
    
    # Product names array
    local products=(
        "Hähnchenbrust" "Rindersteak" "Lachsfilet" "Tiefkühlpizza" "Brokkoli"
        "Erbsen" "Heidelbeeren" "Vollkornbrot" "Butter" "Milch"
        "Joghurt" "Käse" "Eiscreme" "Pommes Frites" "Fischstäbchen"
        "Spinat" "Mais" "Erdbeeren" "Mango" "Hähnchen-Nuggets"
        "Schweinefilet" "Kabeljau" "Lasagne" "Rahmspinat" "Blumenkohl"
        "Karotten" "Himbeeren" "Ciabatta" "Frischkäse" "Sahne"
        "Mozzarella" "Vanilleeis" "Süßkartoffel-Pommes" "Pangasius"
        "Hackfleisch" "Shrimps" "Tortellini" "Rosenkohl" "Bohnen"
        "Apfelstrudel" "Schokokuchen" "Baguette" "Mascarpone" "Quark"
    )
    
    local units=("kg" "g" "stück" "packung" "portion" "dose" "beutel")
    
    for ((i=1; i<=count; i++)); do
        # Random selections
        local product_name="${products[$((RANDOM % ${#products[@]}))]}"
        local unit="${units[$((RANDOM % ${#units[@]}))]}"
        local quantity
        quantity=$(echo "scale=2; 0.1 + ($RANDOM % 490) / 100" | bc)
        
        # Random dates
        local frozen_date
        frozen_date=$(random_date 60 0)
        local expiration_date
        expiration_date=$(random_date -30 300)
        
        # Get random category and location ID (simplified)
        local category_id=$((RANDOM % 8 + 1))
        local location_id=$((RANDOM % 7 + 1))
        
        local product_data
        product_data=$(cat <<EOF
{
    "name": "$product_name $i",
    "quantity": $quantity,
    "unit": "$unit",
    "frozenDate": "$frozen_date",
    "expirationDate": "$expiration_date",
    "categoryId": $category_id,
    "locationId": $location_id,
    "notes": null
}
EOF
)
        
        local response
        response=$(make_request "POST" "/products" "$product_data")
        
        if echo "$response" | grep -q '"id"'; then
            log_success "Created product: $product_name $i"
        else
            log_error "Failed to create product: $product_name $i"
        fi
    done
}

# Clean database
clean_database() {
    log_header "🧹 Cleaning database..."
    
    # Get all data
    local products_response
    local categories_response
    local locations_response
    
    products_response=$(make_request "GET" "/products?size=1000")
    categories_response=$(make_request "GET" "/categories")
    locations_response=$(make_request "GET" "/locations")
    
    # Delete products first
    if echo "$products_response" | grep -q '"content"'; then
        log_info "Deleting products..."
        # Extract product IDs and delete them
        if command -v jq &> /dev/null; then
            echo "$products_response" | jq -r '.content[]?.id' | while read -r id; do
                if [ -n "$id" ] && [ "$id" != "null" ]; then
                    make_request "DELETE" "/products/$id" > /dev/null
                fi
            done
        else
            log_warning "jq not available, skipping product deletion"
        fi
    fi
    
    # Delete categories
    if echo "$categories_response" | grep -q '"id"'; then
        log_info "Deleting categories..."
        if command -v jq &> /dev/null; then
            echo "$categories_response" | jq -r '.[]?.id' | while read -r id; do
                if [ -n "$id" ] && [ "$id" != "null" ]; then
                    make_request "DELETE" "/categories/$id" > /dev/null
                fi
            done
        fi
    fi
    
    # Delete locations
    if echo "$locations_response" | grep -q '"id"'; then
        log_info "Deleting locations..."
        if command -v jq &> /dev/null; then
            echo "$locations_response" | jq -r '.[]?.id' | while read -r id; do
                if [ -n "$id" ] && [ "$id" != "null" ]; then
                    make_request "DELETE" "/locations/$id" > /dev/null
                fi
            done
        fi
    fi
    
    log_success "Database cleaned successfully"
}

# Show database status
show_status() {
    log_header "📊 Database Status"
    echo "=================================================="
    
    local products_response
    local categories_response
    local locations_response
    
    products_response=$(make_request "GET" "/products?size=1000")
    categories_response=$(make_request "GET" "/categories")
    locations_response=$(make_request "GET" "/locations")
    
    # Count items
    local product_count=0
    local category_count=0
    local location_count=0
    
    if command -v jq &> /dev/null; then
        if echo "$products_response" | jq -e '.totalElements' > /dev/null 2>&1; then
            product_count=$(echo "$products_response" | jq '.totalElements')
        fi
        if echo "$categories_response" | jq -e 'length' > /dev/null 2>&1; then
            category_count=$(echo "$categories_response" | jq 'length')
        fi
        if echo "$locations_response" | jq -e 'length' > /dev/null 2>&1; then
            location_count=$(echo "$locations_response" | jq 'length')
        fi
    else
        # Fallback without jq
        product_count=$(echo "$products_response" | grep -o '"id"' | wc -l || echo "0")
        category_count=$(echo "$categories_response" | grep -o '"id"' | wc -l || echo "0")
        location_count=$(echo "$locations_response" | grep -o '"id"' | wc -l || echo "0")
    fi
    
    echo "📦 Products: $product_count"
    echo "📁 Categories: $category_count"
    echo "📍 Locations: $location_count"
    
    if [ "$category_count" -gt 0 ]; then
        echo
        echo "📁 Categories:"
        if command -v jq &> /dev/null; then
            echo "$categories_response" | jq -r '.[] | "  - \(.name) (\(.defaultStorageDays) days)"'
        fi
    fi
    
    if [ "$location_count" -gt 0 ]; then
        echo
        echo "📍 Locations:"
        if command -v jq &> /dev/null; then
            echo "$locations_response" | jq -r '.[] | "  - \(.name) (\(.freezerSection))"'
        fi
    fi
}

# Seed database
seed_database() {
    local product_count="${1:-50}"
    
    log_header "🌱 Seeding database with sample data..."
    echo "=================================================="
    
    check_backend
    
    create_categories
    create_locations
    
    # Wait a bit for data to be created
    sleep 1
    
    create_sample_products "$product_count"
    
    echo
    log_success "Database seeded successfully!"
    show_status
}

# Confirmation prompt
ask_confirmation() {
    local message="$1"
    echo -n "$message (y/N): "
    read -r response
    [[ "$response" =~ ^[Yy]$ ]]
}

# Main function
main() {
    local command="$1"
    
    log_header "🗄️ Gefrierschrank Database Manager"
    echo "========================================"
    
    check_dependencies
    
    case "$command" in
        "seed")
            local count="${2:-50}"
            seed_database "$count"
            ;;
        "clean")
            if ask_confirmation "⚠️ This will delete ALL data. Are you sure?"; then
                check_backend
                clean_database
            else
                log_error "Operation cancelled"
            fi
            ;;
        "status")
            check_backend
            show_status
            ;;
        *)
            echo "Usage:"
            echo "  $0 seed [count]  - Generate sample data (default: 50 products)"
            echo "  $0 clean         - Clean/reset database"
            echo "  $0 status        - Show database status"
            echo
            echo "Examples:"
            echo "  $0 seed          - Create 50 sample products"
            echo "  $0 seed 100      - Create 100 sample products"
            echo "  $0 status        - Show current database state"
            ;;
    esac
}

# Run main function with all arguments
main "$@"