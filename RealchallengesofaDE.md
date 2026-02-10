Real World Example 1: The Missing Revenue

Bug Report:
"Finance dashboard shows $2M less revenue than yesterday
 but no pipeline failed"

Your Data Architecture:
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌───────────┐
│ Postgres │───→│ Airflow  │───→│ Snowflake│───→│ Dashboard │
│ (Source) │    │ Pipeline │    │ (Warehouse)│   │ (Looker)  │
└──────────┘    └──────────┘    └──────────┘    └───────────┘


Step 1: Check pipeline status
  → Airflow: All DAGs green ✅
  → No failures anywhere
  → "But data is WRONG"

Step 2: Check dashboard query
  → SQL looks correct ✅
  → Same query as last month

Step 3: Check Snowflake data
  → SELECT SUM(revenue) FROM fact_orders 
     WHERE order_date = '2024-03-31';
  → Result: $8M (yesterday it showed $10M for same date)
  → 2M rows are MISSING

Step 4: Check Airflow extraction
  → Extracted 500,000 rows today
  → But source Postgres has 520,000 rows for that date
  → 20,000 rows missing from extraction ❌

Step 5: WHY are 20,000 rows missing?
  → Check extraction query:
     SELECT * FROM orders 
     WHERE updated_at > '{{ last_run_time }}'
  → last_run_time = 2024-03-31 00:00:00 UTC

Step 6: AHA MOMENT 💡
  → Daylight saving time changed last night
  → Server timezone shifted
  → last_run_time was calculated in LOCAL time
  → But updated_at is stored in UTC
  → 1 hour gap was created
  → 20,000 orders fell into that gap
  → They were NEVER extracted
  → No error. Pipeline ran "successfully"

Step 7: Fix
  → Force all timestamps to UTC
  → Backfill the missing 1-hour window
  → Add reconciliation check

Real World Example 2: The Duplicate Revenue

Bug Report:
"Revenue jumped 40% overnight. Sales team is celebrating.
 But it's probably wrong."

Architecture:
┌──────────┐    ┌──────────┐    ┌──────────┐
│ Kafka    │───→│ Spark    │───→│ Delta    │
│ (Events) │    │ Streaming│    │ Lake     │
└──────────┘    └──────────┘    └──────────┘

Step 1: Check if data is duplicated
  SELECT order_id, COUNT(*) 
  FROM fact_orders 
  GROUP BY order_id 
  HAVING COUNT(*) > 1;
  → 100,000 duplicate order_ids ❌

Step 2: When did duplicates start?
  SELECT DATE(loaded_at), COUNT(*) - COUNT(DISTINCT order_id) as dupes
  FROM fact_orders
  GROUP BY DATE(loaded_at)
  ORDER BY 1 DESC;
  → Duplicates started 2 days ago

Step 3: What happened 2 days ago?
  → Check deployment logs
  → Spark streaming job was restarted
  → Check Kafka consumer group offsets
  → Consumer offset was RESET to 6 hours earlier
  → 6 hours of events were reprocessed
  → But Delta Lake merge had a bug

Step 4: Why did merge not catch duplicates?
  → Check merge code:
     MERGE INTO fact_orders target
     USING staging source
     ON target.order_id = source.order_id
     WHEN MATCHED THEN UPDATE
     WHEN NOT MATCHED THEN INSERT
  
  → Looks correct... BUT
  → Staging table had duplicates WITHIN the same batch
  → MERGE only deduplicates against TARGET
  → It does NOT deduplicate within SOURCE
  → So duplicate source rows both became "NOT MATCHED"
  → Both got INSERTED

Step 5: Root cause chain
  Kafka offset reset
    → Reprocessed 6 hours of events
      → Staging had duplicate events
        → MERGE didn't deduplicate within batch
          → Duplicates inserted into Delta Lake
            → Revenue inflated 40%
              → Sales team celebrating fake numbers

