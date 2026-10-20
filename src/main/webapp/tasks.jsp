<%@ include file="/WEB-INF/jspf/header.jspf" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Task Flow | My Tasks</title>
  <script src="${pageContext.request.contextPath}/js/theme-init.js?v=20260927-polish"></script><link rel="stylesheet" href="${ctx}/css/style.css?v=20260927-polish">
</head>
<body>
<div class="app">
  <%@ include file="/WEB-INF/jspf/sidebar.jspf" %>
  <main class="main">
    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>

    <section aria-label="Task actions">
    <nav class="task-collections" aria-label="Task collection">
      <a class="pill ${collection=='MINE'?'active':''}" href="${ctx}/tasks?collection=MINE">My Tasks</a>
      <a class="pill ${collection=='SHARED'?'active':''}" href="${ctx}/tasks?collection=SHARED">Shared With Me</a>
    </nav>
    <form class="tasks-toolbar" method="get" action="${ctx}/tasks" aria-label="Task search and filters">
      <input type="hidden" name="collection" value="${collection}">
      <div class="tasks-search-wrap">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><circle cx="10.8" cy="10.8" r="6.8"/><path d="m16 16 4.5 4.5"/></svg>
        <input type="search" name="q" value="<c:out value='${search}'/>" placeholder="Search title, description, or category" aria-label="Search tasks">
      </div>
      <div class="task-list-controls"><select name="filter" aria-label="Filter tasks"><option value="ALL" <c:if test="${filter=='ALL'}">selected</c:if>>Everything</option><option value="TODAY" <c:if test="${filter=='TODAY'}">selected</c:if>>Today</option><option value="UPCOMING" <c:if test="${filter=='UPCOMING'}">selected</c:if>>Upcoming</option><option value="PENDING" <c:if test="${filter=='PENDING'}">selected</c:if>>Active</option><option value="COMPLETED" <c:if test="${filter=='COMPLETED'}">selected</c:if>>Completed</option><option value="OVERDUE" <c:if test="${filter=='OVERDUE'}">selected</c:if>>Overdue</option><option value="HIGH" <c:if test="${filter=='HIGH'}">selected</c:if>>High priority</option></select><select name="sort" aria-label="Sort tasks"><option value="DUE" <c:if test="${sort=='DUE'}">selected</c:if>>Due date</option><option value="NEWEST" <c:if test="${sort=='NEWEST'}">selected</c:if>>Newest</option><option value="OLDEST" <c:if test="${sort=='OLDEST'}">selected</c:if>>Oldest</option><option value="PRIORITY" <c:if test="${sort=='PRIORITY'}">selected</c:if>>Priority</option><option value="ALPHABETICAL" <c:if test="${sort=='ALPHABETICAL'}">selected</c:if>>Alphabetical</option></select><button class="btn secondary" type="submit">Apply</button></div>
      <a class="btn secondary tasks-categories-button" href="${ctx}/categories">Manage categories</a>
      <a class="btn primary tasks-add-button" href="${ctx}/task-form"><span aria-hidden="true">+</span> Add Task</a>
    </form></section>

    <c:if test="${param.saved == '1'}"><div class="task-notice" role="status">Your task is saved.</div></c:if>
    <c:if test="${param.deleted == '1'}"><div class="task-notice" role="status">Task deleted.</div></c:if>
    <c:if test="${param.completed == '1'}"><div class="task-notice" role="status">Task completed. Nice work.</div></c:if>
    <c:if test="${param.reopened == '1'}"><div class="task-notice" role="status">Task moved back to your active plans.</div></c:if>

    <section class="tasks-list" aria-label="Your tasks">
      <c:choose>
        <c:when test="${empty tasks}">
          <div class="tasks-empty">
            <div class="tasks-empty-art" aria-hidden="true"><svg viewBox="0 0 64 64" fill="none"><rect x="12" y="10" width="40" height="46" rx="12" fill="#DDF6EC"/><path d="M23 25h18M23 33h18M23 41h10" stroke="#168A70" stroke-width="3" stroke-linecap="round"/><circle cx="47" cy="45" r="10" fill="#FFF1B8"/><path d="M47 40v10M42 45h10" stroke="#926A13" stroke-width="2.5" stroke-linecap="round"/></svg></div>
            <span class="eyebrow">A FRESH START</span>
            <h2>No tasks yet</h2>
            <p>Start with one small thing.</p>
            <a class="btn primary" href="${ctx}/task-form">+ Create your first task</a>
          </div>
        </c:when>
        <c:otherwise>
          <c:forEach var="task" items="${tasks}">
            <article class="task-card ${task.status == 'COMPLETED' ? 'task-card-completed' : ''}">
              <form class="task-completion-form" method="post" action="${ctx}/tasks">
                <input type="hidden" name="action" value="${task.status == 'COMPLETED' ? 'reopen' : 'complete'}">
                <input type="hidden" name="id" value="${task.taskId}">
                <button class="task-completion-button ${task.status == 'COMPLETED' ? 'is-complete' : ''}" type="submit" aria-label="${task.status == 'COMPLETED' ? 'Mark as incomplete' : 'Mark as complete'}" title="${task.status == 'COMPLETED' ? 'Mark as incomplete' : 'Mark as complete'}">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" aria-hidden="true"><path d="m5 12 4.5 4.5L19 7"/></svg>
                </button>
              </form>
              <a class="task-card-main" href="${ctx}/tasks?action=view&amp;id=${task.taskId}">
                <span class="task-card-mark" aria-hidden="true"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><rect x="4" y="4" width="16" height="16" rx="5"/><path d="M8 12h8M8 16h5"/></svg></span>
                <span class="task-card-copy">
                  <strong><c:if test="${task.important}"><span class="important-star" aria-label="Important">&#9733;</span> </c:if><c:out value="${task.title}"/></strong>
                  <small><c:choose><c:when test="${task.sharedWithMe}">Owner: <c:out value="${task.ownerName}"/> &middot; </c:when><c:otherwise></c:otherwise></c:choose><c:choose><c:when test="${not empty task.description}"><c:out value="${task.description}"/></c:when><c:otherwise>No description</c:otherwise></c:choose></small>
                </span>
              </a>
              <div class="task-card-meta">
                <span class="task-priority priority-${task.priority.toLowerCase()}"><c:out value="${task.priority}"/></span>
                <span class="task-category-name"><c:choose><c:when test="${not empty task.categoryName}"><c:out value="${task.categoryName}"/></c:when><c:otherwise>No category</c:otherwise></c:choose></span>
                <span class="task-due"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><rect x="4" y="5" width="16" height="15" rx="3"/><path d="M8 3v4M16 3v4M4 10h16"/></svg><c:choose><c:when test="${not empty task.dueDate}"><c:out value="${task.dueDate}"/></c:when><c:otherwise>No due date</c:otherwise></c:choose></span>
              </div>
              <div class="task-card-tools">
                <a class="task-card-open" href="${ctx}/tasks?action=view&amp;id=${task.taskId}" aria-label="View task details" title="View task"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m9 18 6-6-6-6"/></svg></a>
                <c:if test="${not task.sharedWithMe}"><a class="task-card-share" href="${ctx}/task-form?id=${task.taskId}#shareTaskSection" aria-label="Share task" title="Share task"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><circle cx="18" cy="5" r="3"/><circle cx="6" cy="12" r="3"/><circle cx="18" cy="19" r="3"/><path d="m8.7 10.6 6.5-4.2M8.7 13.4l6.5 4.2"/></svg></a><a class="task-card-edit" href="${ctx}/task-form?id=${task.taskId}" aria-label="Edit task" title="Edit task"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="m14 5 5 5M4 20l4.5-1 10-10a2.1 2.1 0 0 0-3-3l-10 10L4 20Z"/></svg></a>
                <form method="post" action="${ctx}/tasks" class="task-card-delete-form"><input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${task.taskId}"><button class="task-card-delete" type="submit" data-confirm="Delete this task?&#10;This action cannot be undone." aria-label="Delete task" title="Delete task"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M5 7h14M10 11v6M14 11v6M7 7l1 13h8l1-13M9 7V4h6v3"/></svg></button></form>
                </c:if>
              </div>
            </article>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </section>

    <footer class="app-footer">&#169; 2026 Task Flow</footer>
  </main>
</div>
<script src="${ctx}/js/app.js?v=20260927-polish"></script>
</body>
</html>
