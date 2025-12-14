# 🗄️ TutorFind PostgreSQL Database Documentation

Complete database structure, relationships, and sample data for the TutorFind platform.

---

## 📊 Database Overview

**Database Name:** `TutorFind`
**PostgreSQL Version:** 18.1+
**Total Tables:** 16
**Character Set:** UTF-8

---

## 🏗️ Database Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     TutorFind Database                       │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────┐         ┌──────────────┐                     │
│  │  users   │────────▶│tutor_profiles│                     │
│  └──────────┘         └──────────────┘                     │
│       │                      │                              │
│       │                      ├──────▶ cities               │
│       │                      ├──────▶ subjects             │
│       │                      ├──────▶ tutor_subjects       │
│       │                      ├──────▶ tutor_districts      │
│       │                      └──────▶ availability_slots   │
│       │                                                     │
│       ├──────▶ booking_requests                            │
│       ├──────▶ reviews                                     │
│       ├──────▶ feedback                                    │
│       ├──────▶ enrollments                                 │
│       └──────▶ classes                                     │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 📋 Table Structure & Relationships

### 1. **users** - Core User Accounts

**Purpose:** Stores all platform users (learners, tutors, admins)

**Structure:**

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50),
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hashed VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    age INTEGER,
    phone_number VARCHAR(20),
    profile_picture VARCHAR(255),
    role SMALLINT NOT NULL  -- 0=LEARNER, 1=TUTOR, 2=ADMIN
);
```

**Key Fields:**

- `role`: Determines user type (0=LEARNER, 1=TUTOR, 2=ADMIN)
- `email`: Unique identifier for login
- `password_hashed`: BCrypt hashed password

**Sample Data:**

```
id | email                 | role   | first_name | last_name
2  | test@example.com      | 0      | Test       | User
5  | tutor@example.com     | 1      | John       | Smith
6  | admin@tutorfind.com   | 2      | Platform   | Admin
```

**Relationships:**

- → `tutor_profiles` (one-to-one if role=TUTOR)
- → `booking_requests` (as learner or tutor)
- → `reviews` (as learner or reviewed tutor)
- → `feedback` (as learner or giving tutor)
- → `enrollments` (as learner)
- → `classes` (via tutor_profiles)

---

### 2. **tutor_profiles** - Tutor Professional Information

**Purpose:** Extended profile for users with TUTOR role

**Structure:**

```sql
CREATE TABLE tutor_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    city_id BIGINT REFERENCES cities(id),
    headline VARCHAR(200),
    bio TEXT,
    qualifications TEXT,
    experience_years INTEGER DEFAULT 0,
    monthly_rate DECIMAL(10,2),
    rating_avg FLOAT DEFAULT 0.0,
    rating_count INTEGER DEFAULT 0,
    is_verified BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Key Fields:**

- `user_id`: Links to users table (one-to-one)
- `rating_avg`: Auto-calculated from approved reviews
- `rating_count`: Number of approved reviews
- `is_verified`: Admin-approved tutors
- `is_active`: Can be deactivated by admin

**Sample Data:**

```
id | user_id | city_id | headline                    | is_verified | is_active | rating_avg
5  | 5       | 1       | Experienced Math Tutor      | true        | true      | 4.5
```

**Relationships:**

- ← `users` (belongs to one user)
- → `cities` (located in city)
- → `tutor_subjects` (teaches subjects)
- → `tutor_districts` (works in districts)
- → `availability_slots` (has time slots)
- → `classes` (creates classes)

---

### 3. **cities** - Geographic Locations

**Purpose:** Available cities for tutoring services

**Structure:**

```sql
CREATE TABLE cities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);
```

**Sample Data:**

```
id | name
1  | Baku
2  | Sumqayit
3  | Ganja
```

**Relationships:**

- → `districts` (has many districts)
- ← `tutor_profiles` (tutors based in city)

---

### 4. **districts** - City Subdivisions

**Purpose:** Specific areas within cities where tutors work

**Structure:**

```sql
CREATE TABLE districts (
    id BIGSERIAL PRIMARY KEY,
    city_id BIGINT REFERENCES cities(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL
);
```

**Sample Data (Baku Districts):**

```
id | city_id | name
1  | 1       | Nasimi
2  | 1       | Sabail
3  | 1       | Yasamal
4  | 1       | Narimanov
5  | 1       | Binagadi
```

**Relationships:**

- ← `cities` (belongs to city)
- → `tutor_districts` (tutors work in districts)

---

### 5. **subjects** - Academic Subjects

**Purpose:** Subjects that can be taught/learned

**Structure:**

```sql
CREATE TABLE subjects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50)
);
```

**Categories:**

- Mathematics
- Sciences
- Languages
- Humanities
- Technology
- Arts

**Sample Data:**

