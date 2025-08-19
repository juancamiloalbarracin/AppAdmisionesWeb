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
import repository.InfoAcademicaRepository;

/**
 * InfoAcademicaApiServlet - API REST PROPIA para información académica
 * MIGRA: InfoAcademicaServlet.java a endpoints JSON
 * PRESERVA: Toda la lógica de negocio original
 */
@WebServlet("/api/info-academica/*")
public class InfoAcademicaApiServlet extends HttpServlet {
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
            handleGetInfoAcademica(request, response);
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
            handleSaveInfoAcademica(request, response);
        } else {
            sendErrorResponse(response, 404, "Endpoint no encontrado: " + pathInfo);
        }
    }

    /**
     * API PROPIA: GET /api/info-academica/get
     * PRESERVA: Misma lógica que InfoAcademicaServlet para obtener datos
     */
    private void handleGetInfoAcademica(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            String userEmail = (String) request.getSession().getAttribute("userEmail");
            System.out.println("[InfoAcademica/Get] userEmail=" + userEmail);
            
            InfoAcademicaRepository repo = RepositoryFactory.getInfoAcademicaRepository();
            Map<String, String> infoAcademica = repo.getByEmail(userEmail);
            
            Map<String, Object> responseData = new HashMap<>();
            
            if (infoAcademica != null && !infoAcademica.isEmpty()) {
                responseData.put("success", true);
                responseData.put("data", infoAcademica);
                responseData.put("message", "Información académica encontrada");
            } else {
                responseData.put("success", true);
                responseData.put("data", new HashMap<>());
                responseData.put("message", "No se encontró información académica registrada");
            }
            responseData.put("queriedEmail", userEmail);
            
            response.setStatus(200);
            response.getWriter().write(objectMapper.writeValueAsString(responseData));
            
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Error al obtener información académica: " + e.getMessage());
        }
    }

    /**
     * API PROPIA: POST /api/info-academica/save
     * PRESERVA: Todas las validaciones del InfoAcademicaServlet original
     */
    private void handleSaveInfoAcademica(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            // Leer JSON del request
            InfoAcademicaRequest infoReq = objectMapper.readValue(request.getReader(), InfoAcademicaRequest.class);
            String userEmail = (String) request.getSession().getAttribute("userEmail");
            
            // PRESERVADO: Validaciones del servlet original
            String validationError = validateInfoAcademica(infoReq);
            if (validationError != null) {
                sendErrorResponse(response, 400, validationError);
                return;
            }
            
            // PRESERVADO: Misma lógica de guardado que InfoAcademicaServlet
            InfoAcademicaRepository repo = RepositoryFactory.getInfoAcademicaRepository();
            java.util.Map<String, String> payload = new java.util.HashMap<>();
            payload.put("nivel", infoReq.getNivel());
            payload.put("sede", infoReq.getSede());
            payload.put("gradoAcademico", infoReq.getGradoAcademico());
            payload.put("periodoAdmision", infoReq.getPeriodoAdmision());
            payload.put("metodologia", infoReq.getMetodologia());
            payload.put("jornada", infoReq.getJornada());
            payload.put("planDecision", infoReq.getPlanDecision());
            payload.put("gradoSeleccionado", infoReq.getGradoSeleccionado());
            payload.put("pais", infoReq.getPais());
            payload.put("gradoObtenido", infoReq.getGradoObtenido());
            payload.put("fechaGraduacion", infoReq.getFechaGraduacion());
            boolean success = repo.saveFromApi(userEmail, payload);
            
            Map<String, Object> responseData = new HashMap<>();
            if (success) {
                responseData.put("success", true);
                responseData.put("message", "Información académica guardada exitosamente");
            } else {
                responseData.put("success", false);
                responseData.put("message", "Error al guardar la información académica");
            }
            
            response.setStatus(success ? 200 : 500);
            response.getWriter().write(objectMapper.writeValueAsString(responseData));
            
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Error al procesar la solicitud: " + e.getMessage());
        }
    }
    
    /**
     * PRESERVADO: Validaciones originales del InfoAcademicaServlet
     */
    private String validateInfoAcademica(InfoAcademicaRequest info) {
        if (isEmpty(info.getNivel())) {
            return "El nivel es obligatorio.";
        }
        if (isEmpty(info.getGradoAcademico()) || "Seleccione...".equals(info.getGradoAcademico())) {
            return "Debe seleccionar un grado académico.";
        }
        if (isEmpty(info.getJornada()) || "Seleccione...".equals(info.getJornada())) {
            return "Debe seleccionar una jornada.";
        }
        if (isEmpty(info.getPlanDecision()) || "Seleccione...".equals(info.getPlanDecision())) {
            return "Debe seleccionar un plan de decisión.";
        }
        if (isEmpty(info.getPais())) {
            return "El país es obligatorio.";
        }
        if (isEmpty(info.getGradoObtenido())) {
            return "El grado obtenido es obligatorio.";
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
    public static class InfoAcademicaRequest {
        private String nivel;
        private String sede;
        private String gradoAcademico;
        private String periodoAdmision;
        private String metodologia;
        private String jornada;
        private String planDecision;
        private String gradoSeleccionado;
        private String pais;
        private String gradoObtenido;
        private String fechaGraduacion;
        
        // Getters y setters
        public String getNivel() { return nivel; }
        public void setNivel(String nivel) { this.nivel = nivel; }
        public String getSede() { return sede; }
        public void setSede(String sede) { this.sede = sede; }
        public String getGradoAcademico() { return gradoAcademico; }
        public void setGradoAcademico(String gradoAcademico) { this.gradoAcademico = gradoAcademico; }
        public String getPeriodoAdmision() { return periodoAdmision; }
        public void setPeriodoAdmision(String periodoAdmision) { this.periodoAdmision = periodoAdmision; }
        public String getMetodologia() { return metodologia; }
        public void setMetodologia(String metodologia) { this.metodologia = metodologia; }
        public String getJornada() { return jornada; }
        public void setJornada(String jornada) { this.jornada = jornada; }
        public String getPlanDecision() { return planDecision; }
        public void setPlanDecision(String planDecision) { this.planDecision = planDecision; }
        public String getGradoSeleccionado() { return gradoSeleccionado; }
        public void setGradoSeleccionado(String gradoSeleccionado) { this.gradoSeleccionado = gradoSeleccionado; }
        public String getPais() { return pais; }
        public void setPais(String pais) { this.pais = pais; }
        public String getGradoObtenido() { return gradoObtenido; }
        public void setGradoObtenido(String gradoObtenido) { this.gradoObtenido = gradoObtenido; }
        public String getFechaGraduacion() { return fechaGraduacion; }
        public void setFechaGraduacion(String fechaGraduacion) { this.fechaGraduacion = fechaGraduacion; }
    }
}
