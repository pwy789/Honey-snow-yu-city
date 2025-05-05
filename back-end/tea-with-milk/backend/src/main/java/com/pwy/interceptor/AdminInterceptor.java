package com.pwy.interceptor;

import com.pwy.entity.vo.EmployeeVo;
import com.pwy.enums.EmployeeIdentifyEnum;
import com.pwy.utils.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Slf4j
public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String URI = request.getRequestURI();
        log.info("管理员拦截器拦截到请求：{}",URI);
        EmployeeVo ev =(EmployeeVo) ThreadLocalUtils.get();
        if(!Objects.equals(ev.getIdentify(), EmployeeIdentifyEnum.ADMIN.getCode())){
             //说明不是管理员
            response.setStatus(403);
            return false;
        }
        return true;
    }


}
