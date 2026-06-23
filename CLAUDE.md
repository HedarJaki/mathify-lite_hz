# CLAUDE.md

Guidance for Claude Code (and other agents) working in this repository.

## Related docs

- [README.md](README.md) — setup & how to run the project
- [ARCHITECTURE.md](ARCHITECTURE.md) — request flow, auth, data layer, build/deploy
- [DESIGN.md](DESIGN.md) — index of the visual mockups and UML diagram in `design/`
- [AGENTS.md](.agents/AGENTS.md) — the equivalent rules file for non-Claude agents (Codex, etc.); keep in sync with this file when either changes

## Project

**Mathify Lite** — a Java servlet web application for a gamified math learning
platform (courses, modules, quizzes, achievements, streaks, premium plans).
It is built as a WAR and runs on Tomcat 10.1 (Jakarta Servlet 6.0).

## Tech stack

- **Java 17**, Maven (`pom.xml`), packaged as a **WAR** (`mathify-lite`).
- **Jakarta Servlet 6.0** (`jakarta.servlet`, *not* `javax.servlet`).
- **JSP** for all views — plain JSP, no JSTL or templating framework.
- **MySQL** via `mysql-connector-j` and raw JDBC (`java.sql.*`).
- **Bootstrap 5.3** + Bootstrap Icons + Google Fonts, loaded from CDN.
- JUnit 4 for tests.

## Build & run

```bash
mvn package cargo:run     # build the WAR and launch embedded Tomcat 10.1
```

Then open **http://localhost:8080/** (the app is deployed at context path `/`).
Cargo downloads Tomcat automatically on first run into `target/`.

```bash
mvn compile               # compile Java only
mvn package               # build target/mathify-lite.war
```

## Database

- Schema lives in **`database/mathify_schema.sql`**; database name `mathify_db`.
  Seed scripts (`database/insert_admin.sql`, `database/insert_course_1.sql`) and
  migrations also live under `database/`.
- Connection settings are in **`src/main/resources/db.properties`**
  (`db.url`, `db.user`, `db.password`), read once by
  `com.mathify.db.DBUtil` which exposes `DBUtil.getConnection()`.
- Default config expects a local MySQL/MariaDB at `localhost:3306`, user `root`,
  empty password (matches a default XAMPP install). Do **not** commit real
  credentials.
- **Keep the schema in sync with the DAOs.** The code expects columns that were
  not always reflected in `mathify_schema.sql` — e.g. `users.is_disabled`
  (`BOOLEAN NOT NULL DEFAULT FALSE`, used by the admin "disable student"
  feature). A missing column surfaces as a `?error=server_error` redirect on
  login/register. If you add a column in a DAO, also add it to the schema file
  and a `database/migration_*.sql`.

## Payments (Midtrans)

The **Go Premium** upgrade uses the **Midtrans Snap** flow (sandbox by default).

- **Credentials** are read from a gitignored **`.env`** at the project root by
  `com.mathify.util.MidtransConfig` (keys: `MIDTRANS_SERVER_KEY`,
  `MIDTRANS_CLIENT_KEY`, `MIDTRANS_MERCHANT_ID`, `MIDTRANS_IS_PRODUCTION`,
  `MIDTRANS_PREMIUM_PRICE`). OS env vars override `.env`. See `.env.example`.
  `MidtransConfig` finds `.env` by walking up from the working directory, which
  works under `mvn cargo:run` (cwd = project root). Sandbox keys may or may not
  carry an `SB-` prefix; verify against the **sandbox** dashboard, not production.
- **Flow:** `assets/js/app.js`-independent inline script on `student/premium.jsp`
  → `POST /student/premium/checkout.do` (`PremiumCheckoutServlet`) creates a Snap
  token server-side via `com.mathify.service.MidtransService` (Basic auth =
  `Base64(serverKey + ":")`). The browser opens Snap with that token; on
  success/pending it calls `POST /student/premium/confirm.do`
  (`PremiumConfirmServlet`), which **re-checks the status server-to-server**
  before granting premium. The client's own success callback is never trusted.
