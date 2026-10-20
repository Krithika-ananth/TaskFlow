<%@ include file="/WEB-INF/jspf/header.jspf" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Task Flow | Achievements</title>
  <script src="${pageContext.request.contextPath}/js/theme-init.js?v=20260927-polish"></script><link rel="stylesheet" href="${ctx}/css/style.css?v=20260927-polish">
</head>
<body>
<div class="app">
  <%@ include file="/WEB-INF/jspf/sidebar.jspf" %>
  <main class="main">
    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>

    <section class="achievement-welcome">
      <div class="achievement-welcome-icon" aria-hidden="true"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="m12 3 2.7 5.1 5.7.8-4.1 4 .9 5.7-5.2-2.7-5.2 2.7.9-5.7-4.1-4 5.7-.8z"/></svg></div>
      <div><span class="eyebrow">MILESTONES, NOT PRESSURE</span><h2>Every bit of progress counts.</h2><p><c:out value="${stats.completed}"/> private tasks completed &middot; current streak <c:out value="${stats.currentStreak}"/> days</p></div>
    </section>
    <p class="achievement-data-note">Achievements and goals below come from the existing Task Flow achievement definitions. Shared tasks are not counted because the app cannot reliably identify which participant completed them.</p>

    <c:choose>
      <c:when test="${empty achievements}"><section class="analytics-empty achievement-empty"><h2>No achievements yet</h2><p>Complete tasks and build your streak to unlock achievements.</p><a class="btn primary" href="${ctx}/tasks">View your tasks</a></section></c:when>
      <c:otherwise>
        <section class="achievement-grid" aria-label="Achievement progress">
          <c:forEach var="achievement" items="${achievements}">
            <c:set var="displayName" value="${achievement.name}"/>
            <c:choose>
              <c:when test="${achievement.name=='FIRST_TASK'}"><c:set var="displayName" value="First Task"/></c:when>
              <c:when test="${achievement.name=='10_TASKS'}"><c:set var="displayName" value="10 Tasks Completed"/></c:when>
              <c:when test="${achievement.name=='50_TASKS'}"><c:set var="displayName" value="50 Tasks Completed"/></c:when>
              <c:when test="${achievement.name=='100_TASKS'}"><c:set var="displayName" value="100 Tasks Completed"/></c:when>
              <c:when test="${achievement.name=='7_DAY_STREAK'}"><c:set var="displayName" value="7-Day Streak"/></c:when>
              <c:when test="${achievement.name=='30_DAY_STREAK'}"><c:set var="displayName" value="30-Day Streak"/></c:when>
              <c:when test="${achievement.name=='PRODUCTIVITY_WEEK'}"><c:set var="displayName" value="Productivity Week"/></c:when>
            </c:choose>
            <article class="achievement-card ${achievement.unlocked ? 'achievement-is-unlocked' : ''}">
              <span class="achievement-medal" aria-hidden="true"><c:choose><c:when test="${achievement.unlocked}"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m5 12 4.5 4.5L19 7"/></svg></c:when><c:otherwise><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="m12 3 2.7 5.1 5.7.8-4.1 4 .9 5.7-5.2-2.7-5.2 2.7.9-5.7-4.1-4 5.7-.8z"/></svg></c:otherwise></c:choose></span>
              <div class="achievement-card-copy"><span class="achievement-status"><c:out value="${achievement.unlocked ? 'Unlocked' : 'In progress'}"/></span><h2><c:out value="${displayName}"/></h2><p><c:out value="${achievement.description}"/></p><div class="achievement-progress-label"><span>Progress</span><strong><c:out value="${achievement.progress}"/> / <c:out value="${achievement.requiredValue}"/></strong></div><div class="achievement-progress-track"><span style="width:${achievement.progressPercent}%"></span></div></div>
            </article>
          </c:forEach>
        </section>
      </c:otherwise>
    </c:choose>
    <footer class="app-footer">&#169; 2026 Task Flow</footer>
  </main>
</div>
<script src="${ctx}/js/app.js?v=20260927-polish"></script>
</body>
</html>

