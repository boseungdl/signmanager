package com.example.signmanager.service;

import com.example.signmanager.domain.User;
import com.example.signmanager.dto.auth.UserRegisterRequestDto;
import com.example.signmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service // 이 클래스가 비즈니스 로직을 담당하는 서비스 컴포넌트임을 Spring에게 알립니다.
@RequiredArgsConstructor // 'final'로 선언된 필드(userRepository, passwordEncoder)를 초기화하는 생성자를 자동으로 만들어 줍니다.
public class UserService {

    private final UserRepository userRepository; // UserRepository를 주입받아 데이터베이스 접근에 사용합니다.
    private final PasswordEncoder passwordEncoder; // ⭐ 비밀번호 암호화를 위해 PasswordEncoder를 주입받습니다.
    // (실제 프로젝트에서는 Spring Security 설정을 통해 Bean으로 등록해야 합니다.)

    /**
     * 이메일 중복 여부를 확인합니다.
     * @param email 확인할 이메일 주소
     * @return 해당 이메일을 가진 사용자가 존재하면 true, 없으면 false
     */
    @Transactional(readOnly = true) // 데이터 변경 없이 읽기만 하는 트랜잭션을 설정 (성능 최적화)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 새로운 사용자를 등록합니다.
     * @param request 회원가입 요청 정보 (UserRegisterRequest DTO)
     */
    @Transactional // 이 메서드 내의 모든 데이터베이스 작업이 하나의 트랜잭션으로 묶이도록 합니다.
    // 메서드 실행 중 예외 발생 시 모든 변경사항이 롤백(취소)됩니다.
    public void registerUser(UserRegisterRequestDto request) {
        // 1. 비밀번호 암호화
        // ⭐ 실제 비밀번호는 절대로 평문으로 저장해서는 안 됩니다.
        // Spring Security의 PasswordEncoder를 사용하여 비밀번호를 암호화합니다.
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 2. User 엔티티 생성
        // UserRegisterRequest DTO의 정보를 바탕으로 User 엔티티를 빌더 패턴을 사용하여 생성합니다.
        User newUser = User.builder()
                .email(request.getEmail())
                .password(encodedPassword) // 암호화된 비밀번호 저장
                .username(request.getUsername())
                .build();

        // 3. 데이터베이스에 저장
        userRepository.save(newUser); // UserRepository를 통해 새로운 User 엔티티를 데이터베이스에 저장합니다.
    }
}
