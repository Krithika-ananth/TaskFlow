<%@ include file="/WEB-INF/jspf/header.jspf" %>
<!doctype html><html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Forgot password | Task Flow</title><link rel="stylesheet" href="${ctx}/css/style.css"></head>
<body class="auth-foundation-page"><main class="auth-foundation-shell"><section class="auth-foundation-form-area"><div class="auth-foundation-card">
  <a class="auth-foundation-mobile-brand" href="${ctx}/">Task Flow</a><h2>Reset your password</h2><p class="auth-foundation-intro">Enter the email address on your account and we’ll send you a reset link.</p>
  <c:if test="${not empty error}"><div class="auth-foundation-error" role="alert"><c:out value="${error}"/></div></c:if>
  <c:if test="${not empty message}"><div class="df-alert success" role="status"><c:out value="${message}"/></div></c:if>
  <form method="post" action="${ctx}/forgot-password" class="auth-foundation-form"><label class="auth-foundation-field"><span>Email address</span><input type="email" name="email" autocomplete="email" maxlength="180" required></label><button class="auth-foundation-submit" type="submit">Send reset link <span aria-hidden="true">&rarr;</span></button></form>
  <p class="auth-foundation-switch"><a href="${ctx}/login">Back to login</a></p>
</div></section></main></body></html>
