package api;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import repository.RepositoryFactory;
import repository.UsuarioRepository;
import models.Usuario;

/**
 * UserApiServlet - API REST para gestión de usuarios
 * Endpoints: /api/users/register, /api/users/profile
 */
@WebServlet("/api/users/*")
public class UserApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        switch (pathInfo) {
            case "/register":
                handleRegister(request, response);
                break;
            default:
                sendErrorResponse(response, 404, "Endpoint no encontrado");
        }
    }

    /**
     * API: POST /api/users/register
     * Registra un nuevo usuario
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            // Configurar charset antes de leer el JSON
            request.setCharacterEncoding("UTF-8");
            
            // Leer JSON del request con manejo de errores mejorado
            RegisterRequest registerReq;
            try {
                registerReq = objectMapper.readValue(request.getReader(), RegisterRequest.class);
            } catch (Exception jsonException) {
                sendErrorResponse(response, 400, "Error al procesar JSON: " + jsonException.getMessage());
                return;
            }
            
            // Validaciones básicas
            String validationError = validateRegisterRequest(registerReq);
            if (validationError != null) {
                sendErrorResponse(response, 400, validationError);
                return;
            }
            
            // Crear objeto Usuario con los datos básicos
            String nombreCompleto = registerReq.getNombres();
            Usuario usuario = new Usuario(nombreCompleto, registerReq.getApellidos(), 
                    registerReq.getEmail(), registerReq.getEmail(), registerReq.getPassword());
            
            // Insertar en base de datos via repositorio
            UsuarioRepository usuarioRepo = RepositoryFactory.getUsuarioRepository();
            String resultado = usuarioRepo.create(usuario);
            
            if ("EXITO".equals(resultado)) {
                // Respuesta exitosa
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("message", "Usuario registrado correctamente");
                responseData.put("email", registerReq.getEmail());
                
                response.setStatus(201);
                response.getWriter().write(objectMapper.writeValueAsString(responseData));
                
            } else {
                // Manejo de errores específicos
                String mensajeError = getErrorMessage(resultado);
                sendErrorResponse(response, 400, mensajeError);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Validar datos de registro
     */
    private String validateRegisterRequest(RegisterRequest request) {
        if (request.getNombres() == null || request.getNombres().trim().isEmpty()) {
            return "Los nombres son obligatorios";
        }
        if (request.getApellidos() == null || request.getApellidos().trim().isEmpty()) {
            return "Los apellidos son obligatorios";
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return "El correo electrónico es obligatorio";
        }
        if (!isValidEmail(request.getEmail())) {
            return "El formato del correo electrónico no es válido";
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres";
        }
        if (request.getTipoDocumento() == null || request.getTipoDocumento().trim().isEmpty()) {
            return "El tipo de documento es obligatorio";
        }
        if (request.getNumeroDocumento() == null || request.getNumeroDocumento().trim().isEmpty()) {
            return "El número de documento es obligatorio";
        }
        if (request.getTelefono() == null || request.getTelefono().trim().isEmpty()) {
            return "El teléfono es obligatorio";
        }
        if (!request.getTelefono().matches("\\d{10}")) {
            return "El teléfono debe tener 10 dígitos";
        }
        
        return null; // Sin errores
    }

    /**
     * Validar formato de email
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Convertir códigos de error a mensajes legibles
     */
    private String getErrorMessage(String codigo) {
        switch (codigo) {
            case "ERROR_CONEXION":
                return "Error de conexión con la base de datos. Intente nuevamente.";
            case "ERROR_EMAIL_DUPLICADO":
                return "El correo electrónico ya está registrado. Use un correo diferente.";
            case "ERROR_USERNAME_DUPLICADO":
                return "El usuario ya está registrado.";
            case "ERROR_DATOS_DUPLICADOS":
                return "Los datos ya están registrados. Verifique el correo y documento.";
            case "ERROR_BD":
                return "Error en la base de datos. Intente nuevamente más tarde.";
            default:
                return "Error desconocido: " + codigo;
        }
    }

    /**
     * Enviar respuesta de error en formato JSON
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) 
            throws IOException {
        response.setStatus(status);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * Clase interna para manejar requests de registro
     */
    public static class RegisterRequest {
        private String nombres;
        private String apellidos;
        private String email;
        private String password;
        private String tipoDocumento;
        private String numeroDocumento;
        private String telefono;

        // Getters y setters
        public String getNombres() { return nombres; }
        public void setNombres(String nombres) { this.nombres = nombres; }

        public String getApellidos() { return apellidos; }
        public void setApellidos(String apellidos) { this.apellidos = apellidos; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getTipoDocumento() { return tipoDocumento; }
        public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

        public String getNumeroDocumento() { return numeroDocumento; }
        public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
    }
}
