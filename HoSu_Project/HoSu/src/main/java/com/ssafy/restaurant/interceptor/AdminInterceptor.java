package com.ssafy.restaurant.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import com.ssafy.user.dto.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AdminInterceptor implements HandlerInterceptor {
	
	/*
	 * 식당 추가, 삭제, 변경은 관리자만 가능하도록 Interceptor 적용
	 * 로그인 정보를 먼저 확인하고, ** 세션 속 ** 사용자 정보 내 권한이 관리자여야 기능 수행 가능 
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		HttpSession session = request.getSession(false);
		
		String method = request.getMethod(); // GET, POST, PUT, DELETE 등
		
		if(!"GET".equals(method)) {
			// 로그인 정보가 없으면 차단
	        if (session == null || session.getAttribute("loginUser") == null) {
	        	//만약 Vue 프로젝트 진행 시 loginPage url 설정
	        	//response.sendRedirect("/login");
	            return false;
	        }

	        // 로그인은 했는데 관리자가 아닐 때
	        User user = (User) session.getAttribute("loginUser");
	        if (!"ADMIN".equals(user.getRole())) {
	        	//alert  발생시키기
	        	//response.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자만 접근 가능합니다");
	            return false;
	        }
		}
		return true;
	}
	
}
