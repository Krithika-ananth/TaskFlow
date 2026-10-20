<%@ include file="/WEB-INF/jspf/header.jspf" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Welcome back to Task Flow</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="icon" type="image/svg+xml" href="${ctx}/images/favicon.svg?v=3">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Caveat:wght@500;600;700&family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@600;700;800&display=swap" rel="stylesheet">
  <script src="${pageContext.request.contextPath}/js/theme-init.js?v=20260927-polish"></script><link rel="stylesheet" href="${ctx}/css/style.css?v=20260927-polish">
</head>
<body class="df-login-page">
  <main class="df-login-shell">
    <section class="df-login-visual" aria-label="A peaceful Task Flow workspace overlooking mountains and a lake">
      <img class="df-login-art" src="${ctx}/images/login-workspace.png" alt="A sunlit desk with a laptop, notebook, coffee and plants overlooking a calm lake and mountains">
      <div class="df-login-visual-content">
        <a class="df-brand df-brand-light" href="${ctx}/" aria-label="Task Flow home">
          <span class="df-leaf-logo" aria-hidden="true"><svg viewBox="0 0 40 40"><path class="leaf-shape" d="M31.5 5.5C19 5.8 8.7 12.6 8.7 23c0 5.7 3.7 9.2 9 9.2 9.8 0 15.5-10.3 13.8-26.7Z"/><path class="leaf-vein" d="M8.5 34c4.3-8.1 10.4-14 19-19.5"/></svg></span>
          <span class="df-brand-copy"><b>Task Flow</b><small>Plan less. Do more.</small></span>
        </a>
        <div class="df-login-copy">
          <h1>Small steps<br>create big dreams.</h1>
          <p>Your personal productivity companion.</p>
        </div>
      </div>
    </section>

    <section class="df-login-form-area" aria-label="Login to Task Flow">
      <div class="df-login-card">
        <a class="df-brand df-brand-dark" href="${ctx}/" aria-label="Task Flow home">
          <span class="df-leaf-logo" aria-hidden="true"><svg viewBox="0 0 40 40"><path class="leaf-shape" d="M31.5 5.5C19 5.8 8.7 12.6 8.7 23c0 5.7 3.7 9.2 9 9.2 9.8 0 15.5-10.3 13.8-26.7Z"/><path class="leaf-vein" d="M8.5 34c4.3-8.1 10.4-14 19-19.5"/></svg></span>
          <span class="df-brand-word">Task Flow</span>
        </a>
        <div class="df-login-heading"><h2>Welcome back to Task Flow</h2><p>Login to continue to your account.</p></div>
        <c:if test="${not empty param.registered}"><div class="df-alert success">Your account is ready. You can log in now.</div></c:if>
        <c:if test="${not empty param.logout}"><div class="df-alert success">You have been logged out securely.</div></c:if>
        <c:if test="${not empty param.passwordReset}"><div class="df-alert success" role="status">Your password has been updated. You can log in now.</div></c:if>
        <c:if test="${not empty error}"><div class="df-alert error"><c:out value="${error}"/></div></c:if>
        <form method="post" action="${ctx}/login" class="df-login-form" autocomplete="on">
          <label class="df-field"><span>Username or Email</span><span class="df-input"><svg viewBox="0 0 24 24" aria-hidden="true"><rect x="3" y="5" width="18" height="14" rx="3"/><path d="m4 7 8 6 8-6"/></svg><input type="text" name="usernameOrEmail" value="${fn:escapeXml(rememberedLogin)}" placeholder="Your username or email" autocomplete="username" required></span></label>
          <label class="df-field"><span>Password</span><span class="df-input"><svg viewBox="0 0 24 24" aria-hidden="true"><rect x="5" y="10" width="14" height="10" rx="2"/><path d="M8 10V7a4 4 0 0 1 8 0v3"/></svg><input id="dfPassword" type="password" name="password" placeholder="Enter your password" autocomplete="current-password" required><button type="button" class="df-eye" data-password-toggle="dfPassword" aria-label="Show password"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M2.5 12s3.5-6 9.5-6 9.5 6 9.5 6-3.5 6-9.5 6-9.5-6-9.5-6Z"/><circle cx="12" cy="12" r="2.5"/></svg></button></span></label>
          <div class="df-login-options"><label><input type="checkbox" name="remember" checked><span class="df-check" aria-hidden="true"></span>Remember me</label><a class="df-forgot" href="${ctx}/forgot-password">Forgot password?</a></div>
          <button class="df-submit" type="submit">Login <span aria-hidden="true">&rarr;</span></button>
        </form>
        <div class="df-divider"><span></span><b>OR</b><span></span></div>
        <p class="df-register">Don't have an account? <a href="${ctx}/register">Create account</a></p>
        <footer class="login-copyright">&#169; 2026 Task Flow</footer>
      </div>
    </section>
  </main>
  <script src="${ctx}/js/app.js?v=20260927-polish"></script>
</body>
</html>
