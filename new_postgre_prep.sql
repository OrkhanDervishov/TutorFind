-- TutorFind PostgreSQL Schema (aligned to current backend entities)
-- Tables: 14 (includes TestUsers)

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
DROP TABLE IF EXISTS testusers CASCADE;
DROP TABLE IF EXISTS notifications CASCADE;
DROP TABLE IF EXISTS flags CASCADE;

-- Users (maps to User.java)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50),
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hashed TEXT,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    age INTEGER,
    phone_number VARCHAR(20),
    profile_picture BYTEA,
    role VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Test users (TestUser.java)
CREATE TABLE testusers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255)
);

-- Cities (City.java)
CREATE TABLE cities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    country VARCHAR(100) DEFAULT 'Azerbaijan',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Districts (District.java)
CREATE TABLE districts (
    id BIGSERIAL PRIMARY KEY,
    city_id BIGINT REFERENCES cities(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Subjects (Subject.java)
CREATE TABLE subjects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tutor profiles (TutorDetails.java)
CREATE TABLE tutor_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    city_id BIGINT REFERENCES cities(id),
    headline TEXT,
    bio TEXT,
    qualifications TEXT,
    experience_years INTEGER,
    monthly_rate DECIMAL(10,2),
    rating_avg FLOAT,
    rating_count INTEGER DEFAULT 0,
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tutor subjects (TutorSubject.java)
CREATE TABLE tutor_subjects (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    subject_id BIGINT REFERENCES subjects(id) ON DELETE CASCADE,
    proficiency_level VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tutor_id, subject_id)
);

-- Tutor districts (TutorDistrict.java)
CREATE TABLE tutor_districts (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    district_id BIGINT REFERENCES districts(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tutor_id, district_id)
);

-- Availability slots (AvailabilitySlot.java)
CREATE TABLE availability_slots (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tutor_id, day_of_week, start_time, end_time)
);
ALTER TABLE availability_slots
  ADD CONSTRAINT chk_availability_day_of_week
  CHECK (day_of_week IN ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY'));

-- Booking requests (BookingRequest.java)
CREATE TABLE booking_requests (
    id BIGSERIAL PRIMARY KEY,
    learner_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    subject_id BIGINT REFERENCES subjects(id),
    session_type VARCHAR(50),
    mode VARCHAR(50),
    slot_day VARCHAR(20),
    slot_time VARCHAR(20),
    slot_text TEXT,
    preferred_schedule TEXT,
    learner_note TEXT,
    tutor_response TEXT,
    proposed_price DECIMAL(10,2),
    sessions_count INTEGER DEFAULT 1,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP
);

-- Reviews (Review.java)
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    learner_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    booking_id BIGINT REFERENCES booking_requests(id),
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tutor_id, learner_id)
);

