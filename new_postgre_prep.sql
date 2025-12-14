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
CREATE INDEX idx_enrollments_class ON enrollments(class_id);
CREATE INDEX idx_enrollments_learner ON enrollments(learner_id);
CREATE INDEX idx_availability_tutor_day ON availability_slots(tutor_id, day_of_week);
