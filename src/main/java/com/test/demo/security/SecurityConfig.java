package com.test.demo.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${hub.user.role.username}")
  private String userUsername;

  @Value("${hub.user.role.password}")
  private String userPassword;

  @Value("${hub.admin.role.username}")
  private String adminUsername;

  @Value("${hub.admin.role.password}")
  private String adminPassword;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(requests -> requests
//            .requestMatchers("/api/v1/products/**").hasRole("ADMIN")
            .anyRequest().authenticated())
        .httpBasic(withDefaults());
    return http.build();

  }

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails user = User.builder()
        .username(userUsername)
        .password(passwordEncoder().encode(userPassword))
        .roles("USER")
        .build();

    UserDetails admin = User.builder()
        .username(adminUsername)
        .password(passwordEncoder().encode(adminPassword))
        .roles("ADMIN")
        .build();

    return new InMemoryUserDetailsManager(user, admin);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
