package in.lms.sinchan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import in.lms.sinchan.interceptor.MultiTenancyInterceptorHandler;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("-----Inside WebMvcConfig Class, addInterceptor method----");
       // registry.addInterceptor(new MultiTenancyInterceptorHandler());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("-----Inside WebMvcConfig Class, addMappings method----");
        registry.addMapping("/**");
    }
}
