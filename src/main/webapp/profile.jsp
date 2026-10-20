<%@ include file="/WEB-INF/jspf/header.jspf" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8"><title>Task Flow | Profile</title>
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <script src="${pageContext.request.contextPath}/js/theme-init.js?v=20260927-polish"></script><link rel="stylesheet" href="${ctx}/css/style.css?v=20260927-polish">
</head>
<body><div class="app"><%@ include file="/WEB-INF/jspf/sidebar.jspf" %><main class="main">
<%@ include file="/WEB-INF/jspf/app-header.jspf" %>
<section class="profile-hero">
  <div class="profile-large-avatar"><c:out value="${sessionScope.user.fullName.substring(0,1)}"/></div>
  <div><span class="eyebrow">YOUR PERSONAL SPACE</span><h2><c:out value="${sessionScope.user.fullName}"/></h2><p>Member since <c:out value="${memberSince}"/></p></div>
</section>
<c:if test="${not empty param.saved}"><div class="profile-success" role="status">Your profile has been updated.</div></c:if>
<c:if test="${not empty error}"><div class="auth-foundation-error" role="alert"><c:out value="${error}"/></div></c:if>
<section class="card profile-card">
  <div class="profile-card-heading"><div><h3 class="section-title">Profile details</h3><p>Keep your personal information up to date.</p></div><span class="profile-card-spark" aria-hidden="true"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="m12 3 1.7 5.3L19 10l-5.3 1.7L12 17l-1.7-5.3L5 10l5.3-1.7L12 3Z"/><path d="m19 16 .8 2.2L22 19l-2.2.8L19 22l-.8-2.2L16 19l2.2-.8L19 16Z"/></svg></span></div>
  <form method="post" action="${ctx}/profile" class="profile-form">
    <label class="profile-field"><span>Full name</span><input name="fullName" value="${fn:escapeXml(empty formName ? sessionScope.user.fullName : formName)}" maxlength="120" autocomplete="name" required></label>
    <label class="profile-field"><span>Email address</span><input type="email" name="email" value="${fn:escapeXml(empty formEmail ? sessionScope.user.email : formEmail)}" maxlength="180" autocomplete="email" required></label>
    <label class="profile-field profile-readonly"><span>Username</span><input value="${fn:escapeXml(sessionScope.user.username)}" readonly><small>Your username is used to sign in.</small></label>
    <div class="profile-form-actions"><a class="btn secondary" href="${ctx}/dashboard">Back to dashboard</a><button class="btn primary" type="submit">Save changes</button></div>
  </form>
</section>
<footer class="app-footer">&#169; 2026 Task Flow</footer>
</main></div><script src="${ctx}/js/app.js?v=20260927-polish"></script></body></html>
