package com.project.system.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationFailureHandler failureHandler;

    @Autowired
    private UserDetailsServiceImpl usuarioUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .authorizeHttpRequests(authz -> authz
    	    .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/error", "/accessDenied").permitAll()
    	    .requestMatchers("/users/**").authenticated()
    	    .requestMatchers("/input/user/**").hasRole("USER")
    	    .requestMatchers("/input/admin/**").hasRole("ADMIN")
    	    .requestMatchers("/input/director/**").hasRole("DIRECTOR")
    	    .requestMatchers("/input/manager/**").hasRole("MANAGER")
    	    .anyRequest().authenticated()
    	)

        .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/home", true)
            .failureHandler(failureHandler)
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout=true")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
        )
        .sessionManagement(session -> session
            .invalidSessionUrl("/login?expired=true")
        )
        .exceptionHandling(ex -> ex
            .accessDeniedPage("/accessDenied")
        )
        .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "multiAuthenticationManager")
    public AuthenticationManager multiAuthenticationManager(HttpSecurity http) throws Exception {
        DaoAuthenticationProvider usuarioProvider = new DaoAuthenticationProvider();
        usuarioProvider.setUserDetailsService(usuarioUserDetailsService);
        usuarioProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(usuarioProvider);
    }

    // Custom HttpFirewall para permitir barras duplas nas URLs
    @Bean
    public HttpFirewall allowDoubleSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedDoubleSlash(true);  // Permite barras duplas codificadas (%2F)
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowSemicolon(true);
        return firewall;
    }

    // Configura o firewall customizado no Spring Security
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(HttpFirewall allowDoubleSlashHttpFirewall) {
        return (web) -> web.httpFirewall(allowDoubleSlashHttpFirewall);
    }
}
