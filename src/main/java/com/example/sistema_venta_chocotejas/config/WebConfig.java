package com.example.sistema_venta_chocotejas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration: Indica que esta clase es una fuente de configuración para la aplicación.
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Inyección de dependencia del interceptor de sesión. Spring nos proporciona la
    // instancia.
    private final SessionInterceptor sessionInterceptor;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.logo-dir}")
    private String logoDir;

    // Constructor para la inyección de dependencias.
    public WebConfig(SessionInterceptor sessionInterceptor) {
        this.sessionInterceptor = sessionInterceptor;
    }

    // Este método se usa para configurar cómo se sirven los recursos estáticos
    // (CSS, JS, imágenes).
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Le dice a Spring que cualquier petición que empiece con /css/**
        // debe buscar archivos en la carpeta 'classpath:/static/css/'.
        // 'classpath:' se refiere a la carpeta 'src/main/resources'.
        // setCachePeriod(0) deshabilita el caché en el navegador, útil durante el
        // desarrollo.
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCachePeriod(0);

        // Lo mismo para los archivos JavaScript.
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCachePeriod(0);

        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(0);

        // 👇 NUEVO: mapeo para imágenes subidas dinámicamente
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations("file:" + uploadDir);
        // 👇 NUEVO: mapeo para imágenes subidas dinámicamente
        registry.addResourceHandler("/logos/**")
                .addResourceLocations("file:" + logoDir);
    }

    // Este método se usa para registrar interceptores.
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/gestion/**") // SOLO protege las rutas de gestión
                .excludePathPatterns(
                        "/login",
                        "/logout",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/imagenes/**",
                        "/logos/**",
                        "/error",
                        "/favicon.ico"
                );
    }

    // Configura CORS (Cross-Origin Resource Sharing). Es necesario si tu frontend y
    // backend
    // estuvieran en dominios diferentes. En este caso, es una buena práctica para
    // las APIs.
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        // Configuración CORS para APIs
        registry.addMapping("/usuarios/api/**") // Aplica CORS solo a las rutas de la API de usuarios
                .allowedOrigins("http://localhost:8083")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // Permite el envío de cookies (importante para sesiones).
    }
}