# Complete Guide to Joins: Types and Strategies

## Table of Contents
- [Join Types by Condition](#join-types-by-condition)
- [Join Execution Strategies](#join-execution-strategies)
- [Join Types by Result Set](#join-types-by-result-set)
- [Performance Comparison](#performance-comparison)
- [Real-World Examples](#real-world-examples)
- [Best Practices](#best-practices)

---

## Join Types by Condition

### 1. Equi Join

**Definition:** Join using equality (`=`) operator.

**SQL Example:**
```sql
SELECT *
FROM employees e
JOIN departments d
  ON e.dept_id = d.dept_id;  -- Uses =
```

**PySpark Example:**
```python
result = employees.join(
    departments,
    employees.dept_id == departments.dept_id,
    "inner"
)

# Or simpler when column names match:
result = employees.join(departments, "dept_id", "inner")
```

**Characteristics:**
- ✅ Most common join type (95%+ of joins)
- ✅ Fast performance (can use hash join)
- ✅ Used for foreign key relationships
- ✅ Can leverage indexes

**Use Cases:**
- Primary key / Foreign key relationships
- Matching records by ID
- Standard table relationships

---

### 2. Non-Equi Join

**Definition:** Join using any operator OTHER than `=` (`<`, `>`, `<=`, `>=`, `!=`, `BETWEEN`)

**SQL Examples:**

**Greater Than:**
```sql
SELECT e1.name, e2.name, e1.salary
FROM employees e1
JOIN employees e2
  ON e1.salary > e2.salary;
```

**Range Join:**
```sql
SELECT e.name, e.salary, b.grade
FROM employees e
JOIN salary_bands b
  ON e.salary >= b.min_salary 
  AND e.salary < b.max_salary;
```

**Not Equal:**
```sql
SELECT *
FROM products p1
JOIN products p2
  ON p1.category != p2.category;
```

**PySpark Example:**
```python
from pyspark.sql import functions as F

# Range join
result = employees.join(
    salary_bands,
    (employees.salary >= salary_bands.min_salary) &
    (employees.salary < salary_bands.max_salary)
)

# Greater than
result = employees.alias("e1").join(
    employees.alias("e2"),
    F.col("e1.salary") > F.col("e2.salary")
)
```

**Characteristics:**
- ⚠️ Less common (5% of joins)
- ⚠️ Slower performance (often needs nested loop join)
- ✅ Used for ranges, inequalities, overlaps

**Use Cases:**
- Salary bands / Age groups / Price ranges
- Finding overlapping time periods
- Inequality comparisons
- Proximity matching

---

### 3. Theta Join (θ Join)

**Definition:** Join with ANY condition (umbrella term for all joins). Uses theta (θ) to represent any comparison operator.

**Join Hierarchy:**
```
Theta Join (θ)
├── Equi Join (θ = "=")
│   └── Natural Join (auto equi on same column names)
└── Non-Equi Join (θ ≠ "=")
```

**SQL Examples:**

**Simple Theta (Equi):**
```sql
SELECT *
FROM table1 t1
JOIN table2 t2
  ON t1.id = t2.id;  -- θ is "="
```

**Complex Theta (Multiple Conditions):**
```sql
SELECT *
FROM employees e
JOIN salary_bands b
  ON e.department = b.department        -- Equi
  AND e.salary >= b.min_salary          -- Non-equi
  AND e.salary < b.max_salary;          -- Non-equi
```

**Theta with OR:**
```sql
SELECT *
FROM customers c
JOIN orders o
  ON c.customer_id = o.customer_id      -- Equi
  OR c.email = o.contact_email;         -- Another equi with OR
```

**PySpark Example:**
```python
result = employees.join(
    salary_bands,
    (employees.department == salary_bands.department) &  # Equi
    (employees.salary >= salary_bands.min_salary) &      # Non-equi
    (employees.salary < salary_bands.max_salary)         # Non-equi
)
```

**Characteristics:**
- General term encompassing all join types
- Performance varies based on conditions
- Can combine multiple operators

---

### 4. Natural Join

**Definition:** Automatic equi join on all columns with the same name in both tables.

**SQL Example:**
```sql
-- Automatically joins on columns with same names
SELECT *
FROM employees
NATURAL JOIN departments;

-- Equivalent to:
SELECT *
FROM employees e
JOIN departments d
  ON e.dept_id = d.dept_id 
  AND e.dept_name = d.dept_name;  -- All matching column names
```

**Characteristics:**
- ⚠️ Implicit join condition
- ⚠️ Risky (can join on unintended columns)
- ❌ Not commonly used in production
- ❌ Not supported in PySpark

**Why Risky:**
```sql
-- If both tables have columns: id, name, dept_id
NATURAL JOIN  -- Joins on ALL three columns!
-- This might not be what you want
```

---

### 5. Self Join

**Definition:** Join a table with itself.

**SQL Examples:**

**Employee-Manager Relationships:**
```sql
SELECT 
    e.name AS employee,
    m.name AS manager
FROM employees e
JOIN employees m
  ON e.manager_id = m.employee_id;
```

**Find Higher-Paid Employees:**
```sql
SELECT 
    e1.name AS higher_paid,
    e1.salary,
    e2.name AS lower_paid,
    e2.salary
FROM employees e1
JOIN employees e2
  ON e1.salary > e2.salary;
```

**PySpark Example:**
```python
employees_alias1 = employees.alias("e1")
employees_alias2 = employees.alias("e2")

result = employees_alias1.join(
    employees_alias2,
    F.col("e1.manager_id") == F.col("e2.employee_id")
)
```

**Characteristics:**
- Same table referenced twice with different aliases
- Can be equi, non-equi, or theta join
- Common for hierarchical data

**Use Cases:**
- Organization charts (employee-manager)
- Parent-child relationships
- Finding duplicates
- Comparing rows within same table

---

### 6. Cross Join (Cartesian Product)

**Definition:** Join without any condition - produces all possible combinations.

**SQL Examples:**
```sql
SELECT *
FROM table1
CROSS JOIN table2;

-- Or using older syntax:
SELECT *
FROM table1, table2;
```

**Result Size Calculation:**
```
If table1 has n rows and table2 has m rows
→ Result has n × m rows
```

**Example:**
```sql
-- Products: 100 rows, Sizes: 5 rows
-- Result: 100 × 5 = 500 rows

SELECT p.product_name, s.size_name
FROM products p
CROSS JOIN sizes s;  -- All product-size combinations
```

**PySpark Example:**
```python
result = df1.crossJoin(df2)

# Or:
result = df1.join(df2)  # No condition = cross join
```

**Characteristics:**
- ❌ No join condition
- ❌ Extremely expensive (n × m rows)
- ⚠️ Usually a mistake if unintentional

**Valid Use Cases:**
- Generating all combinations
- Creating test data
- Calendar tables × dimension tables

---

### 7. Semi Join

**Definition:** Returns rows from left table that have a match in the right table (doesn't include right table columns).

**SQL Examples:**
```sql
-- Using IN (semi join)
SELECT *
FROM employees
WHERE dept_id IN (SELECT dept_id FROM departments);

-- Using EXISTS (semi join)
SELECT *
FROM employees e
WHERE EXISTS (
    SELECT 1 
    FROM departments d 
    WHERE d.dept_id = e.dept_id
);
```

**PySpark Example:**
```python
# Left semi join
result = employees.join(
    departments,
    employees.dept_id == departments.dept_id,
    "left_semi"
)

# Only returns columns from employees, not departments
```

**Characteristics:**
- ✅ Returns only left table columns
- ✅ More efficient than regular join when you don't need right table data
- ✅ Eliminates duplicates from left table

**Use Cases:**
- Filtering: "Get customers who placed orders"
- Existence checks
- Finding records that have related data

---

### 8. Anti Join

**Definition:** Returns rows from left table that have NO match in the right table.

**SQL Examples:**
```sql
-- Using NOT IN (anti join)
SELECT *
FROM employees
WHERE dept_id NOT IN (SELECT dept_id FROM departments);

-- Using NOT EXISTS (anti join)
SELECT *
FROM employees e
WHERE NOT EXISTS (
    SELECT 1 
    FROM departments d 
    WHERE d.dept_id = e.dept_id
);
```

**PySpark Example:**
```python
# Left anti join
result = employees.join(
    departments,
    employees.dept_id == departments.dept_id,
    "left_anti"
)

# Returns employees with no matching department
```

**Characteristics:**
- ✅ Returns rows that DON'T match
- ✅ More efficient than `NOT IN` for large datasets
- ✅ Useful for finding orphaned records

**Use Cases:**
- Finding orphaned records
- Data quality checks
- "Get customers who never placed orders"
- Identifying missing relationships

---

## Join Execution Strategies

These are the algorithms databases and Spark use to physically execute joins.

### 1. Nested Loop Join (NLJ)

**How it works:**
```
For each row in Table A:
    For each row in Table B:
        If join condition matches:
            Output the row
```

**Pseudocode:**
```python
result = []
for row_a in table_a:
    for row_b in table_b:
        if row_a.key == row_b.key:  # or any condition
            result.append(merge(row_a, row_b))
```

**Characteristics:**
- **Complexity:** O(n × m) where n = rows in A, m = rows in B
- **Works with:** Any join condition (equi, non-equi, theta)
- **Best for:** Small tables, or when one table is very small
- **Worst for:** Large tables (becomes extremely slow)

**When Used:**
- Non-equi joins (no better option available)
- Very small tables
- When no indexes exist
- Complex join conditions that can't use hash/sort-merge

**SQL Example:**
```sql
-- Database might use nested loop for this
SELECT *
FROM small_table s  -- 100 rows
JOIN large_table l  -- 1M rows
  ON s.value > l.threshold;  -- Non-equi join
```

**Performance:**
```
Small (100 rows) × Large (1M rows) = 100M comparisons 🐢
```

---

### 2. Hash Join

**How it works:**
```
Phase 1 (Build): Create hash table from smaller table
Phase 2 (Probe): For each row in larger table, lookup in hash table
```

**Pseudocode:**
```python
# Phase 1: Build hash table from smaller table
hash_table = {}
for row in table_a:  # Smaller table
    key = row.join_key
    if key not in hash_table:
        hash_table[key] = []
    hash_table[key].append(row)

# Phase 2: Probe with larger table
result = []
for row in table_b:  # Larger table
    key = row.join_key
    if key in hash_table:
        for matching_row in hash_table[key]:
            result.append(merge(matching_row, row))
```

**Characteristics:**
- **Complexity:** O(n + m) - linear! ⚡⚡⚡
- **Works with:** Equi joins only (requires `=`)
- **Best for:** Large tables with equi joins
- **Memory:** Requires enough memory for hash table
- **Performance:** Very fast

**When Used:**
- Equi joins (most common)
- Large datasets
- When memory is available
- No indexes on join columns

**SQL Example:**
```sql
-- Database will likely use hash join
SELECT *
FROM orders o       -- 10M rows
JOIN customers c    -- 1M rows
  ON o.customer_id = c.customer_id;  -- Equi join
```

**PySpark Example:**
```python
# Spark automatically chooses hash join for equi joins
result = large_df.join(medium_df, "customer_id")
```

---

### 3. Sort-Merge Join (SMJ)

**How it works:**
```
Phase 1: Sort both tables by join key
Phase 2: Merge sorted tables (like merge-sort)
```

**Pseudocode:**
```python
# Phase 1: Sort both tables
sorted_a = sort(table_a, key=lambda x: x.join_key)
sorted_b = sort(table_b, key=lambda x: x.join_key)

# Phase 2: Merge
result = []
i, j = 0, 0
while i < len(sorted_a) and j < len(sorted_b):
    if sorted_a[i].key == sorted_b[j].key:
        result.append(merge(sorted_a[i], sorted_b[j]))
        j += 1
    elif sorted_a[i].key < sorted_b[j].key:
        i += 1
    else:
        j += 1
```

**Characteristics:**
- **Complexity:** O(n log n + m log m) for sorting + O(n + m) for merge
- **Works with:** Equi joins, some non-equi joins
- **Best for:** Already sorted data, or when memory is limited
- **Disk-friendly:** Can work with data that doesn't fit in memory

**When Used:**
- Data is already sorted
- Large datasets that don't fit in memory
- Merge joins on indexed columns
- When hash join isn't possible

**SQL Example:**
```sql
-- If data is already indexed/sorted on join columns
SELECT *
FROM large_table1 l1  -- Sorted by id
JOIN large_table2 l2  -- Sorted by id
  ON l1.id = l2.id;
```

---

### 4. Broadcast Join (Spark-Specific)

**How it works:**
```
1. Copy small table to all worker nodes
2. Each worker performs local join with its partition of large table
```

**PySpark Example:**
```python
from pyspark.sql.functions import broadcast

# Explicitly broadcast small table
result = large_df.join(
    broadcast(small_df),
    "customer_id"
)

# Spark auto-broadcasts if table < spark.sql.autoBroadcastJoinThreshold (default 10MB)
```

**Characteristics:**
- **Best for:** Small table (< 10MB) joining with large table
- **Advantage:** No shuffle needed (very fast)
- **Limitation:** Small table must fit in memory on each executor

**When to Use:**
- One table is small (dimension table)
- Want to avoid expensive shuffle
- Joining fact table with dimension table

**Performance:**
```
Without Broadcast: Shuffle both tables across network 🐢
With Broadcast: Copy small table once, no shuffle ⚡⚡⚡
```

---

### 5. Shuffle Hash Join (Distributed Systems)

**How it works:**
```
1. Shuffle both tables by hash of join key
2. Perform hash join on co-located partitions
```

**Characteristics:**
- Used when both tables are large
- Requires network shuffle (expensive)
- Each partition joins independently

**PySpark:**
```python
# Default join strategy for large tables
result = large_df1.join(large_df2, "key")
```

---

### 6. Cartesian Join

**How it works:**
```
For each row in Table A:
    For each row in Table B:
        Output combined row (no condition check)
```

**Characteristics:**
- No join condition
- Produces n × m rows
- Extremely expensive
- Usually indicates a bug

**SQL Example:**
```sql
-- ⚠️ Danger: Creates massive result set
SELECT *
FROM table1  -- 1000 rows
CROSS JOIN table2;  -- 1000 rows
-- Result: 1,000,000 rows!
```

---

## Join Types by Result Set

### Standard Join Types (INNER, LEFT, RIGHT, FULL)

| Join Type | Returns | SQL | PySpark |
|-----------|---------|-----|---------|
| **INNER** | Only matching rows from both tables | `INNER JOIN` | `"inner"` |
| **LEFT** | All from left + matching from right (nulls if no match) | `LEFT JOIN` | `"left"` or `"left_outer"` |
| **RIGHT** | All from right + matching from left (nulls if no match) | `RIGHT JOIN` | `"right"` or `"right_outer"` |
| **FULL OUTER** | All rows from both (nulls where no match) | `FULL OUTER JOIN` | `"outer"` or `"full_outer"` |
| **CROSS** | All combinations (Cartesian product) | `CROSS JOIN` | `crossJoin()` |
| **LEFT SEMI** | Left rows that have match (left columns only) | `WHERE EXISTS` | `"left_semi"` |
| **LEFT ANTI** | Left rows that have NO match | `WHERE NOT EXISTS` | `"left_anti"` |

**Visual Representation:**

```
Table A: [1, 2, 3]
Table B: [2, 3, 4]

INNER:      [2, 3]           # Only matching
LEFT:       [1, 2, 3]        # All from A
RIGHT:      [2, 3, 4]        # All from B  
FULL OUTER: [1, 2, 3, 4]    # All from both
LEFT SEMI:  [2, 3]           # Matching from A only
LEFT ANTI:  [1]              # Non-matching from A
CROSS:      [1-2, 1-3, 1-4, 2-2, 2-3, 2-4, 3-2, 3-3, 3-4]  # All combinations
```

---

## Performance Comparison

### Join Strategy Performance

| Strategy | Complexity | Best For | Memory | Works With |
|----------|-----------|----------|--------|------------|
| **Hash Join** | O(n + m) ⚡⚡⚡ | Large equi joins | High | Equi only |
| **Sort-Merge** | O(n log n + m log m) ⚡⚡ | Sorted data | Medium | Equi, some non-equi |
| **Nested Loop** | O(n × m) 🐢 | Small tables, non-equi | Low | Any condition |
| **Broadcast** | O(n + m) ⚡⚡⚡ | Small × Large | Medium | Any (but best for equi) |

### Join Type Performance

| Join Type | Typical Performance | Reason |
|-----------|-------------------|---------|
| **Equi Join** | ⚡⚡⚡ Fast | Can use hash join or indexed lookup |
| **Non-Equi Join** | 🐢 Slow | Often requires nested loop |
| **Theta Join** | Varies | Depends on whether it includes equi conditions |
| **Cross Join** | 💀 Very Slow | Produces n × m rows |

---

## Real-World Examples

### Example 1: E-commerce Order Processing

**Equi Join - Orders with Customer Info:**
```sql
SELECT 
    o.order_id,
    o.order_date,
    c.customer_name,
    c.email
FROM orders o
INNER JOIN customers c
  ON o.customer_id = c.customer_id;
```

**PySpark:**
```python
result = orders.join(customers, "customer_id", "inner") \
    .select("order_id", "order_date", "customer_name", "email")
```

---

### Example 2: Financial Analysis - Price Ranges

**Non-Equi Join - Stock Prices in Alert Ranges:**
```sql
SELECT 
    s.symbol,
    s.current_price,
    a.alert_type,
    a.action
FROM stock_prices s
JOIN price_alerts a
  ON s.symbol = a.symbol                    -- Equi part
  AND s.current_price >= a.min_price        -- Non-equi
  AND s.current_price <= a.max_price;       -- Non-equi
```

**PySpark:**
```python
result = stock_prices.join(
    price_alerts,
    (stock_prices.symbol == price_alerts.symbol) &
    (stock_prices.current_price >= price_alerts.min_price) &
    (stock_prices.current_price <= price_alerts.max_price)
)
```

---

### Example 3: HR System - Organizational Hierarchy

**Self Join - Employee-Manager Relationships:**
```sql
SELECT 
    e.employee_name,
    e.job_title,
    m.employee_name AS manager_name,
    m.job_title AS manager_title
FROM employees e
LEFT JOIN employees m
  ON e.manager_id = m.employee_id;
```

**PySpark:**
```python
employees_e = employees.alias("e")
employees_m = employees.alias("m")

result = employees_e.join(
    employees_m,
    F.col("e.manager_id") == F.col("m.employee_id"),
    "left"
).select(
    F.col("e.employee_name"),
    F.col("e.job_title"),
    F.col("m.employee_name").alias("manager_name"),
    F.col("m.job_title").alias("manager_title")
)
```

---

### Example 4: Event Analysis - Overlapping Time Periods

**Non-Equi Join - Find Conflicting Meeting Room Bookings:**
```sql
SELECT 
    b1.booking_id AS booking1,
    b2.booking_id AS booking2,
    b1.room_number,
    b1.start_time,
    b1.end_time
FROM room_bookings b1
JOIN room_bookings b2
  ON b1.room_number = b2.room_number        -- Same room (equi)
  AND b1.booking_id < b2.booking_id         -- Avoid duplicates (non-equi)
  AND b1.start_time < b2.end_time           -- Overlap check (non-equi)
  AND b1.end_time > b2.start_time;          -- Overlap check (non-equi)
```

**PySpark:**
```python
b1 = room_bookings.alias("b1")
b2 = room_bookings.alias("b2")

result = b1.join(
    b2,
    (F.col("b1.room_number") == F.col("b2.room_number")) &
    (F.col("b1.booking_id") < F.col("b2.booking_id")) &
    (F.col("b1.start_time") < F.col("b2.end_time")) &
    (F.col("b1.end_time") > F.col("b2.start_time"))
)
```

---

### Example 5: Data Quality - Find Orphaned Records

**Anti Join - Products Never Ordered:**
```sql
SELECT 
    p.product_id,
    p.product_name,
    p.category
FROM products p
LEFT ANTI JOIN order_items oi
  ON p.product_id = oi.product_id;
```

**PySpark:**
```python
# Products that have never been ordered
orphaned_products = products.join(
    order_items,
    products.product_id == order_items.product_id,
    "left_anti"
)
```

---

### Example 6: Customer Segmentation

**Semi Join - Active Customers (Who Placed Orders):**
```sql
SELECT *
FROM customers c
WHERE EXISTS (
    SELECT 1
    FROM orders o
    WHERE o.customer_id = c.customer_id
    AND o.order_date >= '2024-01-01'
);
```

**PySpark:**
```python
recent_orders = orders.filter(F.col("order_date") >= "2024-01-01")

active_customers = customers.join(
    recent_orders,
    "customer_id",
    "left_semi"
)
```

---

## Best Practices

### 1. Choose the Right Join Type

✅ **DO:**
- Use **equi joins** whenever possible (much faster)
- Add equi conditions even in non-equi joins when possible
- Use **semi/anti joins** instead of `IN`/`NOT IN` for better performance

❌ **DON'T:**
- Use cross joins unintentionally
- Use non-equi joins without filtering data first

**Example:**
```python
# ❌ Slow: Pure non-equi join
result = df1.join(df2, df1.value > df2.threshold)

# ✅ Better: Add equi condition
result = df1.join(
    df2,
    (df1.category == df2.category) &  # Equi first (fast)
    (df1.value > df2.threshold)       # Then non-equi
)
```

---

### 2. Filter Before Joining

✅ **DO:**
- Filter data before joining to reduce data volume
- Push predicates down when possible

```python
# ✅ Good: Filter first
df1_filtered = df1.filter(F.col("date") >= "2024-01-01")
df2_filtered = df2.filter(F.col("status") == "active")
result = df1_filtered.join(df2_filtered, "key")

# ❌ Bad: Join then filter
result = df1.join(df2, "key").filter(
    (F.col("date") >= "2024-01-01") & 
    (F.col("status") == "active")
)
```

---

### 3. Use Broadcast for Small Tables (Spark)

✅ **DO:**
- Explicitly broadcast small dimension tables
- Check if auto-broadcast threshold is appropriate

```python
from pyspark.sql.functions import broadcast

# ✅ Good: Broadcast small table
result = large_fact_table.join(
    broadcast(small_dimension_table),
    "key"
)
```

---

### 4. Avoid Cartesian Products

❌ **DON'T:**
- Create unintentional cross joins
- Join without conditions

```python
# ❌ Danger: Cartesian product
result = df1.join(df2)  # No condition!

# ✅ Good: Always specify condition
result = df1.join(df2, "key")
```

---

### 5. Handle NULL Values Properly

✅ **DO:**
- Remember that `NULL = NULL` is FALSE in SQL
- Use `IS NULL` or `coalesce()` when needed

```sql
-- ❌ Won't match NULL values
SELECT * FROM t1 JOIN t2 ON t1.col = t2.col;

-- ✅ Handle NULLs explicitly
SELECT * FROM t1 JOIN t2 
  ON COALESCE(t1.col, -1) = COALESCE(t2.col, -1);
```

---

### 6. Choose Appropriate Join Type

| Scenario | Use This Join |
|----------|---------------|
| Need all matching records | INNER JOIN |
| Need all from left table | LEFT JOIN |
| Check if record exists | LEFT SEMI JOIN |
| Find records without matches | LEFT ANTI JOIN |
| Need records from both tables | FULL OUTER JOIN |
| Generate all combinations | CROSS JOIN |

---

### 7. Monitor and Optimize

✅ **DO:**
- Check query execution plans
- Monitor shuffle sizes in Spark
- Use appropriate partitioning
- Add indexes on join columns (SQL databases)

**PySpark:**
```python
# Check execution plan
result.explain()

# Check for broadcast vs shuffle
result.explain(mode="formatted")
```

---

### 8. Optimize Non-Equi Joins

When you must use non-equi joins:

```python
# Technique 1: Add equi conditions
result = df1.join(
    df2,
    (df1.category == df2.category) &  # Equi first
    (df1.price > df2.min_price)       # Then non-equi
)

# Technique 2: Pre-bucket data
df1 = df1.withColumn("price_bucket", (F.col("price") / 100).cast("int"))
df2 = df2.withColumn("price_bucket", (F.col("min_price") / 100).cast("int"))

result = df1.join(
    df2,
    (df1.price_bucket == df2.price_bucket) &  # Equi on bucket
    (df1.price > df2.min_price)               # Then exact non-equi
)

# Technique 3: Broadcast small table
result = large_df.join(
    broadcast(small_ranges_df),
    (large_df.value >= small_ranges_df.min_val) &
    (large_df.value < small_ranges_df.max_val)
)
```

---

## Summary Cheat Sheet

### Join Types Quick Reference

| Join Type | Symbol | Condition | Performance |
|-----------|--------|-----------|-------------|
| Equi | = | Uses `=` | ⚡⚡⚡ Fast |
| Non-Equi | ≠, <, >, ≤, ≥ | Any except `=` | 🐢 Slow |
| Theta | θ | Any condition | Varies |
| Natural | AUTO | Same column names | ⚡⚡⚡ (but risky) |
| Self | - | Table with itself | Varies |
| Cross | × | No condition | 💀 Very Slow |
| Semi | ∃ | Existence check | ⚡⚡ Fast |
| Anti | ∄ | Non-existence check | ⚡⚡ Fast |

### Execution Strategies Quick Reference

| Strategy | When Used | Complexity | Best For |
|----------|-----------|------------|----------|
| Hash Join | Equi joins | O(n + m) | Large tables, equi joins |
| Sort-Merge | Sorted data | O(n log n + m log m) | Pre-sorted, memory-limited |
| Nested Loop | Non-equi joins | O(n × m) | Small tables, any condition |
| Broadcast | Small × Large | O(n + m) | Dimension × Fact tables |

### Common Patterns

```sql
-- Pattern 1: Standard FK relationship
SELECT * FROM orders o
INNER JOIN customers c ON o.customer_id = c.customer_id;

-- Pattern 2: Range matching
SELECT * FROM employees e
JOIN salary_bands b 
  ON e.salary >= b.min AND e.salary < b.max;

-- Pattern 3: Hierarchy (self-join)
SELECT e.*, m.name AS manager
FROM employees e
LEFT JOIN employees m ON e.manager_id = m.id;

-- Pattern 4: Find missing records
SELECT * FROM products
WHERE product_id NOT IN (SELECT product_id FROM order_items);

-- Pattern 5: Overlapping periods
SELECT * FROM bookings b1
JOIN bookings b2
  ON b1.room = b2.room
  AND b1.start < b2.end
  AND b1.end > b2.start;
```

---

## Additional Resources

### SQL Standards
- ANSI SQL-92 introduced standard JOIN syntax
- ANSI SQL-99 added NATURAL JOIN
- Most modern databases support full SQL-92+ syntax

### PySpark Documentation
- [PySpark Join Documentation](https://spark.apache.org/docs/latest/api/python/reference/pyspark.sql/api/pyspark.sql.DataFrame.join.html)
- [Spark SQL Join Strategies](https://spark.apache.org/docs/latest/sql-performance-tuning.html)

### Performance Tuning
- Monitor execution plans
- Use appropriate partitioning
- Leverage broadcast for small tables
- Filter before joining
- Add indexes on join columns (SQL databases)

---

**Last Updated:** 2026  
**Author:** Comprehensive Joins Guide
