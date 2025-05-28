# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Structure

This is a **monorepo** with separate backend and frontend:
- `backend/` - Spring Boot application
- `frontend/` - Modern web frontend (to be implemented)
- `docs/` - Documentation (FEATURES.md, workflows, etc.)

## Development Commands

### Backend (Spring Boot)
```bash
cd backend
./mvnw clean install        # Build the project
./mvnw spring-boot:run       # Run application locally
./mvnw test                  # Run all tests
./mvnw test -Dtest=ClassName # Run specific test class
./mvnw compile               # Compile only
./mvnw clean                 # Clean build artifacts
```

### Frontend (when implemented)
```bash
cd frontend
npm install                  # Install dependencies
npm start                    # Development server
npm test                     # Run tests
npm run build               # Production build
```

### Git Workflow Commands

#### Feature Development
```bash
# Start new feature
git checkout develop
git pull origin develop
git checkout -b feature/inventory-management

# Create Pull Request when ready
gh pr create --base develop --title "Feature: Inventory Management"

# After PR merge
git checkout develop
git pull origin develop
git branch -d feature/inventory-management
```

#### Release Management
```bash
# Create Beta Release
git checkout develop
git tag v1.0.0-beta.1
git push origin v1.0.0-beta.1

# Create Stable Release
git checkout main
git merge develop
git tag v1.0.0
git push origin v1.0.0

# Emergency Hotfix
gh workflow run hotfix.yml -f base_version=v1.0.0 -f hotfix_description="Fix critical bug"
```

#### Project Management
```bash
# Check feature status
gh issue list --label "v1.0" --state open

# View releases
gh release list

# Monitor workflows
gh run list
```

## Architecture

This is a Spring Boot application (version 4.0.0-SNAPSHOT) with the following key characteristics:

- **Java 23** - Target Java version
- **Package Structure**: `de.hirthe.gefrierschrankapp` - German naming suggests this is a freezer/refrigerator management app
- **Spring Framework Stack**:
  - Spring Boot Web (REST APIs)
  - Spring Data JPA (Database access)
  - Spring Security (Authentication/Authorization)
  - Spring Boot Actuator (Monitoring endpoints)
- **Development Tools**:
  - Spring Boot DevTools (Hot reload during development)
  - Lombok (Code generation for getters/setters/etc.)
- **Testing**: JUnit 5 with Spring Boot Test and Spring Security Test

The application follows standard Spring Boot project structure with main application class at `GefrierschrankAppApplication.java`. Currently minimal with basic Spring Boot starter configuration.

## Git Flow & Release Process

This project follows a **modified Git Flow** with automated CI/CD:

### Branch Strategy
- **`main`** - Production-ready code, always stable
- **`develop`** - Integration branch for features
- **`feature/*`** - New feature development
- **`release/*`** - Release preparation and beta testing
- **`hotfix/*`** - Critical production bug fixes

### Automated Workflows
- **CI Pipeline** (`.github/workflows/ci.yml`) - Tests on push/PR
- **Release Workflow** (`.github/workflows/release.yml`) - Automatic releases from tags
- **Hotfix Workflow** (`.github/workflows/hotfix.yml`) - Emergency patches

### Documentation
Refer to these documents for detailed workflows:
- `GIT_WORKFLOW.md` - Complete Git Flow documentation
- `RELEASE_PROCESS.md` - Step-by-step release guide
- `BRANCH_CONCEPT.md` - Visual branching guide
- `BRANCHING_STRATEGY.md` - Branch protection and rules
- `FEATURES.md` - Feature roadmap and status tracking

## Testing Strategy

### Backend Testing
```bash
cd backend
./mvnw test                    # Unit tests
./mvnw test -Dtest="*Integration*" # Integration tests
./mvnw verify                  # All tests + quality checks
```

### Quality Gates
- All tests must pass before merge
- Code coverage minimum: 80%
- Security scans in CI pipeline
- Manual QA for UI changes

## Deployment

### Docker Deployment
```bash
# Build images
docker build -t gefrierschrank-app-backend ./backend
docker build -t gefrierschrank-app-frontend ./frontend

# Run with docker-compose
docker-compose up -d
```

### Kubernetes (k3s) Deployment
```bash
# Apply manifests
kubectl apply -f k8s/

# Update deployment
kubectl set image deployment/gefrierschrank-app app=gefrierschrank-app:v1.0.0
```

Note: The project uses Spring Boot 4.0.0-SNAPSHOT which is a development version requiring snapshot repositories.