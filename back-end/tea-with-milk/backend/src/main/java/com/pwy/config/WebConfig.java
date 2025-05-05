package com.pwy.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.pwy.interceptor.AdminInterceptor;
import com.pwy.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); // 如果配置多个插件, 切记分页最后添加
        // 如果有多数据源可以不配具体类型, 否则都建议配上具体的 DbType
        return interceptor;
    }
    //查找加有ServiceEndpoint注解的Bean
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).excludePathPatterns("/employee/login").order(0);
        registry.addInterceptor(new AdminInterceptor()).
                addPathPatterns("/employee/list","/employee/add","/employee/del/**",
                        "/shop/update","/shop/password","/voucher",
                        "/goodscategory/add","/goodscategory/status/**","/goodscategory/update",
                "/sku/**","/dimension/add","/dimension/update","/dimension/delete/**","/sku/add","/sku/update",
                        "/goods/add","/goods/update","/goods/delete","/goods/status/**").order(1);
    }
}