- **The amount is fixed server-side** (`MidtransConfig.getPremiumPriceIdr`, IDR);
  never take the charge amount from the client.
- **Persistence:** `com.mathify.dao.SubscriptionDAO` upserts the `subscriptions`
  row. Two columns were added for tracking: `midtrans_order_id VARCHAR(100)` and
  `payment_status VARCHAR(30)` (migration: `database/migration_add_payment_tracking.sql`).
- **No webhook on localhost:** premium is activated during `confirm.do` via a
  status check, so the integration works without a public notification URL. For
  production, also configure a Midtrans notification/webhook endpoint.
- **Testing:** sandbox card `4811 1111 1111 1114`, CVV `123`, OTP `112233` is the
  quickest path. Sandbox QRIS QR codes cannot be scanned by real apps — use the
  Midtrans simulator (`https://simulator.sandbox.midtrans.com/`).
- The navbar premium badge / energy / XP are filled from `GET /student/profile.do`
  (`StudentProfileServlet`), fetched by `app.js`; it falls back to sample values
  on error. `org.json` (in `pom.xml`) is used for all JSON build/parse.

## Layout

```
src/main/java/com/mathify/
  db/       DBUtil            — JDBC connection factory (reads db.properties)
  dao/      UserDAO           — data access (PreparedStatements, returns models)
  model/    35 domain classes — User/Student/Admin, Course/Chapter/Topic,
                                quizzes & questions, progress, achievements…
  servlet/  LoginServlet  (/login),  RegisterServlet (/register)
src/main/webapp/
  WEB-INF/web.xml             — servlet mappings + welcome files
  index.jsp                   — entry; redirects to student/dashboard.jsp
  login.jsp, register.jsp     — auth pages (POST to /login, /register)
  student/*.jsp               — dashboard, catalog, course, module, quiz,
                                achievements, premium
  admin/*.jsp                 — dashboard, courses, editor, quiz-editor, students
  assets/css/app.css          — shared styles
  assets/js/app.js            — injects the shared navbar/header into every page
src/main/resources/db.properties
mathify_schema.sql            — MySQL schema
```

## Conventions & gotchas

- **Views are JSP, not HTML.** All pages use the `.jsp` extension and start with
  `<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>`.
  When adding a page or linking between pages, always reference `.jsp`.
- The current JSPs are essentially static markup (no scriptlets/EL). If you add
  dynamic JSP, beware that literal `<%` and `${...}` in inline `<script>`/`<style>`
  are interpreted by the JSP engine — escape them (`<\%`, `\${`) or use
  `<%@ page isELIgnored="true" %>` where appropriate.
- Navigation chrome is **not** in the page markup — `assets/js/app.js` builds the
  navbar at runtime from `data-role` / `data-page` / `data-base` attributes on
  `<body>`. Update `app.js` when adding nav destinations, not each page.
- Use the **Jakarta** namespace (`jakarta.servlet.*`). Never `javax.servlet.*`.
- **Two servlet registration styles coexist.** The original auth servlets
  (`LoginServlet`, `RegisterServlet`) are mapped in `web.xml`. Newer servlets in
  `servlet/student/` and `servlet/admin/` use `@WebServlet` annotations (e.g.
  `@WebServlet("/student/dashboard.do")`) and are component-scanned — follow the
  annotation style for new servlets in those packages. `StudentAuthFilter`
  (`@WebFilter("/student/*")`) and `AdminAuthFilter` guard those routes.
- DAO methods throw `SQLException`; servlets catch it, log via
  `getServletContext().log(...)`, and redirect with an `?error=...` query param
  that the target JSP reads client-side to show an alert banner.
- `target/` is build output — never edit files there; edit `src/`.