-- Feedback (Feedback.java)
CREATE TABLE feedback (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    learner_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    booking_id BIGINT REFERENCES booking_requests(id),
    subject_id BIGINT REFERENCES subjects(id),
    session_date DATE,
    feedback_text TEXT NOT NULL,
    strengths TEXT,
    areas_for_improvement TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Classes (Class.java)
CREATE TABLE classes (
    id BIGSERIAL PRIMARY KEY,
    tutor_id BIGINT REFERENCES tutor_profiles(id) ON DELETE CASCADE,
    subject_id BIGINT REFERENCES subjects(id),
    availability_slot_id BIGINT NOT NULL REFERENCES availability_slots(id) ON DELETE RESTRICT,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    class_type VARCHAR(50),
    max_students INTEGER DEFAULT 1,
    current_students INTEGER DEFAULT 0,
    price_per_session DECIMAL(10,2),
    total_sessions INTEGER,
    duration_minutes INTEGER,
    status VARCHAR(50) DEFAULT 'OPEN',
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Enrollments (Enrollment.java)
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
    UNIQUE (class_id, learner_id)
);

-- Notifications (Notification.java)
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(100) NOT NULL,
    payload TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Flags (Flag.java)
CREATE TABLE flags (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    content_type VARCHAR(50) NOT NULL,
    content_id BIGINT NOT NULL,
    reason TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


INSERT INTO users (username, email, password_hashed, first_name, last_name, age, phone_number, profile_picture, role)
VALUES
  ('testuser', 'test@example.com',
   '$2a$10$HZOLlxI.uv07Af40BpeddumVN2jIeTpvKVerO7XPUQGhUjwnrPJBS',
   'Test', 'User', 25, '+994501234567', NULL, 'LEARNER'),

  ('newuser', 'new@example.com',
   '$2a$10$HIKK8K3yMmalvs7XHb4SBuDCsClCVhFv.baQtwmNiOQ/v5BH/1D0i',
   'New', 'User', 25, '+994507654321', NULL, 'LEARNER'),

  ('test_1', '999@gmail.com',
   '$2a$10$.umYQEKD52LmOxGqMM97vO.VLVazYg4mbLC97l5MmT5b3.g.pdFv2',
   'test_1', 'test_1', 19, '+994999999', NULL, 'LEARNER'),

  (NULL, 'tutor@example.com',
   '$2a$10$Tf69kqiIadxvkgTKXa0T/O4ls1bntN69mEtomK7ceR/3uJnAqWLrm',
   'John', 'Smith', 30, '+994551234567', NULL, 'TUTOR'),

  ('admin', 'admin@tutorfind.com',
   '$2a$12$J4pxd3Z1BWIBGSMvcap0Fuzu2oKn1pMGl0YwrcqIXjVMqu1TXTFvC',
   'Platform', 'Admin', 30, '+994501111111', NULL, 'ADMIN')
ON CONFLICT (email) DO NOTHING;

-- Optional: TestUsers table sample
INSERT INTO testusers (name)
VALUES ('Sample Test User')
ON CONFLICT DO NOTHING;

-- 2) Cities
INSERT INTO cities (name, country)
VALUES
  ('Baku', 'Azerbaijan'),
  ('Sumqayit', 'Azerbaijan'),
  ('Ganja', 'Azerbaijan')
ON CONFLICT (name) DO NOTHING;

-- 3) Districts (for Baku)
INSERT INTO districts (city_id, name)
SELECT c.id, d.name
FROM cities c
JOIN (VALUES
  ('Nasimi'),
  ('Sabail'),
  ('Yasamal'),
  ('Narimanov'),
  ('Binagadi'),
  ('Sabunchu'),
  ('Surakhani'),
  ('Khatai'),
  ('Garadagh'),
  ('Pirallahi')
) AS d(name) ON TRUE
WHERE c.name = 'Baku';

-- 4) Subjects (24)
INSERT INTO subjects (name, category)
VALUES
  -- Mathematics
  ('Mathematics', 'Mathematics'),
  ('Calculus', 'Mathematics'),
  ('Statistics', 'Mathematics'),
  ('Geometry', 'Mathematics'),

  -- Sciences
  ('Physics', 'Sciences'),
  ('Chemistry', 'Sciences'),
  ('Biology', 'Sciences'),
  ('Environmental Science', 'Sciences'),
  ('Astronomy', 'Sciences'),
  ('Earth Science', 'Sciences'),

  -- Languages
  ('English', 'Languages'),
  ('Spanish', 'Languages'),
  ('French', 'Languages'),
  ('German', 'Languages'),
  ('Mandarin Chinese', 'Languages'),

  -- Humanities
  ('History', 'Humanities'),
  ('Geography', 'Humanities'),
  ('Philosophy', 'Humanities'),
  ('Literature', 'Humanities'),

  -- Technology
  ('Programming', 'Technology'),
  ('Web Development', 'Technology'),
  ('Data Science', 'Technology'),

  -- Arts
  ('Music', 'Arts'),
  ('Visual Arts', 'Arts')
ON CONFLICT (name) DO NOTHING;

-- 5) Tutor profile for tutor@example.com in Baku
INSERT INTO tutor_profiles
  (user_id, city_id, headline, bio, qualifications, experience_years, monthly_rate, rating_avg, rating_count, is_verified, is_active)
SELECT
  u.id,
  c.id,
  'Experienced Mathematics & Physics Tutor',
  'I have 5 years of experience teaching mathematics and physics to high school and university students. My goal is to make complex concepts easy to understand.',
  'MSc in Mathematics, BSc in Physics, TEFL Certified',
  5,
  250.00,
  4.5,
  2,
  TRUE,
  TRUE
FROM users u
JOIN cities c ON c.name = 'Baku'
WHERE u.email = 'tutor@example.com'
ON CONFLICT (user_id) DO NOTHING;

