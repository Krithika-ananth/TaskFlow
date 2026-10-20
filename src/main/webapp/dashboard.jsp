<%@ include file="/WEB-INF/jspf/header.jspf" %>
<!doctype html><html lang="en"><head><meta charset="UTF-8"><title>Task Flow | Dashboard</title><meta name="viewport" content="width=device-width,initial-scale=1"><script src="${pageContext.request.contextPath}/js/theme-init.js?v=20260927-polish"></script><link rel="stylesheet" href="${ctx}/css/style.css?v=20260927-polish"></head><body>
<div class="app"><%@ include file="/WEB-INF/jspf/sidebar.jspf" %><main class="main">
<%@ include file="/WEB-INF/jspf/app-header.jspf" %>

<section class="welcome-panel">
  <div class="welcome-copy">
    <span class="eyebrow">TASK FLOW</span>
    <h2>Good morning, <c:out value="${sessionScope.user.fullName}"/>!</h2>
    <p>Small steps become momentum. <span class="greeting-date" id="greetingDate"></span></p>
  </div>
  <div class="welcome-orbit" aria-hidden="true"><span></span><span></span><span></span></div>
</section>

<div class="grid stats-grid">
  <div class="card stat"><div><small>Today's Tasks</small><h2>${todayCount}</h2></div><div class="stat-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><rect x="4" y="4" width="16" height="16" rx="3"/><path d="M8 9h8M8 13h8M8 17h5"/></svg></div></div>
  <div class="card stat"><div><small>Completed</small><h2>${completed}</h2></div><div class="stat-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m6 12 4 4 8-9"/></svg></div></div>
  <div class="card stat"><div><small>Upcoming</small><h2>${upcomingCount}</h2></div><div class="stat-icon" style="background:var(--peach-soft);color:var(--peach)"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><circle cx="12" cy="12" r="8"/><path d="M12 8v5l3 2"/></svg></div></div>
  <div class="card stat"><div><small>Current Streak</small><h2>${streak}</h2></div><div class="stat-icon" style="background:var(--purple-soft);color:var(--purple)"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M12 20c4 0 7-2.8 7-7 0-2.6-1.5-5-4.3-7.2.2 2.2-.5 3.7-2.1 4.5.1-3.7-1.5-6.2-4.4-8.3.2 3.5-2.2 5.4-2.2 8.2 0 4.1 2.7 9.8 6 9.8Z"/></svg></div></div>
</div>

<section class="quick-actions-card" aria-labelledby="quickActionsTitle">
  <div class="quick-actions-heading"><div><span class="eyebrow">MAKE A LITTLE PROGRESS</span><h3 id="quickActionsTitle">Quick Actions</h3></div><p>Choose what feels right for today.</p></div>
  <div class="quick-actions-grid">
    <a class="quick-action quick-action-mint" href="${ctx}/task-form"><span class="quick-action-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9"><path d="M12 5v14M5 12h14"/></svg></span><span><b>Add Task</b><small>Make a plan</small></span><span class="quick-action-arrow">&rarr;</span></a>
    <a class="quick-action quick-action-blue" href="${ctx}/tasks"><span class="quick-action-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9"><rect x="4" y="4" width="16" height="16" rx="3"/><path d="m8 12 2.2 2.2L16 8.5"/></svg></span><span><b>View Tasks</b><small>See your list</small></span><span class="quick-action-arrow">&rarr;</span></a>
    <a class="quick-action quick-action-peach" href="${ctx}/calendar"><span class="quick-action-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9"><rect x="4" y="5" width="16" height="15" rx="2"/><path d="M8 3v4M16 3v4M4 10h16"/></svg></span><span><b>Open Calendar</b><small>Check your days</small></span><span class="quick-action-arrow">&rarr;</span></a>
    <a class="quick-action quick-action-lavender" href="${ctx}/pomodoro"><span class="quick-action-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9"><circle cx="12" cy="13" r="7"/><path d="M12 9v4l2.5 2M9 3h6"/></svg></span><span><b>Start Focus</b><small>Take one step</small></span><span class="quick-action-arrow">&rarr;</span></a>
  </div>
</section>

