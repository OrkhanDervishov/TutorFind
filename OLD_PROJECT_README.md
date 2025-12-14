# 🎓 TutorFind - Complete Platform

**A production-ready local tutor discovery platform connecting learners with vetted tutors and group classes.**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18.1-blue.svg)](https://www.postgresql.org/)
[![Status](https://img.shields.io/badge/Status-Production%20Ready-success.svg)]()

## **Team 13** | ADA University | CSCI 3509: Intro to Software Engineering

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Architecture](#-architecture)
- [Quick Start](#-quick-start)
- [Services](#-services)
- [Development Setup](#-development-setup)
- [Production Deployment](#-production-deployment)
- [API Documentation](#-api-documentation)
- [Troubleshooting](#-troubleshooting)
- [Project Status](#-project-status)

---

## 🎯 Project Overview

TutorFind is a comprehensive web platform that matches learners with qualified local tutors based on:

- **Location**: City and district filtering (Baku, Azerbaijan)
- **Subject**: Math, Science, Languages, Computer Science, Test Prep
- **Availability**: Weekly recurring time slots
- **Price**: Monthly rate filtering
- **Rating**: Verified tutor reviews and ratings

### Core Features

**For Learners:**

- 🔍 Advanced search with multiple filters
- 📅 View tutor availability in real-time
- 📝 Send booking requests with notes
- 📊 Dashboard to track bookings (pending/accepted/declined)
- ⭐ Read tutor reviews and ratings
- 👤 Manage profile and preferences

**For Tutors:**

- 📋 Complete profile management (bio, qualifications, subjects)
- 💰 Set pricing (monthly rate)
- 📅 Manage weekly availability slots
- 📥 Booking inbox with status filtering
- 💬 Respond to booking requests
- 📈 View booking history

**For Admins:**

- ✅ Verify tutor accounts
- 🛡️ Moderate reviews
- 👥 Manage users
- 📚 Manage subject catalog
- 📊 Platform analytics

---

## 🏗️ Architecture

### System Components

```
┌─────────────────────────────────────────────────────────────┐
│                    tutorfind_frontend                       │
│        React 18 + Vite + Tailwind CSS + Router              │
│                  Port: 5173 (dev)                           │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP/REST API
┌──────────────────────▼──────────────────────────────────────┐
│                    tutorfind_backend                        │
│         Spring Boot 4.0 + Java 17 + Hibernate               │
│                  Port: 8080                                 │
└──────────────┬──────────────────────────────────────────────┘
               │ JDBC
       ┌───────▼──────┐
       │  PostgreSQL  │
       │  Database    │
       │  Port: 5432  │
       └──────────────┘
```

### Technology Stack

| Layer         | Technology                   | Purpose                |
| ------------- | ---------------------------- | ---------------------- |
| **Frontend**  | React 18, Vite, Tailwind CSS | SPA User Interface     |
| **Backend**   | Spring Boot 4.0, Java 17     | REST API Server        |
| **Database**  | PostgreSQL 18.1              | Primary Data Store     |
| **Cache**     | Redis 7                      | Session & Search Cache |
| **Container** | Docker & Docker Compose      | Orchestration          |

---

## 🚀 Quick Start

### Prerequisites

- **Docker** (v20.10+) and **Docker Compose** (v2.0+)
- **Node.js** (v18+) and **npm** (for local frontend development)
- **Java JDK** 17+ and **Maven** 3.9+ (for local backend development)
- **Git**

### Option 1: Docker Compose (Recommended)

**Run the entire stack with one command:**

```bash
# Clone repository
git clone https://github.com/OrkhanDervishov/TutorFind.git
cd TutorFind

# Start all services
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

**Access the application:**

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- PostgreSQL: localhost:5432
- Redis: localhost:6379

### Option 2: Local Development

**Terminal 1 - Database & Cache:**

```bash
# Start PostgreSQL and Redis
docker-compose up -d postgres redis

# Verify they're running
docker-compose ps
```

**Terminal 2 - Backend:**

```bash
cd backend
./mvnw clean install -DskipTests
./mvnw spring-boot:run
```

**Terminal 3 - Frontend:**

```bash
cd frontend
npm install
npm run dev
```

---

## 📦 Services

### 1. Frontend (React SPA)

**Directory**: `frontend/`  
**Port**: 5173 (dev), 4173 (preview)  
**Documentation**: [Frontend README](frontend/README.md)

**Features Implemented:**

- ✅ Home page with CTA
- ✅ Advanced search with filters
- ✅ Tutor profile pages
- ✅ Booking workflow
- ✅ Tutor dashboard (bookings inbox)
- ✅ Learner dashboard
- ✅ Profile settings (role-aware)
- ✅ Availability management
- ✅ Authentication UI (mock)

**Development:**

```bash
cd frontend
npm install
npm run dev          # Start dev server
npm run build        # Production build
npm run preview      # Preview production build
```

### 2. Backend (Spring Boot API)

**Directory**: `backend/`  
**Port**: 8080  
**Documentation**:

- [Backend README](backend/README.md)
- [Implementation Status](backend/IMPLEMENTATION_STATUS.md)

**Current Status**: ~15% Complete

**Implemented:**

- ✅ User registration and login
- ✅ Basic user CRUD
- ✅ Minimal tutor profile support

**Missing (Critical):**

- ❌ Security (plain text passwords, no JWT)
- ❌ Complete database schema (missing 12+ tables)
- ❌ Search & filter API
- ❌ Booking system
- ❌ Classes & enrollment
- ❌ Reviews & ratings
- ❌ Admin console

**Development:**

```bash
cd backend
./mvnw clean install -DskipTests
./mvnw spring-boot:run
```

### 3. PostgreSQL Database

**Port**: 5432  
**Database**: TutorFind  
**Documentation**: [PostgreSQL README](docs/POSTGRESQL_README.md)

**Schema**: 15+ tables including:

- users, tutor_profiles, cities, districts
- subjects, tutor_subjects, availability_slots
- booking_requests, classes, enrollments
- feedback, reviews, admin_actions

**Access:**

```bash
# Via Docker
docker exec -it tutorfind-postgres psql -U postgres -d TutorFind

# Via local psql
psql -U postgres -h localhost -p 5432 -d TutorFind
```

### 4. Redis Cache

**Port**: 6379  
**Documentation**: [Redis README](docs/REDIS_README.md)

**Use Cases:**

- Search result caching (10 min TTL)
- Session token storage (24 hour TTL)
- Rate limiting counters (1 hour TTL)
- Tutor profile caching (30 min TTL)

**Access:**

```bash
# Via Docker
docker exec -it tutorfind-redis redis-cli

# Via local redis-cli
redis-cli -h localhost -p 6379
```

---

## 🛠️ Development Setup

### Full Docker Compose Configuration

Create `docker-compose.yml` in project root:

```yaml
version: "3.8"

services:
  # PostgreSQL Database
  postgres:
    image: postgres:18-alpine
    container_name: tutorfind-postgres
    environment:
      POSTGRES_DB: TutorFind
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD:-tutorfind_dev_password}
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./backend/src/main/resources/db/migration:/docker-entrypoint-initdb.d
    networks:
      - tutorfind-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres -d TutorFind"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Redis Cache
  redis:
    image: redis:7-alpine
    container_name: tutorfind-redis
    command: redis-server --requirepass ${REDIS_PASSWORD:-tutorfind_redis_password}
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    networks:
      - tutorfind-network
    healthcheck:
      test: ["CMD", "redis-cli", "--raw", "incr", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Spring Boot Backend
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: tutorfind-backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/TutorFind
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD:-tutorfind_dev_password}
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PORT: 6379
      SPRING_REDIS_PASSWORD: ${REDIS_PASSWORD:-tutorfind_redis_password}
      SPRING_PROFILES_ACTIVE: docker
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - tutorfind-network
    restart: unless-stopped

  # React Frontend
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: tutorfind-frontend
    environment:
      VITE_API_URL: http://localhost:8080
    ports:
      - "5173:5173"
    depends_on:
      - backend
    networks:
      - tutorfind-network
    restart: unless-stopped

volumes:
  postgres-data:
    driver: local
  redis-data:
    driver: local

networks:
  tutorfind-network:
    driver: bridge
```

### Environment Variables

Create `.env` file in project root:

```bash
# Database
POSTGRES_PASSWORD=tutorfind_dev_password
POSTGRES_USER=postgres
POSTGRES_DB=TutorFind

# Redis
REDIS_PASSWORD=tutorfind_redis_password

# Backend
SPRING_PROFILES_ACTIVE=dev
JWT_SECRET=your_jwt_secret_key_min_256_bits

# Frontend
VITE_API_URL=http://localhost:8080

# Ports (optional override)
FRONTEND_PORT=5173
BACKEND_PORT=8080
POSTGRES_PORT=5432
REDIS_PORT=6379
```

### Backend Dockerfile

Create `backend/Dockerfile`:

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# Production stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Frontend Dockerfile

Create `frontend/Dockerfile`:

```dockerfile
FROM node:18-alpine AS build
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build application
RUN npm run build

# Production stage with nginx
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

Create `frontend/nginx.conf`:

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # SPA routing
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API proxy (optional - if you want same-origin requests)
    location /api {
        proxy_pass http://backend:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;
}
```

---

## 🚀 Production Deployment

### Build for Production

```bash
# Build all services
docker-compose build

# Start in detached mode
docker-compose up -d

# View logs
docker-compose logs -f backend frontend
```

### Environment-Specific Configs

**Production** (`application-prod.properties`):

```properties
# Database
spring.datasource.url=jdbc:postgresql://prod-db-host:5432/TutorFind
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Redis
spring.redis.host=prod-redis-host
spring.redis.port=6379
spring.redis.password=${REDIS_PASSWORD}

# Security
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# Server
server.port=8080
server.error.include-message=never
server.error.include-stacktrace=never
```

### Health Checks

```bash
# Backend health
curl http://localhost:8080/actuator/health

# Database connection
docker exec tutorfind-postgres pg_isready -U postgres

# Redis connection
docker exec tutorfind-redis redis-cli ping
```

---

## 📚 API Documentation

### Authentication

```http
POST /api/auth/register
POST /api/auth/login
POST /api/auth/logout
POST /api/auth/refresh
GET  /api/auth/me
```

### Search & Discovery

```http
GET /api/tutors?city={city}&district={district}&subject={subject}&minPrice={min}&maxPrice={max}&minRating={rating}&day={MON}&sortBy={rating}&page={0}&size={20}
GET /api/tutors/{id}
```

### Bookings

```http
POST /api/bookings
GET  /api/bookings/sent
GET  /api/bookings/received
PUT  /api/bookings/{id}/accept
PUT  /api/bookings/{id}/decline
```

### Profile Management

```http
GET  /api/me/profile
PUT  /api/me/profile
POST /api/tutors/profile
GET  /api/tutors/{id}/availability
POST /api/tutors/{id}/availability
```

**Full API Documentation**: See [Backend README](backend/README.md) for complete endpoint list.

---

## 🐛 Troubleshooting

### Common Issues

#### 1. Docker Services Won't Start

```bash
# Check Docker is running
docker info

# Check ports are not in use
netstat -an | findstr "5432 6379 8080 5173"

# Remove existing containers
docker-compose down -v

# Rebuild and restart
docker-compose up --build -d
```

#### 2. Database Connection Failed

```bash
# Check PostgreSQL is running
docker-compose ps postgres

# Test connection
docker exec tutorfind-postgres psql -U postgres -d TutorFind -c "SELECT 1"

# Check logs
docker-compose logs postgres
```

#### 3. Backend Won't Connect to Database

```bash
# Check application.properties
cat backend/src/main/resources/application.properties

# Verify environment variables
docker-compose config

# Restart backend
docker-compose restart backend
```

#### 4. Frontend Can't Reach Backend

```bash
# Check CORS configuration in backend
# Check VITE_API_URL environment variable
# Check backend is running
curl http://localhost:8080/actuator/health
```

#### 5. Redis Connection Issues

```bash
# Check Redis is running
docker-compose ps redis

# Test connection
docker exec tutorfind-redis redis-cli -a tutorfind_redis_password ping

# Check logs
docker-compose logs redis
```

### Useful Commands

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ deletes data!)
docker-compose down -v

# Rebuild specific service
docker-compose up --build backend

# View logs for all services
docker-compose logs -f

# View logs for specific service
docker-compose logs -f backend

# Execute command in container
docker-compose exec backend bash
docker-compose exec postgres psql -U postgres -d TutorFind

# Restart specific service
docker-compose restart backend

# Check resource usage
docker stats
```

---

## 📊 Project Status

### Overall Progress: ~30% Complete

| Component    | Status               | Progress |
| ------------ | -------------------- | -------- |
| **Frontend** | 🟡 MVP Ready         | 70%      |
| **Backend**  | 🔴 Early Development | 15%      |
| **Database** | 🟢 Schema Ready      | 90%      |
| **Redis**    | 🟡 Configured        | 50%      |
| **Docker**   | 🟢 Complete          | 100%     |

### Critical Path Items

**Week 1-2: Backend Foundation** 🚨

- [ ] Implement security (BCrypt + JWT)
- [ ] Complete database schema implementation
- [ ] Build location system API
- [ ] Implement search API with filters

**Week 3-4: Core Features**

- [ ] Complete tutor profile API
- [ ] Build booking system API
- [ ] Implement availability management
- [ ] Connect frontend to real APIs

**Week 5-6: Social Features**

- [ ] Implement reviews & ratings
- [ ] Add feedback system
- [ ] Build classes & enrollment
- [ ] Add notifications

**Week 7-8: Admin & Polish**

- [ ] Build admin console
- [ ] Add monitoring & logging
- [ ] Performance optimization
- [ ] Testing & bug fixes

### Documentation

- ✅ [Frontend README](frontend/README.md) - Complete
- ✅ [Frontend Assessment](docs/FRONTEND_ASSESSMENT.md) - Complete
- ✅ [Backend README](backend/README.md) - Complete
- ✅ [Backend Implementation Status](backend/IMPLEMENTATION_STATUS.md) - Complete
- ✅ [PostgreSQL Setup](docs/POSTGRESQL_README.md) - Complete
- ✅ [Redis Setup](docs/REDIS_README.md) - Complete
- ✅ Main Project README (this file) - Complete

---

## 👥 Team

**Team 13** - ADA University  
**Course**: CSCI 3509: Intro to Software Engineering  
**Semester**: Fall 2025

---

## 📄 License

This project is developed for academic purposes as part of the CSCI 3509 course at ADA University.

---

## 🎯 Quick Reference

### Start Everything

```bash
docker-compose up -d
```

### Stop Everything

```bash
docker-compose down
```

### View All Services

```bash
docker-compose ps
```

### Access Services

| Service    | URL                   | Credentials                       |
| ---------- | --------------------- | --------------------------------- |
| Frontend   | http://localhost:5173 | N/A                               |
| Backend    | http://localhost:8080 | N/A                               |
| PostgreSQL | localhost:5432        | postgres / tutorfind_dev_password |
| Redis      | localhost:6379        | tutorfind_redis_password          |

### Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
```

---

## 📞 Support

- **Documentation Issues**: Check individual service READMEs
- **Backend Issues**: See [Implementation Status](backend/IMPLEMENTATION_STATUS.md)
- **Database Issues**: See [PostgreSQL README](docs/POSTGRESQL_README.md)
- **Cache Issues**: See [Redis README](docs/REDIS_README.md)

---

**Last Updated**: December 11, 2025  
**Version**: 1.0  
**Status**: Active Development
