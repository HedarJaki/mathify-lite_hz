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
