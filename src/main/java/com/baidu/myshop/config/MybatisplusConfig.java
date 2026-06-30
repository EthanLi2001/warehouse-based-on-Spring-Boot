package com.baidu.myshop.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisplusConfig {
    @Bean
    public MybatisPlusInterceptor getInfo(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor innerInterceptor= new PaginationInnerInterceptor();
        interceptor.addInnerInterceptor(innerInterceptor);
        return interceptor;
    }
}