-- 6) Tutor subjects (with proficiency_level)
INSERT INTO tutor_subjects (tutor_id, subject_id, proficiency_level)
SELECT tp.id, s.id, 'ADVANCED'
FROM tutor_profiles tp
JOIN users u ON u.id = tp.user_id
JOIN subjects s ON s.name IN ('Mathematics', 'Calculus', 'Physics')
WHERE u.email = 'tutor@example.com'
ON CONFLICT (tutor_id, subject_id) DO NOTHING;

-- 7) Tutor districts (Nasimi, Sabail, Yasamal)
INSERT INTO tutor_districts (tutor_id, district_id)
SELECT tp.id, d.id
FROM tutor_profiles tp
JOIN users u ON u.id = tp.user_id
JOIN districts d ON d.name IN ('Nasimi', 'Sabail', 'Yasamal')
WHERE u.email = 'tutor@example.com'
ON CONFLICT (tutor_id, district_id) DO NOTHING;

-- 8) Availability slots
INSERT INTO availability_slots (tutor_id, day_of_week, start_time, end_time, is_active)
SELECT tp.id, x.day_of_week, x.start_time, x.end_time, TRUE
FROM tutor_profiles tp
JOIN users u ON u.id = tp.user_id
JOIN (VALUES
  ('MONDAY',    '09:00:00'::time, '12:00:00'::time),
  ('MONDAY',    '14:00:00'::time, '18:00:00'::time),
  ('WEDNESDAY', '10:00:00'::time, '16:00:00'::time),
  ('FRIDAY',    '09:00:00'::time, '13:00:00'::time)
) AS x(day_of_week, start_time, end_time) ON TRUE
WHERE u.email = 'tutor@example.com'
ON CONFLICT (tutor_id, day_of_week, start_time, end_time) DO NOTHING;

SELECT setval('availability_slots_id_seq', COALESCE((SELECT MAX(id) FROM availability_slots), 1));

-- Normalize day casing in case of legacy data
UPDATE availability_slots SET day_of_week = UPPER(day_of_week);

-- 9) Sample classes tied to availability slots
INSERT INTO classes (tutor_id, subject_id, availability_slot_id, name, description, class_type, max_students, current_students, price_per_session, total_sessions, duration_minutes, status, start_date, end_date)
SELECT tp.id, s.id, a.id,
       'Advanced Calculus Course',
       'Learn advanced calculus concepts including derivatives, integrals, and differential equations. Perfect for university students.',
       'SMALL_GROUP', 8, 2, 55.00, 10, 120, 'OPEN', '2025-01-15', '2025-03-15'
FROM tutor_profiles tp
JOIN users u ON u.id = tp.user_id AND u.email = 'tutor@example.com'
JOIN subjects s ON s.name = 'Calculus'
JOIN availability_slots a ON a.tutor_id = tp.id AND a.day_of_week = 'MONDAY' AND a.start_time = '14:00:00'::time AND a.end_time = '18:00:00'::time
ON CONFLICT DO NOTHING;

INSERT INTO classes (tutor_id, subject_id, availability_slot_id, name, description, class_type, max_students, current_students, price_per_session, total_sessions, duration_minutes, status, start_date, end_date)
SELECT tp.id, s.id, a.id,
       'Physics Fundamentals',
       'Master the fundamentals of physics including mechanics, thermodynamics, and electromagnetism.',
       'LARGE_GROUP', 20, 20, 40.00, 12, 120, 'FULL', '2025-01-20', '2025-04-20'
FROM tutor_profiles tp
JOIN users u ON u.id = tp.user_id AND u.email = 'tutor@example.com'
JOIN subjects s ON s.name = 'Physics'
JOIN availability_slots a ON a.tutor_id = tp.id AND a.day_of_week = 'WEDNESDAY' AND a.start_time = '10:00:00'::time AND a.end_time = '16:00:00'::time
ON CONFLICT DO NOTHING;

SELECT setval('classes_id_seq', COALESCE((SELECT MAX(id) FROM classes), 1));


-- Indexes
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
CREATE INDEX idx_classes_availability ON classes(availability_slot_id);
CREATE INDEX idx_enrollments_class ON enrollments(class_id);
CREATE INDEX idx_enrollments_learner ON enrollments(learner_id);
CREATE INDEX idx_availability_tutor_day ON availability_slots(tutor_id, day_of_week);
