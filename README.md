# Task Flow

**Plan less. Do more.**

Task Flow is built on the existing Java web stack: Java 17, JSP/JSTL, Jakarta Servlets, JDBC, Oracle, Maven and Tomcat 10.1. The current application also contains its existing personal task, calendar, focus and progress features; this setup work keeps them intact.

## Requirements

- JDK 17+
- Maven 3.9+
- Apache Tomcat 10.1+
- Oracle Database 19c or newer

## Database setup

Configure the existing Oracle connection with environment variables before starting Tomcat:

```powershell
$env:DAYFLOW_DB_URL="jdbc:oracle:thin:@localhost:1521/XEPDB1"
$env:DAYFLOW_DB_USER="DAYFLOW"
$env:DAYFLOW_DB_PASSWORD="your_password"
```

For a brand-new, empty application schema, run `database/dayflow_schema.sql` once. It creates the application tables, including unique usernames. It does not drop tables or clear data.

For an existing Task Flow schema, run `database/task_flow_auth_migration.sql` once instead. This additive migration keeps existing accounts and gives each a generated username (`user_<id>`); users can continue logging in with their email. Do not run the fresh-schema script against a populated schema.

For password recovery, run `database/password_reset_migration.sql` once after the user table exists. Configure SMTP before starting Tomcat so reset links can be delivered:

```powershell
$env:DAYFLOW_SMTP_HOST="smtp.example.com"
$env:DAYFLOW_SMTP_PORT="587"
$env:DAYFLOW_SMTP_FROM="Task Flow <no-reply@example.com>"
$env:DAYFLOW_SMTP_USER="smtp-user"
$env:DAYFLOW_SMTP_PASSWORD="smtp-password"
$env:DAYFLOW_SMTP_STARTTLS="true"
```

`DAYFLOW_SMTP_USER` and `DAYFLOW_SMTP_PASSWORD` may be omitted when the SMTP server does not require authentication. Reset requests return the same message whether an email is registered or not. Each link is single-use and expires after 30 minutes.

## Build and run

Build the WAR:

```powershell
mvn clean package
```

Deploy `target/DayFlow.war` to Tomcat's `webapps` directory and start Tomcat. Open `http://localhost:8080/DayFlow/`.

## Public deployment on Render

The repository includes a Dockerfile and Render Blueprint for a Tomcat 10.1 service. To deploy:

1. Open [Render Blueprint setup](https://dashboard.render.com/blueprint/new?repo=https%3A%2F%2Fgithub.com%2FKrithika-ananth%2FTaskFlow) and connect the `TaskFlow` repository.
2. In Render, enter `DAYFLOW_DB_URL`, `DAYFLOW_DB_USER`, and `DAYFLOW_DB_PASSWORD` for a publicly reachable Oracle database. Keep credentials in Render's environment settings; do not commit them.
3. Apply the Blueprint and wait for its first deploy to finish. Render provides the public `https://taskflow-....onrender.com` URL.

The Blueprint uses Render's free instance, which can spin down when idle. Its first request after a spin-down may take longer. Initialize the Oracle schema and any required migrations before testing signup or login.

The landing page links to signup and login. Successful signup redirects to login. Successful login creates a session and opens the existing authenticated dashboard. Logout invalidates that session.

## Manual signup and login check

1. Open `/DayFlow/register` and submit a full name, unique username, valid email, and matching password of at least 8 characters.
2. Confirm the success message on `/DayFlow/login`; sign in using either the username or email and the password.
3. Confirm the authenticated dashboard opens. Log out, then try a wrong password and confirm an error appears without creating a session.
4. Try signing up again with the same username or email and confirm a useful duplicate-account message appears.

Passwords are stored as salted PBKDF2-HMAC-SHA256 hashes. New hashes use 600,000 iterations; existing lower-cost hashes are upgraded at the next successful login. Username and email uniqueness are enforced in Oracle as well as checked during signup. On an existing database, the auth migration is required before the updated application can query the `USERNAME` column.

## Project structure

```text
src/main/java/com/dayflow/  Servlets, DAO, models, filters, database/password utilities
src/main/webapp/            JSP pages, shared fragments, CSS, JavaScript and images
database/                   Oracle fresh-schema script and additive auth migration
```
