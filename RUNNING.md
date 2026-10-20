# Task Flow — Local Run Guide

## Requirements
- JDK 17 or newer
- Maven 3.9+
- Apache Tomcat 10.1+
- Oracle Database 19c or newer

## Configure Oracle

Set the connection values in PowerShell before starting Tomcat:

```powershell
$env:DAYFLOW_DB_URL="jdbc:oracle:thin:@localhost:1521/XEPDB1"
$env:DAYFLOW_DB_USER="DAYFLOW"
$env:DAYFLOW_DB_PASSWORD="your_password"
```

For a new, empty schema, run `database/dayflow_schema.sql` once. For an existing Task Flow schema, run the applicable additive migrations in `database/`. Do not run the fresh schema script against populated tables.

## Build

```powershell
mvn clean package
```

The WAR is generated at `target/DayFlow.war`.

## Deploy

Deploy `target/DayFlow.war` to Tomcat 10.1's `webapps` directory, start Tomcat, then open `http://localhost:8080/DayFlow/`.

## Resources

The Resources module is available at `/resources`. Its migration is `database/resources_module_migration.sql`. It includes document upload, saved links, edit/delete, preview/download, search, filtering, ownership checks, URL validation, and upload security.
