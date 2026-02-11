# Data Engineering: Production-Ready Skills
## PySpark, ADF, Databricks + Real Production Scenarios

**Target Audience**: Data engineer using PySpark, ADF, Databricks  
**Duration**: 14 weeks  
**Time Commitment**: 12-15 hours per week  
**Prerequisites**: Basic PySpark, ADF, Databricks experience

---

## Your Current Foundation

✅ **PySpark** - Core data processing  
✅ **Azure Data Factory (ADF)** - Orchestration  
✅ **Databricks** - Lakehouse platform  
✅ **Mainframe background** - Understanding of data reliability, transactions  
✅ **Python** - Scripting and automation  

---

## The Production Reality Gap

Most data engineers can make pipelines work in dev. Production is different:
- **Data quality issues** at 2 AM trigger alerts
- **Schema changes** break pipelines silently
- **Cost overruns** from inefficient queries ($10K Databricks bills)
- **Performance degradation** as data volume grows
- **Compliance requirements** (GDPR, data retention, PII masking)
- **Debugging failures** in distributed systems
- **Monitoring & alerting** - know before stakeholders complain

This curriculum focuses on **these real scenarios**.

---

## PHASE 0: Production PySpark Fundamentals (Weeks 1-4)
*Goal: Write PySpark that actually performs well and doesn't explode costs*

### Week 1: PySpark Performance & Optimization Deep Dive

**Concepts (3 hours)**
- Spark execution model: jobs, stages, tasks, shuffles
- Lazy evaluation and execution plans (`explain()`)
- Wide vs narrow transformations
- Catalyst optimizer and Tungsten execution engine
- Partitioning strategies (why it matters more than you think)
- Common anti-patterns that kill performance

**Practice (5 hours)**
- Analyze Spark UI for existing pipeline (stages, shuffle, skew)
- Rewrite inefficient query using these optimizations:
  - Broadcast joins vs shuffle joins
  - Filter pushdown and column pruning
  - Partition pruning
  - Coalesce vs repartition (when to use each)
- Compare execution plans before/after optimization
- Identify and fix data skew in real dataset
- **Production scenario**: Debug why job takes 3 hours when it should take 30 min

**Deliverable**: Performance analysis report + optimized pipeline (with metrics)

**Key insight**: *80% of production issues are bad partitioning, skew, or unnecessary shuffles*

---

### Week 2: Data Quality & Schema Management

**Concepts (3 hours)**
- Schema evolution (what happens when source adds column?)
- Schema enforcement vs schema evolution in Delta Lake
- Data quality frameworks (Great Expectations, Deequ)
- Constraint validation and data profiling
- Handling bad records (permissive vs failfast modes)
- Data contracts between teams

**Practice (5 hours)**
- Implement Great Expectations in Databricks notebook
- Create data quality checks for your pipeline:
  - Null checks, uniqueness, referential integrity
  - Value ranges, pattern matching
  - Freshness checks (data recency)
- Handle schema changes gracefully:
  - Add new column to source → pipeline doesn't break
  - Remove column → pipeline detects and alerts
- Build data quality dashboard
- **Production scenario**: Upstream system changes date format, your pipeline starts failing

**Deliverable**: Data quality framework integrated into pipeline + runbook for schema changes

**Key insight**: *Bad data will happen - detect it early, fail fast, alert clearly*

---

### Week 3: Delta Lake Deep Dive

**Concepts (3 hours)**
- Delta Lake architecture (transaction log, time travel)
- ACID transactions in data lakes
- Optimize and Z-ordering (when and why)
- Vacuum operations (retention policies)
- Change Data Feed (CDC tracking)
- Merge operations and SCD Type 2
- Liquid clustering (new feature)

**Practice (5 hours)**
- Convert Parquet tables to Delta
- Implement SCD Type 2 pattern with MERGE
- Set up Change Data Feed for downstream consumption
- Optimize tables with Z-order on query columns
- Implement time travel for data recovery
- Create retention policy and VACUUM schedule
- Benchmark: Delta vs Parquet performance
- **Production scenario**: Accidental DELETE - recover data from 2 days ago

