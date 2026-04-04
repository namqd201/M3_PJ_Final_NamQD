package com.tmdt.m3_pj_final_namqd.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final ObjectMapper mapper;

    public JwtFilter(JwtProvider jwtProvider, UserRepository userRepository, ObjectMapper mapper) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {

            String header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {

                String token = header.substring(7);

                if (jwtProvider.validateToken(token)) {

                    String username = jwtProvider.getUsernameFromToken(token);

                    User user = userRepository
                            .findByUsernameAndIsDeletedFalse(username)
                            .orElse(null);

                    if (user != null) {

                        if (!user.getIsActive()) {
                            log.warn("JWT rejected: inactive user '{}'", username);
                            throw new AppException("USER_DISABLED", HttpStatus.FORBIDDEN);
                        }

                        // MAP ROLE -> GrantedAuthority
                        var authorities = List.of(
                                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                        );

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        user,
                                        null,
                                        authorities
                                );

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        log.warn("JWT valid but user missing or deleted: '{}'", username);
                    }
                } else {
                    log.debug("JWT invalid or expired, uri={}", request.getRequestURI());
                }
            }

            filterChain.doFilter(request, response);

        } catch (AppException ex) {

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            Map<String, Object> body = new HashMap<>();
            body.put("code", ex.getCode());
            body.put("message", ex.getMessage());

            response.setStatus(ex.getStatus().value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            mapper.writeValue(response.getWriter(), body);
            return;
        }
    }
}
