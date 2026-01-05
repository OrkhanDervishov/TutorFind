````md
# TutorFind — Local Setup (Windows PowerShell)

This guide is the **clean, correct order** to run the project locally on Windows using **PowerShell**.

## Prerequisites
- PostgreSQL running locally (default port **5432**)
- Java JDK installed (run `java -version` to confirm)
- Node.js + npm installed (run `node -v` and `npm -v`)

Project folders (typical):
- Backend: `TutorFind-features-backend\tutorfind_back`
- Frontend: `TutorFind-features-backend\tutorfind_front`
- Schema file: `new_postgre_prep.sql` (wherever it is in your repo)

---

## 1) Start PostgreSQL
Make sure the PostgreSQL service is running.

Quick connectivity check:
```powershell
psql -U postgres -h localhost -d postgres -c "SELECT version();"
````

---

## 2) Create the database (use lowercase)

**Important:** PostgreSQL folds unquoted names to lowercase. We use `tutorfind` to avoid case/quote problems.

```powershell
psql -U postgres -h localhost -d postgres -c "DROP DATABASE IF EXISTS tutorfind;"
psql -U postgres -h localhost -d postgres -c "CREATE DATABASE tutorfind;"
```

Verify it exists:

```powershell
psql -U postgres -h localhost -d postgres -c "\l"
```

---

## 3) Apply the schema

Run the schema into the `tutorfind` database.

From the folder that contains `new_postgre_prep.sql`:

```powershell
psql -U postgres -h localhost -d tutorfind -f new_postgre_prep.sql
```

✅ After this, your tables should exist inside `tutorfind`.

> Note: if you re-run the script often, consider adding these two lines near the top of the SQL file (so reruns don’t fail):
>
> ```sql
> DROP TABLE IF EXISTS notifications CASCADE;
> DROP TABLE IF EXISTS flags CASCADE;
> ```

---

## 4) Configure the backend to use the same database

Edit:
`tutorfind_back\src\main\resources\application.properties`

Make sure the DB name is **tutorfind**:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tutorfind
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
server.port=9090
```

---

## 5) Run the backend

Go to the backend folder (where `mvnw.cmd` is next to `pom.xml`) and run it.

```powershell
cd .\TutorFind-features-backend\tutorfind_back
.\mvnw.cmd spring-boot:run
```

Backend should start at:

* `http://localhost:9090`

> PowerShell note: commands in the current folder must be run with `.\` (that’s why it’s `.\mvnw.cmd`).

---

## 6) Configure + run the frontend

Edit the frontend `.env` (inside `tutorfind_front`) and point it to the backend:

```env
VITE_API_URL=http://localhost:9090
```

Then run:

```powershell
cd ..\tutorfind_front
npm install
npm run dev
```

Frontend usually runs at:

* `http://localhost:5173`

---

## Common quick fixes

### “database does not exist”

You’re connecting to the wrong name. Use:

* `tutorfind` (lowercase)

### “mvnw.cmd is not recognized”

You forgot `.\` in PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

### Check what DBs exist

```powershell
psql -U postgres -h localhost -d postgres -c "\l"
```

```

If you want, paste your `application.properties` DB URL line and your `.env` line and I’ll tell you exactly what to set them to (no guessing).
```
