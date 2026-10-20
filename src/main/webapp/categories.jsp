<%@ include file="/WEB-INF/jspf/header.jspf" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Task Flow | Categories</title>
  <script src="${pageContext.request.contextPath}/js/theme-init.js?v=20260927-polish"></script><link rel="stylesheet" href="${ctx}/css/style.css?v=20260927-polish">
</head>
<body>
<div class="app">
  <%@ include file="/WEB-INF/jspf/sidebar.jspf" %>
  <main class="main">
    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>
    <div class="category-page-top"><a class="task-back-link" href="${ctx}/tasks">&larr; Back to My Tasks</a></div>
    <c:if test="${not empty error}"><div class="task-form-error" role="alert"><c:out value="${error}"/></div></c:if>
    <c:if test="${param.created == '1'}"><div class="task-notice" role="status">Category created.</div></c:if>
    <c:if test="${param.updated == '1'}"><div class="task-notice" role="status">Category updated.</div></c:if>
    <c:if test="${param.deleted == '1'}"><div class="task-notice" role="status">Category deleted. Its tasks are still here without a category.</div></c:if>

    <section class="category-create-card">
      <div class="category-section-heading"><span class="category-heading-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M4 5h7l2 2h7v12H4z"/><path d="M12 10v6M9 13h6"/></svg></span><span><h2>Add a category</h2><p>Create a label that fits the way you plan.</p></span></div>
      <form class="category-create-form" method="post" action="${ctx}/categories">
        <input type="hidden" name="action" value="create">
        <label class="sr-only" for="newCategoryName">Category name</label>
        <input id="newCategoryName" name="name" maxlength="80" placeholder="e.g. Creative projects" required>
        <button class="btn primary" type="submit">Add category <span aria-hidden="true">+</span></button>
      </form>
    </section>

    <section class="category-list-section" aria-labelledby="categoryListTitle">
      <div class="category-list-heading"><div><span class="eyebrow">YOUR PERSONAL LABELS</span><h2 id="categoryListTitle">Your categories</h2></div><span class="category-count"><c:out value="${fn:length(categories)}"/> total</span></div>
      <c:choose>
        <c:when test="${empty categories}"><div class="category-empty"><strong>No categories yet</strong><span>Add one above to give your tasks a little more structure.</span></div></c:when>
        <c:otherwise>
          <div class="category-list">
            <c:forEach var="category" items="${categories}">
              <article class="category-item">
                <span class="category-dot" aria-hidden="true"></span>
                <form method="post" action="${ctx}/categories" class="category-rename-form">
                  <input type="hidden" name="action" value="rename"><input type="hidden" name="id" value="${category.categoryId}">
                  <label class="sr-only" for="category-${category.categoryId}">Rename category</label>
                  <input id="category-${category.categoryId}" name="name" maxlength="80" value="<c:out value='${category.categoryName}'/>" required>
                  <button class="category-save" type="submit" aria-label="Save category name">Save</button>
                </form>
                <form method="post" action="${ctx}/categories" class="category-delete-form">
                  <input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${category.categoryId}">
                  <button class="category-delete" type="submit" data-confirm="Delete this category? Tasks in it will remain, but become uncategorized." aria-label="Delete category"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M5 7h14M10 11v6M14 11v6M7 7l1 13h8l1-13M9 7V4h6v3"/></svg></button>
                </form>
              </article>
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
