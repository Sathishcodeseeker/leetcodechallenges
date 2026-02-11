# SQL for Data Engineering & Analytics Curriculum

**Target Audience**: Data engineers and analysts  
**Duration**: 12 weeks  
**Time Commitment**: 8-9 hours per week  
**Prerequisites**: Basic programming knowledge

---

## PHASE 0: SQL Foundations (Weeks 1-3)
*Goal: Master core SQL and understand execution*

### Week 1: SQL Fundamentals & Query Mechanics

**Concepts (2 hours)**
- SQL execution order (FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY)
- Table scans vs index scans
- Query optimization basics
- ANSI SQL standards vs vendor-specific (T-SQL, PL/SQL, PostgreSQL)
- Understanding execution plans
- NULL handling (the source of many bugs)

**Practice (4 hours)**
- Set up local database (PostgreSQL or SQL Server)
- Load sample datasets (employees, sales, orders)
- Write queries practicing:
  - SELECT, WHERE, ORDER BY
  - DISTINCT, TOP/LIMIT
  - NULL handling (IS NULL, COALESCE, NULLIF)
  - String functions (CONCAT, SUBSTRING, TRIM, REPLACE)
  - Date functions (DATEADD, DATEDIFF, DATE_TRUNC)
- Analyze execution plans for each query
- Identify full table scans

**Deliverable**: Query workbook with execution plans annotated

**Key insight**: *SQL is declarative - you say WHAT you want, optimizer decides HOW*

---

### Week 2: JOINs, Subqueries & CTEs

**Concepts (3 hours)**
- JOIN types and when to use each:
  - INNER JOIN (intersection)
  - LEFT/RIGHT JOIN (include unmatched)
  - FULL OUTER JOIN (everything)
  - CROSS JOIN (cartesian product - rarely needed)
- Self-joins (comparing rows to other rows in same table)
- Subqueries vs JOINs (performance implications)
- Common Table Expressions (CTEs) for readability
- Correlated vs non-correlated subqueries

**Practice (5 hours)**
- Complex join scenarios:
  - Find customers with no orders (LEFT JOIN + IS NULL)
  - Find products ordered by all customers (INNER JOIN)
  - Hierarchical data (employee → manager self-join)
  - Many-to-many relationships (bridge tables)
- Rewrite subqueries as JOINs and compare performance
- Build complex CTEs with multiple levels
- Debug common JOIN mistakes:
  - Cartesian product explosion
  - Duplicate rows from JOIN
  - Wrong JOIN type

**Deliverable**: Complex query solving real business problem with CTEs

**Key insight**: *JOINs are the heart of SQL - master them completely*

---

### Week 3: Aggregations & Window Functions

**Concepts (3 hours)**
- GROUP BY mechanics and common mistakes
- Aggregate functions (COUNT, SUM, AVG, MIN, MAX)
- HAVING vs WHERE (pre vs post aggregation)
- Window functions (game-changer for analytics):
  - ROW_NUMBER, RANK, DENSE_RANK
  - Running totals (SUM OVER)
  - Moving averages
  - LAG/LEAD (access previous/next rows)
  - PARTITION BY vs GROUP BY

**Practice (5 hours)**
- Aggregation scenarios:
  - Sales by region, product, month
  - Top N customers by revenue
  - Year-over-year growth
- Window function problems:
  - Running total of sales
  - Rank products by revenue within category
  - Calculate month-over-month change
  - Find first/last purchase per customer
  - Detect gaps in sequences
- Performance comparison: window functions vs self-joins
- **Production scenario**: Build monthly sales report with YoY comparison

**Deliverable**: Analytics queries using window functions

**Key insight**: *Window functions replace complex self-joins - use them everywhere*

---

## PHASE 1: Advanced SQL (Weeks 4-6)
*Goal: Performance, optimization, and complex scenarios*

### Week 4: Indexes & Query Performance

