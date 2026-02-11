# FastAPI for Production APIs Curriculum

**Target Audience**: Backend developers and data engineers  
**Duration**: 12 weeks  
**Time Commitment**: 10-12 hours per week  
**Prerequisites**: Python fundamentals, basic REST API knowledge

---

## PHASE 0: FastAPI Basics (Weeks 1-3)
*Goal: Build REST APIs with FastAPI*

### Week 1: FastAPI Fundamentals

**Concepts (2 hours)**
- Why FastAPI? (speed, type safety, async)
- ASGI vs WSGI
- Path operations (routes)
- Request and response models (Pydantic)
- Automatic API documentation (Swagger UI)
- Type hints and validation
- Dependency injection system

**Practice (4 hours)**
- Set up FastAPI project:
  - Install fastapi, uvicorn
  - Create first endpoint
  - Run server
- Build CRUD API:
  - GET /items (list all)
  - GET /items/{id} (get one)
  - POST /items (create)
  - PUT /items/{id} (update)
  - DELETE /items/{id} (delete)
- Define Pydantic models for validation
- Test endpoints in Swagger UI
- **Production scenario**: Build RESTful API for resource management

**Deliverable**: Working CRUD API with validation

**Key insight**: *Pydantic models = automatic validation + documentation*

---

### Week 2: Request Handling & Validation

**Concepts (3 hours)**
- Path parameters vs query parameters vs request body
- Data validation with Pydantic
- Custom validators
- Request headers and cookies
- File uploads
- Form data handling
- Response models and status codes
- Error handling and custom exceptions

**Practice (5 hours)**
- Implement complex request handling:
  - Pagination (limit, offset)
  - Filtering and sorting
  - Search functionality
- Add data validation:
  - Email validation
  - Date range validation
  - Custom business rules
- Handle file uploads:
  - Image upload
  - CSV file processing
- Implement proper error responses:
  - 400 Bad Request
  - 404 Not Found
  - 422 Validation Error
- **Production scenario**: Build API with complex filtering and validation

**Deliverable**: API with comprehensive request handling

**Key insight**: *Validate early, return clear errors - help API consumers*

---

### Week 3: Database Integration

**Concepts (3 hours)**
- SQLAlchemy with FastAPI
- Async database operations (databases library)
- Database session management
- Connection pooling
- Migrations with Alembic
- Repository pattern
- Dependency injection for database

**Practice (5 hours)**
- Integrate PostgreSQL:
  - SQLAlchemy models
  - Database connection setup
  - Session dependency
- Implement async CRUD operations:
  - Async queries
  - Transactions
- Set up Alembic:
  - Initial migration
  - Schema changes
- Implement repository pattern:
  - Separate data access layer
  - Business logic in services
- **Production scenario**: API with database persistence

**Deliverable**: FastAPI + PostgreSQL application

**Key insight**: *Dependency injection for database keeps code clean and testable*

---

## PHASE 1: Advanced FastAPI (Weeks 4-6)
*Goal: Authentication, security, and real-world patterns*

### Week 4: Authentication & Authorization

**Concepts (3 hours)**
- JWT tokens (JSON Web Tokens)
- OAuth2 with Password flow
- OAuth2 with Authorization Code (proper OAuth)
- Azure AD integration
- API keys
- Role-based access control (RBAC)
- Security best practices

**Practice (5 hours)**
- Implement JWT authentication:
  - Login endpoint (generate token)
  - Protected endpoints (verify token)
  - Token refresh
- Add OAuth2 integration:
  - Azure AD login
  - Token validation
- Implement RBAC:
  - User roles (admin, user, viewer)
  - Permission checks
  - Dependency injection for auth
- Secure sensitive endpoints
- **Production scenario**: Multi-tenant API with role-based access

**Deliverable**: Secured API with authentication + authorization

**Key insight**: *Never roll your own auth - use proven patterns and libraries*

---

### Week 5: Background Tasks & Async Processing