Step 6: Fix
  → Deduplicate staging BEFORE merge
  → Add idempotency to streaming job
  → Add duplicate detection alert
  → Backfill correct data

Real World Example 3: The Slow Pipeline

Bug Report:
"Daily pipeline that usually takes 30 minutes 
 now takes 8 hours. Nothing changed in code."

Architecture:
┌──────────┐    ┌──────────┐    ┌──────────┐
│ S3 Raw   │───→│ Spark on │───→│ S3 Clean │
│ (Parquet)│    │ EMR      │    │ (Parquet)│
└──────────┘    └──────────┘    └──────────┘

Step 1: Check Spark UI
  → One stage taking 7.5 hours
  → 199 tasks finish in 2 minutes
  → 1 task takes 7.5 hours ← DATA SKEW

Step 2: Which key is skewed?
  SELECT customer_id, COUNT(*) as cnt
  FROM raw_data
  GROUP BY customer_id
  ORDER BY cnt DESC
  LIMIT 10;
  
  → customer_id = 'SYSTEM' has 50 MILLION rows
  → All other customers have < 10,000 rows
  → One partition has 50M rows, others have 10K

Step 3: WHY does 'SYSTEM' have 50M rows?
  → Check with upstream team
  → They deployed a new logging feature
  → Every automated process now creates orders under 'SYSTEM'
  → Data volume for that key grew 1000x overnight
  → Nobody told the data team

Step 4: Fix
  → Filter out 'SYSTEM' records (they're not real orders)
  → OR salt the key to distribute across partitions
  → OR handle 'SYSTEM' in a separate pipeline
  → Add data volume monitoring alerts

Root cause chain:
  Upstream team deployed new feature
    → 'SYSTEM' customer got 50M rows
      → Spark join/groupby on customer_id
        → One partition got all 50M rows
          → One executor ran out of memory
            → Spilled to disk
              → 30 min pipeline → 8 hours

Real World Example 4: The Silent Schema Change


Bug Report:
"Customer segmentation is completely wrong this week.
 High-value customers are showing as low-value."

Architecture:
┌──────────┐    ┌──────────┐    ┌──────────┐
│ Vendor   │───→│ Python   │───→│ BigQuery │
│ CSV/API  │    │ Ingestion│    │          │
└──────────┘    └──────────┘    └──────────┘


Step 1: Check segmentation logic
  → SQL is correct ✅
  → Same query as always

Step 2: Check underlying data
  SELECT customer_id, lifetime_value 
  FROM dim_customer 
  ORDER BY lifetime_value DESC 
  LIMIT 10;
  → Top customer shows $150 lifetime value
  → That customer usually shows $150,000
  → Values are 1000x smaller ❌

Step 3: Check source data
  → Download latest vendor CSV
  → Column "revenue" now shows 150.00
  → Last week it showed 150000.00
  → VENDOR CHANGED FROM CENTS TO DOLLARS
  → No notification
  → No documentation update

Step 4: How deep is the damage?
  → This column feeds into:
     → dim_customer
     → fact_transactions  
     → 12 downstream dbt models
     → 5 dashboards
     → 1 ML model for churn prediction
     → Finance monthly report (ALREADY SENT) 😱

Step 5: Fix
  → Add unit conversion logic
  → Backfill affected date range
  → Rebuild all downstream models
  → Notify finance about incorrect report
  → Add data drift detection
  → Add vendor schema monitoring

Root cause chain:
  Vendor silently changed units
    → Ingestion loaded wrong values
      → No validation caught it
        → Wrong values propagated everywhere
          → Business decisions made on wrong data


1. Something LOOKS wrong in the data
2. Each individual system looks fine
3. The bug exists in the INTERACTION between systems
4. Root cause is in a system you DIDN'T expect
5. Finding it requires:
   → Accessing multiple live systems
   → Querying real data
   → Talking to other teams
   → Correlating events across time
   → Creative hypothesis formation
   → Trial and error investigation
6. AI cannot do ANY of steps in #5

7. 
