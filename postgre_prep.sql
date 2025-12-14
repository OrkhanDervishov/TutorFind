-- ============================================
-- TutorFind Database Setup Script
-- ============================================
-- Version: 1.0
-- Date: December 14, 2025
-- Description: Complete database setup with tables and sample data
-- ============================================

-- Drop existing tables (if recreating)
DROP TABLE IF EXISTS enrollments CASCADE;
DROP TABLE IF EXISTS classes CASCADE;
DROP TABLE IF EXISTS feedback CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS booking_requests CASCADE;
DROP TABLE IF EXISTS availability_slots CASCADE;
DROP TABLE IF EXISTS tutor_districts CASCADE;
DROP TABLE IF EXISTS tutor_subjects CASCADE;
DROP TABLE IF EXISTS tutor_profiles CASCADE;
DROP TABLE IF EXISTS districts CASCADE;
DROP TABLE IF EXISTS cities CASCADE;
DROP TABLE IF EXISTS subjects CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ============================================
-- TABLE CREATION
-- ============================================

-- 1. Users Table
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

-- 2. Cities Table
CREATE TABLE cities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- 3. Districts Table
CREATE TABLE districts (
    id BIGSERIAL PRIMARY KEY,
    city_id BIGINT REFERENCES cities(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL
);

-- 4. Subjects Table
CREATE TABLE subjects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50)
);

-- 5. Tutor Profiles Table
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

-- 6. Tutor Subjects Junction Table
CREATE TABLE tutor_subjects (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    subject_id BIGINT REFERENCES subjects(id) ON DELETE CASCADE,
    UNIQUE(tutor_id, subject_id)
);

-- 7. Tutor Districts Junction Table
CREATE TABLE tutor_districts (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    district_id BIGINT REFERENCES districts(id) ON DELETE CASCADE,
    UNIQUE(tutor_id, district_id)
);

-- 8. Availability Slots Table
CREATE TABLE availability_slots (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tutor_id, day_of_week, start_time, end_time)
);

-- 9. Booking Requests Table
CREATE TABLE booking_requests (
    id BIGSERIAL PRIMARY KEY,
    learner_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    subject_id BIGINT REFERENCES subjects(id),
    session_type VARCHAR(50),          -- individual, group (optional)
    mode VARCHAR(50),                  -- ONLINE, IN_PERSON
    slot_day VARCHAR(20),              -- e.g., Monday
    slot_time VARCHAR(50),             -- e.g., 10:00-11:00
    slot_text VARCHAR(200),            -- e.g., Monday 10:00-11:00
    preferred_schedule TEXT,
    learner_note TEXT,
    tutor_response TEXT,
    proposed_price DECIMAL(10,2),
    sessions_count INTEGER DEFAULT 1,
    status VARCHAR(50) DEFAULT 'PENDING',  -- PENDING, ACCEPTED, DECLINED, CANCELLED, COMPLETED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP
);

-- 10. Reviews Table
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    learner_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    booking_id BIGINT REFERENCES booking_requests(id),
    rating INTEGER CHECK(rating >= 1 AND rating <= 5),
    comment TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tutor_id, learner_id)
);

-- 11. Feedback Table
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

