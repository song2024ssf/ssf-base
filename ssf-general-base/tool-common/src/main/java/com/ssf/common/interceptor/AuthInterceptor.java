package com.ssf.common.interceptor;

import com.ssf.common.annotation.IgnoreAuth;
import com.ssf.common.context.UserContextHolder;
import com.ssf.common.model.LoginUser;
import com.ssf.common.security.JwtProperties;
import com.ssf.common.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果不是 Controller 方法，直接放行（如静态资源）
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 检查类或方法上是否有 @IgnoreAuth 注解
        if (handlerMethod.getMethodAnnotation(IgnoreAuth.class) != null ||
            handlerMethod.getBeanType().isAnnotationPresent(IgnoreAuth.class)) {
            return true;
        }

        // 提取 Token
        String token = extractToken(request);
        if (!StringUtils.hasText(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.warn("请求未携带 Token，URL: {}", request.getRequestURI());
            return false;
        }

        // 校验 Token
        try {
            jwtUtils.validateToken(token);
            LoginUser user = jwtUtils.getLoginUserFromToken(token);
            UserContextHolder.setUser(user);
            return true;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            log.warn("Token 校验失败，URL: {}, 原因: {}", request.getRequestURI(), e.getMessage());
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 请求结束后清理 ThreadLocal，防止内存泄漏
        UserContextHolder.clear();
    }

    /**
     * 从请求头中提取 Token（去除 Bearer 前缀）
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(jwtProperties.getHeader());
        if (StringUtils.hasText(header) && header.startsWith(jwtProperties.getTokenPrefix())) {
            return header.substring(jwtProperties.getTokenPrefix().length()).trim();
        }
        return null;
    }
}