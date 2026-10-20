<%@ include file="/WEB-INF/jspf/header.jspf" %>
<!doctype html><html lang="en"><head><meta charset="UTF-8"><title>Task Flow | Coming Soon</title><meta name="viewport" content="width=device-width,initial-scale=1"><script src="${pageContext.request.contextPath}/js/theme-init.js?v=20260927-polish"></script><link rel="stylesheet" href="${ctx}/css/style.css?v=20260927-polish"></head>
<body><div class="app"><%@ include file="/WEB-INF/jspf/sidebar.jspf" %><main class="main">
<c:set var="pageTitle" value="Coming soon" scope="request"/><c:set var="pageSubtitle" value="A few more good things are on the way." scope="request"/>
<%@ include file="/WEB-INF/jspf/app-header.jspf" %>
<section class="coming-soon-card"><div class="coming-soon-art" aria-hidden="true"><svg viewBox="0 0 96 96" fill="none"><path d="M48 9c17 13 27 27 27 43a27 27 0 1 1-54 0c0-16 10-30 27-43Z" fill="#DDF6EC"/><path d="M48 29v27M37 45h22" stroke="#19A982" stroke-width="5" stroke-linecap="round"/><circle cx="73" cy="25" r="8" fill="#FFE1E8"/><circle cx="22" cy="27" r="5" fill="#EAE2FF"/></svg></div><span class="eyebrow">MADE FOR YOUR FLOW</span><h2><c:out value="${empty param.feature ? 'This feature' : param.feature}"/> is coming soon</h2><p>We&rsquo;re keeping this space ready for a future Task Flow update.</p><a class="btn primary" href="${ctx}/dashboard">Back to Dashboard</a></section>
<footer class="app-footer">&#169; 2026 Task Flow</footer>
</main></div><script src="${ctx}/js/app.js?v=20260927-polish"></script></body></html>
