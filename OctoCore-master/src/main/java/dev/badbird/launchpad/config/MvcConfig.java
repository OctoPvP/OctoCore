package dev.badbird.launchpad.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.badbird.launchpad.component.CurrentUserHandlerMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableScheduling
public class MvcConfig implements WebMvcConfigurer { // TODO: Allow plugins to configure this

    @Autowired
    private CurrentUserHandlerMethodArgumentResolver userArgResolver;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //registry.addViewController("/").setViewName("pages/login");
    }

    @Bean
    public Gson gsonBean() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!registry.hasMappingForPattern("/static/**")) {
            registry.addResourceHandler("/static/**")
                    .addResourceLocations("/static/")
                    .addResourceLocations("/dist");
        }
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(new RevokedTokenInterceptor());
    }
}
