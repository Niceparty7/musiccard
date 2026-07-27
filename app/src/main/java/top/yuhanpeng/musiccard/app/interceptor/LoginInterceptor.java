package top.yuhanpeng.musiccard.app.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import top.yuhanpeng.musiccard.module.utils.JwtUtil;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("sign");
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("未登录");
        }
        try {
            Long userId = JwtUtil.getUserId(token);
            request.setAttribute("user", userId);
        } catch (Exception e) {
            throw new RuntimeException("登录过期");
        }
        return true;
    }
}