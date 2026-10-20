<%@ include file="/WEB-INF/jspf/header.jspf" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Task Flow | Calendar</title>
  <script src="${pageContext.request.contextPath}/js/theme-init.js?v=20260927-polish"></script><link rel="stylesheet" href="${ctx}/css/style.css?v=20260927-polish">
</head>
<body>
<div class="app">
  <%@ include file="/WEB-INF/jspf/sidebar.jspf" %>
  <main class="main">
    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>

    <section class="calendar-month-toolbar" aria-label="Calendar navigation">
      <div class="calendar-month-title"><span class="calendar-month-icon" aria-hidden="true"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><rect x="4" y="5" width="16" height="15" rx="3"/><path d="M8 3v4M16 3v4M4 10h16"/></svg></span><div><span class="eyebrow">YOUR MONTH AT A GLANCE</span><h2><c:out value="${monthLabel}"/></h2></div></div>
      <div class="calendar-month-actions">
        <a class="calendar-today-button" href="${ctx}/calendar?date=${today}">Today</a>
        <a class="calendar-arrow-button" href="${ctx}/calendar?month=${previousMonth}" aria-label="Previous month" title="Previous month"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m15 18-6-6 6-6"/></svg></a>
        <a class="calendar-arrow-button" href="${ctx}/calendar?month=${nextMonth}" aria-label="Next month" title="Next month"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m9 18 6-6-6-6"/></svg></a>
        <a class="btn primary calendar-add-button" href="${ctx}/task-form">+ Add Task</a>
      </div>
    </section>

    <section class="calendar-panel" aria-label="Monthly calendar">
      <div class="calendar-grid">
        <div class="calendar-weekday"><span class="weekday-wide">Monday</span><span class="weekday-narrow">M</span></div>
        <div class="calendar-weekday"><span class="weekday-wide">Tuesday</span><span class="weekday-narrow">T</span></div>
        <div class="calendar-weekday"><span class="weekday-wide">Wednesday</span><span class="weekday-narrow">W</span></div>
        <div class="calendar-weekday"><span class="weekday-wide">Thursday</span><span class="weekday-narrow">T</span></div>
        <div class="calendar-weekday"><span class="weekday-wide">Friday</span><span class="weekday-narrow">F</span></div>
        <div class="calendar-weekday"><span class="weekday-wide">Saturday</span><span class="weekday-narrow">S</span></div>
        <div class="calendar-weekday"><span class="weekday-wide">Sunday</span><span class="weekday-narrow">S</span></div>
        <c:forEach var="day" items="${calendarDays}">
          <div class="calendar-cell ${day.monthValue != viewMonth.monthValue ? 'outside-month' : ''} ${day == today ? 'is-today' : ''} ${day == selectedDate ? 'is-selected' : ''}">
            <div class="calendar-cell-top">
              <a class="calendar-day-number ${day == today ? 'today-number' : ''} ${day == selectedDate ? 'selected-number' : ''}" href="${ctx}/calendar?date=${day}" aria-label="Open ${day}"><c:out value="${day.dayOfMonth}"/></a>
              <c:if test="${day == today}"><span class="calendar-today-label">Today</span></c:if>
            </div>
            <div class="calendar-chip-list">
              <c:forEach var="task" items="${calendarTasks[day]}" varStatus="loop">
                <c:if test="${loop.index < 2}">
                  <a class="calendar-task-chip ${task.priority == 'HIGH' ? 'chip-high' : task.priority == 'MEDIUM' ? 'chip-medium' : 'chip-low'} ${task.status == 'COMPLETED' ? 'chip-completed' : ''}" href="${ctx}/tasks?action=view&amp;id=${task.taskId}" title="Open task: ${fn:escapeXml(task.title)}">
                    <strong><c:out value="${task.title}"/></strong><small><c:out value="${task.priority}"/><c:if test="${task.status == 'COMPLETED'}"> &middot; Done</c:if></small><c:if test="${task.sharedWithMe}"><em class="calendar-shared-label">Shared by <c:out value="${task.ownerName}"/></em></c:if>
                  </a>
                </c:if>
              </c:forEach>
              <c:if test="${fn:length(calendarTasks[day]) > 2}"><a class="calendar-more-link" href="${ctx}/calendar?date=${day}">+<c:out value="${fn:length(calendarTasks[day]) - 2}"/> more</a></c:if>
            </div>
            <span class="calendar-mobile-count"><c:if test="${not empty calendarTasks[day]}"><c:out value="${fn:length(calendarTasks[day])}"/> task<c:if test="${fn:length(calendarTasks[day]) != 1}">s</c:if></c:if></span>
          </div>
        </c:forEach>
      </div>
    </section>

    <section class="calendar-agenda" aria-labelledby="calendarAgendaTitle">
      <div class="calendar-agenda-heading"><div><span class="eyebrow">YOUR SELECTED DAY</span><h2 id="calendarAgendaTitle"><c:out value="${selectedDateLabel}"/></h2></div><span class="calendar-agenda-count"><c:out value="${fn:length(dateTasks)}"/> scheduled</span></div>
      <c:choose>
        <c:when test="${empty dateTasks}"><div class="calendar-empty"><span class="calendar-empty-icon" aria-hidden="true"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><rect x="4" y="5" width="16" height="15" rx="3"/><path d="M8 3v4M16 3v4M4 10h16M9 15h.01M12 15h.01"/></svg></span><strong>No tasks scheduled</strong><p>You have nothing due on this date.</p><a href="${ctx}/task-form">Plan a task <span aria-hidden="true">&rarr;</span></a></div></c:when>
        <c:otherwise>
          <div class="calendar-agenda-list">
            <c:forEach var="task" items="${dateTasks}">
              <a class="calendar-agenda-task ${task.status == 'COMPLETED' ? 'agenda-completed' : ''} ${task.sharedWithMe ? 'calendar-shared-agenda' : ''}" href="${ctx}/tasks?action=view&amp;id=${task.taskId}">
                <span class="calendar-agenda-mark ${task.status == 'COMPLETED' ? 'agenda-mark-done' : ''}" aria-hidden="true"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m5 12 4.5 4.5L19 7"/></svg></span>
                <span class="calendar-agenda-copy"><strong><c:out value="${task.title}"/></strong><small><c:choose><c:when test="${not empty task.dueTime}"><c:out value="${task.dueTime}"/></c:when><c:otherwise>Any time</c:otherwise></c:choose><c:if test="${not empty task.categoryName}"> &middot; <c:out value="${task.categoryName}"/></c:if><c:if test="${task.status == 'COMPLETED'}"> &middot; Completed</c:if><c:if test="${task.sharedWithMe}"> &middot; Shared by <c:out value="${task.ownerName}"/></c:if></small></span>
                <span class="task-priority priority-${task.priority.toLowerCase()}"><c:out value="${task.priority}"/></span>
                <svg class="calendar-agenda-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><path d="m9 18 6-6-6-6"/></svg>
              </a>
            </c:forEach>
          </div>
        </c:otherwise>
      </c:choose>
    </section>
    <footer class="app-footer">&#169; 2026 Task Flow</footer>
  </main>
</div>
<script src="${ctx}/js/app.js?v=20260927-polish"></script>
</body>
</html>
