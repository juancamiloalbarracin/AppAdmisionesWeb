package api;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import repository.RepositoryFactory;
import repository.UsuarioRepository;
import utils.DatabaseConnection;
import models.Usuario;
import utils.JwtUtil;

/**
 * AuthApiServlet - API REST PROPIA para autenticación
 * MIGRA: LoginServlet.java a endpoints JSON
 * PRESERVA: Toda la lógica de validación original
 */
@WebServlet("/api/auth/*")
public class AuthApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        switch (pathInfo) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/logout":
                handleLogout(request, response);
                break;
            case "/validate":
                handleValidateSession(request, response);
                break;
            default:
                sendErrorResponse(response, 404, "Endpoint no encontrado");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        if ("/profile".equals(pathInfo)) {
            handleGetProfile(request, response);
        } else {
            sendErrorResponse(response, 404, "Endpoint no encontrado");
        }
    }

    /**
     * API PROPIA: POST /api/auth/login
     * PRESERVA: Lógica exacta del LoginServlet original
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            // Leer JSON del request
            LoginRequest loginReq = objectMapper.readValue(request.getReader(), LoginRequest.class);
            
            // PRESERVADO: Validación básica igual que en LoginServlet
            if (loginReq.getEmail() == null || loginReq.getEmail().trim().isEmpty() || 
                loginReq.getPassword() == null || loginReq.getPassword().trim().isEmpty()) {
                sendErrorResponse(response, 400, "Por favor, complete todos los campos.");
                return;
            }
            
            // PRESERVADO: Exactamente la misma lógica de validación del LoginServlet original
            UsuarioRepository usuarioRepo = RepositoryFactory.getUsuarioRepository();
            boolean valido = usuarioRepo.validateLogin(loginReq.getEmail().trim(), loginReq.getPassword());
            
            if (valido) {
                // PRESERVADO: Guardar email de sesión
                DatabaseConnection.setCurrentUserEmail(loginReq.getEmail().trim());
                
                // Obtener datos completos del usuario
                Usuario usuario = usuarioRepo.findByEmail(loginReq.getEmail().trim());
                
                // Crear sesión HTTP (mantenemos compatibilidad)
                HttpSession session = request.getSession(true);
                session.setAttribute("userEmail", loginReq.getEmail().trim());
                session.setAttribute("usuario", usuario);
                
                // PRESERVADO: Configurar duración según preferencias
                if (loginReq.isRecordarme()) {
                    session.setMaxInactiveInterval(60 * 60 * 24 * 7); // 7 días
                } else {
                    session.setMaxInactiveInterval(60 * 30); // 30 minutos
                }
                
                if (loginReq.isPcPublico()) {
                    session.setAttribute("pcPublico", true);
                    session.setMaxInactiveInterval(60 * 15); // 15 minutos para PC público
                }
                
                // NUEVO: Generar JWT token propio
                String token = JwtUtil.generateToken(usuario.getEmail(), usuario.getNombre());
                
                // No hay conexión directa aquí; repos se encargan.
                
                // Respuesta JSON exitosa
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "¡Bienvenido!");
                responseData.put("token", token);
                responseData.put("sessionId", session.getId());
                responseData.put("user", createUserResponse(usuario));
                
                response.setStatus(200);
                response.getWriter().write(objectMapper.writeValueAsString(responseData));
                
            } else {
                // PRESERVADO: Mismo mensaje de error que en LoginServlet
                sendErrorResponse(response, 401, "Usuario o contraseña incorrectos.");
            }
            
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * API PROPIA: POST /api/auth/logout
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("message", "Sesión cerrada exitosamente");
        
        response.setStatus(200);
        response.getWriter().write(objectMapper.writeValueAsString(responseData));
    }

    /**
     * API PROPIA: POST /api/auth/validate
     */
    private void handleValidateSession(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        boolean isValid = session != null && session.getAttribute("userEmail") != null;
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("valid", isValid);
        
        if (isValid) {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            responseData.put("user", createUserResponse(usuario));
        }
        
        response.setStatus(200);
        response.getWriter().write(objectMapper.writeValueAsString(responseData));
    }

    /**
     * API PROPIA: GET /api/auth/profile
     */
    private void handleGetProfile(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            sendErrorResponse(response, 401, "Sesión no válida");
            return;
        }
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        responseData.put("user", createUserResponse(usuario));
        
        response.setStatus(200);
        response.getWriter().write(objectMapper.writeValueAsString(responseData));
    }
    
    private Map<String, Object> createUserResponse(Usuario usuario) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("email", usuario.getEmail());
        userResponse.put("nombres", usuario.getNombre());
        userResponse.put("apellidos", usuario.getApellido());
        userResponse.put("username", usuario.getUsername());
        return userResponse;
    }
    
    private void sendErrorResponse(HttpServletResponse response, int status, String message) 
            throws IOException {
        response.setStatus(status);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
    
    // Clase interna para deserializar JSON
    public static class LoginRequest {
        private String email;
        private String password;
        private boolean recordarme;
        private boolean pcPublico;
        
        // Getters y setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public boolean isRecordarme() { return recordarme; }
        public void setRecordarme(boolean recordarme) { this.recordarme = recordarme; }
        public boolean isPcPublico() { return pcPublico; }
        public void setPcPublico(boolean pcPublico) { this.pcPublico = pcPublico; }
    }
}
