# Python for Data Engineering & Backend Development Curriculum

**Target Audience**: Data engineers and backend developers  
**Duration**: 12 weeks  
**Time Commitment**: 10-12 hours per week  
**Prerequisites**: Basic programming concepts

---

## PHASE 0: Python Fundamentals (Weeks 1-3)
*Goal: Master core Python and write clean, production code*

### Week 1: Python Core Concepts

**Concepts (2 hours)**
- Python execution model (interpreted, dynamic typing)
- Data types (int, float, str, bool, None)
- Collections (list, tuple, dict, set) - when to use each
- Mutability vs immutability
- List comprehensions (readable iteration)
- Dictionary comprehensions
- Generator expressions (memory efficiency)

**Practice (4 hours)**
- Solve 20 basic problems using different data structures
- Practice list comprehensions:
  - Filter, map, transform lists
  - Nested comprehensions
- Dictionary manipulation:
  - Counting, grouping, inverting
- Understand mutability pitfalls:
  - Default mutable arguments
  - Shallow vs deep copy
- **Production scenario**: Process 10 million records efficiently using generators

**Deliverable**: Python fundamentals notebook with 30+ examples

**Key insight**: *List comprehensions and generators are Python's superpowers - use them*

---

### Week 2: Functions, Modules & OOP Basics

**Concepts (3 hours)**
- Function definition and arguments
- *args and **kwargs
- Lambda functions (when to use)
- Decorators (function wrappers)
- Modules and packages
- import best practices
- Classes and objects (OOP basics)
- Inheritance and composition
- Magic methods (__init__, __str__, __repr__)

**Practice (5 hours)**
- Write utility functions for data processing:
  - Reusable data validation functions
  - Date/time manipulation
  - String cleaning
- Create custom decorators:
  - Timing decorator (measure execution)
  - Logging decorator
  - Retry decorator (for API calls)
- Build classes for domain objects:
  - Customer, Order, Product classes
  - Validation in __init__
- Organize code into modules
- **Production scenario**: Build reusable data processing library

**Deliverable**: Python package with utilities + tests

**Key insight**: *Functions are first-class objects - decorators enable powerful patterns*

---

### Week 3: Error Handling, File I/O & Context Managers

**Concepts (3 hours)**
- Exception handling (try/except/else/finally)
- Raising exceptions
- Custom exception classes
- File operations (read, write, append)
- Context managers (with statement)
- Working with different file formats:
  - CSV (csv module)
  - JSON (json module)
  - Excel (openpyxl, pandas)
- Pathlib for file paths (better than os.path)

**Practice (5 hours)**
- Implement robust file processing:
  - Read CSV, handle errors gracefully
  - Parse JSON with schema validation
  - Process Excel workbooks
- Create custom context manager
- Build error handling framework:
  - Log errors to file
  - Retry on failure
  - Clean up resources
- **Production scenario**: Process daily CSV files with error reporting

**Deliverable**: File processing pipeline with comprehensive error handling

**Key insight**: *Always use context managers (with) - prevents resource leaks*

---

## PHASE 1: Advanced Python (Weeks 4-6)
*Goal: Write professional, maintainable Python*

### Week 4: Working with Data (Pandas & NumPy)

**Concepts (3 hours)**
- NumPy arrays vs Python lists (performance)
- Pandas Series and DataFrames
- Reading data (CSV, JSON, Excel, SQL)
- Data cleaning:
  - Handling missing values
  - Data type conversion
  - Duplicate removal
- Filtering, sorting, grouping
- Merging and joining DataFrames
- Apply functions to DataFrames
- Vectorization (avoid loops)

**Practice (5 hours)**
- Load and clean messy datasets:
  - Handle missing values (dropna, fillna)
  - Convert data types
  - Remove duplicates
- Perform exploratory analysis:
  - Groupby aggregations
  - Pivot tables
  - Multi-index operations
- Merge datasets from different sources
- Optimize pandas operations (vectorization vs loops)
- **Production scenario**: Clean and transform raw data for analytics

**Deliverable**: Data transformation pipeline using pandas

**Key insight**: *Vectorized operations in pandas are 100x faster than loops*

---

### Week 5: APIs, Requests & Data Collection

**Concepts (3 hours)**
- HTTP basics (GET, POST, PUT, DELETE)
- Requests library
- Authentication (API keys, OAuth)
- JSON parsing and validation
- Rate limiting and retry logic
- Async requests (aiohttp for concurrency)
- Web scraping basics (BeautifulSoup)
- Error handling for network calls

**Practice (5 hours)**
- Call public APIs:
  - REST API with authentication
  - Parse JSON responses
  - Paginate through results
- Implement retry logic with exponential backoff
- Handle rate limits gracefully
- Use aiohttp for concurrent requests
- Scrape web pages (legally!)
- **Production scenario**: Pull data from third-party API daily

**Deliverable**: API client with robust error handling + rate limiting

**Key insight**: *Network calls fail - always implement retries and timeouts*

---

### Week 6: Testing, Logging & Code Quality

