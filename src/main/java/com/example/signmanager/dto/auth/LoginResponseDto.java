package com.example.signmanager.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder // 빌더 패턴을 사용하여 객체 생성을 용이하게 합니다.
public class LoginResponseDto {
    private String accessToken; // 발급된 JWT 토큰

    @Builder.Default
    private String tokenType = "Bearer"; // 토큰 타입 (JWT의 경우 일반적으로 "Bearer")
}
