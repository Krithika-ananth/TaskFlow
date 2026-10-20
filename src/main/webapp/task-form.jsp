<%@ include file="/WEB-INF/jspf/header.jspf" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Task Flow | ${task.taskId > 0 ? 'Edit Task' : 'Add Task'}</title>
  <script src="${pageContext.request.contextPath}/js/theme-init.js?v=20260927-polish"></script><link rel="stylesheet" href="${ctx}/css/style.css?v=20260927-polish">
</head>
<body>
<div class="app">
  <%@ include file="/WEB-INF/jspf/sidebar.jspf" %>
  <main class="main">
    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>
    <section class="task-form-card">
      <div class="task-form-intro">
        <span class="eyebrow">ONE THING AT A TIME</span>
        <h2>${task.taskId > 0 ? 'Make a few changes' : 'What would you like to do?'}</h2>
        <p>Give your next step a name. You can add details whenever you need them.</p>
      </div>
      <c:if test="${not empty error}"><div class="task-form-error" role="alert"><c:out value="${error}"/></div></c:if>
      <form method="post" action="${ctx}/task-form" class="task-form">
        <c:if test="${task.taskId > 0}"><input type="hidden" name="id" value="${task.taskId}"></c:if>
        <div class="field task-field-full">
          <label for="taskTitle">Task title</label>
          <input id="taskTitle" name="title" type="text" value="<c:out value='${task.title}'/>" maxlength="180" placeholder="e.g. Finish the project outline" required autofocus>
        </div>
        <div class="field task-field-full">
          <label for="taskDescription">Description <span class="task-optional">Optional</span></label>
          <textarea id="taskDescription" name="description" maxlength="2000" placeholder="Add a little context or a note for later"><c:out value="${task.description}"/></textarea>
        </div>
        <div class="field task-field-full">
          <div class="task-category-label"><label for="taskCategory">Category <span class="task-optional">Optional</span></label><a href="${ctx}/categories">Manage categories</a></div>
          <select id="taskCategory" name="categoryId">
            <option value="">No category</option>
            <c:forEach var="category" items="${categories}">
              <option value="${category.categoryId}" ${task.categoryId == category.categoryId ? 'selected' : ''}><c:out value="${category.categoryName}"/></option>
            </c:forEach>
          </select>
        </div>
        <div class="field">
          <label for="taskDueDate">Due date <span class="task-optional">Optional</span></label>
          <input id="taskDueDate" name="dueDate" type="date" value="<c:out value='${task.dueDate}'/>">
        </div>
        <div class="field">
          <label for="taskPriority">Priority</label>
          <select id="taskPriority" name="priority" required>
            <option value="LOW" ${task.priority == 'LOW' ? 'selected' : ''}>Low</option>
            <option value="MEDIUM" ${empty task.priority || task.priority == 'MEDIUM' ? 'selected' : ''}>Medium</option>
            <option value="HIGH" ${task.priority == 'HIGH' ? 'selected' : ''}>High</option>
          </select>
        </div>
        <div class="field task-field-full share-picker" id="shareTaskSection">
          <label class="important-choice"><input type="checkbox" name="important" ${task.important ? 'checked' : ''}> Star this task as important</label>
          <label for="sharedWith">Share with <span class="task-optional">Optional &middot; accepted friends only</span></label>
          <c:choose><c:when test="${empty shareChoices}"><div class="share-empty">Add friends before sharing tasks. <a href="${ctx}/friends">Find friends</a></div></c:when><c:otherwise><select id="sharedWith" name="sharedWith" multiple size="4" aria-describedby="shareHint"><c:forEach var="person" items="${shareChoices}"><option value="${person.userId}" ${selectedShareIds.contains(person.userId) ? 'selected' : ''}><c:out value="${person.fullName}"/> &middot; @<c:out value="${person.username}"/></option></c:forEach></select><small id="shareHint" class="field-hint">Use Ctrl or Command to select more than one friend. Remove all selections to make this task private.</small></c:otherwise></c:choose>
        </div>
        <div class="task-form-actions">
          <c:choose><c:when test="${task.taskId > 0}"><a class="btn secondary" href="${ctx}/tasks?action=view&amp;id=${task.taskId}">Cancel</a></c:when><c:otherwise><a class="btn secondary" href="${ctx}/tasks">Cancel</a></c:otherwise></c:choose>
          <button class="btn primary" type="submit">${task.taskId > 0 ? 'Save changes' : 'Create Task'} <span aria-hidden="true">&rarr;</span></button>
        </div>
      </form>
    </section>
    <footer class="app-footer">&#169; 2026 Task Flow</footer>
  </main>
</div>
<script src="${ctx}/js/app.js?v=20260927-polish"></script>
</body>
</html>
