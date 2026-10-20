<%@ include file="/WEB-INF/jspf/header.jspf" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Task Flow | Task History</title>
  <script src="${pageContext.request.contextPath}/js/theme-init.js?v=20260927-polish"></script><link rel="stylesheet" href="${ctx}/css/style.css?v=20260927-polish">
</head>
<body>
<div class="app">
  <%@ include file="/WEB-INF/jspf/sidebar.jspf" %>
  <main class="main">
    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>
    <section class="history-intro">
      <span class="history-intro-icon" aria-hidden="true"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M4 12a8 8 0 1 0 2.3-5.6L4 8.7"/><path d="M4 4v4.7h4.7M12 7v5l3 2"/></svg></span>
      <div><h2>Small steps add up.</h2><p>Look back at the plans you started and the ones you finished.</p></div>
    </section>

    <div class="history-coverage-note" role="note"><strong>Available activity</strong><span>This database records when current tasks were created and, for tasks still completed, their completion time. It does not retain edits, reopen events, or deleted tasks.</span></div>

    <form class="history-filters" method="get" action="${ctx}/history">
      <label class="history-search"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><circle cx="10.8" cy="10.8" r="6.8"/><path d="m16 16 4.5 4.5"/></svg><input name="q" value="<c:out value='${historySearch}'/>" maxlength="100" placeholder="Search task names" aria-label="Search history by task name"></label>
      <label class="sr-only" for="historyType">Activity type</label>
      <select id="historyType" name="type"><option value="ALL" ${historyType == 'ALL' ? 'selected' : ''}>All available activity</option><option value="CREATED" ${historyType == 'CREATED' ? 'selected' : ''}>Created</option><option value="COMPLETED" ${historyType == 'COMPLETED' ? 'selected' : ''}>Completed</option></select>
      <button class="btn primary" type="submit">Show activity</button>
      <c:if test="${not empty historySearch or historyType != 'ALL'}"><a class="history-clear" href="${ctx}/history">Clear</a></c:if>
    </form>

    <c:choose>
      <c:when test="${empty historyGroups}">
        <div class="history-empty"><span class="history-empty-mark" aria-hidden="true"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7"><path d="M4 12a8 8 0 1 0 2.3-5.6L4 8.7"/><path d="M4 4v4.7h4.7M12 7v5l3 2"/></svg></span><h2>No activity yet</h2><p>Your task activity will appear here.</p><a href="${ctx}/tasks">Go to My Tasks <span aria-hidden="true">&rarr;</span></a></div>
      </c:when>
      <c:otherwise>
        <div class="history-timeline">
          <c:forEach var="group" items="${historyGroups}">
            <section class="history-day-group">
              <h2><c:out value="${group.key}"/></h2>
              <div class="history-events">
                <c:forEach var="event" items="${group.value}">
                  <article class="history-event">
                    <span class="history-event-icon ${event.eventType == 'COMPLETED' ? 'history-event-completed' : 'history-event-created'}" aria-hidden="true"><c:choose><c:when test="${event.eventType == 'COMPLETED'}"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m5 12 4.5 4.5L19 7"/></svg></c:when><c:otherwise><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M12 5v14M5 12h14"/></svg></c:otherwise></c:choose></span>
                    <div class="history-event-copy"><span class="history-event-label"><c:out value="${event.eventType}"/></span><a href="${ctx}/tasks?action=view&amp;id=${event.taskId}"><c:out value="${event.taskTitle}"/></a></div>
                    <time datetime="${event.occurredAt}"><c:out value="${event.timeLabel}"/></time>
                  </article>
                </c:forEach>
              </div>
            </section>
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
    <footer class="app-footer">&#169; 2026 Task Flow</footer>
  </main>
</div>
<script src="${ctx}/js/app.js?v=20260927-polish"></script>
</body>
</html>
