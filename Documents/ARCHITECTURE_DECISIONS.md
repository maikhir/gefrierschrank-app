# Architecture Decision Records (ADRs)

## ADR-001: Monolith vs. Microservices Architecture

**Status:** ✅ Accepted  
**Date:** 2025-05-28  
**Related Features:** All features in [FEATURES.md](FEATURES.md)

### Context
We need to decide on the overall system architecture for the Gefrierschrank App.

### Decision
We choose a **Monolithic Architecture** over Microservices.

### Rationale
- **Small Domain Scope:** Inventory management is a cohesive, bounded domain
- **Team Size:** Single developer/small team - no organizational benefits from microservices
- **Complexity:** Avoid distributed system complexity (network calls, service discovery, data consistency)
- **Development Speed:** Faster development and debugging in single codebase
- **Deployment Simplicity:** Single JAR deployment to k3s

### Consequences
- ✅ Faster development and iteration
- ✅ Simpler debugging and monitoring
- ✅ Easier deployment and operations
- ⚠️ Future migration to microservices possible if needed
- ❌ All components must use same technology stack

### Implementation
- Single Spring Boot application
- Modular package structure with clear boundaries
- Domain-driven design within monolith

---

## ADR-002: Frontend Framework Selection

**Status:** ✅ Accepted  
**Date:** 2025-05-28  
**Related Features:** [Responsive Design](FEATURES.md#responsive-design-desktoptabletmobile-ui)

### Context
We need to choose a frontend framework for the responsive web application.

### Decision
We choose **Vue.js 3 with JavaScript** (no TypeScript).

### Rationale
- **Learning Curve:** Vue has gentler learning curve than React/Angular
- **Development Speed:** Vue's template syntax and composition API enable rapid development
- **JavaScript Choice:** Team preference to avoid TypeScript complexity for this project size
- **Ecosystem:** Mature ecosystem with Vite, Pinia, Vue Router
- **Mobile Support:** Excellent mobile responsiveness capabilities

### Alternatives Considered
- **React:** More complex, steeper learning curve
- **Angular:** Too enterprise-heavy for this project
- **Svelte:** Smaller ecosystem, less proven

### Consequences
- ✅ Rapid UI development
- ✅ Good mobile responsiveness
- ✅ Strong Vue ecosystem
- ❌ No TypeScript safety net
- ❌ Smaller job market than React

### Implementation
```
Tech Stack:
├── Vue 3 (Composition API)
├── Vite (Build tool)
├── Tailwind CSS (Styling)
├── Pinia (State management)
└── Vue Router 4 (Routing)
```

---

## ADR-003: Database Technology Choice

**Status:** ✅ Accepted  
**Date:** 2025-05-28  
**Related Features:** All data-dependent features

### Context
We need to select the database technology for development, testing, and production.

### Decision
- **Production:** PostgreSQL
- **Development/Testing:** H2 in-memory database

### Rationale
**PostgreSQL Advantages:**
- **JSON Support:** Flexible data structures for user preferences and metadata
- **ACID Compliance:** Data consistency and reliability
- **Performance:** Excellent query performance and optimization
- **k3s Integration:** Well-supported in Kubernetes environments
- **Ecosystem:** Rich tooling and extension ecosystem

**H2 Advantages:**
- **Zero Configuration:** Perfect for development and testing
- **Fast Startup:** In-memory database for quick test cycles
- **JPA Compatible:** Same entity models work in both environments

### Alternatives Considered
- **MySQL/MariaDB:** Less JSON support, fewer advanced features
- **SQLite:** Not suitable for concurrent access in production

### Consequences
- ✅ Flexible JSON data structures
- ✅ High performance and reliability
- ✅ Great development experience with H2
- ✅ Production-ready with PostgreSQL
- ⚠️ Need to maintain compatibility between H2 and PostgreSQL

### Implementation
```sql
Production: PostgreSQL with connection pooling
Development: H2 in-memory database
Migration: Flyway/Liquibase for schema management
```

---

## ADR-004: Authentication and Security Strategy

**Status:** ✅ Accepted  
**Date:** 2025-05-28  
**Related Features:** [Multi-User Support](FEATURES.md#multi-user-support-mehrere-benutzer-optional)

### Context
We need to implement secure authentication and authorization for the application.

### Decision
**JWT Token-based Authentication** with **Role-Based Access Control (RBAC)**.

### Rationale
- **Stateless:** JWT tokens enable stateless authentication, perfect for SPA
- **Scalability:** No server-side session storage required
- **API-Friendly:** Standard approach for REST APIs
- **Frontend Integration:** Easy integration with Vue.js applications
- **Future-Proof:** Works well with microservices if we migrate later

### Security Implementation
```
Authentication Flow:
1. User login → JWT token generation
2. Token stored in localStorage (frontend)
3. Token sent in Authorization header
4. Backend validates JWT on each request

Authorization Levels:
├── ADMIN: Full system access
├── USER: Own data management
└── GUEST: Read-only access (future)
```

### Alternatives Considered
- **Session-based:** Stateful, harder to scale
- **OAuth2/OIDC:** Too complex for initial implementation

### Consequences
- ✅ Stateless and scalable
- ✅ Great SPA integration
- ✅ Industry standard approach
- ⚠️ Token management complexity
- ⚠️ Security considerations (token storage, expiration)

### Implementation
- Spring Security with JWT
- BCrypt password hashing
- Role-based method security
- CORS configuration for SPA

---

## ADR-005: API Design Pattern

**Status:** ✅ Accepted  
**Date:** 2025-05-28  
**Related Features:** All features requiring data access

### Context
We need to define the API architecture and communication pattern between frontend and backend.

### Decision
**RESTful API** with **JSON** payload format.

### Rationale
- **Simplicity:** REST is well-understood and documented
- **Spring Boot Integration:** Excellent REST support out-of-the-box
- **Tooling:** Great tooling and debugging support
- **Caching:** HTTP caching strategies work well with REST
- **Standards:** Industry standard for web APIs

### API Structure
```
Base URL: /api/v1/

Resources:
├── /products      - Product management
├── /categories    - Category management  
├── /locations     - Location management
├── /notifications - Notification system
├── /auth          - Authentication
└── /admin         - Admin functions
```

### Response Format
```json
{
  "success": true,
  "data": { ... },
  "meta": {
    "timestamp": "2025-05-28T18:30:00Z",
    "version": "1.0"
  }
}
```

### Alternatives Considered
- **GraphQL:** Too complex for simple CRUD operations
- **RPC:** Less cacheable, harder to debug

### Consequences
- ✅ Simple and well-understood
- ✅ Great tooling support
- ✅ HTTP caching benefits
- ✅ Easy to document and test
- ❌ Multiple requests for complex data

### Implementation
- Spring Boot REST Controllers
- JSON serialization with Jackson
- HTTP status codes for error handling
- API versioning through URL path

---

## ADR-006: State Management Strategy

**Status:** ✅ Accepted  
**Date:** 2025-05-28  
**Related Features:** All frontend features

### Context
We need to manage application state in the Vue.js frontend effectively.

### Decision
**Pinia** for global state management with **Composition API**.

### Rationale
- **Vue 3 Native:** Official state management for Vue 3
- **Composition API:** Perfect integration with Vue 3 Composition API
- **TypeScript Support:** Good TS support (even though we use JS)
- **DevTools:** Excellent Vue DevTools integration
- **Modularity:** Easy to organize stores by feature

### Store Structure
```javascript
stores/
├── auth.js         - Authentication state
├── products.js     - Product inventory
├── categories.js   - Category management
├── notifications.js - Notification system
└── ui.js          - UI state (modals, loading)
```

### Alternatives Considered
- **Vuex 4:** Deprecated in favor of Pinia
- **Component State:** Not sufficient for complex app state

### Consequences
- ✅ Modern Vue 3 state management
- ✅ Great developer experience
- ✅ Modular and scalable
- ✅ Excellent debugging tools
- ⚠️ Learning curve for team members

### Implementation
- Pinia stores with Composition API
- Reactive state management
- Actions for async operations
- Getters for computed state

---

## ADR-007: Styling and UI Framework

**Status:** ✅ Accepted  
**Date:** 2025-05-28  
**Related Features:** [Responsive Design](FEATURES.md#responsive-design-desktoptabletmobile-ui)

### Context
We need to choose a styling approach and UI component strategy.

### Decision
**Tailwind CSS** for styling with **Headless UI Vue** for accessible components.

### Rationale
- **Utility-First:** Rapid UI development with utility classes
- **Responsive Design:** Built-in responsive design utilities
- **Customization:** Easy to customize design system
- **Bundle Size:** Purging removes unused CSS
- **Accessibility:** Headless UI provides accessible components

### UI Component Strategy
```
Styling Stack:
├── Tailwind CSS - Utility-first styling
├── Headless UI Vue - Accessible components
├── Custom Components - App-specific components
└── Icons - Heroicons or similar
```

### Alternatives Considered
- **Bootstrap:** Less flexible, larger bundle
- **Vuetify:** Material Design, less customizable
- **Custom CSS:** More work, less consistency

### Consequences
- ✅ Rapid UI development
- ✅ Consistent design system
- ✅ Great responsive design
- ✅ Accessibility out-of-the-box
- ⚠️ Learning curve for utility-first CSS
- ⚠️ HTML can become verbose

### Implementation
- Tailwind CSS configuration
- Custom design tokens
- Component library with Headless UI
- Responsive breakpoint strategy

---

## ADR-008: Build and Development Tools

**Status:** ✅ Accepted  
**Date:** 2025-05-28  
**Related Features:** Development workflow

### Context
We need to choose build tools and development environment setup.

### Decision
**Vite** for frontend build tool, **Maven** for backend.

### Rationale
**Vite Advantages:**
- **Fast HMR:** Instant hot module replacement
- **ES Modules:** Native ES module support
- **Vue Integration:** Perfect Vue 3 integration
- **Build Speed:** Fast production builds

**Maven Advantages:**
- **Spring Boot Standard:** Default build tool for Spring Boot
- **Dependency Management:** Mature dependency resolution
- **Plugin Ecosystem:** Rich plugin ecosystem
- **IDE Integration:** Excellent IDE support

### Development Tools
```
Frontend:
├── Vite - Build tool and dev server
├── ESLint - Code linting
├── Prettier - Code formatting
└── Vue DevTools - Debugging

Backend:
├── Maven - Build and dependency management
├── Spring Boot DevTools - Hot reload
├── H2 Console - Database debugging
└── Actuator - Application monitoring
```

### Consequences
- ✅ Fast development feedback loop
- ✅ Modern build tooling
- ✅ Great developer experience
- ✅ Industry standard tools
- ⚠️ Different build systems for frontend/backend

### Implementation
- Vite configuration for Vue + Tailwind
- Maven configuration with Spring Boot
- Development script automation
- Hot reload in both frontend and backend

---

## ADR-009: Testing Strategy

**Status:** ✅ Accepted  
**Date:** 2025-05-28  
**Related Features:** All features require testing

### Context
We need to define our testing approach for both frontend and backend.

### Decision
**Unit and Integration Testing** with framework-native tools.

### Testing Stack
```
Backend Testing:
├── JUnit 5 - Unit testing framework
├── Spring Boot Test - Integration testing
├── Testcontainers - Database testing
├── MockMvc - API testing
└── H2 - Test database

Frontend Testing:
├── Vitest - Unit testing (Vite-native)
├── Vue Test Utils - Component testing
├── Cypress/Playwright - E2E testing (future)
└── MSW - API mocking
```

### Testing Strategy
- **Unit Tests:** Core business logic
- **Integration Tests:** Database and API layers
- **Component Tests:** Vue component behavior
- **E2E Tests:** Critical user workflows (future)

### Consequences
- ✅ Comprehensive testing coverage
- ✅ Fast test execution
- ✅ Framework-native tools
- ✅ CI/CD integration
- ⚠️ Initial setup overhead

### Implementation
- Test-driven development approach
- CI pipeline with automated testing
- Code coverage reporting
- Test data management strategy

---

## ADR-010: Deployment and Infrastructure

**Status:** ✅ Accepted  
**Date:** 2025-05-28  
**Related Features:** Production deployment

### Context
We need to define the deployment strategy for the application.

### Decision
**Docker containers** deployed to **k3s Kubernetes cluster**.

### Rationale
- **Containerization:** Consistent deployment across environments
- **k3s:** Lightweight Kubernetes for edge/IoT deployments
- **Scalability:** Easy horizontal scaling with Kubernetes
- **DevOps:** Modern cloud-native deployment approach

### Deployment Architecture
```
k3s Cluster:
├── Ingress Controller - External access
├── Frontend Pods - Nginx + Vue.js SPA
├── Backend Pods - Spring Boot (2+ replicas)
├── PostgreSQL Pod - Database with persistent volume
└── Monitoring - Prometheus/Grafana (future)
```

### Container Strategy
- **Multi-stage builds** for optimized images
- **Distroless base images** for security
- **Health checks** for container orchestration
- **Resource limits** for predictable performance

### Consequences
- ✅ Modern cloud-native deployment
- ✅ Scalable and resilient architecture
- ✅ Container portability
- ✅ Infrastructure as code
- ⚠️ Kubernetes learning curve
- ⚠️ Operational complexity

### Implementation
- Docker multi-stage builds
- Kubernetes manifests
- Helm charts for deployment
- CI/CD pipeline integration

---

## Feature-Architecture Mapping

### Core Features (v1.0) → Architecture Decisions

| Feature | Related ADRs | Implementation Notes |
|---------|-------------|---------------------|
| [Basis-Inventar Management](FEATURES.md#basis-inventar-management) | ADR-001, ADR-003, ADR-005 | Monolith + PostgreSQL + REST API |
| [Kategorisierung](FEATURES.md#kategorisierung--organisation) | ADR-003, ADR-005 | Database categories table + REST endpoints |
| [Datums-Management](FEATURES.md#datums-management) | ADR-003 | PostgreSQL date handling + business logic |
| [Responsive Design](FEATURES.md#responsive-design-desktoptabletmobile-ui) | ADR-002, ADR-007 | Vue.js + Tailwind CSS responsive utilities |

### Advanced Features (v1.1+) → Architecture Decisions

| Feature | Related ADRs | Implementation Notes |
|---------|-------------|---------------------|
| [Benachrichtigungen](FEATURES.md#benachrichtigungen--warnungen) | ADR-001, ADR-004, ADR-005 | Backend scheduling + Web Push API |
| [Daten-Management](FEATURES.md#daten-management) | ADR-003, ADR-005 | PostgreSQL backup + REST export/import |

### Optional Features (v2.0+) → Architecture Decisions

| Feature | Related ADRs | Implementation Notes |
|---------|-------------|---------------------|
| [Multi-User Support](FEATURES.md#multi-user-support-mehrere-benutzer-optional) | ADR-004 | JWT authentication + RBAC |
| [Produkt-Fotos](FEATURES.md#produkt-fotos-bilder-hinzufügen-optional) | ADR-003, ADR-010 | PostgreSQL blob storage + k3s persistent volumes |

---

## Decision Update Process

1. **Propose:** Create new ADR document
2. **Discuss:** Team review and feedback
3. **Decide:** Accept, reject, or modify
4. **Document:** Update this file and related documentation
5. **Implement:** Update code and configuration
6. **Review:** Periodic review of architectural decisions

---

## Architecture Evolution

These decisions create a solid foundation that can evolve:

- **v1.0:** Monolith with clear domain boundaries
- **v1.5:** Extract notification service (if needed)
- **v2.0:** Microservices migration (if scale requires)
- **v3.0:** Event-driven architecture (for complex workflows)

Each decision includes rationale and consequences to enable future architectural evolution based on actual requirements and constraints.