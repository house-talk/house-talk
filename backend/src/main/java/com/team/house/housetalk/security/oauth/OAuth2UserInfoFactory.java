package com.team.house.housetalk.security.oauth;

import java.util.Map;

public class OAuth2UserInfoFactory {

    private OAuth2UserInfoFactory() {
        // 유틸 클래스이므로 생성자 private
    }

    public static OAuth2UserInfo getOAuth2UserInfo(
            String registrationId,
            Map<String, Object> attributes
    ) {

        if ("google".equalsIgnoreCase(registrationId)) {
            return new GoogleOAuth2UserInfo(attributes);
        }

        throw new IllegalArgumentException(
                "Unsupported OAuth2 provider: " + registrationId
        );
    }
}
