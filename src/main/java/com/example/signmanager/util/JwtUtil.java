package com.example.signmanager.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-time-ms}")
    private long expirationTimeMs;

    private Key key;

    /**
     * 애플리케이션 시작 시 JWT 비밀 키를 초기화합니다.
     * Base64 인코딩된 secretKey 문자열을 실제 JWT 서명에 사용될 Key 객체로 변환합니다.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    /**
     * JWT 토큰 생성
     *
     * 주어진 사용자 이름(username)을 기반으로 JWT 토큰을 생성합니다.
     * 토큰에는 주체(Subject), 발행 시간, 만료 시간이 포함되며,
     * 설정된 비밀 키와 HS256 알고리즘으로 서명됩니다.
     *
     * @param username 토큰의 주체가 될 사용자 이름 (예: 로그인 ID)
     * @return 생성된 JWT 문자열
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTimeMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 토큰 유효성 검증
     *
     * 주어진 JWT 토큰의 서명, 만료 여부 등을 검증합니다.
     * 유효하지 않은 토큰인 경우 각 예외 유형에 따라 로그를 남깁니다.
     *
     * @param token 검증할 JWT 문자열
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.error("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            logger.error("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            logger.error("지원되지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT 토큰이 잘못되었습니다.", e);
        }
        return false;
    }

    /**
     * JWT 토큰에서 사용자 이름 추출
     *
     * 유효한 JWT 토큰으로부터 주체(Subject) 클레임에 해당하는 사용자 이름을 추출합니다.
     * 이 메서드는 토큰이 이미 유효하다고 가정하고 호출되어야 합니다.
     *
     * @param token 사용자 이름을 추출할 JWT 문자열
     * @return 토큰에서 추출된 사용자 이름
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
