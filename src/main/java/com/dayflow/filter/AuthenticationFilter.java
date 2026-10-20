package com.dayflow.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/** Requires a signed-in user for every path that is not explicitly public. */
public class AuthenticationFilter implements Filter {
    private Set<String> excludedPaths = Set.of();

    @Override
    public void init(FilterConfig config) {
        String configuredPaths = config.getInitParameter("excludedPaths");
        if (configuredPaths == null || configuredPaths.isBlank()) {
            return;
        }

        excludedPaths = Arrays.stream(configuredPaths.split(","))
                .map(String::trim)
                .filter(path -> !path.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String requestPath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        var session = httpRequest.getSession(false);
        if (isExcluded(requestPath) || session != null && session.getAttribute("user") != null) {
            chain.doFilter(request, response);
            return;
        }

        httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
    }

    private boolean isExcluded(String requestPath) {
        if (requestPath.equals("/")) {
            return true;
        }

        return excludedPaths.stream().anyMatch(path -> requestPath.equals(path)
                || requestPath.startsWith(path.endsWith("/") ? path : path + "/"));
    }
}
