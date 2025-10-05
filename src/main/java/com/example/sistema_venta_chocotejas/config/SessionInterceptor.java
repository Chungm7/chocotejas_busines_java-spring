package com.example.sistema_venta_chocotejas.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// @Component: Marca esta clase como un componente de Spring. Esto permite que Spring
// la detecte automáticamente y la gestione, por ejemplo, para poder inyectarla en WebConfig.
@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String requestURI = request.getRequestURI();

        // DEBUG: Para ver qué rutas están siendo interceptadas
        System.out.println("Interceptando: " + requestURI);

        // Si es una ruta pública, permitir siempre
        if (requestURI.startsWith("/tienda") ||
                requestURI.equals("/login") ||
                requestURI.startsWith("/css/") ||
                requestURI.startsWith("/js/") ||
                requestURI.startsWith("/images/") ||
                requestURI.startsWith("/imagenes/") ||
                requestURI.startsWith("/logos/") ||
                requestURI.startsWith("/sliders/")) {
            return true;
        }

        // Para rutas de gestión, verificar sesión
        if (requestURI.startsWith("/gestion/")) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuarioLogueado") == null) {
                response.sendRedirect("/login");
                return false;
            }
        }

        return true;
    }
}