<div class="grid layout-2" style="margin-top:18px">
<section class="card">
  <div class="table-head"><div><h3 class="section-title" style="margin-bottom:3px">Today's Tasks</h3><small style="color:var(--muted);font-size:10px">Stay focused on what matters next.</small></div><a href="${ctx}/tasks" style="color:var(--primary);font-size:11px;font-weight:800">View all</a></div>
  <c:choose><c:when test="${empty todayTasks}">
    <div class="empty"><div class="empty-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="m6 12 4 4 8-9"/></svg></div><b>You're all caught up</b><p>No tasks scheduled for today.</p><a class="btn primary" href="${ctx}/task-form">Add a task</a></div>
  </c:when><c:otherwise><c:forEach var="t" items="${todayTasks}">
    <div class="task-row ${t.status=='COMPLETED'?'task-completed':''}">
      <form method="post" action="${ctx}/tasks"><input type="hidden" name="action" value="complete"><input type="hidden" name="id" value="${t.taskId}"><button class="check ${t.status=='COMPLETED'?'done':''}" aria-label="Complete task">${t.status=='COMPLETED'?'&#10003;':''}</button></form>
      <div class="task-info"><a href="${ctx}/tasks?action=view&id=${t.taskId}"><strong><c:out value="${t.title}"/></strong></a><small><c:out value="${t.categoryName}"/> &middot; ${empty t.dueTime?'No time':t.dueTime}</small></div>
      <span class="badge ${t.priority=='HIGH'?'high':t.priority=='MEDIUM'?'medium':'low'}">${t.priority}</span>
    </div>
  </c:forEach></c:otherwise></c:choose>
</section>

<section class="card">
  <div class="table-head"><div><h3 class="section-title" style="margin-bottom:3px">Weekly Progress</h3><small style="color:var(--muted);font-size:10px">Your completion rhythm</small></div><b style="color:var(--primary);font-size:12px">${completed}/${total}</b></div>
  <div class="progress"><span style="width:${total==0?0:(completed*100/total)}%"></span></div>
  <p style="font-size:11px;color:var(--muted);margin:9px 0 0">${total==0?0:(completed*100/total)}% of your tasks completed</p>
  <div class="bars"><c:forEach begin="1" end="7" var="i"><div class="bar" style="height:${25+i*9}%"></div></c:forEach></div>
  <div style="display:flex;justify-content:space-between;color:var(--muted);font-size:9px"><span>Mon</span><span>Tue</span><span>Wed</span><span>Thu</span><span>Fri</span><span>Sat</span><span>Sun</span></div>
</section>
</div>

<div class="grid layout-3" style="margin-top:18px">
<section class="card"><div class="table-head"><h3 class="section-title">Task Distribution</h3><span class="badge blue">By category</span></div><div class="donut"></div>
<c:forEach var="e" items="${categories}"><div style="display:flex;justify-content:space-between;font-size:10px;margin:8px 0"><span><c:out value="${e.key}"/></span><b>${e.value}</b></div></c:forEach></section>
<section class="card"><div class="table-head"><h3 class="section-title">Upcoming</h3><a href="${ctx}/calendar" style="color:var(--primary);font-size:10px;font-weight:800">Calendar</a></div>
<c:choose><c:when test="${empty upcoming}"><div class="empty"><div class="empty-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><rect x="4" y="5" width="16" height="15" rx="2"/><path d="M8 3v4M16 3v4M4 10h16"/></svg></div><b>No upcoming deadlines</b><p>Your schedule is clear.</p></div></c:when><c:otherwise><c:forEach var="t" items="${upcoming}"><div class="task-row"><div class="task-info"><strong><c:out value="${t.title}"/></strong><small>${t.dueDate} &middot; ${t.dueTime}</small></div><span class="badge ${t.priority=='HIGH'?'high':t.priority=='MEDIUM'?'medium':'low'}">${t.priority}</span></div></c:forEach></c:otherwise></c:choose></section>
<section class="card"><div class="table-head"><h3 class="section-title">Overdue</h3><span class="badge high">Attention</span></div>
<c:choose><c:when test="${empty overdueTasks}"><div class="empty"><div class="empty-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="m6 12 4 4 8-9"/></svg></div><b>You're all caught up</b><p>No overdue tasks right now.</p></div></c:when><c:otherwise><c:forEach var="t" items="${overdueTasks}"><div class="task-row"><div class="task-info"><strong><c:out value="${t.title}"/></strong><small>Due ${t.dueDate}</small></div><span class="badge high">OVERDUE</span></div></c:forEach></c:otherwise></c:choose></section>
</div>

<div class="privacy" style="margin-top:18px"><strong>Private by design.</strong> Only your account can access your tasks, statistics and achievements.</div>
<footer class="app-footer">&#169; 2026 Task Flow</footer></main></div><script src="${ctx}/js/app.js?v=20260927-polish"></script></body></html>
