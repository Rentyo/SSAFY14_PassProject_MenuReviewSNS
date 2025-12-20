package com.ssafy.local.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

        @Autowired
        private com.ssafy.restaurant.interceptor.JwtInterceptor jwtInterceptor;

        /*
         * CORS 설정 - Vue 프론트엔드 허용
         */
        @Override
        public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 API 경로에 대해
                                .allowedOrigins(
                                                "http://localhost:5173",
                                                "http://localhost:5174",
                                                "http://localhost:5175",
                                                "https://3.39.4.23.sslip.io") // 프로덕션 도메인
                                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메소드
                                .allowedHeaders("*") // 모든 헤더 허용
                                .allowCredentials(true) // 쿠키/인증 정보 허용
                                .maxAge(3600); // preflight 요청 캐시 시간 (1시간)
        }

        /*
         * GET을 제외한 POST, PUT, DELETE에 Interceptor 추가
         */
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(jwtInterceptor)
                                .addPathPatterns("/**") // 모든 경로에 대해 적용
                                .excludePathPatterns(
                                                "/api/users/**",
                                                "/api/menus/**", // 메뉴 조회 API는 인증 불필요
                                                "/swagger-ui/**",
                                                "/v3/api-docs/**",
                                                "/api/restaurants/search",
                                                "/api/search/**", // 검색 API는 인증 불필요
                                                "/api/ai/**",
                                                "/api/restaurants/**",
                                                "/api/aisns/**",
                                                "/api/admin/**"); // 식당 조회 API는 인증 불필요
        }
}