**Deliverable**: Delta Lake best practices implementation + disaster recovery procedure

**Key insight**: *Delta Lake gives you database guarantees on data lake - use it properly*

---

### Week 4: Error Handling & Resilience

**Concepts (3 hours)**
- Idempotent pipeline design (re-run safety)
- Checkpointing in streaming jobs
- Dead letter queues for bad records
- Retry strategies with exponential backoff
- Circuit breaker patterns for external APIs
- Graceful degradation strategies

**Practice (5 hours)**
- Refactor pipeline to be fully idempotent
- Implement structured error handling:
  - Try-catch blocks with specific exceptions
  - Bad records to separate Delta table (quarantine)
  - Retry logic for transient failures
- Add structured logging (JSON format)
- Create error notification system
- Test failure scenarios:
  - Source data missing
  - Target table locked
  - Network timeout
- **Production scenario**: Pipeline runs twice due to ADF retry - ensure no duplicates

**Deliverable**: Resilient pipeline with comprehensive error handling + test results

**Key insight**: *Pipelines WILL fail - design for failure, not success*

---

## PHASE 1: Azure Data Factory Production Patterns (Weeks 5-7)
*Goal: Build ADF pipelines that are maintainable, monitored, and debuggable*

### Week 5: ADF Architecture & Best Practices

**Concepts (3 hours)**
- Pipeline design patterns (orchestration vs execution)
- Parameterization and dynamic pipelines
- Metadata-driven pipelines (config tables)
- Linked services and integration runtimes
- ADF vs Databricks orchestration (when to use each)
- Source control integration (Git)
- Environment promotion (Dev → Test → Prod)

**Practice (5 hours)**
- Build metadata-driven framework:
  - Config table defines pipelines to run
  - ForEach loop processes table list
  - Parameters passed dynamically
- Implement reusable pipeline patterns
- Set up Git integration and branching strategy
- Create deployment pipeline (Azure DevOps/GitHub Actions)
- Parameterize everything (no hardcoded values)
- **Production scenario**: Need to add 50 similar tables - do it with config, not 50 pipelines

**Deliverable**: Metadata-driven ADF framework + deployment pipeline

**Key insight**: *Hard-coded pipelines are technical debt - parameterize everything*

---

### Week 6: Monitoring, Logging & Alerting

**Concepts (3 hours)**
- ADF monitoring and metrics
- Log Analytics integration
- Custom logging from Databricks
- Azure Monitor alerts and action groups
- Application Insights for custom metrics
- Diagnostic settings and retention
- Creating operational dashboards

**Practice (5 hours)**
- Enable diagnostic logs for ADF and Databricks
- Send logs to Log Analytics workspace
- Create KQL queries for common scenarios:
  - Failed pipelines in last 24 hours
  - Longest running activities
  - Cost by pipeline
- Set up alerts:
  - Pipeline failure → email + Teams notification
  - Duration > threshold → notify on-call
  - Data quality check fails → page team
- Build Azure Dashboard with key metrics
- **Production scenario**: Pipeline fails at 3 AM - you get paged with actionable context

**Deliverable**: Complete monitoring solution + alert runbook

**Key insight**: *You can't fix what you can't see - observability is mandatory*

---

### Week 7: Cost Optimization & Performance

**Concepts (3 hours)**
- Databricks cluster sizing and autoscaling
- Cluster pools vs cold start
- Spot instances for batch workloads
- Job clusters vs all-purpose clusters
- ADF pricing model (activity runs, data movement)
- Storage costs (hot vs cool vs archive tiers)
- Cost allocation tags and showback

**Practice (5 hours)**
- Analyze current Databricks costs (cluster utilization)
- Right-size clusters based on actual usage
- Implement autoscaling policies
- Convert to job clusters where appropriate
- Set up cluster pools for faster startup
- Use spot instances for fault-tolerant jobs
- Move cold data to cool/archive storage
- Create cost monitoring dashboard
- **Production scenario**: Monthly bill is $50K, need to reduce by 40% without breaking SLAs

