package my_spring_backend.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for APIs (we'll rely on JWT later)
                .csrf(csrf -> csrf.disable())

                // Authorize requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()   // allow register/login
                        .requestMatchers("/api/**").permitAll()    // allow all API endpoints for now
                        .anyRequest().authenticated()              // secure everything else
                )

                // No form login or http basic (we'll use JWT later)
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
}

