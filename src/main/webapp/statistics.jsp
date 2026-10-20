<%@ include file="/WEB-INF/jspf/header.jspf" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Task Flow | Statistics</title>
  <script src="${pageContext.request.contextPath}/js/theme-init.js?v=20260927-polish"></script><link rel="stylesheet" href="${ctx}/css/style.css?v=20260927-polish">
</head>
<body>
<div class="app">
  <%@ include file="/WEB-INF/jspf/sidebar.jspf" %>
  <main class="main">
    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>

    <section class="analytics-note" role="note">
      These insights use your private tasks. Shared tasks are excluded because the database does not record which participant completed them. Reopening a task clears its previous completion date, so older reopened-task activity cannot be reconstructed.
    </section>

    <section class="analytics-hero">
      <div><span class="eyebrow">YOUR PRODUCTIVITY</span><h2>Small steps, real progress.</h2><p>Your numbers come directly from your Task Flow workspace.</p></div>
      <div class="analytics-rate"><strong><c:out value="${stats.completionRate}"/>%</strong><span>completion rate</span><div class="analytics-rate-track"><span style="width:${stats.completionRate}%"></span></div></div>
    </section>

    <section class="analytics-metrics" aria-label="Task statistics">
      <article class="analytics-metric metric-mint"><span class="analytics-metric-icon" aria-hidden="true"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><rect x="4" y="4" width="16" height="16" rx="3"/><path d="M8 12h8M8 16h5"/></svg></span><small>Private tasks</small><strong><c:out value="${stats.total}"/></strong></article>
      <article class="analytics-metric metric-sky"><span class="analytics-metric-icon" aria-hidden="true"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="m5 12 4.5 4.5L19 7"/></svg></span><small>Completed</small><strong><c:out value="${stats.completed}"/></strong></article>
      <article class="analytics-metric metric-peach"><span class="analytics-metric-icon" aria-hidden="true"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><circle cx="12" cy="12" r="8"/><path d="M12 8v4l3 2"/></svg></span><small>Pending</small><strong><c:out value="${stats.pending}"/></strong></article>
      <article class="analytics-metric metric-lavender"><span class="analytics-metric-icon" aria-hidden="true"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M12 3v18M5 8h14M5 16h14"/></svg></span><small>Important</small><strong><c:out value="${stats.important}"/></strong></article>
    </section>

    <section class="analytics-small-metrics" aria-label="Schedule overview">
      <article><span>Due today</span><strong><c:out value="${stats.today}"/></strong></article>
      <article><span>Upcoming</span><strong><c:out value="${stats.upcoming}"/></strong></article>
      <article><span>Overdue and pending</span><strong><c:out value="${stats.overdue}"/></strong></article>
      <article><span>Current streak</span><strong><c:out value="${stats.currentStreak}"/> days</strong></article>
      <article><span>Longest streak</span><strong><c:out value="${stats.longestStreak}"/> days</strong></article>
    </section>

    <section class="analytics-panel status-panel">
      <div class="analytics-panel-heading"><div><span class="eyebrow">TASK RHYTHM</span><h2>Where things stand</h2></div></div>
      <div class="status-summary"><span><i class="status-dot status-completed"></i>Completed <b><c:out value="${stats.completed}"/></b></span><span><i class="status-dot status-pending"></i>Pending <b><c:out value="${stats.pending}"/></b></span><span><i class="status-dot status-overdue"></i>Overdue <b><c:out value="${stats.overdue}"/></b></span></div>
      <div class="status-track" role="img" aria-label="${stats.completed} completed and ${stats.pending} pending tasks"><span class="status-fill-completed" style="width:${stats.total == 0 ? 0 : stats.completed * 100 / stats.total}%"></span><span class="status-fill-pending" style="width:${stats.total == 0 ? 0 : stats.pending * 100 / stats.total}%"></span></div>
      <c:if test="${stats.total == 0}"><p class="analytics-empty-inline">Complete your first task to start building your productivity history.</p></c:if>
    </section>

    <div class="analytics-columns">
      <section class="analytics-panel">
        <div class="analytics-panel-heading"><div><span class="eyebrow">LAST SEVEN DAYS</span><h2>Weekly completions</h2></div></div>
        <p class="analytics-panel-caption">Tasks completed each day, based on their saved completion dates.</p>
        <div class="analytics-bars" role="img" aria-label="Weekly task completion chart">
          <c:forEach var="bucket" items="${stats.weekly}"><div class="analytics-bar-column"><strong><c:out value="${bucket.count}"/></strong><div class="analytics-bar-track"><span style="height:${bucket.percent}%"></span></div><small><c:out value="${bucket.label}"/></small></div></c:forEach>
        </div>
      </section>
      <section class="analytics-panel">
        <div class="analytics-panel-heading"><div><span class="eyebrow">YOUR WORKSPACE</span><h2>Tasks by category</h2></div></div>
        <c:choose><c:when test="${empty stats.categories}"><div class="analytics-empty">No category data yet. Your tasks will appear here as you add them.</div></c:when><c:otherwise><div class="analytics-breakdown"><c:forEach var="entry" items="${stats.categories}"><div class="analytics-breakdown-row"><div><span><c:out value="${entry.key}"/></span><b><c:out value="${entry.value}"/></b></div><div class="analytics-breakdown-track"><span style="width:${stats.total == 0 ? 0 : entry.value * 100 / stats.total}%"></span></div></div></c:forEach></div></c:otherwise></c:choose>
      </section>
      <section class="analytics-panel">
        <div class="analytics-panel-heading"><div><span class="eyebrow">EFFORT, YOUR WAY</span><h2>Tasks by priority</h2></div></div>
        <c:choose><c:when test="${empty stats.priorities}"><div class="analytics-empty">Priority details will appear when you create tasks.</div></c:when><c:otherwise><div class="analytics-breakdown"><c:forEach var="entry" items="${stats.priorities}"><div class="analytics-breakdown-row"><div><span><c:out value="${entry.key}"/></span><b><c:out value="${entry.value}"/></b></div><div class="analytics-breakdown-track priority-track"><span class="priority-fill-${entry.key.toLowerCase()}" style="width:${stats.total == 0 ? 0 : entry.value * 100 / stats.total}%"></span></div></div></c:forEach></div></c:otherwise></c:choose>
      </section>
      <section class="analytics-panel monthly-panel">
        <div class="analytics-panel-heading"><div><span class="eyebrow">A WIDER VIEW</span><h2>Monthly completions</h2></div></div>
        <c:choose><c:when test="${not stats.hasMonthlyHistory}"><div class="analytics-empty">Historical productivity data is not available yet. It will appear as you complete tasks.</div></c:when><c:otherwise><div class="analytics-months" role="img" aria-label="Monthly task completion chart"><c:forEach var="bucket" items="${stats.monthly}"><div class="analytics-month-column"><strong><c:out value="${bucket.count}"/></strong><div class="analytics-month-track"><span style="height:${bucket.percent}%"></span></div><small><c:out value="${bucket.label}"/></small></div></c:forEach></div></c:otherwise></c:choose>
      </section>
    </div>
    <footer class="app-footer">&#169; 2026 Task Flow</footer>
  </main>
</div>
<script src="${ctx}/js/app.js?v=20260927-polish"></script>
</body>
</html>