**Deliverable**: Cost optimization plan with projected savings

**Key insight**: *Cloud costs spiral quickly - monitor daily, optimize continuously*

---

## PHASE 2: Advanced Data Engineering Patterns (Weeks 8-11)
*Goal: Handle complex real-world scenarios*

### Week 8: Incremental Processing & CDC

**Concepts (3 hours)**
- Full load vs incremental patterns
- Watermark-based incremental (high/low water mark)
- Change Data Capture (CDC) patterns
- Merge strategies (upsert, SCD Type 2)
- Handling late-arriving data
- Exactly-once processing semantics

**Practice (5 hours)**
- Implement watermark-based incremental load
- Set up CDC from source system:
  - Azure SQL CDC → Event Hubs → Databricks
  - OR use ADF change tracking
- Build MERGE logic in Delta Lake
- Handle out-of-order data arrival
- Test edge cases:
  - Backfill historical data
  - Reprocess specific date range
  - Handle duplicate CDC events
- **Production scenario**: Need to reprocess 6 months of data without affecting current loads

**Deliverable**: Production-ready incremental pipeline with CDC

**Key insight**: *Full loads don't scale - master incremental patterns early*

---

### Week 9: Data Governance & Security

**Concepts (3 hours)**
- Unity Catalog (Databricks governance)
- Table ACLs and fine-grained access control
- PII detection and masking
- Data classification and lineage
- Compliance requirements (GDPR, CCPA)
- Encryption at rest and in transit
- Azure Purview integration
- Audit logging

**Practice (5 hours)**
- Enable Unity Catalog in Databricks
- Implement row/column-level security
- Create PII masking functions
- Set up data lineage tracking
- Implement data retention policies
- Create compliance audit reports
- Tag sensitive data appropriately
- **Production scenario**: GDPR deletion request - remove user's data across all tables

**Deliverable**: Governed data platform with security controls

**Key insight**: *Compliance isn't optional - build governance from day one*

---

### Week 10: Streaming & Real-Time Processing

**Concepts (3 hours)**
- Structured Streaming fundamentals
- Triggers (micro-batch vs continuous)
- Watermarking and late data handling
- Stateful operations (aggregations, joins)
- Exactly-once guarantees
- Event Hubs vs Service Bus for streaming
- Stream-stream vs stream-static joins

**Practice (5 hours)**
- Build streaming pipeline:
  - Event Hubs → Databricks Structured Streaming → Delta Lake
- Implement windowed aggregations
- Handle late-arriving events with watermarks
- Create stream-static join
- Implement checkpointing properly
- Monitor streaming query metrics
- Test failure recovery (restart from checkpoint)
- **Production scenario**: Real-time fraud detection on transaction stream

**Deliverable**: Production streaming pipeline with monitoring

**Key insight**: *Streaming adds complexity - start with batch, move to streaming when needed*

---

### Week 11: Data Pipeline Testing & CI/CD

**Concepts (3 hours)**
- Unit testing PySpark code
- Integration testing pipelines
- Data pipeline testing strategies
- CI/CD for data pipelines
- Infrastructure as Code (Terraform/ARM)
- Deployment strategies (blue-green, canary)
- Rollback procedures

**Practice (5 hours)**
- Write unit tests for PySpark transformations (pytest)
- Create integration tests with sample data
- Set up CI/CD pipeline:
  - Code commit → tests run → deploy to dev
  - Approval gate → deploy to prod
- Implement IaC with Terraform:
  - ADF pipelines
  - Databricks workspace config
  - Storage accounts
- Test rollback procedure
- **Production scenario**: Deploy pipeline change, it breaks prod, rollback in 5 minutes

**Deliverable**: Tested pipeline with automated deployment

**Key insight**: *Test before deploy - data pipeline bugs are expensive*

---

## PHASE 3: Production Scenarios Deep Dive (Weeks 12-14)
*Goal: Handle real production crises and operations*

