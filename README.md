# Mathify Lite

A gamified math learning platform — courses, modules, quizzes, achievements,
streaks, and premium plans. Built as a Java servlet web app (Jakarta EE,
JSP views, MySQL) packaged as a WAR and run on Tomcat 10.1.

## Prerequisites

- **JDK 17**
- **Maven 3.6+**
- **MySQL 8.0+** running locally (or reachable) on port `3306`

## 1. Set up the database

1. Start MySQL and connect as a user that can create databases (e.g. `root`).
2. Run the schema script to create the `mathify_db` database and tables:

   ```bash
   mysql -u root -p < database/mathify_schema.sql
   ```

3. (Optional) Seed initial data:

   ```bash
   mysql -u root -p mathify_db < database/insert_admin.sql      # default admin login
   mysql -u root -p mathify_db < database/insert_course_1.sql   # sample course content
   ```

   `insert_admin.sql` creates a default admin account — see the comment at the
   top of that file for the login password.

## 2. Configure the connection

Connection settings are read from `src/main/resources/db.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/mathify_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.user=root
db.password=
```

Edit `db.user` / `db.password` to match your local MySQL setup if it differs
from the defaults. **Do not commit real credentials** — keep this file
pointed at local/dev values only.

## 3. Build and run

```bash
mvn package cargo:run
```

This builds the WAR and launches an embedded Tomcat 10.1 (downloaded
automatically by the Cargo plugin on first run). Once it's up, open:

```
http://localhost:8080/
```

Other useful commands:

```bash
mvn compile   # compile Java only
mvn package   # build target/mathify-lite.war without running it
```

## Project layout

```
src/main/java/com/mathify/
  db/       DBUtil            — JDBC connection factory (reads db.properties)
  dao/      data access objects (PreparedStatements, returns models)
  model/    domain classes — User/Student/Admin, Course/Chapter/Topic, quizzes…
  servlet/  request handlers (registered in web.xml)
src/main/webapp/
  WEB-INF/web.xml             — servlet mappings + welcome files
  student/*.jsp, admin/*.jsp  — student and admin facing pages
  assets/                     — shared CSS/JS
src/main/resources/db.properties — DB connection config
database/mathify_schema.sql      — MySQL schema
```

See [ARCHITECTURE.md](ARCHITECTURE.md) for how the app fits together
(request flow, auth, data layer), [DESIGN.md](DESIGN.md) for the visual
mockups/UML diagram in `design/`, and [CLAUDE.md](CLAUDE.md) for detailed
conventions and gotchas when working in this codebase.
