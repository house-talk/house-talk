package com.team.house.housetalk.security.jwt;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 서명에 사용할 비밀 키
     * (application.yml → 환경변수에서 주입)
     */
    private String secretKey;

    /**
     * Access Token 만료 시간 (ms)
     */
    private long accessTokenValidityInMillis;

    /**
     * Spring이 application.yml 값을 주입하기 위한 setter
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setAccessTokenValidityInMillis(long accessTokenValidityInMillis) {
        this.accessTokenValidityInMillis = accessTokenValidityInMillis;
    }
}
