<%@ include file="/WEB-INF/jspf/header.jspf" %>
<!doctype html><html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"><title>Choose a new password | Task Flow</title><link rel="stylesheet" href="${ctx}/css/style.css"></head>
<body class="auth-foundation-page"><main class="auth-foundation-shell"><section class="auth-foundation-form-area"><div class="auth-foundation-card">
  <a class="auth-foundation-mobile-brand" href="${ctx}/">Task Flow</a><h2>Choose a new password</h2><p class="auth-foundation-intro">Use at least 8 characters for your new password.</p>
  <c:if test="${not empty error}"><div class="auth-foundation-error" role="alert"><c:out value="${error}"/></div></c:if>
  <c:if test="${not empty token}"><form method="post" action="${ctx}/reset-password" class="auth-foundation-form"><input type="hidden" name="token" value="<c:out value='${token}'/>"/><label class="auth-foundation-field"><span>New password</span><input type="password" name="password" autocomplete="new-password" minlength="8" maxlength="128" required></label><label class="auth-foundation-field"><span>Confirm new password</span><input type="password" name="confirmPassword" autocomplete="new-password" minlength="8" maxlength="128" required></label><button class="auth-foundation-submit" type="submit">Update password <span aria-hidden="true">&rarr;</span></button></form></c:if>
  <p class="auth-foundation-switch"><a href="${ctx}/forgot-password">Request another reset link</a></p>
</div></section></main></body></html>
