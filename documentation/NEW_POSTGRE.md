# TutorFind DB (Aligned to Current Backend)

Total tables: 14

- users: accounts (email, password_hashed, role string, profile fields, is_active, created_at)
- testusers: dev/demo users (id, name)
- cities: name, country, is_active, created_at
- districts: city_id, name, is_active, created_at
- subjects: name, category, is_active, created_at
- tutor_profiles: user_id (unique), city_id, headline, bio, qualifications, experience_years, monthly_rate, rating_avg/count, is_verified, is_active, timestamps
- tutor_subjects: tutor_id, subject_id, proficiency_level, created_at, unique(tutor_id, subject_id)
- tutor_districts: tutor_id, district_id, created_at, unique(tutor_id, district_id)
- availability_slots: tutor_id, day_of_week, start_time, end_time, is_active, created_at, unique(tutor_id, day_of_week, start_time, end_time)
- booking_requests: learner_id, tutor_id, subject_id, session_type, mode, slot_day/time/text, preferred_schedule, learner_note, tutor_response, proposed_price, sessions_count, status, timestamps
- reviews: tutor_id, learner_id, booking_id, rating (1-5), comment, status, timestamps, unique(tutor_id, learner_id)
- feedback: tutor_id, learner_id, booking_id, subject_id, session_date, feedback_text, strengths, areas_for_improvement, timestamps
- classes: tutor_id, subject_id, name, description, class_type, max/current_students, price_per_session, total_sessions, schedule_day/time, duration_minutes, status, start/end dates, timestamps
- enrollments: class_id, learner_id, enrollment_date, status, sessions_attended, payment_status, amount_paid, created_at, unique(class_id, learner_id)

Indexes: tutor_profiles (city, verified, active); booking_requests (status, learner, tutor); reviews (status, tutor); classes (status, tutor); enrollments (class, learner); availability_slots (tutor, day).
