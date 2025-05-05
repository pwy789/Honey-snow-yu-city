package com.pwy.interceptor;

import com.pwy.utils.JWTUtils;
import com.pwy.utils.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("拦截到请求: {}",request.getRequestURI());
        String token = request.getHeader("authorization");
        if(token==null||token.equals("")){
            response.setStatus(401);
            return false;
        }
        boolean isEffective = JWTUtils.verify(token);
        if(!isEffective){
            //无效
            response.setStatus(401);
            return false;
        }
        //有效
        Map<String, Object> userInfo = JWTUtils.getUserInfo(token);
        Object userId = userInfo.get("userId");
        //存入threadLocal
        ThreadLocalUtils.set(userId);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
       ThreadLocalUtils.remove();
    }
}
