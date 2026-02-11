# LLM & Agentic AI Limitations: The Engineer's Critical Role

> **"AI writes code. Engineers build systems."**

---

## Table of Contents
1. [The Fundamental Truth](#the-fundamental-truth)
2. [Critical LLM Limitations](#critical-llm-limitations)
3. [Agentic AI Coding Tools: What They Can't Do](#agentic-ai-coding-tools-what-they-cant-do)
4. [The 90% That Matters: Engineer's Value](#the-90-that-matters-engineers-value)
5. [Real Production Scenarios](#real-production-scenarios)
6. [The Irreplaceable Human Skills](#the-irreplaceable-human-skills)
7. [Future-Proofing Your Career](#future-proofing-your-career)

---

## The Fundamental Truth

```
┌──────────────────────────────────────────────────┐
│                                                  │
│  AI's job: WRITE code                            │
│                                                  │
│  Engineer's job with that code:                  │
│                                                  │
│  1. Validate    → Is it actually correct?        │
│  2. Edge cases  → What about NULLs, empties?     │
│  3. Optimize    → Will it work at scale?         │
│  4. Read        → Do I understand every line?    │
│  5. Prompt      → Did I ask the right question?  │
│  6. Debug       → Why did it fail at 3 AM?       │
│  7. Maintain    → Can I safely change it later?  │
│  8. Clarity     → Can my team understand it?     │
│  9. Correctness → Does business agree?           │
│                                                  │
│  Writing code is now 10% of the job.             │
│  The other 90% is everything above.              │
│                                                  │
└──────────────────────────────────────────────────┘
```

---

## Critical LLM Limitations

### 1. **No Understanding, Only Patterns**

**What LLMs Do:**
- Predict next tokens based on training data
- Match patterns they've seen before
- Generate statistically likely outputs

**What LLMs DON'T Do:**
- Understand concepts
- Reason about causality
- Know if their output is correct

#### Example: The Subtlety Problem

**Prompt to AI:**
```
"Write a function to calculate user discount based on purchase history"
```

**AI Output:**
```python
def calculate_discount(purchase_history):
    total_purchases = sum(purchase_history)
    if total_purchases > 1000:
        return 0.20  # 20% discount
    elif total_purchases > 500:
        return 0.10  # 10% discount
    else:
        return 0.05  # 5% discount
```

**What's Wrong?** (AI doesn't know these requirements)
- ❌ What if `purchase_history` is None?
- ❌ What if list contains negative values (refunds)?
- ❌ What currency? $1000 USD vs 1000 INR is very different
- ❌ Does "total purchases" mean lifetime or last year?
- ❌ Are discounts cumulative with other promotions?
- ❌ What about B2B vs B2C customers?
- ❌ Regulatory limits on discount percentages?
- ❌ How do we log this for auditing?

**Engineer's Fixed Version:**
```python
from decimal import Decimal
from typing import List, Optional
from datetime import datetime, timedelta
import logging

def calculate_discount(
    purchase_history: List[Decimal],
    customer_type: str,
    currency: str = "USD",
    evaluation_period_days: int = 365,
    purchase_dates: Optional[List[datetime]] = None
) -> Decimal:
    """
    Calculate customer discount based on purchase history.
    
    Business Rules:
    - B2C: 5%, 10%, 20% tiers at $500, $1000
    - B2B: 10%, 15%, 25% tiers at $5000, $10000
    - Only purchases within evaluation_period_days count
    - Max discount capped at 25% per legal requirement
    - Refunds (negative values) excluded from total
    
    Args:
        purchase_history: List of purchase amounts (positive = purchase, negative = refund)
        customer_type: "B2C" or "B2B"
        currency: ISO currency code
        evaluation_period_days: Number of days to look back
        purchase_dates: Dates of purchases (must match purchase_history length)
    
    Returns:
        Decimal discount percentage (0.00 to 0.25)
    
    Raises:
        ValueError: If inputs are invalid
    """
    # Validation
    if purchase_history is None or len(purchase_history) == 0:
        logging.warning("Empty purchase history, returning 0 discount")
        return Decimal('0.00')
    
    if customer_type not in ["B2C", "B2B"]:
        raise ValueError(f"Invalid customer_type: {customer_type}")
    
    if currency != "USD":
        raise NotImplementedError("Multi-currency support not yet implemented")
    
    # Filter by date if provided
    if purchase_dates:
        if len(purchase_dates) != len(purchase_history):
            raise ValueError("purchase_dates must match purchase_history length")
        
        cutoff_date = datetime.now() - timedelta(days=evaluation_period_days)
        recent_purchases = [
            amount for amount, date in zip(purchase_history, purchase_dates)
            if date >= cutoff_date and amount > 0  # Exclude refunds
        ]
    else:
        recent_purchases = [amount for amount in purchase_history if amount > 0]
    
    total = sum(recent_purchases)
    
    # Business logic
    if customer_type == "B2C":
        if total >= Decimal('1000'):
            discount = Decimal('0.20')
        elif total >= Decimal('500'):
            discount = Decimal('0.10')
        else:
            discount = Decimal('0.05')
    else:  # B2B
        if total >= Decimal('10000'):
            discount = Decimal('0.25')
        elif total >= Decimal('5000'):
            discount = Decimal('0.15')
        else:
            discount = Decimal('0.10')
    
    # Legal compliance: cap at 25%
    discount = min(discount, Decimal('0.25'))
    
    logging.info(
        f"Calculated discount: {discount} for {customer_type} customer "
        f"with ${total} in purchases over {evaluation_period_days} days"
    )
    
    return discount
```

**Engineer Added:**
1. ✅ Input validation
2. ✅ Type hints for safety
3. ✅ Documentation of business rules
4. ✅ Edge case handling (None, empty, negatives)
5. ✅ Currency awareness
6. ✅ Time-based filtering
7. ✅ B2B vs B2C logic
8. ✅ Regulatory compliance
9. ✅ Audit logging
10. ✅ Decimal for money (not float!)

---

### 2. **Hallucinations: Confidently Wrong**

**The Problem:** LLMs generate plausible-sounding but incorrect information.

#### Example: The API That Doesn't Exist

**Prompt:**
```
"Show me how to use Azure's get_table_stats() method to get row counts"
```

**AI Response:**
```python
from azure.data.tables import TableServiceClient

client = TableServiceClient.from_connection_string(conn_str)
table_client = client.get_table_client("MyTable")

# Get statistics
stats = table_client.get_table_stats()  # ❌ THIS METHOD DOESN'T EXIST!
row_count = stats.row_count
print(f"Table has {row_count} rows")
```

**Why This Happens:**
- AI saw similar patterns in training data
- It "knows" APIs often have `get_*_stats()` methods
- Generates plausible-looking code
- Has no way to verify the API actually exists

**What Engineer Must Do:**
1. **Verify against documentation** - Read actual Azure SDK docs
2. **Test the code** - Run it to see it fails
3. **Find the real solution:**

```python
from azure.data.tables import TableServiceClient

client = TableServiceClient.from_connection_string(conn_str)
table_client = client.get_table_client("MyTable")

# Azure Tables API has no direct row count method
# Must query all entities (expensive!) or maintain separate counter
row_count = 0
for entity in table_client.list_entities():
    row_count += 1

# Better: Maintain a metadata table with counts
# Or use Azure Cosmos DB which has built-in COUNT()
```

---

### 3. **No Context of Your System**

**LLMs Don't Know:**
- Your database schema
- Your company's coding standards
- Your existing architecture patterns
- Your team's naming conventions
- Your performance requirements
- Your security policies
- Your regulatory constraints

#### Example: The Generic Solution

**Prompt:**
```
"Write a function to save user data to database"
```

**AI's Generic Output:**
```python
def save_user(user_data):
    conn = sqlite3.connect('database.db')
    cursor = conn.cursor()
    cursor.execute(
        "INSERT INTO users VALUES (?, ?, ?)",
        (user_data['id'], user_data['name'], user_data['email'])
    )
    conn.commit()
    conn.close()
```

**Problems in YOUR System:**
1. ❌ You use PostgreSQL, not SQLite
2. ❌ You have a connection pool, shouldn't create new connections
3. ❌ You use SQLAlchemy ORM, not raw SQL
4. ❌ You have 15 user fields, not 3
5. ❌ You must hash passwords before storing
6. ❌ You must validate email format
7. ❌ You must check for duplicate emails
8. ❌ You must audit log all user creation
9. ❌ You must comply with GDPR (data retention policies)
10. ❌ You must use prepared statements (SQL injection protection)

**Your Actual Code:**
```python
from sqlalchemy.orm import Session
from sqlalchemy.exc import IntegrityError
from passlib.hash import bcrypt
import re
from datetime import datetime
from typing import Dict, Optional
import logging
from .models import User, AuditLog
from .exceptions import ValidationError, DuplicateUserError

EMAIL_REGEX = re.compile(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$')

def save_user(
    db: Session,
    user_data: Dict[str, any],
    created_by: str
) -> User:
    """
    Create new user in system.
    
    Implements:
    - Email validation
    - Password hashing
    - Duplicate detection
    - GDPR compliance (consent tracking)
    - Audit logging
    
    Args:
        db: Database session (managed by dependency injection)
        user_data: User information
        created_by: Username of person creating user (for audit)
    
    Returns:
        Created User object
    
    Raises:
        ValidationError: If data is invalid
        DuplicateUserError: If email already exists
    """
    # Validate required fields
    required_fields = ['email', 'password', 'first_name', 'last_name', 'gdpr_consent']
    for field in required_fields:
        if field not in user_data or not user_data[field]:
            raise ValidationError(f"Missing required field: {field}")
    
    # Validate email format
    if not EMAIL_REGEX.match(user_data['email']):
        raise ValidationError(f"Invalid email format: {user_data['email']}")
    
    # Check for duplicate email
    existing_user = db.query(User).filter(User.email == user_data['email']).first()
    if existing_user:
        logging.warning(f"Duplicate user creation attempt: {user_data['email']}")
        raise DuplicateUserError(f"User with email {user_data['email']} already exists")
    
    # Hash password (NEVER store plain text!)
    hashed_password = bcrypt.hash(user_data['password'])
    
    # Create user object
    user = User(
        email=user_data['email'],
        password_hash=hashed_password,
        first_name=user_data['first_name'],
        last_name=user_data['last_name'],
        phone=user_data.get('phone'),
        department=user_data.get('department'),
        role=user_data.get('role', 'user'),  # Default role
        is_active=True,
        gdpr_consent=user_data['gdpr_consent'],
        gdpr_consent_date=datetime.utcnow(),
        created_at=datetime.utcnow(),
        created_by=created_by
    )
    
    try:
        db.add(user)
        db.flush()  # Get user.id without committing
        
        # Audit log
        audit = AuditLog(
            entity_type='User',
            entity_id=user.id,
            action='CREATE',
            performed_by=created_by,
            timestamp=datetime.utcnow(),
            details={'email': user.email, 'role': user.role}
        )
        db.add(audit)
        
        db.commit()
        
        logging.info(f"User created: {user.email} (ID: {user.id}) by {created_by}")
        return user
        
    except IntegrityError as e:
        db.rollback()
        logging.error(f"Database integrity error creating user: {e}")
        raise DuplicateUserError("User creation failed due to constraint violation")
    except Exception as e:
        db.rollback()
        logging.error(f"Unexpected error creating user: {e}")
        raise
```

**What Engineer Added:**
- Integration with YOUR ORM (SQLAlchemy)
- YOUR company's required fields
- YOUR security requirements (password hashing)
- YOUR validation rules
- YOUR error handling patterns
- YOUR audit logging requirements
- YOUR regulatory compliance (GDPR)
- YOUR database session management
- YOUR exception hierarchy

---

### 4. **No Awareness of Scale**

**LLMs don't consider:**
- How much data you have
- How many concurrent users
- What your latency requirements are
- What your cost budget is

#### Example: The Naïve Implementation

**Prompt:**
```
"Write code to find duplicate records in user table"
```

**AI Output:**
```python
def find_duplicates():
    conn = psycopg2.connect(DATABASE_URL)
    cursor = conn.cursor()
    
    # Get all users
    cursor.execute("SELECT * FROM users")
    users = cursor.fetchall()
    
    # Find duplicates
    seen = {}
    duplicates = []
    for user in users:
        email = user[2]  # Assuming email is 3rd column
        if email in seen:
            duplicates.append(user)
        else:
            seen[email] = user
    
    return duplicates
```

**Problems at Scale:**
- ❌ Loads entire table into memory (what if you have 10 million users?)
- ❌ O(n) memory complexity
- ❌ Network transfer of all data
- ❌ No pagination
- ❌ Blocks during entire scan

**Engineer's Scalable Solution:**

```python
from typing import Iterator, Tuple
import logging

def find_duplicate_emails_efficient(
    db: Session,
    batch_size: int = 1000
) -> Iterator[Tuple[str, int]]:
    """
    Find emails that appear more than once in users table.
    
    Memory efficient: Uses SQL GROUP BY, returns iterator.
    Scales to millions of records.
    
    Args:
        db: Database session
        batch_size: Number of duplicate emails to fetch per query
    
    Yields:
        Tuples of (email, count) for emails with count > 1
    """
    # Let database do the heavy lifting
    query = """
        SELECT email, COUNT(*) as cnt
        FROM users
        WHERE email IS NOT NULL
        GROUP BY email
        HAVING COUNT(*) > 1
        ORDER BY cnt DESC, email
    """
    
    # Stream results in batches
    offset = 0
    while True:
        batch = db.execute(
            f"{query} LIMIT {batch_size} OFFSET {offset}"
        ).fetchall()
        
        if not batch:
            break
        
        for email, count in batch:
            yield (email, count)
        
        offset += batch_size
        logging.debug(f"Processed {offset} duplicate emails")

# Usage
for email, count in find_duplicate_emails_efficient(db):
    print(f"{email}: {count} duplicates")
    # Process one at a time, not loading everything into memory
```

**What Engineer Did:**
1. ✅ Pushed computation to database (indexes!)
2. ✅ Used SQL GROUP BY (optimized for this)
3. ✅ Iterator pattern (constant memory)
4. ✅ Batch processing
5. ✅ Handles NULL emails
6. ✅ Ordered output (most duplicates first)
7. ✅ Logging for monitoring
8. ✅ Scales to billions of rows

---

### 5. **Training Data Cutoff**

**LLMs Don't Know:**
- Anything after their training cutoff date
- Latest API versions
- Recent security vulnerabilities
- Deprecated methods
- New best practices

#### Example: Outdated Advice

**Prompt (in 2026):**
```
"Show me how to connect to Azure Blob Storage in Python"
```

**AI Output (trained on 2023 data):**
```python
from azure.storage.blob import BlobServiceClient

# Old way (still works but deprecated)
blob_service_client = BlobServiceClient.from_connection_string(
    "DefaultEndpointsProtocol=https;AccountName=myaccount;..."
)
```

**Engineer Knows (from current docs):**
```python
from azure.storage.blob import BlobServiceClient
from azure.identity import DefaultAzureCredential

# New best practice (2025+): Use managed identity, not connection strings
credential = DefaultAzureCredential()
blob_service_client = BlobServiceClient(
    account_url="https://myaccount.blob.core.windows.net",
    credential=credential
)

# Why better:
# 1. No secrets in code/config
# 2. Automatic credential rotation
# 3. Works with RBAC
# 4. Audit trail in Azure AD
```

---

### 6. **No Testing or Verification**

**LLMs:**
- Don't run the code they generate
- Don't know if it compiles
- Don't know if it has logical errors
- Don't write tests

#### Example: The Bug

**AI Output:**
```python
def calculate_average(numbers):
    return sum(numbers) / len(numbers)
```

**Looks fine? Try it:**
```python
calculate_average([])  # ZeroDivisionError!
calculate_average([1, 2, None, 3])  # TypeError!
calculate_average("123")  # Returns 49.666... (ASCII values!)
```

**Engineer's Production Version:**
```python
from typing import List, Union, Optional
from decimal import Decimal

def calculate_average(
    numbers: List[Union[int, float, Decimal]],
    skip_none: bool = True
) -> Optional[Decimal]:
    """
    Calculate average of numbers with robust error handling.
    
    Args:
        numbers: List of numeric values
        skip_none: If True, ignore None values; if False, raise ValueError
    
    Returns:
        Average as Decimal, or None if no valid numbers
    
    Raises:
        ValueError: If numbers is None or contains invalid types
        
    Examples:
        >>> calculate_average([1, 2, 3])
        Decimal('2.000000')
        >>> calculate_average([])
        None
        >>> calculate_average([1, None, 3], skip_none=True)
        Decimal('2.000000')
    """
    if numbers is None:
        raise ValueError("numbers cannot be None")
    
    # Filter out None values if requested
    if skip_none:
        valid_numbers = [n for n in numbers if n is not None]
    else:
        if None in numbers:
            raise ValueError("numbers contains None values")
        valid_numbers = numbers
    
    # Handle empty list
    if len(valid_numbers) == 0:
        return None
    
    # Type validation
    for i, num in enumerate(valid_numbers):
        if not isinstance(num, (int, float, Decimal)):
            raise ValueError(
                f"Invalid type at index {i}: {type(num)}. "
                f"Expected int, float, or Decimal"
            )
    
    # Calculate with Decimal for precision
    total = Decimal('0')
    for num in valid_numbers:
        total += Decimal(str(num))
    
    return total / len(valid_numbers)

# Unit tests (that AI didn't write)
import pytest

def test_calculate_average_normal():
    assert calculate_average([1, 2, 3]) == Decimal('2')

def test_calculate_average_empty():
    assert calculate_average([]) is None

def test_calculate_average_with_none():
    assert calculate_average([1, None, 3], skip_none=True) == Decimal('2')

def test_calculate_average_none_not_allowed():
    with pytest.raises(ValueError):
        calculate_average([1, None, 3], skip_none=False)

def test_calculate_average_invalid_input():
    with pytest.raises(ValueError):
        calculate_average(None)

def test_calculate_average_invalid_type():
    with pytest.raises(ValueError):
        calculate_average([1, "2", 3])

def test_calculate_average_precision():
    # Decimal avoids float precision errors
    result = calculate_average([0.1, 0.2])
    assert result == Decimal('0.15')  # Not 0.15000000000000002
```

---

### 7. **No Security Awareness**

**LLMs Often Generate:**
- SQL injection vulnerabilities
- Hardcoded credentials
- Unvalidated user input
- Missing authentication checks
- Insecure random number generation
- Sensitive data in logs

#### Example: Security Nightmare

**AI Output:**
```python
def login(username, password):
    query = f"SELECT * FROM users WHERE username='{username}' AND password='{password}'"
    result = execute_query(query)
    if result:
        return "Login successful"
    else:
        return "Login failed"
```

**Vulnerabilities:**
1. ❌ SQL Injection: `username = "admin' OR '1'='1"`
2. ❌ Plain text password comparison
3. ❌ Returns user data (information leakage)
4. ❌ No rate limiting (brute force)
5. ❌ No logging (can't detect attacks)
6. ❌ No account lockout
7. ❌ Timing attack possible

**Engineer's Secure Version:**
```python
from sqlalchemy.orm import Session
from passlib.hash import bcrypt
import logging
import time
from datetime import datetime, timedelta
from typing import Optional
from .models import User, LoginAttempt
from .exceptions import AccountLockedError, InvalidCredentialsError

MAX_LOGIN_ATTEMPTS = 5
LOCKOUT_DURATION_MINUTES = 30

def login(
    db: Session,
    username: str,
    password: str,
    ip_address: str,
    user_agent: str
) -> User:
    """
    Authenticate user with security best practices.
    
    Security features:
    - Parameterized queries (SQL injection protection)
    - Password hashing (bcrypt)
    - Rate limiting
    - Account lockout
    - Timing attack mitigation
    - Audit logging
    
    Args:
        db: Database session
        username: User's username
        password: Plain text password (hashed before comparison)
        ip_address: Client IP for logging
        user_agent: Client user agent for logging
    
    Returns:
        Authenticated User object
    
    Raises:
        InvalidCredentialsError: If username/password incorrect
        AccountLockedError: If account is locked
    """
    # Constant time to prevent timing attacks
    start_time = time.time()
    
    try:
        # Parameterized query (SQL injection safe)
        user = db.query(User).filter(User.username == username).first()
        
        # Check account lockout
        if user:
            recent_attempts = db.query(LoginAttempt).filter(
                LoginAttempt.user_id == user.id,
                LoginAttempt.timestamp >= datetime.utcnow() - timedelta(minutes=LOCKOUT_DURATION_MINUTES),
                LoginAttempt.successful == False
            ).count()
            
            if recent_attempts >= MAX_LOGIN_ATTEMPTS:
                logging.warning(
                    f"Account locked: {username} from {ip_address}. "
                    f"{recent_attempts} failed attempts"
                )
                raise AccountLockedError(
                    f"Account locked due to {recent_attempts} failed login attempts. "
                    f"Try again in {LOCKOUT_DURATION_MINUTES} minutes."
                )
        
        # Verify password (constant time comparison)
        if user and bcrypt.verify(password, user.password_hash):
            # SUCCESS
            login_attempt = LoginAttempt(
                user_id=user.id,
                successful=True,
                ip_address=ip_address,
                user_agent=user_agent,
                timestamp=datetime.utcnow()
            )
            db.add(login_attempt)
            db.commit()
            
            logging.info(f"Successful login: {username} from {ip_address}")
            return user
        else:
            # FAILURE
            if user:
                login_attempt = LoginAttempt(
                    user_id=user.id,
                    successful=False,
                    ip_address=ip_address,
                    user_agent=user_agent,
                    timestamp=datetime.utcnow()
                )
                db.add(login_attempt)
                db.commit()
            
            # Don't reveal whether username exists
            logging.warning(
                f"Failed login attempt: {username} from {ip_address}"
            )
            raise InvalidCredentialsError("Invalid username or password")
    
    finally:
        # Constant time execution (prevent timing attacks)
        elapsed = time.time() - start_time
        if elapsed < 0.5:  # Minimum 500ms response time
            time.sleep(0.5 - elapsed)
```

**What Engineer Added:**
1. ✅ SQL injection protection (parameterized queries)
2. ✅ Password hashing (bcrypt)
3. ✅ Rate limiting
4. ✅ Account lockout
5. ✅ Timing attack mitigation
6. ✅ Comprehensive audit logging
7. ✅ No information leakage (don't say "username doesn't exist")
8. ✅ IP and user agent tracking
9. ✅ Proper exception handling

---

## Agentic AI Coding Tools: What They Can't Do

### Tool Limitations Summary

| Capability | AI Coding Tools | Human Engineer |
|------------|-----------------|----------------|
| **Write syntactically correct code** | ✅ Good | ✅ Good |
| **Understand business requirements** | ❌ Poor | ✅ Excellent |
| **Know your system architecture** | ❌ No context | ✅ Deep knowledge |
| **Handle edge cases** | ❌ Misses most | ✅ Anticipates |
| **Consider security** | ❌ Often insecure | ✅ Security-aware |
| **Optimize for scale** | ❌ Naïve solutions | ✅ Performance-tuned |
| **Debug production issues** | ❌ No runtime access | ✅ Root cause analysis |
| **Make architectural decisions** | ❌ No judgment | ✅ Experience-based |
| **Understand trade-offs** | ❌ No context | ✅ Weighs options |
| **Comply with regulations** | ❌ Unaware | ✅ Knows requirements |
| **Maintain existing code** | ⚠️ Risky changes | ✅ Safe refactoring |
| **Write comprehensive tests** | ⚠️ Happy path only | ✅ Edge cases too |
| **Document tribal knowledge** | ❌ Doesn't know | ✅ Preserves context |
| **Explain "why"** | ❌ Only "what" | ✅ Reasoning |

---

## The 90% That Matters: Engineer's Value

### 1. **Validation → Is it actually correct?**

**AI writes code. You ensure it does what it's supposed to do.**

#### Scenario: The Refund Calculator

**Requirement:** "Calculate refund amount based on return date"

**AI Code:**
```python
def calculate_refund(purchase_price, days_since_purchase):
    if days_since_purchase <= 30:
        return purchase_price
    elif days_since_purchase <= 60:
        return purchase_price * 0.5
    else:
        return 0
```

**Engineer's Questions:**
- ❓ What about partial returns (return 2 of 5 items)?
- ❓ What if item was on sale? Refund sale price or original price?
- ❓ What about shipping costs? Refunded or not?
- ❓ What about store credit vs. original payment method?
- ❓ What about restocking fees?
- ❓ What about damaged items vs. unwanted items?
- ❓ Does "30 days" mean calendar days or business days?
- ❓ What timezone? Order placed at 11:59 PM vs. 12:01 AM matters!

**Engineer's Correct Version:**
```python
from decimal import Decimal
from datetime import datetime
from enum import Enum
from typing import Optional

class RefundType(Enum):
    FULL_REFUND = "full_refund"
    PARTIAL_REFUND = "partial_refund"
    STORE_CREDIT = "store_credit"
    NO_REFUND = "no_refund"

class ItemCondition(Enum):
    NEW_UNOPENED = "new_unopened"
    OPENED_UNUSED = "opened_unused"
    USED = "used"
    DAMAGED_BY_CUSTOMER = "damaged_by_customer"
    DAMAGED_BY_SHIPPING = "damaged_by_shipping"

def calculate_refund(
    purchase_price: Decimal,
    original_price: Decimal,
    purchase_date: datetime,
    return_date: datetime,
    item_condition: ItemCondition,
    quantity_purchased: int,
    quantity_returned: int,
    shipping_cost: Decimal,
    is_final_sale: bool = False
) -> dict:
    """
    Calculate refund based on company's return policy.
    
    Business Rules (from legal/finance):
    - Within 30 days: Full refund if NEW_UNOPENED
    - Within 30 days: 80% refund if OPENED_UNUSED
    - Within 60 days: Store credit only, 50% of purchase price
    - After 60 days: No refund
    - Final sale items: No refund ever
    - Damaged by shipping: Full refund + shipping anytime
    - Shipping costs: Refunded only if item defective
    - Restocking fee: 15% for opened items
    - Partial returns: Pro-rated amount
    
    Returns:
        {
            'refund_type': RefundType,
            'amount': Decimal,
            'store_credit': Decimal,
            'shipping_refund': Decimal,
            'restocking_fee': Decimal,
            'reason': str
        }
    """
    result = {
        'refund_type': RefundType.NO_REFUND,
        'amount': Decimal('0'),
        'store_credit': Decimal('0'),
        'shipping_refund': Decimal('0'),
        'restocking_fee': Decimal('0'),
        'reason': ''
    }
    
    # Calculate business days between purchase and return
    days_elapsed = (return_date - purchase_date).days
    
    # Pro-rate for partial returns
    unit_price = purchase_price / quantity_purchased
    return_amount = unit_price * quantity_returned
    
    # Special case: Damaged by shipping (full refund anytime)
    if item_condition == ItemCondition.DAMAGED_BY_SHIPPING:
        result['refund_type'] = RefundType.FULL_REFUND
        result['amount'] = return_amount
        result['shipping_refund'] = shipping_cost
        result['reason'] = "Damaged in shipping - full refund + shipping"
        return result
    
    # Final sale items are not returnable
    if is_final_sale:
        result['reason'] = "Final sale items are not returnable"
        return result
    
    # Damaged by customer: No refund
    if item_condition == ItemCondition.DAMAGED_BY_CUSTOMER:
        result['reason'] = "Customer-damaged items are not refundable"
        return result
    
    # Within 30 days
    if days_elapsed <= 30:
        if item_condition == ItemCondition.NEW_UNOPENED:
            result['refund_type'] = RefundType.FULL_REFUND
            result['amount'] = return_amount
            result['shipping_refund'] = shipping_cost  # Refund shipping
            result['reason'] = "30-day return: New/unopened - full refund"
        elif item_condition == ItemCondition.OPENED_UNUSED:
            restocking_fee = return_amount * Decimal('0.15')
            result['refund_type'] = RefundType.PARTIAL_REFUND
            result['amount'] = return_amount - restocking_fee
            result['restocking_fee'] = restocking_fee
            result['reason'] = "30-day return: Opened/unused - 15% restocking fee"
        else:  # USED
            result['refund_type'] = RefundType.STORE_CREDIT
            result['store_credit'] = return_amount * Decimal('0.5')
            result['reason'] = "30-day return: Used item - 50% store credit"
    
    # Between 31-60 days
    elif days_elapsed <= 60:
        result['refund_type'] = RefundType.STORE_CREDIT
        result['store_credit'] = return_amount * Decimal('0.5')
        result['reason'] = "31-60 day return: Store credit only (50%)"
    
    # After 60 days
    else:
        result['reason'] = "Returns not accepted after 60 days"
    
    return result
```

**Engineer Validated:**
- ✅ Against actual business policy (checked with legal/finance)
- ✅ All edge cases documented
- ✅ Returns structured data for audit trail
- ✅ Business days vs calendar days clarified
- ✅ Partial returns handled
- ✅ Multiple refund types supported
- ✅ Reason field for customer service

---

### 2. **Edge Cases → What about NULLs, empties?**

**AI writes for the happy path. You handle reality.**

#### Common Edge Cases AI Misses

```python
# AI's version
def get_user_full_name(user):
    return f"{user['first_name']} {user['last_name']}"

# What could go wrong?
get_user_full_name(None)  # AttributeError
get_user_full_name({})  # KeyError
get_user_full_name({'first_name': 'John'})  # KeyError
get_user_full_name({'first_name': None, 'last_name': 'Doe'})  # "None Doe"
get_user_full_name({'first_name': '', 'last_name': 'Doe'})  # " Doe"
get_user_full_name({'first_name': 'Prince', 'last_name': ''})  # "Prince "
get_user_full_name({'first_name': 'Cher', 'last_name': None})  # "Cher None"
```

**Engineer's version handles everything:**
```python
from typing import Optional, Dict, Any

def get_user_full_name(
    user: Optional[Dict[str, Any]],
    default: str = "Unknown User"
) -> str:
    """
    Safely construct user's full name from dict.
    
    Handles:
    - None user
    - Missing keys
    - None values
    - Empty strings
    - Single name (like "Prince" or "Cher")
    
    Args:
        user: User dict with 'first_name' and 'last_name' keys
        default: Value to return if name cannot be constructed
    
    Returns:
        Full name as string, or default if unavailable
    
    Examples:
        >>> get_user_full_name({'first_name': 'John', 'last_name': 'Doe'})
        'John Doe'
        >>> get_user_full_name({'first_name': 'Prince', 'last_name': None})
        'Prince'
        >>> get_user_full_name(None)
        'Unknown User'
    """
    if user is None:
        return default
    
    first = user.get('first_name', '').strip() if user.get('first_name') else ''
    last = user.get('last_name', '').strip() if user.get('last_name') else ''
    
    # Handle all combinations
    if first and last:
        return f"{first} {last}"
    elif first:
        return first
    elif last:
        return last
    else:
        return default

# Comprehensive tests
def test_edge_cases():
    assert get_user_full_name(None) == "Unknown User"
    assert get_user_full_name({}) == "Unknown User"
    assert get_user_full_name({'first_name': 'John'}) == "John"
    assert get_user_full_name({'last_name': 'Doe'}) == "Doe"
    assert get_user_full_name({'first_name': None, 'last_name': 'Doe'}) == "Doe"
    assert get_user_full_name({'first_name': '', 'last_name': 'Doe'}) == "Doe"
    assert get_user_full_name({'first_name': 'John', 'last_name': ''}) == "John"
    assert get_user_full_name({'first_name': '  ', 'last_name': '  '}) == "Unknown User"
    assert get_user_full_name({'first_name': 'Prince', 'last_name': None}) == "Prince"
```

**Real Production Edge Cases:**
- Empty datasets
- Null values
- Incorrect types
- Missing keys
- Negative numbers where positive expected
- Division by zero
- Array out of bounds
- Concurrent modifications
- Network timeouts
- Disk full
- Memory exhausted

---

### 3. **Optimize → Will it work at scale?**

**AI writes code that works for 10 rows. You make it work for 10 million.**

#### Scenario: Product Search

**AI's Version (works for demo):**
```python
def search_products(search_term):
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM products")
    all_products = cursor.fetchall()
    
    results = []
    for product in all_products:
        if search_term.lower() in product['name'].lower():
            results.append(product)
    
    return results
```

**Problems at scale:**
- ❌ Loads entire table (10M products = OOM)
- ❌ No database indexing used
- ❌ Case-insensitive search done in Python (slow)
- ❌ No pagination
- ❌ No result limit
- ❌ Full table scan on every search

**Engineer's Optimized Version:**
```python
from typing import List, Dict
from sqlalchemy import func, or_
from sqlalchemy.orm import Session
from .models import Product

def search_products(
    db: Session,
    search_term: str,
    page: int = 1,
    page_size: int = 20,
    include_inactive: bool = False
) -> Dict:
    """
    Search products efficiently using database indexes and pagination.
    
    Performance optimizations:
    - Full-text search index on name, description
    - Case-insensitive search at DB level
    - Pagination to limit memory
    - Count query optimization
    - Only active products by default
    
    Args:
        db: Database session
        search_term: Term to search for
        page: Page number (1-indexed)
        page_size: Results per page
        include_inactive: Include inactive products
    
    Returns:
        {
            'products': List of product dicts,
            'total': Total matching products,
            'page': Current page,
            'pages': Total pages
        }
    """
    # Validate inputs
    if page < 1:
        page = 1
    if page_size < 1 or page_size > 100:
        page_size = 20
    
    # Build base query
    query = db.query(Product)
    
    # Filter active products (indexed column)
    if not include_inactive:
        query = query.filter(Product.is_active == True)
    
    # Full-text search (uses GIN index in PostgreSQL)
    search_term = search_term.strip()
    if search_term:
        # Use database's full-text search (much faster)
        query = query.filter(
            or_(
                Product.name_tsvector.match(search_term),  # Full-text indexed column
                Product.description_tsvector.match(search_term)
            )
        )
    
    # Get total count (optimized - doesn't fetch rows)
    total = query.count()
    
    # Calculate pagination
    total_pages = (total + page_size - 1) // page_size
    offset = (page - 1) * page_size
    
    # Fetch only requested page (limit memory usage)
    products = query.offset(offset).limit(page_size).all()
    
    return {
        'products': [p.to_dict() for p in products],
        'total': total,
        'page': page,
        'pages': total_pages,
        'page_size': page_size
    }

# Database migration to add full-text search
"""
CREATE INDEX idx_product_name_fts ON products 
USING GIN (to_tsvector('english', name));

CREATE INDEX idx_product_description_fts ON products 
USING GIN (to_tsvector('english', description));

CREATE INDEX idx_product_active ON products (is_active);
"""
```

**Performance comparison:**
- Naïve: 10M products = 30 seconds, 5GB RAM
- Optimized: 10M products = 50ms, 10MB RAM

---

### 4. **Read → Do I understand every line?**

**AI can write code you don't understand. That's dangerous.**

#### Scenario: Mysterious Code

**AI generates this:**
```python
def process_data(data):
    return [x for x in (y.strip() for y in data) if x]
```

**Questions:**
- ❓ What does this actually do?
- ❓ What if `data` is None?
- ❓ What if `data` contains non-string elements?
- ❓ Why nested comprehensions?
- ❓ Is `strip()` always appropriate?
- ❓ What's the performance for large datasets?

**Engineer's version (readable):**
```python
from typing import List, Optional

def remove_empty_strings(strings: List[str]) -> List[str]:
    """
    Remove empty and whitespace-only strings from list.
    
    Args:
        strings: List of strings to filter
    
    Returns:
        List with empty/whitespace strings removed
    
    Example:
        >>> remove_empty_strings(['hello', '', '  ', 'world'])
        ['hello', 'world']
    """
    if strings is None:
        return []
    
    result = []
    for string in strings:
        if not isinstance(string, str):
            continue  # Skip non-string elements
        
        trimmed = string.strip()
        if trimmed:  # Not empty after stripping
            result.append(trimmed)
    
    return result
```

**Now it's clear:**
- ✅ Name explains what it does
- ✅ Type hints show expected input/output
- ✅ Docstring with example
- ✅ Handles None input
- ✅ Handles non-string elements
- ✅ Easy to debug
- ✅ Easy to modify later

---

### 5. **Prompt → Did I ask the right question?**

**The quality of AI output depends on the quality of your prompt.**

#### Bad Prompt → Bad Code

**Vague Prompt:**
```
"Write a function to process orders"
```

**AI Output:**
```python
def process_orders(orders):
    for order in orders:
        print(f"Processing order {order['id']}")
```

**This tells you nothing!**

#### Good Prompt → Better Code

**Specific Prompt:**
```
"Write a function that:
1. Validates order data (check required fields: customer_id, items, total)
2. Calculates tax based on customer's state
3. Applies any discount codes
4. Saves to PostgreSQL using SQLAlchemy
5. Sends confirmation email
6. Returns order ID or raises specific exceptions for each failure type

Use type hints, handle edge cases (empty items list, invalid discount codes, negative totals), and include docstring."
```

**AI Output will be much better!**

**But Engineer Still Needs To:**
- Review the logic
- Add the edge cases AI still missed
- Ensure error handling is comprehensive
- Verify against business rules
- Add logging and monitoring
- Write tests

---

### 6. **Debug → Why did it fail at 3 AM?**

**AI can't debug production issues. You can.**

#### Scenario: The 3 AM Page

**The Alert:**
```
CRITICAL: OrderProcessingService failed
Error: NoneType object has no attribute 'calculate_total'
Occurred at: 2026-02-11 03:17:42 UTC
```

**AI's "help":**
```python
# AI suggests: "Add a null check"
if order is not None:
    total = order.calculate_total()
```

**But Engineer Asks:**
- ❓ WHY is order None? It shouldn't be.
- ❓ When did this start happening?
- ❓ Is it all orders or specific ones?
- ❓ What changed recently?
- ❓ Is there a pattern (time of day, customer type)?

**Engineer's Investigation:**
```python
# 1. Check logs for context
import logging
logger = logging.getLogger(__name__)

def process_order(order_id: str):
    logger.info(f"Processing order {order_id}")
    
    try:
        order = Order.get_by_id(order_id)
        
        if order is None:
            # Log WHY, not just THAT
            logger.error(
                f"Order {order_id} not found in database. "
                f"Possible causes: "
                f"1. Invalid ID from upstream "
                f"2. Race condition (deleted after queued) "
                f"3. Database replication lag"
            )
            # Alert specific team
            alert_ops_team(f"Order {order_id} not found")
            return
        
        # Continue processing
        total = order.calculate_total()
        ...
        
    except Exception as e:
        logger.exception(
            f"Unexpected error processing order {order_id}: {e}"
        )
        raise

# 2. Check database
"""
SELECT created_at, order_id 
FROM order_processing_queue 
WHERE order_id NOT IN (SELECT order_id FROM orders)
LIMIT 100;
"""

# 3. Check recent deployments
# 4. Check database replication lag
# 5. Check upstream service (is it sending invalid IDs?)
```

**Engineer Finds:**
- New upstream service has a bug
- Sends order IDs that don't exist yet
- Timing issue: order queued before it's saved
- Fix: Add retry logic with exponential backoff

**AI can't do this investigative work.**

---

### 7. **Maintain → Can I safely change it later?**

**AI writes code for now. You write code for 5 years from now.**

#### Scenario: The Unmaintainable Function

**AI's version:**
```python
def f(d, t):
    r = []
    for i in d:
        if i['s'] == t:
            r.append({'n': i['n'], 'p': i['p'] * 0.9 if i['c'] == 'VIP' else i['p']})
    return r
```

**6 months later:**
- ❓ What does this do?
- ❓ What's 's'? What's 't'?
- ❓ Why 0.9?
- ❓ Who calls this?
- ❓ Can I change it without breaking things?

**Engineer's maintainable version:**
```python
from typing import List, Dict
from decimal import Decimal
from enum import Enum

class CustomerType(Enum):
    """Customer classification for pricing."""
    VIP = "VIP"
    REGULAR = "REGULAR"

class ProductStatus(Enum):
    """Product availability status."""
    IN_STOCK = "in_stock"
    OUT_OF_STOCK = "out_of_stock"
    DISCONTINUED = "discontinued"

VIP_DISCOUNT_RATE = Decimal('0.10')  # 10% discount for VIP customers

def filter_products_by_status(
    products: List[Dict],
    status: ProductStatus
) -> List[Dict]:
    """
    Filter products by status and apply customer-specific pricing.
    
    Business Rules:
    - VIP customers get 10% discount on all products
    - Regular customers pay full price
    - Discount rate defined in VIP_DISCOUNT_RATE constant
    
    Args:
        products: List of product dicts with keys:
            - name: Product name
            - price: Base price (Decimal)
            - status: ProductStatus enum
            - customer_type: CustomerType enum
        status: Filter by this status
    
    Returns:
        List of dicts with 'name' and 'price' (after any discounts)
    
    Example:
        >>> products = [
        ...     {'name': 'Widget', 'price': Decimal('100'), 
        ...      'status': ProductStatus.IN_STOCK, 'customer_type': CustomerType.VIP}
        ... ]
        >>> result = filter_products_by_status(products, ProductStatus.IN_STOCK)
        >>> result[0]['price']
        Decimal('90.00')  # 10% VIP discount applied
    
    Note:
        If discount logic changes, update VIP_DISCOUNT_RATE constant.
        For more complex pricing, consider moving to PricingService.
    """
    filtered_products = []
    
    for product in products:
        if product['status'] != status:
            continue
        
        base_price = product['price']
        
        # Apply VIP discount if applicable
        if product['customer_type'] == CustomerType.VIP:
            discounted_price = base_price * (Decimal('1') - VIP_DISCOUNT_RATE)
        else:
            discounted_price = base_price
        
        filtered_products.append({
            'name': product['name'],
            'price': discounted_price
        })
    
    return filtered_products
```

**What makes it maintainable:**
1. ✅ Descriptive names (not single letters)
2. ✅ Type hints (know what goes in/out)
3. ✅ Enums (prevent typos, IDE autocomplete)
4. ✅ Constants (easy to change discount rate)
5. ✅ Comments explaining business rules
6. ✅ Docstring with examples
7. ✅ Note about future refactoring
8. ✅ Decimal for money (not float)

---

### 8. **Clarity → Can my team understand it?**

**Code is read 10x more than it's written. Optimize for readers.**

#### Scenario: The Clever Code

**AI's "clever" version:**
```python
def process(data):
    return {k: v for k, v in zip([x['id'] for x in data], 
            [sum(y['val'] for y in x['items']) for x in data])}
```

**This is hard to read!**

**Engineer's clear version:**
```python
from typing import List, Dict

def calculate_order_totals(orders: List[Dict]) -> Dict[str, float]:
    """
    Calculate total value for each order.
    
    Args:
        orders: List of order dicts, each containing:
            - id: Unique order identifier
            - items: List of item dicts with 'val' (value) field
    
    Returns:
        Dict mapping order_id -> total_value
    
    Example:
        >>> orders = [
        ...     {'id': 'A', 'items': [{'val': 10}, {'val': 20}]},
        ...     {'id': 'B', 'items': [{'val': 15}]}
        ... ]
        >>> calculate_order_totals(orders)
        {'A': 30.0, 'B': 15.0}
    """
    order_totals = {}
    
    for order in orders:
        order_id = order['id']
        
        # Sum all item values for this order
        total_value = sum(item['val'] for item in order['items'])
        
        order_totals[order_id] = total_value
    
    return order_totals
```

**Why clearer:**
- Name explains what it does
- Intermediate variables with meaningful names
- Comments for non-obvious parts
- Example in docstring
- New team member can understand in 30 seconds

---

### 9. **Correctness → Does business agree?**

**AI doesn't know your business rules. You do.**

#### Scenario: The Shipping Calculator

**Requirements from Product Manager:**
```
"Free shipping on orders over $50"
```

**AI's interpretation:**
```python
def calculate_shipping(order_total):
    if order_total > 50:
        return 0
    else:
        return 5.99
```

**Engineer's Questions to PM:**
- ❓ Is it ">$50" or ">=$50"? ($50.00 exact is free or not?)
- ❓ Before or after tax?
- ❓ Before or after discounts?
- ❓ Does it apply to international orders?
- ❓ What about Alaska/Hawaii? (often higher shipping)
- ❓ What about P.O. boxes?
- ❓ Does it stack with "Free Shipping Friday" promotions?
- ❓ What about orders with multiple shipments?
- ❓ What if cart has gift cards (non-shippable)?

**PM's Clarification:**
```
"Free shipping when:
- Subtotal (before tax, after discounts) >= $50
- Continental US only
- Excludes gift cards from subtotal
- Excludes oversized items (>50 lbs)
- Not combinable with promo code 'FREESHIP'
- No free shipping to P.O. boxes
```

**Engineer's Correct Implementation:**
```python
from decimal import Decimal
from typing import List, Dict, Optional

def calculate_shipping_cost(
    items: List[Dict],
    destination_state: str,
    destination_zip: str,
    is_po_box: bool,
    applied_promo_code: Optional[str] = None
) -> Decimal:
    """
    Calculate shipping cost based on company policy.
    
    Free Shipping Criteria (ALL must be true):
    1. Subtotal >= $50 (excludes gift cards)
    2. No oversized items (>50 lbs)
    3. Continental US destination
    4. Not a P.O. box
    5. Promo code is not 'FREESHIP'
    
    Otherwise:
    - Standard shipping: $5.99
    - Alaska/Hawaii: $15.99
    - Oversized item: +$25.00
    
    Args:
        items: List of items with price, weight, type
        destination_state: Two-letter state code
        destination_zip: ZIP code
        is_po_box: Whether destination is P.O. box
        applied_promo_code: Any promo code being used
    
    Returns:
        Shipping cost as Decimal
    """
    STANDARD_SHIPPING = Decimal('5.99')
    HAWAII_ALASKA_SHIPPING = Decimal('15.99')
    OVERSIZED_FEE = Decimal('25.00')
    FREE_SHIPPING_THRESHOLD = Decimal('50.00')
    OVERSIZED_WEIGHT_LBS = 50
    
    CONTINENTAL_US_STATES = {
        'AL', 'AZ', 'AR', 'CA', 'CO', 'CT', 'DE', 'FL', 'GA',
        'ID', 'IL', 'IN', 'IA', 'KS', 'KY', 'LA', 'ME', 'MD',
        'MA', 'MI', 'MN', 'MS', 'MO', 'MT', 'NE', 'NV', 'NH',
        'NJ', 'NM', 'NY', 'NC', 'ND', 'OH', 'OK', 'OR', 'PA',
        'RI', 'SC', 'SD', 'TN', 'TX', 'UT', 'VT', 'VA', 'WA',
        'WV', 'WI', 'WY', 'DC'
    }
    
    # Calculate subtotal (exclude gift cards)
    subtotal = Decimal('0')
    has_oversized = False
    
    for item in items:
        if item['type'] != 'gift_card':
            subtotal += item['price']
        
        if item.get('weight_lbs', 0) > OVERSIZED_WEIGHT_LBS:
            has_oversized = True
    
    # Check free shipping eligibility
    is_continental_us = destination_state in CONTINENTAL_US_STATES
    is_freeship_promo = (applied_promo_code == 'FREESHIP')
    
    qualifies_for_free_shipping = (
        subtotal >= FREE_SHIPPING_THRESHOLD
        and not has_oversized
        and is_continental_us
        and not is_po_box
        and not is_freeship_promo
    )
    
    if qualifies_for_free_shipping:
        return Decimal('0.00')
    
    # Calculate shipping cost
    if destination_state in ['AK', 'HI']:
        base_cost = HAWAII_ALASKA_SHIPPING
    else:
        base_cost = STANDARD_SHIPPING
    
    if has_oversized:
        base_cost += OVERSIZED_FEE
    
    return base_cost

# Comprehensive tests
def test_free_shipping():
    items = [{'price': Decimal('60'), 'type': 'product', 'weight_lbs': 10}]
    cost = calculate_shipping_cost(items, 'CA', '94102', False, None)
    assert cost == Decimal('0.00')

def test_no_free_shipping_po_box():
    items = [{'price': Decimal('60'), 'type': 'product', 'weight_lbs': 10}]
    cost = calculate_shipping_cost(items, 'CA', '94102', True, None)
    assert cost == Decimal('5.99')

def test_no_free_shipping_with_freeship_promo():
    items = [{'price': Decimal('60'), 'type': 'product', 'weight_lbs': 10}]
    cost = calculate_shipping_cost(items, 'CA', '94102', False, 'FREESHIP')
    assert cost == Decimal('5.99')
```

**Engineer Ensured:**
- ✅ Exact business rules from PM
- ✅ All edge cases documented
- ✅ Constants for easy updates
- ✅ Tests for each rule
- ✅ Code matches what business expects

---

## Real Production Scenarios

### Scenario 1: The Data Migration

**Context:** Migrating 50M user records from old system to new system.

**AI's Approach:**
```python
# "Migrate all users from old database to new database"
old_users = old_db.query("SELECT * FROM users").fetchall()
for user in old_users:
    new_db.execute(
        "INSERT INTO users VALUES (?, ?, ?)",
        (user.id, user.name, user.email)
    )
```

**What AI Missed:**
- ❌ Loads 50M records into memory (crash)
- ❌ No error handling (one bad record kills everything)
- ❌ No progress tracking (is it stuck or working?)
- ❌ No data validation
- ❌ No duplicate checking
- ❌ Single-threaded (takes days)
- ❌ No rollback plan
- ❌ No audit trail

**Engineer's Production Migration:**
```python
import logging
from typing import Generator
from datetime import datetime
from concurrent.futures import ThreadPoolExecutor, as_completed
import time

logger = logging.getLogger(__name__)

BATCH_SIZE = 1000
MAX_WORKERS = 10
MAX_RETRIES = 3

class MigrationStats:
    def __init__(self):
        self.total = 0
        self.migrated = 0
        self.failed = 0
        self.duplicates = 0
        self.start_time = datetime.now()
    
    def elapsed_minutes(self):
        return (datetime.now() - self.start_time).total_seconds() / 60
    
    def rate_per_minute(self):
        minutes = self.elapsed_minutes()
        return self.migrated / minutes if minutes > 0 else 0
    
    def estimated_remaining_minutes(self):
        rate = self.rate_per_minute()
        remaining = self.total - self.migrated
        return remaining / rate if rate > 0 else 0

def fetch_old_users_batch(offset: int, limit: int) -> list:
    """Fetch users from old database in batches."""
    return old_db.execute(
        "SELECT id, name, email, created_at FROM users "
        "ORDER BY id LIMIT ? OFFSET ?",
        (limit, offset)
    ).fetchall()

def validate_user(user: dict) -> tuple[bool, str]:
    """Validate user data before migration."""
    if not user.get('email'):
        return False, "Missing email"
    
    if not user.get('name'):
        return False, "Missing name"
    
    # Email format validation
    if '@' not in user['email']:
        return False, "Invalid email format"
    
    return True, ""

def migrate_user_batch(users: list, stats: MigrationStats) -> None:
    """Migrate a batch of users with error handling."""
    for user in users:
        retry_count = 0
        migrated = False
        
        while retry_count < MAX_RETRIES and not migrated:
            try:
                # Validate
                valid, error = validate_user(user)
                if not valid:
                    logger.warning(f"Invalid user {user['id']}: {error}")
                    stats.failed += 1
                    break
                
                # Check for duplicate (use unique index on email)
                existing = new_db.execute(
                    "SELECT id FROM users WHERE email = ?",
                    (user['email'],)
                ).fetchone()
                
                if existing:
                    logger.info(f"Duplicate user: {user['email']}")
                    stats.duplicates += 1
                    migrated = True
                    break
                
                # Insert into new database
                new_db.execute(
                    "INSERT INTO users (id, name, email, created_at, migrated_at) "
                    "VALUES (?, ?, ?, ?, ?)",
                    (user['id'], user['name'], user['email'], 
                     user['created_at'], datetime.now())
                )
                new_db.commit()
                
                # Log to audit trail
                audit_db.execute(
                    "INSERT INTO migration_audit (old_id, new_id, migrated_at) "
                    "VALUES (?, ?, ?)",
                    (user['id'], user['id'], datetime.now())
                )
                audit_db.commit()
                
                stats.migrated += 1
                migrated = True
                
            except Exception as e:
                retry_count += 1
                logger.error(
                    f"Error migrating user {user['id']} "
                    f"(attempt {retry_count}/{MAX_RETRIES}): {e}"
                )
                
                if retry_count < MAX_RETRIES:
                    time.sleep(2 ** retry_count)  # Exponential backoff
                else:
                    stats.failed += 1
                    # Log to failed_migrations table for manual review
                    audit_db.execute(
                        "INSERT INTO failed_migrations (user_id, error, failed_at) "
                        "VALUES (?, ?, ?)",
                        (user['id'], str(e), datetime.now())
                    )
                    audit_db.commit()

def migrate_all_users():
    """
    Migrate all users with:
    - Batch processing (memory efficient)
    - Parallel execution (faster)
    - Error handling and retries
    - Progress tracking
    - Audit logging
    - Rollback support
    """
    # Get total count
    stats = MigrationStats()
    stats.total = old_db.execute("SELECT COUNT(*) FROM users").fetchone()[0]
    
    logger.info(f"Starting migration of {stats.total} users")
    
    # Create checkpoint file (for resume capability)
    checkpoint_file = "migration_checkpoint.txt"
    start_offset = 0
    
    if os.path.exists(checkpoint_file):
        with open(checkpoint_file, 'r') as f:
            start_offset = int(f.read())
        logger.info(f"Resuming from offset {start_offset}")
    
    # Process in batches with parallel workers
    with ThreadPoolExecutor(max_workers=MAX_WORKERS) as executor:
        futures = []
        
        for offset in range(start_offset, stats.total, BATCH_SIZE):
            users = fetch_old_users_batch(offset, BATCH_SIZE)
            
            if not users:
                break
            
            future = executor.submit(migrate_user_batch, users, stats)
            futures.append(future)
            
            # Log progress every 10 batches
            if (offset // BATCH_SIZE) % 10 == 0:
                logger.info(
                    f"Progress: {stats.migrated}/{stats.total} users "
                    f"({stats.migrated/stats.total*100:.1f}%) | "
                    f"Rate: {stats.rate_per_minute():.0f}/min | "
                    f"ETA: {stats.estimated_remaining_minutes():.0f} min | "
                    f"Failed: {stats.failed} | "
                    f"Duplicates: {stats.duplicates}"
                )
                
                # Update checkpoint
                with open(checkpoint_file, 'w') as f:
                    f.write(str(offset))
        
        # Wait for all futures to complete
        for future in as_completed(futures):
            future.result()
    
    # Final summary
    logger.info(
        f"Migration complete! "
        f"Migrated: {stats.migrated} | "
        f"Failed: {stats.failed} | "
        f"Duplicates: {stats.duplicates} | "
        f"Time: {stats.elapsed_minutes():.0f} minutes"
    )
    
    # Clean up checkpoint file
    if os.path.exists(checkpoint_file):
        os.remove(checkpoint_file)
    
    # Verify migration
    verify_migration(stats)

def verify_migration(stats: MigrationStats):
    """Post-migration verification."""
    logger.info("Verifying migration...")
    
    old_count = old_db.execute("SELECT COUNT(*) FROM users").fetchone()[0]
    new_count = new_db.execute("SELECT COUNT(*) FROM users").fetchone()[0]
    
    logger.info(f"Old DB: {old_count} users")
    logger.info(f"New DB: {new_count} users")
    logger.info(f"Expected: {stats.migrated} (excluding duplicates)")
    
    if new_count == stats.migrated:
        logger.info("✅ Migration verified successfully")
    else:
        logger.error(f"⚠️ Count mismatch! Investigate failed_migrations table")

if __name__ == "__main__":
    migrate_all_users()
```

**Engineer's Production Features:**
1. ✅ Batch processing (memory efficient)
2. ✅ Parallel execution (10x faster)
3. ✅ Error handling with retries
4. ✅ Progress tracking and ETA
5. ✅ Checkpointing (resume on failure)
6. ✅ Data validation
7. ✅ Duplicate detection
8. ✅ Audit trail
9. ✅ Failed records logged for manual review
10. ✅ Post-migration verification
11. ✅ Comprehensive logging

---

### Scenario 2: The Payment Processing Bug

**Incident:** Customers charged twice for some orders.

**AI's "fix":**
```python
# "Prevent duplicate charges"
if not already_charged(order_id):
    charge_customer(order_id)
```

**Engineer's Investigation:**

1. **Root Cause Analysis:**
```python
# Check logs
"""
2026-02-11 03:17:42 - Order #12345 charged $99.99
2026-02-11 03:17:43 - Order #12345 charged $99.99  # DUPLICATE!
"""

# Check timing
# → Charges 1 second apart
# → Race condition when user clicks "Pay" twice
# → Button not disabled during processing
```

2. **The Real Fix:**
```python
from sqlalchemy import Column, String, DateTime, UniqueConstraint
from datetime import datetime
import logging

class PaymentAttempt(Base):
    """Track payment attempts with idempotency."""
    __tablename__ = 'payment_attempts'
    
    id = Column(String, primary_key=True)
    order_id = Column(String, nullable=False)
    idempotency_key = Column(String, nullable=False, unique=True)
    amount = Column(Decimal, nullable=False)
    status = Column(String, nullable=False)  # pending, success, failed
    created_at = Column(DateTime, nullable=False)
    
    __table_args__ = (
        UniqueConstraint('order_id', 'idempotency_key', name='uq_order_idempotency'),
    )

def process_payment(
    order_id: str,
    amount: Decimal,
    idempotency_key: str,
    payment_method: str
) -> dict:
    """
    Process payment with idempotency to prevent duplicate charges.
    
    Idempotency ensures:
    - Same request (same idempotency_key) always returns same result
    - No duplicate charges even if API called twice
    - Safe retries on network failures
    
    Args:
        order_id: Order identifier
        amount: Amount to charge
        idempotency_key: Unique key for this payment attempt (UUID from client)
        payment_method: Payment method token
    
    Returns:
        {
            'status': 'success' | 'already_processed' | 'failed',
            'transaction_id': str,
            'message': str
        }
    """
    # Check if already processed (database unique constraint ensures atomicity)
    try:
        existing = db.query(PaymentAttempt).filter(
            PaymentAttempt.idempotency_key == idempotency_key
        ).first()
        
        if existing:
            logger.info(
                f"Payment already processed: {idempotency_key} "
                f"(Status: {existing.status})"
            )
            return {
                'status': 'already_processed',
                'transaction_id': existing.id,
                'message': 'This payment was already processed'
            }
        
        # Create payment attempt record (locks this idempotency_key)
        attempt = PaymentAttempt(
            id=generate_uuid(),
            order_id=order_id,
            idempotency_key=idempotency_key,
            amount=amount,
            status='pending',
            created_at=datetime.utcnow()
        )
        db.add(attempt)
        db.commit()  # Commit immediately to acquire lock
        
        # Process payment with payment provider
        try:
            result = payment_provider.charge(
                amount=amount,
                payment_method=payment_method,
                idempotency_key=idempotency_key  # Provider also supports idempotency
            )
            
            # Update status
            attempt.status = 'success'
            attempt.transaction_id = result['transaction_id']
            db.commit()
            
            logger.info(
                f"Payment successful: Order {order_id}, "
                f"Transaction {result['transaction_id']}"
            )
            
            return {
                'status': 'success',
                'transaction_id': result['transaction_id'],
                'message': 'Payment processed successfully'
            }
            
        except PaymentProviderError as e:
            # Payment failed
            attempt.status = 'failed'
            attempt.error_message = str(e)
            db.commit()
            
            logger.error(
                f"Payment failed: Order {order_id}, "
                f"Error: {e}"
            )
            
            return {
                'status': 'failed',
                'transaction_id': None,
                'message': f'Payment failed: {e}'
            }
    
    except IntegrityError as e:
        # Race condition: another request with same idempotency_key
        # This is expected and handled gracefully
        db.rollback()
        logger.info(
            f"Concurrent payment attempt detected: {idempotency_key}. "
            f"Waiting for first attempt to complete..."
        )
        
        # Wait and retry (first request will complete)
        time.sleep(0.5)
        return process_payment(order_id, amount, idempotency_key, payment_method)
```

3. **Frontend Fix (also important!):**
```javascript
// Prevent double-click
let paymentInProgress = false;

async function handlePayment() {
    if (paymentInProgress) {
        console.log("Payment already in progress");
        return;
    }
    
    paymentInProgress = true;
    
    // Disable button
    const payButton = document.getElementById('pay-button');
    payButton.disabled = true;
    payButton.textContent = 'Processing...';
    
    // Generate idempotency key (reuse on retry)
    const idempotencyKey = localStorage.getItem(`payment-key-${orderId}`) 
        || generateUUID();
    localStorage.setItem(`payment-key-${orderId}`, idempotencyKey);
    
    try {
        const result = await fetch('/api/payments', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Idempotency-Key': idempotencyKey
            },
            body: JSON.stringify({
                order_id: orderId,
                amount: amount,
                payment_method: paymentMethod
            })
        });
        
        const data = await result.json();
        
        if (data.status === 'success' || data.status === 'already_processed') {
            // Clear idempotency key (payment complete)
            localStorage.removeItem(`payment-key-${orderId}`);
            showSuccessMessage();
        } else {
            showErrorMessage(data.message);
            // Keep idempotency key for retry
        }
    } catch (error) {
        showErrorMessage('Network error. Please try again.');
        // Keep idempotency key for retry
    } finally {
        paymentInProgress = false;
        payButton.disabled = false;
        payButton.textContent = 'Pay Now';
    }
}
```

**What Engineer Did:**
1. ✅ Root cause analysis (not just symptom)
2. ✅ Database-level constraint (can't insert duplicate)
3. ✅ Idempotency pattern (safe retries)
4. ✅ Frontend safeguards (disable button)
5. ✅ Comprehensive logging
6. ✅ Grace handling race conditions
7. ✅ Works with payment provider's idempotency
8. ✅ Client-side idempotency key storage

---

## The Irreplaceable Human Skills

### 1. **Business Context**

**AI Doesn't Know:**
- Company strategy
- Customer pain points
- Regulatory requirements
- Budget constraints
- Political dynamics
- Legacy system quirks
- Why previous approaches failed

**Example:**
- AI suggests: "Use microservices"
- Engineer knows: "We have 3 devs, monolith is fine"

### 2. **System Thinking**

**AI Sees:** Individual functions  
**Engineer Sees:** Entire system

```
User Request
    ↓
Load Balancer (can it handle spike?)
    ↓
API Gateway (rate limits configured?)
    ↓
Auth Service (what if it's down?)
    ↓
Business Logic (is it idempotent?)
    ↓
Database (proper indexes?)
    ↓
Cache (invalidation strategy?)
    ↓
Queue (what if it backs up?)
    ↓
External API (retry policy?)
    ↓
Email Service (deliverability?)
```

### 3. **Trade-off Analysis**

**Every decision has trade-offs. Engineers evaluate them.**

Example: Caching Strategy

| Option | Pros | Cons | When to Use |
|--------|------|------|-------------|
| **No cache** | Simple, always fresh | Slow, expensive | Low traffic |
| **Client-side** | Fast, reduces load | Stale data risk | Static content |
| **CDN** | Global, fast | Expensive, invalidation delay | Images, assets |
| **Redis** | Flexible, fast | More complexity | Session data |
| **Database query cache** | Automatic | Limited control | Read-heavy queries |

**AI might suggest Redis for everything. Engineer chooses based on context.**

### 4. **Long-term Thinking**

**AI optimizes for now. Engineers plan for 5 years.**

Questions engineers ask:
- Will this scale to 100x traffic?
- Can junior devs maintain this?
- What if we need to migrate databases?
- How do we handle schema changes?
- What's the testing strategy?
- How do we monitor this?
- What's the disaster recovery plan?

### 5. **Communication**

**Engineering is a team sport.**

Engineers must:
- Explain technical decisions to non-technical stakeholders
- Write documentation future-you can understand
- Code review with empathy
- Mentor junior developers
- Translate business requirements to technical specs
- Present trade-offs to product managers
- Coordinate across teams

**AI can't attend meetings or build relationships.**

### 6. **Ethical Judgment**

**Engineers face ethical questions:**
- Should we build this feature? (Dark patterns)
- How do we handle user data? (Privacy)
- What bias might this algorithm have? (Fairness)
- Should we report this security issue? (Responsibility)
- Is this accessible? (Inclusion)

**AI has no moral compass.**

### 7. **Creative Problem-Solving**

**AI combines existing patterns. Engineers innovate.**

Example: Netflix's Chaos Monkey
- Problem: How to ensure systems are resilient?
- Standard approach: Better testing
- Creative solution: Randomly kill servers in production
- Result: Forces resilient design

**This required creative, counterintuitive thinking AI wouldn't generate.**

### 8. **Learning and Adaptation**

**Technology changes constantly. Engineers learn.**

- New frameworks
- New best practices
- New security vulnerabilities
- New regulations
- New customer needs

**Engineers read, experiment, adapt. AI is frozen at training time.**

---

## Future-Proofing Your Career

### Skills That Will Always Matter

1. **Problem Decomposition**
   - Breaking complex problems into solvable parts
   - AI: "Here's code"
   - You: "Here's the problem structure"

2. **Debugging**
   - Following cause-and-effect chains
   - AI: Can't access production systems
   - You: Can investigate, experiment, fix

3. **Architecture**
   - Designing systems that work at scale
   - AI: Generates components
   - You: Design how they fit together

4. **Communication**
   - Explaining to humans
   - AI: Generates documentation
   - You: Understand audience, adjust message

5. **Critical Thinking**
   - Evaluating solutions
   - AI: Proposes solutions
   - You: Judge if they're right

6. **Domain Expertise**
   - Understanding business/industry
   - AI: Generic knowledge
   - You: Company-specific insight

7. **Empathy**
   - Understanding user needs
   - AI: No feelings
   - You: Can imagine user frustration

8. **Judgment**
   - Knowing when to ship vs. perfect
   - AI: Doesn't understand deadlines
   - You: Balance quality vs. time

### How to Work With AI

**Don't fight AI. Use it as a tool.**

```
┌─────────────────────────────────────────┐
│  YOU are the architect                  │
│  AI is the assistant                    │
│                                         │
│  YOU decide WHAT to build               │
│  AI helps HOW to build it               │
│                                         │
│  YOU validate correctness               │
│  AI generates first draft               │
│                                         │
│  YOU handle edge cases                  │
│  AI handles happy path                  │
│                                         │
│  YOU ensure security                    │
│  AI writes boilerplate                  │
│                                         │
│  YOU optimize for scale                 │
│  AI writes naïve version                │
│                                         │
│  YOU understand business                │
│  AI doesn't know context                │
└─────────────────────────────────────────┘
```

### The New Engineering Workflow

**Old (Pre-AI):**
```
1. Understand requirements
2. Design solution
3. Write code (70% of time)
4. Test
5. Deploy
```

**New (With AI):**
```
1. Understand requirements (CRITICAL)
2. Design solution (CRITICAL)
3. Prompt AI for code (5% of time)
4. Validate AI output (NEW, CRITICAL)
5. Handle edge cases (CRITICAL)
6. Optimize (CRITICAL)
7. Add security (CRITICAL)
8. Write tests (CRITICAL)
9. Document (CRITICAL)
10. Deploy
```

**Writing code went from 70% → 5% of time.**  
**The other 90% became MORE important.**

---

## Conclusion: The Engineer's Irreplaceable Value

```
┌──────────────────────────────────────────────────┐
│                                                  │
│  AI is a TOOL                                    │
│  YOU are the CRAFTSPERSON                        │
│                                                  │
│  Hammers build houses,                          │
│  but architects design them.                    │
│                                                  │
│  AI writes code,                                │
│  but engineers build systems.                   │
│                                                  │
│  Your value isn't typing.                       │
│  Your value is THINKING.                        │
│                                                  │
└──────────────────────────────────────────────────┘
```

### What Makes You Irreplaceable

1. **Understanding the REAL problem** (not just symptoms)
2. **Knowing your system** (context AI lacks)
3. **Anticipating edge cases** (from experience)
4. **Ensuring security** (AI often generates vulnerabilities)
5. **Optimizing for scale** (AI writes naïve code)
6. **Debugging production issues** (requires investigation)
7. **Making architectural decisions** (requires judgment)
8. **Balancing trade-offs** (speed vs. quality vs. cost)
9. **Communicating with stakeholders** (technical translation)
10. **Maintaining code long-term** (readability, extensibility)

### The Bottom Line

**AI makes you MORE valuable, not less.**

Why?
- You can now focus on HIGH-VALUE work (thinking, designing, deciding)
- AI handles LOW-VALUE work (boilerplate, repetitive code)
- But AI needs YOUR expertise to:
  - Validate its output
  - Handle edge cases
  - Ensure security
  - Optimize performance
  - Make it production-ready

**The engineer who uses AI well is 10x more productive than one who doesn't.**  
**But the engineer who blindly trusts AI is a liability.**

---

## Your Action Items

1. ✅ **Use AI for boilerplate** (save time)
2. ✅ **ALWAYS validate AI output** (trust but verify)
3. ✅ **Add comprehensive error handling** (AI forgets edge cases)
4. ✅ **Write tests** (AI rarely does)
5. ✅ **Review for security** (AI often insecure)
6. ✅ **Optimize for production** (AI writes for demo)
7. ✅ **Document** (AI doesn't explain "why")
8. ✅ **Think about maintenance** (will you understand this in 6 months?)
9. ✅ **Consider your users** (AI doesn't have empathy)
10. ✅ **Keep learning** (stay ahead of AI's capabilities)

---

**Remember:**

```
AI writes code.
YOU build systems.

AI generates output.
YOU ensure it's correct.

AI knows patterns.
YOU understand problems.

AI is a tool.
YOU are the craftsperson.
```

**Your job is safe. Your value is growing. Your skills matter more than ever.**

**Just make sure you're using AI, not being replaced by it.**