**Concepts (3 hours)**
- Unit testing with pytest
- Test fixtures and parametrization
- Mocking external dependencies
- Test coverage
- Logging best practices (logging module)
- Structured logging (JSON logs)
- Code formatting (Black, isort)
- Linting (pylint, flake8, ruff)
- Type hints and mypy

**Practice (5 hours)**
- Write comprehensive tests:
  - Unit tests for functions
  - Integration tests for workflows
  - Mock database/API calls
- Implement proper logging:
  - Different log levels (DEBUG, INFO, WARNING, ERROR)
  - Log to file and console
  - Structured JSON logging
- Add type hints to code
- Set up pre-commit hooks (formatting, linting)
- **Production scenario**: Make codebase production-ready with tests + logging

**Deliverable**: Fully tested, logged, and linted Python project

**Key insight**: *Code without tests is legacy code - test as you write*

---

## PHASE 2: Python for Data Engineering (Weeks 7-9)
*Goal: Build production data pipelines in Python*

### Week 7: Database Connectivity & SQL from Python

**Concepts (3 hours)**
- DB-API 2.0 (standard database interface)
- SQLAlchemy Core (query builder)
- SQLAlchemy ORM (object-relational mapping)
- Connection pooling
- Transaction management
- Parameterized queries (prevent SQL injection)
- Batch inserts for performance
- Pandas integration with databases

**Practice (5 hours)**
- Connect to databases (PostgreSQL, SQL Server, SQLite):
  - Raw connections with psycopg2/pyodbc
  - SQLAlchemy engine
- Execute queries from Python:
  - SELECT queries, fetch results
  - INSERT, UPDATE, DELETE
  - Parameterized queries
- Use SQLAlchemy ORM:
  - Define models
  - Query objects
  - Relationships
- Bulk operations with pandas:
  - to_sql(), read_sql()
- **Production scenario**: Extract data from database, transform, load to another

**Deliverable**: ETL script using SQLAlchemy

**Key insight**: *Use parameterized queries always - prevent SQL injection*

---

### Week 8: Working with Cloud Storage (Azure)

