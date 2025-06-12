package com.example.signmanager.service;

import com.example.signmanager.domain.User;
import com.example.signmanager.repository.UserRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // 이 클래스를 Spring 서비스 빈으로 등록합니다.
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성합니다 (DI를 위함).
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository; // 사용자 데이터 접근을 위한 리포지토리 주입

    /**
     * Spring Security의 인증 과정에서 사용자 이름(username)을 기반으로
     * 사용자 정보를 로드합니다.
     *
     * @param username 로그인 시도하는 사용자 이름
     * @return Spring Security의 UserDetails 객체 (인증에 필요한 사용자 정보)
     * @throws UsernameNotFoundException 해당 사용자 이름의 사용자를 찾을 수 없을 때 발생
     */
    @Override // UserDetailsService 인터페이스의 메서드 오버라이드
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. UserRepository를 사용하여 데이터베이스에서 사용자 이름으로 User 엔티티를 조회합니다.
        // Optional<User>를 사용하여 null이 반환될 경우를 안전하게 처리합니다.
        User user = userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 2. 조회된 User 엔티티 정보를 Spring Security의 UserDetails 객체로 변환하여 반환합니다.
        // Spring Security의 UserDetails 인터페이스는 getAuthorities() 메서드를 필수적으로 요구합니다.
        // 비록 우리 애플리케이션에서 '역할' 개념을 사용하지 않더라도, 이 인터페이스의 계약을 맞추기 위해
        // 빈 권한 목록(Collections.emptyList())을 제공해야 합니다.
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername()) // 사용자 이름 설정
                .password(user.getPassword()) // 암호화된 비밀번호 설정
                .authorities(Collections.emptyList()) // ⭐ Spring Security의 요구사항을 충족하기 위해 빈 권한 목록을 제공 ⭐
                .build();
    }
}
