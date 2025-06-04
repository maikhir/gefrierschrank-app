@echo off
REM Database Manager Script for Gefrierschrank App (Windows)
REM Usage:
REM   db-manager.bat seed [count]   - Generate sample data
REM   db-manager.bat clean          - Clean/reset database
REM   db-manager.bat status         - Show database status

setlocal enabledelayedexpansion

set "API_BASE=http://localhost:8080/api"
set "CURL_CMD=curl"

REM Check if curl is available
where curl >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: curl is required but not installed.
    echo Please install curl or use Windows Subsystem for Linux
    exit /b 1
)

REM Helper function to make HTTP requests
:make_request
    set "method=%~1"
    set "endpoint=%~2"
    set "data=%~3"
    set "url=%API_BASE%%endpoint%"
    
    if "%method%"=="GET" (
        %CURL_CMD% -s -X GET -H "Content-Type: application/json" "%url%"
    ) else if "%method%"=="POST" (
        %CURL_CMD% -s -X POST -H "Content-Type: application/json" -d "%data%" "%url%"
    ) else if "%method%"=="DELETE" (
        %CURL_CMD% -s -X DELETE -H "Content-Type: application/json" "%url%"
    )
    goto :eof

REM Check if backend is running
:check_backend
    echo Checking backend connection...
    for /f %%i in ('%CURL_CMD% -s -o nul -w "%%{http_code}" "%API_BASE%/products?size=1"') do set "response=%%i"
    
    if not "%response%"=="200" (
        echo Error: Backend not reachable at %API_BASE%
        echo Make sure the backend is running on http://localhost:8080
        exit /b 1
    )
    echo Backend is running
    goto :eof

REM Create categories
:create_categories
    echo.
    echo Creating categories...
    
    call :make_request "POST" "/categories" "{\"name\":\"Fleisch\",\"color\":\"#ef4444\",\"defaultStorageDays\":365,\"description\":\"Fleisch und Geflugel\"}"
    echo   - Created category: Fleisch
    
    call :make_request "POST" "/categories" "{\"name\":\"Fisch\",\"color\":\"#06b6d4\",\"defaultStorageDays\":180,\"description\":\"Fisch und Meeresfruchte\"}"
    echo   - Created category: Fisch
    
    call :make_request "POST" "/categories" "{\"name\":\"Gemuse\",\"color\":\"#22c55e\",\"defaultStorageDays\":365,\"description\":\"Gefrorenes Gemuse\"}"
    echo   - Created category: Gemuse
    
    call :make_request "POST" "/categories" "{\"name\":\"Obst\",\"color\":\"#f59e0b\",\"defaultStorageDays\":365,\"description\":\"Gefrorenes Obst\"}"
    echo   - Created category: Obst
    
    call :make_request "POST" "/categories" "{\"name\":\"Brot\",\"color\":\"#8b5cf6\",\"defaultStorageDays\":90,\"description\":\"Brot und Backwaren\"}"
    echo   - Created category: Brot
    
    call :make_request "POST" "/categories" "{\"name\":\"Fertiggerichte\",\"color\":\"#ec4899\",\"defaultStorageDays\":180,\"description\":\"Fertiggerichte und Convenience\"}"
    echo   - Created category: Fertiggerichte
    
    call :make_request "POST" "/categories" "{\"name\":\"Milchprodukte\",\"color\":\"#3b82f6\",\"defaultStorageDays\":60,\"description\":\"Milch, Kase, Joghurt\"}"
    echo   - Created category: Milchprodukte
    
    call :make_request "POST" "/categories" "{\"name\":\"Andere\",\"color\":\"#6b7280\",\"defaultStorageDays\":180,\"description\":\"Sonstige Produkte\"}"
    echo   - Created category: Andere
    goto :eof

REM Create locations
:create_locations
    echo.
    echo Creating locations...
    
    call :make_request "POST" "/locations" "{\"name\":\"Hauptfach\",\"description\":\"Hauptgefrierfach\",\"freezerSection\":\"MAIN\",\"sortOrder\":1}"
    echo   - Created location: Hauptfach
    
    call :make_request "POST" "/locations" "{\"name\":\"Oberes Fach\",\"description\":\"Oberes Gefrierfach\",\"freezerSection\":\"TOP\",\"sortOrder\":2}"
    echo   - Created location: Oberes Fach
    
    call :make_request "POST" "/locations" "{\"name\":\"Mittleres Fach\",\"description\":\"Mittleres Gefrierfach\",\"freezerSection\":\"MIDDLE\",\"sortOrder\":3}"
    echo   - Created location: Mittleres Fach
    
    call :make_request "POST" "/locations" "{\"name\":\"Unteres Fach\",\"description\":\"Unteres Gefrierfach\",\"freezerSection\":\"BOTTOM\",\"sortOrder\":4}"
    echo   - Created location: Unteres Fach
    
    call :make_request "POST" "/locations" "{\"name\":\"Schublade 1\",\"description\":\"Erste Schublade\",\"freezerSection\":\"DRAWER_1\",\"sortOrder\":5}"
    echo   - Created location: Schublade 1
    
    call :make_request "POST" "/locations" "{\"name\":\"Schublade 2\",\"description\":\"Zweite Schublade\",\"freezerSection\":\"DRAWER_2\",\"sortOrder\":6}"
    echo   - Created location: Schublade 2
    
    call :make_request "POST" "/locations" "{\"name\":\"Turfach\",\"description\":\"Gefriertur\",\"freezerSection\":\"DOOR\",\"sortOrder\":7}"
    echo   - Created location: Turfach
    goto :eof