**Concepts (3 hours)**
- Azure SDK for Python
- Azure Blob Storage operations
- Azure Data Lake Storage Gen2
- Authentication (service principals, managed identity)
- Streaming large files (don't load all in memory)
- Parallel uploads/downloads
- Delta Lake from Python

**Practice (5 hours)**
- Connect to Azure Blob Storage:
  - Upload files
  - Download files
  - List blobs
- Process large files:
  - Stream processing (chunks)
  - Parallel processing
- Interact with Delta tables:
  - Read Delta with pandas/PySpark
  - Write to Delta format
- Implement retry logic for network operations
- **Production scenario**: Batch process files from blob storage

**Deliverable**: Azure data pipeline (blob → process → delta)

**Key insight**: *Stream large files - never load gigabytes into memory*

---

### Week 9: Workflow Orchestration with Python

**Concepts (3 hours)**
- Task dependencies and DAGs
- Prefect/Airflow basics (choose one)
- Scheduling and triggers
- Parameterized workflows
- Error handling and retries
- Monitoring and alerting
- Backfilling historical data

**Practice (5 hours)**
- Build data pipeline with Prefect:
  - Define tasks and flows
  - Task dependencies
  - Parameters and configurations
- Schedule workflows:
  - Cron schedules
  - Event-driven triggers
- Implement error handling:
  - Retry logic
  - Failure notifications
- Monitor executions
- **Production scenario**: Daily ETL pipeline with dependencies

**Deliverable**: Scheduled data pipeline with monitoring

**Key insight**: *Don't write custom schedulers - use workflow tools*

---

## PHASE 3: Production Python (Weeks 10-12)
*Goal: Performance, deployment, and best practices*

### Week 10: Performance Optimization

**Concepts (3 hours)**
- Profiling code (cProfile, line_profiler)
- Time complexity (Big O notation)
- Memory profiling (memory_profiler)
- Multiprocessing vs threading vs async
- GIL (Global Interpreter Lock) implications
- Caching strategies (lru_cache, memoization)
- Using compiled extensions (NumPy, Cython)

**Practice (5 hours)**
- Profile slow code:
  - Identify bottlenecks
  - Optimize hot paths
- Implement parallelization:
  - Multiprocessing for CPU-bound tasks
  - Threading for I/O-bound tasks
  - Async for many concurrent I/O operations
- Add caching to expensive functions
- Benchmark before/after optimization
- **Production scenario**: Process 10GB file - optimize from 2 hours to 10 minutes

**Deliverable**: Performance optimization report + optimized code

**Key insight**: *Profile first, optimize second - don't guess where slowness is*

---

### Week 11: Packaging & Deployment

**Concepts (3 hours)**
- Virtual environments (venv, conda)
- Dependency management (requirements.txt, poetry, pipenv)
- Package structure (setup.py, pyproject.toml)
- Building wheels and distributing
- Docker for Python apps
- Environment variables and configuration
- CI/CD for Python (GitHub Actions, Azure Pipelines)

**Practice (5 hours)**
- Create Python package:
  - Proper directory structure
  - Setup.py configuration
  - Build and install locally
- Containerize Python app:
  - Write Dockerfile
  - Multi-stage builds
  - Minimize image size
- Set up CI/CD pipeline:
  - Run tests automatically
  - Build and push to registry
  - Deploy to Azure
- **Production scenario**: Deploy Python app to Azure Container Instances

**Deliverable**: Packaged, containerized, and deployed Python application

**Key insight**: *Containerization ensures consistency - same code runs everywhere*

---

### Week 12: Production Best Practices

**Concepts (3 hours)**
- Configuration management (environment-specific)
- Secrets management (Azure Key Vault)
- Monitoring and observability
- Graceful shutdown and signal handling
- Resource cleanup (connections, files)
- Structured logging for production
- Health checks and readiness probes

**Practice (5 hours)**
- Implement production patterns:
  - Load config from environment
  - Secrets from Key Vault
  - Structured JSON logging
  - Health check endpoint
  - Graceful shutdown on SIGTERM
- Add monitoring:
  - Application Insights integration
  - Custom metrics
  - Error tracking
- Write operations runbook
- **Production scenario**: Production-ready Python service

**Deliverable**: Production-grade Python application

**Key insight**: *Production code needs monitoring, logging, graceful degradation*

---

## Weekly Study Plan

**Weekdays (1.5 hours/day)**: Practice coding, read documentation  
**Weekend (4-5 hours)**: Complete deliverable, test scenarios  
**Total: 10-12 hours/week**

---

## Python Resources

**Books:**
- "Fluent Python" by Luciano Ramalho (advanced)
- "Python Cookbook" by Beazley & Jones
- "Effective Python" by Brett Slatkin

**Online:**
- Real Python (tutorials)
- Python documentation (official)
- Talk Python podcast
- Python Weekly newsletter

**Practice:**
- LeetCode (algorithms)
- HackerRank (practice problems)
- Advent of Code (fun challenges)

---

## Python Patterns You'll Master

1. **List comprehensions** - Readable, fast data transformations
2. **Generators** - Memory-efficient iteration
3. **Decorators** - Function wrapping for cross-cutting concerns
4. **Context managers** - Resource management
5. **Pandas vectorization** - Avoid loops for data operations
6. **Async/await** - Concurrent I/O operations
7. **Type hints** - Self-documenting code
8. **Pytest fixtures** - Reusable test setup
9. **SQLAlchemy ORM** - Database abstraction
10. **Structured logging** - Production observability

---

## Common Python Mistakes to Avoid

1. **Mutable default arguments** - `def func(lst=[]):` creates shared list
2. **Not using context managers** - Resource leaks from unclosed files
3. **Loops instead of vectorization** - 100x slower in pandas/NumPy
4. **Catching bare except** - Hides bugs and makes debugging hard
5. **Global variables** - Makes code untestable and fragile
6. **Not using virtual environments** - Dependency conflicts
7. **Ignoring PEP 8** - Inconsistent, hard-to-read code
8. **No type hints** - Hard to understand function signatures
9. **Not testing** - Bugs in production
10. **Premature optimization** - Profile first, then optimize

---

## Python for Different Roles

**Data Engineer:**
- Focus: Pandas, SQLAlchemy, Azure SDK, workflow orchestration
- Build: ETL pipelines, data quality checks, automation scripts

**Backend Developer:**
- Focus: FastAPI, async programming, database integration
- Build: REST APIs, microservices, authentication systems

**Data Scientist:**
- Focus: NumPy, Pandas, Matplotlib, scikit-learn
- Build: Analysis pipelines, feature engineering, model training

**DevOps Engineer:**
- Focus: Subprocess, Docker SDK, cloud SDKs, automation
- Build: Deployment scripts, infrastructure automation, monitoring

---

## Production Python Checklist

**Code Quality:**
- ✅ Type hints on all functions
- ✅ Docstrings on public APIs
- ✅ Formatted with Black
- ✅ Linted with ruff/pylint
- ✅ No security vulnerabilities (bandit)

**Testing:**
- ✅ Unit tests with >80% coverage
- ✅ Integration tests for workflows
- ✅ Mocked external dependencies
- ✅ Tests run in CI/CD

**Logging & Monitoring:**
- ✅ Structured JSON logging
- ✅ Log levels used appropriately
- ✅ No sensitive data in logs
- ✅ Metrics tracked (custom + system)
- ✅ Health check endpoint

**Configuration:**
- ✅ No hardcoded values
- ✅ Environment-specific configs
- ✅ Secrets from vault/env vars
- ✅ Config validation on startup

**Deployment:**
- ✅ Containerized (Dockerfile)
- ✅ Dependencies pinned (requirements.txt)
- ✅ CI/CD pipeline configured
- ✅ Rollback procedure documented

**Performance:**
- ✅ Profiled for bottlenecks
- ✅ Appropriate parallelization
- ✅ Memory usage optimized
- ✅ Connection pooling for databases

---

**This curriculum takes you from Python basics to production-ready data engineering and backend development.** Focus is on **practical patterns** used in real-world systems.
