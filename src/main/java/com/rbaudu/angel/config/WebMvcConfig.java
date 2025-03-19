package com.rbaudu.angel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Configuration Spring MVC pour l'application.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Ajoute les intercepteurs à la chaîne de traitement des requêtes.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ThymeleafInterceptor());
    }
    
    /**
     * Intercepteur qui ajoute des variables communes aux templates Thymeleaf.
     */
    private static class ThymeleafInterceptor implements HandlerInterceptor {
        
        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                              Object handler, ModelAndView modelAndView) {
            if (modelAndView != null) {
                // Ajouter l'objet HttpServletRequest au modèle pour les templates Thymeleaf
                modelAndView.addObject("httpServletRequest", request);
            }
        }
    }
}
