package com.team.house.housetalk.security;

import com.team.house.housetalk.security.jwt.JwtAuthenticationEntryPoint;
import com.team.house.housetalk.security.jwt.JwtAuthenticationFilter;
import com.team.house.housetalk.security.jwt.JwtProvider;
import com.team.house.housetalk.security.oauth.CustomOAuth2UserService;
import com.team.house.housetalk.security.oauth.OAuthLoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;

    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1️⃣ CSRF 비활성화 (API 서버)
                .csrf(csrf -> csrf.disable())

                // 2️⃣ CORS (프론트 + 쿠키 허용)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.addAllowedOrigin("http://localhost:5173");
                    config.addAllowedOrigin("http://localhost:5174");
                    config.addAllowedMethod("*");
                    config.addAllowedHeader("*");
                    config.setAllowCredentials(true);
                    return config;
                }))

                // ⭐ 3️⃣ OAuth를 위한 세션 허용 (핵심)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // 4️⃣ 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/favicon.ico",
                                "/error",

                                // 🔥 OAuth 필수 허용
                                "/oauth2/**",
                                "/login/success",

                                // 🔥 로그아웃 API 허용 (추가)
                                "/api/auth/logout",

                                // 공개 영역
                                "/tenant/**",
                                "/public/**",

                                "/api/tenant/invites/validate",
                                "/api/tenant/join",
                                "/api/tenant/auth",
                                "/api/tenant/homes",
                                "/api/tenant/me",
                                "/api/tenant/logout",
                                "/api/tenant/buildings/**",


                                "/api/buildings/*/notices",
                                "/api/buildings/*/notices/*"


                        ).permitAll()

                        // 관리자 영역
                        .requestMatchers("/admin/**", "/api/admin/**","/api/buildings/**").authenticated()
                        .anyRequest().authenticated()
                )

                // 5️⃣ 기본 로그인 비활성화
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 6️⃣ OAuth 로그인
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                        .successHandler(oAuthLoginSuccessHandler)
                )

                // 7️⃣ JWT 인증 실패 처리
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                // 8️⃣ JWT 필터
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
