package dev.opencloud.config;

import dev.opencloud.domain.repository.UserRepository;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  PasswordEncoder encoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  UserDetailsService userDetailsService(UserRepository repo) {
    return username -> repo.findByEmail(username)
        .map(u -> User.withUsername(u.getEmail()).password(u.getPasswordHash()).roles(u.getRole().name()).build())
        .orElseThrow(() -> new UsernameNotFoundException(username));
  }

  @Bean
  SecurityFilterChain filter(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(a -> a
            .requestMatchers("/login", "/css/**", "/js/**", "/api/v1/**", "/grpc/**", "/oauth2/**", "/login/oauth2/**")
            .permitAll()
            .anyRequest().authenticated())
        .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/dashboard", true))
        .oauth2Login(o -> o.loginPage("/login").defaultSuccessUrl("/deployments/new", true))
        .logout(l -> l.logoutSuccessUrl("/login?logout"))
        .csrf(c -> c.ignoringRequestMatchers("/api/v1/**"));
    return http.build();
  }
}