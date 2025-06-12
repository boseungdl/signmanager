package com.example.signmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // ⭐ PasswordEncoder 빈을 정의하는 메서드 ⭐
    @Bean // 이 메서드가 반환하는 객체를 Spring 빈으로 등록합니다.
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder는 Spring Security에서 제공하는 비밀번호 암호화 구현체입니다.
        // 강력한 해싱 알고리즘을 사용하여 비밀번호를 안전하게 저장할 수 있게 합니다.
        return new BCryptPasswordEncoder();
    }

    // 인증 매니저(AuthenticationManager) 빈 등록
    // Spring Security의 인증을 처리하는 핵심 객체입니다.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // ⭐ 폼 로그인과 HTTP Basic 인증 비활성화 ⭐
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // ⭐ /h2-console/** 부분 제거 ⭐
                        .requestMatchers("/api/v1/**")
                        .permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest()
                        .authenticated());

        return http.build();
    }
}
