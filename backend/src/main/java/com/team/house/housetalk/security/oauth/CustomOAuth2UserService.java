package com.team.house.housetalk.security.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        // 1️⃣ OAuth 기본 사용자 정보 로딩 (Google API 호출)
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2️⃣ OAuth 제공자 식별자 (google / kakao / naver ...)
        String registrationId =
                userRequest.getClientRegistration().getRegistrationId();

        // 3️⃣ OAuth 제공자별 UserInfo 객체 생성 (하드코딩 제거)
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2UserInfo userInfo;
        try {
            userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                    registrationId,
                    attributes
            );
        } catch (IllegalArgumentException e) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_provider"),
                    e.getMessage()
            );
        }

        // 4️⃣ 필수 값 검증 (도메인 기준)
        if (userInfo.getEmail() == null || userInfo.getEmail().isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_not_found"),
                    "OAuth 계정에서 이메일 정보를 가져올 수 없습니다."
            );
        }

        if (userInfo.getProviderUserId() == null || userInfo.getProviderUserId().isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("provider_id_not_found"),
                    "OAuth 제공자의 사용자 식별자를 가져올 수 없습니다."
            );
        }


        return oAuth2User;
    }
}
