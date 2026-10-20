<%@ include file="/WEB-INF/jspf/header.jspf" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Task Flow | Something went wrong</title>
  <script src="${ctx}/js/theme-init.js?v=20260927-polish"></script>
  <link rel="stylesheet" href="${ctx}/css/style.css?v=20260927-polish">
</head>
<body>
  <main class="error-page">
    <section class="card error-card" role="alert">
      <span class="error-mark" aria-hidden="true">!</span>
      <h1>We couldn't open that page</h1>
      <p>Something went wrong. Please try again, or return to your workspace.</p>
      <a class="btn primary" href="${ctx}/">Back to Task Flow</a>
    </section>
  </main>
</body>
</html>
