package com.pwy.interceptor;


import cn.hutool.core.bean.BeanUtil;
import com.pwy.entity.vo.EmployeeVo;
import com.pwy.utils.JWTUtils;
import com.pwy.utils.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String URI = request.getRequestURI();
        log.info("拦截到请求: {}",URI);
        // 处理预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
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
        Map<String, Object> employeeInfo = JWTUtils.getUserInfo(token);
        EmployeeVo employeeVo = BeanUtil.fillBeanWithMap(employeeInfo, new EmployeeVo(), false);
        ThreadLocalUtils.set(employeeVo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        ThreadLocalUtils.remove();
    }
}
