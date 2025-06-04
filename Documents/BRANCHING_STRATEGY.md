# Git Branching Strategy & Release Management

## Branch Structure

### Main Branches
- **`main`** - Production-ready code, always stable
- **`develop`** - Integration branch for features, pre-release testing

### Supporting Branches
- **`feature/*`** - New feature development
- **`release/*`** - Release preparation and stabilization  
- **`hotfix/*`** - Critical production bug fixes

## Feature Development Workflow

### 1. Feature Branches
```bash
# Create feature branch from develop
git checkout develop
git pull origin develop
git checkout -b feature/inventory-management

# Develop feature
# Commit changes
# Push feature branch

# Create Pull Request to develop
gh pr create --base develop --title "Feature: Inventory Management"
```

**Naming Convention:** `feature/short-description`
- `feature/add-product-form`
- `feature/expiration-warnings`
- `feature/responsive-design`

### 2. Feature Integration
- All features must be reviewed via Pull Request
- CI tests must pass before merge
- Squash merge to develop for clean history
- Delete feature branch after merge

## Release Management

### 1. Beta Releases (Pre-release)
```bash
# Create release branch from develop
git checkout develop
git checkout -b release/v1.0.0-beta.1

# Final testing and bug fixes
# Tag beta release
git tag v1.0.0-beta.1
git push origin v1.0.0-beta.1
```

**Beta Release Characteristics:**
- Marked as "Pre-release" in GitHub
- Beta-specific release notes
- Customer testing and feedback
- Tag format: `v1.0.0-beta.1`, `v1.0.0-rc.1`

### 2. Stable Releases
```bash
# When beta is stable, merge to main
git checkout main
git merge release/v1.0.0-beta.1

# Tag stable release
git tag v1.0.0
git push origin v1.0.0

# Merge back to develop
git checkout develop
git merge main
```

**Stable Release Characteristics:**
- Marked as "Latest release"
- Production-ready
- Full release notes
- Tag format: `v1.0.0`, `v1.1.0`, `v2.0.0`

## Hotfix Process

### 1. Critical Bug in Production
```bash
# Use GitHub workflow to create hotfix branch
gh workflow run hotfix.yml -f base_version=v1.0.0 -f hotfix_description="Fix critical login bug"

# Or manually:
git checkout v1.0.0
git checkout -b hotfix/v1.0.1
```

### 2. Apply Hotfix
```bash
# Fix the bug in hotfix branch
# Test thoroughly
# Tag patch release
git tag v1.0.1
git push origin v1.0.1
```

### 3. Merge Hotfix
```bash
# Merge to main
git checkout main
git merge hotfix/v1.0.1

# Merge to develop (if applicable)
git checkout develop
git merge hotfix/v1.0.1
```

## Automated Workflows

### CI/CD Pipeline (`.github/workflows/ci.yml`)
**Triggers:**
- Push to `main` or `develop`
- Pull Requests to `main`

**Actions:**
- Run tests (backend & frontend)
- Build applications
- Docker image creation (main only)

### Release Workflow (`.github/workflows/release.yml`)
**Triggers:**
- Git tags matching `v*`

**Actions:**
- Build release artifacts
- Generate release notes from `FEATURES.md`
- Create GitHub release (stable or pre-release)
- Attach JAR files and documentation

### Hotfix Workflow (`.github/workflows/hotfix.yml`)
**Triggers:**
- Push to `release/*` or `hotfix/*` branches
- Manual workflow dispatch

**Actions:**
- Run comprehensive tests
- Security scanning
- Validate hotfix readiness

## Version Numbering

### Semantic Versioning (SemVer)
- **Major** (`v2.0.0`) - Breaking changes, major new features
- **Minor** (`v1.1.0`) - New features, backward compatible
- **Patch** (`v1.0.1`) - Bug fixes, backward compatible

### Pre-release Versions
- **Beta** (`v1.0.0-beta.1`) - Feature complete, testing phase
- **Release Candidate** (`v1.0.0-rc.1`) - Potentially final, last testing
- **Alpha** (`v1.0.0-alpha.1`) - Early development, unstable

## Branch Protection Rules

### Recommended GitHub Settings

#### Main Branch Protection
- ✅ Require pull request reviews before merging
- ✅ Require status checks to pass before merging
- ✅ Require branches to be up to date before merging
- ✅ Restrict pushes to matching branches
- ✅ Require conversation resolution before merging

#### Develop Branch Protection  
- ✅ Require pull request reviews before merging
- ✅ Require status checks to pass before merging
- ✅ Allow force pushes (for rebasing)

## Release Communication

### Beta Release Notes Template
```markdown
## 🧪 Beta Release - v1.0.0-beta.1

⚠️ **This is a beta release for testing purposes.**

### New Features:
- Feature A
- Feature B

### Testing Guidelines:
- Test in non-production environment
- Report bugs with detailed reproduction steps
```

### Stable Release Notes Template
```markdown
## 🚀 Stable Release - v1.0.0

### Features:
- Feature A (complete)
- Feature B (complete)

### Bug Fixes:
- Fixed issue X
- Resolved problem Y

### Upgrade Notes:
- Breaking changes (if any)
- Migration steps
```

## Development Commands

```bash
# Setup
git clone https://github.com/maikhir/gefrierschrank-app.git
cd gefrierschrank-app

# Start new feature
git checkout develop
git pull origin develop
git checkout -b feature/my-new-feature

# Create PR when ready
gh pr create --base develop --title "Feature: My New Feature"

# Create beta release
git checkout develop
git tag v1.0.0-beta.1
git push origin v1.0.0-beta.1

# Create stable release  
git checkout main
git merge develop
git tag v1.0.0
git push origin v1.0.0
```