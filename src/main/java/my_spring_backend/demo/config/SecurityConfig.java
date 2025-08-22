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
                .csrf(csrf -> csrf.disable()) // disable CSRF for APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // allow register/login APIs
                        .anyRequest().authenticated() // secure everything else
                )
                .formLogin(form -> form.disable()) // disable default login form
                .httpBasic(httpBasic -> httpBasic.disable()); // disable basic auth

        return http.build();
    }
}

