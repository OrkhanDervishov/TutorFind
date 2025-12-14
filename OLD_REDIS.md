# Redis Setup for TutorFind

**Caching & Session Management Layer**

Redis serves as the caching and session storage layer for TutorFind, providing:
- Search result caching
- Session token storage
- Rate limiting counters
- Temporary data storage

---

## 📋 Table of Contents

- [Overview](#overview)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage Patterns](#usage-patterns)
- [Data Structures](#data-structures)
- [Performance Tuning](#performance-tuning)
- [Monitoring](#monitoring)
- [Troubleshooting](#troubleshooting)

---

## 🎯 Overview

### What Redis Does in TutorFind

1. **Search Results Caching**
   - Cache frequent search queries
   - Reduce database load
   - Improve search response time (target: < 500ms)

2. **Session Management**
   - Store JWT refresh tokens
   - Maintain active session data
   - Fast session validation

3. **Rate Limiting**
   - API request rate limiting
   - Booking request throttling
   - Login attempt tracking

4. **Temporary Data**
   - Email verification tokens
   - Password reset tokens
   - Booking request queue

---

## 🚀 Installation

### Option 1: Standalone Redis (Development)

#### Windows
```powershell
# Using Chocolatey
choco install redis-64

# Or download from GitHub
# https://github.com/microsoftarchive/redis/releases
# Download Redis-x64-3.2.100.msi and install

# Start Redis
redis-server

# Verify installation
redis-cli ping
# Should return: PONG
```

#### macOS
```bash
# Using Homebrew
brew install redis

# Start Redis service
brew services start redis

# Verify
redis-cli ping
```

#### Linux (Ubuntu/Debian)
```bash
# Update package manager
sudo apt update

# Install Redis
sudo apt install redis-server

# Start Redis service
sudo systemctl start redis-server

# Enable on boot
sudo systemctl enable redis-server

# Verify
redis-cli ping
```

### Option 2: Docker (Recommended)

```bash
# Pull Redis image
docker pull redis:7-alpine

# Run Redis container
docker run -d \
  --name tutorfind-redis \
  -p 6379:6379 \
  -v redis-data:/data \
  redis:7-alpine redis-server --appendonly yes

# Verify
docker exec -it tutorfind-redis redis-cli ping
```

### Option 3: Docker Compose (Best for Development)

See the main project README for the complete Docker Compose setup.

---

## ⚙️ Configuration

### redis.conf (Production Settings)

Create `redis.conf` in your project root:

```conf
# Network
bind 127.0.0.1
port 6379
protected-mode yes

# Memory Management
maxmemory 256mb
maxmemory-policy allkeys-lru

# Persistence
save 900 1
save 300 10
save 60 10000
appendonly yes
appendfsync everysec

# Security
requirepass YOUR_REDIS_PASSWORD_HERE

# Performance
tcp-backlog 511
timeout 300
tcp-keepalive 300

# Logging
loglevel notice
logfile "/var/log/redis/redis-server.log"

# Database
databases 16
```

### Spring Boot Configuration

Update `application.properties`:

```properties
# Redis Configuration
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=YOUR_REDIS_PASSWORD_HERE
spring.redis.database=0
spring.redis.timeout=60000

# Connection Pool (using Lettuce)
spring.redis.lettuce.pool.max-active=8
spring.redis.lettuce.pool.max-idle=8
spring.redis.lettuce.pool.min-idle=2
spring.redis.lettuce.pool.max-wait=-1ms

# Cache Configuration
spring.cache.type=redis
spring.cache.redis.time-to-live=600000
spring.cache.redis.cache-null-values=false
```

### Environment Variables (.env)

```bash
# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_secure_password_here
REDIS_DB=0

# Cache TTL (in seconds)
CACHE_TTL_SEARCH=600        # 10 minutes
CACHE_TTL_TUTOR_PROFILE=1800  # 30 minutes
CACHE_TTL_AVAILABILITY=300   # 5 minutes
```

---

## 💡 Usage Patterns

### 1. Search Results Caching

**Key Pattern**: `search:{hash_of_params}`
**TTL**: 10 minutes

```java
// SearchService.java
@Service
public class SearchService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public List<TutorSearchResult> searchTutors(TutorSearchRequest request) {
        String cacheKey = "search:" + generateSearchHash(request);
        
        // Try cache first
        List<TutorSearchResult> cached = 
            (List<TutorSearchResult>) redisTemplate.opsForValue().get(cacheKey);
            
        if (cached != null) {
            return cached;
        }
        
        // Query database
        List<TutorSearchResult> results = tutorRepository.search(request);
        
        // Cache results for 10 minutes
        redisTemplate.opsForValue().set(
            cacheKey, 
            results, 
            10, 
            TimeUnit.MINUTES
        );
        
        return results;
    }
    
    private String generateSearchHash(TutorSearchRequest request) {
        return DigestUtils.md5Hex(
            request.getCity() + 
            request.getDistrict() + 
            request.getSubject() + 
            request.getMinPrice() + 
            request.getMaxPrice()
        );
    }
}
```

### 2. Session Token Storage

**Key Pattern**: `session:{user_id}`
**TTL**: 24 hours

```java
// AuthService.java
@Service
public class AuthService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public void storeRefreshToken(Long userId, String refreshToken) {
        String key = "session:" + userId;
        redisTemplate.opsForValue().set(
            key, 
            refreshToken, 
            24, 
            TimeUnit.HOURS
        );
    }
    
    public String getRefreshToken(Long userId) {
        String key = "session:" + userId;
        return redisTemplate.opsForValue().get(key);
    }
    
    public void invalidateSession(Long userId) {
        String key = "session:" + userId;
        redisTemplate.delete(key);
    }
}
```

### 3. Rate Limiting

**Key Pattern**: `ratelimit:{endpoint}:{ip}`
**TTL**: 1 hour

```java
// RateLimitFilter.java
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;
    
    private static final int MAX_REQUESTS_PER_HOUR = 100;
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        String ip = request.getRemoteAddr();
        String endpoint = request.getRequestURI();
        String key = "ratelimit:" + endpoint + ":" + ip;
        
        Integer requestCount = redisTemplate.opsForValue().get(key);
        
        if (requestCount == null) {
            redisTemplate.opsForValue().set(key, 1, 1, TimeUnit.HOURS);
        } else if (requestCount < MAX_REQUESTS_PER_HOUR) {
            redisTemplate.opsForValue().increment(key);
        } else {
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("Rate limit exceeded");
            return;
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### 4. Tutor Profile Caching

**Key Pattern**: `tutor:{id}`
**TTL**: 30 minutes

```java
// TutorService.java
@Service
public class TutorService {
    @Autowired
    private RedisTemplate<String, TutorProfile> redisTemplate;
    
    public TutorProfile getTutorProfile(Long tutorId) {
        String cacheKey = "tutor:" + tutorId;
        
        // Try cache
        TutorProfile cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        // Query database
        TutorProfile profile = tutorRepository.findById(tutorId)
            .orElseThrow(() -> new NotFoundException("Tutor not found"));
        
        // Cache for 30 minutes
        redisTemplate.opsForValue().set(
            cacheKey, 
            profile, 
            30, 
            TimeUnit.MINUTES
        );
        
        return profile;
    }
    
    // Invalidate cache when profile is updated
    public void updateTutorProfile(Long tutorId, TutorProfile profile) {
        tutorRepository.save(profile);
        
        // Invalidate cache
        String cacheKey = "tutor:" + tutorId;
        redisTemplate.delete(cacheKey);
    }
}
```

### 5. Verification Tokens

**Key Pattern**: `verify:{token}`
**TTL**: 24 hours

```java
// VerificationService.java
@Service
public class VerificationService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public String createVerificationToken(String email) {
        String token = UUID.randomUUID().toString();
        String key = "verify:" + token;
        
        redisTemplate.opsForValue().set(
            key, 
            email, 
            24, 
            TimeUnit.HOURS
        );
        
        return token;
    }
    
    public String validateToken(String token) {
        String key = "verify:" + token;
        String email = redisTemplate.opsForValue().get(key);
        
        if (email != null) {
            // Delete token after use
            redisTemplate.delete(key);
        }
        
        return email;
    }
}
```

---

## 🗂️ Data Structures

### Key Naming Convention

```
{category}:{identifier}[:{sub_identifier}]

Examples:
search:abc123def456          - Search results cache
session:1001                 - User session
tutor:42                     - Tutor profile
ratelimit:/api/search:192.168.1.1  - Rate limiting
verify:uuid-token            - Verification token
booking:queue:123            - Booking queue item
```

### Data Types Used

1. **Strings** (Simple key-value)
   - Session tokens
   - Verification tokens
   - Rate limit counters

2. **Hashes** (Object storage)
   - User sessions with multiple fields
   - Cached objects with metadata

3. **Lists** (Queues)
   - Booking request queue
   - Notification queue

4. **Sets** (Unique collections)
   - Active user IDs
   - Online tutors

5. **Sorted Sets** (Ranking)
   - Top-rated tutors
   - Popular subjects

---

## 📊 Performance Tuning

### Memory Optimization

```conf
# Set max memory based on your server
maxmemory 1gb

# Eviction policy - remove least recently used keys
maxmemory-policy allkeys-lru

# Don't persist everything
save ""
appendonly no  # For pure cache use
```

### Connection Pooling (Spring Boot)

```java
@Configuration
public class RedisConfig {
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration
            .builder()
            .commandTimeout(Duration.ofSeconds(2))
            .shutdownTimeout(Duration.ZERO)
            .build();
            
        RedisStandaloneConfiguration serverConfig = 
            new RedisStandaloneConfiguration();
        serverConfig.setHostName("localhost");
        serverConfig.setPort(6379);
        serverConfig.setPassword("your_password");
        
        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        
        // Use Jackson for JSON serialization
        Jackson2JsonRedisSerializer<Object> serializer = 
            new Jackson2JsonRedisSerializer<>(Object.class);
            
        template.setDefaultSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        
        return template;
    }
}
```

---

## 📈 Monitoring

### Key Metrics to Monitor

1. **Memory Usage**
```bash
redis-cli INFO memory
```

2. **Hit Rate**
```bash
redis-cli INFO stats | grep keyspace
```

3. **Connected Clients**
```bash
redis-cli INFO clients
```

4. **Commands Per Second**
```bash
redis-cli INFO stats | grep instantaneous
```

### Redis CLI Commands

```bash
# Connect to Redis
redis-cli -h localhost -p 6379 -a your_password

# Monitor all commands in real-time
MONITOR

# Get all keys (use with caution in production!)
KEYS *

# Get specific keys
KEYS search:*
KEYS session:*

# Get key info
TYPE session:1001
TTL session:1001
GET session:1001

# Delete keys
DEL session:1001

# Clear all data (DANGEROUS!)
FLUSHALL

# Get memory usage
MEMORY USAGE session:1001

# Get server info
INFO
INFO memory
INFO stats
INFO clients
```

---

## 🔍 Troubleshooting

### Common Issues

#### 1. Connection Refused
```bash
# Check if Redis is running
sudo systemctl status redis-server

# Or on Windows
redis-server --service-status

# Start Redis if not running
sudo systemctl start redis-server
```

#### 2. Authentication Failed
```bash
# Check password in redis.conf
requirepass YOUR_PASSWORD

# Restart Redis after config change
sudo systemctl restart redis-server

# Test authentication
redis-cli -a YOUR_PASSWORD ping
```

#### 3. Out of Memory
```bash
# Check memory usage
redis-cli INFO memory

# Increase maxmemory in redis.conf
maxmemory 2gb

# Or clear cache
redis-cli FLUSHALL
```

#### 4. Slow Performance
```bash
# Check slow queries
redis-cli SLOWLOG GET 10

# Check connected clients
redis-cli INFO clients

# Check memory fragmentation
redis-cli INFO memory | grep fragmentation
```

### Debugging Commands

```bash
# See what keys exist
redis-cli --scan --pattern 'search:*'

# Get key value
redis-cli GET session:1001

# Check TTL
redis-cli TTL session:1001

# Monitor commands
redis-cli MONITOR

# Check latency
redis-cli --latency
```

---

## 🧪 Testing Redis

### Test Script (test-redis.sh)

```bash
#!/bin/bash

echo "Testing Redis connection..."

# Test ping
if redis-cli -a $REDIS_PASSWORD ping | grep -q PONG; then
    echo "✅ Redis is running"
else
    echo "❌ Redis connection failed"
    exit 1
fi

# Test write
redis-cli -a $REDIS_PASSWORD SET test:key "test_value"
echo "✅ Write test passed"

# Test read
VALUE=$(redis-cli -a $REDIS_PASSWORD GET test:key)
if [ "$VALUE" = "test_value" ]; then
    echo "✅ Read test passed"
else
    echo "❌ Read test failed"
    exit 1
fi

# Test expiry
redis-cli -a $REDIS_PASSWORD SETEX test:expire 5 "expires_soon"
sleep 6
EXPIRED=$(redis-cli -a $REDIS_PASSWORD GET test:expire)
if [ -z "$EXPIRED" ]; then
    echo "✅ Expiry test passed"
else
    echo "❌ Expiry test failed"
    exit 1
fi

# Cleanup
redis-cli -a $REDIS_PASSWORD DEL test:key

echo "✅ All Redis tests passed"
```

---

## 📦 Dependencies (Spring Boot)

Add to `pom.xml`:

```xml
<!-- Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Lettuce (Redis client) -->
<dependency>
    <groupId>io.lettuce.core</groupId>
    <artifactId>lettuce-core</artifactId>
</dependency>

<!-- Optional: For caching annotations -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

---

## 📝 Quick Reference

### Start/Stop Commands

```bash
# Linux/macOS
sudo systemctl start redis-server
sudo systemctl stop redis-server
sudo systemctl restart redis-server

# Docker
docker start tutorfind-redis
docker stop tutorfind-redis
docker restart tutorfind-redis

# Windows (as service)
redis-server --service-start
redis-server --service-stop
```

### Essential Commands

| Command | Purpose |
|---------|---------|
| `PING` | Test connection |
| `SET key value` | Store value |
| `GET key` | Retrieve value |
| `DEL key` | Delete key |
| `KEYS pattern` | Find keys |
| `TTL key` | Check expiry |
| `EXPIRE key seconds` | Set expiry |
| `FLUSHDB` | Clear current DB |
| `INFO` | Server info |

---

## 🚀 Next Steps

1. **Install Redis** using one of the methods above
2. **Configure** Spring Boot to connect to Redis
3. **Implement caching** in SearchService
4. **Add session management** in AuthService
5. **Test** the connection and caching
6. **Monitor** performance improvements

---

**Last Updated:** December 11, 2025  
**Redis Version:** 7.x  
**Spring Boot Version:** 4.0.0