### Week 12: Troubleshooting & Debugging Production Issues

**Real scenarios to practice:**

**Scenario 1: Pipeline Suddenly Slow (Performance Regression)**
- Check: Spark UI for skew, shuffle sizes
- Analyze: Execution plan changes
- Investigate: Data volume growth
- Fix: Repartition, optimize joins
- Time limit: 2 hours

**Scenario 2: Silent Data Quality Issue**
- Symptom: Downstream reports look wrong
- Debug: Trace data lineage backward
- Find: Null handling bug in transformation
- Fix: Add quality checks, backfill data
- Time limit: 4 hours

**Scenario 3: Databricks Cluster Won't Start**
- Error: Init script failure
- Debug: Check logs, library conflicts
- Fix: Update cluster config
- Prevent: Test in dev environment first
- Time limit: 30 minutes

**Scenario 4: ADF Pipeline Stuck**
- Symptom: Activity running for 10 hours
- Debug: Check Databricks job, no progress
- Find: Waiting for cluster, autoscaling maxed
- Fix: Increase max workers, optimize query
- Time limit: 1 hour

**Deliverable**: Troubleshooting playbook with step-by-step procedures

---

### Week 13: Building a Production-Grade Lakehouse

**Complete implementation project:**

Build end-to-end lakehouse with all production patterns:

**Architecture:**
```
Source Systems → ADF Ingestion → Bronze (Raw) → Silver (Cleansed) → Gold (Curated)
                                      ↓              ↓                ↓
                                   Delta Lake    Delta Lake      Delta Lake
                                      ↓              ↓                ↓
                                  Quality Checks  Transformations  Aggregations
```

**Requirements:**
1. **Ingestion Layer**:
   - Metadata-driven ADF pipelines
   - Incremental loads with watermarks
   - Error handling and retry logic
   - Landing zone → Bronze Delta tables

2. **Transformation Layer**:
   - Bronze → Silver: cleansing, deduplication, schema validation
   - Silver → Gold: business logic, aggregations, joins
   - SCD Type 2 implementation
   - Data quality checks at each layer

3. **Orchestration**:
   - ADF master pipeline
   - Databricks notebooks parameterized
   - Dependency management between layers
   - Parallel processing where possible

4. **Monitoring & Ops**:
   - Logging to Log Analytics
   - Alerts for failures and SLA breaches
   - Cost tracking dashboard
   - Data lineage documentation

5. **Governance**:
   - Unity Catalog enabled
   - Access controls per layer
   - PII masking in Gold layer
   - Retention policies

**Deliverable**: Complete lakehouse implementation + documentation

---

### Week 14: Capstone - Production Crisis Simulation

**Simulate real production week:**

**Day 1 (Monday)**: Source system changes schema unexpectedly
- Pipeline breaks
- Fix without data loss
- Implement schema evolution handling

**Day 2 (Tuesday)**: Performance degradation detected
- Query taking 3x longer than baseline
- Users complaining about dashboard delays
- Debug and optimize

**Day 3 (Wednesday)**: Data quality issue discovered
- Incorrect aggregations for past week
- Root cause analysis
- Backfill corrected data

**Day 4 (Thursday)**: Cost spike alert
- Databricks bill doubled this week
- Identify cause (cluster left running? Inefficient query?)
- Implement cost controls

**Day 5 (Friday)**: GDPR deletion request
- Delete user data across all tables
- Prove complete deletion
- Document process for future requests

**Deliverable**: Incident reports for each scenario + preventive measures

---

## Weekly Study Structure

**Weekday Evenings (1.5 hours/day)**
- Monday/Tuesday: Study concepts, watch videos
- Wednesday/Thursday: Hands-on practice
- Friday: Document learnings, test scenarios

**Weekend (4-6 hours)**
- Saturday: Build deliverable, deep practice
- Sunday: Testing, refinement, documentation

**Total: 12-15 hours/week**

---

## Essential Tools & Skills Matrix

