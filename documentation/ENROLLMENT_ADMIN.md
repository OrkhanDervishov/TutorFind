# 🎓 Classes & Enrollment + 🛡️ Admin Console Documentation

Complete implementation guide for the last 2 major features added to TutorFind backend.

**Date Added:** December 14, 2025
**Features:** Classes & Enrollment System, Admin Console
**Total New Endpoints:** 20
**Files Created:** 13
**Files Modified:** 5

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Classes & Enrollment System](#classes--enrollment-system)
3. [Admin Console](#admin-console)
4. [Files Created/Modified](#files-createdmodified)
5. [Frontend Integration Guide](#frontend-integration-guide)
6. [Testing Guide](#testing-guide)

---

## 🎯 Overview

### What Was Added

**Feature 1: Classes & Enrollment System**

- Group learning sessions created by tutors
- Learner enrollment/dropout workflow
- Automatic capacity management
- Status auto-updates (OPEN ↔ FULL)

**Feature 2: Admin Console**

- Tutor verification workflow
- Review moderation system
- User management (activate/deactivate)
- Platform statistics dashboard

### Why These Features Matter

**Classes & Enrollment:**

- Enables tutors to teach multiple students simultaneously
- Increases tutor revenue potential
- Provides affordable options for learners
- Automatic seat management prevents overbooking

**Admin Console:**

- Quality control through tutor verification
- Content moderation for reviews
- User management for platform safety
- Analytics for business decisions

---

## 🎓 CLASSES & ENROLLMENT SYSTEM

### Database Tables

#### 1. `classes` Table

```sql
CREATE TABLE classes (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id),
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
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

#### 2. `enrollments` Table

```sql
CREATE TABLE enrollments (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT REFERENCES classes(id),
    learner_id BIGINT REFERENCES users(id),
    enrollment_date TIMESTAMP,
    status VARCHAR(50) DEFAULT 'ACTIVE',  -- ACTIVE, COMPLETED, DROPPED, SUSPENDED
    sessions_attended INTEGER DEFAULT 0,
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    amount_paid DECIMAL(10,2),
    created_at TIMESTAMP,
    UNIQUE(class_id, learner_id)
);
```

### Business Logic

**Enrollment Flow:**

```
1. Learner enrolls → Create enrollment record
2. Increment class.current_students
3. If current_students >= max_students → Set class.status = 'FULL'
```

**Dropout Flow:**

```
1. Learner drops → Set enrollment.status = 'DROPPED'
2. Decrement class.current_students
3. If class was FULL → Set class.status = 'OPEN'
```

**Constraints:**

- One enrollment per learner-class pair (UNIQUE constraint)
- Cannot enroll if class is FULL
- Cannot enroll twice in same class

### API Endpoints

#### 1. Create Class (Tutor Only)

**Endpoint:** `POST /api/classes`

**Authentication:** Required (Tutor role)

**Request Headers:**

```
Authorization: Bearer <tutor_jwt_token>
Content-Type: application/json
```

**Request Body:**

```json
{
  "subjectId": 1,
  "name": "Advanced Calculus Course",
  "description": "Learn derivatives, integrals, and differential equations",
  "classType": "SMALL_GROUP",
  "maxStudents": 8,
  "pricePerSession": 55.0,
  "totalSessions": 10,
  "scheduleDay": "Monday",
  "scheduleTime": "18:00-20:00",
  "durationMinutes": 120,
  "startDate": "2025-01-15",
  "endDate": "2025-03-15"
}
```

**Response (200 OK):**

```json
{
  "success": true,
  "classId": 1,
  "message": "Class created successfully",
  "class": {
    "id": 1,
    "tutorId": 5,
    "tutorName": "John Smith",
    "subjectId": 1,
    "subjectName": "Mathematics",
    "name": "Advanced Calculus Course",
    "description": "Learn derivatives, integrals, and differential equations",
    "classType": "SMALL_GROUP",
    "maxStudents": 8,
    "currentStudents": 0,
    "availableSeats": 8,
    "pricePerSession": 55.0,
    "totalSessions": 10,
    "scheduleDay": "Monday",
    "scheduleTime": "18:00-20:00",
    "durationMinutes": 120,
    "status": "OPEN",
    "startDate": "2025-01-15",
    "endDate": "2025-03-15",
    "createdAt": "2025-12-14T01:59:45.313911"
  }
}
```

---

#### 2. Get All Classes (Public)

**Endpoint:** `GET /api/classes`

**Authentication:** Not required

**Query Parameters:**

- `status` (optional): Filter by status (e.g., `?status=OPEN`)

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "tutorName": "John Smith",
    "subjectName": "Mathematics",
    "name": "Advanced Calculus Course",
    "classType": "SMALL_GROUP",
    "maxStudents": 8,
    "currentStudents": 2,
    "availableSeats": 6,
    "pricePerSession": 55.0,
    "status": "OPEN",
    "scheduleDay": "Monday",
    "scheduleTime": "18:00-20:00",
    "startDate": "2025-01-15"
  }
]
```

---

#### 3. Get Class by ID (Public)

**Endpoint:** `GET /api/classes/{id}`

**Authentication:** Not required

**Response (200 OK):**

```json
{
  "id": 1,
  "tutorId": 5,
  "tutorName": "John Smith",
  "subjectName": "Mathematics",
  "name": "Advanced Calculus Course",
  "description": "Learn derivatives...",
  "classType": "SMALL_GROUP",
  "maxStudents": 8,
  "currentStudents": 2,
  "availableSeats": 6,
  "pricePerSession": 55.00,
  "status": "OPEN",
  ...
}
```

---

#### 4. Get My Classes (Tutor Only)

**Endpoint:** `GET /api/classes/my-classes`

**Authentication:** Required (Tutor role)

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "name": "Advanced Calculus Course",
    "currentStudents": 2,
    "maxStudents": 8,
    "status": "OPEN",
    ...
  }
]
```

---

#### 5. Update Class (Tutor Only)

**Endpoint:** `PUT /api/classes/{id}`

**Authentication:** Required (Tutor role, must own the class)

**Request Body (partial update):**

```json
{
  "description": "Updated description with new content!",
  "maxStudents": 10,
  "pricePerSession": 60.0
}
```

**Response (200 OK):**

```json
{
  "success": true,
  "message": "Class updated successfully",
  "class": {
    "id": 1,
    "description": "Updated description...",
    "maxStudents": 10,
    "pricePerSession": 60.00,
    ...
  }
}
```

---

#### 6. Delete Class (Tutor Only)

**Endpoint:** `DELETE /api/classes/{id}`

**Authentication:** Required (Tutor role, must own the class)

**Constraint:** Cannot delete if class has active enrollments

**Response (200 OK):**

```json
{
  "success": true,
  "message": "Class deleted successfully"
}
```

**Error (400 Bad Request):**

```json
{
  "success": false,
  "message": "Cannot delete class with active enrollments"
}
```

---

#### 7. Enroll in Class (Learner Only)

**Endpoint:** `POST /api/classes/{id}/enroll`

**Authentication:** Required (Learner role)

**No request body needed**

**Response (200 OK):**

```json
{
  "success": true,
  "enrollmentId": 1,
  "message": "Successfully enrolled in class",
  "enrollment": {
    "id": 1,
    "classId": 1,
    "className": "Advanced Calculus Course",
    "learnerId": 2,
    "learnerName": "Test User",
    "enrollmentDate": "2025-12-14T02:12:43.240514",
    "status": "ACTIVE",
    "sessionsAttended": 0,
    "paymentStatus": "PENDING",
    "scheduleDay": "Monday",
    "scheduleTime": "18:00-20:00",
    "tutorName": "John Smith",
    "subjectName": "Mathematics"
  }
}
```

**Errors:**

```json
// Already enrolled
{"success": false, "message": "You are already enrolled in this class"}

// Class is full
{"success": false, "message": "Class is full"}

// Class not open
{"success": false, "message": "Class is not open for enrollment"}

// Not a learner
{"success": false, "message": "Only learners can enroll in classes"}
```

---

#### 8. Drop from Class (Learner Only)

**Endpoint:** `DELETE /api/classes/enrollments/{enrollmentId}`

**Authentication:** Required (Learner role, must own the enrollment)

**Response (200 OK):**

```json
{
  "success": true,
  "message": "Successfully dropped from class"
}
```

---

#### 9. Get Class Roster (Tutor Only)

**Endpoint:** `GET /api/classes/{id}/roster`

**Authentication:** Required (Tutor role, must own the class)

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "className": "Advanced Calculus Course",
    "learnerId": 2,
    "learnerName": "Test User",
    "enrollmentDate": "2025-12-14T02:12:43.240514",
    "status": "ACTIVE",
    "sessionsAttended": 3,
    "paymentStatus": "PAID"
  },
  {
    "id": 2,
    "learnerId": 3,
    "learnerName": "New User",
    "status": "ACTIVE",
    ...
  }
]
```

---

#### 10. Get My Enrollments (Learner Only)

**Endpoint:** `GET /api/classes/enrollments/my`

**Authentication:** Required (Learner role)

**Query Parameters:**

- `status` (optional): Filter by status (e.g., `?status=ACTIVE`)

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "classId": 1,
    "className": "Advanced Calculus Course",
    "status": "ACTIVE",
    "scheduleDay": "Monday",
    "scheduleTime": "18:00-20:00",
    "tutorName": "John Smith",
    "subjectName": "Mathematics",
    "sessionsAttended": 3,
    "enrollmentDate": "2025-12-14T02:12:43.240514"
  }
]
```

---

### Files Created for Classes & Enrollment

```
tutorfind_back/src/main/java/com/team13/TutorFind/
├── entity/
│   ├── Class.java                    ✅ NEW
│   └── Enrollment.java               ✅ NEW
├── Database/Repos/
│   ├── ClassRepo.java                ✅ NEW
│   └── EnrollmentRepo.java           ✅ NEW
├── Database/Services/
│   ├── ClassService.java             ✅ NEW
│   └── EnrollmentService.java        ✅ NEW
├── controller/
│   └── ClassController.java          ✅ NEW
└── dto/
    ├── CreateClassRequest.java       ✅ NEW
    ├── ClassResponse.java            ✅ NEW
    └── EnrollmentResponse.java       ✅ NEW
```

---

## 🛡️ ADMIN CONSOLE

### Database Changes

**Added `status` column to `reviews` table:**

```sql
ALTER TABLE reviews ADD COLUMN status VARCHAR(50) DEFAULT 'PENDING';
```

**Values:** `PENDING`, `APPROVED`, `REJECTED`

### Admin Role

**How to create admin:**

```sql
-- Admin can ONLY be created via database (blocked in API for security)
INSERT INTO users (username, email, password_hashed, first_name, last_name, age, role)
VALUES ('admin', 'admin@tutorfind.com', '<bcrypt_hash>', 'Platform', 'Admin', 30, 2);
```

**Role value:** `2` (ADMIN)

### API Endpoints

#### 1. Get Platform Statistics

**Endpoint:** `GET /api/admin/stats`

**Authentication:** Required (Admin role only)

**Response (200 OK):**

```json
{
  "totalUsers": 5,
  "totalTutors": 1,
  "totalLearners": 3,
  "verifiedTutors": 1,
  "unverifiedTutors": 0,
  "totalReviews": 2,
  "pendingReviews": 1,
  "approvedReviews": 1,
  "rejectedReviews": 0,
  "totalBookings": 3,
  "pendingBookings": 2,
  "acceptedBookings": 1,
  "totalClasses": 2,
  "openClasses": 1,
  "fullClasses": 1,
  "totalEnrollments": 2,
  "activeEnrollments": 2
}
```

**Frontend Use:** Dashboard overview cards

---

#### 2. Get All Users

**Endpoint:** `GET /api/admin/users`

**Authentication:** Required (Admin role only)

**Response (200 OK):**

```json
[
  {
    "id": 2,
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User",
    "role": "LEARNER",
    "age": 25,
    ...
  },
  {
    "id": 5,
    "email": "tutor@example.com",
    "role": "TUTOR",
    ...
  }
]
```

**Frontend Use:** User management table

---

#### 3. Get Unverified Tutors

**Endpoint:** `GET /api/admin/tutors/unverified`

**Authentication:** Required (Admin role only)

**Response (200 OK):**

```json
[
  {
    "id": 5,
    "userId": 5,
    "headline": "Experienced Math Tutor",
    "isVerified": false,
    "isActive": true,
    "experienceYears": 5,
    "createdAt": "2025-12-14T01:59:38.242913"
  }
]
```

**Frontend Use:** Verification queue

---

#### 4. Verify Tutor

**Endpoint:** `PUT /api/admin/tutors/{tutorId}/verify`

**Authentication:** Required (Admin role only)

**Response (200 OK):**

```json
{
  "success": true,
  "message": "Tutor verified successfully"
}
```

**Effect:** Sets `tutor_profiles.is_verified = true`

---

#### 5. Unverify Tutor

**Endpoint:** `PUT /api/admin/tutors/{tutorId}/unverify`

**Authentication:** Required (Admin role only)

**Response (200 OK):**

```json
{
  "success": true,
  "message": "Tutor unverified successfully"
}
```

**Effect:** Sets `tutor_profiles.is_verified = false`

---

#### 6. Get Pending Reviews

**Endpoint:** `GET /api/admin/reviews/pending`

**Authentication:** Required (Admin role only)

**Response (200 OK):**

```json
[
  {
    "id": 2,
    "tutorId": 5,
    "learnerId": 2,
    "rating": 4,
    "comment": "Great tutor!",
    "status": "PENDING",
    "createdAt": "2025-12-13T19:55:21.797173"
  }
]
```

**Frontend Use:** Review moderation queue

---

#### 7. Approve Review

**Endpoint:** `PUT /api/admin/reviews/{reviewId}/approve`

**Authentication:** Required (Admin role only)

**Response (200 OK):**

```json
{
  "success": true,
  "message": "Review approved successfully"
}
```

**Effect:**

1. Sets `reviews.status = 'APPROVED'`
2. **Auto-recalculates** tutor rating:
   - `rating_avg` = AVG of all APPROVED reviews
   - `rating_count` = COUNT of all APPROVED reviews

---

#### 8. Reject Review

**Endpoint:** `PUT /api/admin/reviews/{reviewId}/reject`

**Authentication:** Required (Admin role only)

**Request Body (optional):**

```json
{
  "reason": "Violates community guidelines"
}
```

**Response (200 OK):**

```json
{
  "success": true,
  "message": "Review rejected successfully"
}
```

**Effect:** Sets `reviews.status = 'REJECTED'`

---

#### 9. Deactivate User

**Endpoint:** `PUT /api/admin/users/{userId}/deactivate`

**Authentication:** Required (Admin role only)

**Response (200 OK):**

```json
{
  "success": true,
  "message": "User deactivated successfully"
}
```

**Effect:**

- If user is TUTOR → Also sets `tutor_profiles.is_active = false`
- If user is ADMIN → **Error:** "Cannot deactivate admin users"

---

#### 10. Activate User

**Endpoint:** `PUT /api/admin/users/{userId}/activate`

**Authentication:** Required (Admin role only)

**Response (200 OK):**

```json
{
  "success": true,
  "message": "User activated successfully"
}
```

**Effect:**

- If user is TUTOR → Also sets `tutor_profiles.is_active = true`

---

### Files Created for Admin Console

```
tutorfind_back/src/main/java/com/team13/TutorFind/
├── Database/Services/
│   └── AdminService.java             ✅ NEW
└── controller/
    └── AdminController.java          ✅ NEW
```

### Files Modified for Admin Console

```
tutorfind_back/src/main/java/com/team13/TutorFind/
├── Database/Repos/
│   ├── UserRepo.java                 ✏️ MODIFIED (added findByRole)
│   ├── TutorDetailsRepo.java        ✏️ MODIFIED (added findByIsVerified, countByIsVerified)
│   ├── EnrollmentRepo.java          ✏️ MODIFIED (added countByStatus)
│   └── ReviewRepo.java              ✏️ MODIFIED (added findByStatus)
├── entity/
│   └── Review.java                  ✏️ MODIFIED (added status field)
└── Authorize/
    └── AuthorizationController.java ✏️ MODIFIED (blocked admin registration)
```

---

## 📦 Files Created/Modified Summary

### Total Files Created: 13

**Classes & Enrollment (10 files):**

1. `Class.java` (entity)
2. `Enrollment.java` (entity)
3. `ClassRepo.java` (repository)
4. `EnrollmentRepo.java` (repository)
5. `CreateClassRequest.java` (DTO)
6. `ClassResponse.java` (DTO)
7. `EnrollmentResponse.java` (DTO)
8. `ClassService.java` (service)
9. `EnrollmentService.java` (service)
10. `ClassController.java` (controller)

**Admin Console (3 files):**

1. `AdminService.java` (service)
2. `AdminController.java` (controller)
3. `create_classes_tables.sql` (database)

### Total Files Modified: 5

1. `Review.java` - Added `status` field
2. `UserRepo.java` - Added `findByRole()` method
3. `TutorDetailsRepo.java` - Added verification methods
4. `EnrollmentRepo.java` - Added `countByStatus()`
5. `AuthorizationController.java` - Blocked admin registration

---

## 🎨 Frontend Integration Guide

### Classes & Enrollment Frontend Flow

#### For Tutors:

**1. Create Class Page**

```jsx
// Form with fields:
- Subject dropdown (GET /api/subjects)
- Class name
- Description
- Class type (radio: INDIVIDUAL, SMALL_GROUP, LARGE_GROUP)
- Max students (number input)
- Price per session
- Total sessions
- Schedule (day + time)
- Duration
- Start/end dates

// Submit to:
POST /api/classes
Headers: { Authorization: Bearer <token> }
```

**2. My Classes Page**

```jsx
// Display tutor's classes
GET /api/classes/my-classes
Headers: { Authorization: Bearer <token> }

// Show for each class:
- Name, description
- Current students / Max students
- Status badge (OPEN/FULL)
- Schedule
- Price
- Actions: [View Roster] [Edit] [Delete]
```

**3. Class Roster Modal**

```jsx
// Show enrolled students
GET /api/classes/{id}/roster
Headers: { Authorization: Bearer <token> }

// Display table:
- Learner name
- Enrollment date
- Sessions attended
- Payment status
```

#### For Learners:

**1. Browse Classes Page**

```jsx
// List all open classes
GET /api/classes?status=OPEN

// Show for each class:
- Class name
- Tutor name
- Subject
- Schedule (day + time)
- Available seats
- Price
- [Enroll] button
```

**2. Enroll Button Handler**

```jsx
// Enroll in class
POST /api/classes/{id}/enroll
Headers: { Authorization: Bearer <token> }

// On success:
- Show success message
- Redirect to "My Enrollments"
```

**3. My Enrollments Page**

```jsx
// Show learner's enrollments
GET /api/classes/enrollments/my?status=ACTIVE
Headers: { Authorization: Bearer <token> }

// Display for each:
- Class name
- Tutor name
- Schedule
- Sessions attended
- [Drop Class] button
```

**4. Drop Class Handler**

```jsx
// Drop from class
DELETE /api/classes/enrollments/{enrollmentId}
Headers: { Authorization: Bearer <token> }

// On success:
- Show confirmation
- Remove from list
```

---

### Admin Console Frontend Flow

#### Admin Dashboard

**1. Statistics Cards**

```jsx
// Fetch platform stats
GET /api/admin/stats
Headers: { Authorization: Bearer <admin_token> }

// Display cards:
- Total Users (totalUsers)
- Total Tutors (totalTutors)
- Unverified Tutors (unverifiedTutors) - highlight if > 0
- Pending Reviews (pendingReviews) - highlight if > 0
- Active Classes (openClasses)
- Total Bookings (totalBookings)
```

**2. Tutor Verification Tab**

```jsx
// Get unverified tutors
GET /api/admin/tutors/unverified
Headers: { Authorization: Bearer <admin_token> }

// Display table:
- Tutor name
- Email
- Experience
- Created date
- [View Profile] [Verify] [Reject] buttons

// Verify action:
PUT /api/admin/tutors/{id}/verify
Headers: { Authorization: Bearer <admin_token> }
```

**3. Review Moderation Tab**

```jsx
// Get pending reviews
GET /api/admin/reviews/pending
Headers: { Authorization: Bearer <admin_token> }

// Display list:
- Tutor name
- Learner name
- Rating (stars)
- Comment text
- Date
- [Approve] [Reject] buttons

// Approve action:
PUT /api/admin/reviews/{id}/approve
Headers: { Authorization: Bearer <admin_token> }

// Reject action (with reason):
PUT /api/admin/reviews/{id}/reject
Headers: { Authorization: Bearer <admin_token> }
Body: { "reason": "..." }
```

**4. User Management Tab**

```jsx
// Get all users
GET /api/admin/users
Headers: { Authorization: Bearer <admin_token> }

// Display table:
- Name
- Email
- Role
- Status (active/inactive)
- [Deactivate] or [Activate] button

// Deactivate:
PUT /api/admin/users/{id}/deactivate
Headers: { Authorization: Bearer <admin_token> }

// Activate:
PUT /api/admin/users/{id}/activate
Headers: { Authorization: Bearer <admin_token> }
```

---

### Authorization Handling

**Every admin endpoint returns this on unauthorized access:**

```json
{
  "success": false,
  "message": "Unauthorized: Admin access only"
}
```

**Frontend should:**

1. Check if response is 400/401
2. If message contains "Unauthorized"
3. Redirect to login or show access denied
4. Clear admin token

---

## 🧪 Testing Guide

### Classes & Enrollment Testing

**Prerequisites:**

- Tutor account with token
- Learner account with token

**Test Sequence:**

1. Login as tutor → Create class
2. Login as learner → Browse classes
3. Learner enrolls in class
4. Verify `current_students` incremented
5. Login as tutor → View roster
6. Learner drops from class
7. Verify `current_students` decremented
8. Test capacity: Enroll until FULL
9. Verify cannot enroll when FULL
10. Test duplicate enrollment prevention

### Admin Console Testing

**Prerequisites:**

- Admin account created via SQL
- Admin login token

**Test Sequence:**

1. Login as admin → Get stats
2. View unverified tutors
3. Verify a tutor → Check `is_verified` in DB
4. Create pending review via SQL
5. View pending reviews
6. Approve review → Verify rating recalculated
7. Reject a review
8. Deactivate a tutor user
9. Activate the user again
10. Try to deactivate admin → Should fail

---

## 🔐 Security Notes

### Admin Registration Prevention

**Code in AuthorizationController:**

```java
if ("ADMIN".equalsIgnoreCase(request.getRole())) {
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body("Admin registration is not allowed via API");
}
```

**Frontend should:**

- Not offer "ADMIN" as role option in registration
- Only show LEARNER and TUTOR

### Role-Based Access

**Authorization check in admin endpoints:**

```java
private void verifyAdmin(String authHeader) {
    // Extract user from token
    // Check if user.role == UserRoles.ADMIN
    // Throw error if not admin
}
```

**Frontend should:**

- Show admin menu only if user.role === "ADMIN"
- Hide admin routes from non-admins
- Handle 400 responses with "Unauthorized" message

---

## 📊 Data Flow Diagrams

### Enrollment Flow

```
Learner → POST /api/classes/1/enroll
    ↓
Backend checks:
  - Is user a learner? ✓
  - Is class OPEN? ✓
  - Is class full? ✓
  - Already enrolled? ✓
    ↓
Create enrollment (status=ACTIVE)
    ↓
Increment classes.current_students
    ↓
If full → Update classes.status = 'FULL'
    ↓
Return enrollment details
```

### Review Approval Flow

```
Admin → PUT /api/admin/reviews/5/approve
    ↓
Backend:
  - Verify admin role ✓
  - Find review ✓
    ↓
Set review.status = 'APPROVED'
    ↓
Recalculate tutor rating:
  - Get all APPROVED reviews for tutor
  - Calculate AVG(rating)
  - Update tutor_profiles.rating_avg
  - Update tutor_profiles.rating_count
    ↓
Return success
```

---

## 🎯 Key Takeaways for Frontend

### Classes & Enrollment

1. **Automatic capacity management** - Backend handles it, just show current/max
2. **Status changes automatically** - Display badge based on status
3. **One enrollment per learner** - Disable enroll button if already enrolled
4. **Role-specific views** - Tutors see roster, learners see enrollment list

### Admin Console

1. **Admin-only access** - Check role before showing admin menu
2. **Real-time stats** - Refresh stats periodically
3. **Moderation queue** - Show pending count as badge
4. **Cannot create admin via API** - Only via database

---

## 📚 Related Documentation

- **Database Structure:** See `POSTGRE.md`
- **Complete API Reference:** See `FULL_BACKEND_README.md`
- **Database Setup:** See `postgre_preparation.sql`

---

## ✅ Implementation Checklist

### Backend ✅

- [x] Classes entity and repository
- [x] Enrollment entity and repository
- [x] Class service with business logic
- [x] Enrollment service with capacity management
- [x] Class controller with 10 endpoints
- [x] Admin service with all operations
- [x] Admin controller with 10 endpoints
- [x] Review moderation with rating recalculation
- [x] Admin registration prevention
- [x] All tests passed

### Frontend (Your Task)

- [ ] Classes browsing page
- [ ] Class creation form (tutor)
- [ ] Enrollment UI (learner)
- [ ] My classes page (tutor)
- [ ] My enrollments page (learner)
- [ ] Class roster view (tutor)
- [ ] Admin dashboard
- [ ] Tutor verification UI
- [ ] Review moderation UI
- [ ] User management UI
- [ ] Platform statistics cards

---

**Last Updated:** December 14, 2025
**Status:** ✅ Complete and Production-Ready
**Total Endpoints Added:** 20 (10 classes + 10 admin)