-- 12. Classes Table
CREATE TABLE classes (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    subject_id BIGINT REFERENCES subjects(id),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    class_type VARCHAR(50),
    max_students INTEGER DEFAULT 1,
    current_students INTEGER DEFAULT 0,
    price_per_session DECIMAL(10,2),
    total_sessions INTEGER,
    schedule_day VARCHAR(20),
    schedule_time VARCHAR(20),
    duration_minutes INTEGER,
    status VARCHAR(50) DEFAULT 'OPEN',
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 13. Enrollments Table
CREATE TABLE enrollments (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT REFERENCES classes(id) ON DELETE CASCADE,
    learner_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    sessions_attended INTEGER DEFAULT 0,
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    amount_paid DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(class_id, learner_id)
);

-- ============================================
-- SAMPLE DATA INSERTION
-- ============================================

-- INSERT USERS
-- Note: Passwords are BCrypt hashed
-- Learners: password123
-- Tutor: password123
-- Admin: admin678admin

INSERT INTO users (id, username, email, password_hashed, first_name, last_name, age, phone_number, role) VALUES
(2, 'testuser', 'test@example.com', '$2a$10$HZOLlxI.uv07Af40BpeddumVN2jIeTpvKVerO7XPUQGhUjwnrPJBS', 'Test', 'User', 25, '+994501234567', 0),
(3, 'newuser', 'new@example.com', '$2a$10$HIKK8K3yMmalvs7XHb4SBuDCsClCVhFv.baQtwmNiOQ/v5BH/1D0i', 'New', 'User', 25, '+994507654321', 0),
(4, 'test_1', '999@gmail.com', '$2a$10$.umYQEKD52LmOxGqMM97vO.VLVazYg4mbLC97l5MmT5b3.g.pdFv2', 'test_1', 'test_1', 19, '+994999999', 0),
(5, NULL, 'tutor@example.com', '$2a$10$Tf69kqiIadxvkgTKXa0T/O4ls1bntN69mEtomK7ceR/3uJnAqWLrm', 'John', 'Smith', 30, '+994551234567', 1),
(6, 'admin', 'admin@tutorfind.com', '$2a$12$J4pxd3Z1BWIBGSMvcap0Fuzu2oKn1pMGl0YwrcqIXjVMqu1TXTFvC', 'Platform', 'Admin', 30, '+994501111111', 2);

-- Reset sequence for users
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- INSERT CITIES
INSERT INTO cities (id, name) VALUES
(1, 'Baku'),
(2, 'Sumqayit'),
(3, 'Ganja');

SELECT setval('cities_id_seq', (SELECT MAX(id) FROM cities));

-- INSERT DISTRICTS (Baku)
INSERT INTO districts (id, city_id, name) VALUES
(1, 1, 'Nasimi'),
(2, 1, 'Sabail'),
(3, 1, 'Yasamal'),
(4, 1, 'Narimanov'),
(5, 1, 'Binagadi'),
(6, 1, 'Sabunchu'),
(7, 1, 'Surakhani'),
(8, 1, 'Khatai'),
(9, 1, 'Garadagh'),
(10, 1, 'Pirallahi');

SELECT setval('districts_id_seq', (SELECT MAX(id) FROM districts));

-- INSERT SUBJECTS (24 subjects across 6 categories)
INSERT INTO subjects (id, name, category) VALUES
-- Mathematics (4)
(1, 'Mathematics', 'Mathematics'),
(2, 'Calculus', 'Mathematics'),
(3, 'Statistics', 'Mathematics'),
(4, 'Geometry', 'Mathematics'),

-- Sciences (6)
(5, 'Physics', 'Sciences'),
(6, 'Chemistry', 'Sciences'),
(7, 'Biology', 'Sciences'),
(8, 'Environmental Science', 'Sciences'),
(9, 'Astronomy', 'Sciences'),
(10, 'Earth Science', 'Sciences'),

-- Languages (5)
(11, 'English', 'Languages'),
(12, 'Spanish', 'Languages'),
(13, 'French', 'Languages'),
(14, 'German', 'Languages'),
(15, 'Mandarin Chinese', 'Languages'),

-- Humanities (4)
(16, 'History', 'Humanities'),
(17, 'Geography', 'Humanities'),
(18, 'Philosophy', 'Humanities'),
(19, 'Literature', 'Humanities'),

-- Technology (3)
(20, 'Programming', 'Technology'),
(21, 'Web Development', 'Technology'),
(22, 'Data Science', 'Technology'),

-- Arts (2)
(23, 'Music', 'Arts'),
(24, 'Visual Arts', 'Arts');

SELECT setval('subjects_id_seq', (SELECT MAX(id) FROM subjects));

-- INSERT TUTOR PROFILE
INSERT INTO tutor_profiles (id, user_id, city_id, headline, bio, qualifications, experience_years, monthly_rate, rating_avg, rating_count, is_verified, is_active) VALUES
(5, 5, 1, 'Experienced Mathematics & Physics Tutor', 
 'I have 5 years of experience teaching mathematics and physics to high school and university students. My goal is to make complex concepts easy to understand.',
 'MSc in Mathematics, BSc in Physics, TEFL Certified',
 5, 250.00, 4.5, 2, true, true);

SELECT setval('tutor_profiles_id_seq', (SELECT MAX(id) FROM tutor_profiles));

-- INSERT TUTOR SUBJECTS
INSERT INTO tutor_subjects (tutor_id, subject_id) VALUES
(5, 1),  -- Mathematics
(5, 2),  -- Calculus
(5, 5);  -- Physics

-- INSERT TUTOR DISTRICTS
INSERT INTO tutor_districts (tutor_id, district_id) VALUES
(5, 1),  -- Nasimi
(5, 2),  -- Sabail
(5, 3);  -- Yasamal

-- INSERT AVAILABILITY SLOTS
INSERT INTO availability_slots (tutor_id, day_of_week, start_time, end_time, is_active) VALUES
(5, 'MONDAY', '09:00:00', '12:00:00', true),
(5, 'MONDAY', '14:00:00', '18:00:00', true),
(5, 'WEDNESDAY', '10:00:00', '16:00:00', true),
(5, 'FRIDAY', '09:00:00', '13:00:00', true);

-- INSERT BOOKING REQUESTS (mix of statuses)
INSERT INTO booking_requests (id, learner_id, tutor_id, subject_id, session_type, mode, slot_day, slot_time, slot_text, preferred_schedule, learner_note, tutor_response, proposed_price, sessions_count, status, created_at, responded_at, updated_at) VALUES
(1, 2, 5, 1, NULL, 'ONLINE', 'Monday', '10:00-11:00', 'Monday 10:00-11:00', NULL, 'Need help with calculus homework', NULL, 50.00, 1, 'PENDING', '2025-12-13 10:00:00', NULL, '2025-12-13 10:00:00'),
(2, 3, 5, 5, NULL, 'IN_PERSON', 'Wednesday', '14:00-15:00', 'Wednesday 14:00-15:00', NULL, 'Preparing for physics exam', 'Happy to help! Looking forward to our session.', 60.00, 1, 'ACCEPTED', '2025-12-12 15:30:00', '2025-12-12 18:00:00', '2025-12-12 18:00:00'),
(3, 4, 5, 1, NULL, 'ONLINE', 'Friday', '11:00-12:00', 'Friday 11:00-12:00', NULL, 'Want to improve in mathematics', NULL, 50.00, 1, 'PENDING', '2025-12-11 14:00:00', NULL, '2025-12-11 14:00:00');

SELECT setval('booking_requests_id_seq', (SELECT MAX(id) FROM booking_requests));

-- INSERT REVIEWS (mix of statuses)
INSERT INTO reviews (id, tutor_id, learner_id, booking_id, rating, comment, status, created_at) VALUES
(1, 5, 2, NULL, 5, 'Excellent tutor! Very patient and knowledgeable. Helped me understand calculus concepts clearly.', 'APPROVED', '2025-12-13 19:55:00'),
(2, 5, 3, 2, 4, 'Great experience. The session was very helpful for my physics exam preparation.', 'PENDING', '2025-12-14 10:00:00');

SELECT setval('reviews_id_seq', (SELECT MAX(id) FROM reviews));

-- INSERT FEEDBACK
INSERT INTO feedback (id, tutor_id, learner_id, session_date, feedback_text, strengths, areas_for_improvement, homework_assigned, created_at) VALUES
(1, 5, 2, '2025-12-10', 'Great progress in understanding derivatives!', 
 'Quick learner, asks good questions, completes homework on time',
 'Practice more word problems, review chain rule',
 'Complete exercises 1-10 from Chapter 5',
 '2025-12-10 18:30:00');

SELECT setval('feedback_id_seq', (SELECT MAX(id) FROM feedback));

-- INSERT CLASSES
INSERT INTO classes (id, tutor_id, subject_id, name, description, class_type, max_students, current_students, price_per_session, total_sessions, schedule_day, schedule_time, duration_minutes, status, start_date, end_date, created_at) VALUES
(1, 5, 1, 'Advanced Calculus Course', 
 'Learn advanced calculus concepts including derivatives, integrals, and differential equations. Perfect for university students.',
 'SMALL_GROUP', 8, 2, 55.00, 10, 'Monday', '18:00-20:00', 120, 'OPEN', '2025-01-15', '2025-03-15', '2025-12-14 01:59:45'),
(2, 5, 5, 'Physics Fundamentals', 
 'Master the fundamentals of physics including mechanics, thermodynamics, and electromagnetism.',
 'LARGE_GROUP', 20, 20, 40.00, 12, 'Wednesday', '16:00-18:00', 120, 'FULL', '2025-01-20', '2025-04-20', '2025-12-13 15:00:00');

SELECT setval('classes_id_seq', (SELECT MAX(id) FROM classes));

-- INSERT ENROLLMENTS
INSERT INTO enrollments (id, class_id, learner_id, enrollment_date, status, sessions_attended, payment_status, amount_paid, created_at) VALUES
(1, 1, 2, '2025-12-14 02:12:43', 'ACTIVE', 3, 'PAID', 165.00, '2025-12-14 02:12:43'),
(2, 1, 3, '2025-12-14 08:30:00', 'ACTIVE', 2, 'PENDING', NULL, '2025-12-14 08:30:00');

SELECT setval('enrollments_id_seq', (SELECT MAX(id) FROM enrollments));

-- ============================================
-- CREATE INDEXES FOR PERFORMANCE
-- ============================================

CREATE INDEX idx_tutor_profiles_city ON tutor_profiles(city_id);
CREATE INDEX idx_tutor_profiles_verified ON tutor_profiles(is_verified);
CREATE INDEX idx_tutor_profiles_active ON tutor_profiles(is_active);
CREATE INDEX idx_booking_requests_status ON booking_requests(status);
CREATE INDEX idx_booking_requests_learner ON booking_requests(learner_id);
CREATE INDEX idx_booking_requests_tutor ON booking_requests(tutor_id);
CREATE INDEX idx_reviews_status ON reviews(status);
CREATE INDEX idx_reviews_tutor ON reviews(tutor_id);
CREATE INDEX idx_classes_status ON classes(status);
CREATE INDEX idx_classes_tutor ON classes(tutor_id);
CREATE INDEX idx_enrollments_class ON enrollments(class_id);
CREATE INDEX idx_enrollments_learner ON enrollments(learner_id);
CREATE INDEX idx_enrollments_status ON enrollments(status);

-- ============================================
-- VERIFICATION QUERIES
-- ============================================

-- Verify all tables were created
SELECT 
    'Tables Created:' as info,
    COUNT(*) as count 
FROM information_schema.tables 
WHERE table_schema = 'public';

-- Verify sample data
SELECT 'Users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'Cities', COUNT(*) FROM cities
UNION ALL
SELECT 'Districts', COUNT(*) FROM districts
UNION ALL
SELECT 'Subjects', COUNT(*) FROM subjects
UNION ALL
SELECT 'Tutor Profiles', COUNT(*) FROM tutor_profiles
UNION ALL
SELECT 'Booking Requests', COUNT(*) FROM booking_requests
UNION ALL
SELECT 'Reviews', COUNT(*) FROM reviews
UNION ALL
SELECT 'Classes', COUNT(*) FROM classes
UNION ALL
SELECT 'Enrollments', COUNT(*) FROM enrollments
UNION ALL
SELECT 'Feedback', COUNT(*) FROM feedback;

-- Display sample users
SELECT id, email, role, first_name, last_name FROM users ORDER BY id;

-- ============================================
-- SETUP COMPLETE
-- ============================================

SELECT 
    '✅ Database setup complete!' as status,
    'Total Tables: 13' as tables,
    'Sample Data: Loaded' as data,
    'Ready to use!' as message;