**Concepts (3 hours)**
- Background tasks in FastAPI
- Celery for heavy workloads
- Redis as message broker
- Task queues and workers
- Long-running operations
- WebSockets for real-time updates
- Server-Sent Events (SSE)

**Practice (5 hours)**
- Implement background tasks:
  - Send email after registration
  - Process uploaded file asynchronously
- Set up Celery:
  - Define tasks
  - Configure Redis broker
  - Run workers
- Build long-running operation:
  - Submit job, return task ID
  - Poll for status
  - Get results when complete
- Add WebSocket endpoint:
  - Real-time notifications
  - Progress updates
- **Production scenario**: API for data processing jobs

**Deliverable**: API with async job processing

**Key insight**: *Never block API requests - use background tasks for heavy work*

---

### Week 6: Testing & Documentation

**Concepts (3 hours)**
- Testing FastAPI with pytest
- TestClient for endpoint testing
- Mocking dependencies
- Test database setup/teardown
- Integration tests vs unit tests
- API documentation best practices
- OpenAPI schema customization
- Rate limiting and throttling

**Practice (5 hours)**
- Write comprehensive tests:
  - Unit tests for services
  - Integration tests for endpoints
  - Authentication tests
  - Database transaction tests
- Mock external dependencies:
  - Database
  - External APIs
  - File system
- Enhance API documentation:
  - Add descriptions
  - Example requests/responses
  - Tags and groups
- Implement rate limiting:
  - Per-user limits
  - IP-based throttling
- **Production scenario**: Fully tested API with documentation

**Deliverable**: Test suite with >80% coverage

**Key insight**: *TestClient makes API testing easy - no excuses for untested endpoints*

---

## PHASE 2: Production FastAPI (Weeks 7-9)
*Goal: Deploy and operate APIs in production*

### Week 7: Logging, Monitoring & Observability

**Concepts (3 hours)**
- Structured logging in FastAPI
- Request ID tracking (correlation)
- Application Insights integration
- Metrics and custom telemetry
- Health checks and readiness probes
- Middleware for logging
- Error tracking (Sentry)

**Practice (5 hours)**
- Implement structured logging:
  - JSON log format
  - Log all requests
  - Correlation IDs
- Add Application Insights:
  - Automatic request tracking
  - Custom events
  - Dependencies tracking
- Create health check endpoints:
  - /health (liveness)
  - /ready (readiness)
  - Database connection check
- Implement middleware:
  - Request timing
  - Error logging
  - Request/response logging
- **Production scenario**: Observable API with full telemetry

**Deliverable**: API with comprehensive monitoring

**Key insight**: *Logs and metrics are essential - you can't debug what you can't see*

---

### Week 8: Performance & Scalability

**Concepts (3 hours)**
- FastAPI performance tuning
- Database query optimization
- Caching strategies (Redis)
- Connection pooling
- Async vs sync endpoints
- Load testing
- Horizontal scaling
- API Gateway patterns

**Practice (5 hours)**
- Implement caching:
  - Redis for response caching
  - Cache invalidation strategies
  - Cache-aside pattern
- Optimize database queries:
  - N+1 query problem
  - Eager loading
  - Query profiling
- Load test API:
  - Use Locust or k6
  - Identify bottlenecks
  - Optimize hot paths
- Prepare for scaling:
  - Stateless design
  - Externalize sessions
- **Production scenario**: API handles 1000 req/sec

**Deliverable**: Performance-tested, optimized API

**Key insight**: *Caching and async are your friends for performance*

---

### Week 9: Deployment & DevOps

**Concepts (3 hours)**
- Containerizing FastAPI
- Docker best practices
- Azure Container Apps
- Azure App Service
- Environment configuration
- CI/CD pipelines
- Blue-green deployments
- Health checks in production

**Practice (5 hours)**
- Create production Dockerfile:
  - Multi-stage builds
  - Non-root user
  - Minimal image size
- Deploy to Azure:
  - Azure Container Apps
  - OR Azure App Service
