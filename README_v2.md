# TutorFind — README (Start Here)

TutorFind is a tutor–learner matching platform (similar to Upwork for tutoring) with a Spring Boot backend and a React + Vite frontend. The project supports searching tutors, booking requests, reviews, tutor feedback, availability management, and extensions for classes/enrollment and an admin console.

This **README.md** is the entry point that explains what each of the other `.md` files contains (the detailed instructions and documentation are intentionally split across files).

---

## Quick start (rerun the project)

Use **`QUICK_SETUP.md`** for the full step-by-step rerun guide on Windows PowerShell (PostgreSQL → schema → backend → frontend).

Minimal checklist (details are in `QUICK_SETUP.md`):

1) Start PostgreSQL and confirm `psql` works.
2) Create a fresh **lowercase** database named `tutorfind`.
3) Apply schema from `new_postgre_prep.sql` into `tutorfind`.
4) Set backend DB connection in:
   - `tutorfind_back/src/main/resources/application.properties`
5) Run backend (Spring Boot).
6) Set frontend API base URL in `tutorfind_front/.env` (`VITE_API_URL=...`), then run frontend.

Notes:
- The canonical rerun instructions (including the backend port) are in **`QUICK_SETUP.md`**.

---

## Documentation map (what each `.md` file is for)

### 1) `QUICK_SETUP.md` — how to rerun locally
**Purpose:** Clean, “correct order” local setup instructions (Windows PowerShell).  
**Use when:** You want to run the project from scratch on a new machine or rerun after changes.

Includes:
- Creating and verifying the `tutorfind` database
- Applying `new_postgre_prep.sql`
- Setting `application.properties` and `VITE_API_URL`
- Common fixes (PowerShell `.\mvnw.cmd`, “database does not exist”, etc.)

---

### 2) `BACKEND.md` — backend complete documentation
**Purpose:** Full backend documentation (architecture, features, endpoints, security, structure, troubleshooting).  
**Use when:** You need to understand backend design decisions, API surface, and business logic.

Includes:
- Architecture + project structure
- Feature breakdown and endpoint list
- Security (JWT) and configuration
- Database schema overview from backend side

---

### 3) `FRONTEND.md` — frontend current state + integration
**Purpose:** Explains how the React frontend is structured and how it calls the backend.  
**Use when:** You want to run/extend the UI, understand pages/routes, or see which endpoints are connected.

Includes:
- Folder + file structure in `tutorfind_front/`
- Route map and page responsibilities
- How API calls are made via `VITE_API_URL` + auth token handling
- Running the frontend (`npm install`, `npm run dev`)

---

### 4) `NEW_POSTGRE.md` — database plan + schema notes
**Purpose:** Database plan and schema notes (what tables exist and why), aligned with the SQL script(s).  
**Use when:** You want to understand the DB design or modify schema.

Includes:
- Table-level plan and rationale
- Notes that correspond to `new_postgre_prep.sql` (schema apply script)

---

### 5) `ENROLLMENT_ADMIN.md` — classes/enrollment + admin console extensions
**Purpose:** Documents the added “Classes & Enrollment” feature set and the admin console changes.  
**Use when:** You need to understand the added entities/repos/services/controllers and the related frontend flows.

Includes:
- Files created/modified for classes + enrollment + admin console
- Endpoint usage + sample frontend flow outlines
- Admin actions (stats, user activation, tutor verification, review moderation)

---

### 6) `NEW_FEATURES.md` — additional features summary (part 1)
**Purpose:** Notes about newly added features and changes beyond the base implementation.  
**Use when:** You want a compact summary of the updates and how they changed behavior/endpoints.

---

### 7) `NEW_FEATURES_2.md` — additional features summary (part 2)
**Purpose:** Continuation of feature changes/bug fixes (more detailed breakdown).  
**Use when:** You are verifying recent changes or understanding how fixes were implemented.

---

### 8) `PROPOSAL.md` — original project proposal
**Purpose:** Initial scope/proposal and early planning notes.  
**Use when:** You want to see how the final delivered system maps to the proposed idea/scope.

---

## Repo layout (high-level)

Typical top-level layout (exact names may differ slightly by clone/path):

- `tutorfind_back/` — Spring Boot backend
- `tutorfind_front/` — React + Vite frontend
- `new_postgre_prep.sql` — DB schema apply script used in `QUICK_SETUP.md`
- `*.md` — documentation files listed above

---

## Common rerun issues

- **“database does not exist”**: ensure your DB name is exactly `tutorfind` (lowercase), and backend URL points to it.
- **PowerShell can’t run mvnw**: in PowerShell you must run local commands with `.\` (e.g., `.\mvnw.cmd ...`).
- **Frontend can’t reach backend**: confirm backend port and `.env` (`VITE_API_URL`) match.

---

## Where to start

- Want to run the project: **`QUICK_SETUP.md`**
- Want API and backend logic: **`BACKEND.md`**
- Want UI structure and pages: **`FRONTEND.md`**
- Want DB plan and schema reasoning: **`NEW_POSTGRE.md`**
- Want classes/enrollment + admin extensions: **`ENROLLMENT_ADMIN.md`**
