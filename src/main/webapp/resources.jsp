<%@ include file="/WEB-INF/jspf/header.jspf" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Task Flow | Resources</title>
  <script src="${pageContext.request.contextPath}/js/theme-init.js?v=20260927-polish"></script>
  <link rel="stylesheet" href="${ctx}/css/style.css?v=20260927-polish">
  <c:url var="documentFilterUrl" value="${ctx}/resources"><c:param name="type" value="DOCUMENT"/><c:if test="${not empty search}"><c:param name="q" value="${search}"/></c:if></c:url>
  <c:url var="linkFilterUrl" value="${ctx}/resources"><c:param name="type" value="LINK"/><c:if test="${not empty search}"><c:param name="q" value="${search}"/></c:if></c:url>
</head>
<body>
<div class="app">
  <%@ include file="/WEB-INF/jspf/sidebar.jspf" %>
  <main class="main">
    <%@ include file="/WEB-INF/jspf/app-header.jspf" %>

    <section class="resources-toolbar" aria-label="Resource controls">
      <form class="resources-search" method="get" action="${ctx}/resources">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true"><circle cx="10.8" cy="10.8" r="6.8"/><path d="m16 16 4.5 4.5"/></svg>
        <input type="search" name="q" value="<c:out value='${search}'/>" placeholder="Search title, description, filename, or URL" aria-label="Search resources">
      </form>
      <div class="resources-controls">
        <a class="pill ${resourceTypeFilter=='ALL'?'active':''}" href="${ctx}/resources">All</a>
        <a class="pill ${resourceTypeFilter=='DOCUMENT'?'active':''}" href="${documentFilterUrl}">Documents</a>
        <a class="pill ${resourceTypeFilter=='LINK'?'active':''}" href="${linkFilterUrl}">Links</a>
        <button class="btn primary" type="button" id="resourceAddButton">+ Add Resource</button>
      </div>
    </section>

    <c:if test="${not empty message}"><div class="resource-alert resource-alert-success" role="status"><c:out value="${message}"/></div></c:if>
    <c:if test="${not empty error}"><div class="resource-alert resource-alert-error" role="alert"><c:out value="${error}"/></div></c:if>

    <section class="resources-list" aria-label="Your resources">
      <c:choose>
        <c:when test="${empty resources}">
          <div class="resource-empty">
            <div class="resource-empty-icon"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M4 5.5A2.5 2.5 0 0 1 6.5 3H20v16H6.5A2.5 2.5 0 0 0 4 21z"/><path d="M4 5.5v13A2.5 2.5 0 0 1 6.5 16H20"/></svg></div>
            <h2>No resources yet</h2>
            <p>Add a document or save a useful web link. Your resources are private to your account.</p>
            <button class="btn primary" type="button" id="resourceEmptyAddButton">Add your first resource</button>
          </div>
        </c:when>
        <c:otherwise>
          <div class="resource-grid">
            <c:forEach var="resource" items="${resources}">
              <article class="resource-card">
                <div class="resource-card-icon ${resource.resourceType=='DOCUMENT'?'document':'link'}">
                  <c:choose>
                    <c:when test="${resource.resourceType=='DOCUMENT'}"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M6 3h8l4 4v14H6z"/><path d="M14 3v5h5M9 13h6M9 17h6"/></svg></c:when>
                    <c:otherwise><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><circle cx="12" cy="12" r="8"/><path d="M8 12h8M12 8v8"/></svg></c:otherwise>
                  </c:choose>
                </div>
                <div class="resource-card-copy">
                  <span class="resource-type"><c:out value="${resource.resourceType}"/></span>
                  <h2 title="<c:out value='${resource.title}'/>"><c:out value="${resource.title}"/></h2>
                  <p><c:out value="${resource.description}"/></p>
                  <c:if test="${resource.resourceType=='DOCUMENT'}"><small><c:out value="${resource.originalFilename}"/> &middot; <c:out value="${resource.fileSize}"/> bytes</small></c:if>
                  <c:if test="${resource.resourceType=='LINK'}"><small class="resource-url"><c:out value="${resource.resourceUrl}"/></small></c:if>
                </div>
                <div class="resource-actions">
                  <c:choose>
                    <c:when test="${resource.resourceType=='DOCUMENT'}">
                      <a class="resource-action" href="${ctx}/resources?action=preview&amp;id=${resource.resourceId}" target="_blank" rel="noopener noreferrer" title="Open / preview" aria-label="Open <c:out value='${resource.title}'/>"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M2.5 12s3.5-6 9.5-6 9.5 6 9.5 6-3.5 6-9.5 6-9.5-6-9.5-6Z"/><circle cx="12" cy="12" r="2.5"/></svg></a>
                      <a class="resource-action" href="${ctx}/resources?action=download&amp;id=${resource.resourceId}" title="Download" aria-label="Download <c:out value='${resource.title}'/>"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M12 3v12M7 10l5 5 5-5M5 21h14"/></svg></a>
                    </c:when>
                    <c:otherwise>
                      <a class="resource-action" href="<c:out value='${resource.resourceUrl}'/>" target="_blank" rel="noopener noreferrer" title="Open link" aria-label="Open <c:out value='${resource.title}'/>"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M14 5h5v5M19 5l-8 8"/><path d="M18 13v5a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V7a1 1 0 0 1 1-1h5"/></svg></a>
                    </c:otherwise>
                  </c:choose>
                  <a class="resource-action" href="${ctx}/resources?edit=${resource.resourceId}" title="Edit" aria-label="Edit <c:out value='${resource.title}'/>"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M4 20h4l11-11-4-4L4 16zM13.5 6.5l4 4"/></svg></a>
                  <form method="post" action="${ctx}/resources" class="resource-delete-form">
                    <input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="${resource.resourceId}">
                    <button class="resource-action danger" type="submit" data-confirm="Delete this resource?" title="Delete" aria-label="Delete <c:out value='${resource.title}'/>"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M5 7h14M10 11v6M14 11v6M8 7l1-3h6l1 3M7 7l1 14h8l1-14"/></svg></button>
                  </form>
                </div>
              </article>
            </c:forEach>
          </div>
        </c:otherwise>
      </c:choose>
    </section>

    <c:if test="${not empty param.edit and not empty editResource}">
      <div class="resource-modal" id="resourceEditModal" role="dialog" aria-modal="true" aria-labelledby="resourceEditTitle">
        <div class="resource-modal-backdrop"></div>
        <div class="resource-modal-card">
          <button class="resource-modal-close" type="button" data-close-resource-modal aria-label="Close">&times;</button>
          <span class="eyebrow">EDIT RESOURCE</span>
          <h2 id="resourceEditTitle">Edit <c:out value="${editResource.resourceType}"/></h2>
          <form method="post" action="${ctx}/resources" class="form-grid">
            <input type="hidden" name="action" value="edit"><input type="hidden" name="id" value="${editResource.resourceId}">
            <div class="field full"><label for="editTitle">Title</label><input id="editTitle" name="title" maxlength="180" required value="<c:out value='${editResource.title}'/>"/></div>
            <div class="field full"><label for="editDescription">Description</label><textarea id="editDescription" name="description" maxlength="2000"><c:out value="${editResource.description}"/></textarea></div>
            <c:if test="${editResource.resourceType=='LINK'}"><div class="field full"><label for="editUrl">URL</label><input id="editUrl" name="url" type="url" required value="<c:out value='${editResource.resourceUrl}'/>"/></div></c:if>
            <div class="form-actions full"><a class="btn secondary" href="${ctx}/resources">Cancel</a><button class="btn primary" type="submit">Save changes</button></div>
          </form>
        </div>
      </div>
    </c:if>

    <div class="resource-modal" id="resourceAddModal" hidden role="dialog" aria-modal="true" aria-labelledby="resourceAddTitle">
      <div class="resource-modal-backdrop" data-close-resource-modal></div>
      <div class="resource-modal-card">
        <button class="resource-modal-close" type="button" data-close-resource-modal aria-label="Close">&times;</button>
        <span class="eyebrow">ADD TO YOUR FLOW</span>
        <h2 id="resourceAddTitle">Add Resource</h2>
        <div class="resource-type-tabs" role="tablist" aria-label="Resource type">
          <button type="button" class="pill active" data-resource-type="DOCUMENT">Upload Document</button>
          <button type="button" class="pill" data-resource-type="LINK">Save Link</button>
        </div>
        <form method="post" action="${ctx}/resources" enctype="multipart/form-data" class="form-grid" id="resourceCreateForm">
          <input type="hidden" name="action" value="create"><input type="hidden" name="resourceType" id="resourceType" value="DOCUMENT">
          <div class="field full"><label for="resourceTitle">Title</label><input id="resourceTitle" name="title" maxlength="180" required></div>
          <div class="field full"><label for="resourceDescription">Description</label><textarea id="resourceDescription" name="description" maxlength="2000" placeholder="Optional"></textarea></div>
          <div class="field full" id="documentField"><label for="resourceFile">Document</label><input id="resourceFile" name="file" type="file" accept=".pdf,.txt,.csv,.rtf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.png,.jpg,.jpeg"><span class="field-hint">PDF, Office documents, text, CSV, or common images. Maximum 10 MB.</span></div>
          <div class="field full" id="linkField" hidden><label for="resourceUrl">URL</label><input id="resourceUrl" name="url" type="url" placeholder="https://example.com" inputmode="url"><span class="field-hint">Only http:// and https:// links are allowed.</span></div>
          <div class="form-actions full"><button class="btn secondary" type="button" data-close-resource-modal>Cancel</button><button class="btn primary" type="submit">Add Resource</button></div>
        </form>
      </div>
    </div>

    <footer class="app-footer">&#169; 2026 Task Flow</footer>
  </main>