- Set up CI/CD:
  - GitHub Actions OR Azure Pipelines
  - Run tests
  - Build image
  - Deploy automatically
- Configure production settings:
  - Environment variables
  - Secrets from Key Vault
  - Database connection strings
- **Production scenario**: Automated deployment pipeline

**Deliverable**: Deployed API with CI/CD

**Key insight**: *Automate everything - manual deployments cause errors*

---

## PHASE 3: Advanced Patterns (Weeks 10-12)
*Goal: Enterprise-grade API patterns*

### Week 10: Microservices & Service Communication

**Concepts (3 hours)**
- Microservices architecture
- Service-to-service authentication
- API Gateway (Azure API Management)
- Service mesh basics
- Circuit breakers
- Retry policies
- Event-driven patterns

**Practice (5 hours)**
- Build multiple FastAPI services:
  - User service
  - Order service
  - Payment service
- Implement service communication:
  - HTTP client with retries
  - Circuit breaker (aiobreaker)
  - Service discovery
- Add API Gateway:
  - Route to backend services
  - Rate limiting
  - Authentication
- **Production scenario**: Microservices architecture with FastAPI

**Deliverable**: Multi-service system

**Key insight**: *Distributed systems need resilience patterns - failures are normal*

---

### Week 11: Advanced Security

**Concepts (3 hours)**
- HTTPS and TLS
- CORS configuration
- CSRF protection
- SQL injection prevention
- Input sanitization
- Secrets management
- API versioning
- Audit logging

**Practice (5 hours)**
- Harden API security:
  - Proper CORS setup
  - Rate limiting per endpoint
  - Input validation (prevent injection)
  - Secrets in Key Vault
- Implement API versioning:
  - /v1/ prefix
  - Version in header
- Add audit logging:
  - Track all data modifications
  - User activity logging
- Security testing:
  - OWASP Top 10 checks
  - Penetration testing basics
- **Production scenario**: Security audit ready API

**Deliverable**: Hardened, secure API

**Key insight**: *Security is layers - defense in depth*

---

### Week 12: Capstone - Production API

**Build complete production-grade API:**

**Requirements:**
1. **Authentication** - JWT + Azure AD
2. **Database** - PostgreSQL with migrations
3. **Caching** - Redis for performance
4. **Background tasks** - Celery for async work
5. **Testing** - >80% coverage
6. **Logging** - Structured JSON logs
7. **Monitoring** - Application Insights
8. **Documentation** - Complete OpenAPI docs
9. **Deployment** - Containerized + CI/CD
10. **Security** - Hardened, rate-limited

**Example domains:**
- Order management system
- User management platform
- Data processing API
- Content management system

**Deliverable**: Complete production API + documentation + runbook

---

## Weekly Study Plan

**Weekdays (1.5 hours/day)**: Study concepts, code practice  
**Weekend (4-5 hours)**: Complete deliverable, test scenarios  
**Total: 10-12 hours/week**

---

## FastAPI Resources

**Official:**
- FastAPI documentation (excellent!)
- Pydantic documentation
- SQLAlchemy documentation

**Books:**
- "Building Data Science Applications with FastAPI" by Plumley
- "Modern Python Developer's Toolkit" (has FastAPI section)

**Courses:**
- TestDriven.io FastAPI course (highly recommended)
- FastAPI official tutorial (free, comprehensive)

**Tools:**
- Swagger UI (built-in)
- Postman (API testing)
- Locust (load testing)
- Docker (containerization)

---

## FastAPI Patterns You'll Master

1. **Dependency Injection** - Clean, testable code
2. **Pydantic validation** - Automatic data validation
3. **Async/await** - High-performance I/O
4. **Background tasks** - Non-blocking operations
5. **Middleware** - Cross-cutting concerns
6. **APIRouter** - Modular API organization
7. **Response models** - Control what gets returned
8. **OAuth2 flows** - Proper authentication
9. **WebSockets** - Real-time communication
10. **OpenAPI customization** - Great documentation

