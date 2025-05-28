# Technical Architecture - Gefrierschrank App

## System Overview

```
┌─────────────────────────────────────┐
│     Vue.js Frontend (SPA)          │
│   JavaScript + Vite + Tailwind     │
└─────────────┬───────────────────────┘
              │ HTTP/REST API + JWT Auth
┌─────────────▼───────────────────────┐
│       Spring Boot Backend          │
│     Java 23 + JPA + Security       │
└─────────────┬───────────────────────┘
              │ JDBC + Connection Pool
┌─────────────▼───────────────────────┐
│      PostgreSQL Database           │
│    (H2 for Development/Testing)     │
└─────────────────────────────────────┘
```

## Frontend Stack

### Core Technologies
- **Vue 3** - Progressive JavaScript Framework
- **JavaScript** - Main programming language (no TypeScript)
- **Vite** - Build tool and development server
- **Vue Router 4** - Client-side routing
- **Pinia** - State management
- **Tailwind CSS** - Utility-first CSS framework

### UI Components & Libraries
- **Headless UI Vue** - Accessible, unstyled UI components
- **VueUse** - Collection of Vue composition utilities
- **Axios** - HTTP client for API communication
- **Vue3-toastify** - Toast notifications

### Project Structure
```
frontend/
├── src/
│   ├── components/
│   │   ├── ui/              # Base UI components (Button, Input, Modal)
│   │   ├── layout/          # Layout components (Header, Sidebar, Footer)
│   │   └── features/        # Feature-specific components
│   │       ├── inventory/   # Product management components
│   │       ├── categories/  # Category management
│   │       └── notifications/ # Notification components
│   ├── views/               # Page components (ProductList, Dashboard)
│   ├── stores/              # Pinia stores (auth, products, categories)
│   ├── composables/         # Reusable composition functions
│   ├── services/            # API service layer
│   ├── router/              # Vue Router configuration
│   ├── utils/               # Utility functions
│   └── assets/              # Static assets (images, icons)
├── public/                  # Public static files
├── index.html               # HTML entry point
├── vite.config.js          # Vite configuration
├── tailwind.config.js      # Tailwind CSS configuration
└── package.json            # Dependencies and scripts
```

## Backend Stack

### Core Technologies
- **Spring Boot 4.0.0-SNAPSHOT** - Main framework
- **Java 23** - Programming language
- **Spring Web** - REST API development
- **Spring Data JPA** - Data access layer
- **Spring Security** - Authentication and authorization
- **Spring Boot Actuator** - Monitoring and management

### Additional Dependencies
- **H2 Database** - In-memory database for development/testing
- **PostgreSQL Driver** - Production database connectivity
- **Lombok** - Code generation (getters, setters, constructors)
- **Bean Validation** - Input validation
- **Jackson** - JSON serialization/deserialization

### Architecture Layers
```
┌─────────────────────────────────┐
│        REST Controllers         │ ← HTTP endpoints, request/response handling
├─────────────────────────────────┤
│        Service Layer           │ ← Business logic, transaction management
├─────────────────────────────────┤
│       Repository Layer         │ ← Data access, JPA repositories
├─────────────────────────────────┤
│         Entity Layer           │ ← JPA entities, domain models
└─────────────────────────────────┘
```

## Database Design

### Production Database: PostgreSQL
- **Robust ACID compliance**
- **JSON/JSONB support** for flexible data structures
- **Excellent performance** and scalability
- **Rich ecosystem** of tools and extensions
- **Great k3s/Docker integration**

### Development Database: H2
- **In-memory database** for fast testing
- **Zero configuration** setup
- **Compatible with JPA/Hibernate**
- **Automatic schema generation**

### Core Database Schema
```sql
-- Categories for organizing products
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    color VARCHAR(7) DEFAULT '#3B82F6',
    default_storage_days INTEGER DEFAULT 90,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Physical locations in the freezer
CREATE TABLE locations (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    freezer_section VARCHAR(50), -- 'upper', 'middle', 'lower', 'drawer1', etc.
    sort_order INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Users and households
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    household_id UUID DEFAULT gen_random_uuid(),
    role VARCHAR(20) DEFAULT 'USER', -- 'ADMIN', 'USER', 'GUEST'
    preferences JSONB DEFAULT '{}',
    email_verified BOOLEAN DEFAULT FALSE,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Main product inventory
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    category_id INTEGER REFERENCES categories(id) ON DELETE SET NULL,
    location_id INTEGER REFERENCES locations(id) ON DELETE SET NULL,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    
    -- Quantity and measurements
    quantity DECIMAL(10,2) NOT NULL DEFAULT 1,
    unit VARCHAR(20) DEFAULT 'pieces', -- 'kg', 'g', 'pieces', 'liters', 'ml'
    
    -- Date management
    frozen_date DATE NOT NULL DEFAULT CURRENT_DATE,
    expiration_date DATE,
    
    -- Additional information
    notes TEXT,
    image_url VARCHAR(500),
    barcode VARCHAR(50),
    
    -- Metadata
    metadata JSONB DEFAULT '{}', -- For flexible additional data
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Notification preferences and history
CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    product_id INTEGER REFERENCES products(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL, -- 'EXPIRING_SOON', 'EXPIRED', 'LOW_STOCK'
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    scheduled_for TIMESTAMP,
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Indexes for performance
CREATE INDEX idx_products_user_id ON products(user_id);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_expiration_date ON products(expiration_date);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_scheduled ON notifications(scheduled_for) WHERE sent_at IS NULL;
```

