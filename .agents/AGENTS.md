# Project Rules and Knowledge

See [ARCHITECTURE.md](../ARCHITECTURE.md) for how the app fits together,
[README.md](../README.md) for setup, and [CLAUDE.md](../CLAUDE.md) for the
equivalent rules file used by Claude Code — keep the two in sync when either
changes.

## Servlet Mappings and Deployment
- **Avoid Duplicate Mappings:** Do NOT map a servlet in `web.xml` if it already has a `@WebServlet` annotation. Doing so will cause Tomcat to crash during startup and result in 404s for the entire application.
- **Context Path:** The `cargo-maven3-plugin` in this project is configured to deploy at the root context (`<context>/</context>`). This means the application is accessible at `http://localhost:8080/`, not `http://localhost:8080/mathify-lite/`. Always account for this when manually pinging URLs or debugging routing.
- **Browser Caching:** When modifying static assets like `.js` files or static links, remember that browsers aggressively cache them. If an endpoint is functioning correctly but the UI is misbehaving, suspect a cached `.js` file before assuming the backend or deployment is broken. ALWAYS append a cache-busting query parameter (e.g. `?v=2`) to script and link tags (like `app.js?v=2`) whenever you update static assets to ensure users see changes immediately.

## Navigation and Loading Screens
- **Use Loading Screens:** For forward-moving screen transitions (e.g., successful login, moving to the dashboard, loading catalog items, etc.), do NOT use standard `response.sendRedirect()`. Instead, use `com.mathify.util.NavigationUtil.redirectWithLoading(req, resp, url, message)`.
- **Fast Redirects for Errors:** Standard `response.sendRedirect()` should ONLY be used for validation errors (e.g., missing fields, wrong password) where the user expects an instant flash back to the form they just submitted.

## Database and Schema
- **Keep the schema in sync with the DAOs.** DAO queries assume columns that were not always present in `database/mathify_schema.sql`. A missing column shows up as a `?error=server_error` redirect on login/register (the DAO throws `SQLSyntaxErrorException`, the servlet catches it). Known case: `users.is_disabled BOOLEAN NOT NULL DEFAULT FALSE` (admin "disable student" feature). When you add a column in a DAO, add it to the schema file AND a `database/migration_*.sql`.
- **Schema location:** `database/mathify_schema.sql` (not the repo root). Seeds and migrations live under `database/` too.
- **Local DB:** defaults target `localhost:3306`, user `root`, empty password — matches a stock XAMPP MySQL/MariaDB. XAMPP also ships its own Tomcat on **port 8080**; if `cargo:run` reports "Port 8080 in use", stop XAMPP's Tomcat (only MySQL is needed).

## Payments (Midtrans)
- **Snap flow, sandbox by default.** Credentials come from a gitignored `.env` at the project root, read by `com.mathify.util.MidtransConfig` (`MIDTRANS_SERVER_KEY`, `MIDTRANS_CLIENT_KEY`, `MIDTRANS_MERCHANT_ID`, `MIDTRANS_IS_PRODUCTION`, `MIDTRANS_PREMIUM_PRICE`); OS env vars override the file. Never hard-code keys; see `.env.example`.
- **Server is the source of truth.** `PremiumCheckoutServlet` (`/student/premium/checkout.do`) creates the Snap token with a server-side fixed amount; `PremiumConfirmServlet` (`/student/premium/confirm.do`) re-verifies the transaction status with Midtrans before granting premium. NEVER trust the browser's success callback or a client-supplied amount. HTTP calls live in `com.mathify.service.MidtransService` (Basic auth = `Base64(serverKey + ":")`).
- **No webhook needed on localhost** — activation happens in `confirm.do` via a status check. For production, also configure a Midtrans notification URL.
- **Schema:** premium is persisted by `com.mathify.dao.SubscriptionDAO` into the existing `subscriptions` table; tracking columns `midtrans_order_id` + `payment_status` were added (`database/migration_add_payment_tracking.sql`).
- **Testing:** card `4811 1111 1111 1114`, CVV `123`, OTP `112233`. Sandbox QRIS QR codes are NOT scannable by real apps — use `https://simulator.sandbox.midtrans.com/`.
- **Navbar data:** the premium badge / energy / XP come from `GET /student/profile.do` (`StudentProfileServlet`), fetched by `app.js`. JSON is built/parsed with `org.json` (in `pom.xml`).