```
id | name          | category
1  | Mathematics   | Mathematics
2  | Physics       | Sciences
3  | English       | Languages
4  | History       | Humanities
5  | Programming   | Technology
```

**Total Subjects:** 24 across 6 categories

**Relationships:**

- → `tutor_subjects` (tutors teach subjects)
- → `classes` (class for subject)

---

### 6. **tutor_subjects** - Tutor Teaching Subjects

**Purpose:** Many-to-many relationship between tutors and subjects

**Structure:**

```sql
CREATE TABLE tutor_subjects (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    subject_id BIGINT REFERENCES subjects(id) ON DELETE CASCADE,
    UNIQUE(tutor_id, subject_id)
);
```

**Sample Data:**

```
id | tutor_id | subject_id
1  | 5        | 1          -- Tutor 5 teaches Mathematics
2  | 5        | 2          -- Tutor 5 teaches Physics
```

**Relationships:**

- ← `tutor_profiles`
- ← `subjects`

---

### 7. **tutor_districts** - Tutor Service Areas

**Purpose:** Districts where tutor is willing to teach

**Structure:**

```sql
CREATE TABLE tutor_districts (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    district_id BIGINT REFERENCES districts(id) ON DELETE CASCADE,
    UNIQUE(tutor_id, district_id)
);
```

**Sample Data:**

```
id | tutor_id | district_id
1  | 5        | 1           -- Tutor works in Nasimi
2  | 5        | 2           -- Tutor works in Sabail
```

**Relationships:**

- ← `tutor_profiles`
- ← `districts`

---

### 8. **availability_slots** - Tutor Weekly Schedule

**Purpose:** Tutor's available time slots

**Structure:**

```sql
CREATE TABLE availability_slots (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    day_of_week VARCHAR(10),  -- MONDAY, TUESDAY, etc.
    start_time TIME,
    end_time TIME,
    is_available BOOLEAN DEFAULT true
);
```

**Sample Data:**

```
id | tutor_id | day_of_week | start_time | end_time | is_available
1  | 5        | MONDAY      | 09:00:00   | 12:00:00 | true
2  | 5        | MONDAY      | 14:00:00   | 18:00:00 | true
3  | 5        | WEDNESDAY   | 10:00:00   | 16:00:00 | true
```

**Relationships:**

- ← `tutor_profiles`

---

### 9. **booking_requests** - Session Booking Workflow

**Purpose:** Learner requests for tutoring sessions

**Structure:**

```sql
CREATE TABLE booking_requests (
    id BIGSERIAL PRIMARY KEY,
    learner_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    subject_id BIGINT REFERENCES subjects(id),
    session_mode VARCHAR(50),  -- ONLINE, IN_PERSON
    preferred_time_slot VARCHAR(100),
    learner_note TEXT,
    offered_price DECIMAL(10,2),
    status VARCHAR(50) DEFAULT 'PENDING',  -- PENDING, ACCEPTED, DECLINED
    tutor_response TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP
);
```

**Status Flow:**

```
PENDING → ACCEPTED (tutor accepts)
        → DECLINED (tutor rejects)
```

**Sample Data:**

```
id | learner_id | tutor_id | status   | session_mode | created_at
1  | 2          | 5        | PENDING  | ONLINE       | 2025-12-13...
2  | 3          | 5        | ACCEPTED | IN_PERSON    | 2025-12-12...
3  | 4          | 5        | PENDING  | ONLINE       | 2025-12-11...
```

**Relationships:**

- ← `users` (learner)
- ← `tutor_profiles` (tutor)
- ← `subjects`

---

### 10. **reviews** - Public Tutor Reviews

**Purpose:** Public reviews and ratings for tutors

**Structure:**

```sql
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    learner_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    booking_id BIGINT REFERENCES booking_requests(id),
    rating INTEGER CHECK(rating >= 1 AND rating <= 5),
    comment TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',  -- PENDING, APPROVED, REJECTED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tutor_id, learner_id)
);
```

**Key Features:**

- One review per learner-tutor pair
- Admin moderation (PENDING → APPROVED/REJECTED)
- Only APPROVED reviews count toward tutor rating

**Sample Data:**

```
id | tutor_id | learner_id | rating | status   | comment
1  | 5        | 2          | 5      | APPROVED | Great tutor!
2  | 5        | 3          | 4      | PENDING  | Good experience
```

**Rating Calculation:**

```sql
AVG(rating) WHERE status='APPROVED' → tutor_profiles.rating_avg
COUNT(*) WHERE status='APPROVED' → tutor_profiles.rating_count
```

**Relationships:**

- ← `tutor_profiles`
- ← `users` (learner)
- ← `booking_requests` (optional)

---

### 11. **feedback** - Private Progress Notes

**Purpose:** Private tutor feedback to learners

**Structure:**

