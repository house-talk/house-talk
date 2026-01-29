package com.team.house.housetalk.security.oauth;

import java.util.Map;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * OAuth 제공자 이름
     */
    @Override
    public String getProvider() {
        return "google";
    }

    /**
     * Google OAuth 고유 사용자 ID
     * - Google에서는 "sub" 필드가 유니크 식별자
     */
    @Override
    public String getProviderUserId() {
        return (String) attributes.get("sub");
    }

    /**
     * 사용자 이메일
     */
    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    /**
     * 사용자 이름
     */
    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
