<%@ include file="/WEB-INF/jspf/header.jspf" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Create your Task Flow account</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="icon" type="image/svg+xml" href="${ctx}/images/favicon.svg?v=3">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@600;700;800&display=swap" rel="stylesheet">
  <script src="${pageContext.request.contextPath}/js/theme-init.js?v=20260927-polish"></script><link rel="stylesheet" href="${ctx}/css/style.css?v=20260927-polish">
</head>
<body class="auth-foundation-page">
  <main class="auth-foundation-shell">
    <section class="auth-foundation-story">
      <a class="auth-foundation-brand" href="${ctx}/" aria-label="Task Flow home">
        <span class="auth-foundation-leaf" aria-hidden="true"><svg viewBox="0 0 40 40"><path class="leaf-shape" d="M31.5 5.5C19 5.8 8.7 12.6 8.7 23c0 5.7 3.7 9.2 9 9.2 9.8 0 15.5-10.3 13.8-26.7Z"/><path class="leaf-vein" d="M8.5 34c4.3-8.1 10.4-14 19-19.5"/></svg></span>
        <span>Task Flow<small>Plan less. Do more.</small></span>
      </a>
      <div class="auth-foundation-copy">
        <span class="auth-foundation-kicker">A little progress goes a long way</span>
        <h1>Make room for<br><span>what matters.</span></h1>
        <p>Create a space for your plans, find your focus, and celebrate each small win.</p>
        <div class="auth-foundation-note" aria-hidden="true"><span>Today</span><b>One step at a time</b><i>&#10003;</i></div>
      </div>
      <div class="auth-foundation-orb auth-orb-one" aria-hidden="true"></div>
      <div class="auth-foundation-orb auth-orb-two" aria-hidden="true"></div>
    </section>

    <section class="auth-foundation-form-area">
      <div class="auth-foundation-card">
        <a class="auth-foundation-mobile-brand" href="${ctx}/">Task Flow</a>
        <h2>Create your Task Flow account</h2>
        <p class="auth-foundation-intro">Start with a few details. Your next step is waiting.</p>
        <c:if test="${not empty error}"><div class="auth-foundation-error" role="alert"><c:out value="${error}"/></div></c:if>
        <form method="post" action="${ctx}/register" class="auth-foundation-form" autocomplete="on">
          <label class="auth-foundation-field"><span>Full name</span><input name="fullName" value="${fn:escapeXml(fullName)}" placeholder="Your name" autocomplete="name" maxlength="120" required></label>
          <label class="auth-foundation-field"><span>Username</span><input name="username" value="${fn:escapeXml(username)}" placeholder="Choose a username" autocomplete="username" minlength="3" maxlength="30" pattern="[A-Za-z0-9._-]{3,30}" title="Use 3&ndash;30 letters, numbers, dots, underscores or hyphens" required><small>3&ndash;30 letters, numbers, dots, underscores or hyphens</small></label>
          <label class="auth-foundation-field"><span>Email</span><input type="email" name="email" value="${fn:escapeXml(email)}" placeholder="you@example.com" autocomplete="email" maxlength="180" required></label>
          <label class="auth-foundation-field"><span>Password</span><span class="auth-foundation-password"><input id="registerPassword" type="password" name="password" placeholder="At least 8 characters" autocomplete="new-password" minlength="8" maxlength="128" required><button type="button" data-password-toggle="registerPassword" data-toggle-text aria-label="Show password">Show</button></span></label>
          <label class="auth-foundation-field"><span>Confirm password</span><input type="password" name="confirmPassword" placeholder="Enter your password again" autocomplete="new-password" minlength="8" maxlength="128" required></label>
          <button class="auth-foundation-submit" type="submit">Create account <span aria-hidden="true">&rarr;</span></button>
        </form>
        <p class="auth-foundation-switch">Already have an account? <a href="${ctx}/login">Back to login</a></p>
        <footer class="auth-foundation-footer">&#169; 2026 Task Flow</footer>
      </div>
    </section>
  </main>
  <script src="${ctx}/js/app.js?v=20260927-polish"></script>
</body>
</html>
