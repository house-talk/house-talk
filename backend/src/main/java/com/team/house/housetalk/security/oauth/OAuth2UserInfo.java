package com.team.house.housetalk.security.oauth;

import java.util.Map;

public interface OAuth2UserInfo {

    String getProvider();        // google
    String getProviderUserId();  // sub
    String getEmail();
    String getName();
}
