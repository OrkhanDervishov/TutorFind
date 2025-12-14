Install & start PostgreSQL
Install PostgreSQL 14+ (from postgresql.org) and make sure psql is in your PATH.
Start the service:
Windows (default install): open “Services” → start “postgresql-14” (or run net start postgresql-x64-14 in admin PowerShell).
macOS (Homebrew): brew services start postgresql.
Linux (systemd): sudo systemctl start postgresql.
Default superuser is usually postgres. If you set a different user/pass at install, use that. To set/change a password for postgres:
psql -U postgres -c "ALTER USER postgres WITH PASSWORD '<new_password>';"
Clone the repo
git clone <your-repo-url>
cd tutorfind
Create the database and load schema
psql -U <db_user> -c "CREATE DATABASE tutorfind;"
psql -U <db_user> -d tutorfind -f new_postgre_prep.sql
Use the user/pass from step 1 (<db_user> often postgres).

Configure backend (tutorfind_back/src/main/resources/application.properties)
Create the file with your credentials and a strong JWT secret:
spring.datasource.url=jdbc:postgresql://localhost:5432/tutorfind
spring.datasource.username=<db_user>
spring.datasource.password=<db_pass>
spring.jpa.hibernate.ddl-auto=none
jwt.secret=<put_a_long_random_secret_here>
How to get a secret: generate a long random string, e.g. on any OS with Python:

python - <<'PY'
import secrets
print(secrets.token_hex(32))
PY
Use that output for jwt.secret. Do not commit your real secret.

Run the backend
From repo root:
cd tutorfind_back
mvnw.cmd spring-boot:run # Windows

# or ./mvnw spring-boot:run # macOS/Linux

Backend listens on http://localhost:8080 by default.

Configure frontend (tutorfind_front/.env)
Create or edit:
VITE_API_BASE=http://localhost:8080/api
Install frontend deps and run
cd ../tutorfind_front
npm install
npm run dev
Open the URL shown (default http://localhost:5173).

Smoke test
Backend: visit http://localhost:8080/api/tutors (should return JSON or an empty list).
Frontend: visit http://localhost:5173 and ensure API calls succeed (check browser dev tools → Network).
Port changes (optional)

To change backend port: add server.port=9090 to application.properties, then set VITE_API_BASE=http://localhost:9090/api in .env.
Where credentials live

DB user/pass: in application.properties (keep private).
JWT secret: in application.properties (use a strong random value; keep private).
Do not commit secrets; use local .env/config files per developer.
Service tips

To check Postgres is running: psql -U <db_user> -d postgres -c "SELECT 1;".
To restart Postgres: use the same service command as start (stop/restart variants).
