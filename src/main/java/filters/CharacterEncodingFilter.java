package filters;

import javax.servlet.*;
import java.io.IOException;

/**
 * Filtro para configurar encoding UTF-8 en todas las requests/responses
 * Asegura que los caracteres especiales se manejen correctamente
 */
public class CharacterEncodingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización del filtro
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // Configurar encoding para request y response
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // Solo establecer content type para HTML, no para JSON
        String requestURI = ((javax.servlet.http.HttpServletRequest) request).getRequestURI();
        if (!requestURI.startsWith("/api/")) {
            response.setContentType("text/html; charset=UTF-8");
        }
        
        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Limpieza del filtro
    }
}
