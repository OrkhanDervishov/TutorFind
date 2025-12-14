# TutorFind - Backend API

**A web platform connecting learners with vetted local tutors by city, district, subject, availability, price, and rating.**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18.1-blue.svg)](https://www.postgresql.org/)
[![Status](https://img.shields.io/badge/Status-Early%20Development-yellow.svg)]()

**Team 13** | |

---

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Current Status](#-current-status)
- [Tech Stack](#-tech-stack)
- [Quick Start](#-quick-start)
- [What's Implemented](#-whats-implemented)
- [What's Missing](#-whats-missing)
- [Implementation Roadmap](#-implementation-roadmap)
- [API Documentation](#-api-documentation)
- [Database Schema](#-database-schema)
- [Security Issues](#-security-issues)
- [Contributing](#-contributing)

---

## 🎯 Project Overview

TutorFind is a web platform that matches learners with qualified local tutors based on:

- **Location**: City → District (no map required)
- **Subject**: Math, Science, Languages, etc.
- **Availability**: Weekly recurring time slots
- **Price**: Hourly rates within budget
- **Rating**: Vetted tutors with reviews

### MVP Goals

- **Time-to-match**: < 10 minutes from search to first booking request
- **Tutor utilization**: ≥ 70% capacity by Week 8
- **Search latency**: P95 < 1.2s
- **Platform scale**: 4k users, 1k tutors, 20k searches/month

---

## 📊 Current Status

### ⚠️ CRITICAL: ~15% Complete

| Component       | Status     | Progress |
| --------------- | ---------- | -------- |
| Authentication  | 🟡 Partial | 40%      |
| User Management | 🟡 Partial | 30%      |
| Tutor Profiles  | 🔴 Minimal | 10%      |
| Search & Filter | 🔴 Missing | 0%       |
| Booking System  | 🔴 Missing | 0%       |
| Classes         | 🔴 Missing | 0%       |
| Reviews         | 🔴 Minimal | 5%       |
| Admin Console   | 🔴 Missing | 0%       |

**Legend**: 🟢 Complete | 🟡 Partial | 🔴 Not Started

---

## 🛠️ Tech Stack

### Backend

- **Framework**: Spring Boot 4.0.0
- **Language**: Java 17
- **Database**: PostgreSQL 18.1
- **ORM**: Hibernate/JPA
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven 3.9.11

### Development Tools

- **IDE**: IntelliJ IDEA / Eclipse
- **Database Client**: pgAdmin 4
- **API Testing**: Postman / Insomnia

### Planned Integrations

- **Caching**: Redis (not implemented)
- **Email**: SMTP service (not implemented)
- **Monitoring**: Application Performance Monitoring (not implemented)

---

## 🚀 Quick Start

### Prerequisites

- Java JDK 17+
- Maven 3.6+
- PostgreSQL 18+
- Git

### Installation

1. **Clone the repository**

```bash
git clone https://github.com/.../TutorFind.git
cd TutorFind
```

2. **Configure PostgreSQL**

```bash
# Create database
psql -U postgres
CREATE DATABASE TutorFind;
\q
```

3. **Configure application.properties**

```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/TutorFind
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

4. **Build and Run**

```bash
# Build
./mvnw clean install -DskipTests

# Run
./mvnw spring-boot:run
```

5. **Verify**

```
Application started on: http://localhost:8080
```

---

## ✅ What's Implemented

### 1. Authentication (Partial)

- [x] User registration endpoint
- [x] Login endpoint
- [x] User roles (TUTOR, LEARNER, ADMIN)

**Endpoints:**

```
POST /register       - Register new user
POST /login          - User login
```

**⚠️ WARNING**: Passwords stored in plain text, no session management

### 2. User Management (Partial)

- [x] User entity with basic fields
- [x] User CRUD operations
- [x] Profile fields (name, age, phone, profile picture)

**Endpoints:**

```
POST /addUser        - Create user
GET  /user{id}       - Get user by ID
```

### 3. Tutor Profiles (Minimal)

- [x] TutorDetails entity
- [x] Rating field (1-5 scale)
- [x] Bio field (max 1000 chars)

**Missing**: City, districts, subjects, price, qualifications, availability

### 4. Database

- [x] PostgreSQL connection
- [x] Users table
- [x] Tutor_details table
- [x] HikariCP connection pool

---

## ❌ What's Missing

### 🚨 CRITICAL Security Issues

1. **Plain Text Passwords**

   - Variable named `passwordHashed` but NOT actually hashed
   - **Must fix before production**

2. **No Authentication State**

   - Login returns string, no JWT/session
   - No way to verify user identity on subsequent requests

3. **No Authorization**
   - No role-based access control
   - All endpoints are public

### 🔴 MVP Blockers (Must Implement)

#### 1. Location System (0%)

```
❌ Cities table
❌ Districts table
❌ Seed data (Baku districts)
❌ Location API endpoints
```

#### 2. Tutor Discovery (10%)

```
❌ Complete tutor profile fields:
   - City & districts
   - Subjects taught
   - Price per hour
   - Qualifications
   - Experience
   - Languages
❌ Tutor profile CRUD endpoints
❌ Subject catalog
```

#### 3. Availability System (0%)

```
❌ AvailabilitySlot table
❌ Weekly recurring slots
❌ Availability CRUD endpoints
❌ Slot validation logic
```

#### 4. Search & Filter (0%)

**This is THE core feature**

```
❌ Search by city/district
❌ Filter by subject
❌ Filter by price range
❌ Filter by rating
❌ Filter by availability
❌ Sorting options
❌ Pagination
```

#### 5. Booking System (0%)

```
❌ BookingRequest table
❌ Request workflow (pending → accepted/declined)
❌ Booking endpoints
❌ Status updates
❌ Simple inbox
```

#### 6. Classes & Enrollment (0%)

```
❌ Classes table
❌ Enrollments table
❌ Class roster management
❌ Class CRUD endpoints
```

#### 7. Feedback System (0%)

```
❌ Feedback table
❌ Per-student notes
❌ Privacy controls
❌ Feedback endpoints
```

#### 8. Reviews & Ratings (5%)

```
✅ Rating field exists
❌ Reviews table
❌ Review submission
❌ Moderation workflow
❌ Rating aggregation
❌ Review endpoints
```

#### 9. Admin Console (0%)

```
❌ Tutor verification
❌ Review moderation
❌ User management
❌ Catalog management (subjects, locations)
❌ Analytics dashboard
```

---

## 🗺️ Implementation Roadmap

### Week 1-2: Foundation (CRITICAL)

#### Priority 1: Security Overhaul 🚨

```java
// 1. Add Spring Security
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

// 2. Implement password hashing
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// 3. Add JWT authentication
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
```

**Tasks:**

- [ ] Install Spring Security
- [ ] Implement BCrypt password hashing
- [ ] Create JWT utilities
- [ ] Add authentication filter
- [ ] Implement role-based authorization
- [ ] Secure all endpoints

#### Priority 2: Database Foundation

```sql
-- Create all required tables
CREATE TABLE cities (...);
CREATE TABLE districts (...);
CREATE TABLE subjects (...);
CREATE TABLE tutor_profiles (...);
CREATE TABLE tutor_subjects (...);
CREATE TABLE availability_slots (...);
CREATE TABLE booking_requests (...);
CREATE TABLE classes (...);
CREATE TABLE enrollments (...);
CREATE TABLE feedback (...);
CREATE TABLE reviews (...);
```

**Tasks:**

- [ ] Design complete database schema
- [ ] Create migration scripts
- [ ] Add all indexes
- [ ] Set up foreign key constraints
- [ ] Seed initial data (cities, districts, subjects)

#### Priority 3: Location System

**Tasks:**

- [ ] Create City entity and repository
- [ ] Create District entity and repository
- [ ] Create LocationService
- [ ] Implement location endpoints
- [ ] Seed Baku districts

```
Nasimi, Sabail, Yasamal, Nizami,
Khatai, Sabunchu, Surakhani, Garadagh,
Binagadi, Narimanov, Pirallahi
```

---

### Week 3-4: Core Features

#### Tutor Profiles & Subjects

**Tasks:**

- [ ] Extend TutorProfile entity with all fields
- [ ] Create Subject entity and catalog
- [ ] Implement tutor profile CRUD
- [ ] Add subject association endpoints
- [ ] Calculate profile completeness

**New Endpoints:**

```
POST   /api/tutors/profile
GET    /api/tutors/{id}/profile
PUT    /api/tutors/{id}/profile
GET    /api/tutors/{id}/subjects
POST   /api/tutors/{id}/subjects
DELETE /api/tutors/{id}/subjects/{id}
```

#### Availability System

**Tasks:**

- [ ] Create AvailabilitySlot entity
- [ ] Implement slot CRUD
- [ ] Add validation (no overlapping slots)
- [ ] Implement recurring weekly slots

**New Endpoints:**

```
GET    /api/tutors/{id}/availability
POST   /api/tutors/{id}/availability
PUT    /api/tutors/{id}/availability/{slotId}
DELETE /api/tutors/{id}/availability/{slotId}
```

#### Search & Filter System

**Tasks:**

- [ ] Create SearchService
- [ ] Implement dynamic query builder
- [ ] Add all filters (city, district, subject, price, rating, availability)
- [ ] Implement pagination
- [ ] Add sorting options
- [ ] Create database indexes
- [ ] Optimize for P95 < 1.2s

**Endpoint:**

```
GET /api/search/tutors?
    city={city}&
    district={district}&
    subject={subject}&
    minPrice={min}&
    maxPrice={max}&
    minRating={rating}&
    day={MON}&
    time={18:00}&
    sortBy={rating|price|experience}&
    order={asc|desc}&
    page={0}&
    size={20}
```

---

### Week 5-6: Booking & Classes

#### Booking System

**Tasks:**

- [ ] Create BookingRequest entity
- [ ] Implement booking workflow
- [ ] Add status management (PENDING → ACCEPTED/DECLINED)
- [ ] Create inbox view

**New Endpoints:**

```
POST /api/bookings                - Create booking
GET  /api/bookings/sent           - Learner: sent requests
GET  /api/bookings/received       - Tutor: received requests
PUT  /api/bookings/{id}/accept    - Tutor: accept
PUT  /api/bookings/{id}/decline   - Tutor: decline
PUT  /api/bookings/{id}/cancel    - Cancel booking
```

#### Classes & Enrollment

**Tasks:**

- [ ] Create Class entity
- [ ] Create Enrollment entity
- [ ] Implement class CRUD
- [ ] Add roster management
- [ ] Track attendance

**New Endpoints:**

```
POST   /api/classes                - Create class
GET    /api/classes                - List classes
GET    /api/classes/{id}           - Get class
PUT    /api/classes/{id}           - Update class
GET    /api/classes/{id}/roster    - Get enrollments
POST   /api/classes/{id}/enroll    - Enroll learner
DELETE /api/classes/{id}/drop      - Drop from class
```

---

### Week 7: Social Features

#### Feedback System

**Tasks:**

- [ ] Create Feedback entity
- [ ] Implement feedback CRUD
- [ ] Add privacy controls (visible only to student)
- [ ] Create feedback history view

**New Endpoints:**

```
POST /api/feedback                - Create feedback
GET  /api/feedback/learner/{id}   - Get learner's feedback
GET  /api/feedback/tutor/{id}     - Get tutor's feedback
PUT  /api/feedback/{id}           - Update feedback
```

#### Reviews & Ratings

**Tasks:**

- [ ] Create Review entity
- [ ] Implement review submission
- [ ] Add moderation workflow
- [ ] Calculate aggregated ratings
- [ ] Allow tutor responses

**New Endpoints:**

```
POST /api/reviews                 - Submit review
GET  /api/reviews/tutor/{id}      - Get tutor reviews
GET  /api/reviews/pending         - Admin: pending reviews
PUT  /api/reviews/{id}/approve    - Admin: approve
PUT  /api/reviews/{id}/reject     - Admin: reject
POST /api/reviews/{id}/response   - Tutor: respond
```

---

### Week 8: Admin & Polish

#### Admin Console

**Tasks:**

- [ ] Tutor verification workflow
- [ ] Review moderation interface
- [ ] User management (ban/unban)
- [ ] Catalog management
- [ ] Analytics dashboard

**New Endpoints:**

```
GET    /api/admin/tutors/pending
PUT    /api/admin/tutors/{id}/verify
POST   /api/admin/subjects
GET    /api/admin/dashboard/stats
```

#### Performance & Polish

**Tasks:**

- [ ] Add Redis caching
- [ ] Optimize queries
- [ ] Add API documentation (Swagger)
- [ ] Implement logging
- [ ] Add monitoring
- [ ] Write tests (80% coverage)

---

## 📚 API Documentation

### Current Endpoints (6)

#### Authentication

```http
POST /register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "username": "john_doe",
  "firstName": "John",
  "lastName": "Doe",
  "age": 25,
  "phoneNumber": "+994501234567",
  "role": "TUTOR"
}

Response: 200 OK
"User Registered Successfully"
```

```http
POST /login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response: 200 OK
"Login Successful"
```

#### User Management

```http
GET /user{id}

Response: 200 OK
{
  "id": 1,
  "username": "john_doe",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "age": 25,
  "phoneNumber": "+994501234567"
}
```

### Required Endpoints (74+)

See [Implementation Roadmap](#-implementation-roadmap) for complete list of endpoints to implement.

---

## 🗄️ Database Schema

### Current Tables (3)

#### users

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hashed VARCHAR(255), -- WARNING: Not actually hashed!
    first_name VARCHAR(32),
    last_name VARCHAR(32),
    age INTEGER CHECK(age BETWEEN 18 AND 100),
    phone_number VARCHAR(20),
    profile_picture BYTEA,
    role VARCHAR(20) -- TUTOR, LEARNER, ADMIN
);
```

#### tutor_details

```sql
CREATE TABLE tutor_details (
    id BIGSERIAL PRIMARY KEY,
    rating DECIMAL(3,2) CHECK(rating BETWEEN 1 AND 5),
    bio VARCHAR(1000)
);
```

### Required Tables (15+)

#### Complete Schema Needed:

1. **users** - Needs extension (email verification, timestamps, status)
2. **tutor_profiles** - Complete rewrite needed
3. **cities** - NOT EXISTS ❌
4. **districts** - NOT EXISTS ❌
5. **subjects** - NOT EXISTS ❌
6. **tutor_subjects** - NOT EXISTS ❌
7. **availability_slots** - NOT EXISTS ❌
8. **booking_requests** - NOT EXISTS ❌
9. **classes** - NOT EXISTS ❌
10. **enrollments** - NOT EXISTS ❌
11. **feedback** - NOT EXISTS ❌
12. **reviews** - NOT EXISTS ❌
13. **review_votes** - NOT EXISTS ❌
14. **admin_actions** - NOT EXISTS ❌
15. **verification_requests** - NOT EXISTS ❌

**Detailed schema**: See `IMPLEMENTATION_STATUS.md` for complete table definitions.

---

## 🔒 Security Issues

### 🚨 CRITICAL Vulnerabilities

1. **Plain Text Passwords**

```java
// CURRENT (INSECURE):
user.setPasswordHashed(request.getPassword()); // NOT hashed!

// REQUIRED:
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedPassword = encoder.encode(request.getPassword());
user.setPasswordHashed(hashedPassword);
```

2. **No Authentication State**

```java
// CURRENT: Login returns string, no session
@PostMapping("/login")
public ResponseEntity<String> login(@RequestBody LoginRequest request) {
    // ... validation
    return ResponseEntity.ok("Login Successful");
}

// REQUIRED: Return JWT token
@PostMapping("/api/auth/login")
public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    User user = authenticate(request);
    String token = jwtUtil.generateToken(user);
    return ResponseEntity.ok(new AuthResponse(token, user));
}
```

3. **No Authorization**

```java
// REQUIRED: Add security configuration
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/tutors/**").hasAnyRole("TUTOR", "ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

### Other Security Needs

- [ ] Input validation with `@Valid`
- [ ] SQL injection prevention
- [ ] XSS protection
- [ ] CORS configuration
- [ ] Rate limiting
- [ ] HTTPS enforcement
- [ ] Email verification
- [ ] Password reset with tokens

---

## 🧪 Testing

### Current Status: ❌ 0% Coverage

**Required:**

- [ ] Unit tests for services (target: 80% coverage)
- [ ] Integration tests for controllers
- [ ] Repository tests
- [ ] End-to-end API tests
- [ ] Security tests
- [ ] Performance tests (search latency < 1.2s)

**Example Test Structure:**

```
src/test/java/
├── com/team13/TutorFind/
│   ├── service/
│   │   ├── UserServiceTest.java
│   │   ├── TutorServiceTest.java
│   │   └── SearchServiceTest.java
│   ├── controller/
│   │   ├── AuthControllerTest.java
│   │   └── TutorControllerTest.java
│   └── integration/
│       ├── AuthenticationIntegrationTest.java
│       └── SearchIntegrationTest.java
```

---

## 📊 Project Metrics

### Current Progress

| Metric                | Target | Current | Gap             |
| --------------------- | ------ | ------- | --------------- |
| **Database Tables**   | 15     | 3       | 12 missing      |
| **API Endpoints**     | 80+    | 6       | 74+ missing     |
| **Test Coverage**     | 80%    | 0%      | 80%             |
| **Security Score**    | A      | F       | Critical issues |
| **Features Complete** | 100%   | 15%     | 85%             |

### Development Velocity Needed

With 8 weeks remaining and 85% of features missing:

- **Week 1-2**: Security + Database (30%)
- **Week 3-4**: Search + Profiles (25%)
- **Week 5-6**: Booking + Classes (20%)
- **Week 7**: Reviews + Feedback (15%)
- **Week 8**: Admin + Polish (10%)

---

## 🤝 Contributing

### Development Workflow

1. **Create feature branch**

```bash
git checkout -b feature/booking-system
```

2. **Make changes**

```bash
# Write code
# Write tests
# Update documentation
```

3. **Test locally**

```bash
./mvnw test
./mvnw spring-boot:run
```

4. **Commit with meaningful message**

```bash
git commit -m "feat: implement booking request workflow"
```

5. **Push and create PR**

```bash
git push origin feature/booking-system
```

### Code Standards

- Follow Java naming conventions
- Write JavaDoc for public methods
- Maintain 80% test coverage
- Use DTOs for API requests/responses
- Implement proper error handling
- Add logging with SLF4J

### Commit Message Format

```
<type>(<scope>): <subject>

Types: feat, fix, docs, style, refactor, test, chore
Scope: auth, user, tutor, search, booking, etc.

Examples:
feat(auth): implement JWT authentication
fix(search): correct price filter logic
docs(readme): update API endpoints
```

---

## 📞 Support & Resources

### Documentation

- **SRS Document**: `software_documentation.docx`
- **Implementation Status**: `IMPLEMENTATION_STATUS.md`
- **API Docs**: TBD (Swagger to be added)

### Team Communication

- **Platform**: Notion / Google Docs
- **Meetings**: Daily 15-min standups
- **Sprint**: 1 week iterations

### Useful Links

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT Introduction](https://jwt.io/introduction)

---

## 📝 License

This project is developed for academic purposes as part of ....

---

## 🎯 Next Steps

### This Week (Week 1)

**Day 1-2: Security Crisis**

```bash
# 1. Add Spring Security
./mvnw dependency:get -Dartifact=org.springframework.boot:spring-boot-starter-security

# 2. Implement BCrypt
# 3. Create JWT utilities
# 4. Secure all endpoints
```

**Day 3-4: Database Foundation**

```sql
-- Create migration script
-- Add all 15+ tables
-- Create indexes
-- Seed initial data
```

**Day 5-7: Location System**

```java
// Implement City, District entities
// Create LocationService
// Build location endpoints
// Seed Baku districts
```

### Week 2: Start Core Features

- Extend TutorProfile
- Implement Subjects catalog
- Build Availability system
- Begin Search implementation

---

**Generated:** December 11, 2025  
**Last Updated:** December 11, 2025  
**Version:** 1.0  
**Team:** Team 13

---

## 🔍 Quick Reference

### Environment Setup

```bash
# Java version
java -version  # Must be 17+

# Maven version
mvn -version   # Must be 3.6+

# PostgreSQL
psql --version # Must be 12+
```

### Build Commands

```bash
# Clean build
./mvnw clean install -DskipTests

# Run application
./mvnw spring-boot:run

# Run tests
./mvnw test

# Package JAR
./mvnw package
```

### Database Commands

```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE TutorFind;

# List databases
\l

# Connect to database
\c TutorFind

# List tables
\dt

# Describe table
\d users
```

---

**Ready to contribute? Start with the [Implementation Roadmap](#-implementation-roadmap)!**
