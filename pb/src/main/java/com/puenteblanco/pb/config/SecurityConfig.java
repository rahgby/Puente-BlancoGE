package com.puenteblanco.pb.config;

import com.puenteblanco.pb.security.JwtAuthenticationFilter;
import com.puenteblanco.pb.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final JwtAuthenticationFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            "/css/**",
            "/js/**",
            "/images/**",
            "/favicon.ico",
            "/webjars/**",
            "/vet/**",
            "/vet/veterinarian_dashboard.html"
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/index.html",
                    "/css/**", "/js/**", "/img/**",
                    "/api/auth/**",
                    "/api/reniec/**",
                    "/api/recovery/**",
                    "/vet/veterinarian_dashboard.html"
                ).permitAll()
                .requestMatchers(
                    "/dashboard",
                    "/calendar",
                    "/book-appointment",
                    "/cancel-appointment",
                    "/appointments/**",
                    "/logout",
                    "/pets/**",
                    "/api/payments/**",
                    "/payment-form",
                    "/api/client/**"
                ).hasRole("CLIENT")
                .requestMatchers(
                    "/vet/**",
                    "/api/vet/**",
                    "/api/vet/reports/**"
                ).hasRole("VETERINARIAN")
                .requestMatchers(
                    "/intern/**",
                    "/api/intern/**"
                ).hasRole("INTERN")
                .requestMatchers(
                    "/admin/**",
                    "/api/admin/**",
                    "/api/roles"
                ).hasRole("ADMIN")
                .anyRequest().authenticated())
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**")
            .addResourceLocations("classpath:/static/css/")
            .setCachePeriod(0);
        
        registry.addResourceHandler("/js/**")
            .addResourceLocations("classpath:/static/js/");
        
        registry.addResourceHandler("/images/**")
            .addResourceLocations("classpath:/static/images/");
    }
}