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
import repository.InfoPersonalRepository;

/**
 * InfoPersonalApiServlet - API REST PROPIA para información personal
 * MIGRA: InfoPersonalServlet.java a endpoints JSON
 * PRESERVA: Toda la lógica de negocio original
 */
@WebServlet("/api/info-personal/*")
public class InfoPersonalApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Verificar autenticación
        if (!isAuthenticated(request)) {
            sendErrorResponse(response, 401, "Sesión no válida");
            return;
        }
        
        if ("/get".equals(pathInfo) || pathInfo == null || "".equals(pathInfo) || "/".equals(pathInfo)) {
            handleGetInfoPersonal(request, response);
        } else {
            sendErrorResponse(response, 404, "Endpoint no encontrado: " + pathInfo);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Verificar autenticación
        if (!isAuthenticated(request)) {
            sendErrorResponse(response, 401, "Sesión no válida");
            return;
        }
        
        if ("/save".equals(pathInfo)) {
            handleSaveInfoPersonal(request, response);
        } else {
            sendErrorResponse(response, 404, "Endpoint no encontrado: " + pathInfo);
        }
    }

    /**
     * API PROPIA: GET /api/info-personal/get
     * PRESERVA: Misma lógica que InfoPersonalServlet para obtener datos
     */
    private void handleGetInfoPersonal(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            String userEmail = (String) request.getSession().getAttribute("userEmail");
            System.out.println("[InfoPersonal/Get] userEmail=" + userEmail);
            
            InfoPersonalRepository repo = RepositoryFactory.getInfoPersonalRepository();
            Map<String, String> infoPersonal = repo.getByEmail(userEmail);
            
            Map<String, Object> responseData = new HashMap<>();
            
            if (infoPersonal != null && !infoPersonal.isEmpty()) {
                responseData.put("success", true);
                responseData.put("data", infoPersonal);
                responseData.put("message", "Información personal encontrada");
            } else {
                responseData.put("success", true);
                responseData.put("data", new HashMap<>());
                responseData.put("message", "No se encontró información personal registrada");
            }
            responseData.put("queriedEmail", userEmail);
            
            response.setStatus(200);
            response.getWriter().write(objectMapper.writeValueAsString(responseData));
            
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Error al obtener información personal: " + e.getMessage());
        }
    }

    /**
     * API PROPIA: POST /api/info-personal/save
     * PRESERVA: Todas las validaciones del InfoPersonalServlet original
     */
    private void handleSaveInfoPersonal(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            // Leer JSON del request
            InfoPersonalRequest infoReq = objectMapper.readValue(request.getReader(), InfoPersonalRequest.class);
            String userEmail = (String) request.getSession().getAttribute("userEmail");
            
            // PRESERVADO: Validaciones del servlet original
            String validationError = validateInfoPersonal(infoReq);
            if (validationError != null) {
                sendErrorResponse(response, 400, validationError);
                return;
            }
            
            // PRESERVADO: Misma lógica de guardado que InfoPersonalServlet
            InfoPersonalRepository repo = RepositoryFactory.getInfoPersonalRepository();
            java.util.Map<String, String> payload = new java.util.HashMap<>();
            payload.put("nombres", infoReq.getNombres());
            payload.put("apellidos", infoReq.getApellidos());
            payload.put("fechaNacimiento", infoReq.getFechaNacimiento());
            payload.put("lugarNacimiento", infoReq.getLugarNacimiento());
            payload.put("tipoDocumento", infoReq.getTipoDocumento());
            payload.put("numeroDocumento", infoReq.getNumeroDocumento());
            payload.put("genero", infoReq.getGenero());
            payload.put("estadoCivil", infoReq.getEstadoCivil());
            payload.put("direccion", infoReq.getDireccion());
            payload.put("telefono", infoReq.getTelefono());
            payload.put("celular", infoReq.getCelular());
            payload.put("emailPersonal", infoReq.getEmailPersonal());
            boolean success = repo.saveFromApi(userEmail, payload);
            
            Map<String, Object> responseData = new HashMap<>();
            if (success) {
                responseData.put("success", true);
                responseData.put("message", "Información personal guardada exitosamente");
            } else {
                responseData.put("success", false);
                responseData.put("message", "Error al guardar la información personal");
            }
            
            response.setStatus(success ? 200 : 500);
            response.getWriter().write(objectMapper.writeValueAsString(responseData));
            
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Error al procesar la solicitud: " + e.getMessage());
        }
    }
    
    /**
     * PRESERVADO: Validaciones originales del InfoPersonalServlet
     */
    private String validateInfoPersonal(InfoPersonalRequest info) {
        if (isEmpty(info.getNombres())) {
            return "Los nombres son obligatorios.";
        }
        if (isEmpty(info.getApellidos())) {
            return "Los apellidos son obligatorios.";
        }
        if (isEmpty(info.getFechaNacimiento())) {
            return "La fecha de nacimiento es obligatoria.";
        }
        if (isEmpty(info.getTipoDocumento()) || "Seleccione...".equals(info.getTipoDocumento())) {
            return "Debe seleccionar un tipo de documento.";
        }
        if (isEmpty(info.getNumeroDocumento())) {
            return "El número de documento es obligatorio.";
        }
        if (isEmpty(info.getGenero()) || "Seleccione...".equals(info.getGenero())) {
            return "Debe seleccionar el género.";
        }
        if (isEmpty(info.getTelefono())) {
            return "El teléfono es obligatorio.";
        }
        
        return null; // Sin errores
    }
    
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    private boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("userEmail") != null;
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
    public static class InfoPersonalRequest {
        private String nombres;
        private String apellidos;
        private String fechaNacimiento;
        private String lugarNacimiento;
        private String tipoDocumento;
        private String numeroDocumento;
        private String genero;
        private String estadoCivil;
        private String direccion;
        private String telefono;
        private String celular;
        private String emailPersonal;
        
        // Getters y setters
        public String getNombres() { return nombres; }
        public void setNombres(String nombres) { this.nombres = nombres; }
        public String getApellidos() { return apellidos; }
        public void setApellidos(String apellidos) { this.apellidos = apellidos; }
        public String getFechaNacimiento() { return fechaNacimiento; }
        public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
        public String getLugarNacimiento() { return lugarNacimiento; }
        public void setLugarNacimiento(String lugarNacimiento) { this.lugarNacimiento = lugarNacimiento; }
        public String getTipoDocumento() { return tipoDocumento; }
        public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
        public String getNumeroDocumento() { return numeroDocumento; }
        public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
        public String getGenero() { return genero; }
        public void setGenero(String genero) { this.genero = genero; }
        public String getEstadoCivil() { return estadoCivil; }
        public void setEstadoCivil(String estadoCivil) { this.estadoCivil = estadoCivil; }
        public String getDireccion() { return direccion; }
        public void setDireccion(String direccion) { this.direccion = direccion; }
        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
        public String getCelular() { return celular; }
        public void setCelular(String celular) { this.celular = celular; }
        public String getEmailPersonal() { return emailPersonal; }
        public void setEmailPersonal(String emailPersonal) { this.emailPersonal = emailPersonal; }
    }
}
