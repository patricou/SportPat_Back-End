package com.pat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by patricou on 5/1/2017.
 */
@Configuration
@EnableSwagger2
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
       registry.addMapping("/api/even")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET","PUT","POST","DELETE","OPTIONS")
       ;//        .allowedHeaders("Content-Type, X-Requested-With, Author, accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers,Authorization,Access-Control-Allow-Origin");
        registry.addMapping("/api/even/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET","PUT","POST","DELETE","OPTIONS")
        ;//       .allowedHeaders("Content-Type, X-Requested-With, Author, accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers,Authorization,Access-Control-Allow-Origin");
        registry.addMapping("/api/memb")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET","PUT","POST","DELETE","OPTIONS")
        ;//       .allowedHeaders("Content-Type, X-Requested-With, Author, accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers,Authorization,Access-Control-Allow-Origin");
        registry.addMapping("/api/memb/*")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET","PUT","POST","DELETE","OPTIONS")
        ;//        .allowedHeaders("Content-Type, X-Requested-With, Author, accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers,Authorization,Access-Control-Allow-Origin");
        registry.addMapping("/api/memb/user")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET","PUT","POST","DELETE","OPTIONS")
        ;//        .allowedHeaders("Content-Type", "X-Requested-With", "Author", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers,Authorization","Access-Control-Allow-Origin");
        registry.addMapping("/api/memb/**")
                .allowedOrigins("http://localhost:4200")
                .allowedMethods("GET","PUT","POST","DELETE","OPTIONS")
        ;//       .allowedHeaders("Content-Type, X-Requested-With, Author, accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers,Authorization,Access-Control-Allow-Origin");
        registry.addMapping("/api/file/**")
                .allowedMethods("GET","PUT","POST","DELETE","OPTIONS")
                .allowedOrigins("http://localhost:4200");
        registry.addMapping("/api/file/*")
                .allowedMethods("GET","PUT","POST","DELETE","OPTIONS")
                .allowedOrigins("http://localhost:4200");
        registry.addMapping("/uploadfile/**")
                .allowedMethods("GET","PUT","POST","DELETE","OPTIONS")
                .allowedOrigins("http://localhost:4200");

    }


}
