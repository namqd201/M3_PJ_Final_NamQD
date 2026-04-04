package com.tmdt.m3_pj_final_namqd.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tmdt.m3_pj_final_namqd.config.jwt.JwtAuthenticationEntryPoint;
import com.tmdt.m3_pj_final_namqd.config.jwt.JwtFilter;
import com.tmdt.m3_pj_final_namqd.config.jwt.JwtProvider;
import com.tmdt.m3_pj_final_namqd.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
        public SecurityFilterChain filterChain(HttpSecurity http, JwtProvider jwtProvider, UserRepository userRepository
                                                , JwtAuthenticationEntryPoint entryPoint, ObjectMapper objectMapper) throws Exception {

            JwtFilter jwtFilter = new JwtFilter(jwtProvider, userRepository, objectMapper);

            http
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .httpBasic(httpBasic -> httpBasic.disable())
                    .formLogin(form -> form.disable())
                    .exceptionHandling(ex -> ex
                            .authenticationEntryPoint(entryPoint)
                    )
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/api/auth/**"
                            ).permitAll()
//                            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                            .anyRequest().authenticated()
                    )
                    .addFilterBefore(jwtFilter,
                            org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("Not used");
        };
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
