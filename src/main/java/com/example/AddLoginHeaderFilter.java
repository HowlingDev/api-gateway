package com.example;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@Component
public class AddLoginHeaderFilter extends OncePerRequestFilter {

    private static final String USER_LOGIN_HEADER = "X-User-Login";
    private static final String LOGIN = "preferred_username";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            String login = ((JwtAuthenticationToken) authentication).getToken().getClaim(LOGIN);
            if (login != null) {
                HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request) {
                    @Override
                    public String getHeader(String name) {
                        if (USER_LOGIN_HEADER.equals(name)) {
                            return login;
                        }
                        return super.getHeader(name);
                    }

                    @Override
                    public Enumeration<String> getHeaders(String name) {
                        if (USER_LOGIN_HEADER.equals(name)) {
                            return Collections.enumeration(List.of(login));
                        }
                        return super.getHeaders(name);
                    }

                    @Override
                    public Enumeration<String> getHeaderNames() {
                        List<String> names = Collections.list(super.getHeaderNames());
                        names.add(USER_LOGIN_HEADER);
                        return Collections.enumeration(names);
                    }
                };

                filterChain.doFilter(requestWrapper, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
