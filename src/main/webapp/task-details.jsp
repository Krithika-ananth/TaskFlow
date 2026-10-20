<%@ include file="/WEB-INF/jspf/header.jspf" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Task Flow | Task Details</title>
  <script src="${pageContext.request.contextPath}/js/theme-init.js?v=20260927-polish"></script><link rel="stylesheet" href="${ctx}/css/style.css?v=20260927-polish">
</head>
<body>
<div class="app">
  <%@ include file="/WEB-INF/jspf/sidebar.jspf" %>
  <main class="main">
    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>
    <c:if test="${param.saved == '1'}"><div class="task-notice" role="status">Your changes are saved.</div></c:if>
    <article class="task-detail-card">
      <div class="task-detail-topline">
        <span class="task-priority priority-${task.priority.toLowerCase()}"><c:out value="${task.priority}"/> priority</span>
        <a class="task-back-link" href="${ctx}/tasks${task.sharedWithMe ? '?collection=SHARED' : ''}">All tasks <span aria-hidden="true">&rarr;</span></a>
      </div>
      <c:if test="${task.sharedWithMe}"><p class="shared-owner">Shared by <strong><c:out value="${task.ownerName}"/></strong></p></c:if>
      <section class="task-owner-summary"><h3>Owner</h3><p><c:choose><c:when test="${task.sharedWithMe}"><c:out value="${task.ownerName}"/></c:when><c:otherwise>You</c:otherwise></c:choose></p></section>
      <h2><c:out value="${task.title}"/></h2><c:if test="${task.important}"><p class="important-label">&#9733; Important task</p></c:if>
      <p class="task-detail-description"><c:choose><c:when test="${not empty task.description}"><c:out value="${task.description}"/></c:when><c:otherwise>No description added.</c:otherwise></c:choose></p>
      <div class="task-detail-facts">
        <div class="task-detail-fact"><span class="task-fact-icon task-fact-date"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><rect x="4" y="5" width="16" height="15" rx="3"/><path d="M8 3v4M16 3v4M4 10h16"/></svg></span><span><small>Due date</small><strong><c:choose><c:when test="${not empty task.dueDate}"><c:out value="${task.dueDate}"/></c:when><c:otherwise>No due date</c:otherwise></c:choose></strong></span></div>
        <div class="task-detail-fact"><span class="task-fact-icon task-fact-created"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><circle cx="12" cy="12" r="8"/><path d="M12 7v5l3 2"/></svg></span><span><small>Created</small><strong><c:out value="${task.createdAt}"/></strong></span></div>
        <div class="task-detail-fact"><span class="task-fact-icon task-fact-category"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M4 5h7l2 2h7v12H4z"/></svg></span><span><small>Category</small><strong><c:choose><c:when test="${not empty task.categoryName}"><c:out value="${task.categoryName}"/></c:when><c:otherwise>No category</c:otherwise></c:choose></strong></span></div>
        <div class="task-detail-fact"><span class="task-fact-icon task-fact-status"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="m5 12 4 4L19 6"/></svg></span><span><small>Status</small><strong><c:out value="${task.status}"/></strong></span></div>
      </div>
      <section class="task-participants"><h3>Shared with</h3><c:choose><c:when test="${empty participants}"><p class="task-share-empty">This task is private to you.</p></c:when><c:otherwise><div class="participant-list"><c:forEach var="person" items="${participants}"><span class="pill"><c:choose><c:when test="${task.sharedWithMe and person.userId == sessionScope.user.userId}">You</c:when><c:otherwise><c:out value="${person.fullName}"/></c:otherwise></c:choose></span></c:forEach></div></c:otherwise></c:choose></section>
      <div class="task-detail-actions">
        <form method="post" action="${ctx}/tasks">
          <input type="hidden" name="action" value="${task.status == 'COMPLETED' ? 'reopen' : 'complete'}"><input type="hidden" name="id" value="${task.taskId}">
          <button class="btn ${task.status == 'COMPLETED' ? 'secondary' : 'primary'}" type="submit">${task.status == 'COMPLETED' ? 'Mark as incomplete' : 'Mark as complete'}</button>
        </form>
        <c:if test="${not task.sharedWithMe}"><a class="btn secondary" href="${ctx}/task-form?id=${task.taskId}#shareTaskSection">Share task</a><a class="btn secondary" href="${ctx}/task-form?id=${task.taskId}"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><path d="m14 5 5 5M4 20l4.5-1 10-10a2.1 2.1 0 0 0-3-3l-10 10L4 20Z"/></svg>Edit task</a>
        <form method="post" action="${ctx}/tasks" data-delete-task>
          <input type="hidden" name="action" value="delete">
          <input type="hidden" name="id" value="${task.taskId}">
          <button class="btn danger" type="submit" data-confirm="Delete this task?&#10;This action cannot be undone."><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><path d="M4 7h16M10 11v6M14 11v6M6 7l1 13h10l1-13M9 7V4h6v3"/></svg>Delete</button>
        </form>
        </c:if>
      </div>
    </article>
    <footer class="app-footer">&#169; 2026 Task Flow</footer>
  </main>
</div>
<script src="${ctx}/js/app.js?v=20260927-polish"></script>
</body>
</html>