## Security Architecture

### Authentication Strategy: JWT Tokens
```
1. User Login Request
   Frontend ──→ POST /api/auth/login ──→ Backend
                                      ├─→ Validate credentials
                                      ├─→ Generate JWT token
                                      └─→ Return token + user info

2. Authenticated Requests
   Frontend ──→ GET /api/products ──→ Backend
   Headers:     Authorization: Bearer <JWT_TOKEN>
                                    ├─→ Validate JWT
                                    ├─→ Extract user info
                                    └─→ Process request
```

### Authorization: Role-Based Access Control (RBAC)
```javascript
const permissions = {
  ADMIN: [
    'manage_users',
    'manage_categories', 
    'manage_locations',
    'view_all_products',
    'manage_system_settings'
  ],
  USER: [
    'manage_own_products',
    'view_categories',
    'view_locations', 
    'manage_notifications',
    'export_own_data'
  ],
  GUEST: [
    'view_shared_products' // For future sharing features
  ]
}
```

### Security Implementation

**Backend Security Configuration:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) // Disabled for SPA
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll() // Dev only
                
                // User endpoints
                .requestMatchers(HttpMethod.GET, "/api/categories").hasRole("USER")
                .requestMatchers("/api/products/**").hasRole("USER")
                .requestMatchers("/api/notifications/**").hasRole("USER")
                
                // Admin endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/categories").hasRole("ADMIN")
                
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .build();
    }
}
```

**Frontend Security Integration:**
```javascript
// stores/auth.js - Pinia store for authentication
export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('auth_token'))
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  
  const isAuthenticated = computed(() => !!token.value && !isTokenExpired())
  const hasRole = (role) => user.value?.roles?.includes(role) || false
  const hasPermission = (permission) => {
    const userPermissions = user.value?.permissions || []
    return userPermissions.includes(permission)
  }
  
  const login = async (credentials) => {
    const { data } = await authAPI.login(credentials)
    
    token.value = data.token
    user.value = data.user
    
    localStorage.setItem('auth_token', data.token)
    localStorage.setItem('user', JSON.stringify(data.user))
    
    // Setup axios interceptor for future requests
    setupAuthInterceptor()
  }
  
  const logout = () => {
    token.value = null
    user.value = null
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user')
  }
  
  return {
    token, user, isAuthenticated, hasRole, hasPermission,
    login, logout
  }
})
```

### Input Validation & Security

**Backend Validation:**
```java
@Entity
@Table(name = "products")
public class Product {
    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must be less than 200 characters")
    private String name;
    
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    private BigDecimal quantity;
    
    @NotNull(message = "Frozen date is required")
    @PastOrPresent(message = "Frozen date cannot be in the future")
    private LocalDate frozenDate;
    
    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;
}

@RestController
@RequestMapping("/api/products")
@Validated
@PreAuthorize("hasRole('USER')")
public class ProductController {
    
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
        @Valid @RequestBody CreateProductRequest request,
        Authentication auth) {
        
        // Security: Ensure user can only create products for their household
        Product product = productService.createProduct(request, auth.getName());
        return ResponseEntity.ok(productMapper.toDTO(product));
    }
}
```

**Frontend Validation:**
```javascript
// composables/useFormValidation.js
export function useProductValidation() {
  const errors = ref({})
  
  const validateProduct = (product) => {
    errors.value = {}
    
    if (!product.name?.trim()) {
      errors.value.name = 'Product name is required'
    } else if (product.name.length > 200) {
      errors.value.name = 'Product name is too long'
    }
    
    if (!product.quantity || product.quantity <= 0) {
      errors.value.quantity = 'Quantity must be greater than 0'
    }
    
    if (!product.frozenDate) {
      errors.value.frozenDate = 'Frozen date is required'
    } else if (new Date(product.frozenDate) > new Date()) {
      errors.value.frozenDate = 'Frozen date cannot be in the future'
    }
    
    return Object.keys(errors.value).length === 0
  }
  
  return { errors, validateProduct }
}
```

## API Design

### RESTful API Structure
```
Authentication:
├── POST   /api/auth/login          # User login
├── POST   /api/auth/register       # User registration  
├── POST   /api/auth/refresh        # Token refresh
└── POST   /api/auth/logout         # User logout

