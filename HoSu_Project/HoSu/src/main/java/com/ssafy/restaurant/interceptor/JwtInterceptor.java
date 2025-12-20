package com.ssafy.restaurant.interceptor;

import com.ssafy.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                jwtUtil.validateToken(token);
                // 토큰에서 정보 추출하여 request에 저장 (선택사항)
                request.setAttribute("userNo", jwtUtil.getUserNoFromToken(token));
                request.setAttribute("role", jwtUtil.getRoleFromToken(token));
                return true;
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                return false;
            }
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        return false;
    }
}
