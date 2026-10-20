package com.dayflow.controller;

import com.dayflow.dao.ResourceDAO;
import com.dayflow.model.Resource;
import com.dayflow.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/resources")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 10L * 1024 * 1024, maxRequestSize = 12L * 1024 * 1024)
public class ResourceServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ResourceServlet.class.getName());
    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;
    private static final String STORAGE_ENV = "TASKFLOW_RESOURCE_STORAGE";

    private static final java.util.Map<String, String> ALLOWED_MIME = java.util.Map.ofEntries(
            java.util.Map.entry("pdf", "application/pdf"),
            java.util.Map.entry("txt", "text/plain"),
            java.util.Map.entry("csv", "text/csv"),
            java.util.Map.entry("rtf", "application/rtf"),
            java.util.Map.entry("doc", "application/msword"),
            java.util.Map.entry("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
            java.util.Map.entry("xls", "application/vnd.ms-excel"),
            java.util.Map.entry("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
            java.util.Map.entry("ppt", "application/vnd.ms-powerpoint"),
            java.util.Map.entry("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
            java.util.Map.entry("png", "image/png"),
            java.util.Map.entry("jpg", "image/jpeg"),
            java.util.Map.entry("jpeg", "image/jpeg")
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = currentUser(request);
        if (user == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }
        String action = trim(request.getParameter("action"));
        if ("download".equals(action) || "preview".equals(action)) {
            streamFile(request, response, user.getUserId(), action);
            return;
        }
        try {
            showPage(request, response, user.getUserId(), findEditResource(request, user.getUserId()), trim(request.getParameter("message")), trim(request.getParameter("error")));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to prepare Resources page", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load resources. Please try again.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = currentUser(request);
        if (user == null) { response.sendRedirect(request.getContextPath() + "/login"); return; }
        long userId = user.getUserId();
        String requestContentType = request.getContentType();
        if (requestContentType != null && requestContentType.toLowerCase(Locale.ROOT).startsWith("multipart/form-data")) {
            if (request.getContentLengthLong() > 12L * 1024 * 1024) {
                showPage(request, response, userId, null, null, "Unable to process the upload. Check that the file is no larger than 10 MB.");
                return;
            }
            try {
                request.getParts();
            } catch (IllegalStateException | ServletException | IOException e) {
                LOGGER.log(Level.WARNING, "Unable to parse resource upload for user " + userId, e);
                showPage(request, response, userId, null, null, "Unable to process the upload. Check that the file is no larger than 10 MB.");
                return;
            }
        }
        String action = trim(request.getParameter("action"));
        ResourceDAO dao = new ResourceDAO();
        Path createdFile = null;
        try {
            switch (action) {
                case "create" -> {
                    String type = trim(request.getParameter("resourceType")).toUpperCase(Locale.ROOT);
                    String title = cleanText(request.getParameter("title"));
                    String description = cleanText(request.getParameter("description"));
                    validateTitle(title);
                    if ("LINK".equals(type)) {
                        String url = validateUrl(request.getParameter("url"));
                        dao.createLink(userId, title, description, url);
                    } else if ("DOCUMENT".equals(type)) {
                        Part file = request.getPart("file");
                        Upload upload = validateAndStore(file, title, userId);
                        createdFile = upload.path();
                        dao.createDocument(userId, title, description, upload.originalFilename(), upload.contentType(), upload.size(), upload.path().toString());
                    } else {
                        throw new UserInputException("Choose a document or a link.");
                    }
                    response.sendRedirect(request.getContextPath() + "/resources?message=Resource+added+successfully.");
                }
                case "edit" -> {
                    long id = parseId(request.getParameter("id"));
                    Resource existing = dao.findById(userId, id);
                    if (existing == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found."); return; }
                    String title = cleanText(request.getParameter("title"));
                    String description = cleanText(request.getParameter("description"));
                    validateTitle(title);
                    boolean updated;
                    if ("LINK".equals(existing.getResourceType())) {
                        updated = dao.updateLink(userId, id, title, description, validateUrl(request.getParameter("url")));
                    } else {
                        updated = dao.updateDocumentMetadata(userId, id, title, description);
                    }
                    if (!updated) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found."); return; }
                    response.sendRedirect(request.getContextPath() + "/resources?message=Resource+updated+successfully.");
                }
                case "delete" -> {
                    long id = parseId(request.getParameter("id"));
                    Resource deleted = dao.delete(userId, id);
                    if (deleted == null) { response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found."); return; }
                    deleteStoredFile(deleted);
                    response.sendRedirect(request.getContextPath() + "/resources?message=Resource+deleted+successfully.");
                }
                default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid resource action.");
            }
        } catch (UserInputException e) {
            showPage(request, response, userId, null, null, e.getMessage());
        } catch (IllegalStateException e) {
            if (createdFile != null) deleteQuietly(createdFile);
            LOGGER.log(Level.WARNING, "Resource request rejected", e);
            showPage(request, response, userId, null, null, "Unable to process the upload. Check that the file is no larger than 10 MB.");
        } catch (IOException e) {
            if (createdFile != null) deleteQuietly(createdFile);
            LOGGER.log(Level.WARNING, "Unable to store the resource file for user " + userId, e);
            showPage(request, response, userId, null, null, "Unable to upload the document. Check available storage and try again.");
        } catch (Exception e) {
            if (createdFile != null) deleteQuietly(createdFile);
            LOGGER.log(Level.SEVERE, "Resource operation failed for user " + userId, e);
            showPage(request, response, userId, null, null, "Unable to save the resource. Please try again.");
        }
    }

    private Resource findEditResource(HttpServletRequest request, long userId) throws Exception {
        String raw = trim(request.getParameter("edit"));
        if (raw.isBlank()) return null;
        long id = parseId(raw);
        return new ResourceDAO().findById(userId, id);
    }

    private void showPage(HttpServletRequest request, HttpServletResponse response, long userId,
                          Resource editResource, String message, String error) throws ServletException, IOException {
        try {
            String search = trim(request.getParameter("q"));
            String type = trim(request.getParameter("type")).toUpperCase(Locale.ROOT);
            if (!"DOCUMENT".equals(type) && !"LINK".equals(type)) type = "ALL";
            ResourceDAO dao = new ResourceDAO();
            request.setAttribute("resources", dao.findByUser(userId, search, "ALL".equals(type) ? null : type));
            request.setAttribute("search", search);
            request.setAttribute("resourceTypeFilter", type);
            request.setAttribute("editResource", editResource);
            request.setAttribute("message", message);
            request.setAttribute("error", error);
            request.setAttribute("pageTitle", "Resources");
            request.setAttribute("pageSubtitle", "Keep the documents and links you need close to your work.");
            request.getRequestDispatcher("/resources.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to load resources for user " + userId, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load resources. Please try again.");
        }
    }

    private void streamFile(HttpServletRequest request, HttpServletResponse response, long userId, String action) throws IOException {
        try {
            long id = parseId(request.getParameter("id"));
            Resource resource = new ResourceDAO().findById(userId, id);
            if (resource == null || !"DOCUMENT".equals(resource.getResourceType())) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found."); return;
            }
            Path base = storageRoot().toAbsolutePath().normalize();
            Path file = Paths.get(resource.getStoragePath()).toAbsolutePath().normalize();
            if (!file.startsWith(base) || !Files.isRegularFile(file)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource file not found."); return;
            }
            String contentType = resource.getContentType();
            if (contentType == null || contentType.isBlank()) contentType = "application/octet-stream";
            response.setContentType(contentType);
            response.setContentLengthLong(Files.size(file));
            String safeName = safeDownloadName(resource.getOriginalFilename());
            String disposition = "download".equals(action) ? "attachment" : "inline";
            response.setHeader("Content-Disposition", disposition + "; filename=\"" + safeName + "\"");
            response.setHeader("X-Content-Type-Options", "nosniff");
            try (InputStream in = Files.newInputStream(file); OutputStream out = response.getOutputStream()) {
                in.transferTo(out);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to stream resource", e);
            if (!response.isCommitted()) response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to open the resource.");
        }
    }

    private Upload validateAndStore(Part file, String title, long userId) throws Exception {
        if (file == null || file.getSize() == 0) throw new UserInputException("Please choose a file.");
        if (file.getSize() > MAX_FILE_SIZE) throw new UserInputException("File is too large. Maximum size is 10 MB.");
        String original = sanitizeOriginalFilename(file.getSubmittedFileName());
        String extension = extensionOf(original);
        String expectedMime = ALLOWED_MIME.get(extension);
        if (expectedMime == null) throw new UserInputException("Invalid file type.");
        String declaredMime = normalizeMime(file.getContentType());
        String detectedMime = normalizeMime(Files.probeContentType(Paths.get(original)));
        if (!mimeAllowed(extension, declaredMime, detectedMime)) throw new UserInputException("Invalid file type or MIME type.");
        if (!contentMatchesType(file, extension)) throw new UserInputException("The file content does not match its file type.");
        if (looksExecutable(file, extension)) throw new UserInputException("Executable files are not allowed.");

        Path userDir = storageRoot().resolve("user-" + userId).normalize();
        Files.createDirectories(userDir);
        String storedName = UUID.randomUUID() + "." + extension;
        Path target = userDir.resolve(storedName).normalize();
        if (!target.startsWith(userDir)) throw new UserInputException("Invalid file name.");
        try (InputStream in = file.getInputStream()) { Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING); }
        return new Upload(original, declaredMime.isBlank() ? expectedMime : declaredMime, file.getSize(), target);
    }

    private boolean mimeAllowed(String extension, String declared, String detected) {
        String expected = ALLOWED_MIME.get(extension);
        if (expected.equals(declared)) return true;
        if (expected.equals(detected)) return true;
        if ("txt".equals(extension) && "application/octet-stream".equals(declared)) return true;
        if ("csv".equals(extension) && ("application/vnd.ms-excel".equals(declared) || "application/octet-stream".equals(declared))) return true;
        return false;
    }

    private boolean contentMatchesType(Part file, String extension) throws IOException {
        if (extension.equals("txt") || extension.equals("csv") || extension.equals("rtf")) return true;
        try (InputStream in = file.getInputStream()) {
            byte[] head = in.readNBytes(16);
            if (extension.equals("pdf")) return startsWith(head, "%PDF-");
            if (extension.equals("png")) return startsWith(head, "\u0089PNG\r\n\u001a\n");
            if (extension.equals("jpg") || extension.equals("jpeg")) return head.length >= 3 && (head[0] & 0xff) == 0xff && (head[1] & 0xff) == 0xd8 && (head[2] & 0xff) == 0xff;
            if (extension.equals("docx") || extension.equals("xlsx") || extension.equals("pptx")) return head.length >= 4 && head[0] == 'P' && head[1] == 'K' && head[2] == 3 && head[3] == 4;
            if (extension.equals("doc") || extension.equals("xls") || extension.equals("ppt")) return head.length >= 8 && (head[0] & 0xff) == 0xd0 && (head[1] & 0xff) == 0xcf && (head[2] & 0xff) == 0x11 && (head[3] & 0xff) == 0xe0;
            return false;
        }
    }

    private boolean startsWith(byte[] bytes, String signature) {
        byte[] expected = signature.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
        if (bytes.length < expected.length) return false;
        for (int i = 0; i < expected.length; i++) if (bytes[i] != expected[i]) return false;
        return true;
    }

    private boolean looksExecutable(Part file, String extension) throws IOException {
        if (extension.equals("exe") || extension.equals("bat") || extension.equals("cmd") || extension.equals("jsp") || extension.equals("php") || extension.equals("sh")) return true;
        try (InputStream in = file.getInputStream()) {
            byte[] head = in.readNBytes(8);
            if (head.length >= 2 && head[0] == 'M' && head[1] == 'Z') return true;
            if (head.length >= 4 && head[0] == '#' && head[1] == '!') return true;
            return head.length >= 4 && head[0] == '<' && head[1] == '%' ;
        }
    }

    private void deleteStoredFile(Resource resource) {
        if (!"DOCUMENT".equals(resource.getResourceType()) || resource.getStoragePath() == null) return;
        try {
            Path base = storageRoot().toAbsolutePath().normalize();
            Path file = Paths.get(resource.getStoragePath()).toAbsolutePath().normalize();
            if (file.startsWith(base)) Files.deleteIfExists(file);
        } catch (Exception e) { LOGGER.log(Level.WARNING, "Unable to delete stored resource file", e); }
    }

    private Path storageRoot() throws IOException {
        String configured = System.getenv(STORAGE_ENV);
        String catalinaBase = System.getProperty("catalina.base");
        Path root = configured != null && !configured.isBlank()
                ? Paths.get(configured)
                : catalinaBase != null && !catalinaBase.isBlank()
                        ? Paths.get(catalinaBase, "resources")
                        : Paths.get(System.getProperty("user.home"), ".taskflow", "resources");
        Path normalized = root.toAbsolutePath().normalize();
        Files.createDirectories(normalized);
        return normalized;
    }

    private User currentUser(HttpServletRequest request) {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    private long parseId(String raw) {
        try { long id = Long.parseLong(raw); if (id < 1) throw new NumberFormatException(); return id; }
        catch (Exception e) { throw new NumberFormatException("Invalid resource id"); }
    }

    private void validateTitle(String title) throws UserInputException {
        if (title.isBlank() || title.length() > 180) throw new UserInputException("Title is required and must be 180 characters or fewer.");
    }

    private String validateUrl(String raw) throws UserInputException {
        String value = trim(raw);
        if (value.length() > 2000) throw new UserInputException("URL is too long.");
        try {
            URI uri = URI.create(value);
            String scheme = uri.getScheme();
            if (!("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) || uri.getHost() == null) throw new UserInputException("Only valid http:// and https:// links are allowed.");
            return value;
        } catch (IllegalArgumentException e) { throw new UserInputException("Only valid http:// and https:// links are allowed."); }
    }

    private String sanitizeOriginalFilename(String raw) throws UserInputException {
        String name = raw == null ? "" : raw.replace('\\', '/');
        int slash = name.lastIndexOf('/'); if (slash >= 0) name = name.substring(slash + 1);
        name = name.replaceAll("[\\r\\n\\\"<>]", "_").trim();
        if (name.isBlank() || name.length() > 255) throw new UserInputException("Invalid file name.");
        return name;
    }

    private String safeDownloadName(String raw) { return (raw == null || raw.isBlank() ? "resource" : raw).replaceAll("[\\r\\n\\\"<>]", "_").replace('\\', '_'); }
    private String extensionOf(String name) { int dot = name.lastIndexOf('.'); return dot > 0 && dot < name.length() - 1 ? name.substring(dot + 1).toLowerCase(Locale.ROOT) : ""; }
    private String normalizeMime(String value) { return value == null ? "" : value.toLowerCase(Locale.ROOT).split(";", 2)[0].trim(); }
    private String trim(String value) { return value == null ? "" : value.trim(); }
    private String cleanText(String value) { return trim(value); }
    private void deleteQuietly(Path file) { try { Files.deleteIfExists(file); } catch (IOException ignored) {} }

    private record Upload(String originalFilename, String contentType, long size, Path path) {}
    private static class UserInputException extends Exception { UserInputException(String message) { super(message); } }
}
