package top.yuhanpeng.musiccard.console.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new RuntimeException("未登录或登录已过期");
        }
        Object user = session.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("请重新登录");
        }
        return true;
    }
}