---

## Production FastAPI Checklist

**Code Structure:**
- ✅ Router-based organization (not single file)
- ✅ Models, schemas, services separated
- ✅ Dependency injection used properly
- ✅ Type hints everywhere
- ✅ Pydantic models for validation

**Security:**
- ✅ Authentication implemented
- ✅ Authorization checks on endpoints
- ✅ CORS configured correctly
- ✅ Secrets from environment/vault
- ✅ Rate limiting in place
- ✅ Input validation (prevent injection)

**Database:**
- ✅ Connection pooling configured
- ✅ Migrations managed with Alembic
- ✅ Async queries where appropriate
- ✅ Repository pattern for data access
- ✅ Transactions handled properly

**Testing:**
- ✅ Unit tests for business logic
- ✅ Integration tests for endpoints
- ✅ Mocked dependencies
- ✅ Test database separate from prod
- ✅ CI runs tests automatically

**Observability:**
- ✅ Structured JSON logging
- ✅ Request/response logging
- ✅ Error tracking (Sentry/App Insights)
- ✅ Custom metrics tracked
- ✅ Health check endpoints

**Performance:**
- ✅ Caching implemented (Redis)
- ✅ Database queries optimized
- ✅ Async used for I/O operations
- ✅ Connection pooling configured
- ✅ Load tested

**Deployment:**
- ✅ Containerized (Dockerfile)
- ✅ Environment-specific configs
- ✅ CI/CD pipeline configured
- ✅ Health checks for orchestrator
- ✅ Graceful shutdown handling

---

## Common FastAPI Mistakes to Avoid

1. **Blocking I/O in async endpoints** - Defeats async benefits
2. **No response models** - Leaking internal data
3. **Mixing sync and async** - Confusion and bugs
4. **Not using dependency injection** - Repeated code, hard to test
5. **No rate limiting** - Vulnerable to abuse
6. **Missing input validation** - Security vulnerabilities
7. **Global state** - Breaks in production with multiple workers
8. **Not testing with TestClient** - Bugs in production
9. **Hardcoded configuration** - Can't change without redeployment
10. **No monitoring** - Can't debug production issues

---

## FastAPI vs Other Frameworks

**FastAPI vs Flask:**
- FastAPI: Async, automatic validation, built-in docs
- Flask: Sync by default, more libraries, longer history
- Use FastAPI for: New projects, async workloads, data APIs
- Use Flask for: Legacy integration, simpler sync apps

**FastAPI vs Django:**
- FastAPI: Lightweight, API-focused, high performance
- Django: Full-featured, ORM included, admin panel
- Use FastAPI for: Microservices, APIs, high throughput
- Use Django for: Full web apps, content sites, rapid development

**FastAPI vs Spring Boot (Java):**
- FastAPI: Python, simpler, faster development
- Spring Boot: Java, enterprise features, massive ecosystem
- Use FastAPI for: Data engineering, ML APIs, Python teams
- Use Spring Boot for: Enterprise systems, Java teams, complex requirements

---

## Production Scenarios You'll Handle

1. **API receives 10x normal traffic** - Scale horizontally, enable caching
2. **Database connection pool exhausted** - Tune pool size, add connection limits
3. **Slow endpoint (5 seconds)** - Profile, optimize query, add caching
4. **Memory leak in production** - Profile memory, find leak, fix and redeploy
5. **Authentication bypass attempt** - Security review, harden auth
6. **Rate limit being hit by legitimate users** - Adjust limits, implement tiered access
7. **Background task queue backed up** - Add workers, optimize tasks
8. **Deployment breaks production** - Rollback, fix in dev, redeploy
9. **Dependency vulnerability** - Update dependency, test, deploy
10. **Compliance audit** - Show logging, access controls, data handling

---

**This curriculum takes you from FastAPI basics to production-grade API development.** Focus is on **real-world patterns** and **enterprise practices**.