| Skill | Current Level | Target Level | Priority |
|-------|---------------|--------------|----------|
| PySpark Optimization | Intermediate | Advanced | HIGH |
| Delta Lake | Basic | Advanced | HIGH |
| ADF Patterns | Intermediate | Advanced | HIGH |
| Databricks Admin | Basic | Intermediate | MEDIUM |
| Monitoring/Logging | Basic | Advanced | HIGH |
| Data Quality | Basic | Advanced | HIGH |
| Cost Optimization | Basic | Intermediate | HIGH |
| Unity Catalog | Basic | Intermediate | MEDIUM |
| Streaming | Basic | Intermediate | LOW |
| CI/CD for Data | None | Intermediate | MEDIUM |

---

## Production Readiness Checklist

Before considering a pipeline "production-ready":

**Functionality:**
- ✅ Idempotent (safe to re-run)
- ✅ Parameterized (no hardcoded values)
- ✅ Handles schema changes gracefully
- ✅ Error handling with retries
- ✅ Bad record handling (quarantine)

**Performance:**
- ✅ Optimized for cost and speed
- ✅ Proper partitioning strategy
- ✅ No data skew issues
- ✅ Execution plan reviewed

**Quality:**
- ✅ Data quality checks implemented
- ✅ Freshness monitoring
- ✅ Completeness validation
- ✅ Reconciliation with source

**Observability:**
- ✅ Structured logging
- ✅ Metrics tracked (duration, row counts, costs)
- ✅ Alerts configured
- ✅ Runbook documented

**Governance:**
- ✅ Access controls set
- ✅ PII handled appropriately
- ✅ Retention policies defined
- ✅ Lineage documented

**Operations:**
- ✅ Tested in non-prod
- ✅ Rollback procedure exists
- ✅ On-call runbook ready
- ✅ Stakeholders notified

---

## Learning Resources

**PySpark Performance:**
- "Spark: The Definitive Guide" by Chambers & Zaharia
- Databricks Performance Tuning Guide
- Advanced Spark Summit talks (YouTube)

**Delta Lake:**
- Official Delta Lake documentation
- Databricks Delta Lake best practices
- "Delta Lake: Up and Running" (O'Reilly)

**Azure Data Engineering:**
- Microsoft Learn: Data Engineer path
- Azure Data Factory documentation
- Databricks on Azure certification prep

**Data Quality:**
- Great Expectations documentation
- "Data Quality Fundamentals" by Barat et al.

**Production Practices:**
- "The Data Warehouse Toolkit" by Kimball
- "Designing Data-Intensive Applications" by Kleppmann
- DataOps best practices (DataKitchen blog)

---

## Career Progression Path

**Current → 3 months:**
- Solid PySpark optimization skills
- Production-ready ADF patterns
- Monitoring & troubleshooting expertise

**3-6 months:**
- Lead data pipeline architecture
- Mentor junior engineers
- Design lakehouse implementations

**6-12 months:**
- Senior Data Engineer
- Platform/infrastructure decisions
- Cost optimization leadership

**12+ months:**
- Staff/Principal Data Engineer
- Architectural oversight
- Cross-team technical leadership

Your **mainframe → Databricks → production expertise** trajectory is valuable - you understand reliability at scale.

---

## Common Production Mistakes to Avoid

1. **Not testing with production data volumes** → Dev works, prod crashes
2. **Ignoring data skew** → One partition takes 10x longer
3. **No monitoring until it breaks** → Find out from users, not systems
4. **Hardcoding everything** → Change requires code deployment
5. **No cost tracking** → $50K surprise bill
6. **Assuming data quality** → Garbage in, garbage out
7. **No schema validation** → Silent failures from schema drift
8. **Full loads forever** → Doesn't scale past certain size
9. **No idempotency** → Re-runs create duplicates
10. **Poor error messages** → Debugging takes 10x longer

---

**This curriculum takes you from "it works on my machine" to "it runs reliably in production at scale."** The focus is on **battle-tested patterns** and **real scenarios** you'll face.
