package com.team.house.housetalk.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey secretKey;

    private final long accessTokenValidityInMillis;

    public JwtProvider(
            JwtProperties jwtProperties
    ) {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
        this.accessTokenValidityInMillis = jwtProperties.getAccessTokenValidityInMillis();
    }


    public String generateAccessToken(Long adminId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidityInMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(adminId)) // adminId를 subject로
                .claim("role", "ADMIN")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 유효성 검증
     * - 서명 위조
     * - 만료 여부
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 토큰에서 adminId 추출
     * - validateToken 이후 사용 권장
     */
    public Long getAdminId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.valueOf(claims.getSubject());
    }

    /**
     * 토큰에서 role 추출 (확장 대비)
     */
    public String getRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("role", String.class);
    }
}