```sql
CREATE TABLE feedback (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    learner_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    session_date DATE,
    feedback_text TEXT NOT NULL,
    strengths TEXT,
    areas_for_improvement TEXT,
    homework_assigned TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Privacy:**

- Only tutor and learner can view
- Not public like reviews

**Sample Data:**

```
id | tutor_id | learner_id | feedback_text                    | session_date
1  | 5        | 2          | Great progress in calculus!      | 2025-12-10
```

**Relationships:**

- ← `tutor_profiles`
- ← `users` (learner)

---

### 12. **classes** - Group Learning Sessions

**Purpose:** Group classes offered by tutors

**Structure:**

```sql
CREATE TABLE classes (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    subject_id BIGINT REFERENCES subjects(id),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    class_type VARCHAR(50),  -- INDIVIDUAL, SMALL_GROUP, LARGE_GROUP
    max_students INTEGER DEFAULT 1,
    current_students INTEGER DEFAULT 0,
    price_per_session DECIMAL(10,2),
    total_sessions INTEGER,
    schedule_day VARCHAR(20),
    schedule_time VARCHAR(20),
    duration_minutes INTEGER,
    status VARCHAR(50) DEFAULT 'OPEN',  -- OPEN, FULL, IN_PROGRESS, COMPLETED, CANCELLED
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Status Auto-Update:**

```
OPEN → FULL (when current_students >= max_students)
FULL → OPEN (when student drops and current_students < max_students)
```

**Sample Data:**

```
id | tutor_id | name                  | class_type   | max_students | current_students | status
1  | 5        | Advanced Calculus     | SMALL_GROUP  | 8            | 2                | OPEN
2  | 5        | Physics Fundamentals  | LARGE_GROUP  | 20           | 20               | FULL
```

**Relationships:**

- ← `tutor_profiles`
- ← `subjects`
- → `enrollments`

---

### 13. **enrollments** - Class Participation

**Purpose:** Learner enrollments in classes

**Structure:**

```sql
CREATE TABLE enrollments (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT REFERENCES classes(id) ON DELETE CASCADE,
    learner_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'ACTIVE',  -- ACTIVE, COMPLETED, DROPPED, SUSPENDED
    sessions_attended INTEGER DEFAULT 0,
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    amount_paid DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(class_id, learner_id)
);
```

**Key Features:**

- One enrollment per learner-class pair
- When enrolled: `classes.current_students` increments
- When dropped: `classes.current_students` decrements

**Sample Data:**

```
id | class_id | learner_id | status  | sessions_attended
1  | 1        | 2          | ACTIVE  | 3
2  | 1        | 3          | ACTIVE  | 2
```

**Relationships:**

- ← `classes`
- ← `users` (learner)

---

## 🔗 Key Relationships Summary

### One-to-One

- `users` ↔ `tutor_profiles` (user.role = TUTOR)

### One-to-Many

- `users` → `booking_requests` (as learner)
- `users` → `reviews` (as learner)
- `users` → `enrollments` (as learner)
- `tutor_profiles` → `booking_requests`
- `tutor_profiles` → `reviews`
- `tutor_profiles` → `feedback`
- `tutor_profiles` → `classes`
- `tutor_profiles` → `availability_slots`
- `cities` → `districts`
- `cities` → `tutor_profiles`
- `classes` → `enrollments`

### Many-to-Many (via junction tables)

- `tutor_profiles` ↔ `subjects` (via `tutor_subjects`)
- `tutor_profiles` ↔ `districts` (via `tutor_districts`)

---

## 🎯 Business Logic Rules

### User Registration

1. New user created in `users` table
2. If role = TUTOR → Auto-create entry in `tutor_profiles`
3. If role = ADMIN → Must be created via database (blocked in API)

### Booking Workflow

1. Learner creates `booking_request` (status = PENDING)
2. Tutor reviews request
3. Tutor accepts → status = ACCEPTED, sets `tutor_response`
4. Tutor declines → status = DECLINED, sets `tutor_response`

### Review Workflow

1. Learner creates review (status = PENDING)
2. Admin reviews in admin console
3. Admin approves → status = APPROVED, triggers rating recalculation
4. Admin rejects → status = REJECTED
5. **Only APPROVED reviews** count toward `tutor_profiles.rating_avg`

### Class Enrollment Workflow

1. Learner enrolls → Create `enrollment`, increment `classes.current_students`
2. If `current_students >= max_students` → Set `classes.status = FULL`
3. Learner drops → Set `enrollment.status = DROPPED`, decrement `classes.current_students`
4. If was FULL and now has space → Set `classes.status = OPEN`

### Tutor Verification

1. New tutors start with `is_verified = false`
2. Admin verifies → Set `is_verified = true`
3. Admin can unverify → Set `is_verified = false`

### User Deactivation

1. Admin deactivates user
2. If user is TUTOR → Also set `tutor_profiles.is_active = false`
3. Admin users CANNOT be deactivated

---

## 📊 Sample Data Summary

### Users

- **3 Learners** (role = 0)
- **1 Tutor** (role = 1, has tutor_profile)
- **1 Admin** (role = 2)

### Geographic Data

- **3 Cities** (Baku, Sumqayit, Ganja)
- **10+ Districts** in Baku
- **24 Subjects** across 6 categories

### Activity Data

- **3 Booking Requests** (1 accepted, 2 pending)
- **2 Reviews** (1 approved, 1 pending)
- **1-2 Classes** (some open, some full)
- **2 Enrollments**
- **3 Availability Slots** per tutor
- **1 Feedback** entry

---

## 🔐 Security Considerations

### Password Storage

- All passwords stored as BCrypt hashes
- Never store plain text passwords
- Sample password hashes:
  - Learner/Tutor: `password123`
  - Admin: `admin678admin`

### Role-Based Access

```
LEARNER (0):
  - Create booking requests
  - Enroll in classes
  - Write reviews
  - View feedback received

TUTOR (1):
  - Accept/decline bookings
  - Create classes
  - Give feedback
  - Manage availability

ADMIN (2):
  - Verify tutors
  - Moderate reviews
  - Activate/deactivate users
  - View platform statistics
```

---

## 📈 Database Indexes

**Primary Indexes (AUTO):**

- All `id` columns (PRIMARY KEY)
- `users.email` (UNIQUE)

**Foreign Key Indexes (AUTO):**

- All foreign key columns automatically indexed

**Recommended Custom Indexes:**

```sql
CREATE INDEX idx_tutor_profiles_city ON tutor_profiles(city_id);
CREATE INDEX idx_tutor_profiles_verified ON tutor_profiles(is_verified);
CREATE INDEX idx_booking_requests_status ON booking_requests(status);
CREATE INDEX idx_reviews_status ON reviews(status);
CREATE INDEX idx_classes_status ON classes(status);
```

---

## 🎯 Common Queries

### Find Active Verified Tutors in Baku

```sql
SELECT tp.*, u.first_name, u.last_name
FROM tutor_profiles tp
JOIN users u ON tp.user_id = u.id
WHERE tp.city_id = 1
  AND tp.is_verified = true
  AND tp.is_active = true;
```

### Get Tutor's Average Rating

```sql
SELECT
    tp.id,
    COUNT(r.id) as total_reviews,
    AVG(r.rating) as avg_rating
FROM tutor_profiles tp
LEFT JOIN reviews r ON tp.id = r.tutor_id AND r.status = 'APPROVED'
WHERE tp.id = 5
GROUP BY tp.id;
```

### Find Open Classes

```sql
SELECT c.*, tp.*, s.name as subject_name
FROM classes c
JOIN tutor_profiles tp ON c.tutor_id = tp.id
JOIN subjects s ON c.subject_id = s.id
WHERE c.status = 'OPEN'
  AND c.current_students < c.max_students
ORDER BY c.created_at DESC;
```

### Platform Statistics

```sql
SELECT
    (SELECT COUNT(*) FROM users) as total_users,
    (SELECT COUNT(*) FROM users WHERE role = 1) as total_tutors,
    (SELECT COUNT(*) FROM tutor_profiles WHERE is_verified = true) as verified_tutors,
    (SELECT COUNT(*) FROM booking_requests WHERE status = 'PENDING') as pending_bookings,
    (SELECT COUNT(*) FROM reviews WHERE status = 'APPROVED') as approved_reviews;
```

---

## 🚀 Getting Started

### 1. Create Database

```sql
CREATE DATABASE TutorFind;
```

### 2. Run Setup Script

```sql
-- Use the provided postgre_preparation.sql script
\i postgre_preparation.sql
```

### 3. Verify Setup

```sql
-- Check all tables created
SELECT table_name FROM information_schema.tables
WHERE table_schema = 'public';

-- Check sample data
SELECT COUNT(*) as user_count FROM users;
SELECT COUNT(*) as subject_count FROM subjects;
```

---

## 📝 Maintenance

### Backup Database

```bash
pg_dump -U postgres TutorFind > tutorfind_backup.sql
```

### Restore Database

```bash
psql -U postgres TutorFind < tutorfind_backup.sql
```

### Reset Database

```sql
DROP DATABASE TutorFind;
CREATE DATABASE TutorFind;
-- Run setup script again
```

---

## 🎓 Database Version History

**Version 1.0 (Dec 14, 2025)**

- Initial complete database schema
- 16 tables
- Full relationships established
- Sample data included

---

**For setup script, see:** `postgre_preparation.sql`
**For API documentation, see:** `FULL_BACKEND_README.md`
**For new features documentation, see:** `Enrollment_Admin.md`