**Concepts (3 hours)**
- Index types:
  - Clustered vs non-clustered
  - B-tree indexes (most common)
  - Bitmap indexes (for low cardinality)
  - Covering indexes
  - Filtered indexes
- Index strategy (what to index, what not to)
- Index maintenance (fragmentation, rebuilds)
- Statistics and cardinality estimation
- Query hints (use sparingly)
- Execution plan deep dive (seeks vs scans, hash vs merge joins)

**Practice (5 hours)**
- Load large dataset (millions of rows)
- Write slow queries, analyze execution plans
- Add appropriate indexes:
  - Single column indexes
  - Composite indexes (column order matters!)
  - Covering indexes
- Measure performance before/after indexing
- Find missing indexes using DMVs (SQL Server) or pg_stat_statements (PostgreSQL)
- Identify unused indexes (wasting space)
- **Production scenario**: Query takes 2 minutes, needs to run in <5 seconds

**Deliverable**: Performance tuning report with before/after metrics

**Key insight**: *Proper indexing is 80% of query performance - but over-indexing slows writes*

---

### Week 5: Transactions, Locking & Concurrency

**Concepts (3 hours)**
- ACID properties (Atomicity, Consistency, Isolation, Durability)
- Transaction isolation levels:
  - READ UNCOMMITTED (dirty reads)
  - READ COMMITTED (default, usually)
  - REPEATABLE READ
  - SERIALIZABLE
