package com.test.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
        .authorizeHttpRequests(requests -> requests.anyRequest().authenticated());
    return http.build();

  }

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails user = User.withDefaultPasswordEncoder()
        .username(userUsername)
        .password(userPassword)
        .roles("USER")
        .build();

    UserDetails admin = User.withDefaultPasswordEncoder()
        .username(adminUsername)
        .password(adminPassword)
        .roles("ADMIN")
        .build();

    return new InMemoryUserDetailsManager(user, admin);
  }
}