Products:
├── GET    /api/products            # List user's products
├── POST   /api/products            # Create new product
├── GET    /api/products/{id}       # Get specific product
├── PUT    /api/products/{id}       # Update product
└── DELETE /api/products/{id}       # Delete product

Categories:
├── GET    /api/categories          # List all categories
├── POST   /api/categories          # Create category (ADMIN)
├── PUT    /api/categories/{id}     # Update category (ADMIN)
└── DELETE /api/categories/{id}     # Delete category (ADMIN)

Locations:
├── GET    /api/locations           # List all locations
├── POST   /api/locations           # Create location (ADMIN)
├── PUT    /api/locations/{id}      # Update location (ADMIN)
└── DELETE /api/locations/{id}      # Delete location (ADMIN)

Notifications:
├── GET    /api/notifications       # List user's notifications
├── PUT    /api/notifications/{id}/read  # Mark as read
└── DELETE /api/notifications/{id}  # Delete notification

Admin:
├── GET    /api/admin/users         # List all users
├── GET    /api/admin/stats         # System statistics
└── POST   /api/admin/users/{id}/role  # Change user role
```

### API Response Format
```javascript
// Success Response
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Frozen Pizza",
    "category": "Ready Meals",
    "quantity": 2,
    "unit": "pieces"
  },
  "meta": {
    "timestamp": "2025-05-28T18:30:00Z",
    "version": "1.0"
  }
}

// Error Response
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": {
      "name": ["Product name is required"],
      "quantity": ["Quantity must be greater than 0"]
    }
  },
  "meta": {
    "timestamp": "2025-05-28T18:30:00Z",
    "version": "1.0"
  }
}
```

## Deployment Architecture

### Docker Configuration
```dockerfile
# Backend Dockerfile
FROM eclipse-temurin:23-jre-alpine
COPY target/gefrierschrank-app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Frontend Dockerfile  
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
```

### Kubernetes (k3s) Deployment
```yaml
# Backend Deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: gefrierschrank-backend
spec:
  replicas: 2
  selector:
    matchLabels:
      app: gefrierschrank-backend
  template:
    spec:
      containers:
      - name: backend
        image: gefrierschrank-app-backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            secretKeyRef:
              name: database-secret
              key: url
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
```

## Development Workflow

### Getting Started
```bash
# Clone repository
git clone https://github.com/maikhir/gefrierschrank-app.git
cd gefrierschrank-app

# Backend setup
cd backend
./mvnw spring-boot:run

# Frontend setup (new terminal)
cd frontend
npm install
npm run dev

# Access application
# Frontend: http://localhost:5173
# Backend API: http://localhost:8080/api
# H2 Console: http://localhost:8080/h2-console
```

### Build Process
```bash
# Backend build
cd backend
./mvnw clean package

# Frontend build
cd frontend
npm run build

# Docker build
docker build -t gefrierschrank-app-backend ./backend
docker build -t gefrierschrank-app-frontend ./frontend
```

## Performance Considerations

### Backend Optimizations
- **Connection Pooling:** HikariCP for efficient database connections
- **JPA Optimization:** Lazy loading, query optimization, caching
- **API Pagination:** Large result sets with pagination
- **Async Processing:** Background tasks for notifications

### Frontend Optimizations
- **Code Splitting:** Route-based code splitting with Vue Router
- **Lazy Loading:** Components loaded on demand
- **Asset Optimization:** Image compression, tree shaking
- **Caching:** HTTP caching headers, service worker (future PWA)

### Database Optimizations
- **Indexing:** Strategic indexes on frequently queried columns
- **Query Optimization:** Efficient JPA queries, avoid N+1 problems
- **Connection Management:** Proper connection pooling
- **Backup Strategy:** Regular automated backups

## Future Enhancements

### Progressive Web App (PWA)
- **Service Worker:** Offline functionality
- **App Manifest:** Install as native app
- **Push Notifications:** Browser push notifications

### Advanced Features
- **Real-time Updates:** WebSocket for live updates
- **Image Recognition:** AI-powered product identification
- **Barcode Scanning:** Mobile barcode scanner integration
- **Analytics Dashboard:** Usage statistics and insights

### Scalability
- **Microservices Migration:** If needed in future
- **Event-Driven Architecture:** For complex workflows
- **CDN Integration:** For global asset delivery
- **Multi-tenant Architecture:** For SaaS offering

This architecture provides a solid foundation for building a modern, scalable, and maintainable freezer inventory management application.