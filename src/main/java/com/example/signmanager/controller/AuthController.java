package com.example.signmanager.controller;

import com.example.signmanager.common.response.ApiResponse;
import com.example.signmanager.dto.auth.LoginRequestDto;
import com.example.signmanager.dto.auth.LoginResponseDto;
import com.example.signmanager.dto.auth.UserRegisterRequestDto;
import com.example.signmanager.jwt.JwtUtil;
import com.example.signmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * 사용자 회원가입을 처리하는 API.
     * @param userRegister 회원가입 요청 데이터
     * @return ApiResponse<String> 형태의 성공/실패 메시지
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(@Valid @RequestBody UserRegisterRequestDto userRegister) {

        // 1. 이메일 중복 확인 로직
        if (userService.existsByEmail(userRegister.getEmail())) {
            // 이메일이 이미 사용 중인 경우 (중복)
            return ResponseEntity.status(HttpStatus.CONFLICT) // HTTP 상태 코드 409 (Conflict: 충돌) 반환
                    // ApiResponse의 error 메서드 호출:
                    // HttpStatus.CONFLICT (409), 사용자 메시지, 개발자 에러 코드 ("EMAIL_ALREADY_EXISTS")를 전달합니다.
                    .body(ApiResponse.error(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.", "EMAIL_ALREADY_EXISTS"));
        }

        // 2. 회원가입 처리 로직
        userService.registerUser(userRegister); // UserService를 호출하여 실제 회원가입 비즈니스 로직을 수행합니다.

        // 3. 회원가입 성공 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED) // HTTP 상태 코드 201 (Created: 자원 생성 성공) 반환
                // ApiResponse의 success 메서드 호출:
                // HttpStatus.CREATED (201), 사용자 메시지를 전달합니다. 데이터는 없으므로 Void 타입입니다.
                .body(ApiResponse.success(HttpStatus.CREATED, "회원가입 성공"));
    }

    /**
     * 사용자 로그인을 처리하고 JWT 토큰을 발급하는 API.
     *
     * @param loginRequest 로그인 요청 데이터 (사용자 이름, 비밀번호)
     * @return ApiResponse<LoginResponseDto> 형태의 로그인 성공 시 JWT 토큰 또는 실패 메시지
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            // 1. 사용자 자격 증명(ID/PW)으로 인증 토큰 생성 및 인증 시도
            // authenticationManager가 UserDetailsService와 PasswordEncoder를 사용하여 인증을 수행합니다.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            // 2. 인증 성공 시, Spring Security 컨텍스트에 인증 정보 설정 (선택적이지만 권장)
            // 현재 요청 스레드에서 인증된 사용자 정보를 SecurityContextHolder.getContext().getAuthentication()으로 접근할 수 있게 합니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. JWT 토큰 생성
            // 인증된 사용자의 이름(Principal)을 사용하여 JWT를 생성합니다.
            String jwt = jwtUtil.generateToken(authentication.getName());

            // 4. JWT를 포함한 응답 DTO 생성
            LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                    .accessToken(jwt)
                    .tokenType("Bearer")
                    .build();

            // 5. ApiResponse.success()를 사용하여 JWT DTO와 함께 성공 응답 반환 (HTTP 200 OK)
            return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "로그인 성공!", loginResponseDto));

        } catch (BadCredentialsException e) {
            // 자격 증명이 일치하지 않을 경우 (잘못된 사용자 이름 또는 비밀번호)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED) // HTTP 401 Unauthorized
                    .body(ApiResponse.error(
                            HttpStatus.UNAUTHORIZED, "사용자 이름 또는 비밀번호가 잘못되었습니다.", "AUTH_FAIL")); // 에러 코드 추가
        } catch (Exception e) {
            // 그 외 예상치 못한 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // HTTP 500 Internal Server Error
                    .body(ApiResponse.error(
                            HttpStatus.INTERNAL_SERVER_ERROR, "로그인 중 오류가 발생했습니다.", "SERVER_ERROR")); // 에러 코드 추가
        }
    }
}
