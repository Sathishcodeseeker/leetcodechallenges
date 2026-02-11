# Redis: Scenario-Based Learning for Agentic AI
## From Ground Up to Production AI Applications

---

## Table of Contents
1. [What is Redis? - Ground Up Explanation](#what-is-redis)
2. [Basic Understanding - Core Concepts](#basic-understanding)
3. [Why Redis in Agentic AI Applications](#why-redis-in-agentic-ai)
4. [Code-Based Explanations](#code-based-explanations)
5. [Real-World Scenarios](#real-world-scenarios)
6. [Production Patterns](#production-patterns)
7. [Interview Questions](#interview-questions)

---

## What is Redis? - Ground Up Explanation

### The Simplest Explanation

**Redis** = **RE**mote **DI**ctionary **S**erver

Imagine you have a Python dictionary:
```python
my_dict = {
    "user:1001": "John Doe",
    "counter": 42,
    "session:abc123": {"logged_in": True, "timestamp": 1234567890}
}
```

**Redis is like this dictionary, but:**
1. It lives in **memory** (RAM) instead of disk → super fast
2. It's **shared** across multiple applications/servers
3. It can **persist** data to disk (optional)
4. It's **networked** - accessible over TCP/IP

### Why Does It Exist?

**Problem Without Redis:**
```
Application Server 1: Has data in memory
Application Server 2: Doesn't know about Server 1's data
Application Server 3: Has different data

Result: No shared state, hard to coordinate
```

**Solution With Redis:**
```
Application Server 1  ──┐
Application Server 2  ──┼──> Redis (Shared Memory)
Application Server 3  ──┘

Result: All servers see the same data, instantly
```

---

## Basic Understanding - Core Concepts

### 1. In-Memory Database

**What it means:**
- Data stored in **RAM** (not hard disk)
- RAM is 100-1000x faster than disk
- Data is **volatile** (lost on restart) unless persisted

**Real-world analogy:**
```
Disk (SSD/HDD) = Library bookshelf
  - Large capacity
  - Slow to retrieve
  - Permanent storage

RAM (Redis) = Your desk
  - Limited space
  - Instant access
  - Cleared when you leave (unless you save to bookshelf)
```

**Speed comparison:**
```python
# Traditional database (PostgreSQL on disk)
response_time = 10-100 milliseconds

# Redis (in-memory)
response_time = 0.1-1 millisecond

# That's 100x faster!
```

---

### 2. Key-Value Store

**Concept:**
Every piece of data has a **key** (name) and a **value** (data).

```python
# Python dictionary (local)
cache = {
    "key": "value"
}

# Redis (remote, shared)
redis.set("key", "value")
value = redis.get("key")
```

**Key naming conventions:**
```python
# Good: Hierarchical, descriptive
"user:1001:profile"
"session:abc123:data"
"cache:product:5000"
"queue:email:pending"

# Bad: Unclear, collision-prone
"data"
"temp"
"x"
```

---

### 3. Data Structures in Redis

Redis isn't just key-value, it supports rich data types:

#### **a) Strings (Simple Values)**
```python
# Store simple values
redis.set("user:1001:name", "John Doe")
redis.set("counter", 0)
redis.incr("counter")  # Atomic increment → 1

# Use case: Counters, flags, simple cache
```

#### **b) Lists (Ordered Collections)**
```python
# Queue-like operations
redis.lpush("queue:tasks", "task1")  # Add to left
redis.lpush("queue:tasks", "task2")
redis.rpop("queue:tasks")  # Remove from right → "task1"

# Use case: Message queues, activity feeds, recent items
```

#### **c) Sets (Unique Collections)**
```python
# Unordered unique items
redis.sadd("tags:article:100", "python", "redis", "ai")
redis.smembers("tags:article:100")  # → {"python", "redis", "ai"}

# Set operations
redis.sadd("users:online", "user1", "user2")
redis.sadd("users:premium", "user2", "user3")
redis.sinter("users:online", "users:premium")  # → {"user2"}

# Use case: Tags, unique visitors, relationships
```

#### **d) Hashes (Object-like)**
```python
# Store objects with fields
redis.hset("user:1001", mapping={
    "name": "John Doe",
    "email": "john@example.com",
    "age": "30"
})
redis.hget("user:1001", "name")  # → "John Doe"
redis.hgetall("user:1001")  # → {name: "John Doe", email: ..., age: ...}

# Use case: User profiles, configuration objects
```

#### **e) Sorted Sets (Ranked Collections)**
```python
# Items with scores (for ranking)
redis.zadd("leaderboard", {"player1": 100, "player2": 150, "player3": 120})
redis.zrange("leaderboard", 0, -1, withscores=True)
# → [("player1", 100), ("player3", 120), ("player2", 150)]

redis.zrevrange("leaderboard", 0, 2)  # Top 3
# → ["player2", "player3", "player1"]

# Use case: Leaderboards, priority queues, time-series data
```

---

### 4. Expiration (TTL - Time To Live)

**Concept:** Data automatically disappears after a time period.

```python
# Set key with 60 second expiration
redis.setex("session:abc123", 60, "user_data")

# Or set expiration separately
redis.set("temp:result", "data")
redis.expire("temp:result", 300)  # 5 minutes

# Check time remaining
redis.ttl("session:abc123")  # → 45 (seconds left)

# Use case: Sessions, temporary cache, rate limiting
```

---

### 5. Pub/Sub (Publish-Subscribe)

**Concept:** Message broadcasting system.

```python
# Publisher (sends messages)
redis.publish("notifications", "New order received!")

# Subscriber (receives messages)
pubsub = redis.pubsub()
pubsub.subscribe("notifications")
for message in pubsub.listen():
    print(message)  # → "New order received!"

# Use case: Real-time notifications, event broadcasting
```

---

## Why Redis in Agentic AI Applications

### What is an Agentic AI Application?

**Agentic AI** = AI systems that can:
1. **Act autonomously** (make decisions without human input)
2. **Use tools** (call APIs, query databases, execute code)
3. **Maintain state** (remember context across interactions)
4. **Coordinate** (work with other agents or systems)

**Examples:**
- AI assistant that books flights and hotels
- AI customer support that can access order history
- AI code reviewer that can run tests and commit changes
- AI research assistant that can search web and summarize findings

---

### Why Redis is Critical for Agentic AI

#### **Problem 1: AI Agents Need Memory**

**Scenario:**
```
User: "Book me a flight to New York"
AI Agent: "When would you like to travel?"
User: "Next Monday"
AI Agent: ??? (needs to remember "New York" from earlier)
```

**Without Redis:**
```python
# Agent loses context between requests
def handle_request(user_message):
    # No memory of previous conversation!
    response = llm.generate(user_message)
    return response
```

**With Redis (Conversation Memory):**
```python
def handle_request(user_id, user_message):
    # Retrieve conversation history
    history = redis.lrange(f"conversation:{user_id}", 0, -1)
    
    # Build context
    full_context = "\n".join(history) + f"\nUser: {user_message}"
    
    # Generate response with context
    response = llm.generate(full_context)
    
    # Store in history
    redis.rpush(f"conversation:{user_id}", f"User: {user_message}")
    redis.rpush(f"conversation:{user_id}", f"AI: {response}")
    redis.expire(f"conversation:{user_id}", 3600)  # 1 hour TTL
    
    return response
```

**Why Redis?**
- Fast retrieval (conversation history in <1ms)
- Automatic cleanup (TTL removes old conversations)
- Shared across multiple API servers

---

#### **Problem 2: Caching Expensive AI Operations**

**Scenario:**
AI agent needs to analyze a large document multiple times.

**Cost without caching:**
```python
# User asks 3 similar questions about same document
question_1 = "Summarize this document"  # API call: $0.50
question_2 = "What are the key points?"  # API call: $0.50
question_3 = "Summarize the main ideas"  # API call: $0.50

# Total: $1.50 for essentially the same answer
```

**With Redis caching:**
```python
import hashlib
import json

def get_ai_response(document, question):
    # Create cache key from document + question
    cache_key = hashlib.md5(f"{document}{question}".encode()).hexdigest()
    cache_key = f"ai_cache:{cache_key}"
    
    # Check cache first
    cached = redis.get(cache_key)
    if cached:
        print("Cache hit! Saved API call")
        return json.loads(cached)
    
    # Cache miss - call AI
    print("Cache miss - calling expensive AI API")
    response = expensive_ai_api(document, question)
    
    # Store in cache (24 hour expiration)
    redis.setex(cache_key, 86400, json.dumps(response))
    
    return response

# Usage
doc = "Long document content..."
get_ai_response(doc, "Summarize this")  # API call: $0.50
get_ai_response(doc, "Summarize this")  # Cache hit: $0.00!
get_ai_response(doc, "What are the key points?")  # API call: $0.50

# Saved 33% of costs
```

**Real numbers:**
- Without cache: 1000 similar requests = $500
- With cache (90% hit rate): 1000 requests = $50
- **Savings: $450/day = $13,500/month**

---

#### **Problem 3: Rate Limiting AI Agents**

**Scenario:**
Prevent AI agent from making too many API calls (cost control).

```python
def call_ai_with_rate_limit(user_id, prompt):
    rate_key = f"rate_limit:{user_id}"
    
    # Get current count
    current_count = redis.get(rate_key)
    
    if current_count and int(current_count) >= 10:
        raise Exception("Rate limit exceeded: 10 requests per minute")
    
    # Increment counter
    pipe = redis.pipeline()
    pipe.incr(rate_key)
    pipe.expire(rate_key, 60)  # Reset after 60 seconds
    pipe.execute()
    
    # Make AI call
    return ai_api.call(prompt)

# User can make 10 AI requests per minute
# After that, they must wait
```

**Sliding window rate limiting (more accurate):**
```python
import time

def rate_limit_sliding_window(user_id, max_requests=10, window=60):
    now = time.time()
    key = f"rate_limit:{user_id}:requests"
    
    # Remove old requests outside window
    redis.zremrangebyscore(key, 0, now - window)
    
    # Count requests in current window
    request_count = redis.zcard(key)
    
    if request_count >= max_requests:
        return False  # Rate limited
    
    # Add current request with timestamp as score
    redis.zadd(key, {str(now): now})
    redis.expire(key, window)
    
    return True  # Allowed

# Usage
if rate_limit_sliding_window("user123"):
    response = call_expensive_ai_api()
else:
    return "Please wait before making more requests"
```

---

#### **Problem 4: Distributed Task Queue for Agents**

**Scenario:**
Multiple AI agents working on tasks from a shared queue.

```python
# Producer: Add tasks to queue
def create_ai_task(task_type, data):
    task = {
        "id": generate_id(),
        "type": task_type,
        "data": data,
        "created_at": time.time()
    }
    redis.lpush("tasks:pending", json.dumps(task))
    return task["id"]

# Consumer: Agent workers processing tasks
def ai_worker():
    print("AI Agent worker started")
    while True:
        # Blocking pop - waits for tasks
        task_json = redis.brpop("tasks:pending", timeout=5)
        
        if task_json:
            task = json.loads(task_json[1])
            print(f"Processing task: {task['type']}")
            
            try:
                # Process with AI
                if task['type'] == 'summarize':
                    result = ai_summarize(task['data'])
                elif task['type'] == 'analyze':
                    result = ai_analyze(task['data'])
                
                # Store result
                redis.setex(
                    f"task_result:{task['id']}", 
                    3600, 
                    json.dumps(result)
                )
            except Exception as e:
                # Move to failed queue
                redis.lpush("tasks:failed", json.dumps(task))

# Run multiple workers
import threading
for i in range(5):  # 5 AI agent workers
    threading.Thread(target=ai_worker, daemon=True).start()
```

---

#### **Problem 5: Session State for Multi-Step AI Workflows**

**Scenario:**
AI booking agent needs to collect information across multiple steps.

```python
class BookingAgent:
    def __init__(self, session_id):
        self.session_id = session_id
        self.redis_key = f"booking_session:{session_id}"
    
    def set_state(self, key, value):
        """Store workflow state"""
        redis.hset(self.redis_key, key, json.dumps(value))
        redis.expire(self.redis_key, 1800)  # 30 min session
    
    def get_state(self, key):
        """Retrieve workflow state"""
        value = redis.hget(self.redis_key, key)
        return json.loads(value) if value else None
    
    def get_all_state(self):
        """Get complete session state"""
        state = redis.hgetall(self.redis_key)
        return {k: json.loads(v) for k, v in state.items()}
    
    def handle_message(self, user_message):
        current_step = self.get_state("current_step") or "start"
        
        if current_step == "start":
            # Ask for destination
            self.set_state("current_step", "get_destination")
            return "Where would you like to fly to?"
        
        elif current_step == "get_destination":
            self.set_state("destination", user_message)
            self.set_state("current_step", "get_dates")
            return f"Great! When do you want to fly to {user_message}?"
        
        elif current_step == "get_dates":
            self.set_state("travel_date", user_message)
            self.set_state("current_step", "confirm")
            
            # Retrieve all collected info
            destination = self.get_state("destination")
            date = self.get_state("travel_date")
            
            return f"Confirm: Flight to {destination} on {date}?"
        
        elif current_step == "confirm":
            if "yes" in user_message.lower():
                booking_info = self.get_all_state()
                # Process booking
                return f"Booking confirmed! {booking_info}"

# Usage
agent = BookingAgent(session_id="user123_session456")
print(agent.handle_message("hi"))  # "Where would you like to fly to?"
print(agent.handle_message("Paris"))  # "When do you want to fly to Paris?"
print(agent.handle_message("Next Monday"))  # "Confirm: Flight to Paris on Next Monday?"
```

---

#### **Problem 6: Agent Coordination (Multiple Agents Working Together)**

**Scenario:**
Research agent delegates work to specialized sub-agents.

```python
class AgentCoordinator:
    def research_topic(self, topic):
        """Main agent coordinates sub-agents"""
        job_id = generate_id()
        
        # Create sub-tasks
        tasks = [
            {"agent": "web_search", "query": f"{topic} latest news"},
            {"agent": "academic_search", "query": f"{topic} research papers"},
            {"agent": "summarizer", "input": "web_results"}
        ]
        
        # Store job metadata
        redis.hset(f"job:{job_id}", mapping={
            "status": "pending",
            "total_tasks": len(tasks),
            "completed_tasks": 0,
            "topic": topic
        })
        
        # Queue tasks for sub-agents
        for task in tasks:
            task["job_id"] = job_id
            redis.lpush(f"queue:{task['agent']}", json.dumps(task))
        
        return job_id
    
    def check_job_status(self, job_id):
        """Check if all sub-agents finished"""
        job_data = redis.hgetall(f"job:{job_id}")
        
        if int(job_data[b'completed_tasks']) == int(job_data[b'total_tasks']):
            # All done - compile results
            results = []
            for i in range(int(job_data[b'total_tasks'])):
                result = redis.get(f"job:{job_id}:result:{i}")
                results.append(json.loads(result))
            
            return {"status": "complete", "results": results}
        else:
            return {"status": "processing", "progress": f"{job_data[b'completed_tasks']}/{job_data[b'total_tasks']}"}

# Sub-agent worker
def web_search_agent():
    while True:
        task = redis.brpop("queue:web_search")
        task_data = json.loads(task[1])
        
        # Perform web search
        results = perform_web_search(task_data["query"])
        
        # Store results
        job_id = task_data["job_id"]
        task_index = redis.hincrby(f"job:{job_id}", "completed_tasks", 1) - 1
        redis.set(f"job:{job_id}:result:{task_index}", json.dumps(results))
```

---

#### **Problem 7: Vector Similarity Search for RAG**

**Scenario:**
AI agent needs to find relevant documents for context (RAG pattern).

**Note:** Redis supports vector similarity search with RediSearch module.

```python
from redis.commands.search.field import VectorField, TextField
from redis.commands.search.indexDefinition import IndexDefinition, IndexType
import numpy as np

# Create vector index
def create_vector_index(redis_client):
    schema = (
        TextField("content"),
        VectorField("embedding",
            "FLAT", {
                "TYPE": "FLOAT32",
                "DIM": 1536,  # OpenAI embedding size
                "DISTANCE_METRIC": "COSINE"
            }
        )
    )
    
    redis_client.ft("doc_index").create_index(
        schema,
        definition=IndexDefinition(prefix=["doc:"], index_type=IndexType.HASH)
    )

# Store document with embedding
def store_document(doc_id, content, embedding):
    redis.hset(f"doc:{doc_id}", mapping={
        "content": content,
        "embedding": np.array(embedding, dtype=np.float32).tobytes()
    })

# Search for similar documents
def find_relevant_docs(query_embedding, top_k=5):
    query_vector = np.array(query_embedding, dtype=np.float32).tobytes()
    
    results = redis.ft("doc_index").search(
        f"*=>[KNN {top_k} @embedding $vec AS score]",
        query_params={"vec": query_vector}
    )
    
    return [{"content": doc.content, "score": doc.score} for doc in results.docs]

# AI Agent using RAG
def answer_with_context(user_question):
    # Get question embedding
    question_embedding = get_embedding(user_question)
    
    # Find relevant documents
    relevant_docs = find_relevant_docs(question_embedding, top_k=3)
    
    # Build context
    context = "\n\n".join([doc["content"] for doc in relevant_docs])
    
    # Generate answer with context
    prompt = f"""Context:
{context}

Question: {user_question}
Answer:"""
    
    answer = llm.generate(prompt)
    return answer
```

---

## Code-Based Explanations

### Example 1: Complete AI Chatbot with Redis

```python
import redis
import json
import time
from openai import OpenAI

# Initialize
redis_client = redis.Redis(host='localhost', port=6379, decode_responses=True)
openai_client = OpenAI(api_key="your-key")

class AIChatbot:
    def __init__(self, user_id):
        self.user_id = user_id
        self.conversation_key = f"chat:{user_id}"
        self.max_history = 10  # Keep last 10 messages
    
    def add_message(self, role, content):
        """Store message in conversation history"""
        message = {"role": role, "content": content, "timestamp": time.time()}
        redis_client.lpush(self.conversation_key, json.dumps(message))
        
        # Trim to max history
        redis_client.ltrim(self.conversation_key, 0, self.max_history - 1)
        
        # Set expiration (24 hours)
        redis_client.expire(self.conversation_key, 86400)
    
    def get_history(self):
        """Retrieve conversation history"""
        messages = redis_client.lrange(self.conversation_key, 0, -1)
        # Reverse (oldest first)
        return [json.loads(msg) for msg in reversed(messages)]
    
    def chat(self, user_message):
        """Main chat function with caching"""
        
        # 1. Check cache for exact question
        cache_key = f"cache:{hashlib.md5(user_message.encode()).hexdigest()}"
        cached_response = redis_client.get(cache_key)
        
        if cached_response:
            print("📦 Cache hit!")
            response = cached_response
        else:
            print("🤖 Generating response...")
            
            # 2. Get conversation history
            history = self.get_history()
            
            # 3. Build messages for OpenAI
            messages = [{"role": msg["role"], "content": msg["content"]} 
                       for msg in history]
            messages.append({"role": "user", "content": user_message})
            
            # 4. Call OpenAI
            completion = openai_client.chat.completions.create(
                model="gpt-4",
                messages=messages
            )
            response = completion.choices[0].message.content
            
            # 5. Cache response (1 hour)
            redis_client.setex(cache_key, 3600, response)
        
        # 6. Store in history
        self.add_message("user", user_message)
        self.add_message("assistant", response)
        
        return response

# Usage
bot = AIChatbot(user_id="user_12345")

print(bot.chat("What is Python?"))
# First call: Calls OpenAI API

print(bot.chat("What is Python?"))
# Second call: Returns cached response (instant!)

print(bot.chat("Tell me more about its history"))
# Uses conversation context from history
```

**What's happening:**
1. **Conversation Memory**: Each message stored in Redis list
2. **Caching**: Identical questions return cached answers
3. **Context**: Full conversation history passed to AI
4. **Expiration**: Old conversations auto-deleted
5. **Performance**: Cache hits return in <1ms vs 2000ms API call

---

### Example 2: Multi-Agent Research System

```python
import redis
import json
import time
from concurrent.futures import ThreadPoolExecutor

redis_client = redis.Redis(host='localhost', port=6379, decode_responses=True)

class ResearchCoordinator:
    """Main agent that coordinates research"""
    
    def start_research(self, topic):
        research_id = f"research:{int(time.time())}"
        
        # Define research tasks
        tasks = [
            {"type": "web_search", "query": f"{topic} latest developments"},
            {"type": "academic_search", "query": f"{topic} research papers"},
            {"type": "news_search", "query": f"{topic} recent news"},
        ]
        
        # Store research metadata
        redis_client.hset(research_id, mapping={
            "topic": topic,
            "status": "in_progress",
            "total_tasks": len(tasks),
            "completed_tasks": 0,
            "started_at": time.time()
        })
        
        # Queue tasks
        for idx, task in enumerate(tasks):
            task["research_id"] = research_id
            task["task_id"] = idx
            redis_client.lpush(f"queue:{task['type']}", json.dumps(task))
        
        return research_id
    
    def get_results(self, research_id):
        """Get research results when complete"""
        info = redis_client.hgetall(research_id)
        
        if int(info["completed_tasks"]) < int(info["total_tasks"]):
            return {"status": "in_progress", "progress": f"{info['completed_tasks']}/{info['total_tasks']}"}
        
        # Compile results
        results = []
        for i in range(int(info["total_tasks"])):
            result = redis_client.get(f"{research_id}:result:{i}")
            if result:
                results.append(json.loads(result))
        
        return {
            "status": "complete",
            "topic": info["topic"],
            "results": results,
            "duration": time.time() - float(info["started_at"])
        }

class WebSearchAgent:
    """Agent that performs web searches"""
    
    def start(self):
        print("🔍 Web Search Agent started")
        while True:
            # Wait for task
            task_data = redis_client.brpop("queue:web_search", timeout=5)
            
            if task_data:
                task = json.loads(task_data[1])
                print(f"🔍 Searching: {task['query']}")
                
                # Simulate web search
                time.sleep(2)
                results = {
                    "query": task["query"],
                    "results": [
                        {"title": "Result 1", "url": "http://example.com/1"},
                        {"title": "Result 2", "url": "http://example.com/2"}
                    ]
                }
                
                # Store result
                research_id = task["research_id"]
                task_id = task["task_id"]
                redis_client.set(f"{research_id}:result:{task_id}", json.dumps(results))
                
                # Update progress
                redis_client.hincrby(research_id, "completed_tasks", 1)

class AcademicSearchAgent:
    """Agent that searches academic papers"""
    
    def start(self):
        print("📚 Academic Search Agent started")
        while True:
            task_data = redis_client.brpop("queue:academic_search", timeout=5)
            
            if task_data:
                task = json.loads(task_data[1])
                print(f"📚 Searching papers: {task['query']}")
                
                # Simulate academic search
                time.sleep(3)
                results = {
                    "query": task["query"],
                    "papers": [
                        {"title": "Paper 1", "doi": "10.1234/paper1"},
                        {"title": "Paper 2", "doi": "10.1234/paper2"}
                    ]
                }
                
                # Store result
                research_id = task["research_id"]
                task_id = task["task_id"]
                redis_client.set(f"{research_id}:result:{task_id}", json.dumps(results))
                redis_client.hincrby(research_id, "completed_tasks", 1)

class NewsSearchAgent:
    """Agent that searches news"""
    
    def start(self):
        print("📰 News Search Agent started")
        while True:
            task_data = redis_client.brpop("queue:news_search", timeout=5)
            
            if task_data:
                task = json.loads(task_data[1])
                print(f"📰 Searching news: {task['query']}")
                
                # Simulate news search
                time.sleep(1.5)
                results = {
                    "query": task["query"],
                    "articles": [
                        {"headline": "News 1", "source": "TechCrunch"},
                        {"headline": "News 2", "source": "Wired"}
                    ]
                }
                
                # Store result
                research_id = task["research_id"]
                task_id = task["task_id"]
                redis_client.set(f"{research_id}:result:{task_id}", json.dumps(results))
                redis_client.hincrby(research_id, "completed_tasks", 1)

# Run the system
if __name__ == "__main__":
    # Start agent workers in background threads
    executor = ThreadPoolExecutor(max_workers=3)
    executor.submit(WebSearchAgent().start)
    executor.submit(AcademicSearchAgent().start)
    executor.submit(NewsSearchAgent().start)
    
    # Start research
    coordinator = ResearchCoordinator()
    research_id = coordinator.start_research("Artificial Intelligence")
    print(f"✅ Research started: {research_id}")
    
    # Poll for results
    while True:
        time.sleep(2)
        results = coordinator.get_results(research_id)
        
        if results["status"] == "complete":
            print(f"\n✅ Research complete in {results['duration']:.2f}s")
            print(json.dumps(results["results"], indent=2))
            break
        else:
            print(f"⏳ Progress: {results['progress']}")
```

**Output:**
```
🔍 Web Search Agent started
📚 Academic Search Agent started
📰 News Search Agent started
✅ Research started: research:1707689234
⏳ Progress: 0/3
🔍 Searching: Artificial Intelligence latest developments
📰 Searching news: Artificial Intelligence recent news
📚 Searching papers: Artificial Intelligence research papers
⏳ Progress: 1/3
⏳ Progress: 2/3
✅ Research complete in 6.24s
[
  {
    "query": "Artificial Intelligence latest developments",
    "results": [...]
  },
  {
    "query": "Artificial Intelligence research papers",
    "papers": [...]
  },
  {
    "query": "Artificial Intelligence recent news",
    "articles": [...]
  }
]
```

**What Redis provides:**
1. **Task Queue**: Agents pull tasks from Redis lists
2. **Coordination**: Central state for job progress
3. **Results Storage**: Each agent stores results independently
4. **Scalability**: Can run 100s of agent workers across servers

---

### Example 3: Smart Caching for LLM Responses

```python
import redis
import hashlib
import json
from openai import OpenAI

redis_client = redis.Redis(host='localhost', port=6379, decode_responses=True)
openai_client = OpenAI(api_key="your-key")

class SmartLLMCache:
    """Intelligent caching for LLM API calls"""
    
    def __init__(self):
        self.cache_stats_key = "llm_cache_stats"
    
    def _generate_cache_key(self, prompt, model="gpt-4"):
        """Generate unique cache key from prompt and model"""
        content = f"{model}:{prompt}"
        return f"llm_cache:{hashlib.sha256(content.encode()).hexdigest()}"
    
    def _update_stats(self, hit=True):
        """Track cache performance"""
        if hit:
            redis_client.hincrby(self.cache_stats_key, "hits", 1)
        else:
            redis_client.hincrby(self.cache_stats_key, "misses", 1)
    
    def get_completion(self, prompt, model="gpt-4", max_tokens=500):
        """Get LLM completion with caching"""
        cache_key = self._generate_cache_key(prompt, model)
        
        # Try cache first
        cached = redis_client.get(cache_key)
        
        if cached:
            self._update_stats(hit=True)
            print("💰 Saved API call! Using cached response")
            return json.loads(cached)
        
        # Cache miss - call API
        self._update_stats(hit=False)
        print("💸 Calling OpenAI API...")
        
        response = openai_client.chat.completions.create(
            model=model,
            messages=[{"role": "user", "content": prompt}],
            max_tokens=max_tokens
        )
        
        result = {
            "content": response.choices[0].message.content,
            "model": model,
            "tokens": response.usage.total_tokens,
            "cached_at": time.time()
        }
        
        # Cache for 24 hours
        redis_client.setex(cache_key, 86400, json.dumps(result))
        
        return result
    
    def get_stats(self):
        """Get cache performance statistics"""
        stats = redis_client.hgetall(self.cache_stats_key)
        if not stats:
            return {"hits": 0, "misses": 0, "hit_rate": 0, "savings": 0}
        
        hits = int(stats.get("hits", 0))
        misses = int(stats.get("misses", 0))
        total = hits + misses
        
        hit_rate = (hits / total * 100) if total > 0 else 0
        
        # Estimate savings (assuming $0.03 per 1K tokens, avg 500 tokens)
        avg_cost_per_call = 0.015  # $0.015
        savings = hits * avg_cost_per_call
        
        return {
            "hits": hits,
            "misses": misses,
            "total_calls": total,
            "hit_rate": f"{hit_rate:.1f}%",
            "estimated_savings": f"${savings:.2f}"
        }

# Usage
cache = SmartLLMCache()

# First call - cache miss
result1 = cache.get_completion("What is Redis?")
print(result1["content"][:100])

# Same prompt - cache hit!
result2 = cache.get_completion("What is Redis?")
print(result2["content"][:100])

# Different prompt - cache miss
result3 = cache.get_completion("Explain Python decorators")
print(result3["content"][:100])

# Same as first - cache hit!
result4 = cache.get_completion("What is Redis?")

# Check savings
print("\n📊 Cache Statistics:")
print(json.dumps(cache.get_stats(), indent=2))
```

**Output:**
```
💸 Calling OpenAI API...
Redis is an open-source, in-memory data structure store...

💰 Saved API call! Using cached response
Redis is an open-source, in-memory data structure store...

💸 Calling OpenAI API...
Python decorators are a powerful feature that allows...

💰 Saved API call! Using cached response

📊 Cache Statistics:
{
  "hits": 2,
  "misses": 2,
  "total_calls": 4,
  "hit_rate": "50.0%",
  "estimated_savings": "$0.03"
}
```

**Scaling impact:**
- 10,000 requests/day with 70% cache hit rate
- Misses: 3,000 × $0.015 = $45/day
- Without cache: 10,000 × $0.015 = $150/day
- **Savings: $105/day = $3,150/month**

---

## Real-World Scenarios

### Scenario 1: Customer Support AI Agent

**Requirements:**
- Remember conversation context
- Access customer order history
- Rate limit to prevent abuse
- Cache common questions

```python
class SupportAgent:
    def __init__(self, customer_id):
        self.customer_id = customer_id
        self.conversation_key = f"support:{customer_id}:conversation"
        self.cache = SmartLLMCache()
    
    def handle_message(self, message):
        # 1. Rate limiting
        if not self._check_rate_limit():
            return "Please wait before sending more messages."
        
        # 2. Get conversation history
        history = self._get_history()
        
        # 3. Get customer context
        customer_info = self._get_customer_info()
        
        # 4. Build prompt with context
        prompt = f"""You are a customer support agent.

Customer Info:
{json.dumps(customer_info, indent=2)}

Conversation History:
{history}

Customer Message: {message}

Provide helpful support response:"""
        
        # 5. Get AI response (with caching)
        response = self.cache.get_completion(prompt)
        
        # 6. Store in conversation
        self._add_to_history("customer", message)
        self._add_to_history("agent", response["content"])
        
        return response["content"]
    
    def _check_rate_limit(self):
        """10 messages per minute"""
        key = f"rate_limit:{self.customer_id}"
        count = redis_client.incr(key)
        
        if count == 1:
            redis_client.expire(key, 60)
        
        return count <= 10
    
    def _get_customer_info(self):
        """Get customer data from Redis"""
        # In production, might come from database
        customer_key = f"customer:{self.customer_id}"
        info = redis_client.hgetall(customer_key)
        return info if info else {"id": self.customer_id, "name": "Unknown"}
    
    def _get_history(self):
        """Get last 5 messages"""
        messages = redis_client.lrange(self.conversation_key, 0, 4)
        return "\n".join(reversed([msg.decode() for msg in messages]))
    
    def _add_to_history(self, role, content):
        """Add message to history"""
        msg = f"{role}: {content}"
        redis_client.lpush(self.conversation_key, msg)
        redis_client.ltrim(self.conversation_key, 0, 9)  # Keep last 10
        redis_client.expire(self.conversation_key, 3600)  # 1 hour
```

---

### Scenario 2: Document Processing Pipeline

**Requirements:**
- Queue documents for processing
- Distribute work to multiple workers
- Track processing status
- Cache processed results

```python
class DocumentProcessor:
    def submit_document(self, doc_id, content):
        """Submit document for AI processing"""
        
        # Check if already processed (cache)
        cache_key = f"doc_processed:{doc_id}"
        cached = redis_client.get(cache_key)
        
        if cached:
            return {"status": "cached", "result": json.loads(cached)}
        
        # Create processing job
        job = {
            "doc_id": doc_id,
            "content": content,
            "submitted_at": time.time()
        }
        
        # Add to queue
        redis_client.lpush("queue:documents", json.dumps(job))
        
        # Set job status
        redis_client.hset(f"job:{doc_id}", mapping={
            "status": "queued",
            "submitted_at": job["submitted_at"]
        })
        
        return {"status": "queued", "doc_id": doc_id}
    
    def get_status(self, doc_id):
        """Check processing status"""
        status = redis_client.hgetall(f"job:{doc_id}")
        
        if not status:
            return {"status": "not_found"}
        
        return {
            "status": status.get(b"status", b"unknown").decode(),
            "result": status.get(b"result", b"").decode()
        }

class DocumentWorker:
    """Worker that processes documents with AI"""
    
    def process(self):
        while True:
            # Get next job
            job_data = redis_client.brpop("queue:documents", timeout=5)
            
            if job_data:
                job = json.loads(job_data[1])
                doc_id = job["doc_id"]
                
                # Update status
                redis_client.hset(f"job:{doc_id}", "status", "processing")
                
                try:
                    # AI processing
                    result = self._ai_process(job["content"])
                    
                    # Store result
                    redis_client.hset(f"job:{doc_id}", mapping={
                        "status": "complete",
                        "result": json.dumps(result),
                        "completed_at": time.time()
                    })
                    
                    # Cache result (24 hours)
                    redis_client.setex(
                        f"doc_processed:{doc_id}",
                        86400,
                        json.dumps(result)
                    )
                    
                except Exception as e:
                    # Mark as failed
                    redis_client.hset(f"job:{doc_id}", mapping={
                        "status": "failed",
                        "error": str(e)
                    })
    
    def _ai_process(self, content):
        """Process document with AI"""
        # Summarize, extract entities, classify, etc.
        prompt = f"Analyze this document:\n\n{content}"
        response = openai_client.chat.completions.create(
            model="gpt-4",
            messages=[{"role": "user", "content": prompt}]
        )
        return {"summary": response.choices[0].message.content}

# Usage
processor = DocumentProcessor()

# Submit documents
doc1 = processor.submit_document("doc1", "Long document content...")
print(doc1)  # {"status": "queued", "doc_id": "doc1"}

# Start worker (in background)
worker = DocumentWorker()
threading.Thread(target=worker.process, daemon=True).start()

# Check status
time.sleep(5)
status = processor.get_status("doc1")
print(status)  # {"status": "complete", "result": {...}}
```

---

## Production Patterns

### Pattern 1: Circuit Breaker for External APIs

**Problem:** AI API goes down, don't want to keep calling it.

```python
class CircuitBreaker:
    def __init__(self, service_name, failure_threshold=5, timeout=60):
        self.service_name = service_name
        self.failure_threshold = failure_threshold
        self.timeout = timeout
        self.state_key = f"circuit:{service_name}:state"
        self.failures_key = f"circuit:{service_name}:failures"
    
    def call(self, func, *args, **kwargs):
        """Call function with circuit breaker protection"""
        
        # Check circuit state
        state = redis_client.get(self.state_key)
        
        if state == "open":
            raise Exception(f"Circuit breaker OPEN for {self.service_name}")
        
        try:
            # Try the call
            result = func(*args, **kwargs)
            
            # Success - reset failures
            redis_client.delete(self.failures_key)
            redis_client.set(self.state_key, "closed")
            
            return result
            
        except Exception as e:
            # Increment failures
            failures = redis_client.incr(self.failures_key)
            
            if failures >= self.failure_threshold:
                # Open circuit
                redis_client.setex(self.state_key, self.timeout, "open")
                print(f"⚠️  Circuit breaker OPENED for {self.service_name}")
            
            raise e

# Usage
breaker = CircuitBreaker("openai_api", failure_threshold=3, timeout=30)

def call_openai_api(prompt):
    # Your OpenAI API call
    return openai_client.chat.completions.create(...)

try:
    result = breaker.call(call_openai_api, "Hello")
except Exception as e:
    print(f"API call failed: {e}")
    # Use fallback response
```

---

### Pattern 2: Distributed Locks

**Problem:** Multiple agents trying to process same task.

```python
import uuid

class DistributedLock:
    def __init__(self, lock_name, timeout=10):
        self.lock_name = f"lock:{lock_name}"
        self.timeout = timeout
        self.identifier = str(uuid.uuid4())
    
    def acquire(self):
        """Acquire lock"""
        end_time = time.time() + self.timeout
        
        while time.time() < end_time:
            # Try to set lock (only if not exists)
            if redis_client.set(self.lock_name, self.identifier, nx=True, ex=self.timeout):
                return True
            time.sleep(0.001)
        
        return False
    
    def release(self):
        """Release lock (only if we own it)"""
        # Lua script for atomic check-and-delete
        lua_script = """
        if redis.call("get", KEYS[1]) == ARGV[1] then
            return redis.call("del", KEYS[1])
        else
            return 0
        end
        """
        redis_client.eval(lua_script, 1, self.lock_name, self.identifier)
    
    def __enter__(self):
        if not self.acquire():
            raise Exception(f"Could not acquire lock: {self.lock_name}")
        return self
    
    def __exit__(self, exc_type, exc_val, exc_tb):
        self.release()

# Usage
def process_task(task_id):
    lock_name = f"task:{task_id}"
    
    with DistributedLock(lock_name):
        print(f"Processing task {task_id}")
        # Only one agent can be here at a time
        time.sleep(2)
        print(f"Completed task {task_id}")

# Multiple workers try same task - only one succeeds
import threading
for i in range(5):
    threading.Thread(target=process_task, args=("task_123",)).start()
```

---

### Pattern 3: Leaky Bucket Rate Limiting

**Problem:** Allow bursts but enforce long-term rate limit.

```python
class LeakyBucket:
    def __init__(self, user_id, capacity=10, leak_rate=1):
        """
        capacity: Max tokens in bucket
        leak_rate: Tokens that leak per second
        """
        self.user_id = user_id
        self.key = f"bucket:{user_id}"
        self.capacity = capacity
        self.leak_rate = leak_rate
    
    def allow_request(self):
        """Check if request is allowed"""
        now = time.time()
        
        # Get current bucket state
        bucket = redis_client.hgetall(self.key)
        
        if not bucket:
            # New bucket
            tokens = self.capacity - 1
            redis_client.hset(self.key, mapping={
                "tokens": tokens,
                "last_update": now
            })
            redis_client.expire(self.key, 3600)
            return True
        
        # Calculate leaked tokens since last update
        last_update = float(bucket[b"last_update"])
        current_tokens = float(bucket[b"tokens"])
        
        time_passed = now - last_update
        leaked = time_passed * self.leak_rate
        
        # Add leaked tokens (up to capacity)
        new_tokens = min(self.capacity, current_tokens + leaked)
        
        # Check if request allowed
        if new_tokens >= 1:
            # Allow request, consume token
            redis_client.hset(self.key, mapping={
                "tokens": new_tokens - 1,
                "last_update": now
            })
            return True
        else:
            # Rate limited
            return False

# Usage
bucket = LeakyBucket("user123", capacity=5, leak_rate=0.5)  # 0.5 tokens/sec

for i in range(10):
    if bucket.allow_request():
        print(f"✅ Request {i+1} allowed")
        # Process AI request
    else:
        print(f"❌ Request {i+1} rate limited")
    time.sleep(0.5)
```

---

## Interview Questions

### Beginner Level

**Q1: What is Redis and why is it faster than traditional databases?**

**Answer:**
Redis is an in-memory data structure store. It's faster because:
- Data stored in RAM (not disk)
- RAM access: ~100 nanoseconds
- Disk (SSD) access: ~100 microseconds (1000x slower)
- Simple key-value lookups (O(1) complexity)

**Q2: Explain the difference between Redis Strings and Lists.**

**Answer:**
```python
# String: Single value
redis.set("name", "John")  # Store
redis.get("name")  # → "John"

# List: Ordered collection
redis.lpush("queue", "task1")  # Add to left
redis.lpush("queue", "task2")
redis.rpop("queue")  # Remove from right → "task1"
```

Strings for: simple values, counters, flags
Lists for: queues, recent items, activity feeds

**Q3: What happens to Redis data when the server restarts?**

**Answer:**
By default, data is **lost** (volatile). But Redis offers persistence:

**RDB (snapshots):**
- Saves full database at intervals
- Fast restart
- May lose recent data

**AOF (append-only file):**
- Logs every write operation
- Better durability
- Slower, larger files

**Both:**
- Use both for maximum safety

---

### Intermediate Level

**Q4: How would you implement a rate limiter in Redis for an AI API?**

**Answer:**
```python
def rate_limit(user_id, max_requests=10, window=60):
    key = f"rate_limit:{user_id}"
    
    # Increment counter
    current = redis.incr(key)
    
    # Set expiration on first request
    if current == 1:
        redis.expire(key, window)
    
    # Check if over limit
    return current <= max_requests

# Better: Sliding window
def rate_limit_sliding(user_id, max_requests=10, window=60):
    now = time.time()
    key = f"rate_limit:{user_id}:requests"
    
    # Remove old requests
    redis.zremrangebyscore(key, 0, now - window)
    
    # Count requests in window
    count = redis.zcard(key)
    
    if count >= max_requests:
        return False
    
    # Add current request
    redis.zadd(key, {str(now): now})
    redis.expire(key, window)
    return True
```

**Q5: How do you prevent duplicate AI processing of the same document?**

**Answer:**
```python
import hashlib

def process_document(content):
    # Create unique hash of content
    content_hash = hashlib.sha256(content.encode()).hexdigest()
    lock_key = f"processing:{content_hash}"
    
    # Try to acquire lock
    if not redis.set(lock_key, "1", nx=True, ex=300):
        # Already being processed
        return {"status": "duplicate", "message": "Already processing"}
    
    try:
        # Check if already processed
        result_key = f"result:{content_hash}"
        cached_result = redis.get(result_key)
        
        if cached_result:
            return json.loads(cached_result)
        
        # Process with AI
        result = ai_process(content)
        
        # Cache result (24 hours)
        redis.setex(result_key, 86400, json.dumps(result))
        
        return result
    finally:
        # Release lock
        redis.delete(lock_key)
```

**Q6: Design a conversation memory system for a chatbot using Redis.**

**Answer:**
```python
class ConversationMemory:
    def __init__(self, user_id, max_messages=20):
        self.user_id = user_id
        self.key = f"conversation:{user_id}"
        self.max_messages = max_messages
    
    def add_message(self, role, content):
        """Add message to conversation"""
        message = {
            "role": role,
            "content": content,
            "timestamp": time.time()
        }
        
        # Add to list
        redis.lpush(self.key, json.dumps(message))
        
        # Trim to max size
        redis.ltrim(self.key, 0, self.max_messages - 1)
        
        # Set 24-hour expiration
        redis.expire(self.key, 86400)
    
    def get_history(self, last_n=10):
        """Get recent messages"""
        messages = redis.lrange(self.key, 0, last_n - 1)
        return [json.loads(msg) for msg in reversed(messages)]
    
    def get_context_window(self, max_tokens=4000):
        """Get messages that fit in token limit"""
        messages = self.get_history(last_n=50)
        
        context = []
        token_count = 0
        
        for msg in reversed(messages):
            # Rough token estimate (4 chars = 1 token)
            msg_tokens = len(msg["content"]) // 4
            
            if token_count + msg_tokens > max_tokens:
                break
            
            context.append(msg)
            token_count += msg_tokens
        
        return list(reversed(context))
    
    def summarize_old_context(self):
        """Compress old messages into summary"""
        messages = self.get_history(last_n=50)
        
        if len(messages) > self.max_messages:
            # Get old messages
            old_messages = messages[self.max_messages:]
            
            # Create summary with AI
            summary = ai_summarize(old_messages)
            
            # Store summary
            summary_key = f"{self.key}:summary"
            redis.set(summary_key, summary)
            
            # Keep only recent messages
            redis.ltrim(self.key, 0, self.max_messages - 1)
```

---

### Advanced Level

**Q7: Design a distributed task queue system for multiple AI agents using Redis.**

**Answer:**

```python
class DistributedTaskQueue:
    """Production-grade task queue"""
    
    def __init__(self, queue_name):
        self.queue_name = queue_name
        self.pending_queue = f"queue:{queue_name}:pending"
        self.processing_queue = f"queue:{queue_name}:processing"
        self.failed_queue = f"queue:{queue_name}:failed"
        self.results_prefix = f"queue:{queue_name}:result"
    
    def enqueue(self, task_data, priority=0):
        """Add task to queue with priority"""
        task_id = str(uuid.uuid4())
        task = {
            "id": task_id,
            "data": task_data,
            "enqueued_at": time.time(),
            "attempts": 0
        }
        
        # Higher score = higher priority
        redis.zadd(self.pending_queue, {json.dumps(task): priority})
        
        return task_id
    
    def dequeue(self, worker_id, timeout=300):
        """Get next task (with timeout tracking)"""
        
        # Atomic: pop highest priority task
        task_data = redis.zpopmax(self.pending_queue)
        
        if not task_data:
            return None
        
        task = json.loads(task_data[0][0])
        task["worker_id"] = worker_id
        task["processing_since"] = time.time()
        
        # Move to processing queue (with timeout)
        redis.zadd(
            self.processing_queue,
            {json.dumps(task): time.time() + timeout}
        )
        
        return task
    
    def complete(self, task_id, result):
        """Mark task as complete"""
        
        # Store result
        result_key = f"{self.results_prefix}:{task_id}"
        redis.setex(result_key, 86400, json.dumps(result))
        
        # Remove from processing queue
        self._remove_from_processing(task_id)
    
    def fail(self, task_id, error, max_retries=3):
        """Handle task failure with retry logic"""
        
        task = self._find_in_processing(task_id)
        if not task:
            return
        
        task["attempts"] += 1
        task["last_error"] = error
        
        if task["attempts"] < max_retries:
            # Retry with exponential backoff
            delay = 2 ** task["attempts"]  # 2, 4, 8 seconds
            retry_time = time.time() + delay
            
            redis.zadd(self.pending_queue, {json.dumps(task): retry_time})
        else:
            # Max retries exceeded
            redis.lpush(self.failed_queue, json.dumps(task))
        
        self._remove_from_processing(task_id)
    
    def recover_stuck_tasks(self):
        """Recover tasks stuck in processing (worker crashed)"""
        now = time.time()
        
        # Find expired tasks
        stuck_tasks = redis.zrangebyscore(
            self.processing_queue,
            0,
            now
        )
        
        for task_json in stuck_tasks:
            task = json.loads(task_json)
            
            # Move back to pending
            redis.zadd(self.pending_queue, {task_json: now})
            redis.zrem(self.processing_queue, task_json)
            
            print(f"Recovered stuck task: {task['id']}")
    
    def _find_in_processing(self, task_id):
        """Find task in processing queue"""
        all_tasks = redis.zrange(self.processing_queue, 0, -1)
        for task_json in all_tasks:
            task = json.loads(task_json)
            if task["id"] == task_id:
                return task
        return None
    
    def _remove_from_processing(self, task_id):
        """Remove task from processing queue"""
        all_tasks = redis.zrange(self.processing_queue, 0, -1)
        for task_json in all_tasks:
            task = json.loads(task_json)
            if task["id"] == task_id:
                redis.zrem(self.processing_queue, task_json)
                break

# AI Worker
class AIWorker:
    def __init__(self, worker_id, queue):
        self.worker_id = worker_id
        self.queue = queue
    
    def start(self):
        print(f"Worker {self.worker_id} started")
        
        while True:
            # Get next task
            task = self.queue.dequeue(self.worker_id)
            
            if not task:
                time.sleep(1)
                continue
            
            try:
                print(f"Worker {self.worker_id} processing {task['id']}")
                
                # Process with AI
                result = self.process_ai_task(task["data"])
                
                # Mark complete
                self.queue.complete(task["id"], result)
                
            except Exception as e:
                print(f"Task {task['id']} failed: {e}")
                self.queue.fail(task['id'], str(e))
    
    def process_ai_task(self, data):
        """Simulate AI processing"""
        time.sleep(2)  # AI processing time
        return {"status": "success", "result": f"Processed {data}"}

# Usage
queue = DistributedTaskQueue("ai_tasks")

# Enqueue tasks with priority
queue.enqueue({"type": "urgent", "data": "Critical analysis"}, priority=10)
queue.enqueue({"type": "normal", "data": "Regular task"}, priority=5)

# Start workers
for i in range(3):
    worker = AIWorker(f"worker_{i}", queue)
    threading.Thread(target=worker.start, daemon=True).start()

# Monitor for stuck tasks
def monitor():
    while True:
        time.sleep(60)
        queue.recover_stuck_tasks()

threading.Thread(target=monitor, daemon=True).start()
```

**Q8: How would you implement semantic caching for LLM responses?**

**Answer:**
```python
from sentence_transformers import SentenceTransformer
import numpy as np

class SemanticCache:
    """Cache LLM responses by semantic similarity"""
    
    def __init__(self, similarity_threshold=0.85):
        self.model = SentenceTransformer('all-MiniLM-L6-v2')
        self.threshold = similarity_threshold
    
    def get_cached_response(self, query):
        """Find similar cached query"""
        
        # Get query embedding
        query_embedding = self.model.encode(query)
        
        # Search in Redis (using RediSearch with vectors)
        results = redis.ft("semantic_cache").search(
            f"*=>[KNN 5 @embedding $vec AS score]",
            query_params={"vec": query_embedding.tobytes()}
        )
        
        if results.total == 0:
            return None
        
        # Check best match
        best_match = results.docs[0]
        similarity = 1 - float(best_match.score)
        
        if similarity >= self.threshold:
            print(f"✨ Semantic cache hit! Similarity: {similarity:.2f}")
            return json.loads(best_match.response)
        
        return None
    
    def cache_response(self, query, response):
        """Cache query-response pair with embedding"""
        
        # Get embedding
        embedding = self.model.encode(query)
        
        # Create unique ID
        cache_id = hashlib.md5(query.encode()).hexdigest()
        
        # Store in Redis
        redis.hset(f"cache:{cache_id}", mapping={
            "query": query,
            "response": json.dumps(response),
            "embedding": embedding.tobytes(),
            "created_at": time.time()
        })
    
    def get_completion_with_semantic_cache(self, query):
        """Get LLM completion with semantic caching"""
        
        # Check semantic cache
        cached = self.get_cached_response(query)
        if cached:
            return cached
        
        # Cache miss - call LLM
        response = call_llm_api(query)
        
        # Cache for future
        self.cache_response(query, response)
        
        return response

# Usage
cache = SemanticCache(similarity_threshold=0.90)

# First query
response1 = cache.get_completion_with_semantic_cache(
    "What is machine learning?"
)

# Similar query (different wording) - still cache hit!
response2 = cache.get_completion_with_semantic_cache(
    "Can you explain ML to me?"
)
# ✨ Semantic cache hit! Similarity: 0.92
```

---

## Summary: Why Redis for Agentic AI

| Capability | Redis Feature | AI Use Case |
|------------|---------------|-------------|
| **Memory** | Lists, expiring keys | Conversation history |
| **Speed** | In-memory, <1ms | Real-time agent responses |
| **Caching** | Key-value with TTL | Reduce API costs 70-90% |
| **Coordination** | Locks, queues | Multi-agent task distribution |
| **Rate Limiting** | Counters, sorted sets | Prevent API abuse |
| **Session State** | Hashes with expiration | Multi-step workflows |
| **Vector Search** | RediSearch module | Semantic similarity (RAG) |
| **Pub/Sub** | Publish-subscribe | Real-time agent events |

**Redis is the "nervous system" of Agentic AI** - connecting agents, maintaining state, and enabling coordination at millisecond speeds.

---

**Total Learning Time for This Document**: 6-8 hours of deep study
**Recommended Practice**: Build 3 of the code examples end-to-end
**Next Steps**: Install Redis locally, run examples, modify for your use case
