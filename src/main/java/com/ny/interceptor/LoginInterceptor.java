package com.ny.interceptor;

import com.ny.until.JwtUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 请求拦截器（验证登录Token，手动放行登录/注册接口）
 * @author MI
 * @date 2023/10/03
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUntil jwtUtil; // 注入你的JWT工具类（已实现完整逻辑）
    
    // 配置是否严格验证Redis中的Token（用于降级方案）
    @Value("${TOKEN_STRICT_REDIS_CHECK:true}")
    private boolean tokenStrictRedisCheck;

    // 请求处理前拦截（核心逻辑：先放行登录/注册，再验证其他接口Token）
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取当前请求路径（不含参数，如 "/login"）
        String requestUri = request.getRequestURI();
        log.info("拦截到请求：{}", requestUri);

        // 2. 手动放行 /login 和 /register 接口（无需验证Token）
        if ("/login".equals(requestUri) || "/register".equals(requestUri) || "/error".equals(requestUri)) {
            log.info("请求 {} 为放行接口，直接放行", requestUri);
            return true;
        }

        // 3. 其他接口：验证Token是否有效
        String token = request.getHeader("token");
        log.debug("当前请求头中的Token：{}", token);

        // 验证Token存在且合法（调用你已实现的 jwtUtil.validateToken 方法）
        if (token != null && jwtUtil.validateToken(token)) {
            try {
                // 从Token中提取用户名
                String username = jwtUtil.getUsernameFromToken(token);
                if (username != null) {
                    // 根据配置决定是否严格检查Redis中的Token
                    if (!tokenStrictRedisCheck) {
                        // 非严格模式下，只验证Token本身的有效性
                        log.info("非严格模式：Token验证通过，放行请求：{}", requestUri);
                        return true;
                    }
                    
                    // 严格模式：验证Redis或本地缓存中是否存在该Token
                    String storedToken = jwtUtil.getToken(username);
                    if (storedToken != null && token.equals(storedToken)) {
                        log.info("Token验证通过（存储中存在有效Token），放行请求：{}", requestUri);
                        return true;
                    } else if (storedToken == null) {
                        // 如果存储的Token为null，可能是Redis不可用但使用了本地缓存
                        // 允许通过，因为JWT本身已经验证通过
                        log.warn("未在存储中找到Token，但JWT验证通过，放行请求：{}", requestUri);
                        return true;
                    } else {
                        log.warn("Token已失效（存储中Token不匹配），拒绝请求：{}", requestUri);
                    }
                } else {
                    log.warn("无法从Token中提取用户名，拒绝请求：{}", requestUri);
                }
            } catch (Exception e) {
                // 发生异常时，为了保持系统可用性，允许基于JWT本身的验证通过
                log.error("Token验证过程中发生异常：{}", e.getMessage());
                log.warn("降级处理：基于JWT本身验证通过，放行请求：{}", requestUri);
                return true;
            }
        } else {
            log.warn("Token无效或未携带Token，拒绝请求：{}", requestUri);
        }

        // Token无效/不存在/已注销：返回401未授权
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401状态码
        response.setContentType("text/plain;charset=UTF-8"); // 响应编码（避免中文乱码）
        response.getWriter().write("100,还未登录"); // 自定义响应信息
        return false;
    }

    // 以下方法为拦截器默认方法，保持空实现即可（无需修改）
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}