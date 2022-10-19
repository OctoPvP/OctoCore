package net.octopvp.aetheriacoremaster.config;

import java.util.List;

import net.octopvp.aetheriacoremaster.core.CurrentUserHandlerMethodArgumentResolver;
import net.octopvp.aetheriacoremaster.interceptor.APIKeyInterceptor;
import net.octopvp.aetheriacoremaster.interceptor.RevokedTokenInterceptor;
import net.octopvp.aetheriacoremaster.interceptor.ModelInterceptor;
import net.octopvp.aetheriacoremaster.repositories.APIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableScheduling
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    CurrentUserHandlerMethodArgumentResolver.UserArgResolver userArgResolver;
    @Autowired
    CurrentUserHandlerMethodArgumentResolver.UserDetailsImplArgResolver userDetailsImplArgResolver;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //registry.addViewController("/").setViewName("pages/login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/static/**")) {
            registry.addResourceHandler("/static/**")
                    .addResourceLocations("/static/");
        }
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgResolver);
        argumentResolvers.add(userDetailsImplArgResolver);
    }

    @Autowired
    private APIRepository repository;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ModelInterceptor());
        registry.addInterceptor(new RevokedTokenInterceptor());
        registry.addInterceptor(new APIKeyInterceptor(repository));
    }
}
