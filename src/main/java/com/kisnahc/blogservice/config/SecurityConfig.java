package com.kisnahc.blogservice.config;

import com.kisnahc.blogservice.auth.CustomAuthenticationEntryPoint;
import com.kisnahc.blogservice.auth.filter.CustomAuthenticationFilter;
import com.kisnahc.blogservice.auth.filter.CustomAuthenticationExceptionFilter;
import com.kisnahc.blogservice.auth.handler.CustomAccessDeniedHandler;
import com.kisnahc.blogservice.auth.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtProvider jwtProvider;


    private static final String[] PERMIT_URL = {
            "/api/auth/sign-in",
            "/api/auth/sign-up"
    };
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(STATELESS)

                .and()
                .authorizeHttpRequests()
                .requestMatchers(PERMIT_URL).permitAll()
                .anyRequest().hasRole("ADMIN")

                .and()
                .addFilterBefore(new CustomAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomAuthenticationExceptionFilter(), CustomAuthenticationFilter.class)
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
