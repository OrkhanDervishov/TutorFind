# TutorFind Frontend (Current State)

Front-end code lives in `tutorfind_front/` and is a React 18 + Vite SPA that integrates with the Spring Boot backend described in `FULL_BACKEND_README.md` plus the Classes/Enrollment and Admin endpoints in `ENROLLMENT_ADMIN.md`.

## Structure (app code only)
- `tutorfind_front/src/main.jsx` — App bootstrap with RouterProvider + AuthProvider.
- `tutorfind_front/src/router.jsx` — Route map (home, search, tutor profile, bookings dashboards, classes, enrollments, admin, settings, auth).
- `tutorfind_front/src/App.jsx` — Root layout wrapper.
- `tutorfind_front/src/context/AuthContext.jsx` — Auth state with `{token, user}` persisted in `localStorage`.
- `tutorfind_front/src/api/client.js` — Fetch wrapper for all backend endpoints (auth headers, query helpers).
- `tutorfind_front/src/layouts/MainLayout.jsx` — Shell layout with header + outlet.
- `tutorfind_front/src/components/Header.jsx` — Nav with role-aware links (learner/tutor/admin).
- `tutorfind_front/src/components/TutorCard.jsx` - Tutor search/result card.
- Pages:
  - `pages/Home.jsx` — Landing CTA.
  - `pages/SearchResults.jsx` — Tutor search with filters wired to backend.
  - `pages/TutorProfile.jsx` — Tutor detail view + booking request form.
  - `pages/TutorDashboard.jsx` — Tutor bookings inbox, accept/decline, feedback creation.
  - `pages/LearnerDashboard.jsx` — Learner bookings view, review submission, received feedback.
  - `pages/ProfileSettings.jsx` - Tutor profile edit (headline, bio, qualifications, experience, monthly rate) plus subjects/districts management.
  - `pages/Availability.jsx` - Manage availability slots (list with IDs, add, delete).
  - `pages/Classes.jsx` — Public class catalog with status filter and learner enroll action.
  - `pages/ClassDetail.jsx` — Class detail; learner enroll; tutor roster view (when authorized).
  - `pages/TutorClasses.jsx` — Tutor class creation, list, roster view, basic update/delete.
  - `pages/Enrollments.jsx` — Learner enrollments list with drop action and status filter.
  - `pages/AdminDashboard.jsx` — Admin console: stats, tutor verification queue, review moderation, user activate/deactivate.
  - `pages/auth/Login.jsx` — Auth against `/login`.
  - `pages/auth/Signup.jsx` — Registration against `/register`.
- `tutorfind_front/src/styles/index.css` — Tailwind base + component styles.

## Functional Coverage (frontend → backend)
- Auth:
  - Signup `POST /register` (username, email, password, role, first/last name, phone, age).
  - Login `POST /login`; stores `{token, user}`; roles normalized to uppercase.
  - Auth header: `Authorization: Bearer <token>` on protected calls.
- Search & discovery:
  - `GET /api/tutors` with city, district, subject, minPrice, maxPrice, minRating, sortBy (`rating|price_asc|price_desc`).
  - Reference data: `/api/locations/cities`, `/api/locations/districts`, `/api/subjects`.
- Tutor profile:
  - `GET /api/tutors/:id`, `/api/tutors/:id/reviews`.
  - Booking request: `POST /api/bookings` with `{tutorId, subjectId|subject, mode, slot, note, proposedPrice}` (learner login required).
- Bookings:
  - Learner: `/api/bookings/sent?status=...`.
  - Tutor: `/api/bookings/received?status=...`; accept `/api/bookings/{id}/accept`; decline `/api/bookings/{id}/decline`.
- Reviews:
  - Create review: `POST /api/reviews` (tutorId, rating, comment, optional bookingId).
  - Tutor profile shows reviews.
- Feedback (private tutor→learner):
  - Tutor: list `/api/feedback/given`, create `/api/feedback`.
  - Learner: list `/api/feedback/received`.
- Availability & profile (tutor):
  - List availability (with IDs): `GET /api/tutors/availability`.
  - Add availability `POST /api/tutors/availability`; delete `/api/tutors/availability/{id}`.
  - Subjects add/remove: `/api/tutors/subjects` (POST), `/api/tutors/subjects/{subjectId}` (DELETE).
  - Districts add/remove: `/api/tutors/districts` (POST), `/api/tutors/districts/{districtId}` (DELETE).
  - Update profile: `PUT /api/tutors/profile` (headline, bio, qualifications, experienceYears, monthlyRate); fetch via `/api/tutors/:id`.
- Classes & enrollment:
  - Public catalog: `GET /api/classes?status=...`.
  - Class detail: `GET /api/classes/{id}`.
  - Learner enroll: `POST /api/classes/{id}/enroll`.
  - Learner enrollments: `GET /api/classes/enrollments/my?status=...`; drop `DELETE /api/classes/enrollments/{enrollmentId}`.
  - Tutor classes: `GET /api/classes/my-classes`; create `POST /api/classes`; update `PUT /api/classes/{id}`; delete `DELETE /api/classes/{id}`; roster `GET /api/classes/{id}/roster`.
- Admin console:
  - Stats: `GET /api/admin/stats`.
  - Users: `GET /api/admin/users`; activate/deactivate via `PUT /api/admin/users/{id}/activate|deactivate`.
  - Tutor verification: `GET /api/admin/tutors/unverified`; verify/unverify via `PUT /api/admin/tutors/{id}/verify|unverify`.
  - Review moderation: `GET /api/admin/reviews/pending`; approve via `PUT /api/admin/reviews/{id}/approve`; reject via `PUT /api/admin/reviews/{id}/reject` (optional `{reason}` body).

## How it connects to the backend
- Base URL: `VITE_API_URL` (default `http://localhost:8080`), set in project root `.env`.
- `src/api/client.js` appends paths to `VITE_API_URL`, sets JSON headers, and attaches `Authorization: Bearer <token>` when present.
- Error handling: non-2xx responses throw with backend message when available.

## Dependencies
Declared in `tutorfind_front/package.json`:
- react `^18.2.0`
- react-dom `^18.2.0`
- react-router-dom `^6.28.0`
- @vitejs/plugin-react `^4.3.4` (dev)
- vite `^5.2.0` (dev)
- tailwindcss `^3.4.12` (dev)
- postcss `^8.4.47` (dev)
- autoprefixer `^10.4.20` (dev)

## Running the frontend
1) From repo root: `cd tutorfind_front`
2) Install deps: `npm install`
3) Ensure backend is running at `http://localhost:8080` or adjust `VITE_API_URL` in `.env`
4) Start dev server: `npm run dev` (Vite, default port 5173)
5) Build for prod: `npm run build`; preview: `npm run preview`

## Notes and current limitations
- All booking endpoints properly extract user ID from JWT token. Backend handles authentication automatically.
- Availability management assumes tutor profile exists; listing returns slot IDs for deletion.
- Profile update supports headline/bio/quals/experience/rate; base user identity (name/email) remains read-only.
- Class roster view only when tutor is authorized; class update/delete kept minimal.
- Admin console requires an admin user/token created via DB; API blocks admin signup.