</div>
<script src="${ctx}/js/app.js?v=20260927-polish"></script>
<script>
(function(){
  const addModal=document.getElementById('resourceAddModal');
  const addButtons=[document.getElementById('resourceAddButton'),document.getElementById('resourceEmptyAddButton')].filter(Boolean);
  const typeButtons=document.querySelectorAll('[data-resource-type]');
  const typeInput=document.getElementById('resourceType');
  const docField=document.getElementById('documentField');
  const linkField=document.getElementById('linkField');
  const fileInput=document.getElementById('resourceFile');
  const urlInput=document.getElementById('resourceUrl');
  function openModal(){if(!addModal)return;addModal.hidden=false;document.body.classList.add('resource-modal-open');document.getElementById('resourceTitle')?.focus();}
  function closeModal(){if(!addModal)return;addModal.hidden=true;document.body.classList.remove('resource-modal-open');}
  addButtons.forEach(b=>b.addEventListener('click',openModal));
  document.querySelectorAll('[data-close-resource-modal]').forEach(b=>b.addEventListener('click',function(){if(this.closest('#resourceEditModal')) window.location.href='${ctx}/resources'; else closeModal();}));
  typeButtons.forEach(btn=>btn.addEventListener('click',function(){
    const type=this.dataset.resourceType;typeInput.value=type;typeButtons.forEach(x=>x.classList.toggle('active',x===this));
    const doc=type==='DOCUMENT';docField.hidden=!doc;linkField.hidden=doc;
    fileInput.required=doc;urlInput.required=!doc;
  }));
  document.addEventListener('keydown',e=>{if(e.key==='Escape'&&!addModal.hidden)closeModal();});
})();
</script>
</body>
</html>