- Locking types (shared, exclusive, intent locks)
- Deadlocks (causes and prevention)
- Optimistic vs pessimistic concurrency
- NOLOCK hint (and why it's dangerous)

**Practice (5 hours)**
- Write transactions with COMMIT/ROLLBACK
- Simulate concurrent transactions:
  - Two sessions updating same row
  - Deadlock scenario and resolution
  - Lost update problem
- Test different isolation levels
- Identify and resolve blocking queries
- Use transaction logs for debugging
- **Production scenario**: ETL job deadlocks with user queries - fix it

**Deliverable**: Concurrency testing scenarios + solutions

**Key insight**: *In multi-user systems, locks and isolation levels matter - default isn't always right*

---

### Week 6: Complex Data Transformations

**Concepts (3 hours)**
- PIVOT and UNPIVOT (rows ↔ columns)
- CASE statements for conditional logic
- Recursive CTEs (hierarchies, graphs)
- SET operations (UNION, INTERSECT, EXCEPT)
- STRING_AGG / GROUP_CONCAT (aggregating strings)
- JSON/XML handling in SQL
- Regular expressions in SQL

**Practice (5 hours)**
- Transform data for reporting:
  - Rows to columns (PIVOT sales by month)
  - Columns to rows (UNPIVOT quarterly data)
  - Build organizational hierarchy with recursive CTE
  - Find gaps in date sequences
- Complex CASE logic:
  - Customer segmentation (RFM analysis)
  - Conditional aggregations
- Parse JSON data from API logs
- **Production scenario**: Transform raw transactional data into analytical star schema

**Deliverable**: ETL transformation queries for dimensional model

**Key insight**: *SQL can handle complex transformations - often faster than pulling to Python*

---

## PHASE 2: SQL for Data Engineering (Weeks 7-9)
*Goal: Production data pipelines and warehouse patterns*

### Week 7: Slowly Changing Dimensions (SCD)

**Concepts (3 hours)**
- SCD Type 0: No changes allowed
- SCD Type 1: Overwrite (lose history)
- SCD Type 2: Track history (most common)
- SCD Type 3: Limited history
- SCD Type 4: Separate history table
- SCD Type 6: Hybrid (1+2+3)
- Effective dating patterns
- Surrogate keys vs natural keys

**Practice (5 hours)**
- Implement SCD Type 2 with MERGE statement:
  - Detect new records → INSERT
  - Detect changes → expire old, insert new
  - Detect no changes → do nothing
- Build dimension table with:
  - Surrogate key (identity/sequence)
  - Natural key (business key)
  - Effective dates (valid_from, valid_to)
  - Current flag (is_current)
- Handle late-arriving dimension updates
- Write queries to get point-in-time view
- **Production scenario**: Customer address changes - maintain history for reporting

**Deliverable**: SCD Type 2 implementation + test cases

**Key insight**: *SCD Type 2 is standard in data warehousing - master it completely*

---

### Week 8: Incremental Loading Patterns

**Concepts (3 hours)**
- Full load vs incremental (why incremental matters)
- Watermark-based loading (high water mark)
- Change Data Capture (CDC)
- Merge/Upsert patterns
- Idempotent operations
- Handling deletes (hard delete vs soft delete)
- Late-arriving data strategies

**Practice (5 hours)**
- Implement watermark-based incremental load:
  - Track last_updated timestamp
  - Load only new/changed records
  - Update watermark table
- Write MERGE statement for upsert:
  - Match on business key
  - Update if exists
  - Insert if not exists
- Handle soft deletes (is_deleted flag)
- Test idempotency (run twice, same result)
- **Production scenario**: Daily incremental load from source system to data warehouse

**Deliverable**: Production-ready incremental load procedure

**Key insight**: *Full loads don't scale - incremental patterns are essential for production*

---

### Week 9: Data Quality & Testing

**Concepts (3 hours)**
- Data quality dimensions (completeness, accuracy, consistency, timeliness)
- Constraint validation (NOT NULL, CHECK, UNIQUE, FOREIGN KEY)
- Referential integrity checks
- Duplicate detection strategies
- Outlier detection (statistical methods)
- Reconciliation (source vs target counts)
- Data profiling queries

**Practice (5 hours)**
- Build data quality framework:
  - NULL checks (what % of columns have nulls?)
  - Duplicate detection (GROUP BY + HAVING COUNT(*) > 1)
  - Referential integrity (find orphaned records)
  - Value range checks (dates in future, negative quantities)
  - Pattern matching (email, phone validation)
- Create quality dashboard queries
- Write reconciliation queries (source count = target count?)
- Implement data quality tests that run after ETL
- **Production scenario**: Pipeline loads data, but counts don't match - debug with SQL

**Deliverable**: Data quality test suite

**Key insight**: *Trust but verify - always validate data after loading*

---

## PHASE 3: Database Design & Optimization (Weeks 10-12)
*Goal: Design databases and optimize for production*

### Week 10: Database Design & Normalization

**Concepts (3 hours)**
- Normal forms (1NF, 2NF, 3NF, BCNF)
- When to normalize vs denormalize
- Star schema (fact + dimension tables)
- Snowflake schema (normalized dimensions)
- Data vault modeling (alternative approach)
- Naming conventions and standards
- Data types (choosing the right one matters)

**Practice (5 hours)**
- Design transactional database (OLTP):
  - Normalize to 3NF
  - Define primary and foreign keys
  - Add constraints
- Design analytical database (OLAP):
  - Star schema with fact table
  - Dimension tables (customer, product, date)
  - Slowly changing dimensions
- Compare storage and query performance
- **Production scenario**: Design database for e-commerce system (orders, customers, products)

**Deliverable**: Complete database design with ERD diagram

**Key insight**: *OLTP needs normalization; OLAP needs denormalization - different goals*

---

### Week 11: Stored Procedures, Functions & Triggers

**Concepts (3 hours)**
- Stored procedures vs functions (when to use each)
- Input/output parameters
- Error handling (TRY...CATCH in T-SQL, EXCEPTION in PL/SQL)
- Triggers (BEFORE, AFTER, INSTEAD OF)
- Cursor vs set-based operations (cursors are usually bad)
- Dynamic SQL (and SQL injection risks)
- Temporary tables vs table variables

**Practice (5 hours)**
- Write stored procedures for common operations:
  - Insert/update with validation
  - Error handling and logging
  - Transaction management
- Create functions:
  - Scalar functions (return single value)
  - Table-valued functions (return table)
- Implement audit trigger (track who changed what)
- Convert cursor logic to set-based (performance comparison)
- **Production scenario**: Build ETL procedure with error handling and logging

**Deliverable**: Stored procedure library for data pipeline

**Key insight**: *Stored procedures encapsulate logic; avoid cursors - think in sets*

---

### Week 12: Production Database Operations

**Concepts (3 hours)**
- Backup and recovery strategies
- Point-in-time recovery
- Partitioning large tables (range, list, hash)
- Archiving old data
- Database maintenance (index rebuilds, statistics updates)
- Monitoring and alerting
- Query store / performance insights
- Dealing with large deletes/updates (batching)

**Practice (5 hours)**
- Implement table partitioning:
  - Partition fact table by date
  - Test partition pruning
- Create archival process:
  - Move old data to archive table
  - Batch deletes to avoid locking
- Set up maintenance jobs:
  - Index maintenance
  - Statistics updates
  - Backup schedules
- Monitor long-running queries
- **Production scenario**: Delete 1 billion old records without blocking users

**Deliverable**: Database maintenance playbook

**Key insight**: *Databases need ongoing care - maintenance is not optional*

---

## Weekly Study Plan

**Weekdays (1 hour/day)**: Practice queries, read documentation  
**Weekend (3-4 hours)**: Complete deliverable, test scenarios  
**Total: 8-9 hours/week**

---

## SQL Resources

**Books:**
- "SQL Performance Explained" by Markus Winand
- "SQL Antipatterns" by Bill Karwin
- "The Data Warehouse Toolkit" by Ralph Kimball

**Online:**
- Mode Analytics SQL Tutorial (excellent for analytics)
- Use The Index, Luke (indexing guide)
- PostgreSQL documentation (best DB docs)
- LeetCode SQL problems (practice)

**Tools:**
- DBeaver (universal DB client)
- SQL Server Management Studio (SSMS)
- pgAdmin (PostgreSQL)
- DataGrip (JetBrains)

---

## Production SQL Patterns You'll Master

1. **Incremental loads** - Only process new/changed data
2. **SCD Type 2** - Historical tracking in dimensions
3. **MERGE statements** - Efficient upserts
4. **Window functions** - Complex analytics without self-joins
5. **CTEs** - Readable, maintainable complex queries
6. **Proper indexing** - Performance without over-indexing
7. **Transaction management** - ACID guarantees
8. **Data quality checks** - Automated validation
9. **Partition pruning** - Query only relevant partitions
10. **Set-based operations** - Avoid cursors at all costs

---

## Common SQL Mistakes to Avoid

1. **Using SELECT *** - Specify columns explicitly
2. **No indexes on JOIN columns** - Major performance killer
3. **Cursors for set operations** - 100x slower than set-based
4. **No WHERE on DELETE/UPDATE** - Accidental data loss
5. **Implicit data type conversion** - Index scans become table scans
6. **Not using MERGE for upserts** - Slower than individual statements
7. **NOLOCK everywhere** - Can read uncommitted data
8. **No transaction for multi-statement operations** - Partial failures
9. **Hard-coded values** - Use parameters for reusability
10. **No execution plan analysis** - Flying blind on performance

---

## SQL Interview Questions You'll Be Able to Answer

**Easy:**
- Find second highest salary
- Remove duplicates from table
- Find employees with no manager

**Medium:**
- Running total by department
- Gap detection in sequences
- Pivot table with dynamic columns

**Hard:**
- Median calculation without built-in function
- Detect fraud patterns across transactions
- Optimize slow query with execution plan analysis

---

**This curriculum takes you from basic SQL to production data engineering expertise.** Focus is on **real-world patterns** used daily in data engineering and analytics.
