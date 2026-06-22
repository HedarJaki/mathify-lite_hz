# Design Assets

The `design/` directory holds visual/UX references used while building the
app. It's not code — nothing in it is loaded by the application at runtime.
This file is an index of what's there and why. See
[ARCHITECTURE.md](ARCHITECTURE.md) for how these mockups map onto the real
implementation.

## Interactive HTML mockups

Clickable, self-contained prototypes (HTML + inline JS, Bootstrap 5.3 via
CDN) used to work out screen layout and flow before implementing the real
JSPs/servlets. Open any of them directly in a browser.

- **`Mathify.dc.html`** — the main app prototype: student dashboard, course
  catalog, course/module/quiz screens, and the admin dashboard, course
  editor, and quiz editor.
- **`Mathify Auth.dc.html`** — login/register screen prototype (split brand
  panel + form), used as the visual reference for `login.jsp`/`register.jsp`.
- **`Mathify Loading.dc.html`** — the loading-screen animation prototype,
  including the `.mf-*` CSS and markup notes for porting it into JSP (see
  `com.mathify.util.NavigationUtil.redirectWithLoading`).
- **`support.js`** — shared runtime helper script the three prototypes above
  load via `<script src="./support.js">`. Not used by the real app.
- **`.thumbnail`** — cached preview thumbnail image for the prototypes.

## UML class diagram

- **`uploads/Mathify.json`** — Lucidchart export of the domain model class
  diagram (`User`/`Student`/`Admin`, `Course`/`Chapter`/`Topic`, quiz and
  question hierarchy, `Achievement`, etc.). This is the diagram the
  `src/main/java/com/mathify/model/` classes were derived from; open it at
  lucid.app (Import) to view/edit it visually.
