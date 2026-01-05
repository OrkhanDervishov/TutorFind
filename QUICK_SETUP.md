# TutorFind — Quick Setup (Local Rerun)

This is the **minimum, correct order** to rerun the project locally.

## Prerequisites
- PostgreSQL running locally (default port **5432**)
- Java JDK installed (`java -version`)
- Node.js + npm installed (`node -v`, `npm -v`)

Typical repo folders:
- Backend: `tutorfind_back/`
- Frontend: `tutorfind_front/`
- DB schema script: `new_postgre_prep.sql`

---

## 1) Create the database

Open PowerShell and run:

```powershell
psql -U postgres -h localhost -d postgres
```

Inside `psql`:

```sql
CREATE DATABASE tutorfind;
\q
```

> Use **lowercase** `tutorfind` to avoid name/quoting issues.

---

## 2) Apply the schema

From the folder that contains `new_postgre_prep.sql`:

```powershell
psql -U postgres -h localhost -d tutorfind -f new_postgre_prep.sql
```

---

## 3) Configure backend DB connection

Edit:

`./tutorfind_back/src/main/resources/application.properties`

Set your DB connection (example):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tutorfind
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

# Project default used in QUICK_SETUP:
server.port=9090
```

---

## 4) Run the backend (Spring Boot)

```powershell
cd .\tutorfind_back
.\mvnw.cmd spring-boot:run
```

Backend should be available at:

- `http://localhost:9090`

---

## 5) Configure and run the frontend (React + Vite)

Create or edit:

`./tutorfind_front/.env`

```env
VITE_API_URL=http://localhost:9090
```

Run:

```powershell
cd ..\tutorfind_front
npm install
npm run dev
```

Vite will print the local URL (commonly `http://localhost:5173`).

---

## Common issues

### “database does not exist”
- Confirm the DB name is exactly `tutorfind` (lowercase).
- Confirm backend URL points to `.../tutorfind`.

### PowerShell: “mvnw.cmd is not recognized”
You forgot `.\`:

```powershell
.\mvnw.cmd spring-boot:run
```

### Frontend can’t call backend
- Confirm backend is running and listening on **9090**.
- Confirm `.env` has `VITE_API_URL=http://localhost:9090`.
