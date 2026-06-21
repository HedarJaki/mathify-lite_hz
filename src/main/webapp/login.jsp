<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Sign In · Mathify</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link href="https://fonts.googleapis.com/css2?family=Source+Sans+3:wght@400;500;600;700&family=Source+Serif+4:opsz,wght@8..60,500;8..60,600;8..60,700&display=swap" rel="stylesheet">
<link href="assets/css/app.css" rel="stylesheet">
<style>
  body { display: flex; min-height: 100vh; margin: 0; }
</style>
</head>
<body>

<!-- ============ BRAND PANEL ============ -->
<div class="auth-brand-panel d-none d-lg-flex flex-column justify-content-between">
  <span class="glyph-bg" style="font-size:300px;top:-40px;right:-30px;">&Sigma;</span>
  <span class="glyph-bg" style="font-size:150px;bottom:40px;left:-10px;">&pi;</span>
  <span class="glyph-bg" style="font-size:120px;top:46%;right:18%;">&radic;</span>
  <span class="glyph-bg" style="font-size:90px;bottom:34%;right:8%;">&infin;</span>

  <div class="d-flex align-items-center gap-2 brandfont fw-bold" style="font-size:1.5rem;position:relative;">
    <span style="display:inline-flex;width:40px;height:40px;border-radius:10px;background:#fff;color:#1d4e89;align-items:center;justify-content:center;font-size:1.35rem;">&Sigma;</span>
    Mathify
  </div>

  <div style="position:relative;max-width:420px;">
    <h1 class="mb-3" style="font-size:2.6rem;line-height:1.15;">Welcome back to your math journey.</h1>
    <p style="color:rgba(255,255,255,.82);font-size:1.08rem;line-height:1.6;">Master algebra, geometry and calculus through bite-sized lessons, quizzes and streaks that keep you coming back.</p>
    <div class="d-flex gap-4 mt-4">
      <div>
        <div class="brandfont fw-bold" style="font-size:1.6rem;">40+</div>
        <div style="color:rgba(255,255,255,.7);font-size:.85rem;">Courses</div>
      </div>
      <div>
        <div class="brandfont fw-bold" style="font-size:1.6rem;">180k</div>
        <div style="color:rgba(255,255,255,.7);font-size:.85rem;">Learners</div>
      </div>
      <div>
        <div class="brandfont fw-bold" style="font-size:1.6rem;">4.8&#9733;</div>
        <div style="color:rgba(255,255,255,.7);font-size:.85rem;">Avg rating</div>
      </div>
    </div>
  </div>

  <div style="position:relative;color:rgba(255,255,255,.6);font-size:.85rem;">&copy; 2026 Mathify &middot; Learning, multiplied.</div>
</div>

<!-- ============ FORM PANEL ============ -->
<div class="flex-grow-1 d-flex align-items-center justify-content-center" style="padding:40px 20px;background:#eef1f6;">
  <div style="width:100%;max-width:420px;">

    <!-- mobile brand -->
    <div class="d-flex d-lg-none align-items-center gap-2 brandfont fw-bold mb-4" style="font-size:1.4rem;color:#1d4e89;">
      <span style="display:inline-flex;width:36px;height:36px;border-radius:9px;background:#1d4e89;color:#fff;align-items:center;justify-content:center;font-size:1.2rem;">&Sigma;</span>
      Mathify
    </div>

    <div class="mb-4">
      <h2 class="mb-1" style="font-size:1.9rem;">Sign in</h2>
      <p class="text-secondary mb-0">Pick up right where you left off.</p>
    </div>

    <div id="alertBanner" class="alert d-none mb-3" role="alert" style="font-size:.9rem;"></div>

    <form action="/login" method="post" novalidate>

      <div class="mb-3">
        <label class="form-label small fw-semibold" for="email">Email address</label>
        <div class="position-relative">
          <i class="bi bi-envelope position-absolute text-secondary" style="left:14px;top:50%;transform:translateY(-50%);pointer-events:none;"></i>
          <input id="email" name="email" class="auth-input form-control" type="email" style="padding-left:38px;" placeholder="you@school.edu" autocomplete="email">
        </div>
      </div>

      <div class="mb-3">
        <div class="d-flex justify-content-between align-items-center">
          <label class="form-label small fw-semibold mb-0" for="password">Password</label>
          <a href="#" class="small" style="color:#1d4e89;">Forgot?</a>
        </div>
        <div class="position-relative mt-1">
          <i class="bi bi-lock position-absolute text-secondary" style="left:14px;top:50%;transform:translateY(-50%);pointer-events:none;"></i>
          <input id="password" name="password" class="auth-input form-control" type="password" style="padding-left:38px;padding-right:42px;" placeholder="Enter your password" autocomplete="current-password">
          <i id="pwToggle" class="bi bi-eye position-absolute text-secondary" style="right:14px;top:50%;transform:translateY(-50%);cursor:pointer;" role="button" aria-label="Toggle password visibility"></i>
        </div>
      </div>

      <div class="form-check mb-3">
        <input class="form-check-input" type="checkbox" id="remember" name="remember" checked>
        <label class="form-check-label small text-secondary" for="remember">Keep me signed in</label>
      </div>

      <button type="submit" class="btn btn-auth w-100 fw-semibold" style="padding:.65rem;">Sign in</button>

    </form>

    <p class="text-center text-secondary small mt-4 mb-0">
      Don't have an account? <a href="register.jsp" style="color:#1d4e89;font-weight:600;">Sign up free</a>
    </p>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  (function () {
    var btn = document.getElementById('pwToggle');
    var input = document.getElementById('password');
    btn.addEventListener('click', function () {
      var show = input.type === 'password';
      input.type = show ? 'text' : 'password';
      btn.className = (show ? 'bi bi-eye-slash' : 'bi bi-eye') +
        ' position-absolute text-secondary';
    });

    var messages = {
      invalid_credentials: 'Incorrect email or password. Please try again.',
      missing_fields:      'Please enter your email and password.',
      server_error:        'A server error occurred. Please try again later.'
    };
    var successMessages = {
      registered: 'Account created! You can now sign in.'
    };
    var params = new URLSearchParams(window.location.search);
    var banner = document.getElementById('alertBanner');
    var error = params.get('error');
    var registered = params.get('registered');
    if (error && messages[error]) {
      banner.textContent = messages[error];
      banner.classList.remove('d-none', 'alert-success');
      banner.classList.add('alert-danger');
    } else if (registered) {
      banner.textContent = successMessages.registered;
      banner.classList.remove('d-none', 'alert-danger');
      banner.classList.add('alert-success');
    }
  })();
</script>
</body>
</html>
