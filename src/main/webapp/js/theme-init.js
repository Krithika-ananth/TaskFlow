/* Runs before the stylesheet so a saved theme is applied without a light-mode flash. */
(function () {
  try {
    if (window.localStorage.getItem('taskflow-theme') === 'dark') {
      document.documentElement.setAttribute('data-theme', 'dark');
    }
  } catch (ignored) {
    // Private browsing policies may disable localStorage; light mode remains available.
  }
})();