REM Create sample products
:create_sample_products
    set "count=%~1"
    if "%count%"=="" set "count=50"
    
    echo.
    echo Creating %count% sample products...
    
    set "products[0]=Hahnchenbrust"
    set "products[1]=Rindersteak"
    set "products[2]=Lachsfilet"
    set "products[3]=Tiefkuhlpizza"
    set "products[4]=Brokkoli"
    set "products[5]=Erbsen"
    set "products[6]=Heidelbeeren"
    set "products[7]=Vollkornbrot"
    set "products[8]=Butter"
    set "products[9]=Milch"
    set "products[10]=Joghurt"
    set "products[11]=Kase"
    set "products[12]=Eiscreme"
    set "products[13]=Pommes Frites"
    set "products[14]=Fischstabchen"
    
    set "units[0]=kg"
    set "units[1]=g"
    set "units[2]=stuck"
    set "units[3]=packung"
    set "units[4]=portion"
    
    for /l %%i in (1,1,%count%) do (
        set /a "prod_idx=!random! %% 15"
        set /a "unit_idx=!random! %% 5"
        set /a "cat_id=(!random! %% 8) + 1"
        set /a "loc_id=(!random! %% 7) + 1"
        set /a "quantity=!random! %% 500 + 100"
        
        call set "product_name=%%products[!prod_idx!]%%"
        call set "unit=%%units[!unit_idx!]%%"
        
        REM Get current date
        for /f "tokens=1-3 delims=/" %%a in ("%date%") do (
            set "today=%%c-%%a-%%b"
        )
        
        REM Calculate future date (simplified)
        set /a "future_days=!random! %% 300 + 30"
        set "expiration_date=%today%"
        
        set "product_data={\"name\":\"!product_name! %%i\",\"quantity\":!quantity!,\"unit\":\"!unit!\",\"frozenDate\":\"%today%\",\"expirationDate\":\"%expiration_date%\",\"categoryId\":!cat_id!,\"locationId\":!loc_id!,\"notes\":null}"
        
        call :make_request "POST" "/products" "!product_data!"
        echo   - Created product: !product_name! %%i
    )
    goto :eof

REM Show database status
:show_status
    echo.
    echo Database Status
    echo ==================================================
    
    for /f %%i in ('call :make_request "GET" "/products?size=1000" ^| find /c "\"id\""') do set "product_count=%%i"
    for /f %%i in ('call :make_request "GET" "/categories" ^| find /c "\"id\""') do set "category_count=%%i"
    for /f %%i in ('call :make_request "GET" "/locations" ^| find /c "\"id\""') do set "location_count=%%i"
    
    echo Products: %product_count%
    echo Categories: %category_count%
    echo Locations: %location_count%
    goto :eof

REM Clean database
:clean_database
    echo.
    echo Cleaning database...
    
    REM Note: This is a simplified version
    REM In a real scenario, you'd need to parse JSON and delete individual items
    echo Warning: Clean operation not fully implemented in batch version
    echo Please use the shell script version for full functionality
    goto :eof

REM Seed database
:seed_database
    set "product_count=%~1"
    if "%product_count%"=="" set "product_count=50"
    
    echo.
    echo Seeding database with sample data...
    echo ==================================================
    
    call :check_backend
    if %errorlevel% neq 0 exit /b 1
    
    call :create_categories
    call :create_locations
    call :create_sample_products %product_count%
    
    echo.
    echo Database seeded successfully!
    call :show_status
    goto :eof

REM Main logic
echo Gefrierschrank Database Manager (Windows)
echo ========================================

if "%1"=="seed" (
    call :seed_database %2
) else if "%1"=="clean" (
    set /p "confirm=This will delete ALL data. Are you sure? (y/N): "
    if /i "!confirm!"=="y" (
        call :check_backend
        call :clean_database
    ) else (
        echo Operation cancelled
    )
) else if "%1"=="status" (
    call :check_backend
    call :show_status
) else (
    echo Usage:
    echo   %0 seed [count]  - Generate sample data (default: 50 products^)
    echo   %0 clean         - Clean/reset database
    echo   %0 status        - Show database status
    echo.
    echo Examples:
    echo   %0 seed          - Create 50 sample products
    echo   %0 seed 100      - Create 100 sample products
    echo   %0 status        - Show current database state
    echo.
    echo Note: For full functionality, use the Linux/Mac shell script version
)

endlocal