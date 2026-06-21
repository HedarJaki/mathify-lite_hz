/* ============================================================
   Mathify — shared shell (navbar + profile header)
   Injects the top navigation into every page.

   Each page declares its context on <body>:
     data-role="student" | "admin"
     data-page="dashboard" | "catalog" | ... (matches a nav key)
     data-base="..."   relative path back to /assets root (e.g. "../")
   ============================================================ */
(function () {
  "use strict";

  // Sample profile — mirrors the prototype's initial state.
  var profile = {
    name: "Alex Rivera",
    level: 4,
    totalXP: 1240,
    streak: 12,
    energy: 4,
    energyMax: 5,
    premium: false
  };

  var NAV = {
    student: [
      { key: "dashboard",    label: "Dashboard",    icon: "bi-house-door",      href: "dashboard.do" },
      { key: "catalog",      label: "Courses",      icon: "bi-grid",            href: "catalog.do" },
      { key: "achievements", label: "Achievements", icon: "bi-trophy",          href: "achievements.jsp" },
      { key: "premium",      label: "Premium",      icon: "bi-gem",             href: "premium.jsp" }
    ],
    admin: [
      { key: "overview", label: "Overview", icon: "bi-graph-up-arrow", href: "dashboard.jsp" },
      { key: "courses",  label: "Courses",  icon: "bi-mortarboard",    href: "courses.jsp" },
      { key: "students", label: "Students", icon: "bi-people",         href: "students.jsp" }
    ]
  };

  function initials(name) {
    return name.split(" ").map(function (w) { return w[0]; }).join("");
  }

  function el(html) {
    var t = document.createElement("template");
    t.innerHTML = html.trim();
    return t.content.firstChild;
  }

  function render() {
    var body = document.body;
    var role = body.dataset.role || "student";
    var page = body.dataset.page || "";
    var base = body.dataset.base || "";          // path to webapp root
    var isStudent = role === "student";
    var items = NAV[role] || NAV.student;

    var navLinks = items.map(function (n) {
      var active = n.key === page ? " active" : "";
      return '<li class="nav-item"><a class="nav-link px-3' + active + '" href="' + n.href + '">' +
             '<i class="bi ' + n.icon + ' me-2"></i>' + n.label + "</a></li>";
    }).join("");

    // student-only profile cluster (streak / energy / level)
    var cluster = "";
    if (isStudent) {
      var energyLabel = profile.premium ? "&#8734;" : (profile.energy + "/" + profile.energyMax);
      var premiumBadge = profile.premium
        ? '<span class="badge rounded-pill d-inline-flex align-items-center gap-1" style="background:#1d4e89;color:#fff;font-weight:600;padding:.4em .7em;"><i class="bi bi-gem"></i>Premium</span>'
        : '<a class="btn btn-sm btn-outline-primary d-inline-flex align-items-center gap-1" href="premium.jsp"><i class="bi bi-gem"></i>Upgrade</a>';
      cluster =
        '<div class="d-none d-md-flex align-items-center gap-3">' +
          premiumBadge +
          '<span class="d-flex align-items-center gap-1 fw-semibold" style="color:#d97706;" title="Day streak"><i class="bi bi-fire"></i>' + profile.streak + "</span>" +
          '<span class="d-flex align-items-center gap-1 fw-semibold" style="color:#1d4e89;" title="Energy"><i class="bi bi-lightning-charge-fill"></i>' + energyLabel + "</span>" +
          '<span class="badge rounded-pill" style="background:#eef3fa;color:#1d4e89;border:1px solid #d6e2f1;font-weight:600;">Lv ' + profile.level + " &middot; " + profile.totalXP + " XP</span>" +
        "</div>";
    }

    var avatarText = isStudent ? initials(profile.name) : "AD";

    // role switch (handy for navigating the static demo)
    var switchTo = isStudent
      ? '<a class="dropdown-item" href="' + base + 'admin/dashboard.jsp"><i class="bi bi-shield-lock me-2"></i>Admin area</a>'
      : '<a class="dropdown-item" href="' + base + 'student/dashboard.do"><i class="bi bi-mortarboard me-2"></i>Student area</a>';

    var nav = el(
      '<nav class="navbar navbar-expand-md sticky-top px-3" style="background:#fff;border-bottom:1px solid #e2e7ef;z-index:1030;">' +
        '<div class="container-fluid shell">' +
          '<a class="navbar-brand brandfont fw-bold d-flex align-items-center gap-2 mb-0" style="color:#1d4e89;font-size:1.4rem;" href="' + base + (isStudent ? "student/dashboard.do" : "admin/dashboard.jsp") + '">' +
            '<span class="brand-mark">&Sigma;</span>Mathify' +
          "</a>" +
          '<button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#appNav"><span class="navbar-toggler-icon"></span></button>' +
          '<div class="collapse navbar-collapse" id="appNav">' +
            '<ul class="nav nav-pills gap-1 mx-auto">' + navLinks + "</ul>" +
            '<div class="d-flex align-items-center gap-3 ms-auto">' +
              cluster +
              '<div class="dropdown">' +
                '<div class="rounded-circle d-flex align-items-center justify-content-center fw-semibold avatar" role="button" data-bs-toggle="dropdown">' + avatarText + "</div>" +
                '<ul class="dropdown-menu dropdown-menu-end">' +
                  '<li><h6 class="dropdown-header">' + (isStudent ? profile.name : "Administrator") + "</h6></li>" +
                  "<li>" + switchTo + "</li>" +
                "</ul>" +
              "</div>" +
            "</div>" +
          "</div>" +
        "</div>" +
      "</nav>"
    );

    body.insertBefore(nav, body.firstChild);
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", render);
  } else {
    render();
  }

  // expose for any page that wants the sample profile
  window.Mathify = { profile: profile };
})();
